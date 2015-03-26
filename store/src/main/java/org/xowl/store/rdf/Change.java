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
 * Represents a change in a RDF database
 *
 * @author Laurent Wouters
 */
public class Change {
    /**
     * The value of the change
     */
    private final Quad quad;
    /**
     * Whether this change is an addition, or a removal
     */
    private final boolean positive;

    /**
     * Initializes this change
     *
     * @param quad     The value of this change
     * @param positive Whether this change is an addition, or a removal
     */
    public Change(Quad quad, boolean positive) {
        this.quad = quad;
        this.positive = positive;
    }

    /**
     * Gets the value of this change
     *
     * @return The value of this change
     */
    public Quad getValue() {
        return quad;
    }

    /**
     * Gets whether this change is an addition or a removal
     *
     * @return Whether this change is an addition, or a removal
     */
    public boolean isPositive() {
        return positive;
    }
}
