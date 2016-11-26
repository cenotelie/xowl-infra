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

import org.xowl.infra.store.ProxyObject;

/**
 * Implements a controller for a user
 *
 * @author Laurent Wouters
 */
public class ControllerUser {
    /**
     * The proxy object representing this user in the administration database
     */
    protected final ProxyObject proxy;
    /**
     * The name of the user
     */
    private final String name;

    /**
     * Initializes this controller
     *
     * @param proxy The proxy object representing this user in the administration database
     */
    public ControllerUser(ProxyObject proxy) {
        this.proxy = proxy;
        this.name = (String) proxy.getDataValue(Schema.ADMIN_NAME);
    }

    /**
     * Gets the name of the user
     *
     * @return The name of the user
     */
    public String getName() {
        return name;
    }
}
