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
import org.xowl.utils.collections.*;

import java.io.File;
import java.io.IOException;
import java.util.*;

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
     * Encapsulates a key to a quad node
     */
    private static abstract class QNode {
        /**
         * Gets the key
         *
         * @return The key
         */
        public abstract long key();
    }

    /**
     * Iterator over the quad node in a bucket
     */
    private static class QNodeIterator extends QNode implements Iterator<QNode> {
        /**
         * The backend
         */
        private final FileStore backend;
        /**
         * The next key to iterate over
         */
        private long next;
        /**
         * The current value
         */
        private long value;

        /**
         * Initializes the iterator
         *
         * @param backend The backend
         * @param entry   The first entry
         */
        public QNodeIterator(FileStore backend, long entry) {
            this.backend = backend;
            next = entry;
            value = PersistedNode.KEY_NOT_PRESENT;
        }

        @Override
        public boolean hasNext() {
            return next != PersistedNode.KEY_NOT_PRESENT;
        }

        @Override
        public QNode next() {
            try (IOElement entry = backend.read(next)) {
                value = next;
                next = entry.readLong();
            } catch (IOException | StorageException exception) {
                // do nothing
            }
            return this;
        }

        @Override
        public long key() {
            return value;
        }
    }

    /**
     * Iterator over the subject nodes in a graph index
     */
    private static class GraphQNodeIterator extends QNode implements Iterator<QNode> {
        /**
         * The backend
         */
        private final FileStore backend;
        /**
         * The key to the current graph index entry
         */
        private long keyEntry;
        /**
         * The current radical for the current entry
         */
        private int radical;
        /**
         * The current entry index
         */
        private int index;
        /**
         * The next value to return
         */
        private long next;
        /**
         * The current value to return
         */
        private long value;

        /**
         * Initializes the iterator
         *
         * @param backend The backend
         * @param entry   The first entry of the graph index
         */
        public GraphQNodeIterator(FileStore backend, long entry) {
            this.backend = backend;
            this.keyEntry = entry;
            this.index = -1;
            try {
                this.next = findNext();
            } catch (IOException | StorageException exception) {
                this.next = PersistedNode.KEY_NOT_PRESENT;
            }
            this.value = PersistedNode.KEY_NOT_PRESENT;
        }

        /**
         * Finds the next quad node
         *
         * @return The next quad node
         * @throws IOException      When an IO operation failed
         * @throws StorageException When the page version does not match the expected one
         */
        private long findNext() throws IOException, StorageException {
            while (true) {
                index++;
                if (index == GINDEX_ENTRY_MAX_ITEM_COUNT) {
                    try (IOElement entry = backend.read(keyEntry)) {
                        keyEntry = entry.readLong();
                    }
                    if (keyEntry == PersistedNode.KEY_NOT_PRESENT)
                        return PersistedNode.KEY_NOT_PRESENT;
                    index = 0;
                }
                try (IOElement entry = backend.read(keyEntry)) {
                    radical = entry.seek(8).readInt();
                    for (int i = index; i != GINDEX_ENTRY_MAX_ITEM_COUNT; i++) {
                        int ek = entry.seek(8 + 4 + 4 + i * 8).readInt();
                        if (ek != PersistedNode.SERIALIZED_SIZE) {
                            index = i;
                            return FileStore.getFullKey(radical, ek);
                        }
                    }
                }
            }
        }

        @Override
        public boolean hasNext() {
            return next != PersistedNode.KEY_NOT_PRESENT;
        }

        @Override
        public QNode next() {
            try {
                value = next;
                next = findNext();
            } catch (IOException | StorageException exception) {
                // do nothing
            }
            return this;
        }

        @Override
        public long key() {
            return value;
        }
    }

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
     * Commits the outstanding changes to this store
     *
     * @return Whether the operation succeeded
     */
    public boolean commit() {
        boolean success = backend.commit();
        database.commit();
        return success;
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
        int radical = FileStore.getKeyRadical(subject.getKey());
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
                    int qnode = entry.readInt();
                    int multiplicity = entry.readInt();
                    if (qnode == FileStore.getShortKey(bufferQNSubject)) {
                        multiplicity++;
                        entry.seek(i * 8 + 8 + 4 + 4 + 4).writeInt(multiplicity);
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
                    int qnode = entry.readInt();
                    entry.readInt();
                    if (qnode == PersistedNode.KEY_NOT_PRESENT) {
                        entry.seek(i * 8 + 8 + 4 + 4);
                        entry.writeInt(FileStore.getShortKey(bufferQNSubject));
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
                    int qnode = entry.readInt();
                    int multiplicity = entry.readInt();
                    if (qnode == FileStore.getShortKey(bufferQNSubject)) {
                        multiplicity--;
                        if (multiplicity > 0) {
                            entry.seek(i * 8 + 8 + 4 + 4 + 4).writeLong(multiplicity);
                            return;
                        }
                        count--;
                        if (count > 0) {
                            entry.seek(i * 8 + 8 + 4 + 4);
                            entry.writeInt(-1);
                            entry.writeInt(0);
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

    /**
     * Gets the persisted node
     *
     * @param type The type of node
     * @param key  The key for the node
     * @return The persisted node
     */
    private PersistedNode getNode(int type, long key) {
        switch (type) {
            case Node.TYPE_IRI:
                return nodes.getIRINodeFor(key);
            case Node.TYPE_BLANK:
                return nodes.getBlankNodeFor(key);
            case Node.TYPE_ANONYMOUS:
                return nodes.getAnonNodeFor(key);
            case Node.TYPE_LITERAL:
                return nodes.getLiteralNodeFor(key);
        }
        // cannot happen, avoids being nullable
        return nodes.getBlankNodeFor(-1);
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
        if (subject != null)
            return getAllOnSingleSubject(graph, subject, property, object);
        if (graph != null)
            return getAllOnSingleGraph(graph, property, object);
        return getAllDefault(property, object);
    }

    /**
     * Gets an iterator over all quads from a single subject
     *
     * @param graph    A containing graph to match, or null
     * @param subject  A subject node to match, or null
     * @param property A property to match, or null
     * @param object   An object node to match, or null
     * @return An iterator over the results
     */
    private Iterator<Quad> getAllOnSingleSubject(GraphNode graph, final SubjectNode subject, Property property, Node object) {
        try {
            PersistedNode pSubject = nodes.getPersistent(subject, false);
            if (pSubject == null)
                return new SingleIterator<>(null);
            Long current = mapFor(pSubject).get(pSubject.getKey());
            if (current == null)
                return new SingleIterator<>(null);
            try (IOElement entry = backend.read(current)) {
                long bucket = entry.seek(8 + 4 + 8).readLong();
                return new AdaptingIterator<>(getAllOnProperty(bucket, property, object, graph), new Adapter<Quad>() {
                    @Override
                    public <X> Quad adapt(X element) {
                        PersistedQuad quad = (PersistedQuad) element;
                        quad.setSubject(subject);
                        return quad;
                    }
                });
            }
        } catch (UnsupportedNodeType | IOException | StorageException exception) {
            return new SingleIterator<>(null);
        }
    }

    /**
     * Gets an iterator over all quads from a single graph
     *
     * @param graph    A containing graph to match, or null
     * @param property A property to match, or null
     * @param object   An object node to match, or null
     * @return An iterator over the results
     */
    private Iterator<Quad> getAllOnSingleGraph(GraphNode graph, final Property property, final Node object) {
        final PersistedNode pGraph;
        try {
            pGraph = nodes.getPersistent(graph, false);
        } catch (UnsupportedNodeType exception) {
            return new SingleIterator<>(null);
        }
        if (pGraph == null)
            return new SingleIterator<>(null);
        Map<Long, Long> map = pGraph.getNodeType() == Node.TYPE_IRI ? mapIndexGraphIRI : mapIndexGraphBlank;
        Long bucket = map.get(pGraph.getKey());
        if (graph == null)
            return new SingleIterator<>(null);
        Iterator<QNode> iteratorSubjects = new GraphQNodeIterator(backend, bucket);
        return new AdaptingIterator<>(new CombiningIterator<>(iteratorSubjects, new Adapter<Iterator<PersistedQuad>>() {
            @Override
            public <X> Iterator<PersistedQuad> adapt(X element) {
                long subjectKey = ((QNode) element).key();
                try (IOElement entry = backend.read(subjectKey)) {
                    long propertyBucket = entry.seek(8 + 4 + 8).readLong();
                    return getAllOnProperty(propertyBucket, property, object, (GraphNode) pGraph);
                } catch (IOException | StorageException exception) {
                    return null;
                }
            }
        }), new Adapter<Quad>() {
            @Override
            public <X> Quad adapt(X element) {
                Couple<QNode, PersistedQuad> couple = (Couple<QNode, PersistedQuad>) element;
                long subjectKey = couple.x.key();
                try (IOElement entry = backend.read(subjectKey)) {
                    couple.y.setSubject((SubjectNode) getNode(entry.seek(8).readInt(), entry.readLong()));
                } catch (IOException | StorageException exception) {
                    return null;
                }
                return couple.y;
            }
        });
    }

    /**
     * Gets an iterator over all quads
     *
     * @param property A property to match, or null
     * @param object   An object node to match, or null
     * @return An iterator over the results
     */
    private Iterator<Quad> getAllDefault(final Property property, final Node object) {
        return new AdaptingIterator<>(new CombiningIterator<>(getAllSubjects(), new Adapter<Iterator<PersistedQuad>>() {
            @Override
            public <X> Iterator<PersistedQuad> adapt(X element) {
                long subjectKey = (Long) element;
                try (IOElement entry = backend.read(subjectKey)) {
                    long propertyBucket = entry.seek(8 + 4 + 8).readLong();
                    return getAllOnProperty(propertyBucket, property, object, null);
                } catch (IOException | StorageException exception) {
                    return null;
                }
            }
        }), new Adapter<Quad>() {
            @Override
            public <X> Quad adapt(X element) {
                Couple<Long, PersistedQuad> couple = (Couple<Long, PersistedQuad>) element;
                long subjectKey = couple.x;
                try (IOElement entry = backend.read(subjectKey)) {
                    couple.y.setSubject((SubjectNode) getNode(entry.seek(8).readInt(), entry.readLong()));
                    return couple.y;
                } catch (IOException | StorageException exception) {
                    return null;
                }
            }
        });
    }

    /**
     * Gets an iterator over all the subjects
     *
     * @return The iterator
     */
    private Iterator<Long> getAllSubjects() {
        return new ConcatenatedIterator<>(new Iterator[]{
                getSubjectIterator(mapSubjectIRI),
                getSubjectIterator(mapSubjectBlank),
                getSubjectIterator(mapSubjectAnon)
        });
    }

    /**
     * Gets an iterator over the subjects in an index
     *
     * @param map The indexing map
     * @return The iterator
     */
    private Iterator<Long> getSubjectIterator(Map<Long, Long> map) {
        return new AdaptingIterator<>(map.entrySet().iterator(), new Adapter<Long>() {
            @Override
            public <X> Long adapt(X element) {
                Map.Entry<Long, Long> mapEntry = (Map.Entry<Long, Long>) element;
                return mapEntry.getValue();
            }
        });
    }

    /**
     * Gets an iterator over all quads in a bucket of properties
     *
     * @param bucket   The bucket of properties
     * @param graph    A containing graph to match, or null
     * @param property A property to match, or null
     * @param object   An object node to match, or null
     * @return An iterator over the results
     */
    private Iterator<PersistedQuad> getAllOnProperty(long bucket, final Property property, final Node object, final GraphNode graph) {
        if (property == null || property.getNodeType() == Node.TYPE_VARIABLE) {
            return new AdaptingIterator<>(new CombiningIterator<>(new QNodeIterator(backend, bucket), new Adapter<Iterator<PersistedQuad>>() {
                @Override
                public <X> Iterator<PersistedQuad> adapt(X element) {
                    long key = ((QNode) element).key();
                    try (IOElement entry = backend.read(key)) {
                        entry.seek(8 + 4 + 8);
                        long objectKey = entry.readLong();
                        return getAllOnObject(objectKey, object, graph);
                    } catch (IOException | StorageException exception) {
                        return null;
                    }
                }
            }), new Adapter<PersistedQuad>() {
                @Override
                public <X> PersistedQuad adapt(X element) {
                    Couple<QNode, PersistedQuad> couple = (Couple<QNode, PersistedQuad>) element;
                    long key = couple.x.key();
                    try (IOElement entry = backend.read(key)) {
                        entry.seek(8);
                        PersistedNode node = getNode(entry.readInt(), entry.readLong());
                        couple.y.setProperty((Property) node);
                    } catch (IOException | StorageException exception) {
                        return null;
                    }
                    return couple.y;
                }
            });
        }
        PersistedNode pProperty;
        try {
            pProperty = nodes.getPersistent(property, false);
        } catch (UnsupportedNodeType exception) {
            return new SingleIterator<>(null);
        }
        if (pProperty == null)
            return new SingleIterator<>(null);
        long current = bucket;
        while (current != PersistedNode.KEY_NOT_PRESENT) {
            try (IOElement entry = backend.read(current)) {
                current = entry.readLong();
                int type = entry.readInt();
                long key = entry.readLong();
                long child = entry.readLong();
                if (type == pProperty.getNodeType() && key == pProperty.getKey()) {
                    return new AdaptingIterator<>(getAllOnObject(child, object, graph), new Adapter<PersistedQuad>() {
                        @Override
                        public <X> PersistedQuad adapt(X element) {
                            PersistedQuad quad = (PersistedQuad) element;
                            quad.setProperty(property);
                            return quad;
                        }
                    });
                }
            } catch (IOException | StorageException exception) {
                return new SingleIterator<>(null);
            }
        }
        return new SingleIterator<>(null);
    }

    /**
     * Gets an iterator over all quads in a bucket of objects
     *
     * @param bucket The bucket of properties
     * @param graph  A containing graph to match, or null
     * @param object An object node to match, or null
     * @return An iterator over the results
     */
    private Iterator<PersistedQuad> getAllOnObject(long bucket, final Node object, final GraphNode graph) {
        if (object == null || object.getNodeType() == Node.TYPE_VARIABLE) {
            return new AdaptingIterator<>(new CombiningIterator<>(new QNodeIterator(backend, bucket), new Adapter<Iterator<PersistedQuad>>() {
                @Override
                public <X> Iterator<PersistedQuad> adapt(X element) {
                    long key = ((QNode) element).key();
                    try (IOElement entry = backend.read(key)) {
                        entry.seek(8 + 4 + 8);
                        long graphKey = entry.readLong();
                        return getAllOnGraph(graphKey, graph);
                    } catch (IOException | StorageException exception) {
                        return null;
                    }
                }
            }), new Adapter<PersistedQuad>() {
                @Override
                public <X> PersistedQuad adapt(X element) {
                    Couple<QNode, PersistedQuad> couple = (Couple<QNode, PersistedQuad>) element;
                    long key = couple.x.key();
                    try (IOElement entry = backend.read(key)) {
                        entry.seek(8);
                        PersistedNode node = getNode(entry.readInt(), entry.readLong());
                        couple.y.setObject(node);
                    } catch (IOException | StorageException exception) {
                        return null;
                    }
                    return couple.y;
                }
            });
        }
        PersistedNode pObject;
        try {
            pObject = nodes.getPersistent(object, false);
        } catch (UnsupportedNodeType exception) {
            return new SingleIterator<>(null);
        }
        if (pObject == null)
            return new SingleIterator<>(null);
        long current = bucket;
        while (current != PersistedNode.KEY_NOT_PRESENT) {
            try (IOElement entry = backend.read(current)) {
                current = entry.readLong();
                int type = entry.readInt();
                long key = entry.readLong();
                long child = entry.readLong();
                if (type == pObject.getNodeType() && key == pObject.getKey()) {
                    return new AdaptingIterator<>(getAllOnGraph(child, graph), new Adapter<PersistedQuad>() {
                        @Override
                        public <X> PersistedQuad adapt(X element) {
                            PersistedQuad quad = (PersistedQuad) element;
                            quad.setObject(object);
                            return quad;
                        }
                    });
                }
            } catch (IOException | StorageException exception) {
                return new SingleIterator<>(null);
            }
        }
        return new SingleIterator<>(null);
    }

    /**
     * Gets an iterator over all quads in a bucket of graphs
     *
     * @param bucket The bucket of properties
     * @param graph  A containing graph to match, or null
     * @return An iterator over the results
     */
    private Iterator<PersistedQuad> getAllOnGraph(long bucket, GraphNode graph) {
        if (graph == null || graph.getNodeType() == Node.TYPE_VARIABLE) {
            return new AdaptingIterator<>(new QNodeIterator(backend, bucket), new Adapter<PersistedQuad>() {
                @Override
                public <X> PersistedQuad adapt(X element) {
                    long key = ((QNode) element).key();
                    try (IOElement entry = backend.read(key)) {
                        entry.seek(8);
                        PersistedNode node = getNode(entry.readInt(), entry.readLong());
                        long multiplicity = entry.readLong();
                        return new PersistedQuad((GraphNode) node, multiplicity);
                    } catch (IOException | StorageException exception) {
                        return null;
                    }
                }
            });
        }
        PersistedNode pGraph;
        try {
            pGraph = nodes.getPersistent(graph, false);
        } catch (UnsupportedNodeType exception) {
            return new SingleIterator<>(null);
        }
        if (pGraph == null)
            return new SingleIterator<>(null);
        long current = bucket;
        while (current != PersistedNode.KEY_NOT_PRESENT) {
            try (IOElement entry = backend.read(current)) {
                current = entry.readLong();
                int type = entry.readInt();
                long key = entry.readLong();
                long multiplicity = entry.readLong();
                if (type == pGraph.getNodeType() && key == pGraph.getKey()) {
                    return new SingleIterator<>(new PersistedQuad(graph, multiplicity));
                }
            } catch (IOException | StorageException exception) {
                return new SingleIterator<>(null);
            }
        }
        return new SingleIterator<>(null);
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
        if (pGraph == null)
            return 0;
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
        try {
            PersistedNode pSubject = null;
            if (subject != null && subject.getNodeType() != VariableNode.TYPE_VARIABLE) {
                pSubject = nodes.getPersistent(subject, false);
                if (pSubject == null)
                    return 0;
            }
            PersistedNode pProperty = null;
            if (property != null && property.getNodeType() != VariableNode.TYPE_VARIABLE) {
                pProperty = nodes.getPersistent(property, false);
                if (pProperty == null)
                    return 0;
            }
            PersistedNode pObject = null;
            if (object != null && object.getNodeType() != VariableNode.TYPE_VARIABLE) {
                pObject = nodes.getPersistent(object, false);
                if (pObject == null)
                    return 0;
            }
            PersistedNode pGraph = null;
            if (graph != null && graph.getNodeType() != VariableNode.TYPE_VARIABLE) {
                pGraph = nodes.getPersistent(graph, false);
                if (pGraph == null)
                    return 0;
            }
            if (subject != null)
                return countOnSingleSubject(pGraph, pSubject, pProperty, pObject);
            if (graph != null)
                return countOnSingleGraph(pGraph, pProperty, pObject);
            return countDefault(pProperty, pObject);
        } catch (UnsupportedNodeType exception) {
            return 0;
        }
    }

    /**
     * Counts the quads from a single subject entry
     *
     * @param graph    The graph to match
     * @param subject  The subject
     * @param property The property to match
     * @param object   The object to match
     * @return The number of matching quads
     */
    private long countOnSingleSubject(PersistedNode graph, PersistedNode subject, PersistedNode property, PersistedNode object) {
        try {
            Long current = mapFor(subject).get(subject.getKey());
            if (current == null)
                return 0;
            long child;
            try (IOElement entry = backend.read(current)) {
                child = entry.seek(8 + 4 + 8).readLong();
            }
            return countOnProperty(child, graph, property, object);
        } catch (UnsupportedNodeType | IOException | StorageException exception) {
            return 0;
        }
    }

    /**
     * Counts the quads from a single graph entry
     *
     * @param graph    The graph to match
     * @param property The property to match
     * @param object   The object to match
     * @return The number of matching quads
     */
    private long countOnSingleGraph(PersistedNode graph, PersistedNode property, PersistedNode object) {
        try {
            Map<Long, Long> map = graph.getNodeType() == Node.TYPE_IRI ? mapIndexGraphIRI : mapIndexGraphBlank;
            Long bucket = map.get(graph.getKey());
            if (bucket == null)
                return 0;
            long result = 0;
            long current = bucket;
            while (current != PersistedNode.KEY_NOT_PRESENT) {
                try (IOElement entry = backend.read(current)) {
                    current = entry.readLong();
                    int radical = entry.readInt();
                    int count = entry.readInt();
                    for (int i = 0; i != GINDEX_ENTRY_MAX_ITEM_COUNT; i++) {
                        int sk = entry.readInt();
                        entry.readInt();
                        if (sk != PersistedNode.KEY_NOT_PRESENT) {
                            long child = FileStore.getFullKey(radical, sk);
                            try (IOElement subjectEntry = backend.read(child)) {
                                child = subjectEntry.seek(8 + 4 + 8).readLong();
                            }
                            result += countOnProperty(child, graph, property, object);
                            count--;
                            if (count == 0)
                                break;
                        }
                    }
                }
            }
            return result;
        } catch (IOException | StorageException exception) {
            return 0;
        }
    }

    /**
     * Counts the all the quads
     *
     * @param property The property to match
     * @param object   The object to match
     * @return The number of matching quads
     */
    private long countDefault(PersistedNode property, PersistedNode object) {
        long result = 0;
        result += countDefault(mapSubjectIRI, property, object);
        result += countDefault(mapSubjectBlank, property, object);
        result += countDefault(mapSubjectAnon, property, object);
        return result;
    }

    /**
     * Counts the all the quads
     *
     * @param map      The subject index to use
     * @param property The property to match
     * @param object   The object to match
     * @return The number of matching quads
     */
    private long countDefault(Map<Long, Long> map, PersistedNode property, PersistedNode object) {
        long result = 0;
        for (Long subject : map.values()) {
            long bucket;
            try (IOElement entry = backend.access(subject)) {
                bucket = entry.seek(8 + 4 + 8).readLong();
            } catch (IOException | StorageException exception) {
                return result;
            }
            result += countOnProperty(bucket, null, property, object);
        }
        return result;
    }


    /**
     * Counts the quads from a bucket of properties
     *
     * @param bucket   The bucket of properties
     * @param graph    The graph to match
     * @param property The property to match
     * @param object   The object to match
     * @return The number of matching quads
     */
    private long countOnProperty(long bucket, PersistedNode graph, PersistedNode property, PersistedNode object) {
        long result = 0;
        long current = bucket;
        while (current != PersistedNode.KEY_NOT_PRESENT) {
            long child = PersistedNode.KEY_NOT_PRESENT;
            try (IOElement element = backend.read(current)) {
                current = element.readLong();
                if (property == null || (property.getNodeType() == element.readInt() && property.getKey() == element.readLong())) {
                    child = element.seek(8 + 4 + 8).readLong();
                }
            } catch (IOException | StorageException exception) {
                return result;
            }
            if (child != PersistedNode.KEY_NOT_PRESENT)
                result += countOnObject(child, graph, object);
        }
        return result;
    }

    /**
     * Counts the quads from a bucket of objects
     *
     * @param bucket The bucket of objects
     * @param graph  The graph to match
     * @param object The object to match
     * @return The number of matching quads
     */
    private long countOnObject(long bucket, PersistedNode graph, PersistedNode object) {
        long result = 0;
        long current = bucket;
        while (current != PersistedNode.KEY_NOT_PRESENT) {
            long child = PersistedNode.KEY_NOT_PRESENT;
            try (IOElement element = backend.read(current)) {
                current = element.readLong();
                if (object == null || (object.getNodeType() == element.readInt() && object.getKey() == element.readLong())) {
                    child = element.seek(8 + 4 + 8).readLong();
                }
            } catch (IOException | StorageException exception) {
                return result;
            }
            if (child != PersistedNode.KEY_NOT_PRESENT)
                result += countOnGraph(child, graph);
        }
        return result;
    }

    /**
     * Counts the quads from a bucket of graphs
     *
     * @param bucket The bucket of graphs
     * @param graph  The graph to match
     * @return The number of matching quads
     */
    private long countOnGraph(long bucket, PersistedNode graph) {
        long result = 0;
        long current = bucket;
        while (current != PersistedNode.KEY_NOT_PRESENT) {
            try (IOElement element = backend.read(current)) {
                current = element.readLong();
                if (graph == null || (graph.getNodeType() == element.readInt() && graph.getKey() == element.readLong()))
                    result++;
            } catch (IOException | StorageException exception) {
                return result;
            }
        }
        return result;
    }

    @Override
    public void insert(Changeset changeset) throws UnsupportedNodeType {
        Collection<Quad> incremented = new ArrayList<>();
        Collection<Quad> decremented = new ArrayList<>();
        Collection<Quad> added = new ArrayList<>();
        Collection<Quad> removed = new ArrayList<>();
        try {
            for (Quad quad : changeset.getAdded()) {
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
                    added.add(new Quad((GraphNode) pGraph, (SubjectNode) pSubject, (Property) pProperty, pObject));
                } else if (result == ADD_RESULT_INCREMENT) {
                    incremented.add(new Quad((GraphNode) pGraph, (SubjectNode) pSubject, (Property) pProperty, pObject));
                }
            }
            for (Quad quad : changeset.getRemoved()) {
                PersistedNode pSubject = nodes.getPersistent(quad.getSubject(), false);
                PersistedNode pProperty = nodes.getPersistent(quad.getProperty(), false);
                PersistedNode pObject = nodes.getPersistent(quad.getObject(), false);
                PersistedNode pGraph = nodes.getPersistent(quad.getGraph(), false);
                if (pSubject == null || pProperty == null || pObject == null || pGraph == null)
                    continue;
                int result = doQuadRemove(pSubject, pProperty, pObject, pGraph);
                if (result >= REMOVE_RESULT_REMOVED) {
                    doQuadDeindex(pSubject, pGraph);
                    pSubject.decrementRefCount();
                    pProperty.decrementRefCount();
                    pObject.decrementRefCount();
                    pGraph.decrementRefCount();
                    removed.add(new Quad((GraphNode) pGraph, (SubjectNode) pSubject, (Property) pProperty, pObject));
                } else if (result == REMOVE_RESULT_DECREMENT) {
                    decremented.add(new Quad((GraphNode) pGraph, (SubjectNode) pSubject, (Property) pProperty, pObject));
                }
            }
        } catch (UnsupportedNodeType exception) {
            // rollback the previously inserted quads
            for (Quad quad : incremented)
                doQuadRemove((PersistedNode) quad.getGraph(), (PersistedNode) quad.getSubject(), (PersistedNode) quad.getProperty(), (PersistedNode) quad.getObject());
            for (Quad quad : added) {
                doQuadRemove((PersistedNode) quad.getGraph(), (PersistedNode) quad.getSubject(), (PersistedNode) quad.getProperty(), (PersistedNode) quad.getObject());
                ((PersistedNode) quad.getGraph()).decrementRefCount();
                ((PersistedNode) quad.getSubject()).decrementRefCount();
                ((PersistedNode) quad.getProperty()).decrementRefCount();
                ((PersistedNode) quad.getObject()).decrementRefCount();
            }
            for (Quad quad : decremented)
                doQuadAdd((PersistedNode) quad.getGraph(), (PersistedNode) quad.getSubject(), (PersistedNode) quad.getProperty(), (PersistedNode) quad.getObject());
            for (Quad quad : removed) {
                doQuadAdd((PersistedNode) quad.getGraph(), (PersistedNode) quad.getSubject(), (PersistedNode) quad.getProperty(), (PersistedNode) quad.getObject());
                ((PersistedNode) quad.getGraph()).incrementRefCount();
                ((PersistedNode) quad.getSubject()).incrementRefCount();
                ((PersistedNode) quad.getProperty()).incrementRefCount();
                ((PersistedNode) quad.getObject()).incrementRefCount();
            }
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
            PersistedNode pSubject = nodes.getPersistent(quad.getSubject(), false);
            PersistedNode pProperty = nodes.getPersistent(quad.getProperty(), false);
            PersistedNode pObject = nodes.getPersistent(quad.getObject(), false);
            PersistedNode pGraph = nodes.getPersistent(quad.getGraph(), false);
            if (pSubject == null || pProperty == null || pObject == null || pGraph == null)
                // the quad cannot be in this store
                return;
            removeAll(pGraph, pSubject, pProperty, pObject);
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
            PersistedNode pSubject = nodes.getPersistent(subject, false);
            PersistedNode pProperty = nodes.getPersistent(property, false);
            PersistedNode pObject = nodes.getPersistent(value, false);
            PersistedNode pGraph = nodes.getPersistent(graph, false);
            if (pSubject == null || pProperty == null || pObject == null || pGraph == null)
                // the quad cannot be in this store
                return;
            removeAll(pGraph, pSubject, pProperty, pObject);
        }
    }

    /**
     * Removes all the quads matching the specified data
     *
     * @param graph    The graph to match
     * @param subject  The subject to match
     * @param property The property to match
     * @param object   The object to match
     */
    private void removeAll(PersistedNode graph, PersistedNode subject, PersistedNode property, PersistedNode object) {
        List<PersistedQuad> bufferDecremented = new ArrayList<>();
        List<PersistedQuad> bufferRemoved = new ArrayList<>();
        if (subject != null)
            removeAllOnSingleSubject(graph, subject, property, object, bufferDecremented, bufferRemoved);
        else if (graph != null)
            removeAllOnSingleGraph(graph, property, object, bufferDecremented, bufferRemoved);
        else
            removeAllDefault(property, object, bufferDecremented, bufferRemoved);
        if (!bufferDecremented.isEmpty() || !bufferRemoved.isEmpty()) {
            Changeset changeset = new Changeset(Collections.EMPTY_LIST, Collections.EMPTY_LIST, (Collection) bufferDecremented, (Collection) bufferRemoved);
            for (ChangeListener listener : listeners) {
                listener.onChange(changeset);
            }
        }
    }

    /**
     * Removes matching quads from a single subject
     *
     * @param graph             The graph to match
     * @param subject           The subject
     * @param property          The property to match
     * @param object            The object to match
     * @param bufferDecremented The buffer of decremented quads
     * @param bufferRemoved     The buffer of removed quads
     */
    private void removeAllOnSingleSubject(PersistedNode graph, PersistedNode subject, PersistedNode property, PersistedNode object, List<PersistedQuad> bufferDecremented, List<PersistedQuad> bufferRemoved) {
        try {
            Map<Long, Long> map = mapFor(subject);
            Long key = map.get(subject.getKey());
            if (key == null)
                return;
            int size = bufferRemoved.size();
            boolean isEmpty = removeAllOnSubject(key, property, object, graph, bufferDecremented, bufferRemoved);
            for (int i = size; i != bufferRemoved.size(); i++) {
                PersistedQuad quad = bufferRemoved.get(i);
                doQuadDeindex((PersistedNode) quad.getSubject(), (PersistedNode) quad.getGraph());
            }
            if (isEmpty)
                map.remove(subject.getKey());
        } catch (UnsupportedNodeType exception) {
            // do nothing
        }
    }

    /**
     * Removes matching quads from a single graph
     *
     * @param graph             The graph to match
     * @param property          The property to match
     * @param object            The object to match
     * @param bufferDecremented The buffer of decremented quads
     * @param bufferRemoved     The buffer of removed quads
     */
    private void removeAllOnSingleGraph(PersistedNode graph, PersistedNode property, PersistedNode object, List<PersistedQuad> bufferDecremented, List<PersistedQuad> bufferRemoved) {
        Map<Long, Long> map = graph.getNodeType() == Node.TYPE_IRI ? mapIndexGraphIRI : mapIndexGraphBlank;
        Long bucket = map.get(graph.getKey());
        if (bucket == null)
            return;
        long newBucket = bucket;
        long previous = PersistedNode.KEY_NOT_PRESENT;
        long current = bucket;
        long next;
        while (current != PersistedNode.KEY_NOT_PRESENT) {
            next = PersistedNode.KEY_NOT_PRESENT;
            int count = -1;
            try (IOElement element = backend.read(current)) {
                next = element.readLong();
                int radical = element.readInt();
                count = element.readInt();
                for (int i = 0; i != GINDEX_ENTRY_MAX_ITEM_COUNT; i++) {
                    int sk = element.readInt();
                    int multiplicity = element.readInt();
                    if (sk != PersistedNode.SERIALIZED_SIZE) {
                        long child = FileStore.getFullKey(radical, sk);
                        int size = bufferRemoved.size();
                        boolean isEmpty = removeAllOnSubject(child, property, object, graph, bufferDecremented, bufferRemoved);
                        if (isEmpty) {
                            try (IOElement subjectEntry = backend.read(child)) {
                                PersistedNode subject = getNode(subjectEntry.seek(8).readInt(), subjectEntry.readLong());
                                Map<Long, Long> mapSubjects = mapFor(subject);
                                mapSubjects.remove(subject.getKey());
                            } catch (IOException | StorageException | UnsupportedNodeType exception) {
                                // TODO: handle this
                            }
                        }
                        multiplicity -= bufferRemoved.size() - size;
                        if (multiplicity <= 0) {
                            element.seek(i * GINDEX_ENTRY_SIZE + 8 + 4 + 4);
                            element.writeInt(-1);
                            element.writeInt(0);
                            count--;
                        } else {
                            element.seek(i * GINDEX_ENTRY_SIZE + 8 + 4 + 4 + 4).writeInt(multiplicity);
                        }
                    }
                }
                element.seek(8 + 4).writeInt(count);
            } catch (IOException | StorageException exception) {
                // TODO: handle this
            }
            if (count == 0) {
                try {
                    backend.remove(current);
                } catch (IOException | StorageException exception) {
                    // TODO: handle this
                }
                if (previous == PersistedNode.KEY_NOT_PRESENT) {
                    // the first element
                    newBucket = next;
                    current = PersistedNode.KEY_NOT_PRESENT;
                } else {
                    try (IOElement element = backend.access(previous)) {
                        element.writeLong(next);
                    } catch (IOException | StorageException exception) {
                        // TODO: handle this
                    }
                    current = previous;
                }
            }
            previous = current;
            current = next;
        }

        if (newBucket == PersistedNode.KEY_NOT_PRESENT) {
            // graph completely removed
            map.remove(graph.getKey());
        } else if (newBucket != bucket) {
            map.put(graph.getKey(), newBucket);
        }
    }

    /**
     * Removes matching quads from the store
     *
     * @param property          The property to match
     * @param object            The object to match
     * @param bufferDecremented The buffer of decremented quads
     * @param bufferRemoved     The buffer of removed quads
     */
    private void removeAllDefault(PersistedNode property, PersistedNode object, List<PersistedQuad> bufferDecremented, List<PersistedQuad> bufferRemoved) {
        removeAllDefault(mapSubjectIRI, property, object, bufferDecremented, bufferRemoved);
        removeAllDefault(mapSubjectBlank, property, object, bufferDecremented, bufferRemoved);
        removeAllDefault(mapSubjectAnon, property, object, bufferDecremented, bufferRemoved);
    }

    /**
     * Removes matching quads from the store
     *
     * @param map               The map of subjects
     * @param property          The property to match
     * @param object            The object to match
     * @param bufferDecremented The buffer of decremented quads
     * @param bufferRemoved     The buffer of removed quads
     */
    private void removeAllDefault(Map<Long, Long> map, PersistedNode property, PersistedNode object, List<PersistedQuad> bufferDecremented, List<PersistedQuad> bufferRemoved) {
        List<Long> toRemove = new ArrayList<>();
        for (Map.Entry<Long, Long> entry : map.entrySet()) {
            int size = bufferRemoved.size();
            boolean isEmpty = removeAllOnSubject(entry.getValue(), property, object, null, bufferDecremented, bufferRemoved);
            for (int i = size; i != bufferRemoved.size(); i++) {
                PersistedQuad quad = bufferRemoved.get(i);
                doQuadDeindex((PersistedNode) quad.getSubject(), (PersistedNode) quad.getGraph());
            }
            if (isEmpty)
                toRemove.add(entry.getKey());
        }
        for (Long key : toRemove)
            map.remove(key);
    }

    /**
     * Removes matching quads from a single subject
     *
     * @param subjectKey        The key to the subject
     * @param property          The property to match
     * @param object            The object to match
     * @param graph             The graph to match
     * @param bufferDecremented The buffer of decremented quads
     * @param bufferRemoved     The buffer of removed quads
     * @return Whether the subject has been emptied
     */
    private boolean removeAllOnSubject(long subjectKey, PersistedNode property, PersistedNode object, PersistedNode graph, List<PersistedQuad> bufferDecremented, List<PersistedQuad> bufferRemoved) {
        long child;
        int type;
        long key;
        try (IOElement element = backend.read(subjectKey)) {
            element.seek(8);
            type = element.readInt();
            key = element.readLong();
            child = element.readLong();
        } catch (IOException | StorageException exception) {
            // skip this
            return false;
        }

        SubjectNode rSubject = (SubjectNode) getNode(type, key);
        int sizeDecremented = bufferDecremented.size();
        int sizeRemoved = bufferRemoved.size();
        long newChild = removeAllOnProperty(child, property, object, graph, bufferDecremented, bufferRemoved);
        for (int i = sizeDecremented; i != bufferDecremented.size(); i++)
            bufferDecremented.get(i).setSubject(rSubject);
        for (int i = sizeRemoved; i != bufferRemoved.size(); i++)
            bufferRemoved.get(i).setSubject(rSubject);
        int modifier = sizeRemoved - bufferRemoved.size();
        if (modifier < 0)
            ((PersistedNode) rSubject).modifyRefCount(-modifier);

        if (newChild == PersistedNode.KEY_NOT_PRESENT) {
            // the child bucket is empty
            try {
                backend.remove(subjectKey);
            } catch (IOException | StorageException exception) {
                // TODO: handle this
            }
            return true;
        } else if (newChild != child) {
            // the head of the bucket of graphs changed
            try (IOElement element = backend.access(subjectKey)) {
                element.seek(8 + 4 + 8).writeLong(newChild);
            } catch (IOException | StorageException exception) {
                // skip this
                return false;
            }
        }
        return false;
    }

    /**
     * Removes matching quads from a bucket of properties
     *
     * @param bucket            The bucket of properties
     * @param property          The property to match
     * @param object            The object to match
     * @param graph             The graph to match
     * @param bufferDecremented The buffer of decremented quads
     * @param bufferRemoved     The buffer of removed quads
     * @return The key to the new bucket head, or KEY_NOT_PRESENT if all the bucket is emptied
     */
    private long removeAllOnProperty(long bucket, PersistedNode property, PersistedNode object, PersistedNode graph, List<PersistedQuad> bufferDecremented, List<PersistedQuad> bufferRemoved) {
        long previous = PersistedNode.KEY_NOT_PRESENT;
        long current = bucket;
        long next;
        while (current != PersistedNode.KEY_NOT_PRESENT) {
            long child;
            int type;
            long key;
            try (IOElement element = backend.read(current)) {
                next = element.readLong();
                type = element.readInt();
                key = element.readLong();
                child = element.readLong();
            } catch (IOException | StorageException exception) {
                // skip this
                return bucket;
            }

            if (property != null && (property.getNodeType() != type || property.getKey() != key)) {
                // the object is not matching
                previous = current;
                current = next;
                continue;
            }

            Property rProperty = property != null ? (Property) property : (Property) getNode(type, key);
            int sizeDecremented = bufferDecremented.size();
            int sizeRemoved = bufferRemoved.size();
            long newChild = removeAllOnObject(child, object, graph, bufferDecremented, bufferRemoved);
            for (int i = sizeDecremented; i != bufferDecremented.size(); i++)
                bufferDecremented.get(i).setProperty(rProperty);
            for (int i = sizeRemoved; i != bufferRemoved.size(); i++)
                bufferRemoved.get(i).setProperty(rProperty);
            int modifier = sizeRemoved - bufferRemoved.size();
            if (modifier < 0)
                ((PersistedNode) rProperty).modifyRefCount(-modifier);

            if (newChild == PersistedNode.KEY_NOT_PRESENT) {
                // the child bucket is empty
                try {
                    backend.remove(current);
                } catch (IOException | StorageException exception) {
                    // TODO: handle this
                }
                if (previous == PersistedNode.KEY_NOT_PRESENT) {
                    // this is the first one
                    bucket = next;
                    current = PersistedNode.KEY_NOT_PRESENT;
                } else {
                    try (IOElement element = backend.access(previous)) {
                        element.writeLong(next);
                    } catch (IOException | StorageException exception) {
                        // TODO: handle this
                    }
                    current = previous;
                }
            } else if (newChild != child) {
                // the head of the bucket of graphs changed
                try (IOElement element = backend.access(current)) {
                    element.seek(8 + 4 + 8).writeLong(newChild);
                } catch (IOException | StorageException exception) {
                    // skip this
                    return bucket;
                }
            }

            previous = current;
            current = next;
        }

        return bucket;
    }

    /**
     * Removes matching quads from a bucket of objects
     *
     * @param bucket            The bucket of objects
     * @param object            The object to match
     * @param graph             The graph to match
     * @param bufferDecremented The buffer of decremented quads
     * @param bufferRemoved     The buffer of removed quads
     * @return The key to the new bucket head, or KEY_NOT_PRESENT if all the bucket is emptied
     */
    private long removeAllOnObject(long bucket, PersistedNode object, PersistedNode graph, List<PersistedQuad> bufferDecremented, List<PersistedQuad> bufferRemoved) {
        long previous = PersistedNode.KEY_NOT_PRESENT;
        long current = bucket;
        long next;
        while (current != PersistedNode.KEY_NOT_PRESENT) {
            long child;
            int type;
            long key;
            try (IOElement element = backend.read(current)) {
                next = element.readLong();
                type = element.readInt();
                key = element.readLong();
                child = element.readLong();
            } catch (IOException | StorageException exception) {
                // skip this
                return bucket;
            }

            if (object != null && (object.getNodeType() != type || object.getKey() != key)) {
                // the object is not matching
                previous = current;
                current = next;
                continue;
            }

            Node rObject = object != null ? object : getNode(type, key);
            int sizeDecremented = bufferDecremented.size();
            int sizeRemoved = bufferRemoved.size();
            long newChild = removeAllOnGraph(child, graph, bufferDecremented, bufferRemoved);
            for (int i = sizeDecremented; i != bufferDecremented.size(); i++)
                bufferDecremented.get(i).setObject(rObject);
            for (int i = sizeRemoved; i != bufferRemoved.size(); i++)
                bufferRemoved.get(i).setObject(rObject);
            int modifier = sizeRemoved - bufferRemoved.size();
            if (modifier < 0)
                ((PersistedNode) rObject).modifyRefCount(-modifier);

            if (newChild == PersistedNode.KEY_NOT_PRESENT) {
                // the child bucket is empty
                try {
                    backend.remove(current);
                } catch (IOException | StorageException exception) {
                    // TODO: handle this
                }
                if (previous == PersistedNode.KEY_NOT_PRESENT) {
                    // this is the first one
                    bucket = next;
                    current = PersistedNode.KEY_NOT_PRESENT;
                } else {
                    try (IOElement element = backend.access(previous)) {
                        element.writeLong(next);
                    } catch (IOException | StorageException exception) {
                        // TODO: handle this
                    }
                    current = previous;
                }
            } else if (newChild != child) {
                // the head of the bucket of graphs changed
                try (IOElement element = backend.access(current)) {
                    element.seek(8 + 4 + 8).writeLong(newChild);
                } catch (IOException | StorageException exception) {
                    // skip this
                    return bucket;
                }
            }

            previous = current;
            current = next;
        }

        return bucket;
    }

    /**
     * Removes matching quads from a bucket of graphs
     *
     * @param bucket            The bucket of graphs
     * @param graph             The graph to match
     * @param bufferDecremented The buffer of decremented quads
     * @param bufferRemoved     The buffer of removed quads
     * @return The key to the new bucket head, or KEY_NOT_PRESENT if all the bucket is emptied
     */
    private long removeAllOnGraph(long bucket, PersistedNode graph, List<PersistedQuad> bufferDecremented, List<PersistedQuad> bufferRemoved) {
        long previous = PersistedNode.KEY_NOT_PRESENT;
        long current = bucket;
        long next;
        while (current != PersistedNode.KEY_NOT_PRESENT) {
            PersistedNode removedGraph = null;
            try (IOElement element = backend.access(current)) {
                next = element.readLong();
                int type = element.readInt();
                long key = element.readLong();
                long multiplicity = element.readLong();
                if (graph == null) {
                    multiplicity--;
                    PersistedNode g = getNode(type, key);
                    if (multiplicity <= 0) {
                        bufferRemoved.add(new PersistedQuad((GraphNode) g, multiplicity));
                        removedGraph = g;
                    } else {
                        bufferDecremented.add(new PersistedQuad((GraphNode) g, multiplicity));
                    }
                } else if (graph.getNodeType() == type && graph.getKey() == key) {
                    multiplicity--;
                    if (multiplicity <= 0) {
                        bufferRemoved.add(new PersistedQuad((GraphNode) graph, multiplicity));
                        removedGraph = graph;
                    } else {
                        bufferDecremented.add(new PersistedQuad((GraphNode) graph, multiplicity));
                    }
                }
            } catch (IOException | StorageException exception) {
                return bucket;
            }

            if (removedGraph != null) {
                // multiplicity of the graph reached 0
                removedGraph.decrementRefCount();
                if (graph == null) {
                    // remove all graphs
                    try {
                        backend.remove(current);
                    } catch (IOException | StorageException exception) {
                        // TODO: handle this
                    }
                } else {
                    // remove this element from the linked list
                    try {
                        backend.remove(current);
                    } catch (IOException | StorageException exception) {
                        // TODO: handle this
                    }
                    if (previous == PersistedNode.KEY_NOT_PRESENT) {
                        // this is the first one
                        return next;
                    } else {
                        try (IOElement element = backend.access(previous)) {
                            element.writeLong(next);
                        } catch (IOException | StorageException exception) {
                            // TODO: handle this
                        }
                        return bucket;
                    }
                }
            }

            previous = current;
            current = next;
        }
        if (graph == null)
            return PersistedNode.KEY_NOT_PRESENT;
        return bucket;
    }


    @Override
    public void clear() {
        List<PersistedQuad> buffer = new ArrayList<>();
        for (Long entry : mapSubjectIRI.values())
            clearOnSubject(entry, null, buffer);
        mapSubjectIRI.clear();
        for (Long entry : mapSubjectBlank.values())
            clearOnSubject(entry, null, buffer);
        mapSubjectBlank.clear();
        for (Long entry : mapSubjectAnon.values())
            clearOnSubject(entry, null, buffer);
        mapSubjectAnon.clear();
        if (!buffer.isEmpty()) {
            Changeset changeset = Changeset.fromRemoved((Collection) buffer);
            for (ChangeListener listener : listeners) {
                listener.onChange(changeset);
            }
        }
        mapIndexGraphIRI.clear();
        mapIndexGraphBlank.clear();
        backend.clear();
    }

    @Override
    public void clear(GraphNode graph) {
        if (graph == null) {
            clear();
            return;
        }
        PersistedNode pGraph;
        try {
            pGraph = nodes.getPersistent(graph, false);
        } catch (UnsupportedNodeType exception) {
            return;
        }
        if (pGraph == null)
            return;
        Map<Long, Long> map = pGraph.getNodeType() == Node.TYPE_IRI ? mapIndexGraphIRI : mapIndexGraphBlank;
        Long bucket = map.get(pGraph.getKey());
        if (bucket == null)
            return;
        long current = bucket;
        long next;
        List<PersistedQuad> buffer = new ArrayList<>();
        while (current != PersistedNode.KEY_NOT_PRESENT) {
            next = PersistedNode.KEY_NOT_PRESENT;
            try (IOElement element = backend.read(current)) {
                next = element.readLong();
                int radical = element.readInt();
                int count = element.readInt();
                for (int i = 0; i != GINDEX_ENTRY_MAX_ITEM_COUNT; i++) {
                    int sk = element.readInt();
                    element.readInt();
                    if (sk != PersistedNode.SERIALIZED_SIZE) {
                        long child = FileStore.getFullKey(radical, sk);
                        clearOnSubject(child, pGraph, buffer);
                        count--;
                        if (count == 0)
                            break;
                    }
                }
            } catch (IOException | StorageException exception) {
                // TODO: handle this
            }
            try {
                backend.remove(current);
            } catch (IOException | StorageException exception) {
                // TODO: handle this
            }
            current = next;
        }
        if (!buffer.isEmpty()) {
            Changeset changeset = Changeset.fromRemoved((Collection) buffer);
            for (ChangeListener listener : listeners) {
                listener.onChange(changeset);
            }
        }
        map.remove(pGraph.getKey());
    }

    /**
     * Clears quads from a single subject
     *
     * @param key    The key to the subject
     * @param graph  The graph to match
     * @param buffer The buffer of quads
     * @return Whether the subject is emptied
     */
    private boolean clearOnSubject(long key, PersistedNode graph, List<PersistedQuad> buffer) {
        long child;
        SubjectNode subject;
        try (IOElement element = backend.read(key)) {
            element.seek(8);
            subject = (SubjectNode) getNode(element.readInt(), element.readLong());
            child = element.readLong();
        } catch (IOException | StorageException exception) {
            // skip this
            return false;
        }

        int size = buffer.size();
        long newChild = clearOnProperty(child, graph, buffer);
        for (int i = size; i != buffer.size(); i++)
            buffer.get(i).setSubject(subject);
        int modifier = size - buffer.size();
        if (modifier < 0)
            ((PersistedNode) subject).modifyRefCount(-modifier);

        if (newChild == PersistedNode.KEY_NOT_PRESENT) {
            // the child bucket is empty
            try {
                backend.remove(key);
            } catch (IOException | StorageException exception) {
                // TODO: handle this
            }
            return true;
        } else if (newChild != child) {
            // the head of the bucket of graphs changed
            try (IOElement element = backend.access(key)) {
                element.seek(8 + 4 + 8).writeLong(newChild);
            } catch (IOException | StorageException exception) {
                // skip this
                return false;
            }
        }
        return false;
    }

    /**
     * Clears quads from a bucket of properties
     *
     * @param bucket The bucket of properties
     * @param graph  The graph to match
     * @param buffer The buffer of quads
     * @return The key to the new bucket head, or KEY_NOT_PRESENT if all the bucket is emptied
     */
    private long clearOnProperty(long bucket, PersistedNode graph, List<PersistedQuad> buffer) {
        long previous = PersistedNode.KEY_NOT_PRESENT;
        long current = bucket;
        long next;
        while (current != PersistedNode.KEY_NOT_PRESENT) {
            long child;
            Property property;
            try (IOElement element = backend.read(current)) {
                next = element.readLong();
                property = (Property) getNode(element.readInt(), element.readLong());
                child = element.readLong();
            } catch (IOException | StorageException exception) {
                // skip this
                return bucket;
            }

            int size = buffer.size();
            long newChild = clearOnObject(child, graph, buffer);
            for (int i = size; i != buffer.size(); i++)
                buffer.get(i).setProperty(property);
            int modifier = size - buffer.size();
            if (modifier < 0)
                ((PersistedNode) property).modifyRefCount(-modifier);

            if (newChild == PersistedNode.KEY_NOT_PRESENT) {
                // the child bucket is empty
                try {
                    backend.remove(current);
                } catch (IOException | StorageException exception) {
                    // TODO: handle this
                }
                if (previous == PersistedNode.KEY_NOT_PRESENT) {
                    // this is the first one
                    bucket = next;
                    current = PersistedNode.KEY_NOT_PRESENT;
                } else {
                    try (IOElement element = backend.access(previous)) {
                        element.writeLong(next);
                    } catch (IOException | StorageException exception) {
                        // TODO: handle this
                    }
                    current = previous;
                }
            } else if (newChild != child) {
                // the head of the bucket of graphs changed
                try (IOElement element = backend.access(current)) {
                    element.seek(8 + 4 + 8).writeLong(newChild);
                } catch (IOException | StorageException exception) {
                    // skip this
                    return bucket;
                }
            }

            previous = current;
            current = next;
        }

        return bucket;
    }

    /**
     * Clears quads from a bucket of objects
     *
     * @param bucket The bucket of objects
     * @param graph  The graph to match
     * @param buffer The buffer of quads
     * @return The key to the new bucket head, or KEY_NOT_PRESENT if all the bucket is emptied
     */
    private long clearOnObject(long bucket, PersistedNode graph, List<PersistedQuad> buffer) {
        long previous = PersistedNode.KEY_NOT_PRESENT;
        long current = bucket;
        long next;
        while (current != PersistedNode.KEY_NOT_PRESENT) {
            long child;
            Node object;
            try (IOElement element = backend.read(current)) {
                next = element.readLong();
                object = getNode(element.readInt(), element.readLong());
                child = element.readLong();
            } catch (IOException | StorageException exception) {
                // skip this
                return bucket;
            }

            int size = buffer.size();
            long newChild = clearOnGraph(child, graph, buffer);
            for (int i = size; i != buffer.size(); i++)
                buffer.get(i).setObject(object);
            int modifier = size - buffer.size();
            if (modifier < 0)
                ((PersistedNode) object).modifyRefCount(-modifier);

            if (newChild == PersistedNode.KEY_NOT_PRESENT) {
                // the child bucket is empty
                try {
                    backend.remove(current);
                } catch (IOException | StorageException exception) {
                    // TODO: handle this
                }
                if (previous == PersistedNode.KEY_NOT_PRESENT) {
                    // this is the first one
                    bucket = next;
                    current = PersistedNode.KEY_NOT_PRESENT;
                } else {
                    try (IOElement element = backend.access(previous)) {
                        element.writeLong(next);
                    } catch (IOException | StorageException exception) {
                        // TODO: handle this
                    }
                    current = previous;
                }
            } else if (newChild != child) {
                // the head of the bucket of graphs changed
                try (IOElement element = backend.access(current)) {
                    element.seek(8 + 4 + 8).writeLong(newChild);
                } catch (IOException | StorageException exception) {
                    // skip this
                    return bucket;
                }
            }

            previous = current;
            current = next;
        }

        return bucket;
    }

    /**
     * Clears quads from a bucket of graphs
     *
     * @param bucket The bucket of graphs
     * @param graph  The graph to match
     * @param buffer The buffer of quads
     * @return The key to the new bucket head, or KEY_NOT_PRESENT if all the bucket is emptied
     */
    private long clearOnGraph(long bucket, PersistedNode graph, List<PersistedQuad> buffer) {
        long previous = PersistedNode.KEY_NOT_PRESENT;
        long current = bucket;
        long next;
        while (current != PersistedNode.KEY_NOT_PRESENT) {
            boolean found = false;
            try (IOElement element = backend.read(current)) {
                next = element.readLong();
                int type = element.readInt();
                long key = element.readLong();
                if (graph == null) {
                    PersistedNode g = getNode(type, key);
                    g.decrementRefCount();
                    buffer.add(new PersistedQuad((GraphNode) g, element.readLong()));
                } else if (graph.getNodeType() == type && graph.getKey() == key) {
                    graph.decrementRefCount();
                    buffer.add(new PersistedQuad((GraphNode) graph, element.readLong()));
                    found = true;
                }
            } catch (IOException | StorageException exception) {
                return bucket;
            }

            if (graph == null) {
                // remove all quads
                try {
                    backend.remove(current);
                } catch (IOException | StorageException exception) {
                    // TODO: handle this
                }
            } else if (found) {
                // remove this element from the linked list
                try {
                    backend.remove(current);
                } catch (IOException | StorageException exception) {
                    // TODO: handle this
                }
                if (previous == PersistedNode.KEY_NOT_PRESENT) {
                    // this is the first one
                    return next;
                } else {
                    try (IOElement element = backend.access(previous)) {
                        element.writeLong(next);
                    } catch (IOException | StorageException exception) {
                        // TODO: handle this
                    }
                    return bucket;
                }
            }

            previous = current;
            current = next;
        }
        if (graph == null)
            return PersistedNode.KEY_NOT_PRESENT;
        return bucket;
    }

    @Override
    public void copy(GraphNode origin, GraphNode target, boolean overwrite) {
        // TODO: implement this
    }

    @Override
    public void move(GraphNode origin, GraphNode target) {
        // TODO: implement this
    }

    @Override
    public void close() throws Exception {
        backend.close();
        database.close();
    }
}
