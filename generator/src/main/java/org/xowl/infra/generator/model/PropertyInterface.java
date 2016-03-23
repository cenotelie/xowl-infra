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

package org.xowl.infra.generator.model;

import java.io.IOException;
import java.io.Writer;

/**
 * Represents the interface of a property
 *
 * @author Laurent Wouters
 */
public class PropertyInterface extends PropertyData {
    /**
     * Initializes this property data
     *
     * @param classe   The parent class model
     * @param property The associated property
     */
    public PropertyInterface(ClassModel classe, PropertyModel property) {
        super(classe, property);
    }

    /**
     * Sets that this property is in a type restriction chain
     */
    public void setInTypeRestrictionChain() {
        inTypeRestrictionChain = true;
    }

    /**
     * Gets whether this property is in a type restriction chain
     *
     * @return Whether this property is in a type restriction chain
     */
    public boolean isInTypeRestrictionChain() {
        return inTypeRestrictionChain;
    }

    /**
     * Gets whether this property interface is the same as the specified one
     *
     * @param propertyInterface Another property interface
     * @return true if the two property interfaces matches
     */
    public boolean sameAs(PropertyInterface propertyInterface) {
        if (this.property != propertyInterface.property)
            return false;
        if (this.rangeClass != propertyInterface.rangeClass)
            return false;
        if (this.rangeDatatype != propertyInterface.rangeDatatype)
            return false;
        return (this.isVector() == propertyInterface.isVector());
    }

    /**
     * Generates and writes the code for this property interface as a standalone distribution
     *
     * @param writer The writer to write to
     * @throws java.io.IOException When an IO error occurs
     */
    public void writeStandalone(Writer writer) throws IOException {
        writer.write("    // <editor-fold defaultstate=\"collapsed\" desc=\"Property " + getProperty().getName() + "\">\n");
        String type;
        if (getRangeClass() != null)
            type = getRangeClass().getJavaName();
        else
            type = getRangeDatatype().getJavaType();
        if (getProperty().getDomain() == getParentClass())
            writeStandaloneInterface(writer, type);
        if (isVector())
            writeStandaloneAsVector(writer, type);
        else
            writeStandaloneAsScalar(writer, type);
        writer.write("    // </editor-fold>\n");
    }

    /**
     * Generates and writes the code for this property getter and setter
     *
     * @param writer The writer to write to
     * @param type   The property's type as a string
     * @throws java.io.IOException When an IO error occurs
     */
    private void writeStandaloneAsVector(Writer writer, String type) throws IOException {
        String property = getProperty().getName();
        property = String.valueOf(property.charAt(0)).toUpperCase() + property.substring(1);
        writer.append("    boolean add" + property + "(" + type + " elem);\n");
        writer.append("    boolean remove" + property + "(" + type + " elem);\n");
        if (!getProperty().isObjectProperty() || !isInTypeRestrictionChain())
            writer.append("    java.util.Collection<" + type + "> getAll" + property + "();\n");
        else
            writer.append("    java.util.Collection<" + type + "> getAll" + property + "As(" + type + " type);\n");
    }

    /**
     * Generates and writes the code for this property getter and setter
     *
     * @param writer The writer to write to
     * @param type   The property's type as a string
     * @throws java.io.IOException When an IO error occurs
     */
    private void writeStandaloneAsScalar(Writer writer, String type) throws IOException {
        String property = getProperty().getName();
        property = String.valueOf(property.charAt(0)).toUpperCase() + property.substring(1);
        writer.append("    boolean set" + property + "(" + type + " elem);\n");
        if (!getProperty().isObjectProperty() || !isInTypeRestrictionChain())
            writer.append("    " + type + " get" + property + "();\n");
        else
            writer.append("    " + type + " get" + property + "As(" + type + " type);\n");
    }

    /**
     * Generates and writes the code for this property reification interface
     *
     * @param writer The writer to write to
     * @param type   The property's type as a string
     * @throws java.io.IOException When an IO error occurs
     */
    private void writeStandaloneInterface(Writer writer, String type) throws IOException {
        writer.append("    public static interface " + getProperty().getName() + " {\n");
        writer.append("        boolean check_contains(" + type + " elem);\n");

        writer.append("        boolean user_check_add(" + type + " elem);\n");
        writer.append("        boolean user_check_remove(" + type + " elem);\n");
        writer.append("        boolean user_check_replace(" + type + " oldElem, " + type + "  newElem);\n");
        writer.append("        void user_add(" + type + " elem);\n");
        writer.append("        void user_remove(" + type + " elem);\n");

        writer.append("        boolean inverse_check_add(" + type + " elem);\n");
        writer.append("        boolean inverse_check_remove(" + type + " elem);\n");
        writer.append("        boolean inverse_check_replace(" + type + " oldElem, " + type + "  newElem);\n");
        writer.append("        void inverse_add(" + type + " elem);\n");
        writer.append("        void inverse_remove(" + type + " elem);\n");
        writer.append("    }\n");
        writer.append("    " + getProperty().getName() + " __getImplOf" + getProperty().getName() + "();\n");
    }
}
