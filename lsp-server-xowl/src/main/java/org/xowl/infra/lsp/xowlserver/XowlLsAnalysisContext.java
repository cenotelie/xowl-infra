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

package org.xowl.infra.lsp.xowlserver;

import fr.cenotelie.hime.redist.Text;
import org.xowl.infra.lsp.engine.DocumentAnalysis;
import org.xowl.infra.lsp.engine.SymbolFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a context for an analysis
 *
 * @author Laurent Wouters
 */
public class XowlLsAnalysisContext {
    /**
     * The text input that was parsed
     */
    public final Text input;
    /**
     * The factory for symbols
     */
    public final SymbolFactory factory;
    /**
     * The current analysis to fill
     */
    public final DocumentAnalysis analysis;
    /**
     * The URI of the resource currently being loaded
     */
    public final String resource;
    /**
     * The base URI for relative URIs
     */
    public String baseURI;
    /**
     * Map of the current namespaces
     */
    public final Map<String, String> namespaces;

    /**
     * Initializes this context
     *
     * @param resourceUri The URI of the resource
     * @param input       The text input that was parsed
     * @param factory     The factory for symbols
     * @param analysis    The current analysis to fill
     */
    public XowlLsAnalysisContext(String resourceUri, Text input, SymbolFactory factory, DocumentAnalysis analysis) {
        this.input = input;
        this.factory = factory;
        this.analysis = analysis;
        this.resource = resourceUri;
        this.baseURI = resourceUri;
        this.namespaces = new HashMap<>();
    }
}
