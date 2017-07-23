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
import org.xowl.infra.utils.json.JsonLexer;
import org.xowl.infra.utils.json.JsonParser;
import org.xowl.infra.utils.json.SerializedUnknown;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a de-serializer of JSON-serialized data for the Json-Rpc protocol
 *
 * @author Laurent Wouters
 */
public class JsonRpcDeserializer {
    /**
     * The default de-serializer
     */
    public static final JsonRpcDeserializer DEFAULT = new JsonRpcDeserializer();

    /**
     * De-serializes an object parameters or result related to a request
     *
     * @param definition The serialized parameters
     * @param method     The requested method
     * @return The de-serialized object
     */
    public Object deserialize(ASTNode definition, String method) {
        switch (definition.getSymbol().getID()) {
            case JsonParser.ID.array: {
                List<Object> value = new ArrayList<>();
                for (ASTNode child : definition.getChildren()) {
                    value.add(deserialize(child, method));
                }
                return value;
            }
            case JsonParser.ID.object: {
                return deserializeObject(definition, method);
            }
            case JsonLexer.ID.LITERAL_NULL: {
                return null;
            }
            case JsonLexer.ID.LITERAL_FALSE: {
                return false;
            }
            case JsonLexer.ID.LITERAL_TRUE: {
                return true;
            }
            case JsonLexer.ID.LITERAL_INTEGER: {
                return Integer.parseInt(definition.getValue());
            }
            case JsonLexer.ID.LITERAL_DECIMAL: {
                return Double.parseDouble(definition.getValue());
            }
            case JsonLexer.ID.LITERAL_DOUBLE: {
                return Double.parseDouble(definition.getValue());
            }
            case JsonLexer.ID.LITERAL_STRING: {
                String value = TextUtils.unescape(definition.getValue());
                return value.substring(1, value.length() - 1);
            }
        }
        return null;
    }

    /**
     * De-serializes an object related to a request
     *
     * @param definition The serialized parameters
     * @param method     The requested method
     * @return The de-serialized object
     */
    public Object deserializeObject(ASTNode definition, String method) {
        // fallback to mapping the properties
        SerializedUnknown result = new SerializedUnknown();
        for (ASTNode memberNode : definition.getChildren()) {
            String memberName = TextUtils.unescape(memberNode.getChildren().get(0).getValue());
            memberName = memberName.substring(1, memberName.length() - 1);
            ASTNode memberValue = memberNode.getChildren().get(1);
            result.addProperty(memberName, deserialize(memberValue, method));
        }
        return result;
    }
}
