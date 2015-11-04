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
import org.xowl.server.db.Database;
import org.xowl.server.db.User;

/**
 * A part of a HTTP handler
 *
 * @author Laurent Wouters
 */
interface HandlerPart {
    /**
     * Handles an HTTP exchange
     *
     * @param httpExchange The HTTP exchange
     * @param method       The HTTP method
     * @param contentType  The content type for the request body
     * @param body         The request body
     * @param user         The current user
     * @param database     The current database
     */
    void handle(HttpExchange httpExchange, String method, String contentType, String body, User user, Database database);
}
