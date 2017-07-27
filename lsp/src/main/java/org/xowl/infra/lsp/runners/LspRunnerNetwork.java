/*******************************************************************************
 * Copyright (c) 2017 Association Cénotélie (cenotelie.fr)
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
 ******************************************************************************/

package org.xowl.infra.lsp.runners;

import org.xowl.infra.lsp.LspEndpointRemoteStream;
import org.xowl.infra.lsp.server.LspServer;
import org.xowl.infra.utils.logging.Logging;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Runs a single LSP server in this Java process and serves it over a single port
 *
 * @author Laurent Wouters
 */
public class LspRunnerNetwork extends LspRunner {
    /**
     * The port to listen on for connections
     */
    private final int port;
    /**
     * The server socket to use
     */
    private ServerSocket serverSocket;
    /**
     * The target socket
     */
    private Socket targetSocket;

    /**
     * Initializes this runner
     *
     * @param server The LSP server to run
     * @param port   The port to listen on for connections
     */
    public LspRunnerNetwork(LspServer server, int port) {
        super(server);
        this.port = port;
    }

    @Override
    protected void doRun() {
        // create the server socket
        try {
            serverSocket = new ServerSocket(port);
        } catch (Exception exception) {
            Logging.get().error(exception);
            return;
        }

        // listen on the socket
        try {
            targetSocket = serverSocket.accept();
        } catch (Exception exception) {
            Logging.get().error(exception);
            return;
        }

        // bind the target socket
        try {
            LspEndpointRemoteStream remote = new LspEndpointRemoteStream(server, targetSocket.getOutputStream(), targetSocket.getInputStream()) {
                @Override
                protected void onListenerEnded() {
                    shouldStop = true;
                    signal.countDown();
                    LspRunnerNetwork.this.close();
                }
            };
            server.setRemote(remote);
        } catch (IOException exception) {
            Logging.get().error(exception);
            return;
        }

        while (!shouldStop) {
            try {
                signal.await();
            } catch (InterruptedException exception) {
                break;
            }
        }
    }

    @Override
    protected void onClose() {
        if (targetSocket != null && !targetSocket.isClosed()) {
            try {
                targetSocket.close();
            } catch (Exception exception) {
                // do nothing
            }
        }
        if (serverSocket != null && !serverSocket.isClosed()) {
            try {
                serverSocket.close();
            } catch (Exception exception) {
                // do nothing
            }
        }
    }
}
