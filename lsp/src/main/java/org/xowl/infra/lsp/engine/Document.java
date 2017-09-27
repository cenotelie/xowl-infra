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

import org.xowl.infra.lsp.structures.TextDocumentContentChangeEvent;
import org.xowl.infra.lsp.structures.TextEdit;

/**
 * Represents a document in a workspace
 *
 * @author Laurent Wouters
 */
public class Document {
    /**
     * The document's URI
     */
    private final String uri;
    /**
     * The language's identifier for this document
     */
    private final String languageId;
    /**
     * The last known version of the document
     */
    private DocumentVersion currentVersion;
    /**
     * The last analysis of this document
     */
    private DocumentAnalysis lastAnalysis;

    /**
     * Gets the document's URI
     *
     * @return The document's URI
     */
    public String getUri() {
        return uri;
    }

    /**
     * Gets the language's identifier for this document
     *
     * @return The language's identifier for this document
     */
    public String getLanguageId() {
        return languageId;
    }

    /**
     * Gets the document's current version
     *
     * @return The document's current version
     */
    public DocumentVersion getCurrentVersion() {
        return currentVersion;
    }

    /**
     * Gets the result of the last analysis performed on this document
     *
     * @return The last performed analysis
     */
    public DocumentAnalysis getLastAnalysis() {
        return lastAnalysis;
    }

    /**
     * Sets the result of the last analysis performed on this document
     *
     * @param analysis The last performed analysis
     */
    public void setLastAnalysis(DocumentAnalysis analysis) {
        this.lastAnalysis = analysis;
    }

    /**
     * Initializes this document
     *
     * @param uri         The document's URI
     * @param languageId  The language's identifier for this document
     * @param versionInit The initial version number for this document
     * @param text        The full text of the document
     */
    public Document(String uri, String languageId, int versionInit, String text) {
        this.uri = uri;
        this.languageId = languageId;
        this.currentVersion = new DocumentVersion(versionInit, DocumentContentProvider.getContent(text));
    }

    /**
     * Resets the full context for this document
     *
     * @param text The full context for this document
     */
    public void setFullContent(String text) {
        this.currentVersion = new DocumentVersion(currentVersion.getNumber(), DocumentContentProvider.getContent(text));
    }

    /**
     * From the current version of this document, applies the specified edits to mutate into a new version
     *
     * @param nextNumber The new version number
     * @param edits      The edits to be applied
     */
    public void mutateTo(int nextNumber, TextEdit[] edits) {
        currentVersion = currentVersion.mutateTo(nextNumber, edits);
    }

    /**
     * From the current version of this document, applies the specified edits to mutate into a new version
     *
     * @param nextNumber The new version number
     * @param events     The events to be applied
     */
    public void mutateTo(int nextNumber, TextDocumentContentChangeEvent[] events) {
        currentVersion = currentVersion.mutateTo(nextNumber, events);
    }
}
