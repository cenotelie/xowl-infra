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
import fr.cenotelie.commons.utils.metrics.MetricSnapshot;
import fr.cenotelie.commons.utils.metrics.MetricSnapshotComposite;
import org.xowl.infra.lang.owl2.AnonymousIndividual;
import org.xowl.infra.store.execution.EvaluableExpression;
import org.xowl.infra.store.execution.ExecutionManager;
import org.xowl.infra.store.rdf.*;
import org.xowl.infra.store.storage.cache.CachedNodes;
import org.xowl.infra.store.storage.persistent.PersistedDataset;
import org.xowl.infra.store.storage.persistent.PersistedNodes;
import org.xowl.infra.store.storage.persistent.StorageException;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

/**
 * Concrete implementation of a persisted data store.
 * This class is NOT thread safe.
 * This store uses a cache mechanism to improve its performance.
 *
 * @author Laurent Wouters
 */
class OnDiskStore extends BaseStore {
    /**
     * The store for the nodes
     */
    private final PersistedNodes persistedNodes;
    /**
     * The store for the dataset
     */
    private final PersistedDataset persistedDataset;
    /**
     * The node manager for the cache
     */
    private final CachedNodes cacheNodes;
    /**
     * The caching dataset
     */
    private final OnDiskStoreCache cacheDataset;

    /**
     * Initializes this store
     *
     * @param directory  The parent directory containing the backing files
     * @param isReadonly Whether this store is in readonly mode
     * @throws IOException      When the backing files cannot be accessed
     * @throws StorageException When the storage is in a bad state
     */
    public OnDiskStore(File directory, boolean isReadonly) throws IOException, StorageException {
        persistedNodes = new PersistedNodes(directory, isReadonly);
        persistedDataset = new PersistedDataset(persistedNodes, directory, isReadonly);
        cacheNodes = new CachedNodes();
        cacheDataset = new OnDiskStoreCache(persistedDataset);
        metricStore.addPart(persistedNodes.getMetric());
        metricStore.addPart(persistedDataset.getMetric());
    }

    @Override
    public MetricSnapshot getMetricSnapshot(long timestamp) {
        MetricSnapshotComposite snapshot = new MetricSnapshotComposite(timestamp);
        snapshot.addPart(persistedNodes.getMetric(), persistedNodes.getMetricSnapshot(timestamp));
        snapshot.addPart(persistedDataset.getMetric(), persistedDataset.getMetricSnapshot(timestamp));
        return snapshot;
    }

    @Override
    public void setExecutionManager(ExecutionManager executionManager) {
        persistedNodes.setExecutionManager(executionManager);
        persistedDataset.setExecutionManager(executionManager);
        cacheNodes.setExecutionManager(executionManager);
        cacheDataset.setExecutionManager(executionManager);
    }

    @Override
    public boolean commit() {
        boolean success = persistedNodes.flush();
        success &= persistedDataset.flush();
        return success;
    }

    @Override
    public void close() throws Exception {
        Exception toThrow = null;
        try {
            persistedNodes.close();
        } catch (Exception exception) {
            toThrow = exception;
        }
        try {
            persistedDataset.close();
        } catch (Exception exception) {
            if (toThrow != null)
                Logging.get().error(toThrow);
            toThrow = exception;
        }
        if (toThrow != null)
            throw toThrow;
    }

    @Override
    public void addListener(ChangeListener listener) {
        cacheDataset.addListener(listener);
    }

    @Override
    public void removeListener(ChangeListener listener) {
        cacheDataset.removeListener(listener);
    }

    @Override
    public long getMultiplicity(Quad quad) throws UnsupportedNodeType {
        return cacheDataset.getMultiplicity(quad);
    }

    @Override
    public long getMultiplicity(GraphNode graph, SubjectNode subject, Property property, Node object) throws UnsupportedNodeType {
        return cacheDataset.getMultiplicity(graph, subject, property, object);
    }

    @Override
    public Iterator<Quad> getAll() {
        return cacheDataset.getAll();
    }

    @Override
    public Iterator<Quad> getAll(GraphNode graph) throws UnsupportedNodeType {
        return cacheDataset.getAll(graph);
    }

    @Override
    public Iterator<Quad> getAll(SubjectNode subject, Property property, Node object) throws UnsupportedNodeType {
        return cacheDataset.getAll(subject, property, object);
    }

    @Override
    public Iterator<Quad> getAll(GraphNode graph, SubjectNode subject, Property property, Node object) throws UnsupportedNodeType {
        return cacheDataset.getAll(graph, subject, property, object);
    }

    @Override
    public Collection<GraphNode> getGraphs() {
        return cacheDataset.getGraphs();
    }

    @Override
    public long count() {
        return cacheDataset.count();
    }

    @Override
    public long count(GraphNode graph) throws UnsupportedNodeType {
        return cacheDataset.count(graph);
    }

    @Override
    public long count(SubjectNode subject, Property property, Node object) throws UnsupportedNodeType {
        return cacheDataset.count(subject, property, object);
    }

    @Override
    public long count(GraphNode graph, SubjectNode subject, Property property, Node object) throws UnsupportedNodeType {
        return cacheDataset.count(graph, subject, property, object);
    }


    @Override
    public void insert(Changeset changeset) throws UnsupportedNodeType {
        cacheDataset.insert(changeset);
    }

    @Override
    public void add(Quad quad) throws UnsupportedNodeType {
        cacheDataset.add(quad);
    }

    @Override
    public void add(GraphNode graph, SubjectNode subject, Property property, Node value) throws UnsupportedNodeType {
        cacheDataset.add(graph, subject, property, value);
    }

    @Override
    public void remove(Quad quad) throws UnsupportedNodeType {
        cacheDataset.remove(quad);
    }

    @Override
    public void remove(GraphNode graph, SubjectNode subject, Property property, Node value) throws UnsupportedNodeType {
        cacheDataset.remove(graph, subject, property, value);
    }

    @Override
    public void clear() {
        cacheDataset.clear();
    }

    @Override
    public void clear(GraphNode graph) throws UnsupportedNodeType {
        cacheDataset.clear(graph);
    }

    @Override
    public void copy(GraphNode origin, GraphNode target, boolean overwrite) throws UnsupportedNodeType {
        cacheDataset.copy(origin, target, overwrite);
    }

    @Override
    public void move(GraphNode origin, GraphNode target) throws UnsupportedNodeType {
        cacheDataset.move(origin, target);
    }

    @Override
    public IRINode getIRINode(GraphNode graph) {
        return cacheNodes.getIRINode(graph);
    }

    @Override
    public IRINode getIRINode(String iri) {
        return cacheNodes.getIRINode(iri);
    }

    @Override
    public IRINode getExistingIRINode(String iri) {
        return persistedNodes.getExistingIRINode(iri);
    }

    @Override
    public BlankNode getBlankNode() {
        return persistedNodes.getBlankNode();
    }

    @Override
    public LiteralNode getLiteralNode(String lex, String datatype, String lang) {
        return cacheNodes.getLiteralNode(lex, datatype, lang);
    }

    @Override
    public AnonymousNode getAnonNode(AnonymousIndividual individual) {
        return cacheNodes.getAnonNode(individual);
    }

    @Override
    public DynamicNode getDynamicNode(EvaluableExpression evaluable) {
        return cacheNodes.getDynamicNode(evaluable);
    }
}
