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

package org.xowl.store.rdf;

/**
 * Represents an observer of a query
 *
 * @author Laurent Wouters
 */
public interface QueryObserver {
    /**
     * When a new solution has been found
     *
     * @param solution The new solution
     */
    void onNewSolution(QuerySolution solution);

    /**
     * When a previous solution is revoked (probably due to changes in the input)
     *
     * @param solution The revoked solution
     */
    void onSolutionRevoked(QuerySolution solution);
}
