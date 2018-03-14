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

import fr.cenotelie.commons.storage.Transaction;
import org.xowl.infra.lang.owl2.AnonymousIndividual;
import org.xowl.infra.store.execution.EvaluableExpression;
import org.xowl.infra.store.execution.ExecutionManager;
import org.xowl.infra.store.rdf.*;

import java.util.Collection;
import java.util.Iterator;

/**
 * Base implementation of a dataset of RDF quads
 *
 * @author Laurent Wouters
 */
public abstract class DatasetImpl implements Dataset {
    /**
     * Gets the nodes manager
     *
     * @return The nodes manager
     */
    protected abstract DatasetNodesImpl getNodes();

    /**
     * Gets the quads manager
     *
     * @return The quads manager
     */
    protected abstract DatasetQuadsImpl getQuads();

    /**
     * Starts a new transaction
     * The transaction must be ended by a call to the transaction's close method.
     *
     * @param writable   Whether the transaction shall support writing
     * @param autocommit Whether this transaction should commit when being closed
     * @return The new transaction
     */
    protected abstract Transaction newTransaction(boolean writable, boolean autocommit);

    /**
     * Sets the execution manager to use
     *
     * @param executionManager The execution manager to use
     */
    public void setExecutionManager(ExecutionManager executionManager) {
        getNodes().setExecutionManager(executionManager);
        getQuads().setExecutionManager(this, executionManager);
    }

    /**
     * Adds the specified listener to this store
     *
     * @param listener A listener
     */
    public void addListener(ChangeListener listener) {
        getQuads().addListener(listener);
    }

    /**
     * Removes the specified listener from this store
     *
     * @param listener A listener
     */
    public void removeListener(ChangeListener listener) {
        getQuads().removeListener(listener);
    }

    @Override
    public long getMultiplicity(Quad quad) throws UnsupportedNodeType {
        return getQuads().getMultiplicity(quad);
    }

    @Override
    public long getMultiplicity(GraphNode graph, SubjectNode subject, Property property, Node object) {
        return getQuads().getMultiplicity(graph, subject, property, object);
    }

    @Override
    public Iterator<? extends Quad> getAll() {
        return getQuads().getAll();
    }

    @Override
    public Iterator<? extends Quad> getAll(GraphNode graph) throws UnsupportedNodeType {
        return getQuads().getAll(graph);
    }

    @Override
    public Iterator<? extends Quad> getAll(SubjectNode subject, Property property, Node object) throws UnsupportedNodeType {
        return getQuads().getAll(subject, property, object);
    }

    @Override
    public Iterator<? extends Quad> getAll(GraphNode graph, SubjectNode subject, Property property, Node object) {
        return getQuads().getAll(graph, subject, property, object);
    }

    @Override
    public Collection<GraphNode> getGraphs() {
        return getQuads().getGraphs();
    }

    @Override
    public long count() {
        return getQuads().count();
    }

    @Override
    public long count(GraphNode graph) throws UnsupportedNodeType {
        return getQuads().count(graph);
    }

    @Override
    public long count(SubjectNode subject, Property property, Node object) throws UnsupportedNodeType {
        return getQuads().count(subject, property, object);
    }

    @Override
    public long count(GraphNode graph, SubjectNode subject, Property property, Node object) {
        return getQuads().count(graph, subject, property, object);
    }


    @Override
    public void insert(Changeset changeset) throws UnsupportedNodeType {
        getQuads().insert(changeset);
    }

    @Override
    public void add(Quad quad) throws UnsupportedNodeType {
        getQuads().add(quad);
    }

    @Override
    public void add(GraphNode graph, SubjectNode subject, Property property, Node value) throws UnsupportedNodeType {
        getQuads().add(graph, subject, property, value);
    }

    @Override
    public void remove(Quad quad) throws UnsupportedNodeType {
        getQuads().remove(quad);
    }

    @Override
    public void remove(GraphNode graph, SubjectNode subject, Property property, Node value) throws UnsupportedNodeType {
        getQuads().remove(graph, subject, property, value);
    }

    @Override
    public void clear() {
        getQuads().clear();
    }

    @Override
    public void clear(GraphNode graph) throws UnsupportedNodeType {
        getQuads().clear(graph);
    }

    @Override
    public void copy(GraphNode origin, GraphNode target, boolean overwrite) throws UnsupportedNodeType {
        getQuads().copy(origin, target, overwrite);
    }

    @Override
    public void move(GraphNode origin, GraphNode target) throws UnsupportedNodeType {
        getQuads().move(origin, target);
    }

    @Override
    public IRINode getIRINode(GraphNode graph) {
        return getNodes().getIRINode(graph);
    }

    @Override
    public IRINode getIRINode(String iri) {
        return getNodes().getIRINode(iri);
    }

    @Override
    public IRINode getExistingIRINode(String iri) {
        return getNodes().getExistingIRINode(iri);
    }

    @Override
    public BlankNode getBlankNode() {
        return getNodes().getBlankNode();
    }

    @Override
    public LiteralNode getLiteralNode(String lex, String datatype, String lang) {
        return getNodes().getLiteralNode(lex, datatype, lang);
    }

    @Override
    public AnonymousNode getAnonNode(AnonymousIndividual individual) {
        return getNodes().getAnonNode(individual);
    }

    @Override
    public DynamicNode getDynamicNode(EvaluableExpression evaluable) {
        return getNodes().getDynamicNode(evaluable);
    }
}
