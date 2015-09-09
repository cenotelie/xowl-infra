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

package org.xowl.store.sparql;

import org.xowl.store.Repository;
import org.xowl.store.rdf.GraphNode;
import org.xowl.store.rdf.IRINode;
import org.xowl.store.rdf.Node;
import org.xowl.store.storage.NodeManager;

import java.util.Collection;

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
     * The type of reference to the target
     */
    private final GraphReferenceType targetType;
    /**
     * The IRI of the target to clear (or null)
     */
    private final String target;
    /**
     * Whether the operation shall be silent
     */
    private final boolean isSilent;

    /**
     * Initializes this command
     *
     * @param targetType The type of reference to the target
     * @param target     The IRI of the target to clear (or null)
     * @param isSilent   Whether the operation shall be silent
     */
    public CommandDrop(GraphReferenceType targetType, String target, boolean isSilent) {
        this.targetType = targetType;
        this.target = target;
        this.isSilent = isSilent;
    }

    @Override
    public Result execute(Repository repository) {
        switch (targetType) {
            case Single:
                if (target != null)
                    repository.getStore().clear(repository.getStore().getIRINode(target));
                break;
            case Named:
                Collection<GraphNode> targets = repository.getStore().getGraphs();
                for (GraphNode target : targets) {
                    if (target.getNodeType() == Node.TYPE_IRI && !NodeManager.DEFAULT_GRAPH.equals(((IRINode) target).getIRIValue()))
                        repository.getStore().clear(target);
                }
                break;
            case Default:
                repository.getStore().clear(repository.getStore().getIRINode(NodeManager.DEFAULT_GRAPH));
                break;
            case All:
                repository.getStore().clear();
                break;
        }
        return ResultSuccess.INSTANCE;
    }
}
