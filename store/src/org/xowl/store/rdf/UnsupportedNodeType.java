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

/**
 * Exception thrown when the given RDF node is of an unsupported type
 */
public class UnsupportedNodeType extends Exception {

    /**
     * The node that was unexpected
     */
    private RDFNode node;

    /**
     * Initializes this exception
     *
     * @param node    The unexpected node
     * @param message The exception's message
     */
    public UnsupportedNodeType(RDFNode node, String message) {
        super(message);
        this.node = node;
    }

    /**
     * Gets the unexpected node
     *
     * @return The unexpected node
     */
    public RDFNode getNode() {
        return node;
    }
}
