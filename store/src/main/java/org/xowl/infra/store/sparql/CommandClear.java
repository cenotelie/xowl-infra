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

import org.xowl.infra.store.IRIs;
import org.xowl.infra.store.RepositoryRDF;
import org.xowl.infra.store.rdf.GraphNode;
import org.xowl.infra.store.rdf.IRINode;
import org.xowl.infra.store.rdf.Node;
import org.xowl.infra.store.storage.UnsupportedNodeType;

import java.util.Collection;
import java.util.Map;

/**
 * Represents the SPARQL CLEAR command.
 * The CLEAR operation removes all the triples in the specified graph(s) in the Graph Store.
 * Here, the DEFAULT keyword is used to remove all triples in the default graph of the Graph Store, the NAMED keyword is used to remove all triples in all named graphs of the Graph Store and the ALL keyword is used to remove all triples in all graphs of the Graph Store.
 * The GRAPH keyword is used to remove all triples from a graph denoted by IRIref.
 * This operation is not required to remove the empty graphs from the Graph Store, but an implementation MAY decide to do so.
 * The result of the operation will always be success.
 *
 * @author Laurent Wouters
 */
public class CommandClear implements Command {
    /**
     * The type of reference to the targets
     */
    private final GraphReferenceType type;
    /**
     * The IRI of the target graphs to drop
     */
    private final Collection<String> targets;
    /**
     * Whether the operation shall be silent
     */
    private final boolean isSilent;

    /**
     * Initializes this command
     *
     * @param type     The type of reference to the targets
     * @param targets  The IRI of the target graphs to drop
     * @param isSilent Whether the operation shall be silent
     */
    public CommandClear(GraphReferenceType type, Collection<String> targets, boolean isSilent) {
        this.type = type;
        this.targets = targets;
        this.isSilent = isSilent;
    }

    @Override
    public Result execute(RepositoryRDF repository) {
        try {
            switch (type) {
                case Single:
                    for (String target : targets)
                        repository.getStore().clear(repository.getStore().getIRINode(target));
                    repository.getStore().commit();
                    break;
                case Named:
                    Collection<GraphNode> targets = repository.getStore().getGraphs();
                    for (GraphNode target : targets) {
                        if (target.getNodeType() == Node.TYPE_IRI && !IRIs.GRAPH_DEFAULT.equals(((IRINode) target).getIRIValue()))
                            repository.getStore().clear(target);
                    }
                    repository.getStore().commit();
                    break;
                case Default:
                    repository.getStore().clear(repository.getStore().getIRINode(IRIs.GRAPH_DEFAULT));
                    repository.getStore().commit();
                    break;
                case All:
                    repository.getStore().clear();
                    repository.getStore().commit();
                    break;
            }
            return ResultSuccess.INSTANCE;
        } catch (UnsupportedNodeType exception) {
            return new ResultFailure(exception.getMessage());
        }
    }

    @Override
    public Command clone(Map<String, Node> parameters) {
        return new CommandClear(type, targets, isSilent);
    }
}
