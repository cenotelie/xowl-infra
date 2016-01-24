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
package org.xowl.store.owl;

import org.xowl.store.rdf.Node;
import org.xowl.store.rdf.VariableNode;
import org.xowl.infra.utils.collections.Couple;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents the activation of a parsing rule in a RDF parser
 *
 * @author Laurent Wouters
 */
class RDFParserTrigger {
    /**
     * The triggered rule
     */
    public final RDFParserRule rule;
    /**
     * The bindings
     */
    public final Map<VariableNode, Node> bindings;

    /**
     * Initializes this trigger
     *
     * @param rule     The triggered rule
     * @param bindings The bindings
     */
    public RDFParserTrigger(RDFParserRule rule, Collection<Couple<VariableNode, Node>> bindings) {
        this.rule = rule;
        this.bindings = new HashMap<>();
        for (Couple<VariableNode, Node> binding : bindings)
            this.bindings.put(binding.x, binding.y);
    }

    /**
     * Executes this trigger
     */
    public void execute() {
        rule.activate(bindings);
    }
}
