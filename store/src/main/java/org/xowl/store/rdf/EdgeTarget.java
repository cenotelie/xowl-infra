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
public class EdgeTarget implements Iterable<String> {
    /**
     * The initial size of the buffer of the multiplicities
     */
    private static final int INIT_BUFFER_SIZE = 3;

    /**
     * The represented target node
     */
    private Node target;
    /**
     * The containing ontologies
     */
    private String[] ontologies;
    /**
     * The multiplicity counters for the ontologies
     */
    private int[] multiplicities;
    /**
     * The number of ontologies
     */
    private int size;

    /**
     * Initializes this target
     *
     * @param ontology The first containing ontology
     * @param target   The represented target
     */
    public EdgeTarget(String ontology, Node target) {
        this.target = target;
        this.ontologies = new String[INIT_BUFFER_SIZE];
        this.multiplicities = new int[INIT_BUFFER_SIZE];
        this.ontologies[0] = ontology;
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
     * Gets the number of ontologies for this target
     *
     * @return The number of ontologies
     */
    public int getSize() {
        return size;
    }

    /**
     * Adds the specified ontology (or increment the counter)
     *
     * @param ontology An ontology
     */
    public void add(String ontology) {
        boolean hasEmpty = false;
        for (int i = 0; i != ontologies.length; i++) {
            hasEmpty = hasEmpty || (ontologies[i] == null);
            if (ontologies[i] != null && ontologies[i].equals(ontology)) {
                multiplicities[i]++;
                return;
            }
        }
        if (!hasEmpty) {
            ontologies = Arrays.copyOf(ontologies, ontologies.length + INIT_BUFFER_SIZE);
            multiplicities = Arrays.copyOf(multiplicities, multiplicities.length + INIT_BUFFER_SIZE);
            ontologies[size] = ontology;
            multiplicities[size] = 1;
            size++;
            return;
        }
        for (int i = 0; i != ontologies.length; i++) {
            if (ontologies[i] == null) {
                ontologies[i] = ontology;
                multiplicities[i] = 1;
                size++;
                return;
            }
        }
    }

    /**
     * Removes the specified ontology (or decrement the counter)
     *
     * @param ontology An ontology
     * @return true if this target is now empty and should be removed
     */
    public boolean remove(String ontology) {
        for (int i = 0; i != ontologies.length; i++) {
            if (ontologies[i] != null && ontologies[i].equals(ontology)) {
                multiplicities[i]--;
                if (multiplicities[i] == 0) {
                    ontologies[i] = null;
                    size--;
                }
                return (size == 0);
            }
        }
        return (size == 0);
    }

    @Override
    public Iterator<String> iterator() {
        return new SparseIterator<>(ontologies);
    }

    /**
     * Gets all the xOWL triples with the specified data
     *
     * @param ontology The filtering ontology
     * @return An iterator over the triples
     */
    public Iterator<Quad> getAllTriples(String ontology) {
        if (ontology == null) {
            return new AdaptingIterator<>(iterator(), new Adapter<Quad>() {
                @Override
                public <X> Quad adapt(X element) {
                    return new Quad((String) element, null, null, null);
                }
            });
        }

        for (int i = 0; i != ontologies.length; i++) {
            if (ontologies[i] != null && ontologies[i].equals(ontology)) {
                return new AdaptingIterator<>(new SingleIterator<>(ontology), new Adapter<Quad>() {
                    @Override
                    public <X> Quad adapt(X element) {
                        return new Quad((String) element, null, null, null);
                    }
                });
            }
        }

        return new SingleIterator<>(null);
    }

    /**
     * Returns the number of different triples with the specified data
     *
     * @param ontology The filtering ontology
     * @return The number of different triples
     */
    public int count(String ontology) {
        if (ontology == null)
            return size;
        for (int i = 0; i != ontologies.length; i++)
            if (ontologies[i].equals(ontology))
                return 1;
        return 0;
    }
}