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

import java.util.Iterator;
import java.util.Map;

/**
 * Utility API for the first stage of a persisted map
 * Persisted maps map long to long. Stage 1 maps the upper bits of the key and stage 2 the lower bits.
 * Stage 2 is a B+ tree
 *
 * @author Laurent Wouters
 */
class PersistedMapStage2 {
    /**
     * The size of a node in stage 2
     */
    private static final int NODE_SIZE = 16;
    /**
     * The size of a root stage 2 node
     */
    public static final int ROOT_SIZE = NODE_SIZE;

    /**
     * Initializes an empty stage 2 map
     *
     * @param store The containing store
     * @return The entry key for the root of the stage 2 map
     * @throws StorageException When an IO operation fails
     */
    public static long newMap(FileStore store) throws StorageException {
        long entry = store.allocate(ROOT_SIZE);
        initNode(store, entry);
        return entry;
    }

    /**
     * Initializes a stage 2 node
     *
     * @param store The containing store
     * @param entry The key to the node to initialize
     * @throws StorageException When an IO operation fails
     */
    private static void initNode(FileStore store, long entry) throws StorageException {
        try (IOAccess access = store.access(entry)) {

        }
    }

    /**
     * Retrieves the value associated to a key
     *
     * @param store The containing store
     * @param head  The entry for the stage 2 root node
     * @param key   The stage 2 key to look for
     * @return The associated value, or FileStore.Key_NULL when none is found
     * @throws StorageException When an IO operation fails
     */
    public static long get(FileStore store, long head, int key) throws StorageException {
        return FileStore.KEY_NULL;
    }

    /**
     * Puts a mapping into a stage 2 map
     *
     * @param store The containing store
     * @param head  The entry for the stage 2 root node
     * @param key   The stage 2 key to store
     * @param value The value to store
     * @return The previous value if there was one, or FileStore.Key_NULL
     * @throws StorageException When an IO operation fails
     */
    public static long put(FileStore store, long head, int key, long value) throws StorageException {
        return FileStore.KEY_NULL;
    }

    /**
     * Removes the specified key and its associated value
     *
     * @param store The containing store
     * @param head  The entry for the stage 2 root node
     * @param key   The stage 2 key to look for
     * @return The previous value if there was one, or FileStore.Key_NULL
     * @throws StorageException When an IO operation fails
     */
    public static long remove(FileStore store, long head, int key) throws StorageException {
        return FileStore.KEY_NULL;
    }

    /**
     * Clears the stage 2 map
     *
     * @param store The containing store
     * @param head  The entry for the stage 2 root node
     * @throws StorageException
     */
    public static void clear(FileStore store, long head) throws StorageException {
    }

    /**
     * Gets an iterator over the key-value mappings for a stage 2 map
     *
     * @param store The containing store
     * @param head  The entry for the stage 2 root node
     * @param key1  The associated stage 1 key
     * @return The iterator
     * @throws StorageException When an IO operation fails
     */
    public static Iterator<Map.Entry<Long, Long>> iterator(FileStore store, long head, int key1) throws StorageException {
        return null;
    }
}
