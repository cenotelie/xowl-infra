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

package org.xowl.infra.server.api.remote;

import org.xowl.hime.redist.ASTNode;
import org.xowl.infra.server.api.*;
import org.xowl.infra.server.api.base.*;
import org.xowl.infra.server.xsp.*;
import org.xowl.infra.store.AbstractRepository;
import org.xowl.infra.store.EntailmentRegime;
import org.xowl.infra.store.URIUtils;
import org.xowl.infra.store.http.HttpConnection;
import org.xowl.infra.store.http.HttpConstants;
import org.xowl.infra.store.rdf.Quad;
import org.xowl.infra.store.sparql.Command;
import org.xowl.infra.store.sparql.Result;
import org.xowl.infra.store.storage.StoreStatistics;
import org.xowl.infra.store.writers.NQuadsSerializer;
import org.xowl.infra.utils.Files;
import org.xowl.infra.utils.collections.SingleIterator;
import org.xowl.infra.utils.logging.BufferedLogger;
import org.xowl.infra.utils.logging.Logging;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Collection;
import java.util.List;
import java.util.zip.GZIPOutputStream;

/**
 * Implements the server API for a remote xOWL Server
 * This object maintains the connection to the remote server along with the required parameters.
 *
 * @author Laurent Wouters
 */
public class RemoteServer implements XOWLServer, XOWLFactory {
    /**
     * The remote endpoint
     */
    private final String endpoint;
    /**
     * The connection to the remote host, if any
     */
    private HttpConnection connection;
    /**
     * The login for the current user
     */
    private String currentUser;

    /**
     * Creates a connection to a remote xOWL server
     *
     * @param endpoint The API endpoint of the remote xOWL server
     */
    public RemoteServer(String endpoint) {
        String result = endpoint;
        if (result == null || result.isEmpty()) {
            result = "https://localhost:3443/api";
        } else if (result.endsWith("/")) {
            result = result.substring(0, result.length() - 1);
        }
        this.endpoint = result;
    }

    /**
     * Cleanup any open session
     */
    public void logout() {
        connection = null;
        currentUser = null;
    }

    @Override
    public XSPReply login(String login, String password) {
        connection = new HttpConnection(endpoint, login, password);
        XSPReply reply = XSPReplyUtils.fromHttpResponse(connection.request("/whoami", "GET", HttpConstants.MIME_TEXT_PLAIN + ", " + HttpConstants.MIME_JSON), this);
        if (!reply.isSuccess()) {
            connection = null;
            currentUser = null;
        } else {
            currentUser = ((XSPReplyResult<XOWLUser>) reply).getData().getName();
        }
        return reply;
    }

    @Override
    public XSPReply serverShutdown() {
        if (connection == null)
            return XSPReplyNetworkError.instance();
        return XSPReplyUtils.fromHttpResponse(connection.request("/server?action=shutdown", "POST", HttpConstants.MIME_TEXT_PLAIN + ", " + HttpConstants.MIME_JSON), this);
    }

    @Override
    public XSPReply serverRestart() {
        if (connection == null)
            return XSPReplyNetworkError.instance();
        return XSPReplyUtils.fromHttpResponse(connection.request("/server?action=restart", "POST", HttpConstants.MIME_TEXT_PLAIN + ", " + HttpConstants.MIME_JSON), this);
    }

    @Override
    public XSPReply getUser(String login) {
        if (connection == null)
            return XSPReplyNetworkError.instance();
        return XSPReplyUtils.fromHttpResponse(connection.request("/user/" + URIUtils.encodeComponent(login), "GET", HttpConstants.MIME_TEXT_PLAIN + ", " + HttpConstants.MIME_JSON), this);
    }

    @Override
    public XSPReply getUsers() {
        if (connection == null)
            return XSPReplyNetworkError.instance();
        return XSPReplyUtils.fromHttpResponse(connection.request("/users", "GET", HttpConstants.MIME_TEXT_PLAIN + ", " + HttpConstants.MIME_JSON), this);
    }

    @Override
    public XSPReply createUser(String login, String password) {
        if (connection == null)
            return XSPReplyNetworkError.instance();
        return XSPReplyUtils.fromHttpResponse(connection.request("/user/" + URIUtils.encodeComponent(login), "PUT", password, XSPReply.MIME_XSP_COMMAND, HttpConstants.MIME_TEXT_PLAIN + ", " + HttpConstants.MIME_JSON), this);
    }

    @Override
    public XSPReply deleteUser(XOWLUser toDelete) {
        if (connection == null)
            return XSPReplyNetworkError.instance();
        return XSPReplyUtils.fromHttpResponse(connection.request("/user/" + URIUtils.encodeComponent(toDelete.getName()), "DELETE", HttpConstants.MIME_TEXT_PLAIN + ", " + HttpConstants.MIME_JSON), this);
    }

    @Override
    public XSPReply changePassword(String password) {
        if (connection == null)
            return XSPReplyNetworkError.instance();
        return XSPReplyUtils.fromHttpResponse(connection.request("/user/" + URIUtils.encodeComponent(currentUser), "PUT", password, XSPReply.MIME_XSP_COMMAND, HttpConstants.MIME_TEXT_PLAIN + ", " + HttpConstants.MIME_JSON), this);
    }

    @Override
    public XSPReply resetPassword(XOWLUser target, String password) {
        if (connection == null)
            return XSPReplyNetworkError.instance();
        return XSPReplyUtils.fromHttpResponse(connection.request("/user/" + URIUtils.encodeComponent(target.getName()), "PUT", password, XSPReply.MIME_XSP_COMMAND, HttpConstants.MIME_TEXT_PLAIN + ", " + HttpConstants.MIME_JSON), this);
    }

    @Override
    public XSPReply getPrivileges(XOWLUser user) {
        if (connection == null)
            return XSPReplyNetworkError.instance();
        return XSPReplyUtils.fromHttpResponse(connection.request("/user/" + URIUtils.encodeComponent(user.getName()) + "/privileges", "GET", HttpConstants.MIME_TEXT_PLAIN + ", " + HttpConstants.MIME_JSON), this);
    }

    @Override
    public XSPReply grantServerAdmin(XOWLUser target) {
        if (connection == null)
            return XSPReplyNetworkError.instance();
        return XSPReplyUtils.fromHttpResponse(connection.request("/user/" + URIUtils.encodeComponent(target.getName()) + "/privileges?action=grant&server=&access=ADMIN", "POST", HttpConstants.MIME_TEXT_PLAIN + ", " + HttpConstants.MIME_JSON), this);
    }

    @Override
    public XSPReply revokeServerAdmin(XOWLUser target) {
        if (connection == null)
            return XSPReplyNetworkError.instance();
        return XSPReplyUtils.fromHttpResponse(connection.request("/user/" + URIUtils.encodeComponent(target.getName()) + "/privileges?action=revoke&server=&access=ADMIN", "POST", HttpConstants.MIME_TEXT_PLAIN + ", " + HttpConstants.MIME_JSON), this);
    }

    @Override
    public XSPReply grantDB(XOWLUser user, XOWLDatabase database, int privilege) {
        if (connection == null)
            return XSPReplyNetworkError.instance();
        String access = privilege == XOWLPrivilege.ADMIN ? "ADMIN" : (privilege == XOWLPrivilege.WRITE ? "WRITE" : (privilege == XOWLPrivilege.READ ? "READ" : "NONE"));
        return XSPReplyUtils.fromHttpResponse(connection.request("/user/" + URIUtils.encodeComponent(user.getName()) + "/privileges?action=grant&db=" + URIUtils.encodeComponent(database.getName()) + "&access=" + access, "POST", HttpConstants.MIME_TEXT_PLAIN + ", " + HttpConstants.MIME_JSON), this);
    }

    @Override
    public XSPReply revokeDB(XOWLUser user, XOWLDatabase database, int privilege) {
        if (connection == null)
            return XSPReplyNetworkError.instance();
        String access = privilege == XOWLPrivilege.ADMIN ? "ADMIN" : (privilege == XOWLPrivilege.WRITE ? "WRITE" : (privilege == XOWLPrivilege.READ ? "READ" : "NONE"));
        return XSPReplyUtils.fromHttpResponse(connection.request("/user/" + URIUtils.encodeComponent(user.getName()) + "/privileges?action=revoke&db=" + URIUtils.encodeComponent(database.getName()) + "&access=" + access, "POST", HttpConstants.MIME_TEXT_PLAIN + ", " + HttpConstants.MIME_JSON), this);
    }

    @Override
    public XSPReply getDatabase(String name) {
        if (connection == null)
            return XSPReplyNetworkError.instance();
        return XSPReplyUtils.fromHttpResponse(connection.request("/db/" + URIUtils.encodeComponent(name), "GET", HttpConstants.MIME_TEXT_PLAIN + ", " + HttpConstants.MIME_JSON), this);
    }

    @Override
    public XSPReply getDatabases() {
        if (connection == null)
            return XSPReplyNetworkError.instance();
        return XSPReplyUtils.fromHttpResponse(connection.request("/databases", "GET", HttpConstants.MIME_TEXT_PLAIN + ", " + HttpConstants.MIME_JSON), this);
    }

    @Override
    public XSPReply createDatabase(String name) {
        if (connection == null)
            return XSPReplyNetworkError.instance();
        return XSPReplyUtils.fromHttpResponse(connection.request("/db/" + URIUtils.encodeComponent(name), "PUT", HttpConstants.MIME_TEXT_PLAIN + ", " + HttpConstants.MIME_JSON), this);
    }

    @Override
    public XSPReply dropDatabase(XOWLDatabase database) {
        if (connection == null)
            return XSPReplyNetworkError.instance();
        return XSPReplyUtils.fromHttpResponse(connection.request("/db/" + URIUtils.encodeComponent(database.getName()), "DELETE", HttpConstants.MIME_TEXT_PLAIN + ", " + HttpConstants.MIME_JSON), this);
    }

    @Override
    public XSPReply getPrivileges(XOWLDatabase database) {
        if (connection == null)
            return XSPReplyNetworkError.instance();
        return XSPReplyUtils.fromHttpResponse(connection.request("/db/" + URIUtils.encodeComponent(database.getName()) + "/privileges", "GET", HttpConstants.MIME_TEXT_PLAIN + ", " + HttpConstants.MIME_JSON), this);
    }

    @Override
    public void onShutdown() {
        // kill the HTTP connection
        connection.close();
        connection = null;
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
        if (connection == null)
            return XSPReplyNetworkError.instance();
        if (defaultIRIs != null && !defaultIRIs.isEmpty())
            return new XSPReplyFailure("The specification of default graphs is not supported");
        if (namedIRIs != null && !namedIRIs.isEmpty())
            return new XSPReplyFailure("The specification of named graphs is not supported");
        return XSPReplyUtils.fromHttpResponse(connection.request("/db/" + URIUtils.encodeComponent(database) + "/sparql", "POST", sparql, Command.MIME_SPARQL_QUERY, AbstractRepository.SYNTAX_NQUADS + ", " + Result.SYNTAX_JSON), this);
    }

    /**
     * Gets the entailment regime
     *
     * @param database The target database
     * @return The protocol reply
     */
    XSPReply getEntailmentRegime(String database) {
        if (connection == null)
            return XSPReplyNetworkError.instance();
        return XSPReplyUtils.fromHttpResponse(connection.request("/db/" + URIUtils.encodeComponent(database) + "/entailment", "GET", HttpConstants.MIME_TEXT_PLAIN + ", " + HttpConstants.MIME_JSON), this);
    }

    /**
     * Sets the entailment regime
     *
     * @param database The target database
     * @param regime   The entailment regime
     * @return The protocol reply
     */
    XSPReply setEntailmentRegime(String database, EntailmentRegime regime) {
        if (connection == null)
            return XSPReplyNetworkError.instance();
        return XSPReplyUtils.fromHttpResponse(connection.request("/db/" + URIUtils.encodeComponent(database) + "/entailment", "PUT", regime.toString(), XSPReply.MIME_XSP_COMMAND, HttpConstants.MIME_TEXT_PLAIN + ", " + HttpConstants.MIME_JSON), this);
    }

    /**
     * Gets the rule for the specified name
     *
     * @param database The target database
     * @param name     The name (IRI) of a rule
     * @return The protocol reply
     */
    XSPReply getRule(String database, String name) {
        if (connection == null)
            return XSPReplyNetworkError.instance();
        return XSPReplyUtils.fromHttpResponse(connection.request("/db/" + URIUtils.encodeComponent(database) + "/rules?id=" + URIUtils.encodeComponent(name), "GET", HttpConstants.MIME_TEXT_PLAIN + ", " + HttpConstants.MIME_JSON), this);
    }

    /**
     * Gets the rules in this database
     *
     * @param database The target database
     * @return The protocol reply
     */
    XSPReply getRules(String database) {
        if (connection == null)
            return XSPReplyNetworkError.instance();
        return XSPReplyUtils.fromHttpResponse(connection.request("/db/" + URIUtils.encodeComponent(database) + "/rules", "GET", HttpConstants.MIME_TEXT_PLAIN + ", " + HttpConstants.MIME_JSON), this);
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
        if (connection == null)
            return XSPReplyNetworkError.instance();
        return XSPReplyUtils.fromHttpResponse(connection.request("/db/" + URIUtils.encodeComponent(database) + "/rules" + (activate ? "?active=" : ""), "PUT", content, XSPReply.MIME_XSP_COMMAND, HttpConstants.MIME_TEXT_PLAIN + ", " + HttpConstants.MIME_JSON), this);
    }

    /**
     * Removes a rule from this database
     *
     * @param database The target database
     * @param rule     The rule to remove
     * @return The protocol reply
     */
    XSPReply removeRule(String database, XOWLRule rule) {
        if (connection == null)
            return XSPReplyNetworkError.instance();
        return XSPReplyUtils.fromHttpResponse(connection.request("/db/" + URIUtils.encodeComponent(database) + "/rules?id=" + URIUtils.encodeComponent(rule.getName()), "DELETE", HttpConstants.MIME_TEXT_PLAIN + ", " + HttpConstants.MIME_JSON), this);
    }

    /**
     * Activates an existing rule in this database
     *
     * @param database The target database
     * @param rule     The rule to activate
     * @return The protocol reply
     */
    XSPReply activateRule(String database, XOWLRule rule) {
        if (connection == null)
            return XSPReplyNetworkError.instance();
        return XSPReplyUtils.fromHttpResponse(connection.request("/db/" + URIUtils.encodeComponent(database) + "/rules?id=" + URIUtils.encodeComponent(rule.getName()) + "&action=activate", "POST", HttpConstants.MIME_TEXT_PLAIN + ", " + HttpConstants.MIME_JSON), this);
    }

    /**
     * Deactivates an existing rule in this database
     *
     * @param database The target database
     * @param rule     The rule to deactivate
     * @return The protocol reply
     */
    XSPReply deactivateRule(String database, XOWLRule rule) {
        if (connection == null)
            return XSPReplyNetworkError.instance();
        return XSPReplyUtils.fromHttpResponse(connection.request("/db/" + URIUtils.encodeComponent(database) + "/rules?id=" + URIUtils.encodeComponent(rule.getName()) + "&action=deactivate", "POST", HttpConstants.MIME_TEXT_PLAIN + ", " + HttpConstants.MIME_JSON), this);
    }

    /**
     * Gets the matching status of a rule in this database
     *
     * @param database The target database
     * @param rule     The rule to inquire
     * @return The protocol reply
     */
    XSPReply getRuleStatus(String database, XOWLRule rule) {
        if (connection == null)
            return XSPReplyNetworkError.instance();
        return XSPReplyUtils.fromHttpResponse(connection.request("/db/" + URIUtils.encodeComponent(database) + "/rules?id=" + URIUtils.encodeComponent(rule.getName()) + "&status=", "GET", HttpConstants.MIME_TEXT_PLAIN + ", " + HttpConstants.MIME_JSON), this);
    }

    /**
     * Gets the explanation for a quad in this database
     *
     * @param database The target database
     * @param quad     The quad serialization
     * @return The protocol reply
     */
    XSPReply getQuadExplanation(String database, Quad quad) {
        if (connection == null)
            return XSPReplyNetworkError.instance();
        StringWriter writer = new StringWriter();
        NQuadsSerializer serializer = new NQuadsSerializer(writer);
        serializer.serialize(Logging.getDefault(), new SingleIterator<>(quad));
        return XSPReplyUtils.fromHttpResponse(connection.request("/db/" + URIUtils.encodeComponent(database) + "/explain?quad=" + URIUtils.encodeComponent(writer.toString()) + "&status=", "GET", HttpConstants.MIME_TEXT_PLAIN + ", " + HttpConstants.MIME_JSON), this);
    }

    /**
     * Gets the stored procedure for the specified name
     *
     * @param database The target database
     * @param iri      The name (IRI) of a procedure
     * @return The protocol reply
     */
    XSPReply getStoreProcedure(String database, String iri) {
        if (connection == null)
            return XSPReplyNetworkError.instance();
        return XSPReplyUtils.fromHttpResponse(connection.request("/db/" + URIUtils.encodeComponent(database) + "/procedures?id=" + URIUtils.encodeComponent(iri), "GET", HttpConstants.MIME_TEXT_PLAIN + ", " + HttpConstants.MIME_JSON), this);
    }

    /**
     * Gets the stored procedures in a database
     *
     * @param database The target database
     * @return The protocol reply
     */
    XSPReply getStoredProcedures(String database) {
        if (connection == null)
            return XSPReplyNetworkError.instance();
        return XSPReplyUtils.fromHttpResponse(connection.request("/db/" + URIUtils.encodeComponent(database) + "/procedures", "GET", HttpConstants.MIME_TEXT_PLAIN + ", " + HttpConstants.MIME_JSON), this);
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
        if (connection == null)
            return XSPReplyNetworkError.instance();
        BaseStoredProcedure procedure = new BaseStoredProcedure(iri, sparql, parameters, null);
        return XSPReplyUtils.fromHttpResponse(connection.request("/db/" + URIUtils.encodeComponent(database) + "/procedures", "PUT", procedure.serializedJSON(), HttpConstants.MIME_JSON, HttpConstants.MIME_TEXT_PLAIN + ", " + HttpConstants.MIME_JSON), this);
    }

    /**
     * Removes a stored procedure from a database
     *
     * @param database  The target database
     * @param procedure The procedure to remove
     * @return The protocol reply
     */
    XSPReply removeStoredProcedure(String database, XOWLStoredProcedure procedure) {
        if (connection == null)
            return XSPReplyNetworkError.instance();
        return XSPReplyUtils.fromHttpResponse(connection.request("/db/" + URIUtils.encodeComponent(database) + "/rules?id=" + URIUtils.encodeComponent(procedure.getName()), "DELETE", HttpConstants.MIME_TEXT_PLAIN + ", " + HttpConstants.MIME_JSON), this);
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
        if (connection == null)
            return XSPReplyNetworkError.instance();
        return XSPReplyUtils.fromHttpResponse(connection.request("/db/" + URIUtils.encodeComponent(database) + "/rules?id=" + URIUtils.encodeComponent(procedure.getName()), "POST", context.serializedJSON(), HttpConstants.MIME_JSON, HttpConstants.MIME_TEXT_PLAIN + ", " + HttpConstants.MIME_JSON), this);
    }

    /**
     * Executes a stored procedure
     *
     * @param database  The target database
     * @param procedure The identifier of the procedure to execute
     * @param context   The definition of the execution context to use
     * @return The protocol reply
     */
    XSPReply executeStoredProcedure(String database, String procedure, String context) {
        if (connection == null)
            return XSPReplyNetworkError.instance();
        return XSPReplyUtils.fromHttpResponse(connection.request("/db/" + URIUtils.encodeComponent(database) + "/rules?id=" + URIUtils.encodeComponent(procedure), "POST", context, HttpConstants.MIME_JSON, HttpConstants.MIME_TEXT_PLAIN + ", " + HttpConstants.MIME_JSON), this);
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
        if (connection == null)
            return XSPReplyNetworkError.instance();
        try {
            ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
            GZIPOutputStream compressor = new GZIPOutputStream(byteBuffer);
            byte[] input = content.getBytes(Files.CHARSET);
            compressor.write(input, 0, input.length);
            compressor.finish();
            input = byteBuffer.toByteArray();
            return XSPReplyUtils.fromHttpResponse(connection.request("/db/" + URIUtils.encodeComponent(database), "POST", input, syntax, true, HttpConstants.MIME_TEXT_PLAIN + ", " + HttpConstants.MIME_JSON), this);
        } catch (IOException exception) {
            return new XSPReplyFailure(exception.getMessage());
        }
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
        return upload(database, writer.toString(), AbstractRepository.SYNTAX_NQUADS);
    }

    /**
     * Gets the statistics for a database
     *
     * @param database The target database
     * @return The statistics for the database
     */
    XSPReply getStatistics(String database) {
        if (connection == null)
            return XSPReplyNetworkError.instance();
        return XSPReplyUtils.fromHttpResponse(connection.request("/db/" + URIUtils.encodeComponent(database) + "/statistics", "GET", HttpConstants.MIME_JSON), this);
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
        } else if (StoreStatistics.class.getCanonicalName().equals(type)) {
            return new StoreStatistics(definition);
        }
        return null;
    }
}
