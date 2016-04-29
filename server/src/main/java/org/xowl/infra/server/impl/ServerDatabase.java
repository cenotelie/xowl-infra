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

import org.xowl.infra.server.api.XOWLRule;
import org.xowl.infra.server.api.base.BaseDatabase;
import org.xowl.infra.server.api.base.BaseRule;
import org.xowl.infra.server.xsp.*;
import org.xowl.infra.store.*;
import org.xowl.infra.store.Serializable;
import org.xowl.infra.store.loaders.NQuadsLoader;
import org.xowl.infra.store.loaders.RDFLoaderResult;
import org.xowl.infra.store.loaders.RDFTLoader;
import org.xowl.infra.store.loaders.SPARQLLoader;
import org.xowl.infra.store.rdf.Quad;
import org.xowl.infra.store.rdf.Rule;
import org.xowl.infra.store.rdf.RuleExplanation;
import org.xowl.infra.store.rete.MatchStatus;
import org.xowl.infra.store.sparql.Command;
import org.xowl.infra.store.sparql.Result;
import org.xowl.infra.store.sparql.ResultSuccess;
import org.xowl.infra.store.storage.BaseStore;
import org.xowl.infra.store.storage.StoreFactory;
import org.xowl.infra.utils.Files;
import org.xowl.infra.utils.config.Configuration;
import org.xowl.infra.utils.logging.BufferedLogger;
import org.xowl.infra.utils.logging.ConsoleLogger;
import org.xowl.infra.utils.logging.DispatchLogger;
import org.xowl.infra.utils.logging.Logger;

import java.io.*;
import java.util.*;

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
     * The namespace IRI for rules loaded in a database
     */
    private static final String RULES_RESOURCE = "http://xowl.org/server/rules";
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
    private final Repository repository;
    /**
     * The current configuration for this database
     */
    private final Configuration configuration;
    /**
     * The proxy object representing this database
     */
    private final ProxyObject proxy;

    /**
     * Gets the repository backing this database
     *
     * @return The repository backing this database
     */
    public Repository getRepository() {
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
        this.configuration = new Configuration();
        BaseStore store = initStore();
        this.repository = new Repository(store);
        this.proxy = repository.resolveProxy(Schema.ADMIN_GRAPH_DBS + confServer.getAdminDBName());
        initRepository();
    }

    /**
     * Initializes this database
     *
     * @param location The database's location
     * @param proxy    The proxy object representing this database
     * @throws IOException When the location cannot be accessed
     */
    public ServerDatabase(File location, ProxyObject proxy) throws IOException {
        super((String) proxy.getDataValue(Schema.ADMIN_NAME));
        this.location = location;
        this.logger = new ConsoleLogger();
        this.configuration = new Configuration();
        BaseStore store = initStore();
        this.repository = new Repository(store);
        this.proxy = proxy;
        initRepository();
    }

    /**
     * Initializes the underlying store
     *
     * @return The store
     * @throws IOException When the location cannot be accessed
     */
    private BaseStore initStore() throws IOException {
        if (!location.exists()) {
            if (!location.mkdirs()) {
                throw error(logger, "Failed to create the directory for repository at " + location.getPath());
            }
        }
        File configFile = new File(location, REPO_CONF_NAME);
        if (configFile.exists()) {
            configuration.load(configFile.getAbsolutePath(), Files.CHARSET);
        }
        String cBackend = configuration.get(CONFIG_STORAGE);
        return Objects.equals(cBackend, CONFIG_STORAGE_MEMORY) ?
                StoreFactory.create().inMemory().withReasoning().withMultithreading().make() :
                StoreFactory.create().onDisk(location).withReasoning().withMultithreading().make();
    }

    /**
     * Initializes the repository from configuration
     */
    private void initRepository() {
        String cRegime = configuration.get(CONFIG_ENTAILMENT);
        if (cRegime != null)
            repository.setEntailmentRegime(logger, EntailmentRegime.valueOf(cRegime));
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

    @Override
    public XSPReply sparql(String sparql, List<String> defaultIRIs, List<String> namedIRIs) {
        BufferedLogger bufferedLogger = new BufferedLogger();
        DispatchLogger dispatchLogger = new DispatchLogger(logger, bufferedLogger);
        if (defaultIRIs == null)
            defaultIRIs = Collections.emptyList();
        if (namedIRIs == null)
            namedIRIs = Collections.emptyList();
        SPARQLLoader loader = new SPARQLLoader(repository.getStore(), defaultIRIs, namedIRIs);
        List<Command> commands = loader.load(dispatchLogger, new StringReader(sparql));
        if (commands == null) {
            // ill-formed request
            dispatchLogger.error("Failed to parse and load the request");
            return new XSPReplyFailure(bufferedLogger.getErrorsAsString());
        }
        Result result = ResultSuccess.INSTANCE;
        for (Command command : commands) {
            result = command.execute(repository);
            if (result.isFailure()) {
                break;
            }
        }
        return new XSPReplyResult<>(result);
    }

    @Override
    public XSPReply getEntailmentRegime() {
        return new XSPReplyResult<>(repository.getEntailmentRegime());
    }

    @Override
    public XSPReply setEntailmentRegime(EntailmentRegime regime) {
        repository.setEntailmentRegime(logger, regime);
        configuration.set(CONFIG_ENTAILMENT, regime.toString());
        try {
            configuration.save((new File(location, REPO_CONF_NAME)).getAbsolutePath(), Files.CHARSET);
        } catch (IOException exception) {
            logger.error(exception);
            return new XSPReplyFailure(exception.getMessage());
        }
        repository.getStore().commit();
        return XSPReplySuccess.instance();
    }

    public XSPReply setEntailmentRegime(String regime) {
        EntailmentRegime value = EntailmentRegime.valueOf(regime);
        if (value == null)
            return new XSPReplyFailure("Unexpected regime name");
        return setEntailmentRegime(value);
    }

    @Override
    public XSPReply getRule(String name) {
        Collection<String> names = configuration.getAll(CONFIG_SECTION_RULES, CONFIG_ALL_RULES);
        Collection<String> actives = configuration.getAll(CONFIG_SECTION_RULES, CONFIG_ACTIVE_RULES);
        if (!names.contains(name))
            return new XSPReplyFailure("Rule does not exist");
        File folder = new File(location, REPO_RULES);
        File file = new File(folder, IOUtils.hashSHA1(name));
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
            File file = new File(folder, IOUtils.hashSHA1(name));
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

        Rule rule = result.getRules().get(0);
        String name = IOUtils.hashSHA1(rule.getIRI());
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
    }

    @Override
    public XSPReply removeRule(XOWLRule rule) {
        return removeRule(rule.getName());
    }

    public XSPReply removeRule(String iri) {
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
        File file = new File(folder, IOUtils.hashSHA1(iri));
        if (!file.delete())
            logger.error("Failed to delete " + file.getAbsolutePath());
        return XSPReplySuccess.instance();
    }

    @Override
    public XSPReply activateRule(XOWLRule rule) {
        return activateRule(rule.getName());
    }

    public XSPReply activateRule(String iri) {
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
    }

    /**
     * Activates a rule in this database
     *
     * @param iri The IRI of a rule
     * @return Whether the operation succeeded
     */
    private boolean doActivateRule(String iri) {
        File folder = new File(location, REPO_RULES);
        File file = new File(folder, IOUtils.hashSHA1(iri));
        Rule rule;
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
    }

    @Override
    public XSPReply getRuleStatus(XOWLRule rule) {
        return getRuleStatus(rule.getName());
    }

    public XSPReply getRuleStatus(String iri) {
        if (!configuration.getAll(CONFIG_SECTION_RULES, CONFIG_ACTIVE_RULES).contains(iri))
            return new XSPReplyFailure("Not active");
        MatchStatus result = repository.getRDFRuleEngine().getMatchStatus(iri);
        if (result == null)
            return XSPReplyFailure.instance();
        return new XSPReplyResult<>(result);
    }

    @Override
    public XSPReply getQuadExplanation(Quad quad) {
        if (quad.getGraph() == null)
            return new XSPReplyFailure("Quad must have a graph");
        RuleExplanation explanation = repository.getRDFRuleEngine().explain(quad);
        return new XSPReplyResult<>(explanation);
    }

    public XSPReply getQuadExplanation(String quad) {
        BufferedLogger bufferedLogger = new BufferedLogger();
        DispatchLogger dispatchLogger = new DispatchLogger(logger, bufferedLogger);
        NQuadsLoader loader = new NQuadsLoader(repository.getStore());
        RDFLoaderResult result = loader.loadRDF(dispatchLogger, new StringReader(quad), null, IRIs.GRAPH_DEFAULT);
        if (result == null) {
            // ill-formed request
            dispatchLogger.error("Failed to parse and load the quad");
            return new XSPReplyFailure(bufferedLogger.getErrorsAsString());
        }
        if (result.getQuads().size() != 1)
            return new XSPReplyFailure("Expected one quad");
        return getQuadExplanation(result.getQuads().get(0));
    }

    @Override
    public XSPReply upload(String syntax, String content) {
        BufferedLogger bufferedLogger = new BufferedLogger();
        DispatchLogger dispatchLogger = new DispatchLogger(logger, bufferedLogger);
        repository.loadResource(dispatchLogger, new StringReader(content), IRIs.GRAPH_DEFAULT, IRIs.GRAPH_DEFAULT, syntax);
        if (!bufferedLogger.getErrorMessages().isEmpty()) {
            repository.getStore().rollback();
            return new XSPReplyFailure(bufferedLogger.getErrorsAsString());
        }
        repository.getStore().commit();
        return XSPReplySuccess.instance();
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
