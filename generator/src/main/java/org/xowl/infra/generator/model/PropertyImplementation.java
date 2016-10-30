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

import org.xowl.infra.lang.runtime.Literal;
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
    protected List<PropertyModel> implInverses;

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
    public List<PropertyModel> getInverses() {
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
            if (property.hasInverse())
                implInverses.add(property.getInverse());
            for (PropertyImplementation ancestor : implAncestors)
                if (ancestor.getProperty().hasInverse())
                    implInverses.add(ancestor.getProperty().getInverse());
            buildDescendantsOf(this);
        }
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
     * @throws java.io.IOException When an IO error occurs
     */
    public void writeStandalone(Writer writer) throws IOException {
        boolean isInTypeRestrictionChain = false;
        for (PropertyInterface inter : getInterfaces()) {
            if (inter.isInTypeRestrictionChain()) {
                isInTypeRestrictionChain = true;
                break;
            }
        }
        String property = getProperty().getName();
        property = String.valueOf(property.charAt(0)).toUpperCase() + property.substring(1);

        writer.append("    // <editor-fold defaultstate=\"collapsed\" desc=\"Property " + getProperty().getName() + "\">").append(Files.LINE_SEPARATOR);
        if (getDomain() == getParentClass()) {
            if (getProperty().isObjectProperty())
                writeStandalonePropertyClassInterface(writer, getRangeClass().getJavaName());
            else writeStandalonePropertyClassInterface(writer, getRangeDatatype().getJavaType());
        }
        if (getProperty().isObjectProperty()) {
            if (getRange() == null || getRange() == getRangeClass())
                writeStandalonePropertyClassSameType(writer, getRangeClass().getJavaName());
            else
                writeStandalonePropertyClassTranstype(writer, getRange().getJavaName(), getRangeClass().getJavaName());
        } else {
            writeStandalonePropertyClassSameType(writer, getRangeDatatype().getJavaType());
        }
        writer.append("    private ").append(getProperty().getName()).append("_impl data").append(property).append(";").append(Files.LINE_SEPARATOR);
        writer.append("    public ").append(getDomain().getJavaName()).append(".").append(getProperty().getName()).append(" __getImplOf").append(getProperty().getName()).append("() { return data").append(property).append("; }").append(Files.LINE_SEPARATOR);

        if (!getProperty().isObjectProperty())
            writeStandaloneDatatype(writer);
        else {
            for (PropertyInterface inter : getInterfaces())
                writeStandaloneObjectInterface(writer, inter, isInTypeRestrictionChain);
        }
        writer.append("    // </editor-fold>").append(Files.LINE_SEPARATOR);
    }

    /**
     * Writes the interface of the property reification class
     *
     * @param writer The writer to write to
     * @param type   The property's type
     * @throws IOException When an IO error occurs
     */
    private void writeStandalonePropertyClassInterface(Writer writer, String type) throws IOException {
        writer.append("    public static interface " + getProperty().getName());
        writer.append(" {").append(Files.LINE_SEPARATOR);
        writer.append("        boolean check_contains(" + type + " elem);").append(Files.LINE_SEPARATOR);

        writer.append("        boolean user_check_add(" + type + " elem);").append(Files.LINE_SEPARATOR);
        writer.append("        boolean user_check_remove(" + type + " elem);").append(Files.LINE_SEPARATOR);
        writer.append("        boolean user_check_replace(" + type + " oldElem, " + type + "  newElem);").append(Files.LINE_SEPARATOR);
        writer.append("        void user_add(" + type + " elem);").append(Files.LINE_SEPARATOR);
        writer.append("        void user_remove(" + type + " elem);").append(Files.LINE_SEPARATOR);

        writer.append("        boolean inverse_check_add(" + type + " elem);").append(Files.LINE_SEPARATOR);
        writer.append("        boolean inverse_check_remove(" + type + " elem);").append(Files.LINE_SEPARATOR);
        writer.append("        boolean inverse_check_replace(" + type + " oldElem, " + type + "  newElem);").append(Files.LINE_SEPARATOR);
        writer.append("        void inverse_add(" + type + " elem);").append(Files.LINE_SEPARATOR);
        writer.append("        void inverse_remove(" + type + " elem);").append(Files.LINE_SEPARATOR);
        writer.append("    }").append(Files.LINE_SEPARATOR);
    }

    /**
     * Writes the header of the property reification class
     *
     * @param writer The writer to write to
     * @throws IOException When an IO error occurs
     */
    private void writeStandalonePropertyClassHeader(Writer writer) throws java.io.IOException {
        writer.append("    private static class " + getProperty().getName() + "_impl");
        writer.append(" implements " + getDomain().getJavaName() + "." + getProperty().getName() + " {").append(Files.LINE_SEPARATOR);
    }

    /**
     * Writes the content of the property reification class
     *
     * @param writer The writer to write to
     * @param type   The property's type
     * @throws IOException When an IO error occurs
     */
    private void writeStandalonePropertyClassContent(Writer writer, String type) throws IOException {
        writer.append("        private " + getParentClass().getJavaName() + " domain;").append(Files.LINE_SEPARATOR);
        if (!isVector()) {
            writer.append("        private " + type + " data;").append(Files.LINE_SEPARATOR);
            writer.append("        public " + type + " get_raw() { return data; }").append(Files.LINE_SEPARATOR);
            if (!hasValues())
                writer.append("        public " + type + " get() { return data; }").append(Files.LINE_SEPARATOR);
            else {
                if (hasSelf())
                    writer.append("        public " + type + " get() { return domain; }").append(Files.LINE_SEPARATOR);
                else if (getProperty().isObjectProperty()) {
                    InstanceModel instance = hasObjectValues().get(0);
                    ClassModel c = instance.getType();
                    if (c.getJavaName().equals("java.lang.Class")) {
                        String className = c.getPackage().getModel().getBasePackage() + ".";
                        className += c.getPackage().getModel().getModelFor(instance.getOWLIndividual().getInterpretationOf().getContainedBy()).getName() + ".";
                        className += instance.getName();
                        writer.append("        public " + type + " get() { return " + className + ".class; }").append(Files.LINE_SEPARATOR);
                    } else
                        writer.append("        public " + type + " get() { return " + c.getJavaName() + ".get_" + instance.getName() + "(); }").append(Files.LINE_SEPARATOR);
                } else {
                    Literal value = hasDataValues().get(0);
                    DatatypeModel datatype = getProperty().getModelFor(value.getMemberOf());
                    String data = datatype.getToValue("\"" + value.getLexicalValue() + "\"");
                    writer.append("        public " + type + " get() { return " + data + "; }").append(Files.LINE_SEPARATOR);
                }
            }
        } else {
            writer.append("        private java.util.List<" + type + "> data;").append(Files.LINE_SEPARATOR);
            writer.append("        public java.util.Collection<" + type + "> get_raw() { return new java.util.ArrayList<" + type + ">(data); }").append(Files.LINE_SEPARATOR);
            if (!hasValues())
                writer.append("        public java.util.Collection<" + type + "> get() { return new java.util.ArrayList<" + type + ">(data); }").append(Files.LINE_SEPARATOR);
            else {
                writer.append("        public java.util.Collection<" + type + "> get() {").append(Files.LINE_SEPARATOR);
                writer.append("            java.util.List<" + type + "> temp = new java.util.ArrayList<" + type + ">(data);").append(Files.LINE_SEPARATOR);
                if (hasSelf())
                    writer.append("            temp.add(domain);").append(Files.LINE_SEPARATOR);
                else if (getProperty().isObjectProperty()) {
                    for (InstanceModel instance : hasObjectValues()) {
                        ClassModel c = instance.getType();
                        if (c.getJavaName().equals("java.lang.Class")) {
                            String className = c.getPackage().getModel().getBasePackage() + ".";
                            className += c.getPackage().getModel().getModelFor(instance.getOWLIndividual().getInterpretationOf().getContainedBy()).getName() + ".";
                            className += instance.getName();
                            writer.append("            temp.add(" + className + ".class);").append(Files.LINE_SEPARATOR);
                        } else
                            writer.append("            temp.add(" + c.getJavaName() + ".get_" + instance.getName() + "());").append(Files.LINE_SEPARATOR);
                    }
                } else {
                    for (Literal value : hasDataValues()) {
                        DatatypeModel datatype = getProperty().getModelFor(value.getMemberOf());
                        String data = datatype.getToValue("\"" + value.getLexicalValue() + "\"");
                        writer.append("            temp.add(" + data + ");").append(Files.LINE_SEPARATOR);
                    }
                }
                writer.append("            return temp;").append(Files.LINE_SEPARATOR);
                writer.append("        }").append(Files.LINE_SEPARATOR);
            }
        }
    }

    /**
     * Writes the CheckCard of the property reification class
     *
     * @param writer The writer to write to
     * @throws IOException When an IO error occurs
     */
    private void writeStandalonePropertyClassCheckCard(Writer writer) throws IOException {
        if (!isVector()) {
            writer.append("        private boolean check_card(int modifier) {").append(Files.LINE_SEPARATOR);
            writer.append("            int card = modifier + " + getValuesCount() + ";").append(Files.LINE_SEPARATOR);
            writer.append("            if (data != null) card++;").append(Files.LINE_SEPARATOR);
            writer.append("            return (card >= " + Integer.toString(getCardMin()) + " && card <= " + Integer.toString(getCardMax()) + ");").append(Files.LINE_SEPARATOR);
            writer.append("        }").append(Files.LINE_SEPARATOR);
        } else {
            writer.append("        private boolean check_card(int modifier) {").append(Files.LINE_SEPARATOR);
            writer.append("            int card = data.size() + " + getValuesCount() + " + modifier;").append(Files.LINE_SEPARATOR);
            writer.append("            return (card >= " + Integer.toString(getCardMin()) + " && card <= " + Integer.toString(getCardMax()) + ");").append(Files.LINE_SEPARATOR);
            writer.append("        }").append(Files.LINE_SEPARATOR);
        }
    }

    /**
     * Writes the SimpleCheckAdd of the property reification class
     *
     * @param writer The writer to write to
     * @throws IOException When an IO error occurs
     */
    private void writeStandalonePropertyClassSimpleCheckAdd(Writer writer) throws java.io.IOException {
        writer.append("            if (check_contains(elem)) return false;").append(Files.LINE_SEPARATOR);
        writer.append("            if (!check_card(1)) return false;").append(Files.LINE_SEPARATOR);
        if (getProperty().isObjectProperty() && getProperty().isAsymmetric() && getRangeClass().isCompatibleWith(getDomain()) && getParentClass().isCompatibleWith(getRange()))
            writer.append("            if (elem.__getImplOf" + getProperty().getName() + "().check_contains(domain)) return false;").append(Files.LINE_SEPARATOR);
        if (getProperty().isObjectProperty() && getProperty().isIrreflexive() && getParentClass().isCompatibleWith(getRangeClass()))
            writer.append("            if (elem == domain) return false;").append(Files.LINE_SEPARATOR);
        writer.append("            return true;").append(Files.LINE_SEPARATOR);
    }

    private void writeStandalonePropertyClassSimpleCheckRemove(Writer writer) throws IOException {
        writer.append("            if (!check_contains(elem)) return false;").append(Files.LINE_SEPARATOR);
        writer.append("            if (!check_card(-1)) return false;").append(Files.LINE_SEPARATOR);
        if (getProperty().isObjectProperty() && getProperty().isReflexive() || hasSelf())
            writer.append("            if (elem == domain) return false;").append(Files.LINE_SEPARATOR);
        writer.append("            return true;").append(Files.LINE_SEPARATOR);
    }

    private void writeStandalonePropertyClassSimpleCheckReplace(Writer writer) throws IOException {
        writer.append("            if (check_contains(newElem)) return false;").append(Files.LINE_SEPARATOR);
        writer.append("            if (!check_contains(oldElem)) return false;").append(Files.LINE_SEPARATOR);
        if (getProperty().isObjectProperty() && getProperty().isAsymmetric() && getRangeClass().isCompatibleWith(getDomain()) && getParentClass().isCompatibleWith(getRange()))
            writer.append("            if (newElem.__getImplOf" + getProperty().getName() + "().check_contains(domain)) return false;").append(Files.LINE_SEPARATOR);
        if (getProperty().isObjectProperty() && getProperty().isReflexive() || hasSelf())
            writer.append("            if (oldElem == domain) return false;").append(Files.LINE_SEPARATOR);
        if (getProperty().isObjectProperty() && getProperty().isIrreflexive() && getParentClass().isCompatibleWith(getRangeClass()))
            writer.append("            if (newElem == domain) return false;").append(Files.LINE_SEPARATOR);
        writer.append("            return true;").append(Files.LINE_SEPARATOR);
    }

    private void writeStandalonePropertyClassSimpleDoAdd(Writer writer) throws IOException {
        if (!isVector())
            writer.append("            data = elem;").append(Files.LINE_SEPARATOR);
        else
            writer.append("            data.add(elem);").append(Files.LINE_SEPARATOR);
    }

    private void writeStandalonePropertyClassSimpleDoRemove(Writer writer) throws IOException {
        if (!isVector())
            writer.append("            data = null;").append(Files.LINE_SEPARATOR);
        else
            writer.append("            data.remove(elem);").append(Files.LINE_SEPARATOR);
    }

    private void writeStandalonePropertyClassSimple(Writer writer, String type) throws IOException {
        writer.append("        public boolean simple_check_add(" + type + " elem) {").append(Files.LINE_SEPARATOR);
        writeStandalonePropertyClassSimpleCheckAdd(writer);
        writer.append("        }").append(Files.LINE_SEPARATOR);
        writer.append("        public boolean simple_check_remove(" + type + " elem) {").append(Files.LINE_SEPARATOR);
        writeStandalonePropertyClassSimpleCheckRemove(writer);
        writer.append("        }").append(Files.LINE_SEPARATOR);
        writer.append("        public boolean simple_check_replace(" + type + " oldElem, " + type + "  newElem) {").append(Files.LINE_SEPARATOR);
        writeStandalonePropertyClassSimpleCheckReplace(writer);
        writer.append("        }").append(Files.LINE_SEPARATOR);
        writer.append("        public void simple_add(" + type + " elem) {").append(Files.LINE_SEPARATOR);
        writeStandalonePropertyClassSimpleDoAdd(writer);
        writer.append("        }").append(Files.LINE_SEPARATOR);
        writer.append("        public void simple_remove(" + type + " elem) {").append(Files.LINE_SEPARATOR);
        writeStandalonePropertyClassSimpleDoRemove(writer);
        writer.append("        }").append(Files.LINE_SEPARATOR);
    }

    private void writeStandalonePropertyClassTreeCheckAdd(Writer writer) throws IOException {
        writer.append("            if (!simple_check_add(elem)) return false;").append(Files.LINE_SEPARATOR);
        for (PropertyImplementation ancestor : getAncestors())
            writer.append("            if (!domain." + ancestor.getProperty().getName() + "_data.simple_check_add(elem)) return false;").append(Files.LINE_SEPARATOR);
        writer.append("            return true;").append(Files.LINE_SEPARATOR);
    }

    private void writeStandalonePropertyClassTreeCheckRemove(Writer writer) throws IOException {
        writer.append("            if (!simple_check_remove(elem)) return false;").append(Files.LINE_SEPARATOR);
        for (PropertyImplementation ancestor : getAncestors())
            writer.append("            if (!domain." + ancestor.getProperty().getName() + "_data.simple_check_remove(elem)) return false;").append(Files.LINE_SEPARATOR);
        writer.append("            return true;").append(Files.LINE_SEPARATOR);
    }

    private void writeStandalonePropertyClassTreeCheckReplace(Writer writer) throws IOException {
        writer.append("            if (!simple_check_replace(oldElem, newElem)) return false;").append(Files.LINE_SEPARATOR);
        for (PropertyImplementation ancestor : getAncestors())
            writer.append("            if (!domain." + ancestor.getProperty().getName() + "_data.simple_check_replace(oldElem, newElem)) return false;").append(Files.LINE_SEPARATOR);
        writer.append("            return true;").append(Files.LINE_SEPARATOR);
    }

    private void writeStandalonePropertyClassTreeDoAdd(Writer writer) throws IOException {
        writer.append("            simple_add(elem);").append(Files.LINE_SEPARATOR);
        for (PropertyImplementation ancestor : getAncestors())
            writer.append("            domain." + ancestor.getProperty().getName() + "_data.simple_add(elem);").append(Files.LINE_SEPARATOR);
    }

    private void writeStandalonePropertyClassTreeDoRemove(Writer writer) throws IOException {
        writer.append("            simple_remove(elem);").append(Files.LINE_SEPARATOR);
        for (PropertyImplementation ancestor : getAncestors())
            writer.append("            domain." + ancestor.getProperty().getName() + "_data.simple_remove(elem);").append(Files.LINE_SEPARATOR);
    }

    private void writeStandalonePropertyClassTree(Writer writer, String type) throws IOException {
        writer.append("        private boolean tree_check_add(" + type + " elem) {").append(Files.LINE_SEPARATOR);
        writeStandalonePropertyClassTreeCheckAdd(writer);
        writer.append("        }").append(Files.LINE_SEPARATOR);
        writer.append("        private boolean tree_check_remove(" + type + " elem) {").append(Files.LINE_SEPARATOR);
        writeStandalonePropertyClassTreeCheckRemove(writer);
        writer.append("        }").append(Files.LINE_SEPARATOR);
        writer.append("        private boolean tree_check_replace(" + type + " oldElem, " + type + "  newElem) {").append(Files.LINE_SEPARATOR);
        writeStandalonePropertyClassTreeCheckReplace(writer);
        writer.append("        }").append(Files.LINE_SEPARATOR);
        writer.append("        private void tree_add(" + type + " elem) {").append(Files.LINE_SEPARATOR);
        writeStandalonePropertyClassTreeDoAdd(writer);
        writer.append("        }").append(Files.LINE_SEPARATOR);
        writer.append("        private void tree_remove(" + type + " elem) {").append(Files.LINE_SEPARATOR);
        writeStandalonePropertyClassTreeDoRemove(writer);
        writer.append("        }").append(Files.LINE_SEPARATOR);
    }

    private void writeStandalonePropertyClassUserCheckAdd(Writer writer) throws IOException {
        // Try delegating to a descendant
        for (PropertyImplementation descandant : getDescendants()) {
            writer.append("            if (elem instanceof " + descandant.getRangeClass().getJavaName() + ")").append(Files.LINE_SEPARATOR);
            writer.append("                return domain." + descandant.getProperty().getName() + "_data.user_check_add((" + descandant.getRangeClass().getJavaName() + ")elem);").append(Files.LINE_SEPARATOR);
        }
        // Dispatch to inverse
        if (!getInverses().isEmpty())
            writer.append("            if (!elem.__getImplOf" + getInverses().get(0).getName() + "().inverse_check_add(domain)) return false;").append(Files.LINE_SEPARATOR);
        writer.append("            return tree_check_add(elem);").append(Files.LINE_SEPARATOR);
    }

    private void writeStandalonePropertyClassUserCheckRemove(Writer writer) throws IOException {
        // Try delegating to a descendant
        for (PropertyImplementation descandant : getDescendants()) {
            writer.append("            if (elem instanceof " + descandant.getRangeClass().getJavaName() + ")").append(Files.LINE_SEPARATOR);
            writer.append("                return domain." + descandant.getProperty().getName() + "_data.user_check_remove((" + descandant.getRangeClass().getJavaName() + ")elem);").append(Files.LINE_SEPARATOR);
        }
        // Dispatch to inverse
        if (!getInverses().isEmpty())
            writer.append("            if (!elem.__getImplOf" + getInverses().get(0).getName() + "().inverse_check_remove(domain)) return false;").append(Files.LINE_SEPARATOR);
        writer.append("            return tree_check_remove(elem);").append(Files.LINE_SEPARATOR);
    }

    private void writeStandalonePropertyClassUserCheckReplace(Writer writer) throws IOException {
        // Try delegating to a descendant
        for (PropertyImplementation descandant : getDescendants()) {
            writer.append("            if (newElem instanceof " + descandant.getRangeClass().getJavaName() + ")").append(Files.LINE_SEPARATOR);
            writer.append("                return domain." + descandant.getProperty().getName() + "_data.user_check_replace((" + descandant.getRangeClass().getJavaName() + ")oldElem, (" + descandant.getRangeClass().getJavaName() + ")newElem);").append(Files.LINE_SEPARATOR);
        }
        // Dispatch to inverse
        if (!getInverses().isEmpty()) {
            writer.append("            if (!oldElem.__getImplOf" + getInverses().get(0).getName() + "().inverse_check_remove(domain)) return false;").append(Files.LINE_SEPARATOR);
            writer.append("            if (!newElem.__getImplOf" + getInverses().get(0).getName() + "().inverse_check_add(domain)) return false;").append(Files.LINE_SEPARATOR);
        }
        writer.append("            return tree_check_replace(oldElem, newElem);").append(Files.LINE_SEPARATOR);
    }

    private void writeStandalonePropertyClassUserDoAdd(Writer writer) throws IOException {
        // Try delegating to a descendant
        for (PropertyImplementation descandant : getDescendants()) {
            writer.append("            if (elem instanceof " + descandant.getRangeClass().getJavaName() + ") {").append(Files.LINE_SEPARATOR);
            writer.append("                domain." + descandant.getProperty().getName() + "_data.user_add((" + descandant.getRangeClass().getJavaName() + ")elem);").append(Files.LINE_SEPARATOR);
            writer.append("                return;").append(Files.LINE_SEPARATOR);
            writer.append("            }").append(Files.LINE_SEPARATOR);
        }
        // Dispatch to inverse
        if (!getInverses().isEmpty())
            writer.append("            elem.__getImplOf" + getInverses().get(0).getName() + "().inverse_add(domain);").append(Files.LINE_SEPARATOR);
        writer.append("            tree_add(elem);").append(Files.LINE_SEPARATOR);
    }

    private void writeStandalonePropertyClassUserDoRemove(Writer writer) throws IOException {
        // Try delegating to a descendant
        for (PropertyImplementation descandant : getDescendants()) {
            writer.append("            if (elem instanceof " + descandant.getRangeClass().getJavaName() + ") {").append(Files.LINE_SEPARATOR);
            writer.append("                domain." + descandant.getProperty().getName() + "_data.user_remove((" + descandant.getRangeClass().getJavaName() + ")elem);").append(Files.LINE_SEPARATOR);
            writer.append("                return;").append(Files.LINE_SEPARATOR);
            writer.append("            }").append(Files.LINE_SEPARATOR);
        }
        // Dispatch to inverse
        if (!getInverses().isEmpty())
            writer.append("            elem.__getImplOf" + getInverses().get(0).getName() + "().inverse_remove(domain);").append(Files.LINE_SEPARATOR);
        writer.append("            tree_remove(elem);").append(Files.LINE_SEPARATOR);
    }

    private void writeStandalonePropertyClassInverseCheckAdd(Writer writer) throws IOException {
        // Try delegating to a descendant
        for (PropertyImplementation descandant : getDescendants()) {
            writer.append("            if (elem instanceof " + descandant.getRangeClass().getJavaName() + ")").append(Files.LINE_SEPARATOR);
            writer.append("                return domain." + descandant.getProperty().getName() + "_data.inverse_check_add((" + descandant.getRangeClass().getJavaName() + ")elem);").append(Files.LINE_SEPARATOR);
        }
        writer.append("            return tree_check_add(elem);").append(Files.LINE_SEPARATOR);
    }

    private void writeStandalonePropertyClassInverseCheckRemove(Writer writer) throws IOException {
        // Try delegating to a descendant
        for (PropertyImplementation descandant : getDescendants()) {
            writer.append("            if (elem instanceof " + descandant.getRangeClass().getJavaName() + ")").append(Files.LINE_SEPARATOR);
            writer.append("                return domain." + descandant.getProperty().getName() + "_data.inverse_check_remove((" + descandant.getRangeClass().getJavaName() + ")elem);").append(Files.LINE_SEPARATOR);
        }
        writer.append("            return tree_check_remove(elem);").append(Files.LINE_SEPARATOR);
    }

    private void writeStandalonePropertyClassInverseCheckReplace(Writer writer) throws IOException {
        // Try delegating to a descendant
        for (PropertyImplementation descandant : getDescendants()) {
            writer.append("            if (newElem instanceof " + descandant.getRangeClass().getJavaName() + ")").append(Files.LINE_SEPARATOR);
            writer.append("                return domain." + descandant.getProperty().getName() + "_data.inverse_check_replace((" + descandant.getRangeClass().getJavaName() + ")oldElem, (" + descandant.getRangeClass().getJavaName() + ")newElem);").append(Files.LINE_SEPARATOR);
        }
        writer.append("            return tree_check_replace(oldElem, newElem);").append(Files.LINE_SEPARATOR);
    }

    private void writeStandalonePropertyClassInverseDoAdd(Writer writer) throws IOException {
        // Try delegating to a descendant
        for (PropertyImplementation descandant : getDescendants()) {
            writer.append("            if (elem instanceof " + descandant.getRangeClass().getJavaName() + ") {").append(Files.LINE_SEPARATOR);
            writer.append("                domain." + descandant.getProperty().getName() + "_data.inverse_add((" + descandant.getRangeClass().getJavaName() + ")elem);").append(Files.LINE_SEPARATOR);
            writer.append("                return;").append(Files.LINE_SEPARATOR);
            writer.append("            }").append(Files.LINE_SEPARATOR);
        }
        writer.append("            tree_add(elem);").append(Files.LINE_SEPARATOR);
    }

    private void writeStandalonePropertyClassInverseDoRemove(Writer writer) throws IOException {
        // Try delegating to a descendant
        for (PropertyImplementation descandant : getDescendants()) {
            writer.append("            if (elem instanceof " + descandant.getRangeClass().getJavaName() + ") {").append(Files.LINE_SEPARATOR);
            writer.append("                domain." + descandant.getProperty().getName() + "_data.inverse_remove((" + descandant.getRangeClass().getJavaName() + ")elem);").append(Files.LINE_SEPARATOR);
            writer.append("                return;").append(Files.LINE_SEPARATOR);
            writer.append("            }").append(Files.LINE_SEPARATOR);
        }
        writer.append("            tree_remove(elem);").append(Files.LINE_SEPARATOR);
    }

    private void writeStandalonePropertyClassConstructor(Writer writer, String type) throws IOException {
        writer.append("        public " + getProperty().getName() + "_impl(" + getParentClass().getJavaName() + " domain) {").append(Files.LINE_SEPARATOR);
        writer.append("            this.domain = domain;").append(Files.LINE_SEPARATOR);
        if (isVector())
            writer.append("            this.data = new java.util.ArrayList<" + type + ">();").append(Files.LINE_SEPARATOR);
        writer.append("        }").append(Files.LINE_SEPARATOR);
    }

    private void writeStandalonePropertyClassSameType(Writer writer, String type) throws IOException {
        writeStandalonePropertyClassHeader(writer);
        writeStandalonePropertyClassContent(writer, type);

        writeStandalonePropertyClassCheckCard(writer);
        if (!isVector()) {
            if (getProperty().isObjectProperty())
                writer.append("        @Override public boolean check_contains(" + type + " elem) { return (data == elem); }").append(Files.LINE_SEPARATOR);
            else
                writer.append("        @Override public boolean check_contains(" + type + " elem) { return data.equals(elem); }").append(Files.LINE_SEPARATOR);
        } else
            writer.append("        @Override public boolean check_contains(" + type + " elem) { return (data.contains(elem)); }").append(Files.LINE_SEPARATOR);

        writeStandalonePropertyClassSimple(writer, type);
        writeStandalonePropertyClassTree(writer, type);

        writer.append("        @Override public boolean user_check_add(" + type + " elem) {").append(Files.LINE_SEPARATOR);
        writeStandalonePropertyClassUserCheckAdd(writer);
        writer.append("        }").append(Files.LINE_SEPARATOR);
        writer.append("        @Override public boolean user_check_remove(" + type + " elem) {").append(Files.LINE_SEPARATOR);
        writeStandalonePropertyClassUserCheckRemove(writer);
        writer.append("        }").append(Files.LINE_SEPARATOR);
        writer.append("        @Override public boolean user_check_replace(" + type + " oldElem, " + type + "  newElem) {").append(Files.LINE_SEPARATOR);
        writeStandalonePropertyClassUserCheckReplace(writer);
        writer.append("        }").append(Files.LINE_SEPARATOR);
        writer.append("        @Override public void user_add(" + type + " elem) {").append(Files.LINE_SEPARATOR);
        writeStandalonePropertyClassUserDoAdd(writer);
        writer.append("        }").append(Files.LINE_SEPARATOR);
        writer.append("        @Override public void user_remove(" + type + " elem) {").append(Files.LINE_SEPARATOR);
        writeStandalonePropertyClassUserDoRemove(writer);
        writer.append("        }").append(Files.LINE_SEPARATOR);

        writer.append("        @Override public boolean inverse_check_add(" + type + " elem) {").append(Files.LINE_SEPARATOR);
        writeStandalonePropertyClassInverseCheckAdd(writer);
        writer.append("        }").append(Files.LINE_SEPARATOR);
        writer.append("        @Override public boolean inverse_check_remove(" + type + " elem) {").append(Files.LINE_SEPARATOR);
        writeStandalonePropertyClassInverseCheckRemove(writer);
        writer.append("        }").append(Files.LINE_SEPARATOR);
        writer.append("        @Override public boolean inverse_check_replace(" + type + " oldElem, " + type + "  newElem) {").append(Files.LINE_SEPARATOR);
        writeStandalonePropertyClassInverseCheckReplace(writer);
        writer.append("        }").append(Files.LINE_SEPARATOR);
        writer.append("        @Override public void inverse_add(" + type + " elem) {").append(Files.LINE_SEPARATOR);
        writeStandalonePropertyClassInverseDoAdd(writer);
        writer.append("        }").append(Files.LINE_SEPARATOR);
        writer.append("        @Override public void inverse_remove(" + type + " elem) {").append(Files.LINE_SEPARATOR);
        writeStandalonePropertyClassInverseDoRemove(writer);
        writer.append("        }").append(Files.LINE_SEPARATOR);

        writeStandalonePropertyClassConstructor(writer, type);
        writer.append("    }").append(Files.LINE_SEPARATOR);
    }

    private void writeStandalonePropertyClassTranstype(Writer writer, String baseType, String implType) throws IOException {
        writeStandalonePropertyClassHeader(writer);
        writeStandalonePropertyClassContent(writer, implType);

        writeStandalonePropertyClassCheckCard(writer);
        if (!isVector()) {
            if (getProperty().isObjectProperty())
                writer.append("        public boolean check_contains(" + implType + " elem) { return (data == elem); }").append(Files.LINE_SEPARATOR);
            else
                writer.append("        public boolean check_contains(" + implType + " elem) { return data.equals(elem); }").append(Files.LINE_SEPARATOR);
            writer.append("        @Override public boolean check_contains(" + baseType + " elem) {").append(Files.LINE_SEPARATOR);
            writer.append("            if (!(elem instanceof " + implType + ")) return false;").append(Files.LINE_SEPARATOR);
            writer.append("            return (data == elem);").append(Files.LINE_SEPARATOR);
            writer.append("        }").append(Files.LINE_SEPARATOR);
        } else {
            writer.append("        public boolean check_contains(" + implType + " elem) { return (data.contains(elem)); }").append(Files.LINE_SEPARATOR);
            writer.append("        @Override public boolean check_contains(" + baseType + " elem) {").append(Files.LINE_SEPARATOR);
            writer.append("            if (!(elem instanceof " + implType + ")) return false;").append(Files.LINE_SEPARATOR);
            writer.append("            return (data.contains((" + implType + ")elem));").append(Files.LINE_SEPARATOR);
            writer.append("        }").append(Files.LINE_SEPARATOR);
        }

        writeStandalonePropertyClassSimple(writer, implType);
        writeStandalonePropertyClassTree(writer, implType);

        writer.append("        public boolean user_check_add(" + implType + " elem) {").append(Files.LINE_SEPARATOR);
        writeStandalonePropertyClassUserCheckAdd(writer);
        writer.append("        }").append(Files.LINE_SEPARATOR);
        writer.append("        public boolean user_check_remove(" + implType + " elem) {").append(Files.LINE_SEPARATOR);
        writeStandalonePropertyClassUserCheckRemove(writer);
        writer.append("        }").append(Files.LINE_SEPARATOR);
        writer.append("        public boolean user_check_replace(" + implType + " oldElem, " + implType + "  newElem) {").append(Files.LINE_SEPARATOR);
        writeStandalonePropertyClassUserCheckReplace(writer);
        writer.append("        }").append(Files.LINE_SEPARATOR);
        writer.append("        public void user_add(" + implType + " elem) {").append(Files.LINE_SEPARATOR);
        writeStandalonePropertyClassUserDoAdd(writer);
        writer.append("        }").append(Files.LINE_SEPARATOR);
        writer.append("        public void user_remove(" + implType + " elem) {").append(Files.LINE_SEPARATOR);
        writeStandalonePropertyClassUserDoRemove(writer);
        writer.append("        }").append(Files.LINE_SEPARATOR);
        writer.append("        @Override public boolean user_check_add(" + baseType + " elem) { return user_check_add((" + implType + ")elem); }").append(Files.LINE_SEPARATOR);
        writer.append("        @Override public boolean user_check_remove(" + baseType + " elem) { return user_check_remove((" + implType + ")elem); }").append(Files.LINE_SEPARATOR);
        writer.append("        @Override public boolean user_check_replace(" + baseType + " oldElem, " + baseType + "  newElem) { return user_check_replace((" + implType + ")oldElem, (" + implType + ")newElem); }").append(Files.LINE_SEPARATOR);
        writer.append("        @Override public void user_add(" + baseType + " elem) { user_add((" + implType + ")elem); }").append(Files.LINE_SEPARATOR);
        writer.append("        @Override public void user_remove(" + baseType + " elem) { user_remove((" + implType + ")elem); }").append(Files.LINE_SEPARATOR);

        writer.append("        public boolean inverse_check_add(" + implType + " elem) {").append(Files.LINE_SEPARATOR);
        writeStandalonePropertyClassInverseCheckAdd(writer);
        writer.append("        }").append(Files.LINE_SEPARATOR);
        writer.append("        public boolean inverse_check_remove(" + implType + " elem) {").append(Files.LINE_SEPARATOR);
        writeStandalonePropertyClassInverseCheckRemove(writer);
        writer.append("        }").append(Files.LINE_SEPARATOR);
        writer.append("        public boolean inverse_check_replace(" + implType + " oldElem, " + implType + "  newElem) {").append(Files.LINE_SEPARATOR);
        writeStandalonePropertyClassInverseCheckReplace(writer);
        writer.append("        }").append(Files.LINE_SEPARATOR);
        writer.append("        public void inverse_add(" + implType + " elem) {").append(Files.LINE_SEPARATOR);
        writeStandalonePropertyClassInverseDoAdd(writer);
        writer.append("        }").append(Files.LINE_SEPARATOR);
        writer.append("        public void inverse_remove(" + implType + " elem) {").append(Files.LINE_SEPARATOR);
        writeStandalonePropertyClassInverseDoRemove(writer);
        writer.append("        }").append(Files.LINE_SEPARATOR);
        writer.append("        @Override public boolean inverse_check_add(" + baseType + " elem) { return inverse_check_add((" + implType + ")elem); }").append(Files.LINE_SEPARATOR);
        writer.append("        @Override public boolean inverse_check_remove(" + baseType + " elem) { return inverse_check_remove((" + implType + ")elem); }").append(Files.LINE_SEPARATOR);
        writer.append("        @Override public boolean inverse_check_replace(" + baseType + " oldElem, " + baseType + "  newElem) { return inverse_check_replace((" + implType + ")oldElem, (" + implType + ")newElem); }").append(Files.LINE_SEPARATOR);
        writer.append("        @Override public void inverse_add(" + baseType + " elem) { inverse_add((" + implType + ")elem); }").append(Files.LINE_SEPARATOR);
        writer.append("        @Override public void inverse_remove(" + baseType + " elem) { inverse_remove((" + implType + ")elem); }").append(Files.LINE_SEPARATOR);

        writeStandalonePropertyClassConstructor(writer, implType);
        writer.append("    }").append(Files.LINE_SEPARATOR);
    }

    private void writeStandaloneDatatype(Writer writer) throws IOException {
        for (PropertyInterface inter : getInterfaces()) {
            if (inter.isVector())
                writeStandaloneDatatypeInterfaceVector(writer, inter);
            else
                writeStandaloneDatatypeInterfaceScalar(writer, inter);
        }
    }

    private void writeStandaloneDatatypeInterfaceScalar(Writer writer, PropertyInterface inter) throws IOException {
        String type = getRepresentationRange();
        String property = inter.getProperty().getName();
        property = String.valueOf(property.charAt(0)).toUpperCase() + property.substring(1);
        writer.append("    public boolean set" + property + "(" + type + " elem) {").append(Files.LINE_SEPARATOR);
        writer.append("        data" + property + ".simple_add(elem);").append(Files.LINE_SEPARATOR);
        writer.append("        return true;").append(Files.LINE_SEPARATOR);
        writer.append("    }").append(Files.LINE_SEPARATOR);
        writer.append("    public " + type + " get" + property + "() { return data" + property + ".get(); }").append(Files.LINE_SEPARATOR);
    }

    private void writeStandaloneDatatypeInterfaceVector(Writer writer, PropertyInterface inter) throws IOException {
        String type = getRepresentationRange();
        String property = inter.getProperty().getName();
        property = String.valueOf(property.charAt(0)).toUpperCase() + property.substring(1);
        writer.append("    public boolean add" + property + "(" + type + " elem) {").append(Files.LINE_SEPARATOR);
        writer.append("        if (!data" + property + ".user_check_add(elem)) return false;").append(Files.LINE_SEPARATOR);
        writer.append("        data" + property + ".user_add(elem);").append(Files.LINE_SEPARATOR);
        writer.append("        return true;").append(Files.LINE_SEPARATOR);
        writer.append("    }").append(Files.LINE_SEPARATOR);
        writer.append("    public boolean remove" + property + "(" + type + " elem) {").append(Files.LINE_SEPARATOR);
        writer.append("        if (!data" + property + ".user_check_remove(elem)) return false;").append(Files.LINE_SEPARATOR);
        writer.append("        data" + property + ".user_remove(elem);").append(Files.LINE_SEPARATOR);
        writer.append("        return true;").append(Files.LINE_SEPARATOR);
        writer.append("    }").append(Files.LINE_SEPARATOR);
        writer.append("    public java.util.Collection<" + type + "> getAll" + property + "() { return data" + property + ".get(); }").append(Files.LINE_SEPARATOR);
    }

    private void writeStandaloneObjectInterface(Writer writer, PropertyInterface inter, boolean isInTypeRestrictChain) throws IOException {
        ClassModel interfaceRange = inter.getRangeClass();
        if (inter.isVector()) {
            if (interfaceRange == getRangeClass())
                writeStandaloneObjectInterfaceVector_SameType(writer, inter, isInTypeRestrictChain);
            else
                writeStandaloneObjectInterfaceVector_Transtype(writer, inter);
        } else {
            if (interfaceRange == getRangeClass())
                writeStandaloneObjectInterfaceScalar_SameType(writer, inter, isInTypeRestrictChain);
            else
                writeStandaloneObjectInterfaceScalar_Transtype(writer, inter);
        }
    }

    private void writeStandaloneObjectInterfaceScalar_SameType(Writer writer, PropertyInterface inter, boolean isInTypeRestrictChain) throws IOException {
        String interType = getRangeClass().getJavaName();
        String property = inter.getProperty().getName();
        property = String.valueOf(property.charAt(0)).toUpperCase() + property.substring(1);
        writer.append("    public boolean set" + property + "(" + interType + " elem) {").append(Files.LINE_SEPARATOR);
        writer.append("        if (data" + property + ".get() != null) {").append(Files.LINE_SEPARATOR);
        writer.append("            if (elem == null) {").append(Files.LINE_SEPARATOR);
        writer.append("                if (!data" + property + ".user_check_remove(data" + property + ".get())) return false;").append(Files.LINE_SEPARATOR);
        writer.append("                data" + property + ".user_remove(data" + property + ".get());").append(Files.LINE_SEPARATOR);
        writer.append("            } else {").append(Files.LINE_SEPARATOR);
        writer.append("                if (!data" + property + ".user_check_replace(data" + property + ".get(), elem)) return false;").append(Files.LINE_SEPARATOR);
        writer.append("                data" + property + ".user_remove(data" + property + ".get());").append(Files.LINE_SEPARATOR);
        writer.append("                data" + property + ".user_add(elem);").append(Files.LINE_SEPARATOR);
        writer.append("            }").append(Files.LINE_SEPARATOR);
        writer.append("        } else {").append(Files.LINE_SEPARATOR);
        writer.append("            if (elem == null) return true;").append(Files.LINE_SEPARATOR);
        writer.append("            if (!data" + property + ".user_check_add(elem)) return false;").append(Files.LINE_SEPARATOR);
        writer.append("            data" + property + ".user_add(elem);").append(Files.LINE_SEPARATOR);
        writer.append("        }").append(Files.LINE_SEPARATOR);
        writer.append("        return true;").append(Files.LINE_SEPARATOR);
        writer.append("    }").append(Files.LINE_SEPARATOR);
        if (!isInTypeRestrictChain)
            writer.append("    public " + interType + " get" + property + "() { return data" + property + ".get(); }").append(Files.LINE_SEPARATOR);
        else
            writer.append("    public " + interType + " get" + property + "As(" + interType + " type) { return data" + property + ".get(); }").append(Files.LINE_SEPARATOR);
    }

    private void writeStandaloneObjectInterfaceScalar_Transtype(Writer writer, PropertyInterface inter) throws IOException {
        String implType = getRangeClass().getJavaName();
        String interType = inter.getRepresentationRange();
        String property = inter.getProperty().getName();
        property = String.valueOf(property.charAt(0)).toUpperCase() + property.substring(1);
        writer.append("    public boolean set" + property + "(" + interType + " elem) {").append(Files.LINE_SEPARATOR);
        writer.append("        if (data" + property + ".get() != null) {").append(Files.LINE_SEPARATOR);
        writer.append("            if (elem == null) {").append(Files.LINE_SEPARATOR);
        writer.append("                if (!data" + property + ".user_check_remove(data" + property + ".get())) return false;").append(Files.LINE_SEPARATOR);
        writer.append("                data" + property + ".user_remove(data" + property + ".get());").append(Files.LINE_SEPARATOR);
        writer.append("            } else {").append(Files.LINE_SEPARATOR);
        writer.append("                if (!data" + property + ".user_check_replace(data" + property + ".get(), (" + implType + ")elem)) return false;").append(Files.LINE_SEPARATOR);
        writer.append("                data" + property + ".user_remove(data" + property + ".get());").append(Files.LINE_SEPARATOR);
        writer.append("                data" + property + ".user_add((" + implType + ")elem);").append(Files.LINE_SEPARATOR);
        writer.append("            }").append(Files.LINE_SEPARATOR);
        writer.append("        } else {").append(Files.LINE_SEPARATOR);
        writer.append("            if (elem == null) return true;").append(Files.LINE_SEPARATOR);
        writer.append("            if (!data" + property + ".user_check_add((" + implType + ")elem)) return false;").append(Files.LINE_SEPARATOR);
        writer.append("            data" + property + ".user_add((" + implType + ")elem);").append(Files.LINE_SEPARATOR);
        writer.append("        }").append(Files.LINE_SEPARATOR);
        writer.append("        return true;").append(Files.LINE_SEPARATOR);
        writer.append("    }").append(Files.LINE_SEPARATOR);
        writer.append("    public " + interType + " get" + property + "As(" + interType + " type) { return data" + property + ".get(); }").append(Files.LINE_SEPARATOR);
    }

    private void writeStandaloneObjectInterfaceVector_SameType(Writer writer, PropertyInterface inter, boolean isInTypeRestrictChain) throws IOException {
        String interType = getRangeClass().getJavaName();
        String property = inter.getProperty().getName();
        property = String.valueOf(property.charAt(0)).toUpperCase() + property.substring(1);
        writer.append("    public boolean add" + property + "(" + interType + " elem) {").append(Files.LINE_SEPARATOR);
        writer.append("        if (!data" + property + ".user_check_add(elem)) return false;").append(Files.LINE_SEPARATOR);
        writer.append("        data" + property + ".user_add(elem);").append(Files.LINE_SEPARATOR);
        writer.append("        return true;").append(Files.LINE_SEPARATOR);
        writer.append("    }").append(Files.LINE_SEPARATOR);

        writer.append("    public boolean remove" + property + "(" + interType + " elem) {").append(Files.LINE_SEPARATOR);
        writer.append("        if (!data" + property + ".user_check_remove(elem)) return false;").append(Files.LINE_SEPARATOR);
        writer.append("        data" + property + ".user_remove(elem);").append(Files.LINE_SEPARATOR);
        writer.append("        return true;").append(Files.LINE_SEPARATOR);
        writer.append("    }").append(Files.LINE_SEPARATOR);
        if (isVector()) {
            if (!isInTypeRestrictChain)
                writer.append("    public java.util.Collection<" + interType + "> getAll" + property + "() { return data" + property + ".get(); }").append(Files.LINE_SEPARATOR);
            else
                writer.append("    public java.util.Collection<" + interType + "> getAll" + property + "As(" + interType + " type) { return data" + property + ".get(); }").append(Files.LINE_SEPARATOR);
        } else {
            if (!isInTypeRestrictChain)
                writer.append("    public java.util.Collection<" + interType + "> getAll" + property + "() {").append(Files.LINE_SEPARATOR);
            else
                writer.append("    public java.util.Collection<" + interType + "> getAll" + property + "As(" + interType + " type) {").append(Files.LINE_SEPARATOR);
            writer.append("        java.util.List<" + interType + "> result = new java.util.ArrayList<" + interType + ">();").append(Files.LINE_SEPARATOR);
            writer.append("        if (data" + property + ".get() != null)").append(Files.LINE_SEPARATOR);
            writer.append("            result.add(data" + property + ".get());").append(Files.LINE_SEPARATOR);
            writer.append("        return result;").append(Files.LINE_SEPARATOR);
            writer.append("    }").append(Files.LINE_SEPARATOR);
        }
    }

    private void writeStandaloneObjectInterfaceVector_Transtype(Writer writer, PropertyInterface inter) throws IOException {
        String implType = getRangeClass().getJavaName();
        String interType = inter.getRepresentationRange();
        String property = inter.getProperty().getName();
        property = String.valueOf(property.charAt(0)).toUpperCase() + property.substring(1);
        writer.append("    public boolean add" + property + "(" + interType + " elem) {").append(Files.LINE_SEPARATOR);
        writer.append("        " + implType + " value = (" + implType + ")elem;").append(Files.LINE_SEPARATOR);
        writer.append("        if (!data" + property + ".user_check_add(value)) return false;").append(Files.LINE_SEPARATOR);
        writer.append("        data" + property + ".user_add(value);").append(Files.LINE_SEPARATOR);
        writer.append("        return true;").append(Files.LINE_SEPARATOR);
        writer.append("    }").append(Files.LINE_SEPARATOR);

        writer.append("    public boolean remove" + property + "(" + interType + " elem) {").append(Files.LINE_SEPARATOR);
        writer.append("        " + implType + " value = (" + implType + ")elem;").append(Files.LINE_SEPARATOR);
        writer.append("        if (!data" + property + ".user_check_remove(value)) return false;").append(Files.LINE_SEPARATOR);
        writer.append("        data" + property + ".user_remove(value);").append(Files.LINE_SEPARATOR);
        writer.append("        return true;").append(Files.LINE_SEPARATOR);
        writer.append("    }").append(Files.LINE_SEPARATOR);
        writer.append("    public java.util.Collection<" + interType + "> getAll" + property + "As(" + interType + " type) {").append(Files.LINE_SEPARATOR);
        writer.append("        java.util.List<" + interType + "> result = new java.util.ArrayList<" + interType + ">();").append(Files.LINE_SEPARATOR);
        if (isVector()) {
            writer.append("        for (" + implType + " value : data" + property + ".get())").append(Files.LINE_SEPARATOR);
            writer.append("            result.add(value);").append(Files.LINE_SEPARATOR);
        } else {
            writer.append("        if (data" + property + ".get() != null)").append(Files.LINE_SEPARATOR);
            writer.append("            result.add(data" + property + ".get());").append(Files.LINE_SEPARATOR);
        }
        writer.append("        return result;").append(Files.LINE_SEPARATOR);
        writer.append("    }").append(Files.LINE_SEPARATOR);
    }

    /**
     * Generates and writes the code for the constructor of this property implementation as a standalone distribution
     *
     * @param writer The writer to write to
     * @throws java.io.IOException When an IO error occurs
     */
    public void writeStandaloneConstructor(Writer writer) throws IOException {
        String property = getProperty().getName();
        property = String.valueOf(property.charAt(0)).toUpperCase() + property.substring(1);
        writer.append("        data" + property + " = new " + getProperty().getName() + "_impl(this);").append(Files.LINE_SEPARATOR);
    }
}
