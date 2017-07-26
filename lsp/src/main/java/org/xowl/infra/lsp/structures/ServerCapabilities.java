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
import org.xowl.infra.utils.json.JsonDeserializer;

/**
 * Capabilities offered by the server
 * TODO: add constants
 *
 * @author Laurent Wouters
 */
public class ServerCapabilities extends Capabilities {
    /**
     * Experimental server capabilities
     */
    private final Object experimental;

    /**
     * Initializes this structure
     *
     * @param experimental Experimental server capabilities
     */
    public ServerCapabilities(Object experimental) {
        this.experimental = experimental;
    }

    /**
     * Initializes this structure
     *
     * @param definition   The serialized definition
     * @param deserializer The current de-serializer
     */
    public ServerCapabilities(ASTNode definition, JsonDeserializer deserializer) {
        super(definition, deserializer);
        Object experimental = null;
        for (ASTNode child : definition.getChildren()) {
            ASTNode nodeMemberName = child.getChildren().get(0);
            String name = TextUtils.unescape(nodeMemberName.getValue());
            name = name.substring(1, name.length() - 1);
            ASTNode nodeValue = child.getChildren().get(1);
            switch (name) {
                case "experimental": {
                    experimental = deserializer.deserialize(nodeValue, null);
                    break;
                }
            }
        }
        this.experimental = experimental;
    }
}
