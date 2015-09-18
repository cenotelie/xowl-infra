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
 * Represents the union of multiple graph patterns
 *
 * @author Laurent Wouters
 */
public class GraphPatternUnion implements GraphPattern {
    /**
     * The sub elements
     */
    private final Collection<GraphPattern> elements;

    /**
     * Initializes this pattern
     *
     * @param elements The sub elements
     */
    public GraphPatternUnion(Collection<GraphPattern> elements) {
        this.elements = new ArrayList<>(elements);
    }

    @Override
    public Solutions match(final Repository repository) throws EvalException {
        SolutionsMultiset result = new SolutionsMultiset();
        for (GraphPattern element : elements)
            for (QuerySolution solution : element.match(repository))
                result.add(solution);
        return result;
    }
}
