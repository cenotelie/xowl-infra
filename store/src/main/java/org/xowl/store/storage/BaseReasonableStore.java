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
import org.xowl.store.IRIs;
import org.xowl.store.RDFUtils;
import org.xowl.store.owl.AnonymousNode;
import org.xowl.store.rdf.*;

import java.util.Collection;
import java.util.Iterator;

/**
 * Represents a data store that is composed of a backend store for ground data and a volatile store for data coming from reasoning facilities
 *
 * @author Laurent Wouters
 */
class BaseReasonableStore implements BaseStore {
    /**
     * The store for the ground data
     */
    protected final BaseStore groundStore;
    /**
     * The store for the volatile data
     */
    protected final BaseStore volatileStore;
    /**
     * The aggregating dataset
     */
    protected final AggregateDataset aggregate;
    /**
     * The graph for inferences
     */
    protected final GraphNode graphInference;
    /**
     * The graph for meta data
     */
    protected final GraphNode graphMeta;

    /**
     * Initializes this store
     *
     * @param ground The store for the ground data
     */
    public BaseReasonableStore(BaseStore ground) {
        this.groundStore = ground;
        this.volatileStore = new InMemoryStore();
        this.aggregate = new AggregateDataset(groundStore, volatileStore);
        this.graphInference = volatileStore.getIRINode(IRIs.GRAPH_INFERENCE);
        this.graphMeta = volatileStore.getIRINode(IRIs.GRAPH_META);
    }

    @Override
    public boolean commit() {
        boolean r1 = groundStore.commit();
        boolean r2 = volatileStore.commit();
        return r1 && r2;
    }

    @Override
    public boolean rollback() {
        boolean r1 = groundStore.rollback();
        boolean r2 = volatileStore.rollback();
        return r1 && r2;
    }

    @Override
    public void close() throws Exception {
        // the volatile store is probably an in-memory store, closing it do nothing so this does not throw an exception
        volatileStore.close();
        groundStore.close();
    }

    @Override
    public void addListener(ChangeListener listener) {
        aggregate.addListener(listener);
    }

    @Override
    public void removeListener(ChangeListener listener) {
        aggregate.removeListener(listener);
    }

    @Override
    public long getMultiplicity(Quad quad) {
        return aggregate.getMultiplicity(quad);
    }

    @Override
    public long getMultiplicity(GraphNode graph, SubjectNode subject, Property property, Node object) {
        return aggregate.getMultiplicity(graph, subject, property, object);
    }

    @Override
    public Iterator<Quad> getAll() {
        return aggregate.getAll();
    }

    @Override
    public Iterator<Quad> getAll(GraphNode graph) {
        return aggregate.getAll(graph);
    }

    @Override
    public Iterator<Quad> getAll(SubjectNode subject, Property property, Node object) {
        return aggregate.getAll(subject, property, object);
    }

    @Override
    public Iterator<Quad> getAll(GraphNode graph, SubjectNode subject, Property property, Node object) {
        return aggregate.getAll(graph, subject, property, object);
    }

    @Override
    public Collection<GraphNode> getGraphs() {
        return aggregate.getGraphs();
    }

    @Override
    public long count() {
        return aggregate.count();
    }

    @Override
    public long count(GraphNode graph) {
        return aggregate.count(graph);
    }

    @Override
    public long count(SubjectNode subject, Property property, Node object) {
        return aggregate.count(subject, property, object);
    }

    @Override
    public long count(GraphNode graph, SubjectNode subject, Property property, Node object) {
        return aggregate.count(graph, subject, property, object);
    }

    @Override
    public void insert(Changeset changeset) throws UnsupportedNodeType {
        // TODO: implement this
    }

    @Override
    public void add(Quad quad) throws UnsupportedNodeType {
        if (RDFUtils.same(graphInference, quad.getGraph()) || RDFUtils.same(graphMeta, quad.getGraph()))
            volatileStore.add(quad);
        else
            groundStore.add(quad);
    }

    @Override
    public void add(GraphNode graph, SubjectNode subject, Property property, Node value) throws UnsupportedNodeType {
        if (RDFUtils.same(graphInference, graph) || RDFUtils.same(graphMeta, graph))
            volatileStore.add(graph, subject, property, value);
        else
            groundStore.add(graph, subject, property, value);
    }

    @Override
    public void remove(Quad quad) throws UnsupportedNodeType {
        if (RDFUtils.same(graphInference, quad.getGraph()) || RDFUtils.same(graphMeta, quad.getGraph()))
            volatileStore.remove(quad);
        else
            groundStore.remove(quad);
    }

    @Override
    public void remove(GraphNode graph, SubjectNode subject, Property property, Node value) throws UnsupportedNodeType {
        if (RDFUtils.same(graphInference, graph) || RDFUtils.same(graphMeta, graph))
            volatileStore.remove(graph, subject, property, value);
        else
            groundStore.remove(graph, subject, property, value);
    }

    @Override
    public void clear() {
        groundStore.clear();
        volatileStore.clear();
    }

    @Override
    public void clear(GraphNode graph) {
        if (RDFUtils.same(graphInference, graph) || RDFUtils.same(graphMeta, graph))
            volatileStore.clear(graph);
        else
            groundStore.clear(graph);
    }

    @Override
    public void copy(GraphNode origin, GraphNode target, boolean overwrite) {
        if (RDFUtils.same(graphInference, origin) || RDFUtils.same(graphMeta, origin) || RDFUtils.same(graphInference, target) || RDFUtils.same(graphMeta, target))
            throw new IllegalArgumentException("Invalid arguments, the origin and target graphs cannot refer to volatile graphs");
        groundStore.copy(origin, target, overwrite);
    }

    @Override
    public void move(GraphNode origin, GraphNode target) {
        if (RDFUtils.same(graphInference, origin) || RDFUtils.same(graphMeta, origin) || RDFUtils.same(graphInference, target) || RDFUtils.same(graphMeta, target))
            throw new IllegalArgumentException("Invalid arguments, the origin and target graphs cannot refer to volatile graphs");
        groundStore.move(origin, target);
    }

    @Override
    public IRINode getIRINode(GraphNode graph) {
        return groundStore.getIRINode(graph);
    }

    @Override
    public IRINode getIRINode(String iri) {
        return groundStore.getIRINode(iri);
    }

    @Override
    public IRINode getExistingIRINode(String iri) {
        IRINode result = groundStore.getExistingIRINode(iri);
        if (result != null)
            return result;
        return volatileStore.getExistingIRINode(iri);
    }

    @Override
    public BlankNode getBlankNode() {
        return groundStore.getBlankNode();
    }

    @Override
    public LiteralNode getLiteralNode(String lex, String datatype, String lang) {
        return groundStore.getLiteralNode(lex, datatype, lang);
    }

    @Override
    public AnonymousNode getAnonNode(AnonymousIndividual individual) {
        return groundStore.getAnonNode(individual);
    }
}
