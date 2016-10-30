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

package org.xowl.infra.store.rete;

import org.xowl.infra.store.rdf.Quad;

/**
 * Represents the match of a token and a fact for a join operation
 *
 * @author Laurent Wouters
 */
class JoinMatch {
    /**
     * A triple fact
     */
    public final Quad fact;
    /**
     * A token
     */
    public final Token token;

    /**
     * Initializes this couple
     *
     * @param fact  The triple fact
     * @param token The token
     */
    public JoinMatch(Quad fact, Token token) {
        this.fact = fact;
        this.token = token;
    }
}
