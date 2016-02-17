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

package org.xowl.infra.server.http;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import org.xowl.infra.server.api.XOWLPrivilege;
import org.xowl.infra.server.impl.ServerController;
import org.xowl.infra.server.impl.ServerUser;
import org.xowl.infra.server.xsp.XSPReply;
import org.xowl.infra.server.xsp.XSPReplyUtils;
import org.xowl.infra.store.EntailmentRegime;
import org.xowl.infra.store.http.HttpResponse;
import org.xowl.infra.utils.Files;
import org.xowl.infra.utils.concurrent.SafeRunnable;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Represents an active connection to the HTTP server
 *
 * @author Laurent Wouters
 */
class HTTPAPIConnection extends SafeRunnable {
    /**
     * The HTTP Content-Type header
     */
    private static final String HEADER_CONTENT_TYPE = "Content-Type";
    /**
     * The content type for a SPARQL URL encoded message body
     */
    public static final String SPARQL_TYPE_URL_ENCODED = "application/x-www-form-urlencoded";

    /**
     * The current controller
     */
    private final ServerController controller;
    /**
     * The HTTP exchange to treat
     */
    private final HttpExchange httpExchange;
    /**
     * The client
     */
    private ServerUser client;

    /**
     * Initializes this connection
     *
     * @param controller The current controller
     * @param exchange   The HTTP exchange to treat
     */
    public HTTPAPIConnection(ServerController controller, HttpExchange exchange) {
        super(controller.getLogger());
        this.controller = controller;
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
        client = controller.getPrincipal(httpExchange.getPrincipal().getUsername());
        if (client == null) {
            // should not happen ...
            response(HttpURLConnection.HTTP_UNAUTHORIZED, "Failed to login");
            return;
        }
        String resource = httpExchange.getRequestURI().getPath().substring("/api".length());
        handleRequest(method, resource);
    }

    @Override
    protected void onRunFailed(Throwable throwable) {
        // on failure, attempt to close the connection
        response(HttpURLConnection.HTTP_INTERNAL_ERROR, throwable.getMessage());
    }

    /**
     * Handles the request
     *
     * @param method   The HTTP method
     * @param resource The accessed resource
     * @return The response code
     */
    private int handleRequest(String method, String resource) {
        if (resource.equals("/whoami")) {
            return handleResourceWhoami(method);
        } else if (resource.equals("/server")) {
            return handleResourceServer(method);
        } else if (resource.equals("/databases")) {
            return handleResourceDatabases(method);
        } else if (resource.equals("/users")) {
            return handleResourceUsers(method);
        } else if (resource.startsWith("/user/")) {
            return handleResourceUser(method, resource);
        } else if (resource.startsWith("/db/")) {
            return handleResourceDatabase(method, resource);
        } else {
            return response(HttpURLConnection.HTTP_NOT_FOUND, null);
        }
    }

    /**
     * Handles the request
     *
     * @param method The HTTP method
     * @return The response code
     */
    private int handleResourceWhoami(String method) {
        if (!method.equals("GET"))
            return response(HttpURLConnection.HTTP_BAD_METHOD, "Expected GET method");
        return response(controller.getUser(client, client.getName()));
    }

    /**
     * Handles the request
     *
     * @param method The HTTP method
     * @return The response code
     */
    private int handleResourceServer(String method) {
        if (!method.equals("POST"))
            return response(HttpURLConnection.HTTP_BAD_METHOD, "Expected POST method");
        Map<String, List<String>> params = Utils.getRequestParameters(httpExchange.getRequestURI());
        List<String> actions = params.get("action");
        if (actions == null || actions.isEmpty())
            return response(HttpURLConnection.HTTP_BAD_REQUEST, "Expected 'action' parameter");
        if (actions.get(0).equals("shutdown"))
            return response(controller.serverShutdown(client));
        else if (actions.get(0).equals("restart"))
            return response(controller.serverRestart(client));
        return response(HttpURLConnection.HTTP_BAD_REQUEST, "Expected 'action' parameter");
    }

    /**
     * Handles the request
     *
     * @param method The HTTP method
     * @return The response code
     */
    private int handleResourceDatabases(String method) {
        if (!method.equals("GET"))
            return response(HttpURLConnection.HTTP_BAD_METHOD, "Expected GET method");
        return response(controller.getDatabases(client));
    }

    /**
     * Handles the request
     *
     * @param method The HTTP method
     * @return The response code
     */
    private int handleResourceUsers(String method) {
        if (!method.equals("GET"))
            return response(HttpURLConnection.HTTP_BAD_METHOD, "Expected GET method");
        return response(controller.getUsers(client));
    }

    /**
     * Handles the request
     *
     * @param method   The HTTP method
     * @param resource The accessed resource
     * @return The response code
     */
    private int handleResourceUser(String method, String resource) {
        if (resource.endsWith("/privileges"))
            return handleResourceUserPrivileges(method, resource);
        return handleResourceUserNaked(method, resource);
    }

    /**
     * Handles the request
     *
     * @param method   The HTTP method
     * @param resource The accessed resource
     * @return The response code
     */
    private int handleResourceUserPrivileges(String method, String resource) {
        String name = resource.substring("/user/".length(), resource.length() - "/privileges".length());
        if (method.equals("GET"))
            return response(controller.getPrivilegesUser(client, name));
        if (method.equals("POST")) {
            Map<String, List<String>> params = Utils.getRequestParameters(httpExchange.getRequestURI());
            List<String> actions = params.get("action");
            List<String> databases = params.get("db");
            List<String> servers = params.get("server");
            List<String> accesses = params.get("access");
            if (actions == null || actions.isEmpty() || accesses == null || accesses.isEmpty())
                return response(HttpURLConnection.HTTP_BAD_REQUEST, null);
            int privilege = accesses.get(0).equals("ADMIN") ? XOWLPrivilege.ADMIN : (accesses.get(0).equals("WRITE") ? XOWLPrivilege.WRITE : (accesses.get(0).equals("READ") ? XOWLPrivilege.READ : 0));
            if (privilege == 0)
                return response(HttpURLConnection.HTTP_BAD_REQUEST, null);
            if (databases == null || databases.isEmpty()) {
                if (servers == null || servers.isEmpty())
                    return response(HttpURLConnection.HTTP_BAD_REQUEST, null);
                if (privilege != XOWLPrivilege.ADMIN)
                    return response(HttpURLConnection.HTTP_BAD_REQUEST, null);
                if (actions.get(0).equals("grant"))
                    return response(controller.grantServerAdmin(client, name));
                if (actions.get(0).equals("revoke"))
                    return response(controller.revokeServerAdmin(client, name));
            } else {
                if (actions.get(0).equals("grant"))
                    return response(controller.grantDB(client, name, databases.get(0), privilege));
                if (actions.get(0).equals("revoke"))
                    return response(controller.revokeDB(client, name, databases.get(0), privilege));
            }
            return response(HttpURLConnection.HTTP_BAD_REQUEST, null);
        }
        return response(HttpURLConnection.HTTP_BAD_METHOD, null);
    }

    /**
     * Handles the request
     *
     * @param method   The HTTP method
     * @param resource The accessed resource
     * @return The response code
     */
    private int handleResourceUserNaked(String method, String resource) {
        String name = resource.substring("/user/".length());
        if (name.isEmpty())
            return response(HttpURLConnection.HTTP_BAD_REQUEST, "Expected user name");
        if (method.equals("GET"))
            return response(controller.getUser(client, name));
        if (method.equals("DELETE"))
            return response(controller.deleteUser(client, name));
        if (method.equals("PUT")) {
            ServerUser user = controller.getPrincipal(name);
            try {
                String password = Utils.getRequestBody(httpExchange);
                if (user == null)
                    return response(controller.createUser(client, name, password));
                else if (user.getName().equals(client.getName()))
                    return response(controller.changePassword(client, password));
                else
                    return response(controller.resetPassword(client, name, password));
            } catch (IOException exception) {
                logger.error(exception);
                return response(HttpURLConnection.HTTP_INTERNAL_ERROR, exception.getMessage());
            }
        }
        return response(HttpURLConnection.HTTP_BAD_METHOD, null);
    }

    /**
     * Handles the request
     *
     * @param method   The HTTP method
     * @param resource The accessed resource
     * @return The response code
     */
    private int handleResourceDatabase(String method, String resource) {
        if (resource.endsWith("/sparql"))
            return handleResourceDatabaseSPARQL(method, resource);
        if (resource.endsWith("/entailment"))
            return handleResourceDatabaseEntailment(method, resource);
        if (resource.endsWith("/explain"))
            return handleResourceDatabaseExplain(method, resource);
        if (resource.endsWith("/privileges"))
            return handleResourceDatabasePrivileges(method, resource);
        if (resource.endsWith("/rules"))
            return handleResourceDatabaseRules(method, resource);
        return handleResourceDatabaseNaked(method, resource);
    }

    /**
     * Handles the request
     *
     * @param method   The HTTP method
     * @param resource The accessed resource
     * @return The response code
     */
    private int handleResourceDatabaseNaked(String method, String resource) {
        String name = resource.substring("/db/".length());
        if (name.isEmpty())
            return response(HttpURLConnection.HTTP_BAD_REQUEST, "Expected database name");
        if (method.equals("GET"))
            return response(controller.getDatabase(client, name));
        if (method.equals("DELETE"))
            return response(controller.dropDatabase(client, name));
        if (method.equals("POST"))
            return handleResourceDatabasePostData(name);
        if (method.equals("PUT"))
            return response(controller.createDatabase(client, name));
        return response(HttpURLConnection.HTTP_BAD_METHOD, null);
    }

    /**
     * Handles the request
     *
     * @param name The database name
     * @return The response code
     */
    private int handleResourceDatabasePostData(String name) {
        Headers rHeaders = httpExchange.getRequestHeaders();
        String contentType = Utils.getRequestContentType(rHeaders);
        if (contentType == null)
            return response(HttpURLConnection.HTTP_BAD_REQUEST, "Missing content type");
        String body;
        try {
            body = Utils.getRequestBody(httpExchange);
        } catch (IOException exception) {
            controller.getLogger().error(exception);
            return response(HttpURLConnection.HTTP_BAD_REQUEST, "Failed to read the body");
        }
        return response(controller.upload(client, name, contentType, body));
    }

    /**
     * Handles the request
     *
     * @param method   The HTTP method
     * @param resource The accessed resource
     * @return The response code
     */
    private int handleResourceDatabaseSPARQL(String method, String resource) {
        String name = resource.substring("/db/".length(), resource.length() - "/sparql".length());
        Map<String, List<String>> params = Utils.getRequestParameters(httpExchange.getRequestURI());
        List<String> vQuery = params.get("query");
        List<String> defaults = params.get("default-graph-uri");
        List<String> named = params.get("named-graph-uri");
        String query = vQuery == null ? null : vQuery.get(0);
        String body;
        try {
            body = Utils.getRequestBody(httpExchange);
        } catch (IOException exception) {
            controller.getLogger().error(exception);
            return response(HttpURLConnection.HTTP_BAD_REQUEST, "Failed to read the body");
        }
        switch (method) {
            case "GET":
                if (query != null) {
                    if (body != null && !body.isEmpty()) {
                        // should be empty
                        // ill-formed request
                        response(HttpURLConnection.HTTP_BAD_REQUEST, null);
                    } else {
                        return response(controller.sparql(client, name, query, defaults, named));
                    }
                } else {
                    // ill-formed request
                    response(HttpURLConnection.HTTP_BAD_REQUEST, null);
                }
                break;
            case "POST":
                return response(controller.sparql(client, name, body, defaults, named));
        }
        return response(HttpURLConnection.HTTP_BAD_REQUEST, null);
    }

    /**
     * Handles the request
     *
     * @param method   The HTTP method
     * @param resource The accessed resource
     * @return The response code
     */
    private int handleResourceDatabaseEntailment(String method, String resource) {
        String name = resource.substring("/db/".length(), resource.length() - "/entailment".length());
        if (method.equals("GET"))
            return response(controller.getEntailmentRegime(client, name));
        if (method.equals("PUT")) {
            try {
                String body = Utils.getRequestBody(httpExchange);
                return response(controller.setEntailmentRegime(client, name, body));
            } catch (IOException exception) {
                controller.getLogger().error(exception);
                return response(HttpURLConnection.HTTP_BAD_REQUEST, "Failed to read the body");
            }
        } else if (method.equals("DELETE")) {
            return response(controller.setEntailmentRegime(client, name, EntailmentRegime.none.toString()));
        }
        return response(HttpURLConnection.HTTP_BAD_METHOD, null);
    }

    /**
     * Handles the request
     *
     * @param method   The HTTP method
     * @param resource The accessed resource
     * @return The response code
     */
    private int handleResourceDatabaseExplain(String method, String resource) {
        String name = resource.substring("/db/".length(), resource.length() - "/explain".length());
        if (method.equals("GET")) {
            Map<String, List<String>> params = Utils.getRequestParameters(httpExchange.getRequestURI());
            List<String> quads = params.get("quad");
            if (quads == null || quads.isEmpty())
                return response(HttpURLConnection.HTTP_BAD_REQUEST, null);
            return response(controller.getQuadExplanation(client, name, quads.get(0)));
        }
        return response(HttpURLConnection.HTTP_BAD_METHOD, null);
    }

    /**
     * Handles the request
     *
     * @param method   The HTTP method
     * @param resource The accessed resource
     * @return The response code
     */
    private int handleResourceDatabasePrivileges(String method, String resource) {
        String name = resource.substring("/db/".length(), resource.length() - "/privileges".length());
        if (method.equals("GET"))
            return response(controller.getPrivilegesDB(client, name));
        if (method.equals("POST")) {
            Map<String, List<String>> params = Utils.getRequestParameters(httpExchange.getRequestURI());
            List<String> actions = params.get("action");
            List<String> users = params.get("user");
            List<String> accesses = params.get("access");
            if (actions == null || actions.isEmpty() || users == null || users.isEmpty() || accesses == null || accesses.isEmpty())
                return response(HttpURLConnection.HTTP_BAD_REQUEST, null);
            int privilege = accesses.get(0).equals("ADMIN") ? XOWLPrivilege.ADMIN : (accesses.get(0).equals("WRITE") ? XOWLPrivilege.WRITE : (accesses.get(0).equals("READ") ? XOWLPrivilege.READ : 0));
            if (privilege == 0)
                return response(HttpURLConnection.HTTP_BAD_REQUEST, null);
            if (actions.get(0).equals("grant"))
                return response(controller.grantDB(client, users.get(0), name, privilege));
            if (actions.get(0).equals("revoke"))
                return response(controller.revokeDB(client, users.get(0), name, privilege));
            return response(HttpURLConnection.HTTP_BAD_REQUEST, null);
        }
        return response(HttpURLConnection.HTTP_BAD_METHOD, null);
    }

    /**
     * Handles the request
     *
     * @param method   The HTTP method
     * @param resource The accessed resource
     * @return The response code
     */
    private int handleResourceDatabaseRules(String method, String resource) {
        String name = resource.substring("/db/".length(), resource.length() - "/rules".length());
        Map<String, List<String>> params = Utils.getRequestParameters(httpExchange.getRequestURI());
        List<String> ids = params.get("id");
        switch (method) {
            case "GET":
                if (ids == null || ids.isEmpty())
                    return response(controller.getRules(client, name));
                List<String> statuses = params.get("status");
                if (statuses == null || statuses.isEmpty())
                    return response(controller.getRule(client, name, ids.get(0)));
                return response(controller.getRuleStatus(client, name, ids.get(0)));
            case "POST":
                if (ids == null || ids.isEmpty())
                    return response(HttpURLConnection.HTTP_BAD_REQUEST, null);
                List<String> actions = params.get("action");
                if (actions == null || actions.isEmpty())
                    return response(HttpURLConnection.HTTP_BAD_REQUEST, null);
                if (actions.get(0).equals("activate"))
                    return response(controller.activateRule(client, name, ids.get(0)));
                if (actions.get(0).equals("deactivate"))
                    return response(controller.deactivateRule(client, name, ids.get(0)));
                return response(HttpURLConnection.HTTP_BAD_REQUEST, null);
            case "PUT":
                List<String> actives = params.get("active");
                try {
                    String body = Utils.getRequestBody(httpExchange);
                    return response(controller.addRule(client, name, body, actives != null && !actives.isEmpty()));
                } catch (IOException exception) {
                    controller.getLogger().error(exception);
                    return response(HttpURLConnection.HTTP_BAD_REQUEST, "Failed to read the body");
                }
            case "DELETE":
                if (ids == null || ids.isEmpty())
                    return response(HttpURLConnection.HTTP_BAD_REQUEST, null);
                return response(controller.removeRule(client, name, ids.get(0)));
        }
        return response(HttpURLConnection.HTTP_BAD_METHOD, null);
    }

    /**
     * Ends the current exchange with the specified message and response code
     *
     * @param code    The http code
     * @param message The response body message
     * @return The response code
     */
    private int response(int code, String message) {
        byte[] buffer = message != null ? message.getBytes(Files.CHARSET) : new byte[0];
        Utils.enableCORS(httpExchange.getRequestHeaders(), httpExchange.getResponseHeaders());
        try {
            httpExchange.sendResponseHeaders(code, buffer.length);
        } catch (IOException exception) {
            controller.getLogger().error(exception);
        }
        try (OutputStream stream = httpExchange.getResponseBody()) {
            stream.write(buffer);
        } catch (IOException exception) {
            controller.getLogger().error(exception);
            return code;
        }
        return code;
    }

    /**
     * Ends the current exchange with a protocol reply
     *
     * @param reply The protocol reply
     * @return The response code
     */
    private int response(XSPReply reply) {
        List<String> acceptTypes = Utils.getAcceptTypes(httpExchange.getRequestHeaders());
        HttpResponse response = XSPReplyUtils.toHttpResponse(reply, acceptTypes);
        if (response.getContentType() != null)
            httpExchange.getResponseHeaders().add(HEADER_CONTENT_TYPE, response.getContentType());
        return response(response.getCode(), response.getBodyAsString());
    }
}
