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

import org.xowl.infra.lang.owl2.Ontology;
import org.xowl.infra.lang.runtime.Class;
import org.xowl.infra.lang.runtime.Entity;
import org.xowl.infra.lang.runtime.Interpretation;
import org.xowl.infra.lang.runtime.Property;
import org.xowl.infra.utils.Files;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.HashMap;

/**
 * Represents the model of Java package for an OWL2 ontology
 *
 * @author Laurent Wouters
 */
public class PackageModel {
    /**
     * The parent model
     */
    protected Model parent;
    /**
     * The ontology associated to this model
     */
    protected Ontology ontology;
    /**
     * The name of this package
     */
    protected String name;
    /**
     * The named classes in this package
     */
    protected java.util.Map<Class, ClassModel> classes;
    /**
     * The anonymous classes in this package
     */
    protected java.util.Map<Class, ClassModel> anonymousClasses;
    /**
     * The properties in this package
     */
    protected java.util.Map<Property, PropertyModel> properties;

    /**
     * Gets the parent model
     *
     * @return The parent model
     */
    public Model getModel() {
        return parent;
    }

    /**
     * Gets the associated OWL2 ontology
     *
     * @return The associated ontology
     */
    public Ontology getOWLOntology() {
        return ontology;
    }

    /**
     * Gets the name of this package
     *
     * @return The name of this package
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the full name of this package
     *
     * @return The full name of this package
     */
    public String getFullName() {
        return parent.getBasePackage() + "." + name;
    }

    /**
     * Gets the model for the specified OWL class
     *
     * @param classe An OWL class
     * @return The associated model
     */
    public ClassModel getModelFor(Class classe) {
        return classes.get(classe);
    }

    /**
     * Gets the models for the named classes in this package
     *
     * @return The models for the named classes
     */
    public Collection<ClassModel> getClasses() {
        return classes.values();
    }

    /**
     * Gets the model for the specified OWL property
     *
     * @param owlProperty An OWL property
     * @return The associated model
     */
    public PropertyModel getProperty(Property owlProperty) {
        return properties.get(owlProperty);
    }

    /**
     * Gets the models for the properties in this package
     *
     * @return The models for the properties
     */
    public Collection<PropertyModel> getProperties() {
        return properties.values();
    }

    /**
     * Initializes this package
     *
     * @param model    The parent model
     * @param ontology The associated ontology
     */
    public PackageModel(Model model, Ontology ontology) {
        this.parent = model;
        this.ontology = ontology;
        String[] parts = ontology.getHasIRI().getHasValue().split("/");
        this.name = parts[parts.length - 1].toLowerCase();
        this.classes = new HashMap<>();
        this.anonymousClasses = new HashMap<>();
        this.properties = new HashMap<>();
        loadEntities();
    }

    /**
     * Loads the entities in this ontology
     */
    protected void loadEntities() {
        for (Entity entity : ontology.getAllContains()) {
            for (Interpretation interpretation : entity.getAllInterpretedAs()) {
                if (interpretation instanceof Class) {
                    classes.put((Class) interpretation, new ClassModel(this, (Class) interpretation));
                } else if (interpretation instanceof Property) {
                    properties.put((Property) interpretation, new PropertyModel(this, (Property) interpretation));
                }
            }
        }
    }

    /**
     * Registers an anonymous class
     *
     * @param classe     The class
     * @param classModel The associated model
     */
    public void register(Class classe, ClassModel classModel) {
        anonymousClasses.put(classe, classModel);
    }

    /**
     * Generates and writes the interface code
     *
     * @param folder The target folder
     * @param header The header to use
     * @throws IOException When an IO error occurs
     */
    public void writeInterface(File folder, String header) throws IOException {
        if ((classes.size() + anonymousClasses.size()) == 0)
            return;
        File target = new File(folder, name);
        if (!target.exists() && !target.mkdirs())
            throw new IOException("Failed to create folder " + target);
        for (ClassModel classModel : classes.values())
            classModel.writeInterface(target, header);
        for (ClassModel classModel : anonymousClasses.values())
            classModel.writeInterface(target, header);
    }

    /**
     * Generates and writes the code for this package as a standalone distribution
     *
     * @param folder The target folder
     * @param header The header to use
     * @throws IOException When an IO error occurs
     */
    public void writeStandalone(File folder, String header) throws IOException {
        if ((classes.size() + anonymousClasses.size()) == 0)
            return;
        File target = new File(folder, "impl");
        if (!target.exists() && !target.mkdirs())
            throw new IOException("Failed to create folder " + target);
        for (ClassModel classModel : classes.values())
            classModel.writeStandalone(target, header);
        for (ClassModel classModel : anonymousClasses.values())
            classModel.writeStandalone(target, header);
        // write the factory
        target = new File(folder, name);
        if (!target.exists() && !target.mkdirs())
            throw new IOException("Failed to create folder " + target);
        writeStandaloneFactory(target, header);
    }

    /**
     * Writes the factory class for the standalone implementation
     *
     * @param folder The target folder
     * @param header The header to use
     * @throws IOException When an IO error occurs
     */
    private void writeStandaloneFactory(File folder, String header) throws IOException {
        String name = getName();
        name = String.valueOf(name.charAt(0)).toUpperCase() + name.substring(1);

        Writer writer = Files.getWriter(new File(folder, name + "Factory.java").getAbsolutePath());
        String[] lines = header.split(Files.LINE_SEPARATOR);
        writer.append("/*******************************************************************************").append(Files.LINE_SEPARATOR);
        for (String line : lines) {
            writer.append(" * ");
            writer.append(line);
            writer.append(Files.LINE_SEPARATOR);
        }
        writer.append(" ******************************************************************************/").append(Files.LINE_SEPARATOR);
        writer.append(Files.LINE_SEPARATOR);
        writer.append("package ").append(getFullName()).append(";").append(Files.LINE_SEPARATOR);
        writer.append(Files.LINE_SEPARATOR);
        writer.append("import ").append(parent.getBasePackage()).append(".impl.*;").append(Files.LINE_SEPARATOR);
        writer.append(Files.LINE_SEPARATOR);
        writer.append("import java.util.*;").append(Files.LINE_SEPARATOR);
        writer.append(Files.LINE_SEPARATOR);

        writer.append("/**").append(Files.LINE_SEPARATOR);
        writer.append(" * The default implementation for the concrete OWL class ").append(getName()).append(Files.LINE_SEPARATOR);
        writer.append(" *").append(Files.LINE_SEPARATOR);
        writer.append(" * @author xOWL code generator").append(Files.LINE_SEPARATOR);
        writer.append(" */").append(Files.LINE_SEPARATOR);
        writer.append("public class ").append(name).append("Factory {").append(Files.LINE_SEPARATOR);

        for (ClassModel classModel : classes.values()) {
            if (classModel.isAbstract())
                continue;
            writer.append("    /**").append(Files.LINE_SEPARATOR);
            writer.append("     * Creates a new instance of ").append(classModel.getName()).append(Files.LINE_SEPARATOR);
            writer.append("     *").append(Files.LINE_SEPARATOR);
            writer.append("     * @return A new instance of ").append(classModel.getName()).append(Files.LINE_SEPARATOR);
            writer.append("     */").append(Files.LINE_SEPARATOR);
            writer.append("    public static ").append(classModel.getName()).append(" new").append(classModel.getName()).append("() {").append(Files.LINE_SEPARATOR);
            writer.append("        return new ").append(classModel.getJavaImplName()).append("();").append(Files.LINE_SEPARATOR);
            writer.append("    }").append(Files.LINE_SEPARATOR);
            writer.append(Files.LINE_SEPARATOR);
        }

        writer.append("}").append(Files.LINE_SEPARATOR);
        writer.close();
    }
}
