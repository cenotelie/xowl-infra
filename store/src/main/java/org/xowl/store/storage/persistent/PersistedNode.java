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
 * Represents the common API for the persisted node
 *
 * @author Laurent Wouters
 */
interface PersistedNode {
    /**
     * The key for absent values in a store
     */
    long KEY_NOT_PRESENT = -1;

    /**
     * The size in bytes of the serialized form of a node
     */
    int SERIALIZED_SIZE = 12;

    /**
     * Serializes this node with the specified IO element
     *
     * @param ioElement An IO element for the serialization of this node
     * @throws IOException When an IO error occurs
     */
    void serialize(IOElement ioElement) throws IOException;
}
