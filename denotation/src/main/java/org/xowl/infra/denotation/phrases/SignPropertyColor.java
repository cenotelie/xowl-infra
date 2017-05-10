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

import fr.cenotelie.hime.redist.ASTNode;
import org.xowl.infra.store.Vocabulary;
import org.xowl.infra.store.rdf.Node;
import org.xowl.infra.store.storage.NodeManager;
import org.xowl.infra.utils.TextUtils;

import java.awt.*;

/**
 * Represents the "color" property for a sign
 * The color is expected to be an instance of the AWT Color class
 *
 * @author Laurent Wouters
 */
public class SignPropertyColor extends SignProperty {
    /**
     * The URI for this property
     */
    public static final String URI = "http://xowl.org/infra/denotation/schema#color";

    /**
     * The singleton instance
     */
    public static final SignProperty INSTANCE = new SignPropertyColor();

    /**
     * Initializes this property
     */
    private SignPropertyColor() {
        super(URI, "color", true);
    }

    @Override
    public boolean isValidValue(Object value) {
        return value != null && (value instanceof Color);
    }

    @Override
    public void serializeValueJson(StringBuilder builder, Object value) {
        Color color = (Color) value;
        builder.append("\"");
        builder.append(Integer.toString(color.getRGB()));
        builder.append("\"");
    }

    @Override
    public Node serializeValueRdf(NodeManager nodes, Object value) {
        Color color = (Color) value;
        return nodes.getLiteralNode(Integer.toString(color.getRGB()), Vocabulary.xsdString, null);
    }

    @Override
    public Object deserializeValueJson(ASTNode definition) {
        String value = TextUtils.unescape(definition.getValue());
        value = value.substring(1, value.length() - 1);
        return Integer.parseInt(value);
    }
}
