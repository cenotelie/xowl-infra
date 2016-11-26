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

package org.xowl.infra.server.remote;

import org.xowl.hime.redist.ASTNode;
import org.xowl.infra.server.api.*;
import org.xowl.infra.server.base.*;
import org.xowl.infra.server.xsp.*;
import org.xowl.infra.store.EntailmentRegime;
import org.xowl.infra.store.Repository;
import org.xowl.infra.store.rdf.Quad;
import org.xowl.infra.store.sparql.Command;
import org.xowl.infra.store.sparql.Result;
import org.xowl.infra.store.writers.NQuadsSerializer;
import org.xowl.infra.utils.Files;
import org.xowl.infra.utils.http.HttpConnection;
import org.xowl.infra.utils.http.HttpConstants;
import org.xowl.infra.utils.http.HttpResponse;
import org.xowl.infra.utils.http.URIUtils;
import org.xowl.infra.utils.logging.BufferedLogger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Collection;
import java.util.List;
import java.util.zip.GZIPOutputStream;

/**
 * Implements the server API for a remote xOWL Server
 * This object maintains the connection to the remote server along with the required parameters.
 * This implementation relies on the version v1 of the API
 *
 * @author Laurent Wouters
 */
public class RemoteServer implements XOWLServer, XOWLFactory {
    /**
     * The connection to the remote host, if any
     */
    private final HttpConnection connection;
    /**
     * The currently logged-in user
     */
    private XOWLUser currentUser;
    /**
     * The login for the current user
     */
    private String currentLogin;
    /**
     * The password for the current user
     */
    private String currentPassword;

    /**
     * Creates a connection to a remote xOWL server
     *
     * @param endpoint The naked endpoint for the xOWL server, e.g.: https://localhost:3443
     */
    public RemoteServer(String endpoint) {
        String result = endpoint;
        if (result == null || result.isEmpty()) {
            result = "https://localhost:3443" + ApiV1.URI_PREFIX;
        } else if (result.endsWith("/")) {
            result = result.substring(0, result.length() - 1) + ApiV1.URI_PREFIX;
        }
        this.connection = new HttpConnection(result);
        this.currentUser = null;
        this.currentLogin = null;
        this.currentPassword = null;
    }

    @Override
    public boolean isLoggedIn() {
        return (currentUser != null);
    }

    @Override
    public XOWLUser getLoggedInUser() {
        return currentUser;
    }

    @Override
    public XSPReply login(String login, String password) {
        HttpResponse response = connection.request("/me/login" +
                        "?login=" + URIUtils.encodeComponent(login),
                HttpConstants.METHOD_POST,
                password,
                HttpConstants.MIME_TEXT_PLAIN,
                HttpConstants.MIME_TEXT_PLAIN
        );
        XSPReply reply = XSPReplyUtils.fromHttpResponse(response, this);
        if (reply.isSuccess()) {
            currentUser = ((XSPReplyResult<XOWLUser>) reply).getData();
            currentLogin = login;
            currentPassword = password;
        } else {
            currentUser = null;
            currentLogin = null;
            currentPassword = null;
        }
        return reply;
    }

    @Override
    public XSPReply logout() {
        if (currentUser == null)
            return XSPReplyNetworkError.instance();
        HttpResponse response = connection.request("/me/logout",
                HttpConstants.METHOD_POST,
                HttpConstants.MIME_TEXT_PLAIN
        );
        XSPReply reply = XSPReplyUtils.fromHttpResponse(response, this);
        currentUser = null;
        currentLogin = null;
        currentPassword = null;
        return reply;
    }

    @Override
    public XSPReply serverShutdown() {
        // not logged in
        if (currentUser == null)
            return XSPReplyNetworkError.instance();
        // supposed to be logged-in
        XSPReply reply = XSPReplyUtils.fromHttpResponse(connection.request(
                "/server/shutdown",
                HttpConstants.METHOD_POST,
                HttpConstants.MIME_TEXT_PLAIN), this);
        if (reply != XSPReplyUnauthenticated.instance())
            // not an authentication problem => return this reply
            return reply;
        // try to re-login
        reply = login(currentLogin, currentPassword);
        if (!reply.isSuccess())
            // failed => unauthenticated
            return XSPReplyUnauthenticated.instance();
        // now that we are logged-in, retry
        return XSPReplyUtils.fromHttpResponse(connection.request(
                "/server/shutdown",
                HttpConstants.METHOD_POST,
                HttpConstants.MIME_TEXT_PLAIN), this);
    }

    @Override
    public XSPReply serverRestart() {
        // not logged in
        if (currentUser == null)
            return XSPReplyNetworkError.instance();
        // supposed to be logged-in
        XSPReply reply = XSPReplyUtils.fromHttpResponse(connection.request(
                "/server/restart",
                HttpConstants.METHOD_POST,
                HttpConstants.MIME_TEXT_PLAIN), this);
        if (reply != XSPReplyUnauthenticated.instance())
            // not an authentication problem => return this reply
            return reply;
        // try to re-login
        reply = login(currentLogin, currentPassword);
        if (!reply.isSuccess())
            // failed => unauthenticated
            return XSPReplyUnauthenticated.instance();
        // now that we are logged-in, retry
        return XSPReplyUtils.fromHttpResponse(connection.request(
                "/server/restart",
                HttpConstants.METHOD_POST,
                HttpConstants.MIME_TEXT_PLAIN), this);
    }

    @Override
    public XSPReply serverGrantAdmin(XOWLUser target) {
        return serverGrantAdmin(target.getName());
    }

    @Override
    public XSPReply serverGrantAdmin(String target) {
        // not logged in
        if (currentUser == null)
            return XSPReplyNetworkError.instance();
        // supposed to be logged-in
        XSPReply reply = XSPReplyUtils.fromHttpResponse(connection.request(
                "/server/grantAdmin?user=" + URIUtils.encodeComponent(target),
                HttpConstants.METHOD_POST,
                HttpConstants.MIME_JSON), this);
        if (reply != XSPReplyUnauthenticated.instance())
            // not an authentication problem => return this reply
            return reply;
        // try to re-login
        reply = login(currentLogin, currentPassword);
        if (!reply.isSuccess())
            // failed => unauthenticated
            return XSPReplyUnauthenticated.instance();
        // now that we are logged-in, retry
        return XSPReplyUtils.fromHttpResponse(connection.request(
                "/server/grantAdmin?user=" + URIUtils.encodeComponent(target),
                HttpConstants.METHOD_POST,
                HttpConstants.MIME_JSON), this);
    }

    @Override
    public XSPReply serverRevokeAdmin(XOWLUser target) {
        return serverRevokeAdmin(target.getName());
    }

    @Override
    public XSPReply serverRevokeAdmin(String target) {
        // not logged in
        if (currentUser == null)
            return XSPReplyNetworkError.instance();
        // supposed to be logged-in
        XSPReply reply = XSPReplyUtils.fromHttpResponse(connection.request(
                "/server/revokeAdmin?user=" + URIUtils.encodeComponent(target),
                HttpConstants.METHOD_POST,
                HttpConstants.MIME_JSON), this);
        if (reply != XSPReplyUnauthenticated.instance())
            // not an authentication problem => return this reply
            return reply;
        // try to re-login
        reply = login(currentLogin, currentPassword);
        if (!reply.isSuccess())
            // failed => unauthenticated
            return XSPReplyUnauthenticated.instance();
        // now that we are logged-in, retry
        return XSPReplyUtils.fromHttpResponse(connection.request(
                "/server/revokeAdmin?user=" + URIUtils.encodeComponent(target),
                HttpConstants.METHOD_POST,
                HttpConstants.MIME_JSON), this);
    }

    @Override
    public XSPReply getDatabases() {
        // not logged in
        if (currentUser == null)
            return XSPReplyNetworkError.instance();
        // supposed to be logged-in
        XSPReply reply = XSPReplyUtils.fromHttpResponse(connection.request(
                "/database",
                HttpConstants.METHOD_GET,
                HttpConstants.MIME_JSON), this);
        if (reply != XSPReplyUnauthenticated.instance())
            // not an authentication problem => return this reply
            return reply;
        // try to re-login
        reply = login(currentLogin, currentPassword);
        if (!reply.isSuccess())
            // failed => unauthenticated
            return XSPReplyUnauthenticated.instance();
        // now that we are logged-in, retry
        return XSPReplyUtils.fromHttpResponse(connection.request(
                "/database",
                HttpConstants.METHOD_GET,
                HttpConstants.MIME_JSON), this);
    }

    @Override
    public XSPReply getDatabase(String name) {
        // not logged in
        if (currentUser == null)
            return XSPReplyNetworkError.instance();
        // supposed to be logged-in
        XSPReply reply = XSPReplyUtils.fromHttpResponse(connection.request(
                "/database/" + URIUtils.encodeComponent(name),
                HttpConstants.METHOD_GET,
                HttpConstants.MIME_JSON), this);
        if (reply != XSPReplyUnauthenticated.instance())
            // not an authentication problem => return this reply
            return reply;
        // try to re-login
        reply = login(currentLogin, currentPassword);
        if (!reply.isSuccess())
            // failed => unauthenticated
            return XSPReplyUnauthenticated.instance();
        // now that we are logged-in, retry
        return XSPReplyUtils.fromHttpResponse(connection.request(
                "/database/" + URIUtils.encodeComponent(name),
                HttpConstants.METHOD_GET,
                HttpConstants.MIME_JSON), this);
    }


    @Override
    public XSPReply createDatabase(String name) {
        // not logged in
        if (currentUser == null)
            return XSPReplyNetworkError.instance();
        // supposed to be logged-in
        XSPReply reply = XSPReplyUtils.fromHttpResponse(connection.request(
                "/database/" + URIUtils.encodeComponent(name),
                HttpConstants.METHOD_PUT,
                HttpConstants.MIME_JSON), this);
        if (reply != XSPReplyUnauthenticated.instance())
            // not an authentication problem => return this reply
            return reply;
        // try to re-login
        reply = login(currentLogin, currentPassword);
        if (!reply.isSuccess())
            // failed => unauthenticated
            return XSPReplyUnauthenticated.instance();
        // now that we are logged-in, retry
        return XSPReplyUtils.fromHttpResponse(connection.request(
                "/database/" + URIUtils.encodeComponent(name),
                HttpConstants.METHOD_PUT,
                HttpConstants.MIME_JSON), this);
    }

    @Override
    public XSPReply dropDatabase(XOWLDatabase database) {
        return dropDatabase(database.getName());
    }

    @Override
    public XSPReply dropDatabase(String database) {
        // not logged in
        if (currentUser == null)
            return XSPReplyNetworkError.instance();
        // supposed to be logged-in
        XSPReply reply = XSPReplyUtils.fromHttpResponse(connection.request(
                "/database/" + URIUtils.encodeComponent(database),
                HttpConstants.METHOD_DELETE,
                HttpConstants.MIME_JSON), this);
        if (reply != XSPReplyUnauthenticated.instance())
            // not an authentication problem => return this reply
            return reply;
        // try to re-login
        reply = login(currentLogin, currentPassword);
        if (!reply.isSuccess())
            // failed => unauthenticated
            return XSPReplyUnauthenticated.instance();
        // now that we are logged-in, retry
        return XSPReplyUtils.fromHttpResponse(connection.request(
                "/database/" + URIUtils.encodeComponent(database),
                HttpConstants.METHOD_DELETE,
                HttpConstants.MIME_JSON), this);
    }

    @Override
    public XSPReply getUsers() {
        // not logged in
        if (currentUser == null)
            return XSPReplyNetworkError.instance();
        // supposed to be logged-in
        XSPReply reply = XSPReplyUtils.fromHttpResponse(connection.request(
                "/users",
                HttpConstants.METHOD_GET,
                HttpConstants.MIME_JSON), this);
        if (reply != XSPReplyUnauthenticated.instance())
            // not an authentication problem => return this reply
            return reply;
        // try to re-login
        reply = login(currentLogin, currentPassword);
        if (!reply.isSuccess())
            // failed => unauthenticated
            return XSPReplyUnauthenticated.instance();
        // now that we are logged-in, retry
        return XSPReplyUtils.fromHttpResponse(connection.request(
                "/users",
                HttpConstants.METHOD_GET,
                HttpConstants.MIME_JSON), this);
    }

    @Override
    public XSPReply getUser(String login) {
        // not logged in
        if (currentUser == null)
            return XSPReplyNetworkError.instance();
        // supposed to be logged-in
        XSPReply reply = XSPReplyUtils.fromHttpResponse(connection.request(
                "/users/" + URIUtils.encodeComponent(login),
                HttpConstants.METHOD_GET,
                HttpConstants.MIME_JSON), this);
        if (reply != XSPReplyUnauthenticated.instance())
            // not an authentication problem => return this reply
            return reply;
        // try to re-login
        reply = login(currentLogin, currentPassword);
        if (!reply.isSuccess())
            // failed => unauthenticated
            return XSPReplyUnauthenticated.instance();
        // now that we are logged-in, retry
        return XSPReplyUtils.fromHttpResponse(connection.request(
                "/users/" + URIUtils.encodeComponent(login),
                HttpConstants.METHOD_GET,
                HttpConstants.MIME_JSON), this);
    }

    @Override
    public XSPReply createUser(String login, String password) {
        // not logged in
        if (currentUser == null)
            return XSPReplyNetworkError.instance();
        // supposed to be logged-in
        XSPReply reply = XSPReplyUtils.fromHttpResponse(connection.request(
                "/users/" + URIUtils.encodeComponent(login),
                HttpConstants.METHOD_GET,
                password,
                HttpConstants.MIME_TEXT_PLAIN,
                HttpConstants.MIME_JSON), this);
        if (reply != XSPReplyUnauthenticated.instance())
            // not an authentication problem => return this reply
            return reply;
        // try to re-login
        reply = login(currentLogin, currentPassword);
        if (!reply.isSuccess())
            // failed => unauthenticated
            return XSPReplyUnauthenticated.instance();
        // now that we are logged-in, retry
        return XSPReplyUtils.fromHttpResponse(connection.request(
                "/users/" + URIUtils.encodeComponent(login),
                HttpConstants.METHOD_GET,
                password,
                HttpConstants.MIME_TEXT_PLAIN,
                HttpConstants.MIME_JSON), this);
    }

    /**
     * Gets the definition of the metrics for this database
     *
     * @param database The target database
     * @return The definition of the metrics for this database
     */
    XSPReply dbGetMetric(String database) {
        // not logged in
        if (currentUser == null)
            return XSPReplyNetworkError.instance();
        // supposed to be logged-in
        XSPReply reply = XSPReplyUtils.fromHttpResponse(connection.request(
                "/database/" + URIUtils.encodeComponent(database) + "/metric",
                HttpConstants.METHOD_GET,
                HttpConstants.MIME_JSON), this);
        if (reply != XSPReplyUnauthenticated.instance())
            // not an authentication problem => return this reply
            return reply;
        // try to re-login
        reply = login(currentLogin, currentPassword);
        if (!reply.isSuccess())
            // failed => unauthenticated
            return XSPReplyUnauthenticated.instance();
        // now that we are logged-in, retry
        return XSPReplyUtils.fromHttpResponse(connection.request(
                "/database/" + URIUtils.encodeComponent(database) + "/metric",
                HttpConstants.METHOD_GET,
                HttpConstants.MIME_JSON), this);
    }

    /**
     * Gets a snapshot of the metrics for this database
     *
     * @param database The target database
     * @return A snapshot of the metrics for this database
     */
    XSPReply dbGetMetricSnapshot(String database) {
        // not logged in
        if (currentUser == null)
            return XSPReplyNetworkError.instance();
        // supposed to be logged-in
        XSPReply reply = XSPReplyUtils.fromHttpResponse(connection.request(
                "/database/" + URIUtils.encodeComponent(database) + "/statistics",
                HttpConstants.METHOD_GET,
                HttpConstants.MIME_JSON), this);
        if (reply != XSPReplyUnauthenticated.instance())
            // not an authentication problem => return this reply
            return reply;
        // try to re-login
        reply = login(currentLogin, currentPassword);
        if (!reply.isSuccess())
            // failed => unauthenticated
            return XSPReplyUnauthenticated.instance();
        // now that we are logged-in, retry
        return XSPReplyUtils.fromHttpResponse(connection.request(
                "/database/" + URIUtils.encodeComponent(database) + "/statistics",
                HttpConstants.METHOD_GET,
                HttpConstants.MIME_JSON), this);
    }

    /**
     * Executes a SPARQL command
     *
     * @param database    The target database
     * @param sparql      The SPARQL command(s)
     * @param defaultIRIs The context's default IRIs
     * @param namedIRIs   The context's named IRIs
     * @return The protocol reply
     */
    XSPReply dbSPARQL(String database, String sparql, List<String> defaultIRIs, List<String> namedIRIs) {
        if (defaultIRIs != null && !defaultIRIs.isEmpty())
            return new XSPReplyApiError(ApiV1.ERROR_DEFAULT_GRAPH_NOT_SUPPORTED);
        if (namedIRIs != null && !namedIRIs.isEmpty())
            return new XSPReplyApiError(ApiV1.ERROR_NAMED_GRAPH_NOT_SUPPORTED);
        // not logged in
        if (currentUser == null)
            return XSPReplyNetworkError.instance();
        // supposed to be logged-in
        XSPReply reply = XSPReplyUtils.fromHttpResponse(connection.request(
                "/database/" + URIUtils.encodeComponent(database) + "/sparql",
                HttpConstants.METHOD_POST,
                sparql,
                Command.MIME_SPARQL_QUERY,
                Result.SYNTAX_JSON), this);
        if (reply != XSPReplyUnauthenticated.instance())
            // not an authentication problem => return this reply
            return reply;
        // try to re-login
        reply = login(currentLogin, currentPassword);
        if (!reply.isSuccess())
            // failed => unauthenticated
            return XSPReplyUnauthenticated.instance();
        // now that we are logged-in, retry
        return XSPReplyUtils.fromHttpResponse(connection.request(
                "/database/" + URIUtils.encodeComponent(database) + "/sparql",
                HttpConstants.METHOD_POST,
                sparql,
                Command.MIME_SPARQL_QUERY,
                Result.SYNTAX_JSON), this);
    }

    /**
     * Gets the entailment regime
     *
     * @param database The target database
     * @return The protocol reply
     */
    XSPReply dbGetEntailmentRegime(String database) {
        // not logged in
        if (currentUser == null)
            return XSPReplyNetworkError.instance();
        // supposed to be logged-in
        XSPReply reply = XSPReplyUtils.fromHttpResponse(connection.request(
                "/database/" + URIUtils.encodeComponent(database) + "/entailment",
                HttpConstants.METHOD_GET,
                HttpConstants.MIME_JSON), this);
        if (reply != XSPReplyUnauthenticated.instance())
            // not an authentication problem => return this reply
            return reply;
        // try to re-login
        reply = login(currentLogin, currentPassword);
        if (!reply.isSuccess())
            // failed => unauthenticated
            return XSPReplyUnauthenticated.instance();
        // now that we are logged-in, retry
        return XSPReplyUtils.fromHttpResponse(connection.request(
                "/database/" + URIUtils.encodeComponent(database) + "/entailment",
                HttpConstants.METHOD_GET,
                HttpConstants.MIME_JSON), this);
    }

    /**
     * Sets the entailment regime
     *
     * @param database The target database
     * @param regime   The entailment regime
     * @return The protocol reply
     */
    XSPReply dbSetEntailmentRegime(String database, EntailmentRegime regime) {
        // not logged in
        if (currentUser == null)
            return XSPReplyNetworkError.instance();
        // supposed to be logged-in
        XSPReply reply = XSPReplyUtils.fromHttpResponse(connection.request(
                "/database/" + URIUtils.encodeComponent(database) + "/entailment",
                HttpConstants.METHOD_POST,
                regime.toString(),
                HttpConstants.MIME_TEXT_PLAIN,
                HttpConstants.MIME_JSON), this);
        if (reply != XSPReplyUnauthenticated.instance())
            // not an authentication problem => return this reply
            return reply;
        // try to re-login
        reply = login(currentLogin, currentPassword);
        if (!reply.isSuccess())
            // failed => unauthenticated
            return XSPReplyUnauthenticated.instance();
        // now that we are logged-in, retry
        return XSPReplyUtils.fromHttpResponse(connection.request(
                "/database/" + URIUtils.encodeComponent(database) + "/entailment",
                HttpConstants.METHOD_POST,
                regime.toString(),
                HttpConstants.MIME_TEXT_PLAIN,
                HttpConstants.MIME_JSON), this);
    }

    /**
     * Gets the privileges assigned to users on a database
     *
     * @param database The target database
     * @return The protocol reply
     */
    XSPReply dbGetPrivileges(String database) {
        // not logged in
        if (currentUser == null)
            return XSPReplyNetworkError.instance();
        // supposed to be logged-in
        XSPReply reply = XSPReplyUtils.fromHttpResponse(connection.request(
                "/database/" + URIUtils.encodeComponent(database) + "/privileges",
                HttpConstants.METHOD_GET,
                HttpConstants.MIME_JSON), this);
        if (reply != XSPReplyUnauthenticated.instance())
            // not an authentication problem => return this reply
            return reply;
        // try to re-login
        reply = login(currentLogin, currentPassword);
        if (!reply.isSuccess())
            // failed => unauthenticated
            return XSPReplyUnauthenticated.instance();
        // now that we are logged-in, retry
        return XSPReplyUtils.fromHttpResponse(connection.request(
                "/database/" + URIUtils.encodeComponent(database) + "/privileges",
                HttpConstants.METHOD_GET,
                HttpConstants.MIME_JSON), this);
    }

    /**
     * Grants a privilege to a user on a database
     *
     * @param database  The target database
     * @param user      The target user
     * @param privilege The privilege to grant
     * @return The protocol reply
     */
    XSPReply dbGrant(String database, String user, int privilege) {
        String access = privilege == XOWLPrivilege.ADMIN ? "ADMIN" : (privilege == XOWLPrivilege.WRITE ? "WRITE" : (privilege == XOWLPrivilege.READ ? "READ" : "NONE"));
        // not logged in
        if (currentUser == null)
            return XSPReplyNetworkError.instance();
        // supposed to be logged-in
        XSPReply reply = XSPReplyUtils.fromHttpResponse(connection.request(
                "/database/" + URIUtils.encodeComponent(database) + "/privileges/grant" +
                        "?user=" + URIUtils.encodeComponent(user) +
                        "?access=" + access,
                HttpConstants.METHOD_POST,
                HttpConstants.MIME_JSON), this);
        if (reply != XSPReplyUnauthenticated.instance())
            // not an authentication problem => return this reply
            return reply;
        // try to re-login
        reply = login(currentLogin, currentPassword);
        if (!reply.isSuccess())
            // failed => unauthenticated
            return XSPReplyUnauthenticated.instance();
        // now that we are logged-in, retry
        return XSPReplyUtils.fromHttpResponse(connection.request(
                "/database/" + URIUtils.encodeComponent(database) + "/privileges/grant" +
                        "?user=" + URIUtils.encodeComponent(user) +
                        "?access=" + access,
                HttpConstants.METHOD_POST,
                HttpConstants.MIME_JSON), this);
    }

    /**
     * Revokes a privilege from a user on a database
     *
     * @param database  The target database
     * @param user      The target user
     * @param privilege The privilege to revoke
     * @return The protocol reply
     */
    XSPReply dbRevoke(String database, String user, int privilege) {
        String access = privilege == XOWLPrivilege.ADMIN ? "ADMIN" : (privilege == XOWLPrivilege.WRITE ? "WRITE" : (privilege == XOWLPrivilege.READ ? "READ" : "NONE"));
        // not logged in
        if (currentUser == null)
            return XSPReplyNetworkError.instance();
        // supposed to be logged-in
        XSPReply reply = XSPReplyUtils.fromHttpResponse(connection.request(
                "/database/" + URIUtils.encodeComponent(database) + "/privileges/revoke" +
                        "?user=" + URIUtils.encodeComponent(user) +
                        "?access=" + access,
                HttpConstants.METHOD_POST,
                HttpConstants.MIME_JSON), this);
        if (reply != XSPReplyUnauthenticated.instance())
            // not an authentication problem => return this reply
            return reply;
        // try to re-login
        reply = login(currentLogin, currentPassword);
        if (!reply.isSuccess())
            // failed => unauthenticated
            return XSPReplyUnauthenticated.instance();
        // now that we are logged-in, retry
        return XSPReplyUtils.fromHttpResponse(connection.request(
                "/database/" + URIUtils.encodeComponent(database) + "/privileges/revoke" +
                        "?user=" + URIUtils.encodeComponent(user) +
                        "?access=" + access,
                HttpConstants.METHOD_POST,
                HttpConstants.MIME_JSON), this);
    }

    /**
     * Gets the rules in this database
     *
     * @param database The target database
     * @return The protocol reply
     */
    XSPReply dbGetRules(String database) {
        // not logged in
        if (currentUser == null)
            return XSPReplyNetworkError.instance();
        // supposed to be logged-in
        XSPReply reply = XSPReplyUtils.fromHttpResponse(connection.request(
                "/database/" + URIUtils.encodeComponent(database) + "/rules",
                HttpConstants.METHOD_GET,
                HttpConstants.MIME_JSON), this);
        if (reply != XSPReplyUnauthenticated.instance())
            // not an authentication problem => return this reply
            return reply;
        // try to re-login
        reply = login(currentLogin, currentPassword);
        if (!reply.isSuccess())
            // failed => unauthenticated
            return XSPReplyUnauthenticated.instance();
        // now that we are logged-in, retry
        return XSPReplyUtils.fromHttpResponse(connection.request(
                "/database/" + URIUtils.encodeComponent(database) + "/rules",
                HttpConstants.METHOD_GET,
                HttpConstants.MIME_JSON), this);
    }

    /**
     * Gets the rule for the specified name
     *
     * @param database The target database
     * @param name     The name (IRI) of a rule
     * @return The protocol reply
     */
    XSPReply dbGetRule(String database, String name) {
        // not logged in
        if (currentUser == null)
            return XSPReplyNetworkError.instance();
        // supposed to be logged-in
        XSPReply reply = XSPReplyUtils.fromHttpResponse(connection.request(
                "/database/" + URIUtils.encodeComponent(database) + "/rules/" + URIUtils.encodeComponent(name),
                HttpConstants.METHOD_GET,
                HttpConstants.MIME_JSON), this);
        if (reply != XSPReplyUnauthenticated.instance())
            // not an authentication problem => return this reply
            return reply;
        // try to re-login
        reply = login(currentLogin, currentPassword);
        if (!reply.isSuccess())
            // failed => unauthenticated
            return XSPReplyUnauthenticated.instance();
        // now that we are logged-in, retry
        return XSPReplyUtils.fromHttpResponse(connection.request(
                "/database/" + URIUtils.encodeComponent(database) + "/rules/" + URIUtils.encodeComponent(name),
                HttpConstants.METHOD_GET,
                HttpConstants.MIME_JSON), this);
    }

    /**
     * Adds a new rule to this database
     *
     * @param database The target database
     * @param content  The rule's content
     * @param activate Whether to readily activate the rule
     * @return The protocol reply
     */
    XSPReply dbAddRule(String database, String content, boolean activate) {
        // not logged in
        if (currentUser == null)
            return XSPReplyNetworkError.instance();
        // supposed to be logged-in
        XSPReply reply = XSPReplyUtils.fromHttpResponse(connection.request(
                "/database/" + URIUtils.encodeComponent(database) + "/rules?active=" + Boolean.toString(activate),
                HttpConstants.METHOD_PUT,
                content,
                Repository.SYNTAX_RDFT,
                HttpConstants.MIME_JSON), this);
        if (reply != XSPReplyUnauthenticated.instance())
            // not an authentication problem => return this reply
            return reply;
        // try to re-login
        reply = login(currentLogin, currentPassword);
        if (!reply.isSuccess())
            // failed => unauthenticated
            return XSPReplyUnauthenticated.instance();
        // now that we are logged-in, retry
        return XSPReplyUtils.fromHttpResponse(connection.request(
                "/database/" + URIUtils.encodeComponent(database) + "/rules?active=" + Boolean.toString(activate),
                HttpConstants.METHOD_PUT,
                content,
                Repository.SYNTAX_RDFT,
                HttpConstants.MIME_JSON), this);
    }

    /**
     * Removes a rule from this database
     *
     * @param database The target database
     * @param rule     The rule to remove
     * @return The protocol reply
     */
    XSPReply dbRemoveRule(String database, String rule) {
        // not logged in
        if (currentUser == null)
            return XSPReplyNetworkError.instance();
        // supposed to be logged-in
        XSPReply reply = XSPReplyUtils.fromHttpResponse(connection.request(
                "/database/" + URIUtils.encodeComponent(database) + "/rules/" + URIUtils.encodeComponent(rule),
                HttpConstants.METHOD_DELETE,
                HttpConstants.MIME_JSON), this);
        if (reply != XSPReplyUnauthenticated.instance())
            // not an authentication problem => return this reply
            return reply;
        // try to re-login
        reply = login(currentLogin, currentPassword);
        if (!reply.isSuccess())
            // failed => unauthenticated
            return XSPReplyUnauthenticated.instance();
        // now that we are logged-in, retry
        return XSPReplyUtils.fromHttpResponse(connection.request(
                "/database/" + URIUtils.encodeComponent(database) + "/rules/" + URIUtils.encodeComponent(rule),
                HttpConstants.METHOD_DELETE,
                HttpConstants.MIME_JSON), this);
    }

    /**
     * Activates an existing rule in this database
     *
     * @param database The target database
     * @param rule     The rule to activate
     * @return The protocol reply
     */
    XSPReply dbActivateRule(String database, String rule) {
        // not logged in
        if (currentUser == null)
            return XSPReplyNetworkError.instance();
        // supposed to be logged-in
        XSPReply reply = XSPReplyUtils.fromHttpResponse(connection.request(
                "/database/" + URIUtils.encodeComponent(database) + "/rules/" + URIUtils.encodeComponent(rule) + "/activate",
                HttpConstants.METHOD_POST,
                HttpConstants.MIME_JSON), this);
        if (reply != XSPReplyUnauthenticated.instance())
            // not an authentication problem => return this reply
            return reply;
        // try to re-login
        reply = login(currentLogin, currentPassword);
        if (!reply.isSuccess())
            // failed => unauthenticated
            return XSPReplyUnauthenticated.instance();
        // now that we are logged-in, retry
        return XSPReplyUtils.fromHttpResponse(connection.request(
                "/database/" + URIUtils.encodeComponent(database) + "/rules/" + URIUtils.encodeComponent(rule) + "/activate",
                HttpConstants.METHOD_POST,
                HttpConstants.MIME_JSON), this);
    }

    /**
     * Deactivates an existing rule in this database
     *
     * @param database The target database
     * @param rule     The rule to deactivate
     * @return The protocol reply
     */
    XSPReply dbDeactivateRule(String database, String rule) {
        // not logged in
        if (currentUser == null)
            return XSPReplyNetworkError.instance();
        // supposed to be logged-in
        XSPReply reply = XSPReplyUtils.fromHttpResponse(connection.request(
                "/database/" + URIUtils.encodeComponent(database) + "/rules/" + URIUtils.encodeComponent(rule) + "/deactivate",
                HttpConstants.METHOD_POST,
                HttpConstants.MIME_JSON), this);
        if (reply != XSPReplyUnauthenticated.instance())
            // not an authentication problem => return this reply
            return reply;
        // try to re-login
        reply = login(currentLogin, currentPassword);
        if (!reply.isSuccess())
            // failed => unauthenticated
            return XSPReplyUnauthenticated.instance();
        // now that we are logged-in, retry
        return XSPReplyUtils.fromHttpResponse(connection.request(
                "/database/" + URIUtils.encodeComponent(database) + "/rules/" + URIUtils.encodeComponent(rule) + "/deactivate",
                HttpConstants.METHOD_POST,
                HttpConstants.MIME_JSON), this);
    }

    /**
     * Gets the matching status of a rule in this database
     *
     * @param database The target database
     * @param rule     The rule to inquire
     * @return The protocol reply
     */
    XSPReply dbGetRuleStatus(String database, String rule) {
        // not logged in
        if (currentUser == null)
            return XSPReplyNetworkError.instance();
        // supposed to be logged-in
        XSPReply reply = XSPReplyUtils.fromHttpResponse(connection.request(
                "/database/" + URIUtils.encodeComponent(database) + "/rules/" + URIUtils.encodeComponent(rule) + "/status",
                HttpConstants.METHOD_GET,
                HttpConstants.MIME_JSON), this);
        if (reply != XSPReplyUnauthenticated.instance())
            // not an authentication problem => return this reply
            return reply;
        // try to re-login
        reply = login(currentLogin, currentPassword);
        if (!reply.isSuccess())
            // failed => unauthenticated
            return XSPReplyUnauthenticated.instance();
        // now that we are logged-in, retry
        return XSPReplyUtils.fromHttpResponse(connection.request(
                "/database/" + URIUtils.encodeComponent(database) + "/rules/" + URIUtils.encodeComponent(rule) + "/status",
                HttpConstants.METHOD_GET,
                HttpConstants.MIME_JSON), this);
    }

    /**
     * Gets the stored procedures for this database
     *
     * @param database The target database
     * @return The protocol reply
     */
    XSPReply dbGetStoredProcedures(String database) {
        // not logged in
        if (currentUser == null)
            return XSPReplyNetworkError.instance();
        // supposed to be logged-in
        XSPReply reply = XSPReplyUtils.fromHttpResponse(connection.request(
                "/database/" + URIUtils.encodeComponent(database) + "/procedures",
                HttpConstants.METHOD_GET,
                HttpConstants.MIME_JSON), this);
        if (reply != XSPReplyUnauthenticated.instance())
            // not an authentication problem => return this reply
            return reply;
        // try to re-login
        reply = login(currentLogin, currentPassword);
        if (!reply.isSuccess())
            // failed => unauthenticated
            return XSPReplyUnauthenticated.instance();
        // now that we are logged-in, retry
        return XSPReplyUtils.fromHttpResponse(connection.request(
                "/database/" + URIUtils.encodeComponent(database) + "/procedures",
                HttpConstants.METHOD_GET,
                HttpConstants.MIME_JSON), this);
    }

    /**
     * Gets the stored procedure for the specified name (iri)
     *
     * @param database The target database
     * @param iri      The name (iri) of a stored procedure
     * @return The protocol reply
     */
    XSPReply dbGetStoreProcedure(String database, String iri) {
        // not logged in
        if (currentUser == null)
            return XSPReplyNetworkError.instance();
        // supposed to be logged-in
        XSPReply reply = XSPReplyUtils.fromHttpResponse(connection.request(
                "/database/" + URIUtils.encodeComponent(database) + "/procedures/" + URIUtils.encodeComponent(iri),
                HttpConstants.METHOD_GET,
                HttpConstants.MIME_JSON), this);
        if (reply != XSPReplyUnauthenticated.instance())
            // not an authentication problem => return this reply
            return reply;
        // try to re-login
        reply = login(currentLogin, currentPassword);
        if (!reply.isSuccess())
            // failed => unauthenticated
            return XSPReplyUnauthenticated.instance();
        // now that we are logged-in, retry
        return XSPReplyUtils.fromHttpResponse(connection.request(
                "/database/" + URIUtils.encodeComponent(database) + "/procedures/" + URIUtils.encodeComponent(iri),
                HttpConstants.METHOD_GET,
                HttpConstants.MIME_JSON), this);
    }

    /**
     * Adds a stored procedure in the form of a SPARQL command
     *
     * @param database   The target database
     * @param iri        The name (iri) for this procedure
     * @param sparql     The SPARQL command(s)
     * @param parameters The names of the parameters for this procedure
     * @return The protocol reply
     */
    XSPReply dbAddStoredProcedure(String database, String iri, String sparql, Collection<String> parameters) {
        BaseStoredProcedure procedure = new BaseStoredProcedure(iri, sparql, parameters, null);
        // not logged in
        if (currentUser == null)
            return XSPReplyNetworkError.instance();
        // supposed to be logged-in
        XSPReply reply = XSPReplyUtils.fromHttpResponse(connection.request(
                "/database/" + URIUtils.encodeComponent(database) + "/procedures/" + URIUtils.encodeComponent(iri),
                HttpConstants.METHOD_PUT,
                procedure.serializedJSON(),
                HttpConstants.MIME_JSON,
                HttpConstants.MIME_JSON), this);
        if (reply != XSPReplyUnauthenticated.instance())
            // not an authentication problem => return this reply
            return reply;
        // try to re-login
        reply = login(currentLogin, currentPassword);
        if (!reply.isSuccess())
            // failed => unauthenticated
            return XSPReplyUnauthenticated.instance();
        // now that we are logged-in, retry
        return XSPReplyUtils.fromHttpResponse(connection.request(
                "/database/" + URIUtils.encodeComponent(database) + "/procedures/" + URIUtils.encodeComponent(iri),
                HttpConstants.METHOD_PUT,
                procedure.serializedJSON(),
                HttpConstants.MIME_JSON,
                HttpConstants.MIME_JSON), this);
    }

    /**
     * Remove a stored procedure
     *
     * @param database  The target database
     * @param procedure The procedure to remove
     * @return The protocol reply
     */
    XSPReply dbRemoveStoredProcedure(String database, String procedure) {
        // not logged in
        if (currentUser == null)
            return XSPReplyNetworkError.instance();
        // supposed to be logged-in
        XSPReply reply = XSPReplyUtils.fromHttpResponse(connection.request(
                "/database/" + URIUtils.encodeComponent(database) + "/procedures/" + URIUtils.encodeComponent(procedure),
                HttpConstants.METHOD_DELETE,
                HttpConstants.MIME_JSON), this);
        if (reply != XSPReplyUnauthenticated.instance())
            // not an authentication problem => return this reply
            return reply;
        // try to re-login
        reply = login(currentLogin, currentPassword);
        if (!reply.isSuccess())
            // failed => unauthenticated
            return XSPReplyUnauthenticated.instance();
        // now that we are logged-in, retry
        return XSPReplyUtils.fromHttpResponse(connection.request(
                "/database/" + URIUtils.encodeComponent(database) + "/procedures/" + URIUtils.encodeComponent(procedure),
                HttpConstants.METHOD_DELETE,
                HttpConstants.MIME_JSON), this);
    }

    /**
     * Executes a stored procedure
     *
     * @param database  The target database
     * @param procedure The procedure to execute
     * @param context   The execution context to use
     * @return The protocol reply
     */
    XSPReply dbExecuteStoredProcedure(String database, String procedure, XOWLStoredProcedureContext context) {
        // not logged in
        if (currentUser == null)
            return XSPReplyNetworkError.instance();
        // supposed to be logged-in
        XSPReply reply = XSPReplyUtils.fromHttpResponse(connection.request(
                "/database/" + URIUtils.encodeComponent(database) + "/procedures/" + URIUtils.encodeComponent(procedure),
                HttpConstants.METHOD_POST,
                context.serializedJSON(),
                HttpConstants.MIME_JSON,
                HttpConstants.MIME_JSON), this);
        if (reply != XSPReplyUnauthenticated.instance())
            // not an authentication problem => return this reply
            return reply;
        // try to re-login
        reply = login(currentLogin, currentPassword);
        if (!reply.isSuccess())
            // failed => unauthenticated
            return XSPReplyUnauthenticated.instance();
        // now that we are logged-in, retry
        return XSPReplyUtils.fromHttpResponse(connection.request(
                "/database/" + URIUtils.encodeComponent(database) + "/procedures/" + URIUtils.encodeComponent(procedure),
                HttpConstants.METHOD_POST,
                context.serializedJSON(),
                HttpConstants.MIME_JSON,
                HttpConstants.MIME_JSON), this);
    }

    /**
     * Uploads some content to this database
     *
     * @param database The target database
     * @param syntax   The content's syntax
     * @param content  The content
     * @return The protocol reply
     */
    XSPReply dbUpload(String database, String syntax, String content) {
        byte[] input;
        try {
            ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
            GZIPOutputStream compressor = new GZIPOutputStream(byteBuffer);
            input = content.getBytes(Files.CHARSET);
            compressor.write(input, 0, input.length);
            compressor.finish();
            input = byteBuffer.toByteArray();
        } catch (IOException exception) {
            return new XSPReplyException(exception);
        }
        // not logged in
        if (currentUser == null)
            return XSPReplyNetworkError.instance();
        // supposed to be logged-in
        XSPReply reply = XSPReplyUtils.fromHttpResponse(connection.request(
                "/database/" + URIUtils.encodeComponent(database),
                HttpConstants.METHOD_POST,
                input,
                syntax,
                true,
                HttpConstants.MIME_JSON), this);
        if (reply != XSPReplyUnauthenticated.instance())
            // not an authentication problem => return this reply
            return reply;
        // try to re-login
        reply = login(currentLogin, currentPassword);
        if (!reply.isSuccess())
            // failed => unauthenticated
            return XSPReplyUnauthenticated.instance();
        // now that we are logged-in, retry
        return XSPReplyUtils.fromHttpResponse(connection.request(
                "/database/" + URIUtils.encodeComponent(database),
                HttpConstants.METHOD_POST,
                input,
                syntax,
                true,
                HttpConstants.MIME_JSON), this);
    }

    /**
     * Uploads quads to this database
     *
     * @param database The target database
     * @param quads    The quads to upload
     * @return The protocol reply
     */
    XSPReply dbUpload(String database, Collection<Quad> quads) {
        if (connection == null)
            return XSPReplyNetworkError.instance();
        StringWriter writer = new StringWriter();
        NQuadsSerializer serializer = new NQuadsSerializer(writer);
        BufferedLogger logger = new BufferedLogger();
        serializer.serialize(logger, quads.iterator());
        if (!logger.getErrorMessages().isEmpty())
            return new XSPReplyApiError(ApiV1.ERROR_SERIALIZATION_FAILED, logger.getErrorsAsString());
        return dbUpload(database, writer.toString(), Repository.SYNTAX_NQUADS);
    }

    @Override
    public XSPReply deleteUser(XOWLUser toDelete) {
        return deleteUser(toDelete.getName());
    }

    @Override
    public XSPReply deleteUser(String toDelete) {
        // not logged in
        if (currentUser == null)
            return XSPReplyNetworkError.instance();
        // supposed to be logged-in
        XSPReply reply = XSPReplyUtils.fromHttpResponse(connection.request(
                "/users/" + URIUtils.encodeComponent(toDelete),
                HttpConstants.METHOD_DELETE,
                HttpConstants.MIME_JSON), this);
        if (reply != XSPReplyUnauthenticated.instance())
            // not an authentication problem => return this reply
            return reply;
        // try to re-login
        reply = login(currentLogin, currentPassword);
        if (!reply.isSuccess())
            // failed => unauthenticated
            return XSPReplyUnauthenticated.instance();
        // now that we are logged-in, retry
        return XSPReplyUtils.fromHttpResponse(connection.request(
                "/users/" + URIUtils.encodeComponent(toDelete),
                HttpConstants.METHOD_DELETE,
                HttpConstants.MIME_JSON), this);
    }

    /**
     * Updates the password of the user
     *
     * @param user     The target user
     * @param password The new password
     * @return The protocol reply
     */
    XSPReply userUpdatePassword(String user, String password) {
        // not logged in
        if (currentUser == null)
            return XSPReplyNetworkError.instance();
        // supposed to be logged-in
        XSPReply reply = XSPReplyUtils.fromHttpResponse(connection.request(
                "/users/" + URIUtils.encodeComponent(user) + "/password",
                HttpConstants.METHOD_POST,
                password,
                HttpConstants.MIME_TEXT_PLAIN,
                HttpConstants.MIME_JSON), this);
        if (reply != XSPReplyUnauthenticated.instance())
            // not an authentication problem => return this reply
            return reply;
        // try to re-login
        reply = login(currentLogin, currentPassword);
        if (!reply.isSuccess())
            // failed => unauthenticated
            return XSPReplyUnauthenticated.instance();
        // now that we are logged-in, retry
        return XSPReplyUtils.fromHttpResponse(connection.request(
                "/users/" + URIUtils.encodeComponent(user) + "/password",
                HttpConstants.METHOD_POST,
                password,
                HttpConstants.MIME_TEXT_PLAIN,
                HttpConstants.MIME_JSON), this);
    }

    /**
     * Gets the privileges assigned to a user
     *
     * @param user The target user
     * @return The protocol reply
     */
    XSPReply userGetPrivileges(String user) {
        // not logged in
        if (currentUser == null)
            return XSPReplyNetworkError.instance();
        // supposed to be logged-in
        XSPReply reply = XSPReplyUtils.fromHttpResponse(connection.request(
                "/users/" + URIUtils.encodeComponent(user) + "/privileges",
                HttpConstants.METHOD_GET,
                HttpConstants.MIME_JSON), this);
        if (reply != XSPReplyUnauthenticated.instance())
            // not an authentication problem => return this reply
            return reply;
        // try to re-login
        reply = login(currentLogin, currentPassword);
        if (!reply.isSuccess())
            // failed => unauthenticated
            return XSPReplyUnauthenticated.instance();
        // now that we are logged-in, retry
        return XSPReplyUtils.fromHttpResponse(connection.request(
                "/users/" + URIUtils.encodeComponent(user) + "/privileges",
                HttpConstants.METHOD_GET,
                HttpConstants.MIME_JSON), this);
    }

    @Override
    public Object newObject(String type, ASTNode definition) {
        if (XOWLDatabase.class.getCanonicalName().equals(type)) {
            return new RemoteDatabase(this, definition);
        } else if (XOWLDatabasePrivileges.class.getCanonicalName().equals(type)) {
            return new BaseDatabasePrivileges(definition);
        } else if (XOWLRule.class.getCanonicalName().equals(type)) {
            return new BaseRule(definition);
        } else if (XOWLUser.class.getCanonicalName().equals(type)) {
            return new BaseUser(definition);
        } else if (XOWLUserPrivileges.class.getCanonicalName().equals(type)) {
            return new BaseUserPrivileges(definition);
        }
        return null;
    }

    @Override
    public void close() throws IOException {
        // do nothing
    }
}
