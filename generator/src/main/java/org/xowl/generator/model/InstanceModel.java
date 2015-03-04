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

import org.xowl.lang.runtime.*;

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

    /**
     * Generates and writes the code for this instance as a standalone distribution
     *
     * @param writer The writer to write to
     * @throws java.io.IOException When an IO error occurs
     */
    public void writeStandalone(Writer writer) throws IOException {
        writer.write("    // <editor-fold defaultstate=\"collapsed\" desc=\"Static instance " + getName() + "\">\n");
        writer.write("    private static " + getType().getJavaName() + " " + getName() + ";\n");
        writer.write("    public static " + getType().getJavaName() + " get_" + getName() + "() {\n");
        writer.write("        if (" + getName() + " != null) return " + getName() + ";\n");
        writer.write("        " + getName() + " = new " + getType().getJavaName() + "();\n");

        for (PropertyAssertion assertion : getOWLIndividual().getAllAsserts()) {
            if (assertion instanceof ObjectPropertyAssertion) {
                ObjectPropertyAssertion objectPropertyAssertion = (ObjectPropertyAssertion) assertion;
                PropertyImplementation propertyImplementation = getType().getPropertyImplementation(objectPropertyAssertion.getPropertyAs(null));
                String property = propertyImplementation.getProperty().getName();
                property = String.valueOf(property.charAt(0)).toUpperCase() + property.substring(1);
                InstanceModel value = getType().getPackage().getModel().getModelFor((NamedIndividual) objectPropertyAssertion.getValueIndividual());
                if (value.getType().getJavaName().equals("java.lang.Class")) {
                    String className = getType().getPackage().getModel().getBasePackage() + ".";
                    className += getType().getPackage().getModel().getModelFor(value.getOWLIndividual().getInterpretationOf().getContainedBy()).getName() + ".";
                    className += value.getName();
                    writer.write("        " + getName() + ".data" + property + ".user_add(" + className + ".class);\n");
                } else
                    writer.write("        " + getName() + ".data" + property + ".user_add(" + value.getType().getJavaName() + ".get_" + value.getName() + "());\n");

            } else {
                DataPropertyAssertion dataPropertyAssertion = (DataPropertyAssertion) assertion;
                PropertyImplementation propertyImplementation = getType().getPropertyImplementation(dataPropertyAssertion.getPropertyAs(null));
                String property = propertyImplementation.getProperty().getName();
                property = String.valueOf(property.charAt(0)).toUpperCase() + property.substring(1);
                Literal value = dataPropertyAssertion.getValueLiteral();
                DatatypeModel datatype = propertyImplementation.getProperty().getModelFor(value.getMemberOf());
                String data = datatype.getToValue("\"" + value.getLexicalValue() + "\"");
                writer.write("        " + getName() + ".data" + property + ".user_add(" + data + ");\n");
            }
        }

        writer.write("        return " + getName() + ";\n");
        writer.write("    }\n");
        writer.write("    // </editor-fold>\n");
    }
}
