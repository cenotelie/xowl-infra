/*******************************************************************************
 * Copyright (c) 2016 Association Cénotélie (cenotelie.fr)
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
 ******************************************************************************/

package org.xowl.infra.server.xsp;

import org.xowl.hime.redist.ASTNode;
import org.xowl.hime.redist.ParseError;
import org.xowl.hime.redist.ParseResult;
import org.xowl.infra.server.api.XOWLFactory;
import org.xowl.infra.store.Repository;
import org.xowl.infra.store.loaders.JSONLDLoader;
import org.xowl.infra.store.sparql.Result;
import org.xowl.infra.store.sparql.ResultUtils;
import org.xowl.infra.store.storage.NodeManager;
import org.xowl.infra.store.storage.cache.CachedNodes;
import org.xowl.infra.utils.TextUtils;
import org.xowl.infra.utils.http.HttpConstants;
import org.xowl.infra.utils.http.HttpResponse;
import org.xowl.infra.utils.logging.BufferedLogger;
import org.xowl.infra.utils.logging.DispatchLogger;
import org.xowl.infra.utils.logging.Logger;
import org.xowl.infra.utils.logging.Logging;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
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
        if (reply instanceof XSPReplyUnsupported)
            return new HttpResponse(HttpURLConnection.HTTP_NOT_IMPLEMENTED);
        if (reply instanceof XSPReplyNotFound)
            return new HttpResponse(HttpURLConnection.HTTP_NOT_FOUND);
        // other failures
        if (!reply.isSuccess())
            return new HttpResponse(HttpConstants.HTTP_UNKNOWN_ERROR, HttpConstants.MIME_TEXT_PLAIN, reply.getMessage());
        // handle special case of SPARQL
        if (reply instanceof XSPReplyResult && ((XSPReplyResult) reply).getData() instanceof Result) {
            // special handling for SPARQL
            Result sparqlResult = (Result) ((XSPReplyResult) reply).getData();
            String resultType = ResultUtils.coerceContentType(sparqlResult, acceptTypes != null ? httpNegotiateContentType(acceptTypes) : Repository.SYNTAX_NQUADS);
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
     * Translates an HTTP response to an XSP reply
     *
     * @param response The response
     * @param factory  The factory to use
     * @return The XSP reply
     */
    public static XSPReply fromHttpResponse(HttpResponse response, XOWLFactory factory) {
        if (response == null)
            return XSPReplyNetworkError.instance();
        // handle special HTTP codes
        if (response.getCode() == HttpURLConnection.HTTP_UNAUTHORIZED)
            return XSPReplyUnauthenticated.instance();
        if (response.getCode() == HttpURLConnection.HTTP_FORBIDDEN)
            return XSPReplyUnauthorized.instance();
        if (response.getCode() == HttpURLConnection.HTTP_NOT_IMPLEMENTED)
            return XSPReplyUnsupported.instance();
        if (response.getCode() == HttpURLConnection.HTTP_NOT_FOUND)
            return XSPReplyNotFound.instance();
        if (response.getCode() == HttpURLConnection.HTTP_INTERNAL_ERROR)
            return new XSPReplyFailure(response.getBodyAsString());
        if (response.getCode() == HttpConstants.HTTP_UNKNOWN_ERROR)
            return new XSPReplyFailure(response.getBodyAsString());
        // handle other failures
        if (response.getCode() != HttpURLConnection.HTTP_OK)
            return new XSPReplyFailure(response.getBodyAsString() != null ? response.getBodyAsString() : "failure (HTTP " + response.getCode() + ")");
        if (response.getContentType() != null && !response.getContentType().isEmpty()) {
            // we've got a content type
            if (response.getBodyAsString() != null && !response.getBodyAsString().isEmpty()) {
                // we have content
                if (HttpConstants.MIME_JSON.equals(response.getContentType()))
                    // pure JSON response
                    return XSPReplyUtils.parseJSONResult(response.getBodyAsString(), factory);
                if (HttpConstants.MIME_TEXT_PLAIN.equals(response.getContentType()))
                    // plain text
                    return new XSPReplyResult<>(response.getBodyAsString());
                // assume SPARQL
                Result sparqlResult = ResultUtils.parseResponse(response.getBodyAsString(), response.getContentType());
                return new XSPReplyResult<>(sparqlResult);
            } else {
                // no content but content type is defined
                if (HttpConstants.MIME_JSON.equals(response.getContentType()) || HttpConstants.MIME_TEXT_PLAIN.equals(response.getContentType()))
                    return XSPReplySuccess.instance();
                // assume empty SPARQL
                Result sparqlResult = ResultUtils.parseResponse(null, response.getContentType());
                return new XSPReplyResult<>(sparqlResult);
            }
        } else {
            // no content type
            if (response.getBodyAsString() != null && !response.getBodyAsString().isEmpty()) {
                // too bad we have content ...
                // assume plain text
                return new XSPReplyResult<>(response.getBodyAsString());
            } else {
                return XSPReplySuccess.instance();
            }
        }
    }

    /**
     * Parses a XSP result serialized in JSON
     *
     * @param content The content
     * @param factory The factory to use
     * @return The result
     */
    public static XSPReply parseJSONResult(String content, XOWLFactory factory) {
        NodeManager nodeManager = new CachedNodes();
        JSONLDLoader loader = new JSONLDLoader(nodeManager) {
            @Override
            protected Reader getReaderFor(Logger logger, String iri) {
                return null;
            }
        };
        BufferedLogger bufferedLogger = new BufferedLogger();
        DispatchLogger dispatchLogger = new DispatchLogger(Logging.getDefault(), bufferedLogger);
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

        return parseJSONResult(parseResult.getRoot(), factory);
    }

    /**
     * Parses a XSP result serialized in JSON
     *
     * @param root    The content
     * @param factory The factory to use
     * @return The result
     */
    public static XSPReply parseJSONResult(ASTNode root, XOWLFactory factory) {
        ASTNode nodeIsSuccess = null;
        ASTNode nodeMessage = null;
        ASTNode nodeCause = null;
        ASTNode nodePayload = null;
        for (ASTNode memberNode : root.getChildren()) {
            String memberName = TextUtils.unescape(memberNode.getChildren().get(0).getValue());
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
            String cause = TextUtils.unescape(nodeCause.getValue());
            cause = cause.substring(1, cause.length() - 1);
            if ("UNAUTHENTICATED".equals(cause))
                return XSPReplyUnauthenticated.instance();
            else if ("UNAUTHORIZED".equals(cause))
                return XSPReplyUnauthorized.instance();
            else if ("UNSUPPORTED".equals(cause))
                return XSPReplyUnsupported.instance();
            else if ("NOT FOUND".equals(cause))
                return XSPReplyNotFound.instance();
            else if ("NETWORK ERROR".equals(cause)) {
                String msg = null;
                if (nodeMessage != null) {
                    msg = TextUtils.unescape(nodeMessage.getValue());
                    msg = msg.substring(1, msg.length() - 1);
                }
                if (msg == null)
                    return XSPReplyNetworkError.instance();
                return new XSPReplyNetworkError(msg);
            } else
                return new XSPReplyFailure(cause);
        } else if (!isSuccess) {
            if (nodeMessage == null)
                return new XSPReplyFailure("Unexpected JSON format");
            String message = TextUtils.unescape(nodeMessage.getValue());
            message = message.substring(1, message.length() - 1);
            return new XSPReplyFailure(message);
        } else {
            // this is a success
            if (nodePayload != null) {
                if ("array".equals(nodePayload.getSymbol().getName())) {
                    List<Object> payload = new ArrayList<>(nodePayload.getChildren().size());
                    for (ASTNode child : nodePayload.getChildren())
                        payload.add(getJSONObject(child, factory));
                    return new XSPReplyResultCollection<>(payload);
                } else {
                    return new XSPReplyResult<>(getJSONObject(nodePayload, factory));
                }
            } else {
                if (nodeMessage == null)
                    return XSPReplySuccess.instance();
                String message = TextUtils.unescape(nodeMessage.getValue());
                message = message.substring(1, message.length() - 1);
                return new XSPReplySuccess(message);
            }
        }
    }

    /**
     * Negotiates the content type from the specified requested ones
     *
     * @param contentTypes The requested content types by order of preference
     * @return The accepted content type
     */
    private static String httpNegotiateContentType(List<String> contentTypes) {
        for (String contentType : contentTypes) {
            switch (contentType) {
                // The SPARQL result syntaxes
                case Result.SYNTAX_CSV:
                case Result.SYNTAX_TSV:
                case Result.SYNTAX_XML:
                case Result.SYNTAX_JSON:
                    // The RDF syntaxes for quads
                case Repository.SYNTAX_NTRIPLES:
                case Repository.SYNTAX_NQUADS:
                case Repository.SYNTAX_TURTLE:
                case Repository.SYNTAX_TRIG:
                case Repository.SYNTAX_RDFXML:
                case Repository.SYNTAX_JSON_LD:
                    return contentType;
            }
        }
        return Repository.SYNTAX_NQUADS;
    }

    /**
     * Gets an object representing the specified JSON object
     *
     * @param node    The root AST for the object
     * @param factory The factory to use
     * @return The JSON object
     */
    public static Object getJSONObject(ASTNode node, XOWLFactory factory) {
        // is this an array ?
        if ("array".equals(node.getSymbol().getName())) {
            List<Object> value = new ArrayList<>();
            for (ASTNode child : node.getChildren()) {
                value.add(getJSONObject(child, factory));
            }
            return value;
        }
        // is this a simple value ?
        String value = node.getValue();
        if (value != null) {
            if (value.startsWith("\"")) {
                value = TextUtils.unescape(value);
                return value.substring(1, value.length() - 1);
            }
            return value;
        }
        // this is an object, does it have a type
        ASTNode nodeType = null;
        for (ASTNode memberNode : node.getChildren()) {
            String memberName = TextUtils.unescape(memberNode.getChildren().get(0).getValue());
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
            String type = TextUtils.unescape(nodeType.getValue());
            type = type.substring(1, type.length() - 1);
            Object result = factory.newObject(type, node);
            if (result != null)
                return result;
        }
        // fallback to mapping the properties
        Map<String, Object> properties = new HashMap<>();
        for (ASTNode memberNode : node.getChildren()) {
            String memberName = TextUtils.unescape(memberNode.getChildren().get(0).getValue());
            memberName = memberName.substring(1, memberName.length() - 1);
            ASTNode memberValue = memberNode.getChildren().get(1);
            properties.put(memberName, getJSONObject(memberValue, factory));
        }
        return properties;
    }
}
