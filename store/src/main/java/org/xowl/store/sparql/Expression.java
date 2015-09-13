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
 * Represents an expression in SPARQL
 *
 * @author Laurent Wouters
 */
public interface Expression {
    /**
     * Evaluates this expression
     *
     * @param repository The repository to evaluate on
     * @param bindings   The current bindings
     * @return The result
     * @throws EvalException When an error occurs during the evaluation
     */
    Object eval(Repository repository, QuerySolution bindings) throws EvalException;

    /**
     * Evaluates this expression
     *
     * @param repository The repository to evaluate on
     * @param solutions  The current set of solutions
     * @return The result
     */
    Object eval(Repository repository, Collection<QuerySolution> solutions) throws EvalException;
}
