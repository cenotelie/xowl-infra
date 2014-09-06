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

import java.util.Arrays;
import java.util.Iterator;

/**
 * Represents a collection of targets for edges
 *
 * @author Laurent Wouters
 */
public class EdgeTarget implements Iterable<GraphNode> {
    /**
     * The initial size of the buffer of the multiplicities
     */
    private static final int INIT_BUFFER_SIZE = 3;

    /**
     * The represented target node
     */
    private Node target;
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
     * Gets the number of graphs for this target
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
     */
    public void add(GraphNode graph) {
        boolean hasEmpty = false;
        for (int i = 0; i != graphs.length; i++) {
            hasEmpty = hasEmpty || (graphs[i] == null);
            if (graphs[i] == graph) {
                multiplicities[i]++;
                return;
            }
        }
        if (!hasEmpty) {
            graphs = Arrays.copyOf(graphs, graphs.length + INIT_BUFFER_SIZE);
            multiplicities = Arrays.copyOf(multiplicities, multiplicities.length + INIT_BUFFER_SIZE);
            graphs[size] = graph;
            multiplicities[size] = 1;
            size++;
            return;
        }
        for (int i = 0; i != graphs.length; i++) {
            if (graphs[i] == null) {
                graphs[i] = graph;
                multiplicities[i] = 1;
                size++;
                return;
            }
        }
    }

    /**
     * Removes the specified graph (or decrement the counter)
     *
     * @param graph A graph
     * @return true if this target is now empty and should be removed
     */
    public boolean remove(GraphNode graph) {
        for (int i = 0; i != graphs.length; i++) {
            if (graphs[i] == graph) {
                multiplicities[i]--;
                if (multiplicities[i] == 0) {
                    graphs[i] = null;
                    size--;
                }
                return (size == 0);
            }
        }
        return (size == 0);
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
        if (graph == null) {
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
        if (graph == null)
            return size;
        for (int i = 0; i != graphs.length; i++)
            if (graphs[i] == graph)
                return 1;
        return 0;
    }
}