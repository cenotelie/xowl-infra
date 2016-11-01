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

import org.xowl.infra.utils.Files;

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
     * Generates and writes the code for this property interface
     *
     * @param writer The writer to write to
     * @throws IOException When an IO error occurs
     */
    public void writeInterface(Writer writer) throws IOException {
        if (isVector())
            writeInterfaceVector(writer);
        else
            writeInterfaceScalar(writer);
    }

    /**
     * Writes the javadoc for the add method
     *
     * @param writer   The writer to write to
     * @param property The property's type as a string
     * @throws IOException When an IO error occurs
     */
    public void writeJavadocAdd(Writer writer, String property) throws IOException {
        String iri = this.property.getOWLProperty().getInterpretationOf().getHasIRI().getHasValue();
        writer.append("    /**").append(Files.LINE_SEPARATOR);
        writer.append("     * Adds an element to the property ").append(property).append(Files.LINE_SEPARATOR);
        writer.append("     * Original OWL property is ").append(iri).append(Files.LINE_SEPARATOR);
        writer.append("     *").append(Files.LINE_SEPARATOR);
        writer.append("     * @param elem The element to add").append(Files.LINE_SEPARATOR);
        writer.append("     * @return Whether the operation resulted in a new element (false if the element was already there)").append(Files.LINE_SEPARATOR);
        writer.append("     */").append(Files.LINE_SEPARATOR);
    }

    /**
     * Writes the javadoc for the remove method
     *
     * @param writer   The writer to write to
     * @param property The property's type as a string
     * @throws IOException When an IO error occurs
     */
    public void writeJavadocRemove(Writer writer, String property) throws IOException {
        String iri = this.property.getOWLProperty().getInterpretationOf().getHasIRI().getHasValue();
        writer.append("    /**").append(Files.LINE_SEPARATOR);
        writer.append("     * Removes an element from the property ").append(property).append(Files.LINE_SEPARATOR);
        writer.append("     * Original OWL property is ").append(iri).append(Files.LINE_SEPARATOR);
        writer.append("     *").append(Files.LINE_SEPARATOR);
        writer.append("     * @param elem The element to remove").append(Files.LINE_SEPARATOR);
        writer.append("     * @return Whether the operation resulted in the element being removed").append(Files.LINE_SEPARATOR);
        writer.append("     */").append(Files.LINE_SEPARATOR);
    }

    /**
     * Writes the javadoc for the getAll method
     *
     * @param writer   The writer to write to
     * @param property The property's type as a string
     * @throws IOException When an IO error occurs
     */
    public void writeJavadocGetAll(Writer writer, String property) throws IOException {
        String iri = this.property.getOWLProperty().getInterpretationOf().getHasIRI().getHasValue();
        writer.append("    /**").append(Files.LINE_SEPARATOR);
        writer.append("     * Gets all the elements for the property ").append(property).append(Files.LINE_SEPARATOR);
        writer.append("     * Original OWL property is ").append(iri).append(Files.LINE_SEPARATOR);
        writer.append("     *").append(Files.LINE_SEPARATOR);
        writer.append("     * @return The elements for the property ").append(property).append(Files.LINE_SEPARATOR);
        writer.append("     */").append(Files.LINE_SEPARATOR);
    }

    /**
     * Writes the javadoc for the typed getAll method
     *
     * @param writer   The writer to write to
     * @param property The property's type as a string
     * @throws IOException When an IO error occurs
     */
    public void writeJavadocGetAllTyped(Writer writer, String property) throws IOException {
        String iri = this.property.getOWLProperty().getInterpretationOf().getHasIRI().getHasValue();
        writer.append("    /**").append(Files.LINE_SEPARATOR);
        writer.append("     * Gets all the elements for the property ").append(property).append(Files.LINE_SEPARATOR);
        writer.append("     * Original OWL property is ").append(iri).append(Files.LINE_SEPARATOR);
        writer.append("     *").append(Files.LINE_SEPARATOR);
        writer.append("     * @param type An element of the type expected in result (may be null)").append(Files.LINE_SEPARATOR);
        writer.append("     *             This parameter is used to disambiguate among overloads.").append(Files.LINE_SEPARATOR);
        writer.append("     * @return The elements for the property ").append(property).append(Files.LINE_SEPARATOR);
        writer.append("     */").append(Files.LINE_SEPARATOR);
    }

    /**
     * Writes the javadoc for the get method
     *
     * @param writer   The writer to write to
     * @param property The property's type as a string
     * @throws IOException When an IO error occurs
     */
    public void writeJavadocSet(Writer writer, String property) throws IOException {
        String iri = this.property.getOWLProperty().getInterpretationOf().getHasIRI().getHasValue();
        writer.append("    /**").append(Files.LINE_SEPARATOR);
        writer.append("     * Sets the value for the property ").append(property).append(Files.LINE_SEPARATOR);
        writer.append("     * Original OWL property is ").append(iri).append(Files.LINE_SEPARATOR);
        writer.append("     *").append(Files.LINE_SEPARATOR);
        writer.append("     * @param elem The value to set").append(Files.LINE_SEPARATOR);
        writer.append("     */").append(Files.LINE_SEPARATOR);
    }

    /**
     * Writes the javadoc for the get method
     *
     * @param writer   The writer to write to
     * @param property The property's type as a string
     * @throws IOException When an IO error occurs
     */
    public void writeJavadocGet(Writer writer, String property) throws IOException {
        String iri = this.property.getOWLProperty().getInterpretationOf().getHasIRI().getHasValue();
        writer.append("    /**").append(Files.LINE_SEPARATOR);
        writer.append("     * Gets the value for the property ").append(property).append(Files.LINE_SEPARATOR);
        writer.append("     * Original OWL property is ").append(iri).append(Files.LINE_SEPARATOR);
        writer.append("     *").append(Files.LINE_SEPARATOR);
        writer.append("     * @return The value for the property ").append(property).append(Files.LINE_SEPARATOR);
        writer.append("     */").append(Files.LINE_SEPARATOR);
    }

    /**
     * Writes the javadoc for the typed get method
     *
     * @param writer   The writer to write to
     * @param property The property's type as a string
     * @throws IOException When an IO error occurs
     */
    public void writeJavadocGetTyped(Writer writer, String property) throws IOException {
        String iri = this.property.getOWLProperty().getInterpretationOf().getHasIRI().getHasValue();
        writer.append("    /**").append(Files.LINE_SEPARATOR);
        writer.append("     * Gets the value for the property ").append(property).append(Files.LINE_SEPARATOR);
        writer.append("     * Original OWL property is ").append(iri).append(Files.LINE_SEPARATOR);
        writer.append("     *").append(Files.LINE_SEPARATOR);
        writer.append("     * @param type An element of the type expected in result (may be null)").append(Files.LINE_SEPARATOR);
        writer.append("     *             This parameter is used to disambiguate among overloads.").append(Files.LINE_SEPARATOR);
        writer.append("     * @return The value for the property ").append(property).append(Files.LINE_SEPARATOR);
        writer.append("     */").append(Files.LINE_SEPARATOR);
    }

    /**
     * Generates and writes the code for this property getter and setter
     *
     * @param writer The writer to write to
     * @throws IOException When an IO error occurs
     */
    private void writeInterfaceVector(Writer writer) throws IOException {
        String name = getJavaName();

        writeJavadocAdd(writer, name);
        writer.append("    boolean add").append(name).append("(").append(getJavaRangeScalar()).append(" elem);").append(Files.LINE_SEPARATOR);
        writer.append(Files.LINE_SEPARATOR);

        writeJavadocRemove(writer, name);
        writer.append("    boolean remove").append(name).append("(").append(getJavaRangeScalar()).append(" elem);").append(Files.LINE_SEPARATOR);
        writer.append(Files.LINE_SEPARATOR);

        if (!getProperty().isObjectProperty() || !isInTypeRestrictionChain()) {
            writeJavadocGetAll(writer, name);
            writer.append("    Collection<").append(getJavaRangeVector()).append("> getAll").append(name).append("();").append(Files.LINE_SEPARATOR);
            writer.append(Files.LINE_SEPARATOR);
        } else {
            writeJavadocGetAllTyped(writer, name);
            writer.append("    Collection<").append(getJavaRangeVector()).append("> getAll").append(name).append("As(").append(getJavaRangeScalar()).append(" type);").append(Files.LINE_SEPARATOR);
            writer.append(Files.LINE_SEPARATOR);
        }
    }

    /**
     * Generates and writes the code for this property getter and setter
     *
     * @param writer The writer to write to
     * @throws IOException When an IO error occurs
     */
    private void writeInterfaceScalar(Writer writer) throws IOException {
        String name = getJavaName();

        writeJavadocSet(writer, name);
        writer.append("    void set").append(name).append("(").append(getJavaRangeScalar()).append(" elem);").append(Files.LINE_SEPARATOR);
        writer.append(Files.LINE_SEPARATOR);

        if (!getProperty().isObjectProperty() || !isInTypeRestrictionChain()) {
            writeJavadocGet(writer, name);
            writer.append("    ").append(getJavaRangeScalar()).append(" get").append(name).append("();").append(Files.LINE_SEPARATOR);
            writer.append(Files.LINE_SEPARATOR);
        } else {
            writeJavadocGetTyped(writer, name);
            writer.append("    ").append(getJavaRangeScalar()).append(" get").append(name).append("As(").append(getJavaRangeScalar()).append(" type);").append(Files.LINE_SEPARATOR);
            writer.append(Files.LINE_SEPARATOR);
        }
    }
}
