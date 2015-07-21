/*******************************************************************************
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
 ******************************************************************************/
package org.xowl.store;

import org.xowl.lang.owl2.Ontology;
import org.xowl.lang.rules.Rule;
import org.xowl.store.loaders.*;
import org.xowl.store.owl.*;
import org.xowl.store.owl.QueryEngine;
import org.xowl.store.owl.RuleEngine;
import org.xowl.store.rdf.*;
import org.xowl.store.writers.OWLSerializer;
import org.xowl.store.writers.RDFSerializer;
import org.xowl.utils.Logger;
import org.xowl.utils.collections.Adapter;
import org.xowl.utils.collections.AdaptingIterator;
import org.xowl.utils.collections.SkippableIterator;

import java.io.IOException;
import java.util.*;

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
    private Map<Ontology, Map<IRINode, ProxyObject>> proxies;
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
    public XOWLStore getStore() {
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
        this(IRIMapper.getDefault(), null);
    }

    /**
     * Initializes this repository
     *
     * @param evaluator The evaluator to use
     * @throws IOException When the backend cannot allocate a temporary file
     */
    public Repository(Evaluator evaluator) throws IOException {
        this(IRIMapper.getDefault(), evaluator);
    }

    /**
     * Initializes this repository
     *
     * @param mapper The IRI mapper to use
     * @throws IOException When the backend cannot allocate a temporary file
     */
    public Repository(IRIMapper mapper) throws IOException {
        this(mapper, null);
    }

    /**
     * Initializes this repository
     *
     * @param mapper    The IRI mapper to use
     * @param evaluator The evaluator to use
     * @throws IOException When the backend cannot allocate a temporary file
     */
    public Repository(IRIMapper mapper, Evaluator evaluator) throws IOException {
        super(mapper);
        this.backend = new XOWLStore();
        this.graphs = new HashMap<>();
        this.proxies = new HashMap<>();
        this.queryEngine = new QueryEngine(backend, evaluator);
        this.ruleEngine = new RuleEngine(backend, backend, evaluator);
    }

    /**
     * Activates the entailment rules
     *
     * @param logger The logger to use
     */
    public void activateEntailmentRules(Logger logger) {
        load(logger, IRIMapper.IRI_RDF);
        load(logger, IRIMapper.IRI_RDFS);
        load(logger, IRIMapper.IRI_OWL2);
        load(logger, IRIMapper.IRI_XOWL_RULES + "owl2");
        load(logger, IRIMapper.IRI_XOWL_RULES + "xowl");
    }

    /**
     * Resolves a proxy on the existing entity having the specified IRI in the specified ontology
     *
     * @param ontology The ontology defining the entity
     * @param iri      The IRI of an entity
     * @return The proxy, or null if the entity does not exist
     */
    public ProxyObject getProxy(Ontology ontology, String iri) {
        IRINode node = backend.getNodeExistingIRI(iri);
        if (node == null)
            return null;
        return resolveProxy(ontology, node);
    }

    /**
     * Gets a proxy on the existing entity having the specified IRI
     * The containing ontology will be computed based on the entity's IRI
     *
     * @param iri The IRI of an entity
     * @return The associated proxy, or null if the entity does not exist
     */
    public ProxyObject getProxy(String iri) {
        String[] parts = iri.split("#");
        Ontology ontology = ontologies.get(parts[0]);
        if (ontology == null)
            return null;
        IRINode node = backend.getNodeExistingIRI(iri);
        if (node == null)
            return null;
        return resolveProxy(ontology, node);
    }

    /**
     * Gets the proxy objects on entities defined in the specified ontology
     * An entity is defined within an ontology if at least one of its property is asserted in this ontology.
     * The IRI of this entity may or may not start with the IRI of the ontology, although it easier if it does.
     *
     * @param ontology An ontology
     * @return An iterator over the proxy objects
     */
    public Iterator<ProxyObject> getProxiesIn(final Ontology ontology) {
        Iterator<Quad> quads = backend.getAll(getGraph(ontology));
        final HashSet<Node> known = new HashSet<>();
        return new SkippableIterator<>(new AdaptingIterator<>(quads, new Adapter<ProxyObject>() {
            @Override
            public <X> ProxyObject adapt(X element) {
                Node subject = ((Quad) element).getSubject();
                if (subject.getNodeType() != IRINode.TYPE)
                    return null;
                if (known.contains(subject))
                    return null;
                known.add(subject);
                return resolveProxy(ontology, (IRINode) subject);
            }
        }));
    }

    /**
     * Resolves a proxy on the entity represented by the specified node in the specified ontology
     *
     * @param ontology The ontology defining the entity
     * @param iriNode  The IRI node representing the entity
     * @return The proxy
     */
    private ProxyObject resolveProxy(Ontology ontology, IRINode iriNode) {
        Map<IRINode, ProxyObject> sub = proxies.get(ontology);
        if (sub == null) {
            sub = new HashMap<>();
            proxies.put(ontology, sub);
        }
        ProxyObject proxy = sub.get(iriNode);
        if (proxy != null)
            return proxy;
        proxy = new ProxyObject(this, ontology, iriNode);
        sub.put(iriNode, proxy);
        return proxy;
    }

    /**
     * Resolves a proxy on the entity having the specified IRI in the specified ontology
     *
     * @param ontology The ontology defining the entity
     * @param iri      The IRI of an entity
     * @return The proxy
     */
    public ProxyObject resolveProxy(Ontology ontology, String iri) {
        return resolveProxy(ontology, backend.getNodeIRI(iri));
    }

    /**
     * Resolves a proxy on the entity having the specified IRI
     * The containing ontology will be computed based on the entity's IRI
     *
     * @param iri The IRI of an entity
     * @return The associated proxy
     */
    public ProxyObject resolveProxy(String iri) {
        String[] parts = iri.split("#");
        Ontology ontology = resolveOntology(parts[0]);
        return resolveProxy(ontology, backend.getNodeIRI(iri));
    }

    /**
     * Creates a new object and gets a proxy on it
     *
     * @param ontology The containing ontology for the new object
     * @return A proxy on the new object
     */
    public ProxyObject newObject(Ontology ontology) {
        Map<IRINode, ProxyObject> sub = proxies.get(ontology);
        if (sub == null) {
            sub = new HashMap<>();
            proxies.put(ontology, sub);
        }
        IRINode entity = backend.newNodeIRI(getGraph(ontology));
        ProxyObject proxy = new ProxyObject(this, ontology, entity);
        sub.put(entity, proxy);
        return proxy;
    }

    /**
     * Removes the specified proxy
     *
     * @param proxy A proxy
     */
    protected void remove(ProxyObject proxy) {
        Map<IRINode, ProxyObject> sub = proxies.get(proxy.getOntology());
        if (sub == null)
            return;
        sub.remove(proxy.entity);
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

    protected void exportResourceRDF(Logger logger, Ontology ontology, RDFSerializer output) {
        output.serialize(logger, backend.getAll(getGraph(ontology)));
    }

    protected void exportResourceOWL(Logger logger, Ontology ontology, OWLSerializer output) {
        throw new UnsupportedOperationException();
    }
}
