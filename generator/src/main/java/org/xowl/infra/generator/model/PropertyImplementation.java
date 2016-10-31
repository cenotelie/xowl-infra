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
     * The reverse implementations
     */
    protected List<PropertyImplementation> implInverses;

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
     * Gets the inverse implementations
     *
     * @return The inverse implementations
     */
    public List<PropertyImplementation> getInverses() {
        return implInverses;
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
        implInverses = new ArrayList<>();
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
                implInverses.add(getImplementationOf(property.getInverse()));
            }
            for (PropertyImplementation ancestor : implAncestors) {
                if (ancestor.getProperty().hasInverse()) {
                    implInverses.add(getImplementationOf(ancestor.getProperty().getInverse()));
                }
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
                if (inter.isVector())
                    writeStandaloneDatatypeVector(writer, inter);
                else
                    writeStandaloneDatatypeScalar(writer, inter);
            }
        } else {
            for (PropertyInterface inter : getInterfaces()) {
                if (inter.isVector()) {
                    writeStandaloneObjectVector(writer, inter, isInTypeRestrictionChain);
                } else {
                    writeStandaloneObjectScalar(writer, inter, isInTypeRestrictionChain);
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
        String name = getJavaName();
        writer.append("    /**").append(Files.LINE_SEPARATOR);
        writer.append("     * The backing data for the property ").append(name).append(Files.LINE_SEPARATOR);
        writer.append("     */").append(Files.LINE_SEPARATOR);
        if (isVector())
            writer.append("    protected List<").append(getJavaRangeVector()).append("> __impl").append(name).append(";").append(Files.LINE_SEPARATOR);
        else
            writer.append("    protected ").append(getJavaRangeScalar()).append(" __impl").append(name).append(";").append(Files.LINE_SEPARATOR);
        writer.append(Files.LINE_SEPARATOR);
    }

    /**
     * Writes the constructor part for this implementation
     *
     * @param writer The writer to use
     * @throws IOException When writing failed
     */
    public void writeStandalineConstructor(Writer writer) throws IOException {
        String name = getJavaName();
        if (isVector())
            writer.append("        this.__impl").append(name).append(" = new ArrayList<>();").append(Files.LINE_SEPARATOR);
        else
            writer.append("        this.__impl").append(name).append(" = ").append(getDefaultValue()).append(";").append(Files.LINE_SEPARATOR);
    }

    /**
     * Writes the standalone implementation of this datatype scalar property
     *
     * @param writer The write to use
     * @param inter  The property interface to implement
     * @throws IOException When writing failed
     */
    private void writeStandaloneDatatypeScalar(Writer writer, PropertyInterface inter) throws IOException {
        String name = getJavaName();

        writer.append("    @Override").append(Files.LINE_SEPARATOR);
        writer.append("    public ").append(inter.getJavaRangeScalar()).append(" get").append(name).append("() {").append(Files.LINE_SEPARATOR);
        if (isVector()) {
            // implemented as a vector
            writer.append("        if (__impl)").append(name).append(".isEmpty())").append(Files.LINE_SEPARATOR);
            writer.append("            return ").append(getDefaultValue()).append(";").append(Files.LINE_SEPARATOR);
            writer.append("        return __impl").append(name).append(".get(0);").append(Files.LINE_SEPARATOR);
        } else {
            // implemented as a scalar
            writer.append("        return __impl").append(name).append(";").append(Files.LINE_SEPARATOR);
        }
        writer.append("    }").append(Files.LINE_SEPARATOR);
        writer.append(Files.LINE_SEPARATOR);

        writer.append("    @Override").append(Files.LINE_SEPARATOR);
        writer.append("    public void set").append(name).append("(").append(inter.getJavaRangeScalar()).append(" elem) {").append(Files.LINE_SEPARATOR);
        if (isVector()) {
            // implemented as a vector
            if (getDefaultValue().equals("null")) {
                // not primitive => null means removal
                writer.append("        __impl").append(name).append(".clear();").append(Files.LINE_SEPARATOR);
                writer.append("        if (elem != null)").append(Files.LINE_SEPARATOR);
                writer.append("            __impl").append(name).append(".add(elem);").append(Files.LINE_SEPARATOR);
            } else {
                // primitive
                writer.append("        __impl").append(name).append(".clear();").append(Files.LINE_SEPARATOR);
                writer.append("        __impl").append(name).append(".add(elem);").append(Files.LINE_SEPARATOR);
            }
        } else {
            // implemented as a scalar
            writer.append("        __impl").append(name).append(" = elem;").append(Files.LINE_SEPARATOR);
        }
        writer.append("    }").append(Files.LINE_SEPARATOR);
        writer.append(Files.LINE_SEPARATOR);
    }

    /**
     * Writes the standalone implementation of this datatype vector property
     *
     * @param writer The write to use
     * @param inter  The property interface to implement
     * @throws IOException When writing failed
     */
    private void writeStandaloneDatatypeVector(Writer writer, PropertyInterface inter) throws IOException {
        String name = getJavaName();

        writer.append("    @Override").append(Files.LINE_SEPARATOR);
        writer.append("    public Collection<").append(inter.getJavaRangeVector()).append("> getAll").append(name).append("() {").append(Files.LINE_SEPARATOR);
        if (isVector()) {
            // implemented as a vector
            writer.append("        return Collections.unmodifiableCollection(__impl").append(name).append(");").append(Files.LINE_SEPARATOR);
        } else {
            // implemented as a scalar
            if (getDefaultValue().equals("null")) {
                // not primitive => null means no value
                writer.append("        if (__impl").append(name).append(" == null)").append(Files.LINE_SEPARATOR);
                writer.append("            return Collections.emptyList();").append(Files.LINE_SEPARATOR);
                writer.append("        return Collections.singletonList(__impl").append(name).append(");").append(Files.LINE_SEPARATOR);
            } else {
                // primitive
                writer.append("        return Collections.singletonList(__impl").append(name).append(");").append(Files.LINE_SEPARATOR);
            }
        }
        writer.append("    }").append(Files.LINE_SEPARATOR);
        writer.append(Files.LINE_SEPARATOR);

        writer.append("    @Override").append(Files.LINE_SEPARATOR);
        writer.append("    public boolean add").append(name).append("(").append(inter.getJavaRangeScalar()).append(" elem) {").append(Files.LINE_SEPARATOR);
        if (isVector()) {
            // implemented as a vector
            if (getCardMax() != Integer.MAX_VALUE) {
                writer.append("        if (__impl").append(name).append(".size() >= ").append(Integer.toString(getCardMax())).append(")").append(Files.LINE_SEPARATOR);
                writer.append("            throw new IllegalArgumentException(\"Maximum cardinality is ").append(Integer.toString(getCardMax())).append("\");").append(Files.LINE_SEPARATOR);
            }
            writer.append("        return __impl").append(name).append(".add(elem);").append(Files.LINE_SEPARATOR);
        } else {
            // implemented as a scalar
            if (getDefaultValue().equals("null")) {
                // not primitive => not null means there is a value
                writer.append("        if (__impl").append(name).append(" != null)").append(Files.LINE_SEPARATOR);
                writer.append("            throw new IllegalArgumentException(\"Maximum cardinality is 1\");").append(Files.LINE_SEPARATOR);
            }
            writer.append("        __impl").append(name).append(" = elem;").append(Files.LINE_SEPARATOR);
            writer.append("        return true;").append(Files.LINE_SEPARATOR);
        }
        writer.append("    }").append(Files.LINE_SEPARATOR);
        writer.append(Files.LINE_SEPARATOR);

        writer.append("    @Override").append(Files.LINE_SEPARATOR);
        writer.append("    public boolean remove").append(name).append("(").append(inter.getJavaRangeScalar()).append(" elem) {").append(Files.LINE_SEPARATOR);
        if (isVector()) {
            // implemented as a vector
            if (inter.getJavaRangeScalar().equals("int"))
                // special case for int due to confusion between remove(int) and remove(Object)
                writer.append("        return __impl").append(name).append(".remove((Integer) elem);").append(Files.LINE_SEPARATOR);
            else
                writer.append("        return __impl").append(name).append(".remove(elem);").append(Files.LINE_SEPARATOR);
        } else {
            // implemented as a scalar
            if (getDefaultValue().equals("null")) {
                // not primitive => not null means there is a value
                writer.append("        if (!Objects.equals(__impl").append(name).append(", elem))").append(Files.LINE_SEPARATOR);
                writer.append("            return false;").append(Files.LINE_SEPARATOR);
            } else {
                writer.append("        if (__impl").append(name).append(" != elem)").append(Files.LINE_SEPARATOR);
                writer.append("            return false;").append(Files.LINE_SEPARATOR);
            }
            writer.append("        __impl").append(name).append(" = ").append(getDefaultValue()).append(";").append(Files.LINE_SEPARATOR);
            writer.append("        return true;").append(Files.LINE_SEPARATOR);
        }
        writer.append("    }").append(Files.LINE_SEPARATOR);
        writer.append(Files.LINE_SEPARATOR);
    }

    /**
     * Writes the standalone implementation of this scalar object property
     *
     * @param writer                The write to use
     * @param inter                 The property interface to implement
     * @param isInTypeRestrictChain Whether this property is in a type restriction chain
     * @throws IOException When writing failed
     */
    private void writeStandaloneObjectScalar(Writer writer, PropertyInterface inter, boolean isInTypeRestrictChain) throws IOException {
        String name = getJavaName();

        writer.append("    @Override").append(Files.LINE_SEPARATOR);
        if (isInTypeRestrictChain)
            writer.append("    public ").append(inter.getJavaRangeScalar()).append(" get").append(name).append("As(").append(inter.getJavaRangeScalar()).append(" type) {").append(Files.LINE_SEPARATOR);
        else
            writer.append("    public ").append(inter.getJavaRangeScalar()).append(" get").append(name).append("() {").append(Files.LINE_SEPARATOR);
        if (isVector()) {
            // implemented as a vector
            writer.append("        if (__impl)").append(name).append(".isEmpty())").append(Files.LINE_SEPARATOR);
            writer.append("            return ").append(getDefaultValue()).append(";").append(Files.LINE_SEPARATOR);
            writer.append("        return __impl").append(name).append(".get(0);").append(Files.LINE_SEPARATOR);
        } else {
            // implemented as a scalar
            writer.append("        return __impl").append(name).append(";").append(Files.LINE_SEPARATOR);
        }
        writer.append("    }").append(Files.LINE_SEPARATOR);
        writer.append(Files.LINE_SEPARATOR);

        writer.append("    @Override").append(Files.LINE_SEPARATOR);
        writer.append("    public void set").append(name).append("(").append(inter.getJavaRangeScalar()).append(" elem) {").append(Files.LINE_SEPARATOR);
        writer.append("        if (elem == null) {").append(Files.LINE_SEPARATOR);
        if (isVector()) {
            // implemented as a vector
            writer.append("            __impl").append(name).append(".clear();").append(Files.LINE_SEPARATOR);
        } else {
            // implemented as a scalar
            writer.append("            __impl").append(name).append(" = null;").append(Files.LINE_SEPARATOR);
        }
        writer.append("            return;").append(Files.LINE_SEPARATOR);
        writer.append("        }").append(Files.LINE_SEPARATOR);
        if (!inter.getJavaRangeScalar().equals(getJavaRangeScalar())) {
            // not the same type
            writer.append("        if (!(elem instanceof ").append(getJavaRangeScalar()).append("))").append(Files.LINE_SEPARATOR);
            writer.append("            throw new IllegalArgumentException(\"Expected type ").append(getJavaRangeScalar()).append("\");").append(Files.LINE_SEPARATOR);
            if (isVector()) {
                // implemented as a vector
                writer.append("        __impl").append(name).append(".clear();").append(Files.LINE_SEPARATOR);
                writer.append("        __impl").append(name).append(".add((").append(getJavaRangeScalar()).append(") elem);").append(Files.LINE_SEPARATOR);
            } else {
                // implemented as a scalar
                writer.append("        __impl").append(name).append(" = (").append(getJavaRangeScalar()).append(") elem;").append(Files.LINE_SEPARATOR);
            }
        } else {
            // the same type
            if (isVector()) {
                // implemented as a vector
                writer.append("        __impl").append(name).append(".clear();").append(Files.LINE_SEPARATOR);
                writer.append("        __impl").append(name).append(".add(elem);").append(Files.LINE_SEPARATOR);
            } else {
                // implemented as a scalar
                writer.append("        __impl").append(name).append(" = elem;").append(Files.LINE_SEPARATOR);
            }
        }
        writer.append("    }").append(Files.LINE_SEPARATOR);
        writer.append(Files.LINE_SEPARATOR);
    }

    /**
     * Writes the standalone implementation of this vector object property
     *
     * @param writer                The write to use
     * @param inter                 The property interface to implement
     * @param isInTypeRestrictChain Whether this property is in a type restriction chain
     * @throws IOException When writing failed
     */
    private void writeStandaloneObjectVector(Writer writer, PropertyInterface inter, boolean isInTypeRestrictChain) throws IOException {
        String name = getJavaName();

        writer.append("    @Override").append(Files.LINE_SEPARATOR);
        if (isInTypeRestrictChain)
            writer.append("    public Collection<").append(inter.getJavaRangeVector()).append("> getAll").append(name).append("As(").append(inter.getJavaRangeScalar()).append(" type) {").append(Files.LINE_SEPARATOR);
        else
            writer.append("    public Collection<").append(inter.getJavaRangeVector()).append("> getAll").append(name).append("() {").append(Files.LINE_SEPARATOR);
        if (isVector()) {
            // implemented as a vector
            writer.append("        return Collections.unmodifiableCollection(__impl").append(name).append(");").append(Files.LINE_SEPARATOR);
        } else {
            // implemented as a scalar
            writer.append("        if (__impl").append(name).append(" == null)").append(Files.LINE_SEPARATOR);
            writer.append("            return Collections.emptyList();").append(Files.LINE_SEPARATOR);
            if (!inter.getJavaRangeScalar().equals(getJavaRangeScalar()))
                writer.append("        return Collections.singletonList((").append(inter.getJavaRangeScalar()).append(") __impl").append(name).append(");").append(Files.LINE_SEPARATOR);
            else
                writer.append("        return Collections.singletonList(__impl").append(name).append(");").append(Files.LINE_SEPARATOR);
        }
        writer.append("    }").append(Files.LINE_SEPARATOR);
        writer.append(Files.LINE_SEPARATOR);

        writer.append("    @Override").append(Files.LINE_SEPARATOR);
        writer.append("    public boolean add").append(name).append("(").append(inter.getJavaRangeScalar()).append(" elem) {").append(Files.LINE_SEPARATOR);
        writer.append("        if (elem == null)").append(Files.LINE_SEPARATOR);
        writer.append("            throw new IllegalArgumentException(\"Expected a value\");").append(Files.LINE_SEPARATOR);
        if (!inter.getJavaRangeScalar().equals(getJavaRangeScalar())) {
            // not the same type
            writer.append("        if (!(elem instanceof ").append(getJavaRangeScalar()).append("))").append(Files.LINE_SEPARATOR);
            writer.append("            throw new IllegalArgumentException(\"Expected type ").append(getJavaRangeScalar()).append("\");").append(Files.LINE_SEPARATOR);
        }
        if (isVector()) {
            // implemented as a vector
            if (getCardMax() != Integer.MAX_VALUE) {
                writer.append("        if (__impl").append(name).append(".size() >= ").append(Integer.toString(getCardMax())).append(")").append(Files.LINE_SEPARATOR);
                writer.append("            throw new IllegalArgumentException(\"Maximum cardinality is ").append(Integer.toString(getCardMax())).append("\");").append(Files.LINE_SEPARATOR);
            }
            if (!inter.getJavaRangeScalar().equals(getJavaRangeScalar())) {
                // not the same type
                writer.append("        return __impl").append(name).append(".add((").append(getJavaRangeScalar()).append(") elem);").append(Files.LINE_SEPARATOR);
            } else {
                writer.append("        return __impl").append(name).append(".add(elem);").append(Files.LINE_SEPARATOR);
            }
        } else {
            // implemented as a scalar
            writer.append("        if (__impl").append(name).append(" != null)").append(Files.LINE_SEPARATOR);
            writer.append("            throw new IllegalArgumentException(\"Maximum cardinality is 1\");").append(Files.LINE_SEPARATOR);
            if (!inter.getJavaRangeScalar().equals(getJavaRangeScalar())) {
                // not the same type
                writer.append("        return __impl").append(name).append(" = (").append(getJavaRangeScalar()).append(") elem;").append(Files.LINE_SEPARATOR);
            } else {
                writer.append("        return __impl").append(name).append(" = elem;").append(Files.LINE_SEPARATOR);
            }
            writer.append("        return true;").append(Files.LINE_SEPARATOR);
        }
        writer.append("    }").append(Files.LINE_SEPARATOR);
        writer.append(Files.LINE_SEPARATOR);

        writer.append("    @Override").append(Files.LINE_SEPARATOR);
        writer.append("    public boolean remove").append(name).append("(").append(inter.getJavaRangeScalar()).append(" elem) {").append(Files.LINE_SEPARATOR);
        writer.append("        if (elem == null)").append(Files.LINE_SEPARATOR);
        writer.append("            throw new IllegalArgumentException(\"Expected a value\");").append(Files.LINE_SEPARATOR);
        if (!inter.getJavaRangeScalar().equals(getJavaRangeScalar())) {
            // not the same type
            writer.append("        if (!(elem instanceof ").append(getJavaRangeScalar()).append("))").append(Files.LINE_SEPARATOR);
            writer.append("            throw new IllegalArgumentException(\"Expected type ").append(getJavaRangeScalar()).append("\");").append(Files.LINE_SEPARATOR);
        }
        if (isVector()) {
            // implemented as a vector
            if (!inter.getJavaRangeScalar().equals(getJavaRangeScalar())) {
                // not the same type
                writer.append("        return __impl").append(name).append(".remove((").append(getJavaRangeScalar()).append(") elem);").append(Files.LINE_SEPARATOR);
            } else {
                writer.append("        return __impl").append(name).append(".remove(elem);").append(Files.LINE_SEPARATOR);
            }
        } else {
            // implemented as a scalar
            writer.append("        if (__impl").append(name).append(" != elem)").append(Files.LINE_SEPARATOR);
            writer.append("            return false;").append(Files.LINE_SEPARATOR);
            writer.append("        __impl").append(name).append(" = null;").append(Files.LINE_SEPARATOR);
            writer.append("        return true;").append(Files.LINE_SEPARATOR);
        }
        writer.append("    }").append(Files.LINE_SEPARATOR);
        writer.append(Files.LINE_SEPARATOR);
    }
}
