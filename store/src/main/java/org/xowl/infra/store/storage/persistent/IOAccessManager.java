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

import java.util.concurrent.atomic.AtomicBoolean;
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
     * The backend element that is protected by this manager
     */
    private final IOBackend backend;
    /**
     * The root of the interval tree for the current accesses
     */
    private final AtomicReference<IOAccessOrdered> root;
    /**
     * The current number of live accesses
     */
    private final AtomicInteger currentAccessCount;
    /**
     * Whether further accesses shall be withheld
     */
    private final AtomicBoolean doWithhold;

    /**
     * Initializes this pool
     *
     * @param backend The backend element that is protected by this manager
     */
    public IOAccessManager(IOBackend backend) {
        this.backend = backend;
        this.root = new AtomicReference<>(null);
        this.currentAccessCount = new AtomicInteger(0);
        this.doWithhold = new AtomicBoolean(false);
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
        IOAccessOrdered.insert(root, access);
        try {
            access.setupIOData(backend.onAccessRequested(access));
        } catch (StorageException exception) {
            IOAccessOrdered.remove(root, access);
            currentAccessCount.decrementAndGet();
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
        IOAccessOrdered.insert(root, access);
        return access;
    }

    /**
     * Withhold new accesses under further notice
     */
    public void withhold() {
        while (true) {
            if (doWithhold.compareAndSet(false, true))
                return;
        }
    }

    /**
     * Resume accesses
     */
    public void resume() {
        while (true) {
            if (doWithhold.compareAndSet(true, false))
                return;
        }
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
        currentAccessCount.decrementAndGet();
    }

    /**
     * Resolves a free access object
     *
     * @return A free access object
     */
    private Access newAccess() {
        while (true) {
            boolean onStandby = doWithhold.get();
            if (!onStandby)
                break;
        }
        currentAccessCount.incrementAndGet();
        return new Access(this);
    }
}