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

import org.xowl.infra.store.Repository;
import org.xowl.infra.store.rdf.Quad;
import org.xowl.infra.store.rdf.QuerySolution;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Represents the SPARQL CONSTRUCT command.
 * The CONSTRUCT query form returns a single RDF graph specified by a graph template.
 * The result is an RDF graph formed by taking each query solution in the solution sequence, substituting for the variables in the graph template, and combining the triples into a single RDF graph by set union.
 * If any such instantiation produces a triple containing an unbound variable or an illegal RDF construct, such as a literal in subject or predicate position, then that triple is not included in the output RDF graph.
 * The graph template can contain triples with no variables (known as ground or explicit triples), and these also appear in the output RDF graph returned by the CONSTRUCT query form.
 *
 * @author Laurent Wouters
 */
public class CommandConstruct implements Command {
    /**
     * The graph pattern for this command
     */
    private final GraphPattern pattern;
    /**
     * The template quads
     */
    private final Collection<Quad> template;

    /**
     * Initializes this command
     *
     * @param pattern  The graph pattern for this command
     * @param template The template quads
     */
    public CommandConstruct(GraphPattern pattern, Collection<Quad> template) {
        this.pattern = pattern;
        this.template = new ArrayList<>(template);
    }

    @Override
    public Result execute(Repository repository) {
        try {
            Solutions solutions = pattern.match(repository);
            Collection<Quad> quads = new ArrayList<>();
            for (QuerySolution solution : solutions)
                Utils.instantiate(repository, solution, template, quads);
            return new ResultQuads(quads);
        } catch (EvalException exception) {
            return new ResultFailure(exception.getMessage());
        }
    }
}
