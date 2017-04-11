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

package org.xowl.infra.store.sparql;

import org.xowl.infra.store.execution.EvaluationException;
import org.xowl.infra.store.rdf.Node;
import org.xowl.infra.store.rdf.RDFPatternSolution;
import org.xowl.infra.store.rdf.VariableNode;
import org.xowl.infra.utils.collections.Couple;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * A graph pattern represented by explicit (variable, value) associations
 *
 * @author Laurent Wouters
 */
public class GraphPatternInlineData implements GraphPattern {
    /**
     * The inline data
     */
    private final Collection<RDFPatternSolution> data;

    /**
     * Initializes this graph pattern
     *
     * @param data The inline data
     */
    public GraphPatternInlineData(Collection<RDFPatternSolution> data) {
        this.data = new ArrayList<>(data);
    }

    @Override
    public Solutions eval(EvalContext context) throws EvaluationException {
        return new SolutionsMultiset(data);
    }

    @Override
    public void inspect(Inspector inspector) {
        inspector.onGraphPattern(this);
    }

    @Override
    public GraphPattern clone(Map<String, Node> parameters) {
        Collection<RDFPatternSolution> data = new ArrayList<>(this.data.size());
        for (RDFPatternSolution solution : this.data) {
            List<Couple<VariableNode, Node>> bindings = new ArrayList<>(solution.size());
            for (Couple<VariableNode, Node> binding : solution) {
                bindings.add(new Couple<>(
                        (VariableNode) Utils.clone(binding.x, parameters),
                        Utils.clone(binding.y, parameters)
                ));
            }
            data.add(new RDFPatternSolution(bindings));
        }
        return new GraphPatternInlineData(data);
    }
}
