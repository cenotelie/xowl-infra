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

import org.xowl.infra.store.Repository;
import org.xowl.infra.store.rdf.Changeset;
import org.xowl.infra.store.rdf.Quad;
import org.xowl.infra.store.rdf.QuerySolution;
import org.xowl.infra.store.storage.UnsupportedNodeType;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Represents the SPARQL DELETE/INSERT command.
 * The DELETE/INSERT operation can be used to remove or add triples from/to the Graph Store based on bindings for a query pattern specified in a WHERE clause.
 * This operation identifies data with the WHERE clause, which will be used to compute solution sequences of bindings for a set of variables.
 * The bindings for each solution are then substituted into the DELETE template to remove triples, and then in the INSERT template to create new triples.
 * If any solution produces a triple containing an unbound variable or an illegal RDF construct, such as a literal in a subject or predicate position, then that triple is not included when processing the operation:
 * INSERT will not instantiate new data in the output graph, and DELETE will not remove anything.
 * The graphs used for computing a solution sequence may be different to the graphs modified with the DELETE and INSERT templates.
 * The WITH clause defines the graph that will be modified or matched against for any of the subsequent elements (in DELETE, INSERT, or WHERE clauses) if they do not specify a graph explicitly.
 * If not provided, then the default graph of the Graph Store (or an explicitly declared dataset in the WHERE clause) will be assumed.
 * That is, a WITH clause may be viewed as syntactic sugar for wrapping both the QuadPatterns in subsequent DELETE and INSERT clauses, and likewise the GroupGraphPattern in the subsequent WHERE clause into GRAPH patterns.
 * This can be used to avoid referring to the same graph multiple times in a single operation.
 * Following the optional WITH clause are the INSERT and/or DELETE clauses.
 * The deletion of the triples happens before the insertion.
 * The pattern in the WHERE clause is evaluated only once, before the delete part of the operation is performed.
 * The overall processing model is that the pattern is executed, the results used to instantiate the DELETE template, the deletes performed, the results used again to instantiate the INSERT template, and the inserts performed.
 * If the DELETE clause is omitted, then the operation only inserts data (see INSERT).
 * If the INSERT clause is omitted, then the operation only removes data (see DELETE).
 * The grammar does not permit both DELETE and INSERT to be omitted in the same operation.
 * The USING and USING NAMED clauses affect the RDF Dataset used while evaluating the WHERE clause.
 * This describes a dataset in the same way as FROM and FROM NAMED clauses describe RDF Datasets in the SPARQL 1.1 Query Language.
 * The keyword USING instead of FROM in update requests is to avoid possible ambiguities which could arise from writing "DELETE FROM".
 * That is, the GroupGraphPattern in the WHERE clause will be matched against the dataset described by explicit USING or USING NAMED clauses, if specified, and against the Graph Store otherwise.
 * The WITH clause provides a convenience for when an operation primarily refers to a single graph.
 * If a graph name is specified in a WITH clause, then - for the purposes of evaluating the WHERE clause - this will define an RDF Dataset containing a default graph with the specified name, but only in the absence of USING or USING NAMED clauses.
 * In the presence of one or more graphs referred to in USING clauses and/or USING NAMED clauses, the WITH clause will be ignored while evaluating the WHERE clause.
 * The GroupGraphPattern in the WHERE clause is evaluated as in a SPARQL query "SELECT * WHERE GroupGraphPattern" and all the solution bindings are applied to the preceding DELETE and INSERT templates for defining the triples to be deleted from or inserted into the Graph Store.
 * Again, QuadPatterns are formed by TriplesTemplates, i.e., sets of triple patterns, optionally wrapped into a GRAPH block, where the GRAPH clause indicates the named graph in the Graph Store to be updated;
 * on any TripleTemplates without a GRAPH clause, the INSERT or DELETE clauses applies to the graph specified by the WITH clause, or the default graph of the Graph Store if no WITH clause is present.
 *
 * @author Laurent Wouters
 */
public class CommandModify implements Command {
    /**
     * The graph pattern to match, or null
     */
    private final GraphPattern where;
    /**
     * The template quads to insert
     */
    private final Collection<Quad> insert;
    /**
     * The template quads to delete
     */
    private final Collection<Quad> delete;

    /**
     * Initializes this command
     *
     * @param insert The template quads to insert
     * @param delete The template quads to delete
     * @param where  The graph pattern to match
     */
    public CommandModify(Collection<Quad> insert, Collection<Quad> delete, GraphPattern where) {
        this.where = where;
        this.insert = insert;
        this.delete = delete;
    }

    @Override
    public Result execute(Repository repository) {
        try {
            Solutions solutions = where.match(repository);
            Collection<Quad> toInsert = new ArrayList<>();
            Collection<Quad> toRemove = new ArrayList<>();
            for (QuerySolution solution : solutions) {
                Utils.instantiate(repository, solution, insert, toInsert);
                Utils.instantiate(repository, solution, delete, toRemove);
            }
            repository.getStore().insert(Changeset.fromAddedRemoved(toInsert, toRemove));
            repository.getStore().commit();
        } catch (UnsupportedNodeType | EvalException exception) {
            repository.getStore().rollback();
            return new ResultFailure(exception.getMessage());
        }
        return ResultSuccess.INSTANCE;
    }
}
