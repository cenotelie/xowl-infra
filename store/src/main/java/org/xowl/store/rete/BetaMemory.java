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
import org.xowl.utils.collections.Adapter;
import org.xowl.utils.collections.AdaptingIterator;
import org.xowl.utils.collections.SparseIterator;

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
     * Represents the buffer data of a token
     */
    public static class TChildren {
        /**
         * The buffer of children
         */
        public TChild[] buffer;
        /**
         * The number of children
         */
        public int count;

        /**
         * Initializes this collection
         */
        public TChildren() {
            this.buffer = new TChild[CHILDREN_SIZE];
            this.count = 0;
        }

        /**
         * Gets an iterator over the children tokens
         *
         * @return An iterator over the children tokens
         */
        public Iterator<Token> iterator() {
            return new AdaptingIterator<>(new SparseIterator<>(buffer), new Adapter<Token>() {
                @Override
                public <X> Token adapt(X element) {
                    return ((TChild) element).token;
                }
            });
        }

        /**
         * Gets whether the specified child token is is this buffer
         *
         * @param child A child token
         * @return Whether the child token is contained
         */
        public boolean contains(Token child) {
            for (int i = 0; i != buffer.length; i++)
                if (buffer[i] != null && buffer[i].token == child)
                    return true;
            return false;
        }
    }

    /**
     * Represents the child data of a token
     */
    public static class TChild {
        /**
         * The child token
         */
        public Token token;
        /**
         * The multiplicity
         */
        public int multiplicity;

        /**
         * Initializes this child data
         *
         * @param token The child token
         */
        public TChild(Token token) {
            this.token = token;
            this.multiplicity = 1;
        }
    }


    /**
     * A dummy beta memory
     */
    private static BetaMemory dummy;
    /**
     * The store of tokens
     */
    private Map<Token, TChildren> store;
    /**
     * The buffer of this node
     */
    private List<TokenActivable> children;
    /**
     * The binding operations in this node
     */
    private List<Binder> binders;

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
        TChildren tChildren = new TChildren();
        tChildren.buffer[0] = new TChild(new Token());
        tChildren.count = 1;
        dummy.store.put(null, tChildren);
        return dummy;
    }

    @Override
    public Collection<Token> getTokens() {
        return new TokenCollection(store);
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
        TChildren children = store.get(token);
        if (children == null) {
            children = new TChildren();
            store.put(token, children);
        }
        // optimistically create the child token
        Token childToken = new Token(token, binders.size());
        for (Binder binder : binders)
            binder.execute(childToken, fact);
        // search the pre-existing buffer for a matching
        int indexFree = -1;
        for (int i = 0; i != children.buffer.length; i++) {
            TChild child = children.buffer[i];
            if (child != null) {
                if (matches(child.token, fact)) {
                    // increment the counter
                    child.multiplicity++;
                    return null;
                }
            } else if (children.buffer.length > children.count && indexFree == -1) {
                indexFree = i;
            }
        }
        if (indexFree == -1) {
            // no free slot
            indexFree = children.buffer.length;
            children.buffer = Arrays.copyOf(children.buffer, children.buffer.length + CHILDREN_SIZE);
        }
        children.buffer[indexFree] = new TChild(childToken);
        children.count++;
        return childToken;
    }

    /**
     * Deactivates on the specified token
     *
     * @param token A token
     */
    public void deactivateToken(Token token) {
        TChildren tChildren = store.remove(token);
        if (tChildren == null)
            return;
        Collection<Token> buffer = new ArrayList<>();
        for (int i = 0; i != tChildren.buffer.length; i++) {
            if (tChildren.buffer[i] != null) {
                buffer.add(tChildren.buffer[i].token);
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
        TChildren tChildren = store.get(token);
        if (tChildren == null)
            return;
        Collection<Token> buffer = new ArrayList<>();
        for (int i = 0; i != tChildren.buffer.length; i++) {
            TChild tChild = tChildren.buffer[i];
            if (tChild != null && matches(tChild.token, fact)) {
                tChild.multiplicity--;
                if (tChild.multiplicity == 0) {
                    tChildren.buffer[i] = null;
                    tChildren.count--;
                    if (tChildren.count == 0)
                        store.remove(token);
                    buffer.add(tChild.token);
                }
            }
        }
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
            TChildren tChildren = store.remove(token);
            if (tChildren == null)
                continue;
            for (int i = 0; i != tChildren.buffer.length; i++) {
                if (tChildren.buffer[i] != null) {
                    buffer.add(tChildren.buffer[i].token);
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
            TChildren tChildren = store.get(couple.token);
            if (tChildren == null)
                continue;
            for (int i = 0; i != tChildren.buffer.length; i++) {
                TChild tChild = tChildren.buffer[i];
                if (tChild != null && matches(tChild.token, couple.fact)) {
                    tChild.multiplicity--;
                    if (tChild.multiplicity == 0) {
                        tChildren.buffer[i] = null;
                        tChildren.count--;
                        if (tChildren.count == 0)
                            store.remove(couple.token);
                        buffer.add(tChild.token);
                    }
                }
            }
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
            if (!value1.equals(value2))
                return false;
        }
        return true;
    }
}
