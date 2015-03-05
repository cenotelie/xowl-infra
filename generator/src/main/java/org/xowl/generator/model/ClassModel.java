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

import org.xowl.lang.runtime.Class;
import org.xowl.lang.runtime.*;
import org.xowl.utils.Files;
import org.xowl.utils.Logger;

import java.io.IOException;
import java.io.Writer;
import java.util.*;

/**
 * Represents the model of an OWL class for code generation
 *
 * @author Laurent Wouters
 */
public class ClassModel {
    /**
     * Maximum length of the generated class names for the unions and intersections
     */
    public static final int MAX_NAME_LENGTH = 100;

    /**
     * The counter for the truncated names
     */
    private static int counter = 0;

    /**
     * The parent package
     */
    private PackageModel parent;
    /**
     * The associated OWL class
     */
    private Class classe;
    /**
     * The name of this class
     */
    private String name;
    /**
     * The equivalent classes
     */
    private List<ClassModel> equivalents;
    /**
     * The super classes
     */
    private List<ClassModel> superClasses;
    /**
     * The sub classes
     */
    private List<ClassModel> subClasses;
    /**
     * The properties for which this class is the domain
     */
    private List<PropertyModel> properties;
    /**
     * The property interfaces owned by this class
     */
    private List<PropertyInterface> propertyInterfaces;
    /**
     * The property implementations owned by this class
     */
    private Map<PropertyModel, PropertyImplementation> propertyImplementations;
    /**
     * The static instances of this class
     */
    private List<InstanceModel> staticInstances;

    /**
     * Gets the parent package
     *
     * @return The parent package
     */
    public PackageModel getPackage() {
        return parent;
    }

    /**
     * Gets the associated OWL class
     *
     * @return The associated OWL class
     */
    public Class getOWLClass() {
        return classe;
    }

    /**
     * Gets the equivalency group containing this class and all its equivalents
     *
     * @return Gets the equivalency group in which this class belongs
     */
    public Collection<ClassModel> getEquivalencyGroup() {
        List<ClassModel> group = new ArrayList<>(equivalents);
        group.add(this);
        return group;
    }

    /**
     * Gets all the super-classes of this class, direct or not
     *
     * @return All the super-classes
     */
    public Collection<ClassModel> getSuperClasses() {
        List<ClassModel> ancestors = new ArrayList<>(superClasses);
        for (int i = 0; i != ancestors.size(); i++) {
            for (ClassModel ancestor : ancestors.get(i).superClasses) {
                if (!ancestors.contains(ancestor))
                    ancestors.add(ancestor);
            }
        }
        return ancestors;
    }

    /**
     * Gets all the sub-classes of this class, direct or not
     *
     * @return All the sub-classes
     */
    public Collection<ClassModel> getSubClasses() {
        List<ClassModel> descendants = new ArrayList<>(subClasses);
        for (int i = 0; i != descendants.size(); i++) {
            for (ClassModel descendant : descendants.get(i).subClasses) {
                if (!descendants.contains(descendant))
                    descendants.add(descendant);
            }
        }
        return descendants;
    }

    /**
     * Gets all the properties for which this class is the domain
     *
     * @return The properties for which this class is the domain
     */
    public Collection<PropertyModel> getProperties() {
        return properties;
    }

    /**
     * Gets the property interfaces defined for this class
     *
     * @return The property interfaces defined for this class
     */
    public Collection<PropertyInterface> getPropertyInterfaces() {
        return propertyInterfaces;
    }

    /**
     * Gets the property implementations owned by this class
     *
     * @return The property implementations owned by this class
     */
    public Collection<PropertyImplementation> getPropertyImplementations() {
        return propertyImplementations.values();
    }

    /**
     * Gets the property implementation owned by this class associated to the specified OWL property
     *
     * @param property An OWL property
     * @return The associated implementation owned by this class
     */
    public PropertyImplementation getPropertyImplementation(Property property) {
        for (PropertyModel gen : propertyImplementations.keySet())
            if (gen.getOWLProperty() == property)
                return propertyImplementations.get(gen);
        return null;
    }

    /**
     * Gets the property implementation owned by this class for the specified property
     *
     * @param propertyModel A property model
     * @return The associated implementation owned by this class
     */
    public PropertyImplementation getPropertyImplementation(PropertyModel propertyModel) {
        return propertyImplementations.get(propertyModel);
    }

    /**
     * Gets the static instances of this class
     *
     * @return The static instances of this class
     */
    public Collection<InstanceModel> getStaticInstances() {
        return staticInstances;
    }

    /**
     * Gets the static instance of this class for the specified OWL individual
     *
     * @param individual An OWL individual
     * @return The associated static instance
     */
    public InstanceModel getStaticInstance(NamedIndividual individual) {
        for (InstanceModel inst : staticInstances)
            if (inst.getOWLIndividual() == individual)
                return inst;
        return null;
    }

    /**
     * Gets the name of this class
     *
     * @return The name of this class
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the name of this class in Java
     *
     * @return The name of this class in Java
     */
    public String getJavaName() {
        return parent.getFullName() + "." + name;
    }

    /**
     * Gets whether this class is abstract, i.e. it has at least one sub-class
     *
     * @return true if this is an abstract class, false otherwise
     */
    public boolean isAbstract() {
        return (!subClasses.isEmpty());
    }

    /**
     * Gets whether this class is compatible with the specified class, i.e. it is either the same or a sub-class
     *
     * @param classe A class
     * @return true if this class is compatible
     */
    public boolean isCompatibleWith(ClassModel classe) {
        return ((classe == this) || this.getSuperClasses().contains(classe));
    }

    /**
     * Adds a property to this class
     *
     * @param property The property to add
     */
    public void addProperty(PropertyModel property) {
        properties.add(property);
    }

    /**
     * Gets the class model for the specified OWL class
     *
     * @param classe An OWL class
     * @return The associated class model
     */
    public ClassModel getModelFor(Class classe) {
        return parent.getModel().getModelFor(classe);
    }

    /**
     * Gets the property model associated to the specified OWL property
     *
     * @param property An OWL property
     * @return The associated property model
     */
    public PropertyModel getModelFor(Property property) {
        return parent.getModel().getModelFor(property);
    }


    /**
     * Initializes this class
     *
     * @param packageModel The parent package
     * @param classe       The associated OWL class
     */
    public ClassModel(PackageModel packageModel, Class classe) {
        parent = packageModel;
        this.classe = classe;
        equivalents = new ArrayList<>();
        superClasses = new ArrayList<>();
        subClasses = new ArrayList<>();
        properties = new ArrayList<>();
        propertyInterfaces = new ArrayList<>();
        propertyImplementations = new HashMap<>();
        staticInstances = new ArrayList<>();
        if (this.classe.getInterpretationOf() == null) {
            if (!this.classe.getAllClassUnionOf().isEmpty())
                buildUnionName();
            else if (!this.classe.getAllClassIntersectionOf().isEmpty())
                buildIntersectionName();
        } else {
            String iri = this.classe.getInterpretationOf().getHasIRI().getHasValue();
            String[] parts = iri.split("#");
            name = parts[parts.length - 1];
            for (Individual individual : this.classe.getAllClassifies()) {
                if (individual instanceof NamedIndividual) {
                    InstanceModel instanceModel = new InstanceModel(this, (NamedIndividual) individual);
                    packageModel.getModel().register((NamedIndividual) individual, instanceModel);
                    staticInstances.add(instanceModel);
                }
            }
        }
    }

    /**
     * Builds this class as a union
     */
    private void buildUnionName() {
        StringBuilder builder = new StringBuilder();
        List<String> names = new ArrayList<>();
        for (Class member : classe.getAllClassUnionOf()) {
            ClassModel gen = getModelFor(member);
            if (gen == null)
                continue;
            names.add(gen.name);
        }
        Collections.sort(names);
        for (String name : names) {
            if (builder.length() != 0)
                builder.append("_OR_");
            builder.append(name);
        }
        name = builder.toString();
        if (name.length() > MAX_NAME_LENGTH) {
            name = name.substring(0, MAX_NAME_LENGTH - 1) + Integer.toString(counter);
            counter++;
        }
    }

    /**
     * Builds this class as an intersection
     */
    private void buildIntersectionName() {
        StringBuilder builder = new StringBuilder();
        List<String> names = new ArrayList<>();
        for (Class member : classe.getAllClassIntersectionOf()) {
            ClassModel gen = getModelFor(member);
            if (gen == null)
                continue;
            names.add(gen.name);
        }
        Collections.sort(names);
        for (String name : names) {
            if (builder.length() != 0)
                builder.append("_AND_");
            builder.append(name);
        }
        name = builder.toString();
        if (name.length() > MAX_NAME_LENGTH) {
            name = name.substring(0, MAX_NAME_LENGTH - 1) + Integer.toString(counter);
            counter++;
        }
    }

    /**
     * Builds the equivalency group
     */
    public void buildEquivalents() {
        for (Class equivalent : classe.getAllClassEquivalentTo()) {
            ClassModel classModel = getModelFor(equivalent);
            if (classModel != null && !equivalents.contains(classModel)) {
                equivalents.add(classModel);
                classModel.equivalents.add(this);
            }
        }
    }

    /**
     * Builds the class restrictions
     */
    public void buildRestrictions() {
        for (ClassRestriction restriction : classe.getAllClassRestrictions()) {
            if (restriction instanceof ObjectAllValuesFrom) {
                ObjectAllValuesFrom objectAllValuesFrom = (ObjectAllValuesFrom) restriction;
                getModelFor(objectAllValuesFrom.getClasse());
            } else if (restriction instanceof ObjectSomeValuesFrom) {
                ObjectSomeValuesFrom objectSomeValuesFrom = (ObjectSomeValuesFrom) restriction;
                getModelFor(objectSomeValuesFrom.getClasse());
            }
        }
    }

    /**
     * Builds the class hierarchy
     */
    public void buildHierarchy() {
        for (Class parent : classe.getAllSubClassOf()) {
            ClassModel classModel = getModelFor(parent);
            if (classModel != null && !classModel.name.equals("Thing") && !superClasses.contains(classModel)) {
                superClasses.add(classModel);
                classModel.subClasses.add(this);
            }
        }
        for (Class operand : classe.getAllClassUnionOf()) {
            ClassModel classModel = getModelFor(operand);
            if (classModel != null && !subClasses.contains(classModel)) {
                subClasses.add(classModel);
                classModel.superClasses.add(this);
            }
        }
        for (Class operand : classe.getAllClassIntersectionOf()) {
            ClassModel classModel = getModelFor(operand);
            if (classModel != null && !superClasses.contains(classModel)) {
                superClasses.add(classModel);
                classModel.subClasses.add(this);
            }
        }
    }

    /**
     * Builds the class hierarchy for union classes
     */
    public void buildUnionHierarchy() {
        if (classe.getAllClassUnionOf().isEmpty())
            return;
        List<Collection<ClassModel>> ancestors = new ArrayList<>();
        for (Class operand : classe.getAllClassUnionOf()) {
            ClassModel classModel = getModelFor(operand);
            if (classModel != null)
                ancestors.add(getModelFor(operand).getSubClasses());
        }
        Collection<ClassModel> intersection = ancestors.get(0);
        for (int i = 1; i != ancestors.size(); i++)
            intersection = intersection(intersection, ancestors.get(i));
        for (ClassModel ancestor : intersection) {
            if (!superClasses.contains(ancestor)) {
                superClasses.add(ancestor);
                ancestor.subClasses.add(this);
            }
        }
    }

    /**
     * Computers the intersection of two sets of classes
     *
     * @param set1 A set of classes
     * @param set2 Another set of classes
     * @return The intersection of the two sets
     */
    private static List<ClassModel> intersection(Collection<ClassModel> set1, Collection<ClassModel> set2) {
        List<ClassModel> result = new ArrayList<>();
        for (ClassModel e : set1)
            if (set2.contains(e))
                result.add(e);
        return result;
    }

    /**
     * Builds the property interfaces
     */
    public void buildInterfaces() {
        List<PropertyModel> inheritedProperties = new ArrayList<>();
        for (ClassModel equivalent : getEquivalencyGroup()) {
            for (ClassModel ancestor : equivalent.getSuperClasses()) {
                for (PropertyModel property : ancestor.getProperties()) {
                    List<PropertyModel> tree = getAllPropertiesFrom(property);
                    for (PropertyModel sub : tree)
                        if (!inheritedProperties.contains(sub))
                            inheritedProperties.add(sub);
                }
            }
        }

        List<PropertyModel> myProperties = new ArrayList<>();
        for (ClassModel equivalent : getEquivalencyGroup()) {
            for (PropertyModel property : equivalent.getProperties()) {
                if (inheritedProperties.contains(property))
                    continue;
                // New Property
                List<PropertyModel> tree = getAllPropertiesFrom(property);
                for (PropertyModel sub : tree) {
                    if (!inheritedProperties.contains(sub)) {
                        propertyInterfaces.add(new PropertyInterface(this, sub));
                        myProperties.add(sub);
                    }
                }
            }
        }

        for (ClassModel equivalent : getEquivalencyGroup()) {
            for (ClassRestriction restriction : equivalent.classe.getAllClassRestrictions()) {
                PropertyModel property = getModelFor(PropertyData.getRestrictedProperty(restriction));
                if (myProperties.contains(property))
                    continue;
                // Restriction does not apply on a property for which this class is the domain
                if (!inheritedProperties.contains(property))
                    continue;
                // Restriction applies on an inherited property
                java.util.List<PropertyModel> tree = getAllPropertiesFrom(property);
                for (PropertyModel sub : tree) {
                    if (!myProperties.contains(sub)) {
                        propertyInterfaces.add(new PropertyInterface(this, sub));
                        myProperties.add(sub);
                    }
                }
            }
        }
    }

    /**
     * Gets all the set of a properties and its descendants
     *
     * @param property A property
     * @return The set of the property and its descendants
     */
    private List<PropertyModel> getAllPropertiesFrom(PropertyModel property) {
        List<PropertyModel> result = new ArrayList<>();
        ClassModel domain = property.getDomain();
        result.add(property);
        for (int i = 0; i != result.size(); i++)
            for (PropertyModel sub : result.get(i).getSubProperties())
                if (domain.getEquivalencyGroup().contains(sub.getDomain()))
                    result.add(sub);
        return result;
    }

    /**
     * Builds the property implementations
     *
     * @param logger The logger to use
     */
    public void buildImplementations(Logger logger) {
        if (isAbstract()) return;
        Map<PropertyModel, List<PropertyInterface>> interfaces = new HashMap<>();
        for (ClassModel equivalent : getEquivalencyGroup()) {
            for (ClassModel ancestor : equivalent.getSuperClasses()) {
                for (PropertyInterface inter : ancestor.getPropertyInterfaces()) {
                    if (!interfaces.containsKey(inter.getProperty()))
                        interfaces.put(inter.getProperty(), new java.util.ArrayList<PropertyInterface>());
                    interfaces.get(inter.getProperty()).add(inter);
                }
            }
            for (PropertyInterface inter : equivalent.getPropertyInterfaces()) {
                if (!interfaces.containsKey(inter.getProperty()))
                    interfaces.put(inter.getProperty(), new java.util.ArrayList<PropertyInterface>());
                interfaces.get(inter.getProperty()).add(inter);
            }
        }

        for (PropertyModel property : interfaces.keySet()) {
            PropertyImplementation implementation = new PropertyImplementation(this, property);
            propertyImplementations.put(property, implementation);
            for (PropertyInterface inter : interfaces.get(property))
                implementation.addInterface(inter);
            implementation.close(logger);
        }
    }

    /**
     * Generates and writes the code for this class as a standalone distribution
     *
     * @param folder The target folder
     * @throws java.io.IOException When an IO error occurs
     */
    public void writeStandalone(String folder) throws IOException {
        Writer writer = Files.getWriter(folder + getName() + ".java");
        writer.append("/* This file has been generated by:\n");
        writer.append("   xOWL Code Generator\n");
        writer.append("*/\n");
        writer.append("package " + getPackage().getFullName() + ";\n");
        writer.append("\n");

        if (!isAbstract())
            writer.append("public class " + getName());
        else
            writer.append("public interface " + getName());

        // writes parency
        boolean first = true;
        for (ClassModel parent : getSuperClasses()) {
            if (first) {
                if (!isAbstract())
                    writer.append(" implements ");
                else
                    writer.append(" extends ");
            } else
                writer.append(", ");
            writer.append(parent.getJavaName());
            first = false;
        }
        writer.append(" {\n");

        if (isAbstract())
            for (PropertyInterface inter : getPropertyInterfaces())
                inter.writeStandalone(writer);
        else
            writeStandaloneImplementations(writer);

        writer.append("}\n");
        writer.close();
    }

    /**
     * Generates and writes the property implementations as a standalone distribution
     *
     * @param writer The writer to write to
     * @throws java.io.IOException When an IO error occurs
     */
    private void writeStandaloneImplementations(Writer writer) throws IOException {
        // writes all Implementations
        for (PropertyImplementation implementation : getPropertyImplementations()) {
            implementation.writeStandalone(writer);
            writer.append("\n");
        }
        // writes all static instances
        for (InstanceModel instance : getStaticInstances()) {
            instance.writeStandalone(writer);
            writer.append("\n");
        }

        // writes constructor
        writer.append("    public " + getName() + "() {\n");

        // writes Implementations' constructors
        for (PropertyImplementation implementation : getPropertyImplementations())
            implementation.writeStandaloneConstructor(writer);
        writer.append("    }\n");
        writer.append("\n");
    }
}
