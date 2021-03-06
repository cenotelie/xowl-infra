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

import fr.cenotelie.commons.utils.TextUtils;
import fr.cenotelie.hime.redist.ASTNode;
import org.xowl.infra.store.Vocabulary;
import org.xowl.infra.store.rdf.Node;
import org.xowl.infra.store.storage.NodeManager;

/**
 * Represents the "length" property for a sign in a text
 * The size is expected to be represented as an integer value
 *
 * @author Laurent Wouters
 */
public class SignPropertyLength extends SignProperty {
    /**
     * The URI for this property
     */
    public static final String URI = "http://xowl.org/infra/denotation/schema#length";

    /**
     * The singleton instance
     */
    public static final SignProperty INSTANCE = new SignPropertyLength();

    /**
     * Initializes this property
     */
    private SignPropertyLength() {
        super(URI, "length", true);
    }

    @Override
    public boolean isValidValue(Object value) {
        return value != null && (value instanceof Integer) && ((int) value) >= 0;
    }

    @Override
    public void serializeValueJson(StringBuilder builder, Object value) {
        builder.append("\"");
        builder.append(Integer.toString((int) value));
        builder.append("\"");
    }

    @Override
    public Node serializeValueRdf(NodeManager nodes, Object value) {
        return nodes.getLiteralNode(value.toString(), Vocabulary.xsdInt, null);
    }

    @Override
    public Object deserializeValueJson(ASTNode definition) {
        String value = TextUtils.unescape(definition.getValue());
        value = value.substring(1, value.length() - 1);
        return Integer.parseInt(value);
    }
}
