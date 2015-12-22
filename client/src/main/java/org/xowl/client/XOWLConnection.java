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

package org.xowl.client;

import org.xowl.store.storage.remote.HTTPConnection;
import org.xowl.store.xsp.XSPReply;

/**
 * Represents a connection to a xOWL server using the xOWL Server Protocol
 *
 * @author Laurent Wouters
 */
public class XOWLConnection extends HTTPConnection {
    /**
     * Initializes this connection
     *
     * @param endpoint URI of the endpoint
     * @param login    Login for the endpoint, if any, used for an HTTP Basic authentication
     * @param password Password for the endpoint, if any, used for an HTTP Basic authentication
     */
    public XOWLConnection(String endpoint, String login, String password) {
        super(endpoint, login, password);
    }

    /**
     * Executes a command on the server
     *
     * @param command The command to execute
     * @return The response
     */
    public String execute(String command) {
        XSPReply response = xsp(command);
        return response.serializedString();
    }
}
