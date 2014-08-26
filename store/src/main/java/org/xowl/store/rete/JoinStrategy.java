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

import org.xowl.store.rdf.Triple;

import java.util.Collection;
import java.util.Iterator;

/**
 * Represents a join strategy in a RETE network
 *
 * @author Laurent Wouters
 */
public abstract class JoinStrategy {
    protected BetaJoinNodeTest test1;
    protected BetaJoinNodeTest test2;
    protected BetaJoinNodeTest test3;

    /**
     * Initializes this strategy
     *
     * @param t1 The first test
     * @param t2 The second test
     * @param t3 The third test
     */
    public JoinStrategy(BetaJoinNodeTest t1, BetaJoinNodeTest t2, BetaJoinNodeTest t3) {
        this.test1 = t1;
        this.test2 = t2;
        this.test3 = t3;
    }

    /**
     * Determines whether the specified couple passes the tests
     *
     * @param token A token
     * @param fact  A fact
     * @return true if the couple passes the tests
     */
    protected boolean passTests(Token token, Triple fact) {
        if (test1 == null) return true;
        if (!test1.check(token, fact)) return false;
        if (test2 == null) return true;
        if (!test2.check(token, fact)) return false;
        if (test3 == null) return true;
        return test3.check(token, fact);
    }

    /**
     * Joins the token and fact collections
     *
     * @param tokens A collection of tokens
     * @param facts  A collection of facts
     * @return The result
     */
    public abstract Iterator<Couple> join(Collection<Token> tokens, Collection<Triple> facts);
}
