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
import org.xowl.infra.utils.json.JsonParser;

/**
 * Registration options for the signature help request on a document
 *
 * @author Laurent Wouters
 */
public class SignatureHelpRegistrationOptions extends TextDocumentRegistrationOptions {
    /**
     * The characters that trigger signature help automatically
     */
    private final String[] triggerCharacters;

    /**
     * Gets the characters that trigger signature help automatically
     *
     * @return The characters that trigger signature help automatically
     */
    public String[] getTriggerCharacters() {
        return triggerCharacters;
    }

    /**
     * Initializes this structure
     *
     * @param documentSelector  A document selector to identify the scope of the registration
     * @param triggerCharacters The characters that trigger signature help automatically
     */
    public SignatureHelpRegistrationOptions(DocumentSelector documentSelector, String[] triggerCharacters) {
        super(documentSelector);
        this.triggerCharacters = triggerCharacters;
    }

    /**
     * Initializes this structure
     *
     * @param definition The serialized definition
     */
    public SignatureHelpRegistrationOptions(ASTNode definition) {
        super(definition);
        String[] triggerCharacters = null;
        for (ASTNode child : definition.getChildren()) {
            ASTNode nodeMemberName = child.getChildren().get(0);
            String name = TextUtils.unescape(nodeMemberName.getValue());
            name = name.substring(1, name.length() - 1);
            ASTNode nodeValue = child.getChildren().get(1);
            switch (name) {
                case "triggerCharacters": {
                    if (nodeValue.getSymbol().getID() == JsonParser.ID.array) {
                        triggerCharacters = new String[nodeValue.getChildren().size()];
                        int index = 0;
                        for (ASTNode nodeItem : nodeValue.getChildren()) {
                            String value = TextUtils.unescape(nodeItem.getValue());
                            value = value.substring(1, value.length() - 1);
                            triggerCharacters[index++] = value;
                        }
                    }
                    break;
                }
            }
        }
        this.triggerCharacters = triggerCharacters;
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
            builder.append(", \"triggerCharacters\": [");
            for (int i = 0; i != triggerCharacters.length; i++) {
                if (i != 0)
                    builder.append(", ");
                builder.append("\"");
                builder.append(TextUtils.escapeStringJSON(triggerCharacters[i]));
                builder.append("\"");
            }
            builder.append("]");
        }
        builder.append("}");
        return builder.toString();
    }
}
