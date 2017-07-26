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
}
