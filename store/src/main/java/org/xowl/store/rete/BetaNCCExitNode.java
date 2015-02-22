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

import java.util.*;

/**
 * Represents an exit node for a sub-graph in a RETE network matching a negative conjunction of conditions
 *
 * @author Laurent Wouters
 */
class BetaNCCExitNode implements TokenHolder, TokenActivable {
    /**
     * Flag whether a trasaction is in action
     */
    private boolean isInTransaction;
    /**
     * The parent beta memory
     */
    private TokenHolder betaMem;
    /**
     * The child node
     */
    private TokenActivable child;
    /**
     * Cache of the current matches
     */
    private Map<Token, List<Token>> matches;

    /**
     * Initializes this node
     *
     * @param beta The parent beta memory
     */
    public BetaNCCExitNode(TokenHolder beta) {
        this.isInTransaction = false;
        this.betaMem = beta;
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
        // The child will be dropped in every case before this method is called.
        // We do not have to unregister from the parents because we are the end of a NCC network.
        child = null;
        matches = null;
    }

    @Override
    public void activateToken(Token token) {
        Token original = getOriginal(token);
        matches.get(original).add(token);
        if (!isInTransaction)
            child.deactivateToken(original);
    }

    @Override
    public void deactivateToken(Token token) {
        Token original = getOriginal(token);
        List<Token> sm = matches.get(original);
        sm.remove(token);
        if (!isInTransaction && sm.isEmpty())
            child.activateToken(original);
    }

    @Override
    public void activateTokens(Collection<Token> tokens) {
        Token[] buffer = new Token[tokens.size()];
        int i = 0;
        for (Token token : tokens) {
            Token original = getOriginal(token);
            matches.get(original).add(token);
            buffer[i] = original;
            i++;
        }
        if (!isInTransaction)
            child.deactivateTokens(new FastBuffer<>(buffer));
    }

    @Override
    public void deactivateTokens(Collection<Token> tokens) {
        Collection<Token> buffer = new ArrayList<>();
        for (Token token : tokens) {
            Token original = getOriginal(token);
            List<Token> sm = matches.get(original);
            sm.remove(token);
            if (!isInTransaction && sm.isEmpty())
                buffer.add(original);
        }
        if (!buffer.isEmpty())
            child.activateTokens(new FastBuffer<>(buffer));
    }

    /**
     * Prepares for the incoming inputs on the specified parent token
     *
     * @param token A parent token
     */
    public void preActivation(Token token) {
        matches.put(token, new ArrayList<Token>());
        isInTransaction = true;
    }

    /**
     * Finalizes the transaction on the specified parent token
     *
     * @param token A parent token
     */
    public void postActivation(Token token) {
        isInTransaction = false;
        if (matches.get(token).isEmpty())
            child.activateToken(token);
    }

    /**
     * Prepares for the incoming inputs on the specified parent tokens
     *
     * @param tokens A collection of parent token
     */
    public void preActivation(Collection<Token> tokens) {
        for (Token token : tokens)
            matches.put(token, new ArrayList<Token>());
        isInTransaction = true;
    }

    /**
     * Finalizes the transaction on the specified parent tokens
     *
     * @param tokens A collection of parent token
     */
    public void postActivation(Collection<Token> tokens) {
        isInTransaction = false;
        Iterator<Token> iterator = tokens.iterator();
        while (iterator.hasNext()) {
            Token token = iterator.next();
            if (!matches.get(token).isEmpty())
                iterator.remove();
        }
        if (!tokens.isEmpty())
            child.activateTokens(tokens);
    }

    /**
     * Prepares for the incoming negative inputs
     */
    public void preDeactivation() {
        isInTransaction = true;
    }

    /**
     * Finalizes the transaction on the specified parent token
     *
     * @param token A parent token
     */
    public void postDeactivation(Token token) {
        matches.remove(token);
        child.deactivateToken(token);
    }

    /**
     * Finalizes the transaction on the specified parent tokens
     *
     * @param tokens A collection of parent token
     */
    public void postDeactivation(Collection<Token> tokens) {
        for (Token token : tokens)
            matches.remove(token);
        child.deactivateTokens(tokens);
    }

    /**
     * Gets the original token corresponding to the specified token
     *
     * @param token A token
     * @return The corresponding original token
     */
    private Token getOriginal(Token token) {
        while (token != null) {
            if (matches.containsKey(token))
                return token;
            token = token.getParent();
        }
        return null;
    }
}
