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

import org.xowl.store.RDFUtils;
import org.xowl.store.rdf.*;
import org.xowl.store.storage.cache.CachedDataset;
import org.xowl.store.storage.impl.DatasetImpl;
import org.xowl.store.storage.impl.MQuad;
import org.xowl.store.storage.persistent.PersistedDataset;
import org.xowl.utils.logging.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Represents the caching part of a a on-disk persistent data store
 *
 * @author Laurent Wouters
 */
class OnDiskStoreCache extends DatasetImpl {
    /**
     * The maximum number of cached subjects
     */
    private static final int MAX_CACHED_SUBJECTS = 256;
    /**
     * The maximum number of cached graphs
     */
    private static final int MAX_CACHED_GRAPHS = 4;

    /**
     * Represents a collection of cached elements
     */
    private static class Part {
        /**
         * The cached nodes
         */
        private final Node[] nodes;
        /**
         * The last hit time
         */
        private final long[] hits;
        /**
         * The number of cached items
         */
        private int count;
        /**
         * The current time
         */
        private long current;

        /**
         * Initializes the cache
         *
         * @param size The maximum number of items in this cache
         */
        public Part(int size) {
            this.nodes = new Node[size];
            this.hits = new long[size];
            this.count = 0;
            this.current = Long.MIN_VALUE;
        }

        /**
         * Gets whether the specified node is cached
         *
         * @param node The node to look for
         * @return true if the node is cached
         */
        public boolean contains(Node node) {
            for (int i = 0; i != count; i++) {
                if (RDFUtils.same(nodes[i], node))
                    return true;
            }
            return false;
        }

        /**
         * Caches a node
         * If the node was already in the cached, return it.
         * If the node was simply added, return null.
         * If the insertion resulted in another node being dropped, return the dropped node.
         *
         * @param node The node to cache
         * @return The result
         */
        public Node cache(Node node) {
            current++;
            int oldestIndex = -1;
            long oldestTime = Long.MAX_VALUE;
            for (int i = 0; i != count; i++) {
                if (RDFUtils.same(nodes[i], node)) {
                    hits[i] = current;
                    return node;
                }
                if (hits[i] < oldestTime) {
                    oldestTime = hits[i];
                    oldestIndex = i;
                }
            }
            // not in this collection
            if (count < nodes.length) {
                // still not full
                nodes[count] = node;
                hits[count] = current;
                count++;
                return null;
            }
            // the collection is full, we need to drop the oldest element
            Node toDrop = nodes[oldestIndex];
            nodes[oldestIndex] = node;
            hits[oldestIndex] = current;
            return toDrop;
        }
    }

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
     * Collection of the cached subjects
     */
    private final Part cachedSubjects;
    /**
     * Collection of the cached graphs
     */
    private final Part cachedGraphs;
    /**
     * The estimated size of this cache
     */
    private int size;

    /**
     * Initializes this cache
     *
     * @param persisted The base persisted dataset
     */
    public OnDiskStoreCache(PersistedDataset persisted) {
        this.persisted = persisted;
        this.cache = new CachedDataset();
        this.diff = new DiffDataset(cache);
        this.cachedSubjects = new Part(MAX_CACHED_SUBJECTS);
        this.cachedGraphs = new Part(MAX_CACHED_GRAPHS);
        this.size = 0;
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
     * Makes sure that the quads for the specified subject node are all in the cache
     *
     * @param subject The subject node
     */
    private void ensureInCache(SubjectNode subject) {
        Node result = cachedSubjects.cache(subject);
        if (result == subject) {
            // the subject is already cached
            return;
        }
        if (result != null) {
            commit();
            // drop the quads for the old subject
            Iterator<Quad> iterator = cache.getAll((SubjectNode) result, null, null);
            while (iterator.hasNext()) {
                Quad quad = iterator.next();
                if (!cachedGraphs.contains(quad.getGraph())) {
                    iterator.remove();
                    size--;
                }
            }
        }
        Iterator<MQuad> iterator = (Iterator) persisted.getAll(subject, null, null);
        try {
            while (iterator.hasNext()) {
                MQuad quad = iterator.next();
                if (!cachedGraphs.contains(quad.getGraph())) {
                    for (int i = 0; i < quad.getMultiplicity(); i++) {
                        cache.add(quad);
                    }
                    size++;
                }
            }
        } catch (UnsupportedNodeType exception) {
            // cannot happen
        }
    }

    /**
     * Makes sure that the quads for the specified graph are all in the cache
     *
     * @param graph The graph
     */
    private void ensureInCache(GraphNode graph) {
        Node result = cachedGraphs.cache(graph);
        if (result == graph) {
            // the graph is already cached
            return;
        }
        if (result != null) {
            commit();
            // drop the quads for the old graph
            Iterator<Quad> iterator = cache.getAll((GraphNode) result);
            while (iterator.hasNext()) {
                Quad quad = iterator.next();
                if (!cachedSubjects.contains(quad.getSubject())) {
                    iterator.remove();
                    size--;
                }
            }
        }
        Iterator<MQuad> iterator = (Iterator) persisted.getAll(graph);
        try {
            while (iterator.hasNext()) {
                MQuad quad = iterator.next();
                if (!cachedSubjects.contains(quad.getSubject())) {
                    for (int i = 0; i < quad.getMultiplicity(); i++) {
                        cache.add(quad);
                    }
                    size++;
                }
            }
        } catch (UnsupportedNodeType exception) {
            // cannot happen
        }
    }

    /**
     * Commits the outstanding changes to this cache
     */
    public void commit() {
        try {
            persisted.insert(diff.getChangeset());
        } catch (UnsupportedNodeType exception) {
            persisted.rollback();
            Logger.DEFAULT.error(exception);
        }
        diff.commit();
        persisted.commit();
    }

    /**
     * Rollbacks any outstanding changes to this cache
     */
    public void rollback() {
        diff.rollback();
    }

    @Override
    public int doAddQuad(GraphNode graph, SubjectNode subject, Property property, Node value) throws UnsupportedNodeType {
        ensureInCache(subject);
        int result = diff.doAddQuad(graph, subject, property, value);
        if (result == DatasetImpl.ADD_RESULT_NEW)
            size++;
        return result;
    }

    @Override
    public int doRemoveQuad(GraphNode graph, SubjectNode subject, Property property, Node value) throws UnsupportedNodeType {
        ensureInCache(subject);
        int result = diff.doRemoveQuad(graph, subject, property, value);
        if (result >= DatasetImpl.REMOVE_RESULT_REMOVED)
            size--;
        return result;
    }

    @Override
    public void doRemoveQuads(GraphNode graph, SubjectNode subject, Property property, Node value, List<MQuad> bufferDecremented, List<MQuad> bufferRemoved) throws UnsupportedNodeType {
        if (subject != null && subject.getNodeType() != Node.TYPE_VARIABLE) {
            ensureInCache(subject);
            int before = bufferRemoved.size();
            diff.doRemoveQuads(graph, subject, property, value, bufferDecremented, bufferRemoved);
            size -= bufferRemoved.size() - before;
        } else if (graph != null && graph.getNodeType() != Node.TYPE_VARIABLE) {
            ensureInCache(graph);
            int before = bufferRemoved.size();
            diff.doRemoveQuads(graph, subject, property, value, bufferDecremented, bufferRemoved);
            size -= bufferRemoved.size() - before;
        } else {
            commit();
            // resync the cache
            int beforeDecremented = bufferDecremented.size();
            int beforeRemoved = bufferRemoved.size();
            persisted.doRemoveQuads(graph, subject, property, value, bufferDecremented, bufferRemoved);
            for (int i = beforeDecremented; i != bufferDecremented.size(); i++) {
                MQuad quad = bufferDecremented.get(i);
                if (cachedSubjects.contains(quad.getSubject()) || cachedGraphs.contains(quad.getGraph())) {
                    cache.doRemoveQuad(quad.getGraph(), quad.getSubject(), quad.getProperty(), quad.getObject());
                }
            }
            for (int i = beforeRemoved; i != bufferRemoved.size(); i++) {
                MQuad quad = bufferRemoved.get(i);
                if (cachedSubjects.contains(quad.getSubject()) || cachedGraphs.contains(quad.getGraph())) {
                    cache.doRemoveQuad(quad.getGraph(), quad.getSubject(), quad.getProperty(), quad.getObject());
                }
            }
            size -= bufferRemoved.size() - beforeRemoved;
        }
    }

    @Override
    public void doClear(List<MQuad> buffer) {
        commit();
        cache.doClear(new ArrayList<MQuad>());
        persisted.doClear(buffer);
        size = 0;
    }

    @Override
    public void doClear(GraphNode graph, List<MQuad> buffer) {
        ensureInCache(graph);
        int originalSize = buffer.size();
        diff.doClear(graph, buffer);
        size -= (buffer.size() - originalSize);
    }

    @Override
    public void doCopy(GraphNode origin, GraphNode target, List<MQuad> bufferOld, List<MQuad> bufferNew, boolean overwrite) {
        ensureInCache(origin);
        ensureInCache(target);
        int beforeOld = bufferOld.size();
        int beforeNew = bufferNew.size();
        diff.doCopy(origin, target, bufferOld, bufferNew, overwrite);
        size += (bufferNew.size() - beforeNew) - (bufferOld.size() - beforeOld);
    }

    @Override
    public void doMove(GraphNode origin, GraphNode target, List<MQuad> bufferOld, List<MQuad> bufferNew) {
        ensureInCache(origin);
        ensureInCache(target);
        int beforeOld = bufferOld.size();
        int beforeNew = bufferNew.size();
        diff.doMove(origin, target, bufferOld, bufferNew);
        size += (bufferNew.size() - beforeNew) - (bufferOld.size() - beforeOld);
    }

    @Override
    public long getMultiplicity(GraphNode graph, SubjectNode subject, Property property, Node object) {
        ensureInCache(subject);
        return diff.getMultiplicity(graph, subject, property, object);
    }

    @Override
    public Iterator<Quad> getAll(GraphNode graph, SubjectNode subject, Property property, Node object) {
        if (subject != null && subject.getNodeType() != Node.TYPE_VARIABLE) {
            ensureInCache(subject);
            return diff.getAll(graph, subject, property, object);
        } else if (graph != null && graph.getNodeType() != Node.TYPE_VARIABLE) {
            ensureInCache(graph);
            return diff.getAll(graph, subject, property, object);
        } else {
            commit();
            return persisted.getAll(graph, subject, property, object);
        }
    }

    @Override
    public Collection<GraphNode> getGraphs() {
        Collection<GraphNode> result = persisted.getGraphs();
        for (GraphNode graph : diff.getGraphs()) {
            if (!result.contains(graph))
                result.add(graph);
        }
        return result;
    }

    @Override
    public long count(GraphNode graph, SubjectNode subject, Property property, Node object) {
        if (subject != null && subject.getNodeType() != Node.TYPE_VARIABLE) {
            ensureInCache(subject);
            return diff.count(graph, subject, property, object);
        } else if (graph != null && graph.getNodeType() != Node.TYPE_VARIABLE) {
            ensureInCache(graph);
            return diff.count(graph, subject, property, object);
        } else {
            commit();
            return persisted.count(graph, subject, property, object);
        }
    }
}
