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

import org.xowl.infra.store.RepositoryRDF;
import org.xowl.infra.store.rdf.Node;
import org.xowl.infra.store.rdf.Quad;
import org.xowl.infra.store.rdf.RDFPatternSolution;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

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
    public boolean isUpdateCommand() {
        return false;
    }

    @Override
    public Result execute(RepositoryRDF repository) {
        try {
            EvalContext context = new EvalContextRepository(repository);
            Solutions solutions = pattern.eval(context);
            Collection<Quad> quads = new ArrayList<>();
            for (RDFPatternSolution solution : solutions)
                Utils.instantiate(context, solution, template, quads);
            return new ResultQuads(quads);
        } catch (EvalException exception) {
            return new ResultFailure(exception.getMessage());
        }
    }

    @Override
    public Command clone(Map<String, Node> parameters) {
        List<Quad> template = new ArrayList<>(this.template.size());
        for (Quad quad : this.template)
            template.add(Utils.clone(quad, parameters));
        return new CommandConstruct(pattern.clone(parameters), template);
    }
}
