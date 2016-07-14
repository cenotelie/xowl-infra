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
import java.util.concurrent.atomic.AtomicReference;

/**
 * Manages the concurrent accesses onto a single IO backend
 * This structure is thread safe and lock-free.
 *
 * @author Laurent Wouters
 */
class IOAccessManager {
    /**
     * A pool-able access
     */
    private static class Access extends IOAccessOrdered {
        /**
         * The manager for this access object
         */
        private final IOAccessManager manager;

        /**
         * Initializes this access
         *
         * @param manager The manager for this access object
         * @param index   The index of this access in its pool
         */
        public Access(IOAccessManager manager, int index) {
            super(index);
            this.manager = manager;
        }

        @Override
        public void close() {
            manager.onAccessEnd(this);
        }
    }

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
    private final AtomicReference<IOAccessOrdered> poolFirst;
    /**
     * The root of the interval tree for the current accesses
     */
    private final AtomicReference<IOAccessOrdered> root;
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
        this.poolSize = new AtomicInteger(0);
        this.poolFirst = new AtomicReference<>(null);
        this.root = new AtomicReference<>(null);
        this.totalAccesses = 0;
        this.totalTries = 0;
        this.statisticsTimestamp = System.nanoTime();
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
        onAccess(IOAccessOrdered.insert(pool, root, access));
        try {
            access.setupIOData(backend.onAccessRequested(access));
        } catch (StorageException exception) {
            onAccess(IOAccessOrdered.remove(pool, root, access));
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
        onAccess(IOAccessOrdered.insert(pool, root, access));
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
        onAccess(IOAccessOrdered.remove(pool, root, access));
        IOAccessOrdered.poolInsert(poolFirst, access);
    }

    /**
     * Resolves a free access object
     *
     * @return A free access object
     */
    private Access newAccess() {
        while (true) {
            int size = poolSize.get();
            IOAccessOrdered result = IOAccessOrdered.poolGet(poolFirst);
            if (result != null)
                return (Access) result;
            if (size == pool.length)
                // wait for a free element
                continue;
            if (poolSize.compareAndSet(size, size + 1)) {
                Access newAccess = new Access(this, size);
                pool[size] = newAccess;
                return newAccess;
            }
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
