/**********************************************************************
 * Copyright (c) 2014 Laurent Wouters and others
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
 **********************************************************************/
package org.xowl.store.query;

import org.xowl.store.rdf.RDFStore;

import java.util.Collection;

/**
 * Represents a buffered query engine for RDF dataset
 *
 * @author Laurent Wouters
 */
public class BufferedEngine extends Engine {
    /**
     * The maximal number of cached queries
     */
    private static final int MAX_CACHED = 128;
    /**
     * The query cache
     * The query at cache[0] has the greatest hit count
     */
    private Cache[] cache;
    /**
     * The size of the cache
     */
    private int size;
    /**
     * Initializes this engine
     *
     * @param store The RDF store to query
     */
    public BufferedEngine(RDFStore store) {
        super(store);
        this.cache = new Cache[MAX_CACHED];
        this.size = 0;
    }

    public Collection<Solution> execute(Query query) {
        Cache cache = resolve(query);
        apply();
        return cache.query.getOutput().getSolutions();
    }

    /**
     * Resolves the specified query against the cache
     *
     * @param query The query to look for
     * @return The cached query
     */
    private Cache resolve(Query query) {
        // look for a matching query
        for (int i = 0; i != size; i++) {
            if (matches(cache[i], query)) {
                // found one!
                cache[i].hitCount++;
                // bubble it up as much as possible and return it
                return cache[promote(i)];
            } else {
                cache[i].hitCount--;
            }
        }

        // at this point, no matching cached query was found
        int index = size == cache.length ? size - 1 : size;
        cache[index] = new Cache(query);
        return cache[promote(index)];
    }

    /**
     * Promotes the cached query at the specified index as much as possible
     *
     * @param index The index of a cached query
     * @return The subsequent index of the cached query
     */
    private int promote(int index) {
        while (index != 0 && cache[index].hitCount > cache[index - 1].hitCount) {
            Cache temp = cache[index - 1];
            cache[index - 1] = cache[index];
            cache[index] = temp;
            index--;
        }
        return index;
    }

    /**
     * Determines whether the specified query matches a registered cached one
     *
     * @param cache A cached query
     * @param query A query to match
     * @return <code>true</code> if the query matches the cached one
     */
    private boolean matches(Cache cache, Query query) {
        return (cache.query == query);
    }

    /**
     * Represents a cached query with a hit count
     */
    private static class Cache {
        /**
         * The original query
         */
        public Query query;
        /**
         * The hit count for this query
         */
        public int hitCount;

        public Cache(Query query) {
            this.query = query;
            this.hitCount = 1;
        }
    }
}
