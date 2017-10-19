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

import java.awt.geom.Point2D;

/**
 * Represents the "size2d" property for a visual sign in a 2D graph
 * The position is expected to be represented as a instance of the AWT Point2D class
 *
 * @author Laurent Wouters
 */
public class SignPropertySize2D extends SignProperty {
    /**
     * The URI for this property
     */
    public static final String URI = "http://xowl.org/infra/denotation/schema#size2d";

    /**
     * The singleton instance
     */
    public static final SignProperty INSTANCE = new SignPropertySize2D();

    /**
     * Initializes this property
     */
    private SignPropertySize2D() {
        super(URI, "size2d", true);
    }

    @Override
    public boolean isValidValue(Object value) {
        return value != null && (value instanceof Point2D);
    }

    @Override
    public void serializeValueJson(StringBuilder builder, Object value) {
        Point2D point = (Point2D) value;
        builder.append("{\"x\": \"");
        builder.append(Double.toString(point.getX()));
        builder.append("\", \"y\": \"");
        builder.append(Double.toString(point.getY()));
        builder.append("\"}");
    }

    @Override
    public Node serializeValueRdf(NodeManager nodes, Object value) {
        Point2D point = (Point2D) value;
        return nodes.getLiteralNode(Double.toString(point.getX()) + " " + Double.toString(point.getX()), Vocabulary.xsdString, null);
    }

    @Override
    public Object deserializeValueJson(ASTNode definition) {
        String x = "0.0";
        String y = "0.0";
        for (ASTNode child : definition.getChildren()) {
            ASTNode nodeHeader = child.getChildren().get(0);
            String memberName = TextUtils.unescape(nodeHeader.getValue());
            memberName = memberName.substring(1, memberName.length() - 1);
            switch (memberName) {
                case "x": {
                    ASTNode nodeValue = child.getChildren().get(1);
                    x = TextUtils.unescape(nodeValue.getValue());
                    x = x.substring(1, x.length() - 1);
                    break;
                }
                case "y": {
                    ASTNode nodeValue = child.getChildren().get(1);
                    y = TextUtils.unescape(nodeValue.getValue());
                    y = y.substring(1, y.length() - 1);
                    break;
                }
            }
        }
        return new Point2D.Double(Double.parseDouble(x), Double.parseDouble(y));
    }
}
