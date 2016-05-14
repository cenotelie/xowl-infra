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

import org.xowl.infra.utils.logging.Logger;

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
         */
        public Access(IOAccessManager manager) {
            this.manager = manager;
        }

        @Override
        public void close() {
            manager.onAccessEnd(this);
        }
    }

    /**
     * The maximum size of the pool for reusable access objects
     */
    private static final int POOL_SIZE = 16;

    /**
     * The backend element that is protected by this manager
     */
    private final IOBackend backend;
    /**
     * The pool of free access objects
     */
    private final Access[] accessPool;
    /**
     * The number of pooled elements
     */
    private final AtomicInteger accessPoolSize;
    /**
     * The root of the interval tree for the current accesses
     */
    private final AtomicReference<IOAccessOrdered> root;

    /**
     * Initializes this pool
     *
     * @param backend The backend element that is protected by this manager
     */
    public IOAccessManager(IOBackend backend) {
        this.backend = backend;
        this.accessPool = new Access[POOL_SIZE];
        this.accessPoolSize = new AtomicInteger(0);
        this.root = new AtomicReference<>(null);
    }

    /**
     * Gets an access to the associated backend for the specified span
     *
     * @param location The location of the span within the backend
     * @param length   The length of the allowed span
     * @param writable Whether the access allows writing
     * @return The new access, or null if it cannot be obtained
     */
    public IOAccess get(long location, long length, boolean writable) {
        Access access = poolResolve();
        access.setupIOData(location, length, writable);
        IOAccessOrdered.insert(root, access);
        try {
            access.setupIOData(backend.onAccessRequested(access));
        } catch (StorageException exception) {
            Logger.DEFAULT.error(exception);
            IOAccessOrdered.remove(root, access);
            poolReturn(access);
            return null;
        }
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
            Logger.DEFAULT.error(exception);
        }
        IOAccessOrdered.remove(root, access);
        poolReturn(access);
    }

    /**
     * Resolves a free access object
     *
     * @return A free access object
     */
    private Access poolResolve() {
        while (true) {
            int size = accessPoolSize.get();
            if (size <= 0)
                return new Access(this);
            if (accessPoolSize.compareAndSet(size, size - 1))
                return accessPool[size - 1];
        }
    }

    /**
     * Returns an access object to the pool
     *
     * @param access The access object
     */
    private void poolReturn(Access access) {
        while (true) {
            int size = accessPoolSize.get();
            if (size >= POOL_SIZE)
                return;
            if (accessPoolSize.compareAndSet(size, size + 1)) {
                accessPool[size] = access;
                break;
            }
        }
    }
}
