/*******************************************************************************
 * Copyright (c) 2017 Association Cénotélie (cenotelie.fr)
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

import org.xowl.infra.lang.owl2.AnonymousIndividual;
import org.xowl.infra.store.execution.EvaluableExpression;
import org.xowl.infra.store.execution.ExecutionManager;
import org.xowl.infra.store.rdf.*;
import org.xowl.infra.store.storage.cache.CachedNodes;
import org.xowl.infra.store.storage.persistent.PersistedDataset;
import org.xowl.infra.store.storage.persistent.PersistedNodes;

import java.util.Collection;
import java.util.Iterator;

/**
 * Base implementation of a quad store that is persisted in a file, presumably on disk
 *
 * @author Laurent Wouters
 */
abstract class QuadStoreOnDisk implements QuadStore {
    /**
     * The store for the nodes
     */
    protected PersistedNodes persistedNodes;
    /**
     * The store for the dataset
     */
    protected PersistedDataset persistedDataset;
    /**
     * The node manager for the cache
     */
    protected CachedNodes cacheNodes;

    /**
     * Initializes this store
     */
    protected QuadStoreOnDisk() {
    }

    /**
     * Initializes this store
     *
     * @param toCopy The store to copy
     */
    protected QuadStoreOnDisk(QuadStoreOnDisk toCopy) {
        this.persistedNodes = toCopy.persistedNodes;
        this.persistedDataset = toCopy.persistedDataset;
        this.cacheNodes = toCopy.cacheNodes;
    }

    @Override
    public void setExecutionManager(ExecutionManager executionManager) {
        persistedNodes.setExecutionManager(executionManager);
        persistedDataset.setExecutionManager(executionManager);
        cacheNodes.setExecutionManager(executionManager);
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
    public long getMultiplicity(Quad quad) throws UnsupportedNodeType {
        return persistedDataset.getMultiplicity(quad);
    }

    @Override
    public long getMultiplicity(GraphNode graph, SubjectNode subject, Property property, Node object) throws UnsupportedNodeType {
        return persistedDataset.getMultiplicity(graph, subject, property, object);
    }

    @Override
    public Iterator<? extends Quad> getAll() {
        return persistedDataset.getAll();
    }

    @Override
    public Iterator<? extends Quad> getAll(GraphNode graph) throws UnsupportedNodeType {
        return persistedDataset.getAll(graph);
    }

    @Override
    public Iterator<? extends Quad> getAll(SubjectNode subject, Property property, Node object) throws UnsupportedNodeType {
        return persistedDataset.getAll(subject, property, object);
    }

    @Override
    public Iterator<? extends Quad> getAll(GraphNode graph, SubjectNode subject, Property property, Node object) throws UnsupportedNodeType {
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
    public long count(GraphNode graph) throws UnsupportedNodeType {
        return persistedDataset.count(graph);
    }

    @Override
    public long count(SubjectNode subject, Property property, Node object) throws UnsupportedNodeType {
        return persistedDataset.count(subject, property, object);
    }

    @Override
    public long count(GraphNode graph, SubjectNode subject, Property property, Node object) throws UnsupportedNodeType {
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
    public void clear(GraphNode graph) throws UnsupportedNodeType {
        persistedDataset.clear(graph);
    }

    @Override
    public void copy(GraphNode origin, GraphNode target, boolean overwrite) throws UnsupportedNodeType {
        persistedDataset.copy(origin, target, overwrite);
    }

    @Override
    public void move(GraphNode origin, GraphNode target) throws UnsupportedNodeType {
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
