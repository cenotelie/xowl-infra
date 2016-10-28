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
import org.xowl.infra.store.rdf.Node;

import java.util.Map;

/**
 * Represents the SPARQL CREATE command.
 * This operation creates a graph in the Graph Store.
 * For stores that record empty graphs, this will create a new empty graph in the store with a name specified by the IRI.
 * If the graph already exists, then a failure SHOULD be returned, except when the SILENT keyword is used; in either case, the contents of already existing graphs remain unchanged.
 * If the graph may not be created, then a failure MUST be returned, except when the SILENT keyword is used.
 * Stores that do not record empty named graphs will always return success on creation of a non-existing graph.
 *
 * @author Laurent Wouters
 */
public class CommandCreate implements Command {
    /**
     * The IRI of the target to create
     */
    private final String target;
    /**
     * Whether the operation shall be silent
     */
    private final boolean isSilent;

    /**
     * Initializes this command
     *
     * @param target   The IRI of the target to clear (or null)
     * @param isSilent Whether the operation shall be silent
     */
    public CommandCreate(String target, boolean isSilent) {
        this.target = target;
        this.isSilent = isSilent;
    }

    @Override
    public Result execute(RepositoryRDF repository) {
        return ResultSuccess.INSTANCE;
    }

    @Override
    public Command clone(Map<String, Node> parameters) {
        return new CommandCreate(target, isSilent);
    }
}
