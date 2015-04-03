/**********************************************************************
 * Copyright (c) 2014 Laurent Wouters
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
 **********************************************************************/

package org.xowl.store.rdf;

import org.xowl.store.Vocabulary;
import org.xowl.store.cache.StringStore;
import org.xowl.utils.collections.*;
import org.xowl.utils.data.Attribute;
import org.xowl.utils.data.AttributeTypeException;
import org.xowl.utils.data.Dataset;

import java.io.IOException;
import java.util.*;

/**
 * Represents an RDF graph
 *
 * @author Laurent Wouters
 */
public class RDFStore implements ChangeListener {
    /**
     * When adding a quad, something weird happened
     */
    public static final int ADD_RESULT_UNKNOWN = 0;
    /**
     * When adding a quad, it was already present and its multiplicity incremented
     */
    public static final int ADD_RESULT_INCREMENT = 1;
    /**
     * When adding a quad, it was not already present and was hterefore new
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
     * Initial size of the blank node store
     */
    protected static final int INIT_BLANK_SIZE = 1024;

    /**
     * The resource name for the string store data file
     */
    private static final String RESOURCE_STRINGS_DATA = ".strings.txt";
    /**
     * The resource name for the string store index file
     */
    private static final String RESOURCE_STRINGS_INDEX = ".strings.index";
    /**
     * The resource name for the RDF graph
     */
    private static final String RESOURCE_GRAPH = ".graph";
    /**
     * The identifier key for the serialization of the nodes
     */
    protected static final String SERIALIZATION_NODES = "Nodes";
    /**
     * The identifier key for the serialization of a collection of nodes or edges
     */
    protected static final String SERIALIZATION_COLLECTION = "Collection";
    /**
     * The identifier key for the serialization of the edges
     */
    protected static final String SERIALIZATION_EDGES = "Edges";
    /**
     * The identifier key for the serialization of the nextBlank attribute
     */
    protected static final String SERIALIZATION_NEXT_BLANK = "nextBlank";
    /**
     * The identifier key for the serialization of the id attribute
     */
    protected static final String SERIALIZATION_ID = "id";


    /**
     * Default URIs for the anonymous RDF graphs
     */
    private static final String DEFAULT_GRAPH_URIS = "http://xowl.org/store/rdfgraphs/";
    /**
     * URI of the default graph
     */
    private static final String DEFAULT_GRAPH = "http://xowl.org/store/rdfgraphs/default";

    /**
     * Creates the URI of a new anonymous RDF graph
     *
     * @return The URI of a new anonymous RDF graph
     */
    public static String createAnonymousGraph() {
        return DEFAULT_GRAPH_URIS + UUID.randomUUID().toString();
    }


    /**
     * The embedded string store
     */
    protected StringStore sStore;
    /**
     * The current listeners on this store
     */
    protected Collection<ChangeListener> listeners;
    /**
     * The store of existing IRI Reference nodes
     */
    protected Map<Integer, IRINode> mapNodeIRIs;
    /**
     * The store of existing literal nodes
     */
    protected Map<Integer, LiteralBucket> mapNodeLiterals;
    /**
     * The store for edges starting from IRI nodes
     */
    protected Map<Integer, EdgeBucket> edgesIRI;
    /**
     * The store for edges starting from blank nodes
     */
    protected EdgeBucket[] edgesBlank;
    /**
     * The next blank index and identifier
     */
    protected int nextBlank;

    /**
     * Initializes this graph
     *
     * @throws java.io.IOException when the store cannot allocate a temporary file
     */
    public RDFStore() throws IOException {
        sStore = new StringStore();
        listeners = new ArrayList<>();
        mapNodeIRIs = new HashMap<>();
        mapNodeLiterals = new HashMap<>();
        edgesIRI = new HashMap<>();
        edgesBlank = new EdgeBucket[INIT_BLANK_SIZE];
        nextBlank = 0;
    }

    /**
     * Initializes this graph from a serialized data source
     *
     * @param path The common path for the store resources
     * @throws java.io.IOException when the store cannot allocate a temporary file
     */
    public RDFStore(String path) throws IOException {
        listeners = new ArrayList<>();
        mapNodeIRIs = new HashMap<>();
        mapNodeLiterals = new HashMap<>();
        edgesIRI = new HashMap<>();
        edgesBlank = new EdgeBucket[INIT_BLANK_SIZE];
        load(path);
    }

    /**
     * Adds the specified listener to this store
     *
     * @param listener A listener
     */
    public void addListener(ChangeListener listener) {
        listeners.add(listener);
    }

    /**
     * Removes the specified listener from this store
     *
     * @param listener A listener
     */
    public void removeListener(ChangeListener listener) {
        listeners.remove(listener);
    }

    /**
     * Broadcasts the information that a new quad was added
     *
     * @param quad The quad
     */
    protected void onQuadAdded(Quad quad) {
        Change change = new Change(quad, true);
        for (ChangeListener listener : listeners)
            listener.onChange(change);
    }

    /**
     * Broadcasts the information that a quad was removed
     *
     * @param quad The quad
     */
    protected void onQuadRemoved(Quad quad) {
        Change change = new Change(quad, false);
        for (ChangeListener listener : listeners)
            listener.onChange(change);
    }

    /**
     * Creates a new node within the specified graph
     *
     * @param graph A graph node
     * @return The new IRI node
     */
    public IRINode newNodeIRI(GraphNode graph) {
        if (graph != null && graph.getNodeType() == IRINode.TYPE) {
            String value = ((IRINode) graph).getIRIValue();
            return getNodeIRI(value + "#" + UUID.randomUUID().toString());
        } else {
            return getNodeIRI(DEFAULT_GRAPH + "#" + UUID.randomUUID().toString());
        }
    }

    /**
     * Gets the RDF node for the given IRI
     *
     * @param iri An IRI
     * @return The associated RDF node
     */
    public IRINode getNodeIRI(String iri) {
        int key = sStore.store(iri);
        IRINode node = mapNodeIRIs.get(key);
        if (node != null)
            return node;
        node = new IRINodeImpl(sStore, key);
        mapNodeIRIs.put(key, node);
        return node;
    }

    /**
     * Gets the RDF node for the given existing IRI
     * If the IRI is not registered to a node in this store, return null
     *
     * @param iri An IRI
     * @return The associated RDF node, or null if the IRI is not registered to any node
     */
    public IRINode getNodeExistingIRI(String iri) {
        int key = sStore.contains(iri);
        if (key == StringStore.VALUE_NOT_PRESENT)
            return null;
        return mapNodeIRIs.get(key);
    }

    /**
     * Gets a new RDF blank node
     *
     * @return A new RDF blank node
     */
    public BlankNode newNodeBlank() {
        return new BlankNode(nextBlank++);
    }

    /**
     * Gets the RDF node for the specified literal
     *
     * @param lex      The lexical part of the literal
     * @param datatype The literal's data-type
     * @param lang     The literals' language tag
     * @return The associated RDF node
     */
    public LiteralNode getLiteralNode(String lex, String datatype, String lang) {
        int lexKey = sStore.store(lex);
        int typeKey = sStore.store(datatype);
        int langKey = lang != null ? sStore.store(lang) : -1;

        LiteralBucket bucket = mapNodeLiterals.get(lexKey);
        if (bucket == null) {
            bucket = new LiteralBucket();
            mapNodeLiterals.put(lexKey, bucket);
        }

        LiteralNode node = bucket.get(typeKey, langKey);
        if (node != null)
            return node;
        node = new LiteralNodeImpl(sStore, lexKey, typeKey, langKey);
        bucket.add(typeKey, langKey, node);
        return node;
    }

    /**
     * Reacts to the specified change
     *
     * @param change A change
     */
    public void onChange(Change change) {
        try {
            insert(change);
        } catch (UnsupportedNodeType ex) {
            // do nothing
        }
    }

    /**
     * Reacts to the specified changeset
     *
     * @param changeset A changeset
     */
    public void onChange(Changeset changeset) {
        try {
            insert(changeset);
        } catch (UnsupportedNodeType ex) {
            // do nothing
        }
    }

    /**
     * Inserts the specified change in this store
     *
     * @param change A change
     * @throws org.xowl.store.rdf.UnsupportedNodeType when the subject node type is unsupported
     */
    public void insert(Change change) throws UnsupportedNodeType {
        Quad quad = change.getValue();
        if (change.isPositive()) {
            int result = doAddEdge(quad.getGraph(), quad.getSubject(), quad.getProperty(), quad.getObject());
            if (result == ADD_RESULT_NEW) {
                for (ChangeListener listener : listeners) {
                    listener.onChange(change);
                }
            }
        } else {
            int result = doRemoveEdge(quad.getGraph(), quad.getSubject(), quad.getProperty(), quad.getObject());
            if (result == REMOVE_RESULT_REMOVED) {
                for (ChangeListener listener : listeners) {
                    listener.onChange(change);
                }
            }
        }
    }

    /**
     * Inserts the specified changeset in this store
     *
     * @param changeset A changeset
     * @throws org.xowl.store.rdf.UnsupportedNodeType when the subject node type is unsupported
     */
    public void insert(Changeset changeset) throws UnsupportedNodeType {
        Collection<Quad> positives = new ArrayList<>();
        Collection<Quad> negatives = new ArrayList<>();
        for (Quad quad : changeset.getPositives()) {
            int result = doAddEdge(quad.getGraph(), quad.getSubject(), quad.getProperty(), quad.getObject());
            if (result == ADD_RESULT_NEW)
                positives.add(quad);
        }
        for (Quad quad : changeset.getNegatives()) {
            int result = doRemoveEdge(quad.getGraph(), quad.getSubject(), quad.getProperty(), quad.getObject());
            if (result == REMOVE_RESULT_REMOVED)
                negatives.add(quad);
        }
        if (!positives.isEmpty() || !negatives.isEmpty()) {
            // transmit the changes only if a there are some!
            Changeset newChangeset = new Changeset(positives, negatives);
            for (ChangeListener listener : listeners) {
                listener.onChange(newChangeset);
            }
        }
    }

    /**
     * Adds the specified quad to this graph
     *
     * @param quad A quad
     * @throws org.xowl.store.rdf.UnsupportedNodeType when the subject node type is unsupported
     */
    public void add(Quad quad) throws UnsupportedNodeType {
        int result = doAddEdge(quad.getGraph(), quad.getSubject(), quad.getProperty(), quad.getObject());
        if (result == ADD_RESULT_NEW)
            onQuadAdded(quad);
    }

    /**
     * Adds the specified quad to this graph
     *
     * @param graph    The graph containing the quad
     * @param subject  The quad subject node
     * @param property The quad property
     * @param value    The quad value
     * @throws org.xowl.store.rdf.UnsupportedNodeType when the subject node type is unsupported
     */
    public void add(GraphNode graph, SubjectNode subject, Property property, Node value) throws UnsupportedNodeType {
        int result = doAddEdge(graph, subject, property, value);
        if (result == ADD_RESULT_NEW)
            onQuadAdded(new Quad(graph, subject, property, value));
    }

    /**
     * Adds the specified quad to this graph
     *
     * @param graph    The graph containing the quad
     * @param subject  The quad subject node
     * @param property The quad property
     * @param value    The quad value
     * @return The operation result
     * @throws org.xowl.store.rdf.UnsupportedNodeType when the subject node type is unsupported
     */
    protected int doAddEdge(GraphNode graph, SubjectNode subject, Property property, Node value) throws UnsupportedNodeType {
        switch (subject.getNodeType()) {
            case IRINode.TYPE:
                return doAddEdgeFromIRI(graph, (IRINode) subject, property, value);
            case BlankNode.TYPE:
                return doAddEdgeFromBlank(graph, (BlankNode) subject, property, value);
            default:
                throw new UnsupportedNodeType(subject, "Subject node must be IRI or BLANK");
        }
    }

    /**
     * Adds the specified quad to this graph
     *
     * @param graph    The graph containing the quad
     * @param subject  The quad subject node
     * @param property The quad property
     * @param value    The quad value
     * @return The operation result
     */
    protected int doAddEdgeFromIRI(GraphNode graph, IRINode subject, Property property, Node value) {
        int key = ((IRINodeImpl) subject).getKey();
        EdgeBucket bucket = edgesIRI.get(key);
        if (bucket == null) {
            bucket = new EdgeBucket();
            edgesIRI.put(key, bucket);
        }
        return bucket.add(graph, property, value);
    }

    /**
     * Adds the specified quad to this graph
     *
     * @param graph    The graph containing the quad
     * @param subject  The quad subject node
     * @param property The quad property
     * @param value    The quad value
     * @return The operation result
     */
    protected int doAddEdgeFromBlank(GraphNode graph, BlankNode subject, Property property, Node value) {
        int key = subject.getBlankID();
        while (key >= edgesBlank.length)
            edgesBlank = Arrays.copyOf(edgesBlank, edgesBlank.length + INIT_BLANK_SIZE);
        EdgeBucket bucket = edgesBlank[key];
        if (bucket == null) {
            bucket = new EdgeBucket();
            edgesBlank[key] = bucket;
        }
        return bucket.add(graph, property, value);
    }

    /**
     * Removes the specified quad from this graph
     *
     * @param quad A quad
     * @throws org.xowl.store.rdf.UnsupportedNodeType when the subject node type is unsupported
     */
    public void remove(Quad quad) throws UnsupportedNodeType {
        int result = doRemoveEdge(quad.getGraph(), quad.getSubject(), quad.getProperty(), quad.getObject());
        if (result == REMOVE_RESULT_REMOVED)
            onQuadRemoved(quad);
    }

    /**
     * Removes the specified quad from this graph
     *
     * @param graph    The graph containing the quad
     * @param subject  The quad subject node
     * @param property The quad property
     * @param value    The quad value
     * @throws org.xowl.store.rdf.UnsupportedNodeType when the subject node type is unsupported
     */
    public void remove(GraphNode graph, SubjectNode subject, Property property, Node value) throws UnsupportedNodeType {
        int result = doRemoveEdge(graph, subject, property, value);
        if (result == REMOVE_RESULT_REMOVED)
            onQuadRemoved(new Quad(graph, subject, property, value));
    }

    /**
     * Removes the specified quad from this graph
     *
     * @param graph    The graph containing the quad
     * @param subject  The quad subject node
     * @param property The quad property
     * @param value    The quad value
     * @return The operation result
     * @throws org.xowl.store.rdf.UnsupportedNodeType when the subject node type is unsupported
     */
    protected int doRemoveEdge(GraphNode graph, SubjectNode subject, Property property, Node value) throws UnsupportedNodeType {
        switch (subject.getNodeType()) {
            case IRINode.TYPE:
                return doRemoveEdgeFromIRI(graph, (IRINode) subject, property, value);
            case BlankNode.TYPE:
                return doRemoveEdgeFromBlank(graph, (BlankNode) subject, property, value);
            default:
                throw new UnsupportedNodeType(subject, "Subject node must be IRI or BLANK");
        }
    }

    /**
     * Removes the specified quad from this graph
     *
     * @param graph    The graph containing the quad
     * @param subject  The quad subject node
     * @param property The quad property
     * @param value    The quad value
     * @return The operation result
     */
    protected int doRemoveEdgeFromIRI(GraphNode graph, IRINode subject, Property property, Node value) {
        int key = ((IRINodeImpl) subject).getKey();
        EdgeBucket bucket = edgesIRI.get(key);
        if (bucket == null)
            return REMOVE_RESULT_NOT_FOUND;
        int result = bucket.remove(graph, property, value);
        if (result == REMOVE_RESULT_EMPTIED) {
            edgesIRI.remove(key);
            return REMOVE_RESULT_REMOVED;
        }
        return result;
    }

    /**
     * Removes the specified quad from this graph
     *
     * @param graph    The graph containing the quad
     * @param subject  The quad subject node
     * @param property The quad property
     * @param value    The quad value
     * @return The operation result
     */
    protected int doRemoveEdgeFromBlank(GraphNode graph, BlankNode subject, Property property, Node value) {
        int key = subject.getBlankID();
        if (key >= edgesBlank.length)
            return REMOVE_RESULT_NOT_FOUND;
        EdgeBucket bucket = edgesBlank[key];
        if (bucket == null)
            return REMOVE_RESULT_NOT_FOUND;
        int result = bucket.remove(graph, property, value);
        if (result == REMOVE_RESULT_EMPTIED) {
            edgesBlank[key] = null;
            return REMOVE_RESULT_REMOVED;
        }
        return result;
    }

    /**
     * Gets an iterator over all the quads in this store
     *
     * @return An iterator over the results
     */
    public Iterator<Quad> getAll() {
        return getAll(null, null, null, null);
    }

    /**
     * Gets an iterator over all the quads in this store that are in the specified graph
     *
     * @param graph A containing graph to match, or null
     * @return An iterator over the results
     */
    public Iterator<Quad> getAll(GraphNode graph) {
        return getAll(graph, null, null, null);
    }

    /**
     * Gets an iterator over all the quads in this store that matches the given values
     *
     * @param subject  A subject node to match, or null
     * @param property A property to match, or null
     * @param object   An object node to match, or null
     * @return An iterator over the results
     */
    public Iterator<Quad> getAll(SubjectNode subject, Property property, Node object) {
        return getAll(null, subject, property, object);
    }

    /**
     * Gets an iterator over all the quads in this store that matches the given values
     *
     * @param graph    A containing graph to match, or null
     * @param subject  A subject node to match, or null
     * @param property A property to match, or null
     * @param object   An object node to match, or null
     * @return An iterator over the results
     */
    public Iterator<Quad> getAll(final GraphNode graph, final SubjectNode subject, final Property property, final Node object) {
        if (subject == null || subject.getNodeType() == VariableNode.TYPE) {
            return new AdaptingIterator<>(new CombiningIterator<>(getAllSubjects(), new Adapter<Iterator<Quad>>() {
                @Override
                public <X> Iterator<Quad> adapt(X element) {
                    Couple<SubjectNode, EdgeBucket> subject = (Couple<SubjectNode, EdgeBucket>) element;
                    return subject.y.getAll(graph, property, object);
                }
            }), new Adapter<Quad>() {
                @Override
                public <X> Quad adapt(X element) {
                    Couple<Couple<SubjectNode, EdgeBucket>, Quad> result = (Couple<Couple<SubjectNode, EdgeBucket>, Quad>) element;
                    result.y.setSubject(result.x.x);
                    return result.y;
                }
            });
        } else {
            EdgeBucket bucket = getBucketFor(subject);
            if (bucket == null)
                return new SingleIterator<>(null);
            return new AdaptingIterator<>(bucket.getAll(graph, property, object), new Adapter<Quad>() {
                @Override
                public <X> Quad adapt(X element) {
                    Quad quad = (Quad) element;
                    quad.setSubject(subject);
                    return quad;
                }
            });
        }
    }

    /**
     * Gets the number of different quads in this store
     *
     * @return The number of different quads
     */
    public int count() {
        return count(null, null, null, null);
    }

    /**
     * Gets the number of different quads in this store that matches the given values
     *
     * @param graph A containing graph to match, or null
     * @return The number of different quads
     */
    public int count(GraphNode graph) {
        return count(graph, null, null, null);
    }

    /**
     * Gets the number of different quads in this store that matches the given values
     *
     * @param subject  A subject node to match, or null
     * @param property A property to match, or null
     * @param object   An object node to match, or null
     * @return The number of different quads
     */
    public int count(SubjectNode subject, Property property, Node object) {
        return count(null, subject, property, object);
    }

    /**
     * Gets the number of different quads in this store that matches the given values
     *
     * @param graph    A containing graph to match, or null
     * @param subject  A subject node to match, or null
     * @param property A property to match, or null
     * @param object   An object node to match, or null
     * @return The number of different quads
     */
    public int count(GraphNode graph, SubjectNode subject, Property property, Node object) {
        if (subject == null || subject.getNodeType() == VariableNode.TYPE) {
            int count = 0;
            Iterator<Couple<SubjectNode, EdgeBucket>> iterator = getAllSubjects();
            while (iterator.hasNext())
                count += iterator.next().y.count(graph, property, object);
            return count;
        } else {
            EdgeBucket bucket = getBucketFor(subject);
            if (bucket == null)
                return 0;
            return bucket.count(graph, property, object);
        }
    }

    /**
     * Gets an iterator over all the subjects starting edges in the graph
     *
     * @return An iterator over all the subjects starting edges in the graph
     */
    protected Iterator<Couple<SubjectNode, EdgeBucket>> getAllSubjects() {
        return new ConcatenatedIterator<Couple<SubjectNode, EdgeBucket>>(new Iterator[]{
                new AdaptingIterator<>(edgesIRI.keySet().iterator(), new Adapter<Couple<SubjectNode, EdgeBucket>>() {
                    @Override
                    public <X> Couple<SubjectNode, EdgeBucket> adapt(X element) {
                        Couple<SubjectNode, EdgeBucket> result = new Couple<>();
                        result.x = mapNodeIRIs.get(element);
                        result.y = edgesIRI.get(element);
                        return result;
                    }
                }),
                new AdaptingIterator<>(new IndexIterator<>(edgesBlank), new Adapter<Couple<SubjectNode, EdgeBucket>>() {
                    @Override
                    public <X> Couple<SubjectNode, EdgeBucket> adapt(X element) {
                        Couple<SubjectNode, EdgeBucket> result = new Couple<>();
                        Integer index = (Integer) element;
                        result.x = new BlankNode(index);
                        result.y = edgesBlank[index];
                        return result;
                    }
                })
        });
    }

    /**
     * Gets an iterator over the elements of the specified RDF list
     *
     * @param list The head node of an RDF list
     * @return An iterator over the list's elements
     */
    public Iterator<Node> getList(SubjectNode list) {
        int keyFirst = sStore.store(Vocabulary.rdfFirst);
        int keyRest = sStore.store(Vocabulary.rdfRest);
        int keyNil = sStore.store(Vocabulary.rdfNil);
        return new ListIterator(keyFirst, keyRest, keyNil, list) {
            @Override
            protected EdgeBucket getBucketOf(SubjectNode node) {
                return getBucketFor(node);
            }
        };
    }

    /**
     * Gets the edge bucket for the specified node
     *
     * @param node A node
     * @return The associated edge bucket, or null if there is none
     */
    protected EdgeBucket getBucketFor(Node node) {
        switch (node.getNodeType()) {
            case IRINode.TYPE:
                return edgesIRI.get(((IRINodeImpl) node).getKey());
            case BlankNode.TYPE:
                int id = ((BlankNode) node).getBlankID();
                if (id >= edgesBlank.length)
                    return null;
                return edgesBlank[id];
            default:
                return null;
        }
    }

    /**
     * Saves this store to the specified resources
     *
     * @param path The common path for the store resources
     * @throws IOException When an IO error occurs
     */
    public void save(String path) throws IOException {
        sStore.save(path + RESOURCE_STRINGS_DATA, path + RESOURCE_STRINGS_INDEX);
        Dataset dataset = new Dataset();
        org.xowl.utils.data.Node treeNodes = new org.xowl.utils.data.Node(dataset, SERIALIZATION_NODES);
        org.xowl.utils.data.Node treeEdges = new org.xowl.utils.data.Node(dataset, SERIALIZATION_EDGES);
        dataset.getTrees().add(treeNodes);
        dataset.getTrees().add(treeEdges);
        serializeIRINodes(dataset, treeNodes);
        serializeBlankNodes(dataset, treeNodes);
        serializeLiteralNodes(dataset, treeNodes);
        serializeIRIEdges(dataset, treeEdges);
        serializeBlankEdges(dataset, treeEdges);
        serializeOtherNodes(dataset, treeNodes);
        serializeOtherEdges(dataset, treeEdges);
        dataset.write(path + RESOURCE_GRAPH);
    }

    /**
     * Serializes the IRI nodes
     *
     * @param dataset   The parent dataset
     * @param treeNodes The parent node to use
     */
    protected void serializeIRINodes(Dataset dataset, org.xowl.utils.data.Node treeNodes) {
        org.xowl.utils.data.Node collection = new org.xowl.utils.data.Node(dataset, SERIALIZATION_COLLECTION);
        Attribute attributeType = new Attribute(dataset, Node.SERIALIZATION_TYPE);
        attributeType.setValue(IRINode.TYPE);
        collection.getAttributes().add(attributeType);
        for (IRINode iriNode : mapNodeIRIs.values()) {
            collection.getChildren().add(iriNode.serialize(dataset));
        }
        treeNodes.getChildren().add(collection);
    }

    /**
     * Serializes the blank nodes
     *
     * @param dataset   The parent dataset
     * @param treeNodes The parent node to use
     */
    protected void serializeBlankNodes(Dataset dataset, org.xowl.utils.data.Node treeNodes) {
        org.xowl.utils.data.Node collection = new org.xowl.utils.data.Node(dataset, SERIALIZATION_COLLECTION);
        Attribute attributeType = new Attribute(dataset, Node.SERIALIZATION_TYPE);
        attributeType.setValue(BlankNode.TYPE);
        collection.getAttributes().add(attributeType);
        Attribute attributeNextBlank = new Attribute(dataset, SERIALIZATION_NEXT_BLANK);
        attributeNextBlank.setValue(nextBlank);
        collection.getAttributes().add(attributeNextBlank);
        treeNodes.getChildren().add(collection);
    }

    /**
     * Serializes the literal nodes
     *
     * @param dataset   The parent dataset
     * @param treeNodes The parent node to use
     */
    protected void serializeLiteralNodes(Dataset dataset, org.xowl.utils.data.Node treeNodes) {
        org.xowl.utils.data.Node collection = new org.xowl.utils.data.Node(dataset, SERIALIZATION_COLLECTION);
        Attribute attributeType = new Attribute(dataset, Node.SERIALIZATION_TYPE);
        attributeType.setValue(LiteralNode.TYPE);
        collection.getAttributes().add(attributeType);
        for (LiteralBucket bucket : mapNodeLiterals.values()) {
            collection.getChildren().add(bucket.serialize(dataset));
        }
        treeNodes.getChildren().add(collection);
    }

    /**
     * Serializes the edges starting from IRI nodes
     *
     * @param dataset   The parent dataset
     * @param treeEdges The parent node to use
     */
    protected void serializeIRIEdges(Dataset dataset, org.xowl.utils.data.Node treeEdges) {
        org.xowl.utils.data.Node collection = new org.xowl.utils.data.Node(dataset, SERIALIZATION_COLLECTION);
        Attribute attributeType = new Attribute(dataset, Node.SERIALIZATION_TYPE);
        attributeType.setValue(IRINode.TYPE);
        collection.getAttributes().add(attributeType);
        for (Map.Entry<Integer, EdgeBucket> entry : edgesIRI.entrySet()) {
            org.xowl.utils.data.Node nodeBucket = entry.getValue().serialize(dataset);
            Attribute attributeID = new Attribute(dataset, SERIALIZATION_ID);
            attributeID.setValue(entry.getKey());
            nodeBucket.getAttributes().add(attributeID);
            collection.getChildren().add(nodeBucket);
        }
        treeEdges.getChildren().add(collection);
    }

    /**
     * Serializes the edges starting from blank nodes
     *
     * @param dataset   The parent dataset
     * @param treeEdges The parent node to use
     */
    protected void serializeBlankEdges(Dataset dataset, org.xowl.utils.data.Node treeEdges) {
        org.xowl.utils.data.Node collection = new org.xowl.utils.data.Node(dataset, SERIALIZATION_COLLECTION);
        Attribute attributeType = new Attribute(dataset, Node.SERIALIZATION_TYPE);
        attributeType.setValue(BlankNode.TYPE);
        collection.getAttributes().add(attributeType);
        for (int i = 0; i != nextBlank; i++) {
            if (edgesBlank[i] == null)
                continue;
            org.xowl.utils.data.Node nodeBucket = edgesBlank[i].serialize(dataset);
            Attribute attributeID = new Attribute(dataset, SERIALIZATION_ID);
            attributeID.setValue(i);
            nodeBucket.getAttributes().add(attributeID);
            collection.getChildren().add(nodeBucket);
        }
        treeEdges.getChildren().add(collection);
    }

    /**
     * Serializes the remaining node collections
     *
     * @param dataset   The parent dataset
     * @param treeNodes The parent node to use
     */
    protected void serializeOtherNodes(Dataset dataset, org.xowl.utils.data.Node treeNodes) {
        // do nothing here
    }

    /**
     * Serializes the remaining edge collections
     *
     * @param dataset   The parent dataset
     * @param treeEdges The parent node to use
     */
    protected void serializeOtherEdges(Dataset dataset, org.xowl.utils.data.Node treeEdges) {
        // do nothing here
    }

    /**
     * Loads this store from the specified resources
     *
     * @param path The common path for the store resources
     * @throws IOException When an IO error occurs
     */
    protected void load(String path) throws IOException {
        sStore = new StringStore(path + RESOURCE_STRINGS_DATA, path + RESOURCE_STRINGS_INDEX);
        try {
            Dataset dataset = Dataset.load(path + RESOURCE_GRAPH);
            for (org.xowl.utils.data.Node collection : dataset.tree(SERIALIZATION_NODES).getChildren())
                deserializeNodes(collection);
            for (org.xowl.utils.data.Node collection : dataset.tree(SERIALIZATION_EDGES).getChildren())
                deserializeEdges(collection);
        } catch (AttributeTypeException ex) {
            // cannot happen
        }
    }

    /**
     * Deserializes the specified collection of nodes
     *
     * @param collection The collection to deserialize
     */
    protected void deserializeNodes(org.xowl.utils.data.Node collection) {
        int type = (int) collection.attribute(Node.SERIALIZATION_TYPE).getValue();
        switch (type) {
            case IRINode.TYPE:
                deserializeIRINodes(collection);
                break;
            case BlankNode.TYPE:
                deserializeBlankNodes(collection);
                break;
            case LiteralNode.TYPE:
                deserializeLiteralNodes(collection);
                break;
            default:
                deserializeOtherNodes(collection);
                break;
        }
    }

    /**
     * Deserializes the IRI nodes
     *
     * @param collection The collection to deserialize
     */
    protected void deserializeIRINodes(org.xowl.utils.data.Node collection) {
        for (org.xowl.utils.data.Node node : collection.getChildren()) {
            IRINodeImpl iriNode = new IRINodeImpl(sStore, node);
            mapNodeIRIs.put(iriNode.getKey(), iriNode);
        }
    }

    /**
     * Deserializes the blank nodes
     *
     * @param collection The collection to deserialize
     */
    protected void deserializeBlankNodes(org.xowl.utils.data.Node collection) {
        nextBlank = (int) collection.attribute(SERIALIZATION_NEXT_BLANK).getValue();
    }

    /**
     * Deserializes the literal nodes
     *
     * @param collection The collection to deserialize
     */
    protected void deserializeLiteralNodes(org.xowl.utils.data.Node collection) {
        for (org.xowl.utils.data.Node node : collection.getChildren()) {
            LiteralBucket bucket = new LiteralBucket(sStore, node);
            mapNodeLiterals.put(bucket.getLexicalKey(), bucket);
        }
    }

    /**
     * Deserializes the specified collection of nodes
     *
     * @param collection The collection to deserialize
     */
    protected void deserializeOtherNodes(org.xowl.utils.data.Node collection) {
        // do nothing here
    }

    /**
     * Deserializes the specified collection of edges
     *
     * @param collection The collection to deserialize
     */
    protected void deserializeEdges(org.xowl.utils.data.Node collection) {
        int type = (int) collection.attribute(Node.SERIALIZATION_TYPE).getValue();
        switch (type) {
            case IRINode.TYPE:
                deserializeIRIEdges(collection);
                break;
            case BlankNode.TYPE:
                deserializeBlankEdges(collection);
                break;
            default:
                deserializeOtherEdges(collection);
                break;
        }
    }

    /**
     * Deserializes the IRI edges
     *
     * @param collection The collection to deserialize
     */
    protected void deserializeIRIEdges(org.xowl.utils.data.Node collection) {
        for (org.xowl.utils.data.Node node : collection.getChildren()) {
            int id = (int) node.attribute(SERIALIZATION_ID).getValue();
            EdgeBucket bucket = new EdgeBucket(this, node);
            edgesIRI.put(id, bucket);
        }
    }

    /**
     * Deserializes the blank edges
     *
     * @param collection The collection to deserialize
     */
    protected void deserializeBlankEdges(org.xowl.utils.data.Node collection) {
        for (org.xowl.utils.data.Node node : collection.getChildren()) {
            int id = (int) node.attribute(SERIALIZATION_ID).getValue();
            EdgeBucket bucket = new EdgeBucket(this, node);
            while (id > edgesBlank.length)
                edgesBlank = Arrays.copyOf(edgesBlank, edgesBlank.length + INIT_BLANK_SIZE);
            edgesBlank[id] = bucket;
        }
    }

    /**
     * Deserializes the specified collection of edges
     *
     * @param collection The collection to deserialize
     */
    protected void deserializeOtherEdges(org.xowl.utils.data.Node collection) {
        // do nothing here
    }

    /**
     * Gets the RDF node for the specified serialized data node
     *
     * @param data A serialized data node
     * @return the corresponding RDF node
     */
    protected Node getNodeFor(org.xowl.utils.data.Node data) {
        int type = (int) data.attribute(Node.SERIALIZATION_TYPE).getValue();
        switch (type) {
            case IRINode.TYPE:
                return mapNodeIRIs.get(data.attribute(IRINodeImpl.SERIALIZATION_KEY).getValue());
            case BlankNode.TYPE:
                return new BlankNode(data);
            case LiteralNode.TYPE:
                int lexical = (int) data.attribute(LiteralNodeImpl.SERIALIZATION_LEXICAL).getValue();
                int datatype = (int) data.attribute(LiteralNodeImpl.SERIALIZATION_DATATYPE).getValue();
                int tag = (int) data.attribute(LiteralNodeImpl.SERIALIZATION_TAG).getValue();
                return mapNodeLiterals.get(lexical).get(datatype, tag);
            default:
                return getOherNodeFor(data);
        }
    }

    /**
     * Gets the RDF node for the specified serialized data node
     *
     * @param data A serialized data node
     * @return the corresponding RDF node
     */
    protected Node getOherNodeFor(org.xowl.utils.data.Node data) {
        // do nothing here
        return null;
    }
}
