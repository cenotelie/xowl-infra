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

import org.xowl.infra.server.impl.UserImpl;
import org.xowl.infra.server.xsp.XSPReply;
import org.xowl.infra.store.ProxyObject;

/**
 * Represents a user when embedded within another application
 * A direct access to this user bypasses the access control usually handled by the parent server controller.
 *
 * @author Laurent Wouters
 */
class EmbeddedUser extends UserImpl {
    /**
     * The parent server
     */
    private final EmbeddedServer server;

    /**
     * Initializes this user
     *
     * @param proxy  The proxy object representing this user in the administration database
     * @param server The parent server
     */
    public EmbeddedUser(ProxyObject proxy, EmbeddedServer server) {
        super(proxy);
        this.server = server;
    }

    @Override
    public XSPReply updatePassword(String password) {
        return server.userUpdatePassword(this, password);
    }

    @Override
    public XSPReply getPrivileges() {
        return server.userGetPrivileges(name);
    }
}
