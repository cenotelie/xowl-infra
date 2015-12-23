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
import org.xowl.store.rdf.Changeset;
import org.xowl.store.rdf.Quad;
import org.xowl.store.storage.UnsupportedNodeType;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Represents the SPARQL INSERT DATA command.
 * The INSERT DATA operation adds some triples, given inline in the request, into the Graph Store.
 * Variables in QuadDatas are disallowed in INSERT DATA requests (see Notes 8 in the grammar).
 * That is, the INSERT DATA statement only allows to insert ground triples.
 * Blank nodes in QuadDatas are assumed to be disjoint from the blank nodes in the Graph Store, i.e., will be inserted with "fresh" blank nodes.
 * If no graph is described in the QuadData, then the default graph is presumed.
 * If data is inserted into a graph that does not exist in the Graph Store, it SHOULD be created
 * (there may be implementations providing an update service over a fixed set of graphs which in such case MUST return with failure for update requests that insert data into an unallowed graph).
 * Note that a triple MAY be considered to be "processed" with no action if that triple already exists in the graph.
 *
 * @author Laurent Wouters
 */
public class CommandInsertData implements Command {
    /**
     * The quads to insert
     */
    private final Collection<Quad> quads;

    /**
     * Initializes this command
     *
     * @param quads The quad to insert
     */
    public CommandInsertData(Collection<Quad> quads) {
        this.quads = new ArrayList<>(quads);
    }

    @Override
    public Result execute(Repository repository) {
        try {
            repository.getStore().insert(Changeset.fromAdded(quads));
            repository.getStore().commit();
            return ResultSuccess.INSTANCE;
        } catch (UnsupportedNodeType exception) {
            repository.getStore().rollback();
            return new ResultFailure(exception.getMessage());
        }
    }
}
