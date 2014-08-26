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

import org.xowl.store.rdf.Node;
import org.xowl.store.rdf.Triple;

import java.util.Collection;
import java.util.Iterator;

/**
 * Represents a join node in the beta graph of a RETE network
 *
 * @author Laurent Wouters
 */
public class BetaJoinNode implements FactActivable, TokenActivable {
    /**
     * The maximal size of a join for which the nested loop strategy should be used
     */
    private static final int MAX_SIZE_JOIN_LOOPS = 10;
    /**
     * The maximal size of a join for which the double hash strategy should be used
     */
    private static final int MAX_SIZE_JOIN_DOUBLE_HASH = 10000;

    /**
     * The associated upstream alpha memory
     */
    private FactHolder alphaMem;
    /**
     * The associated upstream beta memory
     */
    private TokenHolder betaMem;
    /**
     * The first test
     */
    private BetaJoinNodeTest test1;
    /**
     * The second test
     */
    private BetaJoinNodeTest test2;
    /**
     * The third test
     */
    private BetaJoinNodeTest test3;
    /**
     * The downstream beta memory
     */
    private BetaMemory child;

    /**
     * Initializes this joi node
     *
     * @param alpha The upstream alpha memory
     * @param beta  The upstream beta memory
     * @param tests The tests on this node
     */
    public BetaJoinNode(FactHolder alpha, TokenHolder beta, Collection<BetaJoinNodeTest> tests) {
        this.alphaMem = alpha;
        this.betaMem = beta;
        Iterator<BetaJoinNodeTest> iter = tests.iterator();
        if (iter.hasNext()) test1 = iter.next();
        if (iter.hasNext()) test2 = iter.next();
        if (iter.hasNext()) test3 = iter.next();
        this.alphaMem.addChild(this);
        this.betaMem.addChild(this);
    }

    /**
     * Gets the downstream beta memory of this node
     *
     * @return The downstream beta memory
     */
    public BetaMemory getChild() {
        return child;
    }

    /**
     * Determines whether this node matches the provided specifications
     *
     * @param alpha The upstream alpha memory to look for
     * @param tests The tests to look for
     * @return true if this node matches the provided specifications
     */
    public boolean match(FactHolder alpha, Collection<BetaJoinNodeTest> tests) {
        if (this.alphaMem != alpha)
            return false;
        if (this.test1 != null) {
            if (!tests.contains(this.test1))
                return false;
            else if (this.test2 != null) {
                if (!tests.contains(this.test2))
                    return false;
                else if (this.test3 != null)
                    return tests.contains(this.test3);
                return (tests.size() == 2);
            }
            return (tests.size() == 1);
        }
        return (tests.isEmpty());
    }

    @Override
    public void activateToken(Token t) {
        for (Triple fact : alphaMem.getFacts())
            if (passTests(t, fact))
                child.activate(t, fact);
    }

    @Override
    public void deactivateToken(Token t) {
        for (Triple fact : alphaMem.getFacts())
            if (passTests(t, fact))
                child.deactivate(t, fact);
    }

    @Override
    public void activateTokens(Collection<Token> tokens) {
        if (!alphaMem.getFacts().isEmpty())
            child.activate(getJoin(tokens, alphaMem.getFacts()));
    }

    @Override
    public void deactivateTokens(Collection<Token> tokens) {
        if (!alphaMem.getFacts().isEmpty())
            child.deactivate(getJoin(tokens, alphaMem.getFacts()));
    }

    @Override
    public void activateFact(Triple fact) {
        for (Token t : betaMem.getTokens())
            if (passTests(t, fact))
                child.activate(t, fact);
    }

    @Override
    public void deactivateFact(Triple fact) {
        for (Token t : betaMem.getTokens())
            if (passTests(t, fact))
                child.deactivate(t, fact);
    }

    @Override
    public void activateFacts(Collection<Triple> facts) {
        if (!betaMem.getTokens().isEmpty())
            child.activate(getJoin(betaMem.getTokens(), facts));
    }

    @Override
    public void deactivateFacts(Collection<Triple> facts) {
        if (!betaMem.getTokens().isEmpty())
            child.deactivate(getJoin(betaMem.getTokens(), facts));
    }

    /**
     * Gets an iterator over the couples representing the join operation of the specified tokens and facts
     *
     * @param tokens A collection of tokens
     * @param facts  A collection of facts
     * @return An iterator over the joined couples
     */
    private Iterator<Couple> getJoin(Collection<Token> tokens, Collection<Triple> facts) {
        int size = tokens.size() * facts.size();
        JoinStrategy join = null;
        if (size <= MAX_SIZE_JOIN_LOOPS)
            join = new NestedLoopJoin(test1, test2, test3);
        else if (size <= MAX_SIZE_JOIN_DOUBLE_HASH)
            join = createDoubeHashJoin();
        else if (facts.size() < tokens.size())
            join = createSimpleHashJoinFact();
        else
            join = createSimpleHashJoinToken();
        return join.join(tokens, facts);
    }

    /**
     * Creates a double hash join strategy
     *
     * @return A double hash join strategy
     */
    private JoinStrategy createDoubeHashJoin() {
        return new GraceHashJoin<Token, Triple>(test1, test2, test3) {
            @Override
            protected Couple createCouple(Token left, Triple right) {
                return new Couple(right, left);
            }

            @Override
            protected Node getValueForLeft(Token left, BetaJoinNodeTest test) {
                return left.getBinding(test.getVariable());
            }

            @Override
            protected Node getValueForRight(Triple right, BetaJoinNodeTest test) {
                return right.getField(test.getField());
            }

            @Override
            public Iterator<Couple> join(Collection<Token> tokens, Collection<Triple> facts) {
                return joinGenerics(tokens, facts);
            }
        };
    }

    /**
     * Creates a simple hash join strategy hashing tokens
     *
     * @return A simple hash join strategy
     */
    private JoinStrategy createSimpleHashJoinToken() {
        return new SimpleHashJoin<Token, Triple>(test1, test2, test3) {
            @Override
            protected Couple createCouple(Token left, Triple right) {
                return new Couple(right, left);
            }

            @Override
            protected Node getValueForLeft(Token left, BetaJoinNodeTest test) {
                return left.getBinding(test.getVariable());
            }

            @Override
            protected Node getValueForRight(Triple right, BetaJoinNodeTest test) {
                return right.getField(test.getField());
            }

            @Override
            public Iterator<Couple> join(Collection<Token> tokens, Collection<Triple> facts) {
                return joinGenerics(tokens, facts);
            }
        };
    }

    /**
     * Creates a simple hash join strategy hashing triples
     *
     * @return A simple hash join strategy
     */
    private JoinStrategy createSimpleHashJoinFact() {
        return new SimpleHashJoin<Triple, Token>(test1, test2, test3) {
            @Override
            protected Couple createCouple(Triple left, Token right) {
                return new Couple(left, right);
            }

            @Override
            protected Node getValueForLeft(Triple left, BetaJoinNodeTest test) {
                return left.getField(test.getField());
            }

            @Override
            protected Node getValueForRight(Token right, BetaJoinNodeTest test) {
                return right.getBinding(test.getVariable());
            }

            @Override
            public Iterator<Couple> join(Collection<Token> tokens, Collection<Triple> facts) {
                return joinGenerics(facts, tokens);
            }
        };
    }

    /**
     * Determines whether the specified token an fact pass the tests in this node
     *
     * @param token A token
     * @param fact  A fact
     * @return true if the couple passes the tests
     */
    private boolean passTests(Token token, Triple fact) {
        if (test1 == null) return true;
        if (!test1.check(token, fact)) return false;
        if (test2 == null) return true;
        if (!test2.check(token, fact)) return false;
        if (test3 == null) return true;
        return test3.check(token, fact);
    }

    /**
     * Resolves the downstream memory of this node with the specified binding operations
     *
     * @param binders The binding operations
     * @return The downstream memory
     */
    public BetaMemory resolveMemory(Collection<Binder> binders) {
        if (child != null) {
            child.addBinders(binders);
            return child;
        }
        child = new BetaMemory();
        child.addBinders(binders);
        if (!alphaMem.getFacts().isEmpty())
            activateFacts(alphaMem.getFacts());
        return child;
    }
}
