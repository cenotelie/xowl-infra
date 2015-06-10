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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.UUID;

/**
 * Represents an abstract RDF store (implementation unknown)
 *
 * @author Laurent Wouters
 */
public abstract class AbstractStore {
    /**
     * Default URIs for the anonymous RDF graphs
     */
    private static final String DEFAULT_GRAPH_URIS = "http://xowl.org/store/rdfgraphs/";

    /**
     * Creates the URI of a new anonymous RDF graph
     *
     * @return The URI of a new anonymous RDF graph
     */
    public static String createAnonymousGraph() {
        return DEFAULT_GRAPH_URIS + UUID.randomUUID().toString();
    }

    /**
     * The current listeners on this store
     */
    protected Collection<ChangeListener> listeners;

    /**
     * Initializes this store
     */
    public AbstractStore() {
        listeners = new ArrayList<>();
    }

    /**
     * Adds the specified listener to this store
     *
     * @param listener A listener
     */
    public void addListener(ChangeListener listener) {
        listeners.add(listener);
    }

    /**
     * Removes the specified listener from this store
     *
     * @param listener A listener
     */
    public void removeListener(ChangeListener listener) {
        listeners.remove(listener);
    }

    /**
     * Broadcasts the information that a new quad was added
     *
     * @param quad The quad
     */
    protected void onQuadAdded(Quad quad) {
        Change change = new Change(quad, true);
        for (ChangeListener listener : listeners)
            listener.onChange(change);
    }

    /**
     * Broadcasts the information that a quad was removed
     *
     * @param quad The quad
     */
    protected void onQuadRemoved(Quad quad) {
        Change change = new Change(quad, false);
        for (ChangeListener listener : listeners)
            listener.onChange(change);
    }

    /**
     * Gets an iterator over all the quads in this store
     *
     * @return An iterator over the results
     */
    public Iterator<Quad> getAll() {
        return getAll(null, null, null, null);
    }

    /**
     * Gets an iterator over all the quads in this store that are in the specified graph
     *
     * @param graph A containing graph to match, or null
     * @return An iterator over the results
     */
    public Iterator<Quad> getAll(GraphNode graph) {
        return getAll(graph, null, null, null);
    }

    /**
     * Gets an iterator over all the quads in this store that matches the given values
     *
     * @param subject  A subject node to match, or null
     * @param property A property to match, or null
     * @param object   An object node to match, or null
     * @return An iterator over the results
     */
    public Iterator<Quad> getAll(SubjectNode subject, Property property, Node object) {
        return getAll(null, subject, property, object);
    }

    /**
     * Gets an iterator over all the quads in this store that matches the given values
     *
     * @param graph    A containing graph to match, or null
     * @param subject  A subject node to match, or null
     * @param property A property to match, or null
     * @param object   An object node to match, or null
     * @return An iterator over the results
     */
    public abstract Iterator<Quad> getAll(GraphNode graph, SubjectNode subject, Property property, Node object);

    /**
     * Gets the number of different quads in this store
     *
     * @return The number of different quads
     */
    public int count() {
        return count(null, null, null, null);
    }

    /**
     * Gets the number of different quads in this store that matches the given values
     *
     * @param graph A containing graph to match, or null
     * @return The number of different quads
     */
    public int count(GraphNode graph) {
        return count(graph, null, null, null);
    }

    /**
     * Gets the number of different quads in this store that matches the given values
     *
     * @param subject  A subject node to match, or null
     * @param property A property to match, or null
     * @param object   An object node to match, or null
     * @return The number of different quads
     */
    public int count(SubjectNode subject, Property property, Node object) {
        return count(null, subject, property, object);
    }

    /**
     * Gets the number of different quads in this store that matches the given values
     *
     * @param graph    A containing graph to match, or null
     * @param subject  A subject node to match, or null
     * @param property A property to match, or null
     * @param object   An object node to match, or null
     * @return The number of different quads
     */
    public abstract int count(GraphNode graph, SubjectNode subject, Property property, Node object);
}
