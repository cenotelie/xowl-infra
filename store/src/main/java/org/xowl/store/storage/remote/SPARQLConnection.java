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

package org.xowl.store.storage.remote;

import org.xowl.store.AbstractRepository;
import org.xowl.store.http.HttpConnection;
import org.xowl.store.http.HttpResponse;
import org.xowl.store.sparql.Command;
import org.xowl.store.sparql.Result;
import org.xowl.store.sparql.ResultFailure;
import org.xowl.store.sparql.ResultUtils;

import java.net.HttpURLConnection;

/**
 * Manages a connection to a remote SPARQL endpoint
 *
 * @author Laurent Wouters
 */
public class SPARQLConnection extends HttpConnection implements Connection {
    /**
     * Initializes this connection
     *
     * @param endpoint URI of the endpoint
     * @param login    Login for the endpoint, if any, used for an HTTP Basic authentication
     * @param password Password for the endpoint, if any, used for an HTTP Basic authentication
     */
    public SPARQLConnection(String endpoint, String login, String password) {
        super(endpoint, login, password);
    }

    @Override
    public Result sparql(String command) {
        HttpResponse response = request("", command, Command.MIME_SPARQL_QUERY, AbstractRepository.SYNTAX_NQUADS + ", " + Result.SYNTAX_JSON);
        if (response == null)
            return new ResultFailure("connection failed");
        if (response.getCode() != HttpURLConnection.HTTP_OK)
            return new ResultFailure(response.getBodyAsString() != null ? response.getBodyAsString() : "failure (HTTP " + response.getCode() + ")");
        return ResultUtils.parseResponse(response.getBodyAsString(), response.getContentType());
    }
}
