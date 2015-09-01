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

package org.xowl.store.storage;

import org.xowl.store.rdf.*;

import java.util.Iterator;

/**
 * Represents the public API of a dataset
 *
 * @author Laurent Wouters
 */
public interface Dataset {
    /**
     * Adds the specified listener to this dataset
     *
     * @param listener A listener
     */
    void addListener(ChangeListener listener);

    /**
     * Removes the specified listener from this dataset
     *
     * @param listener A listener
     */
    void removeListener(ChangeListener listener);

    /**
     * Gets an iterator over all the quads in this dataset
     *
     * @return An iterator over the results
     */
    Iterator<Quad> getAll();

    /**
     * Gets an iterator over all the quads in this dataset that are in the specified graph
     *
     * @param graph A containing graph to match, or null
     * @return An iterator over the results
     */
    Iterator<Quad> getAll(GraphNode graph);

    /**
     * Gets an iterator over all the quads in this dataset that matches the given values
     *
     * @param subject  A subject node to match, or null
     * @param property A property to match, or null
     * @param object   An object node to match, or null
     * @return An iterator over the results
     */
    Iterator<Quad> getAll(SubjectNode subject, Property property, Node object);

    /**
     * Gets an iterator over all the quads in this dataset that matches the given values
     *
     * @param graph    A containing graph to match, or null
     * @param subject  A subject node to match, or null
     * @param property A property to match, or null
     * @param object   An object node to match, or null
     * @return An iterator over the results
     */
    Iterator<Quad> getAll(GraphNode graph, SubjectNode subject, Property property, Node object);

    /**
     * Gets the number of different quads in this dataset
     *
     * @return The number of different quads
     */
    long count();

    /**
     * Gets the number of different quads in this dataset that matches the given values
     *
     * @param graph A containing graph to match, or null
     * @return The number of different quads
     */
    long count(GraphNode graph);

    /**
     * Gets the number of different quads in this dataset that matches the given values
     *
     * @param subject  A subject node to match, or null
     * @param property A property to match, or null
     * @param object   An object node to match, or null
     * @return The number of different quads
     */
    long count(SubjectNode subject, Property property, Node object);

    /**
     * Gets the number of different quads in this dataset that matches the given values
     *
     * @param graph    A containing graph to match, or null
     * @param subject  A subject node to match, or null
     * @param property A property to match, or null
     * @param object   An object node to match, or null
     * @return The number of different quads
     */
    long count(GraphNode graph, SubjectNode subject, Property property, Node object);


    /**
     * Applies the specified change to this dataset
     *
     * @param change A change
     */
    void insert(Change change) throws UnsupportedNodeType;

    /**
     * Applies the specified changeset to this dataset
     *
     * @param changeset A changeset
     * @throws UnsupportedNodeType when the subject node type is unsupported
     */
    void insert(Changeset changeset) throws UnsupportedNodeType;

    /**
     * Adds a single instance of the specified quad to this dataset.
     * If the quad is already in the dataset, its multiplicity is increased; listeners will not be notified.
     *
     * @param quad A quad
     * @throws UnsupportedNodeType when the subject node type is unsupported
     */
    void add(Quad quad) throws UnsupportedNodeType;

    /**
     * Adds a single instance of the specified quad to this dataset.
     * If the quad is already in the dataset, its multiplicity is increased; listeners will not be notified.
     *
     * @param graph    The graph containing the quad
     * @param subject  The quad subject node
     * @param property The quad property
     * @param value    The quad value
     * @throws UnsupportedNodeType when the subject node type is unsupported
     */
    void add(GraphNode graph, SubjectNode subject, Property property, Node value) throws UnsupportedNodeType;

    /**
     * Removes a single instance of the matching quads from this dataset
     * This essentially decreases the multiplicity of the quad.
     * Quads are only removed when their multiplicity reached 0.
     * Listeners are notified of the completely removed quads only.
     *
     * @param quad A quad
     * @throws UnsupportedNodeType when the subject node type is unsupported
     */
    void remove(Quad quad) throws UnsupportedNodeType;

    /**
     * Removes a single instance of the matching quads from this dataset
     * This essentially decreases the multiplicity of the quad.
     * Quads are only removed when their multiplicity reached 0.
     * Listeners are notified of the completely removed quads only.
     *
     * @param graph    The graph containing the quad
     * @param subject  The quad subject node
     * @param property The quad property
     * @param value    The quad value
     * @throws UnsupportedNodeType when the subject node type is unsupported
     */
    void remove(GraphNode graph, SubjectNode subject, Property property, Node value) throws UnsupportedNodeType;
}
