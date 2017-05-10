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
 * A property template for a seme when the value is an IRI
 *
 * @author Laurent Wouters
 */
public class SemeTemplatePropertyIRI extends SemeTemplateProperty {
    /**
     * The IRI for the value
     */
    private final String valueIri;

    /**
     * Initializes this property
     *
     * @param propertyIri The property's IRI
     * @param valueIri    The IRI for the value
     */
    public SemeTemplatePropertyIRI(String propertyIri, String valueIri) {
        super(propertyIri);
        this.valueIri = valueIri;
    }

    /**
     * Gets the IRI for the value
     *
     * @return The IRI for the value
     */
    public String getValueIri() {
        return valueIri;
    }
}
