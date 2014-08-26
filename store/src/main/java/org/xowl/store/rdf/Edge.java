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
 * Represents an edge in a RDF graph
 *
 * @author Laurent Wouters
 */
public class Edge implements Iterable<EdgeTarget> {
    /**
     * The initial size of the buffer of the targets
     */
    private static final int INIT_BUFFER_SIZE = 10;

    /**
     * The label on this edge
     */
    private RDFProperty property;
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
     * @param ontology The ontology containing the triple
     * @param property The property on this edge
     * @param object   The first object node for this edge
     */
    public Edge(Ontology ontology, RDFProperty property, RDFNode object) {
        this.property = property;
        this.targets = new EdgeTarget[INIT_BUFFER_SIZE];
        this.targets[0] = new EdgeTarget(ontology, object);
        this.size = 1;
    }

    /**
     * Gets the property on this edge
     *
     * @return The property on this edge
     */
    public RDFProperty getProperty() {
        return property;
    }

    /**
     * Gets the number of targets for this edge
     *
     * @return The number of targets for this edge
     */
    public int getSize() {
        return size;
    }

    /**
     * Inserts the specified edge (or increment the counter)
     *
     * @param ontology The ontology containing the triple
     * @param value    The edge's target node
     */
    public void increment(Ontology ontology, RDFNode value) {
        boolean hasEmpty = false;
        for (int i = 0; i != targets.length; i++) {
            hasEmpty = hasEmpty || (targets[i] != null);
            if (targets[i] != null && targets[i].getTarget() == value) {
                targets[i].increment(ontology);
                return;
            }
        }
        if (!hasEmpty) {
            targets = Arrays.copyOf(targets, targets.length + INIT_BUFFER_SIZE);
            targets[size] = new EdgeTarget(ontology, value);
            size++;
        }
        for (int i = 0; i != targets.length; i++) {
            if (targets[i] == null) {
                targets[i] = new EdgeTarget(ontology, value);
                size++;
                return;
            }
        }
    }

    /**
     * Removes the specified edge (or decrement the counter)
     *
     * @param ontology The ontology containing the triple
     * @param value    The edge's target node
     * @return true if this edge is now empty and shall be removed
     */
    public boolean decrement(Ontology ontology, RDFNode value) {
        for (int i = 0; i != targets.length; i++) {
            if (targets[i] != null) {
                if (targets[i].getTarget() == value) {
                    boolean remove = targets[i].decrement(ontology);
                    if (remove) {
                        targets[i] = null;
                        size--;
                    }
                    return (size == 0);
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
     * Gets all the xOWL triples with the specified data
     *
     * @param value    The filtering object value
     * @param ontology The filtering ontology
     * @return An iterator over the triples
     */
    public Iterator<RDFTriple> getAllTriples(final RDFNode value, final Ontology ontology) {
        if (value == null) {
            return new AdaptingIterator<>(new CombiningIterator<>(new IndexIterator<>(targets), new Adapter<Iterator<RDFTriple>>() {
                @Override
                public <X> Iterator<RDFTriple> adapt(X element) {
                    Integer index = (Integer) element;
                    return targets[index].getAllTriples(ontology);
                }
            }), new Adapter<RDFTriple>() {
                @Override
                public <X> RDFTriple adapt(X element) {
                    Couple<Integer, RDFTriple> result = (Couple<Integer, RDFTriple>) element;
                    result.y.setObject(targets[result.x].getTarget());
                    return result.y;
                }
            });
        }

        for (int i = 0; i != targets.length; i++) {
            if (targets[i] != null && targets[i].getTarget() == value) {
                return new AdaptingIterator<>(targets[i].getAllTriples(ontology), new Adapter<RDFTriple>() {
                    @Override
                    public <X> RDFTriple adapt(X element) {
                        RDFTriple result = (RDFTriple) element;
                        result.setObject(value);
                        return result;
                    }
                });
            }
        }

        return new SingleIterator<>(null);
    }
}