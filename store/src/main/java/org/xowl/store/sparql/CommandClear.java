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
 * Represents the SPARQL CLEAR command.
 * The CLEAR operation removes all the triples in the specified graph(s) in the Graph Store.
 * Here, the DEFAULT keyword is used to remove all triples in the default graph of the Graph Store, the NAMED keyword is used to remove all triples in all named graphs of the Graph Store and the ALL keyword is used to remove all triples in all graphs of the Graph Store.
 * The GRAPH keyword is used to remove all triples from a graph denoted by IRIref.
 * This operation is not required to remove the empty graphs from the Graph Store, but an implementation MAY decide to do so.
 * If the store records the existence of empty graphs, then the SPARQL 1.1 Update service, by default, SHOULD return failure if the specified graph does not exist.
 * If SILENT is present, the result of the operation will always be success.
 * Stores that do not record empty graphs will always return success.
 *
 * @author Laurent Wouters
 */
public class CommandClear {
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
    public CommandClear(GraphReferenceType targetType, String target, boolean isSilent) {
        this.targetType = targetType;
        this.target = target;
        this.isSilent = isSilent;
    }
}
