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

package org.xowl.server;

import org.xowl.store.rdf.IRINode;
import org.xowl.store.rdf.RDFStore;
import org.xowl.store.writers.NTripleSerializer;
import org.xowl.utils.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Represents an RDF endpoint that serves RDF data from a base URI
 *
 * @author Laurent Wouters
 */
public class RDFEndpoint extends Service {
    /**
     * The base URI for the served data
     */
    private final String baseURI;
    /**
     * The logger
     */
    private final Logger logger;
    /**
     * The backend store
     */
    private final RDFStore store;

    /**
     * Gets the base URI for the served data
     *
     * @return The base URI for the served data
     */
    public String getBaseURI() {
        return baseURI;
    }

    /**
     * Initializes this server
     *
     * @param baseURI The base URI for the served data
     * @param logger  The logger
     * @param store   The backend store
     */
    public RDFEndpoint(String baseURI, Logger logger, RDFStore store) {
        this.baseURI = baseURI;
        this.logger = logger;
        this.store = store;
    }

    @Override
    public void onGet(HttpServletRequest request, HttpServletResponse response) {
        String uri = baseURI + request.getRequestURI();
        response.setHeader("title", uri);
        IRINode node = store.getNodeExistingIRI(uri);
        if (node != null) {
            try {
                PrintWriter out = response.getWriter();
                NTripleSerializer serializer = new NTripleSerializer(out);
                serializer.serialize(logger, store.getAll(null, node, null, null));
                out.flush();
                out.close();
            } catch (IOException exception) {
                logger.error(exception);
            }
        }
    }
}
