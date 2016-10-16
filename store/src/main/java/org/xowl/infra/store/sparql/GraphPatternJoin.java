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

package org.xowl.infra.store.sparql;

import org.xowl.infra.store.rdf.Node;

import java.util.Map;

/**
 * A graph pattern as the join of two other patterns
 *
 * @author Laurent Wouters
 */
public class GraphPatternJoin implements GraphPattern {
    /**
     * The pattern on the left
     */
    private final GraphPattern left;
    /**
     * The pattern on the right
     */
    private final GraphPattern right;

    /**
     * Initializes this graph pattern
     *
     * @param left  The pattern on the left
     * @param right The pattern on the right
     */
    public GraphPatternJoin(GraphPattern left, GraphPattern right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public Solutions eval(EvalContext context) throws EvalException {
        Solutions leftSolutions = left.eval(context);
        Solutions rightSolutions = right.eval(context);
        return Utils.join(leftSolutions, rightSolutions);
    }

    @Override
    public void inspect(Inspector inspector) {
        inspector.onGraphPattern(this);
        left.inspect(inspector);
        right.inspect(inspector);
    }

    @Override
    public GraphPattern clone(Map<String, Node> parameters) {
        return new GraphPatternJoin(left.clone(parameters), right.clone(parameters));
    }
}
