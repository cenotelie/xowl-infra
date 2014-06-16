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
import org.xowl.lang.owl2.Literal;

/**
 * Represents a node associated to a literal value in a RDF graph
 *
 * @author Laurent Wouters
 */
public class RDFLiteralNode implements RDFNode {
    /**
     * The RDF literal
     */
    private RDFLiteral gLit;
    /**
     * The OWL literal
     */
    private Literal literal;

    /**
     * Initializes this node
     *
     * @param ln  The RDF literal
     * @param lit The OWL literal
     */
    RDFLiteralNode(RDFLiteral ln, Literal lit) {
        this.gLit = ln;
        this.literal = lit;
    }

    /**
     * Gets the associated literal
     *
     * @return The associated OWL literal
     */
    public Literal getLiteral() {
        return literal;
    }

    @Override
    public RDFNodeType getNodeType() {
        return RDFNodeType.Literal;
    }

    @Override
    public Key getStoreKey() {
        return null;
    }

    @Override
    public RDFLiteral getLiteralValue() {
        return gLit;
    }

    @Override
    public int getBlankID() {
        return 0;
    }

    @Override
    public AnonymousIndividual getAnonymous() {
        return null;
    }

    @Override
    public int hashCode() {
        return gLit.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof RDFLiteralNode) {
            RDFLiteralNode node = (RDFLiteralNode) obj;
            return (gLit == node.gLit);
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("\"");
        builder.append(literal.getLexicalValue());
        builder.append("\"");
        if (literal.getMemberOf() != null) {
            builder.append("^^");
            builder.append(literal.getMemberOf().getHasValue());
        }
        return builder.toString();
    }
}
