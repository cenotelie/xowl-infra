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

package org.xowl.server.api;

import org.xowl.store.Serializable;

import java.util.Collection;

/**
 * Represents the privileges on a database
 *
 * @author Laurent Wouters
 */
public interface XOWLDatabasePrivileges extends Serializable {
    /**
     * Gets the relevant users
     *
     * @return The relevant users
     */
    Collection<XOWLUser> getUsers();

    /**
     * Gets the privilege associated to the specified user
     *
     * @param user A user
     * @return The associated privilege
     */
    int getPrivilegeFor(XOWLUser user);
}
