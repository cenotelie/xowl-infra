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
import org.xowl.infra.jsonrpc.JsonRpcResponse;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Implements a base LSP server
 *
 * @author Laurent Wouters
 */
public class LspServerBase extends LspServer {
    /**
     * The state of the server
     */
    private final AtomicInteger state;

    /**
     * Gets the current state of this server
     *
     * @return The current state of this server
     */
    public int getState() {
        return state.get();
    }

    /**
     * Initializes this server
     */
    public LspServerBase() {
        this.state = new AtomicInteger(STATE_CREATED);
    }

    @Override
    public JsonRpcResponse handle(JsonRpcRequest request) {
        return null;
    }
}
