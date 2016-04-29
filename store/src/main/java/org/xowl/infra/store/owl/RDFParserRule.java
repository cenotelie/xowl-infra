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
package org.xowl.infra.store.owl;

import org.xowl.infra.store.rdf.Node;
import org.xowl.infra.store.rdf.Quad;
import org.xowl.infra.store.rdf.VariableNode;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a rule for parsing OWL2 axioms from a RDF graph
 *
 * @author Laurent Wouters
 */
abstract class RDFParserRule {
    /**
     * The rule's priority
     */
    protected final int priority;
    /**
     * The variables nodes
     */
    protected final Map<String, VariableNode> variables;

    /**
     * Initializes this rule
     *
     * @param priority The rule's priority
     */
    public RDFParserRule(int priority) {
        this.priority = priority;
        this.variables = new HashMap<>();
    }

    /**
     * Resolves and gets the variable with the specified name
     *
     * @param name The name of a variable
     * @return The associated node
     */
    protected VariableNode getVariable(String name) {
        VariableNode variableNode = variables.get(name);
        if (variableNode == null) {
            variableNode = new VariableNode(name);
            variables.put(name, variableNode);
        }
        return variableNode;
    }

    /**
     * Gets the value of the variable with the specified name
     *
     * @param bindings The current bindings
     * @param name     The name of a variable
     * @return The value of the variable in the provided bindings
     */
    protected Node getValue(Map<VariableNode, Node> bindings, String name) {
        return bindings.get(variables.get(name));
    }

    /**
     * Gets the quad patterns for this rule
     *
     * @return The quad patterns
     */
    public abstract Quad[] getPatterns();

    /**
     * Activates this rule with the specified bindings
     *
     * @param bindings The bindings
     */
    public abstract void activate(Map<VariableNode, Node> bindings);
}
