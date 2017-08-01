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

import java.util.ServiceLoader;
import java.util.WeakHashMap;

/**
 * A provider of document analyzers that uses Java service resolver to find analyzers
 *
 * @author Laurent Wouters
 */
public class DocumentAnalyzerProviderResolveJava implements DocumentAnalyzerProvider {
    /**
     * The map of the resolved analyzers
     */
    private final WeakHashMap<Document, DocumentAnalyzer> resolvedAnalyzers;
    /**
     * The Java service loader for the analyzers
     */
    private final ServiceLoader<DocumentAnalyzer> loader;

    /**
     * Initializes this provider
     */
    public DocumentAnalyzerProviderResolveJava() {
        this.loader = ServiceLoader.load(DocumentAnalyzer.class);
        this.resolvedAnalyzers = new WeakHashMap<>();
    }

    @Override
    public DocumentAnalyzer getAnalyzer(Document document) {
        DocumentAnalyzer best = resolvedAnalyzers.get(document);
        if (best != null)
            return best;

        int bestPriority = -1;

        for (DocumentAnalyzer analyzer : loader) {
            int priority = analyzer.getPriorityFor(document);
            if (priority > bestPriority) {
                best = analyzer;
                bestPriority = priority;
            }
        }
        if (best != null)
            resolvedAnalyzers.put(document, best);
        return best;
    }
}
