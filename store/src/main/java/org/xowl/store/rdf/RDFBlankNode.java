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
 * Represents a blank node in a RDF graph
 *
 * @author Laurent Wouters
 */
public class RDFBlankNode implements RDFSubjectNode {
    /**
     * the node's unique identifier
     */
    private int blankID;

    /**
     * Initializes this node
     *
     * @param id The unique identifier for this node
     */
    RDFBlankNode(int id) {
        blankID = id;
    }

    @Override
    public RDFNodeType getNodeType() {
        return RDFNodeType.BLANK;
    }

    @Override
    public Key getStoreKey() {
        return null;
    }

    @Override
    public Literal getLiteralValue() {
        return null;
    }

    @Override
    public int getBlankID() {
        return blankID;
    }

    @Override
    public AnonymousIndividual getAnonymous() {
        return null;
    }

    @Override
    public int hashCode() {
        return blankID;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof RDFBlankNode) {
            RDFBlankNode node = (RDFBlankNode) obj;
            return (blankID == node.blankID);
        }
        return false;
    }

    @Override
    public String toString() {
        return "<blank>";
    }
}