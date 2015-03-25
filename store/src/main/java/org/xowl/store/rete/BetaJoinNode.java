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
import org.xowl.store.rdf.Quad;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Represents a join node in the beta graph of a RETE network
 *
 * @author Laurent Wouters
 */
class BetaJoinNode extends JoinBase implements FactActivable, TokenActivable {
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
     * The downstream beta memory
     */
    private BetaMemory child;

    /**
     * Initializes this join node
     *
     * @param alpha   The upstream alpha memory
     * @param beta    The upstream beta memory
     * @param tests   The joining tests
     * @param binders The binding operations
     */
    public BetaJoinNode(FactHolder alpha, TokenHolder beta, List<JoinTest> tests, Collection<Binder> binders) {
        super(tests.size() > 0 ? tests.get(0) : null,
                tests.size() > 1 ? tests.get(1) : null,
                tests.size() > 2 ? tests.get(2) : null,
                tests.size() > 3 ? tests.get(3) : null);
        this.alphaMem = alpha;
        this.betaMem = beta;
        this.alphaMem.addChild(this);
        this.betaMem.addChild(this);
        this.child = new BetaMemory(binders);
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
     * Prepares this node for its destruction
     */
    public void onDestroy() {
        alphaMem.removeChild(this);
        betaMem.removeChild(this);
        child = null;
    }

    /**
     * Determines whether this node matches the provided specifications
     *
     * @param alpha The upstream alpha memory to look for
     * @param tests The tests to look for
     * @return true if this node matches the provided specifications
     */
    public boolean match(FactHolder alpha, List<JoinTest> tests) {
        if (this.alphaMem != alpha)
            return false;
        switch (tests.size()) {
            case 0:
                return (test1 == null);
            case 1:
                return (test2 == null && tests.contains(test1));
            case 2:
                return (test3 == null && tests.contains(test1) && tests.contains(test2));
            case 3:
                return (test4 == null && tests.contains(test1) && tests.contains(test2) && tests.contains(test3));
            case 4:
                return (tests.contains(test1) && tests.contains(test2) && tests.contains(test3) && tests.contains(test4));
            default:
                return false;
        }
    }

    @Override
    public void activateToken(Token t) {
        for (Quad fact : alphaMem.getFacts())
            if (passTests(t, fact))
                child.activate(t, fact);
    }

    @Override
    public void deactivateToken(Token t) {
        child.deactivateToken(t);
    }

    @Override
    public void activateTokens(Collection<Token> tokens) {
        if (!alphaMem.getFacts().isEmpty())
            child.activate(getJoin(tokens, alphaMem.getFacts()));
    }

    @Override
    public void deactivateTokens(Collection<Token> tokens) {
        child.deactivateTokens(tokens);
    }

    @Override
    public void activateFact(Quad fact) {
        for (Token t : betaMem.getTokens())
            if (passTests(t, fact))
                child.activate(t, fact);
    }

    @Override
    public void deactivateFact(Quad fact) {
        for (Token t : betaMem.getTokens())
            if (passTests(t, fact))
                child.deactivateCouple(t, fact);
    }

    @Override
    public void activateFacts(Collection<Quad> facts) {
        if (!betaMem.getTokens().isEmpty())
            child.activate(getJoin(betaMem.getTokens(), facts));
    }

    @Override
    public void deactivateFacts(Collection<Quad> facts) {
        if (!betaMem.getTokens().isEmpty())
            child.deactivateCouples(getJoin(betaMem.getTokens(), facts));
    }

    /**
     * Gets an iterator over the couples representing the join operation of the specified tokens and facts
     *
     * @param tokens A collection of tokens
     * @param facts  A collection of facts
     * @return An iterator over the joined couples
     */
    private Iterator<Couple> getJoin(Collection<Token> tokens, Collection<Quad> facts) {
        int size = tokens.size() * facts.size();
        JoinStrategy join;
        if (size <= MAX_SIZE_JOIN_LOOPS)
            join = new NestedLoopJoin(test1, test2, test3, test4);
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
        return new GraceHashJoin<Token, Quad>(test1, test2, test3, test4) {
            @Override
            protected Couple createCouple(Token left, Quad right) {
                return new Couple(right, left);
            }

            @Override
            protected Node getValueForLeft(Token left, JoinTest test) {
                return test.getIndex(left);
            }

            @Override
            protected Node getValueForRight(Quad right, JoinTest test) {
                return test.getIndex(right);
            }

            @Override
            public Iterator<Couple> join(Collection<Token> tokens, Collection<Quad> facts) {
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
        return new SimpleHashJoin<Token, Quad>(test1, test2, test3, test4) {
            @Override
            protected Couple createCouple(Token left, Quad right) {
                return new Couple(right, left);
            }

            @Override
            protected Node getValueForLeft(Token left, JoinTest test) {
                return test.getIndex(left);
            }

            @Override
            protected Node getValueForRight(Quad right, JoinTest test) {
                return test.getIndex(right);
            }

            @Override
            public Iterator<Couple> join(Collection<Token> tokens, Collection<Quad> facts) {
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
        return new SimpleHashJoin<Quad, Token>(test1, test2, test3, test4) {
            @Override
            protected Couple createCouple(Quad left, Token right) {
                return new Couple(left, right);
            }

            @Override
            protected Node getValueForLeft(Quad left, JoinTest test) {
                return test.getIndex(left);
            }

            @Override
            protected Node getValueForRight(Token right, JoinTest test) {
                return test.getIndex(right);
            }

            @Override
            public Iterator<Couple> join(Collection<Token> tokens, Collection<Quad> facts) {
                return joinGenerics(facts, tokens);
            }
        };
    }
}
