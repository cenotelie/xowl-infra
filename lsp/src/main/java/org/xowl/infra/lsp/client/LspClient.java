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

package org.xowl.infra.lsp.client;

import org.xowl.infra.jsonrpc.JsonRpcRequest;
import org.xowl.infra.lsp.LspEndpoint;
import org.xowl.infra.lsp.LspEndpointBase;
import org.xowl.infra.lsp.LspEndpointListener;
import org.xowl.infra.utils.api.Reply;
import org.xowl.infra.utils.api.ReplyUnsupported;

import java.util.List;

/**
 * Base interface for a LSP client
 *
 * @author Laurent Wouters
 */
public class LspClient extends LspEndpointBase {
    /**
     * The server endpoint
     */
    protected LspEndpoint server;

    /**
     * Initializes this endpoint
     *
     * @param listener The listener for requests from the server
     */
    public LspClient(LspEndpointListener listener) {
        super(listener, new LspClientResponseDeserializer());
        this.server = null;
    }

    /**
     * Initializes this endpoint
     *
     * @param listener The listener for requests from the server
     * @param server   The server endpoint
     */
    public LspClient(LspEndpointListener listener, LspEndpoint server) {
        super(listener, new LspClientResponseDeserializer());
        this.server = server;
    }

    /**
     * Gets the associated server endpoint
     *
     * @return The associated server endpoint
     */
    public LspEndpoint getServer() {
        return server;
    }

    /**
     * Sets the associated server endpoint
     *
     * @param server The associated server endpoint
     */
    public void setServer(LspEndpoint server) {
        this.server = server;
    }

    @Override
    public Reply send(JsonRpcRequest request) {
        return server.send(request);
    }

    @Override
    public Reply send(List<JsonRpcRequest> requests) {
        return server.send(requests);
    }

    @Override
    public Reply send(String message, Object context) {
        return server.send(message, context);
    }

    @Override
    protected Reply doSend(String message) {
        return ReplyUnsupported.instance();
    }
}
