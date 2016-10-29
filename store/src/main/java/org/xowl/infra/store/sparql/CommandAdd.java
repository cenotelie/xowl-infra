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
import org.xowl.infra.store.rdf.GraphNode;
import org.xowl.infra.store.rdf.Node;
import org.xowl.infra.store.storage.UnsupportedNodeType;

import java.util.Collection;
import java.util.Map;

/**
 * Represents the SPARQL ADD command.
 * The ADD operation is a shortcut for inserting all data from an input graph into a destination graph.
 * Data from the input graph is not affected, and initial data from the destination graph, if any, is kept intact.
 * If the destination graph does not exist, it will be created.
 * The result of the operation will always be success.
 *
 * @author Laurent Wouters
 */
public class CommandAdd implements Command {
    /**
     * The IRI of the origin graphs
     */
    private final Collection<String> origins;
    /**
     * The IRI of the target graphs
     */
    private final Collection<String> targets;
    /**
     * Whether the operation shall be silent
     */
    private final boolean isSilent;

    /**
     * Initializes this command
     *
     * @param origins  The IRI of the origin graphs
     * @param targets  The IRI of the target graphs
     * @param isSilent Whether the operation shall be silent
     */
    public CommandAdd(Collection<String> origins, Collection<String> targets, boolean isSilent) {
        this.origins = origins;
        this.targets = targets;
        this.isSilent = isSilent;
    }

    @Override
    public boolean isUpdateCommand() {
        return true;
    }

    @Override
    public Result execute(RepositoryRDF repository) {
        for (String origin : origins) {
            GraphNode graphOrigin = repository.getStore().getIRINode(origin);
            for (String target : targets) {
                if (!origin.equals(target)) {
                    GraphNode graphTarget = repository.getStore().getIRINode(target);
                    try {
                        repository.getStore().copy(graphOrigin, graphTarget, false);
                    } catch (UnsupportedNodeType exception) {
                        return new ResultFailure(exception.getMessage());
                    }
                }
            }
        }
        repository.getStore().commit();
        return ResultSuccess.INSTANCE;
    }

    @Override
    public Command clone(Map<String, Node> parameters) {
        return new CommandAdd(origins, targets, isSilent);
    }
}
