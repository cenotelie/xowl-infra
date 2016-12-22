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
import org.xowl.infra.utils.logging.Logging;

import java.util.Iterator;
import java.util.Map;

/**
 * Implements a (long -> long) map that is persisted in files.
 * A persisted map is thread-safe for access and modifications.
 * <p>
 * A persisted map is implemented as a B+ tree with Preparatory Operations.
 * Y. Mond and Y. Raz, Concurrency Control in B+ Tree Databases using Preparatory Operations, in Proceedings of VLDB 1985
 * <p>
 * n is the rate of the tree
 * Invariant: The number of keys (k) of a node is in [n-1, 2n+1].
 * => The number of children of a node is in [n, 2n+2].
 * Invariant: Exception to the above is the root node.
 * Invariant: A node that is not a leaf has at most k keys and k+1 children.
 * Invariant: All the leaves are a the same height.
 * Invariant: The keyed data are all at the leaves.
 *
 * @author Laurent Wouters
 */
class PersistedMap {
    /**
     * The rate of the B+ tree
     */
    private static final int N = 15;
    /**
     * The maximum number of child references in a node (2*n + 2)
     */
    private static final int CHILD_COUNT = N * 2 + 2;
    /**
     * The size of a child entry in a B+ tree node:
     * int: key value
     * long: pointer to child
     */
    private static final int CHILD_SIZE = 8 + 8;
    /**
     * The size of a B+ tree inner node header
     * long: pointer to the parent node
     * char: leaf marker
     * char: number of keys
     */
    private static final int NODE_HEADER = 8 + 2 + 2;
    /**
     * The size of a B+ tree node in stage 2:
     * header: the node header
     * entries[entryCount]: the children entries
     */
    private static final int NODE_SIZE = NODE_HEADER + CHILD_COUNT * CHILD_SIZE;
    /**
     * Marker for leaf nodes
     */
    private static final char NODE_IS_LEAF = 1;


    /**
     * The backing store
     */
    private final FileStore store;
    /**
     * The head entry for the map
     */
    private final long head;

    /**
     * Initializes this map
     *
     * @param store The backing store
     * @param head  The head entry for the map
     */
    public PersistedMap(FileStore store, long head) {
        this.store = store;
        this.head = head;
    }

    /**
     * Creates a new persisted map
     *
     * @param store The backing store
     * @return The persisted map
     * @throws StorageException When an IO operation fails
     */
    public static PersistedMap create(FileStore store) throws StorageException {
        long entry = store.allocate(NODE_SIZE);
        try (IOAccess access = store.accessW(entry)) {
            // write header
            access.writeLong(FileStore.KEY_NULL);
            access.writeChar(NODE_IS_LEAF);
            access.writeChar((char) 0);
            // write last entry to non-existing node
            access.writeLong(0);
            access.writeLong(FileStore.KEY_NULL);
        }
        return new PersistedMap(store, entry);
    }

    /**
     * Gets the value associated to key
     *
     * @param key The requested key
     * @return The associated value, or FileStore.KEY_NULL when the key is not present
     * @throws StorageException When an IO operation fails
     */
    public long get(long key) throws StorageException {
        IOAccess accessFather = null;
        IOAccess accessCurrent = store.accessR(head);
        try {
            while (true) {
                // inspect the current node
                boolean isLeaf = accessCurrent.skip(8).readChar() == NODE_IS_LEAF;
                char count = accessCurrent.readChar();
                if (isLeaf)
                    return getOnNode(accessCurrent, key, count);
                long next = getChild(accessCurrent, key, count);
                // free the father if any, rotate the accesses and access the next node
                if (accessFather != null)
                    accessFather.close();
                accessFather = accessCurrent;
                accessCurrent = store.accessR(next);
            }
        } finally {
            if (accessFather != null)
                accessFather.close();
            if (accessCurrent != null)
                accessCurrent.close();
        }
    }

    /**
     * When on a leaf node, look for the matching entry
     *
     * @param accessCurrent The access to the current node
     * @param key           The stage 2 key to look for
     * @param count         The number of entries in the node
     * @return The associated value, or FileStore.Key_NULL when none is found
     */
    private long getOnNode(IOAccess accessCurrent, long key, char count) {
        for (int i = 0; i != count; i++) {
            // read entry data
            long entryKey = accessCurrent.readLong();
            long entryPtr = accessCurrent.readLong();
            if (entryKey == key) {
                // found the key
                return entryPtr;
            }
        }
        return FileStore.KEY_NULL;
    }

    /**
     * When on an internal node, look for a suitable descendant
     *
     * @param accessCurrent The access to the current node
     * @param key           The stage 2 key to look for
     * @param count         The number of entries in the node
     * @return The descendant
     */
    private long getChild(IOAccess accessCurrent, long key, char count) {
        for (int i = 0; i != count; i++) {
            // read entry data
            long entryKey = accessCurrent.readLong();
            long entryPtr = accessCurrent.readLong();
            if (key < entryKey) {
                // go through this child
                return entryPtr;
            }
        }
        return accessCurrent.skip(8).readLong();
    }

    /**
     * Atomically tries to insert a value in the map for a key
     * This fails if there already is an entry.
     *
     * @param key      The key
     * @param valueNew The associated value
     * @return Whether the operation succeeded
     * @throws StorageException When an IO operation fails
     */
    public boolean tryPut(long key, long valueNew) throws StorageException {
        return compareAndSet(key, FileStore.KEY_NULL, valueNew);
    }

    /**
     * Atomically tries to remove a value from the map
     * This fails if there is no value or if the value to remove is different from the expected one.
     *
     * @param key      The key
     * @param valueOld The expected value to remove
     * @return Whether the operation succeeded
     * @throws StorageException When an IO operation fails
     */
    public boolean tryRemove(long key, long valueOld) throws StorageException {
        return compareAndSet(key, valueOld, FileStore.KEY_NULL);
    }

    /**
     * Atomically replaces a value in the map for a key
     * This fails if there is no value for the key, or if the actual value is different from the expected one.
     *
     * @param key      The key
     * @param valueOld The old value to replace (FileStore.KEY_NULL, if this is expected to be an insertion)
     * @param valueNew The new value for the key (FileStore.KEY_NULL, if this is expected to be a removal)
     * @return Whether the operation succeeded
     * @throws StorageException When an IO operation fails
     */
    public boolean compareAndSet(long key, long valueOld, long valueNew) throws StorageException {
        IOAccess accessFather = null;
        IOAccess accessCurrent = store.accessW(head);
        try {
            inspect(null, accessCurrent, head, valueNew != FileStore.KEY_NULL);
            while (true) {
                // inspect the current node
                boolean isLeaf = accessCurrent.seek(8).readChar() == NODE_IS_LEAF;
                char count = accessCurrent.readChar();
                if (isLeaf) {
                    // look into the entries of this node
                    for (int i = 0; i != count; i++) {
                        long entryKey = accessCurrent.readLong();
                        long entryValue = accessCurrent.readLong();
                        if (entryKey == key)
                            // found the key
                            return doCompareAndReplace(accessCurrent, i, count, entryValue, valueOld, valueNew);
                    }
                    // did not find the key, stop here
                    return doInsert(accessCurrent, count, key, valueOld, valueNew);
                }
                // release the father
                if (accessFather != null)
                    accessFather.close();
                // resolve the child
                long child = getChild(accessCurrent, key, count);
                IOAccess accessChild = store.accessW(child);
                boolean hasChanged = inspect(accessCurrent, accessChild, child, valueNew != FileStore.KEY_NULL);
                while (hasChanged) {
                    accessChild.close();
                    count = accessCurrent.seek(2).readChar();
                    child = getChild(accessCurrent, key, count);
                    accessChild = store.accessW(child);
                    hasChanged = inspect(accessCurrent, accessChild, child, valueNew != FileStore.KEY_NULL);
                }
                // rotate
                accessFather = accessCurrent;
                accessCurrent = accessChild;
            }
        } finally {
            if (accessFather != null)
                accessFather.close();
            if (accessCurrent != null)
                accessCurrent.close();
        }
    }

    /**
     * When an existing key-value map is found in the current node, perform a compare and replace
     *
     * @param accessCurrent The access to the current node
     * @param index         The index of the entry within the node
     * @param count         The number of entries in the node
     * @param entryValue    The current value of the entry
     * @param valueOld      The old value to replace (FileStore.KEY_NULL, if this is expected to be an insertion)
     * @param valueNew      The new value for the key (FileStore.KEY_NULL, if this is expected to be a removal)
     * @return Whether the operation succeeded
     */
    private boolean doCompareAndReplace(IOAccess accessCurrent, int index, char count, long entryValue, long valueOld, long valueNew) {
        if (entryValue != valueOld)
            // oops, compare failed
            return false;
        if (valueNew != FileStore.KEY_NULL) {
            // this is a replace
            accessCurrent.skip(-8).writeLong(valueNew);
            return true;
        } else {
            // this is a removal
            // shift the data to the left
            for (int i = index + 1; i != count + 1; i++) {
                accessCurrent.seek(NODE_HEADER + i * CHILD_SIZE);
                long key = accessCurrent.readLong();
                long value = accessCurrent.readLong();
                accessCurrent.skip(-CHILD_SIZE * 2);
                accessCurrent.writeLong(key);
                accessCurrent.writeLong(value);
            }
            // update the header
            count--;
            accessCurrent.seek(8 + 2).writeChar(count);
            return true;
        }
    }

    /**
     * When the requested key is not found in the current leaf node, try to perform an insert
     *
     * @param accessCurrent The access to the current node
     * @param count         The number of entries in the node
     * @param key           The key
     * @param valueOld      The old value to replace (FileStore.KEY_NULL, if this is expected to be an insertion)
     * @param valueNew      The new value for the key (FileStore.KEY_NULL, if this is expected to be a removal)
     * @return Whether the operation succeeded
     */
    private boolean doInsert(IOAccess accessCurrent, char count, long key, long valueOld, long valueNew) {
        if (valueNew != FileStore.KEY_NULL) {
            // this is an insertion
            if (valueOld != FileStore.KEY_NULL)
                // expected a value
                return false;
            // find the index to insert at
            int insertAt = count;
            accessCurrent.seek(NODE_HEADER);
            for (int i = 0; i != count; i++) {
                long entryKey = accessCurrent.readLong();
                accessCurrent.skip(8);
                if (entryKey > key) {
                    insertAt = i;
                    break;
                }
            }
            // shift the existing data to the right
            for (int i = count; i != insertAt - 1; i--) {
                accessCurrent.seek(NODE_HEADER + i * CHILD_SIZE);
                long entryKey = accessCurrent.readLong();
                long entryValue = accessCurrent.readLong();
                accessCurrent.writeLong(entryKey);
                accessCurrent.writeLong(entryValue);
            }
            // write the inserted key-value
            accessCurrent.seek(NODE_HEADER + insertAt * CHILD_SIZE);
            accessCurrent.writeLong(key);
            accessCurrent.writeLong(valueNew);
            // update the header
            count++;
            accessCurrent.seek(8 + 2).writeChar(count);
            return true;
        } else {
            // the entry is not found and the new value is the null key
            // this is only valid if the expected old value is also null
            return (valueOld == FileStore.KEY_NULL);
        }
    }

    /**
     * Inspect the current node for preparatory operation
     *
     * @param accessFather  The access to the parent node
     * @param accessCurrent The access to the current node
     * @param current       The entry for the node to split
     * @param isInsert      Whether this is an insert operation
     * @return Whether the tree was modified
     * @throws StorageException When an IO operation fails
     */
    private boolean inspect(IOAccess accessFather, IOAccess accessCurrent, long current, boolean isInsert) throws StorageException {
        boolean isLeaf = accessCurrent.reset().readChar() == 1;
        char count = accessCurrent.readChar();
        if (isInsert && count >= 2 * N) {
            // split the current node
            if (accessFather == null) {
                if (isLeaf)
                    splitRootLeaf(accessCurrent, current, count);
                else
                    splitRootInternal(accessCurrent, current, count);
            } else if (isLeaf)
                splitLeaf(accessFather, accessCurrent, current, count);
            else
                splitInternal(accessFather, accessCurrent, current, count);
            return true;
        } else if (!isInsert && count <= N) {
            // TODO: do merge
            return false;
        }
        return false;
    }

    /**
     * Splits the root node when it is an internal node
     *
     * @param accessCurrent The access to the current node
     * @param current       The entry for the node to split
     * @param count         The number of children in the current node
     * @throws StorageException When an IO operation fails
     */
    private void splitRootInternal(IOAccess accessCurrent, long current, char count) throws StorageException {
        // number of keys to transfer to the right (not including the fallback pointer)
        int countRight = (count == 2 * N) ? N - 1 : N;
        long left = store.allocate(NODE_SIZE);
        long right = store.allocate(NODE_SIZE);
        long maxLeft;
        // write the new left node
        accessCurrent.seek(NODE_HEADER);
        try (IOAccess accessLeft = store.accessW(left)) {
            accessLeft.writeLong(current);
            accessLeft.writeChar((char) 0);
            accessLeft.writeChar((char) N);
            for (int i = 0; i != N; i++) {
                accessLeft.writeLong(accessCurrent.readLong());
                accessLeft.writeLong(accessCurrent.readLong());
            }
            maxLeft = accessCurrent.readLong();
            accessLeft.writeLong(0);
            accessLeft.writeLong(accessCurrent.readLong());
        }
        // write the new right node
        try (IOAccess accessRight = store.accessW(right)) {
            accessRight.writeLong(current);
            accessRight.writeChar((char) 0);
            accessRight.writeChar((char) countRight);
            for (int i = 0; i != countRight + 1; i++) {
                accessRight.writeLong(accessCurrent.readLong());
                accessRight.writeLong(accessCurrent.readLong());
            }
        }
        // rewrite the root node
        accessCurrent.seek(8 + 2);
        accessCurrent.writeChar((char) 1); // only one key
        accessCurrent.writeLong(maxLeft);
        accessCurrent.writeLong(left);
        accessCurrent.writeLong(0);
        accessCurrent.writeLong(right);
    }

    /**
     * Splits the root node when it is a leaf
     *
     * @param accessCurrent The access to the current node
     * @param current       The entry for the node to split
     * @param count         The number of children in the current node
     * @throws StorageException When an IO operation fails
     */
    private void splitRootLeaf(IOAccess accessCurrent, long current, char count) throws StorageException {
        // number of keys to transfer to the right (not including the neighbour pointer)
        int countRight = (count == 2 * N) ? N : N + 1;
        long left = store.allocate(NODE_SIZE);
        long right = store.allocate(NODE_SIZE);
        // write the new left node
        accessCurrent.seek(NODE_HEADER);
        try (IOAccess accessLeft = store.accessW(left)) {
            accessLeft.writeLong(current);
            accessLeft.writeChar(NODE_IS_LEAF);
            accessLeft.writeChar((char) N);
            for (int i = 0; i != N; i++) {
                accessLeft.writeLong(accessCurrent.readLong());
                accessLeft.writeLong(accessCurrent.readLong());
            }
            accessLeft.writeLong(0);
            accessLeft.writeLong(right);
        }
        // write the new right node
        long rightFirstKey = 0;
        try (IOAccess accessRight = store.accessW(right)) {
            accessRight.writeLong(current);
            accessRight.writeChar(NODE_IS_LEAF);
            accessRight.writeChar((char) countRight);
            for (int i = 0; i != countRight; i++) {
                long key = accessCurrent.readLong();
                if (i == 0)
                    rightFirstKey = key;
                accessRight.writeLong(key);
                accessRight.writeLong(accessCurrent.readLong());
            }
            accessRight.writeLong(0);
            accessRight.writeLong(FileStore.KEY_NULL);
        }
        // rewrite the root node
        accessCurrent.seek(8);
        accessCurrent.writeChar((char) 0);
        accessCurrent.writeChar((char) 1); // only one key
        accessCurrent.writeLong(rightFirstKey);
        accessCurrent.writeLong(left);
        accessCurrent.writeLong(0);
        accessCurrent.writeLong(right);
    }

    /**
     * Splits an internal node (that is not the root)
     *
     * @param accessFather  The access to the parent node
     * @param accessCurrent The access to the current node
     * @param current       The entry for the node to split
     * @param count         The number of children in the current node
     * @throws StorageException When an IO operation fails
     */
    private void splitInternal(IOAccess accessFather, IOAccess accessCurrent, long current, char count) throws StorageException {
        // number of keys to transfer to the right (not including the fallback child pointer)
        int countRight = (count == 2 * N) ? N - 1 : N;
        accessCurrent.seek(NODE_HEADER + (N + 1) * CHILD_SIZE);
        long right = store.allocate(NODE_SIZE);
        try (IOAccess accessRight = store.accessW(right)) {
            accessRight.writeLong(current);
            accessRight.writeChar((char) 0);
            accessRight.writeChar((char) countRight);
            // write the transferred key-values and the neighbour pointer
            for (int i = 0; i != countRight + 1; i++) {
                accessRight.writeLong(accessCurrent.readLong());
                accessRight.writeLong(accessCurrent.readLong());
            }
        }
        // update the data for the split node
        accessCurrent.seek(8 + 2).writeChar((char) N);
        long maxLeft = accessCurrent.seek(NODE_HEADER + N * CHILD_SIZE).readLong();
        accessCurrent.skip(-8).writeLong(0);
        // update the data for the parent
        splitInsertInParent(accessFather, current, right, maxLeft);
    }

    /**
     * Splits a leaf node (that is not the root)
     *
     * @param accessFather  The access to the parent node
     * @param accessCurrent The access to the current node
     * @param current       The entry for the node to split
     * @param count         The number of children in the current node
     * @throws StorageException When an IO operation fails
     */
    private void splitLeaf(IOAccess accessFather, IOAccess accessCurrent, long current, char count) throws StorageException {
        // number of keys to transfer to the right (not including the neighbour pointer)
        int countRight = (count == 2 * N) ? N : N + 1;
        accessCurrent.seek(NODE_HEADER + N * CHILD_SIZE);
        long right = store.allocate(NODE_SIZE);
        long rightFirstKey = 0;
        try (IOAccess accessRight = store.accessW(right)) {
            accessRight.writeLong(current);
            accessRight.writeChar(NODE_IS_LEAF);
            accessRight.writeChar((char) countRight);
            // write the transferred key-values and the neighbour pointer
            for (int i = 0; i != countRight + 1; i++) {
                long key = accessCurrent.readLong();
                long value = accessCurrent.readLong();
                if (i == 0)
                    rightFirstKey = key;
                accessRight.writeLong(key);
                accessRight.writeLong(value);
            }
        }
        // update the data for the split node
        accessCurrent.seek(8 + 2).writeChar((char) N);
        accessCurrent.seek(NODE_HEADER + N * CHILD_SIZE);
        accessCurrent.writeLong(0);
        accessCurrent.writeLong(right);
        // update the data for the parent
        splitInsertInParent(accessFather, current, right, rightFirstKey);
    }

    /**
     * During a split, insert the data for a split node
     *
     * @param access        The access to the parent of the split node
     * @param leftNode      The entry for the split node in the left
     * @param rightNode     The entry for the split node on the right
     * @param rightFirstKey The first key of the split node on the right
     */
    private void splitInsertInParent(IOAccess access, long leftNode, long rightNode, long rightFirstKey) {
        char count = access.seek(8 + 2).readChar();
        // find the index where to insert
        int insertAt = count;
        long originalKey = 0;
        for (int i = 0; i != count; i++) {
            long key = access.readLong();
            long value = access.readLong();
            if (value == leftNode) {
                insertAt = i;
                originalKey = key;
                break;
            }
        }
        // move the data on the right
        for (int i = count; i != insertAt; i--) {
            access.seek(NODE_HEADER + i * CHILD_SIZE);
            long entryKey = access.readLong();
            long entryValue = access.readLong();
            access.writeLong(entryKey);
            access.writeLong(entryValue);
        }
        // write the new pointers
        access.seek(NODE_HEADER + insertAt * CHILD_SIZE);
        access.writeLong(rightFirstKey);
        access.writeLong(leftNode);
        access.writeLong(originalKey);
        access.writeLong(rightNode);
        // update count
        count++;
        access.seek(8 + 2).writeChar(count);
    }

    /**
     * Removes all entries from this map
     *
     * @throws StorageException When an IO operation fails
     */
    public void clear() throws StorageException {
        long[] stack = new long[CHILD_COUNT];
        int stackHead = -1;
        try (IOAccess accessHead = store.accessW(head)) {
            // initializes the stack with the content of the head
            boolean isLeaf = accessHead.readChar() == 1;
            char count = accessHead.readChar();
            if (!isLeaf) {
                long entryPtr;
                for (int i = 0; i != count; i++) {
                    entryPtr = accessHead.skip(4).readLong();
                    stack[++stackHead] = entryPtr;
                }
                entryPtr = accessHead.skip(4).readLong();
                if (entryPtr != FileStore.KEY_NULL)
                    stack[++stackHead] = entryPtr;
            }

            // deletes the tree, starting with nodes on the stack
            while (stackHead != -1) {
                long current = stack[stackHead];
                stackHead--;
                try (IOAccess accessCurrent = store.accessR(current)) {
                    isLeaf = accessHead.readChar() == 1;
                    count = accessHead.readChar();
                    if (isLeaf) {

                    } else {

                    }


                }
            }
        }
    }

    /**
     * Gets an iterator over the entries in this map
     *
     * @return An iterator over the entries
     * @throws StorageException When an IO operation fails
     */
    public Iterator<Map.Entry<Long, Long>> entries() throws StorageException {
        return new Iter(store, head);
    }

    /**
     * Implements an iterator over the mappings in the B+ tree
     */
    private static class Iter implements Iterator<Map.Entry<Long, Long>> {
        /**
         * The containing store
         */
        private final FileStore store;
        /**
         * The entry for the stage 2 root node
         */
        private final long head;
        /**
         * The current leaf node
         */
        private long currentNode;
        /**
         * The keys in the current node
         */
        private final int[] currentKeys;
        /**
         * The values in the current node
         */
        private final long[] currentValues;
        /**
         * The current number of key-value mappings
         */
        private int currentCount;
        /**
         * The next index in the current node
         */
        private int nextIndex;
        /**
         * The next sibling after this node
         */
        private long nextSibling;
        /**
         * The result structure
         */
        private final Couple<Integer, Long> result;

        /**
         * Initializes this iterator
         *
         * @param store The containing store
         * @param head  The entry for the stage 2 root node
         * @throws StorageException When an IO operation fails
         */
        public Iter(FileStore store, long head) throws StorageException {
            this.store = store;
            this.head = head;
            this.currentKeys = new int[N * 2 + 1];
            this.currentValues = new long[N * 2 + 1];
            this.result = new Couple<>();
            loadNode(findFirstLeaf(head));
        }

        /**
         * Finds the first leaf node
         *
         * @param head The map head
         * @return The first leaf node
         * @throws StorageException When an IO operation fails
         */
        private long findFirstLeaf(long head) throws StorageException {
            long current = head;
            IOAccess accessFather = null;
            IOAccess accessCurrent = store.accessR(current);
            try {
                while (true) {
                    // inspect the current node
                    boolean isLeaf = accessCurrent.readChar() == 1;
                    if (isLeaf)
                        return current;
                    current = accessCurrent.skip(2 + 4).readLong();
                    // free the father if any, rotate the accesses and access the next node
                    if (accessFather != null)
                        accessFather.close();
                    accessFather = accessCurrent;
                    accessCurrent = store.accessR(current);
                }
            } finally {
                if (accessFather != null)
                    accessFather.close();
                if (accessCurrent != null)
                    accessCurrent.close();
            }
        }

        /**
         * Loads the data from the specified node
         *
         * @param node The node to load from
         * @throws StorageException When an IO operation fails
         */
        private void loadNode(long node) throws StorageException {
            currentNode = node;
            nextIndex = -1;
            try (IOAccess access = store.accessR(currentNode)) {
                if (access.readChar() != 1)
                    throw new StorageException("Node is not a leaf");
                currentCount = access.readChar();
                for (int i = 0; i != currentCount; i++) {
                    currentKeys[i] = access.readInt();
                    currentValues[i] = access.readLong();
                }
                nextSibling = access.skip(4).readLong();
            }
            nextIndex = 0;
        }

        @Override
        public boolean hasNext() {
            return nextIndex >= 0 && nextIndex < currentCount;
        }

        @Override
        public Couple<Integer, Long> next() {
            result.x = currentKeys[nextIndex];
            result.y = currentValues[nextIndex];
            nextIndex++;
            if (nextIndex >= currentCount && nextSibling != FileStore.KEY_NULL) {
                // go to next node
                try {
                    loadNode(nextSibling);
                } catch (StorageException exception) {
                    Logging.getDefault().error(exception);
                }
            }
            return result;
        }

        @Override
        public void remove() {
            try {
                // try to remove the mapping
                // this may fail if the map was modified
                compareAndSet(store, head, result.x, result.y, FileStore.KEY_NULL);
            } catch (StorageException exception) {
                Logging.getDefault().error(exception);
            }
        }
    }
}
