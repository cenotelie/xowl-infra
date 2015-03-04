/**********************************************************************
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
 **********************************************************************/

package org.xowl.generator.model;

import org.xowl.lang.runtime.NamedIndividual;

/**
 * Represents a static instance in a model to generate
 *
 * @author Laurent Wouters
 */
public class InstanceModel {
    /**
     * The typing class
     */
    private ClassModel type;
    /**
     * The OWL individual
     */
    private NamedIndividual individual;
    /**
     * The instance's name
     */
    private String name;
    /**
     * The instance's IRI
     */
    private String iri;

    /**
     * Gets the type of this instance
     *
     * @return The type of this instance
     */
    public ClassModel getType() {
        return type;
    }

    /**
     * Gets the OWL individual for this instance
     *
     * @return The OWL individual
     */
    public NamedIndividual getOWLIndividual() {
        return individual;
    }

    /**
     * Gets the instance's name
     *
     * @return The instance's name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the instance's IRI
     *
     * @return The instance's IRI
     */
    public String getIRI() {
        return iri;
    }

    /**
     * Initializes this instance
     *
     * @param type       The typing class
     * @param individual The represented OWL individual
     */
    public InstanceModel(ClassModel type, NamedIndividual individual) {
        this.type = type;
        this.individual = individual;
        this.iri = this.individual.getInterpretationOf().getHasIRI().getHasValue();
        String[] parts = iri.split("#");
        this.name = parts[parts.length - 1];
    }
}
