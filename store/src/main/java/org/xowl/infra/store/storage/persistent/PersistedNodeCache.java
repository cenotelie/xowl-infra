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

import java.lang.ref.WeakReference;

/**
 * Implements a cache of instantiated persisted nodes
 *
 * @author Laurent Wouters
 */
class PersistedNodeCache<T extends PersistedNode> {
    /**
     * The size of the cache
     */
    private static final int SIZE = 256;

    /**
     * The keys of cached nodes
     */
    private final long[] keys;
    /**
     * The cached nodes
     */
    private final WeakReference<T>[] nodes;
    /**
     * Index of the cache start
     */
    private int start;
    /**
     * Current cache length
     */
    private int length;

    /**
     * Initializes the cache
     */
    public PersistedNodeCache() {
        this.keys = new long[SIZE];
        this.nodes = new WeakReference[SIZE];
        this.start = SIZE;
        this.length = 0;
    }

    /**
     * Gets the cached node, or null of it is node cached
     *
     * @param key The key to look for
     * @return The cached node, if any
     */
    public T get(long key) {
        if (length == 0)
            return null;
        for (int i = start; i != SIZE; i++) {
            if (keys[i] == key) {
                T result = nodes[i].get();
                if (result == null) {
                    keys[i] = FileStore.KEY_NULL;
                    nodes[i] = null;
                }
                return result;
            }
        }
        for (int i = 0; i != length - (SIZE - start); i++) {
            if (keys[i] == key) {
                T result = nodes[i].get();
                if (result == null) {
                    keys[i] = FileStore.KEY_NULL;
                    nodes[i] = null;
                }
                return result;
            }
        }
        return null;
    }

    /**
     * Caches a node
     *
     * @param node The node to cache
     */
    public void cache(T node) {
        start--;
        if (start < 0)
            start = SIZE - 1;
        keys[start] = node.getKey();
        nodes[start] = new WeakReference<>(node);
        length = length == SIZE ? SIZE : length + 1;
    }
}
