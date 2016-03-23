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

package org.xowl.infra.store.rete;

import java.util.Collection;

/**
 * Represents an element that holds tokens
 *
 * @author Laurent Wouters
 */
interface TokenHolder {
    /**
     * Gets the tokens in this element
     *
     * @return The tokens in this element
     */
    Collection<Token> getTokens();

    /**
     * Adds the specified child to this element
     *
     * @param activable A child element
     */
    void addChild(TokenActivable activable);

    /**
     * Removes the specified child from this element
     *
     * @param activable A child element
     */
    void removeChild(TokenActivable activable);

    /**
     * Prepares this node for its destruction
     */
    void onDestroy();
}
