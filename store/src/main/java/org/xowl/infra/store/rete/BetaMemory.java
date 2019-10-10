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

import fr.cenotelie.commons.utils.collections.AdaptingIterator;
import fr.cenotelie.commons.utils.collections.CombiningIterator;
import fr.cenotelie.commons.utils.collections.FastBuffer;
import fr.cenotelie.commons.utils.collections.SkippableIterator;
import org.xowl.infra.store.RDFUtils;
import org.xowl.infra.store.rdf.Node;
import org.xowl.infra.store.rdf.Quad;

import java.util.*;

/**
 * Represents a beta memory in a RETE network
 *
 * @author Laurent Wouters
 */
class BetaMemory implements TokenHolder {
    /**
     * The initial size of a buffer buffer
     */
    private static final int CHILDREN_SIZE = 8;

    /**
     * A DUMMY beta memory
     */
    private static BetaMemory DUMMY;

    /**
     * Gets a DUMMY beta memory
     *
     * @return A DUMMY beta memory
     */
    public static synchronized BetaMemory getDummy() {
        if (DUMMY != null)
            return DUMMY;
        DUMMY = new BetaMemory(null, 0);
        FastBuffer<Token> children = new FastBuffer<>(1);
        children.add(new Token());
        DUMMY.store.put(null, children);
        return DUMMY;
    }

    /**
     * The store of tokens
     */
    private final Map<Token, FastBuffer<Token>> store;
    /**
     * The buffer of this node
     */
    private final FastBuffer<TokenActivable> children;
    /**
     * The binding operations in this node
     */
    private final Binder[] binders;

    /**
     * Initializes this node
     *
     * @param binders      The binders for this memory (array of size 4)
     * @param bindersCount The total number of binders (maximum 4)
     */
    public BetaMemory(Binder[] binders, int bindersCount) {
        this.store = new HashMap<>();
        this.children = new FastBuffer<>(8);
        this.binders = bindersCount > 0 ? Arrays.copyOf(binders, bindersCount) : null;
    }

    /**
     * Removes all the children of this memory
     */
    void removeAllChildren() {
        synchronized (children) {
            children.clear();
        }
    }

    @Override
    public Collection<Token> getTokens() {
        return new TokenCollection() {
            @Override
            protected int getSize() {
                int result = 0;
                synchronized (store) {
                    for (FastBuffer<Token> children : store.values()) {
                        result += children.size();
                    }
                }
                return result;
            }

            @Override
            protected boolean contains(Token token) {
                synchronized (store) {
                    for (FastBuffer<Token> children : store.values()) {
                        if (children.contains(token))
                            return true;
                    }
                }
                return false;
            }

            @Override
            public Iterator<Token> iterator() {
                CombiningIterator<Map.Entry<Token, FastBuffer<Token>>, Token> coupleIterator = new CombiningIterator<>(
                        store.entrySet().iterator(),
                        element -> new SkippableIterator<>(element.getValue().iterator()));
                return new AdaptingIterator<>(
                        coupleIterator,
                        element -> element.y);
            }
        };
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
    public void onDestroy() {
        synchronized (children) {
            children.clear();
        }
        synchronized (store) {
            store.clear();
        }
    }

    /**
     * Activates this store
     *
     * @param token The parent token
     * @param fact  The input fact
     */
    public void activate(Token token, Quad fact) {
        Token newToken = buildChildToken(token, fact);
        if (newToken == null)
            return;
        Iterator<TokenActivable> iterator = children.reverseIterator();
        while (iterator.hasNext()) {
            TokenActivable child = iterator.next();
            if (child != null)
                child.activateToken(newToken);
        }
    }

    /**
     * Activates this store
     *
     * @param buffer A buffer of couples
     */
    public void activate(Iterator<JoinMatch> buffer) {
        Collection<Token> result = new ArrayList<>();
        while (buffer.hasNext()) {
            JoinMatch joinMatch = buffer.next();
            Token newToken = buildChildToken(joinMatch.token, joinMatch.fact);
            if (newToken != null)
                result.add(newToken);
        }
        if (!result.isEmpty()) {
            Iterator<TokenActivable> iterator = children.reverseIterator();
            while (iterator.hasNext()) {
                TokenActivable child = iterator.next();
                if (child != null)
                    child.activateTokens(new FastBuffer<>(result));
            }
        }
    }

    /**
     * Builds the token for this store
     *
     * @param token The parent token
     * @param fact  The input fact
     * @return The corresponding child token if it is new, null otherwise
     */
    private Token buildChildToken(Token token, Quad fact) {
        FastBuffer<Token> tChildren;
        synchronized (store) {
            tChildren = store.get(token);
            if (tChildren == null) {
                tChildren = new FastBuffer<>(binders == null ? 1 : CHILDREN_SIZE);
                store.put(token, tChildren);
            }
        }

        if (binders == null) {
            // no binders, there can be only one child
            synchronized (store) {
                if (tChildren.isEmpty()) {
                    // not here, build the child
                    Token child = new Token(token, 0);
                    tChildren.add(child);
                    return child;
                } else {
                    // already here
                    tChildren.first().increment();
                    return null;
                }
            }
        }

        // create the child token
        Token childToken = new Token(token, binders.length);
        for (int i = 0; i != binders.length; i++)
            binders[i].execute(childToken, fact);
        synchronized (store) {
            tChildren.add(childToken);
        }
        return childToken;
    }

    /**
     * Deactivates on the specified token
     *
     * @param token A token
     */
    public void deactivateToken(Token token) {
        FastBuffer<Token> tChildren;
        synchronized (store) {
            tChildren = store.remove(token);
        }
        if (tChildren == null || tChildren.isEmpty())
            return;
        Iterator<TokenActivable> iterator = children.reverseIterator();
        while (iterator.hasNext()) {
            TokenActivable child = iterator.next();
            if (child != null)
                child.deactivateTokens(new FastBuffer<>(tChildren));
        }
    }

    /**
     * Deactivates on the specified token and fact
     *
     * @param token A token
     * @param fact  A fact
     */
    public void deactivateCouple(Token token, Quad fact) {
        Collection<Token> buffer = new ArrayList<>();
        deactivateJoinMatch(token, fact, buffer);
        if (!buffer.isEmpty()) {
            Iterator<TokenActivable> iterator = children.reverseIterator();
            while (iterator.hasNext()) {
                TokenActivable child = iterator.next();
                if (child != null)
                    child.deactivateTokens(new FastBuffer<>(buffer));
            }
        }
    }

    /**
     * Deactivates on the specified tokens
     *
     * @param tokens Some tokens
     */
    public void deactivateTokens(Collection<Token> tokens) {
        Collection<Token> buffer = new ArrayList<>();
        for (Token token : tokens) {
            FastBuffer<Token> tChildren;
            synchronized (store) {
                tChildren = store.remove(token);
                if (tChildren == null)
                    continue;
                buffer.addAll(tChildren);
            }
        }
        if (!buffer.isEmpty()) {
            Iterator<TokenActivable> iterator = children.reverseIterator();
            while (iterator.hasNext()) {
                TokenActivable child = iterator.next();
                if (child != null)
                    child.deactivateTokens(new FastBuffer<>(buffer));
            }
        }
    }

    /**
     * Deactivates on the specified couples
     *
     * @param couples Some couples
     */
    public void deactivateCouples(Iterator<JoinMatch> couples) {
        Collection<Token> buffer = new ArrayList<>();
        while (couples.hasNext()) {
            JoinMatch joinMatch = couples.next();
            deactivateJoinMatch(joinMatch.token, joinMatch.fact, buffer);
        }
        if (!buffer.isEmpty()) {
            Iterator<TokenActivable> iterator = children.reverseIterator();
            while (iterator.hasNext()) {
                TokenActivable child = iterator.next();
                if (child != null)
                    child.deactivateTokens(new FastBuffer<>(buffer));
            }
        }
    }

    /**
     * Deactivates a join match
     *
     * @param token  The matching token
     * @param fact   The matching quad
     * @param buffer The buffer of child tokens to be deactivated
     */
    private void deactivateJoinMatch(Token token, Quad fact, Collection<Token> buffer) {
        FastBuffer<Token> tChildren;
        synchronized (store) {
            tChildren = store.get(token);
        }
        if (tChildren == null)
            return;
        boolean isEmpty = true;
        if (binders == null) {
            Token first = tChildren.first();
            if (first != null) {
                if (first.decrement())
                    buffer.add(first);
            } else
                isEmpty = false;
        } else {
            Iterator<Token> iterator = tChildren.iterator();
            while (iterator.hasNext()) {
                Token child = iterator.next();
                if (child != null) {
                    if (matches(child, fact)) {
                        buffer.add(child);
                        iterator.remove();
                    } else {
                        isEmpty = false;
                    }
                }
            }
        }
        if (isEmpty) {
            synchronized (store) {
                store.remove(token);
            }
        }
    }

    /**
     * Gets whether the specified child token can be matched by the specified fact
     *
     * @param token A child token in this tore
     * @param fact  A fact
     * @return true if te token and fact matches
     */
    private boolean matches(Token token, Quad fact) {
        if (binders == null)
            return true;
        for (int i = 0; i != binders.length; i++) {
            Node value1 = token.getLocalBinding(binders[i].getVariable());
            Node value2 = fact.getField(binders[i].getField());
            if (!RDFUtils.same(value1, value2))
                return false;
        }
        return true;
    }
}
