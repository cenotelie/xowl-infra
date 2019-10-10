/*******************************************************************************
 * Copyright (c) 2017 Association Cénotélie (cenotelie.fr)
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

package org.xowl.infra.store.rdf;

import java.util.Collection;
import java.util.Iterator;

/**
 * Represents the part of the API for handling the quads themselves in a dataset of RDF quads
 *
 * @author Laurent Wouters
 */
public interface DatasetQuads {
    /**
     * Gets the multiplicity of a quad
     *
     * @param quad The quad
     * @return The multiplicity in this store
     */
    long getMultiplicity(Quad quad);

    /**
     * Gets the multiplicity of a quad
     *
     * @param graph    The graph
     * @param subject  The subject
     * @param property The property
     * @param object   The object
     * @return The multiplicity in this store
     */
    long getMultiplicity(GraphNode graph, SubjectNode subject, Property property, Node object);

    /**
     * Gets an iterator over all the quads in this dataset
     *
     * @return An iterator over the results
     */
    Iterator<? extends Quad> getAll();

    /**
     * Gets an iterator over all the quads in this dataset that are in the specified graph
     *
     * @param graph A containing graph to match, or null
     * @return An iterator over the results
     */
    Iterator<? extends Quad> getAll(GraphNode graph);

    /**
     * Gets an iterator over all the quads in this dataset that matches the given values
     *
     * @param subject  A subject node to match, or null
     * @param property A property to match, or null
     * @param object   An object node to match, or null
     * @return An iterator over the results
     */
    Iterator<? extends Quad> getAll(SubjectNode subject, Property property, Node object);

    /**
     * Gets an iterator over all the quads in this dataset that matches the given values
     *
     * @param graph    A containing graph to match, or null
     * @param subject  A subject node to match, or null
     * @param property A property to match, or null
     * @param object   An object node to match, or null
     * @return An iterator over the results
     */
    Iterator<? extends Quad> getAll(GraphNode graph, SubjectNode subject, Property property, Node object);

    /**
     * Gets the graphs in this dataset
     *
     * @return The graphs in this dataset
     */
    Collection<GraphNode> getGraphs();

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
     * Applies the specified changeset to this dataset
     *
     * @param changeset A changeset
     */
    void insert(Changeset changeset);

    /**
     * Adds a single instance of the specified quad to this dataset.
     * If the quad is already in the dataset, its multiplicity is increased; listeners will not be notified.
     *
     * @param quad A quad
     */
    void add(Quad quad);

    /**
     * Adds a single instance of the specified quad to this dataset.
     * If the quad is already in the dataset, its multiplicity is increased; listeners will not be notified.
     *
     * @param graph    The graph containing the quad
     * @param subject  The quad subject node
     * @param property The quad property
     * @param value    The quad value
     */
    void add(GraphNode graph, SubjectNode subject, Property property, Node value);

    /**
     * Removes a single instance of the matching quads from this dataset
     * This essentially decreases the multiplicity of the quad.
     * Quads are only removed when their multiplicity reached 0.
     * Listeners are notified of the completely removed quads only.
     *
     * @param quad A quad
     */
    void remove(Quad quad);

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
     */
    void remove(GraphNode graph, SubjectNode subject, Property property, Node value);

    /**
     * Removes all the quads from this dataset
     */
    void clear();

    /**
     * Removes all the quads from this dataset for the specified graph
     *
     * @param graph A graph
     */
    void clear(GraphNode graph);

    /**
     * Copies all the quads with the specified origin graph, to quads with the target graph.
     * Pre-existing quads from the target graph that do not correspond to an equivalent in the origin graph are removed if the overwrite flag is used.
     * If a target quad already exists, its multiplicity is incremented, otherwise it is created.
     * The quad in the origin graph is not affected.
     *
     * @param origin    The origin graph
     * @param target    The target graph
     * @param overwrite Whether to overwrite quads from the target graph
     */
    void copy(GraphNode origin, GraphNode target, boolean overwrite);

    /**
     * Moves all the quads with the specified origin graph, to quads with the target graph.
     * Pre-existing quads from the target graph that do not correspond to an equivalent in the origin graph are removed.
     * If a target quad already exists, its multiplicity is incremented, otherwise it is created.
     * The quad in the origin graph is always removed.
     *
     * @param origin The origin graph
     * @param target The target graph
     */
    void move(GraphNode origin, GraphNode target);
}
