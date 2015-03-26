/**********************************************************************
 * Copyright (c) 2014 Laurent Wouters
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
 **********************************************************************/
package org.xowl.store.rdf;

import org.xowl.utils.collections.Adapter;
import org.xowl.utils.collections.AdaptingIterator;
import org.xowl.utils.collections.SingleIterator;
import org.xowl.utils.collections.SparseIterator;
import org.xowl.utils.data.Attribute;
import org.xowl.utils.data.Dataset;

import java.util.Arrays;
import java.util.Iterator;

/**
 * Represents a collection of targets for edges
 *
 * @author Laurent Wouters
 */
class EdgeTarget implements Iterable<GraphNode> {
    /**
     * The initial size of the buffer of the multiplicities
     */
    private static final int INIT_BUFFER_SIZE = 3;
    /**
     * The identifier key for the serialization of this element
     */
    private static final String SERIALIZATION_NAME = "EdgeTarget";
    /**
     * The identifier key for the serialization of a target element
     */
    private static final String SERIALIZATION_TARGET = "Target";
    /**
     * The identifier key for the serialization of the graphs
     */
    private static final String SERIALIZATION_GRAPHS = "Graphs";
    /**
     * The identifier key for the serialization of a graph element
     */
    private static final String SERIALIZATION_GRAPH = "Graph";
    /**
     * The identifier key for the serialization of the multiplicity attribute
     */
    private static final String SERIALIZATION_MULTIPLICITY = "multiplicity";

    /**
     * The represented target node
     */
    private final Node target;
    /**
     * The containing graphs
     */
    private GraphNode[] graphs;
    /**
     * The multiplicity counters for the graphs
     */
    private int[] multiplicities;
    /**
     * The number of graphs
     */
    private int size;

    /**
     * Initializes this target
     *
     * @param graph  The first containing graph
     * @param target The represented target
     */
    public EdgeTarget(GraphNode graph, Node target) {
        this.target = target;
        this.graphs = new GraphNode[INIT_BUFFER_SIZE];
        this.multiplicities = new int[INIT_BUFFER_SIZE];
        this.graphs[0] = graph;
        this.multiplicities[0] = 1;
        this.size = 1;
    }

    /**
     * Initializes this target from a dataset
     *
     * @param store The parent RDF store
     * @param data  The node of serialized data
     */
    public EdgeTarget(RDFStore store, org.xowl.utils.data.Node data) {
        this.target = store.getNodeFor(data.child(SERIALIZATION_TARGET).getChildren().get(0));
        org.xowl.utils.data.Node nodeGraphs = data.child(SERIALIZATION_GRAPHS);
        this.size = nodeGraphs.getChildren().size();
        this.graphs = new GraphNode[Math.max(INIT_BUFFER_SIZE, size)];
        this.multiplicities = new int[Math.max(INIT_BUFFER_SIZE, size)];
        for (int i = 0; i != size; i++) {
            org.xowl.utils.data.Node nodeGraph = nodeGraphs.getChildren().get(0);
            graphs[i] = (GraphNode) store.getNodeFor(nodeGraph.getChildren().get(0));
            multiplicities[i] = (int) nodeGraph.attribute(SERIALIZATION_MULTIPLICITY).getValue();
        }
    }


    /**
     * Gets the represented target node
     *
     * @return The represented target node
     */
    public Node getTarget() {
        return target;
    }

    /**
     * Adds the specified graph (or increment the counter)
     *
     * @param graph A graph
     * @return The operation result
     */
    public int add(GraphNode graph) {
        boolean hasEmpty = false;
        for (int i = 0; i != graphs.length; i++) {
            hasEmpty = hasEmpty || (graphs[i] == null);
            if (graphs[i] == graph) {
                multiplicities[i]++;
                return RDFStore.ADD_RESULT_INCREMENT;
            }
        }
        if (!hasEmpty) {
            graphs = Arrays.copyOf(graphs, graphs.length + INIT_BUFFER_SIZE);
            multiplicities = Arrays.copyOf(multiplicities, multiplicities.length + INIT_BUFFER_SIZE);
            graphs[size] = graph;
            multiplicities[size] = 1;
            size++;
            return RDFStore.ADD_RESULT_NEW;
        }
        for (int i = 0; i != graphs.length; i++) {
            if (graphs[i] == null) {
                graphs[i] = graph;
                multiplicities[i] = 1;
                size++;
                return RDFStore.ADD_RESULT_NEW;
            }
        }
        // cannot happen
        return RDFStore.ADD_RESULT_UNKNOWN;
    }

    /**
     * Removes the specified graph (or decrement the counter)
     *
     * @param graph A graph
     * @return The operation result
     */
    public int remove(GraphNode graph) {
        for (int i = 0; i != graphs.length; i++) {
            if (graphs[i] == graph) {
                multiplicities[i]--;
                if (multiplicities[i] == 0) {
                    graphs[i] = null;
                    size--;
                    return (size == 0) ? RDFStore.REMOVE_RESULT_EMPTIED : RDFStore.REMOVE_RESULT_REMOVED;
                }
                return RDFStore.REMOVE_RESULT_DECREMENT;
            }
        }
        return RDFStore.REMOVE_RESULT_NOT_FOUND;
    }

    @Override
    public Iterator<GraphNode> iterator() {
        return new SparseIterator<>(graphs);
    }

    /**
     * Gets all the quads with the specified data
     *
     * @param graph The filtering graph
     * @return An iterator over the quads
     */
    public Iterator<Quad> getAll(GraphNode graph) {
        if (graph == null || graph.getNodeType() == VariableNode.TYPE) {
            return new AdaptingIterator<>(iterator(), new Adapter<Quad>() {
                @Override
                public <X> Quad adapt(X element) {
                    return new Quad((GraphNode) element, null, null, null);
                }
            });
        }

        for (int i = 0; i != graphs.length; i++) {
            if (graphs[i] != null && graphs[i].equals(graph)) {
                return new AdaptingIterator<>(new SingleIterator<>(graph), new Adapter<Quad>() {
                    @Override
                    public <X> Quad adapt(X element) {
                        return new Quad((GraphNode) element, null, null, null);
                    }
                });
            }
        }

        return new SingleIterator<>(null);
    }

    /**
     * Returns the number of different quads with the specified data
     *
     * @param graph The filtering graph
     * @return The number of different quads
     */
    public int count(GraphNode graph) {
        if (graph == null || graph.getNodeType() == VariableNode.TYPE)
            return size;
        for (int i = 0; i != graphs.length; i++)
            if (graphs[i] == graph)
                return 1;
        return 0;
    }

    /**
     * Serializes this target
     *
     * @param dataset The dataset to serialize to
     * @return The serialized data
     */
    public org.xowl.utils.data.Node serialize(Dataset dataset) {
        org.xowl.utils.data.Node result = new org.xowl.utils.data.Node(dataset, SERIALIZATION_NAME);
        org.xowl.utils.data.Node nodeTarget = new org.xowl.utils.data.Node(dataset, SERIALIZATION_TARGET);
        result.getChildren().add(nodeTarget);
        nodeTarget.getChildren().add(target.serialize(dataset));
        org.xowl.utils.data.Node nodeGraphs = new org.xowl.utils.data.Node(dataset, SERIALIZATION_GRAPHS);
        result.getChildren().add(nodeGraphs);
        for (int i = 0; i != graphs.length; i++) {
            if (graphs[i] == null)
                continue;
            org.xowl.utils.data.Node nodeGraph = new org.xowl.utils.data.Node(dataset, SERIALIZATION_GRAPH);
            Attribute attributeType = new Attribute(dataset, SERIALIZATION_MULTIPLICITY);
            attributeType.setValue(multiplicities[i]);
            nodeGraph.getAttributes().add(attributeType);
            nodeGraph.getChildren().add(graphs[i].serialize(dataset));
            nodeGraphs.getChildren().add(nodeGraph);
        }
        return result;
    }
}