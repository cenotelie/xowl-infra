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

import fr.cenotelie.hime.redist.ASTNode;
import org.xowl.infra.lsp.structures.InitializationResult;
import org.xowl.infra.utils.json.JsonDeserializer;

/**
 * A de-serializer for the response objects received by a LSP client
 *
 * @author Laurent Wouters
 */
public class LspClientResponseDeserializer extends JsonDeserializer {
    @Override
    public Object deserializeObject(ASTNode definition, Object context) {
        if (context != null && (context instanceof String))
            return deserializeObject(definition, (String) context);
        return super.deserializeObject(definition, context);
    }

    /**
     * De-serializes an object related to a request
     *
     * @param definition The serialized parameters
     * @param method     The current LSP method
     * @return The de-serialized object
     */
    public Object deserializeObject(ASTNode definition, String method) {
        switch (method) {
            case "initialize":
                return new InitializationResult(definition, this);
            default:
                return super.deserializeObject(definition, method);
        }
    }
}
