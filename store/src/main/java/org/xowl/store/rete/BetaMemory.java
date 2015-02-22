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
import java.util.Map.Entry;

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
    private Map<Token, Map<Quad, Token>> mapAscendants;
    /**
     * The tokens stored in this memory
     */
    private List<Token> tokens;
    /**
     * The children of this node
     */
    private LinkedList<TokenActivable> children;
    /**
     * The binding operations in this node
     */
    private List<Binder> binders;

    /**
     * Initializes this node
     */
    public BetaMemory() {
        mapAscendants = new IdentityHashMap<>();
        children = new LinkedList<>();
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
        Token newToken = buildToken(token, fact);
        for (int i = children.size() - 1; i != -1; i--)
            children.get(i).activateToken(newToken);
    }

    /**
     * Activates this store
     *
     * @param buffer A buffer of couples
     */
    public void activate(Iterator<Couple> buffer) {
        Collection<Token> result = new ArrayList<Token>();
        while (buffer.hasNext()) {
            Couple couple = buffer.next();
            result.add(buildToken(couple.token, couple.fact));
        }
        if (!result.isEmpty())
            for (int i = children.size() - 1; i != -1; i--)
                children.get(i).activateTokens(new FastBuffer<Token>(result));
    }

    /**
     * Builds the token for this store
     *
     * @param token The parent token
     * @param fact  The input fact
     * @return The corresponding token
     */
    private Token buildToken(Token token, Quad fact) {
        Token childToken = new Token(token);
        for (Binder binder : binders)
            binder.execute(childToken, fact);
        Map<Quad, Token> subMap = mapAscendants.get(token);
        if (subMap == null) {
            subMap = new IdentityHashMap<>();
            mapAscendants.put(token, subMap);
        }
        subMap.put(fact, childToken);
        tokens.add(childToken);
        return childToken;
    }

    /**
     * Deactivates this store
     *
     * @param token The parent token
     * @param fact  The input fact
     */
    public void deactivate(Token token, Quad fact) {
        Token newToken = resolveToken(token, fact);
        for (int i = children.size() - 1; i != -1; i--)
            children.get(i).deactivateToken(newToken);
    }

    /**
     * Deactivates this store
     *
     * @param buffer A buffer of couples
     */
    public void deactivate(Iterator<Couple> buffer) {
        Collection<Token> result = new ArrayList<Token>();
        while (buffer.hasNext()) {
            Couple couple = buffer.next();
            result.add(resolveToken(couple.token, couple.fact));
        }
        if (!result.isEmpty())
            for (int i = children.size() - 1; i != -1; i--)
                children.get(i).deactivateTokens(new FastBuffer<Token>(result));
    }

    /**
     * Retrieve the child token in this store corresponding to the specified items
     *
     * @param token The parent token
     * @param fact  The input fact
     * @return The corresponding token in this store
     */
    private Token resolveToken(Token token, Quad fact) {
        Map<Quad, Token> subMap = mapAscendants.get(token);
        Token newToken = subMap.get(fact);
        subMap.remove(fact);
        if (subMap.isEmpty())
            mapAscendants.remove(token);
        tokens.remove(newToken);
        return newToken;
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
            Map<Quad, Token> subMap = mapAscendants.get(base);
            for (Entry<Quad, Token> entry : subMap.entrySet())
                for (Binder binder : binders)
                    binder.execute(entry.getValue(), entry.getKey());
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
