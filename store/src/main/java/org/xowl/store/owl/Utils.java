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

package org.xowl.store.owl;

import org.xowl.lang.actions.DynamicExpression;
import org.xowl.lang.owl2.AnonymousIndividual;
import org.xowl.lang.owl2.IRI;
import org.xowl.lang.owl2.Literal;
import org.xowl.lang.owl2.Ontology;
import org.xowl.lang.runtime.Entity;
import org.xowl.store.rdf.IRINode;
import org.xowl.store.rdf.LiteralNode;
import org.xowl.store.rdf.Node;
import org.xowl.store.storage.NodeManager;

/**
 * Utility methods for round-trip OWL and RDF conversions
 *
 * @author Laurent Wouters
 */
class Utils {
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
            case Node.TYPE_BLANK: {
                // cannot translate back blank nodes ...
                // TODO: throw an error here
                return null;
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
        return null;
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
            // TODO: throw an error here
            return null;
        }
    }
}
