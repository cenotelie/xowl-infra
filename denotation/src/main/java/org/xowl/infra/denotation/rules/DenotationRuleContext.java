/*******************************************************************************
 * Copyright (c) 2017 Association Cénotélie (cenotelie.fr)
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

package org.xowl.infra.denotation.rules;

import org.xowl.infra.store.rdf.RDFRuleSimple;
import org.xowl.infra.store.rdf.VariableNode;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents the lexical context of a rule
 *
 * @author Laurent Wouters
 */
public class DenotationRuleContext {
    /**
     * The variable nodes
     */
    private final Map<String, VariableNode> variables;
    /**
     * The RDF rule
     */
    private final RDFRuleSimple rule;
    /**
     * The counter for generated variables
     */
    private int counter;

    /**
     * Initializes this context
     *
     * @param rule The RDF rule
     */
    public DenotationRuleContext(RDFRuleSimple rule) {
        this.variables = new HashMap<>();
        this.rule = rule;
        this.counter = 0;
    }

    /**
     * Gets the current RDF rule
     *
     * @return The current RDF rule
     */
    public RDFRuleSimple getRdfRule() {
        return rule;
    }

    /**
     * Gets the variable for the specified identifier
     *
     * @param identifier The identifier
     * @return The associated variable
     */
    public VariableNode getVariable(String identifier) {
        VariableNode variable = variables.get(identifier);
        if (variable == null) {
            variable = new VariableNode(identifier);
            variables.put(identifier, variable);
        }
        return variable;
    }

    /**
     * Creates a new unique variable
     *
     * @return The variable
     */
    public VariableNode getVariable() {
        VariableNode variable = new VariableNode("__gen_" + counter);
        counter++;
        return variable;
    }
}
