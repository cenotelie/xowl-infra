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

import org.xowl.lang.runtime.Literal;
import org.xowl.utils.Logger;

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

        writer.write("    // <editor-fold defaultstate=\"collapsed\" desc=\"Property " + getProperty().getName() + "\">\n");
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
        writer.append("    private " + getProperty().getName() + "_impl data" + property + ";\n");
        writer.append("    public " + getDomain().getJavaName() + "." + getProperty().getName() + " __getImplOf" + getProperty().getName() + "() { return data" + property + "; }\n");

        if (!getProperty().isObjectProperty())
            writeStandaloneDatatype(writer);
        else {
            for (PropertyInterface inter : getInterfaces())
                writeStandaloneObjectInterface(writer, inter, isInTypeRestrictionChain);
        }
        writer.write("    // </editor-fold>\n");
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
        writer.append(" {\n");
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
    }

    /**
     * Writes the header of the property reification class
     *
     * @param writer The writer to write to
     * @throws IOException When an IO error occurs
     */
    private void writeStandalonePropertyClassHeader(Writer writer) throws java.io.IOException {
        writer.append("    private static class " + getProperty().getName() + "_impl");
        writer.append(" implements " + getDomain().getJavaName() + "." + getProperty().getName() + " {\n");
    }

    /**
     * Writes the content of the property reification class
     *
     * @param writer The writer to write to
     * @param type   The property's type
     * @throws IOException When an IO error occurs
     */
    private void writeStandalonePropertyClassContent(Writer writer, String type) throws IOException {
        writer.append("        private " + getParentClass().getJavaName() + " domain;\n");
        if (!isVector()) {
            writer.append("        private " + type + " data;\n");
            writer.append("        public " + type + " get_raw() { return data; }\n");
            if (!hasValues())
                writer.append("        public " + type + " get() { return data; }\n");
            else {
                if (hasSelf())
                    writer.append("        public " + type + " get() { return domain; }\n");
                else if (getProperty().isObjectProperty()) {
                    InstanceModel instance = hasObjectValues().get(0);
                    ClassModel c = instance.getType();
                    if (c.getJavaName().equals("java.lang.Class")) {
                        String className = c.getPackage().getModel().getBasePackage() + ".";
                        className += c.getPackage().getModel().getModelFor(instance.getOWLIndividual().getInterpretationOf().getContainedBy()).getName() + ".";
                        className += instance.getName();
                        writer.write("        public " + type + " get() { return " + className + ".class; }\n");
                    } else
                        writer.write("        public " + type + " get() { return " + c.getJavaName() + ".get_" + instance.getName() + "(); }\n");
                } else {
                    Literal value = hasDataValues().get(0);
                    DatatypeModel datatype = getProperty().getModelFor(value.getMemberOf());
                    String data = datatype.getToValue("\"" + value.getLexicalValue() + "\"");
                    writer.write("        public " + type + " get() { return " + data + "; }\n");
                }
            }
        } else {
            writer.append("        private java.util.List<" + type + "> data;\n");
            writer.append("        public java.util.Collection<" + type + "> get_raw() { return new java.util.ArrayList<" + type + ">(data); }\n");
            if (!hasValues())
                writer.append("        public java.util.Collection<" + type + "> get() { return new java.util.ArrayList<" + type + ">(data); }\n");
            else {
                writer.append("        public java.util.Collection<" + type + "> get() {\n");
                writer.append("            java.util.List<" + type + "> temp = new java.util.ArrayList<" + type + ">(data);\n");
                if (hasSelf())
                    writer.append("            temp.add(domain);\n");
                else if (getProperty().isObjectProperty()) {
                    for (InstanceModel instance : hasObjectValues()) {
                        ClassModel c = instance.getType();
                        if (c.getJavaName().equals("java.lang.Class")) {
                            String className = c.getPackage().getModel().getBasePackage() + ".";
                            className += c.getPackage().getModel().getModelFor(instance.getOWLIndividual().getInterpretationOf().getContainedBy()).getName() + ".";
                            className += instance.getName();
                            writer.write("            temp.add(" + className + ".class);\n");
                        } else
                            writer.write("            temp.add(" + c.getJavaName() + ".get_" + instance.getName() + "());\n");
                    }
                } else {
                    for (Literal value : hasDataValues()) {
                        DatatypeModel datatype = getProperty().getModelFor(value.getMemberOf());
                        String data = datatype.getToValue("\"" + value.getLexicalValue() + "\"");
                        writer.write("            temp.add(" + data + ");\n");
                    }
                }
                writer.append("            return temp;\n");
                writer.append("        }\n");
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
            writer.append("        private boolean check_card(int modifier) {\n");
            writer.append("            int card = modifier + " + getValuesCount() + ";\n");
            writer.append("            if (data != null) card++;\n");
            writer.append("            return (card >= " + Integer.toString(getCardMin()) + " && card <= " + Integer.toString(getCardMax()) + ");\n");
            writer.append("        }\n");
        } else {
            writer.append("        private boolean check_card(int modifier) {\n");
            writer.append("            int card = data.size() + " + getValuesCount() + " + modifier;\n");
            writer.append("            return (card >= " + Integer.toString(getCardMin()) + " && card <= " + Integer.toString(getCardMax()) + ");\n");
            writer.append("        }\n");
        }
    }

    /**
     * Writes the SimpleCheckAdd of the property reification class
     *
     * @param writer The writer to write to
     * @throws IOException When an IO error occurs
     */
    private void writeStandalonePropertyClassSimpleCheckAdd(Writer writer) throws java.io.IOException {
        writer.append("            if (check_contains(elem)) return false;\n");
        writer.append("            if (!check_card(1)) return false;\n");
        if (getProperty().isObjectProperty() && getProperty().isAsymmetric() && getRangeClass().isCompatibleWith(getDomain()) && getParentClass().isCompatibleWith(getRange()))
            writer.append("            if (elem.__getImplOf" + getProperty().getName() + "().check_contains(domain)) return false;\n");
        if (getProperty().isObjectProperty() && getProperty().isIrreflexive() && getParentClass().isCompatibleWith(getRangeClass()))
            writer.append("            if (elem == domain) return false;\n");
        writer.append("            return true;\n");
    }

    private void writeStandalonePropertyClassSimpleCheckRemove(Writer writer) throws IOException {
        writer.append("            if (!check_contains(elem)) return false;\n");
        writer.append("            if (!check_card(-1)) return false;\n");
        if (getProperty().isObjectProperty() && getProperty().isReflexive() || hasSelf())
            writer.append("            if (elem == domain) return false;\n");
        writer.append("            return true;\n");
    }

    private void writeStandalonePropertyClassSimpleCheckReplace(Writer writer) throws IOException {
        writer.append("            if (check_contains(newElem)) return false;\n");
        writer.append("            if (!check_contains(oldElem)) return false;\n");
        if (getProperty().isObjectProperty() && getProperty().isAsymmetric() && getRangeClass().isCompatibleWith(getDomain()) && getParentClass().isCompatibleWith(getRange()))
            writer.append("            if (newElem.__getImplOf" + getProperty().getName() + "().check_contains(domain)) return false;\n");
        if (getProperty().isObjectProperty() && getProperty().isReflexive() || hasSelf())
            writer.append("            if (oldElem == domain) return false;\n");
        if (getProperty().isObjectProperty() && getProperty().isIrreflexive() && getParentClass().isCompatibleWith(getRangeClass()))
            writer.append("            if (newElem == domain) return false;\n");
        writer.append("            return true;\n");
    }

    private void writeStandalonePropertyClassSimpleDoAdd(Writer writer) throws IOException {
        if (!isVector())
            writer.append("            data = elem;\n");
        else
            writer.append("            data.add(elem);\n");
    }

    private void writeStandalonePropertyClassSimpleDoRemove(Writer writer) throws IOException {
        if (!isVector())
            writer.append("            data = null;\n");
        else
            writer.append("            data.remove(elem);\n");
    }

    private void writeStandalonePropertyClassSimple(Writer writer, String type) throws IOException {
        writer.append("        public boolean simple_check_add(" + type + " elem) {\n");
        writeStandalonePropertyClassSimpleCheckAdd(writer);
        writer.append("        }\n");
        writer.append("        public boolean simple_check_remove(" + type + " elem) {\n");
        writeStandalonePropertyClassSimpleCheckRemove(writer);
        writer.append("        }\n");
        writer.append("        public boolean simple_check_replace(" + type + " oldElem, " + type + "  newElem) {\n");
        writeStandalonePropertyClassSimpleCheckReplace(writer);
        writer.append("        }\n");
        writer.append("        public void simple_add(" + type + " elem) {\n");
        writeStandalonePropertyClassSimpleDoAdd(writer);
        writer.append("        }\n");
        writer.append("        public void simple_remove(" + type + " elem) {\n");
        writeStandalonePropertyClassSimpleDoRemove(writer);
        writer.append("        }\n");
    }

    private void writeStandalonePropertyClassTreeCheckAdd(Writer writer) throws IOException {
        writer.append("            if (!simple_check_add(elem)) return false;\n");
        for (PropertyImplementation ancestor : getAncestors())
            writer.append("            if (!domain." + ancestor.getProperty().getName() + "_data.simple_check_add(elem)) return false;\n");
        writer.append("            return true;\n");
    }

    private void writeStandalonePropertyClassTreeCheckRemove(Writer writer) throws IOException {
        writer.append("            if (!simple_check_remove(elem)) return false;\n");
        for (PropertyImplementation ancestor : getAncestors())
            writer.append("            if (!domain." + ancestor.getProperty().getName() + "_data.simple_check_remove(elem)) return false;\n");
        writer.append("            return true;\n");
    }

    private void writeStandalonePropertyClassTreeCheckReplace(Writer writer) throws IOException {
        writer.append("            if (!simple_check_replace(oldElem, newElem)) return false;\n");
        for (PropertyImplementation ancestor : getAncestors())
            writer.append("            if (!domain." + ancestor.getProperty().getName() + "_data.simple_check_replace(oldElem, newElem)) return false;\n");
        writer.append("            return true;\n");
    }

    private void writeStandalonePropertyClassTreeDoAdd(Writer writer) throws IOException {
        writer.append("            simple_add(elem);\n");
        for (PropertyImplementation ancestor : getAncestors())
            writer.append("            domain." + ancestor.getProperty().getName() + "_data.simple_add(elem);\n");
    }

    private void writeStandalonePropertyClassTreeDoRemove(Writer writer) throws IOException {
        writer.append("            simple_remove(elem);\n");
        for (PropertyImplementation ancestor : getAncestors())
            writer.append("            domain." + ancestor.getProperty().getName() + "_data.simple_remove(elem);\n");
    }

    private void writeStandalonePropertyClassTree(Writer writer, String type) throws IOException {
        writer.append("        private boolean tree_check_add(" + type + " elem) {\n");
        writeStandalonePropertyClassTreeCheckAdd(writer);
        writer.append("        }\n");
        writer.append("        private boolean tree_check_remove(" + type + " elem) {\n");
        writeStandalonePropertyClassTreeCheckRemove(writer);
        writer.append("        }\n");
        writer.append("        private boolean tree_check_replace(" + type + " oldElem, " + type + "  newElem) {\n");
        writeStandalonePropertyClassTreeCheckReplace(writer);
        writer.append("        }\n");
        writer.append("        private void tree_add(" + type + " elem) {\n");
        writeStandalonePropertyClassTreeDoAdd(writer);
        writer.append("        }\n");
        writer.append("        private void tree_remove(" + type + " elem) {\n");
        writeStandalonePropertyClassTreeDoRemove(writer);
        writer.append("        }\n");
    }

    private void writeStandalonePropertyClassUserCheckAdd(Writer writer) throws IOException {
        // Try delegating to a descendant
        for (PropertyImplementation descandant : getDescendants()) {
            writer.append("            if (elem instanceof " + descandant.getRangeClass().getJavaName() + ")\n");
            writer.append("                return domain." + descandant.getProperty().getName() + "_data.user_check_add((" + descandant.getRangeClass().getJavaName() + ")elem);\n");
        }
        // Dispatch to inverse
        if (!getInverses().isEmpty())
            writer.append("            if (!elem.__getImplOf" + getInverses().get(0).getName() + "().inverse_check_add(domain)) return false;\n");
        writer.append("            return tree_check_add(elem);\n");
    }

    private void writeStandalonePropertyClassUserCheckRemove(Writer writer) throws IOException {
        // Try delegating to a descendant
        for (PropertyImplementation descandant : getDescendants()) {
            writer.append("            if (elem instanceof " + descandant.getRangeClass().getJavaName() + ")\n");
            writer.append("                return domain." + descandant.getProperty().getName() + "_data.user_check_remove((" + descandant.getRangeClass().getJavaName() + ")elem);\n");
        }
        // Dispatch to inverse
        if (!getInverses().isEmpty())
            writer.append("            if (!elem.__getImplOf" + getInverses().get(0).getName() + "().inverse_check_remove(domain)) return false;\n");
        writer.append("            return tree_check_remove(elem);\n");
    }

    private void writeStandalonePropertyClassUserCheckReplace(Writer writer) throws IOException {
        // Try delegating to a descendant
        for (PropertyImplementation descandant : getDescendants()) {
            writer.append("            if (newElem instanceof " + descandant.getRangeClass().getJavaName() + ")\n");
            writer.append("                return domain." + descandant.getProperty().getName() + "_data.user_check_replace((" + descandant.getRangeClass().getJavaName() + ")oldElem, (" + descandant.getRangeClass().getJavaName() + ")newElem);\n");
        }
        // Dispatch to inverse
        if (!getInverses().isEmpty()) {
            writer.append("            if (!oldElem.__getImplOf" + getInverses().get(0).getName() + "().inverse_check_remove(domain)) return false;\n");
            writer.append("            if (!newElem.__getImplOf" + getInverses().get(0).getName() + "().inverse_check_add(domain)) return false;\n");
        }
        writer.append("            return tree_check_replace(oldElem, newElem);\n");
    }

    private void writeStandalonePropertyClassUserDoAdd(Writer writer) throws IOException {
        // Try delegating to a descendant
        for (PropertyImplementation descandant : getDescendants()) {
            writer.append("            if (elem instanceof " + descandant.getRangeClass().getJavaName() + ") {\n");
            writer.append("                domain." + descandant.getProperty().getName() + "_data.user_add((" + descandant.getRangeClass().getJavaName() + ")elem);\n");
            writer.append("                return;\n");
            writer.append("            }\n");
        }
        // Dispatch to inverse
        if (!getInverses().isEmpty())
            writer.append("            elem.__getImplOf" + getInverses().get(0).getName() + "().inverse_add(domain);\n");
        writer.append("            tree_add(elem);\n");
    }

    private void writeStandalonePropertyClassUserDoRemove(Writer writer) throws IOException {
        // Try delegating to a descendant
        for (PropertyImplementation descandant : getDescendants()) {
            writer.append("            if (elem instanceof " + descandant.getRangeClass().getJavaName() + ") {\n");
            writer.append("                domain." + descandant.getProperty().getName() + "_data.user_remove((" + descandant.getRangeClass().getJavaName() + ")elem);\n");
            writer.append("                return;\n");
            writer.append("            }\n");
        }
        // Dispatch to inverse
        if (!getInverses().isEmpty())
            writer.append("            elem.__getImplOf" + getInverses().get(0).getName() + "().inverse_remove(domain);\n");
        writer.append("            tree_remove(elem);\n");
    }

    private void writeStandalonePropertyClassInverseCheckAdd(Writer writer) throws IOException {
        // Try delegating to a descendant
        for (PropertyImplementation descandant : getDescendants()) {
            writer.append("            if (elem instanceof " + descandant.getRangeClass().getJavaName() + ")\n");
            writer.append("                return domain." + descandant.getProperty().getName() + "_data.inverse_check_add((" + descandant.getRangeClass().getJavaName() + ")elem);\n");
        }
        writer.append("            return tree_check_add(elem);\n");
    }

    private void writeStandalonePropertyClassInverseCheckRemove(Writer writer) throws IOException {
        // Try delegating to a descendant
        for (PropertyImplementation descandant : getDescendants()) {
            writer.append("            if (elem instanceof " + descandant.getRangeClass().getJavaName() + ")\n");
            writer.append("                return domain." + descandant.getProperty().getName() + "_data.inverse_check_remove((" + descandant.getRangeClass().getJavaName() + ")elem);\n");
        }
        writer.append("            return tree_check_remove(elem);\n");
    }

    private void writeStandalonePropertyClassInverseCheckReplace(Writer writer) throws IOException {
        // Try delegating to a descendant
        for (PropertyImplementation descandant : getDescendants()) {
            writer.append("            if (newElem instanceof " + descandant.getRangeClass().getJavaName() + ")\n");
            writer.append("                return domain." + descandant.getProperty().getName() + "_data.inverse_check_replace((" + descandant.getRangeClass().getJavaName() + ")oldElem, (" + descandant.getRangeClass().getJavaName() + ")newElem);\n");
        }
        writer.append("            return tree_check_replace(oldElem, newElem);\n");
    }

    private void writeStandalonePropertyClassInverseDoAdd(Writer writer) throws IOException {
        // Try delegating to a descendant
        for (PropertyImplementation descandant : getDescendants()) {
            writer.append("            if (elem instanceof " + descandant.getRangeClass().getJavaName() + ") {\n");
            writer.append("                domain." + descandant.getProperty().getName() + "_data.inverse_add((" + descandant.getRangeClass().getJavaName() + ")elem);\n");
            writer.append("                return;\n");
            writer.append("            }\n");
        }
        writer.append("            tree_add(elem);\n");
    }

    private void writeStandalonePropertyClassInverseDoRemove(Writer writer) throws IOException {
        // Try delegating to a descendant
        for (PropertyImplementation descandant : getDescendants()) {
            writer.append("            if (elem instanceof " + descandant.getRangeClass().getJavaName() + ") {\n");
            writer.append("                domain." + descandant.getProperty().getName() + "_data.inverse_remove((" + descandant.getRangeClass().getJavaName() + ")elem);\n");
            writer.append("                return;\n");
            writer.append("            }\n");
        }
        writer.append("            tree_remove(elem);\n");
    }

    private void writeStandalonePropertyClassConstructor(Writer writer, String type) throws IOException {
        writer.append("        public " + getProperty().getName() + "_impl(" + getParentClass().getJavaName() + " domain) {\n");
        writer.append("            this.domain = domain;\n");
        if (isVector())
            writer.append("            this.data = new java.util.ArrayList<" + type + ">();\n");
        writer.append("        }\n");
    }

    private void writeStandalonePropertyClassSameType(Writer writer, String type) throws IOException {
        writeStandalonePropertyClassHeader(writer);
        writeStandalonePropertyClassContent(writer, type);

        writeStandalonePropertyClassCheckCard(writer);
        if (!isVector()) {
            if (getProperty().isObjectProperty())
                writer.append("        @Override public boolean check_contains(" + type + " elem) { return (data == elem); }\n");
            else
                writer.append("        @Override public boolean check_contains(" + type + " elem) { return data.equals(elem); }\n");
        } else
            writer.append("        @Override public boolean check_contains(" + type + " elem) { return (data.contains(elem)); }\n");

        writeStandalonePropertyClassSimple(writer, type);
        writeStandalonePropertyClassTree(writer, type);

        writer.append("        @Override public boolean user_check_add(" + type + " elem) {\n");
        writeStandalonePropertyClassUserCheckAdd(writer);
        writer.append("        }\n");
        writer.append("        @Override public boolean user_check_remove(" + type + " elem) {\n");
        writeStandalonePropertyClassUserCheckRemove(writer);
        writer.append("        }\n");
        writer.append("        @Override public boolean user_check_replace(" + type + " oldElem, " + type + "  newElem) {\n");
        writeStandalonePropertyClassUserCheckReplace(writer);
        writer.append("        }\n");
        writer.append("        @Override public void user_add(" + type + " elem) {\n");
        writeStandalonePropertyClassUserDoAdd(writer);
        writer.append("        }\n");
        writer.append("        @Override public void user_remove(" + type + " elem) {\n");
        writeStandalonePropertyClassUserDoRemove(writer);
        writer.append("        }\n");

        writer.append("        @Override public boolean inverse_check_add(" + type + " elem) {\n");
        writeStandalonePropertyClassInverseCheckAdd(writer);
        writer.append("        }\n");
        writer.append("        @Override public boolean inverse_check_remove(" + type + " elem) {\n");
        writeStandalonePropertyClassInverseCheckRemove(writer);
        writer.append("        }\n");
        writer.append("        @Override public boolean inverse_check_replace(" + type + " oldElem, " + type + "  newElem) {\n");
        writeStandalonePropertyClassInverseCheckReplace(writer);
        writer.append("        }\n");
        writer.append("        @Override public void inverse_add(" + type + " elem) {\n");
        writeStandalonePropertyClassInverseDoAdd(writer);
        writer.append("        }\n");
        writer.append("        @Override public void inverse_remove(" + type + " elem) {\n");
        writeStandalonePropertyClassInverseDoRemove(writer);
        writer.append("        }\n");

        writeStandalonePropertyClassConstructor(writer, type);
        writer.append("    }\n");
    }

    private void writeStandalonePropertyClassTranstype(Writer writer, String baseType, String implType) throws IOException {
        writeStandalonePropertyClassHeader(writer);
        writeStandalonePropertyClassContent(writer, implType);

        writeStandalonePropertyClassCheckCard(writer);
        if (!isVector()) {
            if (getProperty().isObjectProperty())
                writer.append("        public boolean check_contains(" + implType + " elem) { return (data == elem); }\n");
            else
                writer.append("        public boolean check_contains(" + implType + " elem) { return data.equals(elem); }\n");
            writer.append("        @Override public boolean check_contains(" + baseType + " elem) {\n");
            writer.append("            if (!(elem instanceof " + implType + ")) return false;\n");
            writer.append("            return (data == elem);\n");
            writer.append("        }\n");
        } else {
            writer.append("        public boolean check_contains(" + implType + " elem) { return (data.contains(elem)); }\n");
            writer.append("        @Override public boolean check_contains(" + baseType + " elem) {\n");
            writer.append("            if (!(elem instanceof " + implType + ")) return false;\n");
            writer.append("            return (data.contains((" + implType + ")elem));\n");
            writer.append("        }\n");
        }

        writeStandalonePropertyClassSimple(writer, implType);
        writeStandalonePropertyClassTree(writer, implType);

        writer.append("        public boolean user_check_add(" + implType + " elem) {\n");
        writeStandalonePropertyClassUserCheckAdd(writer);
        writer.append("        }\n");
        writer.append("        public boolean user_check_remove(" + implType + " elem) {\n");
        writeStandalonePropertyClassUserCheckRemove(writer);
        writer.append("        }\n");
        writer.append("        public boolean user_check_replace(" + implType + " oldElem, " + implType + "  newElem) {\n");
        writeStandalonePropertyClassUserCheckReplace(writer);
        writer.append("        }\n");
        writer.append("        public void user_add(" + implType + " elem) {\n");
        writeStandalonePropertyClassUserDoAdd(writer);
        writer.append("        }\n");
        writer.append("        public void user_remove(" + implType + " elem) {\n");
        writeStandalonePropertyClassUserDoRemove(writer);
        writer.append("        }\n");
        writer.append("        @Override public boolean user_check_add(" + baseType + " elem) { return user_check_add((" + implType + ")elem); }\n");
        writer.append("        @Override public boolean user_check_remove(" + baseType + " elem) { return user_check_remove((" + implType + ")elem); }\n");
        writer.append("        @Override public boolean user_check_replace(" + baseType + " oldElem, " + baseType + "  newElem) { return user_check_replace((" + implType + ")oldElem, (" + implType + ")newElem); }\n");
        writer.append("        @Override public void user_add(" + baseType + " elem) { user_add((" + implType + ")elem); }\n");
        writer.append("        @Override public void user_remove(" + baseType + " elem) { user_remove((" + implType + ")elem); }\n");

        writer.append("        public boolean inverse_check_add(" + implType + " elem) {\n");
        writeStandalonePropertyClassInverseCheckAdd(writer);
        writer.append("        }\n");
        writer.append("        public boolean inverse_check_remove(" + implType + " elem) {\n");
        writeStandalonePropertyClassInverseCheckRemove(writer);
        writer.append("        }\n");
        writer.append("        public boolean inverse_check_replace(" + implType + " oldElem, " + implType + "  newElem) {\n");
        writeStandalonePropertyClassInverseCheckReplace(writer);
        writer.append("        }\n");
        writer.append("        public void inverse_add(" + implType + " elem) {\n");
        writeStandalonePropertyClassInverseDoAdd(writer);
        writer.append("        }\n");
        writer.append("        public void inverse_remove(" + implType + " elem) {\n");
        writeStandalonePropertyClassInverseDoRemove(writer);
        writer.append("        }\n");
        writer.append("        @Override public boolean inverse_check_add(" + baseType + " elem) { return inverse_check_add((" + implType + ")elem); }\n");
        writer.append("        @Override public boolean inverse_check_remove(" + baseType + " elem) { return inverse_check_remove((" + implType + ")elem); }\n");
        writer.append("        @Override public boolean inverse_check_replace(" + baseType + " oldElem, " + baseType + "  newElem) { return inverse_check_replace((" + implType + ")oldElem, (" + implType + ")newElem); }\n");
        writer.append("        @Override public void inverse_add(" + baseType + " elem) { inverse_add((" + implType + ")elem); }\n");
        writer.append("        @Override public void inverse_remove(" + baseType + " elem) { inverse_remove((" + implType + ")elem); }\n");

        writeStandalonePropertyClassConstructor(writer, implType);
        writer.append("    }\n");
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
        writer.append("    public boolean set" + property + "(" + type + " elem) {\n");
        writer.append("        data" + property + ".simple_add(elem);\n");
        writer.append("        return true;\n");
        writer.append("    }\n");
        writer.append("    public " + type + " get" + property + "() { return data" + property + ".get(); }\n");
    }

    private void writeStandaloneDatatypeInterfaceVector(Writer writer, PropertyInterface inter) throws IOException {
        String type = getRepresentationRange();
        String property = inter.getProperty().getName();
        property = String.valueOf(property.charAt(0)).toUpperCase() + property.substring(1);
        writer.append("    public boolean add" + property + "(" + type + " elem) {\n");
        writer.append("        if (!data" + property + ".user_check_add(elem)) return false;\n");
        writer.append("        data" + property + ".user_add(elem);\n");
        writer.append("        return true;\n");
        writer.append("    }\n");
        writer.append("    public boolean remove" + property + "(" + type + " elem) {\n");
        writer.append("        if (!data" + property + ".user_check_remove(elem)) return false;\n");
        writer.append("        data" + property + ".user_remove(elem);\n");
        writer.append("        return true;\n");
        writer.append("    }\n");
        writer.append("    public java.util.Collection<" + type + "> getAll" + property + "() { return data" + property + ".get(); }\n");
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
        writer.append("    public boolean set" + property + "(" + interType + " elem) {\n");
        writer.append("        if (data" + property + ".get() != null) {\n");
        writer.append("            if (elem == null) {\n");
        writer.append("                if (!data" + property + ".user_check_remove(data" + property + ".get())) return false;\n");
        writer.append("                data" + property + ".user_remove(data" + property + ".get());\n");
        writer.append("            } else {\n");
        writer.append("                if (!data" + property + ".user_check_replace(data" + property + ".get(), elem)) return false;\n");
        writer.append("                data" + property + ".user_remove(data" + property + ".get());\n");
        writer.append("                data" + property + ".user_add(elem);\n");
        writer.append("            }\n");
        writer.append("        } else {\n");
        writer.append("            if (elem == null) return true;\n");
        writer.append("            if (!data" + property + ".user_check_add(elem)) return false;\n");
        writer.append("            data" + property + ".user_add(elem);\n");
        writer.append("        }\n");
        writer.append("        return true;\n");
        writer.append("    }\n");
        if (!isInTypeRestrictChain)
            writer.append("    public " + interType + " get" + property + "() { return data" + property + ".get(); }\n");
        else
            writer.append("    public " + interType + " get" + property + "As(" + interType + " type) { return data" + property + ".get(); }\n");
    }

    private void writeStandaloneObjectInterfaceScalar_Transtype(Writer writer, PropertyInterface inter) throws IOException {
        String implType = getRangeClass().getJavaName();
        String interType = inter.getRepresentationRange();
        String property = inter.getProperty().getName();
        property = String.valueOf(property.charAt(0)).toUpperCase() + property.substring(1);
        writer.append("    public boolean set" + property + "(" + interType + " elem) {\n");
        writer.append("        if (data" + property + ".get() != null) {\n");
        writer.append("            if (elem == null) {\n");
        writer.append("                if (!data" + property + ".user_check_remove(data" + property + ".get())) return false;\n");
        writer.append("                data" + property + ".user_remove(data" + property + ".get());\n");
        writer.append("            } else {\n");
        writer.append("                if (!data" + property + ".user_check_replace(data" + property + ".get(), (" + implType + ")elem)) return false;\n");
        writer.append("                data" + property + ".user_remove(data" + property + ".get());\n");
        writer.append("                data" + property + ".user_add((" + implType + ")elem);\n");
        writer.append("            }\n");
        writer.append("        } else {\n");
        writer.append("            if (elem == null) return true;\n");
        writer.append("            if (!data" + property + ".user_check_add((" + implType + ")elem)) return false;\n");
        writer.append("            data" + property + ".user_add((" + implType + ")elem);\n");
        writer.append("        }\n");
        writer.append("        return true;\n");
        writer.append("    }\n");
        writer.append("    public " + interType + " get" + property + "As(" + interType + " type) { return data" + property + ".get(); }\n");
    }

    private void writeStandaloneObjectInterfaceVector_SameType(Writer writer, PropertyInterface inter, boolean isInTypeRestrictChain) throws IOException {
        String interType = getRangeClass().getJavaName();
        String property = inter.getProperty().getName();
        property = String.valueOf(property.charAt(0)).toUpperCase() + property.substring(1);
        writer.append("    public boolean add" + property + "(" + interType + " elem) {\n");
        writer.append("        if (!data" + property + ".user_check_add(elem)) return false;\n");
        writer.append("        data" + property + ".user_add(elem);\n");
        writer.append("        return true;\n");
        writer.append("    }\n");

        writer.append("    public boolean remove" + property + "(" + interType + " elem) {\n");
        writer.append("        if (!data" + property + ".user_check_remove(elem)) return false;\n");
        writer.append("        data" + property + ".user_remove(elem);\n");
        writer.append("        return true;\n");
        writer.append("    }\n");
        if (isVector()) {
            if (!isInTypeRestrictChain)
                writer.append("    public java.util.Collection<" + interType + "> getAll" + property + "() { return data" + property + ".get(); }\n");
            else
                writer.append("    public java.util.Collection<" + interType + "> getAll" + property + "As(" + interType + " type) { return data" + property + ".get(); }\n");
        } else {
            if (!isInTypeRestrictChain)
                writer.append("    public java.util.Collection<" + interType + "> getAll" + property + "() {\n");
            else
                writer.append("    public java.util.Collection<" + interType + "> getAll" + property + "As(" + interType + " type) {\n");
            writer.append("        java.util.List<" + interType + "> result = new java.util.ArrayList<" + interType + ">();\n");
            writer.append("        if (data" + property + ".get() != null)\n");
            writer.append("            result.add(data" + property + ".get());\n");
            writer.append("        return result;\n");
            writer.append("    }\n");
        }
    }

    private void writeStandaloneObjectInterfaceVector_Transtype(Writer writer, PropertyInterface inter) throws IOException {
        String implType = getRangeClass().getJavaName();
        String interType = inter.getRepresentationRange();
        String property = inter.getProperty().getName();
        property = String.valueOf(property.charAt(0)).toUpperCase() + property.substring(1);
        writer.append("    public boolean add" + property + "(" + interType + " elem) {\n");
        writer.append("        " + implType + " value = (" + implType + ")elem;\n");
        writer.append("        if (!data" + property + ".user_check_add(value)) return false;\n");
        writer.append("        data" + property + ".user_add(value);\n");
        writer.append("        return true;\n");
        writer.append("    }\n");

        writer.append("    public boolean remove" + property + "(" + interType + " elem) {\n");
        writer.append("        " + implType + " value = (" + implType + ")elem;\n");
        writer.append("        if (!data" + property + ".user_check_remove(value)) return false;\n");
        writer.append("        data" + property + ".user_remove(value);\n");
        writer.append("        return true;\n");
        writer.append("    }\n");
        writer.append("    public java.util.Collection<" + interType + "> getAll" + property + "As(" + interType + " type) {\n");
        writer.append("        java.util.List<" + interType + "> result = new java.util.ArrayList<" + interType + ">();\n");
        if (isVector()) {
            writer.append("        for (" + implType + " value : data" + property + ".get())\n");
            writer.append("            result.add(value);\n");
        } else {
            writer.append("        if (data" + property + ".get() != null)\n");
            writer.append("            result.add(data" + property + ".get());\n");
        }
        writer.append("        return result;\n");
        writer.append("    }\n");
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
        writer.write("        data" + property + " = new " + getProperty().getName() + "_impl(this);\n");
    }
}
