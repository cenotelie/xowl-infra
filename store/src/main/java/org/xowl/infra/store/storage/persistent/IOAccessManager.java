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

import org.xowl.infra.utils.logging.Logging;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLongArray;

/**
 * Manages the concurrent accesses onto a single IO backend
 * This structure is thread safe and lock-free.
 *
 * @author Laurent Wouters
 */
class IOAccessManager {
    /**
     * The size of the access pool
     */
    private static final int ACCESSES_POOL_SIZE = 64;
    /**
     * The index of the head of the list of active accesses
     */
    private static final int ACTIVE_HEAD_ID = 0;
    /**
     * The index of the tail of the list of active accesses
     */
    private static final int ACTIVE_TAIL_ID = 1;

    /**
     * Represents an access managed by this structure
     */
    private class Access extends IOAccess {
        /**
         * The identifier of this access
         */
        private final int identifier;

        /**
         * Initializes this element
         *
         * @param identifier The identifier of this access
         */
        protected Access(int identifier) {
            this.identifier = identifier;
        }

        @Override
        public void close() {
            IOAccessManager.this.onAccessEnd(this);
        }
    }

    /**
     * The backend element that is protected by this manager
     */
    private final IOBackend backend;
    /**
     * The pool of existing accesses in the manager
     */
    private final Access[] accesses;
    /**
     * The current number of accesses in the pool
     */
    private final AtomicInteger accessesCount;
    /**
     * The state of the accesses managed by this structure
     * The state of an access is composed of:
     * - int: key (access location)
     * - short: version (increment at each change)
     * - byte: when this access is free: the index of the next free access
     * when this access is in use: FF for a logically deleted access, or 00
     * - byte: the index of the next active access, or FF if the access is not active
     */
    private final AtomicLongArray accessesState;
    /**
     * The index of the next free access
     */
    private final AtomicInteger accessesFree;
    /**
     * The total number of accesses
     */
    private long totalAccesses;
    /**
     * The total number of tries for all accesses
     */
    private long totalTries;
    /**
     * The timestamp for the last update of the contention statistics
     */
    private long statisticsTimestamp;

    /**
     * Gets the mean number of tries for performing an access operation
     *
     * @return The mean number of tries
     */
    public long getStatisticsContention() {
        long timestamp = System.nanoTime();
        if (timestamp >= statisticsTimestamp + FileStatistics.REFRESH_PERIOD) {
            totalAccesses = 0;
            totalTries = 0;
            statisticsTimestamp = timestamp;
        }
        return totalAccesses == 0 ? 1 : totalTries / totalAccesses;
    }

    /**
     * Gets the current mean of number of accesses per second
     *
     * @return The mean number of accesses per second
     */
    public long getStatisticsAccessPerSecond() {
        long timestamp = System.nanoTime();
        if (timestamp >= statisticsTimestamp + FileStatistics.REFRESH_PERIOD) {
            totalAccesses = 0;
            totalTries = 0;
            statisticsTimestamp = timestamp;
        }
        return totalAccesses * (1000000000 / FileStatistics.REFRESH_PERIOD);
    }

    /**
     * Initializes this pool
     *
     * @param backend The backend element that is protected by this manager
     */
    public IOAccessManager(IOBackend backend) {
        this.backend = backend;
        this.accesses = new Access[ACCESSES_POOL_SIZE];
        this.accesses[ACTIVE_HEAD_ID] = new Access(ACTIVE_HEAD_ID);
        this.accesses[ACTIVE_HEAD_ID].setupIOData(0, 0, false);
        this.accesses[ACTIVE_TAIL_ID] = new Access(ACTIVE_TAIL_ID);
        this.accesses[ACTIVE_TAIL_ID].setupIOData(Integer.MAX_VALUE, 0, false);
        this.accessesCount = new AtomicInteger(2);
        this.accessesState = new AtomicLongArray(ACCESSES_POOL_SIZE);
        this.accessesState.set(ACTIVE_HEAD_ID, 0x0000000000000001L);
        this.accessesState.set(ACTIVE_TAIL_ID, 0x7FFFFFFF00000001L);
        this.accessesFree = new AtomicInteger(ACTIVE_HEAD_ID);
        this.totalAccesses = 0;
        this.totalTries = 0;
        this.statisticsTimestamp = System.nanoTime();
    }

    /**
     * Unsigned promotion of an integer to a long
     * Replacement for Integer.toUnsignedLong when using JDK 7
     *
     * @param i The integer
     * @return The resulting long
     */
    private static long ul(int i) {
        return (long) i & 0xFFFFFFFFL;
    }

    /**
     * Gets the key for an access in the specified state
     *
     * @param state The state of an access
     * @return The key for the access
     */
    private static int stateKey(long state) {
        return (int) (state >>> 32);
    }

    /**
     * Gets whether the state of an access indicates that the access is logically removed from the active list
     *
     * @param state The state of an access
     * @return Whether the access is logically removed from the active list
     */
    private static boolean stateIsLogicallyRemoved(long state) {
        return (state & 0x000000000000FF00L) == 0x000000000000FF00L;
    }

    /**
     * When the access is free, gets the index of the next free access
     *
     * @param state The state of an access
     * @return The index of the next free access
     */
    private static int stateFreeNext(long state) {
        return (int) ((state & 0x000000000000FF00L) >>> 8);
    }

    /**
     * Gets the index of the next active access
     *
     * @param state The state of an access
     * @return The index of the next active access
     */
    private static int stateActiveNext(long state) {
        return (int) (state & 0x00000000000000FFL);
    }

    /**
     * Setups the state of a new active access
     *
     * @param state      The current state
     * @param key        The new key for the access
     * @param nextActive The index of the next active access
     * @return The new state
     */
    private static long stateSetupActive(long state, int key, int nextActive) {
        int version = (int) ((state & 0x00000000FFFF0000L) >> 16) + 1;
        return ul(key) << 32 | ul(version & 0xFFFF) << 16 | nextActive;
    }

    /**
     * Gets the marked version of the state for the specified one
     *
     * @param state The initial state of an access
     * @return The equivalent marked state
     */
    private static long stateSetMark(long state) {
        long base = state & 0xFFFFFFFF000000FFL;
        int version = (int) ((state & 0x00000000FFFF0000L) >> 16) + 1;
        return base | ul(version & 0xFFFF) << 16 | 0x000000000000FF00L;
    }

    /**
     * Gets the new state with a new value for the next free access
     *
     * @param state The initial state of an access
     * @param next  The index of the next free access
     * @return The new state
     */
    private static long stateSetNextFree(long state, int next) {
        long base = state & 0xFFFFFFFF000000FFL;
        int version = (int) ((state & 0x00000000FFFF0000L) >> 16) + 1;
        return base | ul(version & 0xFFFF) << 16 | ul(next << 8);
    }

    /**
     * Gets the new state with a new value for the next active access
     *
     * @param state The initial state of an access
     * @param next  The index of the next active access
     * @return The new state
     */
    private static long stateSetNextActive(long state, int next) {
        long base = state & 0xFFFFFFFF00000000L;
        int version = (int) ((state & 0x00000000FFFF0000L) >> 16) + 1;
        return base | ul(version & 0xFFFF) << 16 | ul(next);
    }

    /**
     * Searches the left and right node in the list for a place to insert the specified access and tries to insert the specified access
     *
     * @param toInsert The access to be inserted
     * @param key      The key to insert at
     * @return Whether the attempt is successful
     */
    private boolean listSearchAndInsert(int toInsert, int key) {
        Access accessToInsert = accesses[toInsert];

        while (true) {
            int leftNode = -1;
            long leftNodeState = 0;
            int rightNode;
            // start by the head
            int currentNode = ACTIVE_HEAD_ID;
            long currentNodeState = accessesState.get(currentNode);

            // 1: Find leftNode and rightNode
            do {
                if (!stateIsLogicallyRemoved(currentNodeState)) {
                    leftNode = currentNode;
                    leftNodeState = currentNodeState;
                }
                currentNode = stateActiveNext(currentNodeState);
                if (currentNode == ACTIVE_TAIL_ID)
                    break;
                if (currentNode == toInsert)
                    throw new Error("Already in list!");
                currentNodeState = accessesState.get(currentNode);
                if (!stateIsLogicallyRemoved(currentNodeState)) {
                    if (stateKey(leftNodeState) > stateKey(currentNodeState))
                        throw new Error("List is not ordered");
                    Access accessCurrentNode = accesses[currentNode];
                    if ((accessToInsert.writable || accessCurrentNode.writable) && !accessToInsert.disjoints(accessCurrentNode))
                        // there is a write overlap
                        return false;
                    if (key < stateKey(currentNodeState))
                        break;
                }
            } while (true);
            rightNode = currentNode;

            // 1 bis: look for overlapping accesses after that
            do {
                currentNode = stateActiveNext(currentNodeState);
                if (currentNode == ACTIVE_TAIL_ID)
                    break;
                if (currentNode == toInsert)
                    throw new Error("Already in list!");
                currentNodeState = accessesState.get(currentNode);
                if (!stateIsLogicallyRemoved(currentNodeState)) {
                    Access accessCurrentNode = accesses[currentNode];
                    if ((accessToInsert.writable || accessCurrentNode.writable) && !accessToInsert.disjoints(accessCurrentNode))
                        // there is a write overlap
                        return false;
                    if (stateKey(currentNodeState) >= key + accessToInsert.length)
                        break;
                }
            } while (true);

            // 2: Check nodes are adjacent
            if (stateActiveNext(leftNodeState) == rightNode) {
                if (rightNode != ACTIVE_TAIL_ID && stateIsLogicallyRemoved(accessesState.get(rightNode)))
                    continue;
                accessesState.set(toInsert, stateSetupActive(accessesState.get(toInsert), key, rightNode));
                return (accessesState.compareAndSet(leftNode, leftNodeState, stateSetNextActive(leftNodeState, toInsert)));
            }

            // 3: Remove one or more marked nodes
            if (leftNode == rightNode)
                throw new Error("oops");
            long leftNodeStateNew = stateSetNextActive(leftNodeState, rightNode);
            if (accessesState.compareAndSet(leftNode, leftNodeState, leftNodeStateNew)) {
                poolReturnSequence(stateActiveNext(leftNodeState), rightNode);
                leftNodeState = leftNodeStateNew;
                if (rightNode != ACTIVE_TAIL_ID && stateIsLogicallyRemoved(accessesState.get(rightNode)))
                    continue;
                accessesState.set(toInsert, stateSetupActive(accessesState.get(toInsert), key, rightNode));
                return (accessesState.compareAndSet(leftNode, leftNodeState, stateSetNextActive(leftNodeState, toInsert)));
            }
        }
    }

    /**
     * Inserts an access into the list of live accesses
     * The method returns only when the access is safely inserted, i.e. there is no blocking access
     *
     * @param toInsert The access to be inserted
     * @param key      The key to insert at
     * @return The number of tries
     */
    private int listInsert(int toInsert, int key) {
        int count = 1;
        while (!listSearchAndInsert(toInsert, key))
            count++;
        return count;
    }

    /**
     * Searches the left and right node in the list for the access to be removed and try to mark it as removed
     *
     * @param toRemove The access to be removed
     * @return Whether the attempt is successful
     */
    private boolean listSearchAndRemove(int toRemove) {
        while (true) {
            int leftNode = -1;
            long leftNodeState = 0;
            long toRemoveState;
            // start by the head
            int currentNode = ACTIVE_HEAD_ID;
            long currentNodeState = accessesState.get(currentNode);

            // 1: Find leftNode and rightNode
            do {
                if (!stateIsLogicallyRemoved(currentNodeState)) {
                    leftNode = currentNode;
                    leftNodeState = currentNodeState;
                }
                currentNode = stateActiveNext(currentNodeState);
                if (currentNode == ACTIVE_TAIL_ID)
                    throw new Error("WTF!");
                currentNodeState = accessesState.get(currentNode);

                if (!stateIsLogicallyRemoved(currentNodeState) && stateKey(leftNodeState) > stateKey(currentNodeState))
                    throw new Error("List is not ordered");
            } while (stateIsLogicallyRemoved(currentNodeState) || currentNode != toRemove);
            toRemoveState = currentNodeState;

            // 2: Check nodes are adjacent
            if (stateActiveNext(leftNodeState) == toRemove) {
                return accessesState.compareAndSet(toRemove, toRemoveState, stateSetMark(toRemoveState));
            }

            // 3: Remove one or more marked nodes
            if (leftNode == toRemove)
                throw new Error("oops");
            if (accessesState.compareAndSet(leftNode, leftNodeState, stateSetNextActive(leftNodeState, toRemove))) {
                poolReturnSequence(stateActiveNext(leftNodeState), toRemove);
                return accessesState.compareAndSet(toRemove, toRemoveState, stateSetMark(toRemoveState));
            }
        }
    }

    /**
     * Removes an access from the list of live accesses
     *
     * @param toRemove The access to be removed
     * @return The number of tries
     */
    private int listRemove(int toRemove) {
        int count = 1;
        while (!listSearchAndRemove(toRemove))
            count++;
        return count;
    }


    /**
     * Gets an access to the associated backend for the specified span
     *
     * @param location The location of the span within the backend
     * @param length   The length of the allowed span
     * @param writable Whether the access allows writing
     * @return The new access, or null if it cannot be obtained
     * @throws StorageException When an IO error occurs
     */
    public IOAccess get(int location, int length, boolean writable) throws StorageException {
        Access access = newAccess();
        access.setupIOData(location, length, writable);
        onAccess(listInsert(access.identifier, location));
        try {
            access.setupIOData(backend.onAccessRequested(access));
        } catch (StorageException exception) {
            onAccess(listRemove(access.identifier));
            throw exception;
        }
        return access;
    }

    /**
     * Gets an access to the associated backend for the specified span
     *
     * @param location The location of the span within the backend
     * @param length   The length of the allowed span
     * @param writable Whether the access allows writing
     * @param element  The backing IO element
     * @return The new access, or null if it cannot be obtained
     */
    public IOAccess get(int location, int length, boolean writable, IOElement element) {
        Access access = newAccess();
        access.setupIOData(location, length, writable);
        access.setupIOData(element);
        onAccess(listInsert(access.identifier, location));
        return access;
    }

    /**
     * Ends an access to the backend
     *
     * @param access The access
     */
    private void onAccessEnd(Access access) {
        try {
            backend.onAccessTerminated(access, access.element);
        } catch (StorageException exception) {
            Logging.getDefault().error(exception);
        }
        onAccess(listRemove(access.identifier));
    }

    /**
     * Resolves a free access object
     *
     * @return A free access object
     */
    private Access newAccess() {
        while (true) {
            // try to reuse an existing free access
            int nextFree = accessesFree.get();
            if (nextFree != ACTIVE_HEAD_ID) {
                long freeStateOld = accessesState.get(nextFree);
                int follower = stateFreeNext(freeStateOld);
                if (!accessesFree.compareAndSet(nextFree, follower))
                    // not fast enough!
                    continue;
                return accesses[nextFree];
            }

            // no free access
            // is the pool full
            int count = accessesCount.get();
            if (count == ACCESSES_POOL_SIZE)
                // the pool is full, retry to reuse a free access
                continue;

            if (!accessesCount.compareAndSet(count, count + 1))
                // failed to grow the pool, retry
                continue;

            accesses[count] = new Access(count);
            accessesState.set(count, 0);
            return accesses[count];
        }
    }

    /**
     * Enqueues a sequence of accesses, making them free to use again
     *
     * @param firstId  The identifier of the first access to release in the sequence
     * @param targetId The target identifier to reach, thus marking the end of the sequence
     */
    private void poolReturnSequence(int firstId, int targetId) {
        int current = firstId;
        while (true) {
            long currentState = accessesState.get(current);
            if (!stateIsLogicallyRemoved(currentState))
                throw new Error("Trying to return a non-marked access");
            poolReturnAccess(current);
            int nextId = stateActiveNext(currentState);
            if (nextId == targetId)
                break;
            current = nextId;
        }
    }

    /**
     * Enqueues a single access, making it free to use again
     *
     * @param identifier The identifier of the access to enqueue
     */
    private void poolReturnAccess(int identifier) {
        // register in the stack of free accesses
        while (true) {
            int previousFree = accessesFree.get();
            long oldState = accessesState.get(identifier);
            long newState = stateSetNextFree(oldState, previousFree);
            if (!accessesState.compareAndSet(identifier, oldState, newState))
                continue;
            if (accessesFree.compareAndSet(previousFree, identifier))
                return;
        }
    }

    /**
     * Updates the contention statistics when an access is required
     *
     * @param tries The number of tries that it took to perform the access initialization or closure
     */
    private void onAccess(int tries) {
        long timestamp = System.nanoTime();
        if (timestamp >= statisticsTimestamp + FileStatistics.REFRESH_PERIOD) {
            totalAccesses = 1;
            totalTries = tries;
            statisticsTimestamp = timestamp;
        } else {
            totalAccesses++;
            totalTries += tries;
        }
    }
}
