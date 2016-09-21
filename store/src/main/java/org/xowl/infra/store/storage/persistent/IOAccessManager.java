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
import java.util.concurrent.atomic.AtomicIntegerArray;

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
     * The state of an access is composed of
     * - byte: mark whether this access is current in the active list
     * - byte: mark whether this access is logically removed from the list of active accesses
     * - byte: the index of the next free access (when this access is free)
     * - byte: the index of the next active access (when this access is in the list of active accesses)
     */
    private final AtomicIntegerArray accessesState;
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
        this.accessesState = new AtomicIntegerArray(ACCESSES_POOL_SIZE);
        this.accessesState.set(ACTIVE_HEAD_ID, 0xFF000001);
        this.accessesState.set(ACTIVE_TAIL_ID, 0xFF000001);
        this.accessesFree = new AtomicInteger(ACTIVE_HEAD_ID);
        this.totalAccesses = 0;
        this.totalTries = 0;
        this.statisticsTimestamp = System.nanoTime();
    }

    /**
     * Gets whether the state of an access indicates that the access is currently active
     *
     * @param state The state of an access
     * @return Whether the access is active according to this state
     */
    private static boolean stateIsActive(int state) {
        return (state & 0xFF000000) == 0xFF000000;
    }

    /**
     * Gets whether the state of an access indicates that the access is marked
     *
     * @param state The state of an access
     * @return Whether the access is marked according to this state
     */
    private static boolean stateIsMarked(int state) {
        return (state & 0x00FF0000) == 0x00FF0000;
    }

    /**
     * Gets the marked version of the state for the specified one
     *
     * @param state The initial state of an access
     * @return The equivalent marked state
     */
    private static int stateSetMark(int state) {
        return state | 0x00FF0000;
    }

    /**
     * Gets the index of the next active access
     *
     * @param state The state of an access
     * @return The index of the next active access
     */
    private static int stateActiveNext(int state) {
        return state & 0x000000FF;
    }

    /**
     * Gets the new state with a new value for the next active access
     *
     * @param state The initial state of an access
     * @param next  The index of the next active access
     * @return The new state
     */
    private static int stateNewActiveNext(int state, int next) {
        return 0xFF000000 | (state & 0x00FFFF00) | next;
    }

    /**
     * Gets the index of the next free access
     *
     * @param state The state of an access
     * @return The index of the next free access
     */
    private static int stateFreeNext(int state) {
        return (state & 0x0000FF00) >>> 8;
    }

    /**
     * Gets the new state with a new value for the next free access
     *
     * @param state The initial state of an access
     * @param next  The index of the next free access
     * @return The new state
     */
    private static int stateNewFreeNext(int state, int next) {
        return (state & 0x00FF00FF) | (next << 8);
    }

    /**
     * Searches the left and right node in the list for a place to insert the specified access
     *
     * @param toInsert The access to be inserted
     * @return The indices of the left and right nodes
     */
    private long listSearchInsert(int toInsert) {
        Access accessToInsert = accesses[toInsert];

        while (true) {
            int leftNode = -1;
            int leftNodeState = 0;
            int rightNode = -1;
            int leftIndex = -1;
            int rightIndex = -1;
            // start by the head
            int currentIndex = 0;
            int currentNode = ACTIVE_HEAD_ID;
            int currentNodeState = accessesState.get(currentNode);

            // 1: Find leftNode and rightNode
            do {
                if (!stateIsMarked(currentNodeState)) {
                    leftNode = currentNode;
                    leftNodeState = currentNodeState;
                    leftIndex = currentIndex;
                }
                currentNode = stateActiveNext(currentNodeState);
                currentIndex++;
                if (currentNode == ACTIVE_TAIL_ID) {
                    rightNode = currentNode;
                    rightIndex = currentIndex;
                    break;
                }
                currentNodeState = accessesState.get(currentNode);
                if (!stateIsActive(currentNodeState))
                    // concurrent removal
                    return 0xFFFFFFFFFFFFFFFFL;
                if (currentNode == toInsert)
                    throw new Error("Already in list!");
                if (!stateIsMarked(currentNodeState) && accesses[leftNode].location > accesses[currentNode].location)
                    throw new Error("List is not ordered");
                Access accessCurrentNode = accesses[currentNode];
                if (!stateIsMarked(currentNodeState) && (accessToInsert.writable || accessCurrentNode.writable) && !accessToInsert.disjoints(accessCurrentNode))
                    // there is a write overlap
                    return 0xFFFFFFFFFFFFFFFFL;
                if (!stateIsMarked(currentNodeState)) {
                    if (accessToInsert.location < accessCurrentNode.location) {
                        rightNode = currentNode;
                        rightIndex = currentIndex;
                    }
                    if (accessCurrentNode.location >= accessToInsert.location + accessToInsert.length)
                        break;
                }
            } while (true);
            if (rightIndex <= leftIndex)
                throw new Error("WTF!");

            // 2: Check nodes are adjacent
            if (stateActiveNext(leftNodeState) == rightNode) {
                if (rightNode != ACTIVE_TAIL_ID && stateIsMarked(accessesState.get(rightNode)))
                    continue;
                return (((long) leftNode) << 32)
                        | ((long) rightNode);
            }

            // 3: Remove one or more marked nodes
            if (leftNode == rightNode)
                throw new Error("oops");
            if (accessesState.compareAndSet(leftNode, leftNodeState, stateNewActiveNext(leftNodeState, rightNode))) {
                if (rightNode != ACTIVE_TAIL_ID && stateIsMarked(accessesState.get(rightNode)))
                    continue;
                poolReturnSequence(stateActiveNext(leftNodeState), rightNode);
                return (((long) leftNode) << 32)
                        | ((long) rightNode);
            }
        }
    }

    /**
     * Tries to insert an access into the list of active accesses
     *
     * @param toInsert The access to be inserted
     * @return Whether the attempt is successful
     */
    private boolean listTryInsert(int toInsert) {
        long data = listSearchInsert(toInsert);
        if (data == 0xFFFFFFFFFFFFFFFFL)
            return false;
        int left = ((int) (data >>> 32));
        int right = ((int) (data & 0xFFFFFFFFL));
        int leftState = accessesState.get(left);
        int rightState = accessesState.get(right);
        Access leftAccess = accesses[left];
        Access rightAccess = accesses[right];
        Access insertAccess = accesses[toInsert];
        if (!stateIsActive(leftState) || !stateIsActive(rightState)
                || stateIsMarked(leftState) || stateIsMarked(rightState)
                || stateActiveNext(leftState) != right
                || leftAccess.location > insertAccess.location || insertAccess.location > rightAccess.location)
            // the list has changed
            return false;
        // check for overlaps on followers
        if (right == toInsert || left == toInsert)
            throw new Error("oops");
        accessesState.set(toInsert, stateNewActiveNext(accessesState.get(toInsert), right));
        return (accessesState.compareAndSet(left, leftState, stateNewActiveNext(leftState, toInsert)));
    }

    /**
     * Inserts an access into the list of live accesses
     * The method returns only when the access is safely inserted, i.e. there is no blocking access
     *
     * @param toInsert The access to be inserted
     * @return The number of tries
     */
    private int listInsert(int toInsert) {
        int count = 1;
        while (!listTryInsert(toInsert))
            count++;
        return count;
    }

    /**
     * Searches the node on the left of the specified access to be removed
     *
     * @param toRemove The access to be removed
     * @return The indices of the left and right nodes
     */
    private long listSearchRemove(int toRemove) {
        while (true) {
            int leftNode = -1;
            int leftNodeState = 0;
            int rightNode;
            // start by the head
            int currentNode = ACTIVE_HEAD_ID;
            int currentNodeState = accessesState.get(currentNode);

            // 1: Find leftNode and rightNode
            do {
                if (!stateIsMarked(currentNodeState)) {
                    leftNode = currentNode;
                    leftNodeState = currentNodeState;
                }
                currentNode = stateActiveNext(currentNodeState);
                if (currentNode == ACTIVE_TAIL_ID)
                    throw new Error("WTF!");
                currentNodeState = accessesState.get(currentNode);
                if (!stateIsActive(currentNodeState))
                    // concurrent removal
                    return 0xFFFFFFFFFFFFFFFFL;
                if (!stateIsMarked(currentNodeState) && accesses[leftNode].location > accesses[currentNode].location)
                    throw new Error("List is not ordered");
            } while (stateIsMarked(currentNodeState) || currentNode != toRemove);
            rightNode = currentNode;

            // 2: Check nodes are adjacent
            if (stateActiveNext(leftNodeState) == rightNode) {
                return (((long) leftNode) << 32)
                        | ((long) rightNode);
            }

            // 3: Remove one or more marked nodes
            if (leftNode == rightNode)
                throw new Error("oops");
            if (accessesState.compareAndSet(leftNode, leftNodeState, stateNewActiveNext(leftNodeState, rightNode))) {
                poolReturnSequence(stateActiveNext(leftNodeState), rightNode);
                return (((long) leftNode) << 32)
                        | ((long) rightNode);
            }
        }
    }

    /**
     * Tries to remove an access from the list of live accesses
     *
     * @param toRemove The access to be removed
     * @return Whether the attempt is successful
     */
    private boolean listTryRemove(int toRemove) {
        long data = listSearchRemove(toRemove);
        if (data == 0xFFFFFFFFFFFFFFFFL)
            return false;
        int left = (int) (data >>> 32);
        int right = (int) (data & 0xFFFFFFFFL);
        if (right != toRemove)
            throw new Error("Concurrent removal of the access");
        int toRemoveState = accessesState.get(toRemove);
        if (stateIsMarked(toRemoveState))
            throw new Error("Concurrent removal of the access");
        // mark the node for a delete
        if (!accessesState.compareAndSet(toRemove, toRemoveState, stateSetMark(toRemoveState)))
            return false;
        // try a simple delete
        int next = stateActiveNext(toRemoveState);
        if (next == left)
            throw new Error("oops");
        //int leftState = accessesState.get(left);
        //int leftExpectedState = (leftState & 0xFF00FF00) | toRemove;
        //int leftNewState = (leftState & 0xFF00FF00) | next;
        //boolean result = (accessesState.compareAndSet(left, leftExpectedState, leftNewState));
        //if (result)
        //    poolReturnAccess(toRemove);
        return true;
    }

    /**
     * Removes an access from the list of live accesses
     *
     * @param toRemove The access to be removed
     * @return The number of tries
     */
    private int listRemove(int toRemove) {
        int count = 1;
        while (!listTryRemove(toRemove))
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
        onAccess(listInsert(access.identifier));
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
        onAccess(listInsert(access.identifier));
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
                int freeStateOld = accessesState.get(nextFree);
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
            accessesState.set(count, 0x00000000);
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
            int currentState = accessesState.get(current);
            if (!stateIsMarked(currentState))
                throw new Error("Trying to return a non-marked access");
            if (!stateIsActive(currentState))
                throw new Error("The access is not active!");
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
            int oldState = accessesState.get(identifier);
            int newState = stateNewFreeNext(oldState, previousFree);
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
