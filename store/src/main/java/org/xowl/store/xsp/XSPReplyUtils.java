/*******************************************************************************
 * Copyright (c) 2015 Laurent Wouters
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General
 * Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 *
 * Contributors:
 *     Laurent Wouters - lwouters@xowl.org
 ******************************************************************************/

package org.xowl.store.xsp;

import org.xowl.hime.redist.ASTNode;
import org.xowl.hime.redist.ParseError;
import org.xowl.hime.redist.ParseResult;
import org.xowl.store.AbstractRepository;
import org.xowl.store.IOUtils;
import org.xowl.store.loaders.JSONLDLoader;
import org.xowl.store.sparql.Result;
import org.xowl.store.sparql.ResultUtils;
import org.xowl.store.storage.NodeManager;
import org.xowl.store.storage.cache.CachedNodes;
import org.xowl.utils.logging.BufferedLogger;
import org.xowl.utils.logging.DispatchLogger;
import org.xowl.utils.logging.Logger;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Utility APIs for the xOWL Server Protocol
 *
 * @author Laurent Wouters
 */
public class XSPReplyUtils {
    /**
     * Translates an XSP reply to an HTTP response
     *
     * @param reply       The reply
     * @param acceptTypes The accepted MIME types, if any
     * @return The HTTP response
     */
    public static IOUtils.HttpResponse toHttpResponse(XSPReply reply, List<String> acceptTypes) {
        if (reply == null)
            // client got banned
            return new IOUtils.HttpResponse(HttpURLConnection.HTTP_FORBIDDEN);
        if (reply instanceof XSPReplyUnauthenticated)
            return new IOUtils.HttpResponse(HttpURLConnection.HTTP_UNAUTHORIZED);
        if (reply instanceof XSPReplyUnauthorized)
            return new IOUtils.HttpResponse(HttpURLConnection.HTTP_FORBIDDEN);
        if (reply instanceof XSPReplyFailure)
            return new IOUtils.HttpResponse(IOUtils.HTTP_UNKNOWN_ERROR, IOUtils.MIME_TEXT_PLAIN, reply.getMessage());
        if (reply instanceof XSPReplyResult && ((XSPReplyResult) reply).getData() instanceof Result) {
            // special handling for SPARQL
            Result sparqlResult = (Result) ((XSPReplyResult) reply).getData();
            String resultType = ResultUtils.coerceContentType(sparqlResult, acceptTypes != null ? IOUtils.httpNegotiateContentType(acceptTypes) : AbstractRepository.SYNTAX_NQUADS);
            StringWriter writer = new StringWriter();
            try {
                sparqlResult.print(writer, resultType);
            } catch (IOException exception) {
                // cannot happen
            }
            return new IOUtils.HttpResponse(sparqlResult.isSuccess() ? HttpURLConnection.HTTP_OK : IOUtils.HTTP_UNKNOWN_ERROR, resultType, writer.toString());
        }
        // general case
        return new IOUtils.HttpResponse(IOUtils.HTTP_UNKNOWN_ERROR, IOUtils.MIME_JSON, reply.serializedJSON());
    }

    /**
     * Parses a XSP result serialized in JSON
     *
     * @param content The content
     * @return The result
     */
    public static XSPReply parseJSONResult(String content) {
        NodeManager nodeManager = new CachedNodes();
        JSONLDLoader loader = new JSONLDLoader(nodeManager) {
            @Override
            protected Reader getReaderFor(Logger logger, String iri) {
                return null;
            }
        };
        BufferedLogger bufferedLogger = new BufferedLogger();
        DispatchLogger dispatchLogger = new DispatchLogger(Logger.DEFAULT, bufferedLogger);
        ParseResult parseResult = loader.parse(dispatchLogger, new StringReader(content));
        if (parseResult == null || !parseResult.isSuccess()) {
            dispatchLogger.error("Failed to parse the response");
            if (parseResult != null) {
                for (ParseError error : parseResult.getErrors()) {
                    dispatchLogger.error(error);
                }
            }
            StringBuilder builder = new StringBuilder();
            for (Object error : bufferedLogger.getErrorMessages()) {
                builder.append(error.toString());
                builder.append("\n");
            }
            return new XSPReplyFailure(builder.toString());
        }

        if ("array".equals(parseResult.getRoot().getSymbol().getName()))
            return new XSPReplyFailure("Unexpected JSON format");

        ASTNode nodeIsSuccess = null;
        ASTNode nodeMessage = null;
        ASTNode nodeCause = null;
        ASTNode nodePayload = null;
        for (ASTNode memberNode : parseResult.getRoot().getChildren()) {
            String memberName = IOUtils.unescape(memberNode.getChildren().get(0).getValue());
            memberName = memberName.substring(1, memberName.length() - 1);
            ASTNode memberValue = memberNode.getChildren().get(1);
            switch (memberName) {
                case "isSuccess":
                    nodeIsSuccess = memberValue;
                    break;
                case "message":
                    nodeMessage = memberValue;
                    break;
                case "cause":
                    nodeCause = memberValue;
                    break;
                case "payload":
                    nodePayload = memberValue;
                    break;
            }
        }

        if (nodeIsSuccess == null)
            return new XSPReplyFailure("Unexpected JSON format");
        boolean isSuccess = "true".equalsIgnoreCase(nodeIsSuccess.getValue());
        if (!isSuccess && nodeCause != null) {
            String cause = IOUtils.unescape(nodeCause.getValue());
            cause = cause.substring(1, cause.length() - 1);
            if ("UNAUTHENTICATED".equals(cause))
                return XSPReplyUnauthenticated.instance();
            else if ("UNAUTHORIZED".equals(cause))
                return XSPReplyUnauthorized.instance();
            else
                return new XSPReplyFailure(cause);
        } else if (!isSuccess) {
            if (nodeMessage == null)
                return new XSPReplyFailure("Unexpected JSON format");
            String message = IOUtils.unescape(nodeMessage.getValue());
            message = message.substring(1, message.length() - 1);
            return new XSPReplyFailure(message);
        } else {
            // this is a success
            if (nodePayload != null) {
                if ("array".equals(nodePayload.getSymbol().getName())) {
                    Collection<Object> payload = new ArrayList<>(nodePayload.getChildren().size());
                    for (ASTNode child : nodePayload.getChildren()) {
                        Object element = loadXSPObject(child);
                        if (element == null)
                            return new XSPReplyFailure("Unexpected JSON format");
                        payload.add(element);
                    }
                    return new XSPReplyResultCollection<>(payload);
                } else {
                    Object payload = loadXSPObject(nodePayload);
                    if (payload == null)
                        return new XSPReplyFailure("Unexpected JSON format");
                    return new XSPReplyResult<>(payload);
                }
            } else {
                if (nodeMessage == null)
                    return XSPReplySuccess.instance();
                String message = IOUtils.unescape(nodeMessage.getValue());
                message = message.substring(1, message.length() - 1);
                return new XSPReplySuccess(message);
            }
        }
    }

    /**
     * Loads an XSP object from the specified AST root
     *
     * @param root The AST root node for an object
     * @return The object, or null if it is not recognized
     */
    private static Object loadXSPObject(ASTNode root) {
        if ("array".equals(root.getSymbol().getName()))
            return null;

        String value = root.getValue();
        if (value != null)
            return value;

        ASTNode nodeType = null;
        ASTNode nodeName = null;
        for (ASTNode memberNode : root.getChildren()) {
            String memberName = IOUtils.unescape(memberNode.getChildren().get(0).getValue());
            memberName = memberName.substring(1, memberName.length() - 1);
            ASTNode memberValue = memberNode.getChildren().get(1);
            switch (memberName) {
                case "type":
                    nodeType = memberValue;
                    break;
                case "name":
                    nodeName = memberValue;
                    break;
            }
        }

        if (nodeType == null)
            return null;
        String type = IOUtils.unescape(nodeType.getValue());
        type = type.substring(1, type.length() - 1);

        switch (type) {
            case "org.xowl.server.db.User":
            case "org.xowl.server.db.Database":
                if (nodeName == null)
                    return null;
                String name = IOUtils.unescape(nodeName.getValue());
                return name.substring(1, name.length() - 1);
            case "org.xowl.server.db.UserPrivileges":
            case "org.xowl.server.db.DatabasePrivileges":
                // TODO: implement this
                return null;
            case "org.xowl.store.rdf.RuleExplanation":
                // TODO: implement this
                return null;
            case "org.xowl.store.rete.MatchStatus":
                // TODO: implement this
                return null;
        }
        return null;
    }
}
