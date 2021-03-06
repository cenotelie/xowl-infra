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
import org.xowl.infra.store.storage.impl.DatasetImpl;
import org.xowl.infra.store.storage.impl.MQuad;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

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
     * Gets the represented target node
     *
     * @return The represented target node
     */
    public Node getTarget() {
        return target;
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
     * Adds the specified graph (or increment the counter)
     *
     * @param graph A graph
     * @return The operation result
     */
    public int add(GraphNode graph) {
        boolean hasEmpty = false;
        for (int i = 0; i != graphs.length; i++) {
            hasEmpty = hasEmpty || (graphs[i] == null);
            if (RDFUtils.same(graphs[i], graph)) {
                multiplicities[i]++;
                return DatasetImpl.ADD_RESULT_INCREMENT;
            }
        }
        if (!hasEmpty) {
            graphs = Arrays.copyOf(graphs, graphs.length + INIT_BUFFER_SIZE);
            multiplicities = Arrays.copyOf(multiplicities, multiplicities.length + INIT_BUFFER_SIZE);
            graphs[size] = graph;
            multiplicities[size] = 1;
            size++;
            return DatasetImpl.ADD_RESULT_NEW;
        }
        for (int i = 0; i != graphs.length; i++) {
            if (graphs[i] == null) {
                graphs[i] = graph;
                multiplicities[i] = 1;
                size++;
                return DatasetImpl.ADD_RESULT_NEW;
            }
        }
        // cannot happen
        return DatasetImpl.ADD_RESULT_UNKNOWN;
    }

    /**
     * Removes the specified graph (or decrement the counter)
     *
     * @param graph A graph
     * @return The operation result
     */
    public int remove(GraphNode graph) {
        for (int i = 0; i != graphs.length; i++) {
            if (RDFUtils.same(graphs[i], graph)) {
                multiplicities[i]--;
                if (multiplicities[i] == 0) {
                    graphs[i] = null;
                    size--;
                    return (size == 0) ? DatasetImpl.REMOVE_RESULT_EMPTIED : DatasetImpl.REMOVE_RESULT_REMOVED;
                }
                return DatasetImpl.REMOVE_RESULT_DECREMENT;
            }
        }
        return DatasetImpl.REMOVE_RESULT_NOT_FOUND;
    }

    /**
     * Removes all the matching graphs (or decrement their counter)
     *
     * @param graph             The graph to match, or null
     * @param bufferDecremented The buffer for the decremented quads
     * @param bufferRemoved     The buffer for the removed quads
     * @return The operation result
     */
    public int removeAll(GraphNode graph, List<MQuad> bufferDecremented, List<MQuad> bufferRemoved) {
        for (int i = 0; i != graphs.length; i++) {
            if (graphs[i] != null && (graph == null || RDFUtils.same(graphs[i], graph))) {
                multiplicities[i]--;
                if (multiplicities[i] == 0) {
                    bufferRemoved.add(new MQuad(graphs[i], 0));
                    graphs[i] = null;
                    size--;
                } else {
                    bufferDecremented.add(new MQuad(graphs[i], multiplicities[i]));
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
        for (int i = 0; i != graphs.length; i++) {
            if (graphs[i] != null) {
                buffer.add(new MQuad(graphs[i], multiplicities[i]));
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
        for (int i = 0; i != graphs.length; i++) {
            if (graphs[i] != null && RDFUtils.same(graphs[i], graph)) {
                buffer.add(new MQuad(graphs[i], multiplicities[i]));
                graphs[i] = null;
                size--;
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
        int indexOld = -1;
        int indexNew = -1;
        int indexEmpty = -1;
        for (int i = 0; i != graphs.length; i++) {
            if (graphs[i] != null) {
                if (RDFUtils.same(graphs[i], origin))
                    indexOld = i;
                if (RDFUtils.same(graphs[i], target))
                    indexNew = i;
            } else if (indexEmpty == -1) {
                indexEmpty = i;
            }
        }
        if (indexOld != -1) {
            // if the origin graph is present, copy
            if (indexNew != -1) {
                // the target graph is also here, increment it
                multiplicities[indexNew]++;
            } else {
                // insert the target graph
                if (indexEmpty == -1) {
                    // the buffer is full
                    graphs = Arrays.copyOf(graphs, graphs.length + INIT_BUFFER_SIZE);
                    multiplicities = Arrays.copyOf(multiplicities, multiplicities.length + INIT_BUFFER_SIZE);
                    indexEmpty = size;
                }
                graphs[indexEmpty] = target;
                multiplicities[indexEmpty] = 1;
                size++;
                bufferNew.add(new MQuad(target, 1));
            }
        } else if (overwrite && indexNew != -1) {
            // the target graph is there but not the old one and we must overwrite
            // we need to remove this
            bufferOld.add(new MQuad(target, multiplicities[indexNew]));
            graphs[indexNew] = null;
            multiplicities[indexNew] = 0;
            size--;
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
        int indexOld = -1;
        int indexNew = -1;
        for (int i = 0; i != graphs.length; i++) {
            if (graphs[i] != null) {
                if (RDFUtils.same(graphs[i], origin))
                    indexOld = i;
                if (RDFUtils.same(graphs[i], target))
                    indexNew = i;
            }
        }
        if (indexOld != -1) {
            // if the origin graph is present, copy
            bufferOld.add(new MQuad(origin, multiplicities[indexOld]));
            if (indexNew != -1) {
                // the target graph is also here, increment it
                multiplicities[indexNew]++;
                graphs[indexOld] = null;
                multiplicities[indexOld] = 0;
                size--;
            } else {
                // replace the origin graph by the target one
                // reset the multiplicity
                graphs[indexOld] = target;
                multiplicities[indexOld] = 1;
                bufferNew.add(new MQuad(target, 1));
            }
        } else if (indexNew != -1) {
            // the target graph is there but not the old one
            // we need to remove this
            bufferOld.add(new MQuad(target, multiplicities[indexNew]));
            graphs[indexNew] = null;
            multiplicities[indexNew] = 0;
            size--;
        }
        return (size == 0);
    }

    @Override
    public Iterator<GraphNode> iterator() {
        return new SparseIterator<>(graphs);
    }

    /**
     * Gets the multiplicity for the quad
     *
     * @param graph The graph
     * @return The multiplicity
     */
    public long getMultiplicity(GraphNode graph) {
        for (int i = 0; i != graphs.length; i++) {
            if (graphs[i] != null && RDFUtils.same(graphs[i], graph))
                return multiplicities[i];
        }
        return 0;
    }

    /**
     * Gets all the quads with the specified data
     *
     * @param graph The filtering graph
     * @return An iterator over the quads
     */
    public Iterator<MQuad> getAll(GraphNode graph) {
        if (graph == null || graph.getNodeType() == Node.TYPE_VARIABLE) {
            return new AdaptingIterator<>(new IndexIterator<GraphNode>(graphs) {
                @Override
                public void remove() {
                    graphs[lastResult] = null;
                    multiplicities[lastResult] = 0;
                    size--;
                }
            }, new Adapter<Integer, MQuad>() {
                @Override
                public MQuad adapt(Integer element) {
                    return new MQuad(graphs[element], multiplicities[element]);
                }
            });
        }

        for (int i = 0; i != graphs.length; i++) {
            if (graphs[i] != null && RDFUtils.same(graphs[i], graph)) {
                final int index = i;
                return new SingleIterator<MQuad>(new MQuad(graphs[i], multiplicities[i])) {
                    @Override
                    public void remove() {
                        graphs[index] = null;
                        multiplicities[index] = 0;
                        size--;
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
        for (int i = 0; i != graphs.length; i++) {
            if (graphs[i] != null && !buffer.contains(graphs[i]))
                buffer.add(graphs[i]);
        }
    }

    /**
     * Returns the number of different quads with the specified data
     *
     * @param graph The filtering graph
     * @return The number of different quads
     */
    public int count(GraphNode graph) {
        if (graph == null || graph.getNodeType() == Node.TYPE_VARIABLE)
            return size;
        for (int i = 0; i != graphs.length; i++)
            if (RDFUtils.same(graphs[i], graph))
                return 1;
        return 0;
    }
}