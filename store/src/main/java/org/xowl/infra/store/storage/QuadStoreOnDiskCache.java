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

package org.xowl.infra.store.storage;

import fr.cenotelie.commons.utils.logging.Logging;
import org.xowl.infra.store.execution.ExecutionManager;
import org.xowl.infra.store.rdf.*;
import org.xowl.infra.store.storage.cache.CachedDataset;
import org.xowl.infra.store.storage.persistent.PersistedDataset;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Represents the caching part of a a on-disk persistent data store
 *
 * @author Laurent Wouters
 */
class QuadStoreOnDiskCache extends DatasetImpl {
    /**
     * The maximum number of cached subjects
     */
    private static final int MAX_CACHED_SUBJECTS = 256;
    /**
     * The maximum number of cached graphs
     */
    private static final int MAX_CACHED_GRAPHS = 4;

    /**
     * The base persisted dataset
     */
    private final PersistedDataset persisted;
    /**
     * Collection of the cached subjects
     */
    private final ConcurrentHashMap<SubjectNode, CachedDataset> cachedSubjects;
    /**
     * Collection of the cached graphs
     */
    private final ConcurrentHashMap<GraphNode, CachedDataset> cachedGraphs;

    /**
     * Initializes this cache
     *
     * @param persisted The base persisted dataset
     */
    public QuadStoreOnDiskCache(PersistedDataset persisted) {
        this.persisted = persisted;
        this.cachedSubjects = new ConcurrentHashMap<>(MAX_CACHED_SUBJECTS);
        this.cachedGraphs = new ConcurrentHashMap<>(MAX_CACHED_GRAPHS);
    }

    /**
     * Gets the cache for a subject
     *
     * @param subject The subject node
     */
    private CachedDataset getCache(SubjectNode subject) {
        CachedDataset dataset = cachedSubjects.get(subject);
        if (dataset != null)
            return dataset;

        dataset = new CachedDataset();
        try {
            Iterator<Quad> iterator = persisted.getAll(null, subject, null, null);
            while (iterator.hasNext())
                dataset.add(iterator.next());
        } catch (UnsupportedNodeType exception) {
            Logging.get().error(exception);
        }
        CachedDataset old = cachedSubjects.putIfAbsent(subject, dataset);
        return old != null ? old : dataset;
    }

    /**
     * Gets the cache for a graph
     *
     * @param graph The graph
     */
    private CachedDataset getCache(GraphNode graph) {
        CachedDataset dataset = cachedGraphs.get(graph);
        if (dataset != null)
            return dataset;

        dataset = new CachedDataset();
        try {
            Iterator<? extends Quad> iterator = persisted.getAll(graph);
            while (iterator.hasNext())
                dataset.add(iterator.next());
        } catch (UnsupportedNodeType exception) {
            Logging.get().error(exception);
        }
        CachedDataset old = cachedGraphs.putIfAbsent(graph, dataset);
        return old != null ? old : dataset;
    }

    /**
     * Invalidates the entire cache
     */
    private void invalidate() {
        cachedSubjects.clear();
        cachedGraphs.clear();
    }

    /**
     * Invalidates the cache for the specified subject
     *
     * @param subject The updated subject
     */
    private void invalidate(SubjectNode subject) {
        cachedSubjects.remove(subject);
    }

    /**
     * Invalidates the cache for the specified graph
     *
     * @param graph The updated graph
     */
    private void invalidate(GraphNode graph) {
        cachedGraphs.remove(graph);
    }

    @Override
    public void setExecutionManager(ExecutionManager executionManager) {
        this.executionManager = executionManager;
    }

    @Override
    public int doAddQuad(GraphNode graph, SubjectNode subject, Property property, Node value) throws UnsupportedNodeType {
        int result = persisted.doAddQuad(graph, subject, property, value);
        if (result == ADD_RESULT_NEW) {
            invalidate(subject);
            invalidate(graph);
        }
        return result;
    }

    @Override
    public int doRemoveQuad(GraphNode graph, SubjectNode subject, Property property, Node value) throws UnsupportedNodeType {
        int result = persisted.doRemoveQuad(graph, subject, property, value);
        if (result >= REMOVE_RESULT_REMOVED) {
            invalidate(subject);
            invalidate(graph);
        }
        return result;
    }

    @Override
    public void doRemoveQuads(GraphNode graph, SubjectNode subject, Property property, Node value, List<MQuad> bufferDecremented, List<MQuad> bufferRemoved) throws UnsupportedNodeType {
        persisted.doRemoveQuads(graph, subject, property, value, bufferDecremented, bufferRemoved);
        for (MQuad quad : bufferRemoved) {
            invalidate(quad.getSubject());
            invalidate(quad.getGraph());
        }
    }

    @Override
    public void doClear(List<MQuad> buffer) {
        persisted.doClear(buffer);
        invalidate();
    }

    @Override
    public void doClear(GraphNode graph, List<MQuad> buffer) throws UnsupportedNodeType {
        persisted.doClear(graph, buffer);
        invalidate(graph);
        for (MQuad quad : buffer) {
            invalidate(quad.getSubject());
        }
    }

    @Override
    public void doCopy(GraphNode origin, GraphNode target, List<MQuad> bufferOld, List<MQuad> bufferNew, boolean overwrite) {
        try {
            persisted.doCopy(origin, target, bufferOld, bufferNew, overwrite);
            invalidate(origin);
            invalidate(target);
            for (MQuad quad : bufferOld) {
                invalidate(quad.getSubject());
            }
            for (MQuad quad : bufferNew) {
                invalidate(quad.getSubject());
            }
        } catch (UnsupportedNodeType exception) {
            Logging.get().error(exception);
        }
    }

    @Override
    public void doMove(GraphNode origin, GraphNode target, List<MQuad> bufferOld, List<MQuad> bufferNew) {
        try {
            persisted.doMove(origin, target, bufferOld, bufferNew);
            invalidate(origin);
            invalidate(target);
            for (MQuad quad : bufferOld) {
                invalidate(quad.getSubject());
            }
            for (MQuad quad : bufferNew) {
                invalidate(quad.getSubject());
            }
        } catch (UnsupportedNodeType exception) {
            Logging.get().error(exception);
        }
    }

    @Override
    public long getMultiplicity(GraphNode graph, SubjectNode subject, Property property, Node object) throws UnsupportedNodeType {
        CachedDataset cache = getCache(subject);
        return cache.getMultiplicity(graph, subject, property, object);
    }

    @Override
    public Iterator<Quad> getAll(GraphNode graph, SubjectNode subject, Property property, Node object) throws UnsupportedNodeType {
        if (subject != null && subject.getNodeType() != Node.TYPE_VARIABLE) {
            CachedDataset cache = getCache(subject);
            return cache.getAll(graph, subject, property, object);
        } else if (graph != null && graph.getNodeType() != Node.TYPE_VARIABLE) {
            CachedDataset cache = getCache(graph);
            return cache.getAll(graph, subject, property, object);
        } else {
            return persisted.getAll(graph, subject, property, object);
        }
    }

    @Override
    public Collection<GraphNode> getGraphs() {
        return persisted.getGraphs();
    }

    @Override
    public long count(GraphNode graph, SubjectNode subject, Property property, Node object) throws UnsupportedNodeType {
        if (subject != null && subject.getNodeType() != Node.TYPE_VARIABLE) {
            CachedDataset cache = getCache(subject);
            return cache.count(graph, subject, property, object);
        } else if (graph != null && graph.getNodeType() != Node.TYPE_VARIABLE) {
            CachedDataset cache = getCache(graph);
            return cache.count(graph, subject, property, object);
        } else {
            return persisted.count(graph, subject, property, object);
        }
    }
}
