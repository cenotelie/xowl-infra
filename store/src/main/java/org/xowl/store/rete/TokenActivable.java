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

package org.xowl.store.rete;

import java.util.Collection;

/**
 * Represnets an element that can be activated by a token
 *
 * @author Laurent Wouters
 */
public interface TokenActivable {
    /**
     * Activates on the specified token
     *
     * @param token A token
     */
    void activateToken(Token token);

    /**
     * Deactivates on the specified token
     *
     * @param token A token
     */
    void deactivateToken(Token token);

    /**
     * Activates on the specified tokens
     *
     * @param tokens Some tokens
     */
    void activateTokens(Collection<Token> tokens);

    /**
     * Deactivates on the specified tokens
     *
     * @param tokens Some tokens
     */
    void deactivateTokens(Collection<Token> tokens);
}
