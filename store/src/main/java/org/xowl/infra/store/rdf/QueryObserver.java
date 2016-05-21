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

package org.xowl.infra.store.rdf;

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
    void onNewSolution(RDFPatternSolution solution);

    /**
     * When a previous solution is revoked (probably due to changes in the input)
     *
     * @param solution The revoked solution
     */
    void onSolutionRevoked(RDFPatternSolution solution);
}
