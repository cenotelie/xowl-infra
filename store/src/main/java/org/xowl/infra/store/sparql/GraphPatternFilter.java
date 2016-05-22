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

/**
 * A graph pattern represented as the filtering of another one
 *
 * @author Laurent Wouters
 */
public class GraphPatternFilter implements GraphPattern {
    /**
     * The inner pattern
     */
    private final GraphPattern origin;
    /**
     * The boolean expression used for filtering
     */
    private final Expression expression;

    /**
     * Gets the inner pattern
     *
     * @return The inner pattern
     */
    public GraphPattern getInner() {
        return origin;
    }

    /**
     * Gets the boolean expression used for filtering
     *
     * @return The boolean expression used for filtering
     */
    public Expression getExpression() {
        return expression;
    }

    /**
     * Initializes this graph pattern
     *
     * @param origin     The inner pattern
     * @param expression The boolean expression used for filtering
     */
    public GraphPatternFilter(GraphPattern origin, Expression expression) {
        this.origin = origin;
        this.expression = expression;
    }

    @Override
    public Solutions eval(EvalContext context) throws EvalException {
        return Utils.filter(origin.eval(context), expression, context);
    }

    @Override
    public void inspect(Inspector inspector) {
        inspector.onGraphPattern(this);
        origin.inspect(inspector);
    }
}
