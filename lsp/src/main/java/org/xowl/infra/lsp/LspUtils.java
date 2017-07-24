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

/**
 * Utility APIs and constants for LSP
 */
public class LspUtils {
    /**
     * The end of line string to use
     */
    public static final String EOL = "\r\n";
    /**
     * The MIME type for LSP messages
     */
    public static final String MIME_LSP = "application/vscode-jsonrpc";
    /**
     * The content of the Content-Type header
     */
    public static final String HEADER_CONTENT_TYPE_VALUE = MIME_LSP + "; charset=utf-8";
    /**
     * The name of the Content-Length header
     */
    public static final String HEADER_CONTENT_LENGTH = "Content-Length";
    /**
     * The name of the Content-Type header
     */
    public static final String HEADER_CONTENT_TYPE = "Content-Type";

    /**
     * The error code when the server is not initialized
     */
    public static final int ERROR_SERVER_NOT_INITIALIZED = -32002;
}
