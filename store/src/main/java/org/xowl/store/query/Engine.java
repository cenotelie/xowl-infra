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

import org.xowl.store.rdf.*;
import org.xowl.store.rete.RETENetwork;
import org.xowl.store.rete.RETERule;
import org.xowl.store.rete.Token;
import org.xowl.store.rete.TokenActivable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Represents a query engine for a RDF store
 */
public class Engine implements ChangeListener {
    /**
     * A RETE network for the pattern matching of queries
     */
    private RETENetwork rete;
    /**
     * The registered continuous queries
     */
    private List<ContinuousQuery> continuousQueries;
    /**
     * The new changes since the last application
     */
    private List<Change> newChanges;
    /**
     * The new changesets since the last application
     */
    private List<Changeset> newChangesets;
    /**
     * Flag whether outstanding changes are currently being applied
     */
    private boolean isApplying;
    /**
     * Buffer of positive quads
     */
    private Collection<Quad> bufferPositives;
    /**
     * Buffer of negative quads
     */
    private Collection<Quad> bufferNegatives;

    /**
     * Initializes this engine
     *
     * @param store The RDF store to query
     */
    public Engine(RDFStore store) {
        this.rete = new RETENetwork(store);
        this.continuousQueries = new ArrayList<>();
        this.newChanges = new ArrayList<>();
        this.newChangesets = new ArrayList<>();
        this.bufferPositives = new ArrayList<>();
        this.bufferNegatives = new ArrayList<>();
        store.addListener(this);
    }

    /**
     * Executes the specified query and gets the solutions
     *
     * @param query A query
     * @return The solutions
     */
    public Collection<Solution> execute(QueryCondition query) {
        // build the RETE rule
        final List<Solution> result = new ArrayList<>();
        RETERule rule = new RETERule(new TokenActivable() {
            @Override
            public void activateToken(Token token) {
                result.add(new Solution(token.getBindings()));
            }

            @Override
            public void deactivateToken(Token token) {
                // not needed
                throw new UnsupportedOperationException();
            }

            @Override
            public void activateTokens(Collection<Token> tokens) {
                for (Token token : tokens)
                    activateToken(token);
            }

            @Override
            public void deactivateTokens(Collection<Token> tokens) {
                // not needed
                throw new UnsupportedOperationException();
            }
        });
        rule.getPositives().addAll(query.getPositives());
        rule.getNegatives().addAll(query.getNegatives());
        // execute
        rete.addRule(rule);
        // TODO remove the rule from the network
        // rete.removeRule(rule);
        return result;
    }

    /**
     * Adds a listener on the output of the specified query
     *
     * @param condition The conditions for the query
     * @param listener  The listener for the results
     */
    public void addListener(QueryCondition condition, QueryListener listener) {
        for (ContinuousQuery query : continuousQueries) {
            if (query.matches(condition)) {
                query.addListener(listener);
                return;
            }
        }
        ContinuousQuery query = new ContinuousQuery(condition);
        query.addListener(listener);
        rete.addRule(query.getRule());
    }

    /**
     * Removes a previously added listener on the output of a query
     *
     * @param condition The condition for the query
     * @param listener  The listener to remove
     */
    public void removeListener(QueryCondition condition, QueryListener listener) {
        for (ContinuousQuery query : continuousQueries) {
            if (query.matches(condition)) {
                query.removeListener(listener);
                if (query.getListenersCount() == 0) {
                    // TODO remove the rule from the network
                    // rete.removeRule(query.getRule());
                    continuousQueries.remove(query);
                }
                return;
            }
        }
    }

    @Override
    public void onChange(Change change) {
        newChanges.add(change);
        apply();
    }

    @Override
    public void onChange(Changeset changeset) {
        newChangesets.add(changeset);
        apply();
    }

    /**
     * Applies all outstanding changes
     */
    protected void apply() {
        if (isApplying)
            return;
        isApplying = true;
        while (newChanges.size() > 0 || newChangesets.size() > 0) {
            for (Change change : newChanges) {
                if (change.isPositive())
                    bufferPositives.add(change.getValue());
                else
                    bufferNegatives.add(change.getValue());
            }
            newChanges.clear();
            for (Changeset changeset : newChangesets) {
                bufferPositives.addAll(changeset.getPositives());
                bufferNegatives.addAll(changeset.getNegatives());
            }
            newChangesets.clear();
            rete.injectPositives(bufferPositives);
            rete.injectNegatives(bufferNegatives);
            bufferPositives.clear();
            bufferNegatives.clear();
        }
        isApplying = false;
    }
}
