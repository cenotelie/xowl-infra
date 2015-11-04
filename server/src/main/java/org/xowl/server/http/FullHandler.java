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

package org.xowl.server.http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.xowl.server.db.Controller;

import java.io.IOException;

/**
 * The aggregated HTTP handler that supports pure SPARQL and xOWL extensions
 *
 * @author Laurent Wouters
 */
class FullHandler implements HttpHandler {

    /**
     * Initializes this handler
     * @param controller The current controller
     */
    public FullHandler(Controller controller) {

    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {

    }
}
