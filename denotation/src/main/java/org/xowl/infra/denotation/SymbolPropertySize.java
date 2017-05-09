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
 * Represents the "size" property for a symbol
 * The size is expected to be represented as a double value
 *
 * @author Laurent Wouters
 */
public class SymbolPropertySize extends SymbolProperty {
    /**
     * The URI for this property
     */
    public static final String URI = "http://xowl.org/infra/denotation/property/size";

    /**
     * The singleton instance
     */
    public static final SymbolProperty INSTANCE = new SymbolPropertySize();

    /**
     * Initializes this property
     */
    private SymbolPropertySize() {
        super(URI, "size", true);
    }

    @Override
    public boolean isValidValue(Object value) {
        return value != null && (value instanceof Double);
    }

    @Override
    public void serializeValueJson(StringBuilder builder, Object value) {
        builder.append("\"");
        builder.append(Double.toString((double) value));
        builder.append("\"");
    }

    @Override
    public Node serializeValueRdf(NodeManager nodes, Object value) {
        return nodes.getLiteralNode(value.toString(), Vocabulary.xsdDouble, null);
    }
}
