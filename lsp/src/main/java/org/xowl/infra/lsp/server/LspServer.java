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

import org.xowl.infra.lsp.LspEndpointLocalBase;
import org.xowl.infra.utils.api.Reply;
import org.xowl.infra.utils.api.ReplyFailure;
import org.xowl.infra.utils.api.ReplySuccess;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Implements a base LSP server
 *
 * @author Laurent Wouters
 */
public class LspServer extends LspEndpointLocalBase {
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
     * Initializes this endpoint
     *
     * @param handler The handler for the requests coming to this endpoint
     */
    public LspServer(LspServerHandlerBase handler) {
        super(handler, new LspServerResponseDeserializer());
        this.state = new AtomicInteger(STATE_CREATED);
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