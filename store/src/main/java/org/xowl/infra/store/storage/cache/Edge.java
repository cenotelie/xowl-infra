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

import fr.cenotelie.commons.utils.collections.*;
import org.xowl.infra.store.RDFUtils;
import org.xowl.infra.store.rdf.GraphNode;
import org.xowl.infra.store.rdf.Node;
import org.xowl.infra.store.rdf.Property;
import org.xowl.infra.store.storage.DatasetQuadsImpl;
import org.xowl.infra.store.storage.MQuad;

import java.util.Arrays;
import java.util.Collection;
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
     * Gets the property on this edge
     *
     * @return The property on this edge
     */
    public Property getProperty() {
        return property;
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
            if (targets[i] != null && RDFUtils.same(targets[i].getTarget(), value)) {
                return targets[i].add(graph);
            }
        }
        if (!hasEmpty) {
            targets = Arrays.copyOf(targets, targets.length + INIT_BUFFER_SIZE);
            targets[size] = new EdgeTarget(graph, value);
            size++;
            return DatasetQuadsImpl.ADD_RESULT_NEW;
        }
        for (int i = 0; i != targets.length; i++) {
            if (targets[i] == null) {
                targets[i] = new EdgeTarget(graph, value);
                size++;
                return DatasetQuadsImpl.ADD_RESULT_NEW;
            }
        }
        // cannot happen
        return DatasetQuadsImpl.ADD_RESULT_UNKNOWN;
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
            if (targets[i] != null && RDFUtils.same(targets[i].getTarget(), value)) {
                int result = targets[i].remove(graph);
                if (result == DatasetQuadsImpl.REMOVE_RESULT_EMPTIED) {
                    targets[i] = null;
                    size--;
                    return (size == 0) ? DatasetQuadsImpl.REMOVE_RESULT_EMPTIED : DatasetQuadsImpl.REMOVE_RESULT_REMOVED;
                }
                return result;
            }
        }
        return DatasetQuadsImpl.REMOVE_RESULT_NOT_FOUND;
    }

    /**
     * Removes all the matching edges (or decrement their counter)
     *
     * @param graph             The graph to match, or null
     * @param value             The edge's target node to match, or null
     * @param bufferDecremented The buffer for the decremented quads
     * @param bufferRemoved     The buffer for the removed quads
     * @return The operation result
     */
    public int removeAll(GraphNode graph, Node value, List<MQuad> bufferDecremented, List<MQuad> bufferRemoved) {
        for (int i = 0; i != targets.length; i++) {
            if (targets[i] != null && (value == null || RDFUtils.same(targets[i].getTarget(), value))) {
                int originalSizeDec = bufferDecremented.size();
                int originalSizeRem = bufferRemoved.size();
                int result = targets[i].removeAll(graph, bufferDecremented, bufferRemoved);
                for (int j = originalSizeDec; j != bufferDecremented.size(); j++)
                    bufferDecremented.get(j).setObject(targets[i].getTarget());
                for (int j = originalSizeRem; j != bufferRemoved.size(); j++)
                    bufferRemoved.get(j).setObject(targets[i].getTarget());
                if (result == DatasetQuadsImpl.REMOVE_RESULT_EMPTIED) {
                    targets[i] = null;
                    size--;
                }
            }
        }
        return (size == 0) ? DatasetQuadsImpl.REMOVE_RESULT_EMPTIED : DatasetQuadsImpl.REMOVE_RESULT_REMOVED;
    }

    /**
     * Clears this object
     *
     * @param buffer The buffer for the removed quads
     */
    public void clear(List<MQuad> buffer) {
        for (int i = 0; i != targets.length; i++) {
            if (targets[i] != null) {
                int originalSize = buffer.size();
                targets[i].clear(buffer);
                for (int j = originalSize; j != buffer.size(); j++)
                    buffer.get(j).setObject(targets[i].getTarget());
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
        for (int i = 0; i != targets.length; i++) {
            if (targets[i] != null) {
                int originalSize = buffer.size();
                boolean empty = targets[i].clear(graph, buffer);
                for (int j = originalSize; j != buffer.size(); j++)
                    buffer.get(j).setObject(targets[i].getTarget());
                if (empty) {
                    targets[i] = null;
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
        for (int i = 0; i != targets.length; i++) {
            if (targets[i] != null) {
                int originalSizeOld = bufferOld.size();
                int originalSizeNew = bufferNew.size();
                boolean empty = targets[i].copy(origin, target, bufferOld, bufferNew, overwrite);
                for (int j = originalSizeOld; j != bufferOld.size(); j++)
                    bufferOld.get(j).setObject(targets[i].getTarget());
                for (int j = originalSizeNew; j != bufferNew.size(); j++)
                    bufferNew.get(j).setObject(targets[i].getTarget());
                if (empty) {
                    targets[i] = null;
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
        for (int i = 0; i != targets.length; i++) {
            if (targets[i] != null) {
                int originalSizeOld = bufferOld.size();
                int originalSizeNew = bufferNew.size();
                boolean empty = targets[i].move(origin, target, bufferOld, bufferNew);
                for (int j = originalSizeOld; j != bufferOld.size(); j++)
                    bufferOld.get(j).setObject(targets[i].getTarget());
                for (int j = originalSizeNew; j != bufferNew.size(); j++)
                    bufferNew.get(j).setObject(targets[i].getTarget());
                if (empty) {
                    targets[i] = null;
                    size--;
                }
            }
        }
        return (size == 0);
    }

    @Override
    public Iterator<EdgeTarget> iterator() {
        return new SparseIterator<>(targets);
    }

    /**
     * Gets the multiplicity for the quad
     *
     * @param graph The graph
     * @param value The edge's target node
     * @return The multiplicity
     */
    public long getMultiplicity(GraphNode graph, Node value) {
        for (int i = 0; i != targets.length; i++) {
            if (targets[i] != null && RDFUtils.same(targets[i].getTarget(), value)) {
                return targets[i].getMultiplicity(graph);
            }
        }
        return 0;
    }

    /**
     * Gets all the quads with the specified data
     *
     * @param graph The filtering graph
     * @param value The filtering object value
     * @return An iterator over the quads
     */
    public Iterator<MQuad> getAll(final GraphNode graph, final Node value) {
        if (value == null || value.getNodeType() == Node.TYPE_VARIABLE) {
            return new AdaptingIterator<>(
                    new CombiningIterator<Integer, MQuad>(
                            new IndexIterator<>(targets),
                            element -> targets[element].getAll(graph)) {
                        @Override
                        public void remove() {
                            lastRightIterator.remove();
                            int index = current.x;
                            if (targets[index].getSize() == 0) {
                                targets[index] = null;
                                size--;
                            }
                        }
                    },
                    element -> {
                        element.y.setObject(targets[element.x].getTarget());
                        return element.y;
                    });
        }

        for (int i = 0; i != targets.length; i++) {
            if (targets[i] != null && RDFUtils.same(targets[i].getTarget(), value)) {
                final int index = i;
                return new AdaptingIterator<MQuad, MQuad>(
                        targets[i].getAll(graph),
                        element -> {
                            element.setObject(value);
                            return element;
                        }) {
                    @Override
                    public void remove() {
                        content.remove();
                        if (targets[index].getSize() == 0) {
                            targets[index] = null;
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
        for (int i = 0; i != targets.length; i++) {
            if (targets[i] != null)
                targets[i].getGraphs(buffer);
        }
    }

    /**
     * Returns the number of different quads with the specified data
     *
     * @param graph The filtering graph
     * @param value The filtering object value
     * @return The number of different quads
     */
    public int count(GraphNode graph, Node value) {
        if (value == null || value.getNodeType() == Node.TYPE_VARIABLE) {
            int count = 0;
            for (int i = 0; i != targets.length; i++)
                if (targets[i] != null)
                    count += targets[i].count(graph);
            return count;
        }
        for (int i = 0; i != targets.length; i++)
            if (targets[i] != null && RDFUtils.same(targets[i].getTarget(), value))
                return targets[i].count(graph);
        return 0;
    }
}
