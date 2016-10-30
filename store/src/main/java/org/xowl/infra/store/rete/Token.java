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

import org.xowl.infra.store.RDFUtils;
import org.xowl.infra.store.rdf.Node;
import org.xowl.infra.store.rdf.RDFPatternMatch;
import org.xowl.infra.store.rdf.RDFPatternSolution;
import org.xowl.infra.store.rdf.VariableNode;
import org.xowl.infra.utils.collections.Couple;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Represent a token in a RETE network, i.e. a piece of matching data
 *
 * @author Laurent Wouters
 */
public class Token implements RDFPatternMatch {
    /**
     * The parent token
     */
    private final Token parent;
    /**
     * The variables bound in this token
     */
    private final VariableNode[] variables;
    /**
     * The values for the bound variables
     */
    private final Node[] values;
    /**
     * The token's multiplicity
     */
    private final AtomicInteger multiplicity;

    /**
     * Initializes a dummy token
     */
    protected Token() {
        this.parent = null;
        this.variables = null;
        this.values = null;
        this.multiplicity = new AtomicInteger(1);
    }

    /**
     * Initializes this token with the specified parent
     *
     * @param parent       The parent token
     * @param bindingCount The number of bindings that will be applied
     */
    protected Token(Token parent, int bindingCount) {
        this.parent = parent;
        if (bindingCount > 0) {
            this.variables = new VariableNode[bindingCount];
            this.values = new Node[bindingCount];
        } else {
            this.variables = null;
            this.values = null;
        }
        this.multiplicity = new AtomicInteger(0);
    }

    /**
     * Gets the parent token
     *
     * @return The parent token
     */
    public Token getParent() {
        return parent;
    }

    /**
     * Increments the token's multiplicity
     */
    public void increment() {
        multiplicity.incrementAndGet();
    }

    /**
     * Decrement's the tokens multiplicity
     *
     * @return Whether the token's multiplicity reached 0
     */
    public boolean decrement() {
        return (multiplicity.decrementAndGet() <= 0);
    }

    /**
     * Binds the specified variable to the specified value in this token
     *
     * @param variable A variable
     * @param value    A value
     */
    public void bind(VariableNode variable, Node value) {
        if (variables == null || values == null)
            throw new IllegalArgumentException("This token is not supposed to contain bindings");
        for (int i = 0; i != variables.length; i++) {
            if (variables[i] == null) {
                variables[i] = variable;
                values[i] = value;
                return;
            }
        }
    }

    /**
     * Gets the local binding of the specified variable
     *
     * @param variable A variable
     * @return The local binding of the variable
     */
    public Node getLocalBinding(VariableNode variable) {
        if (variables == null || values == null)
            return null;
        for (int i = 0; i != variables.length; i++) {
            if (RDFUtils.same(variables[i], variable))
                return values[i];
        }
        return null;
    }

    /**
     * Gets the complete bindings in this token
     *
     * @return The bindings in this token
     */
    public Collection<Couple<VariableNode, Node>> getBindings() {
        Collection<Couple<VariableNode, Node>> result = new ArrayList<>();
        Token current = this;
        while (current != null) {
            if (current.variables != null && current.values != null) {
                for (int i = 0; i != current.variables.length; i++)
                    result.add(new Couple<>(current.variables[i], current.values[i]));
            }
            current = current.parent;
        }
        return result;
    }

    /**
     * Gets whether this token is the same as the specified one (the mappings are the same)
     *
     * @param token A token
     * @return true if the token is the same
     */
    public boolean sameAs(Token token) {
        Token left = this;
        Token right = token;
        while (left != null && right != null) {
            if (!sameBindings(left, right))
                return false;
            left = left.parent;
            right = right.parent;
        }
        return (left == null && right == null);
    }

    /**
     * Gets whether the bindings of two tokens are the same
     *
     * @param token1 A token
     * @param token2 A token
     * @return true if the bindings of two tokens are the same
     */
    private static boolean sameBindings(Token token1, Token token2) {
        if (token1 == token2)
            return true;
        if (token1.variables != null && token1.values != null) {
            if (token2.variables != null && token2.values != null) {
                if (token1.variables.length != token2.variables.length)
                    return false;
                for (int i = 0; i != token1.variables.length; i++) {
                    if (!RDFUtils.same(token1.variables[i], token2.variables[i]) || !RDFUtils.same(token1.values[i], token2.values[i]))
                        return false;
                }
                return true;
            } else {
                return false;
            }
        } else {
            return (token2.variables == null);
        }
    }

    @Override
    public boolean sameAs(RDFPatternMatch match) {
        return sameAs((Token) match);
    }

    @Override
    public Node getBinding(VariableNode variable) {
        Token current = this;
        while (current != null) {
            Node value = current.getLocalBinding(variable);
            if (value != null)
                return value;
            current = current.parent;
        }
        return null;
    }

    @Override
    public RDFPatternSolution getSolution() {
        return new RDFPatternSolution(getBindings());
    }
}
