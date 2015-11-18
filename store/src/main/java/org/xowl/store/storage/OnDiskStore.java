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

import org.xowl.lang.owl2.AnonymousIndividual;
import org.xowl.store.owl.AnonymousNode;
import org.xowl.store.rdf.*;
import org.xowl.store.storage.cache.CachedNodes;
import org.xowl.store.storage.persistent.PersistedDataset;
import org.xowl.store.storage.persistent.PersistedNodes;
import org.xowl.store.storage.persistent.StorageException;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Concrete implementation of a persisted data store.
 * This class is NOT thread safe.
 * This store uses a cache mechanism to improve its performance.
 *
 * @author Laurent Wouters
 */
class OnDiskStore implements BaseStore {
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
     * The cache parts for specific subjects
     */
    private final Map<SubjectNode, OnDiskStoreCache> cacheBySubject;
    /**
     * The cache parts for specific graphs
     */
    private final Map<GraphNode, OnDiskStoreCache> cacheByGraph;
    /**
     * The estimated size
     */
    private int cacheSize;
    /**
     * The number of cache hit
     */
    private long cacheHitCount;
    /**
     * The number of cache miss
     */
    private long cacheMissCount;

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
        cacheBySubject = new HashMap<>();
        cacheByGraph = new HashMap<>();
        cacheSize = 0;
        cacheHitCount = 0;
        cacheMissCount = 0;
    }

    @Override
    public boolean commit() {
        // commit all caches
        boolean success = true;
        for (OnDiskStoreCache part : cacheBySubject.values()) {
            if (!part.isClean()) {
                try {
                    part.commit();
                } catch (Exception exception) {
                    success = false;
                }
            }
        }
        for (OnDiskStoreCache part : cacheByGraph.values()) {
            if (!part.isClean()) {
                try {
                    part.commit();
                } catch (Exception exception) {
                    success = false;
                }
            }
        }
        // commit the persistence layer
        success &= persistedNodes.commit();
        success &= persistedDataset.commit();
        return success;
    }

    @Override
    public boolean rollback() {
        for (OnDiskStoreCache part : cacheBySubject.values()) {
            if (!part.isClean()) {
                part.rollback();
            }
        }
        for (OnDiskStoreCache part : cacheBySubject.values()) {
            if (!part.isClean()) {
                part.rollback();
            }
        }
        boolean success = persistedNodes.rollback();
        success &= persistedDataset.rollback();
        return success;
    }

    @Override
    public void addListener(ChangeListener listener) {
        persistedDataset.addListener(listener);
    }

    @Override
    public void removeListener(ChangeListener listener) {
        persistedDataset.removeListener(listener);
    }

    @Override
    public long getMultiplicity(Quad quad) {
        return persistedDataset.getMultiplicity(quad);
    }

    @Override
    public long getMultiplicity(GraphNode graph, SubjectNode subject, Property property, Node object) {
        return persistedDataset.getMultiplicity(graph, subject, property, object);
    }

    @Override
    public Iterator<Quad> getAll() {
        return persistedDataset.getAll();
    }

    @Override
    public Iterator<Quad> getAll(GraphNode graph) {
        return persistedDataset.getAll(graph);
    }

    @Override
    public Iterator<Quad> getAll(SubjectNode subject, Property property, Node object) {
        return persistedDataset.getAll(subject, property, object);
    }

    @Override
    public Iterator<Quad> getAll(GraphNode graph, SubjectNode subject, Property property, Node object) {
        return persistedDataset.getAll(graph, subject, property, object);
    }

    @Override
    public Collection<GraphNode> getGraphs() {
        return persistedDataset.getGraphs();
    }

    @Override
    public long count() {
        return persistedDataset.count();
    }

    @Override
    public long count(GraphNode graph) {
        return persistedDataset.count(graph);
    }

    @Override
    public long count(SubjectNode subject, Property property, Node object) {
        return persistedDataset.count(subject, property, object);
    }

    @Override
    public long count(GraphNode graph, SubjectNode subject, Property property, Node object) {
        return persistedDataset.count(graph, subject, property, object);
    }


    @Override
    public void insert(Changeset changeset) throws UnsupportedNodeType {
        persistedDataset.insert(changeset);
    }

    @Override
    public void add(Quad quad) throws UnsupportedNodeType {
        persistedDataset.add(quad);
    }

    @Override
    public void add(GraphNode graph, SubjectNode subject, Property property, Node value) throws UnsupportedNodeType {
        persistedDataset.add(graph, subject, property, value);
    }

    @Override
    public void remove(Quad quad) throws UnsupportedNodeType {
        persistedDataset.remove(quad);
    }

    @Override
    public void remove(GraphNode graph, SubjectNode subject, Property property, Node value) throws UnsupportedNodeType {
        persistedDataset.remove(graph, subject, property, value);
    }

    @Override
    public void clear() {
        persistedDataset.clear();
    }

    @Override
    public void clear(GraphNode graph) {
        persistedDataset.clear(graph);
    }

    @Override
    public void copy(GraphNode origin, GraphNode target, boolean overwrite) {
        persistedDataset.copy(origin, target, overwrite);
    }

    @Override
    public void move(GraphNode origin, GraphNode target) {
        persistedDataset.move(origin, target);
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
        return cacheNodes.getExistingIRINode(iri);
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
    public void close() throws Exception {
        Exception ex = null;
        try {
            persistedNodes.close();
        } catch (Exception exception) {
            ex = exception;
        }
        try {
            persistedDataset.close();
        } catch (Exception exception) {
            // TODO: clean this, the previous ex could be swallowed
            ex = exception;
        }
        throw ex;
    }
}
