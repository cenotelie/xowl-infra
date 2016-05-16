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

import org.xowl.infra.utils.collections.Couple;

import java.util.Iterator;

/**
 * Utility API for the first stage of a persisted map
 * Persisted maps map long to long. Stage 1 maps the upper bits of the key and stage 2 the lower bits.
 * Stage 2 is a B+ tree
 *
 * @author Laurent Wouters
 */
class PersistedMapStage2 {
    /**
     * The number of child entries in a B+ tree node
     */
    private static final int ENTRY_PER_NODE = 8;
    /**
     * The size of a children entry in a B+ tree node:
     * int: key
     * long: pointer to child
     */
    private static final int CHILD_ENTRY_SIZE = 4 + 8;
    /**
     * The size of a B+ tree node in stage 2:
     * long: next sibling
     * char: number of entries
     * char: entry flags for internal/external nodes
     * entries[entryCount]: the children entries
     */
    private static final int NODE_SIZE = 8 + 2 + 2 + ENTRY_PER_NODE * CHILD_ENTRY_SIZE;

    /**
     * Initializes an empty stage 2 map
     *
     * @param store The containing store
     * @return The entry key for the root of the stage 2 map
     * @throws StorageException When an IO operation fails
     */
    public static long newMap(FileStore store) throws StorageException {
        long entry = store.allocate(NODE_SIZE);
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
            access.writeLong(FileStore.KEY_NULL);
            access.writeChar((char) 0);
            access.writeChar((char) 0);
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
        long currentNode = head;
        while (currentNode != FileStore.KEY_NULL) {
            try (IOAccess access = store.access(currentNode)) {
                int entryCount = access.seek(8).readChar();
                int entryFlags = access.readChar();
                for (int i = 0; i != entryCount; i++) {
                    // read entry data
                    int entryKey = access.readInt();
                    long entryPtr = access.readLong();
                    // is this a key of interest
                    if (entryKey == key && (entryFlags >>> i) != 0) {
                        // hit on the key, this is external node => found the mapping
                        return entryPtr;
                    } else if (key <= entryKey) {
                        // we must go down this node
                        currentNode = entryPtr;
                        break;
                    }
                }
            }
        }
        // no node found
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
     * @return The iterator
     * @throws StorageException When an IO operation fails
     */
    public static Iterator<Couple<Integer, Long>> iterator(FileStore store, long head) throws StorageException {
        return null;
    }
}
