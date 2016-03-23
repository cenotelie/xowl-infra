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

package org.xowl.infra.store.sparql;

import org.xowl.infra.store.Repository;
import org.xowl.infra.store.rdf.GraphNode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Represents the SPARQL COPY command.
 * The COPY operation is a shortcut for inserting all data from an input graph into a destination graph.
 * Data from the input graph is not affected, but data from the destination graph, if any, is removed before insertion.
 * The difference between COPY and the DROP/INSERT combination is that if COPY is used to copy a graph onto itself then no operation will be performed and the data will be left as it was.
 * Using DROP/INSERT in this situation would result in an empty graph.
 * If the destination graph does not exist, it will be created.
 * The result of the operation will always be success.
 *
 * @author Laurent Wouters
 */
public class CommandCopy implements Command {
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
    public CommandCopy(Collection<String> origins, Collection<String> targets, boolean isSilent) {
        this.origins = origins;
        this.targets = targets;
        this.isSilent = isSilent;
    }

    @Override
    public Result execute(Repository repository) {
        List<String> overwritten = new ArrayList<>();
        for (String origin : origins) {
            GraphNode graphOrigin = repository.getStore().getIRINode(origin);
            for (String target : targets) {
                if (!origin.equals(target)) {
                    boolean overwrite = !overwritten.contains(target);
                    GraphNode graphTarget = repository.getStore().getIRINode(target);
                    repository.getStore().copy(graphOrigin, graphTarget, overwrite);
                    if (overwrite)
                        overwritten.add(target);
                }
            }
        }
        repository.getStore().commit();
        return ResultSuccess.INSTANCE;
    }
}
