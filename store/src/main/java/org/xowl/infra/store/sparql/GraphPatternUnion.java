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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

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
    public Solutions eval(EvalContext context) throws EvaluationException {
        SolutionsMultiset result = new SolutionsMultiset();
        for (GraphPattern element : elements)
            for (RDFPatternSolution solution : element.eval(context))
                result.add(solution);
        return result;
    }

    @Override
    public void inspect(Inspector inspector) {
        inspector.onGraphPattern(this);
        for (GraphPattern pattern : elements)
            pattern.inspect(inspector);
    }

    @Override
    public GraphPattern clone(Map<String, Node> parameters) {
        List<GraphPattern> elements = new ArrayList<>(this.elements.size());
        for (GraphPattern element : this.elements) {
            elements.add(element.clone(parameters));
        }
        return new GraphPatternUnion(elements);
    }
}
