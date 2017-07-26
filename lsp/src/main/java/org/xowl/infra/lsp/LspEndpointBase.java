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
import org.xowl.infra.utils.api.Reply;
import org.xowl.infra.utils.api.ReplyResult;
import org.xowl.infra.utils.json.JsonDeserializer;

/**
 * Base implementation of all LSP endpoints
 *
 * @author Laurent Wouters
 */
public abstract class LspEndpointBase extends JsonRpcClientBase implements LspEndpoint {
    /**
     * The handler for the requests coming to this endpoint
     */
    protected final LspHandler handler;

    /**
     * Initializes this endpoint
     *
     * @param handler      The handler for the requests coming to this endpoint
     * @param deserializer The de-serializer to use for responses
     */
    protected LspEndpointBase(LspHandler handler, JsonDeserializer deserializer) {
        super(deserializer);
        this.handler = handler;
    }

    @Override
    public LspHandler getHandler() {
        return handler;
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
