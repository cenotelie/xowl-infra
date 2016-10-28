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

package org.xowl.infra.generator;

import org.xowl.infra.generator.builder.Builder;
import org.xowl.infra.generator.model.Model;
import org.xowl.infra.store.IRIMapper;
import org.xowl.infra.store.RepositoryDirectSemantics;
import org.xowl.infra.utils.collections.Couple;
import org.xowl.infra.utils.config.Configuration;
import org.xowl.infra.utils.logging.ConsoleLogger;
import org.xowl.infra.utils.logging.Logger;
import org.xowl.infra.utils.logging.Logging;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Main program for the generator
 *
 * @author Laurent Wouters
 */
public class Program {
    /**
     * Configures a mapping (writted as "iri | physical_location") for the forthcoming inputs
     */
    public static final String CONFIG_REPOSITORY = "repository";
    /**
     * Configures the IRI of an input ontology
     */
    public static final String CONFIG_INPUT = "input";
    /**
     * Configures the name of the base Java package for the code to generate
     */
    public static final String CONFIG_BASE_PACKAGE = "basePackage";
    /**
     * Configures whether to emit debug information in the compiled Java bytecode
     */
    public static final String CONFIG_DEBUG = "debug";
    /**
     * Configures the output directory
     */
    public static final String CONFIG_OUTPUT = "output";
    /**
     * Configures the name of the jar to generate (without the .jar extension).
     * If this option is left undefined, the generated code is not built.
     */
    public static final String CONFIG_JAR_NAME = "jarName";


    /**
     * Main entry point for this program
     *
     * @param args The command-line arguments
     */
    public static void main(String[] args) {
        Logger log = new ConsoleLogger();
        if (args.length == 0) {
            log.error("No config file in parameters");
            return;
        }
        Configuration config = new Configuration();
        try {
            config.load(args[0]);
        } catch (IOException ex) {
            log.error("Failed to load configuration file: " + args[0]);
            return;
        }
        Program app = new Program(log, config);
        app.execute();
    }

    /**
     * The logger
     */
    private Logger logger;
    /**
     * The repository specifications as IRI mappings
     */
    private List<Couple<String, String>> repositories;
    /**
     * The input resources to generate code from
     */
    private List<String> inputs;
    /**
     * The base Java package for the generated code
     */
    private String basePackage;
    /**
     * Whether to emit code with debug information
     */
    private boolean debug;
    /**
     * The path to the output directory
     */
    private String outputDirectory;
    /**
     * The name of the jar to generate
     */
    private String outputJarName;

    /**
     * Initializes this program
     *
     * @param logger        The logger to use
     * @param configuration The configuration
     */
    public Program(Logger logger, Configuration configuration) {
        this.logger = logger;
        this.repositories = new ArrayList<>();
        this.inputs = new ArrayList<>();
        this.debug = false;
        loadConfig(configuration);
    }

    /**
     * Loads the specified configuration
     *
     * @param config The configuration to load
     */
    private void loadConfig(Configuration config) {
        List<String> values = config.getAll(null, CONFIG_REPOSITORY);
        for (String val : values) {
            String[] parts = val.split("\\|");
            Couple<String, String> mapping = new Couple<>();
            mapping.x = parts[0].trim();
            mapping.y = parts[1].trim();
            repositories.add(mapping);
        }
        values = config.getAll(null, CONFIG_INPUT);
        for (String val : values) {
            inputs.add(val.trim());
        }

        String value = config.get(CONFIG_BASE_PACKAGE);
        if (value != null)
            basePackage = value;

        value = config.get(CONFIG_DEBUG);
        if (value != null)
            debug = Boolean.valueOf(value);

        value = config.get(CONFIG_OUTPUT);
        if (value != null)
            outputDirectory = value;

        value = config.get(CONFIG_JAR_NAME);
        if (value != null)
            outputJarName = value;
    }

    /**
     * Executes the generation
     */
    public void execute() {
        // load the inputs
        RepositoryDirectSemantics repository = new RepositoryDirectSemantics(Logging.getDefault(), IRIMapper.getDefault(), null);
        for (Couple<String, String> mapping : repositories)
            repository.getIRIMapper().addRegexpMap(mapping.x, mapping.y);
        for (String input : inputs) {
            try {
                repository.load(input);
            } catch (IOException exception) {
                Logging.getDefault().error(exception);
            }
        }

        Model model = new Model(repository, basePackage);
        model.load();
        model.build(logger);

        // generate the code
        if (!outputDirectory.endsWith("/"))
            outputDirectory += "/";
        try {
            model.writeStandalone(outputDirectory + "src/");
        } catch (Exception ex) {
            logger.error(ex);
        }

        // build the code, if required
        if (outputJarName != null) {
            try {
                Builder builder = new Builder(outputDirectory, debug);
                builder.build(outputJarName);
            } catch (Exception ex) {
                logger.error(ex);
            }
        }
    }
}
