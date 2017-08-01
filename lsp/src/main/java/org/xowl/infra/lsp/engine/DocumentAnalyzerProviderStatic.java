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

/**
 * A static provider of document analyzers
 *
 * @author Laurent Wouters
 */
public class DocumentAnalyzerProviderStatic implements DocumentAnalyzerProvider {
    /**
     * The provided analyzers
     */
    private final DocumentAnalyzer[] analyzers;

    /**
     * Initializes this provider
     *
     * @param analyzers The provided analyzers
     */
    public DocumentAnalyzerProviderStatic(DocumentAnalyzer... analyzers) {
        this.analyzers = analyzers;
    }

    @Override
    public DocumentAnalyzer getAnalyzer(Document document) {
        DocumentAnalyzer best = null;
        int bestPriority = -1;
        for (int i = 0; i != analyzers.length; i++) {
            int priority = analyzers[i].getPriorityFor(document);
            if (priority > bestPriority) {
                best = analyzers[i];
                bestPriority = priority;
            }
        }
        return best;
    }
}
