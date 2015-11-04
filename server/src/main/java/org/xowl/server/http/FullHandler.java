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
import org.xowl.server.db.User;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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
        String method = httpExchange.getRequestMethod();
        Headers rHeaders = httpExchange.getRequestHeaders();
        User user = controller.getUser(httpExchange.getPrincipal().getUsername());
        if (user == null) {
            httpExchange.getResponseHeaders().add("WWW-Authenticate", "Basic realm=\"" + controller.getConfiguration().getsecurityRealm() + "\"");
            endOnError(httpExchange, Utils.HTTP_CODE_UNAUTHORIZED, "Failed to login");
            return;
        }
        Database database = controller.getDatabase(rHeaders.getFirst(Utils.HEADER_DATABASE));
        if (database == null) {
            endOnError(httpExchange, Utils.HTTP_CODE_NOT_FOUND, "Database not found");
            return;
        }
        String contentType = rHeaders.getFirst("Content-Type");
        String body;
        try {
            body = Utils.getRequestBody(httpExchange);
        } catch (IOException exception) {
            controller.getLogger().error(exception);
            endOnError(httpExchange, Utils.HTTP_CODE_PROTOCOL_ERROR, "Failed to read the body");
            return;
        }

        if (Objects.equals(method, "OPTIONS")) {
            // always respond with CORS capabilities
            Utils.enableCORS(httpExchange.getResponseHeaders());
            try {
                httpExchange.sendResponseHeaders(Utils.HTTP_CODE_OK, 0);
            } catch (IOException exception) {
                controller.getLogger().error(exception);
            }
            try {
                httpExchange.getResponseBody().close();
            } catch (IOException exception) {
                controller.getLogger().error(exception);
            }
            return;
        }

        Map<String, HandlerPart> subs = parts.get(method);
        if (subs == null) {
            endOnError(httpExchange, Utils.HTTP_CODE_INTERNAL_ERROR, "Cannot handle this request");
            return;
        }
        HandlerPart target = subs.get(contentType);
        if (target == null)
            target = subs.get(null);
        if (target == null) {
            endOnError(httpExchange, Utils.HTTP_CODE_INTERNAL_ERROR, "Cannot handle this request");
            return;
        }
        target.handle(httpExchange, method, contentType, body, user, database);
    }

    /**
     * Ends the current exchange on error
     *
     * @param httpExchange The current exchange
     * @param code         The error code
     * @param message      The error message
     */
    public void endOnError(HttpExchange httpExchange, int code, String message) {
        byte[] buffer = message.getBytes(Charset.forName("UTF-8"));
        Headers headers = httpExchange.getResponseHeaders();
        Utils.enableCORS(headers);
        try {
            httpExchange.sendResponseHeaders(code, buffer.length);
        } catch (IOException exception) {
            controller.getLogger().error(exception);
        }
        try (OutputStream stream = httpExchange.getResponseBody()) {
            stream.write(buffer);
        } catch (IOException exception) {
            controller.getLogger().error(exception);
        }
    }
}
