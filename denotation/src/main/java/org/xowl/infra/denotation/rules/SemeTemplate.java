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

package org.xowl.infra.denotation.rules;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a template of seme (ontological entity) as a consequent to a denotation rule
 *
 * @author Laurent Wouters
 */
public class SemeTemplate extends DenotationRuleConsequent {
    /**
     * The template's identifier
     */
    private final String identifier;
    /**
     * The IRI of the seme's type
     */
    private final String typeIri;
    /**
     * The properties for the seme
     */
    private List<SemeTemplateProperty> properties;

    /**
     * Initializes this consequent
     *
     * @param identifier The template's identifier
     * @param typeIri    The IRI of the seme's type
     */
    public SemeTemplate(String identifier, String typeIri) {
        this.identifier = identifier;
        this.typeIri = typeIri;
    }

    /**
     * Gets the template's identifier
     *
     * @return The template's identifier
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * Gets the IRI of the seme's type
     *
     * @return The IRI of the seme's type
     */
    public String getTypeIri() {
        return typeIri;
    }

    /**
     * Gets the properties for this seme
     *
     * @return The properties
     */
    public List<SemeTemplateProperty> getProperties() {
        if (properties == null)
            return Collections.emptyList();
        return Collections.unmodifiableList(properties);
    }

    /**
     * Adds a property to this seme
     *
     * @param property The property to add
     */
    public void addProperty(SemeTemplateProperty property) {
        if (properties == null)
            properties = new ArrayList<>();
        properties.add(property);
    }

    /**
     * Remove a property from this seme
     *
     * @param property The property to remove
     */
    public void removeProperty(SemeTemplateProperty property) {
        if (properties == null)
            return;
        properties.remove(property);
    }
}
