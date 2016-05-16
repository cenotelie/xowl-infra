/*******************************************************************************
 * Copyright (c) 2016 Association Cénotélie (cenotelie.fr)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Lesser General
 * Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package org.xowl.infra.store.storage.persistent;

import org.xowl.infra.utils.collections.Adapter;
import org.xowl.infra.utils.collections.AdaptingIterator;
import org.xowl.infra.utils.collections.CombiningIterator;
import org.xowl.infra.utils.collections.Couple;
import org.xowl.infra.utils.logging.Logger;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Implements a (long -> long) map that is persisted in files
 * A map is in two stages: 1 and 2, see the respective helpers.
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

    /**
     * Creates a new persisted map
     *
     * @param store The backing store
     * @return The persisted map
     * @throws StorageException When an IO operation fails
     */
    public static PersistedMap create(FileStore store) throws StorageException {
        long mapHead = PersistedMapStage1.newMap(store);
        return new PersistedMap(store, mapHead);
    }

    /**
     * Gets the value associated to key
     *
     * @param key The requested key
     * @return The associated value, or KEY_NOT_FOUND when the key is not present
     * @throws StorageException When an IO operation fails
     */
    public long get(long key) throws StorageException {
        long head2 = PersistedMapStage1.getHeadFor(store, mapHead, key1(key));
        if (head2 == FileStore.KEY_NULL)
            return FileStore.KEY_NULL;
        return PersistedMapStage2.get(store, head2, key2(key));
    }

    /**
     * Puts a new key, value couple into the map
     * If the key is already present, the specified value replaces the existing one.
     *
     * @param key   The key
     * @param value The associated value
     * @throws StorageException When an IO operation fails
     */
    public void put(long key, long value) throws StorageException {
        long head2 = PersistedMapStage1.resolveHeadFor(store, mapHead, key1(key));
        PersistedMapStage2.put(store, head2, key2(key), value);
    }

    /**
     * Removes the entry for the specified key
     *
     * @param key The key
     * @return The value that was associated to the key, or KEY_NOT_FOUND if there was none
     * @throws StorageException When an IO operation fails
     */
    public long remove(long key) throws StorageException {
        long head2 = PersistedMapStage1.getHeadFor(store, mapHead, key1(key));
        if (head2 == FileStore.KEY_NULL)
            return FileStore.KEY_NULL;
        return PersistedMapStage2.remove(store, head2, key2(key));
    }

    /**
     * Removes all entries from this map
     *
     * @throws StorageException When an IO operation fails
     */
    public void clear() throws StorageException {
        PersistedMapStage1.clear(store, mapHead);
    }

    /**
     * Gets an iterator over the entries in this map
     *
     * @return An iterator over the entries
     * @throws StorageException When an IO operation fails
     */
    public Iterator<Map.Entry<Long, Long>> entries() throws StorageException {
        Iterator<Couple<Integer, Long>> iteratorStage1 = PersistedMapStage1.iterator(store, mapHead);
        return new AdaptingIterator<>(new CombiningIterator<>(iteratorStage1, new Adapter<Iterator<Couple<Integer, Long>>>() {
            @Override
            public <X> Iterator<Couple<Integer, Long>> adapt(X element) {
                // gets the stage 2 iterator
                Couple<Integer, Long> stage1Couple = (Couple<Integer, Long>) element;
                try {
                    return PersistedMapStage2.iterator(store, stage1Couple.y);
                } catch (StorageException exception) {
                    Logger.DEFAULT.error(exception);
                    return null;
                }
            }
        }), new Adapter<Map.Entry<Long, Long>>() {
            @Override
            public <X> Map.Entry<Long, Long> adapt(X element) {
                Couple<Couple<Integer, Long>, Couple<Integer, Long>> couple = (Couple<Couple<Integer, Long>, Couple<Integer, Long>>) element;
                // reconstruct the map entry key
                return new HashMap.SimpleEntry<>(key(couple.x.x, couple.y.x), couple.y.y);
            }
        });
    }

    /**
     * Gets the stage 1 key for the specified map key
     *
     * @param key A map key
     * @return The stage 1 key
     */
    private static int key1(long key) {
        return (int) (key >>> 32);
    }

    /**
     * Gets the stage 2 key for the specified map key
     *
     * @param key A map key
     * @return The stage 2 key
     */
    private static int key2(long key) {
        return (int) (key & 0xFFFFFFFFL);
    }

    /**
     * Gets the full map key for stage 1 and 2 keys
     *
     * @param key1 The stage 1 key
     * @param key2 The stage 2 key
     * @return The full map key
     */
    private static long key(int key1, int key2) {
        return (((long) key1) << 32) | ((long) key2);
    }
}
