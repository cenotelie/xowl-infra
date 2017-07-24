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
 * A specialized Json-Rcp server for the use of LSP
 *
 * @author Laurent Wouters
 */
public abstract class LspEndpointListenerBase extends JsonRpcServerBase implements LspEndpointListener {
    /**
     * Initializes this server
     *
     * @param deserializer The de-serializer to use for requests
     */
    public LspEndpointListenerBase(JsonDeserializer deserializer) {
        super(deserializer);
    }

    @Override
    public String handle(String input) {
        String content = stripEnvelope(input);
        if (content == null)
            return null;
        BufferedLogger logger = new BufferedLogger();
        ASTNode definition = Json.parse(logger, content);
        if (definition == null || !logger.getErrorMessages().isEmpty())
            return JsonRpcResponseError.newParseError(null).serializedJSON();
        return handle(definition);
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

    /**
     * Gets the content Json-Rpc payload after the header
     *
     * @param message The input message
     * @return The content
     */
    private String stripEnvelope(String message) {
        if (message == null)
            return null;
        if (!message.startsWith(LspUtils.HEADER_CONTENT_LENGTH))
            return null;
        int index = message.indexOf(LspUtils.EOL, LspUtils.HEADER_CONTENT_LENGTH.length() + 1);
        if (index == -1)
            return null;
        int length;
        try {
            length = Integer.parseInt(message.substring(LspUtils.HEADER_CONTENT_LENGTH.length() + 1, index).trim());
        } catch (NumberFormatException exception) {
            return null;
        }
        message = message.substring(index + LspUtils.EOL.length());
        if (message.startsWith(LspUtils.HEADER_CONTENT_TYPE)) {
            index = message.indexOf(LspUtils.EOL, LspUtils.HEADER_CONTENT_TYPE.length() + 1);
            if (index == -1)
                return null;
            message = message.substring(index + LspUtils.EOL.length());
        }
        if (message.startsWith(LspUtils.EOL))
            message = message.substring(LspUtils.EOL.length());
        return message;
    }
}
