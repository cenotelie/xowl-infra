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
import org.xowl.infra.utils.collections.ConcatenatedIterator;

import java.util.*;

/**
 * Implements a dataset that aggregates other datasets
 * This implementation assumes that the aggregated datasets are disjoint, meaning there are no common quads between them.
 * This class can be used, for example, when using different datasets to store quads from different graphs.
 * This class is primarily used by the BaseReasonableStore.
 *
 * @author Laurent Wouters
 */
class AggregateDataset implements Dataset {
    /**
     * The aggregated datasets
     */
    private final Dataset[] content;
    /**
     * The listeners
     */
    private final List<ChangeListener> listeners;

    /**
     * Initializes this dataset
     *
     * @param content The aggregated datasets
     */
    public AggregateDataset(Dataset... content) {
        this.content = Arrays.copyOf(content, content.length);
        this.listeners = new ArrayList<>();
        ChangeListener inner = new ChangeListener() {
            @Override
            public void onIncremented(Quad quad) {
                for (ChangeListener l : listeners)
                    l.onIncremented(quad);
            }

            @Override
            public void onDecremented(Quad quad) {
                for (ChangeListener l : listeners)
                    l.onDecremented(quad);
            }

            @Override
            public void onAdded(Quad quad) {
                for (ChangeListener l : listeners)
                    l.onAdded(quad);
            }

            @Override
            public void onRemoved(Quad quad) {
                for (ChangeListener l : listeners)
                    l.onRemoved(quad);
            }

            @Override
            public void onChange(Changeset changeset) {
                for (ChangeListener l : listeners)
                    l.onChange(changeset);
            }
        };
        for (Dataset dataset : this.content)
            dataset.addListener(inner);
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
        long result = 0;
        for (int i = 0; i != content.length; i++)
            result += content[i].getMultiplicity(quad);
        return result;
    }

    @Override
    public long getMultiplicity(GraphNode graph, SubjectNode subject, Property property, Node object) throws UnsupportedNodeType {
        long result = 0;
        for (int i = 0; i != content.length; i++)
            result += content[i].getMultiplicity(graph, subject, property, object);
        return result;
    }

    @Override
    public Iterator<Quad> getAll() {
        Iterator<Quad>[] iterators = new Iterator[content.length];
        for (int i = 0; i != content.length; i++)
            iterators[i] = content[i].getAll();
        return new ConcatenatedIterator<>(iterators);
    }

    @Override
    public Iterator<Quad> getAll(GraphNode graph) throws UnsupportedNodeType {
        Iterator<Quad>[] iterators = new Iterator[content.length];
        for (int i = 0; i != content.length; i++)
            iterators[i] = content[i].getAll(graph);
        return new ConcatenatedIterator<>(iterators);
    }

    @Override
    public Iterator<Quad> getAll(SubjectNode subject, Property property, Node object) throws UnsupportedNodeType {
        Iterator<Quad>[] iterators = new Iterator[content.length];
        for (int i = 0; i != content.length; i++)
            iterators[i] = content[i].getAll(subject, property, object);
        return new ConcatenatedIterator<>(iterators);
    }

    @Override
    public Iterator<Quad> getAll(GraphNode graph, SubjectNode subject, Property property, Node object) throws UnsupportedNodeType {
        Iterator<Quad>[] iterators = new Iterator[content.length];
        for (int i = 0; i != content.length; i++)
            iterators[i] = content[i].getAll(graph, subject, property, object);
        return new ConcatenatedIterator<>(iterators);
    }

    @Override
    public Collection<GraphNode> getGraphs() {
        Collection<GraphNode> result = new ArrayList<>();
        for (int i = 0; i != content.length; i++) {
            for (GraphNode candidate : content[i].getGraphs()) {
                if (!result.contains(candidate)) {
                    result.add(candidate);
                }
            }
        }
        return result;
    }

    @Override
    public long count() {
        long result = 0;
        for (int i = 0; i != content.length; i++)
            result += content[i].count();
        return result;
    }

    @Override
    public long count(GraphNode graph) throws UnsupportedNodeType {
        long result = 0;
        for (int i = 0; i != content.length; i++)
            result += content[i].count(graph);
        return result;
    }

    @Override
    public long count(SubjectNode subject, Property property, Node object) throws UnsupportedNodeType {
        long result = 0;
        for (int i = 0; i != content.length; i++)
            result += content[i].count(subject, property, object);
        return result;
    }

    @Override
    public long count(GraphNode graph, SubjectNode subject, Property property, Node object) throws UnsupportedNodeType {
        long result = 0;
        for (int i = 0; i != content.length; i++)
            result += content[i].count(graph, subject, property, object);
        return result;
    }

    @Override
    public void insert(Changeset changeset) throws UnsupportedNodeType {
        throw new UnsupportedOperationException("Cannot insert/remove from an aggregate dataset");
    }

    @Override
    public void add(Quad quad) throws UnsupportedNodeType {
        throw new UnsupportedOperationException("Cannot insert/remove from an aggregate dataset");
    }

    @Override
    public void add(GraphNode graph, SubjectNode subject, Property property, Node value) throws UnsupportedNodeType {
        throw new UnsupportedOperationException("Cannot insert/remove from an aggregate dataset");
    }

    @Override
    public void remove(Quad quad) throws UnsupportedNodeType {
        throw new UnsupportedOperationException("Cannot insert/remove from an aggregate dataset");
    }

    @Override
    public void remove(GraphNode graph, SubjectNode subject, Property property, Node value) throws UnsupportedNodeType {
        throw new UnsupportedOperationException("Cannot insert/remove from an aggregate dataset");
    }

    @Override
    public void clear() {
        for (int i = 0; i != content.length; i++)
            content[i].clear();
    }

    @Override
    public void clear(GraphNode graph) throws UnsupportedNodeType {
        for (int i = 0; i != content.length; i++)
            content[i].clear(graph);
    }

    @Override
    public void copy(GraphNode origin, GraphNode target, boolean overwrite) throws UnsupportedNodeType {
        for (int i = 0; i != content.length; i++)
            content[i].copy(origin, target, overwrite);
    }

    @Override
    public void move(GraphNode origin, GraphNode target) throws UnsupportedNodeType {
        for (int i = 0; i != content.length; i++)
            content[i].move(origin, target);
    }
}
