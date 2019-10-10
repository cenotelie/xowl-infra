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

import fr.cenotelie.commons.utils.collections.FastBuffer;

import java.util.*;

/**
 * Represents an exit node for a sub-graph in a RETE network matching a negative conjunction of conditions
 *
 * @author Laurent Wouters
 */
class BetaNCCExitNode implements TokenHolder, TokenActivable {
    /**
     * The parent beta memory
     */
    private final TokenHolder betaMem;
    /**
     * The number of hops in the NCC
     */
    private final int hop;
    /**
     * Flag whether a transaction is in action
     */
    private boolean isInTransaction;
    /**
     * The child node
     */
    private TokenActivable child;
    /**
     * Cache of the current data
     */
    private Map<Token, Data> tokenData;
    /**
     * Initializes this node
     *
     * @param beta The parent beta memory
     * @param hop  The number of hops in the NCC
     */
    public BetaNCCExitNode(TokenHolder beta, int hop) {
        this.isInTransaction = false;
        this.betaMem = beta;
        this.tokenData = new IdentityHashMap<>();
        this.hop = hop;
    }

    @Override
    public Collection<Token> getTokens() {
        return betaMem.getTokens();
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
    public void onDestroy() {
        // The child will be dropped in every case before this method is called.
        // We do not have to unregister from the parents because we are the end of a NCC network.
        child = null;
        tokenData = null;
    }

    @Override
    public void activateToken(Token token) {
        Token original = getOriginal(token);
        Data data = tokenData.get(original);
        data.counter++;
        if (!isInTransaction && data.isOriginalFired) {
            data.isOriginalFired = false;
            child.deactivateToken(original);
        }
    }

    @Override
    public void deactivateToken(Token token) {
        Token original = getOriginal(token);
        Data data = tokenData.get(original);
        data.counter--;
        if (!isInTransaction && data.counter == 0) {
            data.isOriginalFired = true;
            child.activateToken(original);
        }
    }

    @Override
    public void activateTokens(Collection<Token> tokens) {
        Collection<Token> buffer = (!isInTransaction) ? new ArrayList<>(tokens.size()) : null;
        for (Token token : tokens) {
            Token original = getOriginal(token);
            Data data = tokenData.get(original);
            data.counter++;
            if (!isInTransaction && data.isOriginalFired) {
                data.isOriginalFired = false;
                buffer.add(original);
            }
        }
        if (!isInTransaction && !buffer.isEmpty())
            child.deactivateTokens(new FastBuffer<>(buffer));
    }

    @Override
    public void deactivateTokens(Collection<Token> tokens) {
        Collection<Token> buffer = (!isInTransaction) ? new ArrayList<>(tokens.size()) : null;
        for (Token token : tokens) {
            Token original = getOriginal(token);
            Data data = tokenData.get(original);
            data.counter--;
            if (!isInTransaction && data.counter == 0) {
                data.isOriginalFired = true;
                buffer.add(original);
            }
        }
        if (!isInTransaction && !buffer.isEmpty())
            child.activateTokens(new FastBuffer<>(buffer));
    }

    /**
     * Prepares for the incoming inputs on the specified parent token
     *
     * @param token A parent token
     */
    public void preActivation(Token token) {
        tokenData.put(token, new Data());
        isInTransaction = true;
    }

    /**
     * Finalizes the transaction on the specified parent token
     *
     * @param token A parent token
     */
    public void postActivation(Token token) {
        isInTransaction = false;
        Data data = tokenData.get(token);
        if (data.counter == 0) {
            data.isOriginalFired = true;
            child.activateToken(token);
        }
    }

    /**
     * Prepares for the incoming inputs on the specified parent tokens
     *
     * @param tokens A collection of parent token
     */
    public void preActivation(Collection<Token> tokens) {
        for (Token token : tokens)
            tokenData.put(token, new Data());
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
            Data data = tokenData.get(token);
            if (data.counter > 0)
                iterator.remove();
            else
                data.isOriginalFired = true;
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
        isInTransaction = false;
        Data data = tokenData.remove(token);
        if (data.isOriginalFired)
            child.deactivateToken(token);
    }

    /**
     * Finalizes the transaction on the specified parent tokens
     *
     * @param tokens A collection of parent token
     */
    public void postDeactivation(Collection<Token> tokens) {
        isInTransaction = false;
        Iterator<Token> iterator = tokens.iterator();
        while (iterator.hasNext()) {
            Token token = iterator.next();
            Data data = tokenData.remove(token);
            if (!data.isOriginalFired)
                iterator.remove();
        }
        if (!tokens.isEmpty())
            child.deactivateTokens(tokens);
    }

    /**
     * Gets the original token corresponding to the specified token
     *
     * @param token A token
     * @return The corresponding original token
     */
    private Token getOriginal(Token token) {
        for (int i = 0; i != hop; i++)
            token = token.getParent();
        return token;
    }

    /**
     * The data about a token in a NCC network
     */
    private static class Data {
        /**
         * The number of derived tokens
         */
        public int counter;
        /**
         * Whether the original token was fired to the children
         */
        public boolean isOriginalFired;

        /**
         * Initializes this data
         */
        public Data() {
            this.counter = 0;
            this.isOriginalFired = false;
        }
    }
}
