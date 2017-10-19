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

import fr.cenotelie.commons.utils.collections.Couple;
import org.xowl.infra.store.execution.EvaluationException;
import org.xowl.infra.store.rdf.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/**
 * A graph pattern represented by a template of quads
 *
 * @author Laurent Wouters
 */
public class GraphPatternQuads implements GraphPattern {
    /**
     * The RDF pattern
     */
    private final RDFPattern pattern;

    /**
     * Gets the RDF query represented by this pattern
     *
     * @return The RDF query
     */
    public RDFPattern getPattern() {
        return pattern;
    }

    /**
     * Initializes this pattern
     */
    public GraphPatternQuads() {
        this.pattern = new RDFPattern();
    }

    /**
     * Initializes this pattern
     *
     * @param pattern The RDF pattern
     */
    private GraphPatternQuads(RDFPattern pattern) {
        this.pattern = pattern;
    }

    /**
     * Adds positive quads to this pattern
     *
     * @param quads The quads to add
     */
    public void addPositives(Collection<Quad> quads) {
        pattern.getPositives().addAll(quads);
    }

    /**
     * Adds a conjunction of negative quads to this pattern
     *
     * @param quads The conjunction of negative quads
     */
    public void addNegatives(Collection<Quad> quads) {
        pattern.getNegatives().add(quads);
    }

    @Override
    public Solutions eval(EvalContext context) throws EvaluationException {
        if (pattern.getPositives().isEmpty() && pattern.getNegatives().isEmpty()) {
            // for an empty query return a single solution with no binding
            // this is because an empty match pattern matches all graphs, including the empty one
            SolutionsMultiset result = new SolutionsMultiset(1);
            result.add(new RDFPatternSolution(new ArrayList<Couple<VariableNode, Node>>()));
            return result;
        }
        return context.getSolutions(pattern);
    }

    @Override
    public void inspect(Inspector inspector) {
        inspector.onGraphPattern(this);
    }

    @Override
    public GraphPattern clone(Map<String, Node> parameters) {
        return new GraphPatternQuads(Utils.clone(pattern, parameters));
    }
}
