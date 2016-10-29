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
import org.xowl.infra.server.base.BaseDatabase;
import org.xowl.infra.server.base.BaseRule;
import org.xowl.infra.server.base.BaseStoredProcedure;
import org.xowl.infra.server.base.BaseStoredProcedureContext;
import org.xowl.infra.server.standalone.Program;
import org.xowl.infra.server.xsp.*;
import org.xowl.infra.store.*;
import org.xowl.infra.store.Serializable;
import org.xowl.infra.store.loaders.JSONLDLoader;
import org.xowl.infra.store.loaders.RDFLoaderResult;
import org.xowl.infra.store.loaders.RDFTLoader;
import org.xowl.infra.store.loaders.SPARQLLoader;
import org.xowl.infra.store.rdf.Changeset;
import org.xowl.infra.store.rdf.Quad;
import org.xowl.infra.store.rdf.RDFRule;
import org.xowl.infra.store.rdf.RDFRuleStatus;
import org.xowl.infra.store.sparql.Command;
import org.xowl.infra.store.storage.BaseStore;
import org.xowl.infra.store.storage.StoreFactory;
import org.xowl.infra.store.storage.UnsupportedNodeType;
import org.xowl.infra.utils.Files;
import org.xowl.infra.utils.SHA1;
import org.xowl.infra.utils.config.Configuration;
import org.xowl.infra.utils.logging.BufferedLogger;
import org.xowl.infra.utils.logging.ConsoleLogger;
import org.xowl.infra.utils.logging.DispatchLogger;
import org.xowl.infra.utils.logging.Logger;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Represents a database hosted on this server
 *
 * @author Laurent Wouters
 */
public class ServerDatabase extends BaseDatabase implements Serializable, Closeable {
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
    private static final String RULES_RESOURCE = "http://xowl.org/server/rules";
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
     * The database's location
     */
    private final File location;
    /**
     * The logger
     */
    private final Logger logger;
    /**
     * The repository
     */
    private final RepositoryRDF repository;
    /**
     * The current configuration for this database
     */
    private final Configuration configuration;
    /**
     * The proxy object representing this database
     */
    private final ProxyObject proxy;
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
     * Gets the repository backing this database
     *
     * @return The repository backing this database
     */
    public RepositoryRDF getRepository() {
        return repository;
    }

    /**
     * Gets the proxy object representing this database
     *
     * @return The proxy object representing this database
     */
    public ProxyObject getProxy() {
        return proxy;
    }

    /**
     * Initializes this database (as the admin database)
     *
     * @param confServer The server configuration
     * @param location   The database's location
     * @throws IOException When the location cannot be accessed
     */
    public ServerDatabase(ServerConfiguration confServer, File location) throws IOException {
        super(confServer.getAdminDBName());
        this.location = location;
        this.logger = new ConsoleLogger();
        this.configuration = loadConfiguration(location);
        this.repository = createRepository(configuration, location);
        this.proxy = repository.resolveProxy(Schema.ADMIN_GRAPH_DBS + confServer.getAdminDBName());
        this.procedures = new HashMap<>();
        this.maxThreads = getMaxThreads(confServer, configuration);
        this.currentThreads = new AtomicInteger(0);
        initRepository();
    }

    /**
     * Initializes this database
     *
     * @param confServer The server configuration
     * @param location   The database's location
     * @param proxy      The proxy object representing this database
     * @throws IOException When the location cannot be accessed
     */
    public ServerDatabase(ServerConfiguration confServer, File location, ProxyObject proxy) throws IOException {
        super((String) proxy.getDataValue(Schema.ADMIN_NAME));
        this.location = location;
        this.logger = new ConsoleLogger();
        this.configuration = loadConfiguration(location);
        this.repository = createRepository(configuration, location);
        this.proxy = proxy;
        this.procedures = new HashMap<>();
        this.maxThreads = getMaxThreads(confServer, configuration);
        this.currentThreads = new AtomicInteger(0);
        initRepository();
    }

    /**
     * Loads the configuration for this database
     *
     * @param location The database location
     * @return The configuration
     * @throws IOException When the location cannot be accessed
     */
    private Configuration loadConfiguration(File location) throws IOException {
        if (!location.exists()) {
            if (!location.mkdirs()) {
                throw error(logger, "Failed to create the directory for repository at " + location.getPath());
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
    private RepositoryRDF createRepository(Configuration configuration, File location) {
        BaseStore store = Objects.equals(configuration.get(CONFIG_STORAGE), CONFIG_STORAGE_MEMORY) ?
                StoreFactory.create().inMemory().withReasoning().make() :
                StoreFactory.create().onDisk(location).withReasoning().make();
        return new RepositoryRDF(store);
    }

    /**
     * Gets the maximum number of concurrent threads for this database
     *
     * @param configuration The current configuration
     * @return The maximum number of concurrent threads for this database
     */
    private int getMaxThreads(ServerConfiguration confServer, Configuration configuration) {
        String property = configuration.get(CONFIG_MAX_THREADS);
        if (property == null)
            return confServer.getDefaultMaxThreads();
        int value = Integer.parseInt(property);
        if (value <= 0)
            value = Integer.MAX_VALUE;
        return value;
    }

    /**
     * Initializes the repository from configuration
     */
    private void initRepository() {
        String cRegime = configuration.get(CONFIG_ENTAILMENT);
        if (cRegime != null)
            repository.setEntailmentRegime(EntailmentRegime.valueOf(cRegime));
        for (String rule : configuration.getAll(CONFIG_SECTION_RULES, CONFIG_ACTIVE_RULES)) {
            doActivateRule(rule);
        }
    }

    /**
     * Prepares a new exception to be thrown
     *
     * @param logger  The current logger
     * @param message The message for the exception
     * @return The exception
     */
    private static IOException error(Logger logger, String message) {
        IOException result = new IOException(message);
        logger.error(result);
        return result;
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

    @Override
    public XSPReply sparql(String sparql, List<String> defaultIRIs, List<String> namedIRIs) {
        onThreadEnter();
        try {
            BufferedLogger bufferedLogger = new BufferedLogger();
            DispatchLogger dispatchLogger = new DispatchLogger(logger, bufferedLogger);
            if (defaultIRIs == null)
                defaultIRIs = Collections.emptyList();
            if (namedIRIs == null)
                namedIRIs = Collections.emptyList();
            SPARQLLoader loader = new SPARQLLoader(repository.getStore(), defaultIRIs, namedIRIs);
            Command command = loader.load(dispatchLogger, new StringReader(sparql));
            if (command == null) {
                // ill-formed request
                dispatchLogger.error("Failed to parse and load the request");
                return new XSPReplyFailure(bufferedLogger.getErrorsAsString());
            }
            return new XSPReplyResult<>(command.execute(repository));
        } finally {
            onThreadExit();
        }
    }

    @Override
    public XSPReply sparql(Command sparql) {
        onThreadEnter();
        try {
            if (sparql == null) {
                // ill-formed request
                BufferedLogger bufferedLogger = new BufferedLogger();
                DispatchLogger dispatchLogger = new DispatchLogger(logger, bufferedLogger);
                dispatchLogger.error("Failed to parse and load the request");
                return new XSPReplyFailure(bufferedLogger.getErrorsAsString());
            }
            return new XSPReplyResult<>(sparql.execute(repository));
        } finally {
            onThreadExit();
        }
    }

    @Override
    public XSPReply getEntailmentRegime() {
        return new XSPReplyResult<>(repository.getEntailmentRegime());
    }

    @Override
    public XSPReply setEntailmentRegime(EntailmentRegime regime) {
        onThreadEnter();
        try {
            repository.setEntailmentRegime(regime);
            configuration.set(CONFIG_ENTAILMENT, regime.toString());
            try {
                configuration.save((new File(location, REPO_CONF_NAME)).getAbsolutePath(), Files.CHARSET);
            } catch (IOException exception) {
                logger.error(exception);
                return new XSPReplyFailure(exception.getMessage());
            }
            repository.getStore().commit();
            return XSPReplySuccess.instance();
        } finally {
            onThreadExit();
        }
    }

    public XSPReply setEntailmentRegime(String regime) {
        onThreadEnter();
        try {
            EntailmentRegime value = EntailmentRegime.valueOf(regime);
            if (value == null)
                return new XSPReplyFailure("Unexpected regime name");
            return setEntailmentRegime(value);
        } finally {
            onThreadExit();
        }
    }

    @Override
    public XSPReply getRule(String name) {
        Collection<String> names = configuration.getAll(CONFIG_SECTION_RULES, CONFIG_ALL_RULES);
        Collection<String> actives = configuration.getAll(CONFIG_SECTION_RULES, CONFIG_ACTIVE_RULES);
        if (!names.contains(name))
            return new XSPReplyFailure("Rule does not exist");
        File folder = new File(location, REPO_RULES);
        File file = new File(folder, SHA1.hashSHA1(name));
        try (FileInputStream stream = new FileInputStream(file)) {
            byte[] content = Program.load(stream);
            String definition = new String(content, Files.CHARSET);
            return new XSPReplyResult<>(new BaseRule(name, definition, actives.contains(name)));
        } catch (IOException exception) {
            logger.error(exception);
            return new XSPReplyFailure(exception.getMessage());
        }
    }

    @Override
    public XSPReply getRules() {
        Collection<String> names = configuration.getAll(CONFIG_SECTION_RULES, CONFIG_ALL_RULES);
        Collection<String> actives = configuration.getAll(CONFIG_SECTION_RULES, CONFIG_ACTIVE_RULES);
        Collection<BaseRule> rules = new ArrayList<>(names.size());
        for (String name : names) {
            File folder = new File(location, REPO_RULES);
            File file = new File(folder, SHA1.hashSHA1(name));
            try (FileInputStream stream = new FileInputStream(file)) {
                byte[] content = Program.load(stream);
                String definition = new String(content, Files.CHARSET);
                rules.add(new BaseRule(name, definition, actives.contains(name)));
            } catch (IOException exception) {
                logger.error(exception);
                return new XSPReplyFailure(exception.getMessage());
            }
        }
        return new XSPReplyResultCollection<>(rules);
    }

    @Override
    public XSPReply addRule(String content, boolean activate) {
        onThreadEnter();
        try {
            File folder = new File(location, REPO_RULES);
            if (!folder.exists()) {
                if (!folder.mkdirs())
                    return XSPReplyFailure.instance();
            }

            BufferedLogger bufferedLogger = new BufferedLogger();
            DispatchLogger dispatchLogger = new DispatchLogger(logger, bufferedLogger);
            RDFTLoader loader = new RDFTLoader(repository.getStore());
            RDFLoaderResult result = loader.loadRDF(dispatchLogger, new StringReader(content), RULES_RESOURCE, null);
            if (result == null) {
                // ill-formed request
                dispatchLogger.error("Failed to parse and load the rules");
                return new XSPReplyFailure(bufferedLogger.getErrorsAsString());
            }
            if (result.getRules().size() != 1)
                return new XSPReplyFailure("Expected one rule");

            RDFRule rule = result.getRules().get(0);
            String name = SHA1.hashSHA1(rule.getIRI());
            File file = new File(folder, name);
            try (FileOutputStream stream = new FileOutputStream(file)) {
                stream.write(content.getBytes(Files.CHARSET));
                stream.flush();
            } catch (IOException exception) {
                logger.error(exception);
                return new XSPReplyFailure(exception.getMessage());
            }

            configuration.add(CONFIG_SECTION_RULES, CONFIG_ALL_RULES, rule.getIRI());
            if (activate) {
                repository.getRDFRuleEngine().add(rule);
                repository.getRDFRuleEngine().flush();
                configuration.add(CONFIG_SECTION_RULES, CONFIG_ACTIVE_RULES, rule.getIRI());
            }
            try {
                configuration.save((new File(location, REPO_CONF_NAME)).getAbsolutePath(), Files.CHARSET);
            } catch (IOException exception) {
                logger.error(exception);
            }
            return new XSPReplyResult<>(new BaseRule(rule.getIRI(), content, activate));
        } finally {
            onThreadExit();
        }
    }

    @Override
    public XSPReply removeRule(XOWLRule rule) {
        return removeRule(rule.getName());
    }

    public XSPReply removeRule(String iri) {
        onThreadEnter();
        try {
            if (!configuration.getAll(CONFIG_SECTION_RULES, CONFIG_ALL_RULES).contains(iri))
                return new XSPReplyFailure("Rule does not exist");
            configuration.getAll(CONFIG_SECTION_RULES, CONFIG_ALL_RULES).remove(iri);
            if (configuration.getAll(CONFIG_SECTION_RULES, CONFIG_ACTIVE_RULES).contains(iri)) {
                configuration.remove(CONFIG_SECTION_RULES, CONFIG_ACTIVE_RULES, iri);
                repository.getRDFRuleEngine().remove(iri);
            }
            try {
                configuration.save((new File(location, REPO_CONF_NAME)).getAbsolutePath(), Files.CHARSET);
            } catch (IOException exception) {
                logger.error(exception);
            }

            File folder = new File(location, REPO_RULES);
            File file = new File(folder, SHA1.hashSHA1(iri));
            if (!file.delete())
                logger.error("Failed to delete " + file.getAbsolutePath());
            return XSPReplySuccess.instance();
        } finally {
            onThreadExit();
        }
    }

    @Override
    public XSPReply activateRule(XOWLRule rule) {
        return activateRule(rule.getName());
    }

    public XSPReply activateRule(String iri) {
        onThreadEnter();
        try {
            if (!configuration.getAll(CONFIG_SECTION_RULES, CONFIG_ALL_RULES).contains(iri))
                return new XSPReplyFailure("Rule does not exist");
            if (configuration.getAll(CONFIG_SECTION_RULES, CONFIG_ACTIVE_RULES).contains(iri))
                return new XSPReplyFailure("Already active");

            if (!doActivateRule(iri)) {
                return new XSPReplyFailure("Failed to activate the rule");
            }
            try {
                configuration.add(CONFIG_SECTION_RULES, CONFIG_ACTIVE_RULES, iri);
                configuration.save((new File(location, REPO_CONF_NAME)).getAbsolutePath(), Files.CHARSET);
            } catch (IOException exception) {
                logger.error(exception);
            }
            return XSPReplySuccess.instance();
        } finally {
            onThreadExit();
        }
    }

    /**
     * Activates a rule in this database
     *
     * @param iri The IRI of a rule
     * @return Whether the operation succeeded
     */
    private boolean doActivateRule(String iri) {
        File folder = new File(location, REPO_RULES);
        File file = new File(folder, SHA1.hashSHA1(iri));
        RDFRule rule;
        try (FileInputStream stream = new FileInputStream(file)) {
            RDFTLoader loader = new RDFTLoader(repository.getStore());
            RDFLoaderResult result = loader.loadRDF(logger, new InputStreamReader(stream, Files.CHARSET), RULES_RESOURCE, null);
            rule = result.getRules().get(0);
            repository.getRDFRuleEngine().add(rule);
            repository.getRDFRuleEngine().flush();
            return true;
        } catch (IOException exception) {
            logger.error(exception);
            return false;
        }
    }

    @Override
    public XSPReply deactivateRule(XOWLRule rule) {
        return deactivateRule(rule.getName());
    }

    public XSPReply deactivateRule(String iri) {
        onThreadEnter();
        try {
            if (!configuration.getAll(CONFIG_SECTION_RULES, CONFIG_ALL_RULES).contains(iri))
                return new XSPReplyFailure("Rule does not exist");
            if (!configuration.getAll(CONFIG_SECTION_RULES, CONFIG_ACTIVE_RULES).contains(iri))
                return new XSPReplyFailure("Not active");

            try {
                configuration.remove(CONFIG_SECTION_RULES, CONFIG_ACTIVE_RULES, iri);
                configuration.save((new File(location, REPO_CONF_NAME)).getAbsolutePath(), Files.CHARSET);
            } catch (IOException exception) {
                logger.error(exception);
            }
            repository.getRDFRuleEngine().remove(iri);
            return XSPReplySuccess.instance();
        } finally {
            onThreadExit();
        }
    }

    @Override
    public XSPReply getRuleStatus(XOWLRule rule) {
        return getRuleStatus(rule.getName());
    }

    public XSPReply getRuleStatus(String iri) {
        if (!configuration.getAll(CONFIG_SECTION_RULES, CONFIG_ACTIVE_RULES).contains(iri))
            return new XSPReplyFailure("Not active");
        RDFRuleStatus result = repository.getRDFRuleEngine().getMatchStatus(iri);
        if (result == null)
            return XSPReplyFailure.instance();
        return new XSPReplyResult<>(result);
    }

    @Override
    public XSPReply getStoreProcedure(String iri) {
        synchronized (procedures) {
            Collection<String> names = configuration.getAll(CONFIG_SECTION_PROCEDURES, CONFIG_ALL_PROCEDURES);
            if (!names.contains(iri))
                return new XSPReplyFailure("Procedure does not exist");
            BaseStoredProcedure procedure = procedures.get(iri);
            if (procedure == null)
                procedure = cacheProcedure(iri);
            return new XSPReplyResult<>(procedure);
        }
    }

    /**
     * Caches the existing procedure with the specified name
     *
     * @param name The name of a procedure
     * @return The procedure
     */
    private BaseStoredProcedure cacheProcedure(String name) {
        File folder = new File(location, REPO_PROCEDURES);
        if (!folder.exists()) {
            if (!folder.mkdirs())
                return null;
        }
        File file = new File(folder, SHA1.hashSHA1(name));
        try (FileInputStream stream = new FileInputStream(file)) {
            byte[] content = Program.load(stream);
            String definition = new String(content, Files.CHARSET);
            ASTNode root = JSONLDLoader.parseJSON(logger, definition);
            BaseStoredProcedure procedure = new BaseStoredProcedure(root, repository.getStore(), logger);
            procedures.put(name, procedure);
            return procedure;
        } catch (IOException exception) {
            logger.error(exception);
            return null;
        }
    }

    @Override
    public XSPReply getStoredProcedures() {
        synchronized (procedures) {
            Collection<String> names = configuration.getAll(CONFIG_SECTION_PROCEDURES, CONFIG_ALL_PROCEDURES);
            for (String name : names) {
                BaseStoredProcedure procedure = procedures.get(name);
                if (procedure == null)
                    cacheProcedure(name);
            }
            return new XSPReplyResultCollection<>(procedures.values());
        }
    }

    @Override
    public XSPReply addStoredProcedure(String iri, String sparql, Collection<String> parameters) {
        synchronized (procedures) {
            Collection<String> names = configuration.getAll(CONFIG_SECTION_PROCEDURES, CONFIG_ALL_PROCEDURES);
            if (names.contains(iri))
                return new XSPReplyFailure("A procedure with this name alreay exists");
            SPARQLLoader loader = new SPARQLLoader(repository.getStore());
            BufferedLogger bufferedLogger = new BufferedLogger();
            DispatchLogger dispatchLogger = new DispatchLogger(bufferedLogger, logger);
            Command command = loader.load(dispatchLogger, new StringReader(sparql));
            if (!bufferedLogger.getErrorMessages().isEmpty())
                return new XSPReplyFailure(bufferedLogger.getErrorsAsString());
            BaseStoredProcedure procedure = new BaseStoredProcedure(iri, sparql, parameters, command);

            String name = SHA1.hashSHA1(iri);
            File folder = new File(location, REPO_PROCEDURES);
            if (!folder.exists()) {
                if (!folder.mkdirs())
                    return XSPReplyFailure.instance();
            }
            File file = new File(folder, name);
            try (FileOutputStream stream = new FileOutputStream(file)) {
                stream.write(procedure.serializedJSON().getBytes(Files.CHARSET));
                stream.flush();
            } catch (IOException exception) {
                logger.error(exception);
                return new XSPReplyFailure(exception.getMessage());
            }

            configuration.add(CONFIG_SECTION_PROCEDURES, CONFIG_ALL_PROCEDURES, iri);
            try {
                configuration.save((new File(location, REPO_CONF_NAME)).getAbsolutePath(), Files.CHARSET);
            } catch (IOException exception) {
                logger.error(exception);
            }

            procedures.put(iri, procedure);
            return new XSPReplyResult<>(procedure);
        }
    }

    @Override
    public XSPReply removeStoredProcedure(XOWLStoredProcedure procedure) {
        return removeStoredProcedure(procedure.getName());
    }

    public XSPReply removeStoredProcedure(String iri) {
        synchronized (procedures) {
            if (!configuration.getAll(CONFIG_SECTION_PROCEDURES, CONFIG_ALL_PROCEDURES).contains(iri))
                return new XSPReplyFailure("Procedure does not exist");
            configuration.getAll(CONFIG_SECTION_PROCEDURES, CONFIG_ALL_PROCEDURES).remove(iri);
            try {
                configuration.save((new File(location, REPO_CONF_NAME)).getAbsolutePath(), Files.CHARSET);
            } catch (IOException exception) {
                logger.error(exception);
                return new XSPReplyFailure(exception.getMessage());
            }

            File folder = new File(location, REPO_PROCEDURES);
            if (!folder.exists()) {
                if (!folder.mkdirs())
                    return XSPReplyFailure.instance();
            }
            File file = new File(folder, SHA1.hashSHA1(iri));
            if (!file.delete())
                logger.error("Failed to delete " + file.getAbsolutePath());

            procedures.remove(iri);
            return XSPReplySuccess.instance();
        }
    }

    @Override
    public XSPReply executeStoredProcedure(XOWLStoredProcedure procedure, XOWLStoredProcedureContext context) {
        return executeStoredProcedure(procedure.getName(), context);
    }

    /**
     * Executes a stored procedure
     *
     * @param iri               The name (iri) of the procedure to execute
     * @param contextDefinition The execution context to use (in a serialized JSON form)
     * @return The protocol reply
     */
    public XSPReply executeStoredProcedure(String iri, String contextDefinition) {
        BufferedLogger bufferedLogger = new BufferedLogger();
        DispatchLogger dispatchLogger = new DispatchLogger(bufferedLogger, logger);
        ASTNode root = JSONLDLoader.parseJSON(dispatchLogger, contextDefinition);
        if (!bufferedLogger.getErrorMessages().isEmpty())
            return new XSPReplyFailure(bufferedLogger.getErrorsAsString());
        BaseStoredProcedureContext context = new BaseStoredProcedureContext(root, repository.getStore());
        return executeStoredProcedure(iri, context);
    }

    /**
     * Executes a stored procedure
     *
     * @param iri     The name (iri) of the procedure to execute
     * @param context The execution context to use
     * @return The protocol reply
     */
    public XSPReply executeStoredProcedure(String iri, XOWLStoredProcedureContext context) {
        BaseStoredProcedure procedure;
        synchronized (procedures) {
            Collection<String> names = configuration.getAll(CONFIG_SECTION_PROCEDURES, CONFIG_ALL_PROCEDURES);
            if (!names.contains(iri))
                return new XSPReplyFailure("Procedure does not exist");
            procedure = procedures.get(iri);
            if (procedure == null) {
                procedure = cacheProcedure(iri);
                if (procedure == null)
                    return new XSPReplyFailure("Failed to retrieve the procedure");
            }
        }
        for (String parameter : procedure.getParameters()) {
            if (context.getParameters().get(parameter) == null)
                return new XSPReplyFailure("Missing required parameter: " + parameter);
        }
        Command sparql = procedure.getSPARQL().clone(context.getParameters());
        return sparql(sparql);
    }

    @Override
    public XSPReply upload(String syntax, String content) {
        onThreadEnter();
        try {
            repository.loadResource(new StringReader(content), IRIs.GRAPH_DEFAULT, IRIs.GRAPH_DEFAULT, syntax);
            repository.getStore().commit();
            return XSPReplySuccess.instance();
        } catch (IOException exception) {
            return new XSPReplyFailure(exception.getMessage());
        } finally {
            onThreadExit();
        }
    }

    @Override
    public XSPReply upload(Collection<Quad> quads) {
        onThreadEnter();
        try {
            repository.getStore().insert(Changeset.fromAdded(quads));
            repository.getStore().commit();
            return XSPReplySuccess.instance();
        } catch (UnsupportedNodeType exception) {
            repository.getStore().rollback();
            return new XSPReplyFailure(exception.getMessage());
        } finally {
            onThreadExit();
        }
    }

    @Override
    public XSPReply getStatistics() {
        return new XSPReplyResult<>(repository.getStore().getStatistics());
    }

    @Override
    public void close() throws IOException {
        try {
            repository.getStore().close();
        } catch (Exception exception) {
            logger.error(exception);
        }
    }
}
