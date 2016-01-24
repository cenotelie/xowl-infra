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

package org.xowl.server.xsp;

import org.xowl.hime.redist.ASTNode;
import org.xowl.hime.redist.ParseError;
import org.xowl.hime.redist.ParseResult;
import org.xowl.store.AbstractRepository;
import org.xowl.store.IOUtils;
import org.xowl.store.http.HttpConstants;
import org.xowl.store.http.HttpResponse;
import org.xowl.store.loaders.JSONLDLoader;
import org.xowl.store.sparql.Result;
import org.xowl.store.sparql.ResultUtils;
import org.xowl.store.storage.NodeManager;
import org.xowl.store.storage.cache.CachedNodes;
import org.xowl.infra.utils.logging.BufferedLogger;
import org.xowl.infra.utils.logging.DispatchLogger;
import org.xowl.infra.utils.logging.Logger;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public static HttpResponse toHttpResponse(XSPReply reply, List<String> acceptTypes) {
        if (reply == null)
            // client got banned
            return new HttpResponse(HttpURLConnection.HTTP_FORBIDDEN);
        if (reply instanceof XSPReplyUnauthenticated)
            return new HttpResponse(HttpURLConnection.HTTP_UNAUTHORIZED);
        if (reply instanceof XSPReplyUnauthorized)
            return new HttpResponse(HttpURLConnection.HTTP_FORBIDDEN);
        if (reply instanceof XSPReplyNotFound)
            return new HttpResponse(HttpURLConnection.HTTP_NOT_FOUND);
        if (reply instanceof XSPReplyFailure)
            return new HttpResponse(HttpConstants.HTTP_UNKNOWN_ERROR, HttpConstants.MIME_TEXT_PLAIN, reply.getMessage());
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
            return new HttpResponse(sparqlResult.isSuccess() ? HttpURLConnection.HTTP_OK : HttpConstants.HTTP_UNKNOWN_ERROR, resultType, writer.toString());
        }
        // general case
        return new HttpResponse(HttpURLConnection.HTTP_OK, HttpConstants.MIME_JSON, reply.serializedJSON());
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

        return parseJSONResult(parseResult.getRoot());
    }

    /**
     * Parses a XSP result serialized in JSON
     *
     * @param root The content
     * @return The result
     */
    public static XSPReply parseJSONResult(ASTNode root) {
        ASTNode nodeIsSuccess = null;
        ASTNode nodeMessage = null;
        ASTNode nodeCause = null;
        ASTNode nodePayload = null;
        for (ASTNode memberNode : root.getChildren()) {
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
            else if ("NOT FOUND".equals(cause))
                return XSPReplyNotFound.instance();
            else if ("NETWORK ERROR".equals(cause)) {
                String msg = "";
                if (nodeMessage != null) {
                    msg = IOUtils.unescape(nodeMessage.getValue());
                    msg = msg.substring(1, msg.length() - 1);
                }
                return new XSPReplyNetworkError(msg);
            } else
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
                    List<Object> payload = new ArrayList<>(nodePayload.getChildren().size());
                    for (ASTNode child : nodePayload.getChildren())
                        payload.add(getJSONObject(child));
                    return new XSPReplyResultCollection<>(payload);
                } else {
                    return new XSPReplyResult<>(getJSONObject(nodePayload));
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
     * Gets an object representing the specified JSON object
     *
     * @param node The root AST for the object
     * @return The JSON object
     */
    private static Object getJSONObject(ASTNode node) {
        // is this an array ?
        if ("array".equals(node.getSymbol().getName())) {
            List<Object> value = new ArrayList<>();
            for (ASTNode child : node.getChildren()) {
                value.add(getJSONObject(child));
            }
            return value;
        }
        // is this a simple value ?
        String value = node.getValue();
        if (value != null) {
            if (value.startsWith("\"")) {
                value = IOUtils.unescape(value);
                return value.substring(1, value.length() - 1);
            }
            return value;
        }
        // this is an object, does it have a type
        ASTNode nodeType = null;
        for (ASTNode memberNode : node.getChildren()) {
            String memberName = IOUtils.unescape(memberNode.getChildren().get(0).getValue());
            memberName = memberName.substring(1, memberName.length() - 1);
            ASTNode memberValue = memberNode.getChildren().get(1);
            switch (memberName) {
                case "type":
                    nodeType = memberValue;
                    break;
            }
        }
        if (nodeType != null) {
            // we have a type
            String type = IOUtils.unescape(nodeType.getValue());
            type = type.substring(1, type.length() - 1);
            try {
                // try to instantiate the corresponding class
                Class<?> clazz = Class.forName(type);
                Constructor constructor = clazz.getConstructor(ASTNode.class);
                if (constructor.isAccessible())
                    return constructor.newInstance(node);
            } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException exception) {
                // do nothing
            }
        }
        // fallback to mapping the properties
        Map<String, Object> properties = new HashMap<>();
        for (ASTNode memberNode : node.getChildren()) {
            String memberName = IOUtils.unescape(memberNode.getChildren().get(0).getValue());
            memberName = memberName.substring(1, memberName.length() - 1);
            ASTNode memberValue = memberNode.getChildren().get(1);
            properties.put(memberName, getJSONObject(memberValue));
        }
        return properties;
    }
}
