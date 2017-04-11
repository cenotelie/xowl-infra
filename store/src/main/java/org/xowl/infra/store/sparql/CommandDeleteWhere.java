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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Represents the SPARQL DELETE WHERE command.
 * The DELETE WHERE operation is a shortcut form for the DELETE/INSERT operation where bindings matched by the WHERE clause are used to define the triples in a graph that will be deleted.
 * Analogous to DELETE/INSERT, deleting triples that are not present, or from a graph that is not present will have no effect and will result in success.
 * The QuadPattern is used both as a pattern for matching against triples and graphs, and as the template for deletion.
 * If any TripleTemplates within the QuadPattern appear in the scope of a GRAPH clause then this will determine the graph that that template is matched on, and also the graph from which any matching triples will be removed.
 * Any TripleTemplates not in the scope of a GRAPH clause will be matched against/removed from the default graph.
 *
 * @author Laurent Wouters
 */
public class CommandDeleteWhere implements Command {
    /**
     * The quads to delete
     */
    private final Collection<Quad> quads;

    /**
     * Initializes this command
     *
     * @param quads The quad to delete
     */
    public CommandDeleteWhere(Collection<Quad> quads) {
        this.quads = new ArrayList<>(quads);
    }

    @Override
    public boolean isUpdateCommand() {
        return true;
    }

    @Override
    public Result execute(RepositoryRDF repository) {
        RDFQuery query = new RDFQuery();
        query.getPositives().addAll(quads);
        Collection<RDFPatternSolution> solutions = repository.getRDFQueryEngine().execute(query);
        Collection<Quad> toRemove = new ArrayList<>();
        try {
            EvalContext context = new EvalContextRepository(repository);
            for (RDFPatternSolution solution : solutions)
                Utils.instantiate(context, solution, quads, toRemove);
            repository.getStore().insert(Changeset.fromRemoved(toRemove));
            repository.getStore().commit();
        } catch (UnsupportedNodeType | EvaluationException exception) {
            repository.getStore().rollback();
            return new ResultFailure(exception.getMessage());
        }
        return ResultSuccess.INSTANCE;
    }

    @Override
    public Command clone(Map<String, Node> parameters) {
        List<Quad> quads = new ArrayList<>(this.quads.size());
        for (Quad quad : this.quads)
            quads.add(Utils.clone(quad, parameters));
        return new CommandDeleteWhere(quads);
    }
}
