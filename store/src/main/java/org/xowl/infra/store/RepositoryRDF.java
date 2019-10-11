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
import org.xowl.infra.lang.rules.Rule;
import org.xowl.infra.store.execution.Evaluator;
import org.xowl.infra.store.loaders.OWLLoaderResult;
import org.xowl.infra.store.loaders.RDFLoaderResult;
import org.xowl.infra.store.loaders.SPARQLLoader;
import org.xowl.infra.store.owl.OWLQueryEngine;
import org.xowl.infra.store.owl.OWLRuleEngine;
import org.xowl.infra.store.owl.Translator;
import org.xowl.infra.store.rdf.*;
import org.xowl.infra.store.sparql.*;
import org.xowl.infra.store.storage.Store;
import org.xowl.infra.store.storage.StoreFactory;
import org.xowl.infra.store.storage.StoreTransaction;
import org.xowl.infra.store.writers.OWLSerializer;
import org.xowl.infra.store.writers.RDFSerializer;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.util.Collection;

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
     * The query engine for this repository
     */
    private OWLQueryEngine queryEngine;
    /**
     * The rule engine for this repository
     */
    private OWLRuleEngine ruleEngine;

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
    }

    /**
     * Runs a task as a transaction on this repository
     *
     * @param task     The task to run
     * @param writable Whether the transaction shall be writable
     * @param <T>      The type of the return value
     * @return The result of the callable
     * @throws ConcurrentWriteException When the task cannot be completed due to concurrency errors
     * @throws Exception                When the task fails for other reasons
     */
    public <T> T runAsTransaction(RepositoryTask<RepositoryRDF, T> task, boolean writable) throws Exception {
        return runAsTransaction(task, writable, DEFAULT_RETRY_COUNT, DEFAULT_WAIT_INTERVAL, DEFAULT_BACKOFF_INCREMENT);
    }

    /**
     * Runs a task as a transaction on this repository
     *
     * @param task                The task to run
     * @param writable            Whether the transaction shall be writable
     * @param retryCount          The number of time to retry the transaction in case of concurrency error
     * @param initialWaitInterval The initial time to wait between tries
     * @param backOffIncrement    The backing-off increment to the interval per tries
     * @param <T>                 The type of the return value
     * @return The result of the callable
     * @throws ConcurrentWriteException When the task cannot be completed due to concurrency errors
     * @throws Exception                When the task fails for other reasons
     */
    public <T> T runAsTransaction(RepositoryTask<RepositoryRDF, T> task, boolean writable, int retryCount, long initialWaitInterval, long backOffIncrement) throws Exception {
        int retries = retryCount;
        long waitInterval = initialWaitInterval - backOffIncrement;
        while (true) {
            try (StoreTransaction transaction = store.newTransaction(writable, false)) {
                T result = task.execute(this, transaction);
                transaction.commit();
                return result;
            } catch (ConcurrentWriteException exception) {
                // failed to commit
                if (retries <= 0)
                    throw exception;
            }
            retries--;
            waitInterval += backOffIncrement;
            // retry in a bit
            try {
                Thread.sleep(waitInterval);
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
        // requires to be within a transaction
        StoreTransaction transaction = store.getTransaction();
        SPARQLLoader loader = new SPARQLLoader(transaction.getDataset());
        Command command = loader.load(logger, new StringReader(sparql));
        if (command == null)
            return ResultFailure.INSTANCE;
        return command.execute(new EvalContext() {
            @Override
            public Evaluator getEvaluator() {
                return RepositoryRDF.this.executionManager;
            }

            @Override
            public Dataset getDataset() {
                return transaction.getDataset();
            }

            @Override
            public Solutions getSolutions(RDFPattern pattern) {
                Collection<RDFPatternSolution> results = RepositoryRDF.this.queryEngine.getBackend().execute(new RDFQuery(pattern));
                return new SolutionsMultiset(results);
            }

            @Override
            public String load(Logger logger, String resourceIRI, String ontologyIRI, boolean forceReload) throws IOException {
                return RepositoryRDF.this.load(logger, resourceIRI, ontologyIRI, forceReload);
            }
        });
    }

    /**
     * Gets the OWL query engine
     *
     * @return The OWL query engine
     */
    public synchronized OWLQueryEngine getOWLQueryEngine() {
        if (queryEngine == null)
            queryEngine = new OWLQueryEngine(store, executionManager);
        return queryEngine;
    }

    /**
     * Gets the RDF query engine
     *
     * @return The RDF query engine
     */
    public RDFQueryEngine getRDFQueryEngine() {
        return getOWLQueryEngine().getBackend();
    }

    /**
     * Gets the OWL rule engine
     *
     * @return The OWL rule engine
     */
    public synchronized OWLRuleEngine getOWLRuleEngine() {
        if (ruleEngine == null)
            ruleEngine = new OWLRuleEngine(store, store, executionManager);
        return ruleEngine;
    }

    /**
     * Gets the RDF rule engine
     *
     * @return The RDF rule engine
     */
    public RDFRuleEngine getRDFRuleEngine() {
        return getOWLRuleEngine().getBackend();
    }

    @Override
    public void close() throws Exception {
        this.store.close();
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
    protected String loadInput(Logger logger, Reader reader, String resourceIRI, String ontologyIRI, String syntax, Resource metadata) {
        StoreTransaction transaction = store.getTransaction();
        return this.loadInput(logger, reader, resourceIRI, ontologyIRI, syntax, metadata, transaction.getDataset()).ontology;
    }

    @Override
    protected void exportResource(Logger logger, Writer writer, String resourceIRI, String syntax) {
        StoreTransaction transaction = store.getTransaction();
        this.exportResource(logger, writer, resourceIRI, syntax, transaction.getDataset());
    }

    @Override
    protected void doLoadRDF(Logger logger, String ontology, RDFLoaderResult input, Dataset dataset) {
        dataset.insert(Changeset.fromAdded(input.getQuads()));

        if (!input.getRules().isEmpty()) {
            for (RDFRule rule : input.getRules()) {
                getRDFRuleEngine().add(rule);
            }
            getRDFRuleEngine().flush();
        }
    }

    @Override
    protected void doLoadOWL(Logger logger, String ontology, OWLLoaderResult input, Dataset dataset) {
        Translator translator = new Translator(null, dataset);
        Collection<Quad> quads = translator.translate(input);
        dataset.insert(Changeset.fromAdded(quads));

        if (!input.getRules().isEmpty()) {
            for (Rule rule : input.getRules()) {
                getOWLRuleEngine().add(rule, null, null, null);
            }
            getOWLRuleEngine().flush();
        }
    }

    @Override
    protected void doExportRDF(Logger logger, String ontology, RDFSerializer output, Dataset dataset) {
        output.serialize(logger, dataset.getAll(dataset.getIRINode(ontology)));
    }

    @Override
    protected void doExportRDF(Logger logger, RDFSerializer output, Dataset dataset) {
        output.serialize(logger, dataset.getAll());
    }

    @Override
    protected void exportResourceOWL(Logger logger, String ontology, OWLSerializer output, Dataset dataset) {
        throw new UnsupportedOperationException();
    }
}
