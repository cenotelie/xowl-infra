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

import org.xowl.infra.lang.owl2.AnonymousIndividual;
import org.xowl.infra.lang.owl2.Ontology;
import org.xowl.infra.lang.runtime.Class;
import org.xowl.infra.lang.runtime.*;
import org.xowl.store.Vocabulary;
import org.xowl.store.owl.DirectSemantics;
import org.xowl.infra.utils.logging.Logger;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Represents a model as sets of OWL2 ontologies
 *
 * @author Laurent Wouters
 */
public class Model {
    /**
     * The IRI of the OWL2 ontology
     */
    public static final String OWL = "http://www.w3.org/2002/07/owl";

    /**
     * The repository
     */
    private DirectSemantics repository;
    /**
     * The name of the base package
     */
    private String basePackage;
    /**
     * The packages to generate
     */
    private Map<Ontology, PackageModel> packages;
    /**
     * The known anonymous classes
     */
    private Map<Class, ClassModel> anonymousClasses;
    /**
     * The known datatype implementations
     */
    private Map<Datatype, DatatypeModel> datatypes;
    /**
     * The known static instances
     */
    private Map<NamedIndividual, InstanceModel> individuals;

    /**
     * Initializes this model
     *
     * @param repository  The base repository
     * @param basePackage The namle of the base package
     */
    public Model(DirectSemantics repository, String basePackage) {
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
    public DirectSemantics getRepository() {
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
     * Gets the model for the specified ontology
     *
     * @param ontology An ontology
     * @return The associated model
     */
    public PackageModel getModelFor(Ontology ontology) {
        return packages.get(ontology);
    }

    /**
     * Gets the model for the specified class
     *
     * @param classe A class
     * @return The associated model
     */
    public ClassModel getModelFor(Class classe) {
        if (classe == null)
            return null;
        if (classe.getInterpretationOf() == null) {
            // this is an anonymous class, is it already known?
            if (anonymousClasses.containsKey(classe))
                return anonymousClasses.get(classe);
            // identify the containing ontology
            Ontology ontology = getOntologyFor(classe);
            if (ontology == null)
                return null;
            // create the model
            ClassModel classModel = new ClassModel(packages.get(ontology), classe);
            packages.get(ontology).register(classe, classModel);
            anonymousClasses.put(classe, classModel);
            return classModel;
        } else {
            // look for the appropriate package
            for (PackageModel pack : packages.values()) {
                if (pack.getOWLOntology() == classe.getInterpretationOf().getContainedBy())
                    return pack.getModelFor(classe);
            }
            return null;
        }
    }

    /**
     * Determines the ontology for the specified anonymous class
     *
     * @param classe An anonymous class
     * @return The ontology that will contain the class
     */
    private Ontology getOntologyFor(Class classe) {
        List<ClassModel> results = new ArrayList<>();
        if (!classe.getAllClassUnionOf().isEmpty()) {
            for (Class operand : classe.getAllClassUnionOf()) {
                ClassModel temp = getModelFor(operand);
                if (temp != null)
                    results.add(temp);
            }
        } else if (!classe.getAllClassIntersectionOf().isEmpty()) {
            for (Class operand : classe.getAllClassIntersectionOf()) {
                ClassModel temp = getModelFor(operand);
                if (temp != null)
                    results.add(temp);
            }
        } else if (!classe.getAllClassOneOf().isEmpty()) {
            for (Individual individual : classe.getAllClassOneOf()) {
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
        return results.get(0).getPackage().getOWLOntology();
    }

    /**
     * Gets the model for the specified property
     *
     * @param property A property
     * @return The associated model
     */
    public PropertyModel getModelFor(Property property) {
        for (PackageModel pack : packages.values()) {
            PropertyModel gen = pack.getProperty(property);
            if (gen != null)
                return gen;
        }
        return null;
    }

    /**
     * Gets the model implementation for the specified datatype
     *
     * @param datatype A datatype
     * @return The associated model implementation
     */
    public DatatypeModel getModelFor(Datatype datatype) {
        if (datatypes.containsKey(datatype))
            return datatypes.get(datatype);
        DatatypeModel datatypeModel = DatatypeModel.get(datatype);
        datatypes.put(datatype, datatypeModel);
        return datatypeModel;
    }

    /**
     * Gets the model for the specified individual
     *
     * @param individual An individual
     * @return The associated model
     */
    public InstanceModel getModelFor(NamedIndividual individual) {
        if (individuals.containsKey(individual))
            return individuals.get(individual);
        Entity entity = individual.getInterpretationOf();
        for (Interpretation interpretation : entity.getAllInterpretedAs()) {
            if (interpretation instanceof Class)
                return new InstanceModel(getModelForOWLClass(), individual);
        }
        if (individual.getInterpretationOf().getHasIRI().getHasValue().equals(Vocabulary.owlThing))
            return new InstanceModel(null, individual);
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
            if (OWL.equals(ontology.getHasIRI().getHasValue())) {
                ReflectionPackage reflectionPackage = (ReflectionPackage) packages.get(ontology);
                return reflectionPackage.getClassClass();
            }
        }
        ReflectionPackage reflectionPackage = new ReflectionPackage(this, repository.resolveOntology(OWL));
        packages.put(repository.resolveOntology(OWL), reflectionPackage);
        return reflectionPackage.getClassClass();
    }

    /**
     * Loads the content of this model
     */
    public void load() {
        for (Ontology ontology : repository.getOntologies()) {
            String uri = ontology.getHasIRI().getHasValue();
            PackageModel packageModel;
            if (OWL.equals(uri))
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
    }

    /**
     * Generates and writes the code for this model as a standalone distribution
     *
     * @param folder The target folder
     * @throws java.io.IOException When an IO error occurs
     */
    public void writeStandalone(String folder) throws IOException {
        String[] subs = basePackage.split("\\.");
        for (String sub : subs)
            folder += sub + "/";
        File dir = new File(folder);
        dir.mkdirs();
        for (PackageModel packageModel : packages.values())
            packageModel.writeStandalone(folder);
    }
}
