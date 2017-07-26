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

import org.xowl.infra.jsonrpc.JsonRpcRequest;
import org.xowl.infra.utils.api.Reply;
import org.xowl.infra.utils.json.JsonDeserializer;

import java.util.List;

/**
 * Basic implementation of a local endpoint connected to a remote endpoint
 *
 * @author Laurent Wouters
 */
public class LspEndpointBaseConnected extends LspEndpointBase {
    /**
     * The remote endpoint to connect to
     */
    protected LspEndpoint remote;

    /**
     * Initializes this endpoint
     *
     * @param handler      The handler for the requests coming to this endpoint
     * @param deserializer The de-serializer to use for responses
     */
    protected LspEndpointBaseConnected(LspHandler handler, JsonDeserializer deserializer) {
        super(handler, deserializer);
    }

    /**
     * Initializes this endpoint
     *
     * @param handler      The handler for the requests coming to this endpoint
     * @param deserializer The de-serializer to use for responses
     * @param remote       The remote endpoint to connect to
     */
    protected LspEndpointBaseConnected(LspHandler handler, JsonDeserializer deserializer, LspEndpoint remote) {
        super(handler, deserializer);
        this.remote = remote;
    }

    /**
     * Gets the remote endpoint to connect to
     *
     * @return The remote endpoint to connect to
     */
    public LspEndpoint getRemote() {
        return remote;
    }

    /**
     * Sets the remote endpoint to connect to
     *
     * @param remote The remote endpoint to connect to
     */
    public void setRemote(LspEndpoint remote) {
        this.remote = remote;
    }

    @Override
    public Reply send(String message) {
        return remote.send(message);
    }

    @Override
    public Reply send(JsonRpcRequest request) {
        return remote.send(request);
    }

    @Override
    public Reply send(List<JsonRpcRequest> requests) {
        return remote.send(requests);
    }
}
