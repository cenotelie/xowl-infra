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

import fr.cenotelie.commons.utils.api.Reply;
import fr.cenotelie.commons.utils.api.ReplySuccess;
import fr.cenotelie.commons.utils.api.ReplyUnsupported;
import fr.cenotelie.commons.utils.logging.Logger;
import org.xowl.infra.server.ServerConfiguration;
import org.xowl.infra.server.api.XOWLDatabase;
import org.xowl.infra.server.api.XOWLDatabaseConfiguration;
import org.xowl.infra.server.api.XOWLServer;
import org.xowl.infra.server.api.XOWLUser;
import org.xowl.infra.server.impl.*;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetAddress;

/**
 * Implements an embedded server with persisted databases
 *
 * @author Laurent Wouters
 */
public class EmbeddedServer implements XOWLServer, Closeable {
    /**
     * The server controller
     */
    private final ControllerServer controller;
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
        this.controller = new ControllerServer(logger, configuration) {
            @Override
            protected DatabaseImpl newDB(ControllerDatabase dbController) {
                return new EmbeddedDatabase(logger, this, dbController) {
                    @Override
                    protected UserImpl getAdminUser() {
                        return admin;
                    }
                };
            }

            @Override
            protected UserImpl newUser(ControllerUser userController) {
                return new EmbeddedUser(this, userController);
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
    public Reply login(String login, String password) {
        Reply reply = controller.login(InetAddress.getLoopbackAddress(), login, password);
        if (reply.isSuccess())
            return ReplySuccess.instance();
        return reply;
    }

    @Override
    public Reply logout() {
        return ReplyUnsupported.instance();
    }

    @Override
    public Reply serverShutdown() {
        return ReplyUnsupported.instance();
    }

    @Override
    public Reply serverRestart() {
        return ReplyUnsupported.instance();
    }

    @Override
    public Reply getUser(String login) {
        return controller.getUser(admin, login);
    }

    @Override
    public Reply getUsers() {
        return controller.getUsers(admin);
    }

    @Override
    public Reply createUser(String login, String password) {
        return controller.createUser(admin, login, password);
    }

    @Override
    public Reply deleteUser(XOWLUser toDelete) {
        return controller.deleteUser(admin, toDelete.getIdentifier());
    }

    @Override
    public Reply deleteUser(String toDelete) {
        return controller.deleteUser(admin, toDelete);
    }

    @Override
    public Reply serverGrantAdmin(XOWLUser target) {
        return controller.serverGrantAdmin(admin, target.getIdentifier());
    }

    @Override
    public Reply serverGrantAdmin(String target) {
        return controller.serverGrantAdmin(admin, target);
    }

    @Override
    public Reply serverRevokeAdmin(XOWLUser target) {
        return controller.serverRevokeAdmin(admin, target.getIdentifier());
    }

    @Override
    public Reply serverRevokeAdmin(String target) {
        return controller.serverRevokeAdmin(admin, target);
    }

    @Override
    public Reply getDatabase(String identifier) {
        return controller.getDatabase(admin, identifier);
    }

    @Override
    public Reply getDatabases() {
        return controller.getDatabases(admin);
    }

    @Override
    public Reply createDatabase(String identifier, XOWLDatabaseConfiguration configuration) {
        return controller.createDatabase(admin, identifier, configuration);
    }

    @Override
    public Reply dropDatabase(XOWLDatabase database) {
        return controller.dropDatabase(admin, database.getIdentifier());
    }

    @Override
    public Reply dropDatabase(String database) {
        return controller.dropDatabase(admin, database);
    }

    @Override
    public void close() throws IOException {
        controller.close();
    }
}
