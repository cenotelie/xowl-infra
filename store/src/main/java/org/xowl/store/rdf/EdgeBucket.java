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
 * Represents a bucket of edges starting from the same node
 *
 * @author Laurent Wouters
 */
public class EdgeBucket implements Iterable<Edge> {
    /**
     * Initial size of a bucket
     */
    private static final int INIT_SIZE = 8;
    /**
     * The identifier key for the serialization of this element
     */
    private static final String SERIALIZATION_NAME = "EdgeBucket";

    /**
     * The buffer of edges
     */
    private Edge[] edges;
    /**
     * The number of edges in this bucket
     */
    private int size;

    /**
     * Initializes this bucket
     */
    public EdgeBucket() {
        this.edges = new Edge[INIT_SIZE];
        this.size = 0;
    }

    /**
     * Initializes this edge from a dataset
     *
     * @param store The parent RDF store
     * @param data  The node of serialized data
     */
    public EdgeBucket(RDFStore store, org.xowl.utils.data.Node data) {
        this.size = data.getChildren().size();
        this.edges = new Edge[Math.max(INIT_SIZE, size)];
        for (int i = 0; i != size; i++) {
            edges[i] = new Edge(store, data.getChildren().get(i));
        }
    }

    /**
     * Adds the specified edge from this bucket
     *
     * @param graph    The containing graph
     * @param property The property on this edge
     * @param value    The target value
     * @return The operation result
     */
    public int add(GraphNode graph, Property property, Node value) {
        boolean hasEmpty = false;
        for (int i = 0; i != edges.length; i++) {
            hasEmpty = hasEmpty || (edges[i] == null);
            if (edges[i] != null && edges[i].getProperty() == property) {
                return edges[i].add(graph, value);
            }
        }
        if (!hasEmpty) {
            edges = Arrays.copyOf(edges, edges.length + INIT_SIZE);
            edges[size] = new Edge(graph, property, value);
            size++;
            return RDFStore.ADD_RESULT_NEW;
        }
        for (int i = 0; i != edges.length; i++) {
            if (edges[i] == null) {
                edges[i] = new Edge(graph, property, value);
                size++;
                return RDFStore.ADD_RESULT_NEW;
            }
        }
        // cannot happen
        return RDFStore.ADD_RESULT_UNKNOWN;
    }

    /**
     * Removes the specified edge from this bucket
     *
     * @param graph    The containing graph
     * @param property The property on this edge
     * @param value    The target value
     * @return The operation result
     */
    public int remove(GraphNode graph, Property property, Node value) {
        for (int i = 0; i != edges.length; i++) {
            if (edges[i] != null && edges[i].getProperty() == property) {
                int result = edges[i].remove(graph, value);
                if (result == RDFStore.REMOVE_RESULT_EMPTIED) {
                    edges[i] = null;
                    size--;
                    return (size == 0) ? RDFStore.REMOVE_RESULT_EMPTIED : RDFStore.REMOVE_RESULT_REMOVED;
                }
                return result;
            }
        }
        return RDFStore.REMOVE_RESULT_NOT_FOUND;
    }

    /**
     * Removes all the matching edges from this bucket(or decrement their counter)
     *
     * @param graph    The containing graph to match, or null
     * @param property The property on this edge to match, or null
     * @param value    The target value to match, or null
     * @param buffer   The buffer for the removed quads
     * @return The operation result
     */
    public int removeAll(GraphNode graph, Property property, Node value, List<Quad> buffer) {
        for (int i = 0; i != edges.length; i++) {
            if (edges[i] != null && (property == null || edges[i].getProperty() == property)) {
                int originalSize = buffer.size();
                int result = edges[i].removeAll(graph, value, buffer);
                for (int j = originalSize; j != buffer.size(); j++)
                    buffer.get(j).setProperty(edges[i].getProperty());
                if (result == RDFStore.REMOVE_RESULT_EMPTIED) {
                    edges[i] = null;
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
        for (int i = 0; i != edges.length; i++) {
            if (edges[i] != null) {
                int originalSize = buffer.size();
                edges[i].clear(buffer);
                for (int j = originalSize; j != buffer.size(); j++)
                    buffer.get(j).setProperty(edges[i].getProperty());
            }
        }
    }

    @Override
    public Iterator<Edge> iterator() {
        return new SparseIterator<>(edges);
    }

    /**
     * Gets all the quads with the specified data
     *
     * @param graph    The filtering graph
     * @param property The filtering property
     * @param value    The filtering object value
     * @return An iterator over the quads
     */
    public Iterator<Quad> getAll(final GraphNode graph, final Property property, final Node value) {
        if (property == null || property.getNodeType() == VariableNode.TYPE) {
            return new AdaptingIterator<>(new CombiningIterator<>(new IndexIterator<>(edges), new Adapter<Iterator<Quad>>() {
                @Override
                public <X> Iterator<Quad> adapt(X element) {
                    Integer index = (Integer) element;
                    return edges[index].getAll(graph, value);
                }
            }), new Adapter<Quad>() {
                @Override
                public <X> Quad adapt(X element) {
                    Couple<Integer, Quad> result = (Couple<Integer, Quad>) element;
                    result.y.setProperty(edges[result.x].getProperty());
                    return result.y;
                }
            });
        }

        for (int i = 0; i != edges.length; i++) {
            if (edges[i] != null && edges[i].getProperty() == property) {
                return new AdaptingIterator<>(edges[i].getAll(graph, value), new Adapter<Quad>() {
                    @Override
                    public <X> Quad adapt(X element) {
                        Quad result = (Quad) element;
                        result.setProperty(property);
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
     * @param graph    The filtering graph
     * @param property The filtering property
     * @param value    The filtering object value
     * @return The number of different quads
     */
    public int count(GraphNode graph, Property property, Node value) {
        if (property == null || property.getNodeType() == VariableNode.TYPE) {
            int count = 0;
            for (int i = 0; i != edges.length; i++)
                if (edges[i] != null)
                    count += edges[i].count(graph, value);
            return count;
        }
        for (int i = 0; i != edges.length; i++)
            if (edges[i] != null && edges[i].getProperty() == property)
                return edges[i].count(graph, value);
        return 0;
    }

    /**
     * Serializes this edge bucket
     *
     * @param dataset The dataset to serialize to
     * @return The serialized data
     */
    public org.xowl.utils.data.Node serialize(Dataset dataset) {
        org.xowl.utils.data.Node result = new org.xowl.utils.data.Node(dataset, SERIALIZATION_NAME);
        for (int i = 0; i != edges.length; i++) {
            if (edges[i] == null)
                continue;
            result.getChildren().add(edges[i].serialize(dataset));
        }
        return result;
    }
}
