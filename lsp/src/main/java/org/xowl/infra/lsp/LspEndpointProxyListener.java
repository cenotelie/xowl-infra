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
import org.xowl.infra.utils.api.ReplyResult;
import org.xowl.infra.utils.api.ReplyResultCollection;
import org.xowl.infra.utils.api.ReplyUnsupported;

import java.util.List;

/**
 * Implements a proxy endpoint for sending requests to the listening part of another endpoint object
 *
 * @author Laurent Wouters
 */
public class LspEndpointProxyListener implements LspEndpoint {
    /**
     * The proxied listener
     */
    private final LspEndpointListener listener;

    /**
     * Initialized this proxy
     *
     * @param listener The proxied listener
     */
    public LspEndpointProxyListener(LspEndpointListener listener) {
        this.listener = listener;
    }

    @Override
    public LspEndpointListener getListener() {
        return listener;
    }

    @Override
    public Reply send(JsonRpcRequest request) {
        return new ReplyResult<>(listener.handle(request));
    }

    @Override
    public Reply send(List<JsonRpcRequest> requests) {
        return new ReplyResultCollection<>(listener.handle(requests));
    }

    @Override
    public Reply send(String message, Object context) {
        return ReplyUnsupported.instance();
    }
}
