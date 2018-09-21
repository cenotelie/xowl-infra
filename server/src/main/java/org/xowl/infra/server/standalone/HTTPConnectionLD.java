/*******************************************************************************
 * Copyright (c) 2018 Association Cénotélie (cenotelie.fr)
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
import fr.cenotelie.commons.utils.IOUtils;
import fr.cenotelie.commons.utils.api.Reply;
import fr.cenotelie.commons.utils.http.HttpConstants;
import fr.cenotelie.commons.utils.http.HttpResponse;
import fr.cenotelie.commons.utils.logging.Logging;
import org.xowl.infra.server.ServerConfiguration;
import org.xowl.infra.server.api.XOWLReplyUtils;
import org.xowl.infra.server.impl.ControllerServer;
import org.xowl.infra.server.impl.UserImpl;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Represents a linked data connection for the server
 *
 * @author Laurent Wouters
 */
public class HTTPConnectionLD implements Runnable {
    /**
     * The empty message
     */
    private static final byte[] EMPTY_MESSAGE = new byte[0];

    /**
     * The current configuration
     */
    private final ServerConfiguration configuration;
    /**
     * The current controller
     */
    private final ControllerServer controller;
    /**
     * The HTTP exchange to treat
     */
    private final HttpExchange httpExchange;

    /**
     * Initializes this connection
     *
     * @param configuration The current configuration
     * @param controller    The current controller
     * @param exchange      The HTTP exchange to treat
     */
    public HTTPConnectionLD(ServerConfiguration configuration, ControllerServer controller, HttpExchange exchange) {
        this.configuration = configuration;
        this.controller = controller;
        this.httpExchange = exchange;
    }

    @Override
    public void run() {
        // add caching headers
        httpExchange.getResponseHeaders().put(HttpConstants.HEADER_CACHE_CONTROL, Arrays.asList("private", "no-cache", "no-store", "no-transform", "must-revalidate"));
        httpExchange.getResponseHeaders().put(HttpConstants.HEADER_STRICT_TRANSPORT_SECURITY, Collections.singletonList("max-age=31536000"));
        String method = httpExchange.getRequestMethod();
        if (Objects.equals(method, HttpConstants.METHOD_OPTIONS)) {
            // assume a pre-flight CORS request
            response(HttpURLConnection.HTTP_OK);
            return;
        }

        UserImpl user = controller.getPrincipal(configuration.getLinkedDataPublicUser());
        String resource = httpExchange.getRequestURI().toString();
        Reply reply = controller.sparql(user, configuration.getLinkedDataPublicDb(), "DESCRIBE <" + resource + ">", null, null);
        response(reply);
    }

    /**
     * Ends the current exchange with a response code
     *
     * @param code The http code
     */
    private void response(int code) {
        Utils.enableCORS(httpExchange.getRequestHeaders(), httpExchange.getResponseHeaders());
        try {
            httpExchange.sendResponseHeaders(code, 0);
        } catch (IOException exception) {
            Logging.get().error(exception);
        }
        try (OutputStream stream = httpExchange.getResponseBody()) {
            stream.write(EMPTY_MESSAGE);
        } catch (IOException exception) {
            Logging.get().error(exception);
        }
    }

    /**
     * Ends the current exchange with the specified message and response code
     *
     * @param code    The http code
     * @param message The response body message
     */
    private void response(int code, String message) {
        byte[] buffer = message != null ? message.getBytes(IOUtils.CHARSET) : new byte[0];
        Utils.enableCORS(httpExchange.getRequestHeaders(), httpExchange.getResponseHeaders());
        try {
            if (buffer.length > 0 && !httpExchange.getResponseHeaders().containsKey(HttpConstants.HEADER_CONTENT_TYPE))
                httpExchange.getResponseHeaders().add(HttpConstants.HEADER_CONTENT_TYPE, HttpConstants.MIME_TEXT_PLAIN);
            httpExchange.sendResponseHeaders(code, buffer.length);
        } catch (IOException exception) {
            Logging.get().error(exception);
        }
        try (OutputStream stream = httpExchange.getResponseBody()) {
            stream.write(buffer);
        } catch (IOException exception) {
            Logging.get().error(exception);
        }
    }

    /**
     * Ends the current exchange with a protocol reply
     *
     * @param reply The protocol reply
     */
    private void response(Reply reply) {
        List<String> acceptTypes = Utils.getAcceptTypes(httpExchange.getRequestHeaders());
        HttpResponse response = XOWLReplyUtils.toHttpResponse(reply, acceptTypes);
        if (response.getContentType() != null)
            httpExchange.getResponseHeaders().add(HttpConstants.HEADER_CONTENT_TYPE, response.getContentType());
        response(response.getCode(), response.getBodyAsString());
    }
}
