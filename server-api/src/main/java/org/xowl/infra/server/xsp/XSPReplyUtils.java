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

package org.xowl.infra.server.xsp;

import fr.cenotelie.hime.redist.ASTNode;
import org.xowl.infra.store.Repository;
import org.xowl.infra.store.sparql.Result;
import org.xowl.infra.store.sparql.ResultFailure;
import org.xowl.infra.store.sparql.ResultUtils;
import org.xowl.infra.utils.api.*;
import org.xowl.infra.utils.http.HttpConstants;
import org.xowl.infra.utils.http.HttpResponse;
import org.xowl.infra.utils.json.Json;
import org.xowl.infra.utils.logging.BufferedLogger;

import java.io.IOException;
import java.io.StringWriter;
import java.net.HttpURLConnection;
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
    public static HttpResponse toHttpResponse(Reply reply, List<String> acceptTypes) {
        // handle special case of SPARQL
        if (reply instanceof ReplyResult && ((ReplyResult) reply).getData() instanceof Result) {
            // special handling for SPARQL
            Result sparqlResult = (Result) ((ReplyResult) reply).getData();
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
        return ReplyUtils.toHttpResponse(reply);
    }

    /**
     * Translates an HTTP response to an XSP reply
     *
     * @param response     The response
     * @param deserializer The deserializer to use
     * @return The XSP reply
     */
    public static Reply fromHttpResponse(HttpResponse response, ApiDeserializer deserializer) {
        // XSP replies mapped to HTTP error codes
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
                    return ReplyUtils.fromHttpResponseJSON(response, deserializer);
                case HttpConstants.MIME_OCTET_STREAM:
                    return ReplyUtils.fromHttpResponseBinary(response);
                default:
                    // other text
                    return ReplyUtils.fromHttpResponseOther(response);
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
     * Translates an HTTP response to an XSP reply when the content type is a SPARQL result
     *
     * @param response The response
     * @return The XSP reply
     */
    private static Reply fromHttpResponseSPARQL(HttpResponse response) {
        String content = response.getBodyAsString();
        if (content != null && content.isEmpty())
            content = null;
        Result sparqlResult = ResultUtils.parseResponse(content, response.getContentType());
        return new ReplyResult<>(sparqlResult);
    }
}
