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

package org.xowl.store.query;

import org.xowl.store.rete.Token;
import org.xowl.store.rete.TokenActivable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Represents the output of a query
 *
 * @author Laurent Wouters
 */
class QueryOutput implements TokenActivable {
    /**
     * The current matching tokens
     */
    private List<Token> tokens;

    /**
     * Initializes this output
     */
    public QueryOutput() {
        this.tokens = new ArrayList<>();
    }

    /**
     * Gets the query solutions
     *
     * @return The query solutions
     */
    public Collection<Solution> getSolutions() {
        Collection<Solution> solutions = new ArrayList<>();
        for (Token token : tokens)
            solutions.add(new Solution(token.getBindings()));
        return solutions;
    }

    @Override
    public void activateToken(Token token) {
        tokens.add(token);
    }

    @Override
    public void deactivateToken(Token token) {
        tokens.remove(token);
    }

    @Override
    public void activateTokens(Collection<Token> tokens) {
        for (Token token : tokens)
            activateToken(token);
    }

    @Override
    public void deactivateTokens(Collection<Token> tokens) {
        for (Token token : tokens)
            deactivateToken(token);
    }
}
