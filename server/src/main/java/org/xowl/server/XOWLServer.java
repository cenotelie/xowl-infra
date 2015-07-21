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

import org.xowl.store.ProxyObject;
import org.xowl.store.Repository;
import org.xowl.store.Vocabulary;
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
     * The logger
     */
    private final Logger logger;
    /**
     * The service available on this server
     */
    private final Service service;

    /**
     * Initializes this server
     */
    public XOWLServer() {
        logger = new ConsoleLogger();
        Service temp = null;
        try {
            Repository repository = new Repository();
            ProxyObject test = repository.resolveProxy(repository.resolveOntology("http://xowl.org/server/"), "http://xowl.org/server/test");
            test.setValue(Vocabulary.rdfType, repository.resolveProxy(Vocabulary.owlClass));
            temp = new RDFEndpoint("http://xowl.org", logger, repository.getStore());
        } catch (IOException exception) {
            // do nothing
        }
        service = temp;
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        service.onGet(request, response);
    }
}
