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

import org.xowl.infra.lsp.structures.*;

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
    private final Map<String, Document> documents;

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
     * Initializes an empty workspace
     */
    public Workspace() {
        this.documents = new HashMap<>();
    }

    /**
     * When file events have been received
     *
     * @param events The received file events
     */
    public void onFileEvents(FileEvent[] events) {
        // do nothing
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
        if (document != null)
            document.mutateTo(textDocument.getVersion(), contentChanges);
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
            if (document != null)
                document.setFullContent(text);
        }
    }

    /**
     * When a document has been closed on the client
     *
     * @param textDocument The document that was closed
     */
    public void onDocumentDidClose(TextDocumentIdentifier textDocument) {
        documents.remove(textDocument.getUri());
    }
}
