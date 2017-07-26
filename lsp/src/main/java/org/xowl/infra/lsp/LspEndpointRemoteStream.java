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
import org.xowl.infra.jsonrpc.JsonRpcResponse;
import org.xowl.infra.utils.IOUtils;
import org.xowl.infra.utils.api.Reply;
import org.xowl.infra.utils.api.ReplyException;
import org.xowl.infra.utils.json.JsonDeserializer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Implements a remote LSP endpoint that uses streams to communicate
 *
 * @author Laurent Wouters
 */
public class LspEndpointRemoteStream extends LspEndpointBase {
    /**
     * The input handler for the remote endpoint
     */
    private class InputListener extends LspHandlerBase {
        /**
         * Initializes this server
         *
         * @param deserializer The de-serializer to use for requests
         */
        public InputListener(JsonDeserializer deserializer) {
            super(deserializer);
        }

        @Override
        public JsonRpcResponse handle(JsonRpcRequest request) {
            return null;
        }
    }

    /**
     * The output stream for sending messages to the remote endpoint
     */
    private final OutputStream output;
    /**
     * The input stream to read messages from the remote endpoint
     */
    private final InputStream input;

    /**
     * Initializes this endpoint
     *
     * @param listener     The handler for requests coming from the remote endpoint
     * @param deserializer The de-serializer to use
     */
    public LspEndpointRemoteStream(LspHandler listener, JsonDeserializer deserializer, OutputStream output, InputStream input) {
        super(listener, deserializer);
        this.input = input;
        this.output = output;
    }

    @Override
    public Reply send(String message) {
        byte[] bytes = message.getBytes(IOUtils.UTF8);
        try {
            output.write(bytes);
        } catch (IOException exception) {
            return new ReplyException(exception);
        }

        return null;
    }
}
