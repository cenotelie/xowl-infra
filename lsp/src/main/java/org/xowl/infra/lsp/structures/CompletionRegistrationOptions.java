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
import org.xowl.infra.utils.TextUtils;
import org.xowl.infra.utils.json.JsonLexer;

/**
 * Registration options for the completion request on a document
 *
 * @author Laurent Wouters
 */
public class CompletionRegistrationOptions extends TextDocumentRegistrationOptions {
    /**
     * The characters that trigger completion automatically
     */
    private final String triggerCharacters;
    /**
     * The server provides support to resolve additional information for a completion item
     */
    private final boolean resolveProvider;

    /**
     * Gets the characters that trigger completion automatically
     *
     * @return The characters that trigger completion automatically
     */
    public String getTriggerCharacters() {
        return triggerCharacters;
    }

    /**
     * Gets whether the server provides support to resolve additional information for a completion item
     *
     * @return Whether the server provides support to resolve additional information for a completion item
     */
    public boolean getResolveProvider() {
        return resolveProvider;
    }

    /**
     * Initializes this structure
     *
     * @param documentSelector  A document selector to identify the scope of the registration
     * @param triggerCharacters The characters that trigger completion automatically
     * @param resolveProvider   The server provides support to resolve additional information for a completion item
     */
    public CompletionRegistrationOptions(DocumentSelector documentSelector, String triggerCharacters, boolean resolveProvider) {
        super(documentSelector);
        this.triggerCharacters = triggerCharacters;
        this.resolveProvider = resolveProvider;
    }

    /**
     * Initializes this structure
     *
     * @param definition The serialized definition
     */
    public CompletionRegistrationOptions(ASTNode definition) {
        super(definition);

        String triggerCharacters = null;
        boolean resolveProvider = false;
        for (ASTNode child : definition.getChildren()) {
            ASTNode nodeMemberName = child.getChildren().get(0);
            String name = TextUtils.unescape(nodeMemberName.getValue());
            name = name.substring(1, name.length() - 1);
            ASTNode nodeValue = child.getChildren().get(1);
            switch (name) {
                case "triggerCharacters": {
                    triggerCharacters = TextUtils.unescape(nodeValue.getValue());
                    triggerCharacters = triggerCharacters.substring(1, triggerCharacters.length() - 1);
                    break;
                }
                case "resolveProvider": {
                    if (nodeValue.getSymbol().getID() == JsonLexer.ID.LITERAL_TRUE)
                        resolveProvider = true;
                    else if (nodeValue.getSymbol().getID() == JsonLexer.ID.LITERAL_FALSE)
                        resolveProvider = false;
                    else {
                        String value = TextUtils.unescape(nodeValue.getValue());
                        value = value.substring(1, value.length() - 1);
                        resolveProvider = value.equalsIgnoreCase("true");
                    }
                }
            }
        }
        this.triggerCharacters = triggerCharacters;
        this.resolveProvider = resolveProvider;
    }

    @Override
    public String serializedString() {
        return serializedJSON();
    }

    @Override
    public String serializedJSON() {
        StringBuilder builder = new StringBuilder();
        builder.append("{\"documentSelector\": ");
        if (documentSelector == null)
            builder.append("null");
        else
            builder.append(documentSelector.serializedJSON());
        if (triggerCharacters != null) {
            builder.append(", \"triggerCharacters\": \"");
            builder.append(TextUtils.escapeStringJSON(triggerCharacters));
            builder.append("\"");
        }
        builder.append(", \"resolveProvider\": ");
        builder.append(Boolean.toString(resolveProvider));
        builder.append("}");
        return builder.toString();
    }
}
