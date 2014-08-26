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

import org.xowl.lang.owl2.Literal;
import org.xowl.lang.owl2.Ontology;
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
public class RDFGraph {
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
    protected Map<Integer, RDFIRIReference> mapNodeIRIs;
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
    public RDFGraph() throws IOException {
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
    public RDFIRIReference getNodeIRI(String iri) {
        int key = sStore.store(iri);
        RDFIRIReference node = mapNodeIRIs.get(key);
        if (node != null)
            return node;
        node = new RDFIRIReferenceImpl(sStore, key);
        mapNodeIRIs.put(key, node);
        return node;
    }

    /**
     * Gets a new RDF blank node
     *
     * @return A new RDF blank node
     */
    public RDFBlankNode getBlankNode() {
        return new RDFBlankNode(nextBlank++);
    }

    /**
     * Gets the RDF node for the specified literal
     *
     * @param lex      The lexical part of the literal
     * @param datatype The literal's data-type
     * @param lang     The literals' language tag
     * @return The associated RDF node
     */
    public RDFLiteralNode getLiteralNode(String lex, String datatype, String lang) {
        int lexKey = sStore.store(lex);
        int typeKey = sStore.store(datatype);
        int langKey = lang != null ? sStore.store(lang) : -1;

        LiteralBucket bucket = mapNodeLiterals.get(lexKey);
        if (bucket == null) {
            bucket = new LiteralBucket();
            mapNodeLiterals.put(lexKey, bucket);
        }

        RDFLiteralNode node = bucket.get(typeKey, langKey);
        if (node != null)
            return node;
        node = new RDFLiteralNodeImpl(sStore, lexKey, typeKey, langKey);
        bucket.add(typeKey, langKey, node);
        return node;
    }

    /**
     * Gets the RDF node for the specified literal
     *
     * @param literal A literal
     * @return The associated RDF node
     */
    public RDFLiteralNode getLiteralNode(Literal literal) {
        return getLiteralNode(literal.getLexicalValue(), literal.getMemberOf().getHasValue(), literal.getLangTag());
    }

    /**
     * Adds the specified triple to this graph
     *
     * @param triple A triple
     * @throws org.xowl.store.rdf.UnsupportedNodeType when the subject node type is unsupported
     */
    public void add(RDFTriple triple) throws UnsupportedNodeType {
        add(triple.getOntology(), triple.getSubject(), triple.getProperty(), triple.getObject());
    }

    /**
     * Adds the specified triple to this graph
     *
     * @param ontology The ontology containing the triple
     * @param subject  The triple subject node
     * @param property The triple property
     * @param value    The triple value
     * @throws org.xowl.store.rdf.UnsupportedNodeType when the subject node type is unsupported
     */
    public void add(Ontology ontology, RDFSubjectNode subject, RDFProperty property, RDFNode value) throws UnsupportedNodeType {
        switch (subject.getNodeType()) {
            case RDFIRIReference.TYPE:
                addEdgeFromIRI(ontology, (RDFIRIReference) subject, property, value);
                break;
            case RDFBlankNode.TYPE:
                addEdgeFromBlank(ontology, (RDFBlankNode) subject, property, value);
                break;
            default:
                throw new UnsupportedNodeType(subject, "Subject node must be IRI or BLANK");
        }
    }

    /**
     * Adds the specified triple to this graph
     *
     * @param ontology The ontology containing the triple
     * @param subject  The triple subject node
     * @param property The triple property
     * @param value    The triple value
     */
    protected void addEdgeFromIRI(Ontology ontology, RDFIRIReference subject, RDFProperty property, RDFNode value) {
        int key = ((RDFIRIReferenceImpl) subject).getKey();
        EdgeBucket bucket = edgesIRI.get(key);
        if (bucket == null) {
            bucket = new EdgeBucket();
            edgesIRI.put(key, bucket);
        }
        bucket.add(ontology, property, value);
    }

    /**
     * Adds the specified triple to this graph
     *
     * @param ontology The ontology containing the triple
     * @param subject  The triple subject node
     * @param property The triple property
     * @param value    The triple value
     */
    protected void addEdgeFromBlank(Ontology ontology, RDFBlankNode subject, RDFProperty property, RDFNode value) {
        int key = subject.getBlankID();
        while (key > edgesBlank.length)
            edgesBlank = Arrays.copyOf(edgesBlank, edgesBlank.length + INIT_BLANK_SIZE);
        EdgeBucket bucket = edgesBlank[key];
        if (bucket == null) {
            bucket = new EdgeBucket();
            edgesBlank[key] = bucket;
        }
        bucket.add(ontology, property, value);
    }

    /**
     * Removes the specified triple from this graph
     *
     * @param triple A triple
     * @throws org.xowl.store.rdf.UnsupportedNodeType when the subject node type is unsupported
     */
    public void remove(RDFTriple triple) throws UnsupportedNodeType {
        remove(triple.getOntology(), triple.getSubject(), triple.getProperty(), triple.getObject());
    }

    /**
     * Removes the specified triple from this graph
     *
     * @param ontology The ontology containing the triple
     * @param subject  The triple subject node
     * @param property The triple property
     * @param value    The triple value
     * @throws org.xowl.store.rdf.UnsupportedNodeType when the subject node type is unsupported
     */
    public void remove(Ontology ontology, RDFSubjectNode subject, RDFProperty property, RDFNode value) throws UnsupportedNodeType {
        switch (subject.getNodeType()) {
            case RDFIRIReference.TYPE:
                removeEdgeFromIRI(ontology, (RDFIRIReference) subject, property, value);
                break;
            case RDFBlankNode.TYPE:
                removeEdgeFromBlank(ontology, (RDFBlankNode) subject, property, value);
                break;
            default:
                throw new UnsupportedNodeType(subject, "Subject node must be IRI or BLANK");
        }
    }

    /**
     * Removes the specified triple from this graph
     *
     * @param ontology The ontology containing the triple
     * @param subject  The triple subject node
     * @param property The triple property
     * @param value    The triple value
     */
    protected void removeEdgeFromIRI(Ontology ontology, RDFIRIReference subject, RDFProperty property, RDFNode value) {
        int key = ((RDFIRIReferenceImpl) subject).getKey();
        EdgeBucket bucket = edgesIRI.get(key);
        if (bucket == null)
            return;
        bucket.remove(ontology, property, value);
        if (bucket.getSize() == 0)
            edgesIRI.remove(key);
    }

    /**
     * Removes the specified triple from this graph
     *
     * @param ontology The ontology containing the triple
     * @param subject  The triple subject node
     * @param property The triple property
     * @param value    The triple value
     */
    protected void removeEdgeFromBlank(Ontology ontology, RDFBlankNode subject, RDFProperty property, RDFNode value) {
        int key = subject.getBlankID();
        if (key >= edgesBlank.length)
            return;
        EdgeBucket bucket = edgesBlank[key];
        if (bucket == null)
            return;
        bucket.remove(ontology, property, value);
        if (bucket.getSize() == 0)
            edgesBlank[key] = null;
    }

    /**
     * Gets an iterator over all the triples in this store
     *
     * @return An iterator over the results
     */
    public Iterator<RDFTriple> getAll() {
        try {
            return getAll(null, null, null, null);
        } catch (UnsupportedNodeType ex) {
            // cannot happen ...
            return null;
        }
    }

    /**
     * Gets an iterator over all the triples in this store that are in the specified ontology
     *
     * @param ontology A containing to match
     * @return An iterator over the results
     */
    public Iterator<RDFTriple> getAll(Ontology ontology) {
        try {
            return getAll(null, null, null, ontology);
        } catch (UnsupportedNodeType ex) {
            // cannot happen ...
            return null;
        }
    }

    /**
     * Gets an iterator over all the triples in this store that matches the given values
     *
     * @param subject  A subject node to match, or null
     * @param property A property to match, or null
     * @param object   An object node to match, or null
     * @return An iterator over the results
     * @throws UnsupportedNodeType when the subject node type is unsupported
     */
    public Iterator<RDFTriple> getAll(RDFSubjectNode subject, RDFProperty property, RDFNode object) throws UnsupportedNodeType {
        return getAll(subject, property, object, null);
    }

    /**
     * Gets an iterator over all the triples in this store that matches the given values
     *
     * @param subject  A subject node to match, or null
     * @param property A property to match, or null
     * @param object   An object node to match, or null
     * @param ontology A containing to match, or null
     * @return An iterator over the results
     * @throws UnsupportedNodeType when the subject node type is unsupported
     */
    public Iterator<RDFTriple> getAll(RDFSubjectNode subject, final RDFProperty property, final RDFNode object, final Ontology ontology) throws UnsupportedNodeType {
        if (subject == null) {
            return new AdaptingIterator<>(new CombiningIterator<>(getAllSubjects(), new Adapter<Iterator<RDFTriple>>() {
                @Override
                public <X> Iterator<RDFTriple> adapt(X element) {
                    Couple<RDFSubjectNode, EdgeBucket> subject = (Couple<RDFSubjectNode, EdgeBucket>) element;
                    return subject.y.getAllTriples(property, object, ontology);
                }
            }), new Adapter<RDFTriple>() {
                @Override
                public <X> RDFTriple adapt(X element) {
                    Couple<Couple<RDFSubjectNode, EdgeBucket>, RDFTriple> result = (Couple<Couple<RDFSubjectNode, EdgeBucket>, RDFTriple>) element;
                    result.y.setSubject(result.x.x);
                    return result.y;
                }
            });
        } else {
            switch (subject.getNodeType()) {
                case RDFIRIReference.TYPE:
                    return edgesIRI.get(((RDFIRIReferenceImpl) subject).getKey()).getAllTriples(property, object, ontology);
                case RDFBlankNode.TYPE:
                    return edgesBlank[((RDFBlankNode) subject).getBlankID()].getAllTriples(property, object, ontology);
                default:
                    throw new UnsupportedNodeType(subject, "Subject node must be IRI or BLANK");
            }
        }
    }

    /**
     * Gets an iterator over all the subjects starting edges in the graph
     *
     * @return An iterator over all the subjects starting edges in the graph
     */
    protected Iterator<Couple<RDFSubjectNode, EdgeBucket>> getAllSubjects() {
        final Couple<RDFSubjectNode, EdgeBucket> result = new Couple<>();
        return new ConcatenatedIterator<Couple<RDFSubjectNode, EdgeBucket>>(new Iterator[]{
                new AdaptingIterator<>(edgesIRI.keySet().iterator(), new Adapter<Couple<RDFSubjectNode, EdgeBucket>>() {
                    @Override
                    public <X> Couple<RDFSubjectNode, EdgeBucket> adapt(X element) {
                        result.x = mapNodeIRIs.get(element);
                        result.y = edgesIRI.get(element);
                        return result;
                    }
                }),
                new AdaptingIterator<>(new IndexIterator<>(edgesBlank), new Adapter<Couple<RDFSubjectNode, EdgeBucket>>() {
                    @Override
                    public <X> Couple<RDFSubjectNode, EdgeBucket> adapt(X element) {
                        Integer index = (Integer) element;
                        result.x = new RDFBlankNode(index);
                        result.y = edgesBlank[index];
                        return result;
                    }
                })
        });
    }
}
