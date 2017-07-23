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
import org.xowl.infra.utils.TextUtils;

/**
 * Represents a location inside a resource, such as a line inside a text file.
 *
 * @author Laurent Wouters
 */
public class Location implements Serializable {
    /**
     * The document's URI
     */
    private final String uri;
    /**
     * The range in the document
     */
    private final Range range;

    /**
     * Gets the document's URI
     *
     * @return The document's URI
     */
    public String getUri() {
        return uri;
    }

    /**
     * Gets the range in the document
     *
     * @return The range in the document
     */
    public Range getRange() {
        return range;
    }

    /**
     * Initializes this structure
     *
     * @param uri   The document's URI
     * @param range The range in the document
     */
    public Location(String uri, Range range) {
        this.uri = uri;
        this.range = range;
    }

    /**
     * Initializes this structure
     *
     * @param definition The serialized definition
     */
    public Location(ASTNode definition) {
        String uri = "";
        Range range = null;
        for (ASTNode child : definition.getChildren()) {
            ASTNode nodeMemberName = child.getChildren().get(0);
            String name = TextUtils.unescape(nodeMemberName.getValue());
            name = name.substring(1, name.length() - 1);
            ASTNode nodeValue = child.getChildren().get(1);
            switch (name) {
                case "uri": {
                    uri = TextUtils.unescape(nodeValue.getValue());
                    uri = uri.substring(1, uri.length() - 1);
                    break;
                }
                case "character": {
                    range = new Range(nodeValue);
                    break;
                }
            }
        }
        this.uri = uri;
        this.range = range != null ? range : new Range(new Position(0, 0), new Position(0, 0));
    }

    @Override
    public String serializedString() {
        return serializedJSON();
    }

    @Override
    public String serializedJSON() {
        return "{\"uri\": \"" +
                TextUtils.escapeStringJSON(uri) +
                "\", \"range\": " +
                range.serializedJSON() +
                "}";
    }
}
