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

/**
 * Represents the SPARQL COPY command.
 * The COPY operation is a shortcut for inserting all data from an input graph into a destination graph.
 * Data from the input graph is not affected, but data from the destination graph, if any, is removed before insertion.
 * The difference between COPY and the DROP/INSERT combination is that if COPY is used to copy a graph onto itself then no operation will be performed and the data will be left as it was.
 * Using DROP/INSERT in this situation would result in an empty graph.
 * If the destination graph does not exist, it will be created.
 * By default, the service MAY return failure if the input graph does not exist.
 * If SILENT is present, the result of the operation will always be success.
 *
 * @author Laurent Wouters
 */
public class CommandCopy {
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
    public CommandCopy(GraphReferenceType originType, String origin, GraphReferenceType targetType, String target, boolean isSilent) {
        this.originType = originType;
        this.origin = origin;
        this.targetType = targetType;
        this.target = target;
        this.isSilent = isSilent;
    }
}
