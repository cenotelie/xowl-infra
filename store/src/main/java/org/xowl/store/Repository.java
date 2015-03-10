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
import org.xowl.lang.rules.Rule;
import org.xowl.store.loaders.*;
import org.xowl.store.owl.QueryEngine;
import org.xowl.store.owl.RuleEngine;
import org.xowl.store.owl.*;
import org.xowl.store.rdf.*;
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
    private Map<Ontology, GraphNode> graphs;
    /**
     * The proxies onto this repository
     */
    private Map<Ontology, Map<String, ProxyObject>> proxies;
    /**
     * The query engine for this repository
     */
    private QueryEngine queryEngine;
    /**
     * The rule engine for this repository
     */
    private RuleEngine ruleEngine;

    /**
     * Gets the backend store
     *
     * @return the backend store
     */
    protected XOWLStore getBackend() {
        return backend;
    }

    /**
     * Gets the associated query engine
     *
     * @return The associated query engine
     */
    public QueryEngine getQueryEngine() {
        return queryEngine;
    }

    /**
     * Gets the associated rule engine
     *
     * @return The associated rule engine
     */
    public RuleEngine getRuleEngine() {
        return ruleEngine;
    }

    /**
     * Initializes this repository
     *
     * @throws IOException When the backend cannot allocate a temporary file
     */
    public Repository() throws IOException {
        super();
        this.backend = new XOWLStore();
        this.graphs = new HashMap<>();
        this.proxies = new HashMap<>();
        this.queryEngine = new QueryEngine(backend, null);
        this.ruleEngine = new RuleEngine(backend, null);
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
        this.graphs = new HashMap<>();
        this.proxies = new HashMap<>();
        this.queryEngine = new QueryEngine(backend, null);
        this.ruleEngine = new RuleEngine(backend, null);
    }

    /**
     * Adds the default inference rules to the rule engine
     *
     * @param logger The logger to use
     */
    public void addDefaultInferenceRules(Logger logger) {
        load(logger, "http://xowl.org/store/rules/owl2");
    }

    /**
     * Gets a proxy on an existing entity in this repository
     *
     * @param iri The iri of an entity
     * @return The associated proxy
     */
    public ProxyObject getProxy(String iri) {
        String[] parts = iri.split("#");
        Ontology ontology = resolveOntology(parts[0]);
        Map<String, ProxyObject> sub = proxies.get(ontology);
        if (sub == null) {
            sub = new HashMap<>();
            proxies.put(ontology, sub);
        }
        ProxyObject proxy = sub.get(iri);
        if (proxy != null)
            return proxy;
        proxy = new ProxyObject(this, ontology, backend.getNodeIRI(iri));
        sub.put(iri, proxy);
        return proxy;
    }

    /**
     * Creates a new object and gets a proxy on it
     *
     * @param ontology The containing ontology for the new object
     * @return A proxy on the new object
     */
    public ProxyObject newObject(Ontology ontology) {
        Map<String, ProxyObject> sub = proxies.get(ontology);
        if (sub == null) {
            sub = new HashMap<>();
            proxies.put(ontology, sub);
        }
        IRINode entity = backend.newNodeIRI(getGraph(ontology));
        ProxyObject proxy = new ProxyObject(this, ontology, entity);
        sub.put(entity.getIRIValue(), proxy);
        return proxy;
    }

    /**
     * Removes the specified proxy
     *
     * @param proxy A proxy
     */
    protected void remove(ProxyObject proxy) {
        Map<String, ProxyObject> sub = proxies.get(proxy.getOntology());
        if (sub == null)
            return;
        sub.remove(proxy.getIRIString());
    }

    /**
     * Resolves a graph node for the specified ontology
     *
     * @param ontology An ontology
     * @return The associated graph node
     */
    protected GraphNode getGraph(Ontology ontology) {
        GraphNode node = graphs.get(ontology);
        if (node != null)
            return node;
        node = backend.getNodeIRI(ontology.getHasIRI().getHasValue());
        graphs.put(ontology, node);
        return node;
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
            case SYNTAX_RDFT:
                return new RDFTLoader(backend);
            case SYNTAX_RDFXML:
                return new RDFXMLLoader(backend);
        }
        return null;
    }

    @Override
    protected void loadResourceRDF(Logger logger, Ontology ontology, RDFLoaderResult input) {
        getGraph(ontology);
        try {
            backend.insert(new Changeset(input.getQuads(), new ArrayList<Quad>(0)));
        } catch (UnsupportedNodeType ex) {
            logger.error(ex);
        }

        for (org.xowl.store.rdf.Rule rule : input.getRules()) {
            ruleEngine.getBackend().add(rule);
        }
    }

    @Override
    protected void loadResourceOWL(Logger logger, Ontology ontology, OWLLoaderResult input) {
        try {
            Translator translator = new Translator(null, backend, null);
            Collection<Quad> quads = translator.translate(input);
            backend.insert(new Changeset(quads, new ArrayList<Quad>(0)));
        } catch (TranslationException | UnsupportedNodeType ex) {
            logger.error(ex);
        }

        for (Rule rule : input.getRules()) {
            ruleEngine.add(rule, null, null, null);
        }
    }
}
