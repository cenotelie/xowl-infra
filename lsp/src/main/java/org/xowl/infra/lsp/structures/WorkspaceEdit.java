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
import org.xowl.infra.utils.json.JsonDeserializer;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * A workspace edit represents changes to many resources managed in the workspace.
 *
 * @author Laurent Wouters
 */
public class WorkspaceEdit implements Serializable {
    /**
     * Holds changes to existing resources.
     */
    private final Map<String, TextEdit[]> changes;

    /**
     * Gets the changed resources
     *
     * @return The changed resources
     */
    public Collection<String> getChangedResources() {
        return changes.keySet();
    }

    /**
     * Gets the changes for a resource
     *
     * @param resource The resource
     * @return The changes
     */
    public TextEdit[] getChangesFor(String resource) {
        return changes.get(resource);
    }

    /**
     * Initializes this structure
     */
    public WorkspaceEdit() {
        this.changes = new HashMap<>();
    }

    /**
     * Initializes this structure
     *
     * @param definition   The serialized definition
     * @param deserializer The current deserializer
     */
    public WorkspaceEdit(ASTNode definition, JsonDeserializer deserializer) {
        this.changes = new HashMap<>();
        for (ASTNode child : definition.getChildren()) {
            ASTNode nodeMemberName = child.getChildren().get(0);
            String name = TextUtils.unescape(nodeMemberName.getValue());
            name = name.substring(1, name.length() - 1);
            ASTNode nodeValue = child.getChildren().get(1);
            switch (name) {
                case "changes": {
                    loadChanges(nodeValue);
                    break;
                }
            }
        }
    }

    /**
     * Loads the set of changes from the definition
     *
     * @param definition The definition of changes
     */
    private void loadChanges(ASTNode definition) {
        for (ASTNode child : definition.getChildren()) {
            ASTNode nodeMemberName = child.getChildren().get(0);
            String name = TextUtils.unescape(nodeMemberName.getValue());
            name = name.substring(1, name.length() - 1);
            ASTNode nodeValue = child.getChildren().get(1);

            TextEdit[] changes = new TextEdit[nodeValue.getChildren().size()];
            int index = 0;
            for (ASTNode change : nodeValue.getChildren())
                changes[index++] = new TextEdit(change);
            this.changes.put(name, changes);
        }
    }

    /**
     * Adds changes to a resource
     *
     * @param resource The changed resource
     * @param changes  The changes
     */
    public void addChanges(String resource, TextEdit[] changes) {
        TextEdit[] old = this.changes.get(resource);
        if (old == null) {
            this.changes.put(resource, changes);
            return;
        }
        TextEdit[] concat = Arrays.copyOf(old, old.length + changes.length);
        System.arraycopy(changes, 0, concat, old.length, changes.length);
        this.changes.put(resource, concat);
    }

    @Override
    public String serializedString() {
        return serializedJSON();
    }

    @Override
    public String serializedJSON() {
        StringBuilder builder = new StringBuilder();
        builder.append("{\"changes\": {");
        boolean first = true;
        for (Map.Entry<String, TextEdit[]> entry : changes.entrySet()) {
            if (!first)
                builder.append(", ");
            first = false;
            builder.append("\"");
            builder.append(TextUtils.escapeStringJSON(entry.getKey()));
            builder.append("\": [");
            for (int i = 0; i != entry.getValue().length; i++) {
                if (i != 0)
                    builder.append(", ");
                builder.append(entry.getValue()[i].serializedJSON());
            }
            builder.append("]");
        }
        builder.append("}}");
        return builder.toString();
    }
}
