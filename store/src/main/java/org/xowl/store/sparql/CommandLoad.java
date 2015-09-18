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
import org.xowl.store.storage.NodeManager;
import org.xowl.utils.SinkLogger;

/**
 * Represents the SPARQL LOAD command.
 * The LOAD operation reads an RDF document from a IRI and inserts its triples into the specified graph in the Graph Store.
 * The specified destination graph SHOULD be created if required; again, implementations providing an update service over a fixed set of graphs MUST return with failure for a request that would create a disallowed graph.
 * If the destination graph already exists, then no data in that graph will be removed.
 * IRIref_from specifies the IRI of a document such that a store will be able to identify, locate and read the document.
 * The most common form will be URLs with the http IRI schemes.
 * Once the document has been read, the resulting triples will be inserted into the destination graph named by the IRI referred to by IRIref_to.
 * If no destination graph IRI (IRIref_to) is provided to load the triples into, then the data will be loaded into the default graph.
 * In case no RDF data can be retrieved (as opposed to the empty graph being retrieved) from the IRI denoted by IRIref_from, or in case the retrieval method returns an error (such as, for instance an HTTP error code),
 * the SPARQL 1.1 Update service SHOULD return failure and the status of the Graph Store SHOULD remain in the same status as prior to the request;
 * in case the keyword SILENT is present, however, the operation will still return success and the status of the Graph Store is not specified by the present document:
 * implementations may create the destination graph or not and partially load data, in case of a transmission error where partial data has been received
 * (which itself may be legal RDF).
 *
 * @author Laurent Wouters
 */
public class CommandLoad implements Command {
    /**
     * The IRI of the resource to load
     */
    private final String iri;
    /**
     * The IRI of the target graph to load into (or null)
     */
    private final String target;
    /**
     * Whether the operation shall be silent
     */
    private final boolean isSilent;

    /**
     * Initializes this command
     *
     * @param iri      The IRI of the resource to load
     * @param target   The IRI of the target graph to load into (or null)
     * @param isSilent Whether the operation shall be silent
     */
    public CommandLoad(String iri, String target, boolean isSilent) {
        this.iri = iri;
        this.target = target;
        this.isSilent = isSilent;
    }

    @Override
    public Result execute(Repository repository) {
        SinkLogger logger = new SinkLogger();
        repository.load(logger, iri, target == null ? NodeManager.DEFAULT_GRAPH : target, true);
        return !logger.isOnError() || isSilent ? ResultSuccess.INSTANCE : ResultFailure.INSTANCE;
    }
}
