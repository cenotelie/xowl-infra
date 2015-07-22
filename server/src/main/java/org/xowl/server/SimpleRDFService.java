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

import org.xowl.lang.owl2.Ontology;
import org.xowl.store.IRIMapper;
import org.xowl.store.Repository;
import org.xowl.store.rdf.ClosingQuadIterator;
import org.xowl.store.rdf.IRINode;
import org.xowl.store.rdf.Quad;
import org.xowl.store.writers.RDFSerializer;
import org.xowl.utils.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;

/**
 * Represents an RDF endpoint that serves RDF data from a base URI
 *
 * @author Laurent Wouters
 */
public class SimpleRDFService extends Service {
    /**
     * The logger
     */
    private final Logger logger;
    /**
     * The served repository
     */
    private final Repository repository;
    /**
     * The mapper to use
     */
    private final IRIMapper mapper;

    /**
     * Initializes this service
     *
     * @param logger     The logger
     * @param repository The served repository
     * @param mapper     The mapper to use
     */
    public SimpleRDFService(Logger logger, Repository repository, IRIMapper mapper) {
        this.logger = logger;
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public void onGet(HttpServletRequest request, HttpServletResponse response) {
        String uri = request.getRequestURI() + (request.getQueryString() != null ? "?" + request.getQueryString() : "");
        uri = mapper.get(uri);
        response.setCharacterEncoding("UTF-8");

        // is this an ontology?
        Ontology ontology = repository.getOntology(uri);
        if (ontology != null) {
            try (PrintWriter out = response.getWriter()) {
                String contentType = negotiateType(getContentTypes(request));
                response.setHeader("Content-Type", contentType);
                RDFSerializer serializer = getSerializer(contentType, out);
                Iterator<Quad> quads = repository.getStore().getAll(repository.getStore().getNodeIRI(ontology.getHasIRI().getHasValue()));
                serializer.serialize(logger, quads);
                out.flush();
            } catch (IOException exception) {
                logger.error(exception);
            }
            return;
        }

        // is this a known resource?
        IRINode node = repository.getStore().getNodeExistingIRI(uri);
        if (node != null) {
            try (PrintWriter out = response.getWriter()) {
                String contentType = negotiateType(getContentTypes(request));
                response.setHeader("Content-Type", contentType);
                RDFSerializer serializer = getSerializer(contentType, out);
                Iterator<Quad> quads = repository.getStore().getAll(null, node, null, null);
                quads = new ClosingQuadIterator(repository.getStore(), quads);
                serializer.serialize(logger, quads);
                out.flush();
            } catch (IOException exception) {
                logger.error(exception);
            }
            return;
        }

        // return 404
        response.setStatus(404);
    }
}
