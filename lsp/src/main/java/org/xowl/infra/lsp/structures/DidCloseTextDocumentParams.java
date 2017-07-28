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
 * Parameters for the notification of a document that was closed on the client
 *
 * @author Laurent Wouters
 */
public class DidCloseTextDocumentParams implements Serializable {
    /**
     * The document that was closed
     */
    private final TextDocumentIdentifier textDocument;

    /**
     * Gets the document that was closed
     *
     * @return The document that was closed
     */
    public TextDocumentIdentifier getTextDocument() {
        return textDocument;
    }

    /**
     * Initializes this structure
     *
     * @param textDocument The document that was saved
     */
    public DidCloseTextDocumentParams(TextDocumentIdentifier textDocument) {
        this.textDocument = textDocument;
    }

    /**
     * Initializes this structure
     *
     * @param definition The serialized definition
     */
    public DidCloseTextDocumentParams(ASTNode definition) {
        TextDocumentIdentifier textDocument = null;
        for (ASTNode child : definition.getChildren()) {
            ASTNode nodeMemberName = child.getChildren().get(0);
            String name = TextUtils.unescape(nodeMemberName.getValue());
            name = name.substring(1, name.length() - 1);
            ASTNode nodeValue = child.getChildren().get(1);
            switch (name) {
                case "textDocument": {
                    textDocument = new TextDocumentIdentifier(nodeValue);
                    break;
                }
            }
        }
        this.textDocument = textDocument;
    }

    @Override
    public String serializedString() {
        return serializedJSON();
    }

    @Override
    public String serializedJSON() {
        return "{\"textDocument\": " +
                textDocument.serializedJSON() +
                "}";
    }
}
