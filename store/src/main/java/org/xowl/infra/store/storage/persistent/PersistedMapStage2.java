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
 * Stage 2 is a B+ tree.
 *
 * @author Laurent Wouters
 */
class PersistedMapStage2 {
    /**
     * The maximum number of children for a B+ tree node
     */
    private static final int ORDER = 32;
    /**
     * The size of a child entry in a B+ tree node:
     * int: key value
     * long: pointer to child
     */
    private static final int CHILD_SIZE = 4 + 8;
    /**
     * The size of a B+ tree inner node header
     * long: parent node
     * char: node version
     * byte: leaf node marker (whether the node is a leaf node, i.e. it contains the map data)
     * byte: number of keys in the node
     */
    private static final int NODE_HEADER = 8 + 2 + 1 + 1;
    /**
     * The size of a B+ tree node in stage 2:
     * header: the node header
     * entries[entryCount]: the children entries
     */
    private static final int NODE_SIZE = NODE_HEADER + ORDER * CHILD_SIZE;

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
            access.writeLong(FileStore.KEY_NULL);
            access.writeInt(0);
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
        long currentNode = head;
        while (currentNode != FileStore.KEY_NULL) {
            long next = FileStore.KEY_NULL;
            try (IOAccess access = store.accessR(currentNode)) {
                // read data
                boolean isLeaf = access.skip(8 + 2).readByte() == 1;
                byte count = access.readByte();
                // read registered keys
                for (int i = 0; i != count; i++) {
                    // read entry data
                    int entryKey = access.readInt();
                    long entryPtr = access.readLong();
                    if (isLeaf && entryKey == key)
                        // found the key
                        return entryPtr;
                    if (key < entryKey) {
                        // go through this child
                        next = entryPtr;
                        break;
                    }
                }
                if (isLeaf)
                    // did not find the key, stop here
                    return FileStore.KEY_NULL;
                if (next == FileStore.KEY_NULL) {
                    // not a leaf (internal node) and did not find a descendant
                    // read last pointer
                    next = access.skip(4).readLong();
                }
                currentNode = next;
            }
        }
        // no result found
        return FileStore.KEY_NULL;
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
        if (valueNew != FileStore.KEY_NULL) {
            return compareAndSwap(store, head, key, valueOld, valueNew);
        } else {
            return compareAndRemove(store, head, key, valueOld);
        }
    }

    /**
     * Atomically compare and swap a key-value pair for a new one (or insert the new one)
     *
     * @param store    The containing store
     * @param head     The entry for the stage 2 root node
     * @param key      The key
     * @param valueOld The old value to replace (FileStore.KEY_NULL, if this is expected to be an insertion)
     * @param valueNew The new value for the key (FileStore.KEY_NULL, if this is expected to be a removal)
     * @return Whether the operation succeeded
     * @throws StorageException When an IO operation fails
     */
    private static boolean compareAndSwap(FileStore store, long head, int key, long valueOld, long valueNew) throws StorageException {
        while (true) {
            Node node = findNode(store, head, key, true);
            try (IOAccess access = store.accessW(node.key)) {
                char nodeVersion = access.seek(8).readChar();
                if (nodeVersion != node.version)
                    // the node changed, retry
                    continue;
                byte count = access.skip(1).readByte();
                for (int i = 0; i != count; i++) {
                    int entryKey = access.readInt();
                    long entryValue = access.readLong();
                    if (entryKey == key) {
                        // this is the key
                        if (entryValue != valueOld)
                            return false;
                        access.skip(-8).writeLong(valueNew);
                        return true;
                    }
                }
            }
        }
    }

    /**
     * Atomically compare and remove a key-value pair
     *
     * @param store    The containing store
     * @param head     The entry for the stage 2 root node
     * @param key      The key
     * @param valueOld The old value to replace (FileStore.KEY_NULL, if this is expected to be an insertion)
     * @return Whether the operation succeeded
     * @throws StorageException When an IO operation fails
     */
    private static boolean compareAndRemove(FileStore store, long head, int key, long valueOld) throws StorageException {
        while (true) {
            Node node = findNode(store, head, key, false);
            if (node == null)
                // the key is not in this map ...
                return (valueOld == FileStore.KEY_NULL);
            byte count;
            try (IOAccess access = store.accessW(node.key)) {
                char nodeVersion = access.seek(8).readChar();
                if (nodeVersion != node.version)
                    // the node changed, retry
                    continue;
                count = access.skip(1).readByte();
                int toRemoveIndex = -1;
                for (int i = 0; i != count; i++) {
                    int entryKey = access.readInt();
                    long entryValue = access.readLong();
                    if (entryKey == key) {
                        // this is the key
                        if (entryValue != valueOld)
                            return false;
                        toRemoveIndex = i;
                        break;
                    }
                }
                if (toRemoveIndex == -1)
                    continue;
                // repack entries
                for (int i = toRemoveIndex + 1; i != count + 1; i++) {
                    // read the child data
                    access.seek(NODE_HEADER + i * CHILD_SIZE);
                    int entryKey = access.readInt();
                    long entryValue = access.readLong();
                    // write back
                    access.skip(-CHILD_SIZE * 2);
                    access.writeInt(entryKey);
                    access.writeLong(entryValue);
                }
                nodeVersion++;
                count--;
                access.seek(8).writeChar(nodeVersion);
                access.seek(1).writeByte(count);
            }
            if (count < ORDER / 2 - 1)
                merge(store, node.key);
            return true;
        }
    }

    /**
     * Merges a node with its neighbour
     *
     * @param store The containing store
     * @param node  The node to merge
     * @throws StorageException When an IO operation fails
     */
    private static void merge(FileStore store, long node) throws StorageException {

    }

    /**
     * Represents the data of a node
     */
    private static class Node {
        /**
         * The key to the node in the store
         */
        public final long key;
        /**
         * The node's version
         */
        public final char version;

        /**
         * Initializes this structure
         *
         * @param key     The key to the node in the store
         * @param version The node's version
         */
        public Node(long key, char version) {
            this.key = key;
            this.version = version;
        }
    }

    /**
     * Finds the node for a key
     *
     * @param store   The containing store
     * @param head    The entry for the stage 2 root node
     * @param key     The stage 2 key to store
     * @param resolve Whether to resolve the nodes for the key (insert operation)
     * @return The node that contains the key, or null if there isn't one
     * @throws StorageException When an IO operation fails
     */
    private static Node findNode(FileStore store, long head, int key, boolean resolve) throws StorageException {
        while (true) {
            Node result = findNodeTry(store, head, key, resolve);
            if (result != null || !resolve)
                // we did find the node, or we did not had to resolve it
                return result;
        }
    }

    /**
     * Tries to find a node for the specified key
     *
     * @param store   The containing store
     * @param head    The entry for the stage 2 root node
     * @param key     The stage 2 key to store
     * @param resolve Whether to resolve the nodes for the key (insert operation)
     * @return The node that contains the key, or null if there isn't one, or the operation failed
     * @throws StorageException When an IO operation fails
     */
    private static Node findNodeTry(FileStore store, long head, int key, boolean resolve) throws StorageException {
        long currentNode = head;
        while (currentNode != FileStore.KEY_NULL) {
            long next = FileStore.KEY_NULL;
            try (IOAccess access = store.accessR(currentNode)) {
                // read data
                char version = access.seek(8).readChar();
                boolean isLeaf = access.readByte() == 1;
                byte count = access.readByte();
                // read registered keys
                for (int i = 0; i != count; i++) {
                    // read entry data
                    int entryKey = access.readInt();
                    long entryPtr = access.readLong();
                    if (isLeaf && entryKey == key)
                        // found the key
                        return new Node(currentNode, version);
                    if (key < entryKey) {
                        // go through this child
                        next = entryPtr;
                        break;
                    }
                }
                if (isLeaf) {
                    // did not find the key
                    if (!resolve)
                        return null;
                    return insertInLeaf(store, currentNode, version, key);
                }
                if (next == FileStore.KEY_NULL) {
                    // not a leaf (internal node) and did not find a descendant
                    // read last pointer
                    next = access.skip(4).readLong();
                }
                currentNode = next;
            }
        }
        return null;
    }

    /**
     * Tries to insert the key into the specified leaf node
     *
     * @param store   The containing store
     * @param node    The leaf node to insert in
     * @param version The expected version of the node
     * @param key     The key to insert
     * @return The node's data, or null if the operation failed
     * @throws StorageException When an IO operation fails
     */
    private static Node insertInLeaf(FileStore store, long node, char version, int key) throws StorageException {
        try (IOAccess access = store.accessW(node)) {
            // read data
            char nodeVersion = access.seek(8).readChar();
            if (nodeVersion != version)
                return null;
            byte count = access.skip(1).readByte();
            if (count < ORDER - 1) {
                doInsertKV(access, version, count, key, FileStore.KEY_NULL);
                return new Node(node, ++version);
            }
        }
        // split the node

    }

    /**
     * Effectively perform the insertion of a key-value couple in an accessed node
     *
     * @param access  The access for the node
     * @param version The current version of the node
     * @param count   The current number of keys in the node
     * @param key     The key to insert
     * @param value   The associated value
     * @throws StorageException When an IO operation fails
     */
    private static void doInsertKV(IOAccess access, char version, byte count, int key, long value) throws StorageException {
        access.seek(NODE_HEADER);
        // find the index where to insert
        int insertBefore = count;
        for (int i = 0; i != count; i++) {
            int entryKey = access.readInt();
            access.seek(8);
            if (entryKey > key) {
                insertBefore = i;
                break;
            }
        }
        // move the data on the right
        for (int i = count; i != insertBefore - 1; i--) {
            access.seek(NODE_HEADER + i * CHILD_SIZE);
            int entryKey = access.readInt();
            long entryValue = access.readLong();
            access.writeInt(entryKey);
            access.writeLong(entryValue);
        }
        // write the new key value pair
        access.seek(NODE_HEADER + insertBefore * CHILD_SIZE);
        access.writeInt(key);
        access.writeLong(value);
        // update version and count
        version++;
        count++;
        access.seek(8).writeChar(version);
        access.seek(1).writeByte(count);
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
