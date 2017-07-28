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
 * An event describing a file change
 *
 * @author Laurent Wouters
 */
public class FileEvent implements Serializable {
    /**
     * The file's URI
     */
    private final String uri;
    /**
     * The change type
     */
    private final int type;

    /**
     * Gets the file's URI
     *
     * @return The file's URI
     */
    public String getUri() {
        return uri;
    }

    /**
     * Gets the change type
     *
     * @return The change type
     */
    public int getType() {
        return type;
    }

    /**
     * Initializes this structure
     *
     * @param uri  The file's URI
     * @param type The change type
     */
    public FileEvent(String uri, int type) {
        this.uri = uri;
        this.type = type;
    }

    /**
     * Initializes this structure
     *
     * @param definition The serialized definition
     */
    public FileEvent(ASTNode definition) {
        String uri = "";
        int type = FileChangeType.CHANGED;
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
                case "type": {
                    type = Integer.parseInt(nodeValue.getValue());
                    break;
                }
            }
        }
        this.uri = uri;
        this.type = type;
    }

    @Override
    public String serializedString() {
        return serializedJSON();
    }

    @Override
    public String serializedJSON() {
        return "{\"uri\": \"" +
                TextUtils.escapeStringJSON(uri) +
                "\", \"type\": " +
                Integer.toString(type) +
                "}";
    }
}
