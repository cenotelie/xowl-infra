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
 * A range in a text document expressed as (zero-based) start and end positions. A range is comparable to a selection in an editor. Therefore the end position is exclusive.
 *
 * @author Laurent Wouters
 */
public class Range implements Serializable {
    /**
     * The range's start position.
     */
    private final Position start;

    /**
     * The range's end position.
     */
    private final Position end;

    /**
     * Gets the range's start position.
     *
     * @return The range's start position.
     */
    public Position getStart() {
        return start;
    }

    /**
     * Gets the range's end position.
     *
     * @return The range's end position.
     */
    public Position getEnd() {
        return end;
    }

    /**
     * Initializes this structure
     *
     * @param start The range's start position.
     * @param end   The range's end position.
     */
    public Range(Position start, Position end) {
        this.start = start;
        this.end = end;
    }

    /**
     * Initializes this structure
     *
     * @param definition The serialized definition
     */
    public Range(ASTNode definition) {
        Position start = null;
        Position end = null;
        for (ASTNode child : definition.getChildren()) {
            ASTNode nodeMemberName = child.getChildren().get(0);
            String name = TextUtils.unescape(nodeMemberName.getValue());
            name = name.substring(1, name.length() - 1);
            ASTNode nodeValue = child.getChildren().get(1);
            switch (name) {
                case "start": {
                    start = new Position(nodeValue);
                    break;
                }
                case "end": {
                    end = new Position(nodeValue);
                    break;
                }
            }
        }
        this.start = start != null ? start : new Position(0, 0);
        this.end = end != null ? end : new Position(0, 0);
    }

    @Override
    public String serializedString() {
        return serializedJSON();
    }

    @Override
    public String serializedJSON() {
        return "{\"start\": " +
                start.serializedJSON() +
                ",\"end\": " +
                end.serializedJSON() +
                "}";
    }
}
