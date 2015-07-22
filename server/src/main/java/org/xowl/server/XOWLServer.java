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

import org.xowl.store.IRIMapper;
import org.xowl.store.Repository;
import org.xowl.utils.ConsoleLogger;
import org.xowl.utils.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Represents the entry-point servlet
 *
 * @author Laurent Wouters
 */
public class XOWLServer extends HttpServlet {
    /**
     * The service available on this server
     */
    private final Service service;

    /**
     * Initializes this server
     */
    public XOWLServer() {
        Logger logger = new ConsoleLogger();
        Service temp = null;
        try {
            Repository repository = new Repository();
            repository.activateEntailmentRules(logger);
            temp = newSimpleRDFService(logger, repository, "http://xowl.org");
        } catch (IOException exception) {
            logger.error(exception);
        }
        service = temp;
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        service.onGet(request, response);
    }

    /**
     * Creates a new simple RDF service serving the data in the specified repository
     *
     * @param logger     The logger
     * @param repository The repository to serve
     * @param baseURI    The base URI for this service
     * @return The service
     */
    public static Service newSimpleRDFService(Logger logger, Repository repository, String baseURI) {
        IRIMapper mapper = new IRIMapper();
        mapper.addRegexpMap(75, "(.*)\\?res=(.*)", "\\2");
        mapper.addRegexpMap(50, "(.*)", baseURI + "\\1");
        return new SimpleRDFService(logger, repository, mapper);
    }
}
