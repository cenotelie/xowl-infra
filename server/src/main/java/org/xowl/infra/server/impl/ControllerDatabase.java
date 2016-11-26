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

package org.xowl.infra.server.impl;

import org.xowl.hime.redist.ASTNode;
import org.xowl.infra.server.api.XOWLRule;
import org.xowl.infra.server.api.XOWLStoredProcedure;
import org.xowl.infra.server.api.XOWLStoredProcedureContext;
import org.xowl.infra.server.base.BaseRule;
import org.xowl.infra.server.base.BaseStoredProcedure;
import org.xowl.infra.server.base.BaseStoredProcedureContext;
import org.xowl.infra.store.*;
import org.xowl.infra.store.loaders.JSONLDLoader;
import org.xowl.infra.store.loaders.RDFLoaderResult;
import org.xowl.infra.store.loaders.RDFTLoader;
import org.xowl.infra.store.loaders.SPARQLLoader;
import org.xowl.infra.store.rdf.Changeset;
import org.xowl.infra.store.rdf.Quad;
import org.xowl.infra.store.rdf.RDFRule;
import org.xowl.infra.store.rdf.RDFRuleStatus;
import org.xowl.infra.store.sparql.Command;
import org.xowl.infra.store.sparql.Result;
import org.xowl.infra.store.sparql.ResultFailure;
import org.xowl.infra.store.storage.BaseStore;
import org.xowl.infra.store.storage.StoreFactory;
import org.xowl.infra.store.storage.UnsupportedNodeType;
import org.xowl.infra.utils.Files;
import org.xowl.infra.utils.SHA1;
import org.xowl.infra.utils.config.Configuration;
import org.xowl.infra.utils.logging.BufferedLogger;
import org.xowl.infra.utils.logging.Logger;
import org.xowl.infra.utils.metrics.Metric;
import org.xowl.infra.utils.metrics.MetricComposite;
import org.xowl.infra.utils.metrics.MetricSnapshot;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Implements a controller for a database.
 * The controller is responsible for the management of the persisted data, the associated rules and the stored procedures.
 *
 * @author Laurent Wouters
 */
public class ControllerDatabase implements Closeable {
    /**
     * The configuration file for a repository
     */
    private static final String REPO_CONF_NAME = "config.ini";
    /**
     * The name of the sub folder containing the rules
     */
    private static final String REPO_RULES = "rules";
    /**
     * The name of the sub folder containing the procedures
     */
    private static final String REPO_PROCEDURES = "procedures";
    /**
     * The namespace IRI for rules loaded in a database
     */
    private static final String RULES_RESOURCE = "http://xowl.org/infra/server/rules";
    /**
     * The configuration property for the maximum of concurrent threads
     */
    private static final String CONFIG_MAX_THREADS = "maxThreads";
    /**
     * The configuration property for the storage engine
     */
    private static final String CONFIG_STORAGE = "storage";
    /**
     * The configuration value for the storage engine specifying an in-memory store
     */
    private static final String CONFIG_STORAGE_MEMORY = "memory";
    /**
     * The configuration property for the entailment regime
     */
    private static final String CONFIG_ENTAILMENT = "entailment";
    /**
     * The configuration section for the rules
     */
    private static final String CONFIG_SECTION_RULES = "rules";
    /**
     * The configuration property that holds all the rules
     */
    private static final String CONFIG_ALL_RULES = "all";
    /**
     * The configuration property that holds the active rules
     */
    private static final String CONFIG_ACTIVE_RULES = "actives";
    /**
     * The configuration section for the procedures
     */
    private static final String CONFIG_SECTION_PROCEDURES = "procedures";
    /**
     * The configuration property that holds all the procedures
     */
    private static final String CONFIG_ALL_PROCEDURES = "all";


    /**
     * The proxy object that represents the database in the administration database
     */
    protected final ProxyObject proxy;
    /**
     * The database's name
     */
    private final String name;
    /**
     * The database's location
     */
    private final File location;
    /**
     * The associated repository
     */
    private final RepositoryRDF repository;
    /**
     * The current configuration for this database
     */
    private final Configuration configuration;
    /**
     * The cache of procedures for this database
     */
    private final Map<String, BaseStoredProcedure> procedures;
    /**
     * The maximum number of concurrent threads for this database
     */
    private final int maxThreads;
    /**
     * The current number of threads on this database
     */
    private final AtomicInteger currentThreads;
    /**
     * The composite metric for this database
     */
    protected final MetricComposite metricDB;

    /**
     * Gets the repository backing this database
     *
     * @return The repository
     */
    protected RepositoryRDF getRepository() {
        return repository;
    }

    /**
     * Initializes this database
     *
     * @param location         The database's location
     * @param defaultMaxThread The default maximum number of threads
     * @param proxy            The proxy object that represents the database in the administration database
     * @param name             The name of the database
     * @throws Exception When the location cannot be accessed
     */
    public ControllerDatabase(File location, int defaultMaxThread, ProxyObject proxy, String name) throws Exception {
        this.proxy = proxy;
        this.name = name != null ? name : (String) proxy.getDataValue(Schema.ADMIN_NAME);
        this.location = location;
        this.configuration = loadConfiguration(location);
        this.repository = createRepository(configuration, location);
        this.procedures = new HashMap<>();
        this.maxThreads = getMaxThreads(defaultMaxThread, configuration);
        this.currentThreads = new AtomicInteger(0);
        this.metricDB = new MetricComposite((MetricComposite) repository.getStore().getMetric(), "Database " + location.getAbsolutePath());
        initRepository();
    }

    /**
     * Initializes this administration database
     *
     * @param location         The database's location
     * @param defaultMaxThread The default maximum number of threads
     * @param adminDbName      The expected name if the administrative database
     * @throws Exception When the location cannot be accessed
     */
    public ControllerDatabase(File location, int defaultMaxThread, String adminDbName) throws Exception {
        this.name = adminDbName;
        this.location = location;
        this.configuration = loadConfiguration(location);
        this.repository = createRepository(configuration, location);
        this.procedures = new HashMap<>();
        this.maxThreads = getMaxThreads(defaultMaxThread, configuration);
        this.currentThreads = new AtomicInteger(0);
        this.metricDB = new MetricComposite((MetricComposite) repository.getStore().getMetric(), "Database " + location.getAbsolutePath());
        initRepository();
        this.proxy = repository.resolveProxy(Schema.ADMIN_GRAPH_DBS + adminDbName);
    }

    /**
     * Loads the configuration for this database
     *
     * @param location The database location
     * @return The configuration
     * @throws IOException When the location cannot be accessed
     */
    private static Configuration loadConfiguration(File location) throws IOException {
        if (!location.exists()) {
            if (!location.mkdirs()) {
                throw new IOException("Failed to create the directory for repository at " + location.getPath());
            }
        }
        Configuration configuration = new Configuration();
        File configFile = new File(location, REPO_CONF_NAME);
        if (configFile.exists()) {
            configuration.load(configFile.getAbsolutePath(), Files.CHARSET);
        }
        return configuration;
    }

    /**
     * Creates the repository for the database
     *
     * @param configuration The current configuration
     * @param location      The database location
     * @return The repository
     */
    private static RepositoryRDF createRepository(Configuration configuration, File location) {
        BaseStore store = Objects.equals(configuration.get(CONFIG_STORAGE), CONFIG_STORAGE_MEMORY) ?
                StoreFactory.create().inMemory().withReasoning().make() :
                StoreFactory.create().onDisk(location).withReasoning().make();
        return new RepositoryRDF(store, IRIMapper.getDefault(), Repository.getDefaultEvaluator());
    }

    /**
     * Gets the maximum number of concurrent threads for this database
     *
     * @param defaultMaxThread The default maximum number of threads
     * @return The maximum number of concurrent threads for this database
     */
    private static int getMaxThreads(int defaultMaxThread, Configuration configuration) {
        String property = configuration.get(CONFIG_MAX_THREADS);
        if (property == null)
            return defaultMaxThread;
        int value = Integer.parseInt(property);
        if (value <= 0)
            value = Integer.MAX_VALUE;
        return value;
    }

    /**
     * Initializes the repository from configuration
     */
    private void initRepository() throws Exception {
        String cRegime = configuration.get(CONFIG_ENTAILMENT);
        if (cRegime != null)
            repository.setEntailmentRegime(EntailmentRegime.valueOf(cRegime));
        for (String rule : configuration.getAll(CONFIG_SECTION_RULES, CONFIG_ACTIVE_RULES)) {
            doActivateRule(rule);
        }
    }


    /**
     * When a new threads is touching this database
     */
    private void onThreadEnter() {
        if (maxThreads <= 0)
            return;
        int tries = 0;
        while (true) {
            int number = currentThreads.get();
            if (number < maxThreads && currentThreads.compareAndSet(number, number + 1))
                return;
            tries++;
            if (tries > 50) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException exception) {
                    exception.printStackTrace();
                }
            }
        }
    }

    /**
     * When a thread is leaving this database
     */
    private void onThreadExit() {
        if (maxThreads <= 0)
            return;
        currentThreads.decrementAndGet();
    }

    /**
     * Gets the name of the database
     *
     * @return The name of the database
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the composite metric for this database
     *
     * @return The metric for this database
     */
    public Metric getMetric() {
        return metricDB;
    }

    /**
     * Gets a snapshot of the metrics for this database
     *
     * @return The snapshot
     */
    public MetricSnapshot getMetricSnapshot() {
        return repository.getStore().getMetricSnapshot(System.nanoTime());
    }

    /**
     * Executes a SPARQL command
     *
     * @param sparql      The SPARQL command(s)
     * @param defaultIRIs The context's default IRIs
     * @param namedIRIs   The context's named IRIs
     * @param isReadonly  Whether only reading is allowed for this command
     * @return The SPARQL result
     */
    public Result sparql(String sparql, List<String> defaultIRIs, List<String> namedIRIs, boolean isReadonly) {
        onThreadEnter();
        try {
            BufferedLogger bufferedLogger = new BufferedLogger();
            if (defaultIRIs == null)
                defaultIRIs = Collections.emptyList();
            if (namedIRIs == null)
                namedIRIs = Collections.emptyList();
            SPARQLLoader loader = new SPARQLLoader(repository.getStore(), defaultIRIs, namedIRIs);
            Command command = loader.load(bufferedLogger, new StringReader(sparql));
            if (command == null) {
                // ill-formed request
                bufferedLogger.error("Failed to parse and load the request");
                return new ResultFailure(bufferedLogger.getErrorsAsString());
            }
            if (command.isUpdateCommand() && isReadonly)
                return new ResultFailure("Database is read-only");
            Result result = command.execute(repository);
            if (command.isUpdateCommand())
                repository.getStore().commit();
            return result;
        } finally {
            onThreadExit();
        }
    }

    /**
     * Executes a SPARQL command
     *
     * @param sparql     The SPARQL command(s)
     * @param isReadonly Whether only reading is allowed for this command
     * @return The SPARQL result
     */
    public Result sparql(Command sparql, boolean isReadonly) {
        onThreadEnter();
        try {
            if (sparql == null) {
                // ill-formed request
                BufferedLogger bufferedLogger = new BufferedLogger();
                bufferedLogger.error("Failed to parse and load the request");
                return new ResultFailure(bufferedLogger.getErrorsAsString());
            }
            if (sparql.isUpdateCommand() && isReadonly)
                return new ResultFailure("Database is read-only");
            Result result = sparql.execute(repository);
            if (sparql.isUpdateCommand())
                repository.getStore().commit();
            return result;
        } finally {
            onThreadExit();
        }
    }

    /**
     * Gets the entailment regime
     *
     * @return The entailment regime
     */
    public EntailmentRegime getEntailmentRegime() {
        return repository.getEntailmentRegime();
    }

    /**
     * Sets the entailment regime
     *
     * @param regime The entailment regime
     * @throws IllegalArgumentException When the specified entailment regime cannot be set
     * @throws IOException              When an IO operation fails
     */
    public void setEntailmentRegime(EntailmentRegime regime) throws Exception {
        onThreadEnter();
        try {
            // try to set the entailment regime
            repository.setEntailmentRegime(regime);
            repository.getStore().commit();
            // save the configuration to disk
            synchronized (configuration) {
                configuration.set(CONFIG_ENTAILMENT, regime.toString());
                configuration.save((new File(location, REPO_CONF_NAME)).getAbsolutePath(), Files.CHARSET);
            }
        } finally {
            onThreadExit();
        }
    }

    /**
     * Sets the entailment regime
     *
     * @param regime The entailment regime
     * @throws IllegalArgumentException When the specified entailment regime cannot be set
     * @throws IOException              When an IO operation fails
     */
    public void setEntailmentRegime(String regime) throws Exception {
        setEntailmentRegime(EntailmentRegime.valueOf(regime));
    }

    /**
     * Gets the rule for the specified name
     *
     * @param name The name (IRI) of a rule
     * @return The rule, or null if there is none
     * @throws IOException When the rule cannot be retrieved
     */
    public XOWLRule getRule(String name) throws IOException {
        Collection<String> names;
        Collection<String> actives;
        synchronized (configuration) {
            names = configuration.getAll(CONFIG_SECTION_RULES, CONFIG_ALL_RULES);
            actives = configuration.getAll(CONFIG_SECTION_RULES, CONFIG_ACTIVE_RULES);
        }
        if (!names.contains(name))
            return null;
        File folder = new File(location, REPO_RULES);
        File file = new File(folder, SHA1.hashSHA1(name));
        try (FileInputStream stream = new FileInputStream(file)) {
            String definition = Files.read(stream, Files.CHARSET);
            return new BaseRule(name, definition, actives.contains(name));
        }
    }

    /**
     * Gets the rules in this database
     *
     * @return The rules in this database
     * @throws IOException When the rule cannot be retrieved
     */
    public Collection<XOWLRule> getRules() throws IOException {
        Collection<String> names;
        Collection<String> actives;
        synchronized (configuration) {
            names = configuration.getAll(CONFIG_SECTION_RULES, CONFIG_ALL_RULES);
            actives = configuration.getAll(CONFIG_SECTION_RULES, CONFIG_ACTIVE_RULES);
        }
        Collection<XOWLRule> rules = new ArrayList<>(names.size());
        for (String name : names) {
            File folder = new File(location, REPO_RULES);
            File file = new File(folder, SHA1.hashSHA1(name));
            try (FileInputStream stream = new FileInputStream(file)) {
                String definition = Files.read(stream, Files.CHARSET);
                rules.add(new BaseRule(name, definition, actives.contains(name)));
            }
        }
        return rules;
    }

    /**
     * Adds a new rule to this database
     *
     * @param content  The rule's content
     * @param activate Whether to readily activate the rule
     * @return The added rule
     * @throws IOException              When the rule cannot be written
     * @throws IllegalArgumentException When the rule definition is not valid
     */
    public XOWLRule addRule(String content, boolean activate) throws IOException, IllegalArgumentException {
        onThreadEnter();
        try {
            File folder = new File(location, REPO_RULES);
            if (!folder.exists()) {
                if (!folder.mkdirs())
                    throw new IOException("Failed to create directory for storing the rules");
            }

            BufferedLogger bufferedLogger = new BufferedLogger();
            RDFTLoader loader = new RDFTLoader(repository.getStore());
            RDFLoaderResult result = loader.loadRDF(bufferedLogger, new StringReader(content), RULES_RESOURCE, null);
            if (result == null) {
                // ill-formed request
                throw new IllegalArgumentException("Malformed rule definition: " + bufferedLogger.getErrorsAsString());
            }
            if (result.getRules().size() != 1)
                throw new IllegalArgumentException("Malformed rule definition: Expected one rule");

            RDFRule rule = result.getRules().get(0);
            String name = SHA1.hashSHA1(rule.getIRI());
            File file = new File(folder, name);
            try (FileOutputStream stream = new FileOutputStream(file)) {
                stream.write(content.getBytes(Files.CHARSET));
                stream.flush();
            }

            synchronized (configuration) {
                if (configuration.hasValue(CONFIG_SECTION_RULES, CONFIG_ALL_RULES, rule.getIRI()))
                    throw new IllegalArgumentException("A rule with this IRI already exists");
                configuration.add(CONFIG_SECTION_RULES, CONFIG_ALL_RULES, rule.getIRI());
                if (activate)
                    configuration.add(CONFIG_SECTION_RULES, CONFIG_ACTIVE_RULES, rule.getIRI());
                configuration.save((new File(location, REPO_CONF_NAME)).getAbsolutePath(), Files.CHARSET);
            }
            if (activate) {
                repository.getRDFRuleEngine().add(rule);
                repository.getRDFRuleEngine().flush();
            }
            return new BaseRule(rule.getIRI(), content, activate);
        } finally {
            onThreadExit();
        }
    }

    /**
     * Removes a rule from this database
     *
     * @param iri The name of the rule to remove
     * @throws IOException              When the rule definition cannot be removed
     * @throws IllegalArgumentException When the rule is not in this database
     */
    public void removeRule(String iri) throws IOException, IllegalArgumentException {
        onThreadEnter();
        try {
            boolean removeFromEngine = false;
            synchronized (configuration) {
                if (!configuration.hasValue(CONFIG_SECTION_RULES, CONFIG_ALL_RULES, iri))
                    throw new IllegalArgumentException("Rule does not exist: " + iri);
                if (configuration.hasValue(CONFIG_SECTION_RULES, CONFIG_ACTIVE_RULES, iri)) {
                    configuration.remove(CONFIG_SECTION_RULES, CONFIG_ACTIVE_RULES, iri);
                    removeFromEngine = true;
                }
                configuration.remove(CONFIG_SECTION_RULES, CONFIG_ALL_RULES, iri);
                configuration.save((new File(location, REPO_CONF_NAME)).getAbsolutePath(), Files.CHARSET);
            }
            File folder = new File(location, REPO_RULES);
            File file = new File(folder, SHA1.hashSHA1(iri));
            if (!file.delete())
                throw new IOException("Failed to delete " + file.getAbsolutePath());
            if (removeFromEngine)
                repository.getRDFRuleEngine().remove(iri);
        } finally {
            onThreadExit();
        }
    }

    /**
     * Activates an existing rule in this database
     *
     * @param iri The name of the rule to activate
     * @throws IOException              When the rule definition cannot be read
     * @throws IllegalArgumentException When the rule is not in this database
     */
    public void activateRule(String iri) throws IOException, IllegalArgumentException {
        onThreadEnter();
        try {
            synchronized (configuration) {
                if (!configuration.hasValue(CONFIG_SECTION_RULES, CONFIG_ALL_RULES, iri))
                    throw new IllegalArgumentException("Rule does not exist: " + iri);
                if (configuration.hasValue(CONFIG_SECTION_RULES, CONFIG_ACTIVE_RULES, iri))
                    throw new IllegalArgumentException("Rule is already active: " + iri);
                configuration.add(CONFIG_SECTION_RULES, CONFIG_ACTIVE_RULES, iri);
                configuration.save((new File(location, REPO_CONF_NAME)).getAbsolutePath(), Files.CHARSET);
            }
            doActivateRule(iri);
        } finally {
            onThreadExit();
        }
    }

    /**
     * Activates a rule in this database
     *
     * @param iri The IRI of a rule
     * @throws IOException When the rule definition cannot be read
     */
    private void doActivateRule(String iri) throws IOException {
        File folder = new File(location, REPO_RULES);
        File file = new File(folder, SHA1.hashSHA1(iri));
        BufferedLogger logger = new BufferedLogger();
        try (FileInputStream stream = new FileInputStream(file)) {
            RDFTLoader loader = new RDFTLoader(repository.getStore());
            RDFLoaderResult result = loader.loadRDF(logger, new InputStreamReader(stream, Files.CHARSET), RULES_RESOURCE, null);
            if (result == null || !logger.getErrorMessages().isEmpty())
                throw new IOException("Failed to read rule " + iri + ": " + logger.getErrorsAsString());
            RDFRule rule = result.getRules().get(0);
            repository.getRDFRuleEngine().add(rule);
            repository.getRDFRuleEngine().flush();
        }
    }

    /**
     * Deactivates an existing rule in this database
     *
     * @param iri The name of the rule to deactivate
     * @throws IOException              When the configuration cannot be written
     * @throws IllegalArgumentException When the rule is not in this database
     */
    public void deactivateRule(String iri) throws IOException, IllegalArgumentException {
        onThreadEnter();
        try {
            synchronized (configuration) {
                if (!configuration.hasValue(CONFIG_SECTION_RULES, CONFIG_ALL_RULES, iri))
                    throw new IllegalArgumentException("Rule does not exist: " + iri);
                if (!configuration.hasValue(CONFIG_SECTION_RULES, CONFIG_ACTIVE_RULES, iri))
                    throw new IllegalArgumentException("Rule is not active: " + iri);
                configuration.remove(CONFIG_SECTION_RULES, CONFIG_ACTIVE_RULES, iri);
                configuration.save((new File(location, REPO_CONF_NAME)).getAbsolutePath(), Files.CHARSET);
            }
            repository.getRDFRuleEngine().remove(iri);
        } finally {
            onThreadExit();
        }
    }

    /**
     * Gets the matching status of a rule in this database
     *
     * @param iri The name of the rule to inquire
     * @return The status of the rule, or null if the rule is not active
     * @throws IllegalArgumentException When the rule is not in this database
     */
    public RDFRuleStatus getRuleStatus(String iri) throws IllegalArgumentException {
        synchronized (configuration) {
            if (!configuration.hasValue(CONFIG_SECTION_RULES, CONFIG_ALL_RULES, iri))
                throw new IllegalArgumentException("Rule does not exist: " + iri);
            if (!configuration.hasValue(CONFIG_SECTION_RULES, CONFIG_ACTIVE_RULES, iri))
                return null;
        }
        return repository.getRDFRuleEngine().getMatchStatus(iri);
    }

    /**
     * Gets the stored procedure for the specified name (iri)
     *
     * @param iri The name (iri) of a stored procedure
     * @return The procedure, or null if it does not exist
     * @throws IOException When the procedure definition cannot be read
     */
    public XOWLStoredProcedure getStoredProcedure(String iri) throws IOException {
        synchronized (configuration) {
            if (!configuration.hasValue(CONFIG_SECTION_PROCEDURES, CONFIG_ALL_PROCEDURES, iri))
                return null;
        }
        synchronized (procedures) {
            BaseStoredProcedure procedure = procedures.get(iri);
            if (procedure == null)
                procedure = cacheProcedure(iri);
            return procedure;
        }
    }

    /**
     * Caches the existing procedure with the specified name
     *
     * @param name The name of a procedure
     * @return The procedure
     * @throws IOException When the procedure definition cannot be read
     */
    private BaseStoredProcedure cacheProcedure(String name) throws IOException {
        File folder = new File(location, REPO_PROCEDURES);
        if (!folder.exists()) {
            if (!folder.mkdirs())
                throw new IOException("Failed to create directory for storing the procedures");
        }
        BufferedLogger logger = new BufferedLogger();
        File file = new File(folder, SHA1.hashSHA1(name));
        try (FileInputStream stream = new FileInputStream(file)) {
            String definition = Files.read(stream, Files.CHARSET);
            ASTNode root = JSONLDLoader.parseJSON(logger, definition);
            if (root == null || !logger.getErrorMessages().isEmpty())
                throw new IOException("Failed to read procedure " + name + ": " + logger.getErrorsAsString());
            BaseStoredProcedure procedure = new BaseStoredProcedure(root, repository.getStore(), logger);
            procedures.put(name, procedure);
            return procedure;
        }
    }

    /**
     * Gets the stored procedures for this database
     *
     * @return The stored procedures for this database
     * @throws IOException When the procedure definition cannot be read
     */
    public Collection<XOWLStoredProcedure> getStoredProcedures() throws IOException {
        Collection<String> names;
        synchronized (configuration) {
            names = configuration.getAll(CONFIG_SECTION_PROCEDURES, CONFIG_ALL_PROCEDURES);
        }
        synchronized (procedures) {
            for (String name : names) {
                BaseStoredProcedure procedure = procedures.get(name);
                if (procedure == null)
                    cacheProcedure(name);
            }
            return new ArrayList<XOWLStoredProcedure>(procedures.values());
        }
    }

    /**
     * Adds a stored procedure in the form of a SPARQL command
     *
     * @param iri        The name (iri) for this procedure
     * @param sparql     The SPARQL command(s)
     * @param parameters The names of the parameters for this procedure
     * @return The stored procedure
     * @throws IOException              When the procedure definition cannot be written
     * @throws IllegalArgumentException When the procedure definition is malformed
     */
    public XOWLStoredProcedure addStoredProcedure(String iri, String sparql, Collection<String> parameters) throws IOException, IllegalArgumentException {
        synchronized (configuration) {
            if (configuration.hasValue(CONFIG_SECTION_PROCEDURES, CONFIG_ALL_PROCEDURES, iri))
                throw new IllegalArgumentException("Procedure already exists: " + iri);
            configuration.add(CONFIG_SECTION_PROCEDURES, CONFIG_ALL_PROCEDURES, iri);
            configuration.save((new File(location, REPO_CONF_NAME)).getAbsolutePath(), Files.CHARSET);
        }

        File folder = new File(location, REPO_PROCEDURES);
        if (!folder.exists()) {
            if (!folder.mkdirs())
                throw new IOException("Failed to create directory for storing the procedures");
        }

        SPARQLLoader loader = new SPARQLLoader(repository.getStore());
        BufferedLogger bufferedLogger = new BufferedLogger();
        Command command = loader.load(bufferedLogger, new StringReader(sparql));
        if (!bufferedLogger.getErrorMessages().isEmpty())
            throw new IllegalArgumentException("Malformed procedure definition: " + bufferedLogger.getErrorsAsString());
        BaseStoredProcedure procedure = new BaseStoredProcedure(iri, sparql, parameters, command);

        String name = SHA1.hashSHA1(iri);
        File file = new File(folder, name);
        try (FileOutputStream stream = new FileOutputStream(file)) {
            stream.write(procedure.serializedJSON().getBytes(Files.CHARSET));
            stream.flush();
        }

        synchronized (procedures) {
            procedures.put(iri, procedure);
            return procedure;
        }
    }

    /**
     * Remove a stored procedure
     *
     * @param iri The name (iri) of the procedure to remove
     * @throws IOException              When the procedure definition cannot be read
     * @throws IllegalArgumentException When the procedure does not exist
     */
    public void removeStoredProcedure(String iri) throws IOException, IllegalArgumentException {
        synchronized (configuration) {
            if (!configuration.hasValue(CONFIG_SECTION_PROCEDURES, CONFIG_ALL_PROCEDURES, iri))
                throw new IllegalArgumentException("Procedure does not exist: " + iri);
            configuration.remove(CONFIG_SECTION_PROCEDURES, CONFIG_ALL_PROCEDURES, iri);
            configuration.save((new File(location, REPO_CONF_NAME)).getAbsolutePath(), Files.CHARSET);
        }

        File folder = new File(location, REPO_PROCEDURES);
        if (folder.exists()) {
            File file = new File(folder, SHA1.hashSHA1(iri));
            if (!file.delete())
                throw new IOException("Failed to delete " + file.getAbsolutePath());
        }

        synchronized (procedures) {
            procedures.remove(iri);
        }
    }

    /**
     * Executes a stored procedure
     *
     * @param iri               The name (iri) of the procedure to execute
     * @param contextDefinition The execution context to use
     * @param isReadonly        Whether only reading is allowed for this command
     * @return The result of the procedure
     * @throws IOException              When the procedure definition cannot be read
     * @throws IllegalArgumentException When the procedure does not exist
     */
    public Result executeStoredProcedure(String iri, String contextDefinition, boolean isReadonly) throws IOException, IllegalArgumentException {
        BufferedLogger bufferedLogger = new BufferedLogger();
        ASTNode root = JSONLDLoader.parseJSON(bufferedLogger, contextDefinition);
        if (!bufferedLogger.getErrorMessages().isEmpty())
            throw new IllegalArgumentException(bufferedLogger.getErrorsAsString());
        BaseStoredProcedureContext context = new BaseStoredProcedureContext(root, repository.getStore());
        return executeStoredProcedure(iri, context, isReadonly);
    }

    /**
     * Executes a stored procedure
     *
     * @param iri        The name (iri) of the procedure to execute
     * @param context    The execution context to use
     * @param isReadonly Whether only reading is allowed for this command
     * @return The result of the procedure
     * @throws IOException              When the procedure definition cannot be read
     * @throws IllegalArgumentException When the procedure does not exist
     */
    public Result executeStoredProcedure(String iri, XOWLStoredProcedureContext context, boolean isReadonly) throws IOException, IllegalArgumentException {
        synchronized (configuration) {
            if (!configuration.hasValue(CONFIG_SECTION_PROCEDURES, CONFIG_ALL_PROCEDURES, iri))
                throw new IllegalArgumentException("Procedure does not exist: " + iri);
        }

        BaseStoredProcedure procedure;
        synchronized (procedures) {
            procedure = procedures.get(iri);
            if (procedure == null) {
                procedure = cacheProcedure(iri);
            }
        }

        for (String parameter : procedure.getParameters()) {
            if (context.getParameters().get(parameter) == null)
                throw new IllegalArgumentException("Missing required parameter: " + parameter);
        }
        Command sparql = procedure.getSPARQL().clone(context.getParameters());
        return sparql(sparql, isReadonly);
    }

    /**
     * Uploads some content to this database
     *
     * @param logger  The logger to use
     * @param syntax  The content's syntax
     * @param content The content
     * @throws Exception When the upload fails
     */
    public void upload(Logger logger, String syntax, String content) throws Exception {
        onThreadEnter();
        try {
            repository.load(logger, new StringReader(content), IRIs.GRAPH_DEFAULT, IRIs.GRAPH_DEFAULT, syntax);
            repository.getStore().commit();
        } finally {
            onThreadExit();
        }
    }

    /**
     * Uploads quads to this database
     *
     * @param quads The quads to upload
     * @throws Exception When the upload fails
     */
    public void upload(Collection<Quad> quads) throws Exception {
        onThreadEnter();
        try {
            repository.getStore().insert(Changeset.fromAdded(quads));
            repository.getStore().commit();
        } catch (UnsupportedNodeType exception) {
            repository.getStore().rollback();
            throw exception;
        } finally {
            onThreadExit();
        }
    }

    @Override
    public void close() throws IOException {
        try {
            repository.getStore().close();
        } catch (IOException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new IOException(exception);
        }
    }
}
