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

import fr.cenotelie.commons.utils.http.HttpConstants;
import fr.cenotelie.commons.utils.logging.Logger;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.xowl.infra.lang.owl2.IRI;
import org.xowl.infra.lang.owl2.Ontology;
import org.xowl.infra.lang.owl2.Owl2Factory;
import org.xowl.infra.store.execution.ExecutionManager;
import org.xowl.infra.store.execution.ExecutionManagerProvider;
import org.xowl.infra.store.loaders.*;
import org.xowl.infra.store.rdf.DatasetNodes;
import org.xowl.infra.store.writers.*;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.*;

/**
 * Represents a repository of xOWL ontologies
 * This class does not specify the storage method for the ontologies.
 * This class is thread-safe.
 *
 * @author Laurent Wouters
 * Modification
 * @author Stephen Creff
 */
public abstract class Repository {
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
     * Supported JSON syntax
     */
    public static final String SYNTAX_JSON = HttpConstants.MIME_JSON;
    /**
     * File extension for the JSON syntax
     */
    public static final String SYNTAX_JSON_EXTENSION = ".json";
    /**
     * Supported TriG syntax
     */
    public static final String SYNTAX_TRIG = "application/trig";
    /**
     * File extension for the TriG syntax
     */
    public static final String SYNTAX_TRIG_EXTENSION = ".trig";
    /**
     * Supported xRDF syntax
     */
    public static final String SYNTAX_XRDF = "application/x-xowl-xrdf";
    /**
     * File extension for the xRDF syntax
     */
    public static final String SYNTAX_XRDF_EXTENSION = ".xrdf";
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
    public static final String SYNTAX_XOWL = "application/x-xowl-xowl";
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
        if (resource.endsWith(SYNTAX_RDFXML_EXTENSION))
            return SYNTAX_RDFXML;
        if (resource.endsWith(SYNTAX_JSON_LD_EXTENSION))
            return SYNTAX_JSON_LD;
        if (resource.endsWith(SYNTAX_JSON_EXTENSION))
            return SYNTAX_JSON;
        if (resource.endsWith(SYNTAX_TRIG_EXTENSION))
            return SYNTAX_TRIG;
        if (resource.endsWith(SYNTAX_XRDF_EXTENSION))
            return SYNTAX_XRDF;
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
     * The loader for providers of execution managers
     */
    private static ServiceLoader<ExecutionManagerProvider> EXECUTION_MANAGER_PROVIDER = ServiceLoader.load(ExecutionManagerProvider.class);

    /**
     * Gets an execution manager
     *
     * @param repository The parent repository for the execution manager
     * @return An execution manager
     */
    private static ExecutionManager getExecutionManager(Repository repository) {
        Iterator<ExecutionManagerProvider> services = EXECUTION_MANAGER_PROVIDER.iterator();
        if (services.hasNext())
            return services.next().newManager(repository);

        Bundle bundle = FrameworkUtil.getBundle(Repository.class);
        if (bundle == null)
            return null;
        BundleContext context = FrameworkUtil.getBundle(Repository.class).getBundleContext();
        if (context == null)
            return null;
        ServiceReference reference = context.getServiceReference(ExecutionManagerProvider.class);
        if (reference == null)
            return null;
        ExecutionManagerProvider result = (ExecutionManagerProvider) context.getService(reference);
        context.ungetService(reference);
        return result.newManager(repository);
    }


    /**
     * The data for the resource
     */
    private static class Resource {
        /**
         * The ontology for the resource
         */
        public Ontology ontology;
        /**
         * The dependencies for the resource
         */
        public Collection<String> dependencies;
    }

    /**
     * The IRI mapper
     */
    protected final IRIMapper mapper;
    /**
     * The loaded resources
     */
    protected final Map<String, Resource> resources;
    /**
     * The loaded ontologies by IRI
     */
    protected final Map<String, Ontology> ontologies;
    /**
     * Whether dependencies should be resolved when loading resources
     */
    protected final boolean resolveDependencies;
    /**
     * The evaluator to use
     */
    protected final ExecutionManager executionManager;
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
     * @param mapper              The IRI mapper to use
     * @param resolveDependencies Whether dependencies should be resolved when loading resources
     */
    public Repository(IRIMapper mapper, boolean resolveDependencies) {
        this.mapper = mapper;
        this.resources = new HashMap<>();
        this.ontologies = new HashMap<>();
        this.resolveDependencies = resolveDependencies;
        this.executionManager = getExecutionManager(this);
        this.regime = EntailmentRegime.none;
    }

    /**
     * Gets the execution manager used by this repository
     *
     * @return The execution manager used by this repository
     */
    public ExecutionManager getExecutionManager() {
        return executionManager;
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
        synchronized (ontologies) {
            return new ArrayList<>(ontologies.values());
        }
    }

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
     * @throws IOException              When some loading fails
     */
    public abstract void setEntailmentRegime(EntailmentRegime regime) throws IOException;

    /**
     * Gets a node manager for this repository
     *
     * @return A node manager for this repository
     */
    protected abstract DatasetNodes getNodeManager();

    /**
     * Gets an IRI for the specified value
     *
     * @param value The value of an IRI
     * @return The IRI
     */
    public IRI getIRI(String value) {
        IRI iri = Owl2Factory.newIRI();
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
        synchronized (ontologies) {
            Ontology ontology = ontologies.get(iri);
            if (ontology == null) {
                ontology = Owl2Factory.newOntology();
                ontology.setHasIRI(getIRI(iri));
                ontologies.put(iri, ontology);
            }
            return ontology;
        }
    }

    /**
     * Loads a resource and resolves its dependencies
     *
     * @param logger      The logger to use
     * @param resourceIRI The resource's IRI
     * @return The loaded ontology
     * @throws IOException When the reader cannot be created
     */
    public Ontology load(Logger logger, String resourceIRI) throws IOException {
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
     * @throws IOException When the reader cannot be created
     */
    public Ontology load(Logger logger, String resourceIRI, String ontologyIRI, boolean forceReload) throws IOException {
        // register the resource to be loaded
        Resource metadata;
        synchronized (resources) {
            metadata = resources.get(resourceIRI);
            if (metadata != null) {
                if (!forceReload)
                    return metadata.ontology;
            } else {
                metadata = new Resource();
                resources.put(resourceIRI, metadata);
            }
        }

        // resolve the target physical location
        String physicalResource = mapper.get(resourceIRI);
        if (physicalResource == null)
            throw new IOException("Cannot identify the location of " + resourceIRI);

        // gets the reader for the resource and load
        try (Reader reader = getReaderFor(physicalResource)) {
            String syntax = getSyntax(physicalResource);
            if (syntax == null)
                throw new IOException("Failed to determine the syntax of resource " + resourceIRI);
            loadInput(logger, reader, resourceIRI, ontologyIRI, syntax, metadata);
        }

        // resolve the dependencies
        if (resolveDependencies && metadata.dependencies != null) {
            for (String dependency : metadata.dependencies) {
                load(logger, dependency, dependency, false);
            }
        }
        return metadata.ontology;
    }

    /**
     * Loads data from the specified reader, do not resolve the dependencies
     *
     * @param logger      The logger to use
     * @param reader      The reader to use
     * @param resourceIRI The resource's IRI
     * @param ontologyIRI The IRI of the ontology within the document
     * @param syntax      The resource's syntax
     * @return The loaded ontology
     */
    public Ontology load(Logger logger, Reader reader, String resourceIRI, String ontologyIRI, String syntax) {
        Resource metadata = loadInput(logger, reader, resourceIRI, ontologyIRI, syntax, new Resource());
        return metadata.ontology;
    }

    /**
     * Loads a resource
     *
     * @param logger      The logger to use
     * @param reader      The input reader
     * @param resourceIRI The resource's IRI
     * @param ontologyIRI The IRI of the ontology within the document
     * @param syntax      The resource's syntax
     * @param metadata    The metadata for the resource
     * @return The loaded ontology
     * @throws IllegalArgumentException When the syntax is not supported
     */
    private Resource loadInput(Logger logger, Reader reader, String resourceIRI, String ontologyIRI, String syntax, Resource metadata) {
        switch (syntax) {
            case SYNTAX_NTRIPLES:
                return loadInputRDF(logger, reader, resourceIRI, ontologyIRI, metadata, new NTriplesLoader(getNodeManager()));
            case SYNTAX_NQUADS:
                return loadInputRDF(logger, reader, resourceIRI, ontologyIRI, metadata, new NQuadsLoader(getNodeManager()));
            case SYNTAX_TURTLE:
                return loadInputRDF(logger, reader, resourceIRI, ontologyIRI, metadata, new TurtleLoader(getNodeManager()));
            case SYNTAX_RDFXML:
                return loadInputRDF(logger, reader, resourceIRI, ontologyIRI, metadata, new RDFXMLLoader(getNodeManager()));
            case SYNTAX_JSON_LD:
                return loadInputRDF(logger, reader, resourceIRI, ontologyIRI, metadata, new JsonLdLoader(getNodeManager()) {
                    @Override
                    protected Reader getReaderFor(Logger logger, String iri) {
                        if (!resolveDependencies)
                            return null;
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
                });
            case SYNTAX_JSON:
                return loadInputRDF(logger, reader, resourceIRI, ontologyIRI, metadata, new JsonLoader(this, getNodeManager()));
            case SYNTAX_TRIG:
                return loadInputRDF(logger, reader, resourceIRI, ontologyIRI, metadata, new TriGLoader(getNodeManager()));
            case SYNTAX_XRDF:
                return loadInputRDF(logger, reader, resourceIRI, ontologyIRI, metadata, new xRDFLoader(getNodeManager(), executionManager));
            case SYNTAX_FUNCTIONAL_OWL2:
                return loadInputOWL(logger, reader, resourceIRI, metadata, new FunctionalOWL2Loader());
            case SYNTAX_OWLXML:
                return loadInputOWL(logger, reader, resourceIRI, metadata, new OWLXMLLoader());
            case SYNTAX_XOWL:
                return loadInputOWL(logger, reader, resourceIRI, metadata, new xOWLLoader(executionManager));
            default:
                throw new IllegalArgumentException("Unsupported syntax: " + syntax);
        }
    }

    /**
     * Loads an RDF input
     *
     * @param logger      The logger to use
     * @param reader      The input reader
     * @param resourceIRI The resource's IRI
     * @param ontologyIRI The IRI of the ontology within the document
     * @param metadata    The metadata for the resource
     * @param loader      The RDF loader to use
     * @return The metadata for the resource
     */
    private Resource loadInputRDF(Logger logger, Reader reader, String resourceIRI, String ontologyIRI, Resource metadata, Loader loader) {
        RDFLoaderResult input = loader.loadRDF(logger, reader, resourceIRI, ontologyIRI);
        if (input == null)
            return null;
        metadata.ontology = resolveOntology(ontologyIRI);
        metadata.dependencies = input.getImports();
        doLoadRDF(logger, metadata.ontology, input);
        return metadata;
    }

    /**
     * Loads an OWL input
     *
     * @param logger      The logger to use
     * @param reader      The input reader
     * @param resourceIRI The resource's IRI
     * @param metadata    The metadata for the resource
     * @param loader      The RDF loader to use
     * @return The metadata for the resource
     */
    private Resource loadInputOWL(Logger logger, Reader reader, String resourceIRI, Resource metadata, Loader loader) {
        OWLLoaderResult input = loader.loadOWL(logger, reader, resourceIRI);
        if (input == null)
            return null;
        metadata.ontology = resolveOntology(input.getIRI());
        metadata.dependencies = input.getImports();
        doLoadOWL(logger, metadata.ontology, input);
        return metadata;
    }

    /**
     * Exports a resource
     *
     * @param logger   The logger to use
     * @param ontology The content to export
     * @param iri      The resource's IRI
     * @throws IOException              When an error occurred during the operation
     * @throws IllegalArgumentException When the syntax is not supported
     */
    public void export(Logger logger, Ontology ontology, String iri) throws IOException {
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
     * @throws IOException              When an error occurred during the operation
     * @throws IllegalArgumentException When the syntax is not supported
     */
    public void exportAll(Logger logger, String iri) throws IOException {
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
     * Exports a resource
     *
     * @param logger   The logger to use
     * @param writer   The output writer
     * @param ontology The ontology to export
     * @param syntax   The resource's syntax
     * @throws IllegalArgumentException When the syntax is not supported
     */
    private void exportResource(Logger logger, Writer writer, Ontology ontology, String syntax) {
        switch (syntax) {
            case SYNTAX_NTRIPLES: {
                RDFSerializer serializer = new NTripleSerializer(writer);
                doExportRDF(logger, ontology, serializer);
                break;
            }
            case SYNTAX_NQUADS: {
                RDFSerializer serializer = new NQuadsSerializer(writer);
                doExportRDF(logger, ontology, serializer);
                break;
            }
            case SYNTAX_TURTLE: {
                RDFSerializer serializer = new TurtleSerializer(writer);
                doExportRDF(logger, ontology, serializer);
                break;
            }
            case SYNTAX_TRIG: {
                RDFSerializer serializer = new TriGSerializer(writer);
                doExportRDF(logger, ontology, serializer);
                break;
            }
            case SYNTAX_RDFXML: {
                RDFSerializer serializer = new RDFXMLSerializer(writer);
                doExportRDF(logger, ontology, serializer);
                break;
            }
            case SYNTAX_JSON_LD: {
                RDFSerializer serializer = new JsonLdSerializer(writer);
                doExportRDF(logger, ontology, serializer);
                break;
            }
            case SYNTAX_XRDF: {
                RDFSerializer serializer = new xRDFSerializer(writer);
                doExportRDF(logger, ontology, serializer);
                break;
            }
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
     * @throws IllegalArgumentException When the syntax is not supported
     */
    private void exportResource(Logger logger, Writer writer, String resourceIRI, String syntax) {
        switch (syntax) {
            case SYNTAX_NTRIPLES: {
                RDFSerializer serializer = new NTripleSerializer(writer);
                doExportRDF(logger, this.getOntology(resourceIRI), serializer);
                break;
            }
            case SYNTAX_NQUADS: {
                RDFSerializer serializer = new NQuadsSerializer(writer);
                doExportRDF(logger, serializer);
                break;
            }
            case SYNTAX_TURTLE: {
                RDFSerializer serializer = new TurtleSerializer(writer);
                doExportRDF(logger, this.getOntology(resourceIRI), serializer);
                break;
            }
            case SYNTAX_TRIG: {
                RDFSerializer serializer = new TriGSerializer(writer);
                doExportRDF(logger, serializer);
                break;
            }
            case SYNTAX_RDFXML: {
                RDFSerializer serializer = new RDFXMLSerializer(writer);
                doExportRDF(logger, this.getOntology(resourceIRI), serializer);
                break;
            }
            case SYNTAX_JSON_LD: {
                RDFSerializer serializer = new JsonLdSerializer(writer);
                doExportRDF(logger, serializer);
                break;
            }
            case SYNTAX_XRDF: {
                RDFSerializer serializer = new xRDFSerializer(writer);
                doExportRDF(logger, serializer);
                break;
            }
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
     * Loads a collection of quads
     *
     * @param logger   The logger to use
     * @param ontology The containing ontology
     * @param input    The input data
     */
    protected abstract void doLoadRDF(Logger logger, Ontology ontology, RDFLoaderResult input);

    /**
     * Loads an ontology as a set of axioms
     *
     * @param logger   The logger to use
     * @param ontology The containing ontology
     * @param input    The input data
     */
    protected abstract void doLoadOWL(Logger logger, Ontology ontology, OWLLoaderResult input);

    /**
     * Exports a collection of quads
     *
     * @param logger   The logger to use
     * @param ontology The containing ontology
     * @param output   The output
     */
    protected abstract void doExportRDF(Logger logger, Ontology ontology, RDFSerializer output);

    /**
     * Exports a collection of quads from the whole repository
     *
     * @param logger The logger to use
     * @param output The output
     */
    protected abstract void doExportRDF(Logger logger, RDFSerializer output);

    /**
     * Exports a collection of quads
     *
     * @param logger   The logger to use
     * @param ontology The containing ontology
     * @param output   The output
     */
    protected abstract void exportResourceOWL(Logger logger, Ontology ontology, OWLSerializer output);


    /**
     * Gets a reader for a resource
     *
     * @param resource The resource to read from
     * @return The appropriate reader, or null if there is none for the resource
     * @throws IOException When the reader cannot be created
     */
    public static Reader getReaderFor(String resource) throws IOException {
        ResourceAccess access = ResourceAccess.getAccessFor(resource);
        if (access != null)
            return access.getReader(resource);
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
        ResourceAccess access = ResourceAccess.getAccessFor(resource);
        if (access != null)
            return access.getWriter(resource);
        throw new IOException("Cannot write to resource " + resource);
    }
}