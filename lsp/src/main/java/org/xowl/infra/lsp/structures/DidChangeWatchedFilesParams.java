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
 * Parameters for the notification of file changes
 *
 * @author Laurent Wouters
 */
public class DidChangeWatchedFilesParams implements Serializable {
    /**
     * The actual file events
     */
    private final FileEvent[] changes;

    /**
     * Gets the actual file events
     *
     * @return The actual file events
     */
    public FileEvent[] getChanges() {
        return changes;
    }

    /**
     * Initializes this structure
     *
     * @param changes The actual file events
     */
    public DidChangeWatchedFilesParams(FileEvent[] changes) {
        this.changes = changes;
    }

    /**
     * Initializes this structure
     *
     * @param definition The serialized definition
     */
    public DidChangeWatchedFilesParams(ASTNode definition) {
        FileEvent[] changes = null;
        for (ASTNode child : definition.getChildren()) {
            ASTNode nodeMemberName = child.getChildren().get(0);
            String name = TextUtils.unescape(nodeMemberName.getValue());
            name = name.substring(1, name.length() - 1);
            ASTNode nodeValue = child.getChildren().get(1);
            switch (name) {
                case "changes": {
                    changes = new FileEvent[nodeValue.getChildren().size()];
                    int index = 0;
                    for (ASTNode change : nodeValue.getChildren())
                        changes[index++] = new FileEvent(change);
                    break;
                }
            }
        }
        this.changes = changes;
    }

    @Override
    public String serializedString() {
        return serializedJSON();
    }

    @Override
    public String serializedJSON() {
        StringBuilder builder = new StringBuilder();
        builder.append("{\"changes\": [");
        for (int i = 0; i != changes.length; i++) {
            if (i != 0)
                builder.append(", ");
            builder.append(changes[i].serializedJSON());
        }
        builder.append("]}");
        return builder.toString();
    }
}
