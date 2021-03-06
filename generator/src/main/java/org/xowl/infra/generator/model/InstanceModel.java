/*******************************************************************************
 * Copyright (c) 2016 Association Cénotélie (cenotelie.fr)
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

package org.xowl.infra.generator.model;

import org.xowl.infra.lang.runtime.NamedIndividual;

import java.io.IOException;
import java.io.Writer;

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
     * Initializes this instance
     *
     * @param type       The typing class
     * @param individual The represented OWL individual
     */
    public InstanceModel(ClassModel type, NamedIndividual individual) {
        this.type = type;
        this.individual = individual;
        String iri = this.individual.getInterpretationOf().getHasIRI().getHasValue();
        String[] parts = iri.split("#");
        this.name = parts[parts.length - 1];
    }

    /**
     * Generates and writes the code for this instance as a standalone distribution
     *
     * @param writer The writer to write to
     * @throws java.io.IOException When an IO error occurs
     */
    public void writeStandalone(Writer writer) throws IOException {
    }
}
