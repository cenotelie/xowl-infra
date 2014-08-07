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
 * Represents the identifier of a type of node in a RDF graph
 *
 * @author Laurent Wouters
 */
public enum RDFNodeType {
    /**
     * An IRI
     */
    IRI_REFERENCE,

    /**
     * A blank node
     */
    BLANK,

    /**
     * An anonymous node
     */
    ANONYMOUS,

    /**
     * A literal node
     */
    LITERAL,

    /**
     * A variable node
     */
    VARIABLE,

    /**
     * A dynamic node
     */
    DYNAMIC
}
