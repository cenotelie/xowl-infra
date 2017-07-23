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
 * Position in a text document expressed as zero-based line and character offset. A position is between two characters like an 'insert' cursor in a editor.
 *
 * @author Laurent Wouters
 */
public class Position implements Serializable {
    /**
     * Line position in a document (zero-based).
     */
    private final int line;
    /**
     * Character offset on a line in a document (zero-based).
     */
    private final int character;

    /**
     * Gets the line position in a document (zero-based).
     *
     * @return Line position in a document (zero-based).
     */
    public int getLine() {
        return line;
    }

    /**
     * Gets the character offset on a line in a document (zero-based).
     *
     * @return Character offset on a line in a document (zero-based).
     */
    public int getCharacter() {
        return character;
    }

    /**
     * Initializes this structure
     *
     * @param line      Line position in a document (zero-based).
     * @param character Character offset on a line in a document (zero-based).
     */
    public Position(int line, int character) {
        this.line = line;
        this.character = character;
    }

    /**
     * Initializes this structure
     *
     * @param definition The serialized definition
     */
    public Position(ASTNode definition) {
        int line = 0;
        int character = 0;
        for (ASTNode child : definition.getChildren()) {
            ASTNode nodeMemberName = child.getChildren().get(0);
            String name = TextUtils.unescape(nodeMemberName.getValue());
            name = name.substring(1, name.length() - 1);
            ASTNode nodeValue = child.getChildren().get(1);
            switch (name) {
                case "line": {
                    line = Integer.parseInt(nodeValue.getValue());
                    break;
                }
                case "character": {
                    character = Integer.parseInt(nodeValue.getValue());
                    break;
                }
            }
        }
        this.line = line;
        this.character = character;
    }

    @Override
    public String serializedString() {
        return serializedJSON();
    }

    @Override
    public String serializedJSON() {
        return "{\"line\": " +
                Integer.toString(line) +
                ",\"character\": " +
                Integer.toString(character) +
                "}";
    }
}
