/*******************************************************************************
 * Copyright (c) 2017 Association Cénotélie (cenotelie.fr)
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

package org.xowl.infra.denotation;

import org.xowl.infra.store.Vocabulary;
import org.xowl.infra.store.rdf.Node;
import org.xowl.infra.store.storage.NodeManager;

/**
 * Represents the "orientation" property for a symbol
 * The orientation is expected to be represented by an angle as a double value between -2*PI and 2*PI
 *
 * @author Laurent Wouters
 */
public class SymbolPropertyOrientation extends SymbolProperty {
    /**
     * The URI for this property
     */
    public static final String URI = "http://xowl.org/infra/denotation/property/orientation";

    /**
     * The singleton instance
     */
    public static final SymbolProperty INSTANCE = new SymbolPropertyOrientation();

    /**
     * Initializes this property
     */
    private SymbolPropertyOrientation() {
        super(URI, "orientation", true);
    }

    public boolean isValidValue(Object value) {
        return value != null && (value instanceof Double) && (((double) value) >= -2 * Math.PI) && (((double) value) <= 2 * Math.PI);
    }

    /**
     * Serializes in JSON a value of this property
     *
     * @param builder The string builder to serialize to
     * @param value   The value to serialize
     */
    public void serializeValueJson(StringBuilder builder, Object value) {
        builder.append("\"");
        builder.append(Double.toString((double) value));
        builder.append("\"");
    }

    /**
     * Serializes in RDF a value of this property
     *
     * @param nodes The node manager to use
     * @param value The value to serialize
     * @return The RDF node
     */
    public Node serializeValueRdf(NodeManager nodes, Object value) {
        return nodes.getLiteralNode(value.toString(), Vocabulary.xsdDouble, null);
    }
}
