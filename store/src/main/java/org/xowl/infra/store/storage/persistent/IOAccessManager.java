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
import java.util.concurrent.atomic.AtomicLong;

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
     * The backend element that is protected by this manager
     */
    private final IOBackend backend;
    /**
     * The pool of existing accesses in the manager
     */
    private final IOAccessOrdered[] pool;
    /**
     * The current number of accesses in the pool
     */
    private final AtomicInteger poolSize;
    /**
     * The first free access object in the pool
     */
    private final AtomicLong poolFirst;
    /**
     * The head of the list of live accesses
     */
    private final IOAccessOrdered listHead;
    /**
     * The tail of the list of live accesses
     */
    private final IOAccessOrdered listTail;
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
        this.pool = new IOAccessOrdered[ACCESSES_POOL_SIZE];
        this.poolSize = new AtomicInteger(2);
        this.poolFirst = new AtomicLong(0x00000000FFFFFFFFL);
        this.listHead = new IOAccessOrdered(this, 0);
        this.listTail = new IOAccessOrdered(this, 1);
        this.listHead.next.set(listTail.identifier);
        this.pool[0] = listHead;
        this.pool[1] = listTail;
        this.totalAccesses = 0;
        this.totalTries = 0;
        this.statisticsTimestamp = System.nanoTime();
    }

    /**
     * Gets whether the specified reference is marked
     *
     * @param reference A stamped reference
     * @return Whether the specified reference is marked
     */
    private static boolean isMarked(long reference) {
        return ((reference & 0x0000000100000000L) == 0x0000000100000000L);
    }

    /**
     * Gets the identifier of the access in the specified stamped reference
     *
     * @param reference A stamped reference
     * @return The encapsulated identifier
     */
    private static int id(long reference) {
        return (int) (reference & 0xFFFFFFFFL);
    }

    /**
     * Gets the marked reference
     *
     * @param reference The original reference
     * @return The marked reference
     */
    private static long marked(long reference) {
        return (0x0000000100000000L | reference);
    }

    /**
     * Searches the left and right node in the list for a place to insert the specified access
     *
     * @param access The access to be inserted
     * @return The indices of the left and right nodes
     */
    private long listSearchInsert(IOAccessOrdered access) {
        IOAccessOrdered leftNode = listHead;
        long leftNodeNext = 0x00000000FFFFFFFFL;
        IOAccessOrdered rightNode;

        while (true) {
            // start by the head
            IOAccessOrdered currentNode = listHead;
            long currentNodeNext = listHead.next.get();

            // 1: Find leftNode and rightNode so that either
            do {
                if (!isMarked(currentNodeNext)) {
                    leftNode = currentNode;
                    leftNodeNext = currentNodeNext;
                }
                currentNode = pool[id(currentNodeNext)];
                if (currentNode == listTail)
                    break;
                currentNodeNext = currentNode.next.get();
                if ((access.writable || currentNode.writable) && !isMarked(currentNodeNext) && !access.disjoints(currentNode)) {
                    // there is a write overlap
                    return 0xFFFFFFFFFFFFFFFFL;
                }
            } while (isMarked(currentNodeNext) || currentNode.location < access.location);
            rightNode = currentNode;

            // 2: Check nodes are adjacent
            if (id(leftNodeNext) == rightNode.identifier) {
                if (rightNode != listTail && isMarked(rightNode.next.get()))
                    continue;
                return ((long) (leftNode.identifier) << 32)
                        | ((long) (rightNode.identifier));
            }

            // 3: Remove one or more marked nodes
            IOAccessOrdered toRelease = pool[id(leftNodeNext)];
            if (leftNode.next.compareAndSet(leftNodeNext, rightNode.identifier)) {
                // release the removed nodes
                while (toRelease != rightNode) {
                    long toReleaseNext = toRelease.next.get();
                    if (!isMarked(toReleaseNext))
                        throw new Error("WTF!");
                    poolReturnAccess(toRelease);
                    toRelease = pool[id(toReleaseNext)];
                }
                if (rightNode != listTail && isMarked(rightNode.next.get()))
                    continue;
                return ((long) (leftNode.identifier) << 32)
                        | ((long) (rightNode.identifier));
            }
        }
    }

    /**
     * Tries to insert an access into the list of live accesses
     *
     * @param access The access to insert
     * @return Whether the attempt is successful
     */
    private boolean listTryInsert(IOAccessOrdered access) {
        long data = listSearchInsert(access);
        if (data == 0xFFFFFFFFFFFFFFFFL)
            // there is an overlap
            return false;
        IOAccessOrdered left = pool[((int) (data >> 32))];
        IOAccessOrdered right = pool[((int) (data & 0xFFFFFFFFL))];
        // check for overlaps
        IOAccessOrdered follower = right;
        while (follower != listTail && follower.location < access.location + access.length) {
            long followerNext = follower.next.get();
            if (isMarked(followerNext))
                return false;
            if ((access.writable || follower.writable) && !access.disjoints(follower))
                return false;
            follower = pool[id(followerNext)];
        }
        access.next.set(right.identifier);
        return left.next.compareAndSet(right.identifier, access.identifier);
    }

    /**
     * Inserts an access into the list of live accesses
     * The method returns only when the access is safely inserted, i.e. there is no blocking access
     *
     * @param access The access to insert
     * @return The number of tries
     */
    private int listInsert(IOAccessOrdered access) {
        int count = 1;
        while (!listTryInsert(access))
            count++;
        return count;
    }

    /**
     * Searches the node on the left of the specified access to be removed
     *
     * @param access The access to look for (to be removed)
     * @return The indices of the left and right nodes
     */
    private long listSearchRemove(IOAccessOrdered access) {
        IOAccessOrdered leftNode = listHead;
        long leftNodeNext = 0x00000000FFFFFFFFL;
        IOAccessOrdered rightNode;

        while (true) {
            // start by the head
            IOAccessOrdered currentNode = listHead;
            long currentNodeNext = listHead.next.get();

            // 1: Find leftNode and rightNode so that either
            do {
                if (!isMarked(currentNodeNext)) {
                    leftNode = currentNode;
                    leftNodeNext = currentNodeNext;
                }
                currentNode = pool[id(currentNodeNext)];
                if (currentNode == listTail)
                    break;
                currentNodeNext = currentNode.next.get();
            } while (isMarked(currentNodeNext) || currentNode != access);
            rightNode = currentNode;
            if (rightNode != access) {
                throw new Error("WTF!");
            }

            // 2: Check nodes are adjacent
            if (id(leftNodeNext) == rightNode.identifier) {
                return ((long) (leftNode.identifier) << 32)
                        | ((long) (rightNode.identifier));
            }

            // 3: Remove one or more marked nodes
            IOAccessOrdered toRelease = pool[id(leftNodeNext)];
            if (leftNode.next.compareAndSet(leftNodeNext, rightNode.identifier)) {
                // release the removed nodes
                while (toRelease != rightNode) {
                    long toReleaseNext = toRelease.next.get();
                    if (!isMarked(toReleaseNext))
                        throw new Error("WTF!");
                    poolReturnAccess(toRelease);
                    toRelease = pool[id(toReleaseNext)];
                }
                return ((long) (leftNode.identifier) << 32)
                        | ((long) (rightNode.identifier));
            }
        }
    }

    /**
     * Tries to remove an access from the list of live accesses
     *
     * @param access The access to remove
     * @return Whether the attempt is successful
     */
    private boolean listTryRemove(IOAccessOrdered access) {
        long data = listSearchRemove(access);
        IOAccessOrdered left = pool[((int) (data >> 32))];
        IOAccessOrdered right = pool[((int) (data & 0xFFFFFFFFL))];
        if (right != access)
            throw new Error("Concurrent removal of the access");
        long accessNext = access.next.get();
        if (isMarked(accessNext))
            throw new Error("Concurrent removal of the access");
        // mark the node for a delete
        if (!access.next.compareAndSet(accessNext, marked(accessNext)))
            return false;
        // try a simple delete
        if (left.next.compareAndSet(access.identifier, accessNext))
            poolReturnAccess(access);
        else
            listSearchRemove(access);
        return true;
    }

    /**
     * Removes an access from the list of live accesses
     *
     * @param access The access to remove
     * @return The number of tries
     */
    private int listRemove(IOAccessOrdered access) {
        int count = 1;
        while (!listTryRemove(access))
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
        IOAccessOrdered access = newAccess();
        access.setupIOData(location, length, writable);
        onAccess(listInsert(access));
        try {
            access.setupIOData(backend.onAccessRequested(access));
        } catch (StorageException exception) {
            onAccess(listRemove(access));
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
        IOAccessOrdered access = newAccess();
        access.setupIOData(location, length, writable);
        access.setupIOData(element);
        onAccess(listInsert(access));
        return access;
    }

    /**
     * Ends an access to the backend
     *
     * @param access The access
     */
    public void onAccessEnd(IOAccessOrdered access) {
        try {
            backend.onAccessTerminated(access, access.element);
        } catch (StorageException exception) {
            Logging.getDefault().error(exception);
        }
        onAccess(listRemove(access));
    }

    /**
     * Resolves a free access object
     *
     * @return A free access object
     */
    private IOAccessOrdered newAccess() {
        while (true) {
            int size = poolSize.get();
            long firstData = poolFirst.get();
            int firstId = (int) (firstData & 0xFFFFFFFFL);
            int firstStamp = (int) (firstData >> 32);

            if (firstId != -1) {
                IOAccessOrdered result = pool[firstId];
                int nextId = result.poolNext;
                long nextData = (((long) (firstStamp + 1)) << 32) | nextId;
                if (poolFirst.compareAndSet(firstData, nextData))
                    return result;
                continue;
            }

            if (size == pool.length)
                // wait for a free element
                continue;

            if (poolSize.compareAndSet(size, size + 1)) {
                IOAccessOrdered newAccess = new IOAccessOrdered(this, size);
                pool[size] = newAccess;
                return newAccess;
            }
        }
    }

    /**
     * Returns an access to the pool
     *
     * @param access The access to return
     */
    private void poolReturnAccess(IOAccessOrdered access) {
        while (true) {
            long firstData = poolFirst.get();
            int firstId = (int) (firstData & 0xFFFFFFFFL);
            int firstStamp = (int) (firstData >> 32);
            access.poolNext = firstId;
            long nextData = (((long) (firstStamp + 1)) << 32) | access.identifier;
            if (poolFirst.compareAndSet(firstData, nextData))
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
