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

package org.xowl.server.db;

import org.xowl.server.Program;
import org.xowl.server.ServerConfiguration;
import org.xowl.store.*;
import org.xowl.store.Serializable;
import org.xowl.store.loaders.NQuadsLoader;
import org.xowl.store.loaders.RDFLoaderResult;
import org.xowl.store.loaders.RDFTLoader;
import org.xowl.store.rdf.Quad;
import org.xowl.store.rdf.Rule;
import org.xowl.store.rdf.RuleExplanation;
import org.xowl.store.rete.MatchStatus;
import org.xowl.store.storage.BaseStore;
import org.xowl.store.storage.StoreFactory;
import org.xowl.store.storage.remote.XSPReply;
import org.xowl.store.storage.remote.XSPReplyFailure;
import org.xowl.store.storage.remote.XSPReplyResult;
import org.xowl.store.storage.remote.XSPReplySuccess;
import org.xowl.utils.config.Configuration;
import org.xowl.utils.logging.BufferedLogger;
import org.xowl.utils.logging.ConsoleLogger;
import org.xowl.utils.logging.DispatchLogger;
import org.xowl.utils.logging.Logger;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Objects;

/**
 * Represents a database hosted on this server
 *
 * @author Laurent Wouters
 */
public class Database implements Serializable, Closeable {
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
    final File location;
    /**
     * The logger
     */
    final Logger logger;
    /**
     * The repository
     */
    final Repository repository;
    /**
     * The current configuration for this database
     */
    final Configuration configuration;
    /**
     * The proxy object representing this database
     */
    final ProxyObject proxy;
    /**
     * The cached name
     */
    private String name;

    /**
     * Initializes this database (as the admin database)
     *
     * @param confServer The server configuration
     * @param location   The database's location
     * @throws IOException When the location cannot be accessed
     */
    public Database(ServerConfiguration confServer, File location) throws IOException {
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
    public Database(File location, ProxyObject proxy) throws IOException {
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
            configuration.load(configFile.getAbsolutePath(), Charset.forName("UTF-8"));
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

    /**
     * Gets the name of this user
     *
     * @return The name of this user
     */
    public String getName() {
        if (name != null)
            return name;
        name = (String) proxy.getDataValue(Schema.ADMIN_NAME);
        return name;
    }

    /**
     * Gets the active entailment regime
     *
     * @return The active entailment regime
     */
    public EntailmentRegime getEntailmentRegime() {
        return repository.getEntailmentRegime();
    }

    /**
     * Sets the active entailment regime
     *
     * @param regime The active entailment regime
     */
    public void setEntailmentRegime(EntailmentRegime regime) {
        repository.setEntailmentRegime(logger, regime);
        configuration.set(CONFIG_ENTAILMENT, regime.toString());
        try {
            configuration.save((new File(location, REPO_CONF_NAME)).getAbsolutePath(), Charset.forName("UTF-8"));
        } catch (IOException exception) {
            logger.error(exception);
        }
        repository.getStore().commit();
    }

    /**
     * Gets all the rules in this database
     *
     * @return The protocol reply
     */
    public XSPReply getAllRules() {
        return new XSPReplyResult<>(configuration.getAll(CONFIG_SECTION_RULES, CONFIG_ALL_RULES));
    }

    /**
     * Gets the active rules
     *
     * @return The protocol reply
     */
    public XSPReply getActiveRules() {
        return new XSPReplyResult<>(configuration.getAll(CONFIG_SECTION_RULES, CONFIG_ACTIVE_RULES));
    }

    /**
     * Adds a new rule to this database
     *
     * @param content  The rule's definition
     * @param activate Whether to activate the rule
     * @return The protocol reply
     */
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
            return new XSPReplyFailure(Program.getLog(bufferedLogger));
        }
        if (result.getRules().size() != 1)
            return new XSPReplyFailure("Expected one rule");

        Rule rule = result.getRules().get(0);
        String name = Program.encode(rule.getIRI().getBytes(Charset.forName("UTF-8")));
        File file = new File(folder, name);
        try (FileOutputStream stream = new FileOutputStream(file)) {
            stream.write(content.getBytes(Charset.forName("UTF-8")));
            stream.flush();
        } catch (IOException exception) {
            logger.error(exception);
            return XSPReplyFailure.instance();
        }

        configuration.add(CONFIG_SECTION_RULES, CONFIG_ALL_RULES, rule.getIRI());
        if (activate) {
            repository.getRDFRuleEngine().add(rule);
            configuration.add(CONFIG_SECTION_RULES, CONFIG_ACTIVE_RULES, rule.getIRI());
        }
        try {
            configuration.save((new File(location, REPO_CONF_NAME)).getAbsolutePath(), Charset.forName("UTF-8"));
        } catch (IOException exception) {
            logger.error(exception);
        }
        return new XSPReplyResult<>(rule.getIRI());
    }

    /**
     * Removes a rule from this database
     * If the rule is active, it is first deactivated
     *
     * @param iri The IRI of a rule
     * @return The protocol reply
     */
    public XSPReply removeRule(String iri) {
        if (!configuration.getAll(CONFIG_SECTION_RULES, CONFIG_ALL_RULES).contains(iri))
            return new XSPReplyFailure("Not in this database");
        configuration.getAll(CONFIG_SECTION_RULES, CONFIG_ALL_RULES).remove(iri);
        if (configuration.getAll(CONFIG_SECTION_RULES, CONFIG_ACTIVE_RULES).contains(iri)) {
            configuration.remove(CONFIG_SECTION_RULES, CONFIG_ACTIVE_RULES, iri);
            repository.getRDFRuleEngine().remove(iri);
        }
        try {
            configuration.save((new File(location, REPO_CONF_NAME)).getAbsolutePath(), Charset.forName("UTF-8"));
        } catch (IOException exception) {
            logger.error(exception);
        }

        File folder = new File(location, REPO_RULES);
        File file = new File(folder, Program.encode(iri.getBytes(Charset.forName("UTF-8"))));
        file.delete();
        return XSPReplySuccess.instance();
    }

    /**
     * Gets the definition of a rule
     *
     * @param iri The IRI of a rule
     * @return The protocol reply
     */
    public XSPReply getRuleDefinition(String iri) {
        if (!configuration.getAll(CONFIG_SECTION_RULES, CONFIG_ALL_RULES).contains(iri))
            return new XSPReplyFailure("Not in this database");

        File folder = new File(location, REPO_RULES);
        File file = new File(folder, Program.encode(iri.getBytes(Charset.forName("UTF-8"))));
        try (FileInputStream stream = new FileInputStream(file)) {
            byte[] content = Program.load(stream);
            String definition = new String(content, Charset.forName("UTF-8"));
            return new XSPReplyResult<>(definition);
        } catch (IOException exception) {
            logger.error(exception);
            return XSPReplyFailure.instance();
        }
    }

    /**
     * Activates a rule in this database
     *
     * @param iri The IRI of a rule
     * @return The protocol reply
     */
    public XSPReply activateRule(String iri) {
        if (!configuration.getAll(CONFIG_SECTION_RULES, CONFIG_ALL_RULES).contains(iri))
            return new XSPReplyFailure("Not in this database");
        if (configuration.getAll(CONFIG_SECTION_RULES, CONFIG_ACTIVE_RULES).contains(iri))
            return new XSPReplyFailure("Already active");

        if (!doActivateRule(iri)) {
            return new XSPReplyFailure("Failed to activate the rule");
        }
        try {
            configuration.add(CONFIG_SECTION_RULES, CONFIG_ACTIVE_RULES, iri);
            configuration.save((new File(location, REPO_CONF_NAME)).getAbsolutePath(), Charset.forName("UTF-8"));
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
        File file = new File(folder, Program.encode(iri.getBytes(Charset.forName("UTF-8"))));
        Rule rule;
        try (FileInputStream stream = new FileInputStream(file)) {
            RDFTLoader loader = new RDFTLoader(repository.getStore());
            RDFLoaderResult result = loader.loadRDF(logger, new InputStreamReader(stream, Charset.forName("UTF-8")), RULES_RESOURCE, null);
            rule = result.getRules().get(0);
            repository.getRDFRuleEngine().add(rule);
            return true;
        } catch (IOException exception) {
            logger.error(exception);
            return false;
        }
    }

    /**
     * Deactivates a rule in this database
     *
     * @param iri The IRI of a rule
     * @return The protocol reply
     */
    public XSPReply deactivateRule(String iri) {
        if (!configuration.getAll(CONFIG_SECTION_RULES, CONFIG_ALL_RULES).contains(iri))
            return new XSPReplyFailure("Not in this database");
        if (!configuration.getAll(CONFIG_SECTION_RULES, CONFIG_ACTIVE_RULES).contains(iri))
            return new XSPReplyFailure("Not active");

        try {
            configuration.remove(CONFIG_SECTION_RULES, CONFIG_ACTIVE_RULES, iri);
            configuration.save((new File(location, REPO_CONF_NAME)).getAbsolutePath(), Charset.forName("UTF-8"));
        } catch (IOException exception) {
            logger.error(exception);
        }
        repository.getRDFRuleEngine().remove(iri);
        return XSPReplySuccess.instance();
    }

    /**
     * Gets whether a rule is active in this database
     *
     * @param iri The IRI of a rule
     * @return The protocol reply
     */
    public XSPReply isRuleActive(String iri) {
        return new XSPReplyResult<>(configuration.getAll(CONFIG_SECTION_RULES, CONFIG_ACTIVE_RULES).contains(iri));
    }

    /**
     * Gets the matching status of a rule
     *
     * @param iri The IRI of a rule
     * @return The protocol reply
     */
    public XSPReply getRuleStatus(String iri) {
        if (!configuration.getAll(CONFIG_SECTION_RULES, CONFIG_ACTIVE_RULES).contains(iri))
            return new XSPReplyFailure("Not active");
        MatchStatus result = repository.getRDFRuleEngine().getMatchStatus(iri);
        if (result == null)
            return XSPReplyFailure.instance();
        return new XSPReplyResult<>(result);
    }

    /**
     * Gets an explanation about how the specified quad appeared in the database
     *
     * @param content The quad to investigate
     * @return The protocol reply
     */
    public XSPReply getExplanation(String content) {
        BufferedLogger bufferedLogger = new BufferedLogger();
        DispatchLogger dispatchLogger = new DispatchLogger(logger, bufferedLogger);
        NQuadsLoader loader = new NQuadsLoader(repository.getStore());
        RDFLoaderResult result = loader.loadRDF(dispatchLogger, new StringReader(content), null, IRIs.GRAPH_DEFAULT);
        if (result == null) {
            // ill-formed request
            dispatchLogger.error("Failed to parse and load the quad");
            return new XSPReplyFailure(Program.getLog(bufferedLogger));
        }
        if (result.getQuads().size() != 1)
            return new XSPReplyFailure("Expected one quad");
        Quad quad = result.getQuads().get(0);
        if (quad.getGraph() == null)
            return new XSPReplyFailure("Quad must have a graph");
        RuleExplanation explanation = repository.getRDFRuleEngine().explain(quad);
        return new XSPReplyResult<>(explanation);
    }

    @Override
    public void close() throws IOException {
        try {
            repository.getStore().close();
        } catch (Exception exception) {
            logger.error(exception);
        }
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public String serializedString() {
        return getName();
    }

    @Override
    public String serializedJSON() {
        return "{\"type\": \"" + IOUtils.escapeStringJSON(Database.class.getCanonicalName()) + "\", \"name\": \"" + IOUtils.escapeStringJSON(getName()) + "\"}";
    }
}
