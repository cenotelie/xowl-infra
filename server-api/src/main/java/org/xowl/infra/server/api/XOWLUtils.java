/*******************************************************************************
 * Copyright (c) 2016 Laurent Wouters
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
 *
 * Contributors:
 *     Laurent Wouters - lwouters@xowl.org
 ******************************************************************************/

package org.xowl.infra.server.api;

import org.xowl.hime.redist.ASTNode;
import org.xowl.infra.store.IOUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility APIs for JSON object de-serialization
 *
 * @author Laurent Wouters
 */
public class XOWLUtils {
    /**
     * Gets an object representing the specified JSON object
     *
     * @param node    The root AST for the object
     * @param factory The factory to use
     * @return The JSON object
     */
    public static Object getJSONObject(ASTNode node, XOWLFactory factory) {
        // is this an array ?
        if ("array".equals(node.getSymbol().getName())) {
            List<Object> value = new ArrayList<>();
            for (ASTNode child : node.getChildren()) {
                value.add(getJSONObject(child, factory));
            }
            return value;
        }
        // is this a simple value ?
        String value = node.getValue();
        if (value != null) {
            if (value.startsWith("\"")) {
                value = IOUtils.unescape(value);
                return value.substring(1, value.length() - 1);
            }
            return value;
        }
        // this is an object, does it have a type
        ASTNode nodeType = null;
        for (ASTNode memberNode : node.getChildren()) {
            String memberName = IOUtils.unescape(memberNode.getChildren().get(0).getValue());
            memberName = memberName.substring(1, memberName.length() - 1);
            ASTNode memberValue = memberNode.getChildren().get(1);
            switch (memberName) {
                case "type":
                    nodeType = memberValue;
                    break;
            }
        }
        if (nodeType != null) {
            // we have a type
            String type = IOUtils.unescape(nodeType.getValue());
            type = type.substring(1, type.length() - 1);
            Object result = factory.newObject(type, node);
            if (result != null)
                return result;
        }
        // fallback to mapping the properties
        Map<String, Object> properties = new HashMap<>();
        for (ASTNode memberNode : node.getChildren()) {
            String memberName = IOUtils.unescape(memberNode.getChildren().get(0).getValue());
            memberName = memberName.substring(1, memberName.length() - 1);
            ASTNode memberValue = memberNode.getChildren().get(1);
            properties.put(memberName, getJSONObject(memberValue, factory));
        }
        return properties;
    }
}
