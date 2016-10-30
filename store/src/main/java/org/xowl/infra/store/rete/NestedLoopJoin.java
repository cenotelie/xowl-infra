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

import java.util.Collection;
import java.util.Iterator;

/**
 * Implements the nested loop joining strategy
 *
 * @author Laurent Wouters
 */
class NestedLoopJoin extends JoinStrategy implements Iterator<JoinMatch> {
    /**
     * The tokens to join
     */
    private Collection<Token> tokens;
    /**
     * Iterator over the facts
     */
    private Iterator<Quad> iterFacts;
    /**
     * Iterator over the tokens
     */
    private Iterator<Token> iterTokens;
    /**
     * The next fact to inspect
     */
    private Quad nextFact;
    /**
     * The next resulting couple
     */
    private JoinMatch nextJoinMatch = null;

    /**
     * Initializes this strategy
     *
     * @param test1 The first test
     * @param test2 The second test
     * @param test3 The third test
     * @param test4 The fourth test
     */
    public NestedLoopJoin(JoinTest test1, JoinTest test2, JoinTest test3, JoinTest test4) {
        super(test1, test2, test3, test4);
    }

    @Override
    public Iterator<JoinMatch> join(Collection<Token> tokens, Collection<Quad> facts) {
        this.tokens = tokens;
        this.iterFacts = facts.iterator();
        this.iterTokens = tokens.iterator();
        this.nextFact = this.iterFacts.next();
        this.nextJoinMatch = findNext();
        return this;
    }

    @Override
    public boolean hasNext() {
        return (nextJoinMatch != null);
    }

    @Override
    public JoinMatch next() {
        JoinMatch result = nextJoinMatch;
        nextJoinMatch = findNext();
        return result;
    }

    @Override
    public void remove() {
    }

    /**
     * Computes the next couple in this iterator
     *
     * @return The next couple
     */
    private JoinMatch findNext() {
        while (iterTokens != null) {
            Token currentToken = iterTokens.next();
            Quad currentFact = nextFact;
            // Advance iterators
            if (!iterTokens.hasNext()) {
                // no more tokens => go to next fact
                if (!iterFacts.hasNext()) {
                    // no more facts => set token iterator to null
                    iterTokens = null;
                } else {
                    // go to next fact and reset token iterator
                    nextFact = iterFacts.next();
                    iterTokens = tokens.iterator();
                }
            }
            // test values
            if (passTests(currentToken, currentFact))
                return new JoinMatch(currentFact, currentToken);
        }
        return null;
    }
}
