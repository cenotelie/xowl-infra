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

import java.util.Arrays;
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
     * long: next sibling (non KEY_NULL indicate leaf node)
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
     * Initial size of the traversal stack
     */
    private static final int STACK_SIZE = 8;

    /**
     * Represents the info of a node
     */
    private static class Node {
        /**
         * The entry in the store for the node
         */
        public final long entry;
        /**
         * The node's version
         */
        public final int version;

        /**
         * Initializes this structure
         * @param entry The entry in the store for the node
         * @param version The node's version
         */
        public Node(long entry, int version) {
            this.entry = entry;
            this.version = version;
        }
    }

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
            currentNode = FileStore.KEY_NULL;
            try (IOAccess access = store.accessR(currentNode)) {
                // read data
                boolean isLeaf = access.skip(8 + 2).readByte() == 1;
                byte count = access.readByte();
                // read registered keys
                for (int i = 0; i != count; i++) {
                    // read entry data
                    int entryKey = access.readInt();
                    long entryPtr = access.readLong();
                    if (entryKey == key && isLeaf)
                        // found the key
                        return entryPtr;
                    if (key < entryKey) {
                        // go through this node
                        currentNode = entryPtr;
                        break;
                    }
                }
                if (currentNode == FileStore.KEY_NULL) {
                    // read last pointer
                    access.skip(4).readLong();
                    currentNode = access.readLong();
                }
            }
        }
        // no result found
        return FileStore.KEY_NULL;
    }

    /**
     * Atomically replace a value in the map for a key
     *
     * @param store The containing store
     * @param head  The entry for the stage 2 root node
     * @param key The key
     * @param valueOld The old value to replace (FileStore.KEY_NULL, if this is expected to be an insertion)
     * @param valueNew The new value for the key (FileStore.KEY_NULL, if this is expected to be a removal)
     * @return Whether the operation succeeded
     * @throws StorageException When an IO operation fails
     */
    public static boolean compareAndSet(FileStore store, long head, long key, long valueOld, long valueNew) throws StorageException {

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
        while (true) {
            long[] stack = traversePath(store, head, key, true);
            if (stack == null)
                // failed to resolve the path
                continue;
            // find the top of the stack
            int top = 1;
            while (top - 1 < stack.length && stack[top] != 0)
                top++;
            // try to put the value
            try (IOAccess access = store.accessW(stack[top])) {
                char version = access.seek(8).readChar();
                if (version != stack[0])
                    // the node was modified
                    continue;
                char count = access.readChar();
                char flags = access.readChar();
                for (int i = 0; i != count; i++) {
                    // read entry data
                    int entryKey = access.readInt();
                    long entryPtr = access.readLong();
                    // is this a key of interest
                    if (entryKey == key && (flags >>> i) != 0) {
                        // this is a hit on the key and this is the associated value
                        // success!
                        access.seek(NODE_HEADER + i * CHILD_SIZE + 4).writeLong(value);
                        return entryPtr;
                    }
                }
                // add into the node
                access.seek(NODE_HEADER + count * CHILD_SIZE);
                access.writeInt(key);
                access.writeLong(value);
                version++;
                flags = (char) (flags | (1 << count));
                count++;
                access.seek(8);
                access.writeChar(version);
                access.writeChar(count);
                access.writeChar(flags);
            }
            return FileStore.KEY_NULL;
        }
    }

    /**
     * Tries to allocate an entry in the B+ tree for the specified key
     *
     * @param store The containing store
     * @param head  The entry for the stage 2 root node
     * @param key   The stage 2 key to store
     * @return The info of the target node, or null if the operation fails
     * @throws StorageException When an IO operation fails
     */
    private static Node putAllocate(FileStore store, long head, int key) throws StorageException {
        long[] stack = new long[STACK_SIZE];
        int stackTop = 0;
        stack[stackTop] = head;

        while (true) {
            char version;
            boolean isLeaf;
            byte count;
            long next = FileStore.KEY_NULL;
            try (IOAccess access = store.accessR(stack[stackTop])) {
                // read header
                version = access.seek(8).readChar();
                isLeaf = access.readByte() == 1;
                count = access.readByte();
                // read registered keys
                for (int i = 0; i != count; i++) {
                    // read entry data
                    int entryKey = access.readInt();
                    long entryPtr = access.readLong();
                    if (entryKey == key && isLeaf) {
                        // this is a hit on the key and this is the associated value
                        return new Node(stack[stackTop], (int) version);
                    }
                    if (key < entryKey) {
                        // go through this node
                        next = entryPtr;
                        break;
                    }
                }
                if (next == FileStore.KEY_NULL) {
                    // read last pointer
                    access.skip(4).readLong();
                    next = access.readLong();
                }
            }
            // do we have a next node?
            if (next != FileStore.KEY_NULL) {
                stackTop++;
                if (stackTop == stack.length)
                    stack = Arrays.copyOf(stack, stack.length + STACK_SIZE);
                stack[stackTop] = next;
                continue;
            }
            // here we did not find the key, or an appropriate descendant
            if (count < ORDER - 1) {
                // we can insert in the node at the top of the stack
                try (IOAccess accessW = store.accessR(stack[stackTop])) {

                }
            }
            // ok, now we must split the top node
            // TODO: split here
        }
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
