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

import fr.cenotelie.commons.utils.Serializable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents the vocabulary that can be used for a phrase,
 * i.e. the set of possible properties and relations for the signs in the phrase.
 *
 * @author Laurent Wouters
 */
public class PhraseVocabulary implements Serializable {
    /**
     * The global register of vocabulary elements
     */
    public static final PhraseVocabulary REGISTER = new PhraseVocabulary();

    static {
        REGISTER.addProperty(SignPropertyBrightness.INSTANCE);
        REGISTER.addProperty(SignPropertyColor.INSTANCE);
        REGISTER.addProperty(SignPropertyLength.INSTANCE);
        REGISTER.addProperty(SignPropertyName.INSTANCE);
        REGISTER.addProperty(SignPropertyOrientation.INSTANCE);
        REGISTER.addProperty(SignPropertyPosition2D.INSTANCE);
        REGISTER.addProperty(SignPropertyPositionText.INSTANCE);
        REGISTER.addProperty(SignPropertyShape.INSTANCE);
        REGISTER.addProperty(SignPropertySize.INSTANCE);
        REGISTER.addProperty(SignPropertySize2D.INSTANCE);
        REGISTER.addProperty(SignPropertyTexture.INSTANCE);
        REGISTER.addProperty(SignPropertyZone2D.INSTANCE);
        REGISTER.addRelation(SignRelation.RELATION_CONTAINED_BY);
        REGISTER.addRelation(SignRelation.RELATION_CONTAINS);
        REGISTER.addRelation(SignRelation.RELATION_LINKED_BY);
        REGISTER.addRelation(SignRelation.RELATION_LINKS);
        REGISTER.addRelation(SignRelation.RELATION_OVERLAPS);
    }

    /**
     * The sign properties
     */
    private final Map<String, SignProperty> properties;
    /**
     * The sign relations
     */
    private final Map<String, SignRelation> relations;

    /**
     * Initializes an empty vocabulary
     */
    public PhraseVocabulary() {
        this.properties = new HashMap<>();
        this.relations = new HashMap<>();
    }

    /**
     * Initializes this vocabulary as a copy of the specified one
     *
     * @param copied The vocabulary to copy
     */
    public PhraseVocabulary(PhraseVocabulary copied) {
        this.properties = new HashMap<>(copied.properties);
        this.relations = new HashMap<>(copied.relations);
    }

    /**
     * Gets the properties in this vocabulary
     *
     * @return The properties in this vocabulary
     */
    public Collection<SignProperty> getProperties() {
        return properties.values();
    }

    /**
     * Gets the property for the specified identifier
     *
     * @param identifier The identifier of the property
     * @return The property, or null if it is not in this vocabulary
     */
    public SignProperty getProperty(String identifier) {
        return properties.get(identifier);
    }

    /**
     * Adds a property to this vocabulary
     *
     * @param property The property to add
     */
    public void addProperty(SignProperty property) {
        properties.put(property.getIdentifier(), property);
    }

    /**
     * Gets the relations in this vocabulary
     *
     * @return The relations in this vocabulary
     */
    public Collection<SignRelation> getRelations() {
        return relations.values();
    }

    /**
     * Gets the relation for the specified identifier
     *
     * @param identifier The identifier of a relation
     * @return The relation, or null if it is not in this vocabulary
     */
    public SignRelation getRelation(String identifier) {
        return relations.get(identifier);
    }

    /**
     * Adds a relation to this vocabulary
     *
     * @param relation The relation to add
     */
    public void addRelation(SignRelation relation) {
        relations.put(relation.getIdentifier(), relation);
    }

    @Override
    public String serializedString() {
        return serializedJSON();
    }

    @Override
    public String serializedJSON() {
        StringBuilder builder = new StringBuilder();
        builder.append("{\"type\": \"");
        builder.append(PhraseVocabulary.class.getCanonicalName());
        builder.append("\", \"properties\": [");
        boolean first = true;
        for (SignProperty property : properties.values()) {
            if (!first)
                builder.append(", ");
            first = false;
            builder.append(property.serializedJSON());
        }
        builder.append("], \"relations\": [");
        first = true;
        for (SignRelation relation : relations.values()) {
            if (!first)
                builder.append(", ");
            first = false;
            builder.append(relation.serializedJSON());
        }
        builder.append("]}");
        return builder.toString();
    }
}
