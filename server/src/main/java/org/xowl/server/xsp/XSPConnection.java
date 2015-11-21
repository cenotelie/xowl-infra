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

package org.xowl.server.xsp;

import org.xowl.server.ServerConfiguration;
import org.xowl.server.db.Controller;
import org.xowl.server.db.ProtocolHandler;
import org.xowl.store.storage.remote.XSPReply;
import org.xowl.store.storage.remote.XSPReplyResult;
import org.xowl.store.Serializable;
import org.xowl.store.storage.remote.SocketHelper;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
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
        try {
            this.socket.setSoTimeout(configuration.getXSPMaxIdleTime() * 1000);
        } catch (SocketException exception) {
            controller.getLogger().error(exception);
        }
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
            SocketHelper.write(socket, "XOWL SERVER " + configuration.getServerName());
            // state 1
            while (!exit) {
                String line;
                try {
                    line = SocketHelper.read(socket);
                } catch (SocketTimeoutException exception) {
                    // time out while reading, finish this connection
                    return;
                }
                if (line == null)
                    return;
                XSPReply reply = execute(line);
                if (reply == null) {
                    // client got banned
                    return;
                }
                if (reply instanceof XSPReplyResult) {
                    Object data = ((XSPReplyResult) reply).getData();
                    if (data instanceof Serializable) {
                        String msg = (reply.isSuccess() ? "OK" : "KO") + ((org.xowl.store.Serializable) data).serializedString();
                        SocketHelper.write(socket, msg);
                    } else if (data instanceof Collection) {
                        StringBuilder builder = new StringBuilder();
                        builder.append(reply.isSuccess() ? "OK" : "KO");
                        boolean first = true;
                        for (Object element : (Collection) data) {
                            if (!first)
                                builder.append(System.lineSeparator());
                            first = false;
                            builder.append(element.toString());
                        }
                        SocketHelper.write(socket, builder.toString());
                    } else {
                        String msg = (reply.isSuccess() ? "OK" : "KO") + data.toString();
                        SocketHelper.write(socket, msg);
                    }
                } else {
                    String msg = (reply.isSuccess() ? "OK" : "KO") + reply.getMessage();
                    SocketHelper.write(socket, msg);
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
}
