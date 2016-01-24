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

package org.xowl.infra.store.storage;

import org.xowl.infra.lang.owl2.AnonymousIndividual;
import org.xowl.infra.store.IRIs;
import org.xowl.infra.store.RDFUtils;
import org.xowl.infra.store.owl.AnonymousNode;
import org.xowl.infra.store.rdf.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Represents a data store that is composed of a backend store for ground data and a volatile store for data coming from reasoning facilities
 *
 * @author Laurent Wouters
 */
class BaseReasonableStore extends BaseStore {
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
        if (RDFUtils.same(graphInference, quad.getGraph()) || RDFUtils.same(graphMeta, quad.getGraph()))
            return volatileStore.getMultiplicity(quad);
        return groundStore.getMultiplicity(quad);
    }

    @Override
    public long getMultiplicity(GraphNode graph, SubjectNode subject, Property property, Node object) {
        if (RDFUtils.same(graphInference, graph) || RDFUtils.same(graphMeta, graph))
            return volatileStore.getMultiplicity(graph, subject, property, object);
        return groundStore.getMultiplicity(graph, subject, property, object);
    }

    @Override
    public Iterator<Quad> getAll() {
        return aggregate.getAll();
    }

    @Override
    public Iterator<Quad> getAll(GraphNode graph) {
        if (graph == null)
            return getAll();
        if (RDFUtils.same(graphInference, graph) || RDFUtils.same(graphMeta, graph))
            return volatileStore.getAll(graph);
        return groundStore.getAll(graph);
    }

    @Override
    public Iterator<Quad> getAll(SubjectNode subject, Property property, Node object) {
        return aggregate.getAll(subject, property, object);
    }

    @Override
    public Iterator<Quad> getAll(GraphNode graph, SubjectNode subject, Property property, Node object) {
        if (graph == null)
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
    public long count(GraphNode graph) {
        if (graph == null)
            return count();
        if (RDFUtils.same(graphInference, graph) || RDFUtils.same(graphMeta, graph))
            return volatileStore.count(graph);
        return groundStore.count(graph);
    }

    @Override
    public long count(SubjectNode subject, Property property, Node object) {
        return aggregate.count(subject, property, object);
    }

    @Override
    public long count(GraphNode graph, SubjectNode subject, Property property, Node object) {
        if (graph == null)
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
        boolean addedAllVolatile = true;
        boolean addedAllGround = true;
        int i = 0;
        for (Quad quad : changeset.getAdded()) {
            addedIsVolatile[i] = (RDFUtils.same(graphInference, quad.getGraph()) || RDFUtils.same(graphMeta, quad.getGraph()));
            addedAllVolatile &= addedIsVolatile[i];
            addedAllGround &= !addedIsVolatile[i];
            i++;
        }

        boolean[] removedIsVolatile = new boolean[changeset.getRemoved().size()];
        boolean removedAllVolatile = true;
        boolean removedAllGround = true;
        i = 0;
        for (Quad quad : changeset.getRemoved()) {
            removedIsVolatile[i] = (RDFUtils.same(graphInference, quad.getGraph()) || RDFUtils.same(graphMeta, quad.getGraph()));
            removedAllVolatile &= removedIsVolatile[i];
            removedAllGround &= !removedIsVolatile[i];
            i++;
        }

        if (addedAllVolatile && removedAllVolatile) {
            volatileStore.insert(changeset);
            return;
        }
        if (addedAllGround && removedAllGround) {
            groundStore.insert(changeset);
            return;
        }

        boolean addedIsHandled = true;
        if (addedIsVolatile.length > 0) {
            if (addedAllVolatile) {
                volatileStore.insert(Changeset.fromAdded(changeset.getAdded()));
            } else if (addedAllGround) {
                groundStore.insert(Changeset.fromAdded(changeset.getAdded()));
            } else {
                addedIsHandled = false;
            }
        }

        boolean removedIsHandled = true;
        if (removedIsVolatile.length > 0) {
            if (removedAllVolatile) {
                volatileStore.insert(Changeset.fromRemoved(changeset.getRemoved()));
            } else if (removedAllGround) {
                groundStore.insert(Changeset.fromRemoved(changeset.getRemoved()));
            } else {
                removedIsHandled = false;
            }
        }

        if (addedIsHandled && removedIsHandled)
            return;

        List<Quad> addedForVolatile = addedIsVolatile.length > 0 ? new ArrayList<Quad>(addedIsVolatile.length) : null;
        List<Quad> addedForGround = addedIsVolatile.length > 0 ? new ArrayList<Quad>(addedIsVolatile.length) : null;
        List<Quad> removedForVolatile = removedIsVolatile.length > 0 ? new ArrayList<Quad>(removedIsVolatile.length) : null;
        List<Quad> removedForGround = removedIsVolatile.length > 0 ? new ArrayList<Quad>(removedIsVolatile.length) : null;
        i = 0;
        for (Quad quad : changeset.getAdded()) {
            (addedIsVolatile[i++] ? addedForVolatile : addedForGround).add(quad);
        }
        for (Quad quad : changeset.getRemoved()) {
            (removedIsVolatile[i++] ? removedForVolatile : removedForGround).add(quad);
        }

        if (addedForVolatile != null) {
            if (removedForVolatile != null) {
                volatileStore.insert(Changeset.fromAddedRemoved(addedForVolatile, removedForVolatile));
            } else {
                volatileStore.insert(Changeset.fromRemoved(addedForVolatile));
            }
        } else if (removedForVolatile != null) {
            volatileStore.insert(Changeset.fromRemoved(removedForVolatile));
        }

        if (addedForGround != null) {
            if (removedForGround != null) {
                groundStore.insert(Changeset.fromAddedRemoved(addedForGround, removedForGround));
            } else {
                groundStore.insert(Changeset.fromRemoved(addedForGround));
            }
        } else if (removedForGround != null) {
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
