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

import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.xowl.store.rdf.*;
import org.xowl.store.storage.Dataset;
import org.xowl.store.storage.UnsupportedNodeType;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

/**
 * Represents a persisted RDF dataset
 *
 * @author Laurent Wouters
 */
public class PersistedDataset implements Dataset, AutoCloseable {
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
     * The suffix for the index file
     */
    private static final String FILE_DATA = "quads_data.bin";
    /**
     * The suffix for the index file
     */
    private static final String FILE_INDEX = "quads_index.bin";
    /**
     * The size in bytes of a quad entry
     */
    private static final int QUAD_ENTRY_SIZE = 28;

    /**
     * The current listeners on this store
     */
    private final Collection<ChangeListener> listeners;
    /**
     * The persisted nodes associated to this dataset
     */
    private final PersistedNodes nodes;
    /**
     * The backing storing the nodes' data
     */
    private final FileStore backend;
    /**
     * The database backing the index
     */
    private final DB database;
    /**
     * The subject map for IRIs
     */
    private final Map<Long, Long> mapSubjectIRI;
    /**
     * The subject map for Blanks
     */
    private final Map<Long, Long> mapSubjectBlank;
    /**
     * The subject map for Anons
     */
    private final Map<Long, Long> mapSubjectAnon;

    /**
     * Initializes this dataset
     *
     * @param nodes     The persisted nodes associated to this dataset
     * @param directory The parent directory containing the backing files
     * @throws IOException      When the backing files cannot be accessed
     * @throws StorageException When the storage is in a bad state
     */
    public PersistedDataset(PersistedNodes nodes, File directory) throws IOException, StorageException {
        this.listeners = new ArrayList<>();
        this.nodes = nodes;
        this.backend = new FileStore(directory, FILE_DATA);
        this.database = DBMaker.fileDB(new File(directory, FILE_INDEX)).make();
        this.mapSubjectIRI = database.hashMap("subject-iris");
        this.mapSubjectBlank = database.hashMap("subject-blanks");
        this.mapSubjectAnon = database.hashMap("subject-anons");
    }

    /**
     * Gets the subject map for the specified subject
     *
     * @param subject A quad subject
     * @return The appropriate map
     * @throws UnsupportedNodeType When the subject node is not recognized
     */
    private Map<Long, Long> mapFor(PersistedNode subject) throws UnsupportedNodeType {
        switch (subject.getNodeType()) {
            case Node.TYPE_IRI:
                return mapSubjectIRI;
            case Node.TYPE_BLANK:
                return mapSubjectBlank;
            case Node.TYPE_ANONYMOUS:
                return mapSubjectAnon;
        }
        throw new UnsupportedOperationException("Subject node must be IRI, Blank or Anonymous");
    }

    /**
     * Inserts a quad into the backend
     *
     * @param subject  The subject
     * @param property The property
     * @param object   The object
     * @param graph    The graph
     * @return The result of the insertion
     * @throws UnsupportedNodeType When the subject node is not recognized
     */
    private int doAdd(Node subject, Node property, Node object, Node graph) throws UnsupportedNodeType {
        return doAdd(nodes.persist(subject),
                nodes.persist(property),
                nodes.persist(object),
                nodes.persist(graph)
        );
    }

    /**
     * Inserts a quad into the backend
     *
     * @param subject  The subject
     * @param property The property
     * @param object   The object
     * @param graph    The graph
     * @return The result of the insertion
     * @throws UnsupportedNodeType When the subject node is not recognized
     */
    private int doAdd(PersistedNode subject, PersistedNode property, PersistedNode object, PersistedNode graph) throws UnsupportedNodeType {
        try {
            Map<Long, Long> map = mapFor(subject);
            Long bucket = map.get(subject.getKey());
            if (bucket == null) {
                bucket = newEntry(subject);
                map.put(subject.getKey(), bucket);
            }
            long target = lookup(bucket, property, true);
            target = lookup(target, object, true);
            target = lookup(target, graph, true);
            try (IOElement entry = backend.access(target)) {
                long value = entry.seek(QUAD_ENTRY_SIZE - 8).readLong();
                if (value == PersistedNode.KEY_NOT_PRESENT) {
                    entry.seek(QUAD_ENTRY_SIZE - 8).writeLong(1);
                    return ADD_RESULT_NEW;
                } else {
                    value++;
                    entry.seek(QUAD_ENTRY_SIZE - 8).writeLong(value);
                    return ADD_RESULT_INCREMENT;
                }
            }
        } catch (IOException | StorageException exception) {
            // do nothing
            return ADD_RESULT_UNKNOWN;
        }
    }

    /**
     * Removes a quad into the backend
     *
     * @param subject  The subject
     * @param property The property
     * @param object   The object
     * @param graph    The graph
     * @return The result of the removal
     * @throws UnsupportedNodeType When the subject node is not recognized
     */
    private int doRemove(Node subject, Node property, Node object, Node graph) throws UnsupportedNodeType {
        return doRemove(nodes.persist(subject),
                nodes.persist(property),
                nodes.persist(object),
                nodes.persist(graph)
        );
    }

    /**
     * Removes a quad into the backend
     *
     * @param subject  The subject
     * @param property The property
     * @param object   The object
     * @param graph    The graph
     * @return The result of the removal
     * @throws UnsupportedNodeType When the subject node is not recognized
     */
    private int doRemove(PersistedNode subject, PersistedNode property, PersistedNode object, PersistedNode graph) throws UnsupportedNodeType {
        try {
            Map<Long, Long> map = mapFor(subject);
            Long bucket = map.get(subject.getKey());
            if (bucket == null) {
                return REMOVE_RESULT_NOT_FOUND;
            }
            long target = lookup(bucket, property, false);
            if (target == PersistedNode.KEY_NOT_PRESENT)
                return REMOVE_RESULT_NOT_FOUND;
            target = lookup(target, object, false);
            if (target == PersistedNode.KEY_NOT_PRESENT)
                return REMOVE_RESULT_NOT_FOUND;
            target = lookup(target, graph, false);
            if (target == PersistedNode.KEY_NOT_PRESENT)
                return REMOVE_RESULT_NOT_FOUND;
            try (IOElement entry = backend.access(target)) {
                long value = entry.seek(QUAD_ENTRY_SIZE - 8).readLong();
                value--;
                if (value > 0) {
                    entry.seek(QUAD_ENTRY_SIZE - 8).writeLong(value);
                    return REMOVE_RESULT_DECREMENT;
                }
                // TODO: remove the parent nodes
                return REMOVE_RESULT_REMOVED;
            }
        } catch (IOException | StorageException exception) {
            // do nothing
            return REMOVE_RESULT_NOT_FOUND;
        }
    }

    /**
     * Lookup the target entry for a quad node
     *
     * @param from    Key to the parent entry
     * @param node    The node quad node to resolve
     * @param resolve Whether to create the entry if it does not exist
     * @return The key to the resolved quad node
     * @throws IOException      When an IO operation failed
     * @throws StorageException When the page version does not match the expected one
     */
    private long lookup(long from, PersistedNode node, boolean resolve) throws IOException, StorageException {
        long current;
        try (IOElement entry = backend.read(from)) {
            current = entry.seek(QUAD_ENTRY_SIZE - 8).readLong();
        }
        if (current == PersistedNode.KEY_NOT_PRESENT) {
            if (!resolve)
                return PersistedNode.KEY_NOT_PRESENT;
            // not here
            current = newEntry(node);
            try (IOElement entry = backend.access(from)) {
                entry.seek(QUAD_ENTRY_SIZE - 8).writeLong(current);
            }
            return current;
        }
        // follow the chain
        long previous = PersistedNode.KEY_NOT_PRESENT;
        while (current != PersistedNode.KEY_NOT_PRESENT) {
            try (IOElement entry = backend.read(current)) {
                long next = entry.readLong();
                if (node.getNodeType() == entry.readInt() && node.getKey() == entry.readLong()) {
                    return current;
                }
                previous = current;
                current = next;
            }
        }
        // not present => new entry
        if (!resolve)
            return PersistedNode.KEY_NOT_PRESENT;
        current = newEntry(node);
        try (IOElement entry = backend.access(previous)) {
            entry.writeLong(current);
        }
        return current;
    }

    /**
     * Writes the new quad node entry for the specified node
     *
     * @param node A quad node
     * @return The key to the entry
     * @throws IOException      When an IO operation failed
     * @throws StorageException When the page version does not match the expected one
     */
    private long newEntry(PersistedNode node) throws IOException, StorageException {
        long key = backend.add(QUAD_ENTRY_SIZE);
        try (IOElement entry = backend.access(key)) {
            entry.writeLong(PersistedNode.KEY_NOT_PRESENT);
            entry.writeInt(node.getNodeType());
            entry.writeLong(node.getKey());
            entry.writeLong(PersistedNode.KEY_NOT_PRESENT);
        }
        return key;
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
        int result = doAdd(quad.getGraph(), quad.getSubject(), quad.getProperty(), quad.getObject());
        if (result == ADD_RESULT_NEW) {
            for (ChangeListener listener : listeners)
                listener.onAdded(quad);
        } else if (result == ADD_RESULT_INCREMENT) {
            for (ChangeListener listener : listeners)
                listener.onIncremented(quad);
        }
    }

    @Override
    public void add(GraphNode graph, SubjectNode subject, Property property, Node value) throws UnsupportedNodeType {
        int result = doAdd(subject, property, value, graph);
        if (result == ADD_RESULT_NEW) {
            Quad quad = new Quad(graph, subject, property, value);
            for (ChangeListener listener : listeners)
                listener.onAdded(quad);
        } else if (result == ADD_RESULT_INCREMENT) {
            Quad quad = new Quad(graph, subject, property, value);
            for (ChangeListener listener : listeners)
                listener.onIncremented(quad);
        }
    }

    @Override
    public void remove(Quad quad) throws UnsupportedNodeType {
        if (quad.getGraph() != null && quad.getGraph().getNodeType() != Node.TYPE_VARIABLE
                && quad.getSubject() != null && quad.getSubject().getNodeType() != Node.TYPE_VARIABLE
                && quad.getProperty() != null && quad.getProperty().getNodeType() != Node.TYPE_VARIABLE
                && quad.getObject() != null && quad.getObject().getNodeType() != Node.TYPE_VARIABLE) {
            // this is a ground quad
            int result = doRemove(quad.getGraph(), quad.getSubject(), quad.getProperty(), quad.getObject());
            if (result == REMOVE_RESULT_DECREMENT) {
                for (ChangeListener listener : listeners)
                    listener.onDecremented(quad);
            } else if (result >= REMOVE_RESULT_REMOVED) {
                for (ChangeListener listener : listeners)
                    listener.onRemoved(quad);
            }
        } else {
            // this is a remove all operation
        }
    }

    @Override
    public void remove(GraphNode graph, SubjectNode subject, Property property, Node value) throws UnsupportedNodeType {
        if (graph != null && graph.getNodeType() != Node.TYPE_VARIABLE
                && subject != null && subject.getNodeType() != Node.TYPE_VARIABLE
                && property != null && property.getNodeType() != Node.TYPE_VARIABLE
                && value != null && value.getNodeType() != Node.TYPE_VARIABLE) {
            // this is a ground quad
            int result = doRemove(graph, subject, property, value);
            if (result == REMOVE_RESULT_DECREMENT) {
                Quad quad = new Quad(graph, subject, property, value);
                for (ChangeListener listener : listeners)
                    listener.onDecremented(quad);
            } else if (result >= REMOVE_RESULT_REMOVED) {
                Quad quad = new Quad(graph, subject, property, value);
                for (ChangeListener listener : listeners)
                    listener.onRemoved(quad);
            }
        } else {
            // this is a remove all operation
        }
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
        backend.close();
        database.close();
    }
}
