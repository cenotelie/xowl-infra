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

package org.xowl.infra.lsp.engine;

import org.xowl.infra.jsonrpc.JsonRpcRequest;
import org.xowl.infra.lsp.LspEndpointLocal;
import org.xowl.infra.lsp.structures.*;
import org.xowl.infra.utils.IOUtils;
import org.xowl.infra.utils.logging.Logging;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents the current workspace for a server
 *
 * @author Laurent Wouters
 */
public class Workspace {
    /**
     * The documents in the workspace
     */
    protected final Map<String, Document> documents;
    /**
     * The symbol registry
     */
    protected final SymbolRegistry symbolRegistry;
    /**
     * The local LSP endpoint
     */
    protected LspEndpointLocal local;

    /**
     * Gets the documents in the workspace
     *
     * @return The documents in the workspace
     */
    public Collection<Document> getDocuments() {
        return documents.values();
    }

    /**
     * Gets the document for the specified URI
     *
     * @param uri The URI of a document
     * @return The document, or null if it does not exist
     */
    public Document getDocument(String uri) {
        return documents.get(uri);
    }

    /**
     * Gets the associated symbol registry
     *
     * @return The associated symbol registry
     */
    public SymbolRegistry getSymbols() {
        return symbolRegistry;
    }

    /**
     * Gets the local LSP endpoint
     *
     * @return The local LSP endpoint
     */
    public LspEndpointLocal getLocal() {
        return local;
    }

    /**
     * Sets the local LSP endpoint
     *
     * @param local The local LSP endpoint
     */
    public void setLocal(LspEndpointLocal local) {
        this.local = local;
    }

    /**
     * Initializes an empty workspace
     */
    public Workspace() {
        this.documents = new HashMap<>();
        this.symbolRegistry = new SymbolRegistry();
    }

    /**
     * Initializes this workspace
     *
     * @param rootUri  The root uri for the workspace
     * @param rootPath The root path for the workspace
     */
    public void onInitWorkspace(String rootUri, String rootPath) {
        File workspaceRoot = null;
        if (rootUri != null && rootUri.startsWith("file://"))
            workspaceRoot = new File(rootUri.substring("file://".length()));
        else if (rootPath != null)
            workspaceRoot = new File(rootPath);
        if (workspaceRoot != null && workspaceRoot.exists())
            scanWorkspace(workspaceRoot);
    }

    /**
     * Scans the specified content
     *
     * @param file The current file or directory
     */
    public void scanWorkspace(File file) {
        if (isWorkspaceExcluded(file))
            return;
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files == null)
                return;
            for (int i = 0; i != files.length; i++)
                scanWorkspace(files[i]);
        } else {
            if (!isWorkspaceIncluded(file))
                return;
            Document document = resolveDocument(file);
            if (document == null)
                return;
            onDocumentUpdated(document, false);
        }
    }

    /**
     * Determines whether the specified file or directory is excluded
     *
     * @param file The current file or directory
     * @return true if the file is excluded
     */
    protected boolean isWorkspaceExcluded(File file) {
        if (file.isDirectory()) {
            String name = file.getName();
            return (name.equals(".git") || name.equals(".hg") || name.equals(".svn"));
        }
        return false;
    }

    /**
     * Determines whether the specified file should be analyzed
     *
     * @param file The current file
     * @return true if the file should be analyzed
     */
    protected boolean isWorkspaceIncluded(File file) {
        return false;
    }

    /**
     * Resolves the document for the specified file
     *
     * @param file The file
     * @return The document
     */
    protected Document resolveDocument(File file) {
        String uri = "file://" + file.getAbsolutePath();
        Document document = documents.get(uri);
        if (document == null) {
            try (Reader reader = IOUtils.getAutoReader(file)) {
                String content = IOUtils.read(reader);
                document = new Document(uri, getLanguageFor(file), 0, content);
                documents.put(uri, document);
            } catch (IOException exception) {
                Logging.get().error(exception);
            }
        }
        return document;
    }

    /**
     * Gets the language associated to the specified file
     *
     * @param file The file
     * @return The associated language
     */
    protected String getLanguageFor(File file) {
        return "text";
    }

    /**
     * When file events have been received
     *
     * @param events The received file events
     */
    public void onFileEvents(FileEvent[] events) {
        for (int i = 0; i != events.length; i++) {
            onFileEvent(events[i]);
        }
    }

    /**
     * When file event have been received
     *
     * @param event The received file event
     */
    private void onFileEvent(FileEvent event) {
        switch (event.getType()) {
            case FileChangeType.DELETED: {
                Document document = documents.get(event.getUri());
                if (document == null)
                    return;
                documents.remove(document.getUri());
                symbolRegistry.onDocumentRemoved(document);
                break;
            }
            case FileChangeType.CREATED: {
                if (!event.getUri().startsWith("file://"))
                    return;
                File file = new File(event.getUri().substring("file://".length()));
                if (!file.exists() || isWorkspaceExcluded(file) || !isWorkspaceIncluded(file))
                    return;
                Document document = resolveDocument(file);
                if (document == null)
                    return;
                onDocumentUpdated(document, true);
                break;
            }
            case FileChangeType.CHANGED: {
                Document document = documents.get(event.getUri());
                if (document == null)
                    return;
                onDocumentUpdated(document, true);
                break;
            }
        }
    }

    /**
     * When a text document has been open
     *
     * @param documentItem The document item
     */
    public void onDocumentOpen(TextDocumentItem documentItem) {
        documents.put(documentItem.getUri(), new Document(documentItem.getUri(), documentItem.getLanguageId(), documentItem.getVersion(), documentItem.getText()));
    }

    /**
     * When document changes occurred on the client
     *
     * @param textDocument   The document that did change
     * @param contentChanges The actual content changes
     */
    public void onDocumentChange(VersionedTextDocumentIdentifier textDocument, TextDocumentContentChangeEvent[] contentChanges) {
        Document document = documents.get(textDocument.getUri());
        if (document != null) {
            document.mutateTo(textDocument.getVersion(), contentChanges);
            onDocumentUpdated(document, true);
        }
    }

    /**
     * When a document is being saved
     *
     * @param textDocument The document that was is being saved
     * @param reason       The reason for the save
     */
    public void onDocumentWillSave(TextDocumentIdentifier textDocument, int reason) {
        // do nothing
    }

    /**
     * When a document is being saved
     *
     * @param textDocument The document that was is being saved
     * @param reason       The reason for the save
     */
    public TextEdit[] onDocumentWillSaveUntil(TextDocumentIdentifier textDocument, int reason) {
        // do nothing
        return null;
    }

    /**
     * When a document has been saved on the client
     *
     * @param textDocument The document that was saved
     * @param text         The full text for the saved document, if available
     */
    public void onDocumentDidSave(TextDocumentIdentifier textDocument, String text) {
        if (text != null) {
            Document document = documents.get(textDocument.getUri());
            if (document != null) {
                document.setFullContent(text);
                onDocumentUpdated(document, true);
            }
        }
    }

    /**
     * When a document has been closed on the client
     *
     * @param textDocument The document that was closed
     */
    public void onDocumentDidClose(TextDocumentIdentifier textDocument) {
        // do nothing
    }

    /**
     * When a document has been updated
     *
     * @param document           The updated document
     * @param publishDiagnostics Whether to publish the diagnostics
     */
    protected void onDocumentUpdated(Document document, boolean publishDiagnostics) {
        DocumentAnalyzer analyzer = DocumentAnalyzerProvider.getAnalyzer(document);
        if (analyzer == null)
            return;
        DocumentAnalysis analysis = analyzer.analyze(symbolRegistry, document);
        if (analysis.getDiagnostics() != null && analysis.getDiagnostics().length > 0) {
            if (local != null && publishDiagnostics) {
                local.send(new JsonRpcRequest(
                        Integer.toString(local.getNextId()),
                        "textDocument/publishDiagnostics",
                        new PublishDiagnosticsParams(document.getUri(), analysis.getDiagnostics())
                ));
            }
        }
        symbolRegistry.onDocumentChanged(document, analysis.getSymbols());
    }
}
