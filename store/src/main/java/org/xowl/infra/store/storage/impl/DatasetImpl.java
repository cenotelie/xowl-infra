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

package org.xowl.infra.store.storage.impl;

import org.xowl.infra.store.RDFUtils;
import org.xowl.infra.store.rdf.*;
import org.xowl.infra.store.storage.Dataset;
import org.xowl.infra.store.storage.UnsupportedNodeType;
import org.xowl.infra.utils.collections.SingleIterator;
import org.xowl.infra.utils.logging.Logger;

import java.util.*;

/**
 * Base implementation of a dataset
 *
 * @author Laurent Wouters
 */
public abstract class DatasetImpl implements Dataset {
    /**
     * When adding a quad, something weird happened
     */
    public static final int ADD_RESULT_UNKNOWN = 0;
    /**
     * When adding a quad, it was already present and its multiplicity incremented
     */
    public static final int ADD_RESULT_INCREMENT = 1;
    /**
     * When adding a quad, it was not already present and was therefore new
     */
    public static final int ADD_RESULT_NEW = 2;
    /**
     * When removing a quad, it was not found in the store
     */
    public static final int REMOVE_RESULT_NOT_FOUND = 0;
    /**
     * When removing a quad, it was decremented and the multiplicity did not reached 0 yet
     */
    public static final int REMOVE_RESULT_DECREMENT = 1;
    /**
     * When removing a quad, its multiplicity reached 0
     */
    public static final int REMOVE_RESULT_REMOVED = 2;
    /**
     * When removing a quad, its multiplicity reached 0 and the child dataset is now empty
     */
    public static final int REMOVE_RESULT_EMPTIED = 6;

    /**
     * The current listeners on this store
     */
    protected final Collection<ChangeListener> listeners;

    /**
     * Initializes this dataset
     */
    public DatasetImpl() {
        listeners = new ArrayList<>();
    }

    @Override
    public void addListener(ChangeListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(ChangeListener listener) {
        listeners.remove(listener);
    }

    @Override
    public long getMultiplicity(Quad quad) throws UnsupportedNodeType {
        return getMultiplicity(quad.getGraph(), quad.getSubject(), quad.getProperty(), quad.getObject());
    }

    @Override
    public Iterator<Quad> getAll() {
        try {
            return getAll(null, null, null, null);
        } catch (UnsupportedNodeType exception) {
            Logger.DEFAULT.error(exception);
            return new SingleIterator<>(null);
        }
    }

    @Override
    public Iterator<Quad> getAll(GraphNode graph) throws UnsupportedNodeType {
        return getAll(graph, null, null, null);
    }

    @Override
    public Iterator<Quad> getAll(SubjectNode subject, Property property, Node object) throws UnsupportedNodeType {
        return getAll(null, subject, property, object);
    }

    @Override
    public long count() {
        try {
            return count(null, null, null, null);
        } catch (UnsupportedNodeType exception) {
            Logger.DEFAULT.error(exception);
            return 0;
        }
    }

    @Override
    public long count(GraphNode graph) throws UnsupportedNodeType {
        return count(graph, null, null, null);
    }

    @Override
    public long count(SubjectNode subject, Property property, Node object) throws UnsupportedNodeType {
        return count(null, subject, property, object);
    }

    @Override
    public void insert(Changeset changeset) throws UnsupportedNodeType {
        Collection<Quad> incremented = new ArrayList<>();
        Collection<Quad> decremented = new ArrayList<>();
        Collection<Quad> added = new ArrayList<>();
        Collection<Quad> removed = new ArrayList<>();
        try {
            for (Quad quad : changeset.getAdded()) {
                int result = doAddQuad(quad.getGraph(), quad.getSubject(), quad.getProperty(), quad.getObject());
                if (result == ADD_RESULT_NEW) {
                    added.add(quad);
                } else if (result == ADD_RESULT_INCREMENT) {
                    incremented.add(quad);
                }
            }
            for (Quad quad : changeset.getRemoved()) {
                int result = doRemoveQuad(quad.getGraph(), quad.getSubject(), quad.getProperty(), quad.getObject());
                if (result >= REMOVE_RESULT_REMOVED) {
                    removed.add(quad);
                } else if (result == REMOVE_RESULT_DECREMENT) {
                    decremented.add(quad);
                }
            }
        } catch (UnsupportedNodeType exception) {
            // rollback the previously inserted quads
            for (Quad quad : incremented)
                doRemoveQuad(quad.getGraph(), quad.getSubject(), quad.getProperty(), quad.getObject());
            for (Quad quad : added)
                doRemoveQuad(quad.getGraph(), quad.getSubject(), quad.getProperty(), quad.getObject());
            for (Quad quad : decremented)
                doAddQuad(quad.getGraph(), quad.getSubject(), quad.getProperty(), quad.getObject());
            for (Quad quad : removed)
                doAddQuad(quad.getGraph(), quad.getSubject(), quad.getProperty(), quad.getObject());
            throw exception;
        }
        if (!incremented.isEmpty() || !decremented.isEmpty() || !added.isEmpty() || !removed.isEmpty()) {
            // transmit the changes only if a there are some!
            Changeset newChangeset = new Changeset(incremented, decremented, added, removed);
            for (ChangeListener listener : listeners)
                listener.onChange(newChangeset);
        }
    }

    @Override
    public void add(Quad quad) throws UnsupportedNodeType {
        int result = doAddQuad(quad.getGraph(), quad.getSubject(), quad.getProperty(), quad.getObject());
        if (result < DatasetImpl.ADD_RESULT_INCREMENT)
            return;
        for (ChangeListener listener : listeners) {
            if (result >= DatasetImpl.ADD_RESULT_NEW)
                listener.onAdded(quad);
            else
                listener.onIncremented(quad);
        }
    }

    @Override
    public void add(GraphNode graph, SubjectNode subject, Property property, Node value) throws UnsupportedNodeType {
        int result = doAddQuad(graph, subject, property, value);
        Quad quad = new Quad(graph, subject, property, value);
        if (result < DatasetImpl.ADD_RESULT_INCREMENT)
            return;
        for (ChangeListener listener : listeners) {
            if (result >= DatasetImpl.ADD_RESULT_NEW)
                listener.onAdded(quad);
            else
                listener.onIncremented(quad);
        }
    }

    @Override
    public void remove(Quad quad) throws UnsupportedNodeType {
        if (quad.getGraph() != null && quad.getSubject() != null && quad.getProperty() != null && quad.getObject() != null) {
            // remove a single quad
            int result = doRemoveQuad(quad.getGraph(), quad.getSubject(), quad.getProperty(), quad.getObject());
            if (result < DatasetImpl.REMOVE_RESULT_DECREMENT)
                return;
            for (ChangeListener listener : listeners) {
                if (result >= DatasetImpl.REMOVE_RESULT_REMOVED)
                    listener.onRemoved(quad);
                else
                    listener.onDecremented(quad);
            }
        } else {
            List<MQuad> bufferDecremented = new ArrayList<>();
            List<MQuad> bufferRemoved = new ArrayList<>();
            doRemoveQuads(quad.getGraph(), quad.getSubject(), quad.getProperty(), quad.getObject(), bufferDecremented, bufferRemoved);
            if (!bufferDecremented.isEmpty() || !bufferRemoved.isEmpty()) {
                Changeset changeset = new Changeset(Collections.EMPTY_LIST, Collections.EMPTY_LIST, (Collection) bufferDecremented, (Collection) bufferRemoved);
                for (ChangeListener listener : listeners) {
                    listener.onChange(changeset);
                }
            }
        }
    }

    @Override
    public void remove(GraphNode graph, SubjectNode subject, Property property, Node value) throws UnsupportedNodeType {
        if (graph != null && subject != null && property != null && value != null) {
            // remove a single quad
            int result = doRemoveQuad(graph, subject, property, value);
            if (result < DatasetImpl.REMOVE_RESULT_DECREMENT)
                return;
            Quad quad = new Quad(graph, subject, property, value);
            for (ChangeListener listener : listeners) {
                if (result >= DatasetImpl.REMOVE_RESULT_REMOVED)
                    listener.onRemoved(quad);
                else
                    listener.onDecremented(quad);
            }
        } else {
            List<MQuad> bufferDecremented = new ArrayList<>();
            List<MQuad> bufferRemoved = new ArrayList<>();
            doRemoveQuads(graph, subject, property, value, bufferDecremented, bufferRemoved);
            if (!bufferDecremented.isEmpty() || !bufferRemoved.isEmpty()) {
                Changeset changeset = new Changeset(Collections.EMPTY_LIST, Collections.EMPTY_LIST, (Collection) bufferDecremented, (Collection) bufferRemoved);
                for (ChangeListener listener : listeners) {
                    listener.onChange(changeset);
                }
            }
        }
    }

    @Override
    public void clear() {
        List<MQuad> buffer = new ArrayList<>();
        try {
            doClear(buffer);
        } catch (UnsupportedNodeType exception) {
            Logger.DEFAULT.error(exception);
        }
        if (!buffer.isEmpty()) {
            Changeset changeset = Changeset.fromRemoved((Collection) buffer);
            for (ChangeListener listener : listeners) {
                listener.onChange(changeset);
            }
        }
    }

    @Override
    public void clear(GraphNode graph) throws UnsupportedNodeType {
        if (graph == null) {
            clear();
            return;
        }
        List<MQuad> buffer = new ArrayList<>();
        doClear(graph, buffer);
        if (!buffer.isEmpty()) {
            Changeset changeset = Changeset.fromRemoved((Collection) buffer);
            for (ChangeListener listener : listeners) {
                listener.onChange(changeset);
            }
        }
    }

    @Override
    public void copy(GraphNode origin, GraphNode target, boolean overwrite) throws UnsupportedNodeType {
        if (RDFUtils.same(origin, target))
            return;
        List<MQuad> bufferOld = new ArrayList<>();
        List<MQuad> bufferNew = new ArrayList<>();
        doCopy(origin, target, bufferOld, bufferNew, overwrite);
        if (!bufferOld.isEmpty() || !bufferNew.isEmpty()) {
            Changeset changeset = Changeset.fromAddedRemoved((Collection) bufferNew, (Collection) bufferOld);
            for (ChangeListener listener : listeners) {
                listener.onChange(changeset);
            }
        }
    }

    @Override
    public void move(GraphNode origin, GraphNode target) throws UnsupportedNodeType {
        if (RDFUtils.same(origin, target))
            return;
        List<MQuad> bufferOld = new ArrayList<>();
        List<MQuad> bufferNew = new ArrayList<>();
        doMove(origin, target, bufferOld, bufferNew);
        if (!bufferOld.isEmpty() || !bufferNew.isEmpty()) {
            Changeset changeset = Changeset.fromAddedRemoved((Collection) bufferNew, (Collection) bufferOld);
            for (ChangeListener listener : listeners) {
                listener.onChange(changeset);
            }
        }
    }

    /**
     * Executes the insertion of a single instance of a quad into this store.
     * If the quad is already in the store, its multiplicity is increased.
     *
     * @param graph    The graph containing the quad
     * @param subject  The quad subject node
     * @param property The quad property
     * @param value    The quad value
     * @return The operation result
     * @throws UnsupportedNodeType When a specified node is unsupported
     */
    public abstract int doAddQuad(GraphNode graph, SubjectNode subject, Property property, Node value) throws UnsupportedNodeType;

    /**
     * Executes the removal operation of a single instance of a quad from this store
     *
     * @param graph    The graph containing the quad
     * @param subject  The quad subject node
     * @param property The quad property
     * @param value    The quad value
     * @return The operation result
     * @throws UnsupportedNodeType When a specified node is unsupported
     */
    public abstract int doRemoveQuad(GraphNode graph, SubjectNode subject, Property property, Node value) throws UnsupportedNodeType;

    /**
     * Executes the removal operation of quads matching the specified elements from this store
     *
     * @param graph             The graph containing the quad
     * @param subject           The quad subject node
     * @param property          The quad property
     * @param value             The quad value
     * @param bufferDecremented The buffer for the decremented quads
     * @param bufferRemoved     The buffer for the removed quads
     * @throws UnsupportedNodeType When a specified node is unsupported
     */
    public abstract void doRemoveQuads(GraphNode graph, SubjectNode subject, Property property, Node value, List<MQuad> bufferDecremented, List<MQuad> bufferRemoved) throws UnsupportedNodeType;

    /**
     * Executes the clear operation removing all quads from this store
     *
     * @param buffer The buffer for the removed quads
     * @throws UnsupportedNodeType When a specified node is unsupported
     */
    public abstract void doClear(List<MQuad> buffer) throws UnsupportedNodeType;

    /**
     * Executes the clear operation removing all quads from this store for the specified graph
     *
     * @param graph  The graph to clear
     * @param buffer The buffer for the removed quads
     * @throws UnsupportedNodeType When a specified node is unsupported
     */
    public abstract void doClear(GraphNode graph, List<MQuad> buffer) throws UnsupportedNodeType;

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
     * @throws UnsupportedNodeType When a specified node is unsupported
     */
    public abstract void doCopy(GraphNode origin, GraphNode target, List<MQuad> bufferOld, List<MQuad> bufferNew, boolean overwrite) throws UnsupportedNodeType;

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
     * @throws UnsupportedNodeType When a specified node is unsupported
     */
    public abstract void doMove(GraphNode origin, GraphNode target, List<MQuad> bufferOld, List<MQuad> bufferNew) throws UnsupportedNodeType;
}
