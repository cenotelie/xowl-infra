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

package org.xowl.infra.server.api;

import fr.cenotelie.commons.utils.Serializable;

import java.util.Collection;

/**
 * Represents the privileges for a user
 *
 * @author Laurent Wouters
 */
public interface XOWLUserPrivileges extends Serializable {
    /**
     * Gets the relevant databases
     *
     * @return The relevant databases
     */
    Collection<XOWLDatabase> getDatabases();

    /**
     * Gets the privilege associated to the specified database
     *
     * @param database A database
     * @return The associated privilege
     */
    int getPrivilegeFor(XOWLDatabase database);

    /**
     * Gets whether the user is a server administrator
     *
     * @return Whether the user is a server administrator
     */
    boolean isServerAdmin();
}
