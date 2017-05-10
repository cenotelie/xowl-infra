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

/**
 * A property template for a seme when the value is a literal
 *
 * @author Laurent Wouters
 */
public class SemeTemplatePropertyLiteral extends SemeTemplateProperty {
    /**
     * The value
     */
    private final Object value;

    /**
     * Initializes this property
     *
     * @param propertyIri The property's IRI
     * @param value       The value
     */
    public SemeTemplatePropertyLiteral(String propertyIri, Object value) {
        super(propertyIri);
        this.value = value;
    }

    /**
     * Gets the value
     *
     * @return The value
     */
    public Object getValue() {
        return value;
    }
}
