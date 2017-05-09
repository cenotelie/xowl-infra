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

import java.awt.*;

/**
 * Represents the "color" property for a symbol
 * The color is expected to be an instance of the AWT Color class
 *
 * @author Laurent Wouters
 */
public class SymbolPropertyColor extends SymbolProperty {
    /**
     * The URI for this property
     */
    public static final String URI = "http://xowl.org/infra/denotation/property/color";

    /**
     * The singleton instance
     */
    public static final SymbolProperty INSTANCE = new SymbolPropertyColor();

    /**
     * Initializes this property
     */
    private SymbolPropertyColor() {
        super(URI, "color", true);
    }

    public boolean isValidValue(Object value) {
        return value != null && (value instanceof Color);
    }

    /**
     * Serializes in JSON a value of this property
     *
     * @param builder The string builder to serialize to
     * @param value   The value to serialize
     */
    public void serializeValueJson(StringBuilder builder, Object value) {
        Color color = (Color) value;
        builder.append("\"");
        builder.append(Integer.toString(color.getRGB()));
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
        Color color = (Color) value;
        return nodes.getLiteralNode(Integer.toString(color.getRGB()), Vocabulary.xsdString, null);
    }
}