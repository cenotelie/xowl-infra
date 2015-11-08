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
import org.xowl.server.db.*;
import org.xowl.store.IOUtils;
import org.xowl.store.rdf.RuleExplanation;
import org.xowl.store.rete.MatchStatus;
import org.xowl.store.sparql.Result;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.nio.charset.Charset;
import java.util.*;

/**
 * Represents an active connection to the HTTP server
 *
 * @author Laurent Wouters
 */
class HTTPConnection extends ProtocolHandler implements Runnable {
    /**
     * The content type for a SPARQL URL encoded message body
     */
    public static final String SPARQL_TYPE_URL_ENCODED = "application/x-www-form-urlencoded";
    /**
     * The content type for a SPARQL query in a message body
     */
    public static final String SPARQL_TYPE_SPARQL_QUERY = "application/sparql-query";
    /**
     * The content type for a SPARQL update in a message body
     */
    public static final String SPARQL_TYPE_SPARQL_UPDATE = "application/sparql-update";
    /**
     * The content type for a xOWL XSP command
     */
    public static final String XOWL_TYPE_COMMAND = "application/x-xowl-xsp";

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
    public HTTPConnection(Controller controller, HttpExchange exchange) {
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

        user = controller.user(httpExchange.getPrincipal().getUsername());
        if (user == null) {
            // should not happen ...
            httpExchange.getResponseHeaders().add("WWW-Authenticate", "Basic realm=\"" + controller.getConfiguration().getSecurityRealm() + "\"");
            response(HttpURLConnection.HTTP_UNAUTHORIZED, "Failed to login");
            return;
        }

        String resource = httpExchange.getRequestURI().getPath().substring(1);
        Database database = null;
        if (resource.startsWith("db/")) {
            // requesting a specified database
            String dbName = resource.substring(3);
            int index = dbName.indexOf("/");
            if (index != -1) {
                dbName = dbName.substring(0, index);
            }
            ProtocolReply dbReply = controller.getDatabase(user, dbName);
            if (!dbReply.isSuccess()) {
                response(HttpURLConnection.HTTP_FORBIDDEN, "Forbidden");
                return;
            }
            database = ((ProtocolReplyResult<Database>) dbReply).getData();
        }

        if (Objects.equals(method, "GET")) {
            if (database == null) {
                serveResource(resource);
            } else {
                onGetSPARQL(database);
            }
            return;
        }
        if (Objects.equals(method, "POST")) {
            onPost(database);
            return;
        }
        response(HttpURLConnection.HTTP_BAD_METHOD, null);
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
    private void onGetSPARQL(Database database) {
        Map<String, List<String>> params = Utils.getRequestParameters(httpExchange.getRequestURI());
        List<String> vQuery = params.get("query");
        String query = vQuery == null ? null : vQuery.get(0);
        String body;
        try {
            body = Utils.getRequestBody(httpExchange);
        } catch (IOException exception) {
            controller.getLogger().error(exception);
            response(HttpURLConnection.HTTP_INTERNAL_ERROR, "Failed to read the body");
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
                ProtocolReply reply = controller.sparql(user, database, body, defaults, named);
                response(reply);
            }
        } else {
            // ill-formed request
            response(HttpURLConnection.HTTP_BAD_REQUEST, null);
        }
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

        InputStream input = HTTPConnection.class.getResourceAsStream("/org/xowl/server/site/" + resource);
        if (input == null) {
            response(HttpURLConnection.HTTP_NOT_FOUND, null);
            return;
        }
        try {
            buffer = Utils.load(input);
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
     * When the method is POST
     *
     * @param database The target database
     */
    private void onPost(Database database) {
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
            response(HttpURLConnection.HTTP_INTERNAL_ERROR, "Failed to read the body");
            return;
        }
        switch (contentType) {
            case SPARQL_TYPE_SPARQL_QUERY:
            case SPARQL_TYPE_SPARQL_UPDATE:
                onPostSPARQL(database, body);
                break;
            case SPARQL_TYPE_URL_ENCODED:
                // TODO: decode and implement this
                response(HttpURLConnection.HTTP_INTERNAL_ERROR, "Not implemented");
                break;
            case XOWL_TYPE_COMMAND:
                onPostCommand(body);
                break;
        }
    }

    /**
     * Answers to a SPARQL request on a POST method
     *
     * @param database The target database
     * @param body     The request body
     */
    private void onPostSPARQL(Database database, String body) {
        Map<String, List<String>> params = Utils.getRequestParameters(httpExchange.getRequestURI());
        List<String> defaults = params.get("default-graph-uri");
        List<String> named = params.get("named-graph-uri");
        ProtocolReply reply = controller.sparql(user, database, body, defaults, named);
        response(reply);
    }

    /**
     * Answer to a XSP command on a POST method
     *
     * @param body The request body
     */
    private void onPostCommand(String body) {
        ProtocolReply reply = execute(body);
        response(reply);
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
    private void response(ProtocolReply reply) {
        if (reply == null) {
            // client got banned
            response(HttpURLConnection.HTTP_FORBIDDEN, "Forbidden");
            return;
        }
        if (reply instanceof ProtocolReplyUnauthenticated) {
            response(HttpURLConnection.HTTP_UNAUTHORIZED, null);
            return;
        }
        if (reply instanceof ProtocolReplyUnauthorized) {
            response(HttpURLConnection.HTTP_FORBIDDEN, "Forbidden");
            return;
        }
        if (reply instanceof ProtocolReplyFailure) {
            response(HttpURLConnection.HTTP_INTERNAL_ERROR, reply.getMessage());
            return;
        }
        if (!(reply instanceof ProtocolReplyResult)) {
            // other successes
            response(HttpURLConnection.HTTP_OK, reply.getMessage());
        }

        List<String> acceptTypes = Utils.getAcceptTypes(httpExchange.getRequestHeaders());
        String resultType = Utils.negotiateType(acceptTypes);

        Object data = ((ProtocolReplyResult) reply).getData();
        if (data instanceof Collection) {
            httpExchange.getResponseHeaders().add("Content-Type", "application/json");
            StringBuilder builder = new StringBuilder("{ \"results\": [");
            boolean first = true;
            for (Object elem : ((Collection) data)) {
                if (!first)
                    builder.append(", ");
                first = false;
                builder.append("\"");
                builder.append(IOUtils.escapeStringJSON(elem.toString()));
                builder.append("\"");
            }
            response(HttpURLConnection.HTTP_OK, builder.toString());
        } else if (data instanceof Result) {
            Result sparqlResult = (Result) data;
            StringWriter writer = new StringWriter();
            try {
                sparqlResult.print(writer, Utils.coerceContentType(sparqlResult, resultType));
            } catch (IOException exception) {
                // cannot happen
                exception.printStackTrace();
            }
            httpExchange.getResponseHeaders().add("Content-Type", Utils.coerceContentType(sparqlResult, resultType));
            response(sparqlResult.isSuccess() ? HttpURLConnection.HTTP_OK : HttpURLConnection.HTTP_INTERNAL_ERROR, writer.toString());
        } else if (data instanceof MatchStatus) {
            httpExchange.getResponseHeaders().add("Content-Type", "application/json");
            StringWriter writer = new StringWriter();
            try {
                ((MatchStatus) data).printJSON(writer);
            } catch (IOException exception) {
                // cannot happen
                exception.printStackTrace();
            }
            response(HttpURLConnection.HTTP_OK, writer.toString());
        } else if (data instanceof RuleExplanation) {
            httpExchange.getResponseHeaders().add("Content-Type", "application/json");
            StringWriter writer = new StringWriter();
            try {
                ((RuleExplanation) data).printJSON(writer);
            } catch (IOException exception) {
                // cannot happen
                exception.printStackTrace();
            }
            response(HttpURLConnection.HTTP_OK, writer.toString());
        } else {
            httpExchange.getResponseHeaders().add("Content-Type", "text");
            response(HttpURLConnection.HTTP_OK, data.toString());
        }
    }
}
