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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a repository of xOWL ontologies
 *
 * @author Laurent Wouters
 */
public class Repository extends AbstractRepository {
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
        super();
        this.backend = new XOWLStore();
        this.ontologies = new HashMap<>();
    }

    /**
     * Initializes this repository
     *
     * @param mapper The IRI mapper to use
     * @throws IOException When the backend cannot allocate a temporary file
     */
    public Repository(IRIMapper mapper) throws IOException {
        super(mapper);
        this.backend = new XOWLStore();
        this.ontologies = new HashMap<>();
    }

    @Override
    protected Loader newRDFLoader(String syntax) {
        switch (syntax) {
            case SYNTAX_NTRIPLES:
                return new NTriplesLoader(backend);
            case SYNTAX_NQUADS:
                return new NQuadsLoader(backend);
            case SYNTAX_TURTLE:
                return new TurtleLoader(backend);
            case SYNTAX_RDFXML:
                return new RDFXMLLoader(backend);
        }
        return null;
    }

    @Override
    protected void loadResourceQuads(Logger logger, Ontology ontology, Collection<Quad> quads) {
        GraphNode graphNode = backend.getNodeIRI(ontology.getHasIRI().getHasValue());
        ontologies.put(ontology, graphNode);
        try {
            backend.insert(new Changeset(quads, new ArrayList<Quad>(0)));
        } catch (UnsupportedNodeType ex) {
            logger.error(ex);
        }
    }

    @Override
    protected void loadResourceOntology(Logger logger, Ontology ontology, LoaderResult input) {
        try {
            Translator translator = new Translator(null, backend, null);
            Collection<Quad> quads = translator.translate(input);
            loadResourceQuads(logger, ontology, quads);
        } catch (TranslationException ex) {
            logger.error(ex);
        }
    }
}
