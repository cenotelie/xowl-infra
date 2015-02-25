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

import org.xowl.store.owl.TranslationException;
import org.xowl.store.owl.Translator;
import org.xowl.store.owl.XOWLStore;
import org.xowl.store.loaders.LoaderResult;
import org.xowl.lang.owl2.IRI;
import org.xowl.lang.owl2.Ontology;
import org.xowl.store.rdf.GraphNode;
import org.xowl.store.rdf.UnsupportedNodeType;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a repository of xOWL ontologies
 *
 * @author Laurent Wouters
 */
public class Repository {
    /**
     * The backend store
     */
    private XOWLStore backend;
    /**
     * The ontologies in this repository
     */
    private Map<Ontology, GraphNode> ontologies;

    /**
     * Initializes this repository
     *
     * @throws IOException When the backend cannot allocate a temporary file
     */
    public Repository() throws IOException {
        this.backend = new XOWLStore();
        this.ontologies = new HashMap<>();
    }

    /**
     * Gets the ontologies in this repository
     *
     * @return The ontologies in this repository
     */
    public Collection<Ontology> getOntologies() {
        return ontologies.keySet();
    }

    /**
     * Loads the axioms provided by the specified loader result
     *
     * @param input A loader result
     * @throws org.xowl.store.owl.TranslationException When a runtime entity is not named
     * @throws org.xowl.store.rdf.UnsupportedNodeType       When the subject node type is unsupported
     */
    public void add(LoaderResult input) throws TranslationException, UnsupportedNodeType {
        Translator translator = new Translator(null, backend, input, null);
        backend.insert(translator.execute());
        GraphNode graphNode = backend.getNodeIRI(input.getIri());
        Ontology ontology = new Ontology();
        IRI iri = new IRI();
        iri.setHasValue(input.getIri());
        ontology.setHasIRI(iri);
        ontologies.put(ontology, graphNode);
    }
}
