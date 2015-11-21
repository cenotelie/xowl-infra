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

import org.xowl.store.storage.remote.XSPConnection;

/**
 * Represents a connection to a xOWL server using the xOWL Server Protocol
 *
 * @author Laurent Wouters
 */
public class XOWLConnection extends XSPConnection {
    /**
     * Initializes this connection
     *
     * @param host     The XSP host
     * @param port     The XSP port
     * @param login    Login for the endpoint
     * @param password Password for the endpoint
     */
    public XOWLConnection(String host, int port, String login, String password) {
        super(host, port, null, login, password);
    }

    /**
     * Executes a command on the server
     *
     * @param command The command to execute
     * @return The response
     */
    public String execute(String command) {
        String response = request(command);
        if (response == null)
            return getReplyForError();
        return response.substring(2);
    }
}
