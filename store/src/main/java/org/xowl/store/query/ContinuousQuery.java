/**********************************************************************
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
 **********************************************************************/

package org.xowl.store.query;

import org.xowl.store.rete.RETERule;
import org.xowl.store.rete.Token;
import org.xowl.store.rete.TokenActivable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Represents the data of a continuous query
 *
 * @author Laurent Wouters
 */
class ContinuousQuery {
    /**
     * The query's condition
     */
    private QueryCondition condition;
    /**
     * The associated RETE rule
     */
    private RETERule rule;
    /**
     * The active listeners of the query's output
     */
    private List<QueryListener> listeners;

    /**
     * Initializes this data
     *
     * @param condition The query's condition
     */
    public ContinuousQuery(QueryCondition condition) {
        this.condition = condition;
        this.rule = new RETERule(new TokenActivable() {
            @Override
            public void activateToken(Token token) {
                Solution solution = new Solution(token.getBindings());
                for (QueryListener listener : listeners)
                    listener.onNewSolution(solution);
            }

            @Override
            public void deactivateToken(Token token) {
                Solution solution = new Solution(token.getBindings());
                for (QueryListener listener : listeners)
                    listener.onSolutionRetracted(solution);
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
        });
        this.rule.getPositives().addAll(condition.getPositives());
        this.rule.getNegatives().addAll(condition.getNegatives());
        this.listeners = new ArrayList<>();
    }

    /**
     * Gets whether this query matches the specified condition
     *
     * @param condition A query's condition
     * @return <code>true</code> if the condition is matched
     */
    public boolean matches(QueryCondition condition) {
        return this.condition.equals(condition);
    }

    /**
     * Gets the RETE rule used by this query
     *
     * @return The associated RETE rule
     */
    public RETERule getRule() {
        return rule;
    }

    /**
     * Adds the specified listener to this query
     *
     * @param listener A listener
     */
    public void addListener(QueryListener listener) {
        listeners.add(listener);
    }

    /**
     * Removes the specified listener from this query
     *
     * @param listener A listener
     */
    public void removeListener(QueryListener listener) {
        listeners.remove(listener);
    }

    /**
     * Gets the number of registered listeners on this query
     *
     * @return The number of registered listeners
     */
    public int getListenersCount() {
        return listeners.size();
    }
}
