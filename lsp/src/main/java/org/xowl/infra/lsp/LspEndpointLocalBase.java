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

import org.xowl.infra.jsonrpc.JsonRpcClientBase;
import org.xowl.infra.jsonrpc.JsonRpcRequest;
import org.xowl.infra.utils.api.Reply;
import org.xowl.infra.utils.api.ReplyResult;
import org.xowl.infra.utils.json.JsonDeserializer;

import java.util.List;

/**
 * Base implementation of an LSP endpoint that is local to the current Java process
 *
 * @author Laurent Wouters
 */
public abstract class LspEndpointLocalBase extends JsonRpcClientBase implements LspEndpointLocal {
    /**
     * The handler for the requests coming to this endpoint
     */
    protected final LspHandler handler;
    /**
     * The remote endpoint to connect to
     */
    protected LspEndpointRemote remote;

    /**
     * Initializes this endpoint
     *
     * @param handler      The handler for the requests coming to this endpoint
     * @param deserializer The de-serializer to use for responses
     */
    protected LspEndpointLocalBase(LspHandler handler, JsonDeserializer deserializer) {
        this(handler, deserializer, null);
    }

    /**
     * Initializes this endpoint
     *
     * @param handler      The handler for the requests coming to this endpoint
     * @param deserializer The de-serializer to use for responses
     * @param remote       The remote endpoint to connect to
     */
    protected LspEndpointLocalBase(LspHandler handler, JsonDeserializer deserializer, LspEndpointRemote remote) {
        super(deserializer);
        this.handler = handler;
        this.remote = remote;
    }

    /**
     * Gets the remote endpoint to connect to
     *
     * @return The remote endpoint to connect to
     */
    public LspEndpointRemote getRemote() {
        return remote;
    }

    /**
     * Sets the remote endpoint to connect to
     *
     * @param remote The remote endpoint to connect to
     */
    public void setRemote(LspEndpointRemote remote) {
        this.remote = remote;
    }

    @Override
    public LspHandler getHandler() {
        return handler;
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

    @Override
    public Reply sendAndDeserialize(String message, Object context) {
        Reply reply = send(LspUtils.envelop(message));
        if (!reply.isSuccess())
            return reply;
        String content = LspUtils.stripEnvelope(((ReplyResult<String>) reply).getData());
        return deserializeResponses(content, context);
    }
}
