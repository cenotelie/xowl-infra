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

package org.xowl.store.rete;

import org.xowl.store.rdf.Triple;

import java.util.Collection;

/**
 * Represents an element that holds facts
 *
 * @author Laurent Wouters
 */
public interface FactHolder {
    /**
     * Gets the facts in this element
     *
     * @return The facts in this element
     */
    Collection<Triple> getFacts();

    /**
     * Adds a new child to this element
     *
     * @param activable The child to add
     */
    void addChild(FactActivable activable);

    /**
     * Removes a child from this element
     *
     * @param node The child to remove
     */
    void removeChild(FactActivable node);
}
