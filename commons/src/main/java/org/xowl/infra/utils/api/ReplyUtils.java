/*******************************************************************************
 * Copyright (c) 2017 Association Cénotélie (cenotelie.fr)
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

package org.xowl.infra.utils.api;

import fr.cenotelie.hime.redist.ASTNode;
import org.xowl.infra.utils.Serializable;
import org.xowl.infra.utils.TextUtils;
import org.xowl.infra.utils.http.HttpConstants;
import org.xowl.infra.utils.http.HttpResponse;
import org.xowl.infra.utils.json.Json;
import org.xowl.infra.utils.json.JsonParser;
import org.xowl.infra.utils.json.SerializedUnknown;
import org.xowl.infra.utils.logging.BufferedLogger;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Utility APIs for the management of API replies
 *
 * @author Laurent Wouters
 */
public class ReplyUtils {
    /**
     * Translates an API reply to an HTTP response
     *
     * @param reply The reply
     * @return The HTTP response
     */
    public static HttpResponse toHttpResponse(Reply reply) {
        // replies mapped to HTTP error codes
        if (reply == null)
            // client got banned
            return new HttpResponse(HttpURLConnection.HTTP_FORBIDDEN);
        if (reply instanceof ReplyUnauthenticated)
            return new HttpResponse(HttpURLConnection.HTTP_UNAUTHORIZED);
        if (reply instanceof ReplyUnauthorized)
            return new HttpResponse(HttpURLConnection.HTTP_FORBIDDEN);
        if (reply instanceof ReplyExpiredSession)
            return new HttpResponse(HttpConstants.HTTP_SESSION_EXPIRED);
        if (reply instanceof ReplyUnsupported)
            return new HttpResponse(HttpURLConnection.HTTP_NOT_IMPLEMENTED);
        if (reply instanceof ReplyNotFound)
            return new HttpResponse(HttpURLConnection.HTTP_NOT_FOUND);
        if (reply instanceof ReplyApiError)
            return new HttpResponse(HttpURLConnection.HTTP_BAD_REQUEST, HttpConstants.MIME_JSON, ((ReplyApiError) reply).getError().serializedJSON(((ReplyApiError) reply).getSupplementaryMessage()));
        if (reply instanceof ReplyException)
            return new HttpResponse(HttpURLConnection.HTTP_INTERNAL_ERROR, HttpConstants.MIME_TEXT_PLAIN, ReplyException.MESSAGE);

        // other failures (including ReplyFailure) mapped to HTTP_UNKNOWN_ERROR
        if (!reply.isSuccess())
            return new HttpResponse(HttpConstants.HTTP_UNKNOWN_ERROR, HttpConstants.MIME_TEXT_PLAIN, reply.getMessage());

        // handle a single result
        if (reply instanceof ReplyResult) {
            Object data = ((ReplyResult) reply).getData();
            if (data == null)
                return new HttpResponse(HttpURLConnection.HTTP_OK);
            if (data instanceof Serializable)
                return new HttpResponse(HttpURLConnection.HTTP_OK, HttpConstants.MIME_JSON, ((Serializable) data).serializedJSON());
            else
                return new HttpResponse(HttpURLConnection.HTTP_OK, HttpConstants.MIME_TEXT_PLAIN, data.toString());
        }

        // handle a collection of results
        if (reply instanceof ReplyResultCollection) {
            Collection data = ((ReplyResultCollection) reply).getData();
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
     * Translates an HTTP response to an reply
     *
     * @param response The response
     * @param factory  The factory to use
     * @return The reply
     */
    public static Reply fromHttpResponse(HttpResponse response, ApiFactory factory) {
        // replies mapped to HTTP error codes
        if (response == null)
            return ReplyNetworkError.instance();
        // handle special HTTP codes
        if (response.getCode() == HttpURLConnection.HTTP_UNAUTHORIZED)
            return ReplyUnauthenticated.instance();
        if (response.getCode() == HttpURLConnection.HTTP_FORBIDDEN)
            return ReplyUnauthorized.instance();
        if (response.getCode() == HttpConstants.HTTP_SESSION_EXPIRED)
            return ReplyExpiredSession.instance();
        if (response.getCode() == HttpURLConnection.HTTP_NOT_IMPLEMENTED)
            return ReplyUnsupported.instance();
        if (response.getCode() == HttpURLConnection.HTTP_NOT_FOUND)
            return ReplyNotFound.instance();
        if (response.getCode() == HttpURLConnection.HTTP_BAD_REQUEST) {
            BufferedLogger bufferedLogger = new BufferedLogger();
            ASTNode root = Json.parse(bufferedLogger, response.getBodyAsString());
            if (root == null)
                return new ReplyFailure(response.getBodyAsString());
            return new ReplyApiError(ReplyApiError.parseApiError(root), ReplyApiError.parseSupplementary(root));
        }
        if (response.getCode() == HttpURLConnection.HTTP_INTERNAL_ERROR)
            return new ReplyException(null); // exception not preserved

        // other failures (including XSPReplyFailure) mapped to HTTP_UNKNOWN_ERROR
        if (response.getCode() == HttpConstants.HTTP_UNKNOWN_ERROR)
            return new ReplyFailure(response.getBodyAsString());
        // handle other failures
        if (response.getCode() != HttpURLConnection.HTTP_OK)
            return new ReplyFailure(response.getBodyAsString() != null ? response.getBodyAsString() : "failure (HTTP " + response.getCode() + ")");

        String contentType = response.getContentType();
        if (contentType != null && !contentType.isEmpty()) {
            // we've got a content type
            int index = contentType.indexOf(";");
            if (index > 0)
                contentType = contentType.substring(0, index).trim();
            switch (contentType) {
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
                return new ReplyResult<>(response.getBodyAsString());
            } else {
                return ReplySuccess.instance();
            }
        }
    }

    /**
     * Translates an HTTP response to an reply when the content type is JSON
     *
     * @param response The response
     * @param factory  The factory to use
     * @return The reply
     */
    public static Reply fromHttpResponseJSON(HttpResponse response, ApiFactory factory) {
        String content = response.getBodyAsString();
        if (content == null || content.isEmpty())
            return ReplySuccess.instance();
        BufferedLogger bufferedLogger = new BufferedLogger();
        ASTNode root = Json.parse(bufferedLogger, response.getBodyAsString());
        if (root == null)
            return new ReplyFailure(bufferedLogger.getErrorsAsString());
        Object data = ReplyUtils.getJsonObject(root, factory);
        if (data instanceof Collection)
            return new ReplyResultCollection<>((Collection) data);
        return new ReplyResult<>(data);
    }

    /**
     * Translates an HTTP response to an reply when the content is a binary stream
     *
     * @param response The response
     * @return The reply
     */
    public static Reply fromHttpResponseBinary(HttpResponse response) {
        byte[] content = response.getBodyAsBytes();
        if (content == null || content.length == 0)
            return ReplySuccess.instance();
        return new ReplyResult<>(content);
    }

    /**
     * Translates an HTTP response to an reply for other content types
     *
     * @param response The response
     * @return The reply
     */
    public static Reply fromHttpResponseOther(HttpResponse response) {
        String content = response.getBodyAsString();
        if (content == null || content.isEmpty())
            return ReplySuccess.instance();
        return new ReplyResult<>(content);
    }

    /**
     * Parses a reply serialized in JSON
     *
     * @param content The content
     * @param factory The factory to use
     * @return The result
     */
    public static Reply parse(String content, ApiFactory factory) {
        BufferedLogger bufferedLogger = new BufferedLogger();
        ASTNode root = Json.parse(bufferedLogger, content);
        if (root == null)
            return new ReplyFailure(bufferedLogger.getErrorsAsString());
        if (root.getSymbol().getID() == JsonParser.ID.array)
            return new ReplyFailure("Unexpected JSON format");
        return parse(root, factory);
    }

    /**
     * Parses a result serialized in JSON
     *
     * @param root    The content
     * @param factory The factory to use
     * @return The result
     */
    public static Reply parse(ASTNode root, ApiFactory factory) {
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
            return new ReplyFailure("Unexpected JSON format");
        if (ReplyApiError.class.getCanonicalName().equals(kind))
            return new ReplyApiError(ReplyApiError.parseApiError(nodePayload), message);
        if (ReplyException.class.getCanonicalName().equals(kind))
            return new ReplyException(null); // exception not preserved
        if (ReplyExpiredSession.class.getCanonicalName().equals(kind))
            return ReplyExpiredSession.instance();
        if (ReplyFailure.class.getCanonicalName().equals(kind))
            return new ReplyFailure(message);
        if (ReplyNetworkError.class.getCanonicalName().equals(kind))
            return new ReplyNetworkError(message);
        if (ReplyResult.class.getCanonicalName().equals(kind))
            return new ReplyResult<>(getJsonObject(nodePayload, factory));
        if (ReplyResultCollection.class.getCanonicalName().equals(kind))
            return new ReplyResultCollection<>((Collection<?>) getJsonObject(nodePayload, factory));
        if (ReplySuccess.class.getCanonicalName().equals(kind))
            return new ReplySuccess(message);
        if (ReplyUnauthenticated.class.getCanonicalName().equals(kind))
            return ReplyUnauthenticated.instance();
        if (ReplyUnauthorized.class.getCanonicalName().equals(kind))
            return ReplyUnauthorized.instance();
        if (ReplyUnsupported.class.getCanonicalName().equals(kind))
            return ReplyUnsupported.instance();

        if (isSuccess)
            return new ReplySuccess(message);
        else
            return new ReplyFailure(message);
    }

    /**
     * Gets an object representing the specified JSON object
     *
     * @param node    The root AST for the object
     * @param factory The factory to use
     * @return The JSON object
     */
    public static Object getJsonObject(ASTNode node, ApiFactory factory) {
        // is this an array ?
        if (node.getSymbol().getID() == JsonParser.ID.array) {
            List<Object> value = new ArrayList<>();
            for (ASTNode child : node.getChildren()) {
                value.add(getJsonObject(child, factory));
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
            result.addProperty(memberName, getJsonObject(memberValue, factory));
        }
        return result;
    }
}
