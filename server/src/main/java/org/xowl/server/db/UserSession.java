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

import org.xowl.store.ProxyObject;

/**
 * Represents a user session on this server
 *
 * @author Laurent Wouters
 */
public class UserSession {
    /**
     * The proxy object representing this user
     */
    private final ProxyObject proxy;
    /**
     * Token for the session
     */
    private final String token;

    /**
     * Initializes this user
     *
     * @param proxy The proxy object representing this user
     * @param token Token for the session
     */
    public UserSession(ProxyObject proxy, String token) {
        this.proxy = proxy;
        this.token = token;
    }
}
