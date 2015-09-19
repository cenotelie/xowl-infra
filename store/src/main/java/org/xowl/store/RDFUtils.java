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

import org.xowl.lang.actions.DynamicExpression;
import org.xowl.lang.owl2.AnonymousIndividual;
import org.xowl.lang.owl2.IRI;
import org.xowl.lang.owl2.Literal;
import org.xowl.lang.owl2.Ontology;
import org.xowl.lang.runtime.Entity;
import org.xowl.store.owl.AnonymousNode;
import org.xowl.store.owl.DynamicNode;
import org.xowl.store.rdf.IRINode;
import org.xowl.store.rdf.LiteralNode;
import org.xowl.store.rdf.Node;
import org.xowl.store.storage.NodeManager;

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
        } else if (element instanceof org.xowl.lang.runtime.Literal) {
            org.xowl.lang.runtime.Literal literal = (org.xowl.lang.runtime.Literal) element;
            return store.getLiteralNode(literal.getLexicalValue(), literal.getMemberOf().getInterpretationOf().getHasIRI().getHasValue(), literal.getLangTag());
        } else if (element instanceof DynamicExpression) {
            return new DynamicNode((DynamicExpression) element);
        } else {
            throw new IllegalArgumentException("OWL element " + element.getClass().getName() + " cannot be mapped to a RDF node");
        }
    }
}
