/*******************************************************************************
 * Copyright (c) 2016 Laurent Wouters
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

package org.xowl.infra.store.storage.persistent;

import java.util.HashMap;
import java.util.Map;

/**
 * Implements a (long -> long) map that is persisted in files
 *
 * The map is composed of two parts: a B+ tree for the lower part of the keys and an index for the upper part.
 * To resolve a key, the upper part of the key is checked against the index to get the entry for the B+ tree root for the lower part.
 * The entry's value is then in the B+ tree accessed through this root.
 *
 * An index entry has the layout:
 * long: next entry
 * long[count] key to the B+ tree for the associated index
 *
 * A B+ tree node has the layout:
 * long: next sibling
 * char: count
 * char: flags
 * int[b]: keys
 * long[b]: values
 *
 * @author Laurent Wouters
 */
class PersistedMap {
    /**
     * The value when a key is not found
     */
    public static final long KEY_NOT_FOUND = FileStore.KEY_NULL;

    /**
     * The number of entries in an index
     */
    private static final int INDEX_ENTRY_COUNT = 8;
    /**
     * The size of an index entry
     */
    private static final int INDEX_SIZE = 8 + INDEX_ENTRY_COUNT * 8;

    /**
     * The fan-out factor of the B+ tree
     */
    private static final int BTREE_NODE_SPAN = 16;
    /**
     * The size of a B+ tree node
     */
    private static final int BTREE_NODE_SIZE = 8 + 2 + 2 + BTREE_NODE_SPAN * (4 + 8);

    /**
     * The backing store
     */
    private final FileStore store;
    /**
     * The head entry for the map
     */
    private final long mapHead;

    /**
     * Initializes this map
     *
     * @param store   The backing store
     * @param mapHead The head entry for the map
     */
    public PersistedMap(FileStore store, long mapHead) {
        this.store = store;
        this.mapHead = mapHead;
    }

    /**
     * Creates a new persisted map
     * @param store The backing store
     * @return The persisted map
     * @throws StorageException When an IO error occur
     */
    public static PersistedMap create(FileStore store) throws StorageException {
        long mapHead = allocateIndex(store);
        return new PersistedMap(store, mapHead);
    }

    /**
     * Gets the value associated to key
     *
     * @param key The requested key
     * @return The associated value, or KEY_NOT_FOUND when the key is not present
     */
    public long get(long key) {
        return KEY_NOT_FOUND;
    }

    /**
     * Puts a new key, value couple into the map
     * If the key is already present, the specified value replaces the existing one.
     *
     * @param key   The key
     * @param value The associated value
     */
    public void put(long key, long value) {

    }

    /**
     * Removes the entry for the specified key
     *
     * @param key The key
     * @return The value that was associated to the key, or KEY_NOT_FOUND if there was none
     */
    public long remove(long key) {
        return KEY_NOT_FOUND;
    }

    /**
     * Removes all entries from this map
     */
    public void clear() {

    }

    /**
     * Gets an iterator over the entries in this map
     *
     * @return An iterator over the entries
     */
    public Iterator entries() {
        return null;
    }


    /**
     * Gets the tree head that corresponds to the specified radical
     * @param radical The radical
     * @param resolve Whether to create the tree head if it does not exist
     * @return The tree head
     */
    private long getTreeHead(int radical, boolean resolve) {

    }

    /**
     * Allocates an index node in the specified store
     * @param store The store
     * @return The key to the index node
     * @throws StorageException When an IO error occur
     */
    private static long allocateIndex(FileStore store) throws StorageException {
        long key = store.add(INDEX_SIZE);
        try (IOTransaction transaction = store.access(key)) {
            transaction.writeLong(FileStore.KEY_NULL);
            for (int i=0; i != INDEX_ENTRY_COUNT; i++) {
                transaction.writeLong(FileStore.KEY_NULL);
            }
        }
        return key;
    }

    /**
     * Allocates a B+ tree node in the associated store
     * @return The key to the node
     * @throws StorageException When an IO error occur
     */
    private long allocateTreeNode() throws StorageException {
        long key = store.add(BTREE_NODE_SIZE);
        try (IOTransaction transaction = store.access(key)) {
            transaction.writeLong(FileStore.KEY_NULL);
            transaction.writeChar('\0');
            transaction.writeChar('\0');
        }
        return key;
    }

    /**
     * An iterator over entries in the map
     */
    public class Iterator implements java.util.Iterator<Map.Entry<Long, Long>> {
        /**
         * The key for the current entry
         */
        private long currentEntryKey;
        /**
         * The value for the current entry
         */
        private long currentEntryValue;

        /**
         * Finds the next entry
         *
         * @return Whether there is a next entry
         */
        private boolean findNext() {
            // TODO: implement this
            return false;
        }

        @Override
        public boolean hasNext() {
            return currentEntryKey != KEY_NOT_FOUND;
        }

        @Override
        public Map.Entry<Long, Long> next() {
            Map.Entry<Long, Long> result = new HashMap.SimpleImmutableEntry<>(currentEntryKey, currentEntryValue);
            findNext();
            return result;
        }

        @Override
        public void remove() {
            // TODO: implement removal
        }

        /**
         * Gets the key for the current entry
         *
         * @return The key for the current entry
         */
        public long currentKey() {
            return currentEntryKey;
        }

        /**
         * Gets the value for the current entry
         *
         * @return The value for the current entry
         */
        public long currentValue() {
            return currentEntryValue;
        }

        /**
         * Gets the next key
         *
         * @return The next key
         */
        public long nextKey() {
            long result = currentEntryKey;
            findNext();
            return result;
        }

        /**
         * Gets the next value
         *
         * @return The next value
         */
        public long nextValue() {
            long result = currentEntryValue;
            findNext();
            return result;
        }
    }
}
