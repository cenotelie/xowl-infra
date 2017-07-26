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

package org.xowl.infra.lsp;

import org.xowl.infra.lsp.client.LspClient;
import org.xowl.infra.lsp.client.LspClientResponseDeserializer;
import org.xowl.infra.lsp.server.LspServer;
import org.xowl.infra.lsp.server.LspServerResponseDeserializer;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Front APi for the LSP protocol
 *
 * @author Laurent Wouters
 */
public class Lsp {
    /**
     * Connects a local client and a local server when they are both in the current Java process
     *
     * @param client The LSP client
     * @param server The LSP server
     */
    public static void connectLocal(LspClient client, LspServer server) {
        LspEndpointRemoteProxy proxyForClient = new LspEndpointRemoteProxy(server, new LspClientResponseDeserializer());
        LspEndpointRemoteProxy proxyForServer = new LspEndpointRemoteProxy(client, new LspServerResponseDeserializer());
        client.setRemote(proxyForClient);
        server.setRemote(proxyForServer);
    }

    /**
     * Connects a local client to a server running an remote process through the use of the standard input and output streams
     *
     * @param client  The local LSP client
     * @param process The process running the server to connect to
     * @return The created remote endpoint
     */
    public static LspEndpointRemote connectToProcess(LspClient client, Process process) {
        LspEndpointRemoteStream remote = new LspEndpointRemoteStream(
                client,
                process.getOutputStream(),
                process.getInputStream());
        client.setRemote(remote);
        return remote;
    }

    /**
     * Serves the specified local server through the specified streams
     *
     * @param server The local server to make accessible through streams
     * @param output The output stream for sending messages to the real remote endpoint
     * @param input  The input stream to read messages from the real remote endpoint
     * @return The created remote endpoint
     */
    public static LspEndpointRemote serveByStreams(LspServer server, OutputStream output, InputStream input) {
        LspEndpointRemoteStream remote = new LspEndpointRemoteStream(server, output, input);
        server.setRemote(remote);
        return remote;
    }

    /**
     * Serves the specified local server through the standard input and output streams
     *
     * @param server The local server to make accessible through streams
     * @return The created remote endpoint
     */
    public static LspEndpointRemote serveByStdStreams(LspServer server) {
        return serveByStreams(server, System.out, System.in);
    }
}
