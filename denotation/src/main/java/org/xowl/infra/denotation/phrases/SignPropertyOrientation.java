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

package org.xowl.infra.denotation.phrases;

import org.xowl.infra.store.Vocabulary;
import org.xowl.infra.store.rdf.Node;
import org.xowl.infra.store.storage.NodeManager;

/**
 * Represents the "orientation" property for a sign
 * The orientation is expected to be represented by an angle as a double value between -2*PI and 2*PI
 *
 * @author Laurent Wouters
 */
public class SignPropertyOrientation extends SignProperty {
    /**
     * The URI for this property
     */
    public static final String URI = "http://xowl.org/infra/denotation/property/orientation";

    /**
     * The singleton instance
     */
    public static final SignProperty INSTANCE = new SignPropertyOrientation();

    /**
     * Initializes this property
     */
    private SignPropertyOrientation() {
        super(URI, "orientation", true);
    }

    @Override
    public boolean isValidValue(Object value) {
        return value != null && (value instanceof Double) && (((double) value) >= -2 * Math.PI) && (((double) value) <= 2 * Math.PI);
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
