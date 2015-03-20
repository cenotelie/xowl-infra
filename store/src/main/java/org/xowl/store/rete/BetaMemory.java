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
 * Represents a beta memory in a RETE network
 *
 * @author Laurent Wouters
 */
class BetaMemory implements TokenHolder {
    /**
     * A dummy beta memory
     */
    private static BetaMemory dummy;
    /**
     * The ascendants of this node
     */
    private Map<Token, List<Couple>> mapAscendants;
    /**
     * The tokens stored in this memory
     */
    private List<Token> tokens;
    /**
     * The children of this node
     */
    private List<TokenActivable> children;
    /**
     * The binding operations in this node
     */
    private List<Binder> binders;

    /**
     * Initializes this node
     */
    public BetaMemory() {
        mapAscendants = new IdentityHashMap<>();
        children = new ArrayList<>();
        tokens = new ArrayList<>();
        binders = new ArrayList<>();
    }

    /**
     * Gets a dummy beta memory
     *
     * @return A dummy beta memory
     */
    public static synchronized BetaMemory getDummy() {
        if (dummy != null)
            return dummy;
        dummy = new BetaMemory();
        dummy.tokens.add(new Token(null));
        return dummy;
    }

    @Override
    public Collection<Token> getTokens() {
        return tokens;
    }

    @Override
    public Collection<TokenActivable> getChildren() {
        return children;
    }

    @Override
    public void addChild(TokenActivable activable) {
        children.add(activable);
    }

    @Override
    public void removeChild(TokenActivable activable) {
        children.remove(activable);
    }

    @Override
    public void removeAllChildren() {
        children.clear();
    }

    @Override
    public void onDestroy() {
        mapAscendants = null;
        tokens = null;
        children = null;
    }

    /**
     * Adds the specified collection of binding operation to this node
     *
     * @param binders A collection of binding operations
     */
    public void addBinders(Collection<Binder> binders) {
        this.binders.addAll(binders);
        updateTokens(binders);
    }

    /**
     * Removes the specified binding operations from this node
     *
     * @param binders A collection of binding operations
     */
    public void removeBinders(Collection<Binder> binders) {
        this.binders.removeAll(binders);
    }

    /**
     * Activates this store
     *
     * @param token The parent token
     * @param fact  The input fact
     */
    public void activate(Token token, Quad fact) {
        Token newToken = buildToken(new Couple(fact, token));
        for (int i = children.size() - 1; i != -1; i--)
            children.get(i).activateToken(newToken);
    }

    /**
     * Activates this store
     *
     * @param buffer A buffer of couples
     */
    public void activate(Iterator<Couple> buffer) {
        Collection<Token> result = new ArrayList<>();
        while (buffer.hasNext()) {
            Couple couple = buffer.next();
            result.add(buildToken(couple));
        }
        if (!result.isEmpty())
            for (int i = children.size() - 1; i != -1; i--)
                children.get(i).activateTokens(new FastBuffer<>(result));
    }

    /**
     * Builds the token for this store
     *
     * @param couple The couple to build from
     * @return The corresponding token
     */
    private Token buildToken(Couple couple) {
        Token childToken = new Token(couple.token);
        for (Binder binder : binders)
            binder.execute(childToken, couple.fact);
        List<Couple> sub = mapAscendants.get(couple.token);
        if (sub == null) {
            sub = new ArrayList<>();
            mapAscendants.put(couple.token, sub);
        }
        sub.add(new Couple(couple.fact, childToken));
        tokens.add(childToken);
        return childToken;
    }

    /**
     * Deactivates on the specified token
     *
     * @param token A token
     */
    public void deactivateToken(Token token) {
        List<Couple> couples = mapAscendants.remove(token);
        if (couples != null) {
            Collection<Token> buffer = new ArrayList<>();
            for (Couple couple : couples) {
                buffer.add(couple.token);
                tokens.remove(couple.token);
            }
            if (!buffer.isEmpty())
                for (int i = children.size() - 1; i != -1; i--)
                    children.get(i).deactivateTokens(new FastBuffer<>(buffer));
        }
    }

    /**
     * Deactivates on the specified token and fact
     *
     * @param token A token
     * @param fact  A fact
     */
    public void deactivateCouple(Token token, Quad fact) {
        List<Couple> sub = mapAscendants.get(token);
        for (Couple couple : sub) {
            if (couple.fact == fact) {
                tokens.remove(couple.token);
                for (int i = children.size() - 1; i != -1; i--)
                    children.get(i).deactivateToken(couple.token);
                break;
            }
        }
        if (sub.isEmpty())
            mapAscendants.remove(token);
    }

    /**
     * Deactivates on the specified tokens
     *
     * @param tokens Some tokens
     */
    public void deactivateTokens(Collection<Token> tokens) {
        Collection<Token> buffer = new ArrayList<>();
        for (Token token : tokens) {
            List<Couple> couples = mapAscendants.remove(token);
            if (couples != null) {
                for (Couple couple : couples) {
                    buffer.add(couple.token);
                    tokens.remove(couple.token);
                }
            }
        }
        if (!buffer.isEmpty())
            for (int i = children.size() - 1; i != -1; i--)
                children.get(i).deactivateTokens(new FastBuffer<>(buffer));
    }

    /**
     * Deactivates on the specified couples
     *
     * @param couples Some couples
     */
    public void deactivateCouples(Iterator<Couple> couples) {
        Collection<Token> buffer = new ArrayList<>();
        while (couples.hasNext()) {
            Couple couple = couples.next();
            List<Couple> sub = mapAscendants.get(couple.token);
            if (sub != null) {
                for (Couple potential : sub) {
                    if (potential.fact == couple.fact) {
                        buffer.add(potential.token);
                        tokens.remove(potential.token);
                    }
                }
                if (sub.isEmpty())
                    mapAscendants.remove(couple.token);
            }
        }
        if (!buffer.isEmpty())
            for (int i = children.size() - 1; i != -1; i--)
                children.get(i).deactivateTokens(new FastBuffer<>(buffer));
    }

    /**
     * Pushes tokens in this store down to its children
     */
    public void push() {
        int size = tokens.size();
        if (size != 0) {
            if (size == 1) {
                Token token = tokens.get(0);
                for (int i = children.size() - 1; i != -1; i--)
                    children.get(i).activateToken(token);
            } else {
                for (int i = children.size() - 1; i != -1; i--)
                    children.get(i).activateTokens(new FastBuffer<>(tokens));
            }
        }
    }

    /**
     * Updates the tokens in this store with new binding operations
     *
     * @param binders A set of binding operations
     */
    private void updateTokens(Collection<Binder> binders) {
        for (Token base : mapAscendants.keySet()) {
            for (Couple couple : mapAscendants.get(base))
                for (Binder binder : binders)
                    binder.execute(couple.token, couple.fact);
        }
    }

    /**
     * Resolves a join node, child of this store
     *
     * @param alpha The corresponding alpha memory
     * @param tests The set of tests to match
     * @return The corresponding join node
     */
    public BetaJoinNode resolveJoin(FactHolder alpha, Collection<BetaJoinNodeTest> tests) {
        for (int i = children.size() - 1; i != -1; i--) {
            if (children.get(i) instanceof BetaJoinNode) {
                BetaJoinNode join = (BetaJoinNode) children.get(i);
                if (join.match(alpha, tests)) {
                    join.counter++;
                    return join;
                }
            }
        }
        return new BetaJoinNode(alpha, this, tests);
    }
}
