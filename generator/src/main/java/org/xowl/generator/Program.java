/**********************************************************************
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
 **********************************************************************/

package org.xowl.generator;

import org.xowl.generator.builder.Builder;
import org.xowl.generator.model.Model;
import org.xowl.store.owl.DirectSemantics;
import org.xowl.utils.ConsoleLogger;
import org.xowl.utils.Logger;
import org.xowl.utils.collections.Couple;
import org.xowl.utils.config.Configuration;

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
            log.error("Failed to loadAxioms configuration file: " + args[0]);
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
     * Whether to generate a proxy for the engine
     */
    private boolean proxy;
    /**
     * The path to the output directory
     */
    private String outputDirectory;
    /**
     * The name of the jar to generate
     */
    private String outputJarName;
    /**
     * The maximum length for the name of the generated files
     */
    private int outputMaxFileLength;

    /**
     * Gets the maximum length for the name of the generated files
     *
     * @return The maximum length for the name of the generated files
     */
    public int getMaxFileLength() {
        return outputMaxFileLength;
    }

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
        this.proxy = false;
        this.outputJarName = "Model";
        this.outputMaxFileLength = 100;
        loadConfig(configuration);
    }

    /**
     * Loads the specified configuration
     *
     * @param config The configuration to loadAxioms
     */
    private void loadConfig(Configuration config) {
        List<String> values = config.getValues(null, "repository");
        for (String val : values) {
            String[] parts = val.split("\\|");
            Couple<String, String> mapping = new Couple<>();
            mapping.x = parts[0].trim();
            mapping.y = parts[1].trim();
            repositories.add(mapping);
        }
        values = config.getValues(null, "input");
        for (String val : values) {
            inputs.add(val.trim());
        }

        String value = config.getValue("basePackage");
        if (value != null)
            basePackage = value;

        value = config.getValue("debug");
        if (value != null)
            debug = Boolean.valueOf(value);

        value = config.getValue("proxy");
        if (value != null)
            proxy = Boolean.valueOf(value);

        value = config.getValue("output");
        if (value != null)
            outputDirectory = value;

        value = config.getValue("jarName");
        if (value != null)
            outputJarName = value;

        value = config.getValue("maxFileLength");
        if (value != null)
            outputMaxFileLength = java.lang.Integer.parseInt(value);
    }

    /**
     * Executes the generation
     */
    public void execute() {
        DirectSemantics repository = new DirectSemantics();
        for (Couple<String, String> mapping : repositories)
            repository.getIRIMapper().addRegexpMap(mapping.x, mapping.y);
        for (String input : inputs)
            repository.load(logger, input);

        Model model = new Model(repository, basePackage);
        model.load();
        model.build(logger);

        try {
            model.writeStandalone(outputDirectory + "src/");
        } catch (Exception ex) {
            logger.error(ex);
        }

        try {
            Builder builder = new Builder(outputDirectory, debug);
            builder.build(outputJarName);
        } catch (Exception ex) {
            logger.error(ex);
        }
    }
}
