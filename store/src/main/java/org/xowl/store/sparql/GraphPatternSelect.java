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

package org.xowl.store.sparql;

import org.xowl.store.Repository;
import org.xowl.store.rdf.Node;
import org.xowl.store.rdf.QuerySolution;
import org.xowl.store.rdf.VariableNode;
import org.xowl.utils.collections.Couple;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A graph pattern represented by a SELECT query
 *
 * @author Laurent Wouters
 */
public class GraphPatternSelect implements GraphPattern {
    /**
     * Whether to remove duplicated solutions
     */
    private final boolean isDistinct;
    /**
     * Whether to allows the removal of some of the duplicated solutions
     */
    private final boolean isReduced;
    /**
     * The projection
     */
    private final List<Couple<VariableNode, Expression>> projection;
    /**
     * The WHERE clause
     */
    private final GraphPattern where;
    /**
     * The solutions modifier
     */
    private final GraphPatternModifier modifier;
    /**
     * The inline data values
     */
    private final GraphPatternInlineData values;

    /**
     * Initializes this graph pattern
     *
     * @param isDistinct Whether to remove duplicated solutions
     * @param isReduced  Whether to allows the removal of some of the duplicated solutions
     * @param where      The WHERE clause
     * @param modifier   The solutions modifier, or null
     * @param values     The inline data values, or null
     */
    public GraphPatternSelect(boolean isDistinct, boolean isReduced, GraphPattern where, GraphPatternModifier modifier, GraphPatternInlineData values) {
        this.isDistinct = isDistinct;
        this.isReduced = isReduced;
        this.projection = new ArrayList<>();
        this.where = where;
        this.modifier = modifier;
        this.values = values;
    }

    /**
     * Adds an existing variable to the projection
     *
     * @param variable The variable to include in the projection
     */
    public void addToProjection(VariableNode variable) {
        this.projection.add(new Couple<VariableNode, Expression>(variable, null));
    }

    /**
     * Adds a derived variable to the projection
     *
     * @param variable   The variable to include in the projection
     * @param expression The expression to derive the variable's value
     */
    public void addToProjection(VariableNode variable, Expression expression) {
        this.projection.add(new Couple<>(variable, expression));
    }

    @Override
    public Collection<QuerySolution> match(final Repository repository) throws EvalException {
        Collection<QuerySolution> originals = where.match(repository);

        if (!projection.isEmpty()) {
            Collection<QuerySolution> result = new ArrayList<>();
            for (QuerySolution original : originals) {
                List<Couple<VariableNode, Node>> bindings = new ArrayList<>();
                for (Couple<VariableNode, Expression> projected : projection) {
                    if (projected.y == null)
                        bindings.add(new Couple<>(projected.x, original.get(projected.x)));
                    else {
                        Node value = Utils.evaluateRDF(repository, original, projected.y);
                        bindings.add(new Couple<>(projected.x, value));
                    }
                }
                result.add(new QuerySolution(bindings));
            }
            originals = result;
        }

        if (isDistinct || isReduced) {
            Collection<QuerySolution> result = new ArrayList<>();
            for (QuerySolution solution : originals)
                if (!result.contains(solution))
                    result.add(solution);
            originals = result;
        }

        originals = modifier != null ? modifier.apply(originals) : originals;
        if (values != null)
            originals.addAll(values.match(repository));

        return originals;
    }
}
