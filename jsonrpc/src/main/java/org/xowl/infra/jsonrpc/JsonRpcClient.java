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

package org.xowl.infra.jsonrpc;

import org.xowl.infra.utils.api.ApiError;
import org.xowl.infra.utils.api.Reply;

import java.util.List;

/**
 * Represents a client for Json-Rpc protocol
 *
 * @author Laurent Wouters
 */
public interface JsonRpcClient {
    /**
     * Error when the parsing of the incoming response failed
     */
    ApiError ERROR_RESPONSE_PARSING = new ApiError(0x0001,
            "Error while parsing the response",
            "http://cenotelie.fr/xowl/support/jsonrpc/errors/0x0001.html");
    /**
     * Error when the response object is not valid
     */
    ApiError ERROR_INVALID_RESPONSE = new ApiError(0x0002,
            "Invalid response",
            "http://cenotelie.fr/xowl/support/jsonrpc/errors/0x0002.html");

    /**
     * Sends a request to the server
     *
     * @param request The request
     * @return The response
     */
    Reply send(JsonRpcRequest request);

    /**
     * Sends a batch of requests to the server
     *
     * @param requests The requests
     * @return The responses
     */
    Reply send(List<JsonRpcRequest> requests);

    /**
     * Send a message to the server and get the response
     *
     * @param message The message to send
     * @param context The de-serialization context
     * @return The reply
     */
    Reply send(String message, Object context);
}
