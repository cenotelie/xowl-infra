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

import java.util.Arrays;
import java.util.NoSuchElementException;

/**
 * Utility API for the first stage of a persisted map
 * Persisted maps map long to long. Stage 1 maps the upper bits of the key and stage 2 the lower bits.
 * Stage 1 is simply a linked list of nodes which contains entries for registered stage 2 heads:
 * [next ptr, e0, e1, ... en-1]
 * [next ptr, en, en+2, ... e2n-1]
 * ...
 *
 * @author Laurent Wouters
 */
class PersistedMapStage1 {
    /**
     * The number of entries in a stage 1 entry
     */
    private static final int ENTRY_COUNT = 8;
    /**
     * The size of a stage 1 node:
     * long: next node
     * long[entryCount]: The stage 2 heads for
     */
    private static final int NODE_SIZE = 8 + ENTRY_COUNT * 8;

    /**
     * Initializes an empty stage 2 map
     *
     * @param store The containing store
     * @return The entry key for the root of the stage 2 map
     */
    public static long newMap(FileStore store) throws StorageException {
        long entry = store.allocate(NODE_SIZE);
        initNode(store, entry);
        return entry;
    }

    /**
     * Initializes a stage 1 node
     *
     * @param store The containing store
     * @param entry The key to the node to initialize
     * @throws StorageException When an IO operation fails
     */
    private static void initNode(FileStore store, long entry) throws StorageException {
        try (IOAccess access = store.access(entry)) {
            access.writeLong(FileStore.KEY_NULL);
            for (int i = 0; i != ENTRY_COUNT; i++)
                access.writeLong(FileStore.KEY_NULL);
        }
    }

    /**
     * Gets the entry key for the stage 2 map for the specified key
     *
     * @param store The containing store
     * @param head  The root head for stage 1
     * @param key   The stage 1 key
     * @return The entry key for the stage 2 map, or FileStore.KEY_NULL if there is none
     * @throws StorageException When an IO operation fails
     */
    public static long getHeadFor(FileStore store, long head, int key) throws StorageException {
        return findHeadFor(store, head, key, false);
    }

    /**
     * Resolves the entry key for the stage 2 map for the specified key
     * If the entry for stage 2 does not exist, it is allocated
     *
     * @param store The containing store
     * @param head  The root head for stage 1
     * @param key   The stage 1 key
     * @return The entry key for the stage 2 map
     * @throws StorageException When an IO operation fails
     */
    public static long resolveHeadFor(FileStore store, long head, int key) throws StorageException {
        return findHeadFor(store, head, key, true);
    }

    /**
     * Finds the entry key for the stage 2 map for the specified key
     * If the entry for stage 2 does not exist, it is allocated if doResolve is true
     *
     * @param store     The containing store
     * @param head      The root head for stage 1
     * @param key       The stage 1 key
     * @param doResolve Whether to resolve the stage 2 if it is not present
     * @return The entry key for the stage 2 map
     * @throws StorageException When an IO operation fails
     */
    private static long findHeadFor(FileStore store, long head, int key, boolean doResolve) throws StorageException {
        int offset = key; // current offset in the current node
        long currentNode = head; // current stage 1 node
        while (offset >= ENTRY_COUNT) {
            // the offset indicates that the key is not in this node
            // find the next node
            long next;
            try (IOAccess access = store.read(currentNode)) {
                next = access.readLong();
            }
            if (next == FileStore.KEY_NULL) {
                // the next node does not exist
                if (!doResolve)
                    // this is only a lookup, report not found
                    return FileStore.KEY_NULL;
                // allocate the next node
                try (IOAccess access = store.access(currentNode)) {
                    next = access.readLong();
                    if (next == FileStore.KEY_NULL) {
                        // still empty
                        next = newMap(store);
                        access.reset().writeLong(next);
                    }
                }
            }
            // go to the next node
            currentNode = next;
            offset -= ENTRY_COUNT;
        }
        // the current node is the one containing the key
        // find the result
        long result;
        try (IOAccess access = store.read(currentNode)) {
            result = access.seek(8 + offset * 8).readLong();
        }
        if (result == FileStore.KEY_NULL) {
            // the stage 2 node does not exist
            if (!doResolve)
                // this is only a lookup, report not found
                return FileStore.KEY_NULL;
            // we must allocate it
            try (IOAccess access = store.access(currentNode)) {
                result = access.seek(8 + offset * 8).readLong();
                if (result == FileStore.KEY_NULL) {
                    // still empty
                    result = PersistedMapStage2.newMap(store);
                    access.seek(8 + offset * 8).writeLong(result);
                }
            }
        }
        return result;
    }

    /**
     * Clears the stage 1 map and associated stage 2 maps
     *
     * @param store The containing store
     * @param head  The root head for stage 1
     * @throws StorageException When an IO operation fails
     */
    public static void clear(FileStore store, long head) throws StorageException {
        // buffer of stage 2 map heads
        long[] heads = new long[ENTRY_COUNT];
        int count = 0;
        // the node just after the head
        long currentNode;
        // access the head and clear it
        try (IOAccess access = store.access(head)) {
            // get all the data
            currentNode = access.readLong();
            for (int i = 0; i != ENTRY_COUNT; i++) {
                long head2 = access.readLong();
                if (head2 != FileStore.KEY_NULL) {
                    if (count >= heads.length)
                        heads = Arrays.copyOf(heads, heads.length + ENTRY_COUNT);
                    heads[count++] = head2;
                }
            }
            // clear the node
            access.reset();
            access.writeLong(FileStore.KEY_NULL);
            for (int i = 0; i != ENTRY_COUNT; i++)
                access.writeLong(FileStore.KEY_NULL);
        }
        // iterate over all the stage 1 nodes after the head
        // these node will be de-allocated
        while (currentNode != FileStore.KEY_NULL) {
            long next;
            try (IOAccess access = store.read(currentNode)) {
                next = access.readLong();
                for (int i = 0; i != ENTRY_COUNT; i++) {
                    long head2 = access.readLong();
                    if (head2 != FileStore.KEY_NULL) {
                        if (count >= heads.length)
                            heads = Arrays.copyOf(heads, heads.length + ENTRY_COUNT);
                        heads[count++] = head2;
                    }
                }
            }
            store.free(currentNode);
            currentNode = next;
        }

        // clears the stage 2 maps
        for (int i = 0; i != count; i++)
            PersistedMapStage2.clear(store, heads[i]);
    }

    /**
     * Gets an iterator over the registered stage 2 heads in the requested stage 1 map
     *
     * @param store The containing store
     * @param head  The root head for stage 1
     * @return The iterator
     * @throws StorageException When an IO operation fails
     */
    public static Itr iterator(FileStore store, long head) throws StorageException {
        return new Itr(store, head);
    }

    /**
     * Iterator for stored stage 2 heads
     */
    public static class Itr {
        /**
         * The containing store
         */
        private final FileStore store;
        /**
         * The current stage 1 node
         */
        private long currentNode;
        /**
         * The current offset in the stage 1 node
         */
        private int currentOffset;
        /**
         * The next value
         */
        private long nextValue;

        /**
         * Initializes this iterator
         *
         * @param store The containing store
         * @param head  The stage 1 head
         * @throws StorageException When an IO operation fails
         */
        public Itr(FileStore store, long head) throws StorageException {
            this.store = store;
            this.currentNode = head;
            this.currentOffset = -1;
            this.nextValue = findNext();
        }

        /**
         * Finds the next value for this iterator
         *
         * @return The next value
         * @throws StorageException
         */
        private long findNext() throws StorageException {
            currentOffset++;
            while (true) {
                if (currentOffset >= ENTRY_COUNT) {
                    // we should go to the next node
                    try (IOAccess access = store.read(currentNode)) {
                        long next = access.readLong();
                        if (next == FileStore.KEY_NULL)
                            // no next node
                            return FileStore.KEY_NULL;
                        // go to next
                        currentNode = next;
                        currentOffset = 0;
                    }
                    continue;
                }
                // read the current node
                try (IOAccess access = store.read(currentNode)) {
                    // seek to the current offset
                    access.seek(8 + currentOffset * 8);
                    // read the registered heads
                    for (int i = currentOffset; i != ENTRY_COUNT; i++) {
                        long result = access.readLong();
                        if (result != FileStore.KEY_NULL) {
                            // found the next value!
                            currentOffset = i;
                            return result;
                        }
                    }
                    // no heads found, got ot next node
                    currentOffset = ENTRY_COUNT;
                }
            }
        }

        /**
         * Gets whether there is a next stage 2 head
         *
         * @return Whether there is a next stage 2 head
         */
        public boolean hasNext() {
            return (nextValue != FileStore.KEY_NULL);
        }

        /**
         * Gets the next stage 2 head
         *
         * @return The next stage 2 head
         * @throws StorageException When an IO operation fails
         */
        public long next() throws StorageException {
            if (nextValue == FileStore.KEY_NULL)
                throw new NoSuchElementException();
            long result = nextValue;
            nextValue = findNext();
            return result;
        }
    }
}
