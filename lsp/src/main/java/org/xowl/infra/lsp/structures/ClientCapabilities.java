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
import org.xowl.infra.utils.json.Json;
import org.xowl.infra.utils.json.JsonDeserializer;

/**
 * ClientCapabilities now define capabilities for dynamic registration, workspace and text document features the client supports.
 * The experimental can be used to pass experimental capabilities under development.
 * For future compatibility a ClientCapabilities object literal can have more properties set than currently defined.
 * Servers receiving a ClientCapabilities object literal with unknown properties should ignore these properties.
 * A missing property should be interpreted as an absence of the capability.
 * If a property is missing that defines sub properties all sub properties should be interpreted as an absence of the capability.
 *
 * @author Laurent Wouters
 */
public class ClientCapabilities implements Serializable {
    /**
     * Workspace specific client capabilities.
     */
    private final WorkspaceClientCapabilities workspace;
    /**
     * Text document specific client capabilities.
     */
    private final TextDocumentClientCapabilities textDocument;
    /**
     * Experimental client capabilities.
     */
    private final Object experimental;

    /**
     * Initializes this structure
     */
    public ClientCapabilities() {
        this.workspace = new WorkspaceClientCapabilities();
        this.textDocument = new TextDocumentClientCapabilities();
        this.experimental = null;
    }

    /**
     * Initializes this structure
     *
     * @param workspace    Workspace specific client capabilities.
     * @param textDocument Text document specific client capabilities.
     * @param experimental Experimental client capabilities.
     */
    public ClientCapabilities(
            WorkspaceClientCapabilities workspace,
            TextDocumentClientCapabilities textDocument,
            Object experimental) {
        this.workspace = workspace;
        this.textDocument = textDocument;
        this.experimental = experimental;
    }

    /**
     * Initializes this structure
     *
     * @param definition   The serialized definition
     * @param deserializer The current de-serializer
     */
    public ClientCapabilities(ASTNode definition, JsonDeserializer deserializer) {
        WorkspaceClientCapabilities workspace = null;
        TextDocumentClientCapabilities textDocument = null;
        Object experimental = null;
        for (ASTNode child : definition.getChildren()) {
            ASTNode nodeMemberName = child.getChildren().get(0);
            String name = TextUtils.unescape(nodeMemberName.getValue());
            name = name.substring(1, name.length() - 1);
            ASTNode nodeValue = child.getChildren().get(1);
            switch (name) {
                case "workspace": {
                    workspace = new WorkspaceClientCapabilities(nodeValue, deserializer);
                    break;
                }
                case "textDocument": {
                    textDocument = new TextDocumentClientCapabilities(nodeValue, deserializer);
                    break;
                }
                case "experimental": {
                    experimental = deserializer.deserialize(nodeValue, null);
                    break;
                }
            }
        }
        this.workspace = workspace;
        this.textDocument = textDocument;
        this.experimental = experimental;
    }

    @Override
    public String serializedString() {
        return serializedJSON();
    }

    @Override
    public String serializedJSON() {
        StringBuilder builder = new StringBuilder();
        builder.append("{");
        boolean first = true;
        if (workspace != null) {
            builder.append("\"workspace\": ");
            builder.append(workspace.serializedJSON());
            builder.append("");
            first = false;
        }
        if (textDocument != null) {
            if (!first)
                builder.append(", ");
            builder.append("\"textDocument\": ");
            builder.append(textDocument.serializedJSON());
            builder.append("");
            first = false;
        }
        if (experimental != null) {
            if (!first)
                builder.append(", ");
            builder.append("\"experimental\": ");
            Json.serialize(builder, experimental);
            builder.append("");
        }
        builder.append("}");
        return builder.toString();
    }
}
