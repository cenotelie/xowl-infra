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

import java.util.Collection;

/**
 * A SPARQL expression verifying whether a graph pattern exists
 *
 * @author Laurent Wouters
 */
public class ExpressionExists implements Expression {
    /**
     * The pattern to match
     */
    private final GraphPattern pattern;

    /**
     * Initializes this expression
     *
     * @param pattern The pattern to match
     */
    public ExpressionExists(GraphPattern pattern) {
        this.pattern = pattern;
    }

    @Override
    public Object eval(Repository repository, QuerySolution bindings) throws EvalException {
        Collection<QuerySolution> result = pattern.match(repository);
        return !result.isEmpty();
    }

    @Override
    public Object eval(Repository repository, Collection<QuerySolution> solutions) throws EvalException {
        Collection<QuerySolution> result = pattern.match(repository);
        return !result.isEmpty();
    }
}
