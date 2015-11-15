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

package org.xowl.server.xsp;

import org.xowl.server.ServerConfiguration;
import org.xowl.server.db.Controller;
import org.xowl.server.db.ProtocolHandler;
import org.xowl.server.db.ProtocolReply;
import org.xowl.server.db.ProtocolReplyResult;
import org.xowl.store.Serializable;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Collection;

/**
 * Represents an active connection to the XSP server
 *
 * @author Laurent Wouters
 */
class XSPConnection extends ProtocolHandler {
    /**
     * The current configuration
     */
    private final ServerConfiguration configuration;
    /**
     * The socket for the this connection
     */
    private final Socket socket;
    /**
     * The stream used for writing to the socket
     */
    private final BufferedWriter socketOutput;
    /**
     * The stream used for reading to the socket
     */
    private final BufferedReader socketInput;
    /**
     * Whether to exit
     */
    private boolean exit;

    /**
     * Initializes this connection
     *
     * @param controller The current controller
     * @param socket     The socket for the this connection
     */
    public XSPConnection(ServerConfiguration configuration, Controller controller, Socket socket) {
        super(controller);
        this.configuration = configuration;
        this.socket = socket;
        BufferedWriter output = null;
        BufferedReader input = null;
        try {
            output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));
            input = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
        } catch (IOException exception) {
            controller.getLogger().error(exception);
        }
        socketOutput = output;
        socketInput = input;
        exit = false;
    }

    @Override
    protected InetAddress getClient() {
        return socket.getInetAddress();
    }

    @Override
    protected void onExit() {
        exit = true;
    }

    @Override
    public void doRun() {
        if (controller.isBanned(socket.getInetAddress()))
            return;
        try {
            // state 0
            send("XOWL SERVER " + configuration.getServerName());
            // state 1
            while (!exit) {
                String line = socketInput.readLine();
                if (line == null)
                    return;
                ProtocolReply reply = execute(line);
                if (reply == null) {
                    // client got banned
                    return;
                }
                if (reply instanceof ProtocolReplyResult) {
                    Object data = ((ProtocolReplyResult) reply).getData();
                    if (data instanceof Serializable) {
                        send(((org.xowl.store.Serializable) data).serializedString());
                    } else if (data instanceof Collection) {
                        for (Object element : (Collection) data)
                            send(element.toString());
                    } else {
                        send(data.toString());
                    }
                } else {
                    send(reply.getMessage());
                }
            }
        } catch (IOException exception) {
            logger.error(exception);
        } finally {
            try {
                socket.close();
            } catch (IOException exception) {
                controller.getLogger().error(exception);
            }
        }
    }

    /**
     * Sends data over the socket
     *
     * @param message The message to send
     */
    private void send(String message) throws IOException {
        socketOutput.write(message);
        socketOutput.newLine();
        socketOutput.flush();
    }
}
