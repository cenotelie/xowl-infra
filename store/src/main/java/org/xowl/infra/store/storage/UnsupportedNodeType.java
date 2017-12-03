/*******************************************************************************
 * Copyright (c) 2016 Association Cénotélie (cenotelie.fr)
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

package org.xowl.infra.store.storage;

import org.xowl.infra.store.rdf.Node;
import org.xowl.infra.store.rdf.QuadField;

/**
 * Exception thrown when the given RDF node is of an unsupported type
 *
 * @author Laurent Wouters
 */
public class UnsupportedNodeType extends RuntimeException {
    /**
     * The node that was unexpected
     */
    private final Node node;
    /**
     * The field of the unexpected node
     */
    private final QuadField field;

    /**
     * Initializes this exception
     *
     * @param node  The unexpected node
     * @param field The field of the unexpected node
     */
    public UnsupportedNodeType(Node node, QuadField field) {
        this.node = node;
        this.field = field;
    }

    /**
     * Gets the unexpected node
     *
     * @return The unexpected node
     */
    public Node getNode() {
        return node;
    }

    /**
     * Gets the field of the unexpected node
     *
     * @return The field of the unexpected node
     */
    public QuadField getField() {
        return field;
    }
}
