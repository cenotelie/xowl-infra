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

import org.xowl.store.rdf.QuerySolution;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * Represents the result of a SPARQL command as a set of query solutions
 *
 * @author Laurent Wouters
 */
public class ResultSolutions implements Result {
    /**
     * The solutions
     */
    private final Collection<QuerySolution> solutions;

    /**
     * Gets the solutions
     *
     * @return The solutions
     */
    public Collection<QuerySolution> getSolutions() {
        return Collections.unmodifiableCollection(solutions);
    }

    /**
     * Initializes this result
     *
     * @param solutions The solutions
     */
    public ResultSolutions(Collection<QuerySolution> solutions) {
        this.solutions = new ArrayList<>(solutions);
    }

    @Override
    public boolean isFailure() {
        return false;
    }

    @Override
    public boolean isSuccess() {
        return true;
    }
}
