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

package org.xowl.infra.store.storage.cache;

import org.xowl.infra.store.RDFUtils;
import org.xowl.infra.store.rdf.GraphNode;
import org.xowl.infra.store.rdf.Node;
import org.xowl.infra.store.rdf.Property;
import org.xowl.infra.store.storage.impl.DatasetImpl;
import org.xowl.infra.store.storage.impl.MQuad;
import org.xowl.infra.utils.collections.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Represents a bucket of edges starting from the same node
 *
 * @author Laurent Wouters
 */
class EdgeBucket implements Iterable<Edge> {
    /**
     * Initial size of a bucket
     */
    private static final int INIT_SIZE = 8;

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
     * Gets the number of graphs
     *
     * @return The number of graphs
     */
    public int getSize() {
        return size;
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
            if (edges[i] != null && RDFUtils.same(edges[i].getProperty(), property)) {
                return edges[i].add(graph, value);
            }
        }
        if (!hasEmpty) {
            edges = Arrays.copyOf(edges, edges.length + INIT_SIZE);
            edges[size] = new Edge(graph, property, value);
            size++;
            return DatasetImpl.ADD_RESULT_NEW;
        }
        for (int i = 0; i != edges.length; i++) {
            if (edges[i] == null) {
                edges[i] = new Edge(graph, property, value);
                size++;
                return DatasetImpl.ADD_RESULT_NEW;
            }
        }
        // cannot happen
        return DatasetImpl.ADD_RESULT_UNKNOWN;
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
            if (edges[i] != null && RDFUtils.same(edges[i].getProperty(), property)) {
                int result = edges[i].remove(graph, value);
                if (result == DatasetImpl.REMOVE_RESULT_EMPTIED) {
                    edges[i] = null;
                    size--;
                    return (size == 0) ? DatasetImpl.REMOVE_RESULT_EMPTIED : DatasetImpl.REMOVE_RESULT_REMOVED;
                }
                return result;
            }
        }
        return DatasetImpl.REMOVE_RESULT_NOT_FOUND;
    }

    /**
     * Removes all the matching edges from this bucket(or decrement their counter)
     *
     * @param graph             The containing graph to match, or null
     * @param property          The property on this edge to match, or null
     * @param value             The target value to match, or null
     * @param bufferDecremented The buffer for the decremented quads
     * @param bufferRemoved     The buffer for the removed quads
     * @return The operation result
     */
    public int removeAll(GraphNode graph, Property property, Node value, List<MQuad> bufferDecremented, List<MQuad> bufferRemoved) {
        for (int i = 0; i != edges.length; i++) {
            if (edges[i] != null && (property == null || RDFUtils.same(edges[i].getProperty(), property))) {
                int originalSizeDec = bufferDecremented.size();
                int originalSizeRem = bufferRemoved.size();
                int result = edges[i].removeAll(graph, value, bufferDecremented, bufferRemoved);
                for (int j = originalSizeDec; j != bufferDecremented.size(); j++)
                    bufferDecremented.get(j).setProperty(edges[i].getProperty());
                for (int j = originalSizeRem; j != bufferRemoved.size(); j++)
                    bufferRemoved.get(j).setProperty(edges[i].getProperty());
                if (result == DatasetImpl.REMOVE_RESULT_EMPTIED) {
                    edges[i] = null;
                    size--;
                }
            }
        }
        return (size == 0) ? DatasetImpl.REMOVE_RESULT_EMPTIED : DatasetImpl.REMOVE_RESULT_REMOVED;
    }

    /**
     * Clears this object
     *
     * @param buffer The buffer for the removed quads
     */
    public void clear(List<MQuad> buffer) {
        for (int i = 0; i != edges.length; i++) {
            if (edges[i] != null) {
                int originalSize = buffer.size();
                edges[i].clear(buffer);
                for (int j = originalSize; j != buffer.size(); j++)
                    buffer.get(j).setProperty(edges[i].getProperty());
            }
        }
    }

    /**
     * Clears this object for the specified graph
     *
     * @param graph  The graph to clear
     * @param buffer The buffer for the removed quads
     * @return true if the object is now empty
     */
    public boolean clear(GraphNode graph, List<MQuad> buffer) {
        for (int i = 0; i != edges.length; i++) {
            if (edges[i] != null) {
                int originalSize = buffer.size();
                boolean empty = edges[i].clear(graph, buffer);
                for (int j = originalSize; j != buffer.size(); j++)
                    buffer.get(j).setProperty(edges[i].getProperty());
                if (empty) {
                    edges[i] = null;
                    size--;
                }
            }
        }
        return (size == 0);
    }

    /**
     * Copies all the quads with the specified origin graph, to quads with the target graph.
     * Pre-existing quads from the target graph that do not correspond to an equivalent in the origin graph are removed if the overwrite flag is used.
     * If a target quad already exists, its multiplicity is incremented, otherwise it is created.
     * The quad in the origin graph is not affected.
     *
     * @param origin    The origin graph
     * @param target    The target graph
     * @param bufferOld The buffer of the removed quads
     * @param bufferNew The buffer of the new quads
     * @param overwrite Whether to overwrite quads from the target graph
     * @return true if the object is now empty
     */
    public boolean copy(GraphNode origin, GraphNode target, List<MQuad> bufferOld, List<MQuad> bufferNew, boolean overwrite) {
        for (int i = 0; i != edges.length; i++) {
            if (edges[i] != null) {
                int originalSizeOld = bufferOld.size();
                int originalSizeNew = bufferNew.size();
                boolean empty = edges[i].copy(origin, target, bufferOld, bufferNew, overwrite);
                for (int j = originalSizeOld; j != bufferOld.size(); j++)
                    bufferOld.get(j).setProperty(edges[i].getProperty());
                for (int j = originalSizeNew; j != bufferNew.size(); j++)
                    bufferNew.get(j).setProperty(edges[i].getProperty());
                if (empty) {
                    edges[i] = null;
                    size--;
                }
            }
        }
        return (size == 0);
    }

    /**
     * Moves all the quads with the specified origin graph, to quads with the target graph.
     * Pre-existing quads from the target graph that do not correspond to an equivalent in the origin graph are removed.
     * If a target quad already exists, its multiplicity is incremented, otherwise it is created.
     * The quad in the origin graph is always removed.
     *
     * @param origin    The origin graph
     * @param target    The target graph
     * @param bufferOld The buffer of the removed quads
     * @param bufferNew The buffer of the new quads
     * @return true if the object is now empty
     */
    public boolean move(GraphNode origin, GraphNode target, List<MQuad> bufferOld, List<MQuad> bufferNew) {
        for (int i = 0; i != edges.length; i++) {
            if (edges[i] != null) {
                int originalSizeOld = bufferOld.size();
                int originalSizeNew = bufferNew.size();
                boolean empty = edges[i].move(origin, target, bufferOld, bufferNew);
                for (int j = originalSizeOld; j != bufferOld.size(); j++)
                    bufferOld.get(j).setProperty(edges[i].getProperty());
                for (int j = originalSizeNew; j != bufferNew.size(); j++)
                    bufferNew.get(j).setProperty(edges[i].getProperty());
                if (empty) {
                    edges[i] = null;
                    size--;
                }
            }
        }
        return (size == 0);
    }

    @Override
    public Iterator<Edge> iterator() {
        return new SparseIterator<>(edges);
    }

    /**
     * Gets the multiplicity for the quad
     *
     * @param graph    The graph
     * @param property The property on this edge
     * @param value    The edge's target node
     * @return The multiplicity
     */
    public long getMultiplicity(GraphNode graph, Property property, Node value) {
        for (int i = 0; i != edges.length; i++) {
            if (edges[i] != null && RDFUtils.same(edges[i].getProperty(), property)) {
                return edges[i].getMultiplicity(graph, value);
            }
        }
        return 0;
    }

    /**
     * Gets all the quads with the specified data
     *
     * @param graph    The filtering graph
     * @param property The filtering property
     * @param value    The filtering object value
     * @return An iterator over the quads
     */
    public Iterator<MQuad> getAll(final GraphNode graph, final Property property, final Node value) {
        if (property == null || property.getNodeType() == Node.TYPE_VARIABLE) {
            return new AdaptingIterator<>(new CombiningIterator<Integer, MQuad>(new IndexIterator<>(edges), new Adapter<Iterator<MQuad>>() {
                @Override
                public <X> Iterator<MQuad> adapt(X element) {
                    Integer index = (Integer) element;
                    return edges[index].getAll(graph, value);
                }
            }) {
                @Override
                public void remove() {
                    lastRightIterator.remove();
                    int index = current.x;
                    if (edges[index].getSize() == 0) {
                        edges[index] = null;
                        size--;
                    }
                }
            }, new Adapter<MQuad>() {
                @Override
                public <X> MQuad adapt(X element) {
                    Couple<Integer, MQuad> result = (Couple<Integer, MQuad>) element;
                    result.y.setProperty(edges[result.x].getProperty());
                    return result.y;
                }
            });
        }

        for (int i = 0; i != edges.length; i++) {
            if (edges[i] != null && RDFUtils.same(edges[i].getProperty(), property)) {
                final int index = i;
                return new AdaptingIterator<MQuad, MQuad>(edges[i].getAll(graph, value), new Adapter<MQuad>() {
                    @Override
                    public <X> MQuad adapt(X element) {
                        MQuad result = (MQuad) element;
                        result.setProperty(property);
                        return result;
                    }
                }) {
                    @Override
                    public void remove() {
                        content.remove();
                        if (edges[index].getSize() == 0) {
                            edges[index] = null;
                            size--;
                        }
                    }
                };
            }
        }

        return new SingleIterator<>(null);
    }

    /**
     * Finds all the graphs in this object
     *
     * @param buffer The buffer to store the result
     */
    public void getGraphs(Collection<GraphNode> buffer) {
        for (int i = 0; i != edges.length; i++) {
            if (edges[i] != null)
                edges[i].getGraphs(buffer);
        }
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
        if (property == null || property.getNodeType() == Node.TYPE_VARIABLE) {
            int count = 0;
            for (int i = 0; i != edges.length; i++)
                if (edges[i] != null)
                    count += edges[i].count(graph, value);
            return count;
        }
        for (int i = 0; i != edges.length; i++)
            if (edges[i] != null && RDFUtils.same(edges[i].getProperty(), property))
                return edges[i].count(graph, value);
        return 0;
    }
}
