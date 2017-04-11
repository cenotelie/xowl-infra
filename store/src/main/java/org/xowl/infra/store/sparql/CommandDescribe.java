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
import org.xowl.infra.store.execution.EvaluationException;
import org.xowl.infra.store.rdf.*;
import org.xowl.infra.store.storage.UnsupportedNodeType;

import java.util.*;

/**
 * Represents the SPARQL DESCRIBE command.
 * The DESCRIBE form returns a single result RDF graph containing RDF data about resources.
 * This data is not prescribed by a SPARQL query, where the query client would need to know the structure of the RDF in the data source, but, instead, is determined by the SPARQL query processor.
 * The query pattern is used to create a result set.
 * The DESCRIBE form takes each of the resources identified in a solution, together with any resources directly named by IRI, and assembles a single RDF graph by taking a "description" which can come from any information available including the target RDF Dataset.
 * The description is determined by the query service.
 * The syntax DESCRIBE * is an abbreviation that describes all of the variables in a query.
 *
 * @author Laurent Wouters
 */
public class CommandDescribe implements Command {
    /**
     * The graph pattern for this command
     */
    private final GraphPattern pattern;
    /**
     * The bound variables to describe
     */
    private final Collection<VariableNode> variables;
    /**
     * The plain IRIs to describe
     */
    private final Collection<String> iris;

    @Override
    public boolean isUpdateCommand() {
        return false;
    }

    /**
     * Initializes this command
     *
     * @param pattern The graph pattern for this command
     */
    public CommandDescribe(GraphPattern pattern) {
        this.pattern = pattern;
        this.variables = new ArrayList<>();
        this.iris = new ArrayList<>();
    }

    /**
     * Adds a new variable to describe
     *
     * @param variable A variable node bound by the pattern
     */
    public void addTargetVariable(VariableNode variable) {
        this.variables.add(variable);
    }

    /**
     * Adds a new IRI to describe
     *
     * @param iri A new IRI to describe
     */
    public void addTargetIRI(String iri) {
        this.iris.add(iri);
    }

    @Override
    public Result execute(RepositoryRDF repository) {
        try {
            Collection<Quad> buffer = new ArrayList<>();
            if (variables.isEmpty() && !iris.isEmpty()) {
                // only describe static resource
                for (String iri : iris)
                    describe(repository, repository.getStore().getIRINode(iri), buffer);
            } else {
                Solutions solutions = pattern.eval(new EvalContextRepository(repository));
                List<SubjectNode> explored = new ArrayList<>();
                for (RDFPatternSolution solution : solutions) {
                    for (VariableNode variable : variables) {
                        Node target = solution.get(variable);
                        if (target == null)
                            throw new EvaluationException("Unbound variable " + variable.getName());
                        if ((target.getNodeType() & Node.FLAG_SUBJECT) == Node.FLAG_SUBJECT && !explored.contains(target)) {
                            SubjectNode subject = (SubjectNode) target;
                            describe(repository, subject, buffer);
                            explored.add(subject);
                        }
                    }
                }
                for (String iri : iris) {
                    IRINode subject = repository.getStore().getIRINode(iri);
                    if (!explored.contains(subject)) {
                        describe(repository, subject, buffer);
                        explored.add(subject);
                    }
                }
            }

            // closes the results over blank nodes
            Collection<Quad> result = new ArrayList<>();
            ClosingQuadIterator closure = new ClosingQuadIterator(repository.getStore(), buffer.iterator());
            while (closure.hasNext())
                result.add(closure.next());

            return new ResultQuads(result);
        } catch (EvaluationException | UnsupportedNodeType exception) {
            return new ResultFailure(exception.getMessage());
        }
    }

    /**
     * Describes a resource
     *
     * @param repository The repository that contains the information
     * @param node       The node representing the resource to describe
     * @param quads      The buffer of quads
     */
    private void describe(RepositoryRDF repository, SubjectNode node, Collection<Quad> quads) throws UnsupportedNodeType {
        Iterator<Quad> iterator = repository.getStore().getAll(node, null, null);
        while (iterator.hasNext())
            quads.add(iterator.next());
    }

    @Override
    public Command clone(Map<String, Node> parameters) {
        CommandDescribe result = new CommandDescribe(pattern.clone(parameters));
        for (VariableNode variable : this.variables)
            result.variables.add((VariableNode) Utils.clone(variable, parameters));
        result.iris.addAll(this.iris);
        return result;
    }
}
