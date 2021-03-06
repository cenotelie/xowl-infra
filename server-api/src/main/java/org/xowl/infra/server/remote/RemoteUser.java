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

import fr.cenotelie.commons.utils.api.Reply;
import fr.cenotelie.hime.redist.ASTNode;
import org.xowl.infra.server.base.BaseUser;

/**
 * Represents a user on a remote xOWL server
 *
 * @author Laurent Wouters
 */
class RemoteUser extends BaseUser {
    /**
     * The parent server
     */
    private final RemoteServer server;

    /**
     * Initializes this user
     *
     * @param server The parent server
     * @param name   The user's name
     */
    public RemoteUser(RemoteServer server, String name) {
        super(name);
        this.server = server;
    }

    /**
     * Initializes this user
     *
     * @param server The parent server
     * @param root   The user's definition
     */
    public RemoteUser(RemoteServer server, ASTNode root) {
        super(root);
        this.server = server;
    }

    @Override
    public Reply updatePassword(String password) {
        return server.userUpdatePassword(identifier, password);
    }

    @Override
    public Reply getPrivileges() {
        return server.userGetPrivileges(identifier);
    }
}
