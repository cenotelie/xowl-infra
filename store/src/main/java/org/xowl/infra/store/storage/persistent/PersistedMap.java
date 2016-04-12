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
 * Implements a (long -> long) map that is persisted in files
 *
 * @author Laurent Wouters
 */
class PersistedMap {
    /**
     * The backing store
     */
    private final FileStore store;
    /**
     * The head entry for the map
     */
    private final long mapHead;

    /**
     * Initializes this map
     *
     * @param store   The backing store
     * @param mapHead The head entry for the map
     */
    public PersistedMap(FileStore store, long mapHead) {
        this.store = store;
        this.mapHead = mapHead;
    }

    public static PersistedMap create(FileStore store) {
        return null;
    }

    /**
     * Gets the value associated to key
     *
     * @param key The requested key
     * @return The associated value, or -1 when the key is not present
     */
    public long get(long key) {
        return -1;
    }

    /**
     * Puts a new key, value couple into the map
     * If the key is already present, the specified value replaces the existing one.
     *
     * @param key   The key
     * @param value The associated value
     */
    public void put(long key, long value) {

    }

    /**
     * Removes the entry for the specified key
     *
     * @param key The key
     * @return The value that was associated to the key, or -1 if there was none
     */
    public long remove(long key) {
        return -1;
    }
}
