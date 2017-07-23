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
import org.xowl.infra.utils.json.Json;
import org.xowl.infra.utils.json.JsonLexer;
import org.xowl.infra.utils.json.JsonParser;
import org.xowl.infra.utils.json.JsonDeserializer;
import org.xowl.infra.utils.logging.BufferedLogger;

import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Implementation of a server for a Json-Rpc protocol
 *
 * @author Laurent Wouters
 */
public abstract class JsonRpcServer {
    /**
     * The de-serializer to use
     */
    private final JsonDeserializer deserializer;

    /**
     * Initializes this server
     */
    public JsonRpcServer() {
        this(JsonDeserializer.DEFAULT);
    }

    /**
     * Initializes this server
     *
     * @param deserializer The de-serializer to use
     */
    public JsonRpcServer(JsonDeserializer deserializer) {
        this.deserializer = deserializer;
    }

    /**
     * Handles the specified input
     *
     * @param input The input for the server
     * @return The response
     */
    public String handle(Reader input) {
        BufferedLogger logger = new BufferedLogger();
        ASTNode definition = Json.parse(logger, input);
        if (definition == null || !logger.getErrorMessages().isEmpty())
            return JsonRpcResponseError.newParseError(null).serializedJSON();
        Object object = deserializeRequests(definition);
        if (object == null)
            return JsonRpcResponseError.newInvalidRequest(null).serializedJSON();
        if (object instanceof JsonRpcRequest) {
            JsonRpcResponse response = handle((JsonRpcRequest) object);
            if (response == null)
                return "";
            return response.serializedJSON();
        } else {
            List<JsonRpcRequest> requests = ((List<JsonRpcRequest>) object);
            List<JsonRpcResponse> responses = handle(requests);
            if (responses.isEmpty())
                return "";
            return Json.serialize(responses);
        }
    }

    /**
     * De-serializes the requests objects
     *
     * @param definition The serialized definition
     * @return The request(s)
     */
    private Object deserializeRequests(ASTNode definition) {
        if (definition.getSymbol().getID() == JsonParser.ID.object)
            return deserializeRequest(definition);
        if (definition.getSymbol().getID() != JsonParser.ID.array)
            return null;
        List<JsonRpcRequest> requests = new ArrayList<>();
        for (ASTNode child : definition.getChildren()) {
            JsonRpcRequest request = deserializeRequest(child);
            requests.add(request);
        }
        return requests;
    }

    /**
     * De-serializes the request object
     *
     * @param definition The serialized definition
     * @return The request
     */
    private JsonRpcRequest deserializeRequest(ASTNode definition) {
        if (definition.getSymbol().getID() != JsonParser.ID.object)
            return null;

        String jsonRpc = null;
        String identifier = null;
        String method = null;
        ASTNode params = null;

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
                case "method": {
                    method = TextUtils.unescape(nodeValue.getValue());
                    method = method.substring(1, method.length() - 1);
                    break;
                }
                case "params": {
                    params = nodeValue;
                    break;
                }
            }
        }

        if (!Objects.equals(jsonRpc, "2.0"))
            return null;
        if (method == null)
            return null;
        Object params2 = deserializer.deserialize(params);
        return new JsonRpcRequest(identifier, method, params2);
    }

    /**
     * Handles a request
     *
     * @param request The request
     * @return The response
     */
    public abstract JsonRpcResponse handle(JsonRpcRequest request);

    /**
     * Handles a batch of requests
     *
     * @param requests The requests
     * @return The responses
     */
    public List<JsonRpcResponse> handle(List<JsonRpcRequest> requests) {
        List<JsonRpcResponse> responses = new ArrayList<>(requests.size());
        for (JsonRpcRequest request : requests) {
            if (request == null)
                responses.add(JsonRpcResponseError.newInvalidRequest(null));
            else {
                JsonRpcResponse response = handle(request);
                if (response != null)
                    responses.add(response);
            }
        }
        return responses;
    }
}
