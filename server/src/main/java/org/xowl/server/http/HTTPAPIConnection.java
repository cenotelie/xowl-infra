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
import org.xowl.server.api.Controller;
import org.xowl.server.api.XOWLDatabase;
import org.xowl.server.api.ProtocolHandler;
import org.xowl.store.AbstractRepository;
import org.xowl.store.http.HttpResponse;
import org.xowl.store.sparql.Command;
import org.xowl.server.xsp.*;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Represents an active connection to the HTTP server
 *
 * @author Laurent Wouters
 */
class HTTPAPIConnection extends ProtocolHandler {
    /**
     * The HTTP Content-Type header
     */
    private static final String HEADER_CONTENT_TYPE = "Content-Type";
    /**
     * The content type for a SPARQL URL encoded message body
     */
    public static final String SPARQL_TYPE_URL_ENCODED = "application/x-www-form-urlencoded";

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
    public HTTPAPIConnection(Controller controller, HttpExchange exchange) {
        super(controller);
        this.httpExchange = exchange;
    }

    @Override
    public void doRun() {
        String method = httpExchange.getRequestMethod();
        if (Objects.equals(method, "OPTIONS")) {
            // assume a pre-flight CORS request
            response(HttpURLConnection.HTTP_OK, null);
            return;
        }

        user = controller.user(httpExchange.getPrincipal().getUsername());
        if (user == null) {
            // should not happen ...
            httpExchange.getResponseHeaders().add("WWW-Authenticate", "Basic realm=\"" + controller.getConfiguration().getSecurityRealm() + "\"");
            response(HttpURLConnection.HTTP_UNAUTHORIZED, "Failed to login");
            return;
        }

        String resource = httpExchange.getRequestURI().getPath().substring("/api".length());
        XOWLDatabase database = null;
        if (resource.startsWith("/db/")) {
            // requesting a specified database
            String dbName = resource.substring(4);
            int index = dbName.indexOf("/");
            if (index != -1) {
                dbName = dbName.substring(0, index);
            }
            XSPReply dbReply = controller.getDatabase(user, dbName);
            if (!dbReply.isSuccess()) {
                response(HttpURLConnection.HTTP_FORBIDDEN, null);
                return;
            }
            database = ((XSPReplyResult<XOWLDatabase>) dbReply).getData();
        }

        if (Objects.equals(method, "GET")) {
            if (database == null) {
                // this is most probably an authentication request
                response(HttpURLConnection.HTTP_OK, null);
                return;
            }
            onGetSPARQL(database);
            return;
        }
        if (Objects.equals(method, "POST")) {
            onPost(database);
            return;
        }
        response(HttpURLConnection.HTTP_BAD_METHOD, null);
    }

    @Override
    protected void onRunFailed(Throwable throwable) {
        // on failure, attempt to close the connection
        response(HttpURLConnection.HTTP_INTERNAL_ERROR, throwable.getMessage());
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
     * Answers to a SPARQL request on a GET method
     *
     * @param database The target database
     */
    private void onGetSPARQL(XOWLDatabase database) {
        Map<String, List<String>> params = Utils.getRequestParameters(httpExchange.getRequestURI());
        List<String> vQuery = params.get("query");
        String query = vQuery == null ? null : vQuery.get(0);
        String body;
        try {
            body = Utils.getRequestBody(httpExchange);
        } catch (IOException exception) {
            controller.getLogger().error(exception);
            response(HttpURLConnection.HTTP_BAD_REQUEST, "Failed to read the body");
            return;
        }
        if (query != null) {
            if (body != null && !body.isEmpty()) {
                // should be empty
                // ill-formed request
                response(HttpURLConnection.HTTP_BAD_REQUEST, null);
            } else {
                List<String> defaults = params.get("default-graph-uri");
                List<String> named = params.get("named-graph-uri");
                XSPReply reply = controller.sparql(user, database, body, defaults, named);
                response(reply);
            }
        } else {
            // ill-formed request
            response(HttpURLConnection.HTTP_BAD_REQUEST, null);
        }
    }

    /**
     * When the method is POST
     *
     * @param database The target database
     */
    private void onPost(XOWLDatabase database) {
        Headers rHeaders = httpExchange.getRequestHeaders();
        String contentType = Utils.getRequestContentType(rHeaders);
        if (contentType == null) {
            response(HttpURLConnection.HTTP_BAD_REQUEST, "Missing content type");
            return;
        }
        String body;
        try {
            body = Utils.getRequestBody(httpExchange);
        } catch (IOException exception) {
            controller.getLogger().error(exception);
            response(HttpURLConnection.HTTP_BAD_REQUEST, "Failed to read the body");
            return;
        }
        switch (contentType) {
            case Command.MIME_SPARQL_QUERY:
            case Command.MIME_SPARQL_UPDATE:
                onPostSPARQL(database, body);
                break;
            case SPARQL_TYPE_URL_ENCODED:
                // TODO: decode and implement this
                response(HttpURLConnection.HTTP_NOT_IMPLEMENTED, "Not implemented");
                break;
            case XSPReply.MIME_XSP_COMMAND:
                onPostCommand(body);
                break;
            case AbstractRepository.SYNTAX_NTRIPLES:
            case AbstractRepository.SYNTAX_NQUADS:
            case AbstractRepository.SYNTAX_TURTLE:
            case AbstractRepository.SYNTAX_TRIG:
            case AbstractRepository.SYNTAX_RDFXML:
            case AbstractRepository.SYNTAX_JSON_LD:
            case AbstractRepository.SYNTAX_FUNCTIONAL_OWL2:
            case AbstractRepository.SYNTAX_OWLXML:
            case AbstractRepository.SYNTAX_RDFT:
            case AbstractRepository.SYNTAX_XOWL:
                onPostData(database, contentType, body);
                break;
            default:
                response(HttpURLConnection.HTTP_BAD_REQUEST, null);
                break;
        }
    }

    /**
     * Answers to a SPARQL request on a POST method
     *
     * @param database The target database
     * @param body     The request body
     */
    private void onPostSPARQL(XOWLDatabase database, String body) {
        if (database == null) {
            response(HttpURLConnection.HTTP_FORBIDDEN, "Forbidden");
        } else {
            Map<String, List<String>> params = Utils.getRequestParameters(httpExchange.getRequestURI());
            List<String> defaults = params.get("default-graph-uri");
            List<String> named = params.get("named-graph-uri");
            XSPReply reply = controller.sparql(user, database, body, defaults, named);
            response(reply);
        }
    }

    /**
     * Answers to a XSP command on a POST method
     *
     * @param body The request body
     */
    private void onPostCommand(String body) {
        XSPReply reply = execute(body);
        response(reply);
    }

    /**
     * Answers to the upload of data for a database
     *
     * @param database    The target database
     * @param contentType The request content type
     * @param body        The request body
     */
    private void onPostData(XOWLDatabase database, String contentType, String body) {
        if (database == null) {
            response(HttpURLConnection.HTTP_FORBIDDEN, "Forbidden");
        } else {
            XSPReply reply = controller.upload(user, database, contentType, body);
            response(reply);
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

    /**
     * Ends the current exchange with a protocol reply
     *
     * @param reply The protocol reply
     */
    private void response(XSPReply reply) {
        List<String> acceptTypes = Utils.getAcceptTypes(httpExchange.getRequestHeaders());
        HttpResponse response = XSPReplyUtils.toHttpResponse(reply, acceptTypes);
        if (response.getContentType() != null)
            httpExchange.getResponseHeaders().add(HEADER_CONTENT_TYPE, response.getContentType());
        response(response.getCode(), response.getBodyAsString());
    }
}
