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

package org.xowl.store.storage.persistent;

import org.xowl.store.rdf.*;
import org.xowl.store.storage.Dataset;
import org.xowl.store.storage.UnsupportedNodeType;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * Represents a persisted RDF dataset
 *
 * @author Laurent Wouters
 */
public class PersistedDataset implements Dataset, AutoCloseable {
    /**
     * The current listeners on this store
     */
    private final Collection<ChangeListener> listeners;

    /**
     * Initializes this dataset
     * @param directory The parent directory containing the backing files
     * @throws IOException      When the backing files cannot be accessed
     * @throws StorageException When the storage is in a bad state
     */
    public PersistedDataset(File directory) throws IOException, StorageException {
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
    public Iterator<Quad> getAll() {
        return getAll(null, null, null, null);
    }

    @Override
    public Iterator<Quad> getAll(GraphNode graph) {
        return getAll(graph, null, null, null);
    }

    @Override
    public Iterator<Quad> getAll(SubjectNode subject, Property property, Node object) {
        return getAll(null, subject, property, object);
    }

    @Override
    public Iterator<Quad> getAll(GraphNode graph, SubjectNode subject, Property property, Node object) {
        return null;
    }

    @Override
    public Collection<GraphNode> getGraphs() {
        return null;
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public long count(GraphNode graph) {
        return 0;
    }

    @Override
    public long count(SubjectNode subject, Property property, Node object) {
        return 0;
    }

    @Override
    public long count(GraphNode graph, SubjectNode subject, Property property, Node object) {
        return 0;
    }

    @Override
    public void insert(Changeset changeset) throws UnsupportedNodeType {

    }

    @Override
    public void add(Quad quad) throws UnsupportedNodeType {

    }

    @Override
    public void add(GraphNode graph, SubjectNode subject, Property property, Node value) throws UnsupportedNodeType {

    }

    @Override
    public void remove(Quad quad) throws UnsupportedNodeType {

    }

    @Override
    public void remove(GraphNode graph, SubjectNode subject, Property property, Node value) throws UnsupportedNodeType {

    }

    @Override
    public void clear() {

    }

    @Override
    public void clear(GraphNode graph) {

    }

    @Override
    public void copy(GraphNode origin, GraphNode target, boolean overwrite) {

    }

    @Override
    public void move(GraphNode origin, GraphNode target) {

    }

    @Override
    public void close() throws Exception {

    }
}
