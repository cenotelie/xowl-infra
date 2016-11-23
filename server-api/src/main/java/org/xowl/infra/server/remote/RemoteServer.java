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
                        "?login=" + URIUtils.encodeComponent(login) +
                        "&password=" + URIUtils.encodeComponent(password),
                HttpConstants.METHOD_POST,
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

    @Override
    public XSPReply deleteUser(XOWLUser toDelete) {
        // not logged in
        if (currentUser == null)
            return XSPReplyNetworkError.instance();
        // supposed to be logged-in
        XSPReply reply = XSPReplyUtils.fromHttpResponse(connection.request(
                "/users/" + URIUtils.encodeComponent(toDelete.getName()),
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
                "/users/" + URIUtils.encodeComponent(toDelete.getName()),
                HttpConstants.METHOD_DELETE,
                HttpConstants.MIME_JSON), this);
    }

    @Override
    public XSPReply changePassword(String password) {
        // not logged in
        if (currentUser == null)
            return XSPReplyNetworkError.instance();
        // supposed to be logged-in
        XSPReply reply = XSPReplyUtils.fromHttpResponse(connection.request(
                "/users/" + URIUtils.encodeComponent(currentLogin) + "/password",
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
                "/users/" + URIUtils.encodeComponent(currentLogin) + "/password",
                HttpConstants.METHOD_POST,
                password,
                HttpConstants.MIME_TEXT_PLAIN,
                HttpConstants.MIME_JSON), this);
    }

    @Override
    public XSPReply resetPassword(XOWLUser target, String password) {
        // not logged in
        if (currentUser == null)
            return XSPReplyNetworkError.instance();
        // supposed to be logged-in
        XSPReply reply = XSPReplyUtils.fromHttpResponse(connection.request(
                "/users/" + URIUtils.encodeComponent(target.getName()) + "/password",
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
                "/users/" + URIUtils.encodeComponent(target.getName()) + "/password",
                HttpConstants.METHOD_POST,
                password,
                HttpConstants.MIME_TEXT_PLAIN,
                HttpConstants.MIME_JSON), this);
    }

    @Override
    public XSPReply getPrivileges(XOWLUser user) {
        // not logged in
        if (currentUser == null)
            return XSPReplyNetworkError.instance();
        // supposed to be logged-in
        XSPReply reply = XSPReplyUtils.fromHttpResponse(connection.request(
                "/users/" + URIUtils.encodeComponent(user.getName()) + "/privileges",
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
                "/users/" + URIUtils.encodeComponent(user.getName()) + "/privileges",
                HttpConstants.METHOD_GET,
                HttpConstants.MIME_JSON), this);
    }

    @Override
    public XSPReply grantServerAdmin(XOWLUser target) {
        // not logged in
        if (currentUser == null)
            return XSPReplyNetworkError.instance();
        // supposed to be logged-in
        XSPReply reply = XSPReplyUtils.fromHttpResponse(connection.request(
                "/server/grantAdmin?user=" + URIUtils.encodeComponent(target.getName()),
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
                "/server/grantAdmin?user=" + URIUtils.encodeComponent(target.getName()),
                HttpConstants.METHOD_POST,
                HttpConstants.MIME_JSON), this);
    }

    @Override
    public XSPReply revokeServerAdmin(XOWLUser target) {
        // not logged in
        if (currentUser == null)
            return XSPReplyNetworkError.instance();
        // supposed to be logged-in
        XSPReply reply = XSPReplyUtils.fromHttpResponse(connection.request(
                "/server/revokeAdmin?user=" + URIUtils.encodeComponent(target.getName()),
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
                "/server/revokeAdmin?user=" + URIUtils.encodeComponent(target.getName()),
                HttpConstants.METHOD_POST,
                HttpConstants.MIME_JSON), this);
    }

    @Override
    public XSPReply grantDB(XOWLUser user, XOWLDatabase database, int privilege) {
        String access = privilege == XOWLPrivilege.ADMIN ? "ADMIN" : (privilege == XOWLPrivilege.WRITE ? "WRITE" : (privilege == XOWLPrivilege.READ ? "READ" : "NONE"));
        // not logged in
        if (currentUser == null)
            return XSPReplyNetworkError.instance();
        // supposed to be logged-in
        XSPReply reply = XSPReplyUtils.fromHttpResponse(connection.request(
                "/database/" + URIUtils.encodeComponent(database.getName()) + "/privileges/grant" +
                        "?user=" + URIUtils.encodeComponent(user.getName()) +
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
                "/database/" + URIUtils.encodeComponent(database.getName()) + "/privileges/grant" +
                        "?user=" + URIUtils.encodeComponent(user.getName()) +
                        "?access=" + access,
                HttpConstants.METHOD_POST,
                HttpConstants.MIME_JSON), this);
    }

    @Override
    public XSPReply revokeDB(XOWLUser user, XOWLDatabase database, int privilege) {
        String access = privilege == XOWLPrivilege.ADMIN ? "ADMIN" : (privilege == XOWLPrivilege.WRITE ? "WRITE" : (privilege == XOWLPrivilege.READ ? "READ" : "NONE"));
        // not logged in
        if (currentUser == null)
            return XSPReplyNetworkError.instance();
        // supposed to be logged-in
        XSPReply reply = XSPReplyUtils.fromHttpResponse(connection.request(
                "/database/" + URIUtils.encodeComponent(database.getName()) + "/privileges/revoke" +
                        "?user=" + URIUtils.encodeComponent(user.getName()) +
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
                "/database/" + URIUtils.encodeComponent(database.getName()) + "/privileges/revoke" +
                        "?user=" + URIUtils.encodeComponent(user.getName()) +
                        "?access=" + access,
                HttpConstants.METHOD_POST,
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
        // not logged in
        if (currentUser == null)
            return XSPReplyNetworkError.instance();
        // supposed to be logged-in
        XSPReply reply = XSPReplyUtils.fromHttpResponse(connection.request(
                "/database/" + URIUtils.encodeComponent(database.getName()),
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
                "/database/" + URIUtils.encodeComponent(database.getName()),
                HttpConstants.METHOD_DELETE,
                HttpConstants.MIME_JSON), this);
    }

    @Override
    public XSPReply getPrivileges(XOWLDatabase database) {
// not logged in
        if (currentUser == null)
            return XSPReplyNetworkError.instance();
        // supposed to be logged-in
        XSPReply reply = XSPReplyUtils.fromHttpResponse(connection.request(
                "/database/" + URIUtils.encodeComponent(database.getName()) + "/privileges",
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
                "/database/" + URIUtils.encodeComponent(database.getName()) + "/privileges",
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
    XSPReply sparql(String database, String sparql, List<String> defaultIRIs, List<String> namedIRIs) {
        if (defaultIRIs != null && !defaultIRIs.isEmpty())
            return new XSPReplyFailure("The specification of default graphs is not supported");
        if (namedIRIs != null && !namedIRIs.isEmpty())
            return new XSPReplyFailure("The specification of named graphs is not supported");
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
    XSPReply getEntailmentRegime(String database) {
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
    XSPReply setEntailmentRegime(String database, EntailmentRegime regime) {
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
     * Gets the rule for the specified name
     *
     * @param database The target database
     * @param name     The name (IRI) of a rule
     * @return The protocol reply
     */
    XSPReply getRule(String database, String name) {
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
     * Gets the rules in this database
     *
     * @param database The target database
     * @return The protocol reply
     */
    XSPReply getRules(String database) {
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
     * Adds a new rule to this database
     *
     * @param database The target database
     * @param content  The rule's content
     * @param activate Whether to readily activate the rule
     * @return The protocol reply
     */
    XSPReply addRule(String database, String content, boolean activate) {
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
    XSPReply removeRule(String database, XOWLRule rule) {
        // not logged in
        if (currentUser == null)
            return XSPReplyNetworkError.instance();
        // supposed to be logged-in
        XSPReply reply = XSPReplyUtils.fromHttpResponse(connection.request(
                "/database/" + URIUtils.encodeComponent(database) + "/rules/" + URIUtils.encodeComponent(rule.getName()),
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
                "/database/" + URIUtils.encodeComponent(database) + "/rules/" + URIUtils.encodeComponent(rule.getName()),
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
    XSPReply activateRule(String database, XOWLRule rule) {
        // not logged in
        if (currentUser == null)
            return XSPReplyNetworkError.instance();
        // supposed to be logged-in
        XSPReply reply = XSPReplyUtils.fromHttpResponse(connection.request(
                "/database/" + URIUtils.encodeComponent(database) + "/rules/" + URIUtils.encodeComponent(rule.getName()) + "/activate",
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
                "/database/" + URIUtils.encodeComponent(database) + "/rules/" + URIUtils.encodeComponent(rule.getName()) + "/activate",
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
    XSPReply deactivateRule(String database, XOWLRule rule) {
        // not logged in
        if (currentUser == null)
            return XSPReplyNetworkError.instance();
        // supposed to be logged-in
        XSPReply reply = XSPReplyUtils.fromHttpResponse(connection.request(
                "/database/" + URIUtils.encodeComponent(database) + "/rules/" + URIUtils.encodeComponent(rule.getName()) + "/deactivate",
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
                "/database/" + URIUtils.encodeComponent(database) + "/rules/" + URIUtils.encodeComponent(rule.getName()) + "/deactivate",
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
    XSPReply getRuleStatus(String database, XOWLRule rule) {
        // not logged in
        if (currentUser == null)
            return XSPReplyNetworkError.instance();
        // supposed to be logged-in
        XSPReply reply = XSPReplyUtils.fromHttpResponse(connection.request(
                "/database/" + URIUtils.encodeComponent(database) + "/rules/" + URIUtils.encodeComponent(rule.getName()) + "/status",
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
                "/database/" + URIUtils.encodeComponent(database) + "/rules/" + URIUtils.encodeComponent(rule.getName()) + "/status",
                HttpConstants.METHOD_GET,
                HttpConstants.MIME_JSON), this);
    }

    /**
     * Gets the stored procedure for the specified name
     *
     * @param database The target database
     * @param iri      The name (IRI) of a procedure
     * @return The protocol reply
     */
    XSPReply getStoreProcedure(String database, String iri) {
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
     * Gets the stored procedures in a database
     *
     * @param database The target database
     * @return The protocol reply
     */
    XSPReply getStoredProcedures(String database) {
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
     * Adds a stored procedure in a database
     *
     * @param database   The database that would store the procedure
     * @param iri        The name (IRI) of the procedure
     * @param sparql     The SPARQL definition of the procedure
     * @param parameters The parameters for this procedure
     * @return The protocol reply
     */
    XSPReply addStoredProcedure(String database, String iri, String sparql, Collection<String> parameters) {
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
     * Removes a stored procedure from a database
     *
     * @param database  The target database
     * @param procedure The procedure to remove
     * @return The protocol reply
     */
    XSPReply removeStoredProcedure(String database, XOWLStoredProcedure procedure) {
        // not logged in
        if (currentUser == null)
            return XSPReplyNetworkError.instance();
        // supposed to be logged-in
        XSPReply reply = XSPReplyUtils.fromHttpResponse(connection.request(
                "/database/" + URIUtils.encodeComponent(database) + "/procedures/" + URIUtils.encodeComponent(procedure.getName()),
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
                "/database/" + URIUtils.encodeComponent(database) + "/procedures/" + URIUtils.encodeComponent(procedure.getName()),
                HttpConstants.METHOD_DELETE,
                HttpConstants.MIME_JSON), this);
    }

    /**
     * Executes a stored procedure
     *
     * @param database  The target database
     * @param procedure The procedure to execute
     * @param context   The context for the execution
     * @return The protocol reply
     */
    XSPReply executeStoredProcedure(String database, XOWLStoredProcedure procedure, XOWLStoredProcedureContext context) {
        // not logged in
        if (currentUser == null)
            return XSPReplyNetworkError.instance();
        // supposed to be logged-in
        XSPReply reply = XSPReplyUtils.fromHttpResponse(connection.request(
                "/database/" + URIUtils.encodeComponent(database) + "/procedures/" + URIUtils.encodeComponent(procedure.getName()),
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
                "/database/" + URIUtils.encodeComponent(database) + "/procedures/" + URIUtils.encodeComponent(procedure.getName()),
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
    XSPReply upload(String database, String syntax, String content) {
        byte[] input;
        try {
            ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
            GZIPOutputStream compressor = new GZIPOutputStream(byteBuffer);
            input = content.getBytes(Files.CHARSET);
            compressor.write(input, 0, input.length);
            compressor.finish();
            input = byteBuffer.toByteArray();
        } catch (IOException exception) {
            return new XSPReplyFailure(exception.getMessage());
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
     * Uploads some content to this database
     *
     * @param database The target database
     * @param quads    The quads to upload
     * @return The protocol reply
     */
    XSPReply upload(String database, Collection<Quad> quads) {
        if (connection == null)
            return XSPReplyNetworkError.instance();
        StringWriter writer = new StringWriter();
        NQuadsSerializer serializer = new NQuadsSerializer(writer);
        BufferedLogger logger = new BufferedLogger();
        serializer.serialize(logger, quads.iterator());
        if (!logger.getErrorMessages().isEmpty())
            return new XSPReplyFailure(logger.getErrorsAsString());
        return upload(database, writer.toString(), Repository.SYNTAX_NQUADS);
    }

    /**
     * Gets the definition of the metrics for a database
     *
     * @param database The target database
     * @return The definition of the metrics for a database
     */
    XSPReply getMetric(String database) {
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
     * Gets a snapshot of the metrics for a database
     *
     * @param database The target database
     * @return A snapshot of the metrics for a database
     */
    XSPReply getMetricSnapshot(String database) {
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
}
