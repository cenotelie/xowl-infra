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
import org.xowl.server.db.ProtocolHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.nio.charset.Charset;
import java.util.Objects;

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

        Headers rHeaders = httpExchange.getRequestHeaders();
        String contentType = Utils.getRequestContentType(rHeaders);
        String body;
        try {
            body = Utils.getRequestBody(httpExchange);
        } catch (IOException exception) {
            controller.getLogger().error(exception);
            response(HttpURLConnection.HTTP_INTERNAL_ERROR, "Failed to read the body");
            return;
        }
        String dbName = httpExchange.getRequestURI().getPath();
        System.out.println(dbName);
    }

    @Override
    protected InetAddress getClient() {
        return httpExchange.getRemoteAddress().getAddress();
    }

    @Override
    protected void onExit() {
        // do nothing
    }


    /*
    private void onGet(HttpExchange httpExchange, String contentType, String body, User user, Database database) {
        Map<String, List<String>> params = Utils.getRequestParameters(httpExchange.getRequestURI());
        List<String> vQuery = params.get("query");
        String query = vQuery == null ? null : vQuery.get(0);
        if (query != null) {
            if (body != null && !body.isEmpty()) {
                // should be empty
                // ill-formed request
                response(httpExchange, Utils.HTTP_CODE_PROTOCOL_ERROR, "Ill-formed request, content is not empty");
            } else {
                List<String> acceptTypes = Utils.getAcceptTypes(httpExchange.getRequestHeaders());
                List<String> defaults = params.get("default-graph-uri");
                List<String> named = params.get("named-graph-uri");
                String resultType = Utils.negotiateType(acceptTypes);
                executeSPARQL(
                        httpExchange,
                        query,
                        defaults == null ? new ArrayList<String>() : defaults,
                        named == null ? new ArrayList<String>() : named,
                        resultType, database);
            }
        } else {
            // ill-formed request
            response(httpExchange, Utils.HTTP_CODE_PROTOCOL_ERROR, "Ill-formed request, expected a query parameter");
        }
    }

    private void onPost(HttpExchange httpExchange, String contentType, String body, User user, Database database) {
        Map<String, List<String>> params = Utils.getRequestParameters(httpExchange.getRequestURI());
        List<String> acceptTypes = Utils.getAcceptTypes(httpExchange.getRequestHeaders());
        switch (contentType) {
            case TYPE_SPARQL_QUERY:
            case TYPE_SPARQL_UPDATE: {
                List<String> defaults = params.get("default-graph-uri");
                List<String> named = params.get("named-graph-uri");
                String resultType = Utils.negotiateType(acceptTypes);
                executeSPARQL(
                        httpExchange,
                        body,
                        defaults == null ? new ArrayList<String>() : defaults,
                        named == null ? new ArrayList<String>() : named,
                        resultType, database);
                break;
            }
            case TYPE_URL_ENCODED: {
                // TODO: decode and implement this
                response(httpExchange, Utils.HTTP_CODE_INTERNAL_ERROR, "Not implemented");
                break;
            }
        }
    }

    private void executeSPARQL(HttpExchange httpExchange, String body, Collection<String> defaultIRIs, Collection<String> namedIRIs, String contentType, Database database) {
        BufferedLogger bufferedLogger = new BufferedLogger();
        DispatchLogger dispatchLogger = new DispatchLogger(database.getLogger(), bufferedLogger);
        SPARQLLoader loader = new SPARQLLoader(database.getRepository().getStore(), defaultIRIs, namedIRIs);
        List<Command> commands = loader.load(dispatchLogger, new StringReader(body));
        if (commands == null) {
            // ill-formed request
            dispatchLogger.error("Failed to parse and load the request");
            response(httpExchange, Utils.HTTP_CODE_PROTOCOL_ERROR, Utils.getLog(bufferedLogger));
            return;
        }
        Result result = ResultFailure.INSTANCE;
        for (Command command : commands) {
            result = command.execute(database.getRepository());
            if (result.isFailure()) {
                break;
            }
        }
        StringWriter writer = new StringWriter();
        try {
            result.print(writer, Utils.coerceContentType(result, contentType));
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        httpExchange.getResponseHeaders().add("Content-Type", Utils.coerceContentType(result, contentType));
        response(
                httpExchange,
                result.isFailure() ? Utils.HTTP_CODE_INTERNAL_ERROR : Utils.HTTP_CODE_OK,
                writer.toString());
    }

    */


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
