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

import org.xowl.infra.jsonrpc.JsonRpcRequest;
import org.xowl.infra.jsonrpc.JsonRpcResponse;
import org.xowl.infra.jsonrpc.JsonRpcResponseError;
import org.xowl.infra.jsonrpc.JsonRpcResponseResult;
import org.xowl.infra.lsp.LspHandlerBase;
import org.xowl.infra.lsp.LspUtils;
import org.xowl.infra.lsp.structures.ClientCapabilities;
import org.xowl.infra.utils.api.Reply;

/**
 * Represents the part of a LSP server that handles requests from a client
 *
 * @author Laurent Wouters
 */
public abstract class LspServerHandlerBase extends LspHandlerBase {
    /**
     * The parent server
     */
    protected LspServer server;

    /**
     * Initializes this server
     */
    public LspServerHandlerBase() {
        super(new LspServerRequestDeserializer());
    }

    /**
     * Sets the parent server
     *
     * @param server The parent server
     */
    protected void setServer(LspServer server) {
        this.server = server;
    }

    @Override
    public JsonRpcResponse handle(JsonRpcRequest request) {
        int state = server.getState();
        if (state < LspServer.STATE_READY) {
            if ("initialize".equals(request.getMethod()))
                return onInitialize(request);
            if (request.isNotification())
                return null;
            return new JsonRpcResponseError(
                    request.getIdentifier(),
                    LspUtils.ERROR_SERVER_NOT_INITIALIZED,
                    "Server is not initialized",
                    null);
        } else if (state >= LspServer.STATE_EXITING) {
            if (request.isNotification())
                return null;
            return new JsonRpcResponseError(
                    request.getIdentifier(),
                    LspUtils.ERROR_SERVER_HAS_EXITED,
                    "Server has exited",
                    null);
        } else if (state >= LspServer.STATE_SHUTTING_DOWN) {
            if ("exit".equals(request.getMethod()))
                return onExit(request);
            if (request.isNotification())
                return null;
            return new JsonRpcResponseError(
                    request.getIdentifier(),
                    LspUtils.ERROR_SERVER_SHUT_DOWN,
                    "Server has shut down",
                    null);
        }

        // here the server is ready
        switch (request.getMethod()) {
            case "initialize":
                return JsonRpcResponseError.newInvalidRequest(request.getIdentifier());
            case "initialized":
                return onInitialized(request);
            case "shutdown":
                return onShutdown(request);
            case "exit":
                return onExit(request);
            case "$/cancelRequest":
                return onCancelRequest(request);
            case "workspace/didChangeConfiguration":
                return onWorkspaceDidChangeConfiguration(request);
            case "workspace/didChangeWatchedFiles":
                return onWorkspaceDidChangeWatchedFiles(request);
            case "workspace/symbol":
                return onWorkspaceSymbol(request);
            case "workspace/executeCommand":
                return onWorkspaceExecuteCommand(request);
            case "textDocument/didOpen":
                return onTextDocumentDidOpen(request);
            case "textDocument/didChange":
                return onTextDocumentDidChange(request);
            case "textDocument/willSave":
                return onTextDocumentWillSave(request);
            case "textDocument/willSaveWaitUntil":
                return onTextDocumentWillSaveUntil(request);
            case "textDocument/didSave":
                return onTextDocumentDidSave(request);
            case "textDocument/didClose":
                return onTextDocumentDidClose(request);
            case "textDocument/completion":
                return onTextDocumentCompletion(request);
            case "completionItem/resolve":
                return onCompletionItemResolve(request);
            case "textDocument/hover":
                return onTextDocumentHover(request);
            case "textDocument/signatureHelp":
                return onTextDocumentSignatureHelp(request);
            case "textDocument/references":
                return onTextDocumentReferences(request);
            case "textDocument/documentHighlight":
                return onTextDocumentHighlights(request);
            case "textDocument/documentSymbol":
                return onTextDocumentSymbols(request);
            case "textDocument/formatting":
                return onTextDocumentFormatting(request);
            case "textDocument/rangeFormatting":
                return onTextDocumentRangeFormatting(request);
            case "textDocument/onTypeFormatting":
                return onTextDocumentOnTypeFormatting(request);
            case "textDocument/definition":
                return onTextDocumentDefinition(request);
            case "textDocument/codeAction":
                return onTextDocumentCodeAction(request);
            case "textDocument/codeLens":
                return onTextDocumentCodeLenses(request);
            case "codeLens/resolve":
                return onCodeLensResolve(request);
            case "textDocument/documentLink":
                return onTextDocumentLink(request);
            case "documentLink/resolve":
                return onDocumentLinkResolve(request);
            case "textDocument/rename":
                return onTextDocumentRename(request);
            default:
                return onOther(request);
        }
    }

    /**
     * Responds to any other request
     *
     * @param request The request
     * @return The response
     */
    protected JsonRpcResponse onOther(JsonRpcRequest request) {
        return JsonRpcResponseError.newInvalidRequest(request.getIdentifier());
    }

    /**
     * The initialize request is sent as the first request from the client to the server.
     *
     * @param request The request
     * @return The response
     */
    protected JsonRpcResponse onInitialize(JsonRpcRequest request) {
        server.clientCapabilities = (ClientCapabilities) request.getParams();
        Reply reply = server.initialize();
        if (!reply.isSuccess())
            return JsonRpcResponseError.newInternalError(request.getIdentifier());
        return new JsonRpcResponseResult<>(request.getIdentifier(), server.serverCapabilities);
    }

    /**
     * The initialized notification is sent from the client to the server after the client received the result
     * of the initialize request but before the client is sending any other request or notification to the server.
     * The server can use the initialized notification for example to dynamically register capabilities.
     *
     * @param request The request
     * @return The response
     */
    protected JsonRpcResponse onInitialized(JsonRpcRequest request) {
        return null;
    }

    /**
     * The shutdown request is sent from the client to the server.
     * It asks the server to shut down, but to not exit (otherwise the response might not be delivered correctly to the client).
     * There is a separate exit notification that asks the server to exit.
     *
     * @param request The request
     * @return The response
     */
    protected JsonRpcResponse onShutdown(JsonRpcRequest request) {
        Reply reply = server.shutdown();
        if (!reply.isSuccess())
            return JsonRpcResponseError.newInternalError(request.getIdentifier());
        return new JsonRpcResponseResult<>(request.getIdentifier(), null);
    }

    /**
     * A notification to ask the server to exit its process.
     * The server should exit with success code 0 if the shutdown request has been received before; otherwise with error code 1.
     *
     * @param request The request
     * @return The response
     */
    protected JsonRpcResponse onExit(JsonRpcRequest request) {
        server.exit();
        return null;
    }

    /**
     * The base protocol offers support for request cancellation.
     *
     * @param request The request
     * @return The response
     */
    protected abstract JsonRpcResponse onCancelRequest(JsonRpcRequest request);

    /**
     * A notification sent from the client to the server to signal the change of configuration settings.
     *
     * @param request The request
     * @return The response
     */
    protected abstract JsonRpcResponse onWorkspaceDidChangeConfiguration(JsonRpcRequest request);

    /**
     * The watched files notification is sent from the client to the server when the client detects changes to files watched by the language client.
     *
     * @param request The request
     * @return The response
     */
    protected abstract JsonRpcResponse onWorkspaceDidChangeWatchedFiles(JsonRpcRequest request);

    /**
     * The workspace symbol request is sent from the client to the server to list project-wide symbols matching the query string.
     *
     * @param request The request
     * @return The response
     */
    protected abstract JsonRpcResponse onWorkspaceSymbol(JsonRpcRequest request);

    /**
     * The workspace/executeCommand request is sent from the client to the server to trigger command execution on the server.
     * In most cases the server creates a WorkspaceEdit structure and applies the changes to the workspace using the request workspace/applyEdit which is sent from the server to the client.
     *
     * @param request The request
     * @return The response
     */
    protected abstract JsonRpcResponse onWorkspaceExecuteCommand(JsonRpcRequest request);

    /**
     * The document open notification is sent from the client to the server to signal newly opened text documents.
     * The document's truth is now managed by the client and the server must not try to read the document's truth using the document's uri.
     *
     * @param request The request
     * @return The response
     */
    protected abstract JsonRpcResponse onTextDocumentDidOpen(JsonRpcRequest request);

    /**
     * The document change notification is sent from the client to the server to signal changes to a text document.
     * In 2.0 the shape of the params has changed to include proper version numbers and language ids.
     *
     * @param request The request
     * @return The response
     */
    protected abstract JsonRpcResponse onTextDocumentDidChange(JsonRpcRequest request);

    /**
     * The document will save notification is sent from the client to the server before the document is actually saved.
     *
     * @param request The request
     * @return The response
     */
    protected abstract JsonRpcResponse onTextDocumentWillSave(JsonRpcRequest request);

    /**
     * The document will save request is sent from the client to the server before the document is actually saved.
     * The request can return an array of TextEdits which will be applied to the text document before it is saved.
     * Please note that clients might drop results if computing the text edits took too long or if a server constantly fails on this request.
     * This is done to keep the save fast and reliable.
     *
     * @param request The request
     * @return The response
     */
    protected abstract JsonRpcResponse onTextDocumentWillSaveUntil(JsonRpcRequest request);

    /**
     * The document save notification is sent from the client to the server when the document was saved in the client.
     *
     * @param request The request
     * @return The response
     */
    protected abstract JsonRpcResponse onTextDocumentDidSave(JsonRpcRequest request);

    /**
     * The document close notification is sent from the client to the server when the document got closed in the client.
     * The document's truth now exists where the document's uri points to (e.g. if the document's uri is a file uri the truth now exists on disk).
     *
     * @param request The request
     * @return The response
     */
    protected abstract JsonRpcResponse onTextDocumentDidClose(JsonRpcRequest request);

    /**
     * The Completion request is sent from the client to the server to compute completion items at a given cursor position.
     * Completion items are presented in the IntelliSense user interface.
     * If computing full completion items is expensive, servers can additionally provide a handler for the completion item resolve request ('completionItem/resolve').
     * This request is sent when a completion item is selected in the user interface.
     * A typical use case is for example:
     * the 'textDocument/completion' request doesn't fill in the documentation property for returned completion items since it is expensive to compute.
     * When the item is selected in the user interface then a 'completionItem/resolve' request is sent with the selected completion item as a param.
     * The returned completion item should have the documentation property filled in.
     *
     * @param request The request
     * @return The response
     */
    protected abstract JsonRpcResponse onTextDocumentCompletion(JsonRpcRequest request);

    /**
     * The request is sent from the client to the server to resolve additional information for a given completion item.
     *
     * @param request The request
     * @return The response
     */
    protected abstract JsonRpcResponse onCompletionItemResolve(JsonRpcRequest request);

    /**
     * The hover request is sent from the client to the server to request hover information at a given text document position.
     *
     * @param request The request
     * @return The response
     */
    protected abstract JsonRpcResponse onTextDocumentHover(JsonRpcRequest request);

    /**
     * The signature help request is sent from the client to the server to request signature information at a given cursor position.
     *
     * @param request The request
     * @return The response
     */
    protected abstract JsonRpcResponse onTextDocumentSignatureHelp(JsonRpcRequest request);

    /**
     * The references request is sent from the client to the server to resolve project-wide references for the symbol denoted by the given text document position.
     *
     * @param request The request
     * @return The response
     */
    protected abstract JsonRpcResponse onTextDocumentReferences(JsonRpcRequest request);

    /**
     * The document highlight request is sent from the client to the server to resolve a document highlights for a given text document position.
     * For programming languages this usually highlights all references to the symbol scoped to this file.
     * However we kept 'textDocument/documentHighlight' and 'textDocument/references' separate requests since the first one is allowed to be more fuzzy.
     * Symbol matches usually have a DocumentHighlightKind of Read or Write whereas fuzzy or textual matches use Text as the kind.
     *
     * @param request The request
     * @return The response
     */
    protected abstract JsonRpcResponse onTextDocumentHighlights(JsonRpcRequest request);

    /**
     * The document symbol request is sent from the client to the server to list all symbols found in a given text document.
     *
     * @param request The request
     * @return The response
     */
    protected abstract JsonRpcResponse onTextDocumentSymbols(JsonRpcRequest request);

    /**
     * The document formatting request is sent from the server to the client to format a whole document.
     *
     * @param request The request
     * @return The response
     */
    protected abstract JsonRpcResponse onTextDocumentFormatting(JsonRpcRequest request);

    /**
     * The document range formatting request is sent from the client to the server to format a given range in a document.
     *
     * @param request The request
     * @return The response
     */
    protected abstract JsonRpcResponse onTextDocumentRangeFormatting(JsonRpcRequest request);

    /**
     * The document on type formatting request is sent from the client to the server to format parts of the document during typing.
     *
     * @param request The request
     * @return The response
     */
    protected abstract JsonRpcResponse onTextDocumentOnTypeFormatting(JsonRpcRequest request);

    /**
     * The goto definition request is sent from the client to the server to resolve the definition location of a symbol at a given text document position.
     *
     * @param request The request
     * @return The response
     */
    protected abstract JsonRpcResponse onTextDocumentDefinition(JsonRpcRequest request);

    /**
     * The code action request is sent from the client to the server to compute commands for a given text document and range.
     * These commands are typically code fixes to either fix problems or to beautify/refactor code.
     *
     * @param request The request
     * @return The response
     */
    protected abstract JsonRpcResponse onTextDocumentCodeAction(JsonRpcRequest request);

    /**
     * The code lens request is sent from the client to the server to compute code lenses for a given text document.
     *
     * @param request The request
     * @return The response
     */
    protected abstract JsonRpcResponse onTextDocumentCodeLenses(JsonRpcRequest request);

    /**
     * The code lens resolve request is sent from the client to the server to resolve the command for a given code lens item.
     *
     * @param request The request
     * @return The response
     */
    protected abstract JsonRpcResponse onCodeLensResolve(JsonRpcRequest request);

    /**
     * The document links request is sent from the client to the server to request the location of links in a document.
     *
     * @param request The request
     * @return The response
     */
    protected abstract JsonRpcResponse onTextDocumentLink(JsonRpcRequest request);

    /**
     * The document link resolve request is sent from the client to the server to resolve the target of a given document link.
     *
     * @param request The request
     * @return The response
     */
    protected abstract JsonRpcResponse onDocumentLinkResolve(JsonRpcRequest request);

    /**
     * The rename request is sent from the client to the server to perform a workspace-wide rename of a symbol.
     *
     * @param request The request
     * @return The response
     */
    protected abstract JsonRpcResponse onTextDocumentRename(JsonRpcRequest request);
}
