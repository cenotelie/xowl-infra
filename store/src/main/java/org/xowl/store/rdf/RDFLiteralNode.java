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

/**
 * Represents a node associated to a literal value in a RDF graph
 *
 * @author Laurent Wouters
 */
public abstract class RDFLiteralNode implements RDFNode {
    @Override
    public RDFNodeType getNodeType() {
        return RDFNodeType.LITERAL;
    }

    @Override
    public IRI getIRI() {
        return null;
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
    public String toString() {
        Literal literal = getLiteralValue();
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
