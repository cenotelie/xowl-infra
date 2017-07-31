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
import org.xowl.infra.lsp.structures.*;
import org.xowl.infra.utils.json.JsonDeserializer;

/**
 * A de-serializer for the request objects received by a LSP server
 *
 * @author Laurent Wouters
 */
public class LspServerRequestDeserializer extends JsonDeserializer {
    @Override
    public Object deserializeObject(ASTNode definition, Object context) {
        return deserializeObject(definition, (String) context);
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
                return new InitializeParams(definition, this);
            case "$/cancelRequest":
                return new CancelParams(definition);
            case "workspace/didChangeConfiguration":
                return new DidChangeConfigurationParams(definition, this);
            case "workspace/didChangeWatchedFiles":
                return new DidChangeWatchedFilesParams(definition);
            case "workspace/symbol":
                return new WorkspaceSymbolParams(definition);

            case "textDocument/didOpen":
                return new DidOpenTextDocumentParams(definition);
            case "textDocument/didChange":
                return new DidChangeTextDocumentParams(definition);
            case "textDocument/willSave":
                return new WillSaveTextDocumentParams(definition);
            case "textDocument/willSaveWaitUntil":
                return new WillSaveTextDocumentParams(definition);
            case "textDocument/didSave":
                return new DidSaveTextDocumentParams(definition);
            case "textDocument/didClose":
                return new DidCloseTextDocumentParams(definition);

            case "textDocument/documentSymbol":
                return new DocumentSymbolParams(definition);
            case "textDocument/definition":
                return new TextDocumentPositionParams(definition);
        }
        return super.deserializeObject(definition, method);
    }
}
