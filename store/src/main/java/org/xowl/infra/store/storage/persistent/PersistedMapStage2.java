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
 * Utility API for the second stage of a persisted map
 * Persisted maps map long to long. Stage 1 maps the upper bits of the key and stage 2 the lower bits.
 * Stage 2 is a B+ tree with Preparatory Operations.
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
class PersistedMapStage2 {
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
    private static final int CHILD_SIZE = 4 + 8;
    /**
     * The size of a B+ tree inner node header
     * char: leaf marker
     * char: number of keys
     */
    private static final int NODE_HEADER = 2 + 2;
    /**
     * The size of a B+ tree node in stage 2:
     * header: the node header
     * entries[entryCount]: the children entries
     */
    private static final int NODE_SIZE = NODE_HEADER + CHILD_COUNT * CHILD_SIZE;

    /**
     * Initializes an empty stage 2 map
     *
     * @param store The containing store
     * @return The entry key for the root of the stage 2 map
     * @throws StorageException When an IO operation fails
     */
    public static long newMap(FileStore store) throws StorageException {
        long entry = store.allocate(NODE_SIZE);
        try (IOAccess access = store.accessW(entry)) {
            // write header
            access.writeChar((char) 1);
            access.writeChar((char) 0);
            // write last entry to non-existing node
            access.writeInt(0);
            access.writeLong(FileStore.KEY_NULL);
        }
        return entry;
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
        IOAccess accessFather = null;
        IOAccess accessCurrent = store.accessR(head);
        try {
            while (true) {
                // inspect the current node
                boolean isLeaf = accessCurrent.readChar() == 1;
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
     * @throws StorageException When an IO operation fails
     */
    private static long getOnNode(IOAccess accessCurrent, int key, char count) throws StorageException {
        for (int i = 0; i != count; i++) {
            // read entry data
            int entryKey = accessCurrent.readInt();
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
     * @throws StorageException When an IO operation fails
     */
    private static long getChild(IOAccess accessCurrent, int key, char count) throws StorageException {
        for (int i = 0; i != count; i++) {
            // read entry data
            int entryKey = accessCurrent.readInt();
            long entryPtr = accessCurrent.readLong();
            if (key < entryKey) {
                // go through this child
                return entryPtr;
            }
        }
        return accessCurrent.skip(4).readLong();
    }

    /**
     * Atomically replace a value in the map for a key
     *
     * @param store    The containing store
     * @param head     The entry for the stage 2 root node
     * @param key      The key
     * @param valueOld The old value to replace (FileStore.KEY_NULL, if this is expected to be an insertion)
     * @param valueNew The new value for the key (FileStore.KEY_NULL, if this is expected to be a removal)
     * @return Whether the operation succeeded
     * @throws StorageException When an IO operation fails
     */
    public static boolean compareAndSet(FileStore store, long head, int key, long valueOld, long valueNew) throws StorageException {
        IOAccess accessFather = null;
        IOAccess accessCurrent = store.accessW(head);
        inspect(store, null, accessCurrent, head, valueNew != FileStore.KEY_NULL);
        try {
            while (true) {
                // inspect the current node
                boolean isLeaf = accessCurrent.reset().readChar() == 1;
                char count = accessCurrent.readChar();
                if (isLeaf) {
                    // look into the entries of this node
                    for (int i = 0; i != count; i++) {
                        int entryKey = accessCurrent.readInt();
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
                boolean hasChanged = inspect(store, accessCurrent, accessChild, child, valueNew != FileStore.KEY_NULL);
                while (hasChanged) {
                    accessChild.close();
                    count = accessCurrent.seek(2).readChar();
                    child = getChild(accessCurrent, key, count);
                    accessChild = store.accessW(child);
                    hasChanged = inspect(store, accessCurrent, accessChild, child, valueNew != FileStore.KEY_NULL);
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
     * @throws StorageException When an IO operation fails
     */
    private static boolean doCompareAndReplace(IOAccess accessCurrent, int index, char count, long entryValue, long valueOld, long valueNew) throws StorageException {
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
                int key = accessCurrent.readInt();
                long value = accessCurrent.readLong();
                accessCurrent.skip(-CHILD_SIZE * 2);
                accessCurrent.writeInt(key);
                accessCurrent.writeLong(value);
            }
            // update the header
            count--;
            accessCurrent.seek(2).writeChar(count);
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
     * @throws StorageException When an IO operation fails
     */
    private static boolean doInsert(IOAccess accessCurrent, char count, int key, long valueOld, long valueNew) throws StorageException {
        if (valueNew != FileStore.KEY_NULL) {
            // this is an insertion
            if (valueOld != FileStore.KEY_NULL)
                // expected a value
                return false;
            // find the index to insert at
            int insertAt = count;
            accessCurrent.seek(NODE_HEADER);
            for (int i = 0; i != count; i++) {
                int entryKey = accessCurrent.readInt();
                accessCurrent.skip(8);
                if (entryKey > key) {
                    insertAt = i;
                    break;
                }
            }
            // shift the existing data to the right
            for (int i = count; i != insertAt - 1; i--) {
                accessCurrent.seek(NODE_HEADER + i * CHILD_SIZE);
                int entryKey = accessCurrent.readInt();
                long entryValue = accessCurrent.readLong();
                accessCurrent.writeInt(entryKey);
                accessCurrent.writeLong(entryValue);
            }
            // write the inserted key-value
            accessCurrent.seek(NODE_HEADER + insertAt * CHILD_SIZE);
            accessCurrent.writeInt(key);
            accessCurrent.writeLong(valueNew);
            // update the header
            count++;
            accessCurrent.seek(2).writeChar(count);
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
     * @param store         The containing store
     * @param accessFather  The access to the parent node
     * @param accessCurrent The access to the current node
     * @param current       The entry for the node to split
     * @param isInsert      Whether this is an insert operation
     * @return Whether the tree was modified
     * @throws StorageException When an IO operation fails
     */
    private static boolean inspect(FileStore store, IOAccess accessFather, IOAccess accessCurrent, long current, boolean isInsert) throws StorageException {
        boolean isLeaf = accessCurrent.reset().readChar() == 1;
        char count = accessCurrent.readChar();
        if (isInsert && count >= 2 * N) {
            // split the current node
            if (accessFather == null) {
                if (isLeaf)
                    splitRootLeaf(store, accessCurrent, count);
                else
                    splitRootInternal(store, accessCurrent, count);
            } else if (isLeaf)
                splitLeaf(store, accessFather, accessCurrent, current, count);
            else
                splitInternal(store, accessFather, accessCurrent, current, count);
            return true;
        } else if (!isInsert && count <= N) {
            // TODO: do merge
            return true;
        }
        return false;
    }

    /**
     * Splits the root node when it is an internal node
     *
     * @param store         The containing store
     * @param accessCurrent The access to the current node
     * @param count         The number of children in the current node
     * @throws StorageException When an IO operation fails
     */
    private static void splitRootInternal(FileStore store, IOAccess accessCurrent, char count) throws StorageException {
        // number of keys to transfer to the right (not including the fallback pointer)
        int countRight = (count == 2 * N) ? N - 1 : N;
        long left = store.allocate(NODE_SIZE);
        long right = store.allocate(NODE_SIZE);
        int maxLeft;
        // write the new left node
        accessCurrent.seek(NODE_HEADER);
        try (IOAccess accessLeft = store.accessW(left)) {
            accessLeft.writeChar((char) 0);
            accessLeft.writeChar((char) N);
            for (int i = 0; i != N; i++) {
                accessLeft.writeInt(accessCurrent.readInt());
                accessLeft.writeLong(accessCurrent.readLong());
            }
            maxLeft = accessCurrent.readInt();
            accessLeft.writeInt(0);
            accessLeft.writeLong(accessCurrent.readLong());
        }
        // write the new right node
        try (IOAccess accessRight = store.accessW(right)) {
            accessRight.writeChar((char) 0);
            accessRight.writeChar((char) countRight);
            for (int i = 0; i != countRight + 1; i++) {
                accessRight.writeInt(accessCurrent.readInt());
                accessRight.writeLong(accessCurrent.readLong());
            }
        }
        // rewrite the root node
        accessCurrent.reset();
        accessCurrent.writeChar((char) 0);
        accessCurrent.writeChar((char) 1); // only one key
        accessCurrent.writeInt(maxLeft);
        accessCurrent.writeLong(left);
        accessCurrent.writeInt(0);
        accessCurrent.writeLong(right);
    }

    /**
     * Splits the root node when it is a leaf
     *
     * @param store         The containing store
     * @param accessCurrent The access to the current node
     * @param count         The number of children in the current node
     * @throws StorageException When an IO operation fails
     */
    private static void splitRootLeaf(FileStore store, IOAccess accessCurrent, char count) throws StorageException {
        // number of keys to transfer to the right (not including the neighbour pointer)
        int countRight = (count == 2 * N) ? N : N + 1;
        long left = store.allocate(NODE_SIZE);
        long right = store.allocate(NODE_SIZE);
        // write the new left node
        accessCurrent.seek(NODE_HEADER);
        try (IOAccess accessLeft = store.accessW(left)) {
            accessLeft.writeChar((char) 1);
            accessLeft.writeChar((char) N);
            for (int i = 0; i != N; i++) {
                accessLeft.writeInt(accessCurrent.readInt());
                accessLeft.writeLong(accessCurrent.readLong());
            }
            accessLeft.writeInt(0);
            accessLeft.writeLong(right);
        }
        // write the new right node
        int rightFirstKey = 0;
        try (IOAccess accessRight = store.accessW(right)) {
            accessRight.writeChar((char) 1);
            accessRight.writeChar((char) countRight);
            for (int i = 0; i != countRight; i++) {
                int key = accessCurrent.readInt();
                if (i == 0)
                    rightFirstKey = key;
                accessRight.writeInt(key);
                accessRight.writeLong(accessCurrent.readLong());
            }
            accessRight.writeInt(0);
            accessRight.writeLong(FileStore.KEY_NULL);
        }
        // rewrite the root node
        accessCurrent.reset();
        accessCurrent.writeChar((char) 0);
        accessCurrent.writeChar((char) 1); // only one key
        accessCurrent.writeInt(rightFirstKey);
        accessCurrent.writeLong(left);
        accessCurrent.writeInt(0);
        accessCurrent.writeLong(right);
    }

    /**
     * Splits an internal node (that is not the root)
     *
     * @param store         The containing store
     * @param accessFather  The access to the parent node
     * @param accessCurrent The access to the current node
     * @param current       The entry for the node to split
     * @param count         The number of children in the current node
     * @throws StorageException When an IO operation fails
     */
    private static void splitInternal(FileStore store, IOAccess accessFather, IOAccess accessCurrent, long current, char count) throws StorageException {
        // number of keys to transfer to the right (not including the fallback child pointer)
        int countRight = (count == 2 * N) ? N - 1 : N;
        accessCurrent.seek(NODE_HEADER + (N + 1) * CHILD_SIZE);
        long right = store.allocate(NODE_SIZE);
        try (IOAccess accessRight = store.accessW(right)) {
            accessRight.writeChar((char) 0);
            accessRight.writeChar((char) countRight);
            // write the transferred key-values and the neighbour pointer
            for (int i = 0; i != countRight + 1; i++) {
                accessRight.writeInt(accessCurrent.readInt());
                accessRight.writeLong(accessCurrent.readLong());
            }
        }
        // update the data for the split node
        accessCurrent.seek(2).writeChar((char) N);
        int maxLeft = accessCurrent.seek(NODE_HEADER + N * CHILD_SIZE).readInt();
        accessCurrent.skip(-4).writeInt(0);
        // update the data for the parent
        splitInsertInParent(accessFather, current, right, maxLeft);
    }

    /**
     * Splits a leaf node (that is not the root)
     *
     * @param store         The containing store
     * @param accessFather  The access to the parent node
     * @param accessCurrent The access to the current node
     * @param current       The entry for the node to split
     * @param count         The number of children in the current node
     * @throws StorageException When an IO operation fails
     */
    private static void splitLeaf(FileStore store, IOAccess accessFather, IOAccess accessCurrent, long current, char count) throws StorageException {
        // number of keys to transfer to the right (not including the neighbour pointer)
        int countRight = (count == 2 * N) ? N : N + 1;
        accessCurrent.seek(NODE_HEADER + N * CHILD_SIZE);
        long right = store.allocate(NODE_SIZE);
        int rightFirstKey = 0;
        try (IOAccess accessRight = store.accessW(right)) {
            accessRight.writeChar((char) 1);
            accessRight.writeChar((char) countRight);
            // write the transferred key-values and the neighbour pointer
            for (int i = 0; i != countRight + 1; i++) {
                int key = accessCurrent.readInt();
                long value = accessCurrent.readLong();
                if (i == 0)
                    rightFirstKey = key;
                accessRight.writeInt(key);
                accessRight.writeLong(value);
            }
        }
        // update the data for the split node
        accessCurrent.seek(2).writeChar((char) N);
        accessCurrent.seek(NODE_HEADER + N * CHILD_SIZE);
        accessCurrent.writeInt(0);
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
     * @throws StorageException When an IO operation fails
     */
    private static void splitInsertInParent(IOAccess access, long leftNode, long rightNode, int rightFirstKey) throws StorageException {
        char count = access.seek(2).readChar();
        // find the index where to insert
        int insertAt = count;
        int originalKey = 0;
        for (int i = 0; i != count; i++) {
            int key = access.readInt();
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
            int entryKey = access.readInt();
            long entryValue = access.readLong();
            access.writeInt(entryKey);
            access.writeLong(entryValue);
        }
        // write the new pointers
        access.seek(NODE_HEADER + insertAt * CHILD_SIZE);
        access.writeInt(rightFirstKey);
        access.writeLong(leftNode);
        access.writeInt(originalKey);
        access.writeLong(rightNode);
        // update count
        count++;
        access.seek(2).writeChar(count);
    }

    /**
     * Clears the stage 2 map
     *
     * @param store The containing store
     * @param head  The entry for the stage 2 root node
     * @throws StorageException When an IO operation fails
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
