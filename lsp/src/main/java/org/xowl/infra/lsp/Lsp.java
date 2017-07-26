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

/**
 * Front APi for the LSP protocol
 *
 * @author Laurent Wouters
 */
public class Lsp {
    /**
     * Create a LSP server for local services (within the current Java process)
     *
     * @param handler The handler for client requests
     * @return The LSP server
     */
    public static LspServer createLocalServer(LspEndpointListener handler) {
        return new LspServer(handler);
    }

    /**
     * Creates a LSP client for local services (within the current Java process)
     *
     * @param server  The server to connect to
     * @param handler The handler for the server requests
     * @return The LSP client
     */
    public static LspClient createLocalClient(LspServer server, LspEndpointListener handler) {
        LspEndpoint proxy = new LspEndpointProxyListener(server.getListener(), new LspClientResponseDeserializer());
        return new LspClient(handler, proxy);
    }
}
