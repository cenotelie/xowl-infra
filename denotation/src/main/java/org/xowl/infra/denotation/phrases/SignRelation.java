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

package org.xowl.infra.denotation.phrases;

import fr.cenotelie.hime.redist.ASTNode;
import org.xowl.infra.utils.Identifiable;
import org.xowl.infra.utils.Serializable;
import org.xowl.infra.utils.TextUtils;

/**
 * Represents a significant relation of a sign with another sign
 *
 * @author Laurent Wouters
 */
public class SignRelation implements Identifiable, Serializable {
    /**
     * The standard contains relation
     */
    public static final SignRelation RELATION_CONTAINS = new SignRelation("http://xowl.org/infra/denotation/relation/contains", "contains");
    /**
     * The standard contained by relation
     */
    public static final SignRelation RELATION_CONTAINED_BY = new SignRelation("http://xowl.org/infra/denotation/relation/containedBy", "containedBy");
    /**
     * The standard overlaps relation
     */
    public static final SignRelation RELATION_OVERLAPS = new SignRelation("http://xowl.org/infra/denotation/relation/overlaps", "overlaps");
    /**
     * The standard links relation
     */
    public static final SignRelation RELATION_LINKS = new SignRelation("http://xowl.org/infra/denotation/relation/links", "links");
    /**
     * The standard linked by relation
     */
    public static final SignRelation RELATION_LINKED_BY = new SignRelation("http://xowl.org/infra/denotation/relation/linkedBy", "linkedBy");


    /**
     * The identifier of this relation
     */
    private final String uri;
    /**
     * The human-readable name of this relation
     */
    private final String name;

    /**
     * Initializes this relation
     *
     * @param uri  The uri of this relation
     * @param name The human-readable name of this relation
     */
    public SignRelation(String uri, String name) {
        this.uri = uri;
        this.name = name;
    }

    /**
     * Initializes this relation
     *
     * @param definition The serialized definition
     */
    public SignRelation(ASTNode definition) {
        String identifier = null;
        String name = null;
        for (ASTNode child : definition.getChildren()) {
            ASTNode nodeHeader = child.getChildren().get(0);
            String memberName = TextUtils.unescape(nodeHeader.getValue());
            memberName = memberName.substring(1, memberName.length() - 1);
            switch (memberName) {
                case "identifier": {
                    ASTNode nodeValue = child.getChildren().get(1);
                    identifier = TextUtils.unescape(nodeValue.getValue());
                    identifier = identifier.substring(1, identifier.length() - 1);
                    break;
                }
                case "name": {
                    ASTNode nodeValue = child.getChildren().get(1);
                    name = TextUtils.unescape(nodeValue.getValue());
                    name = name.substring(1, name.length() - 1);
                    break;
                }
            }
        }
        this.uri = identifier;
        this.name = name;
    }

    @Override
    public String getIdentifier() {
        return uri;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String serializedString() {
        return uri;
    }

    @Override
    public String serializedJSON() {
        return "{\"type\": \"" +
                SignRelation.class.getCanonicalName() +
                "\", \"identifier\": \"" +
                TextUtils.escapeStringJSON(uri) +
                "\", \"name\": \"" +
                TextUtils.escapeStringJSON(name) +
                "\"}";
    }

    @Override
    public String toString() {
        return uri;
    }
}
