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

package org.xowl.infra.lsp.structures;

import fr.cenotelie.hime.redist.ASTNode;
import org.xowl.infra.utils.Serializable;
import org.xowl.infra.utils.json.Json;

/**
 * Represents an element for the selection of documents based on filters
 *
 * @author Laurent Wouters
 */
public class DocumentSelector implements Serializable {
    /**
     * The inner filters
     */
    private final DocumentFilter[] filters;

    /**
     * Gets the inner filter
     *
     * @return The inner filters
     */
    public DocumentFilter[] getFilters() {
        return filters;
    }

    /**
     * Gets the number of filters
     *
     * @return the number of filters
     */
    public int size() {
        return filters.length;
    }

    /**
     * Gets the i-th item
     *
     * @param index The index
     * @return The i-th item
     */
    public DocumentFilter get(int index) {
        return filters[index];
    }

    /**
     * Initializes this structure
     *
     * @param filters The filters
     */
    public DocumentSelector(DocumentFilter[] filters) {
        this.filters = filters;
    }

    /**
     * Initializes this structure
     *
     * @param definition The serialized definition
     */
    public DocumentSelector(ASTNode definition) {
        this.filters = new DocumentFilter[definition.getChildren().size()];
        int index = 0;
        for (ASTNode child : definition.getChildren())
            filters[index++] = new DocumentFilter(child);
    }

    @Override
    public String serializedString() {
        return serializedJSON();
    }

    @Override
    public String serializedJSON() {
        return Json.serialize(filters);
    }
}
