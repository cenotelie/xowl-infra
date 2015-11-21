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
import org.xowl.utils.collections.LockingIterator;
import org.xowl.utils.concurrent.TrackedReentrantLock;

import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Represents a data store that adds thread safety over an existing backend store
 *
 * @author Laurent Wouters
 */
class BaseMTSafeStore extends BaseStore {
    /**
     * The backend store
     */
    private final BaseStore backend;
    /**
     * Global lock for this store used to prevent concurrent access
     */
    private final ReentrantLock globalLock;

    /**
     * Initializes this store
     *
     * @param backend The backend store
     */
    public BaseMTSafeStore(BaseStore backend) {
        this.backend = backend;
        this.globalLock = new TrackedReentrantLock();
    }

    @Override
    public boolean commit() {
        globalLock.lock();
        try {
            return backend.commit();
        } finally {
            globalLock.unlock();
        }
    }

    @Override
    public boolean rollback() {
        globalLock.lock();
        try {
            return backend.rollback();
        } finally {
            globalLock.unlock();
        }
    }

    @Override
    public void close() throws Exception {
        globalLock.lock();
        try {
            backend.close();
        } finally {
            globalLock.unlock();
        }
    }

    @Override
    public void addListener(ChangeListener listener) {
        globalLock.lock();
        try {
            backend.addListener(listener);
        } finally {
            globalLock.unlock();
        }
    }

    @Override
    public void removeListener(ChangeListener listener) {
        globalLock.lock();
        try {
            backend.removeListener(listener);
        } finally {
            globalLock.unlock();
        }
    }

    @Override
    public long getMultiplicity(Quad quad) {
        globalLock.lock();
        try {
            return backend.getMultiplicity(quad);
        } finally {
            globalLock.unlock();
        }
    }

    @Override
    public long getMultiplicity(GraphNode graph, SubjectNode subject, Property property, Node object) {
        globalLock.lock();
        try {
            return backend.getMultiplicity(graph, subject, property, object);
        } finally {
            globalLock.unlock();
        }
    }

    @Override
    public Iterator<Quad> getAll() {
        globalLock.lock();
        return new LockingIterator<>(backend.getAll(), globalLock);
    }

    @Override
    public Iterator<Quad> getAll(GraphNode graph) {
        globalLock.lock();
        return new LockingIterator<>(backend.getAll(graph), globalLock);
    }

    @Override
    public Iterator<Quad> getAll(SubjectNode subject, Property property, Node object) {
        globalLock.lock();
        return new LockingIterator<>(backend.getAll(subject, property, object), globalLock);
    }

    @Override
    public Iterator<Quad> getAll(GraphNode graph, SubjectNode subject, Property property, Node object) {
        globalLock.lock();
        return new LockingIterator<>(backend.getAll(graph, subject, property, object), globalLock);
    }

    @Override
    public Collection<GraphNode> getGraphs() {
        globalLock.lock();
        try {
            return backend.getGraphs();
        } finally {
            globalLock.unlock();
        }
    }

    @Override
    public long count() {
        globalLock.lock();
        try {
            return backend.count();
        } finally {
            globalLock.unlock();
        }
    }

    @Override
    public long count(GraphNode graph) {
        globalLock.lock();
        try {
            return backend.count(graph);
        } finally {
            globalLock.unlock();
        }
    }

    @Override
    public long count(SubjectNode subject, Property property, Node object) {
        globalLock.lock();
        try {
            return backend.count(subject, property, object);
        } finally {
            globalLock.unlock();
        }
    }

    @Override
    public long count(GraphNode graph, SubjectNode subject, Property property, Node object) {
        globalLock.lock();
        try {
            return backend.count(graph, subject, property, object);
        } finally {
            globalLock.unlock();
        }
    }


    @Override
    public void insert(Changeset changeset) throws UnsupportedNodeType {
        globalLock.lock();
        try {
            backend.insert(changeset);
        } finally {
            globalLock.unlock();
        }
    }

    @Override
    public void add(Quad quad) throws UnsupportedNodeType {
        globalLock.lock();
        try {
            backend.add(quad);
        } finally {
            globalLock.unlock();
        }
    }

    @Override
    public void add(GraphNode graph, SubjectNode subject, Property property, Node value) throws UnsupportedNodeType {
        globalLock.lock();
        try {
            backend.add(graph, subject, property, value);
        } finally {
            globalLock.unlock();
        }
    }

    @Override
    public void remove(Quad quad) throws UnsupportedNodeType {
        globalLock.lock();
        try {
            backend.remove(quad);
        } finally {
            globalLock.unlock();
        }
    }

    @Override
    public void remove(GraphNode graph, SubjectNode subject, Property property, Node value) throws UnsupportedNodeType {
        globalLock.lock();
        try {
            backend.remove(graph, subject, property, value);
        } finally {
            globalLock.unlock();
        }
    }

    @Override
    public void clear() {
        globalLock.lock();
        try {
            backend.clear();
        } finally {
            globalLock.unlock();
        }
    }

    @Override
    public void clear(GraphNode graph) {
        globalLock.lock();
        try {
            backend.clear(graph);
        } finally {
            globalLock.unlock();
        }
    }

    @Override
    public void copy(GraphNode origin, GraphNode target, boolean overwrite) {
        globalLock.lock();
        try {
            backend.copy(origin, target, overwrite);
        } finally {
            globalLock.unlock();
        }
    }

    @Override
    public void move(GraphNode origin, GraphNode target) {
        globalLock.lock();
        try {
            backend.move(origin, target);
        } finally {
            globalLock.unlock();
        }
    }

    @Override
    public IRINode getIRINode(GraphNode graph) {
        globalLock.lock();
        try {
            return backend.getIRINode(graph);
        } finally {
            globalLock.unlock();
        }
    }

    @Override
    public IRINode getIRINode(String iri) {
        globalLock.lock();
        try {
            return backend.getIRINode(iri);
        } finally {
            globalLock.unlock();
        }
    }

    @Override
    public IRINode getExistingIRINode(String iri) {
        globalLock.lock();
        try {
            return backend.getExistingIRINode(iri);
        } finally {
            globalLock.unlock();
        }
    }

    @Override
    public BlankNode getBlankNode() {
        globalLock.lock();
        try {
            return backend.getBlankNode();
        } finally {
            globalLock.unlock();
        }
    }

    @Override
    public LiteralNode getLiteralNode(String lex, String datatype, String lang) {
        globalLock.lock();
        try {
            return backend.getLiteralNode(lex, datatype, lang);
        } finally {
            globalLock.unlock();
        }
    }

    @Override
    public AnonymousNode getAnonNode(AnonymousIndividual individual) {
        globalLock.lock();
        try {
            return backend.getAnonNode(individual);
        } finally {
            globalLock.unlock();
        }
    }
}
