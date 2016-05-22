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

package org.xowl.infra.store.sparql;

/**
 * A graph pattern represented as the restriction of one by another
 *
 * @author Laurent Wouters
 */
public class GraphPatternMinus implements GraphPattern {
    /**
     * The restricted pattern
     */
    private final GraphPattern original;
    /**
     * The restricting pattern
     */
    private final GraphPattern restricting;

    /**
     * Initializes this graph pattern
     *
     * @param original    The restricted pattern
     * @param restricting The restricting pattern
     */
    public GraphPatternMinus(GraphPattern original, GraphPattern restricting) {
        this.original = original;
        this.restricting = restricting;
    }

    @Override
    public Solutions eval(EvalContext context) throws EvalException {
        Solutions originalSolutions = original.eval(context);
        Solutions restrictingSolutions = restricting.eval(context);
        return Utils.minus(originalSolutions, restrictingSolutions);
    }

    @Override
    public void inspect(Inspector inspector) {
        inspector.onGraphPattern(this);
        original.inspect(inspector);
        restricting.inspect(inspector);
    }
}
