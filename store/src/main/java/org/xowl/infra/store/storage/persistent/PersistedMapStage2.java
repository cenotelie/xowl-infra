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
    private static final int NODE_CHILDREN = 8;
    /**
     * The size of a child entry in a B+ tree node:
     * int: key
     * long: pointer to child
     */
    private static final int NODE_ENTRY_SIZE = 4 + 8;
    /**
     * The size of a B+ tree node header in stage 2:
     * long: next sibling
     * char: node version
     * char: number of entries
     * char: entry flags for internal/external nodes
     */
    private static final int NODE_HEADER_SIZE = 8 + 2 + 2 + 2;
    /**
     * The size of a B+ tree node in stage 2:
     * header: the node header
     * entries[entryCount]: the children entries
     */
    private static final int NODE_SIZE = NODE_HEADER_SIZE + NODE_CHILDREN * NODE_ENTRY_SIZE;
    /**
     * Initial size of the traversal stack
     */
    private static final int STACK_SIZE = 8;

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
            access.writeInt(0);
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
            try (IOAccess access = store.read(currentNode)) {
                char count = access.seek(8 + 2).readChar();
                char flags = access.readChar();
                for (int i = 0; i != count; i++) {
                    // read entry data
                    int entryKey = access.readInt();
                    long entryPtr = access.readLong();
                    // is this a key of interest
                    if (entryKey == key && (flags >>> i) != 0) {
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
            try (IOAccess access = store.access(stack[top])) {
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
                        access.seek(NODE_HEADER_SIZE + i * NODE_ENTRY_SIZE + 4).writeLong(value);
                        return entryPtr;
                    }
                }
                // add into the node
                access.seek(NODE_HEADER_SIZE + count * NODE_ENTRY_SIZE);
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
     * Travers the B+ tree and build the stack of the nodes when looking up a key
     * The path is an array of the nodes beginning at index 1.
     * Index 0 of the array contains the version of the top node.
     *
     * @param store   The containing store
     * @param head    The entry for the stage 2 root node
     * @param key     The stage 2 key to store
     * @param resolve Whether to resolve the path when not existing
     * @return The path
     * @throws StorageException When an IO operation fails
     */
    private static long[] traversePath(FileStore store, long head, int key, boolean resolve) throws StorageException {
        long[] stack = new long[STACK_SIZE];
        int stackTop = 1;
        stack[stackTop] = head;

        while (true) {
            char version;
            char count;
            char flags;
            try (IOAccess access = store.read(stack[stackTop])) {
                version = access.seek(8).readChar();
                count = access.readChar();
                flags = access.readChar();
                for (int i = 0; i != count; i++) {
                    // read entry data
                    int entryKey = access.readInt();
                    long entryPtr = access.readLong();
                    // is this a key of interest
                    if (entryKey == key && (flags >>> i) != 0) {
                        // this is a hit on the key and this is the associated value
                        stack[0] = version;
                        return stack;
                    } else if (key < entryKey) {
                        // we must go down this node
                        stackTop++;
                        if (stackTop == stack.length)
                            stack = Arrays.copyOf(stack, stack.length + NODE_CHILDREN);
                        stack[stackTop] = entryPtr;
                        break;
                    }
                }
            }
            // here we did not find the key, or an appropriate descendant
            if (!resolve)
                // do not resolve, no path found
                return null;
            if (count < NODE_CHILDREN) {
                // we can insert in the node at the top of the stack
                stack[0] = version;
                return stack;
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
