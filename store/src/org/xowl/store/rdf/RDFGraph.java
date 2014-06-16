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

import org.xowl.lang.owl2.AnonymousIndividual;
import org.xowl.lang.owl2.IRI;
import org.xowl.lang.owl2.Literal;
import org.xowl.lang.owl2.Ontology;
import org.xowl.store.cache.StringStore;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
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
    private static final int initSize = 1024;
    /**
     * The embedded string store
     */
    private StringStore sStore;
    /**
     * The store for literals
     */
    private Map<Key, RDFLiteral> mapLiterals;
    /**
     * The store for edges in this graph
     */
    private Map<Key, Edge> edgesIRI;
    /**
     * The store if anonymous nodes
     */
    private Map<AnonymousIndividual, Edge> edgesAnon;
    /**
     * The store of blank nodes
     */
    private Edge[] edgesBlank;
    /**
     * The next blank index and identifier
     */
    private int nextBlank;


    /**
     * Initializes this graph
     *
     * @throws java.io.IOException when the store cannot allocate a temporary file
     */
    public RDFGraph() throws IOException {
        sStore = new StringStore();
        mapLiterals = new HashMap<>();
        edgesIRI = new HashMap<>();
        edgesAnon = new HashMap<>();
        edgesBlank = new Edge[initSize];
        nextBlank = 0;
    }

    /**
     * Create an edge in the specified ontology
     *
     * @param onto     An ontology
     * @param property The property on the edge
     * @param value    The edge's target node
     * @return The created edge
     */
    private static Edge createEdge(Ontology onto, RDFProperty property, RDFNode value) {
        switch (value.getNodeType()) {
            case IRIReference:
                return new EdgeToIRI(onto, property, value);
            case Anonymous:
                return new EdgeToAnon(onto, property, value);
            case Blank:
                return new EdgeToBlank(onto, property, value);
            case Literal:
                return new EdgeToLiteral(onto, property, value);
        }
        // Cannot handle dynamic nodes
        return null;
    }

    /**
     * Gets the RDF node for the given IRI
     *
     * @param iri An IRI
     * @return The associated RDF node
     */
    public RDFIRIReference getNodeIRI(String iri) {
        Key key = sStore.store(iri);
        IRI oIri = new IRI();
        oIri.setHasValue(iri);
        return new RDFIRIReference(key, oIri);
    }

    /**
     * Gets the RDF node for the given anonymous individual
     *
     * @param anon An anonymous individual
     * @return The associated RDF node
     */
    public RDFAnonymousNode getAnonymousNode(AnonymousIndividual anon) {
        return new RDFAnonymousNode(anon);
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
     * @return The associated RDF node
     */
    public RDFLiteralNode getNodeLiteral(String lex, String datatype) {
        Key lexKey = sStore.store(lex);
        Key typeKey = sStore.store(datatype);
        RDFLiteral nl = mapLiterals.get(lexKey);
        if (nl != null) {
            nl = lookupLiteral(nl, typeKey);
        } else {
            nl = new RDFLiteral(typeKey);
            mapLiterals.put(lexKey, nl);
        }
        IRI iri = new IRI();
        iri.setHasValue(datatype);
        Literal literal = new Literal();
        literal.setLexicalValue(lex);
        literal.setMemberOf(iri);
        return new RDFLiteralNode(nl, literal);
    }

    /**
     * Lookups the specified datatype in the given bucket's root
     *
     * @param root The root of a bucket
     * @param type The data-type key to look for
     * @return The associated RDF literal
     */
    private RDFLiteral lookupLiteral(RDFLiteral root, Key type) {
        RDFLiteral current = root;
        RDFLiteral parent = root;
        while (current != null) {
            if (current.getTypeKey() == type)
                return current;
            parent = current;
            current = current.getNext();
        }
        parent.setNext(new RDFLiteral(type));
        return parent.getNext();
    }

    /**
     * Adds the specified triple to this graph
     *
     * @param ontology The ontology containing the triple
     * @param subject  The triple subject node
     * @param property The triple property
     * @param value    The triple value
     */
    public void add(Ontology ontology, RDFSubjectNode subject, RDFProperty property, RDFNode value) {
        switch (subject.getNodeType()) {
            case IRIReference:
                add_IRI(ontology, subject, property, value);
                break;
            case Anonymous:
                add_Anon(ontology, subject, property, value);
                break;
            case Blank:
                add_Blank(ontology, subject, property, value);
                break;
        }
    }

    private void add_IRI(Ontology ontology, RDFSubjectNode subject, RDFProperty property, RDFNode value) {
        Key key = subject.getStoreKey();
        Edge edge = edgesIRI.get(key);
        if (edge != null)
            edge.increment(ontology, property, value);
        else
            edgesIRI.put(key, createEdge(ontology, property, value));
    }

    private void add_Anon(Ontology ontology, RDFSubjectNode subject, RDFProperty property, RDFNode value) {
        org.xowl.lang.owl2.AnonymousIndividual anon = subject.getAnonymous();
        Edge edge = edgesAnon.get(anon);
        if (edge != null)
            edge.increment(ontology, property, value);
        else
            edgesAnon.put(anon, createEdge(ontology, property, value));
    }

    private void add_Blank(Ontology ontology, RDFSubjectNode subject, RDFProperty property, RDFNode value) {
        int index = subject.getBlankID();
        while (index > edgesBlank.length)
            edgesBlank = Arrays.copyOf(edgesBlank, edgesBlank.length + initSize);
        Edge edge = edgesBlank[index];
        if (edge != null)
            edge.increment(ontology, property, value);
        else
            edgesBlank[index] = createEdge(ontology, property, value);
    }

    /**
     * Removes the specified triple from this graph
     *
     * @param ontology The ontology containing the triple
     * @param subject  The triple subject node
     * @param property The triple property
     * @param value    The triple value
     */
    public void remove(Ontology ontology, RDFSubjectNode subject, RDFProperty property, RDFNode value) {
        switch (subject.getNodeType()) {
            case IRIReference:
                remove_IRI(ontology, subject, property, value);
                break;
            case Anonymous:
                remove_Anon(ontology, subject, property, value);
                break;
            case Blank:
                remove_Blank(ontology, subject, property, value);
                break;
        }
    }

    private void remove_IRI(Ontology ontology, RDFSubjectNode subject, RDFProperty property, RDFNode value) {
        Key key = subject.getStoreKey();
        Edge edge = edgesIRI.get(key);
        if (edge == null)
            return;
        Edge temp = edge.decrement(ontology, property, value);
        if (temp == null)
            edgesIRI.remove(key);
        else if (temp != edge)
            edgesIRI.put(key, temp);
    }

    private void remove_Anon(Ontology ontology, RDFSubjectNode subject, RDFProperty property, RDFNode value) {
        org.xowl.lang.owl2.AnonymousIndividual anon = subject.getAnonymous();
        Edge edge = edgesAnon.get(anon);
        if (edge == null)
            return;
        Edge temp = edge.decrement(ontology, property, value);
        if (temp == null)
            edgesAnon.remove(anon);
        else if (temp != edge)
            edgesAnon.put(anon, temp);
    }

    private void remove_Blank(Ontology ontology, RDFSubjectNode subject, RDFProperty property, RDFNode value) {
        int index = subject.getBlankID();
        if (index >= edgesBlank.length)
            return;
        Edge edge = edgesBlank[index];
        if (edge == null)
            return;
        edgesBlank[index] = edge.decrement(ontology, property, value);
    }

    /**
     * Associates an ontology to the number of occurrence of an edge in this ontology
     */
    private static class Multiplicity {
        /**
         * The ontology for this counter
         */
        private Ontology ontology;
        /**
         * The multiplicity of the associated edge in the ontology
         */
        private int count;
        /**
         * The next multiplicity data
         */
        private Multiplicity next;

        /**
         * Initializes this multiplicity data
         *
         * @param onto The represented ontology
         */
        public Multiplicity(Ontology onto) {
            this.ontology = onto;
            this.count = 1;
            this.next = null;
        }

        /**
         * Increments the counter for the specified ontology
         *
         * @param onto An ontology
         */
        public void increment(Ontology onto) {
            Multiplicity current = this;
            Multiplicity parent = this;
            while (current != null) {
                if (current.ontology == onto) {
                    count++;
                    return;
                }
                parent = current;
                current = current.next;
            }
            parent.next = new Multiplicity(onto);
        }

        /**
         * Decrements the counter for the specified ontology
         *
         * @param onto An ontology
         * @return The new multiplicity data node if if needs to be changed
         */
        public Multiplicity decrement(Ontology onto) {
            Multiplicity current = this;
            Multiplicity parent = this;
            while (current != null) {
                if (current.ontology == onto) {
                    count--;
                    if (count == 0) {
                        if (current == this)
                            return this.next;
                        parent.next = current.next;
                    }
                    return this;
                }
                parent = current;
                current = current.next;
            }
            return this;
        }
    }

    /**
     * Represents an edge in a RDF graph
     */
    private abstract static class Edge {
        /**
         * Key for the property's value on this edge
         */
        private Key property;
        /**
         * The multiplicities on this edge
         */
        private Multiplicity counters;
        /**
         * The next edge in this bucket for a starting node
         */
        private Edge next;

        /**
         * Initializes this edge
         *
         * @param ontology The ontology containing the triple
         * @param property The property on this edge
         */
        public Edge(Ontology ontology, RDFProperty property) {
            this.property = property.getStoreKey();
            this.counters = new Multiplicity(ontology);
            this.next = null;
        }

        /**
         * Determines whether this edge matches the specified value node
         *
         * @param value A value node
         * @return true if this edge matches
         */
        protected abstract boolean equalsValue(RDFNode value);

        /**
         * Inserts the specified edge (or increment the counter)
         *
         * @param onto     The ontology containing the triple
         * @param property The property on the edge
         * @param value    The edge's target node
         */
        public void increment(Ontology onto, RDFProperty property, RDFNode value) {
            Edge current = this;
            Edge parent = this;
            while (current != null) {
                if (current.property == property.getStoreKey() && equalsValue(value)) {
                    current.counters.increment(onto);
                    return;
                }
                parent = current;
                current = current.next;
            }
            parent.next = createEdge(onto, property, value);
        }

        /**
         * Removes the specified edge (or decrement the counter)
         *
         * @param onto     The ontology containing the triple
         * @param property The property on the edge
         * @param value    The edge's target node
         * @return The new bucket root if it changed
         */
        public Edge decrement(Ontology onto, RDFProperty property, RDFNode value) {
            Edge current = this;
            Edge parent = this;
            while (current != null) {
                if (current.property == property.getStoreKey() && equalsValue(value)) {
                    current.counters = current.counters.decrement(onto);
                    if (current.counters == null) {
                        if (current == this)
                            return this.next;
                        parent.next = current.next;
                    }
                    return this;
                }
                parent = current;
                current = current.next;
            }
            return this;
        }
    }

    /**
     * Represent an edge to an IRI node
     */
    private static class EdgeToIRI extends Edge {
        /**
         * Key to the IRI value
         */
        private Key value;

        /**
         * Initializes this edge
         *
         * @param ontology The ontology containing the triple
         * @param property The property on this edge
         * @param value    The target node
         */
        public EdgeToIRI(Ontology ontology, RDFProperty property, RDFNode value) {
            super(ontology, property);
            this.value = value.getStoreKey();
        }

        @Override
        protected boolean equalsValue(RDFNode value) {
            return this.value == value.getStoreKey();
        }
    }

    /**
     * Represents an edge to an anonymous node
     */
    private static class EdgeToAnon extends Edge {
        /**
         * The target anonymous individual
         */
        private AnonymousIndividual value;

        /**
         * Initializes this edge
         *
         * @param ontology The ontology containing the triple
         * @param property The property on this edge
         * @param value    The target node
         */
        public EdgeToAnon(Ontology ontology, RDFProperty property, RDFNode value) {
            super(ontology, property);
            this.value = value.getAnonymous();
        }

        @Override
        protected boolean equalsValue(RDFNode value) {
            return this.value == value.getAnonymous();
        }
    }

    /**
     * Represents an edge to a blank node
     */
    private static class EdgeToBlank extends Edge {
        /**
         * The target blank identifier
         */
        private int value;

        /**
         * Initializes this edge
         *
         * @param ontology The ontology containing the triple
         * @param property The property on this edge
         * @param value    The target node
         */
        public EdgeToBlank(Ontology ontology, RDFProperty property, RDFNode value) {
            super(ontology, property);
            this.value = value.getBlankID();
        }

        @Override
        protected boolean equalsValue(RDFNode value) {
            return this.value == value.getBlankID();
        }
    }

    /**
     * Represents an edge to a literal node
     */
    private static class EdgeToLiteral extends Edge {
        /**
         * The target literal's value
         */
        private RDFLiteral value;

        /**
         * Initializes this edge
         *
         * @param ontology The ontology containing the triple
         * @param property The property on this edge
         * @param value    The target node
         */
        public EdgeToLiteral(Ontology ontology, RDFProperty property, RDFNode value) {
            super(ontology, property);
            this.value = value.getLiteralValue();
        }

        @Override
        protected boolean equalsValue(RDFNode value) {
            return this.value == value.getLiteralValue();
        }
    }
}
