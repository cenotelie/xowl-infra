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
import org.xowl.store.rdf.VariableNode;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Represent a token in a RETE network, i.e. a piece of matching data
 *
 * @author Laurent Wouters
 */
public class Token {
    /**
     * The initial size of the binding buffers
     */
    private static final int BINDINGS_SIZE = 4;

    /**
     * The parent token
     */
    private Token parent;
    /**
     * The variables bound in this token
     */
    private VariableNode[] variables;
    /**
     * The values for the bound variables
     */
    private Node[] values;

    /**
     * Initializes this token with the specified parent
     *
     * @param parent The parent token
     */
    public Token(Token parent) {
        this.parent = parent;
        this.variables = new VariableNode[BINDINGS_SIZE];
        this.values = new Node[BINDINGS_SIZE];
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
        for (int i = 0; i != variables.length; i++) {
            if (variables[i] == null) {
                variables[i] = variable;
                values[i] = value;
                return;
            }
        }
        int index = variables.length;
        variables = Arrays.copyOf(variables, variables.length + BINDINGS_SIZE);
        values = Arrays.copyOf(values, values.length + BINDINGS_SIZE);
        variables[index] = variable;
        values[index] = value;
    }

    /**
     * Gets the value bound to the specified variable in this token
     *
     * @param variable A variable
     * @return The value bound to the variable, or null if none is
     */
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

    /**
     * Gets the local binding of the specified variable
     *
     * @param variable A variable
     * @return The local binding of the variable
     */
    private Node getLocalBinding(VariableNode variable) {
        for (int i = 0; i != variables.length; i++) {
            if (variables[i] == variable)
                return values[i];
        }
        return null;
    }

    /**
     * Gets the complete bindings in this token
     *
     * @return The bindings in this token
     */
    public Map<VariableNode, Node> getBindings() {
        HashMap<VariableNode, Node> bindings = new HashMap<>();
        Token current = this;
        while (current != null) {
            for (int i = 0; i != current.variables.length; i++) {
                if (current.variables[i] == null)
                    break;
                bindings.put(current.variables[i], current.values[i]);
            }
            current = current.parent;
        }
        return bindings;
    }
}
