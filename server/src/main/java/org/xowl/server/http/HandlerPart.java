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
import org.xowl.server.db.Controller;
import org.xowl.server.db.Database;
import org.xowl.server.db.User;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

/**
 * A part of a HTTP handler
 *
 * @author Laurent Wouters
 */
abstract class HandlerPart {
    /**
     * The top controller
     */
    protected final Controller controller;

    /**
     * Initializes this handler
     *
     * @param controller The current controller
     */
    public HandlerPart(Controller controller) {
        this.controller = controller;
    }

    /**
     * Handles an HTTP exchange
     *
     * @param httpExchange The HTTP exchange
     * @param method       The HTTP method
     * @param contentType  The content type for the request body
     * @param body         The request body
     * @param user         The current user
     * @param database     The current database
     */
    public abstract void handle(HttpExchange httpExchange, String method, String contentType, String body, User user, Database database);

    /**
     * Ends the current exchange on error
     *
     * @param httpExchange The current exchange
     * @param code         The http code
     * @param message      The response body message
     */
    protected void response(HttpExchange httpExchange, int code, String message) {
        byte[] buffer = message != null ? message.getBytes(Charset.forName("UTF-8")) : new byte[0];
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
