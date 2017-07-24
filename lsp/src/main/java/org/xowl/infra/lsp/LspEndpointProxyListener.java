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
import org.xowl.infra.utils.json.JsonDeserializer;

import java.util.List;

/**
 * Implements a proxy endpoint for sending requests to the listening part of another endpoint object
 *
 * @author Laurent Wouters
 */
public class LspEndpointProxyListener extends LspEndpointBase implements LspEndpoint {
    /**
     * Initializes this endpoint
     *
     * @param listener     The proxied listener
     * @param deserializer The de-serializer to use for responses
     */
    protected LspEndpointProxyListener(LspEndpointListener listener, JsonDeserializer deserializer) {
        super(listener, deserializer);
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
        String content = listener.handle(LspUtils.envelop(message));
        return doParseResponse(LspUtils.stripEnvelope(content), context);
    }

    @Override
    protected Reply doSend(String message) {
        return ReplyUnsupported.instance();
    }
}
