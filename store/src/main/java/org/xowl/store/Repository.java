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
import org.xowl.store.owl.TranslationException;
import org.xowl.store.owl.Translator;
import org.xowl.store.owl.XOWLStore;
import org.xowl.store.rdf.Changeset;
import org.xowl.store.rdf.GraphNode;
import org.xowl.store.rdf.Quad;
import org.xowl.store.rdf.UnsupportedNodeType;
import org.xowl.utils.Logger;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarFile;

/**
 * Represents a repository of xOWL ontologies
 *
 * @author Laurent Wouters
 */
public class Repository {
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
    private IRIMapper mapper;
    /**
     * The backend store
     */
    private XOWLStore backend;
    /**
     * The loaded resources
     */
    private Map<String, Ontology> resources;
    /**
     * The ontologies in this repository
     */
    private Map<Ontology, GraphNode> ontologies;
    /**
     * The remaining dependencies
     */
    private List<String> dependencies;

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
     * @throws IOException When the backend cannot allocate a temporary file
     */
    public Repository() throws IOException {
        this.mapper = IRIMapper.getDefault();
        this.backend = new XOWLStore();
        this.resources = new HashMap<>();
        this.ontologies = new HashMap<>();
        this.dependencies = new ArrayList<>();
    }

    /**
     * Initializes this repository
     *
     * @param mapper The IRI mapper to use
     * @throws IOException When the backend cannot allocate a temporary file
     */
    public Repository(IRIMapper mapper) throws IOException {
        this.mapper = mapper;
        this.backend = new XOWLStore();
        this.ontologies = new HashMap<>();
    }


    /**
     * Loads a resource and resolves its dependencies
     *
     * @param logger The current logger
     * @param iri    The resource's IRI
     */
    public void load(Logger logger, String iri) {
        loadResource(logger, iri);
        while (!dependencies.isEmpty()) {
            List<String> batch = new ArrayList<>(dependencies);
            dependencies.clear();
            for (String dependency : batch) {
                loadResource(logger, dependency);
            }
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
     */
    private void loadResource(Logger logger, String iri) {
        String resource = mapper.get(iri);
        if (resource == null) {
            logger.error("Cannot identify the location of " + iri);
            return;
        }
        Reader reader = getReaderFor(logger, resource);
        if (reader == null)
            return;
        String syntax = getSyntax(logger, resource);
        if (syntax == null)
            return;
        loadResource(logger, reader, iri, syntax);
    }

    /**
     * Loads a resource
     *
     * @param logger The current logger
     * @param reader The input reader
     * @param iri    The resource's IRI
     * @param syntax The resource's syntax
     */
    private void loadResource(Logger logger, Reader reader, String iri, String syntax) {
        if (resources.containsKey(iri))
            return;
        switch (syntax) {
            case SYNTAX_NTRIPLES:
                loadResourceQuads(logger, reader, iri, new NTriplesLoader(backend));
                break;
            case SYNTAX_NQUADS:
                loadResourceQuads(logger, reader, iri, new NQuadsLoader(backend));
                break;
            case SYNTAX_TURTLE:
                loadResourceQuads(logger, reader, iri, new TurtleLoader(backend));
                break;
            case SYNTAX_RDFXML:
                loadResourceQuads(logger, reader, iri, new RDFXMLLoader(backend));
                break;
            case SYNTAX_FUNCTIONAL_OWL2:
                loadResourceAxioms(logger, reader, iri, new FunctionalOWL2Loader());
                break;
            case SYNTAX_FUNCTIONAL_XOWL:
                loadResourceAxioms(logger, reader, iri, new FunctionalXOWLLoader());
                break;
        }
        try {
            reader.close();
        } catch (IOException ex) {
            logger.error(ex);
        }
    }

    /**
     * Loads a resource as a set of quads
     *
     * @param logger The current logger
     * @param reader The input reader
     * @param iri    The resource's IRI
     * @param loader The resource's loader
     */
    private void loadResourceQuads(Logger logger, Reader reader, String iri, Loader loader) {
        List<Quad> quads = loader.loadQuads(logger, reader, iri);
        try {
            backend.insert(new Changeset(quads, new ArrayList<Quad>(0)));
            registerResource(iri, iri);
        } catch (UnsupportedNodeType ex) {
            logger.error(ex);
        }
    }

    /**
     * Loads a resource as a set of axioms
     *
     * @param logger The current logger
     * @param reader The input reader
     * @param iri    The resource's IRI
     * @param loader The resource's loader
     */
    private void loadResourceAxioms(Logger logger, Reader reader, String iri, Loader loader) {
        try {
            LoaderResult result = loader.loadAxioms(logger, reader, iri);
            Translator translator = new Translator(null, backend, result, null);
            backend.insert(translator.execute());
            registerResource(iri, result.getIRI());
            for (String importedIRI : result.getImports())
                dependencies.add(importedIRI);
        } catch (TranslationException | UnsupportedNodeType ex) {
            logger.error(ex);
        }
    }

    /**
     * Registers a loaded resource
     *
     * @param documentIRI The document's IRI
     * @param logicalIRI  The logical IRI of the data within the document
     */
    private void registerResource(String documentIRI, String logicalIRI) {
        GraphNode graphNode = backend.getNodeIRI(logicalIRI);
        Ontology ontology = new Ontology();
        IRI ontoIRI = new IRI();
        ontoIRI.setHasValue(logicalIRI);
        ontology.setHasIRI(ontoIRI);
        ontologies.put(ontology, graphNode);
        resources.put(documentIRI, ontology);
    }
}
