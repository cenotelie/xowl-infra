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

package org.xowl.infra.server.api;

import org.xowl.infra.store.Repository;
import org.xowl.infra.store.sparql.Result;
import org.xowl.infra.store.sparql.ResultFailure;
import org.xowl.infra.store.sparql.ResultUtils;
import org.xowl.infra.utils.api.ApiDeserializer;
import org.xowl.infra.utils.api.Reply;
import org.xowl.infra.utils.api.ReplyResult;
import org.xowl.infra.utils.api.ReplyUtils;
import org.xowl.infra.utils.http.HttpConstants;
import org.xowl.infra.utils.http.HttpResponse;

import java.io.IOException;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.util.List;

/**
 * Utility APIs for the xOWL Server Protocol
 *
 * @author Laurent Wouters
 */
public class XOWLReplyUtils {
    /**
     * Translates an API reply to an HTTP response
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
     * Translates an HTTP response to an API reply
     *
     * @param response     The response
     * @param deserializer The deserializer to use
     * @return The API Reply
     */
    public static Reply fromHttpResponse(HttpResponse response, ApiDeserializer deserializer) {
        if (response != null) {
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
                }
            }
        }
        return ReplyUtils.fromHttpResponse(response, deserializer);
    }

    /**
     * Translates an HTTP response to an API reply when the content type is a SPARQL result
     *
     * @param response The response
     * @return The API Reply
     */
    private static Reply fromHttpResponseSPARQL(HttpResponse response) {
        String content = response.getBodyAsString();
        if (content != null && content.isEmpty())
            content = null;
        Result sparqlResult = ResultUtils.parseResponse(content, response.getContentType());
        return new ReplyResult<>(sparqlResult);
    }
}
