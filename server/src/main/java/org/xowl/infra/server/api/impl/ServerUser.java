/*******************************************************************************
 * Copyright (c) 2015 Laurent Wouters
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Lesser General
 * Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 * <p>
 * Contributors:
 * Laurent Wouters - lwouters@xowl.org
 ******************************************************************************/

package org.xowl.infra.server.api.impl;

import org.xowl.infra.server.api.base.BaseUser;
import org.xowl.infra.store.ProxyObject;

/**
 * Represents a user on this server
 *
 * @author Laurent Wouters
 */
public class ServerUser extends BaseUser {
    /**
     * The proxy object representing this user
     */
    private final ProxyObject proxy;

    /**
     * Gets the proxy object representing this user
     *
     * @return The proxy object representing this user
     */
    public ProxyObject getProxy() {
        return proxy;
    }

    /**
     * Initializes this user
     *
     * @param proxy The proxy object representing this user
     */
    public ServerUser(ProxyObject proxy) {
        super((String) proxy.getDataValue(Schema.ADMIN_NAME));
        this.proxy = proxy;
    }
}
