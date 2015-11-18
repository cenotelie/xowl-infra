/*******************************************************************************
 * Copyright (c) 2015 Laurent Wouters
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

package org.xowl.store.storage;

import org.xowl.store.rdf.*;
import org.xowl.store.storage.cache.CachedDataset;
import org.xowl.store.storage.impl.DatasetImpl;
import org.xowl.store.storage.impl.MQuad;
import org.xowl.store.storage.persistent.PersistedDataset;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Represents a part of a cache for the on-disk store.
 * A cache part represents a set of cached quad corresponding either to a single subject, or to a single graph
 *
 * @author Laurent Wouters
 */
class OnDiskStoreCache extends DatasetImpl implements AutoCloseable {
    /**
     * The base persisted dataset
     */
    private final PersistedDataset persisted;
    /**
     * The cache corresponding exactly to the on-disk data
     */
    private final CachedDataset cache;
    /**
     * The current state of the cached data with the pending changes applied
     */
    private final DiffDataset diff;
    /**
     * Whether this cache part is clean, i.e. it has no pending changes
     */
    private boolean isClean;
    /**
     * The estimated size of this cache
     */
    private int size;

    /**
     * Gets whether this cache is clean
     *
     * @return Whether the cache is clean
     */
    public boolean isClean() {
        return isClean;
    }

    /**
     * Gets the estimated size of this cache
     *
     * @return The estimated size of this cache
     */
    public int getSize() {
        return size;
    }

    /**
     * Initializes this cache part for a subject node
     *
     * @param persisted The base persisted dataset
     * @param subject   The subject node for which to cache the quads
     */
    public OnDiskStoreCache(PersistedDataset persisted, SubjectNode subject) {
        this.persisted = persisted;
        this.cache = new CachedDataset();
        this.diff = new DiffDataset(cache);
        this.isClean = true;
        doCache((Iterator) persisted.getAll(subject, null, null));
    }

    /**
     * Initializes this cache part for a graph
     *
     * @param persisted The base persisted dataset
     * @param graph     The graph node for which to cache the quads
     */
    public OnDiskStoreCache(PersistedDataset persisted, GraphNode graph) {
        this.persisted = persisted;
        this.cache = new CachedDataset();
        this.diff = new DiffDataset(cache);
        this.isClean = true;
        doCache((Iterator) persisted.getAll(graph));
    }

    /**
     * Loads this cache for the specified iterator
     *
     * @param iterator The iterator over the quads to cache
     */
    private void doCache(Iterator<MQuad> iterator) {
        try {
            while (iterator.hasNext()) {
                MQuad quad = iterator.next();
                for (int i = 0; i < quad.getMultiplicity(); i++) {
                    cache.add(quad);
                }
                size++;
            }
        } catch (UnsupportedNodeType exception) {
            // cannot happen
        }
    }

    /**
     * Commits the outstanding changes to this cache
     */
    public void commit() throws UnsupportedNodeType {
        persisted.insert(diff.getChangeset());
        persisted.commit();
        diff.rollback();
        isClean = true;
    }

    /**
     * Rollbacks any pending changes on this cache
     */
    public void rollback() {
        diff.rollback();
    }

    @Override
    public void close() throws Exception {
        commit();
    }

    @Override
    public int doAddQuad(GraphNode graph, SubjectNode subject, Property property, Node value) throws UnsupportedNodeType {
        isClean = false;
        int result = diff.doAddQuad(graph, subject, property, value);
        if (result == DatasetImpl.ADD_RESULT_NEW)
            size++;
        return result;
    }

    @Override
    public int doRemoveQuad(GraphNode graph, SubjectNode subject, Property property, Node value) throws UnsupportedNodeType {
        isClean = false;
        int result = diff.doRemoveQuad(graph, subject, property, value);
        if (result >= DatasetImpl.REMOVE_RESULT_REMOVED)
            size--;
        return result;
    }

    @Override
    public void doRemoveQuads(GraphNode graph, SubjectNode subject, Property property, Node value, List<MQuad> bufferDecremented, List<MQuad> bufferRemoved) throws UnsupportedNodeType {
        isClean = false;
        int before = bufferRemoved.size();
        diff.doRemoveQuads(graph, subject, property, value, bufferDecremented, bufferRemoved);
        size -= bufferRemoved.size() - before;
    }

    @Override
    public void doClear(List<MQuad> buffer) {
        isClean = false;
        int before = buffer.size();
        diff.doClear(buffer);
        size -= buffer.size() - before;
    }

    @Override
    public void doClear(GraphNode graph, List<MQuad> buffer) {
        isClean = false;
        int before = buffer.size();
        diff.doClear(graph, buffer);
        size -= buffer.size() - before;
    }

    @Override
    public void doCopy(GraphNode origin, GraphNode target, List<MQuad> bufferOld, List<MQuad> bufferNew, boolean overwrite) {
        isClean = false;
        int beforeOld = bufferOld.size();
        int beforeNew = bufferNew.size();
        diff.doCopy(origin, target, bufferOld, bufferNew, overwrite);
        size += (bufferNew.size() - beforeNew) - (bufferOld.size() - beforeOld);
    }

    @Override
    public void doMove(GraphNode origin, GraphNode target, List<MQuad> bufferOld, List<MQuad> bufferNew) {
        isClean = false;
        int beforeOld = bufferOld.size();
        int beforeNew = bufferNew.size();
        diff.doMove(origin, target, bufferOld, bufferNew);
        size += (bufferNew.size() - beforeNew) - (bufferOld.size() - beforeOld);
    }

    @Override
    public long getMultiplicity(GraphNode graph, SubjectNode subject, Property property, Node object) {
        return diff.getMultiplicity(graph, subject, property, object);
    }

    @Override
    public Iterator<Quad> getAll(GraphNode graph, SubjectNode subject, Property property, Node object) {
        return diff.getAll(graph, subject, property, object);
    }

    @Override
    public Collection<GraphNode> getGraphs() {
        return diff.getGraphs();
    }

    @Override
    public long count(GraphNode graph, SubjectNode subject, Property property, Node object) {
        return diff.count(graph, subject, property, object);
    }
}
