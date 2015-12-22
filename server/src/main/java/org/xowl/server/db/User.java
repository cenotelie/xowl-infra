/*******************************************************************************
 * Copyright (c) 2015 Laurent Wouters
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
 *
 * Contributors:
 *     Laurent Wouters - lwouters@xowl.org
 ******************************************************************************/

package org.xowl.server.db;

import org.xowl.store.IOUtils;
import org.xowl.store.ProxyObject;
import org.xowl.store.Serializable;

/**
 * Represents a user on this server
 *
 * @author Laurent Wouters
 */
public class User implements Serializable {
    /**
     * The proxy object representing this user
     */
    final ProxyObject proxy;
    /**
     * The cached name
     */
    private String name;

    /**
     * Initializes this user
     *
     * @param proxy The proxy object representing this user
     */
    public User(ProxyObject proxy) {
        this.proxy = proxy;
    }

    /**
     * Gets the name of this user
     *
     * @return The name of this user
     */
    public String getName() {
        if (name != null)
            return name;
        name = (String) proxy.getDataValue(Schema.ADMIN_NAME);
        return name;
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public String serializedString() {
        return getName();
    }

    @Override
    public String serializedJSON() {
        return "{\"type\": \"" + IOUtils.escapeStringJSON(User.class.getCanonicalName()) + "\", \"name\": \"" + IOUtils.escapeStringJSON(getName()) + "\"}";
    }
}
