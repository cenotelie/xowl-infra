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

import fr.cenotelie.hime.redist.ASTNode;
import org.xowl.infra.jsonrpc.JsonRpcResponseError;
import org.xowl.infra.jsonrpc.JsonRpcServerBase;
import org.xowl.infra.utils.IOUtils;
import org.xowl.infra.utils.json.Json;
import org.xowl.infra.utils.json.JsonDeserializer;
import org.xowl.infra.utils.logging.BufferedLogger;
import org.xowl.infra.utils.logging.Logging;

import java.io.IOException;
import java.io.Reader;

/**
 * Implements a basic handler of LSP requests
 *
 * @author Laurent Wouters
 */
public abstract class LspHandlerBase extends JsonRpcServerBase implements LspHandler {
    /**
     * Initializes this server
     *
     * @param deserializer The de-serializer to use for requests
     */
    public LspHandlerBase(JsonDeserializer deserializer) {
        super(deserializer);
    }

    @Override
    public JsonDeserializer getRequestsDeserializer() {
        return deserializer;
    }

    @Override
    public String handle(String input) {
        String content = LspUtils.stripEnvelope(input);
        if (content == null)
            return null;
        BufferedLogger logger = new BufferedLogger();
        ASTNode definition = Json.parse(logger, content);
        if (definition == null || !logger.getErrorMessages().isEmpty())
            return LspUtils.envelop(JsonRpcResponseError.newParseError(null).serializedJSON());
        return LspUtils.envelop(handle(definition));
    }

    @Override
    public String handle(Reader input) {
        try {
            return handle(IOUtils.read(input));
        } catch (IOException exception) {
            Logging.get().error(exception);
            return null;
        }
    }
}
