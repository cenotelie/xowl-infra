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

package org.xowl.store;

import org.xowl.infra.lang.actions.DynamicExpression;
import org.xowl.infra.lang.owl2.AnonymousIndividual;
import org.xowl.infra.lang.owl2.IRI;
import org.xowl.infra.lang.owl2.Literal;
import org.xowl.infra.lang.owl2.Ontology;
import org.xowl.infra.lang.runtime.Entity;
import org.xowl.store.owl.AnonymousNode;
import org.xowl.store.owl.DynamicNode;
import org.xowl.store.rdf.*;
import org.xowl.store.storage.NodeManager;
import org.xowl.infra.utils.collections.Couple;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility APIs for RDF
 *
 * @author Laurent Wouters
 */
public class RDFUtils {
    /**
     * Determines whether two RDF nodes are equivalent
     *
     * @param node1 A first node
     * @param node2 A second node
     * @return true of the two nodes are equivalent
     */
    public static boolean same(Node node1, Node node2) {
        return (node1 == node2 || (node1 != null && node2 != null
                && node1.getNodeType() == node2.getNodeType()
                && node1.equals(node2)));
    }

    /**
     * Gets the native value for the specified RDF node
     *
     * @param node A RDF node
     * @return The native value
     */
    public static Object getNative(Node node) {
        switch (node.getNodeType()) {
            case Node.TYPE_IRI: {
                IRI iri = new IRI();
                iri.setHasValue(((IRINode) node).getIRIValue());
                return iri;
            }
            case Node.TYPE_BLANK: {
                return node;
            }
            case Node.TYPE_LITERAL: {
                return Datatypes.toNative((LiteralNode) node);
            }
            case Node.TYPE_ANONYMOUS: {
                return ((AnonymousNode) node).getIndividual();
            }
        }
        throw new IllegalArgumentException("Illegal unevaluated node");
    }

    /**
     * Gets the OWL element represented by the specified RDF node
     *
     * @param node A RDF node
     * @return The represented OWL element
     */
    public static Object getOWL(Node node) {
        switch (node.getNodeType()) {
            case Node.TYPE_IRI: {
                IRI iri = new IRI();
                iri.setHasValue(((IRINode) node).getIRIValue());
                return iri;
            }
            case Node.TYPE_LITERAL: {
                LiteralNode literalNode = (LiteralNode) node;
                Literal result = new Literal();
                String value = literalNode.getLexicalValue();
                if (value != null)
                    result.setLexicalValue(value);
                value = literalNode.getDatatype();
                if (value != null) {
                    IRI iri = new IRI();
                    iri.setHasValue(value);
                    result.setMemberOf(iri);
                }
                value = literalNode.getLangTag();
                if (value != null)
                    result.setLangTag(value);
                return result;
            }
            case Node.TYPE_ANONYMOUS: {
                return ((AnonymousNode) node).getIndividual();
            }
            case Node.TYPE_DYNAMIC: {
                return ((DynamicNode) node).getDynamicExpression();
            }
        }
        throw new IllegalArgumentException("RDF node " + node.getClass().getName() + " cannot be mapped to an OWL element");
    }

    /**
     * Gets the RDF node representing the specified OWL element
     *
     * @param store   The node store to look into
     * @param element An OWL element
     * @return The representing RDF node
     */
    public static Node getRDF(NodeManager store, Object element) {
        if (element instanceof IRI) {
            return store.getIRINode(((IRI) element).getHasValue());
        } else if (element instanceof Entity) {
            return store.getIRINode(((Entity) element).getHasIRI().getHasValue());
        } else if (element instanceof Ontology) {
            return store.getIRINode(((Ontology) element).getHasIRI().getHasValue());
        } else if (element instanceof AnonymousIndividual) {
            return store.getAnonNode((AnonymousIndividual) element);
        } else if (element instanceof Literal) {
            Literal literal = (Literal) element;
            return store.getLiteralNode(literal.getLexicalValue(), literal.getMemberOf().getHasValue(), literal.getLangTag());
        } else if (element instanceof org.xowl.infra.lang.runtime.Literal) {
            org.xowl.infra.lang.runtime.Literal literal = (org.xowl.infra.lang.runtime.Literal) element;
            return store.getLiteralNode(literal.getLexicalValue(), literal.getMemberOf().getInterpretationOf().getHasIRI().getHasValue(), literal.getLangTag());
        } else if (element instanceof DynamicExpression) {
            return new DynamicNode((DynamicExpression) element);
        } else {
            Couple<String, String> data = Datatypes.toLiteral(element);
            return store.getLiteralNode(data.x, data.y, null);
        }
    }

    /**
     * Tests whether the node is a Blank node
     *
     * @param node A RDF node
     * @return true if the node is a blank node
     */
    public static boolean isBlankNode(Node node) {
        return node.getNodeType() == Node.TYPE_BLANK;
    }

    /**
     * Tests whether the node is the rdf:nil IRI node
     *
     * @param node A RDF node
     * @return true of the node is the rdf:nil IRI node, false otherwise
     */
    public static boolean isRdfNil(Node node) {
        return (node.getNodeType() == Node.TYPE_IRI) && Vocabulary.rdfNil.equals(((IRINode) node).getIRIValue());
    }

    /**
     * Tests whether the property is the rdf:first IRI node
     *
     * @param property A RDF property
     * @return true of the node is the rdf:first IRI node, false otherwise
     */
    public static boolean isRdfFirst(Property property) {
        return (property.getNodeType() == Node.TYPE_IRI) && Vocabulary.rdfFirst.equals(((IRINode) property).getIRIValue());
    }

    /**
     * Tests whether the property is the rdf:rest IRI node
     *
     * @param property A RDF property
     * @return true of the node is the rdf:rest IRI node, false otherwise
     */
    public static boolean isRdfRest(Property property) {
        return (property.getNodeType() == Node.TYPE_IRI) && Vocabulary.rdfRest.equals(((IRINode) property).getIRIValue());
    }

    /**
     * Tests whether the property is the rdf:type IRI node
     *
     * @param property A RDF property
     * @return true of the node is the rdf:type IRI node, false otherwise
     */
    public static boolean isRdfType(Property property) {
        return (property.getNodeType() == Node.TYPE_IRI) && Vocabulary.rdfType.equals(((IRINode) property).getIRIValue());
    }

    /**
     * Computes the difference between two sets of quads
     * The result is changeset of positive and negative quads in the set difference: left - right.
     * This means that positive (added) quads are present on the left but not the right.
     * Conversely, negative (removed) quads are present on the right but not on the left.
     * The two sets are assumed to not contain duplicated quads, i.e. the same quad does not appear twice or more in the same set.
     *
     * @param left        The set of quads on the left
     * @param right       The set of quads on the right
     * @param ignoreGraph Whether to ignore the graph when comparing quads (compare as triples)
     * @return The changeset representing the difference
     */
    public static Changeset diff(Collection<Quad> left, Collection<Quad> right, boolean ignoreGraph) {
        Quad[] leftArray = left.toArray(new Quad[left.size()]);
        Quad[] rightArray = right.toArray(new Quad[right.size()]);

        Map<BlankNode, BlankNode> blanks = new HashMap<>();
        int countLeft = leftArray.length;
        int countRight = rightArray.length;

        for (int i = 0; i != leftArray.length; i++) {
            if (leftArray[i].getSubject().getNodeType() != Node.TYPE_BLANK) {
                // ignore blank nodes at this time
                for (int j = 0; j != rightArray.length; j++) {
                    if (rightArray[j] != null && diffSameQuads(leftArray[i], rightArray[j], blanks, ignoreGraph)) {
                        leftArray[i] = null;
                        rightArray[j] = null;
                        countLeft--;
                        countRight--;
                        break;
                    }
                }
            }
        }

        boolean modified = true;
        while (modified && countLeft > 0 && countRight > 0) {
            modified = false;
            for (int i = 0; i != leftArray.length; i++) {
                if (leftArray[i] == null)
                    continue;
                for (int j = 0; j != rightArray.length; j++) {
                    if (rightArray[j] != null && diffSameQuads(leftArray[i], rightArray[j], blanks, ignoreGraph)) {
                        leftArray[i] = null;
                        rightArray[j] = null;
                        countLeft--;
                        countRight--;
                        modified = true;
                        break;
                    }
                }
            }
        }

        Collection<Quad> remainingLeft = new ArrayList<>(countLeft);
        Collection<Quad> remainingRight = new ArrayList<>(countRight);
        if (countLeft > 0) {
            for (int i = 0; i != leftArray.length; i++) {
                if (leftArray[i] != null)
                    remainingLeft.add(leftArray[i]);
            }
        }
        if (countRight > 0) {
            for (int i = 0; i != rightArray.length; i++) {
                if (rightArray[i] != null)
                    remainingRight.add(rightArray[i]);
            }
        }
        return Changeset.fromAddedRemoved(remainingLeft, remainingRight);
    }

    /**
     * Determines whether the specified quads are equivalent, using the given blank node mapping
     *
     * @param quad1       A quad
     * @param quad2       Another quad
     * @param blanks      A map of blank nodes
     * @param ignoreGraph Whether to ignore the graph when comparing quads (compare as triples)
     * @return <code>true</code> if the two quads are equivalent
     */
    private static boolean diffSameQuads(Quad quad1, Quad quad2, Map<BlankNode, BlankNode> blanks, boolean ignoreGraph) {
        GraphNode graph = quad1.getGraph();
        SubjectNode subject = quad1.getSubject();
        Property property = quad1.getProperty();
        Node object = quad1.getObject();
        if (graph.getNodeType() == Node.TYPE_BLANK)
            graph = blanks.get(graph);
        if (subject.getNodeType() == Node.TYPE_BLANK)
            subject = blanks.get(subject);
        if (object.getNodeType() == Node.TYPE_BLANK)
            object = blanks.get(object);
        if (!RDFUtils.same(property, quad2.getProperty()))
            return false;
        if (!ignoreGraph && graph != null && !RDFUtils.same(graph, quad2.getGraph()))
            return false;
        if (subject != null && !RDFUtils.same(subject, quad2.getSubject()))
            return false;
        if (object != null && !RDFUtils.same(object, quad2.getObject()))
            return false;
        if (!ignoreGraph && graph == null && quad2.getGraph().getNodeType() != Node.TYPE_BLANK)
            return false;
        if (subject == null && quad2.getSubject().getNodeType() != Node.TYPE_BLANK)
            return false;
        if (object == null && quad2.getObject().getNodeType() != Node.TYPE_BLANK)
            return false;
        if (graph == null)
            blanks.put((BlankNode) quad1.getGraph(), (BlankNode) quad2.getGraph());
        if (subject == null)
            blanks.put((BlankNode) quad1.getSubject(), (BlankNode) quad2.getSubject());
        if (object == null)
            blanks.put((BlankNode) quad1.getObject(), (BlankNode) quad2.getObject());
        return true;
    }
}
