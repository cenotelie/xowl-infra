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

import org.xowl.store.rdf.XOWLTriple;

/**
 * Represents a couple of matching items
 *
 * @author Laurent Wouters
 */
public class Couple {
    /**
     * A triple fact
     */
    public XOWLTriple fact;
    /**
     * A token
     */
    public Token token;

    /**
     * Initializes this couple
     *
     * @param fact  The triple fact
     * @param token The token
     */
    public Couple(XOWLTriple fact, Token token) {
        this.fact = fact;
        this.token = token;
    }
}