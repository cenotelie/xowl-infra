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

import org.xowl.store.rdf.Quad;

import java.util.Collection;

/**
 * Represents an element that can be activated by a fact in a RETE graph
 *
 * @author Laurent Wouters
 */
interface FactActivable {
    /**
     * Activates on the specified fact
     *
     * @param fact A fact
     */
    void activateFact(Quad fact);

    /**
     * Deactivates on the specified fact
     *
     * @param fact A fact
     */
    void deactivateFact(Quad fact);

    /**
     * Activates on a collection of facts
     *
     * @param facts A collection of facts
     */
    void activateFacts(Collection<Quad> facts);

    /**
     * Deactivates on a collection of facts
     *
     * @param facts A collection of facts
     */
    void deactivateFacts(Collection<Quad> facts);
}
