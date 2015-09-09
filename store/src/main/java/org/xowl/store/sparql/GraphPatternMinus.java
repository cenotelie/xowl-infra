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
import org.xowl.store.rdf.*;
import org.xowl.utils.collections.Couple;

import java.util.ArrayList;
import java.util.Collection;

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
    public Collection<QuerySolution> match(final Repository repository) throws EvalException {
        Collection<QuerySolution> originalSolutions = original.match(repository);
        Collection<QuerySolution> restrictingSolutions = restricting.match(repository);
        Collection<QuerySolution> result = new ArrayList<>();
        for (QuerySolution solution : originalSolutions) {
            boolean toRemove = false;
            for (QuerySolution restricting : restrictingSolutions) {
                boolean matching = true;
                for (Couple<VariableNode, Node> binding : restricting) {
                    Node value = solution.get(binding.x);
                    if (!org.xowl.store.rdf.Utils.same(binding.y, value)) {
                        matching = false;
                        break;
                    }
                }
                if (matching) {
                    toRemove = true;
                    break;
                }
            }
            if (!toRemove)
                result.add(solution);
        }
        return result;
    }
}
