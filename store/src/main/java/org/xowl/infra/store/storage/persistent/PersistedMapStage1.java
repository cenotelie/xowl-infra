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
import org.xowl.infra.utils.logging.Logger;

import java.util.Arrays;
import java.util.Iterator;
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
        try (IOAccess access = store.accessW(entry)) {
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
            try (IOAccess access = store.accessR(currentNode)) {
                next = access.readLong();
            }
            if (next == FileStore.KEY_NULL) {
                // the next node does not exist
                if (!doResolve)
                    // this is only a lookup, report not found
                    return FileStore.KEY_NULL;
                // allocate the next node
                try (IOAccess access = store.accessW(currentNode)) {
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
        try (IOAccess access = store.accessR(currentNode)) {
            result = access.seek(8 + offset * 8).readLong();
        }
        if (result == FileStore.KEY_NULL) {
            // the stage 2 node does not exist
            if (!doResolve)
                // this is only a lookup, report not found
                return FileStore.KEY_NULL;
            // we must allocate it
            try (IOAccess access = store.accessW(currentNode)) {
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
        try (IOAccess access = store.accessW(head)) {
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
            try (IOAccess access = store.accessR(currentNode)) {
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
    public static Iterator<Couple<Integer, Long>> iterator(FileStore store, long head) throws StorageException {
        return new Itr(store, head);
    }

    /**
     * Iterator for stored stage 2 heads
     */
    private static class Itr implements Iterator<Couple<Integer, Long>> {
        /**
         * The containing store
         */
        private final FileStore store;
        /**
         * The current stage 1 node for this iterator
         */
        private long currentNode;
        /**
         * The current offset in the stage 1 node
         */
        private int currentOffset;
        /**
         * The current stage 1 key
         */
        private int currentKey;
        /**
         * The next result
         */
        private Couple<Integer, Long> nextResult;

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
            this.currentKey = -1;
            this.nextResult = findNext();
        }

        /**
         * Finds the next item for this iterator
         *
         * @return The next item
         * @throws StorageException When an IO operation failed
         */
        private Couple<Integer, Long> findNext() throws StorageException {
            // go to next
            currentOffset++;
            currentKey++;
            while (true) {
                if (currentOffset == ENTRY_COUNT) {
                    // we should go to the next node
                    try (IOAccess access = store.accessR(currentNode)) {
                        long next = access.readLong();
                        if (next == FileStore.KEY_NULL) {
                            // no next node
                            return null;
                        }
                        // go to next
                        currentNode = next;
                        currentOffset = 0;
                    }
                    continue;
                }
                // read the current node
                try (IOAccess access = store.accessR(currentNode)) {
                    // seek to the current offset
                    access.seek(8 + currentOffset * 8);
                    // read the registered heads
                    while (currentOffset < ENTRY_COUNT) {
                        long result = access.readLong();
                        if (result != FileStore.KEY_NULL) {
                            // found the next value!
                            return new Couple<>(currentKey, result);
                        }
                        currentOffset++;
                        currentKey++;
                    }
                    // no heads found, got to next node
                }
            }
        }

        @Override
        public boolean hasNext() {
            return (nextResult != null);
        }

        @Override
        public Couple<Integer, Long> next() {
            if (nextResult == null)
                throw new NoSuchElementException();
            Couple<Integer, Long> result = nextResult;
            try {
                nextResult = findNext();
            } catch (StorageException exception) {
                Logger.DEFAULT.error(exception);
                nextResult = null;
            }
            return result;
        }

        @Override
        public void remove() {
            // do nothing here
        }
    }
}
