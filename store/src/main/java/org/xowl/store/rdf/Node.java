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

import org.xowl.utils.data.Dataset;

/**
 * Represents a node in a RDF graph
 *
 * @author Laurent Wouters
 */
public interface Node {
    /**
     * The identifier key for the serialization of this element
     */
    String SERIALIZATION_NAME = "Node";
    /**
     * The identifier key for the serialization of the type attribute
     */
    String SERIALIZATION_TYPE = "type";

    /**
     * Gets the node's type
     *
     * @return The node's type
     */
    int getNodeType();

    /**
     * Serializes this node
     *
     * @param dataset The dataset to serialize to
     * @return The serialized data
     */
    org.xowl.utils.data.Node serialize(Dataset dataset);
}
