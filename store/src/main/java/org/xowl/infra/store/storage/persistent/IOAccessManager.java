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
     * The maximum number of concurrent threads
     */
    private static final int THREAD_POOL_SIZE = 16;

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
     * - byte: access state (0=free, 1=active, 2=logically removed, 3=removed, 4=returning)
     * - byte: when returning: bit-field for remaining touching threads
     * - byte: when returning: bit-field for remaining touching threads, when free: the index of the next free access, or 00
     * - byte: the index of the next active access
     */
    private final AtomicLongArray accessesState;
    /**
     * The bit-field of current threads touching the list
     */
    private final AtomicInteger accessesThreads;
    /**
     * The index of the next free access
     */
    private final AtomicInteger accessesFree;
    /**
     * The pool of free threads identifiers
     */
    private final AtomicInteger threads;
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
        this.accessesState.set(ACTIVE_HEAD_ID, 0x0000000001000001L);
        this.accessesState.set(ACTIVE_TAIL_ID, 0x7FFFFFFF01000001L);
        this.accessesThreads = new AtomicInteger(0);
        this.accessesFree = new AtomicInteger(ACTIVE_HEAD_ID);
        this.threads = new AtomicInteger(0x00FFFF);
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
     * Gets whether the state of an access indicates that the access is active
     *
     * @param state The state of an access
     * @return Whether the access is active
     */
    private static boolean stateIsActive(long state) {
        return (state & 0x00000000FF000000L) == 0x0000000001000000;
    }

    /**
     * Gets whether the state of an access indicates that the access is no longer accessible from the active list, but not being returned yet
     *
     * @param state The state of an access
     * @return Whether the access is being returned
     */
    private static boolean stateIsRemoved(long state) {
        return (state & 0x00000000FF000000L) == 0x0000000003000000;
    }

    /**
     * Gets whether the state of an access indicates that the access is being returned to the free access list
     *
     * @param state The state of an access
     * @return Whether the access is being returned
     */
    private static boolean stateIsReturning(long state) {
        return (state & 0x00000000FF000000L) == 0x0000000004000000;
    }

    /**
     * When returning an access, gets the threads that can touch this access
     *
     * @param state The state of an access
     * @return The threads that can touch this access
     */
    private static int stateThreads(long state) {
        return (int) ((state & 0x0000000000FFFF00L) >>> 8);
    }

    /**
     * Gets the index of the next free access
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
     * @param key        The new key for the access
     * @param nextActive The index of the next active access
     * @return The new state
     */
    private static long stateSetupActive(int key, int nextActive) {
        return ul(key) << 32 | 0x0000000001000000L | nextActive;
    }

    /**
     * Gets the marked version of the state for the specified one
     *
     * @param state The initial state of an access
     * @return The equivalent marked state
     */
    private static long stateSetLogicallyRemoved(long state) {
        return (state & 0xFFFFFFFF00FFFFFFL) | 0x0000000002000000L;
    }

    /**
     * Gets the version of the state representing a removed access (not yet returning)
     *
     * @param state          The initial state of an access
     * @param currentThreads The current threads accessing the active list
     * @return The equivalent marked state
     */
    private static long stateSetRemoved(long state, int currentThreads) {
        return (state & 0xFFFFFFFF000000FFL) | 0x0000000003000000L | ul(currentThreads) << 8;
    }

    /**
     * Gets the version of the state representing a returning access
     *
     * @param state The initial state of an access
     * @return The equivalent marked state
     */
    private static long stateSetReturning(long state) {
        return (state & 0xFFFFFFFF00FFFFFFL) | 0x0000000004000000L;
    }

    /**
     * Gets the new state when removing a thread as potentially touching the access
     *
     * @param state            The initial state of an access
     * @param threadIdentifier The identifier of the thread to remove
     * @return The new state
     */
    private static long stateRemoveThread(long state, int threadIdentifier) {
        return state & ~(ul(threadIdentifier) << 8);
    }

    /**
     * Gets the new state with a new value for the next free access
     *
     * @param state The initial state of an access
     * @param next  The index of the next free access
     * @return The new state
     */
    private static long stateSetNextFree(long state, int next) {
        return (state & 0xFFFFFFFF000000FFL) | ul(next << 8);
    }

    /**
     * Gets the new state with a new value for the next active access
     *
     * @param state The initial state of an access
     * @param next  The index of the next active access
     * @return The new state
     */
    private static long stateSetNextActive(long state, int next) {
        return (state & 0xFFFFFFFFFFFFFF00L) | ul(next);
    }

    /**
     * Gets a identifier for this thread
     *
     * @return The identifier for this thread
     */
    private int getThreadId() {
        while (true) {
            int mask = threads.get();
            for (int i = 0; i != THREAD_POOL_SIZE; i++) {
                int id = (1 << i);
                if ((mask & id) == id) {
                    // the identifier is available
                    int newMask = mask & ~id;
                    if (threads.compareAndSet(mask, newMask))
                        // reserved the identifier
                        return id;
                    // the mask has changed
                    break;
                }
            }
        }
    }

    /**
     * Returns a thread identifier when no longer required
     *
     * @param threadIdentifier The identifier to return
     */
    private void returnThreadId(int threadIdentifier) {
        while (true) {
            int mask = threads.get();
            if (threads.compareAndSet(mask, mask | threadIdentifier))
                return;
        }
    }

    /**
     * Registers the specified thread as accessing the list of active accesses
     *
     * @param threadIdentifier The identifier of this thread
     */
    private void beginActiveAccess(int threadIdentifier) {
        while (true) {
            int mask = accessesThreads.get();
            if (accessesThreads.compareAndSet(mask, mask | threadIdentifier))
                return;
        }
    }

    /**
     * Unregisters the specified thread as accessing the list of active accesses
     *
     * @param threadIdentifier The identifier of this thread
     */
    private void endActiveAccess(int threadIdentifier) {
        while (true) {
            int mask = accessesThreads.get();
            if (accessesThreads.compareAndSet(mask, mask & ~threadIdentifier))
                return;
        }
    }

    /**
     * Inspect an access being removed
     *
     * @param identifier The identifier of the access
     */
    private void inspectRemoved(int identifier) {
        while (true) {
            long oldState = accessesState.get(identifier);
            if (stateIsRemoved(oldState) || stateIsReturning(oldState))
                // someone-else is returning this access, abandon here
                return;
            int currentThreads = accessesThreads.get();
            long newState = stateSetRemoved(oldState, currentThreads);
            if (accessesState.compareAndSet(identifier, oldState, newState)) {
                // we marked the access as returning
                // are the threads information correct?
                if (accessesThreads.get() == currentThreads) {
                    // yes, mark as returning
                    oldState = newState;
                    newState = stateSetReturning(oldState);
                    accessesState.compareAndSet(identifier, oldState, newState);
                    return;
                }
                break;
            }
        }
        // here, we marked the access as removed
        // the threads information are not up to date, update them
        while (true) {
            long oldState = accessesState.get(identifier);
            int currentThreads = accessesThreads.get();
            long newState = stateSetRemoved(oldState, currentThreads);
            if (accessesState.compareAndSet(identifier, oldState, newState)
                    && accessesThreads.get() == currentThreads) {
                oldState = newState;
                newState = stateSetReturning(oldState);
                accessesState.compareAndSet(identifier, oldState, newState);
                return;
            }
        }
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
        // the buffer of logically removed access that could be freed
        // we hope that with escape analysis the array is on the stack
        int[] removed = new int[ACCESSES_POOL_SIZE];
        int removedCount = 0;

        int leftNode = -1;
        long leftNodeState = 0;
        int rightNode;
        // start by the head
        int currentNode = ACTIVE_HEAD_ID;
        long currentNodeState = accessesState.get(currentNode);

        // 1: Find leftNode and rightNode
        while (true) {
            if (stateIsActive(currentNodeState)) {
                // cleanup nodes touched for removal, if any, because we are going to find new one
                removedCount = 0;
                leftNode = currentNode;
                leftNodeState = currentNodeState;
            }
            currentNode = stateActiveNext(currentNodeState);
            if (currentNode == ACTIVE_TAIL_ID)
                break;
            currentNodeState = accessesState.get(currentNode);
            if (stateIsRemoved(currentNodeState) || stateIsReturning(currentNodeState))
                return false;
            if (stateIsActive(currentNodeState)) {
                Access accessCurrentNode = accesses[currentNode];
                if ((accessToInsert.writable || accessCurrentNode.writable) && !accessToInsert.disjoints(accessCurrentNode))
                    // there is a write overlap
                    return false;
                if (key < stateKey(currentNodeState))
                    break;
            } else {
                removed[removedCount++] = currentNode;
            }
        }
        rightNode = currentNode;

        // 1 bis: look for overlapping accesses after that
        while (true) {
            currentNode = stateActiveNext(currentNodeState);
            if (currentNode == ACTIVE_TAIL_ID)
                break;
            currentNodeState = accessesState.get(currentNode);
            if (stateIsRemoved(currentNodeState) || stateIsReturning(currentNodeState))
                return false;
            if (stateIsActive(currentNodeState)) {
                Access accessCurrentNode = accesses[currentNode];
                if ((accessToInsert.writable || accessCurrentNode.writable) && !accessToInsert.disjoints(accessCurrentNode))
                    // there is a write overlap
                    return false;
                if (stateKey(currentNodeState) >= key + accessToInsert.length)
                    break;
            }
        }

        // 2: Check nodes are adjacent
        if (stateActiveNext(leftNodeState) == rightNode) {
            if (rightNode != ACTIVE_TAIL_ID && !stateIsActive(accessesState.get(rightNode)))
                return false;
            accessesState.set(toInsert, stateSetupActive(key, rightNode));
            return (accessesState.compareAndSet(leftNode, leftNodeState, stateSetNextActive(leftNodeState, toInsert)));
        }

        // 3: Remove one or more marked nodes
        long leftNodeStateNew = stateSetNextActive(leftNodeState, rightNode);
        if (accessesState.compareAndSet(leftNode, leftNodeState, leftNodeStateNew)) {
            // mark the returning nodes
            for (int i = 0; i != removedCount; i++)
                inspectRemoved(removed[i]);
            leftNodeState = leftNodeStateNew;
            if (rightNode != ACTIVE_TAIL_ID && !stateIsActive(accessesState.get(rightNode)))
                return false;
            accessesState.set(toInsert, stateSetupActive(key, rightNode));
            return (accessesState.compareAndSet(leftNode, leftNodeState, stateSetNextActive(leftNodeState, toInsert)));
        }
        return false;
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
        int threadIdentifier = getThreadId();
        beginActiveAccess(threadIdentifier);
        int count = 1;
        while (true) {
            if (listSearchAndInsert(toInsert, key))
                break;
            count++;
        }
        endActiveAccess(threadIdentifier);
        poolCleanup(threadIdentifier);
        returnThreadId(threadIdentifier);
        return count;
    }

    /**
     * Searches the left and right node in the list for the access to be removed and try to mark it as removed
     *
     * @param toRemove The access to be removed
     * @return Whether the attempt is successful
     */
    private boolean listSearchAndRemove(int toRemove) {
        // the buffer of logically removed access that could be freed
        // we hope that with escape analysis the array is on the stack
        int[] removed = new int[ACCESSES_POOL_SIZE];
        int removedCount = 0;

        int leftNode = -1;
        long leftNodeState = 0;
        long toRemoveState;
        // start by the head
        int currentNode = ACTIVE_HEAD_ID;
        long currentNodeState = accessesState.get(currentNode);

        // 1: Find leftNode and rightNode
        while (true) {
            if (stateIsActive(currentNodeState)) {
                // cleanup nodes touched for removal, if any, because we are going to find new one
                removedCount = 0;
                leftNode = currentNode;
                leftNodeState = currentNodeState;
            }
            currentNode = stateActiveNext(currentNodeState);
            currentNodeState = accessesState.get(currentNode);
            if (stateIsRemoved(currentNodeState) || stateIsReturning(currentNodeState))
                return false;
            if (stateIsActive(currentNodeState)) {
                if (currentNode == toRemove)
                    break;
            } else {
                removed[removedCount++] = currentNode;
            }
        }
        toRemoveState = currentNodeState;

        // 2: Check nodes are adjacent
        if (stateActiveNext(leftNodeState) == toRemove) {
            return accessesState.compareAndSet(toRemove, toRemoveState, stateSetLogicallyRemoved(toRemoveState));
        }

        // 3: Remove one or more marked nodes
        if (accessesState.compareAndSet(leftNode, leftNodeState, stateSetNextActive(leftNodeState, toRemove))) {
            // mark the returning nodes
            for (int i = 0; i != removedCount; i++)
                inspectRemoved(removed[i]);
            return accessesState.compareAndSet(toRemove, toRemoveState, stateSetLogicallyRemoved(toRemoveState));
        }
        return false;
    }

    /**
     * Removes an access from the list of live accesses
     *
     * @param toRemove The access to be removed
     * @return The number of tries
     */
    private int listRemove(int toRemove) {
        int threadIdentifier = getThreadId();
        beginActiveAccess(threadIdentifier);
        int count = 1;
        while (true) {
            if (listSearchAndRemove(toRemove))
                break;
            count++;
        }
        endActiveAccess(threadIdentifier);
        poolCleanup(threadIdentifier);
        returnThreadId(threadIdentifier);
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
     * Collects the returning accesses that are no longer touched by a thread
     *
     * @param threadIdentifier The identifier of this thread
     */
    private void poolCleanup(int threadIdentifier) {
        int count = accessesCount.get();
        for (int i = 2; i < count; i++) {
            long state = accessesState.get(i);
            if (stateIsReturning(state) && (stateThreads(state) & threadIdentifier) == threadIdentifier)
                onAccessReturning(threadIdentifier, i, state);
        }
    }

    /**
     * When a returning access touched by this thread is found while cleaning up the pool
     *
     * @param threadIdentifier The identifier of this thread
     * @param accessIdentifier The identifier of the access
     * @param currentState     The supposed current state of the access
     */
    private void onAccessReturning(int threadIdentifier, int accessIdentifier, long currentState) {
        // remove the mark for the current thread
        while (true) {
            long newState = stateRemoveThread(currentState, threadIdentifier);
            boolean success = accessesState.compareAndSet(accessIdentifier, currentState, newState);
            if (success) {
                currentState = newState;
                break;
            }
            currentState = accessesState.get(accessIdentifier);
        }

        if (stateThreads(currentState) != 0)
            // more threads can still touch this access
            return;

        // no more thread can touch this node, return the access
        while (true) {
            int previousFree = accessesFree.get();
            long newState = stateSetNextFree(currentState, previousFree);
            accessesState.compareAndSet(accessIdentifier, currentState, newState);
            if (accessesFree.compareAndSet(previousFree, accessIdentifier))
                return;
            currentState = newState;
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
