/*******************************************************************************
 * Copyright (c) 2016 Association Cénotélie (cenotelie.fr)
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
 ******************************************************************************/

package org.xowl.infra.store.rdf;

/**
 * Represents a blank node in a RDF graph
 *
 * @author Laurent Wouters
 */
public class BlankNode implements SubjectNode, GraphNode {
    /**
     * The node's unique identifier
     */
    private final long blankID;

    /**
     * Initializes this node
     *
     * @param id The unique identifier for this node
     */
    public BlankNode(long id) {
        blankID = id;
    }

    @Override
    public int getNodeType() {
        return TYPE_BLANK;
    }

    /**
     * Gets the blank identifier of this node
     *
     * @return The blank identifier of this node, or -1 if none
     */
    public long getBlankID() {
        return blankID;
    }

    @Override
    public int hashCode() {
        return (int) blankID;
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof BlankNode) && (this.blankID == ((BlankNode) o).getBlankID());
    }

    @Override
    public String toString() {
        return "_:" + blankID;
    }
}
