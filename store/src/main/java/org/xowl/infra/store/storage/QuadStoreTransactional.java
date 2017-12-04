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

import fr.cenotelie.commons.storage.NoTransactionException;
import org.xowl.infra.lang.owl2.AnonymousIndividual;
import org.xowl.infra.store.execution.EvaluableExpression;
import org.xowl.infra.store.rdf.*;

import java.util.Collection;
import java.util.Iterator;

/**
 * Represents a transactional quad store.
 * Accessing quads within this store requires the creation of a transaction.
 *
 * @author Laurent Wouters
 */
public abstract class QuadStoreTransactional implements QuadStore {
    /**
     * Starts a new transaction
     * The transaction must be ended by a call to the transaction's close method.
     * The transaction will NOT automatically commit when closed, the commit method should be called before closing.
     *
     * @param writable Whether the transaction shall support writing
     * @return The new transaction
     */
    public QuadStoreTransaction newTransaction(boolean writable) {
        return newTransaction(writable, false);
    }

    /**
     * Starts a new transaction
     * The transaction must be ended by a call to the transaction's close method.
     *
     * @param writable   Whether the transaction shall support writing
     * @param autocommit Whether this transaction should commit when being closed
     * @return The new transaction
     */
    public abstract QuadStoreTransaction newTransaction(boolean writable, boolean autocommit);

    /**
     * Gets the currently running transactions for the current thread
     *
     * @return The current transaction
     * @throws NoTransactionException when the current thread does not use a transaction
     */
    public abstract QuadStoreTransaction getTransaction() throws NoTransactionException;

    @Override
    public void addListener(ChangeListener listener) {
        getTransaction().getStore().addListener(listener);
    }

    @Override
    public void removeListener(ChangeListener listener) {
        getTransaction().getStore().removeListener(listener);
    }

    @Override
    public long getMultiplicity(Quad quad) {
        return getTransaction().getStore().getMultiplicity(quad);
    }

    @Override
    public long getMultiplicity(GraphNode graph, SubjectNode subject, Property property, Node object) {
        return getTransaction().getStore().getMultiplicity(graph, subject, property, object);
    }

    @Override
    public Iterator<? extends Quad> getAll() {
        return getTransaction().getStore().getAll();
    }

    @Override
    public Iterator<? extends Quad> getAll(GraphNode graph) {
        return getTransaction().getStore().getAll(graph);
    }

    @Override
    public Iterator<? extends Quad> getAll(SubjectNode subject, Property property, Node object) {
        return getTransaction().getStore().getAll(subject, property, object);
    }

    @Override
    public Iterator<? extends Quad> getAll(GraphNode graph, SubjectNode subject, Property property, Node object) {
        return getTransaction().getStore().getAll(graph, subject, property, object);
    }

    @Override
    public Collection<GraphNode> getGraphs() {
        return getTransaction().getStore().getGraphs();
    }

    @Override
    public long count() {
        return getTransaction().getStore().count();
    }

    @Override
    public long count(GraphNode graph) {
        return getTransaction().getStore().count(graph);
    }

    @Override
    public long count(SubjectNode subject, Property property, Node object) {
        return getTransaction().getStore().count(subject, property, object);
    }

    @Override
    public long count(GraphNode graph, SubjectNode subject, Property property, Node object) {
        return getTransaction().getStore().count(graph, subject, property, object);
    }

    @Override
    public void insert(Changeset changeset) {
        getTransaction().getStore().insert(changeset);
    }

    @Override
    public void add(Quad quad) {
        getTransaction().getStore().add(quad);
    }

    @Override
    public void add(GraphNode graph, SubjectNode subject, Property property, Node value) {
        getTransaction().getStore().add(graph, subject, property, value);
    }

    @Override
    public void remove(Quad quad) {
        getTransaction().getStore().remove(quad);
    }

    @Override
    public void remove(GraphNode graph, SubjectNode subject, Property property, Node value) {
        getTransaction().getStore().remove(graph, subject, property, value);
    }

    @Override
    public void clear() {
        getTransaction().getStore().clear();
    }

    @Override
    public void clear(GraphNode graph) {
        getTransaction().getStore().clear(graph);
    }

    @Override
    public void copy(GraphNode origin, GraphNode target, boolean overwrite) {
        getTransaction().getStore().copy(origin, target, overwrite);
    }

    @Override
    public void move(GraphNode origin, GraphNode target) {
        getTransaction().getStore().move(origin, target);
    }

    @Override
    public IRINode getIRINode(GraphNode graph) {
        return getTransaction().getStore().getIRINode(graph);
    }

    @Override
    public IRINode getIRINode(String iri) {
        return getTransaction().getStore().getIRINode(iri);
    }

    @Override
    public IRINode getExistingIRINode(String iri) {
        return getTransaction().getStore().getExistingIRINode(iri);
    }

    @Override
    public BlankNode getBlankNode() {
        return getTransaction().getStore().getBlankNode();
    }

    @Override
    public LiteralNode getLiteralNode(String lex, String datatype, String lang) {
        return getTransaction().getStore().getLiteralNode(lex, datatype, lang);
    }

    @Override
    public AnonymousNode getAnonNode(AnonymousIndividual individual) {
        return getTransaction().getStore().getAnonNode(individual);
    }

    @Override
    public DynamicNode getDynamicNode(EvaluableExpression evaluable) {
        return getTransaction().getStore().getDynamicNode(evaluable);
    }
}
