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

import org.xowl.lang.owl2.AnonymousIndividual;
import org.xowl.store.owl.AnonymousNode;
import org.xowl.store.rdf.*;
import org.xowl.store.storage.persistent.PersistedDataset;
import org.xowl.store.storage.persistent.PersistedNodes;
import org.xowl.store.storage.persistent.StorageException;
import org.xowl.utils.collections.LockingIterator;
import org.xowl.utils.concurrent.TrackedReentrantLock;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Concrete implementation of a persisted data store.
 * This implementation delegates all its behavior to persisted stores.
 * This class is NOT thread safe.
 *
 * @author Laurent Wouters
 */
class OnDiskStore implements BaseStore {
    /**
     * The store for the nodes
     */
    private final PersistedNodes nodes;
    /**
     * The store for the dataset
     */
    private final PersistedDataset dataset;
    /**
     * Global lock for this store used to prevent concurrent access
     */
    private final ReentrantLock globalLock;

    /**
     * Initializes this store
     *
     * @param directory  The parent directory containing the backing files
     * @param isReadonly Whether this store is in readonly mode
     * @throws IOException      When the backing files cannot be accessed
     * @throws StorageException When the storage is in a bad state
     */
    public OnDiskStore(File directory, boolean isReadonly) throws IOException, StorageException {
        nodes = new PersistedNodes(directory, isReadonly);
        dataset = new PersistedDataset(nodes, directory, isReadonly);
        globalLock = new TrackedReentrantLock();
    }

    @Override
    public boolean commit() {
        globalLock.lock();
        try {
            boolean success = nodes.commit();
            success &= dataset.commit();
            return success;
        } finally {
            globalLock.unlock();
        }
    }

    @Override
    public boolean rollback() {
        globalLock.lock();
        try {
            boolean success = nodes.rollback();
            success &= dataset.rollback();
            return success;
        } finally {
            globalLock.unlock();
        }
    }

    @Override
    public void addListener(ChangeListener listener) {
        globalLock.lock();
        try {
            dataset.addListener(listener);
        } finally {
            globalLock.unlock();
        }
    }

    @Override
    public void removeListener(ChangeListener listener) {
        globalLock.lock();
        try {
            dataset.removeListener(listener);
        } finally {
            globalLock.unlock();
        }
    }

    @Override
    public long getMultiplicity(Quad quad) {
        globalLock.lock();
        try {
            return dataset.getMultiplicity(quad);
        } finally {
            globalLock.unlock();
        }
    }

    @Override
    public long getMultiplicity(GraphNode graph, SubjectNode subject, Property property, Node object) {
        globalLock.lock();
        try {
            return dataset.getMultiplicity(graph, subject, property, object);
        } finally {
            globalLock.unlock();
        }
    }

    @Override
    public Iterator<Quad> getAll() {
        globalLock.lock();
        return new LockingIterator<>(dataset.getAll(), globalLock);
    }

    @Override
    public Iterator<Quad> getAll(GraphNode graph) {
        globalLock.lock();
        return new LockingIterator<>(dataset.getAll(graph), globalLock);
    }

    @Override
    public Iterator<Quad> getAll(SubjectNode subject, Property property, Node object) {
        globalLock.lock();
        return new LockingIterator<>(dataset.getAll(subject, property, object), globalLock);
    }

    @Override
    public Iterator<Quad> getAll(GraphNode graph, SubjectNode subject, Property property, Node object) {
        globalLock.lock();
        return new LockingIterator<>(dataset.getAll(graph, subject, property, object), globalLock);
    }

    @Override
    public Collection<GraphNode> getGraphs() {
        globalLock.lock();
        try {
            return dataset.getGraphs();
        } finally {
            globalLock.unlock();
        }
    }

    @Override
    public long count() {
        globalLock.lock();
        try {
            return dataset.count();
        } finally {
            globalLock.unlock();
        }
    }

    @Override
    public long count(GraphNode graph) {
        globalLock.lock();
        try {
            return dataset.count(graph);
        } finally {
            globalLock.unlock();
        }
    }

    @Override
    public long count(SubjectNode subject, Property property, Node object) {
        globalLock.lock();
        try {
            return dataset.count(subject, property, object);
        } finally {
            globalLock.unlock();
        }
    }

    @Override
    public long count(GraphNode graph, SubjectNode subject, Property property, Node object) {
        globalLock.lock();
        try {
            return dataset.count(graph, subject, property, object);
        } finally {
            globalLock.unlock();
        }
    }


    @Override
    public void insert(Changeset changeset) throws UnsupportedNodeType {
        globalLock.lock();
        try {
            dataset.insert(changeset);
        } finally {
            globalLock.unlock();
        }
    }

    @Override
    public void add(Quad quad) throws UnsupportedNodeType {
        globalLock.lock();
        try {
            dataset.add(quad);
        } finally {
            globalLock.unlock();
        }
    }

    @Override
    public void add(GraphNode graph, SubjectNode subject, Property property, Node value) throws UnsupportedNodeType {
        globalLock.lock();
        try {
            dataset.add(graph, subject, property, value);
        } finally {
            globalLock.unlock();
        }
    }

    @Override
    public void remove(Quad quad) throws UnsupportedNodeType {
        globalLock.lock();
        try {
            dataset.remove(quad);
        } finally {
            globalLock.unlock();
        }
    }

    @Override
    public void remove(GraphNode graph, SubjectNode subject, Property property, Node value) throws UnsupportedNodeType {
        globalLock.lock();
        try {
            dataset.remove(graph, subject, property, value);
        } finally {
            globalLock.unlock();
        }
    }

    @Override
    public void clear() {
        globalLock.lock();
        try {
            dataset.clear();
        } finally {
            globalLock.unlock();
        }
    }

    @Override
    public void clear(GraphNode graph) {
        globalLock.lock();
        try {
            dataset.clear(graph);
        } finally {
            globalLock.unlock();
        }
    }

    @Override
    public void copy(GraphNode origin, GraphNode target, boolean overwrite) {
        globalLock.lock();
        try {
            dataset.copy(origin, target, overwrite);
        } finally {
            globalLock.unlock();
        }
    }

    @Override
    public void move(GraphNode origin, GraphNode target) {
        globalLock.lock();
        try {
            dataset.move(origin, target);
        } finally {
            globalLock.unlock();
        }
    }

    @Override
    public IRINode getIRINode(GraphNode graph) {
        globalLock.lock();
        try {
            return nodes.getIRINode(graph);
        } finally {
            globalLock.unlock();
        }
    }

    @Override
    public IRINode getIRINode(String iri) {
        globalLock.lock();
        try {
            return nodes.getIRINode(iri);
        } finally {
            globalLock.unlock();
        }
    }

    @Override
    public IRINode getExistingIRINode(String iri) {
        globalLock.lock();
        try {
            return nodes.getExistingIRINode(iri);
        } finally {
            globalLock.unlock();
        }
    }

    @Override
    public BlankNode getBlankNode() {
        globalLock.lock();
        try {
            return nodes.getBlankNode();
        } finally {
            globalLock.unlock();
        }
    }

    @Override
    public LiteralNode getLiteralNode(String lex, String datatype, String lang) {
        globalLock.lock();
        try {
            return nodes.getLiteralNode(lex, datatype, lang);
        } finally {
            globalLock.unlock();
        }
    }

    @Override
    public AnonymousNode getAnonNode(AnonymousIndividual individual) {
        globalLock.lock();
        try {
            return nodes.getAnonNode(individual);
        } finally {
            globalLock.unlock();
        }
    }

    @Override
    public void close() throws Exception {
        globalLock.lock();
        try {
            dataset.close();
            nodes.close();
        } finally {
            globalLock.unlock();
        }
    }
}
