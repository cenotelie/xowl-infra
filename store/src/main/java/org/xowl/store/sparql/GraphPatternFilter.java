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
import org.xowl.store.rdf.QuerySolution;

import java.util.ArrayList;
import java.util.Collection;

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
    public Collection<QuerySolution> match(final Repository repository) throws EvalException {
        if (origin != null) {
            Collection<QuerySolution> originalSolutions = origin.match(repository);
            Collection<QuerySolution> result = new ArrayList<>(originalSolutions.size());
            for (QuerySolution solution : originalSolutions) {
                if (Utils.evaluateBoolean(repository, solution, expression))
                    result.add(solution);
            }
            return result;
        } else {
            return new ArrayList<>();
        }
    }
}
