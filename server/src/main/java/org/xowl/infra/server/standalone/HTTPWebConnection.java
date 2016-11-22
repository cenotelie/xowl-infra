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

package org.xowl.infra.server.standalone;

import com.sun.net.httpserver.HttpExchange;
import org.xowl.infra.utils.Files;
import org.xowl.infra.utils.concurrent.SafeRunnable;
import org.xowl.infra.utils.http.HttpConstants;
import org.xowl.infra.utils.logging.Logging;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.*;

/**
 * Represents an active connection to the HTTP server for the request of a resource
 *
 * @author Laurent Wouters
 */
class HTTPWebConnection extends SafeRunnable implements Runnable {
    /**
     * The HTTP exchange to treat
     */
    private final HttpExchange httpExchange;

    /**
     * Initializes this connection
     *
     * @param exchange The HTTP exchange to treat
     */
    public HTTPWebConnection(HttpExchange exchange) {
        super(Logging.getDefault());
        this.httpExchange = exchange;
    }

    @Override
    public void doRun() {
        // add caching headers
        httpExchange.getResponseHeaders().put(HttpConstants.HEADER_CACHE_CONTROL, Arrays.asList("public", "max-age=31536000", "immutable"));
        httpExchange.getResponseHeaders().put(HttpConstants.HEADER_STRICT_TRANSPORT_SECURITY, Collections.singletonList("max-age=31536000"));
        httpExchange.getResponseHeaders().put(HttpConstants.HEADER_X_FRAME_OPTIONS, Collections.singletonList("deny"));
        httpExchange.getResponseHeaders().put(HttpConstants.HEADER_X_XSS_PROTECTION, Collections.singletonList("1; mode=block"));
        httpExchange.getResponseHeaders().put(HttpConstants.HEADER_X_CONTENT_TYPE_OPTIONS, Collections.singletonList("nosniff"));
        String method = httpExchange.getRequestMethod();
        if (Objects.equals(method, HttpConstants.METHOD_OPTIONS)) {
            // assume a pre-flight CORS request
            response(HttpURLConnection.HTTP_OK, null);
            return;
        }

        String resource = httpExchange.getRequestURI().getPath().substring("/web/".length());
        while (resource.endsWith("/")) {
            resource = resource.substring(0, resource.length() - 1);
        }

        if (Objects.equals(method, HttpConstants.METHOD_GET)) {
            serveResource(resource);
        } else {
            response(HttpURLConnection.HTTP_BAD_METHOD, null);
        }
    }

    @Override
    protected void onRunFailed(Throwable throwable) {
        // on failure, attempt to close the connection
        response(HttpURLConnection.HTTP_INTERNAL_ERROR, throwable.getMessage());
    }


    private static class Resource {
        /**
         * The content of the resource
         */
        public final byte[] content;
        /**
         * The MIME type for the resource
         */
        public final String mime;

        /**
         * Initializes this resource
         *
         * @param content The content of the resource
         * @param mime    The MIME type for the resource
         */
        public Resource(byte[] content, String mime) {
            this.content = content;
            this.mime = mime;
        }
    }

    /**
     * The cache for serving HTTP resources
     */
    private static final Map<String, Resource> CACHE = new HashMap<>();

    /**
     * Serves an embedded resource
     *
     * @param resource The requested resource
     */
    private void serveResource(String resource) {
        if (resource.isEmpty())
            resource = "index.html";

        Resource data = CACHE.get(resource);
        if (data != null) {
            serveResource(data);
            return;
        }

        InputStream input = HTTPAPIConnection.class.getResourceAsStream("/org/xowl/infra/server/site/" + resource);
        if (input == null) {
            response(HttpURLConnection.HTTP_NOT_FOUND, null);
            return;
        }
        try {
            byte[] buffer = Files.load(input);
            input.close();
            data = new Resource(buffer, getMime(resource));
        } catch (IOException exception) {
            // do nothing
        }

        CACHE.put(resource, data);
        serveResource(data);
    }

    /**
     * Determines the MIME type of a resource
     *
     * @param resource The resource's name
     * @return The MIME type
     */
    private String getMime(String resource) {
        if (resource.endsWith(".html"))
            return "text/html";
        if (resource.endsWith(".css"))
            return "text/css";
        if (resource.endsWith(".js"))
            return "application/javascript";
        if (resource.endsWith(".txt"))
            return "text/plain";
        if (resource.endsWith(".eot"))
            return "application/octet-stream";
        if (resource.endsWith(".ttf"))
            return "application/octet-stream";
        if (resource.endsWith(".woff"))
            return "application/font-woff";
        if (resource.endsWith(".woff2"))
            return "application/font-woff";
        if (resource.endsWith(".svg"))
            return "image/svg+xml";
        if (resource.endsWith(".png"))
            return "image/png";
        if (resource.endsWith(".gif"))
            return "image/gif";
        return "";
    }

    /**
     * Serves the specified resource data
     *
     * @param data The resource's data
     */
    private void serveResource(Resource data) {
        Utils.enableCORS(httpExchange.getRequestHeaders(), httpExchange.getResponseHeaders());
        httpExchange.getResponseHeaders().put(HttpConstants.HEADER_CONTENT_TYPE, Collections.singletonList(data.mime));
        try {
            httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, data.content.length);
        } catch (IOException exception) {
            logger.error(exception);
        }
        try (OutputStream output = httpExchange.getResponseBody()) {
            output.write(data.content);
        } catch (IOException exception) {
            logger.error(exception);
        }
    }

    /**
     * Ends the current exchange with the specified message and response code
     *
     * @param code    The http code
     * @param message The response body message
     */
    private void response(int code, String message) {
        byte[] buffer = message != null ? message.getBytes(Files.CHARSET) : new byte[0];
        Utils.enableCORS(httpExchange.getRequestHeaders(), httpExchange.getResponseHeaders());
        try {
            httpExchange.sendResponseHeaders(code, buffer.length);
        } catch (IOException exception) {
            logger.error(exception);
        }
        try (OutputStream stream = httpExchange.getResponseBody()) {
            stream.write(buffer);
        } catch (IOException exception) {
            logger.error(exception);
        }
    }
}
