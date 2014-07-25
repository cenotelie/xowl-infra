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

import java.util.ArrayList;
import java.util.Collection;

/**
 * Represents an entry node for a sub-graph in a RETE network matching a negative conjunction of conditions
 *
 * @author Laurent Wouters
 */
public class BetaNCCEntryNode implements TokenHolder, TokenActivable {
    /**
     * The parent beta memory
     */
    private TokenHolder betaMem;
    /**
     * The child node
     */
    private TokenActivable child;
    /**
     * The exit node for the sub-graph
     */
    private BetaNCCExitNode exit;

    /**
     * Initializes this node
     *
     * @param beta The parent beta memory
     */
    public BetaNCCEntryNode(TokenHolder beta) {
        this.betaMem = beta;
        this.exit = new BetaNCCExitNode(beta);
        this.betaMem.addChild(this);
    }

    /**
     * Gets the exit node of the sub-graph
     *
     * @return The exit node of the sub-graph
     */
    public BetaNCCExitNode getExitNode() {
        return exit;
    }

    @Override
    public void activateToken(Token token) {
        exit.preActivation(token);
        child.activateToken(token);
        exit.postActivation(token);
    }

    @Override
    public void deactivateToken(Token token) {
        exit.preDeactivation();
        child.deactivateToken(token);
        exit.postDeactivation(token);
    }

    @Override
    public void activateTokens(Collection<Token> tokens) {
        exit.preActivation(tokens);
        child.activateTokens(new FastBuffer<>(tokens));
        exit.postActivation(tokens);
    }

    @Override
    public void deactivateTokens(Collection<Token> tokens) {
        exit.preDeactivation();
        child.deactivateTokens(new FastBuffer<>(tokens));
        exit.postDeactivation(tokens);
    }

    @Override
    public Collection<Token> getTokens() {
        return this.betaMem.getTokens();
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
}
