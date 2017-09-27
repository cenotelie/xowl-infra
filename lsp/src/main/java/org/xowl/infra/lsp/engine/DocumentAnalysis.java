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

import org.xowl.infra.lsp.structures.Diagnostic;
import org.xowl.infra.lsp.structures.DocumentLink;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the result of a document analysis
 *
 * @author Laurent Wouters
 */
public class DocumentAnalysis {
    /**
     * The symbols for the document
     */
    protected final DocumentSymbols symbols;
    /**
     * The diagnostics for the document
     */
    protected final List<Diagnostic> diagnostics;
    /**
     * The links within the document
     */
    protected final List<DocumentLink> links;

    /**
     * Gets the symbols for the document
     *
     * @return The symbols for the document
     */
    public DocumentSymbols getSymbols() {
        return symbols;
    }

    /**
     * The diagnostics for the document
     *
     * @return The diagnostics for the document
     */
    public List<Diagnostic> getDiagnostics() {
        return diagnostics;
    }

    /**
     * Gets the links within the document
     *
     * @return The links within the document
     */
    public List<DocumentLink> getLinks() {
        return links;
    }

    /**
     * Initializes this analysis
     */
    public DocumentAnalysis() {
        this.symbols = new DocumentSymbols();
        this.diagnostics = new ArrayList<>();
        this.links = new ArrayList<>();
    }
}
