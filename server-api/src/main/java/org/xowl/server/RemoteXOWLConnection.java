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

package org.xowl.server;

import org.xowl.server.xsp.*;
import org.xowl.store.http.HttpConstants;
import org.xowl.store.http.HttpResponse;
import org.xowl.store.sparql.Result;
import org.xowl.store.sparql.ResultUtils;
import org.xowl.store.storage.remote.SPARQLConnection;

import java.net.HttpURLConnection;

/**
 * Implements a connection to a remote xOWL Server
 * @author Laurent Wouters
 */
public class RemoteXOWLConnection extends SPARQLConnection {
    /**
     * Initializes this connection
     *
     * @param endpoint URI of the endpoint
     * @param login    Login for the endpoint, if any, used for an HTTP Basic authentication
     * @param password Password for the endpoint, if any, used for an HTTP Basic authentication
     */
    public RemoteXOWLConnection(String endpoint, String login, String password) {
        super(endpoint, login, password);
    }

    public XSPReply xsp(String command) {
        HttpResponse response = request("", command, XSPReply.MIME_XSP_COMMAND, Result.SYNTAX_JSON);
        if (response == null)
            return new XSPReplyNetworkError("connection failed");
        if (response.getCode() == HttpURLConnection.HTTP_UNAUTHORIZED)
            return XSPReplyUnauthenticated.instance();
        if (response.getCode() == HttpURLConnection.HTTP_FORBIDDEN)
            return XSPReplyUnauthorized.instance();
        if (response.getCode() == HttpURLConnection.HTTP_INTERNAL_ERROR)
            return new XSPReplyFailure(response.getBodyAsString());
        if (response.getCode() == HttpConstants.HTTP_UNKNOWN_ERROR)
            return new XSPReplyFailure(response.getBodyAsString());
        if (response.getCode() != HttpURLConnection.HTTP_OK)
            return new XSPReplyFailure(response.getBodyAsString() != null ? response.getBodyAsString() : "failure (HTTP " + response.getCode() + ")");
        // the result is OK from hereon
        if (response.getBodyAsString() == null)
            return XSPReplySuccess.instance();
        if (response.getContentType() == null || HttpConstants.MIME_TEXT_PLAIN.equals(response.getContentType()))
            // no response type or plain text
            return new XSPReplyResult<>(response.getBodyAsString());
        if (HttpConstants.MIME_JSON.equals(response.getContentType()))
            // pure JSON response
            return XSPReplyUtils.parseJSONResult(response.getBodyAsString());
        // assume SPARQL reply
        Result sparqlResult = ResultUtils.parseResponse(response.getBodyAsString(), response.getContentType());
        return new XSPReplyResult<>(sparqlResult);
    }
}
