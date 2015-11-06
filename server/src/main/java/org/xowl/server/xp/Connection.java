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

package org.xowl.server.xp;

import org.xowl.server.ServerConfiguration;
import org.xowl.server.db.Controller;

import java.net.Socket;

/**
 * Represents an active connection to the xOWL protocol server
 * This class basically implements the xOWL protocol from the server's standpoint
 *
 * @author Laurent Wouters
 */
class Connection implements Runnable {
    /**
     * The current configuration
     */
    private final ServerConfiguration configuration;
    /**
     * The current controller
     */
    private final Controller controller;
    /**
     * The socket for the this connection
     */
    private final Socket socket;

    /**
     * Initializes this connection
     *
     * @param controller The current controller
     * @param socket     The socket for the this connection
     */
    public Connection(ServerConfiguration configuration, Controller controller, Socket socket) {
        this.configuration = configuration;
        this.controller = controller;
        this.socket = socket;
    }

    @Override
    public void run() {

    }
}
