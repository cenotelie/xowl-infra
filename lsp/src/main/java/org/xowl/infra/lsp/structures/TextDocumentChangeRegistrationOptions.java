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

/**
 * Registration options for the text changed events
 *
 * @author Laurent Wouters
 */
public class TextDocumentChangeRegistrationOptions extends TextDocumentRegistrationOptions {
    /**
     * How documents are synced to the server.
     * See TextDocumentSyncKind.Full and TextDocumentSyncKindIncremental
     */
    private final int syncKind;

    /**
     * Initializes this structure
     *
     * @param documentSelector A document selector to identify the scope of the registration
     * @param syncKind         How documents are synced to the server
     */
    public TextDocumentChangeRegistrationOptions(DocumentSelector documentSelector, int syncKind) {
        super(documentSelector);
        this.syncKind = syncKind;
    }

    /**
     * Initializes this structure
     *
     * @param definition The serialized definition
     */
    public TextDocumentChangeRegistrationOptions(ASTNode definition) {
        super(definition);
        int syncKind = TextDocumentSyncKind.FULL;
        for (ASTNode child : definition.getChildren()) {
            ASTNode nodeMemberName = child.getChildren().get(0);
            String name = TextUtils.unescape(nodeMemberName.getValue());
            name = name.substring(1, name.length() - 1);
            ASTNode nodeValue = child.getChildren().get(1);
            switch (name) {
                case "syncKind": {
                    syncKind = Integer.parseInt(nodeValue.getValue());
                    break;
                }
            }
        }
        this.syncKind = syncKind;
    }

    @Override
    public String serializedJSON() {
        StringBuilder builder = new StringBuilder();
        builder.append("{\"documentSelector\": ");
        if (documentSelector == null)
            builder.append("null");
        else
            builder.append(documentSelector.serializedJSON());
        builder.append(", \"syncKind\": ");
        builder.append(Integer.toString(syncKind));
        builder.append("}");
        return builder.toString();
    }
}
