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
 *     Stephen Creff - stephen.creff@gmail.com
 ******************************************************************************/
package org.xowl.store;

import org.xowl.lang.owl2.Ontology;
import org.xowl.lang.rules.Rule;
import org.xowl.store.loaders.*;
import org.xowl.store.owl.QueryEngine;
import org.xowl.store.owl.RuleEngine;
import org.xowl.store.owl.TranslationException;
import org.xowl.store.owl.Translator;
import org.xowl.store.rdf.*;
import org.xowl.store.sparql.Command;
import org.xowl.store.sparql.Result;
import org.xowl.store.sparql.ResultFailure;
import org.xowl.store.storage.BaseStore;
import org.xowl.store.storage.InMemoryStore;
import org.xowl.store.storage.UnsupportedNodeType;
import org.xowl.store.writers.OWLSerializer;
import org.xowl.store.writers.RDFSerializer;
import org.xowl.utils.Logger;
import org.xowl.utils.collections.Adapter;
import org.xowl.utils.collections.AdaptingIterator;
import org.xowl.utils.collections.SkippableIterator;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.*;

/**
 * Represents a repository of xOWL ontologies
 *
 * @author Laurent Wouters
 * Modified by
 * @author Stephen Creff
 */
public class Repository extends AbstractRepository {
    /**
     * The loader of evaluator services
     */
    private static ServiceLoader<Evaluator> SERVICE_EVALUATOR = ServiceLoader.load(Evaluator.class);

    /**
     * Gets the default evaluator
     *
     * @return The default evaluator
     */
    private static Evaluator getDefaultEvaluator() {
        Iterator<Evaluator> services = SERVICE_EVALUATOR.iterator();
        return services.hasNext() ? services.next() : null;
    }

    /**
     * Gets a new default store
     *
     * @return A new default store
     */
    private static BaseStore getDefaultStore() {
        return new InMemoryStore();
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
     * The evaluator to use
     */
    private final Evaluator evaluator;
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
    public BaseStore getStore() {
        return backend;
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
     * Gets the associated OWL query engine
     *
     * @return The associated OWL query engine
     */
    public QueryEngine getOWLQueryEngine() {
        if (queryEngine == null)
            queryEngine = new QueryEngine(backend, evaluator);
        return queryEngine;
    }

    /**
     * Gets the associated RDF query engine
     *
     * @return The associated RDF query engine
     */
    public org.xowl.store.rdf.QueryEngine getRDFQueryEngine() {
        return getOWLQueryEngine().getBackend();
    }

    /**
     * Gets the associated OWL rule engine
     *
     * @return The associated OWL rule engine
     */
    public RuleEngine getOWLRuleEngine() {
        if (ruleEngine == null)
            ruleEngine = new RuleEngine(backend, backend, evaluator);
        return ruleEngine;
    }

    /**
     * Gets the associated RDF rule engine
     *
     * @return The associated RDF rule engine
     */
    public org.xowl.store.rdf.RuleEngine getRDFRuleEngine() {
        return getOWLRuleEngine().getBackend();
    }

    /**
     * Initializes this repository
     */
    public Repository() {
        this(getDefaultStore(), IRIMapper.getDefault(), getDefaultEvaluator());
    }

    /**
     * Initializes this repository
     *
     * @param store The store to use as backend
     */
    public Repository(BaseStore store) {
        this(store, IRIMapper.getDefault(), getDefaultEvaluator());
    }

    /**
     * Initializes this repository
     *
     * @param mapper The IRI mapper to use
     */
    public Repository(IRIMapper mapper) {
        this(getDefaultStore(), mapper, getDefaultEvaluator());
    }

    /**
     * Initializes this repository
     *
     * @param evaluator The evaluator to use
     */
    public Repository(Evaluator evaluator) {
        this(getDefaultStore(), IRIMapper.getDefault(), evaluator);
    }

    /**
     * Initializes this repository
     *
     * @param store  The store to use as backend
     * @param mapper The IRI mapper to use
     */
    public Repository(BaseStore store, IRIMapper mapper) {
        this(store, mapper, getDefaultEvaluator());
    }

    /**
     * Initializes this repository
     *
     * @param store     The store to use as backend
     * @param evaluator The evaluator to use
     */
    public Repository(BaseStore store, Evaluator evaluator) {
        this(store, IRIMapper.getDefault(), evaluator);
    }

    /**
     * Initializes this repository
     *
     * @param mapper    The IRI mapper to use
     * @param evaluator The evaluator to use
     */
    public Repository(IRIMapper mapper, Evaluator evaluator) {
        this(getDefaultStore(), mapper, evaluator);
    }

    /**
     * Initializes this repository
     *
     * @param store     The store to use as backend
     * @param mapper    The IRI mapper to use
     * @param evaluator The evaluator to use
     */
    public Repository(BaseStore store, IRIMapper mapper, Evaluator evaluator) {
        super(mapper);
        this.backend = store;
        this.graphs = new HashMap<>();
        this.proxies = new HashMap<>();
        this.evaluator = evaluator;
    }

    /**
     * Executes the specified SPARQL command
     *
     * @param logger The logger to use
     * @param sparql A SPARQL command
     * @return The result
     */
    public Result execute(Logger logger, String sparql) {
        SPARQLLoader loader = new SPARQLLoader(backend);
        List<Command> commands = loader.load(logger, new StringReader(sparql));
        if (commands == null)
            return ResultFailure.INSTANCE;
        Result result = ResultFailure.INSTANCE;
        for (Command command : commands) {
            result = command.execute(this);
            if (result.isFailure())
                break;
        }
        return result;
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
        String[] parts = iri.split("#");
        Ontology ontology = ontologies.get(parts[0]);
        if (ontology == null)
            return null;
        IRINode node = backend.getExistingIRINode(iri);
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
    }

    /**
     * Resolves a proxy on the entity represented by the specified node in the specified ontology
     *
     * @param ontology The ontology defining the entity
     * @param subject  The subject node representing the entity
     * @return The proxy
     */
    public ProxyObject resolveProxy(Ontology ontology, SubjectNode subject) {
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
    protected GraphNode getGraph(Ontology ontology) {
        GraphNode node = graphs.get(ontology);
        if (node != null)
            return node;
        node = backend.getIRINode(ontology.getHasIRI().getHasValue());
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
            case SYNTAX_JSON_LD:
                return new JSONLDLoader(backend) {
                    @Override
                    protected Reader getReaderFor(Logger logger, String iri) {
                        String resource = mapper.get(iri);
                        if (resource == null) {
                            logger.error("Cannot identify the location of " + iri);
                            return null;
                        }
                        try {
                            return AbstractRepository.getReaderFor(resource);
                        } catch (IOException ex) {
                            logger.error(ex);
                            return null;
                        }
                    }
                };
        }
        return null;
    }

    @Override
    protected void loadResourceRDF(Logger logger, Ontology ontology, RDFLoaderResult input) {
        getGraph(ontology);
        try {
            backend.insert(new Changeset(input.getQuads(), new ArrayList<>(0)));
        } catch (UnsupportedNodeType ex) {
            logger.error(ex);
        }

        for (org.xowl.store.rdf.Rule rule : input.getRules()) {
            getRDFRuleEngine().add(rule);
        }
    }

    @Override
    protected void loadResourceOWL(Logger logger, Ontology ontology, OWLLoaderResult input) {
        try {
            Translator translator = new Translator(null, backend, null);
            Collection<Quad> quads = translator.translate(input);
            backend.insert(new Changeset(quads, new ArrayList<>(0)));
        } catch (TranslationException | UnsupportedNodeType ex) {
            logger.error(ex);
        }

        for (Rule rule : input.getRules()) {
            getOWLRuleEngine().add(rule, null, null, null);
        }
    }

    @Override
    protected void exportResourceRDF(Logger logger, Ontology ontology, RDFSerializer output) {
        output.serialize(logger, backend.getAll(getGraph(ontology)));
    }

    @Override
    protected void exportResourceRDF(Logger logger, RDFSerializer output) {
        output.serialize(logger, backend.getAll());
    }

    @Override
    protected void exportResourceOWL(Logger logger, Ontology ontology, OWLSerializer output) {
        throw new UnsupportedOperationException();
    }
}
