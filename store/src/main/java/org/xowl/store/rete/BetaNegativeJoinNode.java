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
     * The parent alpha memory
     */
    private FactHolder alphaMem;
    /**
     * The parent beta memory
     */
    private TokenHolder betaMem;
    /**
     * The joining tests for this node
     */
    private List<BetaJoinNodeTest> tests;
    /**
     * The child node
     */
    private TokenActivable child;
    /**
     * The current matches in this node
     */
    private Map<Token, List<Quad>> matches;

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
        this.tests = new ArrayList<>(tests);
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
        Collection<TokenActivable> list = new ArrayList<TokenActivable>();
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
    public void activateToken(Token token) {
        for (Quad fact : alphaMem.getFacts())
            applyPositive(token, fact);
        if (!matches.containsKey(token))
            child.activateToken(token);
    }

    @Override
    public void deactivateToken(Token token) {
        matches.remove(token);
        child.deactivateToken(token);
    }

    @Override
    public void activateTokens(Collection<Token> tokens) {
        Iterator<Token> iterator = tokens.iterator();
        while (iterator.hasNext()) {
            Token token = iterator.next();
            for (Quad fact : alphaMem.getFacts())
                applyPositive(token, fact);
            if (matches.containsKey(token))
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
        for (Token token : tokens)
            matches.remove(token);
        child.deactivateTokens(tokens);
    }

    @Override
    public void activateFact(Quad fact) {
        for (Token token : betaMem.getTokens()) {
            applyPositive(token, fact);
            if (matches.containsKey(token))
                child.deactivateToken(token);
        }
    }

    @Override
    public void deactivateFact(Quad fact) {
        for (Token token : betaMem.getTokens()) {
            if (applyNegative(token, fact)) {
                child.activateToken(token);
            }
        }
    }

    @Override
    public void activateFacts(Collection<Quad> facts) {
        List<Token> deactivated = new ArrayList<Token>();
        for (Quad fact : facts) {
            for (Token token : betaMem.getTokens()) {
                applyPositive(token, fact);
                if (matches.containsKey(token))
                    deactivated.add(token);
            }
        }
        int size = deactivated.size();
        if (size != 0) {
            if (size == 1)
                child.deactivateToken(deactivated.get(0));
            else
                child.deactivateTokens(new FastBuffer<Token>(deactivated));
        }
    }

    @Override
    public void deactivateFacts(Collection<Quad> facts) {
        List<Token> reactivated = new ArrayList<Token>();
        for (Quad fact : facts) {
            for (Token token : betaMem.getTokens()) {
                if (applyNegative(token, fact))
                    reactivated.add(token);
            }
        }
        int size = reactivated.size();
        if (size != 0) {
            if (size == 1)
                child.activateToken(reactivated.get(0));
            else
                child.activateTokens(new FastBuffer<Token>(reactivated));
        }
    }

    /**
     * Positively applies the join operation for the specified element
     *
     * @param token A parent token
     * @param fact  An input fact
     */
    private void applyPositive(Token token, Quad fact) {
        for (BetaJoinNodeTest test : tests) {
            if (!test.check(token, fact)) {
                return;
            }
        }
        List<Quad> tsm = matches.get(token);
        if (tsm == null) {
            tsm = new ArrayList<>();
            matches.put(token, tsm);
        }
        tsm.add(fact);
    }

    /**
     * Negatively applies the join operation to the specified element
     *
     * @param token A parent token
     * @param fact  An input fact
     * @return True if the negative join was triggered
     */
    private boolean applyNegative(Token token, Quad fact) {
        List<Quad> tsm = matches.get(token);
        if (tsm == null)
            return false;
        if (!tsm.remove(fact))
            return false;
        if (!tsm.isEmpty())
            return false;
        matches.remove(token);
        return true;
    }
}
