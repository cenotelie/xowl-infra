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

package org.xowl.infra.store.storage.persistent;

import org.xowl.infra.store.rdf.*;
import org.xowl.infra.store.storage.UnsupportedNodeType;
import org.xowl.infra.store.storage.impl.DatasetImpl;
import org.xowl.infra.store.storage.impl.MQuad;
import org.xowl.infra.utils.collections.*;
import org.xowl.infra.utils.logging.Logger;

import java.io.File;
import java.util.*;

/**
 * Represents a persisted RDF dataset
 *
 * @author Laurent Wouters
 */
public class PersistedDataset extends DatasetImpl implements AutoCloseable {
    /**
     * The suffix for the index file
     */
    private static final String FILE_DATA = "quads";
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
     * Entry for the next blank value data
     */
    private static final long DATA_MAP_SUBJECT_IRI_ENTRY = 0x00010000L;
    /**
     * Entry for the string map data
     */
    private static final long DATA_MAP_SUBJECT_BLANK_ENTRY = 0x00010001L;
    /**
     * Entry for the literal map data
     */
    private static final long DATA_MAP_SUBJECT_ANON_ENTRY = 0x00010002L;
    /**
     * Entry for the string map data
     */
    private static final long DATA_MAP_INDEX_IRI_ENTRY = 0x00010003L;
    /**
     * Entry for the literal map data
     */
    private static final long DATA_MAP_INDEX_BLANK_ENTRY = 0x00010004L;

    /**
     * Iterator over the quad node in a bucket
     */
    private static class QNodeIterator implements Iterator<Long> {
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
            value = FileStore.KEY_NULL;
        }

        @Override
        public boolean hasNext() {
            return next != FileStore.KEY_NULL;
        }

        @Override
        public Long next() {
            try (IOAccess entry = backend.accessR(next)) {
                value = next;
                next = entry.readLong();
            } catch (StorageException exception) {
                Logger.DEFAULT.error(exception);
            }
            return value;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    /**
     * Iterator over the subject nodes in a graph index
     */
    private static class GraphQNodeIterator implements Iterator<Long> {
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
            } catch (StorageException exception) {
                Logger.DEFAULT.error(exception);
                this.next = FileStore.KEY_NULL;
            }
            this.value = FileStore.KEY_NULL;
        }

        /**
         * Finds the next quad node
         *
         * @return The next quad node
         * @throws StorageException When the page version does not match the expected one
         */
        private long findNext() throws StorageException {
            while (true) {
                index++;
                if (index == GINDEX_ENTRY_MAX_ITEM_COUNT) {
                    try (IOAccess entry = backend.accessR(keyEntry)) {
                        keyEntry = entry.readLong();
                    }
                    if (keyEntry == FileStore.KEY_NULL)
                        return FileStore.KEY_NULL;
                    index = 0;
                }
                try (IOAccess entry = backend.accessR(keyEntry)) {
                    radical = entry.seek(8).readInt();
                    for (int i = index; i != GINDEX_ENTRY_MAX_ITEM_COUNT; i++) {
                        int ek = entry.seek(8 + 4 + 4 + i * 8).readInt();
                        if (ek != FileStore.KEY_NULL) {
                            index = i;
                            return FileStore.getFullKey(radical, ek);
                        }
                    }
                }
            }
        }

        @Override
        public boolean hasNext() {
            return next != FileStore.KEY_NULL;
        }

        @Override
        public Long next() {
            try {
                value = next;
                next = findNext();
            } catch (StorageException exception) {
                Logger.DEFAULT.error(exception);
            }
            return value;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    /**
     * The persisted nodes associated to this dataset
     */
    private final PersistedNodes nodes;
    /**
     * The backing storing the nodes' data
     */
    private final FileStore store;
    /**
     * The subject map for IRIs
     */
    private final PersistedMap mapSubjectIRI;
    /**
     * The subject map for Blanks
     */
    private final PersistedMap mapSubjectBlank;
    /**
     * The subject map for Anons
     */
    private final PersistedMap mapSubjectAnon;
    /**
     * The index map for IRI graphs
     */
    private final PersistedMap mapIndexGraphIRI;
    /**
     * The index map for blank graphs
     */
    private final PersistedMap mapIndexGraphBlank;
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
     * @throws StorageException When the storage is in a bad state
     */
    public PersistedDataset(PersistedNodes nodes, File directory, boolean isReadonly) throws StorageException {
        this.nodes = nodes;
        this.store = new FileStore(directory, FILE_DATA, isReadonly);
        PersistedMap tempMapSubjectIRI;
        PersistedMap tempMapSubjectBlank;
        PersistedMap tempMapSubjectAnon;
        PersistedMap tempMapIndexIRI;
        PersistedMap tempMapIndexBlank;
        if (store.isEmpty()) {
            tempMapSubjectIRI = PersistedMap.create(store);
            tempMapSubjectBlank = PersistedMap.create(store);
            tempMapSubjectAnon = PersistedMap.create(store);
            tempMapIndexIRI = PersistedMap.create(store);
            tempMapIndexBlank = PersistedMap.create(store);
        } else {
            tempMapSubjectIRI = new PersistedMap(store, DATA_MAP_SUBJECT_IRI_ENTRY);
            tempMapSubjectBlank = new PersistedMap(store, DATA_MAP_SUBJECT_BLANK_ENTRY);
            tempMapSubjectAnon = new PersistedMap(store, DATA_MAP_SUBJECT_ANON_ENTRY);
            tempMapIndexIRI = new PersistedMap(store, DATA_MAP_INDEX_IRI_ENTRY);
            tempMapIndexBlank = new PersistedMap(store, DATA_MAP_INDEX_BLANK_ENTRY);
        }
        mapSubjectIRI = tempMapSubjectIRI;
        mapSubjectBlank = tempMapSubjectBlank;
        mapSubjectAnon = tempMapSubjectAnon;
        mapIndexGraphIRI = tempMapIndexIRI;
        mapIndexGraphBlank = tempMapIndexBlank;
    }

    /**
     * Flushes any outstanding changes to the backing files
     *
     * @return Whether the operation succeeded
     */
    public boolean flush() {
        return store.flush();
    }

    @Override
    public long getMultiplicity(GraphNode graph, SubjectNode subject, Property property, Node object) throws UnsupportedNodeType {
        try {
            PersistedNode pGraph = nodes.getPersistent(graph, false);
            if (pGraph == null)
                return 0;
            PersistedNode pSubject = nodes.getPersistent(subject, false);
            if (pSubject == null)
                return 0;
            PersistedNode pProperty = nodes.getPersistent(property, false);
            if (pProperty == null)
                return 0;
            PersistedNode pObject = nodes.getPersistent(object, false);
            if (pObject == null)
                return 0;
            return getMultiplicity(pGraph, pSubject, pProperty, pObject);
        } catch (StorageException exception) {
            Logger.DEFAULT.error(exception);
            return 0;
        }
    }

    /**
     * Gets the multiplicity of a quad
     *
     * @param graph    The graph
     * @param subject  The subject
     * @param property The property
     * @param object   The object
     * @return The multiplicity in this store
     * @throws UnsupportedNodeType When a specified node is unsupported
     * @throws StorageException    When an IO operation failed
     */
    private long getMultiplicity(PersistedNode graph, PersistedNode subject, PersistedNode property, PersistedNode object) throws StorageException, UnsupportedNodeType {
        PersistedMap map = mapFor(subject);
        long bucket = map.get(subject.getKey());
        if (bucket == FileStore.KEY_NULL)
            return 0;
        bufferQNSubject = bucket;
        long target = lookupQNode(bufferQNSubject, property, false);
        if (target == FileStore.KEY_NULL)
            return 0;
        target = lookupQNode(target, object, false);
        if (target == FileStore.KEY_NULL)
            return 0;
        target = lookupQNode(target, graph, false);
        if (target == FileStore.KEY_NULL)
            return 0;
        try (IOAccess entry = store.accessW(target)) {
            return entry.seek(QUAD_ENTRY_SIZE - 8).readLong();
        }
    }

    @Override
    public Iterator<Quad> getAll(GraphNode graph, SubjectNode subject, Property property, Node object) throws UnsupportedNodeType {
        try {
            if (subject != null && subject.getNodeType() != Node.TYPE_VARIABLE)
                return getAllOnSingleSubject(graph, subject, property, object);
            if (graph != null && graph.getNodeType() != Node.TYPE_VARIABLE)
                return getAllOnSingleGraph(graph, property, object);
            return getAllDefault(property, object);
        } catch (StorageException exception) {
            Logger.DEFAULT.error(exception);
            return new SingleIterator<>(null);
        }
    }

    /**
     * Gets an iterator over all quads from a single subject
     *
     * @param graph    A containing graph to match, or null
     * @param subject  A subject node to match, or null
     * @param property A property to match, or null
     * @param object   An object node to match, or null
     * @return An iterator over the results
     * @throws UnsupportedNodeType When a specified node is unsupported
     * @throws StorageException    When an IO operation failed
     */
    private Iterator<Quad> getAllOnSingleSubject(GraphNode graph, final SubjectNode subject, Property property, Node object) throws StorageException, UnsupportedNodeType {
        PersistedNode pSubject = nodes.getPersistent(subject, false);
        if (pSubject == null)
            return new SingleIterator<>(null);
        long current = mapFor(pSubject).get(pSubject.getKey());
        if (current == FileStore.KEY_NULL)
            return new SingleIterator<>(null);
        try (IOAccess entry = store.accessR(current)) {
            long bucket = entry.seek(8 + 4 + 8).readLong();
            return new AdaptingIterator<>(getAllOnProperty(bucket, property, object, graph), new Adapter<Quad>() {
                @Override
                public <X> Quad adapt(X element) {
                    MQuad quad = (MQuad) element;
                    quad.setSubject(subject);
                    return quad;
                }
            });
        }
    }

    /**
     * Gets an iterator over all quads from a single graph
     *
     * @param graph    A containing graph to match, or null
     * @param property A property to match, or null
     * @param object   An object node to match, or null
     * @return An iterator over the results
     * @throws UnsupportedNodeType When a specified node is unsupported
     * @throws StorageException    When an IO operation failed
     */
    private Iterator<Quad> getAllOnSingleGraph(GraphNode graph, final Property property, final Node object) throws StorageException, UnsupportedNodeType {
        final PersistedNode pGraph = nodes.getPersistent(graph, false);
        if (pGraph == null)
            return new SingleIterator<>(null);
        PersistedMap map = pGraph.getNodeType() == Node.TYPE_IRI ? mapIndexGraphIRI : mapIndexGraphBlank;
        long bucket = map.get(pGraph.getKey());
        if (bucket == FileStore.KEY_NULL)
            return new SingleIterator<>(null);
        Iterator<Long> iteratorSubjects = new GraphQNodeIterator(store, bucket);
        return new AdaptingIterator<>(new CombiningIterator<>(iteratorSubjects, new Adapter<Iterator<MQuad>>() {
            @Override
            public <X> Iterator<MQuad> adapt(X element) {
                long subjectKey = ((Long) element);
                try (IOAccess entry = store.accessR(subjectKey)) {
                    long propertyBucket = entry.seek(8 + 4 + 8).readLong();
                    return getAllOnProperty(propertyBucket, property, object, (GraphNode) pGraph);
                } catch (UnsupportedNodeType | StorageException exception) {
                    Logger.DEFAULT.error(exception);
                    return null;
                }
            }
        }), new Adapter<Quad>() {
            @Override
            public <X> Quad adapt(X element) {
                Couple<Long, MQuad> couple = (Couple<Long, MQuad>) element;
                long subjectKey = couple.x;
                try (IOAccess entry = store.accessR(subjectKey)) {
                    couple.y.setSubject((SubjectNode) getNode(entry.seek(8).readInt(), entry.readLong()));
                } catch (StorageException exception) {
                    Logger.DEFAULT.error(exception);
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
     * @throws StorageException When an IO operation failed
     */
    private Iterator<Quad> getAllDefault(final Property property, final Node object) throws StorageException {
        return new AdaptingIterator<>(new CombiningIterator<>(getAllSubjects(), new Adapter<Iterator<MQuad>>() {
            @Override
            public <X> Iterator<MQuad> adapt(X element) {
                long subjectKey = (Long) element;
                try (IOAccess entry = store.accessR(subjectKey)) {
                    long propertyBucket = entry.seek(8 + 4 + 8).readLong();
                    return getAllOnProperty(propertyBucket, property, object, null);
                } catch (UnsupportedNodeType | StorageException exception) {
                    Logger.DEFAULT.error(exception);
                    return null;
                }
            }
        }), new Adapter<Quad>() {
            @Override
            public <X> Quad adapt(X element) {
                Couple<Long, MQuad> couple = (Couple<Long, MQuad>) element;
                long subjectKey = couple.x;
                try (IOAccess entry = store.accessR(subjectKey)) {
                    couple.y.setSubject((SubjectNode) getNode(entry.seek(8).readInt(), entry.readLong()));
                    return couple.y;
                } catch (StorageException exception) {
                    Logger.DEFAULT.error(exception);
                    return null;
                }
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
     * @throws UnsupportedNodeType When a specified node is unsupported
     * @throws StorageException    When an IO operation failed
     */
    private Iterator<MQuad> getAllOnProperty(long bucket, final Property property, final Node object, final GraphNode graph) throws UnsupportedNodeType, StorageException {
        if (property == null || property.getNodeType() == Node.TYPE_VARIABLE) {
            return new AdaptingIterator<>(new CombiningIterator<>(new QNodeIterator(store, bucket), new Adapter<Iterator<MQuad>>() {
                @Override
                public <X> Iterator<MQuad> adapt(X element) {
                    long key = ((Long) element);
                    try (IOAccess entry = store.accessR(key)) {
                        entry.seek(8 + 4 + 8);
                        long objectKey = entry.readLong();
                        return getAllOnObject(objectKey, object, graph);
                    } catch (UnsupportedNodeType | StorageException exception) {
                        Logger.DEFAULT.error(exception);
                        return null;
                    }
                }
            }), new Adapter<MQuad>() {
                @Override
                public <X> MQuad adapt(X element) {
                    Couple<Long, MQuad> couple = (Couple<Long, MQuad>) element;
                    long key = couple.x;
                    try (IOAccess entry = store.accessR(key)) {
                        entry.seek(8);
                        PersistedNode node = getNode(entry.readInt(), entry.readLong());
                        couple.y.setProperty((Property) node);
                    } catch (StorageException exception) {
                        Logger.DEFAULT.error(exception);
                        return null;
                    }
                    return couple.y;
                }
            });
        }
        PersistedNode pProperty = nodes.getPersistent(property, false);
        if (pProperty == null)
            return new SingleIterator<>(null);
        long current = bucket;
        while (current != FileStore.KEY_NULL) {
            try (IOAccess entry = store.accessR(current)) {
                current = entry.readLong();
                int type = entry.readInt();
                long key = entry.readLong();
                long child = entry.readLong();
                if (type == pProperty.getNodeType() && key == pProperty.getKey()) {
                    return new AdaptingIterator<>(getAllOnObject(child, object, graph), new Adapter<MQuad>() {
                        @Override
                        public <X> MQuad adapt(X element) {
                            MQuad quad = (MQuad) element;
                            quad.setProperty(property);
                            return quad;
                        }
                    });
                }
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
     * @throws UnsupportedNodeType When a specified node is unsupported
     * @throws StorageException    When an IO operation failed
     */
    private Iterator<MQuad> getAllOnObject(long bucket, final Node object, final GraphNode graph) throws UnsupportedNodeType, StorageException {
        if (object == null || object.getNodeType() == Node.TYPE_VARIABLE) {
            return new AdaptingIterator<>(new CombiningIterator<>(new QNodeIterator(store, bucket), new Adapter<Iterator<MQuad>>() {
                @Override
                public <X> Iterator<MQuad> adapt(X element) {
                    long key = ((Long) element);
                    try (IOAccess entry = store.accessR(key)) {
                        entry.seek(8 + 4 + 8);
                        long graphKey = entry.readLong();
                        return getAllOnGraph(graphKey, graph);
                    } catch (UnsupportedNodeType | StorageException exception) {
                        Logger.DEFAULT.error(exception);
                        return null;
                    }
                }
            }), new Adapter<MQuad>() {
                @Override
                public <X> MQuad adapt(X element) {
                    Couple<Long, MQuad> couple = (Couple<Long, MQuad>) element;
                    long key = couple.x;
                    try (IOAccess entry = store.accessR(key)) {
                        entry.seek(8);
                        PersistedNode node = getNode(entry.readInt(), entry.readLong());
                        couple.y.setObject(node);
                    } catch (StorageException exception) {
                        Logger.DEFAULT.error(exception);
                        return null;
                    }
                    return couple.y;
                }
            });
        }
        PersistedNode pObject = nodes.getPersistent(object, false);
        if (pObject == null)
            return new SingleIterator<>(null);
        long current = bucket;
        while (current != FileStore.KEY_NULL) {
            try (IOAccess entry = store.accessR(current)) {
                current = entry.readLong();
                int type = entry.readInt();
                long key = entry.readLong();
                long child = entry.readLong();
                if (type == pObject.getNodeType() && key == pObject.getKey()) {
                    return new AdaptingIterator<>(getAllOnGraph(child, graph), new Adapter<MQuad>() {
                        @Override
                        public <X> MQuad adapt(X element) {
                            MQuad quad = (MQuad) element;
                            quad.setObject(object);
                            return quad;
                        }
                    });
                }
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
     * @throws UnsupportedNodeType When a specified node is unsupported
     * @throws StorageException    When an IO operation failed
     */
    private Iterator<MQuad> getAllOnGraph(long bucket, GraphNode graph) throws UnsupportedNodeType, StorageException {
        if (graph == null || graph.getNodeType() == Node.TYPE_VARIABLE) {
            return new AdaptingIterator<>(new QNodeIterator(store, bucket), new Adapter<MQuad>() {
                @Override
                public <X> MQuad adapt(X element) {
                    long key = ((Long) element);
                    try (IOAccess entry = store.accessR(key)) {
                        entry.seek(8);
                        PersistedNode node = getNode(entry.readInt(), entry.readLong());
                        long multiplicity = entry.readLong();
                        return new MQuad((GraphNode) node, multiplicity);
                    } catch (StorageException exception) {
                        Logger.DEFAULT.error(exception);
                        return null;
                    }
                }
            });
        }
        PersistedNode pGraph = nodes.getPersistent(graph, false);
        if (pGraph == null)
            return new SingleIterator<>(null);
        long current = bucket;
        while (current != FileStore.KEY_NULL) {
            try (IOAccess entry = store.accessR(current)) {
                current = entry.readLong();
                int type = entry.readInt();
                long key = entry.readLong();
                long multiplicity = entry.readLong();
                if (type == pGraph.getNodeType() && key == pGraph.getKey()) {
                    return new SingleIterator<>(new MQuad(graph, multiplicity));
                }
            }
        }
        return new SingleIterator<>(null);
    }

    @Override
    public Collection<GraphNode> getGraphs() {
        Collection<GraphNode> result = new ArrayList<>();
        try {

            Iterator<Map.Entry<Long, Long>> iterator = mapIndexGraphIRI.entries();
            while (iterator.hasNext()) {
                result.add(nodes.getIRINodeFor(iterator.next().getKey()));
            }
            iterator = mapIndexGraphBlank.entries();
            while (iterator.hasNext()) {
                result.add(nodes.getBlankNodeFor(iterator.next().getKey()));
            }
        } catch (StorageException exception) {
            Logger.DEFAULT.error(exception);
        }
        return result;
    }

    @Override
    public long count() {
        long result = 0;
        try {
            Iterator<Map.Entry<Long, Long>> iterator = mapIndexGraphIRI.entries();
            while (iterator.hasNext()) {
                result += count(mapIndexGraphIRI, iterator.next().getKey());
            }
            iterator = mapIndexGraphBlank.entries();
            while (iterator.hasNext()) {
                result += count(mapIndexGraphBlank, iterator.next().getKey());
            }
        } catch (StorageException exception) {
            Logger.DEFAULT.error(exception);
            return 0;
        }
        return result;
    }

    @Override
    public long count(GraphNode graph) {
        PersistedNode pGraph;
        try {
            pGraph = nodes.getPersistent(graph, false);
        } catch (UnsupportedNodeType exception) {
            Logger.DEFAULT.error(exception);
            return 0;
        }
        if (pGraph == null)
            return 0;
        PersistedMap map = pGraph.getNodeType() == Node.TYPE_IRI ? mapIndexGraphIRI : mapIndexGraphBlank;
        try {
            return count(map, pGraph.getKey());
        } catch (StorageException exception) {
            Logger.DEFAULT.error(exception);
            return 0;
        }
    }

    /**
     * Counts the number of quads in a graph
     *
     * @param map The corresponding graph index map
     * @param key The graph key
     * @return The number of quads
     * @throws StorageException When an IO operation failed
     */
    private long count(PersistedMap map, long key) throws StorageException {
        long bucket = map.get(key);
        if (bucket == FileStore.KEY_NULL)
            return 0;
        long result = 0;
        long current = bucket;
        while (current != FileStore.KEY_NULL) {
            try (IOAccess entry = store.accessR(current)) {
                long next = entry.readLong();
                int count = entry.seek(8 + 4).readInt();
                for (int i = 0; i != GINDEX_ENTRY_MAX_ITEM_COUNT; i++) {
                    int eK = entry.readInt();
                    int mult = entry.readInt();
                    if (eK != FileStore.KEY_NULL) {
                        result += mult;
                        count--;
                        if (count <= 0)
                            break;
                    }
                }
                current = next;
            }
        }
        return result;
    }

    @Override
    public long count(GraphNode graph, SubjectNode subject, Property property, Node object) throws UnsupportedNodeType {
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
            if (pSubject != null)
                return countOnSingleSubject(pGraph, pSubject, pProperty, pObject);
            if (pGraph != null)
                return countOnSingleGraph(pGraph, pProperty, pObject);
            return countDefault(pProperty, pObject);
        } catch (StorageException exception) {
            Logger.DEFAULT.error(exception);
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
     * @throws UnsupportedNodeType When a specified node is unsupported
     * @throws StorageException    When an IO operation failed
     */
    private long countOnSingleSubject(PersistedNode graph, PersistedNode subject, PersistedNode property, PersistedNode object) throws UnsupportedNodeType, StorageException {
        long current = mapFor(subject).get(subject.getKey());
        if (current == FileStore.KEY_NULL)
            return 0;
        long child;
        try (IOAccess entry = store.accessR(current)) {
            child = entry.seek(8 + 4 + 8).readLong();
        }
        return countOnProperty(child, graph, property, object);
    }

    /**
     * Counts the quads from a single graph entry
     *
     * @param graph    The graph to match
     * @param property The property to match
     * @param object   The object to match
     * @return The number of matching quads
     * @throws StorageException When an IO operation failed
     */
    private long countOnSingleGraph(PersistedNode graph, PersistedNode property, PersistedNode object) throws StorageException {
        PersistedMap map = graph.getNodeType() == Node.TYPE_IRI ? mapIndexGraphIRI : mapIndexGraphBlank;
        long bucket = map.get(graph.getKey());
        if (bucket == FileStore.KEY_NULL)
            return 0;
        long result = 0;
        long current = bucket;
        while (current != FileStore.KEY_NULL) {
            try (IOAccess entry = store.accessR(current)) {
                current = entry.readLong();
                int radical = entry.readInt();
                int count = entry.readInt();
                for (int i = 0; i != GINDEX_ENTRY_MAX_ITEM_COUNT; i++) {
                    int sk = entry.readInt();
                    entry.readInt();
                    if (sk != FileStore.KEY_NULL) {
                        long child = FileStore.getFullKey(radical, sk);
                        try (IOAccess subjectEntry = store.accessR(child)) {
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
    }

    /**
     * Counts the all the quads
     *
     * @param property The property to match
     * @param object   The object to match
     * @return The number of matching quads
     * @throws StorageException When an IO operation failed
     */
    private long countDefault(PersistedNode property, PersistedNode object) throws StorageException {
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
     * @throws StorageException When an IO operation failed
     */
    private long countDefault(PersistedMap map, PersistedNode property, PersistedNode object) throws StorageException {
        long result = 0;
        Iterator<Map.Entry<Long, Long>> iterator = map.entries();
        while (iterator.hasNext()) {
            long bucket;
            try (IOAccess entry = store.accessW(iterator.next().getValue())) {
                bucket = entry.seek(8 + 4 + 8).readLong();
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
     * @throws StorageException When an IO operation failed
     */
    private long countOnProperty(long bucket, PersistedNode graph, PersistedNode property, PersistedNode object) throws StorageException {
        long result = 0;
        long current = bucket;
        while (current != FileStore.KEY_NULL) {
            long child = FileStore.KEY_NULL;
            try (IOAccess element = store.accessR(current)) {
                current = element.readLong();
                if (property == null || (property.getNodeType() == element.readInt() && property.getKey() == element.readLong())) {
                    child = element.seek(8 + 4 + 8).readLong();
                }
            }
            if (child != FileStore.KEY_NULL)
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
     * @throws StorageException When an IO operation failed
     */
    private long countOnObject(long bucket, PersistedNode graph, PersistedNode object) throws StorageException {
        long result = 0;
        long current = bucket;
        while (current != FileStore.KEY_NULL) {
            long child = FileStore.KEY_NULL;
            try (IOAccess element = store.accessR(current)) {
                current = element.readLong();
                if (object == null || (object.getNodeType() == element.readInt() && object.getKey() == element.readLong())) {
                    child = element.seek(8 + 4 + 8).readLong();
                }
            }
            if (child != FileStore.KEY_NULL)
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
     * @throws StorageException When an IO operation failed
     */
    private long countOnGraph(long bucket, PersistedNode graph) throws StorageException {
        long result = 0;
        long current = bucket;
        while (current != FileStore.KEY_NULL) {
            try (IOAccess element = store.accessR(current)) {
                current = element.readLong();
                if (graph == null || (graph.getNodeType() == element.readInt() && graph.getKey() == element.readLong()))
                    result++;
            }
        }
        return result;
    }

    @Override
    public int doAddQuad(GraphNode graph, SubjectNode subject, Property property, Node value) throws UnsupportedNodeType {
        PersistedNode pSubject = nodes.getPersistent(subject, true);
        PersistedNode pProperty = nodes.getPersistent(property, true);
        PersistedNode pObject = nodes.getPersistent(value, true);
        PersistedNode pGraph = nodes.getPersistent(graph, true);
        int result;
        try {
            result = doQuadAdd(pSubject, pProperty, pObject, pGraph);
        } catch (StorageException exception) {
            Logger.DEFAULT.error(exception);
            return ADD_RESULT_UNKNOWN;
        }
        if (result == ADD_RESULT_NEW) {
            try {
                doQuadIndex(pSubject, pGraph);
            } catch (StorageException exception) {
                Logger.DEFAULT.error(exception);
                return ADD_RESULT_UNKNOWN;
            }
            try {
                pSubject.incrementRefCount();
            } catch (StorageException exception) {
                Logger.DEFAULT.error(exception);
            }
            try {
                pProperty.incrementRefCount();
            } catch (StorageException exception) {
                Logger.DEFAULT.error(exception);
            }
            try {
                pObject.incrementRefCount();
            } catch (StorageException exception) {
                Logger.DEFAULT.error(exception);
            }
            try {
                pGraph.incrementRefCount();
            } catch (StorageException exception) {
                Logger.DEFAULT.error(exception);
            }
        }
        return result;
    }

    /**
     * Inserts a quad into the backend
     *
     * @param subject  The subject
     * @param property The property
     * @param object   The object
     * @param graph    The graph
     * @return The result of the insertion
     * @throws UnsupportedNodeType When a specified node is unsupported
     * @throws StorageException    When an IO operation failed
     */
    private int doQuadAdd(PersistedNode subject, PersistedNode property, PersistedNode object, PersistedNode graph) throws UnsupportedNodeType, StorageException {
        PersistedMap map = mapFor(subject);
        long bucket = map.get(subject.getKey());
        if (bucket == FileStore.KEY_NULL) {
            bufferQNSubject = newEntry(subject);
            map.put(subject.getKey(), bufferQNSubject);
        } else {
            bufferQNSubject = bucket;
        }
        long target = lookupQNode(bufferQNSubject, property, true);
        target = lookupQNode(target, object, true);
        target = lookupQNode(target, graph, true);
        try (IOAccess entry = store.accessW(target)) {
            long value = entry.seek(QUAD_ENTRY_SIZE - 8).readLong();
            if (value == FileStore.KEY_NULL) {
                entry.seek(QUAD_ENTRY_SIZE - 8).writeLong(1);
                return ADD_RESULT_NEW;
            } else {
                value++;
                entry.seek(QUAD_ENTRY_SIZE - 8).writeLong(value);
                return ADD_RESULT_INCREMENT;
            }
        }
    }

    /**
     * Indexes a quad into the backend
     *
     * @param subject The subject
     * @param graph   The graph
     * @throws StorageException When an IO operation failed
     */
    private void doQuadIndex(PersistedNode subject, PersistedNode graph) throws StorageException {
        int radical = FileStore.getKeyRadical(subject.getKey());
        PersistedMap map = graph.getNodeType() == Node.TYPE_IRI ? mapIndexGraphIRI : mapIndexGraphBlank;
        long bucket = map.get(graph.getKey());
        if (bucket == FileStore.KEY_NULL) {
            // this is the first quad for this graph
            long key = writeNewGraphIndex(radical, bufferQNSubject);
            map.put(graph.getKey(), key);
            return;
        }
        // look for an appropriate entry
        long emptyEntry = FileStore.KEY_NULL;
        bufferQNPrevious = FileStore.KEY_NULL;
        long current = bucket;
        while (current != FileStore.KEY_NULL) {
            try (IOAccess entry = store.accessW(current)) {
                long next = entry.readLong();
                int eRadical = entry.readInt();
                if (eRadical != radical)
                    continue;
                int count = entry.readInt();
                if (emptyEntry == FileStore.KEY_NULL && count < GINDEX_ENTRY_MAX_ITEM_COUNT)
                    emptyEntry = current;
                for (int i = 0; i != GINDEX_ENTRY_MAX_ITEM_COUNT; i++) {
                    int qnode = entry.readInt();
                    int multiplicity = entry.readInt();
                    if (qnode == FileStore.KEY_NULL)
                        continue;
                    if (qnode == FileStore.getShortKey(bufferQNSubject)) {
                        multiplicity++;
                        entry.seek(i * 8 + 8 + 4 + 4 + 4).writeInt(multiplicity);
                        return;
                    }
                    count--;
                    if (count == 0)
                        break;
                }
                bufferQNPrevious = current;
                current = next;
            }
        }
        // not found in an entry
        if (emptyEntry != FileStore.KEY_NULL) {
            try (IOAccess entry = store.accessW(emptyEntry)) {
                int count = entry.seek(12).readInt();
                for (int i = 0; i != GINDEX_ENTRY_MAX_ITEM_COUNT; i++) {
                    int qnode = entry.readInt();
                    entry.readInt();
                    if (qnode == FileStore.KEY_NULL) {
                        entry.seek(i * 8 + 8 + 4 + 4);
                        entry.writeInt(FileStore.getShortKey(bufferQNSubject));
                        entry.writeInt(1);
                        break;
                    }
                }
                entry.seek(8 + 4).writeInt(count + 1);
            }
        } else {
            // requires a new entry
            long key = writeNewGraphIndex(radical, bufferQNSubject);
            try (IOAccess entry = store.accessW(bufferQNPrevious)) {
                entry.writeLong(key);
            }
        }
    }

    /**
     * Writes a new graph index entry
     *
     * @param radical The key radical for this entry
     * @param qnode   The key to the subject quad node
     * @return The key to this entry
     * @throws StorageException When an IO operation failed
     */
    private long writeNewGraphIndex(int radical, long qnode) throws StorageException {
        long key = store.allocate(GINDEX_ENTRY_SIZE);
        try (IOAccess entry = store.accessW(key)) {
            entry.writeLong(FileStore.KEY_NULL);
            entry.writeInt(radical);
            entry.writeInt(1);
            entry.writeInt(FileStore.getShortKey(qnode));
            entry.writeInt(1);
            for (int i = 1; i != GINDEX_ENTRY_MAX_ITEM_COUNT; i++) {
                entry.writeInt((int) FileStore.KEY_NULL);
                entry.writeInt(0);
            }
        }
        return key;
    }

    @Override
    public int doRemoveQuad(GraphNode graph, SubjectNode subject, Property property, Node value) throws UnsupportedNodeType {
        PersistedNode pSubject = nodes.getPersistent(subject, false);
        PersistedNode pProperty = nodes.getPersistent(property, false);
        PersistedNode pObject = nodes.getPersistent(value, false);
        PersistedNode pGraph = nodes.getPersistent(graph, false);
        if (pSubject == null || pProperty == null || pObject == null || pGraph == null)
            // the quad cannot be in this store
            return REMOVE_RESULT_NOT_FOUND;
        int result;
        try {
            result = doQuadRemove(pSubject, pProperty, pObject, pGraph);
        } catch (StorageException exception) {
            Logger.DEFAULT.error(exception);
            return REMOVE_RESULT_NOT_FOUND;
        }
        if (result >= REMOVE_RESULT_REMOVED) {
            try {
                doQuadDeindex(pSubject, pGraph);
            } catch (StorageException exception) {
                Logger.DEFAULT.error(exception);
            }
            try {
                pSubject.decrementRefCount();
            } catch (StorageException exception) {
                Logger.DEFAULT.error(exception);
            }
            try {
                pProperty.decrementRefCount();
            } catch (StorageException exception) {
                Logger.DEFAULT.error(exception);
            }
            try {
                pObject.decrementRefCount();
            } catch (StorageException exception) {
                Logger.DEFAULT.error(exception);
            }
            try {
                pGraph.decrementRefCount();
            } catch (StorageException exception) {
                Logger.DEFAULT.error(exception);
            }
        }
        return result;
    }

    /**
     * Removes a quad into the backend
     *
     * @param subject  The subject
     * @param property The property
     * @param object   The object
     * @param graph    The graph
     * @return The result of the removal
     * @throws UnsupportedNodeType When a specified node is unsupported
     * @throws StorageException    When an IO operation failed
     */
    private int doQuadRemove(PersistedNode subject, PersistedNode property, PersistedNode object, PersistedNode graph) throws UnsupportedNodeType, StorageException {
        PersistedMap map = mapFor(subject);
        Long bucket = map.get(subject.getKey());
        if (bucket == FileStore.KEY_NULL) {
            return REMOVE_RESULT_NOT_FOUND;
        }
        bufferQNSubject = bucket;
        long bufferQNProperty = lookupQNode(bufferQNSubject, property, false);
        if (bufferQNProperty == FileStore.KEY_NULL)
            return REMOVE_RESULT_NOT_FOUND;
        long keyPropertyPrevious = bufferQNPrevious;
        long bufferQNObject = lookupQNode(bufferQNProperty, object, false);
        if (bufferQNObject == FileStore.KEY_NULL)
            return REMOVE_RESULT_NOT_FOUND;
        long keyObjectPrevious = bufferQNPrevious;
        long bufferQNGraph = lookupQNode(bufferQNObject, graph, false);
        if (bufferQNGraph == FileStore.KEY_NULL)
            return REMOVE_RESULT_NOT_FOUND;
        long keyGraphPrevious = bufferQNPrevious;
        try (IOAccess entry = store.accessW(bufferQNGraph)) {
            long value = entry.seek(QUAD_ENTRY_SIZE - 8).readLong();
            value--;
            if (value > 0) {
                entry.seek(QUAD_ENTRY_SIZE - 8).writeLong(value);
                return REMOVE_RESULT_DECREMENT;
            }
        }

        // free the graph node
        long next;
        try (IOAccess entry = store.accessR(bufferQNGraph)) {
            next = entry.readLong();
        }
        if (keyGraphPrevious == bufferQNObject) {
            // the previous of the graph is the object
            if (next == FileStore.KEY_NULL) {
                // the last one
                store.free(bufferQNGraph);
            } else {
                try (IOAccess entry = store.accessW(bufferQNObject)) {
                    entry.seek(QUAD_ENTRY_SIZE - 8).writeLong(next);
                }
                store.free(bufferQNGraph);
                return REMOVE_RESULT_REMOVED;
            }
        } else {
            try (IOAccess entry = store.accessW(keyGraphPrevious)) {
                entry.writeLong(next);
            }
            store.free(bufferQNGraph);
            return REMOVE_RESULT_REMOVED;
        }

        // free the object node
        try (IOAccess entry = store.accessR(bufferQNObject)) {
            next = entry.readLong();
        }
        if (keyObjectPrevious == bufferQNProperty) {
            // the previous of the object is the property
            if (next == FileStore.KEY_NULL) {
                // the last one
                store.free(bufferQNObject);
            } else {
                try (IOAccess entry = store.accessW(bufferQNProperty)) {
                    entry.seek(QUAD_ENTRY_SIZE - 8).writeLong(next);
                }
                store.free(bufferQNObject);
                return REMOVE_RESULT_REMOVED;
            }
        } else {
            try (IOAccess entry = store.accessW(keyObjectPrevious)) {
                entry.writeLong(next);
            }
            store.free(bufferQNObject);
            return REMOVE_RESULT_REMOVED;
        }

        // free the property node
        try (IOAccess entry = store.accessR(bufferQNProperty)) {
            next = entry.readLong();
        }
        if (keyPropertyPrevious == bufferQNSubject) {
            // the previous of the property is the subject
            if (next == FileStore.KEY_NULL) {
                // the last one
                store.free(bufferQNProperty);
            } else {
                try (IOAccess entry = store.accessW(bufferQNSubject)) {
                    entry.seek(QUAD_ENTRY_SIZE - 8).writeLong(next);
                }
                store.free(bufferQNProperty);
                return REMOVE_RESULT_REMOVED;
            }
        } else {
            try (IOAccess entry = store.accessW(keyPropertyPrevious)) {
                entry.writeLong(next);
            }
            store.free(bufferQNProperty);
            return REMOVE_RESULT_REMOVED;
        }

        // free the subject node
        store.free(bufferQNSubject);
        map.remove(subject.getKey());
        return REMOVE_RESULT_EMPTIED;
    }

    /**
     * De-indexes a quad into the backend
     *
     * @param subject The subject
     * @param graph   The graph
     * @throws StorageException When an IO operation failed
     */
    private void doQuadDeindex(PersistedNode subject, PersistedNode graph) throws StorageException {
        int radical = ((int) (subject.getKey() >>> 32));
        PersistedMap map = graph.getNodeType() == Node.TYPE_IRI ? mapIndexGraphIRI : mapIndexGraphBlank;
        long bucket = map.get(graph.getKey());
        if (bucket == FileStore.KEY_NULL) {
            // this is the first quad for this graph
            return;
        }
        // look for an appropriate entry
        bufferQNPrevious = FileStore.KEY_NULL;
        long current = bucket;
        while (current != FileStore.KEY_NULL) {
            try (IOAccess entry = store.accessW(current)) {
                long next = entry.readLong();
                int eRadical = entry.readInt();
                if (eRadical != radical)
                    continue;
                int count = entry.readInt();
                int c = count;
                for (int i = 0; i != GINDEX_ENTRY_MAX_ITEM_COUNT; i++) {
                    int qnode = entry.readInt();
                    int multiplicity = entry.readInt();
                    if (qnode == FileStore.KEY_NULL)
                        continue;
                    if (qnode == FileStore.getShortKey(bufferQNSubject)) {
                        multiplicity--;
                        if (multiplicity > 0) {
                            entry.seek(i * 8 + 8 + 4 + 4 + 4).writeInt(multiplicity);
                            return;
                        }
                        count--;
                        if (count > 0) {
                            entry.seek(i * 8 + 8 + 4 + 4);
                            entry.writeInt((int) FileStore.KEY_NULL);
                            entry.writeInt(0);
                            entry.seek(8 + 4).writeInt(count);
                            return;
                        }
                        if (bufferQNPrevious == FileStore.KEY_NULL) {
                            // this is the first entry for this index
                            if (next == FileStore.KEY_NULL) {
                                // this is the sole entry
                                map.remove(graph.getKey());
                            } else {
                                map.put(graph.getKey(), next);
                            }
                        } else {
                            try (IOAccess pe = store.accessW(bufferQNPrevious)) {
                                pe.writeLong(next);
                            }
                        }
                        store.free(current);
                        return;
                    }
                    c--;
                    if (c == 0)
                        break;
                }
                bufferQNPrevious = current;
                current = next;
            }
        }
    }

    @Override
    public void doRemoveQuads(GraphNode graph, SubjectNode subject, Property property, Node value, List<MQuad> bufferDecremented, List<MQuad> bufferRemoved) throws UnsupportedNodeType {
        PersistedNode pSubject = nodes.getPersistent(subject, false);
        PersistedNode pProperty = nodes.getPersistent(property, false);
        PersistedNode pObject = nodes.getPersistent(value, false);
        PersistedNode pGraph = nodes.getPersistent(graph, false);
        if ((subject != null && pSubject == null)
                || (property != null && pProperty == null)
                || (value != null && pObject == null)
                || (graph != null && pGraph == null))
            // the quad cannot be in this store
            return;
        try {
            if (subject != null)
                removeAllOnSingleSubject(pGraph, pSubject, pProperty, pObject, bufferDecremented, bufferRemoved);
            else if (graph != null)
                removeAllOnSingleGraph(pGraph, pProperty, pObject, bufferDecremented, bufferRemoved);
            else
                removeAllDefault(pProperty, pObject, bufferDecremented, bufferRemoved);
        } catch (StorageException exception) {
            Logger.DEFAULT.error(exception);
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
     * @throws UnsupportedNodeType When a specified node is unsupported
     * @throws StorageException    When an IO operation failed
     */
    private void removeAllOnSingleSubject(PersistedNode graph, PersistedNode subject, PersistedNode property, PersistedNode object, List<MQuad> bufferDecremented, List<MQuad> bufferRemoved) throws UnsupportedNodeType, StorageException {
        PersistedMap map = mapFor(subject);
        long key = map.get(subject.getKey());
        if (key == FileStore.KEY_NULL)
            return;
        int size = bufferRemoved.size();
        boolean isEmpty = removeAllOnSubject(key, property, object, graph, bufferDecremented, bufferRemoved);
        for (int i = size; i != bufferRemoved.size(); i++) {
            MQuad quad = bufferRemoved.get(i);
            doQuadDeindex((PersistedNode) quad.getSubject(), (PersistedNode) quad.getGraph());
            ((PersistedNode) quad.getSubject()).decrementRefCount();
            ((PersistedNode) quad.getProperty()).decrementRefCount();
            ((PersistedNode) quad.getObject()).decrementRefCount();
            ((PersistedNode) quad.getGraph()).decrementRefCount();
        }
        if (isEmpty)
            map.remove(subject.getKey());

    }

    /**
     * Removes matching quads from a single graph
     *
     * @param graph             The graph to match
     * @param property          The property to match
     * @param object            The object to match
     * @param bufferDecremented The buffer of decremented quads
     * @param bufferRemoved     The buffer of removed quads
     * @throws StorageException When an IO operation failed
     */
    private void removeAllOnSingleGraph(PersistedNode graph, PersistedNode property, PersistedNode object, List<MQuad> bufferDecremented, List<MQuad> bufferRemoved) throws StorageException {
        PersistedMap map = graph.getNodeType() == Node.TYPE_IRI ? mapIndexGraphIRI : mapIndexGraphBlank;
        long bucket = map.get(graph.getKey());
        if (bucket == FileStore.KEY_NULL)
            return;
        long newBucket = bucket;
        long previous = FileStore.KEY_NULL;
        long current = bucket;
        long next;
        while (current != FileStore.KEY_NULL) {
            boolean empty = removeAllOnSingleGraphPage(current, graph, property, object, bufferDecremented, bufferRemoved);
            next = bufferQNPrevious;
            if (empty) {
                try {
                    store.free(current);
                } catch (StorageException exception) {
                    Logger.DEFAULT.error(exception);
                }
                if (previous == FileStore.KEY_NULL) {
                    // the first element
                    newBucket = next;
                    current = FileStore.KEY_NULL;
                } else {
                    try (IOAccess element = store.accessW(previous)) {
                        element.writeLong(next);
                    } catch (StorageException exception) {
                        Logger.DEFAULT.error(exception);
                    }
                    current = previous;
                }
            }
            previous = current;
            current = next;
        }

        if (newBucket == FileStore.KEY_NULL) {
            // graph completely removed
            map.remove(graph.getKey());
        } else if (newBucket != bucket) {
            map.put(graph.getKey(), newBucket);
        }
    }

    /**
     * Removes matching quads from a single graph - inspecting the specified page of graph index
     *
     * @param pageKey           The key to the page to inspect
     * @param graph             The graph to match
     * @param property          The property to match
     * @param object            The object to match
     * @param bufferDecremented The buffer of decremented quads
     * @param bufferRemoved     The buffer of removed quads
     * @return Whether the page is now empty
     * @throws StorageException When an IO operation failed
     */
    private boolean removeAllOnSingleGraphPage(long pageKey, PersistedNode graph, PersistedNode property, PersistedNode object, List<MQuad> bufferDecremented, List<MQuad> bufferRemoved) throws StorageException {
        try (IOAccess element = store.accessR(pageKey)) {
            bufferQNPrevious = element.readLong();
            int radical = element.readInt();
            int count = element.readInt();
            int c = count;
            for (int i = 0; i != GINDEX_ENTRY_MAX_ITEM_COUNT; i++) {
                int sk = element.readInt();
                int multiplicity = element.readInt();
                if (sk == PersistedNode.SERIALIZED_SIZE)
                    continue;
                long child = FileStore.getFullKey(radical, sk);
                int size = bufferRemoved.size();
                boolean isEmpty = removeAllOnSubject(child, property, object, graph, bufferDecremented, bufferRemoved);
                if (isEmpty) {
                    try (IOAccess subjectEntry = store.accessR(child)) {
                        PersistedNode subject = getNode(subjectEntry.seek(8).readInt(), subjectEntry.readLong());
                        PersistedMap mapSubjects = mapFor(subject);
                        mapSubjects.remove(subject.getKey());
                    } catch (StorageException | UnsupportedNodeType exception) {
                        Logger.DEFAULT.error(exception);
                    }
                }
                multiplicity -= bufferRemoved.size() - size;
                if (multiplicity <= 0) {
                    element.seek(i * 8 + 8 + 4 + 4);
                    element.writeInt((int) FileStore.KEY_NULL);
                    element.writeInt(0);
                    count--;
                } else {
                    element.seek(i * 8 + 8 + 4 + 4 + 4).writeInt(multiplicity);
                }
                c--;
                if (c == 0)
                    break;
            }
            element.seek(8 + 4).writeInt(count);
            return count == 0;
        }
    }

    /**
     * Removes matching quads from the store
     *
     * @param property          The property to match
     * @param object            The object to match
     * @param bufferDecremented The buffer of decremented quads
     * @param bufferRemoved     The buffer of removed quads
     * @throws StorageException When an IO operation failed
     */
    private void removeAllDefault(PersistedNode property, PersistedNode object, List<MQuad> bufferDecremented, List<MQuad> bufferRemoved) throws StorageException {
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
     * @throws StorageException When an IO operation failed
     */
    private void removeAllDefault(PersistedMap map, PersistedNode property, PersistedNode object, List<MQuad> bufferDecremented, List<MQuad> bufferRemoved) throws StorageException {
        Iterator<Map.Entry<Long, Long>> iterator = map.entries();
        while (iterator.hasNext()) {
            int size = bufferRemoved.size();
            boolean isEmpty = removeAllOnSubject(iterator.next().getValue(), property, object, null, bufferDecremented, bufferRemoved);
            for (int i = size; i != bufferRemoved.size(); i++) {
                MQuad quad = bufferRemoved.get(i);
                doQuadDeindex((PersistedNode) quad.getSubject(), (PersistedNode) quad.getGraph());
                ((PersistedNode) quad.getSubject()).decrementRefCount();
                ((PersistedNode) quad.getProperty()).decrementRefCount();
                ((PersistedNode) quad.getObject()).decrementRefCount();
                ((PersistedNode) quad.getGraph()).decrementRefCount();
            }
            if (isEmpty)
                iterator.remove();
        }
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
     * @throws StorageException When an IO operation failed
     */
    private boolean removeAllOnSubject(long subjectKey, PersistedNode property, PersistedNode object, PersistedNode graph, List<MQuad> bufferDecremented, List<MQuad> bufferRemoved) throws StorageException {
        long child;
        int type;
        long key;
        try (IOAccess element = store.accessR(subjectKey)) {
            element.seek(8);
            type = element.readInt();
            key = element.readLong();
            child = element.readLong();
        }

        SubjectNode rSubject = (SubjectNode) getNode(type, key);
        int sizeDecremented = bufferDecremented.size();
        int sizeRemoved = bufferRemoved.size();
        long newChild = removeAllOnProperty(child, property, object, graph, bufferDecremented, bufferRemoved);
        for (int i = sizeDecremented; i != bufferDecremented.size(); i++)
            bufferDecremented.get(i).setSubject(rSubject);
        for (int i = sizeRemoved; i != bufferRemoved.size(); i++)
            bufferRemoved.get(i).setSubject(rSubject);

        if (newChild == FileStore.KEY_NULL) {
            // the child bucket is empty
            store.free(subjectKey);
            return true;
        } else if (newChild != child) {
            // the head of the bucket of graphs changed
            try (IOAccess element = store.accessW(subjectKey)) {
                element.seek(8 + 4 + 8).writeLong(newChild);
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
     * @return The key to the new bucket head, or KEY_NULL if all the bucket is emptied
     * @throws StorageException When an IO operation failed
     */
    private long removeAllOnProperty(long bucket, PersistedNode property, PersistedNode object, PersistedNode graph, List<MQuad> bufferDecremented, List<MQuad> bufferRemoved) throws StorageException {
        long previous = FileStore.KEY_NULL;
        long current = bucket;
        long next;
        while (current != FileStore.KEY_NULL) {
            long child;
            int type;
            long key;
            try (IOAccess element = store.accessR(current)) {
                next = element.readLong();
                type = element.readInt();
                key = element.readLong();
                child = element.readLong();
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

            if (newChild == FileStore.KEY_NULL) {
                // the child bucket is empty
                try {
                    store.free(current);
                } catch (StorageException exception) {
                    Logger.DEFAULT.error(exception);
                }
                if (previous == FileStore.KEY_NULL) {
                    // this is the first one
                    bucket = next;
                    current = FileStore.KEY_NULL;
                } else {
                    try (IOAccess element = store.accessW(previous)) {
                        element.writeLong(next);
                    }
                    current = previous;
                }
            } else if (newChild != child) {
                // the head of the bucket of graphs changed
                try (IOAccess element = store.accessW(current)) {
                    element.seek(8 + 4 + 8).writeLong(newChild);
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
     * @return The key to the new bucket head, or KEY_NULL if all the bucket is emptied
     * @throws StorageException When an IO operation failed
     */
    private long removeAllOnObject(long bucket, PersistedNode object, PersistedNode graph, List<MQuad> bufferDecremented, List<MQuad> bufferRemoved) throws StorageException {
        long previous = FileStore.KEY_NULL;
        long current = bucket;
        long next;
        while (current != FileStore.KEY_NULL) {
            long child;
            int type;
            long key;
            try (IOAccess element = store.accessR(current)) {
                next = element.readLong();
                type = element.readInt();
                key = element.readLong();
                child = element.readLong();
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

            if (newChild == FileStore.KEY_NULL) {
                // the child bucket is empty
                store.free(current);
                if (previous == FileStore.KEY_NULL) {
                    // this is the first one
                    bucket = next;
                    current = FileStore.KEY_NULL;
                } else {
                    try (IOAccess element = store.accessW(previous)) {
                        element.writeLong(next);
                    }
                    current = previous;
                }
            } else if (newChild != child) {
                // the head of the bucket of graphs changed
                try (IOAccess element = store.accessW(current)) {
                    element.seek(8 + 4 + 8).writeLong(newChild);
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
     * @return The key to the new bucket head, or KEY_NULL if all the bucket is emptied
     * @throws StorageException When an IO operation failed
     */
    private long removeAllOnGraph(long bucket, PersistedNode graph, List<MQuad> bufferDecremented, List<MQuad> bufferRemoved) throws StorageException {
        long previous = FileStore.KEY_NULL;
        long current = bucket;
        long next;
        while (current != FileStore.KEY_NULL) {
            PersistedNode removedGraph = null;
            try (IOAccess element = store.accessW(current)) {
                next = element.readLong();
                int type = element.readInt();
                long key = element.readLong();
                long multiplicity = element.readLong();
                if (graph == null) {
                    multiplicity--;
                    PersistedNode g = getNode(type, key);
                    if (multiplicity <= 0) {
                        bufferRemoved.add(new MQuad((GraphNode) g, multiplicity));
                        removedGraph = g;
                    } else {
                        bufferDecremented.add(new MQuad((GraphNode) g, multiplicity));
                    }
                } else if (graph.getNodeType() == type && graph.getKey() == key) {
                    multiplicity--;
                    if (multiplicity <= 0) {
                        bufferRemoved.add(new MQuad((GraphNode) graph, multiplicity));
                        removedGraph = graph;
                    } else {
                        bufferDecremented.add(new MQuad((GraphNode) graph, multiplicity));
                    }
                }
            }

            if (removedGraph != null) {
                // multiplicity of the graph reached 0
                if (graph == null) {
                    // free all graphs
                    try {
                        store.free(current);
                    } catch (StorageException exception) {
                        Logger.DEFAULT.error(exception);
                    }
                } else {
                    // free this element from the linked list
                    store.free(current);
                    if (previous == FileStore.KEY_NULL) {
                        // this is the first one
                        return next;
                    } else {
                        try (IOAccess element = store.accessW(previous)) {
                            element.writeLong(next);
                        }
                        return bucket;
                    }
                }
            }

            previous = current;
            current = next;
        }
        if (graph == null)
            return FileStore.KEY_NULL;
        return bucket;
    }

    @Override
    public void doClear(List<MQuad> buffer) {
        try {
            Iterator<Map.Entry<Long, Long>> iterator = mapSubjectIRI.entries();
            while (iterator.hasNext())
                clearOnSubject(iterator.next().getValue(), null, buffer);
            mapSubjectIRI.clear();
            mapIndexGraphIRI.clear();
        } catch (StorageException exception) {
            Logger.DEFAULT.error(exception);
        }
        try {
            Iterator<Map.Entry<Long, Long>> iterator = mapSubjectBlank.entries();
            while (iterator.hasNext())
                clearOnSubject(iterator.next().getValue(), null, buffer);
            mapSubjectBlank.clear();
            mapIndexGraphBlank.clear();
        } catch (StorageException exception) {
            Logger.DEFAULT.error(exception);
        }
        try {
            Iterator<Map.Entry<Long, Long>> iterator = mapSubjectAnon.entries();
            while (iterator.hasNext())
                clearOnSubject(iterator.next().getValue(), null, buffer);
            mapSubjectAnon.clear();
        } catch (StorageException exception) {
            Logger.DEFAULT.error(exception);
        }
        store.clear();
    }

    @Override
    public void doClear(GraphNode graph, List<MQuad> buffer) throws UnsupportedNodeType {
        PersistedNode pGraph = nodes.getPersistent(graph, false);
        if (pGraph == null)
            return;
        PersistedMap map = pGraph.getNodeType() == Node.TYPE_IRI ? mapIndexGraphIRI : mapIndexGraphBlank;
        long bucket;
        try {
            bucket = map.get(pGraph.getKey());
        } catch (StorageException exception) {
            Logger.DEFAULT.error(exception);
            return;
        }
        if (bucket == FileStore.KEY_NULL)
            return;
        long current = bucket;
        try {
            while (current != FileStore.KEY_NULL) {
                long next;
                try (IOAccess element = store.accessR(current)) {
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
                }
                store.free(current);
                current = next;
            }
            map.remove(pGraph.getKey());
        } catch (StorageException exception) {
            Logger.DEFAULT.error(exception);
        }
    }

    /**
     * Clears quads from a single subject
     *
     * @param key    The key to the subject
     * @param graph  The graph to match
     * @param buffer The buffer of quads
     * @return Whether the subject is emptied
     * @throws StorageException When an IO operation failed
     */
    private boolean clearOnSubject(long key, PersistedNode graph, List<MQuad> buffer) throws StorageException {
        long child;
        SubjectNode subject;
        try (IOAccess element = store.accessR(key)) {
            element.seek(8);
            subject = (SubjectNode) getNode(element.readInt(), element.readLong());
            child = element.readLong();
        }

        int size = buffer.size();
        long newChild = clearOnProperty(child, graph, buffer);
        for (int i = size; i != buffer.size(); i++) {
            MQuad quad = buffer.get(i);
            quad.setSubject(subject);
            ((PersistedNode) quad.getSubject()).decrementRefCount();
            ((PersistedNode) quad.getProperty()).decrementRefCount();
            ((PersistedNode) quad.getObject()).decrementRefCount();
            ((PersistedNode) quad.getGraph()).decrementRefCount();
        }

        if (newChild == FileStore.KEY_NULL) {
            // the child bucket is empty
            store.free(key);
            return true;
        } else if (newChild != child) {
            // the head of the bucket of graphs changed
            try (IOAccess element = store.accessW(key)) {
                element.seek(8 + 4 + 8).writeLong(newChild);
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
     * @return The key to the new bucket head, or KEY_NULL if all the bucket is emptied
     * @throws StorageException When an IO operation failed
     */
    private long clearOnProperty(long bucket, PersistedNode graph, List<MQuad> buffer) throws StorageException {
        long previous = FileStore.KEY_NULL;
        long current = bucket;
        long next;
        while (current != FileStore.KEY_NULL) {
            long child;
            Property property;
            try (IOAccess element = store.accessR(current)) {
                next = element.readLong();
                property = (Property) getNode(element.readInt(), element.readLong());
                child = element.readLong();
            }

            int size = buffer.size();
            long newChild = clearOnObject(child, graph, buffer);
            for (int i = size; i != buffer.size(); i++)
                buffer.get(i).setProperty(property);

            if (newChild == FileStore.KEY_NULL) {
                // the child bucket is empty
                store.free(current);
                if (previous == FileStore.KEY_NULL) {
                    // this is the first one
                    bucket = next;
                    current = FileStore.KEY_NULL;
                } else {
                    try (IOAccess element = store.accessW(previous)) {
                        element.writeLong(next);
                    }
                    current = previous;
                }
            } else if (newChild != child) {
                // the head of the bucket of graphs changed
                try (IOAccess element = store.accessW(current)) {
                    element.seek(8 + 4 + 8).writeLong(newChild);
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
     * @return The key to the new bucket head, or KEY_NULL if all the bucket is emptied
     * @throws StorageException When an IO operation failed
     */
    private long clearOnObject(long bucket, PersistedNode graph, List<MQuad> buffer) throws StorageException {
        long previous = FileStore.KEY_NULL;
        long current = bucket;
        long next;
        while (current != FileStore.KEY_NULL) {
            long child;
            Node object;
            try (IOAccess element = store.accessR(current)) {
                next = element.readLong();
                object = getNode(element.readInt(), element.readLong());
                child = element.readLong();
            }

            int size = buffer.size();
            long newChild = clearOnGraph(child, graph, buffer);
            for (int i = size; i != buffer.size(); i++)
                buffer.get(i).setObject(object);

            if (newChild == FileStore.KEY_NULL) {
                // the child bucket is empty
                store.free(current);
                if (previous == FileStore.KEY_NULL) {
                    // this is the first one
                    bucket = next;
                    current = FileStore.KEY_NULL;
                } else {
                    try (IOAccess element = store.accessW(previous)) {
                        element.writeLong(next);
                    }
                    current = previous;
                }
            } else if (newChild != child) {
                // the head of the bucket of graphs changed
                try (IOAccess element = store.accessW(current)) {
                    element.seek(8 + 4 + 8).writeLong(newChild);
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
     * @return The key to the new bucket head, or KEY_NULL if all the bucket is emptied
     * @throws StorageException When an IO operation failed
     */
    private long clearOnGraph(long bucket, PersistedNode graph, List<MQuad> buffer) throws StorageException {
        long previous = FileStore.KEY_NULL;
        long current = bucket;
        long next;
        while (current != FileStore.KEY_NULL) {
            boolean found = false;
            try (IOAccess element = store.accessR(current)) {
                next = element.readLong();
                int type = element.readInt();
                long key = element.readLong();
                if (graph == null) {
                    PersistedNode g = getNode(type, key);
                    buffer.add(new MQuad((GraphNode) g, element.readLong()));
                } else if (graph.getNodeType() == type && graph.getKey() == key) {
                    buffer.add(new MQuad((GraphNode) graph, element.readLong()));
                    found = true;
                }
            }

            if (graph == null) {
                // free all quads
                store.free(current);
            } else if (found) {
                // free this element from the linked list
                store.free(current);
                if (previous == FileStore.KEY_NULL) {
                    // this is the first one
                    return next;
                } else {
                    try (IOAccess element = store.accessW(previous)) {
                        element.writeLong(next);
                    }
                    return bucket;
                }
            }

            previous = current;
            current = next;
        }
        if (graph == null)
            return FileStore.KEY_NULL;
        return bucket;
    }

    @Override
    public void doCopy(GraphNode origin, GraphNode target, List<MQuad> bufferOld, List<MQuad> bufferNew, boolean overwrite) throws UnsupportedNodeType {
        PersistedNode pOrigin = nodes.getPersistent(origin, false);
        PersistedNode pTarget = nodes.getPersistent(target, false);
        if (pOrigin == null) {
            if (overwrite && pTarget != null) {
                doClear((GraphNode) pTarget, bufferOld);
            }
            return;
        }
        if (pTarget == null) {
            pTarget = nodes.getPersistent(target, true);
        }

        try {
            copyOnSubject(mapSubjectIRI, pOrigin, pTarget, bufferOld, bufferNew, overwrite);
            copyOnSubject(mapSubjectAnon, pOrigin, pTarget, bufferOld, bufferNew, overwrite);
            copyOnSubject(mapSubjectAnon, pOrigin, pTarget, bufferOld, bufferNew, overwrite);
        } catch (StorageException exception) {
            Logger.DEFAULT.error(exception);
        }
    }

    /**
     * Copies all the quads with the specified origin graph, to quads with the target graph.
     *
     * @param map       The map of subjects
     * @param origin    The origin graph
     * @param target    The target graph
     * @param bufferOld The buffer of the removed quads
     * @param bufferNew The buffer of the new quads
     * @param overwrite Whether to overwrite quads from the target graph
     * @throws StorageException When an IO operation failed
     */
    private void copyOnSubject(PersistedMap map, PersistedNode origin, PersistedNode target, List<MQuad> bufferOld, List<MQuad> bufferNew, boolean overwrite) throws StorageException {
        Iterator<Map.Entry<Long, Long>> iterator = map.entries();
        while (iterator.hasNext()) {
            int sizeOld = bufferOld.size();
            int sizeNew = bufferNew.size();
            boolean isEmpty = copyOnSubject(iterator.next().getValue(), origin, target, bufferOld, bufferNew, overwrite);
            for (int i = sizeOld; i != bufferOld.size(); i++) {
                MQuad quad = bufferOld.get(i);
                doQuadDeindex((PersistedNode) quad.getSubject(), (PersistedNode) quad.getGraph());
                ((PersistedNode) quad.getSubject()).decrementRefCount();
                ((PersistedNode) quad.getProperty()).decrementRefCount();
                ((PersistedNode) quad.getObject()).decrementRefCount();
                ((PersistedNode) quad.getGraph()).decrementRefCount();
            }
            for (int i = sizeNew; i != bufferNew.size(); i++) {
                MQuad quad = bufferOld.get(i);
                doQuadIndex((PersistedNode) quad.getSubject(), (PersistedNode) quad.getGraph());
                ((PersistedNode) quad.getSubject()).incrementRefCount();
                ((PersistedNode) quad.getProperty()).incrementRefCount();
                ((PersistedNode) quad.getObject()).incrementRefCount();
                ((PersistedNode) quad.getGraph()).incrementRefCount();
            }
            if (isEmpty)
                iterator.remove();
        }
    }

    /**
     * Copies all the quads with the specified origin graph, to quads with the target graph.
     *
     * @param key       The key to the subject
     * @param origin    The origin graph
     * @param target    The target graph
     * @param bufferOld The buffer of the removed quads
     * @param bufferNew The buffer of the new quads
     * @param overwrite Whether to overwrite quads from the target graph
     * @return Whether the subject is emptied
     * @throws StorageException When an IO operation failed
     */
    private boolean copyOnSubject(long key, PersistedNode origin, PersistedNode target, List<MQuad> bufferOld, List<MQuad> bufferNew, boolean overwrite) throws StorageException {
        long child;
        SubjectNode subject;
        try (IOAccess element = store.accessR(key)) {
            element.seek(8);
            subject = (SubjectNode) getNode(element.readInt(), element.readLong());
            child = element.readLong();
        }

        int sizeOld = bufferOld.size();
        int sizeNew = bufferNew.size();
        long newChild = copyOnProperty(child, origin, target, bufferOld, bufferNew, overwrite);
        for (int i = sizeOld; i != bufferOld.size(); i++)
            bufferOld.get(i).setSubject(subject);
        for (int i = sizeNew; i != bufferNew.size(); i++)
            bufferNew.get(i).setSubject(subject);

        if (newChild == FileStore.KEY_NULL) {
            // the child bucket is empty
            store.free(key);
            return true;
        } else if (newChild != child) {
            // the head of the bucket of graphs changed
            try (IOAccess element = store.accessW(key)) {
                element.seek(8 + 4 + 8).writeLong(newChild);
            }
        }
        return false;
    }

    /**
     * Copies all the quads with the specified origin graph, to quads with the target graph.
     *
     * @param bucket    The bucket of properties
     * @param origin    The origin graph
     * @param target    The target graph
     * @param bufferOld The buffer of the removed quads
     * @param bufferNew The buffer of the new quads
     * @param overwrite Whether to overwrite quads from the target graph
     * @return The key to the new bucket head, or KEY_NULL if all the bucket is emptied
     * @throws StorageException When an IO operation failed
     */
    private long copyOnProperty(long bucket, PersistedNode origin, PersistedNode target, List<MQuad> bufferOld, List<MQuad> bufferNew, boolean overwrite) throws StorageException {
        long previous = FileStore.KEY_NULL;
        long current = bucket;
        long next;
        while (current != FileStore.KEY_NULL) {
            long child;
            Property property;
            try (IOAccess element = store.accessR(current)) {
                next = element.readLong();
                property = (Property) getNode(element.readInt(), element.readLong());
                child = element.readLong();
            }

            int sizeOld = bufferOld.size();
            int sizeNew = bufferNew.size();
            long newChild = copyOnObject(child, origin, target, bufferOld, bufferNew, overwrite);
            for (int i = sizeOld; i != bufferOld.size(); i++)
                bufferOld.get(i).setProperty(property);
            for (int i = sizeNew; i != bufferNew.size(); i++)
                bufferNew.get(i).setProperty(property);

            if (newChild == FileStore.KEY_NULL) {
                // the child bucket is empty
                store.free(current);
                if (previous == FileStore.KEY_NULL) {
                    // this is the first one
                    bucket = next;
                    current = FileStore.KEY_NULL;
                } else {
                    try (IOAccess element = store.accessW(previous)) {
                        element.writeLong(next);
                    }
                    current = previous;
                }
            } else if (newChild != child) {
                // the head of the bucket of graphs changed
                try (IOAccess element = store.accessW(current)) {
                    element.seek(8 + 4 + 8).writeLong(newChild);
                }
            }

            previous = current;
            current = next;
        }

        return bucket;
    }

    /**
     * Copies all the quads with the specified origin graph, to quads with the target graph.
     *
     * @param bucket    The bucket of objects
     * @param origin    The origin graph
     * @param target    The target graph
     * @param bufferOld The buffer of the removed quads
     * @param bufferNew The buffer of the new quads
     * @param overwrite Whether to overwrite quads from the target graph
     * @return The key to the new bucket head, or KEY_NULL if all the bucket is emptied
     * @throws StorageException When an IO operation failed
     */
    private long copyOnObject(long bucket, PersistedNode origin, PersistedNode target, List<MQuad> bufferOld, List<MQuad> bufferNew, boolean overwrite) throws StorageException {
        long previous = FileStore.KEY_NULL;
        long current = bucket;
        long next;
        while (current != FileStore.KEY_NULL) {
            long child;
            Node object;
            try (IOAccess element = store.accessR(current)) {
                next = element.readLong();
                object = getNode(element.readInt(), element.readLong());
                child = element.readLong();
            }

            int sizeOld = bufferOld.size();
            int sizeNew = bufferNew.size();
            long newChild = copyOnGraph(child, origin, target, bufferOld, bufferNew, overwrite);
            for (int i = sizeOld; i != bufferOld.size(); i++)
                bufferOld.get(i).setObject(object);
            for (int i = sizeNew; i != bufferNew.size(); i++)
                bufferNew.get(i).setObject(object);

            if (newChild == FileStore.KEY_NULL) {
                // the child bucket is empty
                store.free(current);
                if (previous == FileStore.KEY_NULL) {
                    // this is the first one
                    bucket = next;
                    current = FileStore.KEY_NULL;
                } else {
                    try (IOAccess element = store.accessW(previous)) {
                        element.writeLong(next);
                    }
                    current = previous;
                }
            } else if (newChild != child) {
                // the head of the bucket of graphs changed
                try (IOAccess element = store.accessW(current)) {
                    element.seek(8 + 4 + 8).writeLong(newChild);
                }
            }

            previous = current;
            current = next;
        }

        return bucket;
    }

    /**
     * Copies all the quads with the specified origin graph, to quads with the target graph.
     *
     * @param bucket    The bucket of graphs
     * @param origin    The origin graph
     * @param target    The target graph
     * @param bufferOld The buffer of the removed quads
     * @param bufferNew The buffer of the new quads
     * @param overwrite Whether to overwrite quads from the target graph
     * @return The key to the new bucket head, or KEY_NULL if all the bucket is emptied
     * @throws StorageException When an IO operation failed
     */
    private long copyOnGraph(long bucket, PersistedNode origin, PersistedNode target, List<MQuad> bufferOld, List<MQuad> bufferNew, boolean overwrite) throws StorageException {
        long keyOrigin = FileStore.KEY_NULL;
        long keyTargetPrevious = FileStore.KEY_NULL;
        long keyTarget = FileStore.KEY_NULL;
        long keyTargetNext = FileStore.KEY_NULL;
        long targetMultiplicity = 0;

        // traverse the bucket
        long previous = FileStore.KEY_NULL;
        long current = bucket;
        long next;
        while (current != FileStore.KEY_NULL) {
            try (IOAccess element = store.accessR(current)) {
                next = element.readLong();
                int type = element.readInt();
                long key = element.readLong();
                if (type == origin.getNodeType() && key == origin.getKey()) {
                    keyOrigin = current;
                    if (keyTarget != FileStore.KEY_NULL)
                        break;
                } else if (type == target.getNodeType() && key == target.getKey()) {
                    keyTarget = current;
                    keyTargetPrevious = previous;
                    keyTargetNext = next;
                    targetMultiplicity = element.readLong();
                    if (keyOrigin != FileStore.KEY_NULL)
                        break;
                }
            }
            previous = current;
            current = next;
        }

        if (keyOrigin != FileStore.KEY_NULL) {
            // if the origin graph is present, copy
            if (keyTarget == FileStore.KEY_NULL) {
                // insert the target graph
                keyTarget = newEntry(target);
                try (IOAccess element = store.accessW(previous)) {
                    element.writeLong(keyTarget);
                }
                bufferNew.add(new MQuad((GraphNode) target, 1));
            }
            // write the multiplicity
            try (IOAccess element = store.accessW(keyTarget)) {
                element.seek(8 + 4 + 8).writeLong(targetMultiplicity + 1);
            }
        } else if (overwrite && keyTarget != FileStore.KEY_NULL) {
            // the target graph is there but not the old one and we must overwrite
            // we need to free this
            bufferOld.add(new MQuad((GraphNode) target, targetMultiplicity));
            store.free(keyTarget);
            if (keyTargetPrevious == FileStore.KEY_NULL) {
                // target is the first node
                return keyTargetNext;
            } else {
                try (IOAccess element = store.accessW(keyTargetPrevious)) {
                    element.writeLong(keyTargetNext);
                }
            }
        }
        return bucket;
    }

    @Override
    public void doMove(GraphNode origin, GraphNode target, List<MQuad> bufferOld, List<MQuad> bufferNew) throws UnsupportedNodeType {
        PersistedNode pOrigin = nodes.getPersistent(origin, false);
        PersistedNode pTarget = nodes.getPersistent(target, false);
        if (pOrigin == null) {
            if (pTarget != null) {
                doClear((GraphNode) pTarget, bufferOld);
            }
            return;
        }
        if (pTarget == null) {
            pTarget = nodes.getPersistent(target, true);
        }

        try {
            moveOnSubject(mapSubjectIRI, pOrigin, pTarget, bufferOld, bufferNew);
            moveOnSubject(mapSubjectAnon, pOrigin, pTarget, bufferOld, bufferNew);
            moveOnSubject(mapSubjectAnon, pOrigin, pTarget, bufferOld, bufferNew);
        } catch (StorageException exception) {
            Logger.DEFAULT.error(exception);
        }
    }

    /**
     * Moves all the quads with the specified origin graph, to quads with the target graph.
     *
     * @param map       The map of subjects
     * @param origin    The origin graph
     * @param target    The target graph
     * @param bufferOld The buffer of the removed quads
     * @param bufferNew The buffer of the new quads
     * @throws StorageException When an IO operation failed
     */
    private void moveOnSubject(PersistedMap map, PersistedNode origin, PersistedNode target, List<MQuad> bufferOld, List<MQuad> bufferNew) throws StorageException {
        Iterator<Map.Entry<Long, Long>> iterator = map.entries();
        while (iterator.hasNext()) {
            int sizeOld = bufferOld.size();
            int sizeNew = bufferNew.size();
            boolean isEmpty = moveOnSubject(iterator.next().getValue(), origin, target, bufferOld, bufferNew);
            for (int i = sizeOld; i != bufferOld.size(); i++) {
                MQuad quad = bufferOld.get(i);
                doQuadDeindex((PersistedNode) quad.getSubject(), (PersistedNode) quad.getGraph());
                ((PersistedNode) quad.getSubject()).decrementRefCount();
                ((PersistedNode) quad.getProperty()).decrementRefCount();
                ((PersistedNode) quad.getObject()).decrementRefCount();
                ((PersistedNode) quad.getGraph()).decrementRefCount();
            }
            for (int i = sizeNew; i != bufferNew.size(); i++) {
                MQuad quad = bufferOld.get(i);
                doQuadIndex((PersistedNode) quad.getSubject(), (PersistedNode) quad.getGraph());
                ((PersistedNode) quad.getSubject()).incrementRefCount();
                ((PersistedNode) quad.getProperty()).incrementRefCount();
                ((PersistedNode) quad.getObject()).incrementRefCount();
                ((PersistedNode) quad.getGraph()).incrementRefCount();
            }
            if (isEmpty)
                iterator.remove();
        }
    }

    /**
     * Moves all the quads with the specified origin graph, to quads with the target graph.
     *
     * @param key       The key to the subject
     * @param origin    The origin graph
     * @param target    The target graph
     * @param bufferOld The buffer of the removed quads
     * @param bufferNew The buffer of the new quads
     * @return Whether the subject is emptied
     * @throws StorageException When an IO operation failed
     */
    private boolean moveOnSubject(long key, PersistedNode origin, PersistedNode target, List<MQuad> bufferOld, List<MQuad> bufferNew) throws StorageException {
        long child;
        SubjectNode subject;
        try (IOAccess element = store.accessR(key)) {
            element.seek(8);
            subject = (SubjectNode) getNode(element.readInt(), element.readLong());
            child = element.readLong();
        }

        int sizeOld = bufferOld.size();
        int sizeNew = bufferNew.size();
        long newChild = moveOnProperty(child, origin, target, bufferOld, bufferNew);
        for (int i = sizeOld; i != bufferOld.size(); i++)
            bufferOld.get(i).setSubject(subject);
        for (int i = sizeNew; i != bufferNew.size(); i++)
            bufferNew.get(i).setSubject(subject);

        if (newChild == FileStore.KEY_NULL) {
            // the child bucket is empty
            store.free(key);
            return true;
        } else if (newChild != child) {
            // the head of the bucket of graphs changed
            try (IOAccess element = store.accessW(key)) {
                element.seek(8 + 4 + 8).writeLong(newChild);
            }
        }
        return false;
    }

    /**
     * Moves all the quads with the specified origin graph, to quads with the target graph.
     *
     * @param bucket    The bucket of properties
     * @param origin    The origin graph
     * @param target    The target graph
     * @param bufferOld The buffer of the removed quads
     * @param bufferNew The buffer of the new quads
     * @return The key to the new bucket head, or KEY_NULL if all the bucket is emptied
     * @throws StorageException When an IO operation failed
     */
    private long moveOnProperty(long bucket, PersistedNode origin, PersistedNode target, List<MQuad> bufferOld, List<MQuad> bufferNew) throws StorageException {
        long previous = FileStore.KEY_NULL;
        long current = bucket;
        long next;
        while (current != FileStore.KEY_NULL) {
            long child;
            Property property;
            try (IOAccess element = store.accessR(current)) {
                next = element.readLong();
                property = (Property) getNode(element.readInt(), element.readLong());
                child = element.readLong();
            }

            int sizeOld = bufferOld.size();
            int sizeNew = bufferNew.size();
            long newChild = moveOnObject(child, origin, target, bufferOld, bufferNew);
            for (int i = sizeOld; i != bufferOld.size(); i++)
                bufferOld.get(i).setProperty(property);
            for (int i = sizeNew; i != bufferNew.size(); i++)
                bufferNew.get(i).setProperty(property);

            if (newChild == FileStore.KEY_NULL) {
                // the child bucket is empty
                store.free(current);
                if (previous == FileStore.KEY_NULL) {
                    // this is the first one
                    bucket = next;
                    current = FileStore.KEY_NULL;
                } else {
                    try (IOAccess element = store.accessW(previous)) {
                        element.writeLong(next);
                    }
                    current = previous;
                }
            } else if (newChild != child) {
                // the head of the bucket of graphs changed
                try (IOAccess element = store.accessW(current)) {
                    element.seek(8 + 4 + 8).writeLong(newChild);
                }
            }

            previous = current;
            current = next;
        }

        return bucket;
    }

    /**
     * Moves all the quads with the specified origin graph, to quads with the target graph.
     *
     * @param bucket    The bucket of objects
     * @param origin    The origin graph
     * @param target    The target graph
     * @param bufferOld The buffer of the removed quads
     * @param bufferNew The buffer of the new quads
     * @return The key to the new bucket head, or KEY_NULL if all the bucket is emptied
     * @throws StorageException When an IO operation failed
     */
    private long moveOnObject(long bucket, PersistedNode origin, PersistedNode target, List<MQuad> bufferOld, List<MQuad> bufferNew) throws StorageException {
        long previous = FileStore.KEY_NULL;
        long current = bucket;
        long next;
        while (current != FileStore.KEY_NULL) {
            long child;
            Node object;
            try (IOAccess element = store.accessR(current)) {
                next = element.readLong();
                object = getNode(element.readInt(), element.readLong());
                child = element.readLong();
            }

            int sizeOld = bufferOld.size();
            int sizeNew = bufferNew.size();
            long newChild = moveOnGraph(child, origin, target, bufferOld, bufferNew);
            for (int i = sizeOld; i != bufferOld.size(); i++)
                bufferOld.get(i).setObject(object);
            for (int i = sizeNew; i != bufferNew.size(); i++)
                bufferNew.get(i).setObject(object);

            if (newChild == FileStore.KEY_NULL) {
                // the child bucket is empty
                store.free(current);
                if (previous == FileStore.KEY_NULL) {
                    // this is the first one
                    bucket = next;
                    current = FileStore.KEY_NULL;
                } else {
                    try (IOAccess element = store.accessW(previous)) {
                        element.writeLong(next);
                    }
                    current = previous;
                }
            } else if (newChild != child) {
                // the head of the bucket of graphs changed
                try (IOAccess element = store.accessW(current)) {
                    element.seek(8 + 4 + 8).writeLong(newChild);
                }
            }

            previous = current;
            current = next;
        }

        return bucket;
    }

    /**
     * Moves all the quads with the specified origin graph, to quads with the target graph.
     *
     * @param bucket    The bucket of graphs
     * @param origin    The origin graph
     * @param target    The target graph
     * @param bufferOld The buffer of the removed quads
     * @param bufferNew The buffer of the new quads
     * @return The key to the new bucket head, or KEY_NULL if all the bucket is emptied
     * @throws StorageException When an IO operation failed
     */
    private long moveOnGraph(long bucket, PersistedNode origin, PersistedNode target, List<MQuad> bufferOld, List<MQuad> bufferNew) throws StorageException {
        long keyOriginPrevious = FileStore.KEY_NULL;
        long keyOrigin = FileStore.KEY_NULL;
        long keyOriginNext = FileStore.KEY_NULL;
        long originMultiplicity = 0;
        long keyTargetPrevious = FileStore.KEY_NULL;
        long keyTarget = FileStore.KEY_NULL;
        long keyTargetNext = FileStore.KEY_NULL;
        long targetMultiplicity = 0;

        // traverse the bucket
        long previous = FileStore.KEY_NULL;
        long current = bucket;
        long next;
        while (current != FileStore.KEY_NULL) {
            try (IOAccess element = store.accessR(current)) {
                next = element.readLong();
                int type = element.readInt();
                long key = element.readLong();
                if (type == origin.getNodeType() && key == origin.getKey()) {
                    keyOrigin = current;
                    keyOriginPrevious = previous;
                    keyOriginNext = next;
                    originMultiplicity = element.readLong();
                    if (keyTarget != FileStore.KEY_NULL)
                        break;
                } else if (type == target.getNodeType() && key == target.getKey()) {
                    keyTarget = current;
                    keyTargetPrevious = previous;
                    keyTargetNext = next;
                    targetMultiplicity = element.readLong();
                    if (keyOrigin != FileStore.KEY_NULL)
                        break;
                }
            }
            previous = current;
            current = next;
        }

        if (keyOrigin != FileStore.KEY_NULL) {
            // if the origin graph is present, move
            // free the origin
            bufferOld.add(new MQuad((GraphNode) origin, originMultiplicity));
            if (keyTarget != FileStore.KEY_NULL) {
                // the target graph is also here, increment it
                try (IOAccess element = store.accessW(keyTarget)) {
                    element.seek(8 + 4 + 8).writeLong(targetMultiplicity + 1);
                }
                // free the origin
                store.free(keyOrigin);
                if (keyOriginPrevious == FileStore.KEY_NULL) {
                    // target is the first node
                    return keyOriginNext;
                } else {
                    try (IOAccess element = store.accessW(keyOriginPrevious)) {
                        element.writeLong(keyOriginNext);
                    }
                }
            } else {
                // replace the origin graph by the target one
                // reset the multiplicity
                bufferNew.add(new MQuad((GraphNode) target, 1));
                try (IOAccess element = store.accessW(keyOrigin)) {
                    element.seek(8);
                    element.writeInt(target.getNodeType());
                    element.writeLong(target.getKey());
                    element.writeLong(1);
                }
            }
        } else if (keyTarget != FileStore.KEY_NULL) {
            // the target graph is there but not the old one
            // we need to free this
            bufferOld.add(new MQuad((GraphNode) target, targetMultiplicity));
            store.free(keyTarget);
            if (keyTargetPrevious == FileStore.KEY_NULL) {
                // target is the first node
                return keyTargetNext;
            } else {
                try (IOAccess element = store.accessW(keyTargetPrevious)) {
                    element.writeLong(keyTargetNext);
                }
            }
        }
        return bucket;
    }

    @Override
    public void close() throws Exception {
        store.close();
    }

    /*
    Utility API
     */

    /**
     * Gets the subject map for the specified subject
     *
     * @param subject A quad subject
     * @return The appropriate map
     * @throws UnsupportedNodeType When the subject node is not recognized
     */
    private PersistedMap mapFor(PersistedNode subject) throws UnsupportedNodeType {
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
     * Lookup the target entry for a quad node
     *
     * @param from    Key to the parent entry
     * @param node    The node quad node to resolve
     * @param resolve Whether to create the entry if it does not exist
     * @return The key to the resolved quad node
     * @throws StorageException When an IO operation failed
     */
    private long lookupQNode(long from, PersistedNode node, boolean resolve) throws StorageException {
        bufferQNPrevious = from;
        long current;
        try (IOAccess entry = store.accessR(from)) {
            current = entry.seek(QUAD_ENTRY_SIZE - 8).readLong();
        }
        if (current == FileStore.KEY_NULL) {
            if (!resolve)
                return FileStore.KEY_NULL;
            // not here
            current = newEntry(node);
            try (IOAccess entry = store.accessW(from)) {
                entry.seek(QUAD_ENTRY_SIZE - 8).writeLong(current);
            }
            return current;
        }
        // follow the chain
        while (current != FileStore.KEY_NULL) {
            try (IOAccess entry = store.accessR(current)) {
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
            return FileStore.KEY_NULL;
        current = newEntry(node);
        try (IOAccess entry = store.accessW(bufferQNPrevious)) {
            entry.writeLong(current);
        }
        return current;
    }

    /**
     * Writes the new quad node entry for the specified node
     *
     * @param node A quad node
     * @return The key to the entry
     * @throws StorageException When an IO operation failed
     */
    private long newEntry(PersistedNode node) throws StorageException {
        long key = store.allocate(QUAD_ENTRY_SIZE);
        try (IOAccess entry = store.accessW(key)) {
            entry.writeLong(FileStore.KEY_NULL);
            entry.writeInt(node.getNodeType());
            entry.writeLong(node.getKey());
            entry.writeLong(FileStore.KEY_NULL);
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

    /**
     * Gets an iterator over all the subjects
     *
     * @return The iterator
     * @throws StorageException When an IO operation failed
     */
    private Iterator<Long> getAllSubjects() throws StorageException {
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
     * @throws StorageException When an IO operation failed
     */
    private Iterator<Long> getSubjectIterator(PersistedMap map) throws StorageException {
        return new AdaptingIterator<>(map.entries(), new Adapter<Long>() {
            @Override
            public <X> Long adapt(X element) {
                Map.Entry<Long, Long> mapEntry = (Map.Entry<Long, Long>) element;
                return mapEntry.getValue();
            }
        });
    }
}
