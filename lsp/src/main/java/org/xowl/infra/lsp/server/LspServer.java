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

package org.xowl.infra.lsp.server;

import org.xowl.infra.jsonrpc.JsonRpcRequest;
import org.xowl.infra.lsp.LspEndpoint;
import org.xowl.infra.lsp.LspEndpointBase;
import org.xowl.infra.lsp.LspEndpointListener;
import org.xowl.infra.lsp.client.LspClientResponseDeserializer;
import org.xowl.infra.utils.api.Reply;
import org.xowl.infra.utils.api.ReplyFailure;
import org.xowl.infra.utils.api.ReplySuccess;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Implements a base LSP server
 *
 * @author Laurent Wouters
 */
public class LspServer extends LspEndpointBase {
    /**
     * The server has been created, it has not been initialized yet
     */
    public static final int STATE_CREATED = 0;
    /**
     * The server is being initialized
     */
    public static final int STATE_INITIALIZING = 1;
    /**
     * The server is initialized and ready for work
     */
    public static final int STATE_READY = 2;
    /**
     * The server is shutting down
     */
    public static final int STATE_SHUTTING_DOWN = 3;
    /**
     * The server has been shut down
     */
    public static final int STATE_SHUT_DOWN = 4;
    /**
     * The server is exiting
     */
    public static final int STATE_EXITING = 5;
    /**
     * The server has exited
     */
    public static final int STATE_EXITED = 6;

    /**
     * The server's current state
     */
    private final AtomicInteger state;
    /**
     * The server endpoint
     */
    protected LspEndpoint client;

    /**
     * Initializes this endpoint
     *
     * @param listener The listener for requests from the server
     */
    public LspServer(LspEndpointListener listener) {
        super(listener, new LspClientResponseDeserializer());
        this.state = new AtomicInteger(STATE_CREATED);
        this.client = null;
    }

    /**
     * Initializes this endpoint
     *
     * @param listener The listener for requests from the server
     * @param client   The client endpoint
     */
    public LspServer(LspEndpointListener listener, LspEndpoint client) {
        super(listener, new LspClientResponseDeserializer());
        this.state = new AtomicInteger(STATE_CREATED);
        this.client = client;
    }

    /**
     * Gets the server's current state
     *
     * @return The server's current state
     */
    public int getState() {
        return state.get();
    }

    /**
     * Gets the associated client endpoint
     *
     * @return The associated client endpoint
     */
    public LspEndpoint getClient() {
        return client;
    }

    /**
     * Sets the associated client endpoint
     *
     * @param client The associated client endpoint
     */
    public void setClient(LspEndpoint client) {
        this.client = client;
    }

    @Override
    public Reply send(String message) {
        return client.send(message);
    }

    @Override
    public Reply send(JsonRpcRequest request) {
        return client.send(request);
    }

    @Override
    public Reply send(List<JsonRpcRequest> requests) {
        return client.send(requests);
    }

    /**
     * Performs the server's initialization
     *
     * @return The reply
     */
    protected Reply initialize() {
        if (!state.compareAndSet(STATE_CREATED, STATE_INITIALIZING))
            return ReplyFailure.instance();
        Reply reply = doInitialize();
        if (!reply.isSuccess())
            return reply;
        state.compareAndSet(STATE_INITIALIZING, STATE_READY);
        return reply;
    }

    /**
     * Do the server-specific work for its initialization
     *
     * @return The reply
     */
    protected Reply doInitialize() {
        return ReplySuccess.instance();
    }

    /**
     * Performs the server's shutdown
     *
     * @return The reply
     */
    protected Reply shutdown() {
        if (!state.compareAndSet(STATE_READY, STATE_SHUTTING_DOWN))
            return ReplyFailure.instance();
        Reply reply = doShutdown();
        if (!reply.isSuccess())
            return reply;
        state.compareAndSet(STATE_SHUTTING_DOWN, STATE_SHUT_DOWN);
        return reply;
    }

    /**
     * Do the server-specific work for its shutdown
     *
     * @return The reply
     */
    protected Reply doShutdown() {
        return ReplySuccess.instance();
    }

    /**
     * Performs the server's exit
     *
     * @return The reply
     */
    protected Reply exit() {
        if (!state.compareAndSet(STATE_SHUT_DOWN, STATE_EXITING))
            return ReplyFailure.instance();
        Reply reply = doExit();
        if (!reply.isSuccess())
            return reply;
        state.compareAndSet(STATE_EXITING, STATE_EXITED);
        return reply;
    }

    /**
     * Do the server-specific work for its exit
     *
     * @return The reply
     */
    protected Reply doExit() {
        return ReplySuccess.instance();
    }
}