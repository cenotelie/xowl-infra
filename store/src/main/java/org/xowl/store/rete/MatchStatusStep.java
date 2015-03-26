/**********************************************************************
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
 **********************************************************************/
package org.xowl.store.rete;

import org.xowl.store.rdf.Node;
import org.xowl.store.rdf.Quad;
import org.xowl.store.rdf.VariableNode;

import java.util.*;

/**
 * Represents a step in a matching rule, i.e. a join operation
 *
 * @author Laurent Wouters
 */
public class MatchStatusStep {
    /**
     * The pattern that need to be matched
     */
    private final Quad pattern;
    /**
     * The current bindings at the end of this step
     */
    private final List<Map<VariableNode, Node>> bindings;

    /**
     * Gets the pattern that need to be matched
     *
     * @return The pattern that need to be matched
     */
    public Quad getPattern() {
        return pattern;
    }

    /**
     * Gets the solution bindings at the end of this step
     *
     * @return The solution bindings
     */
    public Collection<Map<VariableNode, Node>> getBindings() {
        return Collections.unmodifiableCollection(bindings);
    }

    /**
     * Initializes this step
     *
     * @param pattern The pattern that need to be matched
     */
    protected MatchStatusStep(Quad pattern) {
        this.pattern = pattern;
        this.bindings = new ArrayList<>();
    }

    /**
     * Adds the bindings of the specified token
     *
     * @param token A token
     */
    protected void addBindings(Token token) {
        this.bindings.add(token.getBindings());
    }
}
