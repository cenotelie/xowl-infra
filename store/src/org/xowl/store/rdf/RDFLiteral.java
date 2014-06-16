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
 * Represents a literal in a RDF graph
 */
public class RDFLiteral {
    /**
     * The key for the literal's type
     */
    private Key type;
    /**
     * The next literal in the bucket
     */
    private RDFLiteral next;

    /**
     * Initializes this literal
     *
     * @param type The key for the literal's type
     */
    public RDFLiteral(Key type) {
        this.type = type;
    }

    /**
     * Gets the key for the literal's type
     *
     * @return The key for the literal's type
     */
    public Key getTypeKey() {
        return type;
    }

    /**
     * Gets the next literal in this bucket
     *
     * @return The next literal in this bucket
     */
    public RDFLiteral getNext() {
        return next;
    }

    /**
     * Sets the next literal in this bucket
     *
     * @param next The next literal in this bucket
     */
    public void setNext(RDFLiteral next) {
        this.next = next;
    }
}
