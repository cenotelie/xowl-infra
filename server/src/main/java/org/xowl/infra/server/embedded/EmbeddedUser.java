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
import org.xowl.infra.server.impl.ControllerServer;
import org.xowl.infra.server.impl.ControllerUser;
import org.xowl.infra.server.impl.UserImpl;

/**
 * Represents a user when embedded within another application
 * A direct access to this user bypasses the access control usually handled by the parent server controller.
 *
 * @author Laurent Wouters
 */
class EmbeddedUser extends UserImpl {
    /**
     * Initializes this user
     *
     * @param serverController The parent server controller
     * @param userController   The associated user controller
     */
    public EmbeddedUser(ControllerServer serverController, ControllerUser userController) {
        super(serverController, userController);
    }

    @Override
    public Reply updatePassword(String password) {
        return serverController.updatePassword(this, identifier, password);
    }

    @Override
    public Reply getPrivileges() {
        return serverController.getUserPrivileges(this, identifier);
    }
}
