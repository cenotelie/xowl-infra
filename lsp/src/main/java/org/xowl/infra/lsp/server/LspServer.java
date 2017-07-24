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

package org.xowl.infra.lsp.server;

import fr.cenotelie.hime.redist.ASTNode;
import org.xowl.infra.jsonrpc.JsonRpcResponseError;
import org.xowl.infra.jsonrpc.JsonRpcServer;
import org.xowl.infra.utils.IOUtils;
import org.xowl.infra.utils.json.Json;
import org.xowl.infra.utils.logging.BufferedLogger;
import org.xowl.infra.utils.logging.Logging;

import java.io.IOException;
import java.io.Reader;

/**
 * Implements a base LSP server
 *
 * @author Laurent Wouters
 */
public abstract class LspServer extends JsonRpcServer {
    /**
     * The end of line string to use
     */
    private static final String EOL = "\r\n";
    /**
     * The MIME type for LSP messages
     */
    public static final String MIME_LSP = "application/vscode-jsonrpc";
    /**
     * The name of the Content-Length header
     */
    protected static final String HEADER_CONTENT_LENGTH = "Content-Length";
    /**
     * The name of the Content-Type header
     */
    protected static final String HEADER_CONTENT_TYPE = "Content-Type";
    /**
     * The content of the Content-Type header
     */
    protected static final String HEADER_CONTENT_TYPE_VALUE = MIME_LSP + "; charset=utf-8";

    /**
     * The server has been created, it has not been initialized yet
     */
    public static final int STATE_CREATED = 0;
    /**
     * The server is being initialized
     */
    public static final int STATE_INITIALIZING = 1;
    /**
     * The server is initialized and ready for work
     */
    public static final int STATE_READY = 2;
    /**
     * The server is shutting down
     */
    public static final int STATE_SHUTTING_DOWN = 3;
    /**
     * The server has been shut down
     */
    public static final int STATE_SHUT_DOWN = 4;
    /**
     * The server is exiting
     */
    public static final int STATE_EXITING = 5;
    /**
     * The server has exited
     */
    public static final int STATE_EXITED = 6;

    /**
     * Initializes this server
     */
    public LspServer() {
        super(new LspServerRequestDeserializer());
    }

    @Override
    public String handle(String input) {
        String content = getMessageContent(input);
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
    public static String getMessageContent(String message) {
        if (message == null)
            return null;
        if (!message.startsWith(HEADER_CONTENT_LENGTH))
            return null;
        int index = message.indexOf(EOL, HEADER_CONTENT_LENGTH.length() + 1);
        if (index == -1)
            return null;
        int length;
        try {
            length = Integer.parseInt(message.substring(HEADER_CONTENT_LENGTH.length() + 1, index).trim());
        } catch (NumberFormatException exception) {
            return null;
        }
        message = message.substring(index + EOL.length());
        if (message.startsWith(HEADER_CONTENT_TYPE)) {
            index = message.indexOf(EOL, HEADER_CONTENT_TYPE.length() + 1);
            if (index == -1)
                return null;
            message = message.substring(index + EOL.length());
        }
        if (message.startsWith(EOL))
            message = message.substring(EOL.length());
        return message;
    }

    /**
     * Gets the full message for the specified content
     *
     * @param content The content
     * @return The full message with the envelope
     */
    public static String getMessageFor(String content) {
        if (content == null)
            return null;
        return HEADER_CONTENT_LENGTH + ": " + Integer.toString(content.length()) + EOL +
                HEADER_CONTENT_TYPE + ": " + HEADER_CONTENT_TYPE_VALUE + EOL +
                EOL +
                content;
    }
}