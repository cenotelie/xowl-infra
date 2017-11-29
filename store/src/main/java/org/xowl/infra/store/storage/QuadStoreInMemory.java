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

import fr.cenotelie.commons.storage.ConcurrentWriteException;
import fr.cenotelie.commons.storage.NoTransactionException;
import org.xowl.infra.lang.owl2.AnonymousIndividual;
import org.xowl.infra.store.execution.EvaluableExpression;
import org.xowl.infra.store.execution.ExecutionManager;
import org.xowl.infra.store.rdf.*;
import org.xowl.infra.store.storage.cache.CachedDataset;
import org.xowl.infra.store.storage.cache.CachedNodes;

import java.util.Collection;
import java.util.Iterator;
import java.util.WeakHashMap;

/**
 * Concrete implementation of an in-memory data store
 * This implementation delegates all its behavior to caching stores
 * This quad storage system is NOT transactional.
 *
 * @author Laurent Wouters
 */
class QuadStoreInMemory extends QuadStore {
    /**
     * The store for the nodes
     */
    private final CachedNodes nodes;
    /**
     * The store for the dataset
     */
    private final CachedDataset dataset;
    /**
     * The currently running transactions by thread
     */
    private final WeakHashMap<Thread, QuadTransaction> transactionsByThread;

    /**
     * Initializes this store
     */
    public QuadStoreInMemory() {
        nodes = new CachedNodes();
        dataset = new CachedDataset();
        transactionsByThread = new WeakHashMap<>();
    }

    public void setExecutionManager(ExecutionManager executionManager) {
        nodes.setExecutionManager(executionManager);
        dataset.setExecutionManager(executionManager);
    }

    @Override
    public QuadTransaction newTransaction(boolean writable, boolean autocommit) {
        QuadTransaction transaction = new QuadTransaction(writable, autocommit) {
            @Override
            protected void doCommit() throws ConcurrentWriteException {
                // do nothing
            }

            @Override
            protected void onClose() {
                transactionsByThread.remove(Thread.currentThread());
            }
        };
        synchronized (transactionsByThread) {
            transactionsByThread.put(Thread.currentThread(), transaction);
        }
        return transaction;
    }

    @Override
    public QuadTransaction getTransaction() throws NoTransactionException {
        return transactionsByThread.get(Thread.currentThread());
    }

    @Override
    public void addListener(ChangeListener listener) {
        dataset.addListener(listener);
    }

    @Override
    public void removeListener(ChangeListener listener) {
        dataset.removeListener(listener);
    }

    @Override
    public long getMultiplicity(Quad quad) throws UnsupportedNodeType {
        return dataset.getMultiplicity(quad);
    }

    @Override
    public long getMultiplicity(GraphNode graph, SubjectNode subject, Property property, Node object) {
        return dataset.getMultiplicity(graph, subject, property, object);
    }

    @Override
    public Iterator<Quad> getAll() {
        return dataset.getAll();
    }

    @Override
    public Iterator<Quad> getAll(GraphNode graph) throws UnsupportedNodeType {
        return dataset.getAll(graph);
    }

    @Override
    public Iterator<Quad> getAll(SubjectNode subject, Property property, Node object) throws UnsupportedNodeType {
        return dataset.getAll(subject, property, object);
    }

    @Override
    public Iterator<Quad> getAll(GraphNode graph, SubjectNode subject, Property property, Node object) {
        return dataset.getAll(graph, subject, property, object);
    }

    @Override
    public Collection<GraphNode> getGraphs() {
        return dataset.getGraphs();
    }

    @Override
    public long count() {
        return dataset.count();
    }

    @Override
    public long count(GraphNode graph) throws UnsupportedNodeType {
        return dataset.count(graph);
    }

    @Override
    public long count(SubjectNode subject, Property property, Node object) throws UnsupportedNodeType {
        return dataset.count(subject, property, object);
    }

    @Override
    public long count(GraphNode graph, SubjectNode subject, Property property, Node object) {
        return dataset.count(graph, subject, property, object);
    }


    @Override
    public void insert(Changeset changeset) throws UnsupportedNodeType {
        dataset.insert(changeset);
    }

    @Override
    public void add(Quad quad) throws UnsupportedNodeType {
        dataset.add(quad);
    }

    @Override
    public void add(GraphNode graph, SubjectNode subject, Property property, Node value) throws UnsupportedNodeType {
        dataset.add(graph, subject, property, value);
    }

    @Override
    public void remove(Quad quad) throws UnsupportedNodeType {
        dataset.remove(quad);
    }

    @Override
    public void remove(GraphNode graph, SubjectNode subject, Property property, Node value) throws UnsupportedNodeType {
        dataset.remove(graph, subject, property, value);
    }

    @Override
    public void clear() {
        dataset.clear();
    }

    @Override
    public void clear(GraphNode graph) throws UnsupportedNodeType {
        dataset.clear(graph);
    }

    @Override
    public void copy(GraphNode origin, GraphNode target, boolean overwrite) throws UnsupportedNodeType {
        dataset.copy(origin, target, overwrite);
    }

    @Override
    public void move(GraphNode origin, GraphNode target) throws UnsupportedNodeType {
        dataset.move(origin, target);
    }

    @Override
    public IRINode getIRINode(GraphNode graph) {
        return nodes.getIRINode(graph);
    }

    @Override
    public IRINode getIRINode(String iri) {
        return nodes.getIRINode(iri);
    }

    @Override
    public IRINode getExistingIRINode(String iri) {
        return nodes.getExistingIRINode(iri);
    }

    @Override
    public BlankNode getBlankNode() {
        return nodes.getBlankNode();
    }

    @Override
    public LiteralNode getLiteralNode(String lex, String datatype, String lang) {
        return nodes.getLiteralNode(lex, datatype, lang);
    }

    @Override
    public AnonymousNode getAnonNode(AnonymousIndividual individual) {
        return nodes.getAnonNode(individual);
    }

    @Override
    public DynamicNode getDynamicNode(EvaluableExpression evaluable) {
        return nodes.getDynamicNode(evaluable);
    }
}
