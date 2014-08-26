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

/**
 * Represents a node associated to an IRI in a RDF graph
 *
 * @author Laurent Wouters
 */
public abstract class IRINode implements SubjectNode, Property {
    /**
     * The type of node
     */
    public static final int TYPE = 0;

    @Override
    public int getNodeType() {
        return TYPE;
    }

    @Override
    public String toString() {
        return getIRIValue();
    }

    /**
     * Gets the IRI's value associated to this node
     *
     * @return The IRI's value associated to this node
     */
    public abstract String getIRIValue();
}
