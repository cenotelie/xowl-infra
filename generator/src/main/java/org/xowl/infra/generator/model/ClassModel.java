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

import org.xowl.infra.lang.runtime.Class;
import org.xowl.infra.lang.runtime.*;
import org.xowl.infra.utils.Files;
import org.xowl.infra.utils.logging.Logger;

import java.io.File;
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
     * The parent package
     */
    private final PackageModel parent;
    /**
     * The associated OWL class
     */
    private final Class owlClass;
    /**
     * The name of this class
     */
    private String name;
    /**
     * Whether this class must be explicitly imported
     */
    private final boolean mustExplicitlyImport;
    /**
     * The equivalent classes
     */
    private final List<ClassModel> equivalents;
    /**
     * The super classes
     */
    private final List<ClassModel> superClasses;
    /**
     * The sub classes
     */
    private final List<ClassModel> subClasses;
    /**
     * The properties for which this class is the domain
     */
    private final List<PropertyModel> properties;
    /**
     * The property interfaces owned by this class
     */
    private final List<PropertyInterface> propertyInterfaces;
    /**
     * The property implementations owned by this class
     */
    private final Map<PropertyModel, PropertyImplementation> propertyImplementations;
    /**
     * The static instances of this class
     */
    private final List<InstanceModel> staticInstances;

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
    public Class getOWL() {
        return owlClass;
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
     * @param from The requesting entity
     * @return The name of this class in Java
     */
    public String getJavaName(ClassModel from) {
        if (from.parent == this.parent) {
            return name;
        }
        return parent.getFullName() + "." + name;
    }

    /**
     * Gets the implementation name in Java
     *
     * @return The implementation name in Java
     */
    public String getJavaImplName() {
        String result = parent.getName();
        result = String.valueOf(result.charAt(0)).toUpperCase() + result.substring(1);
        return result + name + "Impl";
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
     * Initializes this class
     *
     * @param packageModel The parent package
     * @param owlClass     The associated OWL class
     */
    public ClassModel(PackageModel packageModel, Class owlClass) {
        this(packageModel, owlClass, getNameFromEntity(owlClass));
    }

    /**
     * Initializes this class
     *
     * @param packageModel The parent package
     * @param owlClass     The associated OWL class
     * @param name         The name for this class
     */
    public ClassModel(PackageModel packageModel, Class owlClass, String name) {
        this.parent = packageModel;
        this.owlClass = owlClass;
        this.name = name;
        this.mustExplicitlyImport = existsInJavaLang(name);
        this.equivalents = new ArrayList<>();
        this.superClasses = new ArrayList<>();
        this.subClasses = new ArrayList<>();
        this.properties = new ArrayList<>();
        this.propertyInterfaces = new ArrayList<>();
        this.propertyImplementations = new HashMap<>();
        this.staticInstances = new ArrayList<>();
        for (Individual individual : owlClass.getAllClassifies()) {
            if (individual instanceof NamedIndividual) {
                InstanceModel instanceModel = new InstanceModel(this, (NamedIndividual) individual);
                packageModel.getModel().register((NamedIndividual) individual, instanceModel);
                staticInstances.add(instanceModel);
            }
        }
    }

    /**
     * Gets the name of the class from the entity
     *
     * @param classe The OWL class
     * @return The name for the class model
     */
    private static String getNameFromEntity(Class classe) {
        if (classe.getInterpretationOf() == null)
            return null;
        String iri = classe.getInterpretationOf().getHasIRI().getHasValue();
        String[] parts = iri.split("#");
        return parts[parts.length - 1];
    }

    /**
     * Gets whether the specified name is the name of a class in the java.lang package
     *
     * @param name The name to check
     * @return true if this is the name of a class in the java.lang package
     */
    private static boolean existsInJavaLang(String name) {
        if (name == null)
            return false;
        try {
            java.lang.Class result = java.lang.Class.forName("java.lang." + name);
            if (result != null)
                return true;
        } catch (ClassNotFoundException exception) {
            // do nothing
        }
        return false;
    }

    /**
     * Builds the equivalency group
     */
    public void buildEquivalents() {
        for (Class equivalent : owlClass.getAllClassEquivalentTo()) {
            ClassModel classModel = parent.getModel().getModelFor(equivalent);
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
        for (ClassRestriction restriction : owlClass.getAllClassRestrictions()) {
            if (restriction instanceof ObjectAllValuesFrom) {
                ObjectAllValuesFrom objectAllValuesFrom = (ObjectAllValuesFrom) restriction;
                parent.getModel().getModelFor(objectAllValuesFrom.getClasse());
            } else if (restriction instanceof ObjectSomeValuesFrom) {
                ObjectSomeValuesFrom objectSomeValuesFrom = (ObjectSomeValuesFrom) restriction;
                parent.getModel().getModelFor(objectSomeValuesFrom.getClasse());
            }
        }
    }

    /**
     * Builds the class hierarchy
     */
    public void buildHierarchy() {
        for (Class owlSubClass : owlClass.getAllSubClassOf()) {
            ClassModel classModel = parent.getModel().getModelFor(owlSubClass);
            if (classModel != null && !classModel.name.equals("Thing") && !superClasses.contains(classModel)) {
                superClasses.add(classModel);
                classModel.subClasses.add(this);
            }
        }
        for (Class operand : owlClass.getAllClassUnionOf()) {
            ClassModel classModel = parent.getModel().getModelFor(operand);
            if (classModel != null && !subClasses.contains(classModel)) {
                subClasses.add(classModel);
                classModel.superClasses.add(this);
            }
        }
        for (Class operand : owlClass.getAllClassIntersectionOf()) {
            ClassModel classModel = parent.getModel().getModelFor(operand);
            if (classModel != null && !superClasses.contains(classModel)) {
                superClasses.add(classModel);
                classModel.subClasses.add(this);
            }
        }
        Collections.sort(superClasses, new Comparator<ClassModel>() {
            @Override
            public int compare(ClassModel c1, ClassModel c2) {
                return c1.name.compareTo(c2.name);
            }
        });
    }

    /**
     * Builds the class hierarchy for union classes
     */
    public void buildUnionHierarchy() {
        if (owlClass.getAllClassUnionOf().isEmpty())
            return;
        List<Collection<ClassModel>> ancestors = new ArrayList<>();
        for (Class operand : owlClass.getAllClassUnionOf()) {
            ClassModel classModel = parent.getModel().getModelFor(operand);
            if (classModel != null)
                ancestors.add(parent.getModel().getModelFor(operand).getSubClasses());
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
        Collections.sort(superClasses, new Comparator<ClassModel>() {
            @Override
            public int compare(ClassModel c1, ClassModel c2) {
                return c1.name.compareTo(c2.name);
            }
        });
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
            for (ClassRestriction restriction : equivalent.owlClass.getAllClassRestrictions()) {
                PropertyModel property = parent.getModel().getModelFor(PropertyData.getRestrictedProperty(restriction));
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

        Collections.sort(propertyInterfaces, new Comparator<PropertyInterface>() {
            @Override
            public int compare(PropertyInterface property1, PropertyInterface property2) {
                return property1.getModel().getName().compareTo(property2.getModel().getName());
            }
        });
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
                    if (!interfaces.containsKey(inter.getModel()))
                        interfaces.put(inter.getModel(), new java.util.ArrayList<PropertyInterface>());
                    interfaces.get(inter.getModel()).add(inter);
                }
            }
            for (PropertyInterface inter : equivalent.getPropertyInterfaces()) {
                if (!interfaces.containsKey(inter.getModel()))
                    interfaces.put(inter.getModel(), new java.util.ArrayList<PropertyInterface>());
                interfaces.get(inter.getModel()).add(inter);
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
     * Rebuilds the name of this class (if anonymous)
     */
    public void rebuildName() {
        if (name != null)
            return;
        // look for a property for which this is the domain
        for (PropertyInterface inter : propertyInterfaces) {
            String genName = "DomainOf" + inter.getJavaName();
            if (parent.isNameFree(genName)) {
                this.name = genName;
                return;
            }
        }
        // look for a property for which this is the range
        for (ClassModel model : parent.getClasses()) {
            for (PropertyImplementation property : model.getPropertyImplementations()) {
                if (property.getRange() == this) {
                    String genName = "RangeOf" + property.getJavaName();
                    if (parent.isNameFree(genName)) {
                        this.name = genName;
                        return;
                    }
                }
            }
        }
        int counter = 0;
        while (true) {
            String genName = "AnonymousClass" + counter;
            if (parent.isNameFree(genName)) {
                this.name = genName;
                return;
            }
        }
    }

    /**
     * Generates and writes the code for the interface of this OWL class
     *
     * @param folder The target folder
     * @param header The header to use
     * @throws IOException When an IO error occurs
     */
    public void writeInterface(File folder, String header) throws IOException {
        String classIRI = owlClass.getInterpretationOf() != null ? owlClass.getInterpretationOf().getHasIRI().getHasValue() : null;

        Writer writer = Files.getWriter(new File(folder, name + ".java").getAbsolutePath());
        String[] lines = header.split(Files.LINE_SEPARATOR);
        writer.append("/*******************************************************************************").append(Files.LINE_SEPARATOR);
        for (String line : lines) {
            writer.append(" * ");
            writer.append(line);
            writer.append(Files.LINE_SEPARATOR);
        }
        writer.append(" ******************************************************************************/").append(Files.LINE_SEPARATOR);
        writer.append(Files.LINE_SEPARATOR);
        writer.append("package ").append(parent.getFullName()).append(";").append(Files.LINE_SEPARATOR);
        writer.append(Files.LINE_SEPARATOR);
        writer.append("import java.util.*;").append(Files.LINE_SEPARATOR);
        writer.append(Files.LINE_SEPARATOR);

        writer.append("/**").append(Files.LINE_SEPARATOR);
        writer.append(" * Represents the base interface for ").append(name).append(Files.LINE_SEPARATOR);
        if (classIRI != null)
            writer.append(" * Original OWL class is ").append(classIRI).append(Files.LINE_SEPARATOR);
        writer.append(" *").append(Files.LINE_SEPARATOR);
        writer.append(" * @author xOWL code generator").append(Files.LINE_SEPARATOR);
        writer.append(" */").append(Files.LINE_SEPARATOR);
        writer.append("public interface ").append(name);

        boolean first = true;
        for (ClassModel parent : superClasses) {
            if (first) {
                writer.append(" extends ");
            } else
                writer.append(", ");
            writer.append(parent.getJavaName(this));
            first = false;
        }
        writer.append(" {").append(Files.LINE_SEPARATOR);

        for (PropertyInterface inter : getPropertyInterfaces())
            inter.writeInterface(writer);

        writer.append("}").append(Files.LINE_SEPARATOR);
        writer.close();
    }

    /**
     * Generates and writes the code for the standalone implementation of this OWL class
     *
     * @param folder The target folder
     * @param header The header to use
     * @throws IOException When an IO error occurs
     */
    public void writeStandalone(File folder, String header) throws IOException {
        if (isAbstract())
            return;

        String nameImpl = getJavaImplName();
        String classIRI = owlClass.getInterpretationOf() != null ? owlClass.getInterpretationOf().getHasIRI().getHasValue() : null;
        List<ClassModel> explicitImports = new ArrayList<>();
        for (ClassModel model : parent.getClasses()) {
            if (model.mustExplicitlyImport)
                explicitImports.add(model);
        }
        Collections.sort(explicitImports, new Comparator<ClassModel>() {
            @Override
            public int compare(ClassModel c1, ClassModel c2) {
                return c1.name.compareTo(c2.name);
            }
        });

        Writer writer = Files.getWriter(new File(folder, nameImpl + ".java").getAbsolutePath());
        String[] lines = header.split(Files.LINE_SEPARATOR);
        writer.append("/*******************************************************************************").append(Files.LINE_SEPARATOR);
        for (String line : lines) {
            writer.append(" * ");
            writer.append(line);
            writer.append(Files.LINE_SEPARATOR);
        }
        writer.append(" ******************************************************************************/").append(Files.LINE_SEPARATOR);
        writer.append(Files.LINE_SEPARATOR);
        writer.append("package ").append(parent.getModel().getBasePackage()).append(".impl;").append(Files.LINE_SEPARATOR);
        writer.append(Files.LINE_SEPARATOR);
        writer.append("import ").append(parent.getFullName()).append(".*;").append(Files.LINE_SEPARATOR);
        for (ClassModel importedClass : explicitImports)
            writer.append("import ").append(parent.getFullName()).append(".").append(importedClass.name).append(";").append(Files.LINE_SEPARATOR);
        writer.append(Files.LINE_SEPARATOR);
        writer.append("import java.util.*;").append(Files.LINE_SEPARATOR);
        writer.append(Files.LINE_SEPARATOR);

        writer.append("/**").append(Files.LINE_SEPARATOR);
        writer.append(" * The default implementation for ").append(name).append(Files.LINE_SEPARATOR);
        if (classIRI != null)
            writer.append(" * Original OWL class is ").append(classIRI).append(Files.LINE_SEPARATOR);
        writer.append(" *").append(Files.LINE_SEPARATOR);
        writer.append(" * @author xOWL code generator").append(Files.LINE_SEPARATOR);
        writer.append(" */").append(Files.LINE_SEPARATOR);
        writer.append("public class ").append(nameImpl).append(" implements ").append(name).append(" {").append(Files.LINE_SEPARATOR);

        List<PropertyImplementation> implementations = new ArrayList<>(getPropertyImplementations());
        Collections.sort(implementations, new Comparator<PropertyImplementation>() {
            @Override
            public int compare(PropertyImplementation property1, PropertyImplementation property2) {
                return property1.getModel().getName().compareTo(property2.getModel().getName());
            }
        });

        // writes all Implementations
        for (PropertyImplementation implementation : implementations) {
            implementation.writeStandalone(writer);
        }
        // writes all static instances
        for (InstanceModel instance : getStaticInstances()) {
            instance.writeStandalone(writer);
        }

        // writes constructor
        writer.append("    /**").append(Files.LINE_SEPARATOR);
        writer.append("     * Constructor for the implementation of ").append(name).append(Files.LINE_SEPARATOR);
        writer.append("     */").append(Files.LINE_SEPARATOR);
        writer.append("    public ").append(nameImpl).append("() {").append(Files.LINE_SEPARATOR);
        for (PropertyImplementation implementation : implementations) {
            implementation.writeStandaloneConstructor(writer);
        }
        writer.append("    }").append(Files.LINE_SEPARATOR);
        writer.append("}").append(Files.LINE_SEPARATOR);
        writer.close();
    }
}
