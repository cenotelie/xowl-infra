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
 * A parameter literal used in requests to pass a text document and a position inside that document.
 *
 * @author Laurent Wouters
 */
public class TextDocumentPositionParams implements Serializable {
    /**
     * The text document.
     */
    private final TextDocumentIdentifier textDocument;
    /**
     * The position inside the text document.
     */
    private final Position position;

    /**
     * Gets the text document
     *
     * @return The text document
     */
    public TextDocumentIdentifier getTextDocument() {
        return textDocument;
    }

    /**
     * Gets the position inside the text document.
     *
     * @return The position inside the text document.
     */
    public Position getPosition() {
        return position;
    }

    /**
     * Initializes this structure
     *
     * @param textDocument The text document
     * @param position     The position inside the text document.
     */
    public TextDocumentPositionParams(TextDocumentIdentifier textDocument, Position position) {
        this.textDocument = textDocument;
        this.position = position;
    }

    /**
     * Initializes this structure
     *
     * @param definition The serialized definition
     */
    public TextDocumentPositionParams(ASTNode definition) {
        TextDocumentIdentifier textDocument = null;
        Position position = null;
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
                case "position": {
                    position = new Position(nodeValue);
                    break;
                }
            }
        }
        this.textDocument = textDocument != null ? textDocument : new TextDocumentIdentifier("");
        this.position = position != null ? position : new Position(0, 0);
    }

    @Override
    public String serializedString() {
        return serializedJSON();
    }

    @Override
    public String serializedJSON() {
        return "{\"textDocument\": " +
                textDocument.serializedJSON() +
                ", \"position\": " +
                position.serializedJSON() +
                "}";
    }
}
