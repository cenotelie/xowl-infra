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

package org.xowl.infra.store.storage;

import org.xowl.infra.store.rdf.*;
import org.xowl.infra.store.storage.cache.CachedDataset;
import org.xowl.infra.store.storage.impl.DatasetImpl;
import org.xowl.infra.store.storage.impl.MQuad;
import org.xowl.infra.utils.collections.Adapter;
import org.xowl.infra.utils.collections.AdaptingIterator;
import org.xowl.infra.utils.collections.ConcatenatedIterator;
import org.xowl.infra.utils.collections.SkippableIterator;
import org.xowl.infra.utils.logging.Logging;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Implements a dataset that is an increment on an original one.
 *
 * @author Laurent Wouters
 */
class DiffDataset extends DatasetImpl {
    /**
     * The original dataset
     */
    private final DatasetImpl original;
    /**
     * The differentially positive quads
     */
    private CachedDataset diffPositives;
    /**
     * The differentially negative quads
     */
    private CachedDataset diffNegatives;
    /**
     * The size of the diff
     */
    private int size;

    /**
     * Initializes this dataset
     *
     * @param original The original dataset
     */
    public DiffDataset(DatasetImpl original) {
        this.original = original;
        this.size = 0;
    }

    /**
     * Gets the size of the diff represented by this dataset
     *
     * @return The size of the diff
     */
    public int getSize() {
        return size;
    }

    /**
     * Combines iterators to produce an iterator that represents the quads in this dataset
     *
     * @param base     The iterator coming from the original dataset
     * @param positive The iterator coming from the positive dataset
     * @return The combined iterator
     */
    private Iterator<Quad> combine(Iterator<Quad> base, Iterator<Quad> positive) {
        Iterator<Quad> result = base;
        if (diffNegatives != null) {
            result = new SkippableIterator<>(new AdaptingIterator<>(base, new Adapter<Quad>() {
                @Override
                public <X> Quad adapt(X element) {
                    MQuad quad = (MQuad) element;
                    try {
                        long mn = diffNegatives.getMultiplicity(quad);
                        long mp = diffPositives == null ? 0 : diffPositives.getMultiplicity(quad);
                        return (quad.modifyMultiplicity(mp - mn) <= 0) ? null : quad;
                    } catch (UnsupportedNodeType exception) {
                        Logging.getDefault().error(exception);
                        return quad;
                    }
                }
            }));
        }
        if (diffPositives != null) {
            Iterator<Quad> filteredPositive = new SkippableIterator<>(new AdaptingIterator<>(positive, new Adapter<Quad>() {
                @Override
                public <X> Quad adapt(X element) {
                    MQuad quad = (MQuad) element;
                    try {
                        long m = original.getMultiplicity(quad);
                        return (m > 0) ? null : quad;
                    } catch (UnsupportedNodeType exception) {
                        Logging.getDefault().error(exception);
                        return quad;
                    }
                }
            }));
            result = new ConcatenatedIterator<>(new Iterator[]{
                    result,
                    filteredPositive
            });
        }
        return result;
    }

    /**
     * Commits all outstanding changes to the origin dataset
     */
    public void commit() {
        Iterator<Quad> iterator;
        if (diffPositives != null) {
            iterator = diffPositives.getAll();
            while (iterator.hasNext()) {
                MQuad quad = (MQuad) iterator.next();
                for (int i = 0; i < quad.getMultiplicity(); i++) {
                    try {
                        original.add(quad);
                    } catch (UnsupportedNodeType exception) {
                        Logging.getDefault().error(exception);
                    }
                }
            }
            diffPositives.clear();
        }
        if (diffNegatives != null) {
            iterator = diffNegatives.getAll();
            while (iterator.hasNext()) {
                MQuad quad = (MQuad) iterator.next();
                for (int i = 0; i < quad.getMultiplicity(); i++) {
                    try {
                        original.remove(quad);
                    } catch (UnsupportedNodeType exception) {
                        Logging.getDefault().error(exception);
                    }
                }
            }
            diffNegatives.clear();
        }
        size = 0;
    }

    /**
     * Drops all outstanding changes
     */
    public void rollback() {
        diffPositives.clear();
        diffNegatives.clear();
        size = 0;
    }

    /**
     * Gets the changeset representing the differences between this dataset and the original one
     *
     * @return The changeset
     */
    public Changeset getChangeset() {
        List<Quad> added = new ArrayList<>();
        List<Quad> removed = new ArrayList<>();
        Iterator<Quad> iterator;
        if (diffPositives != null) {
            iterator = diffPositives.getAll();
            while (iterator.hasNext()) {
                MQuad quad = (MQuad) iterator.next();
                for (int i = 0; i < quad.getMultiplicity(); i++)
                    added.add(quad);
            }
        }
        if (diffNegatives != null) {
            iterator = diffNegatives.getAll();
            while (iterator.hasNext()) {
                MQuad quad = (MQuad) iterator.next();
                for (int i = 0; i < quad.getMultiplicity(); i++)
                    removed.add(quad);
            }
        }
        return Changeset.fromAddedRemoved(added, removed);
    }

    @Override
    public long getMultiplicity(GraphNode graph, SubjectNode subject, Property property, Node object) throws UnsupportedNodeType {
        long result = original.getMultiplicity(graph, subject, property, object);
        if (diffPositives != null)
            result += diffPositives.getMultiplicity(graph, subject, property, object);
        if (diffNegatives != null)
            result -= diffNegatives.getMultiplicity(graph, subject, property, object);
        return result;
    }

    @Override
    public Iterator<Quad> getAll(GraphNode graph, SubjectNode subject, Property property, Node object) throws UnsupportedNodeType {
        return combine(
                original.getAll(graph, subject, property, object),
                diffPositives == null ? null : diffPositives.getAll(graph, subject, property, object)
        );
    }

    @Override
    public Collection<GraphNode> getGraphs() {
        Collection<GraphNode> result = original.getGraphs();
        for (GraphNode supp : diffPositives.getGraphs()) {
            if (!result.contains(supp))
                result.add(supp);
        }
        return result;
    }

    @Override
    public long count(GraphNode graph, SubjectNode subject, Property property, Node object) throws UnsupportedNodeType {
        Iterator<Quad> iterator = getAll(graph, subject, property, object);
        long result = 0;
        while (iterator.hasNext()) {
            result++;
            iterator.next();
        }
        return result;
    }

    @Override
    public int doAddQuad(GraphNode graph, SubjectNode subject, Property property, Node value) throws UnsupportedNodeType {
        if (diffNegatives != null) {
            int result = diffNegatives.doRemoveQuad(graph, subject, property, value);
            if (result >= REMOVE_RESULT_REMOVED) {
                size--;
                long multiplicity = original.getMultiplicity(graph, subject, property, value);
                return multiplicity > 0 ? ADD_RESULT_INCREMENT : ADD_RESULT_NEW;
            } else if (result == REMOVE_RESULT_DECREMENT) {
                long m1 = original.getMultiplicity(graph, subject, property, value);
                long m2 = diffNegatives.getMultiplicity(graph, subject, property, value);
                return (m1 - m2 > 0) ? ADD_RESULT_NEW : ADD_RESULT_INCREMENT;
            }
        }
        if (diffPositives == null)
            diffPositives = new CachedDataset();
        long multiplicity = original.getMultiplicity(graph, subject, property, value);
        int result = diffPositives.doAddQuad(graph, subject, property, value);
        if (result == ADD_RESULT_NEW)
            size++;
        return (multiplicity == 0) ? result : ADD_RESULT_INCREMENT;
    }

    @Override
    public int doRemoveQuad(GraphNode graph, SubjectNode subject, Property property, Node value) throws UnsupportedNodeType {
        if (diffPositives != null) {
            int result = diffPositives.doRemoveQuad(graph, subject, property, value);
            if (result == REMOVE_RESULT_DECREMENT)
                return REMOVE_RESULT_DECREMENT;
            else if (result >= REMOVE_RESULT_REMOVED) {
                size--;
                long multiplicity = original.getMultiplicity(graph, subject, property, value);
                return multiplicity > 0 ? REMOVE_RESULT_DECREMENT : REMOVE_RESULT_REMOVED;
            }
        }
        if (diffNegatives == null)
            diffNegatives = new CachedDataset();
        long m1 = original.getMultiplicity(graph, subject, property, value);
        int result = diffNegatives.doAddQuad(graph, subject, property, value);
        if (result == ADD_RESULT_NEW) {
            size++;
            return (m1 - 1 > 0) ? REMOVE_RESULT_DECREMENT : REMOVE_RESULT_REMOVED;
        }
        long m2 = diffNegatives.getMultiplicity(graph, subject, property, value);
        return (m1 - m2 > 0) ? REMOVE_RESULT_DECREMENT : REMOVE_RESULT_REMOVED;
    }

    @Override
    public void doRemoveQuads(GraphNode graph, SubjectNode subject, Property property, Node value, List<MQuad> bufferDecremented, List<MQuad> bufferRemoved) throws UnsupportedNodeType {
        List<MQuad> toRemove = new ArrayList<>();
        Iterator<Quad> iterator = getAll(graph, subject, property, value);
        while (iterator.hasNext()) {
            toRemove.add((MQuad) iterator.next());
        }
        for (MQuad quad : toRemove) {
            int result = doRemoveQuad(quad.getGraph(), quad.getSubject(), quad.getProperty(), quad.getObject());
            if (result == REMOVE_RESULT_DECREMENT)
                bufferDecremented.add(quad);
            else if (result >= REMOVE_RESULT_REMOVED)
                bufferRemoved.add(quad);
        }
    }

    @Override
    public void doClear(List<MQuad> buffer) {
        try {
            int originalSize = buffer.size();
            Iterator<Quad> iterator = getAll(null, null, null, null);
            while (iterator.hasNext()) {
                buffer.add((MQuad) iterator.next());
            }
            for (int i = originalSize; i != buffer.size(); i++) {
                MQuad quad = buffer.get(i);
                for (int j = 0; j < quad.getMultiplicity(); j++)
                    doRemoveQuad(quad.getGraph(), quad.getSubject(), quad.getProperty(), quad.getObject());
            }
        } catch (UnsupportedNodeType exception) {
            // cannot happen
        }
    }

    @Override
    public void doClear(GraphNode graph, List<MQuad> buffer) throws UnsupportedNodeType {
        int originalSize = buffer.size();
        Iterator<Quad> iterator = getAll(graph, null, null, null);
        while (iterator.hasNext()) {
            buffer.add((MQuad) iterator.next());
        }
        for (int i = originalSize; i != buffer.size(); i++) {
            MQuad quad = buffer.get(i);
            for (int j = 0; j < quad.getMultiplicity(); j++)
                doRemoveQuad(quad.getGraph(), quad.getSubject(), quad.getProperty(), quad.getObject());
        }
    }

    @Override
    public void doCopy(GraphNode origin, GraphNode target, List<MQuad> bufferOld, List<MQuad> bufferNew, boolean overwrite) {
        // TODO: implement this
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void doMove(GraphNode origin, GraphNode target, List<MQuad> bufferOld, List<MQuad> bufferNew) {
        // TODO: implement this
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
