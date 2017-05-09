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

package org.xowl.infra.denotation.artifact;

import org.xowl.infra.store.Vocabulary;
import org.xowl.infra.store.rdf.Node;
import org.xowl.infra.store.storage.NodeManager;

import java.awt.geom.Point2D;

/**
 * Represents the "position" property for a visual symbol in a 2D graph
 * The position is expected to be represented as a instance of the AWT Point2D class
 *
 * @author Laurent Wouters
 */
public class SymbolPropertyPosition2D extends SymbolProperty {
    /**
     * The URI for this property
     */
    public static final String URI = "http://xowl.org/infra/denotation/property/position2d";

    /**
     * The singleton instance
     */
    public static final SymbolProperty INSTANCE = new SymbolPropertyPosition2D();

    /**
     * Initializes this property
     */
    private SymbolPropertyPosition2D() {
        super(URI, "position2d", true);
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
}
