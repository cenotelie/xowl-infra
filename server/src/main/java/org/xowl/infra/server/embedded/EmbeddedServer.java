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

package org.xowl.infra.server.embedded;

import org.xowl.infra.server.ServerConfiguration;
import org.xowl.infra.server.api.XOWLDatabase;
import org.xowl.infra.server.api.XOWLServer;
import org.xowl.infra.server.api.XOWLUser;
import org.xowl.infra.server.impl.UserImpl;
import org.xowl.infra.server.xsp.XSPReply;
import org.xowl.infra.server.xsp.XSPReplyFailure;
import org.xowl.infra.server.xsp.XSPReplyResult;
import org.xowl.infra.server.xsp.XSPReplyUnsupported;
import org.xowl.infra.store.ProxyObject;
import org.xowl.infra.utils.logging.Logger;

import java.io.Closeable;
import java.io.IOException;

/**
 * Implements an embedded server with persisted databases
 *
 * @author Laurent Wouters
 */
public class EmbeddedServer implements XOWLServer, Closeable {
    /**
     * The server controller
     */
    private final EmbeddedController controller;
    /**
     * The client user to use
     */
    private final UserImpl admin;

    /**
     * Initializes this embedded server
     *
     * @param logger        The logger for this server
     * @param configuration The server configuration
     * @throws Exception When the location cannot be accessed
     */
    public EmbeddedServer(Logger logger, ServerConfiguration configuration) throws Exception {
        this.controller = new EmbeddedController(logger, configuration) {
            @Override
            protected UserImpl newUser(ProxyObject proxy) {
                return new EmbeddedUser(proxy, EmbeddedServer.this);
            }
        };
        this.admin = controller.getPrincipal(configuration.getAdminDefaultUser());
    }

    @Override
    public boolean isLoggedIn() {
        return true;
    }

    @Override
    public XOWLUser getLoggedInUser() {
        return admin;
    }

    @Override
    public XSPReply login(String login, String password) {
        return new XSPReplyResult<>(admin);
    }

    @Override
    public XSPReply logout() {
        return XSPReplyFailure.instance();
    }

    @Override
    public XSPReply serverShutdown() {
        return XSPReplyUnsupported.instance();
    }

    @Override
    public XSPReply serverRestart() {
        return XSPReplyUnsupported.instance();
    }

    @Override
    public XSPReply getUser(String login) {
        return controller.getUser(admin, login);
    }

    @Override
    public XSPReply getUsers() {
        return controller.getUsers(admin);
    }

    @Override
    public XSPReply createUser(String login, String password) {
        return controller.createUser(admin, login, password);
    }

    @Override
    public XSPReply deleteUser(XOWLUser toDelete) {
        return controller.deleteUser(admin, toDelete.getName());
    }

    @Override
    public XSPReply deleteUser(String toDelete) {
        return controller.deleteUser(admin, toDelete);
    }

    @Override
    public XSPReply serverGrantAdmin(XOWLUser target) {
        return controller.serverGrantAdmin(admin, target.getName());
    }

    @Override
    public XSPReply serverGrantAdmin(String target) {
        return controller.serverGrantAdmin(admin, target);
    }

    @Override
    public XSPReply serverRevokeAdmin(XOWLUser target) {
        return controller.serverRevokeAdmin(admin, target.getName());
    }

    @Override
    public XSPReply serverRevokeAdmin(String target) {
        return controller.serverRevokeAdmin(admin, target);
    }

    @Override
    public XSPReply getDatabase(String name) {
        return controller.getDatabase(admin, name);
    }

    @Override
    public XSPReply getDatabases() {
        return controller.getDatabases(admin);
    }

    @Override
    public XSPReply createDatabase(String name) {
        return controller.createDatabase(admin, name);
    }

    @Override
    public XSPReply dropDatabase(XOWLDatabase database) {
        return controller.dropDatabase(admin, database.getName());
    }

    @Override
    public XSPReply dropDatabase(String database) {
        return controller.dropDatabase(admin, database);
    }

    @Override
    public void close() throws IOException {
        controller.close();
    }

    /**
     * Updates the password of the user
     *
     * @param user     The target user
     * @param password The new password
     * @return The protocol reply
     */
    XSPReply userUpdatePassword(UserImpl user, String password) {
        return controller.changePassword(user, password);
    }

    /**
     * Gets the privileges assigned to a user
     *
     * @param user The target user
     * @return The protocol reply
     */
    XSPReply userGetPrivileges(String user) {
        return controller.getUserPrivileges(admin, user);
    }
}
