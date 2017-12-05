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

package org.xowl.infra.store.storage;

import org.xowl.infra.store.RDFUtils;
import org.xowl.infra.store.Vocabulary;
import org.xowl.infra.store.execution.ExecutionManager;
import org.xowl.infra.store.rdf.*;

import java.util.*;

/**
 * Base implementation of a set of RDF quads
 *
 * @author Laurent Wouters
 */
public abstract class DatasetQuadsImpl implements DatasetQuads {
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
     * The execution manager to use
     */
    protected ExecutionManager executionManager;

    /**
     * Initializes this dataset
     */
    public DatasetQuadsImpl() {
        listeners = new ArrayList<>();
    }

    /**
     * Sets the execution manager to use
     *
     * @param parent           The parent dataset
     * @param executionManager The execution manager to use
     */
    public void setExecutionManager(Dataset parent, ExecutionManager executionManager) {
        this.executionManager = executionManager;
        if (executionManager != null) {
            IRINode definedAs = parent.getIRINode(Vocabulary.xowlDefinedAs);
            Iterator<? extends Quad> iterator = getAll(null, definedAs, null);
            while (iterator.hasNext()) {
                Quad quad = iterator.next();
                if (isFunctionDefinition(quad.getSubject(), quad.getProperty(), quad.getObject()))
                    registerFunctionDefinition((IRINode) quad.getSubject(), (DynamicNode) quad.getObject());
            }
        }
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
    public long getMultiplicity(Quad quad) {
        return getMultiplicity(quad.getGraph(), quad.getSubject(), quad.getProperty(), quad.getObject());
    }

    @Override
    public Iterator<? extends Quad> getAll() {
        return getAll(null, null, null, null);
    }

    @Override
    public Iterator<? extends Quad> getAll(GraphNode graph) {
        return getAll(graph, null, null, null);
    }

    @Override
    public Iterator<? extends Quad> getAll(SubjectNode subject, Property property, Node object) {
        return getAll(null, subject, property, object);
    }

    @Override
    public long count() {
        return count(null, null, null, null);
    }

    @Override
    public long count(GraphNode graph) {
        return count(graph, null, null, null);
    }

    @Override
    public long count(SubjectNode subject, Property property, Node object) {
        return count(null, subject, property, object);
    }

    @Override
    public void insert(Changeset changeset) {
        Collection<Quad> incremented = new ArrayList<>();
        Collection<Quad> decremented = new ArrayList<>();
        Collection<Quad> added = new ArrayList<>();
        Collection<Quad> removed = new ArrayList<>();
        for (Quad quad : changeset.getAdded()) {
            int result = doAddQuad(quad.getGraph(), quad.getSubject(), quad.getProperty(), quad.getObject());
            if (result == ADD_RESULT_NEW) {
                added.add(quad);
                if (isFunctionDefinition(quad.getSubject(), quad.getProperty(), quad.getObject()))
                    registerFunctionDefinition((IRINode) quad.getSubject(), (DynamicNode) quad.getObject());
            } else if (result == ADD_RESULT_INCREMENT) {
                incremented.add(quad);
            }
        }
        for (Quad quad : changeset.getRemoved()) {
            int result = doRemoveQuad(quad.getGraph(), quad.getSubject(), quad.getProperty(), quad.getObject());
            if (result >= REMOVE_RESULT_REMOVED) {
                if (isFunctionDefinition(quad.getSubject(), quad.getProperty(), quad.getObject()))
                    unregisterFunctionDefinition((IRINode) quad.getSubject());
                removed.add(quad);
            } else if (result == REMOVE_RESULT_DECREMENT) {
                decremented.add(quad);
            }
        }
        if (!incremented.isEmpty() || !decremented.isEmpty() || !added.isEmpty() || !removed.isEmpty()) {
            // transmit the changes only if a there are some!
            Changeset newChangeset = new Changeset(incremented, decremented, added, removed);
            for (ChangeListener listener : listeners)
                listener.onChange(newChangeset);
        }
    }

    @Override
    public void add(Quad quad) {
        int result = doAddQuad(quad.getGraph(), quad.getSubject(), quad.getProperty(), quad.getObject());
        if (result < DatasetQuadsImpl.ADD_RESULT_INCREMENT)
            return;
        if (result >= DatasetQuadsImpl.ADD_RESULT_NEW) {
            if (isFunctionDefinition(quad.getSubject(), quad.getProperty(), quad.getObject()))
                registerFunctionDefinition((IRINode) quad.getSubject(), (DynamicNode) quad.getObject());
            for (ChangeListener listener : listeners)
                listener.onAdded(quad);
        } else {
            for (ChangeListener listener : listeners)
                listener.onIncremented(quad);
        }
    }

    @Override
    public void add(GraphNode graph, SubjectNode subject, Property property, Node value) {
        int result = doAddQuad(graph, subject, property, value);
        Quad quad = new Quad(graph, subject, property, value);
        if (result < DatasetQuadsImpl.ADD_RESULT_INCREMENT)
            return;
        if (result >= DatasetQuadsImpl.ADD_RESULT_NEW) {
            if (isFunctionDefinition(subject, property, value))
                registerFunctionDefinition((IRINode) subject, (DynamicNode) value);
            for (ChangeListener listener : listeners)
                listener.onAdded(quad);
        } else {
            for (ChangeListener listener : listeners)
                listener.onIncremented(quad);
        }
    }

    @Override
    public void remove(Quad quad) {
        if (quad.getGraph() != null && quad.getSubject() != null && quad.getProperty() != null && quad.getObject() != null) {
            // remove a single quad
            int result = doRemoveQuad(quad.getGraph(), quad.getSubject(), quad.getProperty(), quad.getObject());
            if (result < DatasetQuadsImpl.REMOVE_RESULT_DECREMENT)
                return;
            if (result >= DatasetQuadsImpl.REMOVE_RESULT_REMOVED) {
                if (isFunctionDefinition(quad.getSubject(), quad.getProperty(), quad.getObject()))
                    unregisterFunctionDefinition((IRINode) quad.getSubject());
                for (ChangeListener listener : listeners)
                    listener.onRemoved(quad);
            } else {
                for (ChangeListener listener : listeners)
                    listener.onDecremented(quad);
            }
        } else {
            List<MQuad> bufferDecremented = new ArrayList<>();
            List<MQuad> bufferRemoved = new ArrayList<>();
            doRemoveQuads(quad.getGraph(), quad.getSubject(), quad.getProperty(), quad.getObject(), bufferDecremented, bufferRemoved);
            if (!bufferDecremented.isEmpty() || !bufferRemoved.isEmpty()) {
                for (Quad removed : bufferRemoved) {
                    if (isFunctionDefinition(removed.getSubject(), removed.getProperty(), removed.getObject()))
                        unregisterFunctionDefinition((IRINode) removed.getSubject());
                }
                Changeset changeset = new Changeset(Collections.emptyList(), Collections.emptyList(), bufferDecremented, bufferRemoved);
                for (ChangeListener listener : listeners) {
                    listener.onChange(changeset);
                }
            }
        }
    }

    @Override
    public void remove(GraphNode graph, SubjectNode subject, Property property, Node value) {
        if (graph != null && subject != null && property != null && value != null) {
            // remove a single quad
            int result = doRemoveQuad(graph, subject, property, value);
            if (result < DatasetQuadsImpl.REMOVE_RESULT_DECREMENT)
                return;
            Quad quad = new Quad(graph, subject, property, value);
            if (result >= DatasetQuadsImpl.REMOVE_RESULT_REMOVED) {
                if (isFunctionDefinition(subject, property, value))
                    unregisterFunctionDefinition((IRINode) subject);
                for (ChangeListener listener : listeners)
                    listener.onRemoved(quad);
            } else {
                for (ChangeListener listener : listeners)
                    listener.onDecremented(quad);
            }
        } else {
            List<MQuad> bufferDecremented = new ArrayList<>();
            List<MQuad> bufferRemoved = new ArrayList<>();
            doRemoveQuads(graph, subject, property, value, bufferDecremented, bufferRemoved);
            if (!bufferDecremented.isEmpty() || !bufferRemoved.isEmpty()) {
                for (Quad removed : bufferRemoved) {
                    if (isFunctionDefinition(removed.getSubject(), removed.getProperty(), removed.getObject()))
                        unregisterFunctionDefinition((IRINode) removed.getSubject());
                }
                Changeset changeset = new Changeset(Collections.emptyList(), Collections.emptyList(), bufferDecremented, bufferRemoved);
                for (ChangeListener listener : listeners) {
                    listener.onChange(changeset);
                }
            }
        }
    }

    @Override
    public void clear() {
        List<MQuad> buffer = new ArrayList<>();
        doClear(buffer);
        if (!buffer.isEmpty()) {
            for (Quad removed : buffer) {
                if (isFunctionDefinition(removed.getSubject(), removed.getProperty(), removed.getObject()))
                    unregisterFunctionDefinition((IRINode) removed.getSubject());
            }
            Changeset changeset = Changeset.fromRemoved(buffer);
            for (ChangeListener listener : listeners) {
                listener.onChange(changeset);
            }
        }
    }

    @Override
    public void clear(GraphNode graph) {
        if (graph == null) {
            clear();
            return;
        }
        List<MQuad> buffer = new ArrayList<>();
        doClear(graph, buffer);
        if (!buffer.isEmpty()) {
            for (Quad removed : buffer) {
                if (isFunctionDefinition(removed.getSubject(), removed.getProperty(), removed.getObject()))
                    unregisterFunctionDefinition((IRINode) removed.getSubject());
            }
            Changeset changeset = Changeset.fromRemoved(buffer);
            for (ChangeListener listener : listeners) {
                listener.onChange(changeset);
            }
        }
    }

    @Override
    public void copy(GraphNode origin, GraphNode target, boolean overwrite) {
        if (RDFUtils.same(origin, target))
            return;
        List<MQuad> bufferOld = new ArrayList<>();
        List<MQuad> bufferNew = new ArrayList<>();
        doCopy(origin, target, bufferOld, bufferNew, overwrite);
        if (!bufferOld.isEmpty() || !bufferNew.isEmpty()) {
            Changeset changeset = Changeset.fromAddedRemoved(bufferNew, bufferOld);
            for (ChangeListener listener : listeners) {
                listener.onChange(changeset);
            }
        }
    }

    @Override
    public void move(GraphNode origin, GraphNode target) {
        if (RDFUtils.same(origin, target))
            return;
        List<MQuad> bufferOld = new ArrayList<>();
        List<MQuad> bufferNew = new ArrayList<>();
        doMove(origin, target, bufferOld, bufferNew);
        if (!bufferOld.isEmpty() || !bufferNew.isEmpty()) {
            Changeset changeset = Changeset.fromAddedRemoved(bufferNew, bufferOld);
            for (ChangeListener listener : listeners) {
                listener.onChange(changeset);
            }
        }
    }

    /**
     * Determines whether the given triple defines a xOWL function
     *
     * @param subject  The triple's subject
     * @param property The triples's property
     * @param object   The triples's object
     * @return Whether the triple defines a xOWL function
     */
    protected boolean isFunctionDefinition(SubjectNode subject, Property property, Node object) {
        return (object.getNodeType() == Node.TYPE_DYNAMIC
                && property.getNodeType() == Node.TYPE_IRI
                && subject.getNodeType() == Node.TYPE_IRI
                && Vocabulary.xowlDefinedAs.equals(((IRINode) property).getIRIValue()));
    }

    /**
     * Registers a xOWL function
     *
     * @param subject The function as a subject node
     * @param object  The function's definition as a dynamic node
     */
    protected void registerFunctionDefinition(IRINode subject, DynamicNode object) {
        if (executionManager != null)
            executionManager.registerFunction(subject.getIRIValue(), object.getEvaluable());
    }

    /**
     * Unregisters a xOWL function
     *
     * @param subject The function as a subject node
     */
    protected void unregisterFunctionDefinition(IRINode subject) {
        if (executionManager != null)
            executionManager.unregisterFunction(subject.getIRIValue());
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
     */
    public abstract int doAddQuad(GraphNode graph, SubjectNode subject, Property property, Node value);

    /**
     * Executes the removal operation of a single instance of a quad from this store
     *
     * @param graph    The graph containing the quad
     * @param subject  The quad subject node
     * @param property The quad property
     * @param value    The quad value
     * @return The operation result
     */
    public abstract int doRemoveQuad(GraphNode graph, SubjectNode subject, Property property, Node value);

    /**
     * Executes the removal operation of quads matching the specified elements from this store
     *
     * @param graph             The graph containing the quad
     * @param subject           The quad subject node
     * @param property          The quad property
     * @param value             The quad value
     * @param bufferDecremented The buffer for the decremented quads
     * @param bufferRemoved     The buffer for the removed quads
     */
    public abstract void doRemoveQuads(GraphNode graph, SubjectNode subject, Property property, Node value, List<MQuad> bufferDecremented, List<MQuad> bufferRemoved);

    /**
     * Executes the clear operation removing all quads from this store
     *
     * @param buffer The buffer for the removed quads
     */
    public abstract void doClear(List<MQuad> buffer);

    /**
     * Executes the clear operation removing all quads from this store for the specified graph
     *
     * @param graph  The graph to clear
     * @param buffer The buffer for the removed quads
     */
    public abstract void doClear(GraphNode graph, List<MQuad> buffer);

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
     */
    public abstract void doCopy(GraphNode origin, GraphNode target, List<MQuad> bufferOld, List<MQuad> bufferNew, boolean overwrite);

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
     */
    public abstract void doMove(GraphNode origin, GraphNode target, List<MQuad> bufferOld, List<MQuad> bufferNew);
}
