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
 * Represents the SPARQL DROP command.
 * The DROP operation removes the specified graph(s) from the Graph Store.
 * The GRAPH keyword is used to remove a graph denoted by IRIref, the DEFAULT keyword is used to remove the default graph from the Graph Store, the NAMED keyword is used to remove all named graphs from the Graph Store, and the ALL keyword is used to remove all graphs from the Graph Store, i.e., resetting the store.
 * After successful completion of this operation, the specified graphs are no longer available for further graph update operations.
 * However, in case the DEFAULT graph of the Graph Store is dropped, implementations MUST restore it after it was removed, i.e., DROP DEFAULT is equivalent to CLEAR DEFAULT.
 * If the store records the existence of empty graphs, then the SPARQL 1.1 Update service, by default, SHOULD return failure if the specified named graph does not exist.
 * If SILENT is present, the result of the operation will always be success.
 * Stores that do not record empty graphs will always return success.
 *
 * @author Laurent Wouters
 */
public class CommandDrop implements Command {
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
    public CommandDrop(GraphReferenceType type, Collection<String> targets, boolean isSilent) {
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
        return new CommandDrop(type, targets, isSilent);
    }
}
