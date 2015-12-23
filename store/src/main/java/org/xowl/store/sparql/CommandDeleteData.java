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
 * Represents the SPARQL DELETE DATA command.
 * The DELETE DATA operation removes some triples, given inline in the request, if the respective graphs in the Graph Store contain those.
 * QuadData denotes triples to be removed and is as described in INSERT DATA, with the difference that in a DELETE DATA operation neither variables nor blank nodes are allowed (see Notes 8+9 in the grammar).
 * As with INSERT DATA, DELETE DATA is meant for deletion of ground triples data which is why QuadData that contains variables or blank nodes is disallowed in DELETE DATA operations.
 * The DELETE/INSERT operation can be used to remove triples containing blank nodes.
 * Note that the deletion of non-existing triples has no effect, i.e., triples in the QuadData that did not exist in the Graph Store are ignored.
 * Blank nodes are not permitted in the QuadData, as these do not match any existing data.
 *
 * @author Laurent Wouters
 */
public class CommandDeleteData implements Command {
    /**
     * The quads to delete
     */
    private final Collection<Quad> quads;

    /**
     * Initializes this command
     *
     * @param quads The quad to delete
     */
    public CommandDeleteData(Collection<Quad> quads) {
        this.quads = new ArrayList<>(quads);
    }

    @Override
    public Result execute(Repository repository) {
        try {
            repository.getStore().insert(Changeset.fromRemoved(quads));
            repository.getStore().commit();
            return ResultSuccess.INSTANCE;
        } catch (UnsupportedNodeType exception) {
            repository.getStore().rollback();
            return new ResultFailure(exception.getMessage());
        }
    }
}
