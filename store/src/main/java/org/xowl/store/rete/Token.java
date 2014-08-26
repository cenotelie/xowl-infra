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

import org.xowl.store.owl.VariableNode;
import org.xowl.store.rdf.Node;

import java.util.IdentityHashMap;
import java.util.Map;

/**
 * Represent a token in a RETE network, i.e. a piece of matching data
 *
 * @author Laurent Wouters
 */
public class Token {
    /**
     * The parent token
     */
    private Token parent;
    /**
     * The bindings in this token
     */
    private Map<VariableNode, Node> bindings;

    /**
     * Initializes this token with the specified parent
     *
     * @param parent The parent token
     */
    public Token(Token parent) {
        this.parent = parent;
        if (parent != null)
            this.bindings = new IdentityHashMap<>(parent.bindings);
        else
            this.bindings = new IdentityHashMap<>();
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
     * Binds the specified variable to the specified value in this token
     *
     * @param variable A variable
     * @param value    A value
     */
    public void bind(VariableNode variable, Node value) {
        bindings.put(variable, value);
    }

    /**
     * Gets the value bound to the specified variable in this token
     *
     * @param variable A variable
     * @return The value bound to the variable, or null if none is
     */
    public Node getBinding(VariableNode variable) {
        return bindings.get(variable);
    }

    /**
     * Gets the complete bindings in this token
     *
     * @return The bindings in this token
     */
    public Map<VariableNode, Node> getBindings() {
        return bindings;
    }
}
