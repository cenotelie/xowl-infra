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

package org.xowl.server.http;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.xowl.server.db.Controller;
import org.xowl.server.db.Database;
import org.xowl.server.db.UserSession;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * The aggregated HTTP handler that supports pure SPARQL and xOWL extensions
 *
 * @author Laurent Wouters
 */
class FullHandler implements HttpHandler {
    /**
     * The top controller
     */
    private final Controller controller;
    /**
     * The handler parts
     */
    private final Map<String, Map<String, HandlerPart>> parts;

    /**
     * Initializes this handler
     *
     * @param controller The current controller
     */
    public FullHandler(Controller controller) {
        this.controller = controller;
        this.parts = new HashMap<>();
        this.parts.put("GET", new HashMap<String, HandlerPart>());
        this.parts.put("POST", new HashMap<String, HandlerPart>());
        SPARQLHandler sparql = new SPARQLHandler(controller);
        this.parts.get("GET").put(null, sparql);
        this.parts.get("POST").put(SPARQLHandler.TYPE_SPARQL_QUERY, sparql);
        this.parts.get("POST").put(SPARQLHandler.TYPE_SPARQL_UPDATE, sparql);
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        // getRequestMethod() to determine the command
        // getRequestHeaders() to examine the request headers (if needed)
        // getRequestBody() returns a InputStream for reading the request body. After reading the request body, the stream is close.
        // getResponseHeaders() to set any response headers, except content-length
        // sendResponseHeaders(int,long) to send the response headers. Must be called before next step.
        // getResponseBody() to get a OutputStream to send the response body. When the response body has been written, the stream must be closed to terminate the exchange.

        String method = httpExchange.getRequestMethod();
        Headers rHeaders = httpExchange.getRequestHeaders();
        UserSession session = getSession(rHeaders);
        if (session == null) {
            endOnError(httpExchange, 403, "Failed to login");
        }
        Database database = getDatabase(rHeaders);


    }

    /**
     * Ends the current exchange on error
     * @param httpExchange The current exchange
     * @param code The error code
     * @param message The error message
     */
    private void endOnError(HttpExchange httpExchange, int code, String message) {
        Headers headers = httpExchange.getResponseHeaders();
        Utils.enableCORS(httpExchange);
    }


    /**
     * Gets a user session for the specified exchange
     *
     * @param headers The request headers
     * @return The user session
     */
    private UserSession getSession(Headers headers) {
        if (headers.containsKey(Utils.HEADER_USER_TOKEN)) {
            return controller.getSession(headers.getFirst(Utils.HEADER_USER_TOKEN));
        }
        return controller.login(
                headers.getFirst(Utils.HEADER_USER_LOGIN),
                headers.getFirst(Utils.HEADER_USER_PASSWORD));
    }

    /**
     * Gets the database for the specified exchange
     *
     * @param headers The request headers
     * @return The database
     */
    private Database getDatabase(Headers headers) {
        return controller.getDatabase(headers.getFirst(Utils.HEADER_DATABASE));
    }
}
