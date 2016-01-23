/*******************************************************************************
 * Copyright (c) 2015 Laurent Wouters
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

package org.xowl.server.api.base;

import org.xowl.hime.redist.ASTNode;
import org.xowl.server.api.XOWLRule;
import org.xowl.store.IOUtils;

/**
 * The base implementation of a rule
 *
 * @author Laurent Wouters
 */
public class BaseRule implements XOWLRule {
    /**
     * The rule's name (IRI)
     */
    private final String name;
    /**
     * The rule's definition
     */
    private final String definition;
    /**
     * Whether the rule is active
     */
    private final boolean isActive;

    /**
     * Initializes this rule
     *
     * @param name       The rule's name (IRI)
     * @param definition The rule's definition
     * @param isActive   Whether the rule is active
     */
    public BaseRule(String name, String definition, boolean isActive) {
        this.name = name;
        this.definition = definition;
        this.isActive = isActive;
    }

    /**
     * Initializes this rule
     *
     * @param root The rule's definition
     */
    public BaseRule(ASTNode root) {
        String vName = null;
        String vDef = null;
        boolean vActive = false;
        for (ASTNode child : root.getChildren()) {
            ASTNode nodeMemberName = child.getChildren().get(0);
            String name = IOUtils.unescape(nodeMemberName.getValue());
            name = name.substring(1, name.length() - 1);
            switch (name) {
                case "name": {
                    ASTNode nodeValue = child.getChildren().get(1);
                    vName = IOUtils.unescape(nodeValue.getValue());
                    vName = vName.substring(1, vName.length() - 1);
                    break;
                }
                case "definition": {
                    ASTNode nodeValue = child.getChildren().get(1);
                    vDef = IOUtils.unescape(nodeValue.getValue());
                    vDef = vDef.substring(1, vDef.length() - 1);
                    break;
                }
                case "isActive": {
                    ASTNode nodeValue = child.getChildren().get(1);
                    String value = IOUtils.unescape(nodeValue.getValue());
                    vActive = value.equalsIgnoreCase("true");
                    break;
                }
            }
        }
        this.name = vName;
        this.definition = vDef;
        this.isActive = vActive;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isActive() {
        return isActive;
    }

    @Override
    public String getDefinition() {
        return definition;
    }

    @Override
    public String serializedString() {
        return null;
    }

    @Override
    public String serializedJSON() {
        return "{\"type\": \"" +
                IOUtils.escapeStringJSON(BaseRule.class.getCanonicalName()) +
                "\", \"name\": \"" +
                IOUtils.escapeStringJSON(name) +
                "\", \"definition\": \"" +
                IOUtils.escapeStringJSON(definition) +
                "\", \"isActive\": " +
                Boolean.toString(isActive) +
                "}";
    }
}
