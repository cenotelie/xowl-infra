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

import org.xowl.lang.owl2.Ontology;
import org.xowl.utils.collections.*;

import java.util.Arrays;
import java.util.Iterator;

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
     * Gets the number of edges in this bucket
     *
     * @return The number of edges in this bucket
     */
    public int getSize() {
        return size;
    }

    /**
     * Adds the specified edge from this bucket
     *
     * @param ontology The containing ontology
     * @param property The property on this edge
     * @param value    The target value
     */
    public void add(Ontology ontology, Property property, Node value) {
        boolean hasEmpty = false;
        for (int i = 0; i != edges.length; i++) {
            hasEmpty = hasEmpty || (edges[i] == null);
            if (edges[i] != null && edges[i].getProperty() == property) {
                edges[i].add(ontology, value);
                return;
            }
        }
        if (!hasEmpty) {
            edges = Arrays.copyOf(edges, edges.length + INIT_SIZE);
            edges[size] = new Edge(ontology, property, value);
            size++;
            return;
        }
        for (int i = 0; i != edges.length; i++) {
            if (edges[i] == null) {
                edges[i] = new Edge(ontology, property, value);
                size++;
                return;
            }
        }
    }

    /**
     * Removes the specified edge from this bucket
     *
     * @param ontology The containing ontology
     * @param property The property on this edge
     * @param value    The target value
     * @return true if this bucket is now empty and shall be removed
     */
    public boolean remove(Ontology ontology, Property property, Node value) {
        for (int i = 0; i != edges.length; i++) {
            if (edges[i] != null && edges[i].getProperty() == property) {
                if (edges[i].remove(ontology, value)) {
                    edges[i] = null;
                    size--;
                }
                return (size == 0);
            }
        }
        return (size == 0);
    }

    @Override
    public Iterator<Edge> iterator() {
        return new SparseIterator<>(edges);
    }

    /**
     * Gets all the xOWL triples with the specified data
     *
     * @param property The filtering property
     * @param value    The filtering object value
     * @param ontology The filtering ontology
     * @return An iterator over the triples
     */
    public Iterator<Triple> getAllTriples(final Property property, final Node value, final Ontology ontology) {
        if (property == null) {
            return new AdaptingIterator<>(new CombiningIterator<>(new IndexIterator<>(edges), new Adapter<Iterator<Triple>>() {
                @Override
                public <X> Iterator<Triple> adapt(X element) {
                    Integer index = (Integer) element;
                    return edges[index].getAllTriples(value, ontology);
                }
            }), new Adapter<Triple>() {
                @Override
                public <X> Triple adapt(X element) {
                    Couple<Integer, Triple> result = (Couple<Integer, Triple>) element;
                    result.y.setProperty(edges[result.x].getProperty());
                    return result.y;
                }
            });
        }

        for (int i = 0; i != edges.length; i++) {
            if (edges[i] != null && edges[i].getProperty() == property) {
                return new AdaptingIterator<>(edges[i].getAllTriples(value, ontology), new Adapter<Triple>() {
                    @Override
                    public <X> Triple adapt(X element) {
                        Triple result = (Triple) element;
                        result.setProperty(property);
                        return result;
                    }
                });
            }
        }

        return new SingleIterator<>(null);
    }

    /**
     * Returns the number of different triples with the specified data
     *
     * @param property The filtering property
     * @param value    The filtering object value
     * @param ontology The filtering ontology
     * @return The number of different triples
     */
    public int count(Property property, Node value, Ontology ontology) {
        if (property == null) {
            int count = 0;
            for (int i = 0; i != edges.length; i++)
                if (edges[i] != null)
                    count += edges[i].count(value, ontology);
            return count;
        }
        for (int i = 0; i != edges.length; i++)
            if (edges[i] != null && edges[i].getProperty() == property)
                return edges[i].count(value, ontology);
        return 0;
    }
}
