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

package org.xowl.infra.store;

import org.xowl.infra.lang.owl2.IRI;
import org.xowl.infra.lang.owl2.Ontology;
import org.xowl.infra.store.loaders.*;
import org.xowl.infra.store.owl.OWLQueryEngine;
import org.xowl.infra.store.owl.OWLRuleEngine;
import org.xowl.infra.store.rdf.RDFQueryEngine;
import org.xowl.infra.store.rdf.RDFRuleEngine;
import org.xowl.infra.store.storage.NodeManager;
import org.xowl.infra.store.writers.*;
import org.xowl.infra.utils.Files;
import org.xowl.infra.utils.logging.Logger;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.jar.JarFile;

/**
 * Represents a repository of xOWL ontologies
 * This class does not specify the storage method for the ontologies.
 *
 * @author Laurent Wouters
 *         Modification
 * @author Stephen Creff
 */
public abstract class Repository {
    /**
     * Supported http scheme for the physical resources to load
     */
    public static final String SCHEME_HTTP = "http://";
    /**
     * Supported resource scheme for the physical resources to load
     */
    public static final String SCHEME_RESOURCE = "resource://";
    /**
     * Supported jar scheme for the physical resources to load
     */
    public static final String SCHEME_JAR = "jar://";
    /**
     * Supported file scheme for the physical resources to load
     */
    public static final String SCHEME_FILE = "file://";

    /**
     * Supported N-Triples syntax
     */
    public static final String SYNTAX_NTRIPLES = "application/n-triples";
    /**
     * File extension for the N-Triples syntax
     */
    public static final String SYNTAX_NTRIPLES_EXTENSION = ".nt";
    /**
     * Supported N-Quads syntax
     */
    public static final String SYNTAX_NQUADS = "application/n-quads";
    /**
     * File extension for the N-Quads syntax
     */
    public static final String SYNTAX_NQUADS_EXTENSION = ".nq";
    /**
     * Supported Turtle syntax
     */
    public static final String SYNTAX_TURTLE = "text/turtle";
    /**
     * File extension for the Turtle syntax
     */
    public static final String SYNTAX_TURTLE_EXTENSION = ".ttl";
    /**
     * Supported RDF Transform syntax
     */
    public static final String SYNTAX_RDFT = "application/x-xowl-rdft";
    /**
     * File extension for the RDF Transform syntax
     */
    public static final String SYNTAX_RDFT_EXTENSION = ".rdft";
    /**
     * Supported RDF/XML syntax
     */
    public static final String SYNTAX_RDFXML = "application/rdf+xml";
    /**
     * File extension for the RDF/XML syntax
     */
    public static final String SYNTAX_RDFXML_EXTENSION = ".rdf";
    /**
     * Supported JSON-LD syntax
     */
    public static final String SYNTAX_JSON_LD = "application/ld+json";
    /**
     * File extension for the JSON-LD syntax
     */
    public static final String SYNTAX_JSON_LD_EXTENSION = ".jsonld";
    /**
     * Supported TriG syntax
     */
    public static final String SYNTAX_TRIG = "application/trig";
    /**
     * File extension for the TriG syntax
     */
    public static final String SYNTAX_TRIG_EXTENSION = ".trig";
    /**
     * Supported Functional OWL2 syntax
     */
    public static final String SYNTAX_FUNCTIONAL_OWL2 = "text/owl-functional";
    /**
     * File extension for the Functional OWL2 syntax
     */
    public static final String SYNTAX_FUNCTIONAL_OWL2_EXTENSION_A = ".ofn";
    /**
     * File extension for the Functional OWL2 syntax
     */
    public static final String SYNTAX_FUNCTIONAL_OWL2_EXTENSION_B = ".fs";
    /**
     * Supported OWL/XML OWL2 syntax
     */
    public static final String SYNTAX_OWLXML = "application/owl+xml";
    /**
     * File extension for the OWL/XML OWL2 syntax
     */
    public static final String SYNTAX_OWLXML_EXTENSION_A = ".owx";
    /**
     * File extension for the OWL/XML OWL2 syntax
     */
    public static final String SYNTAX_OWLXML_EXTENSION_B = ".owl";
    /**
     * Supported Functional xOWL syntax
     */
    public static final String SYNTAX_XOWL = "application/x-xowl";
    /**
     * File extension for the Functional xOWL syntax
     */
    public static final String SYNTAX_XOWL_EXTENSION = ".xowl";

    /**
     * Determines the syntax for the specified resource
     *
     * @param resource The resource to identify
     * @return The recognized syntax
     */
    public static String getSyntax(String resource) {
        if (resource.endsWith(SYNTAX_NTRIPLES_EXTENSION))
            return SYNTAX_NTRIPLES;
        if (resource.endsWith(SYNTAX_NQUADS_EXTENSION))
            return SYNTAX_NQUADS;
        if (resource.endsWith(SYNTAX_TURTLE_EXTENSION))
            return SYNTAX_TURTLE;
        if (resource.endsWith(SYNTAX_RDFT_EXTENSION))
            return SYNTAX_RDFT;
        if (resource.endsWith(SYNTAX_RDFXML_EXTENSION))
            return SYNTAX_RDFXML;
        if (resource.endsWith(SYNTAX_JSON_LD_EXTENSION))
            return SYNTAX_JSON_LD;
        if (resource.endsWith(SYNTAX_TRIG_EXTENSION))
            return SYNTAX_TRIG;
        if (resource.endsWith(SYNTAX_FUNCTIONAL_OWL2_EXTENSION_A) || resource.endsWith(SYNTAX_FUNCTIONAL_OWL2_EXTENSION_B))
            return SYNTAX_FUNCTIONAL_OWL2;
        if (resource.endsWith(SYNTAX_OWLXML_EXTENSION_A) || resource.endsWith(SYNTAX_OWLXML_EXTENSION_B))
            return SYNTAX_OWLXML;
        if (resource.endsWith(SYNTAX_XOWL_EXTENSION))
            return SYNTAX_XOWL;
        // TODO: try to look into the file to determine the syntax
        return null;
    }

    /**
     * The loader of evaluator services
     */
    private static ServiceLoader<Evaluator> SERVICE_EVALUATOR = ServiceLoader.load(Evaluator.class);

    /**
     * Gets the default evaluator
     *
     * @return The default evaluator
     */
    public static Evaluator getDefaultEvaluator() {
        Iterator<Evaluator> services = SERVICE_EVALUATOR.iterator();
        return services.hasNext() ? services.next() : null;
    }


    /**
     * The IRI mapper
     */
    protected final IRIMapper mapper;
    /**
     * The loaded resources
     */
    protected final Map<String, Ontology> resources;
    /**
     * The loaded ontologies by IRI
     */
    protected final Map<String, Ontology> ontologies;
    /**
     * The remaining dependencies
     */
    protected final List<String> dependencies;
    /**
     * The evaluator to use
     */
    protected final Evaluator evaluator;
    /**
     * The entailment regime
     */
    protected EntailmentRegime regime;

    /**
     * Gets the IRI mapper used by this repository
     *
     * @return The IRI mapper
     */
    public IRIMapper getIRIMapper() {
        return mapper;
    }

    /**
     * Initializes this repository
     *
     * @param mapper    The IRI mapper to use
     * @param evaluator The evaluator to use
     */
    public Repository(IRIMapper mapper, Evaluator evaluator) {
        this.mapper = mapper;
        this.resources = new HashMap<>();
        this.ontologies = new HashMap<>();
        this.dependencies = new ArrayList<>();
        this.evaluator = evaluator;
        this.regime = EntailmentRegime.none;
    }

    /**
     * Gets the evaluator used by this repository
     *
     * @return The evaluator used by this repository
     */
    public Evaluator getEvaluator() {
        return evaluator;
    }

    /**
     * Gets the ontology with the specified IRI, or null if it is not present
     *
     * @param iri An IRI
     * @return The corresponding ontology, or null if it is not present
     */
    public Ontology getOntology(String iri) {
        return ontologies.get(iri);
    }

    /**
     * Gets the known ontologies
     *
     * @return The known ontologies
     */
    public Collection<Ontology> getOntologies() {
        return ontologies.values();
    }

    /**
     * Gets the associated OWL query engine, if any
     *
     * @return The associated OWL query engine, or null if there is none
     */
    public abstract OWLQueryEngine getOWLQueryEngine();

    /**
     * Gets the associated RDF query engine, if any
     *
     * @return The associated RDF query engine, or null if there is none
     */
    public abstract RDFQueryEngine getRDFQueryEngine();

    /**
     * Gets the associated OWL rule engine, if any
     *
     * @return The associated OWL rule engine, or null if there is none
     */
    public abstract OWLRuleEngine getOWLRuleEngine();

    /**
     * Gets the associated RDF rule engine, if any
     *
     * @return The associated RDF rule engine, or null if there is none
     */
    public abstract RDFRuleEngine getRDFRuleEngine();

    /**
     * Gets the current entailment regime
     *
     * @return The current entailment regime
     */
    public EntailmentRegime getEntailmentRegime() {
        return regime;
    }

    /**
     * Sets the entailment regime
     *
     * @param regime The entailment regime to use
     * @throws IllegalArgumentException When the specified entailment regime cannot be set
     * @throws IOException              When an IO operation fails
     */
    public abstract void setEntailmentRegime(EntailmentRegime regime) throws Exception;

    /**
     * Gets a node manager for this repository
     *
     * @return A node manager for this repository
     */
    protected abstract NodeManager getNodeManager();

    /**
     * Gets an IRI for the specified value
     *
     * @param value The value of an IRI
     * @return The IRI
     */
    public IRI getIRI(String value) {
        IRI iri = new IRI();
        iri.setHasValue(value);
        return iri;
    }

    /**
     * Resolves an ontology for the specified IRI
     *
     * @param iri An IRI
     * @return The corresponding ontology
     */
    public Ontology resolveOntology(String iri) {
        Ontology ontology = ontologies.get(iri);
        if (ontology == null) {
            ontology = new Ontology();
            ontology.setHasIRI(getIRI(iri));
            ontologies.put(iri, ontology);
        }
        return ontology;
    }

    /**
     * Loads a resource and resolves its dependencies
     *
     * @param logger      The logger to use
     * @param resourceIRI The resource's IRI
     * @return The loaded ontology
     * @throws Exception   When an error occurred during the operation
     * @throws IOException When the reader cannot be created
     */
    public Ontology load(Logger logger, String resourceIRI) throws Exception {
        return load(logger, resourceIRI, resourceIRI, false);
    }

    /**
     * Loads a resource and resolves its dependencies
     *
     * @param logger      The logger to use
     * @param resourceIRI The resource's IRI
     * @param ontologyIRI The IRI of the ontology within the document
     * @param forceReload Whether to force the reloading of the resource
     * @return The loaded ontology
     * @throws Exception   When an error occurred during the operation
     * @throws IOException When the reader cannot be created
     */
    public Ontology load(Logger logger, String resourceIRI, String ontologyIRI, boolean forceReload) throws Exception {
        Ontology result = loadResource(logger, resourceIRI, ontologyIRI, forceReload);
        while (!dependencies.isEmpty()) {
            List<String> batch = new ArrayList<>(dependencies);
            dependencies.clear();
            for (String dependency : batch) {
                loadResource(logger, dependency, dependency, false);
            }
        }
        return result;
    }

    /**
     * Loads data from the specified reader
     *
     * @param logger      The logger to use
     * @param reader      The reader to use
     * @param resourceIRI The resource's IRI
     * @param ontologyIRI The IRI of the ontology within the document
     * @param syntax      The resource's syntax
     * @return The loaded ontology
     * @throws Exception   When an error occurred during the operation
     * @throws IOException When the reader cannot be created
     */
    public Ontology load(Logger logger, Reader reader, String resourceIRI, String ontologyIRI, String syntax) throws Exception {
        Ontology result = loadResource(logger, reader, resourceIRI, ontologyIRI, syntax);
        while (!dependencies.isEmpty()) {
            List<String> batch = new ArrayList<>(dependencies);
            dependencies.clear();
            for (String dependency : batch) {
                loadResource(logger, dependency, dependency, false);
            }
        }
        return result;
    }

    /**
     * Exports a resource
     *
     * @param logger   The logger to use
     * @param ontology The content to export
     * @param iri      The resource's IRI
     * @throws Exception                When an error occurred during the operation
     * @throws IllegalArgumentException When the syntax is not supported
     */
    public void export(Logger logger, Ontology ontology, String iri) throws Exception {
        String resource = mapper.get(iri);
        if (resource == null) {
            logger.error("Cannot identify the location of " + iri);
            return;
        }
        try (Writer writer = getWriterFor(resource)) {
            String syntax = getSyntax(resource);
            if (syntax == null)
                throw new IOException("Failed to determine the syntax of resource " + resource);
            exportResource(logger, writer, ontology, syntax);
        }
    }

    /**
     * Exports all stored ontologies into a resource
     *
     * @param logger The logger to use
     * @param iri    The resource's IRI
     * @throws Exception                When an error occurred during the operation
     * @throws IllegalArgumentException When the syntax is not supported
     */
    public void exportAll(Logger logger, String iri) throws Exception {
        String resource = mapper.get(iri);
        if (resource == null) {
            logger.error("Cannot identify the location of " + iri);
            return;
        }
        try (Writer writer = getWriterFor(resource)) {
            String syntax = getSyntax(resource);
            if (syntax == null)
                throw new IOException("Failed to determine the syntax of resource " + resource);
            exportResource(logger, writer, iri, syntax);
        }
    }

    /**
     * Loads a resource
     *
     * @param logger      The logger to use
     * @param resourceIRI The resource's IRI
     * @param ontologyIRI The IRI of the ontology within the document
     * @param forceReload Whether to force the reloading of the resource
     * @return The loaded ontology
     * @throws Exception   When an error occurred during the operation
     * @throws IOException When the reader cannot be created
     */
    private Ontology loadResource(Logger logger, String resourceIRI, String ontologyIRI, boolean forceReload) throws Exception {
        String resource = mapper.get(resourceIRI);
        if (resource == null)
            throw new IOException("Cannot identify the location of " + resourceIRI);
        if (!forceReload) {
            Ontology ontology = resources.get(resourceIRI);
            if (ontology != null)
                // the resource is already loaded
                return ontology;
        }
        try (Reader reader = getReaderFor(resource)) {
            String syntax = getSyntax(resource);
            if (syntax == null)
                throw new IOException("Failed to determine the syntax of resource " + resource);
            return loadResource(logger, reader, resourceIRI, ontologyIRI, syntax);
        }
    }

    /**
     * Loads a resource
     *
     * @param logger      The logger to use
     * @param reader      The input reader
     * @param resourceIRI The resource's IRI
     * @param ontologyIRI The IRI of the ontology within the document
     * @param syntax      The resource's syntax
     * @return The loaded ontology
     * @throws Exception                When an error occurred during the operation
     * @throws IllegalArgumentException When the syntax is not supported
     */
    private Ontology loadResource(Logger logger, Reader reader, String resourceIRI, String ontologyIRI, String syntax) throws Exception {
        switch (syntax) {
            case SYNTAX_NTRIPLES: {
                Loader loader = new NTriplesLoader(getNodeManager());
                RDFLoaderResult input = loader.loadRDF(logger, reader, resourceIRI, ontologyIRI);
                return input == null ? null : loadResourceRDF(logger, resourceIRI, ontologyIRI, input);
            }
            case SYNTAX_NQUADS: {
                Loader loader = new NQuadsLoader(getNodeManager());
                RDFLoaderResult input = loader.loadRDF(logger, reader, resourceIRI, ontologyIRI);
                return input == null ? null : loadResourceRDF(logger, resourceIRI, ontologyIRI, input);
            }
            case SYNTAX_TURTLE: {
                Loader loader = new TurtleLoader(getNodeManager());
                RDFLoaderResult input = loader.loadRDF(logger, reader, resourceIRI, ontologyIRI);
                return input == null ? null : loadResourceRDF(logger, resourceIRI, ontologyIRI, input);
            }
            case SYNTAX_RDFT: {
                Loader loader = new RDFTLoader(getNodeManager());
                RDFLoaderResult input = loader.loadRDF(logger, reader, resourceIRI, ontologyIRI);
                return input == null ? null : loadResourceRDF(logger, resourceIRI, ontologyIRI, input);
            }
            case SYNTAX_RDFXML: {
                Loader loader = new RDFXMLLoader(getNodeManager());
                RDFLoaderResult input = loader.loadRDF(logger, reader, resourceIRI, ontologyIRI);
                return input == null ? null : loadResourceRDF(logger, resourceIRI, ontologyIRI, input);
            }
            case SYNTAX_JSON_LD: {
                Loader loader = new JSONLDLoader(getNodeManager()) {
                    @Override
                    protected Reader getReaderFor(Logger logger, String iri) {
                        String resource = mapper.get(iri);
                        if (resource == null) {
                            logger.error("Cannot identify the location of " + iri);
                            return null;
                        }
                        try {
                            return Repository.getReaderFor(resource);
                        } catch (IOException ex) {
                            logger.error(ex);
                            return null;
                        }
                    }
                };
                RDFLoaderResult input = loader.loadRDF(logger, reader, resourceIRI, ontologyIRI);
                return input == null ? null : loadResourceRDF(logger, resourceIRI, ontologyIRI, input);
            }
            case SYNTAX_TRIG: {
                Loader loader = new TriGLoader(getNodeManager());
                RDFLoaderResult input = loader.loadRDF(logger, reader, resourceIRI, ontologyIRI);
                return input == null ? null : loadResourceRDF(logger, resourceIRI, ontologyIRI, input);
            }
            case SYNTAX_FUNCTIONAL_OWL2: {
                Loader loader = new FunctionalOWL2Loader();
                OWLLoaderResult input = loader.loadOWL(logger, reader, resourceIRI);
                return input == null ? null : loadResourceOWL(logger, resourceIRI, input);
            }
            case SYNTAX_OWLXML: {
                Loader loader = new OWLXMLLoader();
                OWLLoaderResult input = loader.loadOWL(logger, reader, resourceIRI);
                return input == null ? null : loadResourceOWL(logger, resourceIRI, input);
            }
            case SYNTAX_XOWL: {
                Loader loader = new XOWLLoader();
                OWLLoaderResult input = loader.loadOWL(logger, reader, resourceIRI);
                return input == null ? null : loadResourceOWL(logger, resourceIRI, input);
            }
            default:
                throw new IllegalArgumentException("Unsupported syntax: " + syntax);
        }
    }

    /**
     * Exports a resource
     *
     * @param logger   The logger to use
     * @param writer   The output writer
     * @param ontology The ontology to export
     * @param syntax   The resource's syntax
     * @throws Exception                When an error occurred during the operation
     * @throws IllegalArgumentException When the syntax is not supported
     */
    private void exportResource(Logger logger, Writer writer, Ontology ontology, String syntax) throws Exception {
        switch (syntax) {
            case SYNTAX_NTRIPLES: {
                RDFSerializer serializer = new NTripleSerializer(writer);
                exportResourceRDF(logger, ontology, serializer);
                break;
            }
            case SYNTAX_NQUADS: {
                RDFSerializer serializer = new NQuadsSerializer(writer);
                exportResourceRDF(logger, ontology, serializer);
                break;
            }
            case SYNTAX_TURTLE: {
                RDFSerializer serializer = new TurtleSerializer(writer);
                exportResourceRDF(logger, ontology, serializer);
                break;
            }
            case SYNTAX_TRIG: {
                RDFSerializer serializer = new TriGSerializer(writer);
                exportResourceRDF(logger, ontology, serializer);
                break;
            }
            case SYNTAX_RDFXML: {
                RDFSerializer serializer = new RDFXMLSerializer(writer);
                exportResourceRDF(logger, ontology, serializer);
                break;
            }
            case SYNTAX_JSON_LD: {
                RDFSerializer serializer = new JSONLDSerializer(writer);
                exportResourceRDF(logger, ontology, serializer);
                break;
            }
            case SYNTAX_RDFT:
                throw new IllegalArgumentException("Syntax " + syntax + " is not supported");
            case SYNTAX_FUNCTIONAL_OWL2:
                throw new IllegalArgumentException("Syntax " + syntax + " is not supported");
            case SYNTAX_OWLXML:
                throw new IllegalArgumentException("Syntax " + syntax + " is not supported");
            case SYNTAX_XOWL:
                throw new IllegalArgumentException("Syntax " + syntax + " is not supported");
            default:
                throw new IllegalArgumentException("Unknown syntax: " + syntax);
        }
    }

    /**
     * Exports the repository to a resource
     *
     * @param logger      The logger to use
     * @param writer      The output writer
     * @param resourceIRI The resource's IRI
     * @param syntax      The resource's syntax
     * @throws Exception                When an error occurred during the operation
     * @throws IllegalArgumentException When the syntax is not supported
     */
    private void exportResource(Logger logger, Writer writer, String resourceIRI, String syntax) throws Exception {
        switch (syntax) {
            case SYNTAX_NTRIPLES: {
                RDFSerializer serializer = new NTripleSerializer(writer);
                exportResourceRDF(logger, this.getOntology(resourceIRI), serializer);
                break;
            }
            case SYNTAX_NQUADS: {
                RDFSerializer serializer = new NQuadsSerializer(writer);
                exportResourceRDF(logger, serializer);
                break;
            }
            case SYNTAX_TURTLE: {
                RDFSerializer serializer = new TurtleSerializer(writer);
                exportResourceRDF(logger, this.getOntology(resourceIRI), serializer);
                break;
            }
            case SYNTAX_TRIG: {
                RDFSerializer serializer = new TriGSerializer(writer);
                exportResourceRDF(logger, serializer);
                break;
            }
            case SYNTAX_RDFXML: {
                RDFSerializer serializer = new RDFXMLSerializer(writer);
                exportResourceRDF(logger, this.getOntology(resourceIRI), serializer);
                break;
            }
            case SYNTAX_JSON_LD: {
                RDFSerializer serializer = new JSONLDSerializer(writer);
                exportResourceRDF(logger, serializer);
                break;
            }
            case SYNTAX_RDFT:
                throw new IllegalArgumentException("Syntax " + syntax + " is not supported");
            case SYNTAX_FUNCTIONAL_OWL2:
                throw new IllegalArgumentException("Syntax " + syntax + " is not supported");
            case SYNTAX_OWLXML:
                throw new IllegalArgumentException("Syntax " + syntax + " is not supported");
            case SYNTAX_XOWL:
                throw new IllegalArgumentException("Syntax " + syntax + " is not supported");
            default:
                throw new IllegalArgumentException("Unknown syntax: " + syntax);
        }
    }

    /**
     * Loads an RDF resource
     *
     * @param logger      The logger to use
     * @param resourceIRI The resource's IRI
     * @param ontologyIRI The IRI of the ontology within the document
     * @param input       The resource's content
     * @return The loaded ontology
     * @throws Exception When an error occurred during the operation
     */
    private Ontology loadResourceRDF(Logger logger, String resourceIRI, String ontologyIRI, RDFLoaderResult input) throws Exception {
        Ontology ontology = registerResource(resourceIRI, ontologyIRI);
        for (String importedIRI : input.getImports())
            dependencies.add(importedIRI);
        loadResourceRDF(logger, ontology, input);
        return ontology;
    }

    /**
     * Loads an OWL resource
     *
     * @param logger      The logger to use
     * @param resourceIRI The resource's IRI
     * @param input       The resource's content
     * @return The loaded ontology
     * @throws Exception When an error occurred during the operation
     */
    private Ontology loadResourceOWL(Logger logger, String resourceIRI, OWLLoaderResult input) throws Exception {
        Ontology ontology = registerResource(resourceIRI, input.getIRI());
        for (String importedIRI : input.getImports())
            dependencies.add(importedIRI);
        loadResourceOWL(logger, ontology, input);
        return ontology;
    }

    /**
     * Registers a loaded resource
     *
     * @param documentIRI The document's IRI
     * @param ontologyIRI The IRI of the ontology within the document
     * @return The corresponding Ontology
     */
    private Ontology registerResource(String documentIRI, String ontologyIRI) {
        Ontology ontology = resolveOntology(ontologyIRI);
        resources.put(documentIRI, ontology);
        return ontology;
    }

    /**
     * Loads a collection of quads
     *
     * @param logger   The logger to use
     * @param ontology The containing ontology
     * @param input    The input data
     * @throws Exception When an error occurred during the operation
     */
    protected abstract void loadResourceRDF(Logger logger, Ontology ontology, RDFLoaderResult input) throws Exception;

    /**
     * Loads an ontology as a set of axioms
     *
     * @param logger   The logger to use
     * @param ontology The containing ontology
     * @param input    The input data
     * @throws Exception When an error occurred during the operation
     */
    protected abstract void loadResourceOWL(Logger logger, Ontology ontology, OWLLoaderResult input) throws Exception;

    /**
     * Exports a collection of quads
     *
     * @param logger   The logger to use
     * @param ontology The containing ontology
     * @param output   The output
     * @throws Exception When an error occurred during the operation
     */
    protected abstract void exportResourceRDF(Logger logger, Ontology ontology, RDFSerializer output) throws Exception;

    /**
     * Exports a collection of quads from the whole repository
     *
     * @param logger The logger to use
     * @param output The output
     * @throws Exception When an error occurred during the operation
     */
    protected abstract void exportResourceRDF(Logger logger, RDFSerializer output) throws Exception;

    /**
     * Exports a collection of quads
     *
     * @param logger   The logger to use
     * @param ontology The containing ontology
     * @param output   The output
     * @throws Exception When an error occurred during the operation
     */
    protected abstract void exportResourceOWL(Logger logger, Ontology ontology, OWLSerializer output) throws Exception;


    /**
     * Gets a reader for a resource
     *
     * @param resource The resource to read from
     * @return The appropriate reader, or null if there is none for the resource
     * @throws IOException When the reader cannot be created
     */
    public static Reader getReaderFor(String resource) throws IOException {
        if (resource.startsWith(SCHEME_HTTP)) {
            URL url = new URL(resource);
            URLConnection connection = url.openConnection();
            return new InputStreamReader(connection.getInputStream(), Files.CHARSET);
        } else if (resource.startsWith(SCHEME_RESOURCE)) {
            InputStream stream = Repository.class.getResourceAsStream(resource.substring(SCHEME_RESOURCE.length()));
            return new InputStreamReader(stream, Files.CHARSET);
        } else if (resource.startsWith(SCHEME_JAR)) {
            String parts[] = resource.substring(SCHEME_JAR.length()).split("!");
            JarFile jar = new JarFile(parts[0]);
            InputStream stream = jar.getInputStream(jar.getEntry(parts[1]));
            return new InputStreamReader(stream, Files.CHARSET);
        } else if (resource.startsWith(SCHEME_FILE)) {
            FileInputStream stream = new FileInputStream(resource.substring(SCHEME_FILE.length()));
            return new InputStreamReader(stream, Files.CHARSET);
        }
        throw new IOException("Cannot read from resource " + resource);
    }

    /**
     * Gets a writer for a resource
     *
     * @param resource The resource to write to
     * @return The appropriate writer, or null if there is none for the resource
     * @throws IOException When the writer cannot be created
     */
    public static Writer getWriterFor(String resource) throws IOException {
        if (resource.startsWith(SCHEME_HTTP)) {
            URL url = new URL(resource);
            URLConnection connection = url.openConnection();
            return new OutputStreamWriter(connection.getOutputStream(), Files.CHARSET);
        } else if (resource.startsWith(SCHEME_RESOURCE)) {
            // cannot write to resources
            return null;
        } else if (resource.startsWith(SCHEME_JAR)) {
            // cannot write to jar
            return null;
        } else if (resource.startsWith(SCHEME_FILE)) {
            FileOutputStream stream = new FileOutputStream(resource.substring(SCHEME_FILE.length()));
            return new OutputStreamWriter(stream, Files.CHARSET);
        }
        throw new IOException("Cannot write to resource " + resource);
    }
}