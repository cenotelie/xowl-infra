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
import org.xowl.infra.utils.logging.Logger;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Represents a set of data regarding the implementation of a property
 *
 * @author Laurent Wouters
 */
public class PropertyImplementation extends PropertyData {
    /**
     * The implemented interfaces
     */
    protected List<PropertyInterface> interfaces;
    /**
     * The superseded interfaces
     */
    protected List<PropertyInterface> masked;
    /**
     * The property domain class
     */
    protected ClassModel propertyDomain;
    /**
     * The property range class
     */
    protected ClassModel propertyRange;
    /**
     * The ancestor implementations
     */
    protected List<PropertyImplementation> implAncestors;
    /**
     * The descendant implementations
     */
    protected List<PropertyImplementation> implDescendants;
    /**
     * The implementation of the reverse property
     */
    protected PropertyImplementation implInverse;

    /**
     * Gets the implemented interfaces
     *
     * @return The implemented interfaces
     */
    public Collection<PropertyInterface> getInterfaces() {
        return interfaces;
    }

    /**
     * Gets the domain class
     *
     * @return The domain class
     */
    public ClassModel getDomain() {
        return propertyDomain;
    }

    /**
     * Gets the range class
     *
     * @return The range class
     */
    public ClassModel getRange() {
        return propertyRange;
    }

    /**
     * Gets the ancestor implementations
     *
     * @return The ancestor implementations
     */
    public List<PropertyImplementation> getAncestors() {
        return implAncestors;
    }

    /**
     * Gets the descendant implementations
     *
     * @return The descendant implementations
     */
    public List<PropertyImplementation> getDescendants() {
        return implDescendants;
    }

    /**
     * Initializes this property data
     *
     * @param classe   The parent class model
     * @param property The associated property
     */
    public PropertyImplementation(ClassModel classe, PropertyModel property) {
        super(classe, property);
        propertyDomain = property.getDomain();
        if (property.isObjectProperty())
            propertyRange = property.getRangeClass();
        interfaces = new ArrayList<>();
        masked = new ArrayList<>();
        implAncestors = new ArrayList<>();
        implDescendants = new ArrayList<>();
    }

    /**
     * Adds an implementation interface
     *
     * @param propertyInterface An implemented interfaces
     */
    public void addInterface(PropertyInterface propertyInterface) {
        for (PropertyInterface current : interfaces) {
            if (current.sameAs(propertyInterface)) {
                masked.add(propertyInterface);
                return;
            }
        }
        interfaces.add(propertyInterface);
    }

    /**
     * Closes this implementation
     *
     * @param logger The logger to use for reporting errors
     */
    public void close(Logger logger) {
        inTypeRestrictionChain = false;
        for (PropertyInterface propertyInterface : interfaces) {
            if (propertyInterface.restrictType()) {
                inTypeRestrictionChain = true;
                break;
            }
        }
        if (!inTypeRestrictionChain) {
            for (PropertyInterface propertyInterface : masked) {
                if (propertyInterface.restrictType()) {
                    inTypeRestrictionChain = true;
                    break;
                }
            }
        }

        if (inTypeRestrictionChain) {
            for (PropertyInterface propertyInterface : interfaces)
                propertyInterface.setInTypeRestrictionChain();
            for (PropertyInterface propertyInterface : masked)
                propertyInterface.setInTypeRestrictionChain();
        }

        if (property.isObjectProperty()) {
            String header = "Property " + property.getName() + " : " + parentClass.getJavaName() + " -> " + rangeClass.getJavaName();
            List<String> errors = new ArrayList<>();
            if (property.isSymmetric()) {
                if (!property.hasInverse())
                    errors.add("Property is symmetric but has no inverse");
                else if (property.getInverse() != property)
                    errors.add("Property is symmetric but the inverse property is different");
            }
            if (property.isReflexive()) {
                if (!parentClass.isCompatibleWith(rangeClass))
                    errors.add("Property is reflexive but the domain is not a subset of the range");
            }
            if (property.hasInverse()) {
                ClassModel inverseDomain = property.getInverse().getDomain();
                ClassModel inverseRange = property.getInverse().getRangeClass();
                if (propertyDomain != inverseRange)
                    errors.add("Property domain is different from the range of the inverse property " + property.getInverse().getName());
                if (propertyRange != inverseDomain)
                    errors.add("Property range is different from the domain of the inverse property " + property.getInverse().getName());
            }
            if (!errors.isEmpty()) {
                logger.error(header);
                for (String error : errors)
                    logger.error(error);
            }
        }
    }

    /**
     * Builds the property graph
     */
    public void buildPropertyGraph() {
        if (property.isObjectProperty()) {
            for (PropertyModel parent : property.getSuperProperties())
                implAncestors.add(parentClass.getPropertyImplementation(parent));
            for (int i = 0; i != implAncestors.size(); i++) {
                for (PropertyModel parent : implAncestors.get(i).getProperty().getDirectSuperProperties()) {
                    PropertyImplementation impl = parentClass.getPropertyImplementation(parent);
                    if (!implAncestors.contains(impl))
                        implAncestors.add(impl);
                }
            }
            if (property.hasInverse()) {
                implInverse = getImplementationOf(property.getInverse());
            }
            buildDescendantsOf(this);
        }
    }

    /**
     * Gets the implementation of a property
     *
     * @param model The model of the property
     * @return The implementation
     */
    private PropertyImplementation getImplementationOf(PropertyModel model) {
        ClassModel domain = model.getDomain();
        if (!domain.isAbstract())
            return domain.getPropertyImplementation(model);
        for (ClassModel descendant : domain.getSubClasses()) {
            if (descendant.isAbstract())
                continue;
            return descendant.getPropertyImplementation(model);
        }
        return null;
    }

    /**
     * Builds the descendants of the specified property implementation
     *
     * @param propertyImplementation A property implementation
     */
    private void buildDescendantsOf(PropertyImplementation propertyImplementation) {
        if (propertyImplementation.property.getSubProperties().isEmpty()) {
            if (propertyImplementation != this)
                implDescendants.add(propertyImplementation);
            return;
        }
        for (PropertyModel child : propertyImplementation.property.getDirectSubProperties()) {
            ClassModel childDomain = child.getDomain();
            if (parentClass.isCompatibleWith(childDomain)) {
                PropertyImplementation childImpl = parentClass.getPropertyImplementation(child);
                buildDescendantsOf(childImpl);
            }
        }
        if (propertyImplementation != this)
            implDescendants.add(propertyImplementation);
    }

    /**
     * Generates and writes the code for this property implementation as a standalone distribution
     *
     * @param writer The writer to write to
     * @throws IOException When an IO error occurs
     */
    public void writeStandalone(Writer writer) throws IOException {
        boolean isInTypeRestrictionChain = false;
        for (PropertyInterface inter : getInterfaces()) {
            if (inter.isInTypeRestrictionChain()) {
                isInTypeRestrictionChain = true;
                break;
            }
        }

        writeStandaloneFields(writer);

        if (!getProperty().isObjectProperty()) {
            for (PropertyInterface inter : getInterfaces()) {
                if (inter.isVector()) {
                    if (isVector()) {
                        // implemented as a vector
                        writeStandaloneDatatypeVectorInterfaceOnVectorImpl(writer, inter);
                    } else {
                        // implemented as a scalar
                        if (getDefaultValue().equals("null")) {
                            writeStandaloneDatatypeVectorInterfaceOnObjectScalarImpl(writer, inter);
                        } else {
                            writeStandaloneDatatypeVectorInterfaceOnPrimitiveScalarImpl(writer, inter);
                        }
                    }
                } else {
                    if (isVector()) {
                        // implemented as a vector
                        if (getDefaultValue().equals("null")) {
                            writeStandaloneDatatypeScalarInterfaceOnObjectVectorImpl(writer, inter);
                        } else {
                            writeStandaloneDatatypeScalarInterfaceOnPrimitiveVectorImpl(writer, inter);
                        }
                    } else {
                        // implemented as a scalar
                        writeStandaloneDatatypeScalarInterfaceOnScalarImpl(writer, inter);
                    }
                }
            }
        } else {
            writeStandaloneObjectMutators(writer);
            for (PropertyInterface inter : getInterfaces()) {
                if (inter.isVector()) {
                    if (isVector()) {
                        if (inter.getJavaRangeScalar().equals(getJavaRangeScalar())) {
                            writeStandaloneObjectVectorInterfaceOnVectorImplSameType(writer, inter, isInTypeRestrictionChain);
                        } else {
                            writeStandaloneObjectVectorInterfaceOnVectorImplSubType(writer, inter, isInTypeRestrictionChain);
                        }
                    } else {
                        if (inter.getJavaRangeScalar().equals(getJavaRangeScalar())) {
                            writeStandaloneObjectVectorInterfaceOnScalarImplSameType(writer, inter, isInTypeRestrictionChain);
                        } else {
                            writeStandaloneObjectVectorInterfaceOnScalarImplSubType(writer, inter, isInTypeRestrictionChain);
                        }
                    }
                } else {
                    if (isVector()) {
                        if (inter.getJavaRangeScalar().equals(getJavaRangeScalar())) {
                            writeStandaloneObjectScalarInterfaceOnVectorImplSameType(writer, inter, isInTypeRestrictionChain);
                        } else {
                            writeStandaloneObjectScalarInterfaceOnVectorImplSubType(writer, inter, isInTypeRestrictionChain);
                        }
                    } else {
                        if (inter.getJavaRangeScalar().equals(getJavaRangeScalar())) {
                            writeStandaloneObjectScalarInterfaceOnScalarImplSameType(writer, inter, isInTypeRestrictionChain);
                        } else {
                            writeStandaloneObjectScalarInterfaceOnScalarImplSubType(writer, inter, isInTypeRestrictionChain);
                        }
                    }
                }
            }
        }
    }

    /**
     * Writes the fields for the standalone generation
     *
     * @param writer The write to use
     * @throws IOException When writing failed
     */
    private void writeStandaloneFields(Writer writer) throws IOException {
        String iri = this.property.getOWLProperty().getInterpretationOf().getHasIRI().getHasValue();
        String name = getJavaName();
        writer.append("    /**").append(Files.LINE_SEPARATOR);
        writer.append("     * The backing data for the property ").append(name).append(Files.LINE_SEPARATOR);
        writer.append("     * This implements the storage for original OWL property ").append(iri).append(Files.LINE_SEPARATOR);
        writer.append("     */").append(Files.LINE_SEPARATOR);
        if (isVector())
            writer.append("    private List<").append(getJavaRangeVector()).append("> __impl").append(name).append(";").append(Files.LINE_SEPARATOR);
        else
            writer.append("    private ").append(getJavaRangeScalar()).append(" __impl").append(name).append(";").append(Files.LINE_SEPARATOR);
        writer.append(Files.LINE_SEPARATOR);
    }

    /**
     * Writes the constructor part for this implementation
     *
     * @param writer The writer to use
     * @throws IOException When writing failed
     */
    public void writeStandaloneConstructor(Writer writer) throws IOException {
        String iri = this.property.getOWLProperty().getInterpretationOf().getHasIRI().getHasValue();
        String name = getJavaName();
        writer.append("        // initialize property ").append(iri).append(Files.LINE_SEPARATOR);
        if (isVector())
            writer.append("        this.__impl").append(name).append(" = new ArrayList<>();").append(Files.LINE_SEPARATOR);
        else
            writer.append("        this.__impl").append(name).append(" = ").append(getDefaultValue()).append(";").append(Files.LINE_SEPARATOR);
    }

    /**
     * Writes the core of the set method for a datatype property implementation
     *
     * @param writer         The write to use
     * @param implementation The implementation to write for
     * @throws IOException When writing failed
     */
    private void writeStandaloneDatatypeDoSet(Writer writer, PropertyImplementation implementation) throws IOException {
        if (!implementation.isVector()) {
            writer.append("        __impl").append(implementation.getJavaName()).append(" = elem;").append(Files.LINE_SEPARATOR);
        } else if (implementation.getDefaultValue().equals("null")) {
            writer.append("        __impl").append(implementation.getJavaName()).append(".clear();").append(Files.LINE_SEPARATOR);
            writer.append("        if (elem != null)").append(Files.LINE_SEPARATOR);
            writer.append("            __impl").append(implementation.getJavaName()).append(".add(elem);").append(Files.LINE_SEPARATOR);
        } else {
            writer.append("        __impl").append(implementation.getJavaName()).append(".clear();").append(Files.LINE_SEPARATOR);
            writer.append("        __impl").append(implementation.getJavaName()).append(".add(elem);").append(Files.LINE_SEPARATOR);
        }
    }

    /**
     * Writes the standalone implementation of this datatype scalar property implemented as a scalar
     *
     * @param writer The write to use
     * @param inter  The property interface to implement
     * @throws IOException When writing failed
     */
    private void writeStandaloneDatatypeScalarInterfaceOnScalarImpl(Writer writer, PropertyInterface inter) throws IOException {
        String name = getJavaName();

        writer.append("    @Override").append(Files.LINE_SEPARATOR);
        writer.append("    public ").append(inter.getJavaRangeScalar()).append(" get").append(name).append("() {").append(Files.LINE_SEPARATOR);
        writer.append("        return __impl").append(name).append(";").append(Files.LINE_SEPARATOR);
        writer.append("    }").append(Files.LINE_SEPARATOR);
        writer.append(Files.LINE_SEPARATOR);

        writer.append("    @Override").append(Files.LINE_SEPARATOR);
        writer.append("    public void set").append(name).append("(").append(inter.getJavaRangeScalar()).append(" elem) {").append(Files.LINE_SEPARATOR);
        for (PropertyImplementation impl : getAncestors())
            writeStandaloneDatatypeDoSet(writer, impl);
        writeStandaloneDatatypeDoSet(writer, this);
        for (PropertyImplementation impl : getDescendants())
            writeStandaloneDatatypeDoSet(writer, impl);
        writer.append("    }").append(Files.LINE_SEPARATOR);
        writer.append(Files.LINE_SEPARATOR);
    }

    /**
     * Writes the standalone implementation of this datatype scalar property implemented as a vector of primitives
     *
     * @param writer The write to use
     * @param inter  The property interface to implement
     * @throws IOException When writing failed
     */
    private void writeStandaloneDatatypeScalarInterfaceOnPrimitiveVectorImpl(Writer writer, PropertyInterface inter) throws IOException {
        String name = getJavaName();

        writer.append("    @Override").append(Files.LINE_SEPARATOR);
        writer.append("    public ").append(inter.getJavaRangeScalar()).append(" get").append(name).append("() {").append(Files.LINE_SEPARATOR);
        writer.append("        if (__impl)").append(name).append(".isEmpty())").append(Files.LINE_SEPARATOR);
        writer.append("            return ").append(getDefaultValue()).append(";").append(Files.LINE_SEPARATOR);
        writer.append("        return __impl").append(name).append(".get(0);").append(Files.LINE_SEPARATOR);
        writer.append("    }").append(Files.LINE_SEPARATOR);
        writer.append(Files.LINE_SEPARATOR);

        writer.append("    @Override").append(Files.LINE_SEPARATOR);
        writer.append("    public void set").append(name).append("(").append(inter.getJavaRangeScalar()).append(" elem) {").append(Files.LINE_SEPARATOR);
        for (PropertyImplementation impl : getAncestors())
            writeStandaloneDatatypeDoSet(writer, impl);
        writeStandaloneDatatypeDoSet(writer, this);
        for (PropertyImplementation impl : getDescendants())
            writeStandaloneDatatypeDoSet(writer, impl);
        writer.append("    }").append(Files.LINE_SEPARATOR);
        writer.append(Files.LINE_SEPARATOR);
    }

    /**
     * Writes the standalone implementation of this datatype scalar property implemented as a vector of objects
     *
     * @param writer The write to use
     * @param inter  The property interface to implement
     * @throws IOException When writing failed
     */
    private void writeStandaloneDatatypeScalarInterfaceOnObjectVectorImpl(Writer writer, PropertyInterface inter) throws IOException {
        String name = getJavaName();

        writer.append("    @Override").append(Files.LINE_SEPARATOR);
        writer.append("    public ").append(inter.getJavaRangeScalar()).append(" get").append(name).append("() {").append(Files.LINE_SEPARATOR);
        writer.append("        if (__impl)").append(name).append(".isEmpty())").append(Files.LINE_SEPARATOR);
        writer.append("            return ").append(getDefaultValue()).append(";").append(Files.LINE_SEPARATOR);
        writer.append("        return __impl").append(name).append(".get(0);").append(Files.LINE_SEPARATOR);
        writer.append("    }").append(Files.LINE_SEPARATOR);
        writer.append(Files.LINE_SEPARATOR);

        writer.append("    @Override").append(Files.LINE_SEPARATOR);
        writer.append("    public void set").append(name).append("(").append(inter.getJavaRangeScalar()).append(" elem) {").append(Files.LINE_SEPARATOR);
        for (PropertyImplementation impl : getAncestors())
            writeStandaloneDatatypeDoSet(writer, impl);
        writeStandaloneDatatypeDoSet(writer, this);
        for (PropertyImplementation impl : getDescendants())
            writeStandaloneDatatypeDoSet(writer, impl);
        writer.append("    }").append(Files.LINE_SEPARATOR);
        writer.append(Files.LINE_SEPARATOR);
    }

    /**
     * Writes the check part of the add method for a datatype property implementation
     *
     * @param writer         The write to use
     * @param implementation The implementation to write for
     * @throws IOException When writing failed
     */
    private void writeStandaloneDatatypeCheckAdd(Writer writer, PropertyImplementation implementation) throws IOException {
        if (implementation.isVector()) {
            if (implementation.getCardMax() != Integer.MAX_VALUE) {
                writer.append("        if (__impl").append(implementation.getJavaName()).append(".size() >= ").append(Integer.toString(implementation.getCardMax())).append(")").append(Files.LINE_SEPARATOR);
                writer.append("            throw new IllegalArgumentException(\"Maximum cardinality is ").append(Integer.toString(implementation.getCardMax())).append("\");").append(Files.LINE_SEPARATOR);
            }
        } else if (implementation.getDefaultValue().equals("null")) {
            writer.append("        if (__impl").append(implementation.getJavaName()).append(" != null)").append(Files.LINE_SEPARATOR);
            writer.append("            throw new IllegalArgumentException(\"Maximum cardinality is 1\");").append(Files.LINE_SEPARATOR);
        }
    }

    /**
     * Writes the core of the add method for a datatype property implementation
     *
     * @param writer         The write to use
     * @param implementation The implementation to write for
     * @throws IOException When writing failed
     */
    private void writeStandaloneDatatypeDoAdd(Writer writer, PropertyImplementation implementation) throws IOException {
        if (implementation.isVector()) {
            writer.append("        __impl").append(implementation.getJavaName()).append(".add(elem);").append(Files.LINE_SEPARATOR);
        } else {
            writer.append("        __impl").append(implementation.getJavaName()).append(" = elem;").append(Files.LINE_SEPARATOR);
        }
    }

    /**
     * Writes the core of the remove method for a datatype property implementation
     *
     * @param writer         The write to use
     * @param implementation The implementation to write for
     * @throws IOException When writing failed
     */
    private void writeStandaloneDatatypeDoRemoveCombine(Writer writer, PropertyImplementation implementation) throws IOException {
        if (implementation.isVector()) {
            if (implementation.getJavaRangeScalar().equals("int"))
                // special case for int due to confusion between remove(int) and remove(Object)
                writer.append("        success &= __impl").append(implementation.getJavaName()).append(".remove((Integer) elem);").append(Files.LINE_SEPARATOR);
            else
                writer.append("        success &= __impl").append(implementation.getJavaName()).append(".remove(elem);").append(Files.LINE_SEPARATOR);
        } else if (implementation.getDefaultValue().equals("null")) {
            writer.append("        if (Objects.equals(__impl").append(implementation.getJavaName()).append(", elem))").append(Files.LINE_SEPARATOR);
            writer.append("            __impl").append(implementation.getJavaName()).append(" = null;").append(Files.LINE_SEPARATOR);
            writer.append("        else").append(Files.LINE_SEPARATOR);
            writer.append("            success = false;").append(Files.LINE_SEPARATOR);
        } else {
            writer.append("        if (__impl").append(implementation.getJavaName()).append(" == elem)").append(Files.LINE_SEPARATOR);
            writer.append("            __impl").append(implementation.getJavaName()).append(" = ").append(getDefaultValue()).append(";").append(Files.LINE_SEPARATOR);
            writer.append("        else").append(Files.LINE_SEPARATOR);
            writer.append("            success = false;").append(Files.LINE_SEPARATOR);
        }
    }

    /**
     * Writes the core of the remove method for a datatype property implementation
     *
     * @param writer         The write to use
     * @param implementation The implementation to write for
     * @throws IOException When writing failed
     */
    private void writeStandaloneDatatypeDoRemoveSolo(Writer writer, PropertyImplementation implementation) throws IOException {
        if (implementation.isVector()) {
            if (implementation.getJavaRangeScalar().equals("int"))
                // special case for int due to confusion between remove(int) and remove(Object)
                writer.append("        return __impl").append(implementation.getJavaName()).append(".remove((Integer) elem);").append(Files.LINE_SEPARATOR);
            else
                writer.append("        return __impl").append(implementation.getJavaName()).append(".remove(elem);").append(Files.LINE_SEPARATOR);
        } else if (implementation.getDefaultValue().equals("null")) {
            writer.append("        if (!Objects.equals(__impl").append(implementation.getJavaName()).append(", elem))").append(Files.LINE_SEPARATOR);
            writer.append("            return false;").append(Files.LINE_SEPARATOR);
            writer.append("        __impl").append(implementation.getJavaName()).append(" = null;").append(Files.LINE_SEPARATOR);
        } else {
            writer.append("        if (__impl").append(implementation.getJavaName()).append(" != elem)").append(Files.LINE_SEPARATOR);
            writer.append("            return false;").append(Files.LINE_SEPARATOR);
            writer.append("        __impl").append(implementation.getJavaName()).append(" = ").append(getDefaultValue()).append(";").append(Files.LINE_SEPARATOR);
        }
    }

    /**
     * Writes the standalone implementation of this datatype vector property implemented as a primitive scalar
     *
     * @param writer The write to use
     * @param inter  The property interface to implement
     * @throws IOException When writing failed
     */
    private void writeStandaloneDatatypeVectorInterfaceOnPrimitiveScalarImpl(Writer writer, PropertyInterface inter) throws IOException {
        String name = getJavaName();

        writer.append("    @Override").append(Files.LINE_SEPARATOR);
        writer.append("    public Collection<").append(inter.getJavaRangeVector()).append("> getAll").append(name).append("() {").append(Files.LINE_SEPARATOR);
        writer.append("        return Collections.singletonList(__impl").append(name).append(");").append(Files.LINE_SEPARATOR);
        writer.append("    }").append(Files.LINE_SEPARATOR);
        writer.append(Files.LINE_SEPARATOR);

        writer.append("    @Override").append(Files.LINE_SEPARATOR);
        writer.append("    public boolean add").append(name).append("(").append(inter.getJavaRangeScalar()).append(" elem) {").append(Files.LINE_SEPARATOR);
        if (getDefaultValue().equals("null")) {
            writer.append("        if (elem == null)").append(Files.LINE_SEPARATOR);
            writer.append("            throw new IllegalArgumentException(\"Expected a value\");").append(Files.LINE_SEPARATOR);
        }
        for (PropertyImplementation impl : getAncestors())
            writeStandaloneDatatypeCheckAdd(writer, impl);
        writeStandaloneDatatypeCheckAdd(writer, this);
        for (PropertyImplementation impl : getDescendants())
            writeStandaloneDatatypeCheckAdd(writer, impl);
        for (PropertyImplementation impl : getAncestors())
            writeStandaloneDatatypeDoAdd(writer, impl);
        writeStandaloneDatatypeDoAdd(writer, this);
        for (PropertyImplementation impl : getDescendants())
            writeStandaloneDatatypeDoAdd(writer, impl);
        writer.append("        return true;").append(Files.LINE_SEPARATOR);
        writer.append("    }").append(Files.LINE_SEPARATOR);
        writer.append(Files.LINE_SEPARATOR);

        writer.append("    @Override").append(Files.LINE_SEPARATOR);
        writer.append("    public boolean remove").append(name).append("(").append(inter.getJavaRangeScalar()).append(" elem) {").append(Files.LINE_SEPARATOR);
        if (getDefaultValue().equals("null")) {
            writer.append("        if (elem == null)").append(Files.LINE_SEPARATOR);
            writer.append("            throw new IllegalArgumentException(\"Expected a value\");").append(Files.LINE_SEPARATOR);
        }
        if (!getAncestors().isEmpty() || !getDescendants().isEmpty()) {
            writer.append("        boolean success = true;").append(Files.LINE_SEPARATOR);
            for (PropertyImplementation impl : getAncestors())
                writeStandaloneDatatypeDoRemoveCombine(writer, impl);
            writeStandaloneDatatypeDoRemoveCombine(writer, this);
            for (PropertyImplementation impl : getDescendants())
                writeStandaloneDatatypeDoRemoveCombine(writer, impl);
            writer.append("        return success;").append(Files.LINE_SEPARATOR);
        } else {
            writeStandaloneDatatypeDoRemoveSolo(writer, this);
        }
        writer.append("    }").append(Files.LINE_SEPARATOR);
        writer.append(Files.LINE_SEPARATOR);
    }

    /**
     * Writes the standalone implementation of this datatype vector property implemented as an object scalar
     *
     * @param writer The write to use
     * @param inter  The property interface to implement
     * @throws IOException When writing failed
     */
    private void writeStandaloneDatatypeVectorInterfaceOnObjectScalarImpl(Writer writer, PropertyInterface inter) throws IOException {
        String name = getJavaName();

        writer.append("    @Override").append(Files.LINE_SEPARATOR);
        writer.append("    public Collection<").append(inter.getJavaRangeVector()).append("> getAll").append(name).append("() {").append(Files.LINE_SEPARATOR);
        writer.append("        if (__impl").append(name).append(" == null)").append(Files.LINE_SEPARATOR);
        writer.append("            return Collections.emptyList();").append(Files.LINE_SEPARATOR);
        writer.append("        return Collections.singletonList(__impl").append(name).append(");").append(Files.LINE_SEPARATOR);
        writer.append("    }").append(Files.LINE_SEPARATOR);
        writer.append(Files.LINE_SEPARATOR);

        writer.append("    @Override").append(Files.LINE_SEPARATOR);
        writer.append("    public boolean add").append(name).append("(").append(inter.getJavaRangeScalar()).append(" elem) {").append(Files.LINE_SEPARATOR);
        for (PropertyImplementation impl : getAncestors())
            writeStandaloneDatatypeCheckAdd(writer, impl);
        writeStandaloneDatatypeCheckAdd(writer, this);
        for (PropertyImplementation impl : getDescendants())
            writeStandaloneDatatypeCheckAdd(writer, impl);
        for (PropertyImplementation impl : getAncestors())
            writeStandaloneDatatypeDoAdd(writer, impl);
        writeStandaloneDatatypeDoAdd(writer, this);
        for (PropertyImplementation impl : getDescendants())
            writeStandaloneDatatypeDoAdd(writer, impl);
        writer.append("        return true;").append(Files.LINE_SEPARATOR);
        writer.append("    }").append(Files.LINE_SEPARATOR);
        writer.append(Files.LINE_SEPARATOR);

        writer.append("    @Override").append(Files.LINE_SEPARATOR);
        writer.append("    public boolean remove").append(name).append("(").append(inter.getJavaRangeScalar()).append(" elem) {").append(Files.LINE_SEPARATOR);
        if (getDefaultValue().equals("null")) {
            writer.append("        if (elem == null)").append(Files.LINE_SEPARATOR);
            writer.append("            throw new IllegalArgumentException(\"Expected a value\");").append(Files.LINE_SEPARATOR);
        }
        if (!getAncestors().isEmpty() || !getDescendants().isEmpty()) {
            writer.append("        boolean success = true;").append(Files.LINE_SEPARATOR);
            for (PropertyImplementation impl : getAncestors())
                writeStandaloneDatatypeDoRemoveCombine(writer, impl);
            writeStandaloneDatatypeDoRemoveCombine(writer, this);
            for (PropertyImplementation impl : getDescendants())
                writeStandaloneDatatypeDoRemoveCombine(writer, impl);
            writer.append("        return success;").append(Files.LINE_SEPARATOR);
        } else {
            writeStandaloneDatatypeDoRemoveSolo(writer, this);
        }
        writer.append("    }").append(Files.LINE_SEPARATOR);
        writer.append(Files.LINE_SEPARATOR);
    }

    /**
     * Writes the standalone implementation of this datatype vector property implemented as a vector
     *
     * @param writer The write to use
     * @param inter  The property interface to implement
     * @throws IOException When writing failed
     */
    private void writeStandaloneDatatypeVectorInterfaceOnVectorImpl(Writer writer, PropertyInterface inter) throws IOException {
        String name = getJavaName();

        writer.append("    @Override").append(Files.LINE_SEPARATOR);
        writer.append("    public Collection<").append(inter.getJavaRangeVector()).append("> getAll").append(name).append("() {").append(Files.LINE_SEPARATOR);
        writer.append("        return Collections.unmodifiableCollection(__impl").append(name).append(");").append(Files.LINE_SEPARATOR);
        writer.append("    }").append(Files.LINE_SEPARATOR);
        writer.append(Files.LINE_SEPARATOR);

        writer.append("    @Override").append(Files.LINE_SEPARATOR);
        writer.append("    public boolean add").append(name).append("(").append(inter.getJavaRangeScalar()).append(" elem) {").append(Files.LINE_SEPARATOR);
        for (PropertyImplementation impl : getAncestors())
            writeStandaloneDatatypeCheckAdd(writer, impl);
        writeStandaloneDatatypeCheckAdd(writer, this);
        for (PropertyImplementation impl : getDescendants())
            writeStandaloneDatatypeCheckAdd(writer, impl);
        for (PropertyImplementation impl : getAncestors())
            writeStandaloneDatatypeDoAdd(writer, impl);
        writeStandaloneDatatypeDoAdd(writer, this);
        for (PropertyImplementation impl : getDescendants())
            writeStandaloneDatatypeDoAdd(writer, impl);
        writer.append("        return true;").append(Files.LINE_SEPARATOR);
        writer.append("    }").append(Files.LINE_SEPARATOR);
        writer.append(Files.LINE_SEPARATOR);

        writer.append("    @Override").append(Files.LINE_SEPARATOR);
        writer.append("    public boolean remove").append(name).append("(").append(inter.getJavaRangeScalar()).append(" elem) {").append(Files.LINE_SEPARATOR);
        if (getDefaultValue().equals("null")) {
            writer.append("        if (elem == null)").append(Files.LINE_SEPARATOR);
            writer.append("            throw new IllegalArgumentException(\"Expected a value\");").append(Files.LINE_SEPARATOR);
        }
        if (!getAncestors().isEmpty() || !getDescendants().isEmpty()) {
            writer.append("        boolean success = true;").append(Files.LINE_SEPARATOR);
            for (PropertyImplementation impl : getAncestors())
                writeStandaloneDatatypeDoRemoveCombine(writer, impl);
            writeStandaloneDatatypeDoRemoveCombine(writer, this);
            for (PropertyImplementation impl : getDescendants())
                writeStandaloneDatatypeDoRemoveCombine(writer, impl);
            writer.append("        return success;").append(Files.LINE_SEPARATOR);
        } else {
            writeStandaloneDatatypeDoRemoveSolo(writer, this);
        }
        writer.append("    }").append(Files.LINE_SEPARATOR);
        writer.append(Files.LINE_SEPARATOR);
    }

    /**
     * Writes the standalone mutators of this object scalar property
     *
     * @param writer The write to use
     * @throws IOException When writing failed
     */
    private void writeStandaloneObjectMutators(Writer writer) throws IOException {
        String iri = this.property.getOWLProperty().getInterpretationOf().getHasIRI().getHasValue();
        String name = getJavaName();

        List<String> inverseDomains = new ArrayList<>();
        if (implInverse != null) {
            if (getRangeClass().isAbstract()) {
                for (ClassModel classModel : getRangeClass().getSubClasses()) {
                    if (!classModel.isAbstract() && getParentClass().isCompatibleWith(implInverse.getRangeClass()))
                        inverseDomains.add(classModel.getJavaImplName());
                }
            } else {
                inverseDomains.add(getRangeClass().getJavaImplName());
            }
        }

        writer.append("    /**").append(Files.LINE_SEPARATOR);
        writer.append("     * Adds a value to the property ").append(name).append(Files.LINE_SEPARATOR);
        writer.append("     * Original OWL property is ").append(iri).append(Files.LINE_SEPARATOR);
        writer.append("     *").append(Files.LINE_SEPARATOR);
        writer.append("     * @param elem The element value to add (must not be null)").append(Files.LINE_SEPARATOR);
        writer.append("     */").append(Files.LINE_SEPARATOR);
        writer.append("    protected void doSimpleAdd").append(name).append("(").append(getJavaRangeScalar()).append(" elem) {").append(Files.LINE_SEPARATOR);
        if (isVector()) {
            writer.append("        __impl").append(name).append(".add(elem);").append(Files.LINE_SEPARATOR);
        } else {
            writer.append("        __impl").append(name).append(" = elem;").append(Files.LINE_SEPARATOR);
        }
        writer.append("    }").append(Files.LINE_SEPARATOR);
        writer.append(Files.LINE_SEPARATOR);

        writer.append("    /**").append(Files.LINE_SEPARATOR);
        writer.append("     * Removes a value from the property ").append(name).append(Files.LINE_SEPARATOR);
        writer.append("     * Original OWL property is ").append(iri).append(Files.LINE_SEPARATOR);
        writer.append("     *").append(Files.LINE_SEPARATOR);
        writer.append("     * @param elem The element value to remove (must not be null)").append(Files.LINE_SEPARATOR);
        writer.append("     */").append(Files.LINE_SEPARATOR);
        writer.append("    protected void doSimpleRemove").append(name).append("(").append(getJavaRangeScalar()).append(" elem) {").append(Files.LINE_SEPARATOR);
        if (isVector()) {
            writer.append("        __impl").append(name).append(".remove(elem);").append(Files.LINE_SEPARATOR);
        } else {
            writer.append("        __impl").append(name).append(" = null;").append(Files.LINE_SEPARATOR);
        }
        writer.append("    }").append(Files.LINE_SEPARATOR);
        writer.append(Files.LINE_SEPARATOR);

        writer.append("    /**").append(Files.LINE_SEPARATOR);
        writer.append("     * Adds a value to the property ").append(name).append(Files.LINE_SEPARATOR);
        writer.append("     * Original OWL property is ").append(iri).append(Files.LINE_SEPARATOR);
        if (implInverse != null)
            writer.append("     * This method will also update the inverse property ").append(implInverse.getJavaName()).append(Files.LINE_SEPARATOR);
        writer.append("     *").append(Files.LINE_SEPARATOR);
        writer.append("     * @param elem The element value to add (must not be null)").append(Files.LINE_SEPARATOR);
        writer.append("     */").append(Files.LINE_SEPARATOR);
        writer.append("    private void doPropertyAdd").append(name).append("(").append(getJavaRangeScalar()).append(" elem) {").append(Files.LINE_SEPARATOR);
        writer.append("        doSimpleAdd").append(name).append("(elem);").append(Files.LINE_SEPARATOR);
        if (implInverse != null) {
            for (int i = 0; i != inverseDomains.size(); i++) {
                if (i == 0) {
                    writer.append("        if (elem instanceof ").append(inverseDomains.get(i)).append(")").append(Files.LINE_SEPARATOR);
                } else {
                    writer.append("        else if (elem instanceof ").append(inverseDomains.get(i)).append(")").append(Files.LINE_SEPARATOR);
                }
                writer.append("            ((").append(inverseDomains.get(i)).append(") elem).doSimpleAdd").append(implInverse.getJavaName()).append("(this);").append(Files.LINE_SEPARATOR);
            }
        }
        writer.append("    }").append(Files.LINE_SEPARATOR);
        writer.append(Files.LINE_SEPARATOR);

        writer.append("    /**").append(Files.LINE_SEPARATOR);
        writer.append("     * Removes a value from the property ").append(name).append(Files.LINE_SEPARATOR);
        writer.append("     * Original OWL property is ").append(iri).append(Files.LINE_SEPARATOR);
        if (implInverse != null)
            writer.append("     * This method will also update the inverse property ").append(implInverse.getJavaName()).append(Files.LINE_SEPARATOR);
        writer.append("     *").append(Files.LINE_SEPARATOR);
        writer.append("     * @param elem The element value to remove (must not be null)").append(Files.LINE_SEPARATOR);
        writer.append("     */").append(Files.LINE_SEPARATOR);
        writer.append("    private void doPropertyRemove").append(name).append("(").append(getJavaRangeScalar()).append(" elem) {").append(Files.LINE_SEPARATOR);
        writer.append("        doSimpleRemove").append(name).append("(elem);").append(Files.LINE_SEPARATOR);
        if (implInverse != null) {
            for (int i = 0; i != inverseDomains.size(); i++) {
                if (i == 0) {
                    writer.append("        if (elem instanceof ").append(inverseDomains.get(i)).append(")").append(Files.LINE_SEPARATOR);
                } else {
                    writer.append("        else if (elem instanceof ").append(inverseDomains.get(i)).append(")").append(Files.LINE_SEPARATOR);
                }
                writer.append("            ((").append(inverseDomains.get(i)).append(") elem).doSimpleRemove").append(implInverse.getJavaName()).append("(this);").append(Files.LINE_SEPARATOR);
            }
        }
        writer.append("    }").append(Files.LINE_SEPARATOR);
        writer.append(Files.LINE_SEPARATOR);

        writer.append("    /**").append(Files.LINE_SEPARATOR);
        writer.append("     * Tries to add a value to the property ").append(name).append(" and its super properties (if any)").append(Files.LINE_SEPARATOR);
        writer.append("     * Original OWL property is ").append(iri).append(Files.LINE_SEPARATOR);
        writer.append("     *").append(Files.LINE_SEPARATOR);
        writer.append("     * @param elem The element value to add (must not be null)").append(Files.LINE_SEPARATOR);
        writer.append("     */").append(Files.LINE_SEPARATOR);
        writer.append("    private void doGraphAdd").append(name).append("(").append(getJavaRangeScalar()).append(" elem) {").append(Files.LINE_SEPARATOR);
        for (PropertyImplementation ancestor : getAncestors()) {
            writer.append("        doPropertyAdd").append(ancestor.getJavaName()).append("(elem);").append(Files.LINE_SEPARATOR);
        }
        writer.append("        doPropertyAdd").append(name).append("(elem);").append(Files.LINE_SEPARATOR);
        writer.append("    }").append(Files.LINE_SEPARATOR);
        writer.append(Files.LINE_SEPARATOR);

        writer.append("    /**").append(Files.LINE_SEPARATOR);
        writer.append("     * Tries to remove a value from the property ").append(name).append(" and its super properties (if any)").append(Files.LINE_SEPARATOR);
        writer.append("     * Original OWL property is ").append(iri).append(Files.LINE_SEPARATOR);
        writer.append("     *").append(Files.LINE_SEPARATOR);
        writer.append("     * @param elem The element value to remove (must not be null)").append(Files.LINE_SEPARATOR);
        writer.append("     */").append(Files.LINE_SEPARATOR);
        writer.append("    private void doGraphRemove").append(name).append("(").append(getJavaRangeScalar()).append(" elem) {").append(Files.LINE_SEPARATOR);
        for (PropertyImplementation ancestor : getAncestors()) {
            writer.append("        doPropertyRemove").append(ancestor.getJavaName()).append("(elem);").append(Files.LINE_SEPARATOR);
        }
        writer.append("        doPropertyRemove").append(name).append("(elem);").append(Files.LINE_SEPARATOR);
        writer.append("    }").append(Files.LINE_SEPARATOR);
        writer.append(Files.LINE_SEPARATOR);

        writer.append("    /**").append(Files.LINE_SEPARATOR);
        writer.append("     * Dispatches the request for the addition of a value to the property ").append(name).append(Files.LINE_SEPARATOR);
        writer.append("     * This method tries to delegate to a sub property, if any.").append(Files.LINE_SEPARATOR);
        writer.append("     * Original OWL property is ").append(iri).append(Files.LINE_SEPARATOR);
        writer.append("     *").append(Files.LINE_SEPARATOR);
        writer.append("     * @param elem The element value to add (must not be null)").append(Files.LINE_SEPARATOR);
        writer.append("     */").append(Files.LINE_SEPARATOR);
        writer.append("    private void doDispatchAdd").append(name).append("(").append(getJavaRangeScalar()).append(" elem) {").append(Files.LINE_SEPARATOR);
        for (PropertyImplementation descendant : getDescendants()) {
            writer.append("        if (elem instanceof ").append(descendant.getJavaRangeScalar()).append(") {").append(Files.LINE_SEPARATOR);
            writer.append("            doGraphAdd").append(descendant.getJavaName()).append("((").append(descendant.getJavaRangeScalar()).append(") elem);").append(Files.LINE_SEPARATOR);
            writer.append("            return;").append(Files.LINE_SEPARATOR);
            writer.append("        }").append(Files.LINE_SEPARATOR);
        }
        writer.append("        doGraphAdd").append(name).append("(elem);").append(Files.LINE_SEPARATOR);
        writer.append("    }").append(Files.LINE_SEPARATOR);
        writer.append(Files.LINE_SEPARATOR);

        writer.append("    /**").append(Files.LINE_SEPARATOR);
        writer.append("     * Dispatches the request for the removal of a value from the property ").append(name).append(Files.LINE_SEPARATOR);
        writer.append("     * This method tries to delegate to a sub property, if any.").append(Files.LINE_SEPARATOR);
        writer.append("     * Original OWL property is ").append(iri).append(Files.LINE_SEPARATOR);
        writer.append("     *").append(Files.LINE_SEPARATOR);
        writer.append("     * @param elem The element value to remove (must not be null)").append(Files.LINE_SEPARATOR);
        writer.append("     */").append(Files.LINE_SEPARATOR);
        writer.append("    private void doDispatchRemove").append(name).append("(").append(getJavaRangeScalar()).append(" elem) {").append(Files.LINE_SEPARATOR);
        for (PropertyImplementation descendant : getDescendants()) {
            writer.append("        if (elem instanceof ").append(descendant.getJavaRangeScalar()).append(") {").append(Files.LINE_SEPARATOR);
            writer.append("            doGraphRemove").append(descendant.getJavaName()).append("((").append(descendant.getJavaRangeScalar()).append(") elem);").append(Files.LINE_SEPARATOR);
            writer.append("            return;").append(Files.LINE_SEPARATOR);
            writer.append("        }").append(Files.LINE_SEPARATOR);
        }
        writer.append("        doGraphRemove").append(name).append("(elem);").append(Files.LINE_SEPARATOR);
        writer.append("    }").append(Files.LINE_SEPARATOR);
        writer.append(Files.LINE_SEPARATOR);
    }

    /**
     * Writes the standalone implementation of this object scalar property implemented as a same type scalar
     *
     * @param writer The write to use
     * @param inter  The property interface to implement
     * @throws IOException When writing failed
     */
    private void writeStandaloneObjectScalarInterfaceOnScalarImplSameType(Writer writer, PropertyInterface inter, boolean isInTypeRestrictChain) throws IOException {
        String name = getJavaName();

        writer.append("    @Override").append(Files.LINE_SEPARATOR);
        if (isInTypeRestrictChain)
            writer.append("    public ").append(inter.getJavaRangeScalar()).append(" get").append(name).append("As(").append(inter.getJavaRangeScalar()).append(" type) {").append(Files.LINE_SEPARATOR);
        else
            writer.append("    public ").append(inter.getJavaRangeScalar()).append(" get").append(name).append("() {").append(Files.LINE_SEPARATOR);
        writer.append("        return __impl").append(name).append(";").append(Files.LINE_SEPARATOR);
        writer.append("    }").append(Files.LINE_SEPARATOR);
        writer.append(Files.LINE_SEPARATOR);

        writer.append("    @Override").append(Files.LINE_SEPARATOR);
        writer.append("    public void set").append(name).append("(").append(inter.getJavaRangeScalar()).append(" elem) {").append(Files.LINE_SEPARATOR);
        writer.append("        if (__impl").append(name).append(" == elem)").append(Files.LINE_SEPARATOR);
        writer.append("            return;").append(Files.LINE_SEPARATOR);
        writer.append("        if (elem == null) {").append(Files.LINE_SEPARATOR);
        writer.append("            doDispatchRemove").append(name).append("(__impl").append(name).append(");").append(Files.LINE_SEPARATOR);
        writer.append("        } else if (__impl").append(name).append(" == null) {").append(Files.LINE_SEPARATOR);
        writer.append("            doDispatchAdd").append(name).append("(elem);").append(Files.LINE_SEPARATOR);
        writer.append("        } else {").append(Files.LINE_SEPARATOR);
        writer.append("            doDispatchRemove").append(name).append("(__impl").append(name).append(");").append(Files.LINE_SEPARATOR);
        writer.append("            doDispatchAdd").append(name).append("(elem);").append(Files.LINE_SEPARATOR);
        writer.append("        }").append(Files.LINE_SEPARATOR);
        writer.append("    }").append(Files.LINE_SEPARATOR);
        writer.append(Files.LINE_SEPARATOR);
    }

    /**
     * Writes the standalone implementation of this object scalar property implemented as a sub-type scalar
     *
     * @param writer                The write to use
     * @param inter                 The property interface to implement
     * @param isInTypeRestrictChain Whether this property is in a type restriction chain
     * @throws IOException When writing failed
     */
    private void writeStandaloneObjectScalarInterfaceOnScalarImplSubType(Writer writer, PropertyInterface inter, boolean isInTypeRestrictChain) throws IOException {
        String name = getJavaName();

        writer.append("    @Override").append(Files.LINE_SEPARATOR);
        if (isInTypeRestrictChain)
            writer.append("    public ").append(inter.getJavaRangeScalar()).append(" get").append(name).append("As(").append(inter.getJavaRangeScalar()).append(" type) {").append(Files.LINE_SEPARATOR);
        else
            writer.append("    public ").append(inter.getJavaRangeScalar()).append(" get").append(name).append("() {").append(Files.LINE_SEPARATOR);
        writer.append("        return __impl").append(name).append(";").append(Files.LINE_SEPARATOR);
        writer.append("    }").append(Files.LINE_SEPARATOR);
        writer.append(Files.LINE_SEPARATOR);

        writer.append("    @Override").append(Files.LINE_SEPARATOR);
        writer.append("    public void set").append(name).append("(").append(inter.getJavaRangeScalar()).append(" elem) {").append(Files.LINE_SEPARATOR);
        writer.append("        if (__impl").append(name).append(" == elem)").append(Files.LINE_SEPARATOR);
        writer.append("            return;").append(Files.LINE_SEPARATOR);
        writer.append("        if (elem == null) {").append(Files.LINE_SEPARATOR);
        writer.append("            doDispatchRemove").append(name).append("(__impl").append(name).append(");").append(Files.LINE_SEPARATOR);
        writer.append("        } else if (__impl").append(name).append(" == null) {").append(Files.LINE_SEPARATOR);
        writer.append("            doDispatchAdd").append(name).append("((").append(getJavaRangeScalar()).append(") elem);").append(Files.LINE_SEPARATOR);
        writer.append("        } else {").append(Files.LINE_SEPARATOR);
        writer.append("            if (!(elem instanceof ").append(getJavaRangeScalar()).append("))").append(Files.LINE_SEPARATOR);
        writer.append("                throw new IllegalArgumentException(\"Expected type ").append(getJavaRangeScalar()).append("\");").append(Files.LINE_SEPARATOR);
        writer.append("            doDispatchRemove").append(name).append("(__impl").append(name).append(");").append(Files.LINE_SEPARATOR);
        writer.append("            doDispatchAdd").append(name).append("((").append(getJavaRangeScalar()).append(") elem);").append(Files.LINE_SEPARATOR);
        writer.append("        }").append(Files.LINE_SEPARATOR);
        writer.append("    }").append(Files.LINE_SEPARATOR);
        writer.append(Files.LINE_SEPARATOR);
    }

    /**
     * Writes the standalone implementation of this object scalar property implemented as a same type vector
     *
     * @param writer                The write to use
     * @param inter                 The property interface to implement
     * @param isInTypeRestrictChain Whether this property is in a type restriction chain
     * @throws IOException When writing failed
     */
    private void writeStandaloneObjectScalarInterfaceOnVectorImplSameType(Writer writer, PropertyInterface inter, boolean isInTypeRestrictChain) throws IOException {
        String name = getJavaName();

        writer.append("    @Override").append(Files.LINE_SEPARATOR);
        if (isInTypeRestrictChain)
            writer.append("    public ").append(inter.getJavaRangeScalar()).append(" get").append(name).append("As(").append(inter.getJavaRangeScalar()).append(" type) {").append(Files.LINE_SEPARATOR);
        else
            writer.append("    public ").append(inter.getJavaRangeScalar()).append(" get").append(name).append("() {").append(Files.LINE_SEPARATOR);
        writer.append("        if (__impl)").append(name).append(".isEmpty())").append(Files.LINE_SEPARATOR);
        writer.append("            return ").append(getDefaultValue()).append(";").append(Files.LINE_SEPARATOR);
        writer.append("        return __impl").append(name).append(".get(0);").append(Files.LINE_SEPARATOR);
        writer.append("    }").append(Files.LINE_SEPARATOR);
        writer.append(Files.LINE_SEPARATOR);

        writer.append("    @Override").append(Files.LINE_SEPARATOR);
        writer.append("    public void set").append(name).append("(").append(inter.getJavaRangeScalar()).append(" elem) {").append(Files.LINE_SEPARATOR);
        writer.append("        if (elem == null) {").append(Files.LINE_SEPARATOR);
        writer.append("            if (__impl").append(name).append(".isEmpty())").append(Files.LINE_SEPARATOR);
        writer.append("                return;").append(Files.LINE_SEPARATOR);
        writer.append("            doDispatchRemove").append(name).append("(__impl").append(name).append(".get(__impl").append(name).append(".size() - 1));").append(Files.LINE_SEPARATOR);
        writer.append("        } else if (__impl").append(name).append(".isEmpty()) {").append(Files.LINE_SEPARATOR);
        writer.append("            doDispatchAdd").append(name).append("(elem);").append(Files.LINE_SEPARATOR);
        writer.append("        } else {").append(Files.LINE_SEPARATOR);
        writer.append("            doDispatchRemove").append(name).append("(__impl").append(name).append(".get(__impl").append(name).append(".size() - 1));").append(Files.LINE_SEPARATOR);
        writer.append("            doDispatchAdd").append(name).append("(elem);").append(Files.LINE_SEPARATOR);
        writer.append("        }").append(Files.LINE_SEPARATOR);
        writer.append("    }").append(Files.LINE_SEPARATOR);
        writer.append(Files.LINE_SEPARATOR);
    }

    /**
     * Writes the standalone implementation of this object scalar property implemented as a sub-type vector
     *
     * @param writer                The write to use
     * @param inter                 The property interface to implement
     * @param isInTypeRestrictChain Whether this property is in a type restriction chain
     * @throws IOException When writing failed
     */
    private void writeStandaloneObjectScalarInterfaceOnVectorImplSubType(Writer writer, PropertyInterface inter, boolean isInTypeRestrictChain) throws IOException {
        String name = getJavaName();

        writer.append("    @Override").append(Files.LINE_SEPARATOR);
        if (isInTypeRestrictChain)
            writer.append("    public ").append(inter.getJavaRangeScalar()).append(" get").append(name).append("As(").append(inter.getJavaRangeScalar()).append(" type) {").append(Files.LINE_SEPARATOR);
        else
            writer.append("    public ").append(inter.getJavaRangeScalar()).append(" get").append(name).append("() {").append(Files.LINE_SEPARATOR);
        writer.append("        if (__impl)").append(name).append(".isEmpty())").append(Files.LINE_SEPARATOR);
        writer.append("            return ").append(getDefaultValue()).append(";").append(Files.LINE_SEPARATOR);
        writer.append("        return __impl").append(name).append(".get(0);").append(Files.LINE_SEPARATOR);
        writer.append("    }").append(Files.LINE_SEPARATOR);
        writer.append(Files.LINE_SEPARATOR);

        writer.append("    @Override").append(Files.LINE_SEPARATOR);
        writer.append("    public void set").append(name).append("(").append(inter.getJavaRangeScalar()).append(" elem) {").append(Files.LINE_SEPARATOR);
        writer.append("        if (elem == null) {").append(Files.LINE_SEPARATOR);
        writer.append("            if (__impl").append(name).append(".isEmpty())").append(Files.LINE_SEPARATOR);
        writer.append("                return;").append(Files.LINE_SEPARATOR);
        writer.append("            doDispatchRemove").append(name).append("(__impl").append(name).append(".get(__impl").append(name).append(".size() - 1));").append(Files.LINE_SEPARATOR);
        writer.append("        } else if (__impl").append(name).append(".isEmpty()) {").append(Files.LINE_SEPARATOR);
        writer.append("            if (!(elem instanceof ").append(getJavaRangeScalar()).append("))").append(Files.LINE_SEPARATOR);
        writer.append("                throw new IllegalArgumentException(\"Expected type ").append(getJavaRangeScalar()).append("\");").append(Files.LINE_SEPARATOR);
        writer.append("            doDispatchAdd").append(name).append("((").append(getJavaRangeScalar()).append(") elem);").append(Files.LINE_SEPARATOR);
        writer.append("        } else {").append(Files.LINE_SEPARATOR);
        writer.append("            if (!(elem instanceof ").append(getJavaRangeScalar()).append("))").append(Files.LINE_SEPARATOR);
        writer.append("                throw new IllegalArgumentException(\"Expected type ").append(getJavaRangeScalar()).append("\");").append(Files.LINE_SEPARATOR);
        writer.append("            doDispatchRemove").append(name).append("(__impl").append(name).append(".get(__impl").append(name).append(".size() - 1));").append(Files.LINE_SEPARATOR);
        writer.append("            doDispatchAdd").append(name).append("((").append(getJavaRangeScalar()).append(") elem);").append(Files.LINE_SEPARATOR);
        writer.append("        }").append(Files.LINE_SEPARATOR);
        writer.append("    }").append(Files.LINE_SEPARATOR);
        writer.append(Files.LINE_SEPARATOR);
    }

    /**
     * Writes the standalone implementation of this object vector property implemented as a same type vector
     *
     * @param writer                The write to use
     * @param inter                 The property interface to implement
     * @param isInTypeRestrictChain Whether this property is in a type restriction chain
     * @throws IOException When writing failed
     */
    private void writeStandaloneObjectVectorInterfaceOnVectorImplSameType(Writer writer, PropertyInterface inter, boolean isInTypeRestrictChain) throws IOException {
        String name = getJavaName();

        writer.append("    @Override").append(Files.LINE_SEPARATOR);
        if (isInTypeRestrictChain)
            writer.append("    public Collection<").append(inter.getJavaRangeVector()).append("> getAll").append(name).append("As(").append(inter.getJavaRangeScalar()).append(" type) {").append(Files.LINE_SEPARATOR);
        else
            writer.append("    public Collection<").append(inter.getJavaRangeVector()).append("> getAll").append(name).append("() {").append(Files.LINE_SEPARATOR);
        writer.append("        return Collections.unmodifiableCollection(__impl").append(name).append(");").append(Files.LINE_SEPARATOR);
        writer.append("    }").append(Files.LINE_SEPARATOR);
        writer.append(Files.LINE_SEPARATOR);

        writer.append("    @Override").append(Files.LINE_SEPARATOR);
        writer.append("    public boolean add").append(name).append("(").append(inter.getJavaRangeScalar()).append(" elem) {").append(Files.LINE_SEPARATOR);
        writer.append("        if (elem == null)").append(Files.LINE_SEPARATOR);
        writer.append("            throw new IllegalArgumentException(\"Expected a value\");").append(Files.LINE_SEPARATOR);
        writer.append("        if (__impl").append(name).append(".contains(elem))").append(Files.LINE_SEPARATOR);
        writer.append("            return false;").append(Files.LINE_SEPARATOR);
        writer.append("        doDispatchAdd").append(name).append("(elem);").append(Files.LINE_SEPARATOR);
        writer.append("        return true;").append(Files.LINE_SEPARATOR);
        writer.append("    }").append(Files.LINE_SEPARATOR);
        writer.append(Files.LINE_SEPARATOR);

        writer.append("    @Override").append(Files.LINE_SEPARATOR);
        writer.append("    public boolean remove").append(name).append("(").append(inter.getJavaRangeScalar()).append(" elem) {").append(Files.LINE_SEPARATOR);
        writer.append("        if (elem == null)").append(Files.LINE_SEPARATOR);
        writer.append("            throw new IllegalArgumentException(\"Expected a value\");").append(Files.LINE_SEPARATOR);
        writer.append("        if (!__impl").append(name).append(".contains(elem))").append(Files.LINE_SEPARATOR);
        writer.append("            return false;").append(Files.LINE_SEPARATOR);
        writer.append("        doDispatchRemove").append(name).append("(elem);").append(Files.LINE_SEPARATOR);
        writer.append("        return true;").append(Files.LINE_SEPARATOR);
        writer.append("    }").append(Files.LINE_SEPARATOR);
        writer.append(Files.LINE_SEPARATOR);
    }

    /**
     * Writes the standalone implementation of this object vector property implemented as a sub-type vector
     *
     * @param writer                The write to use
     * @param inter                 The property interface to implement
     * @param isInTypeRestrictChain Whether this property is in a type restriction chain
     * @throws IOException When writing failed
     */
    private void writeStandaloneObjectVectorInterfaceOnVectorImplSubType(Writer writer, PropertyInterface inter, boolean isInTypeRestrictChain) throws IOException {
        String name = getJavaName();

        writer.append("    @Override").append(Files.LINE_SEPARATOR);
        if (isInTypeRestrictChain)
            writer.append("    public Collection<").append(inter.getJavaRangeVector()).append("> getAll").append(name).append("As(").append(inter.getJavaRangeScalar()).append(" type) {").append(Files.LINE_SEPARATOR);
        else
            writer.append("    public Collection<").append(inter.getJavaRangeVector()).append("> getAll").append(name).append("() {").append(Files.LINE_SEPARATOR);
        writer.append("        return (Collection) Collections.unmodifiableCollection(__impl").append(name).append(");").append(Files.LINE_SEPARATOR);
        writer.append("    }").append(Files.LINE_SEPARATOR);
        writer.append(Files.LINE_SEPARATOR);

        writer.append("    @Override").append(Files.LINE_SEPARATOR);
        writer.append("    public boolean add").append(name).append("(").append(inter.getJavaRangeScalar()).append(" elem) {").append(Files.LINE_SEPARATOR);
        writer.append("        if (elem == null)").append(Files.LINE_SEPARATOR);
        writer.append("            throw new IllegalArgumentException(\"Expected a value\");").append(Files.LINE_SEPARATOR);
        writer.append("        if (!(elem instanceof ").append(getJavaRangeScalar()).append("))").append(Files.LINE_SEPARATOR);
        writer.append("            throw new IllegalArgumentException(\"Expected type ").append(getJavaRangeScalar()).append("\");").append(Files.LINE_SEPARATOR);
        writer.append("        if (__impl").append(name).append(".contains((").append(getJavaRangeScalar()).append(") elem))").append(Files.LINE_SEPARATOR);
        writer.append("            return false;").append(Files.LINE_SEPARATOR);
        writer.append("        doDispatchAdd").append(name).append("((").append(getJavaRangeScalar()).append(") elem);").append(Files.LINE_SEPARATOR);
        writer.append("        return true;").append(Files.LINE_SEPARATOR);
        writer.append("    }").append(Files.LINE_SEPARATOR);
        writer.append(Files.LINE_SEPARATOR);

        writer.append("    @Override").append(Files.LINE_SEPARATOR);
        writer.append("    public boolean remove").append(name).append("(").append(inter.getJavaRangeScalar()).append(" elem) {").append(Files.LINE_SEPARATOR);
        writer.append("        if (elem == null)").append(Files.LINE_SEPARATOR);
        writer.append("            throw new IllegalArgumentException(\"Expected a value\");").append(Files.LINE_SEPARATOR);
        writer.append("        if (!(elem instanceof ").append(getJavaRangeScalar()).append("))").append(Files.LINE_SEPARATOR);
        writer.append("            throw new IllegalArgumentException(\"Expected type ").append(getJavaRangeScalar()).append("\");").append(Files.LINE_SEPARATOR);
        writer.append("        if (!__impl").append(name).append(".contains((").append(getJavaRangeScalar()).append(") elem))").append(Files.LINE_SEPARATOR);
        writer.append("            return false;").append(Files.LINE_SEPARATOR);
        writer.append("        doDispatchRemove").append(name).append("((").append(getJavaRangeScalar()).append(") elem);").append(Files.LINE_SEPARATOR);
        writer.append("        return true;").append(Files.LINE_SEPARATOR);
        writer.append("    }").append(Files.LINE_SEPARATOR);
        writer.append(Files.LINE_SEPARATOR);
    }

    /**
     * Writes the standalone implementation of this object vector property implemented as a same type scalar
     *
     * @param writer                The write to use
     * @param inter                 The property interface to implement
     * @param isInTypeRestrictChain Whether this property is in a type restriction chain
     * @throws IOException When writing failed
     */
    private void writeStandaloneObjectVectorInterfaceOnScalarImplSameType(Writer writer, PropertyInterface inter, boolean isInTypeRestrictChain) throws IOException {
        String name = getJavaName();

        writer.append("    @Override").append(Files.LINE_SEPARATOR);
        if (isInTypeRestrictChain)
            writer.append("    public Collection<").append(inter.getJavaRangeVector()).append("> getAll").append(name).append("As(").append(inter.getJavaRangeScalar()).append(" type) {").append(Files.LINE_SEPARATOR);
        else
            writer.append("    public Collection<").append(inter.getJavaRangeVector()).append("> getAll").append(name).append("() {").append(Files.LINE_SEPARATOR);
        writer.append("        if (__impl").append(name).append(" == null)").append(Files.LINE_SEPARATOR);
        writer.append("            return Collections.emptyList();").append(Files.LINE_SEPARATOR);
        writer.append("        return Collections.singletonList(__impl").append(name).append(");").append(Files.LINE_SEPARATOR);
        writer.append("    }").append(Files.LINE_SEPARATOR);
        writer.append(Files.LINE_SEPARATOR);

        writer.append("    @Override").append(Files.LINE_SEPARATOR);
        writer.append("    public boolean add").append(name).append("(").append(inter.getJavaRangeScalar()).append(" elem) {").append(Files.LINE_SEPARATOR);
        writer.append("        if (elem == null)").append(Files.LINE_SEPARATOR);
        writer.append("            throw new IllegalArgumentException(\"Expected a value\");").append(Files.LINE_SEPARATOR);
        writer.append("        if (__impl").append(name).append(" != null)").append(Files.LINE_SEPARATOR);
        writer.append("            throw new IllegalArgumentException(\"Maximum cardinality is 1\");").append(Files.LINE_SEPARATOR);
        writer.append("        doDispatchAdd").append(name).append("(elem);").append(Files.LINE_SEPARATOR);
        writer.append("        return true;").append(Files.LINE_SEPARATOR);
        writer.append("    }").append(Files.LINE_SEPARATOR);
        writer.append(Files.LINE_SEPARATOR);

        writer.append("    @Override").append(Files.LINE_SEPARATOR);
        writer.append("    public boolean remove").append(name).append("(").append(inter.getJavaRangeScalar()).append(" elem) {").append(Files.LINE_SEPARATOR);
        writer.append("        if (elem == null)").append(Files.LINE_SEPARATOR);
        writer.append("            throw new IllegalArgumentException(\"Expected a value\");").append(Files.LINE_SEPARATOR);
        writer.append("        if (__impl").append(name).append(" != elem)").append(Files.LINE_SEPARATOR);
        writer.append("            return false;").append(Files.LINE_SEPARATOR);
        writer.append("        doDispatchRemove").append(name).append("(elem);").append(Files.LINE_SEPARATOR);
        writer.append("        return true;").append(Files.LINE_SEPARATOR);
        writer.append("    }").append(Files.LINE_SEPARATOR);
        writer.append(Files.LINE_SEPARATOR);
    }

    /**
     * Writes the standalone implementation of this object vector property implemented as a sub-type scalar
     *
     * @param writer                The write to use
     * @param inter                 The property interface to implement
     * @param isInTypeRestrictChain Whether this property is in a type restriction chain
     * @throws IOException When writing failed
     */
    private void writeStandaloneObjectVectorInterfaceOnScalarImplSubType(Writer writer, PropertyInterface inter, boolean isInTypeRestrictChain) throws IOException {
        String name = getJavaName();

        writer.append("    @Override").append(Files.LINE_SEPARATOR);
        if (isInTypeRestrictChain)
            writer.append("    public Collection<").append(inter.getJavaRangeVector()).append("> getAll").append(name).append("As(").append(inter.getJavaRangeScalar()).append(" type) {").append(Files.LINE_SEPARATOR);
        else
            writer.append("    public Collection<").append(inter.getJavaRangeVector()).append("> getAll").append(name).append("() {").append(Files.LINE_SEPARATOR);
        writer.append("        if (__impl").append(name).append(" == null)").append(Files.LINE_SEPARATOR);
        writer.append("            return Collections.emptyList();").append(Files.LINE_SEPARATOR);
        writer.append("        return Collections.singletonList(__impl").append(name).append(");").append(Files.LINE_SEPARATOR);
        writer.append("    }").append(Files.LINE_SEPARATOR);
        writer.append(Files.LINE_SEPARATOR);

        writer.append("    @Override").append(Files.LINE_SEPARATOR);
        writer.append("    public boolean add").append(name).append("(").append(inter.getJavaRangeScalar()).append(" elem) {").append(Files.LINE_SEPARATOR);
        writer.append("        if (elem == null)").append(Files.LINE_SEPARATOR);
        writer.append("            throw new IllegalArgumentException(\"Expected a value\");").append(Files.LINE_SEPARATOR);
        writer.append("        if (!(elem instanceof ").append(getJavaRangeScalar()).append("))").append(Files.LINE_SEPARATOR);
        writer.append("            throw new IllegalArgumentException(\"Expected type ").append(getJavaRangeScalar()).append("\");").append(Files.LINE_SEPARATOR);
        writer.append("        if (__impl").append(name).append(" != null)").append(Files.LINE_SEPARATOR);
        writer.append("            throw new IllegalArgumentException(\"Maximum cardinality is 1\");").append(Files.LINE_SEPARATOR);
        writer.append("        doDispatchAdd").append(name).append("((").append(getJavaRangeScalar()).append(") elem);").append(Files.LINE_SEPARATOR);
        writer.append("        return true;").append(Files.LINE_SEPARATOR);
        writer.append("    }").append(Files.LINE_SEPARATOR);
        writer.append(Files.LINE_SEPARATOR);

        writer.append("    @Override").append(Files.LINE_SEPARATOR);
        writer.append("    public boolean remove").append(name).append("(").append(inter.getJavaRangeScalar()).append(" elem) {").append(Files.LINE_SEPARATOR);
        writer.append("        if (elem == null)").append(Files.LINE_SEPARATOR);
        writer.append("            throw new IllegalArgumentException(\"Expected a value\");").append(Files.LINE_SEPARATOR);
        writer.append("        if (!(elem instanceof ").append(getJavaRangeScalar()).append("))").append(Files.LINE_SEPARATOR);
        writer.append("            throw new IllegalArgumentException(\"Expected type ").append(getJavaRangeScalar()).append("\");").append(Files.LINE_SEPARATOR);
        writer.append("        if (__impl").append(name).append(" != elem)").append(Files.LINE_SEPARATOR);
        writer.append("            return false;").append(Files.LINE_SEPARATOR);
        writer.append("        doDispatchRemove").append(name).append("((").append(getJavaRangeScalar()).append(") elem);").append(Files.LINE_SEPARATOR);
        writer.append("        return true;").append(Files.LINE_SEPARATOR);
        writer.append("    }").append(Files.LINE_SEPARATOR);
        writer.append(Files.LINE_SEPARATOR);
    }
}
