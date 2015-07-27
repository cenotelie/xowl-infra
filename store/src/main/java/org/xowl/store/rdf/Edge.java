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
package org.xowl.store.rdf;

import org.xowl.utils.collections.*;
import org.xowl.utils.data.Dataset;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Represents an edge in a RDF graph
 *
 * @author Laurent Wouters
 */
class Edge implements Iterable<EdgeTarget> {
    /**
     * The initial size of the buffer of the targets
     */
    private static final int INIT_BUFFER_SIZE = 10;
    /**
     * The identifier key for the serialization of this element
     */
    private static final String SERIALIZATION_NAME = "Edge";
    /**
     * The identifier key for the serialization of a property element
     */
    private static final String SERIALIZATION_PROPERTY = "Property";
    /**
     * The identifier key for the serialization of the targets
     */
    private static final String SERIALIZATION_TARGETS = "Targets";

    /**
     * The label on this edge
     */
    private final Property property;
    /**
     * The target for this edges
     */
    private EdgeTarget[] targets;
    /**
     * The number of targets
     */
    private int size;

    /**
     * Initializes this edge
     *
     * @param graph    The graph containing the quad
     * @param property The property on this edge
     * @param object   The first object node for this edge
     */
    public Edge(GraphNode graph, Property property, Node object) {
        this.property = property;
        this.targets = new EdgeTarget[INIT_BUFFER_SIZE];
        this.targets[0] = new EdgeTarget(graph, object);
        this.size = 1;
    }

    /**
     * Initializes this edge from a dataset
     *
     * @param store The parent RDF store
     * @param data  The node of serialized data
     */
    public Edge(RDFStore store, org.xowl.utils.data.Node data) {
        this.property = (Property) store.getNodeFor(data.child(SERIALIZATION_PROPERTY).getChildren().get(0));
        org.xowl.utils.data.Node nodeTargets = data.child(SERIALIZATION_TARGETS);
        this.size = nodeTargets.getChildren().size();
        this.targets = new EdgeTarget[Math.max(INIT_BUFFER_SIZE, size)];
        for (int i = 0; i != size; i++) {
            targets[i] = new EdgeTarget(store, nodeTargets.getChildren().get(i));
        }
    }

    /**
     * Gets the property on this edge
     *
     * @return The property on this edge
     */
    public Property getProperty() {
        return property;
    }

    /**
     * Adds the specified edge (or increment the counter)
     *
     * @param graph The graph containing the quad
     * @param value The edge's target node
     * @return The operation result
     */
    public int add(GraphNode graph, Node value) {
        boolean hasEmpty = false;
        for (int i = 0; i != targets.length; i++) {
            hasEmpty = hasEmpty || (targets[i] == null);
            if (targets[i] != null && targets[i].getTarget() == value) {
                return targets[i].add(graph);
            }
        }
        if (!hasEmpty) {
            targets = Arrays.copyOf(targets, targets.length + INIT_BUFFER_SIZE);
            targets[size] = new EdgeTarget(graph, value);
            size++;
            return RDFStore.ADD_RESULT_NEW;
        }
        for (int i = 0; i != targets.length; i++) {
            if (targets[i] == null) {
                targets[i] = new EdgeTarget(graph, value);
                size++;
                return RDFStore.ADD_RESULT_NEW;
            }
        }
        // cannot happen
        return RDFStore.ADD_RESULT_UNKNOWN;
    }

    /**
     * Removes the specified edge (or decrement the counter)
     *
     * @param graph The graph containing the quad
     * @param value The edge's target node
     * @return The operation result
     */
    public int remove(GraphNode graph, Node value) {
        for (int i = 0; i != targets.length; i++) {
            if (targets[i] != null && targets[i].getTarget() == value) {
                int result = targets[i].remove(graph);
                if (result == RDFStore.REMOVE_RESULT_EMPTIED) {
                    targets[i] = null;
                    size--;
                    return (size == 0) ? RDFStore.REMOVE_RESULT_EMPTIED : RDFStore.REMOVE_RESULT_REMOVED;
                }
                return result;
            }
        }
        return RDFStore.REMOVE_RESULT_NOT_FOUND;
    }

    /**
     * Removes all the matching edges (or decrement their counter)
     *
     * @param graph  The graph to match, or null
     * @param value  The edge's target node to match, or null
     * @param buffer The buffer for the removed quads
     * @return The operation result
     */
    public int removeAll(GraphNode graph, Node value, List<Quad> buffer) {
        for (int i = 0; i != targets.length; i++) {
            if (targets[i] != null && (value == null || targets[i].getTarget() == value)) {
                int originalSize = buffer.size();
                int result = targets[i].removeAll(graph, buffer);
                for (int j = originalSize; j != buffer.size(); j++)
                    buffer.get(j).setObject(targets[i].getTarget());
                if (result == RDFStore.REMOVE_RESULT_EMPTIED) {
                    targets[i] = null;
                    size--;
                }
            }
        }
        return (size == 0) ? RDFStore.REMOVE_RESULT_EMPTIED : RDFStore.REMOVE_RESULT_REMOVED;
    }

    /**
     * Clears this object
     *
     * @param buffer The buffer for the removed quads
     */
    public void clear(List<Quad> buffer) {
        for (int i = 0; i != targets.length; i++) {
            if (targets[i] != null) {
                int originalSize = buffer.size();
                targets[i].clear(buffer);
                for (int j = originalSize; j != buffer.size(); j++)
                    buffer.get(j).setObject(targets[i].getTarget());
            }
        }
    }

    @Override
    public Iterator<EdgeTarget> iterator() {
        return new SparseIterator<>(targets);
    }

    /**
     * Gets all the quads with the specified data
     *
     * @param graph The filtering graph
     * @param value The filtering object value
     * @return An iterator over the quads
     */
    public Iterator<Quad> getAll(final GraphNode graph, final Node value) {
        if (value == null || value.getNodeType() == VariableNode.TYPE) {
            return new AdaptingIterator<>(new CombiningIterator<>(new IndexIterator<>(targets), new Adapter<Iterator<Quad>>() {
                @Override
                public <X> Iterator<Quad> adapt(X element) {
                    Integer index = (Integer) element;
                    return targets[index].getAll(graph);
                }
            }), new Adapter<Quad>() {
                @Override
                public <X> Quad adapt(X element) {
                    Couple<Integer, Quad> result = (Couple<Integer, Quad>) element;
                    result.y.setObject(targets[result.x].getTarget());
                    return result.y;
                }
            });
        }

        for (int i = 0; i != targets.length; i++) {
            if (targets[i] != null && targets[i].getTarget() == value) {
                return new AdaptingIterator<>(targets[i].getAll(graph), new Adapter<Quad>() {
                    @Override
                    public <X> Quad adapt(X element) {
                        Quad result = (Quad) element;
                        result.setObject(value);
                        return result;
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
     * @param value The filtering object value
     * @return The number of different quads
     */
    public int count(GraphNode graph, Node value) {
        if (value == null || value.getNodeType() == VariableNode.TYPE) {
            int count = 0;
            for (int i = 0; i != targets.length; i++)
                if (targets[i] != null)
                    count += targets[i].count(graph);
            return count;
        }
        for (int i = 0; i != targets.length; i++)
            if (targets[i] != null && targets[i].getTarget() == value)
                return targets[i].count(graph);
        return 0;
    }

    /**
     * Serializes this edge
     *
     * @param dataset The dataset to serialize to
     * @return The serialized data
     */
    public org.xowl.utils.data.Node serialize(Dataset dataset) {
        org.xowl.utils.data.Node result = new org.xowl.utils.data.Node(dataset, SERIALIZATION_NAME);
        org.xowl.utils.data.Node nodeProperty = new org.xowl.utils.data.Node(dataset, SERIALIZATION_PROPERTY);
        result.getChildren().add(nodeProperty);
        nodeProperty.getChildren().add(property.serialize(dataset));
        org.xowl.utils.data.Node nodeTargets = new org.xowl.utils.data.Node(dataset, SERIALIZATION_TARGETS);
        result.getChildren().add(nodeTargets);
        for (int i = 0; i != targets.length; i++) {
            if (targets[i] == null)
                continue;
            nodeTargets.getChildren().add(targets[i].serialize(dataset));
        }
        return result;
    }
}