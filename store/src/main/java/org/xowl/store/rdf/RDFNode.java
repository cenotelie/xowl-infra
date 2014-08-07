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
 * Represents a node in a RDF graph
 *
 * @author Laurent Wouters
 */
public interface RDFNode {
    /**
     * Gets the node's type
     *
     * @return The node's type
     */
    RDFNodeType getNodeType();

    /**
     * Gets the store key to retrieve the node's value
     *
     * @return The store key associated to this node, or null if none is
     */
    Key getStoreKey();

    /**
     * Gets the literal value associated to this node
     *
     * @return The literal value associated to this node, or null if none is
     */
    Literal getLiteralValue();

    /**
     * Gets the blank identifier of this node
     *
     * @return The blank identifier of this node, or -1 if none
     */
    int getBlankID();

    /**
     * Gets the anonymous individual associated to this node
     *
     * @return The anonymous individual associated to this node, or null if none is
     */
    AnonymousIndividual getAnonymous();
}
