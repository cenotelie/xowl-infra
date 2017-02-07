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

package org.xowl.infra.utils.config;

import org.xowl.infra.utils.Files;

import java.io.IOException;
import java.io.Writer;
import java.util.Collections;
import java.util.List;

/**
 * Generator of code for configurations
 *
 * @author Laurent Wouters
 */
public class ConfigurationGenerator {
    /**
     * Input sample of configuration
     */
    private final String input;
    /**
     * Output file
     */
    private final String output;
    /**
     * Package for the generated code
     */
    private final String pack;
    /**
     * Generated class name
     */
    private final String name;

    /**
     * Initializes this generator
     *
     * @param input     Input sample of configuration
     * @param output    Output file
     * @param className Name of the class to generated
     */
    public ConfigurationGenerator(String input, String output, String className) {
        this.input = input;
        this.output = output;
        String[] parts = className.split("\\.");
        this.name = parts[parts.length - 1];
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i != parts.length - 1; i++) {
            if (i != 0) builder.append(".");
            builder.append(parts[i]);
        }
        this.pack = builder.toString();
    }

    /**
     * Generates the code
     *
     * @throws IOException when writing fails
     */
    public void generate() throws IOException {
        Configuration config = new Configuration();
        config.load(input);
        List<String> properties = config.getGlobalSection().getProperties();
        Collections.sort(properties);
        Writer writer = null;
        try {
            writer = org.xowl.infra.utils.Files.getWriter(output);
            writer.append("/*" + Files.LINE_SEPARATOR);
            writer.append("WARNING: this file has been automatically generated" + Files.LINE_SEPARATOR);
            writer.append("*/" + Files.LINE_SEPARATOR);
            writer.append(Files.LINE_SEPARATOR);
            writer.append("package ").append(pack).append(";").append(Files.LINE_SEPARATOR);
            writer.append(Files.LINE_SEPARATOR);
            writer.append("import org.xowl.infra.utils.config.Configuration;" + Files.LINE_SEPARATOR);
            writer.append("import java.util.ArrayList;" + Files.LINE_SEPARATOR);
            writer.append("import java.util.List;" + Files.LINE_SEPARATOR);
            writer.append(Files.LINE_SEPARATOR);
            writer.append("public class ").append(name).append("{").append(Files.LINE_SEPARATOR);
            for (String property : properties) {
                List<String> values = config.getAll(property);
                if (values.size() == 1) {
                    writer.append("    public String ").append(property.replace(".", "_")).append(";").append(Files.LINE_SEPARATOR);
                } else {
                    writer.append("    public List<String> ").append(property.replace(".", "_")).append(";").append(Files.LINE_SEPARATOR);
                }
            }
            writer.append("    public ").append(name).append("() {").append(Files.LINE_SEPARATOR);
            for (String property : properties) {
                List<String> values = config.getAll(property);
                if (values.size() != 1)
                    writer.append("        ").append(property.replace(".", "_")).append(" = new ArrayList<String>();").append(Files.LINE_SEPARATOR);
            }
            writer.append("    }" + Files.LINE_SEPARATOR);
            writer.append("    public ").append(name).append("(Configuration config) {").append(Files.LINE_SEPARATOR);
            for (String property : properties) {
                List<String> values = config.getAll(property);
                if (values.size() == 1) {
                    writer.append("        ").append(property.replace(".", "_")).append(" = config.get(\"").append(property).append("\");").append(Files.LINE_SEPARATOR);
                } else {
                    writer.append("        ").append(property.replace(".", "_")).append(" = config.getAll(\"").append(property).append("\");").append(Files.LINE_SEPARATOR);
                }
            }
            writer.append("    }" + Files.LINE_SEPARATOR);
            writer.append("    public void save(Configuration config) {" + Files.LINE_SEPARATOR);
            for (String property : properties) {
                List<String> values = config.getAll(property);
                if (values.size() == 1) {
                    writer.append("        config.add(null, \"").append(property).append("\", ").append(property.replace(".", "_")).append(");").append(Files.LINE_SEPARATOR);
                } else {
                    writer.append("        for (String value : ").append(property.replace(".", "_")).append(") config.add(null, \"").append(property).append("\", value);").append(Files.LINE_SEPARATOR);
                }
            }
            writer.append("    }" + Files.LINE_SEPARATOR);
            writer.append("}" + Files.LINE_SEPARATOR);
        } finally {
            if (writer != null)
                writer.close();
        }
    }
}
