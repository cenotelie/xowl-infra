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

package org.xowl.store.storage.persistent;

import java.io.IOException;

/**
 * Represents an object that can be persisted in a binary form
 *
 * @author Laurent Wouters
 */
interface Persistable {
    /**
     * Gets the length in bytes of the persisted form of this object
     *
     * @return The length in bytes
     */
    int persistedLength();

    /**
     * Writes the binary form into the specified file.
     * The file is expected to be correctly positioned.
     *
     * @param file The file to persist this object in
     * @throws IOException When an IO operation failed
     */
    void persist(PersistedFile file) throws IOException;

    /**
     * Determines whether the specified file at its current position contains the persisted form of this object.
     *
     * @param file The file to check against
     * @return true if the data ahead in the specified file correspond to the persistent form of the object
     * @throws IOException When an IO operation failed
     */
    boolean isPersistedIn(PersistedFile file) throws IOException;
}
