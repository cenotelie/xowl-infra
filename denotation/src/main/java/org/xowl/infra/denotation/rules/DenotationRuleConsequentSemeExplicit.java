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
 * Represents an explicit seme (ontological entity) as a consequent of a denotation rule
 *
 * @author Laurent Wouters
 */
public class DenotationRuleConsequentSemeExplicit extends DenotationRuleConsequent {
    /**
     * The seme's IRI
     */
    private final String iri;

    /**
     * Initializes this consequent
     *
     * @param iri The seme's IRI
     */
    public DenotationRuleConsequentSemeExplicit(String iri) {
        this.iri = iri;
    }

    /**
     * Gets the seme's IRI
     *
     * @return The seme's IRI
     */
    public String getSemeIri() {
        return iri;
    }
}
