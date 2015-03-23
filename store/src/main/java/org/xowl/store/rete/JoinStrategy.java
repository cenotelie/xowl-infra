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
import java.util.Iterator;

/**
 * Represents a join strategy in a RETE network
 *
 * @author Laurent Wouters
 */
abstract class JoinStrategy {
    /**
     * The first test
     */
    protected BetaJoinNodeTest test1;
    /**
     * The second test
     */
    protected BetaJoinNodeTest test2;
    /**
     * The third test
     */
    protected BetaJoinNodeTest test3;
    /**
     * The fourth test
     */
    protected BetaJoinNodeTest test4;

    /**
     * Initializes this strategy
     *
     * @param test1 The first test
     * @param test2 The second test
     * @param test3 The third test
     * @param test4 The fourth test
     */
    public JoinStrategy(BetaJoinNodeTest test1, BetaJoinNodeTest test2, BetaJoinNodeTest test3, BetaJoinNodeTest test4) {
        this.test1 = test1;
        this.test2 = test2;
        this.test3 = test3;
        this.test4 = test4;
    }

    /**
     * Determines whether the specified couple passes the tests
     *
     * @param token A token
     * @param fact  A fact
     * @return true if the couple passes the tests
     */
    protected boolean passTests(Token token, Quad fact) {
        return ((test1 == null || test1.check(token, fact))
                && (test2 == null || test2.check(token, fact))
                && (test3 == null || test3.check(token, fact))
                && (test4 == null || test4.check(token, fact)));
    }

    /**
     * Joins the token and fact collections
     *
     * @param tokens A collection of tokens
     * @param facts  A collection of facts
     * @return The result
     */
    public abstract Iterator<Couple> join(Collection<Token> tokens, Collection<Quad> facts);
}
