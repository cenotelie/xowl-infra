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

import org.xowl.infra.lang.owl2.Ontology;
import org.xowl.infra.lang.rules.Rule;
import org.xowl.infra.store.loaders.OWLLoaderResult;
import org.xowl.infra.store.loaders.RDFLoaderResult;
import org.xowl.infra.store.loaders.SPARQLLoader;
import org.xowl.infra.store.owl.OWLQueryEngine;
import org.xowl.infra.store.owl.OWLRuleEngine;
import org.xowl.infra.store.owl.Translator;
import org.xowl.infra.store.rdf.*;
import org.xowl.infra.store.sparql.Command;
import org.xowl.infra.store.sparql.Result;
import org.xowl.infra.store.sparql.ResultFailure;
import org.xowl.infra.store.storage.BaseStore;
import org.xowl.infra.store.storage.NodeManager;
import org.xowl.infra.store.storage.StoreFactory;
import org.xowl.infra.store.storage.UnsupportedNodeType;
import org.xowl.infra.store.writers.OWLSerializer;
import org.xowl.infra.store.writers.RDFSerializer;
import org.xowl.infra.utils.collections.Adapter;
import org.xowl.infra.utils.collections.AdaptingIterator;
import org.xowl.infra.utils.collections.SingleIterator;
import org.xowl.infra.utils.collections.SkippableIterator;
import org.xowl.infra.utils.logging.Logger;
import org.xowl.infra.utils.logging.Logging;

import java.io.StringReader;
import java.util.*;

/**
 * Represents a repository of xOWL ontologies base on RDF
 *
 * @author Laurent Wouters
 *         Modified by
 * @author Stephen Creff
 */
public class RepositoryRDF extends Repository {
    /**
     * Gets a new default store
     *
     * @return A new default store
     */
    private static BaseStore getDefaultStore() {
        return StoreFactory.create().make();
    }

    /**
     * The backend store
     */
    private final BaseStore backend;
    /**
     * The ontologies in this repository
     */
    private final Map<Ontology, GraphNode> graphs;
    /**
     * The proxies onto this repository
     */
    private final Map<Ontology, Map<SubjectNode, ProxyObject>> proxies;
    /**
     * The query engine for this repository
     */
    private OWLQueryEngine queryEngine;
    /**
     * The rule engine for this repository
     */
    private OWLRuleEngine ruleEngine;

    /**
     * Gets the backend store
     *
     * @return the backend store
     */
    public BaseStore getStore() {
        return backend;
    }

    /**
     * Initializes this repository
     */
    public RepositoryRDF() {
        this(getDefaultStore(), IRIMapper.getDefault());
    }

    /**
     * Initializes this repository
     *
     * @param store The store to use as backend
     */
    public RepositoryRDF(BaseStore store) {
        this(store, IRIMapper.getDefault());
    }

    /**
     * Initializes this repository
     *
     * @param mapper The IRI mapper to use
     */
    public RepositoryRDF(IRIMapper mapper) {
        this(getDefaultStore(), mapper);
    }

    /**
     * Initializes this repository
     *
     * @param store  The store to use as backend
     * @param mapper The IRI mapper to use
     */
    public RepositoryRDF(BaseStore store, IRIMapper mapper) {
        super(mapper);
        this.backend = store;
        this.backend.setEvaluableFactory(executionManager);
        this.graphs = new HashMap<>();
        this.proxies = new HashMap<>();
    }

    /**
     * Executes the specified SPARQL command
     *
     * @param logger The logger to use
     * @param sparql A SPARQL command
     * @return The result
     */
    public Result execute(Logger logger, String sparql) {
        SPARQLLoader loader = new SPARQLLoader(getNodeManager());
        Command command = loader.load(logger, new StringReader(sparql));
        if (command == null)
            return ResultFailure.INSTANCE;
        return command.execute(this);
    }

    /**
     * Gets a proxy on the existing entity having the specified IRI in the specified ontology
     *
     * @param ontology The ontology defining the entity
     * @param iri      The IRI of an entity
     * @return The proxy, or null if the entity does not exist
     */
    public ProxyObject getProxy(Ontology ontology, String iri) {
        IRINode node = backend.getExistingIRINode(iri);
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
        IRINode node = backend.getExistingIRINode(iri);
        if (node == null)
            return null;
        String[] parts = iri.split("#");
        Ontology ontology = ontologies.get(parts[0]);
        if (ontology == null) {
            ontology = resolveOntology(parts[0]);
            ontologies.put(parts[0], ontology);
        }
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
        try {
            Iterator<Quad> quads = backend.getAll(getGraph(ontology));
            final HashSet<SubjectNode> known = new HashSet<>();
            return new SkippableIterator<>(new AdaptingIterator<>(quads, new Adapter<ProxyObject>() {
                @Override
                public <X> ProxyObject adapt(X element) {
                    SubjectNode subject = ((Quad) element).getSubject();
                    if (subject.getNodeType() != Node.TYPE_IRI && subject.getNodeType() != Node.TYPE_BLANK)
                        return null;
                    if (known.contains(subject))
                        return null;
                    known.add(subject);
                    return resolveProxy(ontology, subject);
                }
            }));
        } catch (UnsupportedNodeType exception) {
            Logging.get().error(exception);
            return new SingleIterator<>(null);
        }
    }

    /**
     * Resolves a proxy on the entity represented by the specified node in the specified ontology
     *
     * @param ontology The ontology defining the entity
     * @param subject  The subject node representing the entity
     * @return The proxy
     */
    public ProxyObject resolveProxy(Ontology ontology, SubjectNode subject) {
        synchronized (proxies) {
            Map<SubjectNode, ProxyObject> sub = proxies.get(ontology);
            if (sub == null) {
                sub = new HashMap<>();
                proxies.put(ontology, sub);
            }
            ProxyObject proxy = sub.get(subject);
            if (proxy != null)
                return proxy;
            proxy = new ProxyObject(this, ontology, subject);
            sub.put(subject, proxy);
            return proxy;
        }
    }

    /**
     * Resolves a proxy on the entity having the specified IRI in the specified ontology
     *
     * @param ontology The ontology defining the entity
     * @param iri      The IRI of an entity
     * @return The proxy
     */
    public ProxyObject resolveProxy(Ontology ontology, String iri) {
        return resolveProxy(ontology, backend.getIRINode(iri));
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
        return resolveProxy(ontology, backend.getIRINode(iri));
    }

    /**
     * Creates a new object and gets a proxy on it
     *
     * @param ontology The containing ontology for the new object
     * @return A proxy on the new object
     */
    public ProxyObject newObject(Ontology ontology) {
        synchronized (proxies) {
            Map<SubjectNode, ProxyObject> sub = proxies.get(ontology);
            if (sub == null) {
                sub = new HashMap<>();
                proxies.put(ontology, sub);
            }
            IRINode entity = backend.getIRINode(getGraph(ontology));
            ProxyObject proxy = new ProxyObject(this, ontology, entity);
            sub.put(entity, proxy);
            return proxy;
        }
    }

    /**
     * Removes the specified proxy
     *
     * @param proxy A proxy
     */
    protected void remove(ProxyObject proxy) {
        Map<SubjectNode, ProxyObject> sub = proxies.get(proxy.getOntology());
        if (sub == null)
            return;
        sub.remove(proxy.subject);
    }

    /**
     * Resolves a graph node for the specified ontology
     *
     * @param ontology An ontology
     * @return The associated graph node
     */
    public GraphNode getGraph(Ontology ontology) {
        synchronized (graphs) {
            GraphNode node = graphs.get(ontology);
            if (node != null)
                return node;
            node = backend.getIRINode(ontology.getHasIRI().getHasValue());
            graphs.put(ontology, node);
            return node;
        }
    }

    @Override
    public synchronized OWLQueryEngine getOWLQueryEngine() {
        if (queryEngine == null)
            queryEngine = new OWLQueryEngine(backend, executionManager);
        return queryEngine;
    }

    @Override
    public RDFQueryEngine getRDFQueryEngine() {
        return getOWLQueryEngine().getBackend();
    }

    @Override
    public synchronized OWLRuleEngine getOWLRuleEngine() {
        if (ruleEngine == null)
            ruleEngine = new OWLRuleEngine(backend, backend, executionManager);
        return ruleEngine;
    }

    @Override
    public RDFRuleEngine getRDFRuleEngine() {
        return getOWLRuleEngine().getBackend();
    }

    @Override
    public void setEntailmentRegime(EntailmentRegime regime) throws Exception {
        if (this.regime != EntailmentRegime.none) {
            throw new IllegalArgumentException("Entailment regime is already set");
        }
        this.regime = regime;
        switch (this.regime) {
            case RDF:
                load(Logging.get(), IRIs.RDF);
                break;
            case RDFS:
                load(Logging.get(), IRIs.RDFS);
                break;
            case OWL2_RDF:
            case OWL2_DIRECT:
                load(Logging.get(), IRIs.RDF);
                load(Logging.get(), IRIs.RDFS);
                load(Logging.get(), IRIs.OWL2);
                load(Logging.get(), IRIs.XOWL_RULES + "owl2");
                load(Logging.get(), IRIs.XOWL_RULES + "xowl");
                break;
        }
    }

    @Override
    protected NodeManager getNodeManager() {
        return backend;
    }

    @Override
    protected void doLoadRDF(Logger logger, Ontology ontology, RDFLoaderResult input) throws Exception {
        getGraph(ontology);
        backend.insert(Changeset.fromAdded(input.getQuads()));

        if (!input.getRules().isEmpty()) {
            for (RDFRule rule : input.getRules()) {
                getRDFRuleEngine().add(rule);
            }
            getRDFRuleEngine().flush();
        }
    }

    @Override
    protected void doLoadOWL(Logger logger, Ontology ontology, OWLLoaderResult input) throws Exception {
        Translator translator = new Translator(null, backend);
        Collection<Quad> quads = translator.translate(input);
        backend.insert(Changeset.fromAdded(quads));

        if (!input.getRules().isEmpty()) {
            for (Rule rule : input.getRules()) {
                getOWLRuleEngine().add(rule, null, null, null);
            }
            getOWLRuleEngine().flush();
        }
    }

    @Override
    protected void doExportRDF(Logger logger, Ontology ontology, RDFSerializer output) throws Exception {
        output.serialize(logger, backend.getAll(getGraph(ontology)));
    }

    @Override
    protected void doExportRDF(Logger logger, RDFSerializer output) throws Exception {
        output.serialize(logger, backend.getAll());
    }

    @Override
    protected void exportResourceOWL(Logger logger, Ontology ontology, OWLSerializer output) throws Exception {
        throw new UnsupportedOperationException();
    }
}
