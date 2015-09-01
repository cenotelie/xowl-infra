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

package org.xowl.store.owl;

import org.xowl.lang.owl2.AnonymousIndividual;
import org.xowl.store.rdf.SubjectNode;
import org.xowl.utils.data.Attribute;
import org.xowl.utils.data.Dataset;

/**
 * Represents a RDF store associated to an anonymous individual
 *
 * @author Laurent Wouters
 */
public class AnonymousNode implements SubjectNode {
    /**
     * The type of node
     */
    public static final int TYPE = 4;


    /**
     * The associated anonymous individual
     */
    private final AnonymousIndividual anonInd;

    /**
     * Initializes this node
     *
     * @param anon The represented anonymous individual
     */
    public AnonymousNode(AnonymousIndividual anon) {
        anonInd = anon;
    }

    @Override
    public int getNodeType() {
        return TYPE;
    }

    /**
     * Gets the anonymous individual represented by this node
     *
     * @return The anonymous individual represented by this node
     */
    public AnonymousIndividual getIndividual() {
        return anonInd;
    }

    @Override
    public int hashCode() {
        return anonInd.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof AnonymousNode) {
            AnonymousNode node = (AnonymousNode) obj;
            return (anonInd == node.anonInd);
        }
        return false;
    }

    @Override
    public String toString() {
        return "_:" + anonInd.getNodeID();
    }
}
