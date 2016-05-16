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
import org.xowl.infra.store.rdf.GraphNode;
import org.xowl.infra.store.storage.UnsupportedNodeType;

/**
 * Represents the SPARQL MOVE command.
 * The MOVE operation is a shortcut for moving all data from an input graph into a destination graph.
 * The input graph is removed after insertion and data from the destination graph, if any, is removed before insertion.
 * The difference between MOVE and the DROP/INSERT/DROP combination is that if MOVE is used to move a graph onto itself then no operation will be performed and the data will be left as it was.
 * Using DROP/INSERT/DROP in this situation would result in the graph being removed.
 * If the destination graph does not exist, it will be created.
 * By default, the service MAY return failure if the input graph does not exist.
 * If SILENT is present, the result of the operation will always be success.
 *
 * @author Laurent Wouters
 */
public class CommandMove implements Command {
    /**
     * The IRI of the origin graph
     */
    private final String origin;
    /**
     * The IRI of the target graph
     */
    private final String target;
    /**
     * Whether the operation shall be silent
     */
    private final boolean isSilent;

    /**
     * Initializes this command
     *
     * @param origin   The IRI of the origin graph
     * @param target   The IRI of the target graph
     * @param isSilent Whether the operation shall be silent
     */
    public CommandMove(String origin, String target, boolean isSilent) {
        this.origin = origin;
        this.target = target;
        this.isSilent = isSilent;
    }

    @Override
    public Result execute(Repository repository) {
        if (origin.equals(target))
            return ResultSuccess.INSTANCE;
        GraphNode graphOrigin = repository.getStore().getIRINode(origin);
        GraphNode graphTarget = repository.getStore().getIRINode(target);
        try {
            repository.getStore().move(graphOrigin, graphTarget);
            repository.getStore().commit();
            return ResultSuccess.INSTANCE;
        } catch (UnsupportedNodeType exception) {
            return new ResultFailure(exception.getMessage());
        }
    }
}
