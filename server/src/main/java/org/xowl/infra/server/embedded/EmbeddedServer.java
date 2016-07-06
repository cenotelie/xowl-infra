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

import org.xowl.infra.server.api.XOWLDatabase;
import org.xowl.infra.server.api.XOWLServer;
import org.xowl.infra.server.api.XOWLUser;
import org.xowl.infra.server.base.ServerConfiguration;
import org.xowl.infra.server.base.ServerController;
import org.xowl.infra.server.base.ServerUser;
import org.xowl.infra.server.xsp.XSPReply;
import org.xowl.infra.server.xsp.XSPReplyUnsupported;
import org.xowl.infra.utils.logging.Logging;

import java.io.IOException;

/**
 * Implements an embedded server with persisted databases
 *
 * @author Laurent Wouters
 */
public class EmbeddedServer implements XOWLServer {
    /**
     * The server controller
     */
    private final ServerController controller;
    /**
     * The client user to use
     */
    private final ServerUser admin;

    /**
     * Initializes this embedded server
     *
     * @param configuration The server configuration
     */
    public EmbeddedServer(ServerConfiguration configuration) throws IOException {
        this.controller = new ServerController(configuration);
        this.admin = controller.getPrincipal(configuration.getAdminDefaultUser());
    }

    @Override
    public XSPReply login(String login, String password) {
        return XSPReplyUnsupported.instance();
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
    public XSPReply changePassword(String password) {
        return controller.changePassword(admin, password);
    }

    @Override
    public XSPReply resetPassword(XOWLUser target, String password) {
        return controller.resetPassword(admin, target.getName(), password);
    }

    @Override
    public XSPReply getPrivileges(XOWLUser user) {
        return controller.getPrivilegesUser(admin, user.getName());
    }

    @Override
    public XSPReply grantServerAdmin(XOWLUser target) {
        return controller.grantServerAdmin(admin, target.getName());
    }

    @Override
    public XSPReply revokeServerAdmin(XOWLUser target) {
        return controller.revokeServerAdmin(admin, target.getName());
    }

    @Override
    public XSPReply grantDB(XOWLUser user, XOWLDatabase database, int privilege) {
        return controller.grantDB(admin, user.getName(), database.getName(), privilege);
    }

    @Override
    public XSPReply revokeDB(XOWLUser user, XOWLDatabase database, int privilege) {
        return controller.revokeDB(admin, user.getName(), database.getName(), privilege);
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
    public XSPReply getPrivileges(XOWLDatabase database) {
        return controller.getPrivilegesDB(admin, database.getName());
    }

    @Override
    public void onShutdown() {
        try {
            controller.close();
        } catch (IOException exception) {
            Logging.getDefault().error(exception);
        }
    }
}
