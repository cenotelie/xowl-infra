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

import java.util.*;

/**
 * Represents a negative join node in a RETE network
 *
 * @author Laurent Wouters
 */
class BetaNegativeJoinNode implements TokenHolder, TokenActivable, FactActivable {
    /**
     * Represents a counter of matches
     */
    private static class Counter {
        /**
         * The encapsulated value
         */
        public int value;
    }

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
     * The child node
     */
    private TokenActivable child;
    /**
     * The current matches in this node
     */
    private Map<Token, Counter> matches;

    /**
     * Initializes this node
     *
     * @param alpha The parent alpha memory
     * @param beta  The parent beta memory
     * @param tests The joining tests
     */
    public BetaNegativeJoinNode(FactHolder alpha, TokenHolder beta, Collection<BetaJoinNodeTest> tests) {
        this.alphaMem = alpha;
        this.betaMem = beta;
        Iterator<BetaJoinNodeTest> iter = tests.iterator();
        if (iter.hasNext()) test1 = iter.next();
        if (iter.hasNext()) test2 = iter.next();
        if (iter.hasNext()) test3 = iter.next();
        this.alphaMem.addChild(this);
        this.betaMem.addChild(this);
        this.matches = new IdentityHashMap<>();
    }

    @Override
    public Collection<Token> getTokens() {
        return betaMem.getTokens();
    }

    @Override
    public Collection<TokenActivable> getChildren() {
        Collection<TokenActivable> list = new ArrayList<>();
        if (child != null)
            list.add(child);
        return list;
    }

    @Override
    public void addChild(TokenActivable activable) {
        this.child = activable;
    }

    @Override
    public void removeChild(TokenActivable activable) {
        this.child = null;
    }

    @Override
    public void removeAllChildren() {
        this.child = null;
    }

    @Override
    public void onDestroy() {
        alphaMem.removeChild(this);
        betaMem.removeChild(this);
        child = null;
        matches = null;
    }

    /**
     * Acts on receiving a new activating token
     *
     * @param token An activating token
     * @return Whether to transmit the token to the child
     */
    private boolean onTokenActivated(Token token) {
        Counter counter = new Counter();
        matches.put(token, counter);
        for (Quad fact : alphaMem.getFacts()) {
            if (passTests(token, fact)) {
                counter.value++;
            }
        }
        return (counter.value == 0);
    }

    /**
     * Acts on receiving a deactivating token
     *
     * @param token A deactivating token
     * @return Whether to transmit the token to the child
     */
    private boolean onTokenDeactivated(Token token) {
        Counter counter = matches.remove(token);
        return (counter.value == 0);
    }

    @Override
    public void activateToken(Token token) {
        if (onTokenActivated(token))
            child.activateToken(token);
    }

    @Override
    public void deactivateToken(Token token) {
        if (onTokenDeactivated(token))
            child.deactivateToken(token);
    }

    @Override
    public void activateTokens(Collection<Token> tokens) {
        Iterator<Token> iterator = tokens.iterator();
        while (iterator.hasNext()) {
            if (!onTokenActivated(iterator.next()))
                iterator.remove();
        }
        int size = tokens.size();
        if (size != 0) {
            if (size == 1)
                child.activateToken(tokens.iterator().next());
            else
                child.activateTokens(tokens);
        }
    }

    @Override
    public void deactivateTokens(Collection<Token> tokens) {
        Iterator<Token> iterator = tokens.iterator();
        while (iterator.hasNext()) {
            if (!onTokenDeactivated(iterator.next()))
                iterator.remove();
        }
        int size = tokens.size();
        if (size != 0) {
            if (size == 1)
                child.deactivateToken(tokens.iterator().next());
            else
                child.deactivateTokens(tokens);
        }
    }

    /**
     * Determines whether the specified token an fact pass the tests in this node
     *
     * @param token A token
     * @param fact  A fact
     * @return true if the couple passes the tests
     */
    private boolean passTests(Token token, Quad fact) {
        if (test1 == null) return true;
        if (!test1.check(token, fact)) return false;
        if (test2 == null) return true;
        if (!test2.check(token, fact)) return false;
        if (test3 == null) return true;
        return test3.check(token, fact);
    }

    /**
     * Acts on receiving a new activating fact
     *
     * @param fact         An activating fact
     * @param toDeactivate The buffer of token that will be subsequently deactivated
     */
    private void onFactActivated(Quad fact, Collection<Token> toDeactivate) {
        for (Map.Entry<Token, Counter> entry : matches.entrySet()) {
            if (passTests(entry.getKey(), fact)) {
                entry.getValue().value++;
                if (entry.getValue().value == 1) {
                    // was 0, we should deactivate the token
                    toDeactivate.add(entry.getKey());
                }
            }
        }
    }

    /**
     * Acts on receiving a deactivating fact
     *
     * @param fact       A deactivating fact
     * @param toActivate The buffer of token that will be subsequently activated
     */
    private void onFactDeactivated(Quad fact, Collection<Token> toActivate) {
        for (Map.Entry<Token, Counter> entry : matches.entrySet()) {
            if (passTests(entry.getKey(), fact)) {
                entry.getValue().value--;
                if (entry.getValue().value == 0) {
                    // now 0, we should activate the token
                    toActivate.add(entry.getKey());
                }
            }
        }
    }

    @Override
    public void activateFact(Quad fact) {
        ArrayList<Token> toDeactivate = new ArrayList<>();
        onFactActivated(fact, toDeactivate);
        if (toDeactivate.size() > 0) {
            if (toDeactivate.size() == 1) {
                child.deactivateToken(toDeactivate.get(0));
            } else {
                child.deactivateTokens(new FastBuffer<>(toDeactivate));
            }
        }
    }

    @Override
    public void deactivateFact(Quad fact) {
        ArrayList<Token> toActivate = new ArrayList<>();
        onFactDeactivated(fact, toActivate);
        if (toActivate.size() > 0) {
            if (toActivate.size() == 1) {
                child.activateToken(toActivate.get(0));
            } else {
                child.activateTokens(new FastBuffer<>(toActivate));
            }
        }
    }

    @Override
    public void activateFacts(Collection<Quad> facts) {
        ArrayList<Token> toDeactivate = new ArrayList<>();
        for (Quad fact : facts)
            onFactActivated(fact, toDeactivate);
        if (toDeactivate.size() > 0) {
            if (toDeactivate.size() == 1) {
                child.deactivateToken(toDeactivate.get(0));
            } else {
                child.deactivateTokens(new FastBuffer<>(toDeactivate));
            }
        }
    }

    @Override
    public void deactivateFacts(Collection<Quad> facts) {
        ArrayList<Token> toActivate = new ArrayList<>();
        for (Quad fact : facts)
            onFactDeactivated(fact, toActivate);
        if (toActivate.size() > 0) {
            if (toActivate.size() == 1) {
                child.activateToken(toActivate.get(0));
            } else {
                child.activateTokens(new FastBuffer<>(toActivate));
            }
        }
    }


}
