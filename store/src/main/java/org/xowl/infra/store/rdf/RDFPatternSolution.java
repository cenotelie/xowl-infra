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

package org.xowl.infra.store.rdf;

import fr.cenotelie.commons.utils.collections.Couple;
import org.xowl.infra.store.RDFUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * Represents a solution to a RDF pattern
 *
 * @author Laurent Wouters
 */
public class RDFPatternSolution implements Iterable<Couple<VariableNode, Node>> {
    /**
     * The content of this solution
     */
    private final Collection<Couple<VariableNode, Node>> bindings;

    /**
     * Initializes this solution
     *
     * @param bindings The bindings
     */
    public RDFPatternSolution(Collection<Couple<VariableNode, Node>> bindings) {
        this.bindings = new ArrayList<>(bindings);
    }

    /**
     * Initializes this solution as a copy the specified one augmented with a new one (or a replacement)
     *
     * @param original The original solution
     * @param variable The new variable to bind
     * @param value    The value to bind to
     */
    public RDFPatternSolution(RDFPatternSolution original, VariableNode variable, Node value) {
        this.bindings = new ArrayList<>(original.size());
        for (Couple<VariableNode, Node> binding : original.bindings) {
            if (binding.x != variable)
                bindings.add(binding);
        }
        this.bindings.add(new Couple<>(variable, value));
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
            if (RDFUtils.same(binding.x, variable))
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

    @Override
    public Iterator<Couple<VariableNode, Node>> iterator() {
        return bindings.iterator();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof RDFPatternSolution))
            return false;
        RDFPatternSolution solution = (RDFPatternSolution) o;
        if (solution.bindings.size() != this.bindings.size())
            return false;
        for (Couple<VariableNode, Node> binding : this.bindings) {
            boolean found = false;
            for (Couple<VariableNode, Node> candidate : solution.bindings) {
                if (RDFUtils.same(candidate.x, binding.x) && RDFUtils.same(candidate.y, binding.y)) {
                    found = true;
                    break;
                }
            }
            if (!found)
                return false;
        }
        return true;
    }
}
