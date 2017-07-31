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

import org.osgi.framework.*;

import java.util.Collection;
import java.util.ServiceLoader;
import java.util.WeakHashMap;

/**
 * The provider of document analyzers
 *
 * @author Laurent Wouters
 */
public class DocumentAnalyzerProvider {
    /**
     * The singleton instance
     */
    private static final DocumentAnalyzerProvider INSTANCE = new DocumentAnalyzerProvider();

    /**
     * The Java service loader for the analyzers
     */
    private final ServiceLoader<DocumentAnalyzer> javaProvider;
    /**
     * The map of the resolved analyzers
     */
    private final WeakHashMap<Document, DocumentAnalyzer> resolvedAnalyzers;

    /**
     * Initialize this provider
     */
    private DocumentAnalyzerProvider() {
        this.javaProvider = ServiceLoader.load(DocumentAnalyzer.class);
        this.resolvedAnalyzers = new WeakHashMap<>();
    }

    /**
     * Gets the document analyzer for the specified document
     *
     * @param document A document
     * @return The corresponding analyzer
     */
    public static DocumentAnalyzer getAnalyzer(Document document) {
        return INSTANCE.doGetAnalyzer(document);
    }

    /**
     * Gets the document analyzer for the specified document
     *
     * @param document A document
     * @return The corresponding analyzer
     */
    private DocumentAnalyzer doGetAnalyzer(Document document) {
        DocumentAnalyzer best = resolvedAnalyzers.get(document);
        if (best != null)
            return best;

        int bestPriority = -1;

        for (DocumentAnalyzer analyzer : javaProvider) {
            int priority = analyzer.getPriorityFor(document);
            if (priority > bestPriority) {
                best = analyzer;
                bestPriority = priority;
            }
        }

        Bundle bundle = FrameworkUtil.getBundle(DocumentContentProvider.class);
        if (bundle != null) {
            BundleContext context = FrameworkUtil.getBundle(DocumentContentProvider.class).getBundleContext();
            if (context != null) {
                try {
                    Collection<ServiceReference<DocumentAnalyzer>> references = context.getServiceReferences(DocumentAnalyzer.class, null);
                    for (ServiceReference<DocumentAnalyzer> reference : references) {
                        DocumentAnalyzer analyzer = context.getService(reference);
                        context.ungetService(reference);
                        int priority = analyzer.getPriorityFor(document);
                        if (priority > bestPriority) {
                            best = analyzer;
                            bestPriority = priority;
                        }
                    }
                } catch (InvalidSyntaxException exception) {
                    // cannot happen
                }
            }
        }
        return best;
    }
}
