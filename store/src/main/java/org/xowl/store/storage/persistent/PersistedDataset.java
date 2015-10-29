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
     * long: next node in list
     * long+int: persisted node data
     * long: first node of child list
     */
    private static final int QUAD_ENTRY_SIZE = 8 + PersistedNode.SERIALIZED_SIZE + 8;
    /**
     * The maximum number of items in an entry for an index on a graph
     */
    private static final int GINDEX_ENTRY_MAX_ITEM_COUNT = 128;
    /**
     * The size in bytes of an entry for an index on a graph
     * long: next index entry
     * int: key radical for items in this entry
     * int: number of items in this index entry
     * int: key to the subject quad node for item 1
     * int: multiplicity for item 1
     * int: ...
     * int: key to the subject quad node for item n
     * int: multiplicity for item n
     */
    private static final int GINDEX_ENTRY_SIZE = 8 + 4 + 4 + GINDEX_ENTRY_MAX_ITEM_COUNT * (4 + 4);

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
     * The index map for IRI graphs
     */
    private final Map<Long, Long> mapIndexGraphIRI;
    /**
     * The index map for blank graphs
     */
    private final Map<Long, Long> mapIndexGraphBlank;
    /**
     * A temporary key to the previous quad node entry
     */
    private long bufferQNPrevious;
    /**
     * A temporary key to the quad node entry for a subject
     */
    private long bufferQNSubject;

    /**
     * Initializes this dataset
     *
     * @param nodes      The persisted nodes associated to this dataset
     * @param directory  The parent directory containing the backing files
     * @param isReadonly Whether this store is in readonly mode
     * @throws IOException      When the backing files cannot be accessed
     * @throws StorageException When the storage is in a bad state
     */
    public PersistedDataset(PersistedNodes nodes, File directory, boolean isReadonly) throws IOException, StorageException {
        this.listeners = new ArrayList<>();
        this.nodes = nodes;
        this.backend = new FileStore(directory, FILE_DATA, isReadonly);
        this.database = dbMaker(directory, isReadonly).make();
        this.mapSubjectIRI = database.hashMap("subject-iris");
        this.mapSubjectBlank = database.hashMap("subject-blanks");
        this.mapSubjectAnon = database.hashMap("subject-anons");
        this.mapIndexGraphIRI = database.hashMap("graph-index-iris");
        this.mapIndexGraphBlank = database.hashMap("graph-index-blanks");
    }

    /**
     * Gets the mapDB database maker for this store
     *
     * @param directory  The parent directory containing the backing files
     * @param isReadonly Whether this store is in readonly mode
     * @return The DB maker
     */
    private static DBMaker.Maker dbMaker(File directory, boolean isReadonly) {
        DBMaker.Maker maker = DBMaker.fileDB(new File(directory, FILE_INDEX));
        if (isReadonly)
            maker = maker.readOnly();
        return maker;
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
    private int doQuadAdd(PersistedNode subject, PersistedNode property, PersistedNode object, PersistedNode graph) throws UnsupportedNodeType {
        try {
            Map<Long, Long> map = mapFor(subject);
            Long bucket = map.get(subject.getKey());
            if (bucket == null) {
                bufferQNSubject = newEntry(subject);
                map.put(subject.getKey(), bufferQNSubject);
            } else {
                bufferQNSubject = bucket;
            }
            long target = lookupQNode(bufferQNSubject, property, true);
            target = lookupQNode(target, object, true);
            target = lookupQNode(target, graph, true);
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
     * Indexes a quad into the backend
     *
     * @param subject The subject
     * @param graph   The graph
     */
    private void doQuadIndex(PersistedNode subject, PersistedNode graph) {
        int radical = ((int) (subject.getKey() >>> 32));
        Map<Long, Long> map = graph.getNodeType() == Node.TYPE_IRI ? mapIndexGraphIRI : mapIndexGraphBlank;
        Long bucket = map.get(graph.getKey());
        if (bucket == null) {
            // this is the first quad for this graph
            long key = writeNewGraphIndex(radical, bufferQNSubject);
            map.put(graph.getKey(), key);
            return;
        }
        // look for an appropriate entry
        long emptyEntry = PersistedNode.KEY_NOT_PRESENT;
        bufferQNPrevious = PersistedNode.KEY_NOT_PRESENT;
        long current = bucket;
        while (current != PersistedNode.KEY_NOT_PRESENT) {
            try (IOElement entry = backend.access(current)) {
                long next = entry.readLong();
                int eRadical = entry.readInt();
                if (eRadical != radical)
                    continue;
                int count = entry.readInt();
                if (emptyEntry == PersistedNode.KEY_NOT_PRESENT && count < GINDEX_ENTRY_MAX_ITEM_COUNT)
                    emptyEntry = current;
                for (int i = 0; i != GINDEX_ENTRY_MAX_ITEM_COUNT; i++) {
                    long qnode = entry.readLong();
                    long multiplicity = entry.readLong();
                    if (qnode == bufferQNSubject) {
                        multiplicity++;
                        entry.seek(i * 8 + 8 + 4 + 4 + 4).writeLong(multiplicity);
                        return;
                    }
                }
                bufferQNPrevious = current;
                current = next;
            } catch (IOException | StorageException exception) {
                // do nothing
                return;
            }
        }
        // not found in an entry
        if (emptyEntry != PersistedNode.KEY_NOT_PRESENT) {
            try (IOElement entry = backend.access(emptyEntry)) {
                int count = entry.seek(12).readInt();
                for (int i = 0; i != GINDEX_ENTRY_MAX_ITEM_COUNT; i++) {
                    long qnode = entry.readLong();
                    entry.readLong();
                    if (qnode == PersistedNode.KEY_NOT_PRESENT) {
                        entry.seek(i * 8 + 8 + 4 + 4);
                        entry.writeInt((int) (bufferQNSubject - radical));
                        entry.writeInt(1);
                        break;
                    }
                }
                entry.seek(8 + 4).writeInt(count + 1);
            } catch (IOException | StorageException exception) {
                // do nothing
            }
        } else {
            // requires a new entry
            long key = writeNewGraphIndex(radical, bufferQNSubject);
            try (IOElement entry = backend.access(bufferQNPrevious)) {
                entry.writeLong(key);
            } catch (IOException | StorageException exception) {
                // do nothing
            }
        }
    }

    /**
     * Writes a new graph index entry
     *
     * @param radical The key radical for this entry
     * @param qnode   The key to the subject quad node
     * @return The key to this entry
     */
    private long writeNewGraphIndex(int radical, long qnode) {
        try {
            long key = backend.add(GINDEX_ENTRY_SIZE);
            try (IOElement entry = backend.access(key)) {
                entry.writeLong(PersistedNode.KEY_NOT_PRESENT);
                entry.writeInt(radical);
                entry.writeInt(1);
                entry.writeInt((int) (qnode - radical));
                entry.writeInt(1);
            }
            return key;
        } catch (IOException | StorageException exception) {
            // do nothing
            return PersistedNode.KEY_NOT_PRESENT;
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
    private int doQuadRemove(PersistedNode subject, PersistedNode property, PersistedNode object, PersistedNode graph) throws UnsupportedNodeType {
        try {
            Map<Long, Long> map = mapFor(subject);
            Long bucket = map.get(subject.getKey());
            if (bucket == null) {
                return REMOVE_RESULT_NOT_FOUND;
            }
            bufferQNSubject = bucket;
            long bufferQNProperty = lookupQNode(bufferQNSubject, property, false);
            if (bufferQNProperty == PersistedNode.KEY_NOT_PRESENT)
                return REMOVE_RESULT_NOT_FOUND;
            long keyPropertyPrevious = bufferQNPrevious;
            long bufferQNObject = lookupQNode(bufferQNProperty, object, false);
            if (bufferQNObject == PersistedNode.KEY_NOT_PRESENT)
                return REMOVE_RESULT_NOT_FOUND;
            long keyObjectPrevious = bufferQNPrevious;
            long bufferQNGraph = lookupQNode(bufferQNObject, graph, false);
            if (bufferQNGraph == PersistedNode.KEY_NOT_PRESENT)
                return REMOVE_RESULT_NOT_FOUND;
            long keyGraphPrevious = bufferQNPrevious;
            try (IOElement entry = backend.access(bufferQNGraph)) {
                long value = entry.seek(QUAD_ENTRY_SIZE - 8).readLong();
                value--;
                if (value > 0) {
                    entry.seek(QUAD_ENTRY_SIZE - 8).writeLong(value);
                    return REMOVE_RESULT_DECREMENT;
                }
            }

            // remove the graph node
            long next;
            try (IOElement entry = backend.read(bufferQNGraph)) {
                next = entry.readLong();
            }
            if (keyGraphPrevious == bufferQNObject) {
                // the previous of the graph is the object
                if (next == PersistedNode.KEY_NOT_PRESENT) {
                    // the last one
                    backend.remove(bufferQNGraph);
                } else {
                    try (IOElement entry = backend.access(bufferQNObject)) {
                        entry.seek(QUAD_ENTRY_SIZE - 8).writeLong(next);
                    }
                    backend.remove(bufferQNGraph);
                    return REMOVE_RESULT_REMOVED;
                }
            } else {
                try (IOElement entry = backend.access(keyGraphPrevious)) {
                    entry.writeLong(next);
                }
                backend.remove(bufferQNGraph);
                return REMOVE_RESULT_REMOVED;
            }

            // remove the object node
            try (IOElement entry = backend.read(bufferQNObject)) {
                next = entry.readLong();
            }
            if (keyObjectPrevious == bufferQNProperty) {
                // the previous of the object is the property
                if (next == PersistedNode.KEY_NOT_PRESENT) {
                    // the last one
                    backend.remove(bufferQNObject);
                } else {
                    try (IOElement entry = backend.access(bufferQNProperty)) {
                        entry.seek(QUAD_ENTRY_SIZE - 8).writeLong(next);
                    }
                    backend.remove(bufferQNObject);
                    return REMOVE_RESULT_REMOVED;
                }
            } else {
                try (IOElement entry = backend.access(keyObjectPrevious)) {
                    entry.writeLong(next);
                }
                backend.remove(bufferQNObject);
                return REMOVE_RESULT_REMOVED;
            }

            // remove the property node
            try (IOElement entry = backend.read(bufferQNProperty)) {
                next = entry.readLong();
            }
            if (keyPropertyPrevious == bufferQNSubject) {
                // the previous of the property is the subject
                if (next == PersistedNode.KEY_NOT_PRESENT) {
                    // the last one
                    backend.remove(bufferQNProperty);
                } else {
                    try (IOElement entry = backend.access(bufferQNSubject)) {
                        entry.seek(QUAD_ENTRY_SIZE - 8).writeLong(next);
                    }
                    backend.remove(bufferQNProperty);
                    return REMOVE_RESULT_REMOVED;
                }
            } else {
                try (IOElement entry = backend.access(keyPropertyPrevious)) {
                    entry.writeLong(next);
                }
                backend.remove(bufferQNProperty);
                return REMOVE_RESULT_REMOVED;
            }

            // remove the subject node
            backend.remove(bufferQNSubject);
            map.remove(subject.getKey());
            return REMOVE_RESULT_EMPTIED;
        } catch (IOException | StorageException exception) {
            // do nothing
            return REMOVE_RESULT_NOT_FOUND;
        }
    }

    /**
     * De-indexes a quad into the backend
     *
     * @param subject The subject
     * @param graph   The graph
     */
    private void doQuadDeindex(PersistedNode subject, PersistedNode graph) {
        int radical = ((int) (subject.getKey() >>> 32));
        Map<Long, Long> map = graph.getNodeType() == Node.TYPE_IRI ? mapIndexGraphIRI : mapIndexGraphBlank;
        Long bucket = map.get(graph.getKey());
        if (bucket == null) {
            // this is the first quad for this graph
            return;
        }
        // look for an appropriate entry
        bufferQNPrevious = PersistedNode.KEY_NOT_PRESENT;
        long current = bucket;
        while (current != PersistedNode.KEY_NOT_PRESENT) {
            try (IOElement entry = backend.access(current)) {
                long next = entry.readLong();
                int eRadical = entry.readInt();
                if (eRadical != radical)
                    continue;
                int count = entry.readInt();
                for (int i = 0; i != count; i++) {
                    long qnode = entry.readLong();
                    long multiplicity = entry.readLong();
                    if (qnode == bufferQNSubject) {
                        multiplicity--;
                        if (multiplicity > 0) {
                            entry.seek(i * 8 + 8 + 4 + 4 + 4).writeLong(multiplicity);
                            return;
                        }
                        count--;
                        if (count > 0) {
                            entry.seek(i * 8 + 8 + 4 + 4);
                            entry.writeLong(PersistedNode.KEY_NOT_PRESENT);
                            entry.writeLong(0);
                            entry.seek(8 + 4).writeInt(count);
                            return;
                        }
                        if (bufferQNPrevious == PersistedNode.KEY_NOT_PRESENT) {
                            // this is the first entry for this index
                            if (next == PersistedNode.KEY_NOT_PRESENT) {
                                // this is the sole entry
                                map.remove(graph.getKey());
                            } else {
                                map.put(graph.getKey(), next);
                            }
                        } else {
                            try (IOElement pe = backend.access(bufferQNPrevious)) {
                                pe.writeLong(next);
                            }
                        }
                        backend.remove(current);
                        return;
                    }
                }
                bufferQNPrevious = current;
                current = next;
            } catch (IOException | StorageException exception) {
                // do nothing
                return;
            }
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
    private long lookupQNode(long from, PersistedNode node, boolean resolve) throws IOException, StorageException {
        bufferQNPrevious = from;
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
        while (current != PersistedNode.KEY_NOT_PRESENT) {
            try (IOElement entry = backend.read(current)) {
                long next = entry.readLong();
                if (node.getNodeType() == entry.readInt() && node.getKey() == entry.readLong()) {
                    return current;
                }
                bufferQNPrevious = current;
                current = next;
            }
        }
        // not present => new entry
        if (!resolve)
            return PersistedNode.KEY_NOT_PRESENT;
        current = newEntry(node);
        try (IOElement entry = backend.access(bufferQNPrevious)) {
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
        Collection<GraphNode> result = new ArrayList<>();
        for (Long key : mapIndexGraphIRI.keySet()) {
            result.add(nodes.getIRINodeFor(key));
        }
        for (Long key : mapIndexGraphBlank.keySet()) {
            result.add(nodes.getBlankNodeFor(key));
        }
        return result;
    }

    @Override
    public long count() {
        long result = 0;
        for (Long key : mapIndexGraphIRI.keySet()) {
            result += count(mapIndexGraphIRI, key);
        }
        for (Long key : mapIndexGraphBlank.keySet()) {
            result += count(mapIndexGraphBlank, key);
        }
        return result;
    }

    @Override
    public long count(GraphNode graph) {
        PersistedNode pGraph;
        try {
            pGraph = nodes.getPersistent(graph, false);
        } catch (UnsupportedNodeType exception) {
            return 0;
        }
        Map<Long, Long> map = pGraph.getNodeType() == Node.TYPE_IRI ? mapIndexGraphIRI : mapIndexGraphBlank;
        return count(map, pGraph.getKey());
    }

    /**
     * Counts the number of quads in a graph
     *
     * @param map The corresponding graph index map
     * @param key The graph key
     * @return The number of quads
     */
    private long count(Map<Long, Long> map, long key) {
        Long bucket = map.get(key);
        if (bucket == null)
            return 0;
        long result = 0;
        long current = bucket;
        while (current != PersistedNode.KEY_NOT_PRESENT) {
            try (IOElement entry = backend.read(current)) {
                long next = entry.readLong();
                int count = entry.seek(8 + 4).readInt();
                for (int i = 0; i != GINDEX_ENTRY_MAX_ITEM_COUNT; i++) {
                    int eK = entry.readInt();
                    int mult = entry.readInt();
                    if (eK != PersistedNode.KEY_NOT_PRESENT) {
                        result += mult;
                        count--;
                        if (count <= 0)
                            break;
                    }
                }
                current = next;
            } catch (IOException | StorageException exception) {
                return 0;
            }
        }
        return result;
    }

    @Override
    public long count(SubjectNode subject, Property property, Node object) {
        return count(null, subject, property, object);
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
        PersistedNode pSubject = nodes.getPersistent(quad.getSubject(), true);
        PersistedNode pProperty = nodes.getPersistent(quad.getProperty(), true);
        PersistedNode pObject = nodes.getPersistent(quad.getObject(), true);
        PersistedNode pGraph = nodes.getPersistent(quad.getGraph(), true);
        int result = doQuadAdd(pSubject, pProperty, pObject, pGraph);
        if (result == ADD_RESULT_NEW) {
            doQuadIndex(pSubject, pGraph);
            pSubject.incrementRefCount();
            pProperty.incrementRefCount();
            pObject.incrementRefCount();
            pGraph.incrementRefCount();
            for (ChangeListener listener : listeners)
                listener.onAdded(quad);
        } else if (result == ADD_RESULT_INCREMENT) {
            for (ChangeListener listener : listeners)
                listener.onIncremented(quad);
        }
    }

    @Override
    public void add(GraphNode graph, SubjectNode subject, Property property, Node value) throws UnsupportedNodeType {
        PersistedNode pSubject = nodes.getPersistent(subject, true);
        PersistedNode pProperty = nodes.getPersistent(property, true);
        PersistedNode pObject = nodes.getPersistent(value, true);
        PersistedNode pGraph = nodes.getPersistent(graph, true);
        int result = doQuadAdd(pSubject, pProperty, pObject, pGraph);
        if (result == ADD_RESULT_NEW) {
            doQuadIndex(pSubject, pGraph);
            pSubject.incrementRefCount();
            pProperty.incrementRefCount();
            pObject.incrementRefCount();
            pGraph.incrementRefCount();
            Quad quad = new Quad((GraphNode) pGraph, (SubjectNode) pSubject, (Property) pProperty, pObject);
            for (ChangeListener listener : listeners)
                listener.onAdded(quad);
        } else if (result == ADD_RESULT_INCREMENT) {
            Quad quad = new Quad((GraphNode) pGraph, (SubjectNode) pSubject, (Property) pProperty, pObject);
            for (ChangeListener listener : listeners)
                listener.onIncremented(quad);
        }
    }

    @Override
    public void remove(Quad quad) throws UnsupportedNodeType {
        if (quad.getGraph() != null && quad.getSubject() != null && quad.getProperty() != null && quad.getObject() != null) {
            // this is a ground quad
            PersistedNode pSubject = nodes.getPersistent(quad.getSubject(), false);
            PersistedNode pProperty = nodes.getPersistent(quad.getProperty(), false);
            PersistedNode pObject = nodes.getPersistent(quad.getObject(), false);
            PersistedNode pGraph = nodes.getPersistent(quad.getGraph(), false);
            if (pSubject == null || pProperty == null || pObject == null || pGraph == null)
                // the quad cannot be in this store
                return;
            int result = doQuadRemove(pGraph, pSubject, pProperty, pObject);
            if (result == REMOVE_RESULT_DECREMENT) {
                for (ChangeListener listener : listeners)
                    listener.onDecremented(quad);
            } else if (result >= REMOVE_RESULT_REMOVED) {
                doQuadDeindex(pSubject, pGraph);
                pSubject.decrementRefCount();
                pProperty.decrementRefCount();
                pObject.decrementRefCount();
                pGraph.decrementRefCount();
                for (ChangeListener listener : listeners)
                    listener.onRemoved(quad);
            }
        } else {
            // this is a remove all operation
        }
    }

    @Override
    public void remove(GraphNode graph, SubjectNode subject, Property property, Node value) throws UnsupportedNodeType {
        if (graph != null && subject != null && property != null && value != null) {
            // this is a ground quad
            PersistedNode pSubject = nodes.getPersistent(subject, false);
            PersistedNode pProperty = nodes.getPersistent(property, false);
            PersistedNode pObject = nodes.getPersistent(value, false);
            PersistedNode pGraph = nodes.getPersistent(graph, false);
            if (pSubject == null || pProperty == null || pObject == null || pGraph == null)
                // the quad cannot be in this store
                return;
            int result = doQuadRemove(pGraph, pSubject, pProperty, pObject);
            if (result == REMOVE_RESULT_DECREMENT) {
                Quad quad = new Quad(graph, subject, property, value);
                for (ChangeListener listener : listeners)
                    listener.onDecremented(quad);
            } else if (result >= REMOVE_RESULT_REMOVED) {
                doQuadDeindex(pSubject, pGraph);
                pSubject.decrementRefCount();
                pProperty.decrementRefCount();
                pObject.decrementRefCount();
                pGraph.decrementRefCount();
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
