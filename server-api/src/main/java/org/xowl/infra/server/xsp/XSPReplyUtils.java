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

import fr.cenotelie.hime.redist.ASTNode;
import org.xowl.infra.server.api.XOWLFactory;
import org.xowl.infra.store.Repository;
import org.xowl.infra.store.sparql.Result;
import org.xowl.infra.store.sparql.ResultFailure;
import org.xowl.infra.store.sparql.ResultUtils;
import org.xowl.infra.utils.Serializable;
import org.xowl.infra.utils.TextUtils;
import org.xowl.infra.utils.http.HttpConstants;
import org.xowl.infra.utils.http.HttpResponse;
import org.xowl.infra.utils.json.Json;
import org.xowl.infra.utils.json.SerializedUnknown;
import org.xowl.infra.utils.logging.BufferedLogger;

import java.io.IOException;
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
    public static HttpResponse toHttpResponse(XSPReply reply, List<String> acceptTypes) {
        // XSP replies mapped to HTTP error codes
        if (reply == null)
            // client got banned
            return new HttpResponse(HttpURLConnection.HTTP_FORBIDDEN);
        if (reply instanceof XSPReplyUnauthenticated)
            return new HttpResponse(HttpURLConnection.HTTP_UNAUTHORIZED);
        if (reply instanceof XSPReplyUnauthorized)
            return new HttpResponse(HttpURLConnection.HTTP_FORBIDDEN);
        if (reply instanceof XSPReplyExpiredSession)
            return new HttpResponse(HttpConstants.HTTP_SESSION_EXPIRED);
        if (reply instanceof XSPReplyUnsupported)
            return new HttpResponse(HttpURLConnection.HTTP_NOT_IMPLEMENTED);
        if (reply instanceof XSPReplyNotFound)
            return new HttpResponse(HttpURLConnection.HTTP_NOT_FOUND);
        if (reply instanceof XSPReplyApiError)
            return new HttpResponse(HttpURLConnection.HTTP_BAD_REQUEST, HttpConstants.MIME_JSON, ((XSPReplyApiError) reply).getError().serializedJSON(((XSPReplyApiError) reply).getSupplementaryMessage()));
        if (reply instanceof XSPReplyException)
            return new HttpResponse(HttpURLConnection.HTTP_INTERNAL_ERROR, HttpConstants.MIME_TEXT_PLAIN, XSPReplyException.MESSAGE);

        // other failures (including XSPReplyFailure) mapped to HTTP_UNKNOWN_ERROR
        if (!reply.isSuccess())
            return new HttpResponse(HttpConstants.HTTP_UNKNOWN_ERROR, HttpConstants.MIME_TEXT_PLAIN, reply.getMessage());

        // handle special case of SPARQL
        if (reply instanceof XSPReplyResult && ((XSPReplyResult) reply).getData() instanceof Result) {
            // special handling for SPARQL
            Result sparqlResult = (Result) ((XSPReplyResult) reply).getData();
            if (sparqlResult.isSuccess()) {
                String resultType = ResultUtils.coerceContentType(sparqlResult, acceptTypes);
                StringWriter writer = new StringWriter();
                try {
                    sparqlResult.print(writer, resultType);
                } catch (IOException exception) {
                    // cannot happen
                }
                return new HttpResponse(HttpURLConnection.HTTP_OK, resultType, writer.toString());
            } else
                return new HttpResponse(HttpConstants.HTTP_SPARQL_ERROR, HttpConstants.MIME_TEXT_PLAIN, ((ResultFailure) sparqlResult).getMessage());
        }

        // handle a single result
        if (reply instanceof XSPReplyResult) {
            Object data = ((XSPReplyResult) reply).getData();
            if (data == null)
                return new HttpResponse(HttpURLConnection.HTTP_OK);
            if (data instanceof Serializable)
                return new HttpResponse(HttpURLConnection.HTTP_OK, HttpConstants.MIME_JSON, ((Serializable) data).serializedJSON());
            else
                return new HttpResponse(HttpURLConnection.HTTP_OK, HttpConstants.MIME_TEXT_PLAIN, data.toString());
        }

        // handle a collection of results
        if (reply instanceof XSPReplyResultCollection) {
            Collection data = ((XSPReplyResultCollection) reply).getData();
            StringBuilder builder = new StringBuilder("[");
            boolean first = true;
            for (Object obj : data) {
                if (!first)
                    builder.append(", ");
                first = false;
                Json.serialize(builder, obj);
            }
            builder.append("]");
            return new HttpResponse(HttpURLConnection.HTTP_OK, HttpConstants.MIME_JSON, builder.toString());
        }

        // general case, only OK
        return new HttpResponse(HttpURLConnection.HTTP_OK);
    }

    /**
     * Translates an HTTP response to an XSP reply
     *
     * @param response The response
     * @param factory  The factory to use
     * @return The XSP reply
     */
    public static XSPReply fromHttpResponse(HttpResponse response, XOWLFactory factory) {
        // XSP replies mapped to HTTP error codes
        if (response == null)
            return XSPReplyNetworkError.instance();
        // handle special HTTP codes
        if (response.getCode() == HttpURLConnection.HTTP_UNAUTHORIZED)
            return XSPReplyUnauthenticated.instance();
        if (response.getCode() == HttpURLConnection.HTTP_FORBIDDEN)
            return XSPReplyUnauthorized.instance();
        if (response.getCode() == HttpConstants.HTTP_SESSION_EXPIRED)
            return XSPReplyExpiredSession.instance();
        if (response.getCode() == HttpURLConnection.HTTP_NOT_IMPLEMENTED)
            return XSPReplyUnsupported.instance();
        if (response.getCode() == HttpURLConnection.HTTP_NOT_FOUND)
            return XSPReplyNotFound.instance();
        if (response.getCode() == HttpURLConnection.HTTP_BAD_REQUEST) {
            BufferedLogger bufferedLogger = new BufferedLogger();
            ASTNode root = Json.parse(bufferedLogger, response.getBodyAsString());
            if (root == null)
                return new XSPReplyFailure(response.getBodyAsString());
            return new XSPReplyApiError(XSPReplyApiError.parseApiError(root), XSPReplyApiError.parseSupplementary(root));
        }
        if (response.getCode() == HttpURLConnection.HTTP_INTERNAL_ERROR)
            return new XSPReplyException(null); // exception not preserved

        // other failures (including XSPReplyFailure) mapped to HTTP_UNKNOWN_ERROR
        if (response.getCode() == HttpConstants.HTTP_UNKNOWN_ERROR)
            return new XSPReplyFailure(response.getBodyAsString());
        // handle other failures
        if (response.getCode() != HttpURLConnection.HTTP_OK)
            return new XSPReplyFailure(response.getBodyAsString() != null ? response.getBodyAsString() : "failure (HTTP " + response.getCode() + ")");

        String contentType = response.getContentType();
        if (contentType != null && !contentType.isEmpty()) {
            // we've got a content type
            int index = contentType.indexOf(";");
            if (index > 0)
                contentType = contentType.substring(0, index).trim();
            switch (contentType) {
                case Repository.SYNTAX_NQUADS:
                case Repository.SYNTAX_NTRIPLES:
                case Repository.SYNTAX_TURTLE:
                case Repository.SYNTAX_TRIG:
                case Repository.SYNTAX_RDFXML:
                case Repository.SYNTAX_JSON_LD:
                case Repository.SYNTAX_XRDF:
                case Result.SYNTAX_JSON:
                case Result.SYNTAX_CSV:
                case Result.SYNTAX_TSV:
                case Result.SYNTAX_XML:
                    return fromHttpResponseSPARQL(response);
                case HttpConstants.MIME_JSON:
                    return fromHttpResponseJSON(response, factory);
                case HttpConstants.MIME_OCTET_STREAM:
                    return fromHttpResponseBinary(response);
                default:
                    // other text
                    return fromHttpResponseOther(response);
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
     * Translates an HTTP response to an XSP reply when the content type is a SPARQL result
     *
     * @param response The response
     * @return The XSP reply
     */
    private static XSPReply fromHttpResponseSPARQL(HttpResponse response) {
        String content = response.getBodyAsString();
        if (content != null && content.isEmpty())
            content = null;
        Result sparqlResult = ResultUtils.parseResponse(content, response.getContentType());
        return new XSPReplyResult<>(sparqlResult);
    }

    /**
     * Translates an HTTP response to an XSP reply when the content type is JSON
     *
     * @param response The response
     * @param factory  The factory to use
     * @return The XSP reply
     */
    private static XSPReply fromHttpResponseJSON(HttpResponse response, XOWLFactory factory) {
        String content = response.getBodyAsString();
        if (content == null || content.isEmpty())
            return XSPReplySuccess.instance();
        BufferedLogger bufferedLogger = new BufferedLogger();
        ASTNode root = Json.parse(bufferedLogger, response.getBodyAsString());
        if (root == null)
            return new XSPReplyFailure(bufferedLogger.getErrorsAsString());
        Object data = getJSONObject(root, factory);
        if (data instanceof Collection)
            return new XSPReplyResultCollection<>((Collection) data);
        return new XSPReplyResult<>(data);
    }

    /**
     * Translates an HTTP response to an XSP reply when the content is a binary stream
     *
     * @param response The response
     * @return The XSP reply
     */
    private static XSPReply fromHttpResponseBinary(HttpResponse response) {
        byte[] content = response.getBodyAsBytes();
        if (content == null || content.length == 0)
            return XSPReplySuccess.instance();
        return new XSPReplyResult<>(content);
    }

    /**
     * Translates an HTTP response to an XSP reply for other content types
     *
     * @param response The response
     * @return The XSP reply
     */
    private static XSPReply fromHttpResponseOther(HttpResponse response) {
        String content = response.getBodyAsString();
        if (content == null || content.isEmpty())
            return XSPReplySuccess.instance();
        return new XSPReplyResult<>(content);
    }

    /**
     * Parses a XSP result serialized in JSON
     *
     * @param content The content
     * @param factory The factory to use
     * @return The result
     */
    public static XSPReply parseJSONResult(String content, XOWLFactory factory) {
        BufferedLogger bufferedLogger = new BufferedLogger();
        ASTNode root = Json.parse(bufferedLogger, content);
        if (root == null)
            return new XSPReplyFailure(bufferedLogger.getErrorsAsString());
        if ("array".equals(root.getSymbol().getName()))
            return new XSPReplyFailure("Unexpected JSON format");
        return parseJSONResult(root, factory);
    }

    /**
     * Parses a XSP result serialized in JSON
     *
     * @param root    The content
     * @param factory The factory to use
     * @return The result
     */
    public static XSPReply parseJSONResult(ASTNode root, XOWLFactory factory) {
        String kind = null;
        boolean isSuccess = false;
        String message = null;
        ASTNode nodePayload = null;
        for (ASTNode memberNode : root.getChildren()) {
            String memberName = TextUtils.unescape(memberNode.getChildren().get(0).getValue());
            memberName = memberName.substring(1, memberName.length() - 1);
            ASTNode memberValue = memberNode.getChildren().get(1);
            switch (memberName) {
                case "kind": {
                    kind = TextUtils.unescape(memberValue.getValue());
                    kind = kind.substring(1, kind.length() - 1);
                    break;
                }
                case "isSuccess": {
                    String value = TextUtils.unescape(memberValue.getValue());
                    isSuccess = Boolean.parseBoolean(value);
                    break;
                }
                case "message": {
                    message = TextUtils.unescape(memberValue.getValue());
                    message = message.substring(1, message.length() - 1);
                    break;
                }
                case "payload":
                    nodePayload = memberValue;
                    break;
            }
        }

        if (kind == null)
            return new XSPReplyFailure("Unexpected JSON format");
        if (XSPReplyApiError.class.getCanonicalName().equals(kind))
            return new XSPReplyApiError(XSPReplyApiError.parseApiError(nodePayload), message);
        if (XSPReplyException.class.getCanonicalName().equals(kind))
            return new XSPReplyException(null); // exception not preserved
        if (XSPReplyExpiredSession.class.getCanonicalName().equals(kind))
            return XSPReplyExpiredSession.instance();
        if (XSPReplyFailure.class.getCanonicalName().equals(kind))
            return new XSPReplyFailure(message);
        if (XSPReplyNetworkError.class.getCanonicalName().equals(kind))
            return new XSPReplyNetworkError(message);
        if (XSPReplyResult.class.getCanonicalName().equals(kind))
            return new XSPReplyResult<>(getJSONObject(nodePayload, factory));
        if (XSPReplyResultCollection.class.getCanonicalName().equals(kind))
            return new XSPReplyResultCollection<>((Collection<?>) getJSONObject(nodePayload, factory));
        if (XSPReplySuccess.class.getCanonicalName().equals(kind))
            return new XSPReplySuccess(message);
        if (XSPReplyUnauthenticated.class.getCanonicalName().equals(kind))
            return XSPReplyUnauthenticated.instance();
        if (XSPReplyUnauthorized.class.getCanonicalName().equals(kind))
            return XSPReplyUnauthorized.instance();
        if (XSPReplyUnsupported.class.getCanonicalName().equals(kind))
            return XSPReplyUnsupported.instance();

        if (isSuccess)
            return new XSPReplySuccess(message);
        else
            return new XSPReplyFailure(message);
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
        if (nodeType != null && factory != null) {
            // we have a type
            String type = TextUtils.unescape(nodeType.getValue());
            type = type.substring(1, type.length() - 1);
            Object result = factory.newObject(type, node);
            if (result != null)
                return result;
        }
        // fallback to mapping the properties
        SerializedUnknown result = new SerializedUnknown();
        for (ASTNode memberNode : node.getChildren()) {
            String memberName = TextUtils.unescape(memberNode.getChildren().get(0).getValue());
            memberName = memberName.substring(1, memberName.length() - 1);
            ASTNode memberValue = memberNode.getChildren().get(1);
            result.addProperty(memberName, getJSONObject(memberValue, factory));
        }
        return result;
    }
}
