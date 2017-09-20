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

import org.xowl.infra.lsp.structures.CompletionItem;
import org.xowl.infra.lsp.structures.CompletionList;
import org.xowl.infra.lsp.structures.Position;

/**
 * Represents an entity that provides completion items for a document
 *
 * @author Laurent Wouters
 */
public interface DocumentCompleter extends DocumentService {
    /**
     * Gets the completion items for the specified document and position
     *
     * @param document The document
     * @param position The position within the document
     * @return The completion items
     */
    CompletionList getCompletionItems(Document document, Position position);

    /**
     * Resolves additional information for a completion item
     *
     * @param document The document
     * @param position The position within the document
     * @param item     The completion item to resolve
     * @return The resolved completion item
     */
    CompletionItem resolve(Document document, Position position, CompletionItem item);
}
