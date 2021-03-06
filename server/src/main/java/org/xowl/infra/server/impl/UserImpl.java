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

package org.xowl.infra.server.impl;

import org.xowl.infra.server.base.BaseUser;

/**
 * Implementation of a XOWL User
 *
 * @author Laurent Wouters
 */
public class UserImpl extends BaseUser {
    /**
     * The parent server controller
     */
    protected final ControllerServer serverController;
    /**
     * The associated user controller
     */
    protected final ControllerUser userController;

    /**
     * Initializes this user
     *
     * @param serverController The parent server controller
     * @param userController   The associated user controller
     */
    public UserImpl(ControllerServer serverController, ControllerUser userController) {
        super(userController.getName());
        this.serverController = serverController;
        this.userController = userController;
    }
}
