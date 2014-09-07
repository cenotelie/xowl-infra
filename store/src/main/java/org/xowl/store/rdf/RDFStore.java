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

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Represents an RDF graph
 *
 * @author Laurent Wouters
 */
public class RDFStore {
    /**
     * Initial size of the blank node store
     */
    protected static final int INIT_BLANK_SIZE = 1024;
    /**
     * The embedded string store
     */
    protected StringStore sStore;
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
        mapNodeIRIs = new HashMap<>();
        mapNodeLiterals = new HashMap<>();
        edgesIRI = new HashMap<>();
        edgesBlank = new EdgeBucket[INIT_BLANK_SIZE];
        nextBlank = 0;
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
     * Gets a new RDF blank node
     *
     * @return A new RDF blank node
     */
    public BlankNode getBlankNode() {
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
     * Adds the specified quad to this graph
     *
     * @param quad A quad
     * @throws org.xowl.store.rdf.UnsupportedNodeType when the subject node type is unsupported
     */
    public void add(Quad quad) throws UnsupportedNodeType {
        add(quad.getGraph(), quad.getSubject(), quad.getProperty(), quad.getObject());
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
        switch (subject.getNodeType()) {
            case IRINode.TYPE:
                addEdgeFromIRI(graph, (IRINode) subject, property, value);
                break;
            case BlankNode.TYPE:
                addEdgeFromBlank(graph, (BlankNode) subject, property, value);
                break;
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
     */
    protected void addEdgeFromIRI(GraphNode graph, IRINode subject, Property property, Node value) {
        int key = ((IRINodeImpl) subject).getKey();
        EdgeBucket bucket = edgesIRI.get(key);
        if (bucket == null) {
            bucket = new EdgeBucket();
            edgesIRI.put(key, bucket);
        }
        bucket.add(graph, property, value);
    }

    /**
     * Adds the specified quad to this graph
     *
     * @param graph    The graph containing the quad
     * @param subject  The quad subject node
     * @param property The quad property
     * @param value    The quad value
     */
    protected void addEdgeFromBlank(GraphNode graph, BlankNode subject, Property property, Node value) {
        int key = subject.getBlankID();
        while (key > edgesBlank.length)
            edgesBlank = Arrays.copyOf(edgesBlank, edgesBlank.length + INIT_BLANK_SIZE);
        EdgeBucket bucket = edgesBlank[key];
        if (bucket == null) {
            bucket = new EdgeBucket();
            edgesBlank[key] = bucket;
        }
        bucket.add(graph, property, value);
    }

    /**
     * Removes the specified quad from this graph
     *
     * @param quad A quad
     * @throws org.xowl.store.rdf.UnsupportedNodeType when the subject node type is unsupported
     */
    public void remove(Quad quad) throws UnsupportedNodeType {
        remove(quad.getGraph(), quad.getSubject(), quad.getProperty(), quad.getObject());
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
        switch (subject.getNodeType()) {
            case IRINode.TYPE:
                removeEdgeFromIRI(graph, (IRINode) subject, property, value);
                break;
            case BlankNode.TYPE:
                removeEdgeFromBlank(graph, (BlankNode) subject, property, value);
                break;
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
     */
    protected void removeEdgeFromIRI(GraphNode graph, IRINode subject, Property property, Node value) {
        int key = ((IRINodeImpl) subject).getKey();
        EdgeBucket bucket = edgesIRI.get(key);
        if (bucket == null)
            return;
        bucket.remove(graph, property, value);
        if (bucket.getSize() == 0)
            edgesIRI.remove(key);
    }

    /**
     * Removes the specified quad from this graph
     *
     * @param graph    The graph containing the quad
     * @param subject  The quad subject node
     * @param property The quad property
     * @param value    The quad value
     */
    protected void removeEdgeFromBlank(GraphNode graph, BlankNode subject, Property property, Node value) {
        int key = subject.getBlankID();
        if (key >= edgesBlank.length)
            return;
        EdgeBucket bucket = edgesBlank[key];
        if (bucket == null)
            return;
        bucket.remove(graph, property, value);
        if (bucket.getSize() == 0)
            edgesBlank[key] = null;
    }

    /**
     * Gets an iterator over all the quads in this store
     *
     * @return An iterator over the results
     */
    public Iterator<Quad> getAll() {
        try {
            return getAll(null, null, null, null);
        } catch (UnsupportedNodeType ex) {
            // cannot happen ...
            return null;
        }
    }

    /**
     * Gets an iterator over all the quads in this store that are in the specified graph
     *
     * @param graph A containing graph to match, or null
     * @return An iterator over the results
     */
    public Iterator<Quad> getAll(GraphNode graph) {
        try {
            return getAll(graph, null, null, null);
        } catch (UnsupportedNodeType ex) {
            // cannot happen ...
            return null;
        }
    }

    /**
     * Gets an iterator over all the quads in this store that matches the given values
     *
     * @param subject  A subject node to match, or null
     * @param property A property to match, or null
     * @param object   An object node to match, or null
     * @return An iterator over the results
     * @throws UnsupportedNodeType when the subject node type is unsupported
     */
    public Iterator<Quad> getAll(SubjectNode subject, Property property, Node object) throws UnsupportedNodeType {
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
     * @throws UnsupportedNodeType when the subject node type is unsupported
     */
    public Iterator<Quad> getAll(final GraphNode graph, SubjectNode subject, final Property property, final Node object) throws UnsupportedNodeType {
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
                throw new UnsupportedNodeType(subject, "Subject node must be IRI or BLANK");
            return bucket.getAll(graph, property, object);
        }
    }

    /**
     * Gets the number of different quads in this store
     *
     * @return The number of different quads
     */
    public int count() {
        try {
            return count(null, null, null, null);
        } catch (UnsupportedNodeType ex) {
            // cannot happen ...
            return 0;
        }
    }

    /**
     * Gets the number of different quads in this store that matches the given values
     *
     * @param graph A containing graph to match, or null
     * @return The number of different quads
     */
    public int count(GraphNode graph) {
        try {
            return count(graph, null, null, null);
        } catch (UnsupportedNodeType ex) {
            // cannot happen ...
            return 0;
        }
    }

    /**
     * Gets the number of different quads in this store that matches the given values
     *
     * @param subject  A subject node to match, or null
     * @param property A property to match, or null
     * @param object   An object node to match, or null
     * @return The number of different quads
     * @throws UnsupportedNodeType when the subject node type is unsupported
     */
    public int count(SubjectNode subject, Property property, Node object) throws UnsupportedNodeType {
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
     * @throws UnsupportedNodeType when the subject node type is unsupported
     */
    public int count(GraphNode graph, SubjectNode subject, Property property, Node object) throws UnsupportedNodeType {
        if (subject == null || subject.getNodeType() == VariableNode.TYPE) {
            int count = 0;
            Iterator<Couple<SubjectNode, EdgeBucket>> iterator = getAllSubjects();
            while (iterator.hasNext())
                count += iterator.next().y.count(graph, property, object);
            return count;
        } else {
            EdgeBucket bucket = getBucketFor(subject);
            if (bucket == null)
                throw new UnsupportedNodeType(subject, "Subject node must be IRI or BLANK");
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
}
