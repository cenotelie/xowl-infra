/*******************************************************************************
 * Copyright (c) 2015 Laurent Wouters
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
 ******************************************************************************/

package org.xowl.infra.store.rete;

import org.xowl.infra.store.RDFUtils;
import org.xowl.infra.store.rdf.Node;
import org.xowl.infra.store.rdf.Quad;
import org.xowl.infra.utils.collections.Adapter;
import org.xowl.infra.utils.collections.AdaptingIterator;
import org.xowl.infra.utils.collections.CombiningIterator;
import org.xowl.infra.utils.collections.SparseIterator;

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
     * A dummy beta memory
     */
    private static BetaMemory dummy;
    /**
     * The store of tokens
     */
    private Map<Token, Token[]> store;
    /**
     * The buffer of this node
     */
    private List<TokenActivable> children;
    /**
     * The binding operations in this node
     */
    private final List<Binder> binders;

    /**
     * Initializes this node
     *
     * @param binders The binders for this memory
     */
    public BetaMemory(Collection<Binder> binders) {
        this.store = new HashMap<>();
        this.children = new ArrayList<>();
        this.binders = new ArrayList<>(binders);
    }

    /**
     * Gets a dummy beta memory
     *
     * @return A dummy beta memory
     */
    public static synchronized BetaMemory getDummy() {
        if (dummy != null)
            return dummy;
        dummy = new BetaMemory(new ArrayList<Binder>(0));
        Token[] children = new Token[1];
        children[0] = new Token();
        dummy.store.put(null, children);
        return dummy;
    }

    /**
     * Removes all the children of this memory
     */
    void removeAllChildren() {
        children.clear();
    }

    @Override
    public Collection<Token> getTokens() {
        return new TokenCollection() {
            @Override
            protected int getSize() {
                int result = 0;
                for (Token[] children : store.values()) {
                    for (Token child : children) {
                        if (child != null)
                            result++;
                    }
                }
                return result;
            }

            @Override
            protected boolean contains(Token token) {
                for (Token[] children : store.values()) {
                    for (Token child : children) {
                        if (child == token)
                            return true;
                    }
                }
                return false;
            }

            @Override
            public Iterator<Token> iterator() {
                CombiningIterator<Map.Entry<Token, Token[]>, Token> coupleIterator = new CombiningIterator<>(store.entrySet().iterator(), new Adapter<Iterator<Token>>() {
                    @Override
                    public <X> Iterator<Token> adapt(X element) {
                        return new SparseIterator<>(((Map.Entry<Token, Token[]>) element).getValue());
                    }
                });
                return new AdaptingIterator<>(coupleIterator, new Adapter<Token>() {
                    @Override
                    public <X> Token adapt(X element) {
                        return ((org.xowl.infra.utils.collections.Couple<Map.Entry<Token, Token[]>, Token>) element).y;
                    }
                });
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
        store = null;
        children = null;
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
            Token newToken = buildChildToken(couple.token, couple.fact);
            if (newToken != null)
                result.add(newToken);
        }
        if (!result.isEmpty())
            for (int i = children.size() - 1; i != -1; i--)
                children.get(i).activateTokens(new FastBuffer<>(result));
    }

    /**
     * Builds the token for this store
     *
     * @param token The parent token
     * @param fact  The input fact
     * @return The corresponding child token if it is new, null otherwise
     */
    private Token buildChildToken(Token token, Quad fact) {
        Token[] tChildren = store.get(token);
        if (tChildren == null) {
            tChildren = new Token[binders.isEmpty() ? 1 : CHILDREN_SIZE];
            store.put(token, tChildren);
        }

        if (binders.isEmpty()) {
            // no binders, there can be only one child
            if (tChildren[0] != null) {
                // already here
                tChildren[0].multiplicity++;
                return null;
            }
            // not here, build the child
            tChildren[0] = new Token(token, binders.size());
            return tChildren[0];
        }

        // create the child token
        Token childToken = new Token(token, binders.size());
        for (Binder binder : binders)
            binder.execute(childToken, fact);
        for (int i = 0; i != tChildren.length; i++) {
            if (tChildren[i] == null) {
                tChildren[i] = childToken;
                return childToken;
            }
        }
        int index = tChildren.length;
        tChildren = Arrays.copyOf(tChildren, tChildren.length + CHILDREN_SIZE);
        tChildren[index] = childToken;
        store.put(token, tChildren);
        return childToken;
    }

    /**
     * Deactivates on the specified token
     *
     * @param token A token
     */
    public void deactivateToken(Token token) {
        Token[] tChildren = store.remove(token);
        if (tChildren == null)
            return;
        Collection<Token> buffer = new ArrayList<>();
        for (int i = 0; i != tChildren.length; i++) {
            if (tChildren[i] != null) {
                buffer.add(tChildren[i]);
            }
        }
        if (!buffer.isEmpty())
            for (int i = children.size() - 1; i != -1; i--)
                children.get(i).deactivateTokens(new FastBuffer<>(buffer));
    }

    /**
     * Deactivates on the specified token and fact
     *
     * @param token A token
     * @param fact  A fact
     */
    public void deactivateCouple(Token token, Quad fact) {
        Token[] tChildren = store.get(token);
        if (tChildren == null)
            return;
        Collection<Token> buffer = new ArrayList<>();
        boolean isEmpty = true;
        if (binders.isEmpty()) {
            tChildren[0].multiplicity--;
            if (tChildren[0].multiplicity <= 0)
                buffer.add(tChildren[0]);
            else
                isEmpty = false;
        } else {
            for (int i = 0; i != tChildren.length; i++) {
                if (tChildren[i] != null) {
                    if (matches(tChildren[i], fact)) {
                        buffer.add(tChildren[i]);
                        tChildren[i] = null;
                    } else {
                        isEmpty = false;
                    }
                }
            }
        }
        if (isEmpty)
            store.remove(token);
        if (!buffer.isEmpty())
            for (int i = children.size() - 1; i != -1; i--)
                children.get(i).deactivateTokens(new FastBuffer<>(buffer));
    }

    /**
     * Deactivates on the specified tokens
     *
     * @param tokens Some tokens
     */
    public void deactivateTokens(Collection<Token> tokens) {
        Collection<Token> buffer = new ArrayList<>();
        for (Token token : tokens) {
            Token[] tChildren = store.remove(token);
            if (tChildren == null)
                continue;
            for (int i = 0; i != tChildren.length; i++) {
                if (tChildren[i] != null) {
                    buffer.add(tChildren[i]);
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
            Token[] tChildren = store.get(couple.token);
            if (tChildren == null)
                continue;
            boolean isEmpty = true;
            if (binders.isEmpty()) {
                tChildren[0].multiplicity--;
                if (tChildren[0].multiplicity <= 0)
                    buffer.add(tChildren[0]);
                else
                    isEmpty = false;
            } else {
                for (int i = 0; i != tChildren.length; i++) {
                    if (tChildren[i] != null) {
                        if (matches(tChildren[i], couple.fact)) {
                            buffer.add(tChildren[i]);
                            tChildren[i] = null;
                        } else {
                            isEmpty = false;
                        }
                    }
                }
            }
            if (isEmpty)
                store.remove(couple.token);
        }
        if (!buffer.isEmpty())
            for (int i = children.size() - 1; i != -1; i--)
                children.get(i).deactivateTokens(new FastBuffer<>(buffer));
    }

    /**
     * Gets whether the specified child token can be matched by the specified fact
     *
     * @param token A child token in this tore
     * @param fact  A fact
     * @return true if te token and fact matches
     */
    private boolean matches(Token token, Quad fact) {
        for (Binder binder : binders) {
            Node value1 = token.getLocalBinding(binder.getVariable());
            Node value2 = fact.getField(binder.getField());
            if (!RDFUtils.same(value1, value2))
                return false;
        }
        return true;
    }
}
