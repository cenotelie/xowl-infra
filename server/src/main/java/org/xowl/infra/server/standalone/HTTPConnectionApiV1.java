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

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import org.xowl.hime.redist.ASTNode;
import org.xowl.infra.server.api.ApiV1;
import org.xowl.infra.server.api.XOWLPrivilege;
import org.xowl.infra.server.base.BaseStoredProcedure;
import org.xowl.infra.server.impl.ControllerServer;
import org.xowl.infra.server.impl.UserImpl;
import org.xowl.infra.server.xsp.XSPReply;
import org.xowl.infra.server.xsp.XSPReplyResult;
import org.xowl.infra.server.xsp.XSPReplyUtils;
import org.xowl.infra.store.EntailmentRegime;
import org.xowl.infra.store.loaders.JSONLDLoader;
import org.xowl.infra.utils.Files;
import org.xowl.infra.utils.concurrent.SafeRunnable;
import org.xowl.infra.utils.http.HttpConstants;
import org.xowl.infra.utils.http.HttpResponse;
import org.xowl.infra.utils.http.URIUtils;
import org.xowl.infra.utils.logging.Logging;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.*;

/**
 * Represents an active connection to the HTTP server
 *
 * @author Laurent Wouters
 */
class HTTPConnectionApiV1 extends SafeRunnable {
    /**
     * The empty message
     */
    private static final byte[] EMPTY_MESSAGE = new byte[0];

    /**
     * The current controller
     */
    private final ControllerServer controller;
    /**
     * The HTTP exchange to treat
     */
    private final HttpExchange httpExchange;
    /**
     * The client
     */
    private UserImpl client;

    /**
     * Initializes this connection
     *
     * @param controller The current controller
     * @param exchange   The HTTP exchange to treat
     */
    public HTTPConnectionApiV1(ControllerServer controller, HttpExchange exchange) {
        super(Logging.getDefault());
        this.controller = controller;
        this.httpExchange = exchange;
    }

    @Override
    public void doRun() {
        // add caching headers
        httpExchange.getResponseHeaders().put(HttpConstants.HEADER_CACHE_CONTROL, Arrays.asList("private", "no-cache", "no-store", "no-transform", "must-revalidate"));
        httpExchange.getResponseHeaders().put(HttpConstants.HEADER_STRICT_TRANSPORT_SECURITY, Collections.singletonList("max-age=31536000"));
        String method = httpExchange.getRequestMethod();
        if (Objects.equals(method, HttpConstants.METHOD_OPTIONS)) {
            // assume a pre-flight CORS request
            response(HttpURLConnection.HTTP_OK);
            return;
        }

        String resource = httpExchange.getRequestURI().getPath().substring("/api".length());
        if (resource.equals("/me/login")) {
            handleRequestLogin(method);
            return;
        }

        List<String> cookies = httpExchange.getRequestHeaders().get(HttpConstants.HEADER_COOKIE);
        if (cookies != null) {
            for (String cookie : cookies) {
                if (cookie.startsWith(ApiV1.AUTH_TOKEN + "=")) {
                    String token = cookie.substring(ApiV1.AUTH_TOKEN.length() + 1);
                    XSPReply reply = controller.authenticate(httpExchange.getRemoteAddress().getAddress(), token);
                    if (reply != null && reply.isSuccess())
                        client = ((XSPReplyResult<UserImpl>) reply).getData();
                    break;
                }
            }
        }
        if (client == null) {
            response(HttpURLConnection.HTTP_UNAUTHORIZED);
            return;
        }
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
     * @param method The HTTP method
     * @return The response code
     */
    private int handleRequestLogin(String method) {
        if (!method.equals(HttpConstants.METHOD_POST))
            return response(HttpURLConnection.HTTP_BAD_METHOD);
        Map<String, List<String>> params = Utils.getRequestParameters(httpExchange.getRequestURI());
        List<String> logins = params.get("login");
        List<String> passwords = params.get("password");
        if (logins == null || passwords == null || logins.isEmpty() || passwords.isEmpty())
            return response(HttpURLConnection.HTTP_BAD_REQUEST);
        XSPReply reply = controller.login(httpExchange.getRemoteAddress().getAddress(), logins.get(0), passwords.get(0));
        if (reply == null)
            return response(HttpURLConnection.HTTP_UNAUTHORIZED);
        if (!reply.isSuccess())
            return response(HttpURLConnection.HTTP_UNAUTHORIZED);
        String token = ((XSPReplyResult<String>) reply).getData();
        httpExchange.getResponseHeaders().put(HttpConstants.HEADER_SET_COOKIE, Collections.singletonList(
                ApiV1.AUTH_TOKEN + "=" + token +
                        "; Max-Age=" + Long.toString(controller.getSecurityTokenTTL()) +
                        "; Path=/api" +
                        "; Secure" +
                        "; HttpOnly"
        ));
        return response(HttpURLConnection.HTTP_OK);
    }

    /**
     * Handles the request
     *
     * @param method The HTTP method
     * @return The response code
     */
    private int handleRequestLogout(String method) {
        if (!method.equals(HttpConstants.METHOD_POST))
            return response(HttpURLConnection.HTTP_BAD_METHOD);
        XSPReply reply = controller.logout(client);
        if (!reply.isSuccess())
            return response(reply);
        httpExchange.getResponseHeaders().put(HttpConstants.HEADER_SET_COOKIE, Collections.singletonList(
                ApiV1.AUTH_TOKEN + "= " +
                        "; Max-Age=0" +
                        "; Path=/api" +
                        "; Secure" +
                        "; HttpOnly"
        ));
        return response(HttpURLConnection.HTTP_OK);
    }

    /**
     * Handles the request
     *
     * @param method   The HTTP method
     * @param resource The accessed resource
     * @return The response code
     */
    private int handleRequest(String method, String resource) {
        if (resource.startsWith("/me")) {
            return handleResourceMe(method, resource);
        } else if (resource.startsWith("/server")) {
            return handleResourceServer(method, resource);
        } else if (resource.startsWith("/databases")) {
            return handleResourceDatabases(method, resource);
        } else if (resource.startsWith("/users")) {
            return handleResourceUsers(method, resource);
        } else {
            return response(HttpURLConnection.HTTP_NOT_FOUND);
        }
    }

    /**
     * Handles the request
     *
     * @param method   The HTTP method
     * @param resource The accessed resource
     * @return The response code
     */
    private int handleResourceMe(String method, String resource) {
        if (resource.equals("/me")) {
            if (!method.equals(HttpConstants.METHOD_GET))
                return response(HttpURLConnection.HTTP_BAD_METHOD);
            return response(controller.getUser(client, client.getName()));
        } else if (resource.equals("/me/logout")) {
            return handleRequestLogout(method);
        }
        return response(HttpURLConnection.HTTP_NOT_FOUND, null);
    }

    /**
     * Handles the request
     *
     * @param method   The HTTP method
     * @param resource The accessed resource
     * @return The response code
     */
    private int handleResourceServer(String method, String resource) {
        switch (resource) {
            case "/server/shutdown":
                if (!method.equals(HttpConstants.METHOD_POST))
                    return response(HttpURLConnection.HTTP_BAD_METHOD);
                return response(controller.serverShutdown(client));
            case "/server/restart":
                if (!method.equals(HttpConstants.METHOD_POST))
                    return response(HttpURLConnection.HTTP_BAD_METHOD);
                return response(controller.serverRestart(client));
            case "/server/grantAdmin": {
                if (!method.equals(HttpConstants.METHOD_POST))
                    return response(HttpURLConnection.HTTP_BAD_METHOD);
                Map<String, List<String>> params = Utils.getRequestParameters(httpExchange.getRequestURI());
                List<String> users = params.get("user");
                if (users == null || users.isEmpty())
                    return response(HttpURLConnection.HTTP_BAD_REQUEST);
                return response(controller.serverGrantAdmin(client, users.get(0)));
            }
            case "/server/revokeAdmin": {
                if (!method.equals(HttpConstants.METHOD_POST))
                    return response(HttpURLConnection.HTTP_BAD_METHOD);
                Map<String, List<String>> params = Utils.getRequestParameters(httpExchange.getRequestURI());
                List<String> users = params.get("user");
                if (users == null || users.isEmpty())
                    return response(HttpURLConnection.HTTP_BAD_REQUEST);
                return response(controller.serverRevokeAdmin(client, users.get(0)));
            }
        }
        return response(HttpURLConnection.HTTP_NOT_FOUND, null);
    }

    /**
     * Handles the request
     *
     * @param method   The HTTP method
     * @param resource The accessed resource
     * @return The response code
     */
    private int handleResourceDatabases(String method, String resource) {
        if (resource.equals("/databases")) {
            if (!method.equals(HttpConstants.METHOD_GET))
                return response(HttpURLConnection.HTTP_BAD_METHOD);
            return response(controller.getDatabases(client));
        } else if (resource.startsWith("/databases/")) {
            String rest = resource.substring("/databases/".length());
            int index = rest.indexOf("/");
            if (index < 0)
                return handleResourceDatabaseNaked(URIUtils.decodeComponent(rest), method);
            String name = URIUtils.decodeComponent(rest.substring(0, index));
            if (name.isEmpty())
                return response(HttpURLConnection.HTTP_NOT_FOUND);
            rest = rest.substring(index);
            if (rest.startsWith("/privileges"))
                return handleResourceDatabasePrivileges(name, method, rest);
            if (rest.startsWith("/rules"))
                return handleResourceDatabaseRules(name, method, rest);
            if (rest.startsWith("/procedures"))
                return handleResourceDatabaseProcedures(name, method, rest);
            switch (rest) {
                case "/metric":
                    return handleResourceDatabaseMetric(name, method);
                case "/statistics":
                    return handleResourceDatabaseMetricSnapshot(name, method);
                case "/sparql":
                    return handleResourceDatabaseSPARQL(name, method);
                case "/entailment":
                    return handleResourceDatabaseEntailment(name, method);
            }
        }
        return response(HttpURLConnection.HTTP_NOT_FOUND);
    }

    /**
     * Handles the request
     *
     * @param name   The database's name
     * @param method The HTTP method
     * @return The response code
     */
    private int handleResourceDatabaseNaked(String name, String method) {
        switch (method) {
            case HttpConstants.METHOD_GET:
                return response(controller.getDatabase(client, name));
            case HttpConstants.METHOD_PUT:
                return response(controller.createDatabase(client, name));
            case HttpConstants.METHOD_DELETE:
                return response(controller.dropDatabase(client, name));
            case HttpConstants.METHOD_POST:
                return handleResourceDatabasePostData(name);
        }
        return response(HttpURLConnection.HTTP_BAD_METHOD);
    }

    /**
     * Handles the request
     *
     * @param name The database's name
     * @return The response code
     */
    private int handleResourceDatabasePostData(String name) {
        Headers rHeaders = httpExchange.getRequestHeaders();
        String contentType = Utils.getRequestContentType(rHeaders);
        if (contentType == null)
            return response(HttpURLConnection.HTTP_BAD_REQUEST);
        String body;
        try {
            body = Utils.getRequestBody(httpExchange);
        } catch (IOException exception) {
            Logging.getDefault().error(exception);
            return response(HttpURLConnection.HTTP_BAD_REQUEST);
        }
        return response(controller.upload(client, name, contentType, body));
    }

    /**
     * Handles the request
     *
     * @param name   The database's name
     * @param method The HTTP method
     * @return The response code
     */
    private int handleResourceDatabaseMetric(String name, String method) {
        if (!method.equals(HttpConstants.METHOD_GET))
            return response(HttpURLConnection.HTTP_BAD_METHOD);
        return response(controller.getDatabaseMetric(client, name));
    }

    /**
     * Handles the request
     *
     * @param name   The database's name
     * @param method The HTTP method
     * @return The response code
     */
    private int handleResourceDatabaseMetricSnapshot(String name, String method) {
        if (!method.equals(HttpConstants.METHOD_GET))
            return response(HttpURLConnection.HTTP_BAD_METHOD);
        return response(controller.getDatabaseMetricSnapshot(client, name));
    }

    /**
     * Handles the request
     *
     * @param name   The database's name
     * @param method The HTTP method
     * @return The response code
     */
    private int handleResourceDatabaseSPARQL(String name, String method) {
        Map<String, List<String>> params = Utils.getRequestParameters(httpExchange.getRequestURI());
        List<String> vQuery = params.get("query");
        List<String> defaults = params.get("default-graph-uri");
        List<String> named = params.get("named-graph-uri");
        String query = vQuery == null ? null : vQuery.get(0);
        String body;
        try {
            body = Utils.getRequestBody(httpExchange);
        } catch (IOException exception) {
            Logging.getDefault().error(exception);
            return response(HttpURLConnection.HTTP_BAD_REQUEST);
        }
        switch (method) {
            case HttpConstants.METHOD_GET:
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
            case HttpConstants.METHOD_POST:
                return response(controller.sparql(client, name, body, defaults, named));
        }
        return response(HttpURLConnection.HTTP_BAD_REQUEST, null);
    }

    /**
     * Handles the request
     *
     * @param name   The database's name
     * @param method The HTTP method
     * @return The response code
     */
    private int handleResourceDatabaseEntailment(String name, String method) {
        switch (method) {
            case HttpConstants.METHOD_GET:
                return response(controller.getEntailmentRegime(client, name));
            case HttpConstants.METHOD_PUT:
                try {
                    String body = Utils.getRequestBody(httpExchange);
                    return response(controller.setEntailmentRegime(client, name, body));
                } catch (IOException exception) {
                    Logging.getDefault().error(exception);
                    return response(HttpURLConnection.HTTP_BAD_REQUEST);
                }
            case HttpConstants.METHOD_DELETE:
                return response(controller.setEntailmentRegime(client, name, EntailmentRegime.none.toString()));
        }
        return response(HttpURLConnection.HTTP_BAD_METHOD);
    }

    /**
     * Handles the request
     *
     * @param name     The database's name
     * @param method   The HTTP method
     * @param resource The accessed resource
     * @return The response code
     */
    private int handleResourceDatabasePrivileges(String name, String method, String resource) {
        if (resource.equals("/privileges")) {
            if (!method.equals(HttpConstants.METHOD_GET))
                return response(HttpURLConnection.HTTP_BAD_METHOD);
            return response(controller.getDatabasePrivileges(client, name));
        }
        if (resource.equals("/privileges/grant")) {
            if (!method.equals(HttpConstants.METHOD_POST))
                return response(HttpURLConnection.HTTP_BAD_METHOD);
            Map<String, List<String>> params = Utils.getRequestParameters(httpExchange.getRequestURI());
            List<String> users = params.get("user");
            List<String> accesses = params.get("access");
            if (users == null || users.isEmpty() || accesses == null || accesses.isEmpty())
                return response(HttpURLConnection.HTTP_BAD_REQUEST);
            int privilege = accesses.get(0).equals("ADMIN") ? XOWLPrivilege.ADMIN : (accesses.get(0).equals("WRITE") ? XOWLPrivilege.WRITE : (accesses.get(0).equals("READ") ? XOWLPrivilege.READ : 0));
            if (privilege == 0)
                return response(HttpURLConnection.HTTP_BAD_REQUEST);
            return response(controller.grantDatabase(client, users.get(0), name, privilege));
        }
        if (resource.equals("/privileges/revoke")) {
            if (!method.equals(HttpConstants.METHOD_POST))
                return response(HttpURLConnection.HTTP_BAD_METHOD);
            Map<String, List<String>> params = Utils.getRequestParameters(httpExchange.getRequestURI());
            List<String> users = params.get("user");
            List<String> accesses = params.get("access");
            if (users == null || users.isEmpty() || accesses == null || accesses.isEmpty())
                return response(HttpURLConnection.HTTP_BAD_REQUEST);
            int privilege = accesses.get(0).equals("ADMIN") ? XOWLPrivilege.ADMIN : (accesses.get(0).equals("WRITE") ? XOWLPrivilege.WRITE : (accesses.get(0).equals("READ") ? XOWLPrivilege.READ : 0));
            if (privilege == 0)
                return response(HttpURLConnection.HTTP_BAD_REQUEST);
            return response(controller.revokeDatabase(client, users.get(0), name, privilege));
        }
        return response(HttpURLConnection.HTTP_NOT_FOUND);
    }

    /**
     * Handles the request
     *
     * @param name     The database's name
     * @param method   The HTTP method
     * @param resource The accessed resource
     * @return The response code
     */
    private int handleResourceDatabaseRules(String name, String method, String resource) {
        if (resource.equals("/rules")) {
            switch (method) {
                case HttpConstants.METHOD_GET:
                    return response(controller.getRules(client, name));
                case HttpConstants.METHOD_PUT: {
                    Map<String, List<String>> params = Utils.getRequestParameters(httpExchange.getRequestURI());
                    List<String> actives = params.get("active");
                    try {
                        String body = Utils.getRequestBody(httpExchange);
                        return response(controller.addRule(client, name, body, actives != null && !actives.isEmpty() && actives.get(0).equals("true")));
                    } catch (IOException exception) {
                        Logging.getDefault().error(exception);
                        return response(HttpURLConnection.HTTP_BAD_REQUEST, "Failed to read the body");
                    }
                }
            }
            return response(HttpURLConnection.HTTP_BAD_METHOD);
        }

        resource = resource.substring("/rules/".length());
        int index = resource.indexOf("/");
        String ruleId = resource.substring(0, index != -1 ? index : resource.length());
        ruleId = URIUtils.decodeComponent(ruleId);

        if (index != -1) {
            resource = resource.substring(index);
            if (resource.equals("/status")) {
                if (!method.equals(HttpConstants.METHOD_GET))
                    return response(HttpURLConnection.HTTP_BAD_METHOD);
                return response(controller.getRuleStatus(client, name, ruleId));
            }
            if (!method.equals(HttpConstants.METHOD_POST))
                return response(HttpURLConnection.HTTP_BAD_METHOD);
            if (resource.equals("/activate"))
                return response(controller.activateRule(client, name, ruleId));
            if (resource.equals("/deactivate"))
                return response(controller.deactivateRule(client, name, ruleId));
            return response(HttpURLConnection.HTTP_NOT_FOUND);
        } else {
            // this is the naked rule
            switch (method) {
                case HttpConstants.METHOD_GET:
                    return response(controller.getRule(client, name, ruleId));
                case HttpConstants.METHOD_DELETE:
                    return response(controller.removeRule(client, name, ruleId));
            }
            return response(HttpURLConnection.HTTP_BAD_METHOD);
        }
    }

    /**
     * Handles the request
     *
     * @param name     The database's name
     * @param method   The HTTP method
     * @param resource The accessed resource
     * @return The response code
     */
    private int handleResourceDatabaseProcedures(String name, String method, String resource) {
        if (resource.equals("/procedures")) {
            if (!method.equals(HttpConstants.METHOD_GET))
                return response(HttpURLConnection.HTTP_BAD_METHOD);
            return response(controller.getStoredProcedures(client, name));
        }

        resource = resource.substring("/procedures/".length());
        String procedureId = resource.substring(0, resource.length());
        procedureId = URIUtils.decodeComponent(procedureId);

        switch (method) {
            case HttpConstants.METHOD_GET:
                return response(controller.getStoreProcedure(client, name, procedureId));
            case HttpConstants.METHOD_DELETE:
                return response(controller.removeStoredProcedure(client, name, procedureId));
            case HttpConstants.METHOD_PUT: {
                try {
                    String body = Utils.getRequestBody(httpExchange);
                    ASTNode root = JSONLDLoader.parseJSON(logger, body);
                    if (root == null)
                        return response(HttpURLConnection.HTTP_BAD_REQUEST, "Failed to read the body");
                    BaseStoredProcedure procedure = new BaseStoredProcedure(root, null, logger);
                    return response(controller.addStoredProcedure(client, name, procedure.getName(), procedure.getDefinition(), procedure.getParameters()));
                } catch (IOException exception) {
                    Logging.getDefault().error(exception);
                    return response(HttpURLConnection.HTTP_BAD_REQUEST, "Failed to read the body");
                }
            }
            case HttpConstants.METHOD_POST: {
                try {
                    String body = Utils.getRequestBody(httpExchange);
                    return response(controller.executeStoredProcedure(client, name, procedureId, body));
                } catch (IOException exception) {
                    Logging.getDefault().error(exception);
                    return response(HttpURLConnection.HTTP_BAD_REQUEST, "Failed to read the body");
                }
            }
        }
        return response(HttpURLConnection.HTTP_BAD_METHOD);
    }


    /**
     * Handles the request
     *
     * @param method   The HTTP method
     * @param resource The accessed resource
     * @return The response code
     */
    private int handleResourceUsers(String method, String resource) {
        if (resource.equals("/users")) {
            if (!method.equals(HttpConstants.METHOD_GET))
                return response(HttpURLConnection.HTTP_BAD_METHOD);
            return response(controller.getUsers(client));
        } else if (resource.startsWith("/users/")) {
            String rest = resource.substring("/users/".length());
            int index = rest.indexOf("/");
            String name = URIUtils.decodeComponent(rest.substring(0, index < 0 ? rest.length() : index));
            if (name.isEmpty())
                return response(HttpURLConnection.HTTP_NOT_FOUND);
            if (index < 0)
                return handleResourceUserNaked(name, method);
            rest = rest.substring(index);
            if (rest.equals("/password"))
                return handleResourceUserPassword(name, method, rest);
            if (rest.equals("/privileges"))
                return handleResourceUserPrivileges(name, method, rest);
        }
        return response(HttpURLConnection.HTTP_NOT_FOUND);
    }

    /**
     * Handles the request
     *
     * @param name   The database's name
     * @param method The HTTP method
     * @return The response code
     */
    private int handleResourceUserNaked(String name, String method) {
        switch (method) {
            case HttpConstants.METHOD_GET:
                return response(controller.getUser(client, name));
            case HttpConstants.METHOD_DELETE:
                return response(controller.deleteUser(client, name));
            case HttpConstants.METHOD_PUT: {
                try {
                    String password = Utils.getRequestBody(httpExchange);
                    return response(controller.createUser(client, name, password));
                } catch (IOException exception) {
                    logger.error(exception);
                    return response(HttpURLConnection.HTTP_INTERNAL_ERROR, exception.getMessage());
                }
            }
        }
        return response(HttpURLConnection.HTTP_BAD_METHOD);
    }

    /**
     * Handles the request
     *
     * @param name     The database's name
     * @param method   The HTTP method
     * @param resource The accessed resource
     * @return The response code
     */
    private int handleResourceUserPassword(String name, String method, String resource) {
        if (resource.equals("/password/change")) {
            if (!method.equals(HttpConstants.METHOD_POST))
                return response(HttpURLConnection.HTTP_BAD_METHOD);
            Map<String, List<String>> params = Utils.getRequestParameters(httpExchange.getRequestURI());
            List<String> passwords = params.get("password");
            if (passwords == null || passwords.isEmpty())
                return response(HttpURLConnection.HTTP_BAD_REQUEST);
            return response(controller.updatePassword(client, name, passwords.get(0)));
        }
        if (resource.equals("/password/reset")) {
            if (!method.equals(HttpConstants.METHOD_POST))
                return response(HttpURLConnection.HTTP_BAD_METHOD);
            Map<String, List<String>> params = Utils.getRequestParameters(httpExchange.getRequestURI());
            List<String> passwords = params.get("password");
            if (passwords == null || passwords.isEmpty())
                return response(HttpURLConnection.HTTP_BAD_REQUEST);
            return response(controller.updatePassword(client, name, passwords.get(0)));
        }
        return response(HttpURLConnection.HTTP_NOT_FOUND);
    }

    /**
     * Handles the request
     *
     * @param name     The database's name
     * @param method   The HTTP method
     * @param resource The accessed resource
     * @return The response code
     */
    private int handleResourceUserPrivileges(String name, String method, String resource) {
        if (resource.equals("/privileges")) {
            if (!method.equals(HttpConstants.METHOD_GET))
                return response(HttpURLConnection.HTTP_BAD_METHOD);
            return response(controller.getUserPrivileges(client, name));
        }
        if (resource.equals("/privileges/grant")) {
            if (!method.equals(HttpConstants.METHOD_POST))
                return response(HttpURLConnection.HTTP_BAD_METHOD);
            Map<String, List<String>> params = Utils.getRequestParameters(httpExchange.getRequestURI());
            List<String> databases = params.get("db");
            List<String> accesses = params.get("access");
            if (databases == null || databases.isEmpty() || accesses == null || accesses.isEmpty())
                return response(HttpURLConnection.HTTP_BAD_REQUEST);
            int privilege = accesses.get(0).equals("ADMIN") ? XOWLPrivilege.ADMIN : (accesses.get(0).equals("WRITE") ? XOWLPrivilege.WRITE : (accesses.get(0).equals("READ") ? XOWLPrivilege.READ : 0));
            if (privilege == 0)
                return response(HttpURLConnection.HTTP_BAD_REQUEST);
            return response(controller.grantDatabase(client, name, databases.get(0), privilege));
        }
        if (resource.equals("/privileges/revoke")) {
            if (!method.equals(HttpConstants.METHOD_POST))
                return response(HttpURLConnection.HTTP_BAD_METHOD);
            Map<String, List<String>> params = Utils.getRequestParameters(httpExchange.getRequestURI());
            List<String> databases = params.get("db");
            List<String> accesses = params.get("access");
            if (databases == null || databases.isEmpty() || accesses == null || accesses.isEmpty())
                return response(HttpURLConnection.HTTP_BAD_REQUEST);
            int privilege = accesses.get(0).equals("ADMIN") ? XOWLPrivilege.ADMIN : (accesses.get(0).equals("WRITE") ? XOWLPrivilege.WRITE : (accesses.get(0).equals("READ") ? XOWLPrivilege.READ : 0));
            if (privilege == 0)
                return response(HttpURLConnection.HTTP_BAD_REQUEST);
            return response(controller.revokeDatabase(client, name, databases.get(0), privilege));
        }
        return response(HttpURLConnection.HTTP_NOT_FOUND);
    }

    /**
     * Ends the current exchange with a response code
     *
     * @param code The http code
     * @return The response code
     */
    private int response(int code) {
        Utils.enableCORS(httpExchange.getRequestHeaders(), httpExchange.getResponseHeaders());
        try {
            httpExchange.sendResponseHeaders(code, 0);
        } catch (IOException exception) {
            Logging.getDefault().error(exception);
        }
        try (OutputStream stream = httpExchange.getResponseBody()) {
            stream.write(EMPTY_MESSAGE);
        } catch (IOException exception) {
            Logging.getDefault().error(exception);
            return code;
        }
        return code;
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
            httpExchange.getResponseHeaders().add(HttpConstants.HEADER_CONTENT_TYPE, HttpConstants.MIME_TEXT_PLAIN);
            httpExchange.sendResponseHeaders(code, buffer.length);
        } catch (IOException exception) {
            Logging.getDefault().error(exception);
        }
        try (OutputStream stream = httpExchange.getResponseBody()) {
            stream.write(buffer);
        } catch (IOException exception) {
            Logging.getDefault().error(exception);
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
            httpExchange.getResponseHeaders().add(HttpConstants.HEADER_CONTENT_TYPE, response.getContentType());
        return response(response.getCode(), response.getBodyAsString());
    }
}
