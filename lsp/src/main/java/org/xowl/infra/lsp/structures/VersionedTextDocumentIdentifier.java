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
 * An identifier to denote a specific version of a text document.
 *
 * @author Laurent Wouters
 */
public class VersionedTextDocumentIdentifier implements Serializable {
    /**
     * The version number of this document.
     */
    private final int version;

    /**
     * Gets the version number of this document
     *
     * @return The version number of this document
     */
    public int getVersion() {
        return version;
    }

    /**
     * Initializes this structure
     *
     * @param version The version number of this document
     */
    public VersionedTextDocumentIdentifier(int version) {
        this.version = version;
    }

    /**
     * Initializes this structure
     *
     * @param definition The serialized definition
     */
    public VersionedTextDocumentIdentifier(ASTNode definition) {
        int version = 0;
        for (ASTNode child : definition.getChildren()) {
            ASTNode nodeMemberName = child.getChildren().get(0);
            String name = TextUtils.unescape(nodeMemberName.getValue());
            name = name.substring(1, name.length() - 1);
            ASTNode nodeValue = child.getChildren().get(1);
            switch (name) {
                case "version": {
                    version = Integer.parseInt(nodeValue.getValue());
                    break;
                }
            }
        }
        this.version = version;
    }

    @Override
    public String serializedString() {
        return serializedJSON();
    }

    @Override
    public String serializedJSON() {
        return "{\"version\": " +
                Integer.toString(version) +
                "}";
    }
}
