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
import org.xowl.infra.store.writers.*;
import org.xowl.infra.utils.Files;
import org.xowl.infra.utils.logging.Logger;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.jar.JarFile;

/**
 * Represents the interface of a repository of xOWL ontologies
 *
 * @author Laurent Wouters
 * Modification
 * @author Stephen Creff
 */
public abstract class AbstractRepository {
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
    public static final String EXT_NTRIPLES = ".nt";
    /**
     * Supported N-Quads syntax
     */
    public static final String SYNTAX_NQUADS = "application/n-quads";
    public static final String EXT_NQUADS = ".nq";
    /**
     * Supported Turtle syntax
     */
    public static final String SYNTAX_TURTLE = "text/turtle";
    public static final String EXT_TURTLE = ".ttl";
    /**
     * Supported RDF Transform syntax
     */
    public static final String SYNTAX_RDFT = "application/x-xowl-rdft";
    public static final String EXT_RDFT = ".rdft";
    /**
     * Supported RDF/XML syntax
     */
    public static final String SYNTAX_RDFXML = "application/rdf+xml";
    public static final String EXT_RDFXML = ".rdf";
    /**
     * Supported Functional OWL2 syntax
     */
    public static final String SYNTAX_FUNCTIONAL_OWL2 = "text/owl-functional";
    public static final String EXT_OWL2_A = ".ofn";
    public static final String EXT_OWL2_B = ".fs";
    /**
     * Supported OWL/XML OWL2 syntax
     */
    public static final String SYNTAX_OWLXML = "application/owl+xml";
    public static final String EXT_OWLXML_A = ".owx";
    public static final String EXT_OWLXML_B = ".owl";
    /**
     * Supported Functional xOWL syntax
     */
    public static final String SYNTAX_XOWL = "application/x-xowl";
    public static final String EXT_XOWL = ".xowl";
    /**
     * Supported JSON-LD syntax
     */
    public static final String SYNTAX_JSON_LD = "application/ld+json";
    public static final String EXT_JSON_LD = ".jsonld";
    /**
     * Supported TriG syntax
     */
    public static final String SYNTAX_TRIG = "application/trig";
    public static final String EXT_TRIG = ".trig";


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
     * @param mapper The IRI mapper to use
     */
    public AbstractRepository(IRIMapper mapper) {
        this.mapper = mapper;
        this.resources = new HashMap<>();
        this.ontologies = new HashMap<>();
        this.dependencies = new ArrayList<>();
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
     * @param logger      The current logger
     * @param resourceIRI The resource's IRI
     * @return The loaded ontology
     */
    public Ontology load(Logger logger, String resourceIRI) {
        return load(logger, resourceIRI, resourceIRI, false);
    }

    /**
     * Loads a resource and resolves its dependencies
     *
     * @param logger      The current logger
     * @param resourceIRI The resource's IRI
     * @param ontologyIRI The IRI of the ontology within the document
     * @param forceReload Whether to force the reloading of the resource
     * @return The loaded ontology
     */
    public Ontology load(Logger logger, String resourceIRI, String ontologyIRI, boolean forceReload) {
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
     * Exports a resource
     *
     * @param logger   The current logger
     * @param ontology The content to export
     * @param iri      The resource's IRI
     */
    public void export(Logger logger, Ontology ontology, String iri) {
        String resource = mapper.get(iri);
        if (resource == null) {
            logger.error("Cannot identify the location of " + iri);
            return;
        }
        Writer writer = getWriterFor(logger, resource);
        if (writer == null)
            return;
        String syntax = getSyntax(logger, resource);
        if (syntax == null)
            return;
        exportResource(logger, writer, ontology, iri, syntax);
        try {
            writer.close();
        } catch (IOException e) {
            logger.error(e);
        }
    }

    /**
     * Exports all stored ontologies into a resource
     *
     * @param logger   The current logger
     * @param iri      The resource's IRI
     */
    public void exportAll(Logger logger, String iri) {
        String resource = mapper.get(iri);
        if (resource == null) {
            logger.error("Cannot identify the location of " + iri);
            return;
        }
        Writer writer = getWriterFor(logger, resource);
        if (writer == null)
            return;
        String syntax = getSyntax(logger, resource);
        if (syntax == null)
            return;
        exportResource(logger, writer, iri, syntax);
        try {
            writer.close();
        } catch (IOException e) {
            logger.error(e);
        }
    }

    /**
     * Gets a reader for a resource
     *
     * @param logger   The current logger
     * @param resource The resource to read
     * @return The appropriate reader
     */
    private Reader getReaderFor(Logger logger, String resource) {
        try {
            return getReaderFor(resource);
        } catch (IOException ex) {
            logger.error(ex);
            return null;
        }
    }

    /**
     * Gets a writer for a resource
     *
     * @param logger   The current logger
     * @param resource The resource to read
     * @return The appropriate writer
     */
    private Writer getWriterFor(Logger logger, String resource) {
        try {
            return getWriterFor(resource);
        } catch (IOException ex) {
            logger.error(ex);
            return null;
        }
    }

    /**
     * Determines the syntax for the specified resource
     *
     * @param logger   The current logger
     * @param resource The resource to identify
     * @return The recognized syntax
     */
    private String getSyntax(Logger logger, String resource) {
        String syntax = getSyntax(resource);
        if (syntax != null)
            return syntax;
        logger.error("Unknown syntax for resource " + resource);
        return null;
    }

    /**
     * Loads a resource
     *
     * @param logger      The current logger
     * @param resourceIRI The resource's IRI
     * @param ontologyIRI The IRI of the ontology within the document
     * @param forceReload Whether to force the reloading of the resource
     * @return The loaded ontology
     */
    private Ontology loadResource(Logger logger, String resourceIRI, String ontologyIRI, boolean forceReload) {
        String resource = mapper.get(resourceIRI);
        if (resource == null) {
            logger.error("Cannot identify the location of " + resourceIRI);
            return null;
        }
        if (!forceReload) {
            Ontology ontology = resources.get(resourceIRI);
            if (ontology != null)
                // the resource is already loaded
                return ontology;
        }
        Reader reader = getReaderFor(logger, resource);
        if (reader == null)
            return null;
        String syntax = getSyntax(logger, resource);
        if (syntax == null)
            return null;
        return loadResource(logger, reader, resourceIRI, ontologyIRI, syntax);
    }

    /**
     * Loads a resource
     *
     * @param logger      The current logger
     * @param reader      The input reader
     * @param resourceIRI The resource's IRI
     * @param ontologyIRI The IRI of the ontology within the document
     * @param syntax      The resource's syntax
     * @return The loaded ontology
     */
    public Ontology loadResource(Logger logger, Reader reader, String resourceIRI, String ontologyIRI, String syntax) {
        Ontology ontology = null;
        switch (syntax) {
            case SYNTAX_NTRIPLES:
            case SYNTAX_NQUADS:
            case SYNTAX_TURTLE:
            case SYNTAX_TRIG:
            case SYNTAX_RDFXML:
            case SYNTAX_JSON_LD:
            case SYNTAX_RDFT: {
                Loader loader = newRDFLoader(syntax);
                RDFLoaderResult input = loader.loadRDF(logger, reader, resourceIRI, ontologyIRI);
                if (input == null)
                    break;
                ontology = loadResourceRDF(logger, resourceIRI, ontologyIRI, input);
                break;
            }
            case SYNTAX_FUNCTIONAL_OWL2:
            case SYNTAX_OWLXML:
            case SYNTAX_XOWL: {
                Loader loader = newOWLLoader(syntax);
                OWLLoaderResult input = loader.loadOWL(logger, reader, resourceIRI);
                if (input == null)
                    break;
                ontology = loadResourceOWL(logger, resourceIRI, input);
                break;
            }
            default:
                logger.error("Unsupported syntax: " + syntax);
                break;
        }
        try {
            reader.close();
        } catch (IOException ex) {
            logger.error(ex);
        }
        return ontology;
    }

    /**
     * Exports a resource
     *
     * @param logger      The current logger
     * @param writer      The output writer
     * @param ontology    The ontology to export
     * @param resourceIRI The resource's IRI
     * @param syntax      The resource's syntax
     */
    private void exportResource(Logger logger, Writer writer, Ontology ontology, String resourceIRI, String syntax) {
        switch (syntax) {
            case SYNTAX_NTRIPLES:
            case SYNTAX_NQUADS:
            case SYNTAX_TURTLE:
            case SYNTAX_TRIG:
            case SYNTAX_RDFXML:
            case SYNTAX_JSON_LD:
            case SYNTAX_RDFT: {
                RDFSerializer serializer = newRDFSerializer(syntax, writer);
                exportResourceRDF(logger, ontology, serializer);
                break;
            }
            case SYNTAX_FUNCTIONAL_OWL2:
            case SYNTAX_OWLXML:
            case SYNTAX_XOWL: {
                OWLSerializer serializer = newOWLSerializer(syntax, writer);
                exportResourceOWL(logger, ontology, serializer);
                break;
            }
            default:
                logger.error("Unsupported syntax: " + syntax);
                break;
        }
        try {
            writer.close();
        } catch (IOException ex) {
            logger.error(ex);
        }
    }

    /**
     * Exports the repository to a resource
     *
     * @param logger      The current logger
     * @param writer      The output writer
     * @param resourceIRI The resource's IRI
     * @param syntax      The resource's syntax
     */
    private void exportResource(Logger logger, Writer writer, String resourceIRI, String syntax) {
        switch (syntax) {
            case SYNTAX_NQUADS:
            case SYNTAX_TRIG:
            case SYNTAX_JSON_LD:{
                RDFSerializer serializer = newRDFSerializer(syntax, writer);
                exportResourceRDF(logger, serializer);
                break;
            }
            case SYNTAX_NTRIPLES:
            case SYNTAX_TURTLE:
            case SYNTAX_RDFXML:
            case SYNTAX_RDFT: {
                RDFSerializer serializer = newRDFSerializer(syntax, writer);
                exportResourceRDF(logger, this.getOntology(resourceIRI), serializer);
                break;
            }
            case SYNTAX_FUNCTIONAL_OWL2:
            case SYNTAX_OWLXML:
            case SYNTAX_XOWL: {
                //TODO : support multiple ontologies export
                logger.error("Exporting multiple ontologies not supported yet by the syntax, IRI: " + resourceIRI);
                OWLSerializer serializer = newOWLSerializer(syntax, writer);
                exportResourceOWL(logger, this.getOntology(resourceIRI), serializer);
                break;
            }
            default:
                logger.error("Unsupported syntax: " + syntax);
                break;
        }
        try {
            writer.close();
        } catch (IOException ex) {
            logger.error(ex);
        }
    }

    /**
     * Loads an RDF resource
     *
     * @param logger      The current logger
     * @param resourceIRI The resource's IRI
     * @param ontologyIRI The IRI of the ontology within the document
     * @param input       The resource's content
     * @return The loaded ontology
     */
    private Ontology loadResourceRDF(Logger logger, String resourceIRI, String ontologyIRI, RDFLoaderResult input) {
        Ontology ontology = registerResource(resourceIRI, ontologyIRI);
        for (String importedIRI : input.getImports())
            dependencies.add(importedIRI);
        loadResourceRDF(logger, ontology, input);
        return ontology;
    }

    /**
     * Loads an OWL resource
     *
     * @param logger      The current logger
     * @param resourceIRI The resource's IRI
     * @param input       The resource's content
     * @return The loaded ontology
     */
    private Ontology loadResourceOWL(Logger logger, String resourceIRI, OWLLoaderResult input) {
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
     * Creates a new RDF loader for the specified syntax
     *
     * @param syntax A RDF syntax
     * @return The adapted loader
     */
    protected abstract Loader newRDFLoader(String syntax);

    /**
     * Creates a new OWL loader for the specified syntax
     *
     * @param syntax A OWL syntax
     * @return The adapted loader
     */
    protected Loader newOWLLoader(String syntax) {
        switch (syntax) {
            case SYNTAX_FUNCTIONAL_OWL2:
                return new FunctionalOWL2Loader();
            case SYNTAX_OWLXML:
                return new OWLXMLLoader();
            case SYNTAX_XOWL:
                return new XOWLLoader();
        }
        return null;
    }

    /**
     * Creates a new RDF serializer for the specified syntax
     *
     * @param syntax A RDF syntax
     * @param writer The backend writer
     * @return The adapted serializer
     */
    protected RDFSerializer newRDFSerializer(String syntax, Writer writer) {
        switch (syntax) {
            case SYNTAX_NTRIPLES:
                return new NTripleSerializer(writer);
            case SYNTAX_NQUADS:
                return new NQuadsSerializer(writer);
            case SYNTAX_TURTLE:
                return new TurtleSerializer(writer);
            case SYNTAX_TRIG:
                return new TriGSerializer(writer);
            case SYNTAX_RDFXML:
                return new RDFXMLSerializer(writer);
            case SYNTAX_RDFT:
                return null;
            case SYNTAX_JSON_LD:
                return new JSONLDSerializer(writer);
        }
        return null;
    }

    /**
     * Creates a new OWL serializer for the specified syntax
     *
     * @param syntax A OWL syntax
     * @param writer The backend writer
     * @return The adapted serializer
     */
    protected OWLSerializer newOWLSerializer(String syntax, Writer writer) {
        switch (syntax) {
            case SYNTAX_FUNCTIONAL_OWL2:
            case SYNTAX_OWLXML:
            case SYNTAX_XOWL:
                return null;
        }
        return null;
    }

    /**
     * Loads a collection of quads
     *
     * @param logger   The current logger
     * @param ontology The containing ontology
     * @param input    The input data
     */
    protected abstract void loadResourceRDF(Logger logger, Ontology ontology, RDFLoaderResult input);

    /**
     * Loads an ontology as a set of axioms
     *
     * @param logger   The current logger
     * @param ontology The containing ontology
     * @param input    The input data
     */
    protected abstract void loadResourceOWL(Logger logger, Ontology ontology, OWLLoaderResult input);

    /**
     * Exports a collection of quads
     *
     * @param logger   The current logger
     * @param ontology The containing ontology
     * @param output   The output
     */
    protected abstract void exportResourceRDF(Logger logger, Ontology ontology, RDFSerializer output);

    /**
     * Exports a collection of quads from the whole store
     *
     * @param logger   The current logger
     * @param output   The output
     */
    protected abstract void exportResourceRDF(Logger logger, RDFSerializer output);

    /**
     * Exports a collection of quads
     *
     * @param logger   The current logger
     * @param ontology The containing ontology
     * @param output   The output
     */
    protected abstract void exportResourceOWL(Logger logger, Ontology ontology, OWLSerializer output);

    /**
     * Determines the syntax for the specified resource
     *
     * @param resource The resource to identify
     * @return The recognized syntax
     */
    public static String getSyntax(String resource) {
        if (resource.endsWith(EXT_NTRIPLES))
            return SYNTAX_NTRIPLES;
        if (resource.endsWith(EXT_NQUADS))
            return SYNTAX_NQUADS;
        if (resource.endsWith(EXT_TURTLE))
            return SYNTAX_TURTLE;
        if (resource.endsWith(EXT_RDFT))
            return SYNTAX_RDFT;
        if (resource.endsWith(EXT_RDFXML))
            return SYNTAX_RDFXML;
        if (resource.endsWith(EXT_JSON_LD))
            return SYNTAX_JSON_LD;
        if (resource.endsWith(EXT_TRIG))
            return SYNTAX_TRIG;
        if (resource.endsWith(EXT_OWL2_A) || resource.endsWith(EXT_OWL2_B))
            return SYNTAX_FUNCTIONAL_OWL2;
        if (resource.endsWith(EXT_OWLXML_A) || resource.endsWith(EXT_OWLXML_B))
            return SYNTAX_OWLXML;
        if (resource.endsWith(EXT_XOWL))
            return SYNTAX_XOWL;
        // TODO: try to look into the file to determine the syntax
        return null;
    }

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
        } else {
            return null;
        }
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
        } else {
            return null;
        }
    }
}