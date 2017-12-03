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

import org.xowl.infra.lang.owl2.AnonymousIndividual;
import org.xowl.infra.store.IRIs;
import org.xowl.infra.store.RDFUtils;
import org.xowl.infra.store.execution.EvaluableExpression;
import org.xowl.infra.store.execution.ExecutionManager;
import org.xowl.infra.store.rdf.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Represents a quad store that is composed of:
 * - a backend store for ground quads and
 * - a volatile store for quads coming from reasoning facilities
 *
 * @author Laurent Wouters
 */
class QuadStoreReasonable implements QuadStore {
    /**
     * The store for the ground data
     */
    private final QuadStore groundStore;
    /**
     * The store for the volatile data
     */
    private final QuadStore volatileStore;
    /**
     * The aggregating dataset
     */
    private final DatasetAggregate aggregate;
    /**
     * The graph for inferences
     */
    private final GraphNode graphInference;
    /**
     * The graph for meta data
     */
    private final GraphNode graphMeta;

    /**
     * Initializes this store
     *
     * @param ground The store for the ground data
     */
    public QuadStoreReasonable(QuadStore ground) {
        this.groundStore = ground;
        this.volatileStore = new QuadStoreInMemory();
        this.aggregate = new DatasetAggregate(groundStore, volatileStore);
        this.graphInference = volatileStore.getIRINode(IRIs.GRAPH_INFERENCE);
        this.graphMeta = volatileStore.getIRINode(IRIs.GRAPH_META);
    }

    @Override
    public void setExecutionManager(ExecutionManager executionManager) {
        groundStore.setExecutionManager(executionManager);
        volatileStore.setExecutionManager(executionManager);
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
    public long getMultiplicity(Quad quad) throws UnsupportedNodeType {
        if (RDFUtils.same(graphInference, quad.getGraph()) || RDFUtils.same(graphMeta, quad.getGraph()))
            return volatileStore.getMultiplicity(quad);
        return groundStore.getMultiplicity(quad);
    }

    @Override
    public long getMultiplicity(GraphNode graph, SubjectNode subject, Property property, Node object) throws UnsupportedNodeType {
        if (RDFUtils.same(graphInference, graph) || RDFUtils.same(graphMeta, graph))
            return volatileStore.getMultiplicity(graph, subject, property, object);
        return groundStore.getMultiplicity(graph, subject, property, object);
    }

    @Override
    public Iterator<? extends Quad> getAll() {
        return aggregate.getAll();
    }

    @Override
    public Iterator<? extends Quad> getAll(GraphNode graph) throws UnsupportedNodeType {
        if (graph == null || graph.getNodeType() == Node.TYPE_VARIABLE)
            return getAll();
        if (RDFUtils.same(graphInference, graph) || RDFUtils.same(graphMeta, graph))
            return volatileStore.getAll(graph);
        return groundStore.getAll(graph);
    }

    @Override
    public Iterator<? extends Quad> getAll(SubjectNode subject, Property property, Node object) throws UnsupportedNodeType {
        return aggregate.getAll(subject, property, object);
    }

    @Override
    public Iterator<? extends Quad> getAll(GraphNode graph, SubjectNode subject, Property property, Node object) throws UnsupportedNodeType {
        if (graph == null || graph.getNodeType() == Node.TYPE_VARIABLE)
            return getAll(subject, property, object);
        if (RDFUtils.same(graphInference, graph) || RDFUtils.same(graphMeta, graph))
            return volatileStore.getAll(graph, subject, property, object);
        return groundStore.getAll(graph, subject, property, object);
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
    public long count(GraphNode graph) throws UnsupportedNodeType {
        if (graph == null || graph.getNodeType() == Node.TYPE_VARIABLE)
            return count();
        if (RDFUtils.same(graphInference, graph) || RDFUtils.same(graphMeta, graph))
            return volatileStore.count(graph);
        return groundStore.count(graph);
    }

    @Override
    public long count(SubjectNode subject, Property property, Node object) throws UnsupportedNodeType {
        return aggregate.count(subject, property, object);
    }

    @Override
    public long count(GraphNode graph, SubjectNode subject, Property property, Node object) throws UnsupportedNodeType {
        if (graph == null || graph.getNodeType() == Node.TYPE_VARIABLE)
            return count(subject, property, object);
        if (RDFUtils.same(graphInference, graph) || RDFUtils.same(graphMeta, graph))
            return volatileStore.count(graph, subject, property, object);
        return groundStore.count(graph, subject, property, object);
    }

    @Override
    public void insert(Changeset changeset) throws UnsupportedNodeType {
        // try to simplify this operation
        if (changeset.getRemoved().isEmpty() && changeset.getAdded().size() == 1) {
            add(changeset.getAdded().iterator().next());
            return;
        }
        if (changeset.getAdded().isEmpty() && changeset.getRemoved().size() == 1) {
            remove(changeset.getRemoved().iterator().next());
            return;
        }

        // nope, try to discriminate
        boolean[] addedIsVolatile = new boolean[changeset.getAdded().size()];
        int addedVolatile = 0;
        int addedGround = 0;
        int i = 0;
        for (Quad quad : changeset.getAdded()) {
            addedIsVolatile[i] = (RDFUtils.same(graphInference, quad.getGraph()) || RDFUtils.same(graphMeta, quad.getGraph()));
            if (addedIsVolatile[i])
                addedVolatile++;
            else
                addedGround++;
            i++;
        }

        boolean[] removedIsVolatile = new boolean[changeset.getRemoved().size()];
        int removedVolatile = 0;
        int removedGround = 0;
        i = 0;
        for (Quad quad : changeset.getRemoved()) {
            removedIsVolatile[i] = (RDFUtils.same(graphInference, quad.getGraph()) || RDFUtils.same(graphMeta, quad.getGraph()));
            if (removedIsVolatile[i])
                removedVolatile++;
            else
                removedGround++;
            i++;
        }

        if (addedGround == 0 && removedGround == 0) {
            // all volatile
            volatileStore.insert(changeset);
            return;
        }
        if (addedVolatile == 0 && removedVolatile == 0) {
            // all ground
            groundStore.insert(changeset);
            return;
        }

        // split into volatile and grounds
        List<Quad> addedForVolatile = new ArrayList<>(addedVolatile);
        List<Quad> addedForGround = new ArrayList<>(addedGround);
        List<Quad> removedForVolatile = new ArrayList<>(removedVolatile);
        List<Quad> removedForGround = new ArrayList<>(removedGround);
        i = 0;
        for (Quad quad : changeset.getAdded()) {
            (addedIsVolatile[i++] ? addedForVolatile : addedForGround).add(quad);
        }
        for (Quad quad : changeset.getRemoved()) {
            (removedIsVolatile[i++] ? removedForVolatile : removedForGround).add(quad);
        }

        // dispatch
        if (addedVolatile > 0) {
            if (removedVolatile > 0) {
                volatileStore.insert(Changeset.fromAddedRemoved(addedForVolatile, removedForVolatile));
            } else {
                volatileStore.insert(Changeset.fromAdded(addedForVolatile));
            }
        } else if (removedVolatile > 0) {
            volatileStore.insert(Changeset.fromRemoved(removedForVolatile));
        }

        if (addedGround > 0) {
            if (removedGround > 0) {
                groundStore.insert(Changeset.fromAddedRemoved(addedForGround, removedForGround));
            } else {
                groundStore.insert(Changeset.fromAdded(addedForGround));
            }
        } else if (removedGround > 0) {
            groundStore.insert(Changeset.fromRemoved(removedForGround));
        }
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
    public void clear(GraphNode graph) throws UnsupportedNodeType {
        if (RDFUtils.same(graphInference, graph) || RDFUtils.same(graphMeta, graph))
            volatileStore.clear(graph);
        else
            groundStore.clear(graph);
    }

    @Override
    public void copy(GraphNode origin, GraphNode target, boolean overwrite) throws UnsupportedNodeType {
        if (RDFUtils.same(graphInference, origin) || RDFUtils.same(graphMeta, origin) || RDFUtils.same(graphInference, target) || RDFUtils.same(graphMeta, target))
            throw new IllegalArgumentException("Invalid arguments, the origin and target graphs cannot refer to volatile graphs");
        groundStore.copy(origin, target, overwrite);
    }

    @Override
    public void move(GraphNode origin, GraphNode target) throws UnsupportedNodeType {
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

    @Override
    public DynamicNode getDynamicNode(EvaluableExpression evaluable) {
        return groundStore.getDynamicNode(evaluable);
    }
}
