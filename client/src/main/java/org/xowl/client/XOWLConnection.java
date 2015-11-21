/*******************************************************************************
 * Copyright (c) 2015 Laurent Wouters
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Lesser General
 * Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 * <p>
 * Contributors:
 * Laurent Wouters - lwouters@xowl.org
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
     * @param database The default target database, if any
     * @param login    Login for the endpoint
     * @param password Password for the endpoint
     */
    public XOWLConnection(String host, int port, String database, String login, String password) {
        super(host, port, database, login, password);
    }

    /**
     * Shut down the server
     *
     * @return Whether the operation succeeded
     */
    public boolean serverShutdown() {
        String response = request("ADMIN SHUTDOWN");
        return response != null && ("OK".equals(response));
    }

}
