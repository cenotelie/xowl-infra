/*******************************************************************************
 * Copyright (c) 2016 Laurent Wouters
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
 *
 * Contributors:
 *     Laurent Wouters - lwouters@xowl.org
 ******************************************************************************/

package org.xowl.infra.store.storage.persistent;

/**
 * Implements a persistent value stored in a file
 * This structure is thread-safe and lock-free.
 *
 * @author Laurent Wouters
 */
class PersistedLong {
    /**
     * The backing store
     */
    private final FileStore store;
    /**
     * The entry in the store
     */
    private final long entry;
    /**
     * The cached value
     */
    private volatile long cache;

    /**
     * Initializes this persisted value from a stored one
     *
     * @param store The backing store
     * @param entry The entry in the store
     * @throws StorageException When the backend cannot be read
     */
    public PersistedLong(FileStore store, long entry) throws StorageException {
        this.store = store;
        this.entry = store.isEmpty() ? store.add(8) : entry;
        if (this.entry != entry)
            throw new StorageException("Failed to initialize the empty store for this data");
        try (IOTransaction transaction = store.read(entry)) {
            this.cache = transaction.readLong();
        }
    }

    /**
     * Gets the value
     *
     * @return The value
     */
    public long get() {
        return cache;
    }

    /**
     * Gets the value and increment it
     *
     * @return The value before the increment
     * @throws StorageException When the backend cannot be written
     */
    public long getAndIncrement() throws StorageException {
        try (IOTransaction transaction = store.access(entry)) {
            // the fact that this transaction is obtained, we have exclusive write on the containing block
            // in practice, this is a synchronized block because only one concurrent thread can be here
            long result = cache;
            cache++;
            transaction.writeLong(cache);
            return result;
        }
    }
}
