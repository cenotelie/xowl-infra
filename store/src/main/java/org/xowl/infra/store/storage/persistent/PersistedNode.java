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

package org.xowl.infra.store.storage.persistent;

import org.xowl.infra.store.rdf.Node;

/**
 * Represents the common API for the persisted node
 *
 * @author Laurent Wouters
 */
interface PersistedNode extends Node {
    /**
     * The size in bytes of the serialized form of a node
     * int: node type
     * long: key to node data
     */
    int SERIALIZED_SIZE = 8 + 4;

    /**
     * Gets the store that maintains this node
     *
     * @return The store that maintains this node
     */
    PersistedNodes getStore();

    /**
     * Gets the key identifying this node
     *
     * @return The key identifying this node
     */
    long getKey();

    /**
     * Increments the reference count for this node
     *
     * @throws StorageException When an IO operation failed
     */
    void incrementRefCount() throws StorageException;

    /**
     * Decrements the reference count for this node
     *
     * @throws StorageException When an IO operation failed
     */
    void decrementRefCount() throws StorageException;
}
