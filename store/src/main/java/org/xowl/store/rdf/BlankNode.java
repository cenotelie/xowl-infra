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

import org.xowl.utils.data.Attribute;
import org.xowl.utils.data.Dataset;

/**
 * Represents a blank node in a RDF graph
 *
 * @author Laurent Wouters
 */
public class BlankNode implements SubjectNode {
    /**
     * The identifier key for the serialization of the id attribute
     */
    private static final String SERIALIZATION_ID = "id";

    /**
     * The type of node
     */
    public static final int TYPE = 1;

    /**
     * the node's unique identifier
     */
    private int blankID;

    /**
     * Initializes this node
     *
     * @param id The unique identifier for this node
     */
    public BlankNode(int id) {
        blankID = id;
    }

    /**
     * Initializes this node from a dataset
     *
     * @param data The node of serialized data
     */
    public BlankNode(org.xowl.utils.data.Node data) {
        this.blankID = (int) data.attribute(SERIALIZATION_ID).getValue();
    }

    @Override
    public int getNodeType() {
        return TYPE;
    }

    /**
     * Gets the blank identifier of this node
     *
     * @return The blank identifier of this node, or -1 if none
     */
    public int getBlankID() {
        return blankID;
    }

    @Override
    public org.xowl.utils.data.Node serialize(Dataset dataset) {
        org.xowl.utils.data.Node result = new org.xowl.utils.data.Node(dataset, SERIALIZATION_NAME);
        Attribute attributeType = new Attribute(dataset, SERIALIZATION_TYPE);
        attributeType.setValue(TYPE);
        result.getAttributes().add(attributeType);
        Attribute attributeID = new Attribute(dataset, SERIALIZATION_ID);
        attributeID.setValue(blankID);
        result.getAttributes().add(attributeID);
        return result;
    }

    @Override
    public int hashCode() {
        return blankID;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof BlankNode) {
            BlankNode node = (BlankNode) obj;
            return (blankID == node.blankID);
        }
        return false;
    }

    @Override
    public String toString() {
        return "_:" + Integer.toString(blankID);
    }
}
