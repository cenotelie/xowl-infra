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

import fr.cenotelie.commons.storage.ConcurrentWriteException;
import fr.cenotelie.commons.utils.logging.Logger;
import fr.cenotelie.commons.utils.logging.Logging;
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
import org.xowl.infra.store.storage.Store;
import org.xowl.infra.store.storage.StoreFactory;
import org.xowl.infra.store.storage.StoreTransaction;
import org.xowl.infra.store.writers.OWLSerializer;
import org.xowl.infra.store.writers.RDFSerializer;

import java.io.IOException;
import java.io.StringReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a repository of xOWL ontologies base on RDF
 *
 * @author Laurent Wouters
 * Modified by
 * @author Stephen Creff
 */
public class RepositoryRDF extends Repository {
    /**
     * The backing store
     */
    private final Store store;
    /**
     * The ontologies in this repository
     */
    private final Map<Ontology, GraphNode> graphs;
    /**
     * The query engine for this repository
     */
    private OWLQueryEngine queryEngine;
    /**
     * The rule engine for this repository
     */
    private OWLRuleEngine ruleEngine;

    /**
     * Gets the backing store
     *
     * @return The backing store
     */
    public Store getStore() {
        return store;
    }

    /**
     * Initializes this repository
     */
    public RepositoryRDF() {
        this(StoreFactory.newInMemory(), IRIMapper.getDefault(), false);
    }

    /**
     * Initializes this repository
     *
     * @param store The store to use
     */
    public RepositoryRDF(Store store) {
        this(store, IRIMapper.getDefault(), false);
    }

    /**
     * Initializes this repository
     *
     * @param mapper The IRI mapper to use
     */
    public RepositoryRDF(IRIMapper mapper) {
        this(StoreFactory.newInMemory(), mapper, false);
    }

    /**
     * Initializes this repository
     *
     * @param mapper              The IRI mapper to use
     * @param resolveDependencies Whether dependencies should be resolved when loading resources
     */
    public RepositoryRDF(IRIMapper mapper, boolean resolveDependencies) {
        this(StoreFactory.newInMemory(), mapper, resolveDependencies);
    }

    /**
     * Initializes this repository
     *
     * @param store               The store to use
     * @param mapper              The IRI mapper to use
     * @param resolveDependencies Whether dependencies should be resolved when loading resources
     */
    public RepositoryRDF(Store store, IRIMapper mapper, boolean resolveDependencies) {
        super(mapper, resolveDependencies);
        this.store = store;
        try (StoreTransaction transaction = this.store.newTransaction(false)) {
            this.store.setExecutionManager(executionManager);
        } catch (ConcurrentWriteException exception) {
            // cannot happen
        }
        this.graphs = new HashMap<>();
    }

    /**
     * Runs a task as a transaction on this repository
     *
     * @param task The task to run
     * @param <T>  The type of the return value
     * @return The result of the callable
     */
    public <T> T runAsTransaction(RepositoryTask<RepositoryRDF, T> task) {
        while (true) {
            try (StoreTransaction transaction = store.newTransaction(true, false)) {
                T result = task.execute(this);
                transaction.commit();
                return result;
            } catch (ConcurrentWriteException exception) {
                // failed to commit
            }
            // retry in a bit
            try {
                Thread.sleep(100);
            } catch (InterruptedException exception) {
                // do nothing
            }
        }
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
            node = store.getTransaction().getDataset().getIRINode(ontology.getHasIRI().getHasValue());
            graphs.put(ontology, node);
            return node;
        }
    }

    @Override
    public synchronized OWLQueryEngine getOWLQueryEngine() {
        if (queryEngine == null)
            queryEngine = new OWLQueryEngine(store, executionManager);
        return queryEngine;
    }

    @Override
    public RDFQueryEngine getRDFQueryEngine() {
        return getOWLQueryEngine().getBackend();
    }

    @Override
    public synchronized OWLRuleEngine getOWLRuleEngine() {
        if (ruleEngine == null)
            ruleEngine = new OWLRuleEngine(store, store, executionManager);
        return ruleEngine;
    }

    @Override
    public RDFRuleEngine getRDFRuleEngine() {
        return getOWLRuleEngine().getBackend();
    }

    @Override
    public void setEntailmentRegime(EntailmentRegime regime) throws IOException {
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
                load(Logging.get(), IRIs.RDF, IRIs.GRAPH_INFERENCE, false);
                load(Logging.get(), IRIs.RDFS, IRIs.GRAPH_INFERENCE, false);
                load(Logging.get(), IRIs.OWL2, IRIs.GRAPH_INFERENCE, false);
                load(Logging.get(), IRIs.XOWL_RULES + "owl2", IRIs.GRAPH_INFERENCE, false);
                load(Logging.get(), IRIs.XOWL_RULES + "xowl", IRIs.GRAPH_INFERENCE, false);
                break;
        }
    }

    @Override
    protected DatasetNodes getNodeManager() {
        return store.getTransaction().getDataset();
    }

    @Override
    protected void doLoadRDF(Logger logger, Ontology ontology, RDFLoaderResult input) {
        getGraph(ontology);
        store.getTransaction().getDataset().insert(Changeset.fromAdded(input.getQuads()));

        if (!input.getRules().isEmpty()) {
            for (RDFRule rule : input.getRules()) {
                getRDFRuleEngine().add(rule);
            }
            getRDFRuleEngine().flush();
        }
    }

    @Override
    protected void doLoadOWL(Logger logger, Ontology ontology, OWLLoaderResult input) {
        Translator translator = new Translator(null, store.getTransaction().getDataset());
        Collection<Quad> quads = translator.translate(input);
        store.getTransaction().getDataset().insert(Changeset.fromAdded(quads));

        if (!input.getRules().isEmpty()) {
            for (Rule rule : input.getRules()) {
                getOWLRuleEngine().add(rule, null, null, null);
            }
            getOWLRuleEngine().flush();
        }
    }

    @Override
    protected void doExportRDF(Logger logger, Ontology ontology, RDFSerializer output) {
        output.serialize(logger, store.getTransaction().getDataset().getAll(getGraph(ontology)));
    }

    @Override
    protected void doExportRDF(Logger logger, RDFSerializer output) {
        output.serialize(logger, store.getTransaction().getDataset().getAll());
    }

    @Override
    protected void exportResourceOWL(Logger logger, Ontology ontology, OWLSerializer output) {
        throw new UnsupportedOperationException();
    }
}
