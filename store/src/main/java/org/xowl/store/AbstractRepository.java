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
package org.xowl.store;

import org.xowl.lang.owl2.IRI;
import org.xowl.lang.owl2.Ontology;
import org.xowl.store.loaders.*;
import org.xowl.store.rdf.Quad;
import org.xowl.utils.Logger;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.jar.JarFile;

/**
 * Represents the interface of a repository of xOWL ontologies
 *
 * @author Laurent Wouters
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
    public static final String SYNTAX_NTRIPLES = ".nt";
    /**
     * Supported N-Quads syntax
     */
    public static final String SYNTAX_NQUADS = ".nq";
    /**
     * Supported Turtle syntax
     */
    public static final String SYNTAX_TURTLE = ".ttl";
    /**
     * Supported RDF/XML syntax
     */
    public static final String SYNTAX_RDFXML = ".rdf";
    /**
     * Supported Functional OWL2 syntax
     */
    public static final String SYNTAX_FUNCTIONAL_OWL2 = ".fs";
    /**
     * Supported Functional xOWL syntax
     */
    public static final String SYNTAX_FUNCTIONAL_XOWL = ".owl";


    /**
     * The IRI mapper
     */
    protected IRIMapper mapper;
    /**
     * The loaded resources
     */
    protected Map<String, Ontology> resources;
    /**
     * The loaded ontologies by IRI
     */
    protected Map<String, Ontology> ontologies;
    /**
     * The remaining dependencies
     */
    protected List<String> dependencies;

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
     */
    public AbstractRepository() {
        this(IRIMapper.getDefault());
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
     * @param logger The current logger
     * @param iri    The resource's IRI
     * @return The loaded ontology
     */
    public Ontology load(Logger logger, String iri) {
        Ontology result = loadResource(logger, iri);
        while (!dependencies.isEmpty()) {
            List<String> batch = new ArrayList<>(dependencies);
            dependencies.clear();
            for (String dependency : batch) {
                loadResource(logger, dependency);
            }
        }
        return result;
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
            if (resource.startsWith(SCHEME_HTTP)) {
                URL url = new URL(resource);
                URLConnection connection = url.openConnection();
                return new InputStreamReader(connection.getInputStream());
            } else if (resource.startsWith(SCHEME_RESOURCE)) {
                InputStream stream = Repository.class.getResourceAsStream(resource.substring(SCHEME_RESOURCE.length()));
                return new InputStreamReader(stream);
            } else if (resource.startsWith(SCHEME_JAR)) {
                String parts[] = resource.substring(SCHEME_JAR.length()).split("!");
                JarFile jar = new JarFile(parts[0]);
                InputStream stream = jar.getInputStream(jar.getEntry(parts[1]));
                return new InputStreamReader(stream);
            } else if (resource.startsWith(SCHEME_FILE)) {
                FileInputStream stream = new FileInputStream(resource.substring(SCHEME_FILE.length()));
                return new InputStreamReader(stream);
            } else {
                // assume a local path
                FileInputStream stream = new FileInputStream(resource);
                return new InputStreamReader(stream);
            }
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
        if (resource.endsWith(SYNTAX_NTRIPLES))
            return SYNTAX_NTRIPLES;
        if (resource.endsWith(SYNTAX_NQUADS))
            return SYNTAX_NQUADS;
        if (resource.endsWith(SYNTAX_TURTLE))
            return SYNTAX_TURTLE;
        if (resource.endsWith(SYNTAX_RDFXML))
            return SYNTAX_RDFXML;
        if (resource.endsWith(SYNTAX_FUNCTIONAL_OWL2))
            return SYNTAX_FUNCTIONAL_OWL2;
        if (resource.endsWith(SYNTAX_FUNCTIONAL_XOWL))
            return SYNTAX_FUNCTIONAL_XOWL;
        logger.error("Unknown syntax for resource " + resource);
        return null;
    }

    /**
     * Loads a resource
     *
     * @param logger The current logger
     * @param iri    The resource's IRI
     * @return The loaded ontology
     */
    private Ontology loadResource(Logger logger, String iri) {
        String resource = mapper.get(iri);
        if (resource == null) {
            logger.error("Cannot identify the location of " + iri);
            return null;
        }
        Reader reader = getReaderFor(logger, resource);
        if (reader == null)
            return null;
        String syntax = getSyntax(logger, resource);
        if (syntax == null)
            return null;
        return loadResource(logger, reader, iri, syntax);
    }

    /**
     * Loads a resource
     *
     * @param logger The current logger
     * @param reader The input reader
     * @param iri    The resource's IRI
     * @param syntax The resource's syntax
     * @return The loaded ontology
     */
    private Ontology loadResource(Logger logger, Reader reader, String iri, String syntax) {
        Ontology ontology = resources.get(iri);
        if (ontology != null)
            return ontology;
        switch (syntax) {
            case SYNTAX_NTRIPLES:
            case SYNTAX_NQUADS:
            case SYNTAX_TURTLE:
            case SYNTAX_RDFXML: {
                Loader loader = newRDFLoader(syntax);
                List<Quad> quads = loader.loadQuads(logger, reader, iri);
                ontology = loadResourceRDF(logger, iri, quads);
                break;
            }
            case SYNTAX_FUNCTIONAL_OWL2: {
                Loader loader = new FunctionalOWL2Loader();
                LoaderResult input = loader.loadAxioms(logger, reader, iri);
                ontology = loadResourceOWL(logger, iri, input);
                break;
            }
            case SYNTAX_FUNCTIONAL_XOWL: {
                Loader loader = new FunctionalXOWLLoader();
                LoaderResult input = loader.loadAxioms(logger, reader, iri);
                ontology = loadResourceOWL(logger, iri, input);
                break;
            }
        }
        try {
            reader.close();
        } catch (IOException ex) {
            logger.error(ex);
        }
        return ontology;
    }

    /**
     * Loads an RDF resource
     *
     * @param logger The current logger
     * @param iri    The resource's IRI
     * @param input  The resource's content
     * @return The loaded ontology
     */
    private Ontology loadResourceRDF(Logger logger, String iri, List<Quad> input) {
        Ontology ontology = registerResource(iri, iri);
        loadResourceQuads(logger, ontology, input);
        return ontology;
    }

    /**
     * Loads an OWL resource
     *
     * @param logger The current logger
     * @param iri    The resource's IRI
     * @param input  The resource's content
     * @return The loaded ontology
     */
    private Ontology loadResourceOWL(Logger logger, String iri, LoaderResult input) {
        Ontology ontology = registerResource(iri, input.getIRI());
        for (String importedIRI : input.getImports())
            dependencies.add(importedIRI);
        loadResourceOntology(logger, ontology, input);
        return ontology;
    }

    /**
     * Registers a loaded resource
     *
     * @param documentIRI The document's IRI
     * @param logicalIRI  The logical IRI of the data within the document
     * @return The corresponding Ontology
     */
    private Ontology registerResource(String documentIRI, String logicalIRI) {
        Ontology ontology = resolveOntology(logicalIRI);
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
     * Loads a collection of quads
     *
     * @param logger   The current logger
     * @param ontology The containing ontology
     * @param quads    The quads
     */
    protected abstract void loadResourceQuads(Logger logger, Ontology ontology, Collection<Quad> quads);

    /**
     * Loads an ontology as a set of axioms
     *
     * @param logger   The current logger
     * @param ontology The containing ontology
     * @param input    The input data
     */
    protected abstract void loadResourceOntology(Logger logger, Ontology ontology, LoaderResult input);
}
