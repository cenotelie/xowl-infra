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

import fr.cenotelie.commons.utils.logging.Logger;
import org.xowl.infra.lang.owl2.AnonymousIndividual;
import org.xowl.infra.lang.owl2.Ontology;
import org.xowl.infra.lang.runtime.Class;
import org.xowl.infra.lang.runtime.*;
import org.xowl.infra.store.IRIs;
import org.xowl.infra.store.RepositoryDirectSemantics;
import org.xowl.infra.store.Vocabulary;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a model as sets of OWL2 ontologies
 *
 * @author Laurent Wouters
 */
public class Model {
    /**
     * The repository
     */
    private final RepositoryDirectSemantics repository;
    /**
     * The name of the base package
     */
    private final String basePackage;
    /**
     * The packages to generate
     */
    private final Map<Ontology, PackageModel> packages;
    /**
     * The known anonymous classes
     */
    private final Map<Class, ClassModel> anonymousClasses;
    /**
     * The known datatype implementations
     */
    private final Map<Datatype, DatatypeModel> datatypes;
    /**
     * The known static instances
     */
    private final Map<NamedIndividual, InstanceModel> individuals;

    /**
     * Initializes this model
     *
     * @param repository  The base repository
     * @param basePackage The namle of the base package
     */
    public Model(RepositoryDirectSemantics repository, String basePackage) {
        this.repository = repository;
        this.basePackage = basePackage;
        this.packages = new HashMap<>();
        this.anonymousClasses = new HashMap<>();
        this.datatypes = new HashMap<>();
        this.individuals = new HashMap<>();
    }

    /**
     * Gets the repository for this model
     *
     * @return The repository
     */
    public RepositoryDirectSemantics getRepository() {
        return repository;
    }

    /**
     * Gets the name of the base package
     *
     * @return The name of the base package
     */
    public String getBasePackage() {
        return basePackage;
    }

    /**
     * Gets the model for the specified OWL class
     *
     * @param owlClass An OWL class
     * @return The associated class model
     */
    public ClassModel getModelFor(Class owlClass) {
        if (owlClass == null)
            return null;
        if (owlClass.getInterpretationOf() == null) {
            // this is an anonymous class, is it already known?
            if (anonymousClasses.containsKey(owlClass))
                return anonymousClasses.get(owlClass);
            // identify the containing ontology
            Ontology ontology = getOntologyFor(owlClass);
            if (ontology == null)
                return null;
            // create the model
            ClassModel classModel = new ClassModel(packages.get(ontology), owlClass);
            packages.get(ontology).register(owlClass, classModel);
            anonymousClasses.put(owlClass, classModel);
            return classModel;
        } else {
            // look for the appropriate package
            for (PackageModel pack : packages.values()) {
                if (pack.getOWL() == owlClass.getInterpretationOf().getContainedBy())
                    return pack.getModelFor(owlClass);
            }
            return null;
        }
    }

    /**
     * Determines the ontology for the specified anonymous class
     *
     * @param owlClass An anonymous OWL class
     * @return The ontology that will contain the class
     */
    private Ontology getOntologyFor(Class owlClass) {
        List<ClassModel> results = new ArrayList<>();
        if (!owlClass.getAllClassUnionOf().isEmpty()) {
            for (Class operand : owlClass.getAllClassUnionOf()) {
                ClassModel temp = getModelFor(operand);
                if (temp != null)
                    results.add(temp);
            }
        } else if (!owlClass.getAllClassIntersectionOf().isEmpty()) {
            for (Class operand : owlClass.getAllClassIntersectionOf()) {
                ClassModel temp = getModelFor(operand);
                if (temp != null)
                    results.add(temp);
            }
        } else if (!owlClass.getAllClassOneOf().isEmpty()) {
            for (Individual individual : owlClass.getAllClassOneOf()) {
                if (individual instanceof AnonymousIndividual)
                    continue;
                for (Class type : individual.getAllClassifiedBy()) {
                    ClassModel temp = getModelFor(type);
                    if (temp != null)
                        results.add(temp);
                }
            }
        }
        if (results.isEmpty())
            return null;
        return results.get(0).getPackage().getOWL();
    }

    /**
     * Gets the model for the specified OWL property
     *
     * @param owlProperty An OWL property
     * @return The associated model
     */
    public PropertyModel getModelFor(Property owlProperty) {
        for (PackageModel pack : packages.values()) {
            PropertyModel gen = pack.getProperty(owlProperty);
            if (gen != null)
                return gen;
        }
        return null;
    }

    /**
     * Gets the model implementation for the specified OWL datatype
     *
     * @param owlDatatype An OWL datatype
     * @return The associated model implementation
     */
    public DatatypeModel getModelFor(Datatype owlDatatype) {
        if (datatypes.containsKey(owlDatatype))
            return datatypes.get(owlDatatype);
        DatatypeModel datatypeModel = DatatypeModel.get(owlDatatype);
        datatypes.put(owlDatatype, datatypeModel);
        return datatypeModel;
    }

    /**
     * Gets the model for the specified OWL individual
     *
     * @param owlIndividual An OWL individual
     * @return The associated model
     */
    public InstanceModel getModelFor(NamedIndividual owlIndividual) {
        if (individuals.containsKey(owlIndividual))
            return individuals.get(owlIndividual);
        Entity entity = owlIndividual.getInterpretationOf();
        for (Interpretation interpretation : entity.getAllInterpretedAs()) {
            if (interpretation instanceof Class)
                return new InstanceModel(getModelForOWLClass(), owlIndividual);
        }
        if (owlIndividual.getInterpretationOf().getHasIRI().getHasValue().equals(Vocabulary.owlThing))
            return new InstanceModel(null, owlIndividual);
        return null;
    }

    /**
     * Registers a model for an individual
     *
     * @param individual    The individual
     * @param instanceModel The associated model
     */
    public void register(NamedIndividual individual, InstanceModel instanceModel) {
        individuals.put(individual, instanceModel);
    }

    /**
     * Gets the class model for the OWL#Class metaclass
     *
     * @return The corresponding model
     */
    private ClassModel getModelForOWLClass() {
        for (Ontology ontology : packages.keySet()) {
            if (IRIs.OWL2.equals(ontology.getHasIRI().getHasValue())) {
                ReflectionPackage reflectionPackage = (ReflectionPackage) packages.get(ontology);
                return reflectionPackage.getClassClass();
            }
        }
        ReflectionPackage reflectionPackage = new ReflectionPackage(this, repository.resolveOntology(IRIs.OWL2));
        packages.put(repository.resolveOntology(IRIs.OWL2), reflectionPackage);
        return reflectionPackage.getClassClass();
    }

    /**
     * Loads the content of this model
     */
    public void load() {
        for (Ontology ontology : repository.getOntologies()) {
            String uri = ontology.getHasIRI().getHasValue();
            PackageModel packageModel;
            if (IRIs.OWL2.equals(uri))
                packageModel = new ReflectionPackage(this, ontology);
            else
                packageModel = new PackageModel(this, ontology);
            packages.put(ontology, packageModel);
        }
    }

    /**
     * Builds the content to generate
     *
     * @param logger The logger to use
     */
    public void build(Logger logger) {
        // Build properties domain and range and inverses
        for (PackageModel packageModel : packages.values()) {
            for (PropertyModel propertyModel : packageModel.getProperties()) {
                propertyModel.buildDomainRange();
                propertyModel.buildInverses();
            }
        }

        // Build class equivalency network
        for (PackageModel packageModel : packages.values()) {
            for (ClassModel classModel : packageModel.getClasses()) {
                classModel.buildEquivalents();
                classModel.buildRestrictions();
            }
        }
        // Build class hierarchy
        for (PackageModel packageModel : packages.values())
            for (ClassModel classModel : packageModel.getClasses())
                classModel.buildHierarchy();
        for (ClassModel classModel : anonymousClasses.values())
            classModel.buildHierarchy();
        // Finalize hierarchy
        for (PackageModel packageModel : packages.values())
            for (ClassModel classModel : packageModel.getClasses())
                classModel.buildUnionHierarchy();
        for (ClassModel classModel : anonymousClasses.values())
            classModel.buildUnionHierarchy();

        // Build property equivalency network
        for (PackageModel packageModel : packages.values())
            for (PropertyModel propertyModel : packageModel.getProperties())
                propertyModel.buildEquivalents();
        // Build property hierarchy
        for (PackageModel packageModel : packages.values())
            for (PropertyModel propertyModel : packageModel.getProperties())
                propertyModel.buildHierarchy();

        // Build property interfaces and implementation, build property graph
        for (PackageModel packageModel : packages.values())
            for (ClassModel classModel : packageModel.getClasses())
                classModel.buildInterfaces();
        for (ClassModel classModel : anonymousClasses.values())
            classModel.buildInterfaces();
        for (PackageModel packageModel : packages.values())
            for (ClassModel classModel : packageModel.getClasses())
                classModel.buildImplementations(logger);
        for (ClassModel classModel : anonymousClasses.values())
            classModel.buildImplementations(logger);
        for (PackageModel packageModel : packages.values())
            for (ClassModel classModel : packageModel.getClasses())
                for (PropertyImplementation propertyImplementation : classModel.getPropertyImplementations())
                    propertyImplementation.buildPropertyGraph();
        for (ClassModel classModel : anonymousClasses.values())
            for (PropertyImplementation propertyImplementation : classModel.getPropertyImplementations())
                propertyImplementation.buildPropertyGraph();
        for (ClassModel classModel : anonymousClasses.values())
            classModel.rebuildName();
    }

    /**
     * Generates and writes the interface code for this model
     *
     * @param folder The target folder
     * @param header The header to use
     * @throws IOException When an IO error occurs
     */
    public void writeInterface(File folder, String header) throws IOException {
        File target = folder;
        for (String subPackage : basePackage.split("\\."))
            target = new File(target, subPackage);
        if (!target.exists() && !target.mkdirs())
            throw new IOException("Failed to create folder " + target);
        for (PackageModel packageModel : packages.values())
            packageModel.writeInterface(target, header);
    }

    /**
     * Generates and writes the code for this model as a standalone distribution
     *
     * @param folder The target folder
     * @param header The header to use
     * @throws IOException When an IO error occurs
     */
    public void writeStandalone(File folder, String header) throws IOException {
        File target = folder;
        for (String subPackage : basePackage.split("\\."))
            target = new File(target, subPackage);
        if (!target.exists() && !target.mkdirs())
            throw new IOException("Failed to create folder " + target);
        for (PackageModel packageModel : packages.values())
            packageModel.writeStandalone(target, header);
    }
}
