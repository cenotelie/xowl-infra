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

package org.xowl.store.rdf;

import org.xowl.utils.collections.Couple;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Represents a solution to a RDF query
 *
 * @author Laurent Wouters
 */
public class QuerySolution {
    /**
     * The content of this solution
     */
    protected final Collection<Couple<VariableNode, Node>> bindings;

    /**
     * Initializes this solution
     *
     * @param bindings The bindings
     */
    public QuerySolution(Collection<Couple<VariableNode, Node>> bindings) {
        this.bindings = new ArrayList<>(bindings);
    }

    /**
     * Gets the size of this solution, i.e. the number of bindings it represents
     *
     * @return The size of this solution
     */
    public int size() {
        return bindings.size();
    }

    /**
     * Gets the list of the matched variables in this solution
     *
     * @return The list if the matched variables in this solution
     */
    public Collection<VariableNode> getVariables() {
        Collection<VariableNode> result = new ArrayList<>(bindings.size());
        for (Couple<VariableNode, Node> binding : bindings)
            result.add(binding.x);
        return result;
    }

    /**
     * Retrieves the value associated to the specified variable in this solution
     *
     * @param variable A variable
     * @return The value associated to the specified variable
     */
    public Node get(VariableNode variable) {
        for (Couple<VariableNode, Node> binding : bindings)
            if (binding.x.equals(variable))
                return binding.y;
        return null;
    }

    /**
     * Retrieves the value associated to the specified variable in this solution
     *
     * @param variable A variable
     * @return The value associated to the specified variable
     */
    public Node get(String variable) {
        for (Couple<VariableNode, Node> binding : bindings)
            if (binding.x.getName().equals(variable))
                return binding.y;
        return null;
    }

    /**
     * Binds (or re-binds) the specified variable to a value
     *
     * @param variable The variable to bind
     * @param value    The value to bind to
     */
    public void bind(String variable, Node value) {
        bind(new VariableNode(variable), value);
    }

    /**
     * Binds (or re-binds) the specified variable to a value
     *
     * @param variable The variable to bind
     * @param value    The value to bind to
     */
    public void bind(VariableNode variable, Node value) {
        bindings.add(new Couple<>(variable, value));
    }
}
