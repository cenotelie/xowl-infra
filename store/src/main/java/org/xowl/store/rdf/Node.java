/*******************************************************************************
 * Copyright (c) 2015 Laurent Wouters
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
 ******************************************************************************/

package org.xowl.store.rdf;

/**
 * Represents a node in a RDF graph
 *
 * @author Laurent Wouters
 */
public interface Node {
    /**
     * Flag for subject nodes
     */
    int FLAG_SUBJECT = 1;
    /**
     * Flag for property nodes
     */
    int FLAG_PROPERTY = 1 << 1;
    /**
     * Flag for graph nodes
     */
    int FLAG_GRAPH = 1 << 2;

    /**
     * The type value of an IRI node
     */
    int TYPE_IRI = FLAG_SUBJECT | FLAG_PROPERTY | FLAG_GRAPH | 1 << 3;
    /**
     * The type value of a blank node
     */
    int TYPE_BLANK = FLAG_SUBJECT | FLAG_GRAPH | 1 << 4;
    /**
     * The type value of a literal node
     */
    int TYPE_LITERAL = 1 << 5;
    /**
     * The type value of an anonymous node
     */
    int TYPE_ANONYMOUS = FLAG_SUBJECT | 1 << 6;
    /**
     * The type value of a dynamic node
     */
    int TYPE_DYNAMIC = FLAG_SUBJECT | FLAG_PROPERTY | FLAG_GRAPH | 1 << 7;
    /**
     * The type value of a variable node
     */
    int TYPE_VARIABLE = FLAG_SUBJECT | FLAG_PROPERTY | FLAG_GRAPH | 1 << 8;

    /**
     * Gets the node's type
     *
     * @return The node's type
     */
    int getNodeType();
}
