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

import fr.cenotelie.commons.storage.Access;
import fr.cenotelie.commons.storage.Constants;
import fr.cenotelie.commons.storage.stores.ObjectStore;
import fr.cenotelie.commons.storage.stores.StoredMap;
import fr.cenotelie.commons.utils.collections.AdaptingIterator;
import fr.cenotelie.commons.utils.collections.CombiningIterator;
import fr.cenotelie.commons.utils.collections.ConcatenatedIterator;
import fr.cenotelie.commons.utils.collections.SingleIterator;
import fr.cenotelie.commons.utils.logging.Logging;
import org.xowl.infra.store.rdf.*;
import org.xowl.infra.store.storage.DatasetQuadsImpl;
import org.xowl.infra.store.storage.MQuad;
import org.xowl.infra.store.storage.UnsupportedNodeType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Represents a persisted store of RDF quads.
 *
 * @author Laurent Wouters
 */
public class PersistedDatasetQuads extends DatasetQuadsImpl {
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
     * The persisted nodes associated to this dataset
     */
    private final PersistedDatasetNodes nodes;
    /**
     * The backing storing the nodes' data
     */
    private final ObjectStore store;
    /**
     * The subject map for IRIs
     */
    private final StoredMap mapSubjectIRI;
    /**
     * The subject map for Blanks
     */
    private final StoredMap mapSubjectBlank;
    /**
     * The subject map for Anons
     */
    private final StoredMap mapSubjectAnon;
    /**
     * The index map for IRI graphs
     */
    private final StoredMap mapIndexGraphIRI;
    /**
     * The index map for blank graphs
     */
    private final StoredMap mapIndexGraphBlank;
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
     * @param store      The The backing storing the dataset
     * @param initialize Whether to initialize the store
     */
    public PersistedDatasetQuads(PersistedDatasetNodes nodes, ObjectStore store, boolean initialize) {
        this.nodes = nodes;
        this.store = store;
        StoredMap tempMapSubjectIRI;
        StoredMap tempMapSubjectBlank;
        StoredMap tempMapSubjectAnon;
        StoredMap tempMapIndexIRI;
        StoredMap tempMapIndexBlank;
        if (initialize) {
            tempMapSubjectIRI = store.register("subj-iri", StoredMap.create(store));
            tempMapSubjectBlank = store.register("subj-blk", StoredMap.create(store));
            tempMapSubjectAnon = store.register("subj-ano", StoredMap.create(store));
            tempMapIndexIRI = store.register("indx-iri", StoredMap.create(store));
            tempMapIndexBlank = store.register("indx-blk", StoredMap.create(store));
        } else {
            tempMapSubjectIRI = new StoredMap(store, store.getObject("subj-iri"));
            tempMapSubjectBlank = new StoredMap(store, store.getObject("subj-blk"));
            tempMapSubjectAnon = new StoredMap(store, store.getObject("subj-ano"));
            tempMapIndexIRI = new StoredMap(store, store.getObject("indx-iri"));
            tempMapIndexBlank = new StoredMap(store, store.getObject("indx-blk"));
        }
        mapSubjectIRI = tempMapSubjectIRI;
        mapSubjectBlank = tempMapSubjectBlank;
        mapSubjectAnon = tempMapSubjectAnon;
        mapIndexGraphIRI = tempMapIndexIRI;
        mapIndexGraphBlank = tempMapIndexBlank;
    }

    /**
     * Gets the key radical for the specified key
     *
     * @param key A key
     * @return The radical
     */
    private static int getKeyRadical(long key) {
        return (int) (key >>> 32);
    }

    /**
     * Gets the short key for the specified one
     *
     * @param key A key
     * @return The short key
     */
    private static int getShortKey(long key) {
        return (int) (key & 0xFFFFFFFFL);
    }

    /**
     * Gets the full key a radical and a short key
     *
     * @param radical  The radical
     * @param shortKey The short key
     * @return The full key
     */
    private static long getFullKey(int radical, int shortKey) {
        return (((long) radical << 32) | (long) shortKey);
    }

    @Override
    public long getMultiplicity(GraphNode graph, SubjectNode subject, Property property, Node object) throws UnsupportedNodeType {
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
     */
    private long getMultiplicity(PersistedNode graph, PersistedNode subject, PersistedNode property, PersistedNode object) throws UnsupportedNodeType {
        StoredMap map = mapFor(subject);
        long bucket = map.get(subject.getKey());
        if (bucket == Constants.KEY_NULL)
            return 0;
        bufferQNSubject = bucket;
        long target = lookupQNode(bufferQNSubject, property, false);
        if (target == Constants.KEY_NULL)
            return 0;
        target = lookupQNode(target, object, false);
        if (target == Constants.KEY_NULL)
            return 0;
        target = lookupQNode(target, graph, false);
        if (target == Constants.KEY_NULL)
            return 0;
        try (Access entry = store.access(target, true)) {
            return entry.seek(QUAD_ENTRY_SIZE - 8).readLong();
        }
    }

    @Override
    public Iterator<Quad> getAll(GraphNode graph, SubjectNode subject, Property property, Node object) throws UnsupportedNodeType {
        if (subject != null && subject.getNodeType() != Node.TYPE_VARIABLE)
            return getAllOnSingleSubject(graph, subject, property, object);
        if (graph != null && graph.getNodeType() != Node.TYPE_VARIABLE)
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
     * @throws UnsupportedNodeType When a specified node is unsupported
     */
    private Iterator<Quad> getAllOnSingleSubject(GraphNode graph, final SubjectNode subject, Property property, Node object) throws UnsupportedNodeType {
        PersistedNode pSubject = nodes.getPersistent(subject, false);
        if (pSubject == null)
            return new SingleIterator<>(null);
        long current = mapFor(pSubject).get(pSubject.getKey());
        if (current == Constants.KEY_NULL)
            return new SingleIterator<>(null);
        try (Access entry = store.access(current, false)) {
            long bucket = entry.seek(8 + 4 + 8).readLong();
            return new AdaptingIterator<>(getAllOnProperty(bucket, property, object, graph), quad -> {
                quad.setSubject(subject);
                return quad;
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
     */
    private Iterator<Quad> getAllOnSingleGraph(GraphNode graph, final Property property, final Node object) throws UnsupportedNodeType {
        final PersistedNode pGraph = nodes.getPersistent(graph, false);
        if (pGraph == null)
            return new SingleIterator<>(null);
        StoredMap map = pGraph.getNodeType() == Node.TYPE_IRI ? mapIndexGraphIRI : mapIndexGraphBlank;
        long bucket = map.get(pGraph.getKey());
        if (bucket == Constants.KEY_NULL)
            return new SingleIterator<>(null);
        Iterator<Long> iteratorSubjects = new GraphQNodeIterator(store, bucket);
        return new AdaptingIterator<>(new CombiningIterator<>(iteratorSubjects, subjectKey -> {
            try (Access entry = store.access(subjectKey, false)) {
                long propertyBucket = entry.seek(8 + 4 + 8).readLong();
                return getAllOnProperty(propertyBucket, property, object, (GraphNode) pGraph);
            } catch (UnsupportedNodeType exception) {
                Logging.get().error(exception);
                return null;
            }
        }), couple -> {
            long subjectKey = couple.x;
            try (Access entry = store.access(subjectKey, false)) {
                couple.y.setSubject((SubjectNode) getNode(entry.seek(8).readInt(), entry.readLong()));
            }
            return couple.y;
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
        return new AdaptingIterator<>(new CombiningIterator<>(getAllSubjects(), subjectKey -> {
            try (Access entry = store.access(subjectKey, false)) {
                long propertyBucket = entry.seek(8 + 4 + 8).readLong();
                return getAllOnProperty(propertyBucket, property, object, null);
            } catch (UnsupportedNodeType exception) {
                Logging.get().error(exception);
                return null;
            }
        }), couple -> {
            long subjectKey = couple.x;
            try (Access entry = store.access(subjectKey, false)) {
                couple.y.setSubject((SubjectNode) getNode(entry.seek(8).readInt(), entry.readLong()));
                return couple.y;
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
     */
    private Iterator<MQuad> getAllOnProperty(long bucket, final Property property, final Node object, final GraphNode graph) throws UnsupportedNodeType {
        if (property == null || property.getNodeType() == Node.TYPE_VARIABLE) {
            return new AdaptingIterator<>(new CombiningIterator<>(new QNodeIterator(store, bucket), key -> {
                try (Access entry = store.access(key, false)) {
                    entry.seek(8 + 4 + 8);
                    long objectKey = entry.readLong();
                    return getAllOnObject(objectKey, object, graph);
                } catch (UnsupportedNodeType exception) {
                    Logging.get().error(exception);
                    return null;
                }
            }), couple -> {
                long key = couple.x;
                try (Access entry = store.access(key, false)) {
                    entry.seek(8);
                    PersistedNode node = getNode(entry.readInt(), entry.readLong());
                    couple.y.setProperty((Property) node);
                }
                return couple.y;
            });
        }
        PersistedNode pProperty = nodes.getPersistent(property, false);
        if (pProperty == null)
            return new SingleIterator<>(null);
        long current = bucket;
        while (current != Constants.KEY_NULL) {
            try (Access entry = store.access(current, false)) {
                current = entry.readLong();
                int type = entry.readInt();
                long key = entry.readLong();
                long child = entry.readLong();
                if (type == pProperty.getNodeType() && key == pProperty.getKey()) {
                    return new AdaptingIterator<>(getAllOnObject(child, object, graph), quad -> {
                        quad.setProperty(property);
                        return quad;
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
     */
    private Iterator<MQuad> getAllOnObject(long bucket, final Node object, final GraphNode graph) throws UnsupportedNodeType {
        if (object == null || object.getNodeType() == Node.TYPE_VARIABLE) {
            return new AdaptingIterator<>(new CombiningIterator<>(new QNodeIterator(store, bucket), key -> {
                try (Access entry = store.access(key, false)) {
                    entry.seek(8 + 4 + 8);
                    long graphKey = entry.readLong();
                    return getAllOnGraph(graphKey, graph);
                } catch (UnsupportedNodeType exception) {
                    Logging.get().error(exception);
                    return null;
                }
            }), couple -> {
                long key = couple.x;
                try (Access entry = store.access(key, false)) {
                    entry.seek(8);
                    PersistedNode node = getNode(entry.readInt(), entry.readLong());
                    couple.y.setObject(node);
                }
                return couple.y;
            });
        }
        PersistedNode pObject = nodes.getPersistent(object, false);
        if (pObject == null)
            return new SingleIterator<>(null);
        long current = bucket;
        while (current != Constants.KEY_NULL) {
            try (Access entry = store.access(current, false)) {
                current = entry.readLong();
                int type = entry.readInt();
                long key = entry.readLong();
                long child = entry.readLong();
                if (type == pObject.getNodeType() && key == pObject.getKey()) {
                    return new AdaptingIterator<>(getAllOnGraph(child, graph), quad -> {
                        quad.setObject(object);
                        return quad;
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
     */
    private Iterator<MQuad> getAllOnGraph(long bucket, GraphNode graph) throws UnsupportedNodeType {
        if (graph == null || graph.getNodeType() == Node.TYPE_VARIABLE) {
            return new AdaptingIterator<>(new QNodeIterator(store, bucket), key -> {
                try (Access entry = store.access(key, false)) {
                    entry.seek(8);
                    PersistedNode node = getNode(entry.readInt(), entry.readLong());
                    long multiplicity = entry.readLong();
                    return new MQuad((GraphNode) node, multiplicity);
                }
            });
        }
        PersistedNode pGraph = nodes.getPersistent(graph, false);
        if (pGraph == null)
            return new SingleIterator<>(null);
        long current = bucket;
        while (current != Constants.KEY_NULL) {
            try (Access entry = store.access(current, false)) {
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
        Iterator<StoredMap.Entry> iterator = mapIndexGraphIRI.entries();
        while (iterator.hasNext()) {
            result.add(nodes.getIRINodeFor(iterator.next().key));
        }
        iterator = mapIndexGraphBlank.entries();
        while (iterator.hasNext()) {
            result.add(nodes.getBlankNodeFor(iterator.next().key));
        }
        return result;
    }

    @Override
    public long count() {
        long result = 0;
        Iterator<StoredMap.Entry> iterator = mapIndexGraphIRI.entries();
        while (iterator.hasNext()) {
            result += count(mapIndexGraphIRI, iterator.next().key);
        }
        iterator = mapIndexGraphBlank.entries();
        while (iterator.hasNext()) {
            result += count(mapIndexGraphBlank, iterator.next().key);
        }
        return result;
    }

    @Override
    public long count(GraphNode graph) {
        PersistedNode pGraph;
        try {
            pGraph = nodes.getPersistent(graph, false);
        } catch (UnsupportedNodeType exception) {
            Logging.get().error(exception);
            return 0;
        }
        if (pGraph == null)
            return 0;
        StoredMap map = pGraph.getNodeType() == Node.TYPE_IRI ? mapIndexGraphIRI : mapIndexGraphBlank;
        return count(map, pGraph.getKey());
    }

    /**
     * Counts the number of quads in a graph
     *
     * @param map The corresponding graph index map
     * @param key The graph key
     * @return The number of quads
     */
    private long count(StoredMap map, long key) {
        long bucket = map.get(key);
        if (bucket == Constants.KEY_NULL)
            return 0;
        long result = 0;
        long current = bucket;
        while (current != Constants.KEY_NULL) {
            try (Access entry = store.access(current, false)) {
                long next = entry.readLong();
                int count = entry.seek(8 + 4).readInt();
                for (int i = 0; i != GINDEX_ENTRY_MAX_ITEM_COUNT; i++) {
                    int eK = entry.readInt();
                    int mult = entry.readInt();
                    if (eK != Constants.KEY_NULL) {
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
     */
    private long countOnSingleSubject(PersistedNode graph, PersistedNode subject, PersistedNode property, PersistedNode object) throws UnsupportedNodeType {
        long current = mapFor(subject).get(subject.getKey());
        if (current == Constants.KEY_NULL)
            return 0;
        long child;
        try (Access entry = store.access(current, false)) {
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
     */
    private long countOnSingleGraph(PersistedNode graph, PersistedNode property, PersistedNode object) {
        StoredMap map = graph.getNodeType() == Node.TYPE_IRI ? mapIndexGraphIRI : mapIndexGraphBlank;
        long bucket = map.get(graph.getKey());
        if (bucket == Constants.KEY_NULL)
            return 0;
        long result = 0;
        long current = bucket;
        while (current != Constants.KEY_NULL) {
            try (Access entry = store.access(current, false)) {
                current = entry.readLong();
                int radical = entry.readInt();
                int count = entry.readInt();
                for (int i = 0; i != GINDEX_ENTRY_MAX_ITEM_COUNT; i++) {
                    int sk = entry.readInt();
                    entry.readInt();
                    if (sk != Constants.KEY_NULL) {
                        long child = getFullKey(radical, sk);
                        try (Access subjectEntry = store.access(child, false)) {
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
    private long countDefault(StoredMap map, PersistedNode property, PersistedNode object) {
        long result = 0;
        Iterator<StoredMap.Entry> iterator = map.entries();
        while (iterator.hasNext()) {
            long bucket;
            try (Access entry = store.access(iterator.next().value, true)) {
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
     */
    private long countOnProperty(long bucket, PersistedNode graph, PersistedNode property, PersistedNode object) {
        long result = 0;
        long current = bucket;
        while (current != Constants.KEY_NULL) {
            long child = Constants.KEY_NULL;
            try (Access element = store.access(current, false)) {
                current = element.readLong();
                if (property == null || (property.getNodeType() == element.readInt() && property.getKey() == element.readLong())) {
                    child = element.seek(8 + 4 + 8).readLong();
                }
            }
            if (child != Constants.KEY_NULL)
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
        while (current != Constants.KEY_NULL) {
            long child = Constants.KEY_NULL;
            try (Access element = store.access(current, false)) {
                current = element.readLong();
                if (object == null || (object.getNodeType() == element.readInt() && object.getKey() == element.readLong())) {
                    child = element.seek(8 + 4 + 8).readLong();
                }
            }
            if (child != Constants.KEY_NULL)
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
        while (current != Constants.KEY_NULL) {
            try (Access element = store.access(current, false)) {
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
        int result = doQuadAdd(pSubject, pProperty, pObject, pGraph);
        if (result == ADD_RESULT_NEW) {
            doQuadIndex(pSubject, pGraph);
            pSubject.incrementRefCount();
            pProperty.incrementRefCount();
            pObject.incrementRefCount();
            pGraph.incrementRefCount();
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
     */
    private int doQuadAdd(PersistedNode subject, PersistedNode property, PersistedNode object, PersistedNode graph) throws UnsupportedNodeType {
        StoredMap map = mapFor(subject);
        long bucket = map.get(subject.getKey());
        if (bucket == Constants.KEY_NULL) {
            bufferQNSubject = newEntry(subject);
            map.tryPut(subject.getKey(), bufferQNSubject);
        } else {
            bufferQNSubject = bucket;
        }
        long target = lookupQNode(bufferQNSubject, property, true);
        target = lookupQNode(target, object, true);
        target = lookupQNode(target, graph, true);
        try (Access entry = store.access(target, true)) {
            long value = entry.seek(QUAD_ENTRY_SIZE - 8).readLong();
            if (value == Constants.KEY_NULL) {
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
     */
    private void doQuadIndex(PersistedNode subject, PersistedNode graph) {
        int radical = getKeyRadical(subject.getKey());
        StoredMap map = graph.getNodeType() == Node.TYPE_IRI ? mapIndexGraphIRI : mapIndexGraphBlank;
        long bucket = map.get(graph.getKey());
        if (bucket == Constants.KEY_NULL) {
            // this is the first quad for this graph
            long key = writeNewGraphIndex(radical, bufferQNSubject);
            map.tryPut(graph.getKey(), key);
            return;
        }
        // look for an appropriate entry
        long emptyEntry = Constants.KEY_NULL;
        bufferQNPrevious = Constants.KEY_NULL;
        long current = bucket;
        while (current != Constants.KEY_NULL) {
            try (Access entry = store.access(current, true)) {
                long next = entry.readLong();
                int eRadical = entry.readInt();
                if (eRadical != radical)
                    continue;
                int count = entry.readInt();
                if (emptyEntry == Constants.KEY_NULL && count < GINDEX_ENTRY_MAX_ITEM_COUNT)
                    emptyEntry = current;
                for (int i = 0; i != GINDEX_ENTRY_MAX_ITEM_COUNT; i++) {
                    int qnode = entry.readInt();
                    int multiplicity = entry.readInt();
                    if (qnode == Constants.KEY_NULL)
                        continue;
                    if (qnode == getShortKey(bufferQNSubject)) {
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
        if (emptyEntry != Constants.KEY_NULL) {
            try (Access entry = store.access(emptyEntry, true)) {
                int count = entry.seek(12).readInt();
                for (int i = 0; i != GINDEX_ENTRY_MAX_ITEM_COUNT; i++) {
                    int qnode = entry.readInt();
                    entry.readInt();
                    if (qnode == Constants.KEY_NULL) {
                        entry.seek(i * 8 + 8 + 4 + 4);
                        entry.writeInt(getShortKey(bufferQNSubject));
                        entry.writeInt(1);
                        break;
                    }
                }
                entry.seek(8 + 4).writeInt(count + 1);
            }
        } else {
            // requires a new entry
            long key = writeNewGraphIndex(radical, bufferQNSubject);
            try (Access entry = store.access(bufferQNPrevious, true)) {
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
     */
    private long writeNewGraphIndex(int radical, long qnode) {
        long key = store.allocate(GINDEX_ENTRY_SIZE);
        try (Access entry = store.access(key, true)) {
            entry.writeLong(Constants.KEY_NULL);
            entry.writeInt(radical);
            entry.writeInt(1);
            entry.writeInt(getShortKey(qnode));
            entry.writeInt(1);
            for (int i = 1; i != GINDEX_ENTRY_MAX_ITEM_COUNT; i++) {
                entry.writeInt((int) Constants.KEY_NULL);
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
        int result = doQuadRemove(pSubject, pProperty, pObject, pGraph);
        if (result >= REMOVE_RESULT_REMOVED) {
            doQuadDeindex(pSubject, pGraph);
            pSubject.decrementRefCount();
            pProperty.decrementRefCount();
            pObject.decrementRefCount();
            pGraph.decrementRefCount();
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
     */
    private int doQuadRemove(PersistedNode subject, PersistedNode property, PersistedNode object, PersistedNode graph) throws UnsupportedNodeType {
        StoredMap map = mapFor(subject);
        Long bucket = map.get(subject.getKey());
        if (bucket == Constants.KEY_NULL) {
            return REMOVE_RESULT_NOT_FOUND;
        }
        bufferQNSubject = bucket;
        long bufferQNProperty = lookupQNode(bufferQNSubject, property, false);
        if (bufferQNProperty == Constants.KEY_NULL)
            return REMOVE_RESULT_NOT_FOUND;
        long keyPropertyPrevious = bufferQNPrevious;
        long bufferQNObject = lookupQNode(bufferQNProperty, object, false);
        if (bufferQNObject == Constants.KEY_NULL)
            return REMOVE_RESULT_NOT_FOUND;
        long keyObjectPrevious = bufferQNPrevious;
        long bufferQNGraph = lookupQNode(bufferQNObject, graph, false);
        if (bufferQNGraph == Constants.KEY_NULL)
            return REMOVE_RESULT_NOT_FOUND;
        long keyGraphPrevious = bufferQNPrevious;
        try (Access entry = store.access(bufferQNGraph, true)) {
            long value = entry.seek(QUAD_ENTRY_SIZE - 8).readLong();
            value--;
            if (value > 0) {
                entry.seek(QUAD_ENTRY_SIZE - 8).writeLong(value);
                return REMOVE_RESULT_DECREMENT;
            }
        }

        // free the graph node
        long next;
        try (Access entry = store.access(bufferQNGraph, false)) {
            next = entry.readLong();
        }
        if (keyGraphPrevious == bufferQNObject) {
            // the previous of the graph is the object
            if (next == Constants.KEY_NULL) {
                // the last one
                store.free(bufferQNGraph);
            } else {
                try (Access entry = store.access(bufferQNObject, true)) {
                    entry.seek(QUAD_ENTRY_SIZE - 8).writeLong(next);
                }
                store.free(bufferQNGraph);
                return REMOVE_RESULT_REMOVED;
            }
        } else {
            try (Access entry = store.access(keyGraphPrevious, true)) {
                entry.writeLong(next);
            }
            store.free(bufferQNGraph);
            return REMOVE_RESULT_REMOVED;
        }

        // free the object node
        try (Access entry = store.access(bufferQNObject, false)) {
            next = entry.readLong();
        }
        if (keyObjectPrevious == bufferQNProperty) {
            // the previous of the object is the property
            if (next == Constants.KEY_NULL) {
                // the last one
                store.free(bufferQNObject);
            } else {
                try (Access entry = store.access(bufferQNProperty, true)) {
                    entry.seek(QUAD_ENTRY_SIZE - 8).writeLong(next);
                }
                store.free(bufferQNObject);
                return REMOVE_RESULT_REMOVED;
            }
        } else {
            try (Access entry = store.access(keyObjectPrevious, true)) {
                entry.writeLong(next);
            }
            store.free(bufferQNObject);
            return REMOVE_RESULT_REMOVED;
        }

        // free the property node
        try (Access entry = store.access(bufferQNProperty, false)) {
            next = entry.readLong();
        }
        if (keyPropertyPrevious == bufferQNSubject) {
            // the previous of the property is the subject
            if (next == Constants.KEY_NULL) {
                // the last one
                store.free(bufferQNProperty);
            } else {
                try (Access entry = store.access(bufferQNSubject, true)) {
                    entry.seek(QUAD_ENTRY_SIZE - 8).writeLong(next);
                }
                store.free(bufferQNProperty);
                return REMOVE_RESULT_REMOVED;
            }
        } else {
            try (Access entry = store.access(keyPropertyPrevious, true)) {
                entry.writeLong(next);
            }
            store.free(bufferQNProperty);
            return REMOVE_RESULT_REMOVED;
        }

        // free the subject node
        store.free(bufferQNSubject);
        map.tryRemove(subject.getKey(), bufferQNSubject);
        return REMOVE_RESULT_EMPTIED;
    }

    /**
     * De-indexes a quad into the backend
     *
     * @param subject The subject
     * @param graph   The graph
     */
    private void doQuadDeindex(PersistedNode subject, PersistedNode graph) {
        int radical = ((int) (subject.getKey() >>> 32));
        StoredMap map = graph.getNodeType() == Node.TYPE_IRI ? mapIndexGraphIRI : mapIndexGraphBlank;
        long bucket = map.get(graph.getKey());
        if (bucket == Constants.KEY_NULL) {
            // this is the first quad for this graph
            return;
        }
        // look for an appropriate entry
        bufferQNPrevious = Constants.KEY_NULL;
        long current = bucket;
        while (current != Constants.KEY_NULL) {
            try (Access entry = store.access(current, true)) {
                long next = entry.readLong();
                int eRadical = entry.readInt();
                if (eRadical != radical)
                    continue;
                int count = entry.readInt();
                int c = count;
                for (int i = 0; i != GINDEX_ENTRY_MAX_ITEM_COUNT; i++) {
                    int qnode = entry.readInt();
                    int multiplicity = entry.readInt();
                    if (qnode == Constants.KEY_NULL)
                        continue;
                    if (qnode == getShortKey(bufferQNSubject)) {
                        multiplicity--;
                        if (multiplicity > 0) {
                            entry.seek(i * 8 + 8 + 4 + 4 + 4).writeInt(multiplicity);
                            return;
                        }
                        count--;
                        if (count > 0) {
                            entry.seek(i * 8 + 8 + 4 + 4);
                            entry.writeInt((int) Constants.KEY_NULL);
                            entry.writeInt(0);
                            entry.seek(8 + 4).writeInt(count);
                            return;
                        }
                        if (bufferQNPrevious == Constants.KEY_NULL) {
                            // this is the first entry for this index
                            if (next == Constants.KEY_NULL) {
                                // this is the sole entry
                                map.tryRemove(graph.getKey(), bucket);
                            } else {
                                map.compareAndSet(graph.getKey(), bucket, next);
                            }
                        } else {
                            try (Access pe = store.access(bufferQNPrevious, true)) {
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
        if (subject != null)
            removeAllOnSingleSubject(pGraph, pSubject, pProperty, pObject, bufferDecremented, bufferRemoved);
        else if (graph != null)
            removeAllOnSingleGraph(pGraph, pProperty, pObject, bufferDecremented, bufferRemoved);
        else
            removeAllDefault(pProperty, pObject, bufferDecremented, bufferRemoved);
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
     */
    private void removeAllOnSingleSubject(PersistedNode graph, PersistedNode subject, PersistedNode property, PersistedNode object, List<MQuad> bufferDecremented, List<MQuad> bufferRemoved) throws UnsupportedNodeType {
        StoredMap map = mapFor(subject);
        long key = map.get(subject.getKey());
        if (key == Constants.KEY_NULL)
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
            map.tryRemove(subject.getKey(), key);

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
    private void removeAllOnSingleGraph(PersistedNode graph, PersistedNode property, PersistedNode object, List<MQuad> bufferDecremented, List<MQuad> bufferRemoved) {
        StoredMap map = graph.getNodeType() == Node.TYPE_IRI ? mapIndexGraphIRI : mapIndexGraphBlank;
        long bucket = map.get(graph.getKey());
        if (bucket == Constants.KEY_NULL)
            return;
        long newBucket = bucket;
        long previous = Constants.KEY_NULL;
        long current = bucket;
        long next;
        while (current != Constants.KEY_NULL) {
            boolean empty = removeAllOnSingleGraphPage(current, graph, property, object, bufferDecremented, bufferRemoved);
            next = bufferQNPrevious;
            if (empty) {
                store.free(current);
                if (previous == Constants.KEY_NULL) {
                    // the first element
                    newBucket = next;
                    current = Constants.KEY_NULL;
                } else {
                    try (Access element = store.access(previous, true)) {
                        element.writeLong(next);
                    }
                    current = previous;
                }
            }
            previous = current;
            current = next;
        }

        if (newBucket == Constants.KEY_NULL) {
            // graph completely removed
            map.tryRemove(graph.getKey(), bucket);
        } else if (newBucket != bucket) {
            map.compareAndSet(graph.getKey(), bucket, newBucket);
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
     */
    private boolean removeAllOnSingleGraphPage(long pageKey, PersistedNode graph, PersistedNode property, PersistedNode object, List<MQuad> bufferDecremented, List<MQuad> bufferRemoved) {
        try (Access element = store.access(pageKey, false)) {
            bufferQNPrevious = element.readLong();
            int radical = element.readInt();
            int count = element.readInt();
            int c = count;
            for (int i = 0; i != GINDEX_ENTRY_MAX_ITEM_COUNT; i++) {
                int sk = element.readInt();
                int multiplicity = element.readInt();
                if (sk == PersistedNode.SERIALIZED_SIZE)
                    continue;
                long child = getFullKey(radical, sk);
                int size = bufferRemoved.size();
                boolean isEmpty = removeAllOnSubject(child, property, object, graph, bufferDecremented, bufferRemoved);
                if (isEmpty) {
                    try (Access subjectEntry = store.access(child, false)) {
                        PersistedNode subject = getNode(subjectEntry.seek(8).readInt(), subjectEntry.readLong());
                        StoredMap mapSubjects = mapFor(subject);
                        mapSubjects.tryRemove(subject.getKey(), mapSubjects.get(subject.getKey()));
                    } catch (UnsupportedNodeType exception) {
                        Logging.get().error(exception);
                    }
                }
                multiplicity -= bufferRemoved.size() - size;
                if (multiplicity <= 0) {
                    element.seek(i * 8 + 8 + 4 + 4);
                    element.writeInt((int) Constants.KEY_NULL);
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
     */
    private void removeAllDefault(PersistedNode property, PersistedNode object, List<MQuad> bufferDecremented, List<MQuad> bufferRemoved) {
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
    private void removeAllDefault(StoredMap map, PersistedNode property, PersistedNode object, List<MQuad> bufferDecremented, List<MQuad> bufferRemoved) {
        Iterator<StoredMap.Entry> iterator = map.entries();
        while (iterator.hasNext()) {
            int size = bufferRemoved.size();
            boolean isEmpty = removeAllOnSubject(iterator.next().value, property, object, null, bufferDecremented, bufferRemoved);
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
     */
    private boolean removeAllOnSubject(long subjectKey, PersistedNode property, PersistedNode object, PersistedNode graph, List<MQuad> bufferDecremented, List<MQuad> bufferRemoved) {
        long child;
        int type;
        long key;
        try (Access element = store.access(subjectKey, false)) {
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

        if (newChild == Constants.KEY_NULL) {
            // the child bucket is empty
            store.free(subjectKey);
            return true;
        } else if (newChild != child) {
            // the head of the bucket of graphs changed
            try (Access element = store.access(subjectKey, true)) {
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
     */
    private long removeAllOnProperty(long bucket, PersistedNode property, PersistedNode object, PersistedNode graph, List<MQuad> bufferDecremented, List<MQuad> bufferRemoved) {
        long previous = Constants.KEY_NULL;
        long current = bucket;
        long next;
        while (current != Constants.KEY_NULL) {
            long child;
            int type;
            long key;
            try (Access element = store.access(current, false)) {
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

            if (newChild == Constants.KEY_NULL) {
                // the child bucket is empty
                store.free(current);
                if (previous == Constants.KEY_NULL) {
                    // this is the first one
                    bucket = next;
                    current = Constants.KEY_NULL;
                } else {
                    try (Access element = store.access(previous, true)) {
                        element.writeLong(next);
                    }
                    current = previous;
                }
            } else if (newChild != child) {
                // the head of the bucket of graphs changed
                try (Access element = store.access(current, true)) {
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
     */
    private long removeAllOnObject(long bucket, PersistedNode object, PersistedNode graph, List<MQuad> bufferDecremented, List<MQuad> bufferRemoved) {
        long previous = Constants.KEY_NULL;
        long current = bucket;
        long next;
        while (current != Constants.KEY_NULL) {
            long child;
            int type;
            long key;
            try (Access element = store.access(current, false)) {
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

            if (newChild == Constants.KEY_NULL) {
                // the child bucket is empty
                store.free(current);
                if (previous == Constants.KEY_NULL) {
                    // this is the first one
                    bucket = next;
                    current = Constants.KEY_NULL;
                } else {
                    try (Access element = store.access(previous, true)) {
                        element.writeLong(next);
                    }
                    current = previous;
                }
            } else if (newChild != child) {
                // the head of the bucket of graphs changed
                try (Access element = store.access(current, true)) {
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
     */
    private long removeAllOnGraph(long bucket, PersistedNode graph, List<MQuad> bufferDecremented, List<MQuad> bufferRemoved) {
        long previous = Constants.KEY_NULL;
        long current = bucket;
        long next;
        while (current != Constants.KEY_NULL) {
            PersistedNode removedGraph = null;
            try (Access element = store.access(current, true)) {
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
                    store.free(current);
                } else {
                    // free this element from the linked list
                    store.free(current);
                    if (previous == Constants.KEY_NULL) {
                        // this is the first one
                        return next;
                    } else {
                        try (Access element = store.access(previous, true)) {
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
            return Constants.KEY_NULL;
        return bucket;
    }

    @Override
    public void doClear(List<MQuad> buffer) {
        Iterator<StoredMap.Entry> iterator = mapSubjectIRI.entries();
        while (iterator.hasNext())
            clearOnSubject(iterator.next().value, null, buffer);
        mapSubjectIRI.clear();
        mapIndexGraphIRI.clear();

        iterator = mapSubjectBlank.entries();
        while (iterator.hasNext())
            clearOnSubject(iterator.next().value, null, buffer);
        mapSubjectBlank.clear();
        mapIndexGraphBlank.clear();

        iterator = mapSubjectAnon.entries();
        while (iterator.hasNext())
            clearOnSubject(iterator.next().value, null, buffer);
        mapSubjectAnon.clear();
    }

    @Override
    public void doClear(GraphNode graph, List<MQuad> buffer) throws UnsupportedNodeType {
        PersistedNode pGraph = nodes.getPersistent(graph, false);
        if (pGraph == null)
            return;
        StoredMap map = pGraph.getNodeType() == Node.TYPE_IRI ? mapIndexGraphIRI : mapIndexGraphBlank;
        long bucket = map.get(pGraph.getKey());
        if (bucket == Constants.KEY_NULL)
            return;
        long current = bucket;
        while (current != Constants.KEY_NULL) {
            long next;
            try (Access element = store.access(current, false)) {
                next = element.readLong();
                int radical = element.readInt();
                int count = element.readInt();
                for (int i = 0; i != GINDEX_ENTRY_MAX_ITEM_COUNT; i++) {
                    int sk = element.readInt();
                    element.readInt();
                    if (sk != PersistedNode.SERIALIZED_SIZE) {
                        long child = getFullKey(radical, sk);
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
        map.tryRemove(pGraph.getKey(), bucket);
    }

    /**
     * Clears quads from a single subject
     *
     * @param key    The key to the subject
     * @param graph  The graph to match
     * @param buffer The buffer of quads
     * @return Whether the subject is emptied
     */
    private boolean clearOnSubject(long key, PersistedNode graph, List<MQuad> buffer) {
        long child;
        SubjectNode subject;
        try (Access element = store.access(key, false)) {
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

        if (newChild == Constants.KEY_NULL) {
            // the child bucket is empty
            store.free(key);
            return true;
        } else if (newChild != child) {
            // the head of the bucket of graphs changed
            try (Access element = store.access(key, true)) {
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
     */
    private long clearOnProperty(long bucket, PersistedNode graph, List<MQuad> buffer) {
        long previous = Constants.KEY_NULL;
        long current = bucket;
        long next;
        while (current != Constants.KEY_NULL) {
            long child;
            Property property;
            try (Access element = store.access(current, false)) {
                next = element.readLong();
                property = (Property) getNode(element.readInt(), element.readLong());
                child = element.readLong();
            }

            int size = buffer.size();
            long newChild = clearOnObject(child, graph, buffer);
            for (int i = size; i != buffer.size(); i++)
                buffer.get(i).setProperty(property);

            if (newChild == Constants.KEY_NULL) {
                // the child bucket is empty
                store.free(current);
                if (previous == Constants.KEY_NULL) {
                    // this is the first one
                    bucket = next;
                    current = Constants.KEY_NULL;
                } else {
                    try (Access element = store.access(previous, true)) {
                        element.writeLong(next);
                    }
                    current = previous;
                }
            } else if (newChild != child) {
                // the head of the bucket of graphs changed
                try (Access element = store.access(current, true)) {
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
     */
    private long clearOnObject(long bucket, PersistedNode graph, List<MQuad> buffer) {
        long previous = Constants.KEY_NULL;
        long current = bucket;
        long next;
        while (current != Constants.KEY_NULL) {
            long child;
            Node object;
            try (Access element = store.access(current, false)) {
                next = element.readLong();
                object = getNode(element.readInt(), element.readLong());
                child = element.readLong();
            }

            int size = buffer.size();
            long newChild = clearOnGraph(child, graph, buffer);
            for (int i = size; i != buffer.size(); i++)
                buffer.get(i).setObject(object);

            if (newChild == Constants.KEY_NULL) {
                // the child bucket is empty
                store.free(current);
                if (previous == Constants.KEY_NULL) {
                    // this is the first one
                    bucket = next;
                    current = Constants.KEY_NULL;
                } else {
                    try (Access element = store.access(previous, true)) {
                        element.writeLong(next);
                    }
                    current = previous;
                }
            } else if (newChild != child) {
                // the head of the bucket of graphs changed
                try (Access element = store.access(current, true)) {
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
     */
    private long clearOnGraph(long bucket, PersistedNode graph, List<MQuad> buffer) {
        long previous = Constants.KEY_NULL;
        long current = bucket;
        long next;
        while (current != Constants.KEY_NULL) {
            boolean found = false;
            try (Access element = store.access(current, false)) {
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
                if (previous == Constants.KEY_NULL) {
                    // this is the first one
                    return next;
                } else {
                    try (Access element = store.access(previous, true)) {
                        element.writeLong(next);
                    }
                    return bucket;
                }
            }

            previous = current;
            current = next;
        }
        if (graph == null)
            return Constants.KEY_NULL;
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

        copyOnSubject(mapSubjectIRI, pOrigin, pTarget, bufferOld, bufferNew, overwrite);
        copyOnSubject(mapSubjectAnon, pOrigin, pTarget, bufferOld, bufferNew, overwrite);
        copyOnSubject(mapSubjectAnon, pOrigin, pTarget, bufferOld, bufferNew, overwrite);
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
     */
    private void copyOnSubject(StoredMap map, PersistedNode origin, PersistedNode target, List<MQuad> bufferOld, List<MQuad> bufferNew, boolean overwrite) {
        Iterator<StoredMap.Entry> iterator = map.entries();
        while (iterator.hasNext()) {
            int sizeOld = bufferOld.size();
            int sizeNew = bufferNew.size();
            boolean isEmpty = copyOnSubject(iterator.next().value, origin, target, bufferOld, bufferNew, overwrite);
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
     */
    private boolean copyOnSubject(long key, PersistedNode origin, PersistedNode target, List<MQuad> bufferOld, List<MQuad> bufferNew, boolean overwrite) {
        long child;
        SubjectNode subject;
        try (Access element = store.access(key, false)) {
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

        if (newChild == Constants.KEY_NULL) {
            // the child bucket is empty
            store.free(key);
            return true;
        } else if (newChild != child) {
            // the head of the bucket of graphs changed
            try (Access element = store.access(key, true)) {
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
     */
    private long copyOnProperty(long bucket, PersistedNode origin, PersistedNode target, List<MQuad> bufferOld, List<MQuad> bufferNew, boolean overwrite) {
        long previous = Constants.KEY_NULL;
        long current = bucket;
        long next;
        while (current != Constants.KEY_NULL) {
            long child;
            Property property;
            try (Access element = store.access(current, false)) {
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

            if (newChild == Constants.KEY_NULL) {
                // the child bucket is empty
                store.free(current);
                if (previous == Constants.KEY_NULL) {
                    // this is the first one
                    bucket = next;
                    current = Constants.KEY_NULL;
                } else {
                    try (Access element = store.access(previous, true)) {
                        element.writeLong(next);
                    }
                    current = previous;
                }
            } else if (newChild != child) {
                // the head of the bucket of graphs changed
                try (Access element = store.access(current, true)) {
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
     */
    private long copyOnObject(long bucket, PersistedNode origin, PersistedNode target, List<MQuad> bufferOld, List<MQuad> bufferNew, boolean overwrite) {
        long previous = Constants.KEY_NULL;
        long current = bucket;
        long next;
        while (current != Constants.KEY_NULL) {
            long child;
            Node object;
            try (Access element = store.access(current, false)) {
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

            if (newChild == Constants.KEY_NULL) {
                // the child bucket is empty
                store.free(current);
                if (previous == Constants.KEY_NULL) {
                    // this is the first one
                    bucket = next;
                    current = Constants.KEY_NULL;
                } else {
                    try (Access element = store.access(previous, true)) {
                        element.writeLong(next);
                    }
                    current = previous;
                }
            } else if (newChild != child) {
                // the head of the bucket of graphs changed
                try (Access element = store.access(current, true)) {
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
     */
    private long copyOnGraph(long bucket, PersistedNode origin, PersistedNode target, List<MQuad> bufferOld, List<MQuad> bufferNew, boolean overwrite) {
        long keyOrigin = Constants.KEY_NULL;
        long keyTargetPrevious = Constants.KEY_NULL;
        long keyTarget = Constants.KEY_NULL;
        long keyTargetNext = Constants.KEY_NULL;
        long targetMultiplicity = 0;

        // traverse the bucket
        long previous = Constants.KEY_NULL;
        long current = bucket;
        long next;
        while (current != Constants.KEY_NULL) {
            try (Access element = store.access(current, false)) {
                next = element.readLong();
                int type = element.readInt();
                long key = element.readLong();
                if (type == origin.getNodeType() && key == origin.getKey()) {
                    keyOrigin = current;
                    if (keyTarget != Constants.KEY_NULL)
                        break;
                } else if (type == target.getNodeType() && key == target.getKey()) {
                    keyTarget = current;
                    keyTargetPrevious = previous;
                    keyTargetNext = next;
                    targetMultiplicity = element.readLong();
                    if (keyOrigin != Constants.KEY_NULL)
                        break;
                }
            }
            previous = current;
            current = next;
        }

        if (keyOrigin != Constants.KEY_NULL) {
            // if the origin graph is present, copy
            if (keyTarget == Constants.KEY_NULL) {
                // insert the target graph
                keyTarget = newEntry(target);
                try (Access element = store.access(previous, true)) {
                    element.writeLong(keyTarget);
                }
                bufferNew.add(new MQuad((GraphNode) target, 1));
            }
            // write the multiplicity
            try (Access element = store.access(keyTarget, true)) {
                element.seek(8 + 4 + 8).writeLong(targetMultiplicity + 1);
            }
        } else if (overwrite && keyTarget != Constants.KEY_NULL) {
            // the target graph is there but not the old one and we must overwrite
            // we need to free this
            bufferOld.add(new MQuad((GraphNode) target, targetMultiplicity));
            store.free(keyTarget);
            if (keyTargetPrevious == Constants.KEY_NULL) {
                // target is the first node
                return keyTargetNext;
            } else {
                try (Access element = store.access(keyTargetPrevious, true)) {
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

        moveOnSubject(mapSubjectIRI, pOrigin, pTarget, bufferOld, bufferNew);
        moveOnSubject(mapSubjectAnon, pOrigin, pTarget, bufferOld, bufferNew);
        moveOnSubject(mapSubjectAnon, pOrigin, pTarget, bufferOld, bufferNew);
    }

    /**
     * Moves all the quads with the specified origin graph, to quads with the target graph.
     *
     * @param map       The map of subjects
     * @param origin    The origin graph
     * @param target    The target graph
     * @param bufferOld The buffer of the removed quads
     * @param bufferNew The buffer of the new quads
     */
    private void moveOnSubject(StoredMap map, PersistedNode origin, PersistedNode target, List<MQuad> bufferOld, List<MQuad> bufferNew) {
        Iterator<StoredMap.Entry> iterator = map.entries();
        while (iterator.hasNext()) {
            int sizeOld = bufferOld.size();
            int sizeNew = bufferNew.size();
            boolean isEmpty = moveOnSubject(iterator.next().value, origin, target, bufferOld, bufferNew);
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
     */
    private boolean moveOnSubject(long key, PersistedNode origin, PersistedNode target, List<MQuad> bufferOld, List<MQuad> bufferNew) {
        long child;
        SubjectNode subject;
        try (Access element = store.access(key, false)) {
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

        if (newChild == Constants.KEY_NULL) {
            // the child bucket is empty
            store.free(key);
            return true;
        } else if (newChild != child) {
            // the head of the bucket of graphs changed
            try (Access element = store.access(key, true)) {
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
     */
    private long moveOnProperty(long bucket, PersistedNode origin, PersistedNode target, List<MQuad> bufferOld, List<MQuad> bufferNew) {
        long previous = Constants.KEY_NULL;
        long current = bucket;
        long next;
        while (current != Constants.KEY_NULL) {
            long child;
            Property property;
            try (Access element = store.access(current, false)) {
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

            if (newChild == Constants.KEY_NULL) {
                // the child bucket is empty
                store.free(current);
                if (previous == Constants.KEY_NULL) {
                    // this is the first one
                    bucket = next;
                    current = Constants.KEY_NULL;
                } else {
                    try (Access element = store.access(previous, true)) {
                        element.writeLong(next);
                    }
                    current = previous;
                }
            } else if (newChild != child) {
                // the head of the bucket of graphs changed
                try (Access element = store.access(current, true)) {
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
     */
    private long moveOnObject(long bucket, PersistedNode origin, PersistedNode target, List<MQuad> bufferOld, List<MQuad> bufferNew) {
        long previous = Constants.KEY_NULL;
        long current = bucket;
        long next;
        while (current != Constants.KEY_NULL) {
            long child;
            Node object;
            try (Access element = store.access(current, false)) {
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

            if (newChild == Constants.KEY_NULL) {
                // the child bucket is empty
                store.free(current);
                if (previous == Constants.KEY_NULL) {
                    // this is the first one
                    bucket = next;
                    current = Constants.KEY_NULL;
                } else {
                    try (Access element = store.access(previous, true)) {
                        element.writeLong(next);
                    }
                    current = previous;
                }
            } else if (newChild != child) {
                // the head of the bucket of graphs changed
                try (Access element = store.access(current, true)) {
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
     */
    private long moveOnGraph(long bucket, PersistedNode origin, PersistedNode target, List<MQuad> bufferOld, List<MQuad> bufferNew) {
        long keyOriginPrevious = Constants.KEY_NULL;
        long keyOrigin = Constants.KEY_NULL;
        long keyOriginNext = Constants.KEY_NULL;
        long originMultiplicity = 0;
        long keyTargetPrevious = Constants.KEY_NULL;
        long keyTarget = Constants.KEY_NULL;
        long keyTargetNext = Constants.KEY_NULL;
        long targetMultiplicity = 0;

        // traverse the bucket
        long previous = Constants.KEY_NULL;
        long current = bucket;
        long next;
        while (current != Constants.KEY_NULL) {
            try (Access element = store.access(current, false)) {
                next = element.readLong();
                int type = element.readInt();
                long key = element.readLong();
                if (type == origin.getNodeType() && key == origin.getKey()) {
                    keyOrigin = current;
                    keyOriginPrevious = previous;
                    keyOriginNext = next;
                    originMultiplicity = element.readLong();
                    if (keyTarget != Constants.KEY_NULL)
                        break;
                } else if (type == target.getNodeType() && key == target.getKey()) {
                    keyTarget = current;
                    keyTargetPrevious = previous;
                    keyTargetNext = next;
                    targetMultiplicity = element.readLong();
                    if (keyOrigin != Constants.KEY_NULL)
                        break;
                }
            }
            previous = current;
            current = next;
        }

        if (keyOrigin != Constants.KEY_NULL) {
            // if the origin graph is present, move
            // free the origin
            bufferOld.add(new MQuad((GraphNode) origin, originMultiplicity));
            if (keyTarget != Constants.KEY_NULL) {
                // the target graph is also here, increment it
                try (Access element = store.access(keyTarget, true)) {
                    element.seek(8 + 4 + 8).writeLong(targetMultiplicity + 1);
                }
                // free the origin
                store.free(keyOrigin);
                if (keyOriginPrevious == Constants.KEY_NULL) {
                    // target is the first node
                    return keyOriginNext;
                } else {
                    try (Access element = store.access(keyOriginPrevious, true)) {
                        element.writeLong(keyOriginNext);
                    }
                }
            } else {
                // replace the origin graph by the target one
                // reset the multiplicity
                bufferNew.add(new MQuad((GraphNode) target, 1));
                try (Access element = store.access(keyOrigin, true)) {
                    element.seek(8);
                    element.writeInt(target.getNodeType());
                    element.writeLong(target.getKey());
                    element.writeLong(1);
                }
            }
        } else if (keyTarget != Constants.KEY_NULL) {
            // the target graph is there but not the old one
            // we need to free this
            bufferOld.add(new MQuad((GraphNode) target, targetMultiplicity));
            store.free(keyTarget);
            if (keyTargetPrevious == Constants.KEY_NULL) {
                // target is the first node
                return keyTargetNext;
            } else {
                try (Access element = store.access(keyTargetPrevious, true)) {
                    element.writeLong(keyTargetNext);
                }
            }
        }
        return bucket;
    }

    /**
     * Gets the subject map for the specified subject
     *
     * @param subject A quad subject
     * @return The appropriate map
     * @throws UnsupportedNodeType When the subject node is not recognized
     */
    private StoredMap mapFor(PersistedNode subject) throws UnsupportedNodeType {
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
     */
    private long lookupQNode(long from, PersistedNode node, boolean resolve) {
        bufferQNPrevious = from;
        long current;
        try (Access entry = store.access(from, false)) {
            current = entry.seek(QUAD_ENTRY_SIZE - 8).readLong();
        }
        if (current == Constants.KEY_NULL) {
            if (!resolve)
                return Constants.KEY_NULL;
            // not here
            current = newEntry(node);
            try (Access entry = store.access(from, true)) {
                entry.seek(QUAD_ENTRY_SIZE - 8).writeLong(current);
            }
            return current;
        }
        // follow the chain
        while (current != Constants.KEY_NULL) {
            try (Access entry = store.access(current, false)) {
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
            return Constants.KEY_NULL;
        current = newEntry(node);
        try (Access entry = store.access(bufferQNPrevious, true)) {
            entry.writeLong(current);
        }
        return current;
    }

    /*
    Utility API
     */

    /**
     * Writes the new quad node entry for the specified node
     *
     * @param node A quad node
     * @return The key to the entry
     */
    private long newEntry(PersistedNode node) {
        long key = store.allocate(QUAD_ENTRY_SIZE);
        try (Access entry = store.access(key, true)) {
            entry.writeLong(Constants.KEY_NULL);
            entry.writeInt(node.getNodeType());
            entry.writeLong(node.getKey());
            entry.writeLong(Constants.KEY_NULL);
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
            case Node.TYPE_DYNAMIC:
                return nodes.getDynamicNodeFor(key);
        }
        // cannot happen, avoids being nullable
        return nodes.getBlankNodeFor(-1);
    }

    /**
     * Gets an iterator over all the subjects
     *
     * @return The iterator
     */
    private Iterator<Long> getAllSubjects() {
        return new ConcatenatedIterator<Long>(new Iterator[]{
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
    private Iterator<Long> getSubjectIterator(StoredMap map) {
        return new AdaptingIterator<>(map.entries(), mapEntry -> mapEntry.value);
    }

    /**
     * Iterator over the quad node in a bucket
     */
    private static class QNodeIterator implements Iterator<Long> {
        /**
         * The backend
         */
        private final ObjectStore backend;
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
        public QNodeIterator(ObjectStore backend, long entry) {
            this.backend = backend;
            next = entry;
            value = Constants.KEY_NULL;
        }

        @Override
        public boolean hasNext() {
            return next != Constants.KEY_NULL;
        }

        @Override
        public Long next() {
            try (Access entry = backend.access(next, false)) {
                value = next;
                next = entry.readLong();
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
        private final ObjectStore backend;
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
        public GraphQNodeIterator(ObjectStore backend, long entry) {
            this.backend = backend;
            this.keyEntry = entry;
            this.index = -1;
            this.next = findNext();
            this.value = Constants.KEY_NULL;
        }

        /**
         * Finds the next quad node
         *
         * @return The next quad node
         */
        private long findNext() {
            while (true) {
                index++;
                if (index == GINDEX_ENTRY_MAX_ITEM_COUNT) {
                    try (Access entry = backend.access(keyEntry, false)) {
                        keyEntry = entry.readLong();
                    }
                    if (keyEntry == Constants.KEY_NULL)
                        return Constants.KEY_NULL;
                    index = 0;
                }
                try (Access entry = backend.access(keyEntry, false)) {
                    radical = entry.seek(8).readInt();
                    for (int i = index; i != GINDEX_ENTRY_MAX_ITEM_COUNT; i++) {
                        int ek = entry.seek(8 + 4 + 4 + i * 8).readInt();
                        if (ek != Constants.KEY_NULL) {
                            index = i;
                            return getFullKey(radical, ek);
                        }
                    }
                }
            }
        }

        @Override
        public boolean hasNext() {
            return next != Constants.KEY_NULL;
        }

        @Override
        public Long next() {
            value = next;
            next = findNext();
            return value;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
