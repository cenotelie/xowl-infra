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
import org.xowl.store.storage.NodeManager;

import java.util.Objects;

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
     * The type of reference to the origin
     */
    private final GraphReferenceType originType;
    /**
     * The IRI of the origin
     */
    private final String origin;
    /**
     * The type of reference to the target
     */
    private final GraphReferenceType targetType;
    /**
     * The IRI of the target
     */
    private final String target;
    /**
     * Whether the operation shall be silent
     */
    private final boolean isSilent;

    /**
     * Initializes this command
     *
     * @param originType The type of reference to the origin
     * @param origin     The IRI of the origin
     * @param targetType The type of reference to the target
     * @param target     The IRI of the target
     * @param isSilent   Whether the operation shall be silent
     */
    public CommandMove(GraphReferenceType originType, String origin, GraphReferenceType targetType, String target, boolean isSilent) {
        this.originType = originType;
        this.origin = origin;
        this.targetType = targetType;
        this.target = target;
        this.isSilent = isSilent;
    }

    @Override
    public Result execute(Repository repository) {
        if (originType == targetType && Objects.equals(origin, target))
            return ResultSuccess.INSTANCE;
        GraphNode graphOrigin = repository.getStore().getIRINode(originType == GraphReferenceType.Default ? NodeManager.DEFAULT_GRAPH : origin);
        GraphNode graphTarget = repository.getStore().getIRINode(targetType == GraphReferenceType.Default ? NodeManager.DEFAULT_GRAPH : target);
        repository.getStore().move(graphOrigin, graphTarget);
        return ResultSuccess.INSTANCE;
    }
}
