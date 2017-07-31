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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * The registry of symbols found in the managed files
 *
 * @author Laurent Wouters
 */
public class SymbolRegistry {
    /**
     * The known symbols
     */
    private final Map<String, Symbol> symbols;

    /**
     * Initializes this registry
     */
    public SymbolRegistry() {
        this.symbols = new HashMap<>();
    }

    /**
     * Resolves the specified symbol
     *
     * @param identifier The unique identifier for the symbol
     * @param name       The name of the symbol
     * @return The symbol data
     */
    public Symbol resolve(String identifier, String name) {
        Symbol symbol = symbols.get(identifier);
        if (symbol == null) {
            symbol = new Symbol(identifier, name);
            symbols.put(identifier, symbol);
        }
        return symbol;
    }

    /**
     * When a document has been updated
     *
     * @param document The updated document
     */
    public void onDocumentChanged(Document document) {
        DocumentAnalyzer analyzer = DocumentAnalyzerProvider.getAnalyzer(document);
        if (analyzer == null)
            return;
        onDocumentRemoved(document);
        Collection<Symbol> founds = analyzer.getSymbols(this, document);
        for (Symbol found : founds) {
            Symbol original = symbols.get(found.getIdentifier());
            if (original == null) {
                symbols.put(found.getIdentifier(), found);
            } else {
                original.merge(found);
            }
        }
    }

    /**
     * When a document has been removed
     *
     * @param document The removed document
     */
    public void onDocumentRemoved(Document document) {
        Collection<String> toRemove = null;
        for (Map.Entry<String, Symbol> entry : symbols.entrySet()) {
            if (!entry.getValue().onFileRemoved(document.getUri())) {
                if (toRemove == null)
                    toRemove = new ArrayList<>();
                toRemove.add(entry.getKey());
            }
        }
        if (toRemove != null) {
            for (String identifier : toRemove)
                symbols.remove(identifier);
        }
    }
}
