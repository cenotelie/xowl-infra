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

import org.xowl.lang.actions.QueryVariable;
import org.xowl.lang.owl2.*;
import org.xowl.store.cache.StringStore;

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
    private static final int INIT_BLANK_SIZE = 1024;
    /**
     * The initial number of the edge buffer for a given node
     */
    private static final int INIT_EDGE_COUNT = 10;

    /**
     * The embedded string store
     */
    private StringStore sStore;
    /**
     * The store for literals
     */
    private Map<Key, Lit> mapLiterals;
    /**
     * The store of variable nodes
     */
    private Map<QueryVariable, XOWLVariableNode> mapVariables;
    /**
     * The store or dynamic nodes
     */
    private Map<Expression, XOWLDynamicNode> mapDynamics;
    /**
     * The store for edges starting from IRI nodes
     */
    private Map<Key, Edge[]> edgesIRI;
    /**
     * The store for edges starting from anonymous nodes
     */
    private Map<AnonymousIndividual, Edge[]> edgesAnon;
    /**
     * The store for edges starting from blank nodes
     */
    private Edge[][] edgesBlank;
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
        mapVariables = new HashMap<>();
        mapDynamics = new HashMap<>();
        edgesIRI = new HashMap<>();
        edgesAnon = new HashMap<>();
        edgesBlank = new Edge[INIT_BLANK_SIZE][];
        nextBlank = 0;
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
    public RDFLiteralNode getLiteralNode(String lex, String datatype) {
        Key lexKey = sStore.store(lex);
        Key typeKey = sStore.store(datatype);

        Lit innerLiteral = mapLiterals.get(lexKey);
        if (innerLiteral != null) {
            innerLiteral = lookupLiteral(innerLiteral, typeKey);
        } else {
            innerLiteral = new Lit(typeKey);
            mapLiterals.put(lexKey, innerLiteral);
        }

        if (innerLiteral.node == null) {
            IRI iri = new IRI();
            iri.setHasValue(datatype);
            Literal literal = new Literal();
            literal.setLexicalValue(lex);
            literal.setMemberOf(iri);
            innerLiteral.node = new RDFLiteralNode(literal);
        }

        return innerLiteral.node;
    }

    /**
     * Gets the RDF node for the specified literal
     *
     * @param literal A literal
     * @return The associated RDF node
     */
    public RDFLiteralNode getLiteralNode(Literal literal) {
        Key lexKey = sStore.store(literal.getLexicalValue());
        Key typeKey = sStore.store(literal.getMemberOf().getHasValue());

        Lit innerLiteral = mapLiterals.get(lexKey);
        if (innerLiteral != null) {
            innerLiteral = lookupLiteral(innerLiteral, typeKey);
        } else {
            innerLiteral = new Lit(typeKey);
            mapLiterals.put(lexKey, innerLiteral);
        }

        if (innerLiteral.node == null) {
            innerLiteral.node = new RDFLiteralNode(literal);
        }

        return innerLiteral.node;
    }

    /**
     * Lookups the specified datatype in the given bucket's root
     *
     * @param root The root of a bucket
     * @param type The data-type key to look for
     * @return The associated RDF literal
     */
    private Lit lookupLiteral(Lit root, Key type) {
        Lit current = root;
        Lit parent = root;
        while (current != null) {
            if (current.typeKey == type)
                return current;
            parent = current;
            current = current.next;
        }
        parent.next = new Lit(type);
        return parent.next;
    }

    /**
     * Gets the RDF node for the specified variable
     *
     * @param variable A variable
     * @return The associated RDF node
     */
    public XOWLVariableNode getVariableNode(QueryVariable variable) {
        if (mapVariables.containsKey(variable))
            return mapVariables.get(variable);
        XOWLVariableNode node = new XOWLVariableNode(variable);
        mapVariables.put(variable, node);
        return node;
    }

    /**
     * Gets the RDF node for the specified expression
     *
     * @param expression An expression
     * @return The associated RDF node
     */
    public XOWLDynamicNode getDynamicNode(Expression expression) {
        if (mapDynamics.containsKey(expression))
            return mapDynamics.get(expression);
        XOWLDynamicNode node = new XOWLDynamicNode(expression);
        mapDynamics.put(expression, node);
        return node;
    }

    /**
     * Adds the specified triple to this graph
     *
     * @param triple A triple
     * @throws org.xowl.store.rdf.UnsupportedNodeType when the subject node type is unsupported
     */
    public void add(XOWLTriple triple) throws UnsupportedNodeType {
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
            case IRI_REFERENCE:
                addEdgeFromIRI(ontology, subject, property, value);
                break;
            case ANONYMOUS:
                addEdgeFromAnonymous(ontology, subject, property, value);
                break;
            case BLANK:
                addEdgeFromBlank(ontology, subject, property, value);
                break;
            default:
                throw new UnsupportedNodeType(subject, "Subject node must not be of type VARIABLE or DYNAMIC");
        }
    }

    private void addEdgeFromIRI(Ontology ontology, RDFSubjectNode subject, RDFProperty property, RDFNode value) {
        Key key = subject.getStoreKey();
        Edge[] buffer = edgesIRI.get(key);
        if (buffer != null) {
            Edge[] result = addEdge(buffer, ontology, property, value);
            if (result != buffer)
                edgesIRI.put(key, result);
        } else {
            buffer = new Edge[INIT_EDGE_COUNT];
            buffer = addEdge(buffer, ontology, property, value);
            edgesIRI.put(key, buffer);
        }
    }

    private void addEdgeFromAnonymous(Ontology ontology, RDFSubjectNode subject, RDFProperty property, RDFNode value) {
        AnonymousIndividual anon = subject.getAnonymous();
        Edge[] buffer = edgesAnon.get(anon);
        if (buffer != null) {
            Edge[] result = addEdge(buffer, ontology, property, value);
            if (result != buffer)
                edgesAnon.put(anon, result);
        } else {
            buffer = new Edge[INIT_EDGE_COUNT];
            buffer = addEdge(buffer, ontology, property, value);
            edgesAnon.put(anon, buffer);
        }
    }

    private void addEdgeFromBlank(Ontology ontology, RDFSubjectNode subject, RDFProperty property, RDFNode value) {
        int index = subject.getBlankID();
        while (index > edgesBlank.length)
            edgesBlank = Arrays.copyOf(edgesBlank, edgesBlank.length + INIT_BLANK_SIZE);

        Edge[] buffer = edgesBlank[index];
        if (buffer != null) {
            Edge[] result = addEdge(buffer, ontology, property, value);
            if (result != buffer)
                edgesBlank[index] = result;
        } else {
            buffer = new Edge[INIT_EDGE_COUNT];
            buffer = addEdge(buffer, ontology, property, value);
            edgesBlank[index] = buffer;
        }
    }

    /**
     * Adds the specified edge in the provided buffer
     *
     * @param buffer   A buffer of edges
     * @param ontology The containing ontology
     * @param property The property on this edge
     * @param value    The target value
     * @return The edge buffer, or a new one if the provided one was too small
     */
    private Edge[] addEdge(Edge[] buffer, Ontology ontology, RDFProperty property, RDFNode value) {
        boolean hasEmpty = false;
        for (int i = 0; i != buffer.length; i++) {
            if (buffer[i] == null) {
                hasEmpty = true;
                continue;
            }
            if (buffer[i].property == property) {
                buffer[i].increment(ontology, value);
                return buffer;
            }
        }
        if (hasEmpty) {
            for (int i = 0; i != buffer.length; i++) {
                if (buffer[i] == null) {
                    buffer[i] = new Edge(ontology, property, value);
                    return buffer;
                }
            }
        }
        int size = buffer.length;
        buffer = Arrays.copyOf(buffer, buffer.length + INIT_EDGE_COUNT);
        buffer[size] = new Edge(ontology, property, value);
        return buffer;
    }

    /**
     * Removes the specified triple from this graph
     *
     * @param triple A triple
     * @throws org.xowl.store.rdf.UnsupportedNodeType when the subject node type is unsupported
     */
    public void remove(XOWLTriple triple) throws UnsupportedNodeType {
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
            case IRI_REFERENCE:
                removeEdgeFromIRI(ontology, subject, property, value);
                break;
            case ANONYMOUS:
                removeEdgeFromAnon(ontology, subject, property, value);
                break;
            case BLANK:
                removeEdgeFromBlank(ontology, subject, property, value);
                break;
            default:
                throw new UnsupportedNodeType(subject, "Subject node must not be of type VARIABLE or DYNAMIC");
        }
    }

    private void removeEdgeFromIRI(Ontology ontology, RDFSubjectNode subject, RDFProperty property, RDFNode value) {
        Key key = subject.getStoreKey();
        Edge[] buffer = edgesIRI.get(key);
        if (buffer == null)
            return;
        boolean remove = removeEdge(buffer, ontology, property, value);
        if (remove)
            edgesIRI.remove(key);
    }

    private void removeEdgeFromAnon(Ontology ontology, RDFSubjectNode subject, RDFProperty property, RDFNode value) {
        AnonymousIndividual anon = subject.getAnonymous();
        Edge[] buffer = edgesAnon.get(anon);
        if (buffer == null)
            return;
        boolean remove = removeEdge(buffer, ontology, property, value);
        if (remove)
            edgesAnon.remove(anon);
    }

    private void removeEdgeFromBlank(Ontology ontology, RDFSubjectNode subject, RDFProperty property, RDFNode value) {
        int index = subject.getBlankID();
        if (index >= edgesBlank.length)
            return;
        Edge[] buffer = edgesBlank[index];
        if (buffer == null)
            return;
        boolean remove = removeEdge(buffer, ontology, property, value);
        if (remove)
            edgesBlank[index] = null;
    }

    /**
     * Removes the specified edge from the provided buffer
     *
     * @param buffer   A buffer of edges
     * @param ontology The containing ontology
     * @param property The property on this edge
     * @param value    The target value
     * @return true if the buffer is empty and should be removed
     */
    private boolean removeEdge(Edge[] buffer, Ontology ontology, RDFProperty property, RDFNode value) {
        int total = 0;
        for (int i = 0; i != buffer.length; i++) {
            if (buffer[i] == null) {
                continue;
            }
            if (buffer[i].property == property) {
                boolean remove = buffer[i].decrement(ontology, value);
                if (remove) {
                    buffer[i] = null;
                } else {
                    total += 1;
                }
            } else {
                total += 1;
            }
        }
        return (total == 0);
    }

    /**
     * Gets an iterator over all the triples in this store that matches the given values
     *
     * @param subject  A subject node to match, or null
     * @param property A property to match, or null
     * @param object   An object node to match, or null
     * @return An iterator over the results
     * @throws UnsupportedNodeType
     */
    public Iterator<XOWLTriple> getAll(RDFSubjectNode subject, RDFProperty property, RDFNode object) throws UnsupportedNodeType {
        if (subject == null) {
            return new GraphIterator(this, property, object);
        } else {
            switch (subject.getNodeType()) {
                case IRI_REFERENCE:
                    return new EdgeIterator(edgesIRI.get(subject.getStoreKey()), subject, property, object);
                case ANONYMOUS:
                    return new EdgeIterator(edgesAnon.get(subject.getAnonymous()), subject, property, object);
                case BLANK:
                    return new EdgeIterator(edgesBlank[subject.getBlankID()], subject, property, object);
                default:
                    throw new UnsupportedNodeType(subject, "Subject node must not be of type VARIABLE or DYNAMIC");
            }
        }
    }

    /**
     * Represents a literal in a RDF graph
     */
    private static class Lit {
        /**
         * The key to the datatype IRI
         */
        public Key typeKey;
        /**
         * The RDF node for this datatype
         */
        public RDFLiteralNode node;
        /**
         * The next data
         */
        public Lit next;

        /**
         * Initializes this data
         *
         * @param typeKey The key to the datatype IRI
         */
        public Lit(Key typeKey) {
            this.typeKey = typeKey;
        }
    }

    /**
     * Represents a collection of targets for edges
     */
    private static class Target {
        /**
         * The initial size of the buffer of the multiplicities
         */
        private static final int INIT_BUFFER_SIZE = 3;
        /**
         * The represented target node
         */
        private RDFNode target;
        /**
         * The containing ontologies
         */
        private Ontology[] ontologies;
        /**
         * The multiplicity counters for the ontologies
         */
        private int[] multiplicities;

        /**
         * Initializes this target
         *
         * @param ontology The first containing ontology
         * @param target   The represented target
         */
        public Target(Ontology ontology, RDFNode target) {
            this.target = target;
            this.ontologies = new Ontology[INIT_BUFFER_SIZE];
            this.multiplicities = new int[INIT_BUFFER_SIZE];
            this.ontologies[0] = ontology;
            this.multiplicities[0] = 1;
        }

        /**
         * Increment the multiplicity for the specified ontology
         *
         * @param ontology An ontology
         */
        public void increment(Ontology ontology) {
            for (int i = 0; i != ontologies.length; i++) {
                if (ontologies[i] == ontology) {
                    multiplicities[i]++;
                    return;
                }
            }
            for (int i = 0; i != ontologies.length; i++) {
                if (ontologies[i] == null) {
                    ontologies[i] = ontology;
                    multiplicities[i] = 1;
                    return;
                }
            }
            int size = ontologies.length;
            ontologies = Arrays.copyOf(ontologies, ontologies.length + INIT_BUFFER_SIZE);
            multiplicities = Arrays.copyOf(multiplicities, multiplicities.length + INIT_BUFFER_SIZE);
            ontologies[size] = ontology;
            multiplicities[size] = 1;
        }

        /**
         * Decrement the multiplicity for the specified ontology
         *
         * @param ontology An ontology
         * @return true if this target is now empty and should be removed
         */
        public boolean decrement(Ontology ontology) {
            int total = 0;
            for (int i = 0; i != ontologies.length; i++) {
                if (ontologies[i] == ontology) {
                    multiplicities[i]--;
                    if (multiplicities[i] == 0) {
                        ontologies[i] = null;
                    } else {
                        total += multiplicities[i];
                    }
                } else if (ontologies[i] != null) {
                    total += multiplicities[i];
                }
            }
            return (total == 0);
        }
    }

    /**
     * Represents an edge in a RDF graph
     */
    private static class Edge {
        /**
         * The initial size of the buffer of the targets
         */
        private static final int INIT_BUFFER_SIZE = 10;

        /**
         * The label on this edge
         */
        private RDFProperty property;
        /**
         * The target for this edges
         */
        private Target[] targets;

        /**
         * Initializes this edge
         *
         * @param ontology The ontology containing the triple
         * @param property The property on this edge
         * @param object   The first object node for this edge
         */
        public Edge(Ontology ontology, RDFProperty property, RDFNode object) {
            this.property = property;
            this.targets = new Target[INIT_BUFFER_SIZE];
            this.targets[0] = new Target(ontology, object);
        }

        /**
         * Inserts the specified edge (or increment the counter)
         *
         * @param ontology The ontology containing the triple
         * @param value    The edge's target node
         */
        public void increment(Ontology ontology, RDFNode value) {
            for (int i = 0; i != targets.length; i++) {
                if (targets[i].target == value) {
                    targets[i].increment(ontology);
                    return;
                }
            }
            for (int i = 0; i != targets.length; i++) {
                if (targets[i] == null) {
                    targets[i] = new Target(ontology, value);
                    return;
                }
            }
            int size = targets.length;
            targets = Arrays.copyOf(targets, targets.length + INIT_BUFFER_SIZE);
            targets[size] = new Target(ontology, value);
        }

        /**
         * Removes the specified edge (or decrement the counter)
         *
         * @param ontology The ontology containing the triple
         * @param value    The edge's target node
         * @return true if this edge is now empty and shall be removed
         */
        public boolean decrement(Ontology ontology, RDFNode value) {
            int total = 0;
            for (int i = 0; i != targets.length; i++) {
                if (targets[i] != null) {
                    if (targets[i].target == value) {
                        boolean remove = targets[i].decrement(ontology);
                        if (remove) {
                            targets[i] = null;
                        } else {
                            total++;
                        }
                    } else {
                        total++;
                    }
                }
            }
            return (total == 0);
        }
    }


    /**
     * Represents an iterator of xOWL triples within a target
     */
    private static class OntologyIterator implements Iterator<XOWLTriple> {
        /**
         * The current edge starting node
         */
        private RDFSubjectNode subject;
        /**
         * The current edge label property
         */
        private RDFProperty property;
        /**
         * The target to iterate over
         */
        private Target target;
        /**
         * The current index
         */
        private int index;

        /**
         * Initializes this iterator
         *
         * @param subject  The edge starting node
         * @param property The edge label property
         * @param target   The target to iterate over
         */
        public OntologyIterator(RDFSubjectNode subject, RDFProperty property, Target target) {
            this.subject = subject;
            this.property = property;
            this.target = target;
            this.index = -1;
            for (int i = 0; i != target.ontologies.length; i++) {
                if (target.ontologies[i] != null) {
                    this.index = i;
                    break;
                }
            }
        }

        @Override
        public boolean hasNext() {
            return (index != -1);
        }

        @Override
        public XOWLTriple next() {
            XOWLTriple triple = new XOWLTriple(target.ontologies[index], subject, property, target.target);
            int start = this.index;
            this.index = -1;
            for (int i = start + 1; i != target.ontologies.length; i++) {
                if (target.ontologies[i] != null) {
                    this.index = i;
                    break;
                }
            }
            return triple;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    /**
     * Represents an iterator of xOWL triples within an edge
     */
    private static class TargetIterator implements Iterator<XOWLTriple> {
        /**
         * The current edge starting node
         */
        private RDFSubjectNode subject;
        /**
         * The current edge
         */
        private Edge edge;
        /**
         * Index of the current target in the edge
         */
        private int index;
        /**
         * Current inner iterator
         */
        private Iterator<XOWLTriple> inner;

        /**
         * Initializes this iterator
         *
         * @param subject The edge starting node
         * @param edge    The edge to iterate over
         * @param value   The edge target value that must be matched, or null if all values are queried
         */
        public TargetIterator(RDFSubjectNode subject, Edge edge, RDFNode value) {
            this.subject = subject;
            this.edge = edge;
            if (value == null) {
                for (int i = 0; i != edge.targets.length; i++) {
                    if (edge.targets[i] != null) {
                        index = i;
                        inner = new OntologyIterator(subject, edge.property, edge.targets[i]);
                        if (inner.hasNext())
                            break;
                    }
                }
            } else {
                this.index = -1;
                for (int i = 0; i != edge.targets.length; i++) {
                    if (edge.targets[i] != null && edge.targets[i].target == value) {
                        inner = new OntologyIterator(subject, edge.property, edge.targets[i]);
                        break;
                    }
                }
            }
        }

        @Override
        public boolean hasNext() {
            return (inner != null && inner.hasNext());
        }

        @Override
        public XOWLTriple next() {
            if (inner == null)
                return null;
            XOWLTriple result = inner.next();
            if (inner.hasNext())
                return result;
            inner = null;
            if (index != -1) {
                int start = index;
                for (int i = start + 1; i != edge.targets.length; i++) {
                    if (edge.targets[i] != null) {
                        index = i;
                        inner = new OntologyIterator(subject, edge.property, edge.targets[i]);
                        if (inner.hasNext())
                            break;
                    }
                }
            }
            return result;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    /**
     * Represents an iterator of xOWL triples within an edge buffer
     */
    private static class EdgeIterator implements Iterator<XOWLTriple> {
        /**
         * The current edge starting node
         */
        private RDFSubjectNode subject;
        /**
         * The edge target value that must be matched, or null if all values are queried
         */
        private RDFNode value;
        /**
         * The buffer to iterator over
         */
        private Edge[] buffer;
        /**
         * The next edge to be provided
         */
        private int index;
        /**
         * Current inner iterator
         */
        private Iterator<XOWLTriple> inner;

        /**
         * Initializes this iterator
         *
         * @param buffer   The buffer to iterate over
         * @param subject  The current edge starting node
         * @param property The edge label that must be matched, or null if all values are queried
         * @param value    The edge target value that must be matched, or null if all values are queried
         */
        public EdgeIterator(Edge[] buffer, RDFSubjectNode subject, RDFProperty property, RDFNode value) {
            this.subject = subject;
            this.value = value;
            this.buffer = buffer;
            if (property == null) {
                for (int i = 0; i != buffer.length; i++) {
                    if (buffer[i] != null) {
                        index = i;
                        inner = new TargetIterator(subject, buffer[i], value);
                        if (inner.hasNext())
                            break;
                    }
                }
            } else {
                index = -1;
                for (int i = 0; i != buffer.length; i++) {
                    if (buffer[i] != null && buffer[i].property == property) {
                        inner = new TargetIterator(subject, buffer[i], value);
                        break;
                    }
                }
            }
        }

        @Override
        public boolean hasNext() {
            return (inner != null && inner.hasNext());
        }

        @Override
        public XOWLTriple next() {
            if (inner == null)
                return null;
            XOWLTriple result = inner.next();
            if (inner.hasNext())
                return result;
            inner = null;
            if (index != -1) {
                int start = index;
                for (int i = start + 1; i != buffer.length; i++) {
                    if (buffer[i] != null) {
                        index = i;
                        inner = new TargetIterator(subject, buffer[i], value);
                        if (inner.hasNext())
                            break;
                    }
                }
            }
            return result;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    /**
     * Represents an iterator of xOWL triples within a graph
     */
    private static class GraphIterator implements Iterator<XOWLTriple> {
        /**
         * The parent graph
         */
        private RDFGraph graph;
        /**
         * The edge label that must be matched, or null if all values are queried
         */
        private RDFProperty property;
        /**
         * The edge target value that must be matched, or null if all values are queried
         */
        private RDFNode value;
        /**
         * Iterator for edges starting from an IRI reference node
         */
        private Iterator<Map.Entry<Key, Edge[]>> iteratorForIRI;
        /**
         * Iterator for edges starting from an anonymous node
         */
        private Iterator<Map.Entry<AnonymousIndividual, Edge[]>> iteratorForAnon;
        /**
         * Iterator for edges starting from a blank node
         */
        private int iteratorBlank;
        /**
         * Inner iterator
         */
        private Iterator<XOWLTriple> inner;

        /**
         * Initializes this iterator
         *
         * @param graph    The parent graph
         * @param property The edge label that must be matched, or null if all values are queried
         * @param value    The edge target value that must be matched, or null if all values are queried
         */
        public GraphIterator(RDFGraph graph, RDFProperty property, RDFNode value) {
            this.graph = graph;
            this.property = property;
            this.value = value;
            this.iteratorForIRI = graph.edgesIRI.entrySet().iterator();
            this.iteratorForAnon = graph.edgesAnon.entrySet().iterator();
            this.iteratorBlank = 0;
            gotoNext();
        }

        @Override
        public boolean hasNext() {
            return (inner != null && inner.hasNext());
        }

        @Override
        public XOWLTriple next() {
            if (inner == null)
                return null;
            XOWLTriple result = inner.next();
            if (inner.hasNext())
                return result;
            inner = null;
            gotoNext();
            return result;
        }

        private void gotoNext() {
            while (iteratorForIRI != null && iteratorForIRI.hasNext()) {
                Map.Entry<Key, Edge[]> elem = iteratorForIRI.next();
                IRI oIri = new IRI();
                oIri.setHasValue(graph.sStore.retrieve((StringStore.Key) elem.getKey()));
                RDFSubjectNode subject = new RDFIRIReference(elem.getKey(), oIri);
                inner = new EdgeIterator(elem.getValue(), subject, property, value);
                if (inner.hasNext())
                    return;
            }
            iteratorForIRI = null;
            while (iteratorForAnon != null && iteratorForAnon.hasNext()) {
                Map.Entry<AnonymousIndividual, Edge[]> elem = iteratorForAnon.next();
                RDFSubjectNode subject = new RDFAnonymousNode(elem.getKey());
                inner = new EdgeIterator(elem.getValue(), subject, property, value);
                if (inner.hasNext())
                    return;
            }
            iteratorForAnon = null;
            while (iteratorBlank < graph.edgesBlank.length) {
                if (graph.edgesBlank[iteratorBlank] != null) {
                    RDFSubjectNode subject = new RDFBlankNode(iteratorBlank);
                    inner = new EdgeIterator(graph.edgesBlank[iteratorBlank], subject, property, value);
                    iteratorBlank++;
                    if (inner.hasNext())
                        return;
                } else {
                    iteratorBlank++;
                }
            }
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
