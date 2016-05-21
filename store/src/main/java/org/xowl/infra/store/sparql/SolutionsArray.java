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

import org.xowl.infra.store.rdf.RDFPatternSolution;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * An array of SPARQL solutions
 *
 * @author Laurent Wouters
 */
class SolutionsArray implements Solutions {
    /**
     * The content
     */
    private final List<RDFPatternSolution> content;

    /**
     * Initializes the solutions
     */
    public SolutionsArray() {
        content = new ArrayList<>();
    }

    /**
     * Adds a new solution to this set
     *
     * @param solution The new solution
     */
    public void add(RDFPatternSolution solution) {
        content.add(solution);
    }

    @Override
    public int size() {
        return content.size();
    }

    @Override
    public Iterator<RDFPatternSolution> iterator() {
        return content.iterator();
    }
}
