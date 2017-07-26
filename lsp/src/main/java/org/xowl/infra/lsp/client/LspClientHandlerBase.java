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
import org.xowl.infra.jsonrpc.JsonRpcResponse;
import org.xowl.infra.jsonrpc.JsonRpcResponseError;
import org.xowl.infra.lsp.LspHandlerBase;

/**
 * Represents the part of a LSP client that handles requests from a server
 *
 * @author Laurent Wouters
 */
public class LspClientHandlerBase extends LspHandlerBase {
    /**
     * Initializes this server
     */
    public LspClientHandlerBase() {
        super(new LspClientRequestDeserializer());
    }

    @Override
    public JsonRpcResponse handle(JsonRpcRequest request) {
        return JsonRpcResponseError.newInvalidRequest(request.getIdentifier());
    }
}
