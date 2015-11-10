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
import org.xowl.server.Program;
import org.xowl.server.db.Controller;
import org.xowl.server.db.ProtocolHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author Laurent Wouters
 */
public class HTTPWebConnection extends ProtocolHandler implements Runnable {
    /**
     * The HTTP exchange to treat
     */
    private final HttpExchange httpExchange;

    /**
     * Initializes this connection
     *
     * @param controller The current controller
     * @param exchange   The HTTP exchange to treat
     */
    public HTTPWebConnection(Controller controller, HttpExchange exchange) {
        super(controller);
        this.httpExchange = exchange;
    }

    @Override
    public void run() {
        String method = httpExchange.getRequestMethod();
        if (Objects.equals(method, "OPTIONS")) {
            // assume a pre-flight CORS request
            response(HttpURLConnection.HTTP_OK, null);
            return;
        }

        if (httpExchange.getPrincipal() != null) {
            user = controller.user(httpExchange.getPrincipal().getUsername());
        }
        String resource = httpExchange.getRequestURI().getPath().substring("/web".length());

        if (user == null && resource.startsWith("/auth")) {
            httpExchange.getResponseHeaders().add("WWW-Authenticate", "Basic realm=\"" + controller.getConfiguration().getSecurityRealm() + "\"");
            response(HttpURLConnection.HTTP_FORBIDDEN, "Failed to login");
            return;
        }

        if (Objects.equals(method, "GET")) {
            serveResource(resource);
        } else {
            response(HttpURLConnection.HTTP_BAD_METHOD, null);
        }
    }

    @Override
    protected InetAddress getClient() {
        return httpExchange.getRemoteAddress().getAddress();
    }

    @Override
    protected void onExit() {
        // do nothing
    }

    /**
     * The cache for serving HTTP resources
     */
    private static final Map<String, byte[]> CACHE = new HashMap<>();

    /**
     * Serves an embedded resource
     *
     * @param resource The requested resource
     */
    private void serveResource(String resource) {
        if (resource.isEmpty())
            resource = "index.html";

        byte[] buffer = CACHE.get(resource);
        if (buffer != null) {
            serveResource(buffer);
            return;
        }

        InputStream input = HTTPAPIConnection.class.getResourceAsStream("/org/xowl/server/site/" + resource);
        if (input == null) {
            response(HttpURLConnection.HTTP_NOT_FOUND, null);
            return;
        }
        try {
            buffer = Program.load(input);
            input.close();
        } catch (IOException exception) {
            // do nothing
        }
        CACHE.put(resource, buffer);
        serveResource(buffer);
    }

    /**
     * Serves the resource in the specified buffer
     *
     * @param buffer The buffer containing the resource
     */
    private void serveResource(byte[] buffer) {
        Headers headers = httpExchange.getResponseHeaders();
        Utils.enableCORS(headers);
        try {
            httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, buffer.length);
        } catch (IOException exception) {
            controller.getLogger().error(exception);
        }
        try (OutputStream output = httpExchange.getResponseBody()) {
            output.write(buffer);
        } catch (IOException exception) {
            controller.getLogger().error(exception);
        }
    }

    /**
     * Ends the current exchange with the specified message and response code
     *
     * @param code    The http code
     * @param message The response body message
     */
    private void response(int code, String message) {
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
