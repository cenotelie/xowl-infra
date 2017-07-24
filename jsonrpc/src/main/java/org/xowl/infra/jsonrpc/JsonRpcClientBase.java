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

import fr.cenotelie.hime.redist.ASTNode;
import org.xowl.infra.utils.TextUtils;
import org.xowl.infra.utils.api.Reply;
import org.xowl.infra.utils.api.ReplyApiError;
import org.xowl.infra.utils.api.ReplyResult;
import org.xowl.infra.utils.api.ReplyResultCollection;
import org.xowl.infra.utils.json.Json;
import org.xowl.infra.utils.json.JsonDeserializer;
import org.xowl.infra.utils.json.JsonLexer;
import org.xowl.infra.utils.json.JsonParser;
import org.xowl.infra.utils.logging.BufferedLogger;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A base implementation of a Json-Rpc client
 *
 * @author Laurent Wouters
 */
public abstract class JsonRpcClientBase implements JsonRpcClient {
    /**
     * The de-serializer to use
     */
    protected final JsonDeserializer deserializer;

    /**
     * Initializes this client
     */
    public JsonRpcClientBase() {
        this(JsonDeserializer.DEFAULT);
    }

    /**
     * Initializes this client
     *
     * @param deserializer The de-serializer to use for responses
     */
    public JsonRpcClientBase(JsonDeserializer deserializer) {
        this.deserializer = deserializer;
    }

    @Override
    public Reply send(JsonRpcRequest request) {
        return send(request.serializedJSON());
    }

    @Override
    public Reply send(List<JsonRpcRequest> requests) {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        boolean first = true;
        for (JsonRpcRequest request : requests) {
            if (!first)
                builder.append(", ");
            first = false;
            builder.append(request.serializedJSON());
        }
        builder.append("]");
        return send(builder.toString());
    }

    /**
     * Send a message to the server and get the response
     *
     * @param message The message to send
     * @return The reply
     */
    public Reply send(String message) {
        Reply reply = doSend(message);
        if (!reply.isSuccess())
            return reply;
        BufferedLogger logger = new BufferedLogger();
        ASTNode definition = Json.parse(logger, ((ReplyResult<String>) reply).getData());
        if (definition == null || !logger.getErrorMessages().isEmpty())
            return new ReplyApiError(ERROR_RESPONSE_PARSING, logger.getErrorsAsString());
        Object object = deserializeResponses(definition);
        if (object == null)
            return new ReplyApiError(ERROR_INVALID_RESPONSE);
        if (object instanceof JsonRpcResponse)
            return new ReplyResult<>((JsonRpcResponse) object);
        if (object instanceof List)
            return new ReplyResultCollection<>((List<JsonRpcResponse>) object);
        return new ReplyApiError(ERROR_INVALID_RESPONSE);
    }

    /**
     * Do send a message to the server
     *
     * @param message The message to send
     * @return The reply
     */
    protected abstract Reply doSend(String message);

    /**
     * De-serializes the response objects
     *
     * @param definition The serialized definition
     * @return The response(s)
     */
    protected Object deserializeResponses(ASTNode definition) {
        if (definition.getSymbol().getID() == JsonParser.ID.object)
            return deserializeResponse(definition);
        if (definition.getSymbol().getID() != JsonParser.ID.array)
            return null;
        List<JsonRpcResponse> responses = new ArrayList<>();
        for (ASTNode child : definition.getChildren()) {
            JsonRpcResponse response = deserializeResponse(child);
            responses.add(response);
        }
        return responses;
    }

    /**
     * De-serializes the response object
     *
     * @param definition The serialized definition
     * @return The response
     */
    protected JsonRpcResponse deserializeResponse(ASTNode definition) {
        if (definition.getSymbol().getID() != JsonParser.ID.object)
            return null;

        String jsonRpc = null;
        String identifier = null;
        Object result = null;
        ASTNode nodeError = null;

        for (ASTNode child : definition.getChildren()) {
            ASTNode nodeMemberName = child.getChildren().get(0);
            String name = TextUtils.unescape(nodeMemberName.getValue());
            name = name.substring(1, name.length() - 1);
            ASTNode nodeValue = child.getChildren().get(1);
            switch (name) {
                case "jsonrpc": {
                    jsonRpc = TextUtils.unescape(nodeValue.getValue());
                    jsonRpc = jsonRpc.substring(1, jsonRpc.length() - 1);
                    break;
                }
                case "id": {
                    switch (nodeValue.getSymbol().getID()) {
                        case JsonLexer.ID.LITERAL_NULL:
                            identifier = null;
                            break;
                        case JsonLexer.ID.LITERAL_FALSE:
                            return null;
                        case JsonLexer.ID.LITERAL_TRUE:
                            return null;
                        case JsonLexer.ID.LITERAL_INTEGER:
                            identifier = definition.getValue();
                            break;
                        case JsonLexer.ID.LITERAL_DECIMAL:
                            identifier = definition.getValue();
                            break;
                        case JsonLexer.ID.LITERAL_DOUBLE:
                            identifier = definition.getValue();
                            break;
                        case JsonLexer.ID.LITERAL_STRING:
                            identifier = TextUtils.unescape(definition.getValue());
                            identifier = identifier.substring(1, identifier.length() - 1);
                            break;
                        default:
                            return null;
                    }
                    break;
                }
                case "result": {
                    result = deserializer.deserialize(nodeValue, null);
                    break;
                }
                case "error": {
                    nodeError = nodeValue;
                    break;
                }
            }
        }

        if (!Objects.equals(jsonRpc, "2.0"))
            return null;
        if (identifier == null)
            return null;
        if (result != null)
            return new JsonRpcResponseResult<>(identifier, result);
        if (nodeError != null)
            return deserializeResponseError(nodeError, identifier);
        return null;
    }

    /**
     * De-serializes the error response object
     *
     * @param definition The serialized definition
     * @return The response
     */
    protected JsonRpcResponse deserializeResponseError(ASTNode definition, String identifier) {
        if (definition.getSymbol().getID() != JsonParser.ID.object)
            return null;

        int code = 0;
        String message = null;
        Object data = null;

        for (ASTNode child : definition.getChildren()) {
            ASTNode nodeMemberName = child.getChildren().get(0);
            String name = TextUtils.unescape(nodeMemberName.getValue());
            name = name.substring(1, name.length() - 1);
            ASTNode nodeValue = child.getChildren().get(1);
            switch (name) {
                case "code": {
                    code = Integer.parseInt(nodeValue.getValue());
                    break;
                }
                case "message": {
                    message = TextUtils.unescape(nodeValue.getValue());
                    message = message.substring(1, message.length() - 1);
                    break;
                }
                case "data": {
                    data = deserializer.deserialize(nodeValue, null);
                    break;
                }
            }
        }

        if (code == 0 || message == null)
            return null;
        return new JsonRpcResponseError(identifier, code, message, data);
    }
}
