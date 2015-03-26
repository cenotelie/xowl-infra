/**********************************************************************
 * Copyright (c) 2014 Laurent Wouters
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

package org.xowl.utils.config;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a configuration with values associated to properties
 *
 * @author Laurent Wouters
 */
public class Configuration {
    /**
     * The global section of this configuration
     */
    private final Section global;
    /**
     * The named sections
     */
    private final Map<String, Section> sections;

    /**
     * Initializes an empty configuration
     */
    public Configuration() {
        global = new Section(null);
        sections = new HashMap<>();
    }

    /**
     * Gets the global section in this configuration
     *
     * @return The global section in this configuration
     */
    public Section getGlobalSection() {
        return global;
    }

    /**
     * Gets the section with the specified name
     *
     * @param name A section's name
     * @return The section with the specified name
     */
    public Section getSection(String name) {
        if (name == null)
            return global;
        if (!sections.containsKey(name))
            sections.put(name, new Section(name));
        return sections.get(name);
    }

    /**
     * Gets a list of the values for the specified property in the global section
     *
     * @param property A property in the global section
     * @return A list of the values associated to the specified property
     */
    public List<String> getValues(String property) {
        return global.getValues(property);
    }

    /**
     * Gets the first value for the specified property in the global section
     *
     * @param property A property in the global section
     * @return The first value associated to the specified property
     */
    public String getValue(String property) {
        return global.getValue(property);
    }

    /**
     * Gets the list of values for the specified section and property
     *
     * @param section  A section in this configuration
     * @param property A property in the section
     * @return A list of the values associated to the specified property
     */
    public List<String> getValues(String section, String property) {
        if (section == null)
            return global.getValues(property);
        Section current = sections.get(section);
        if (current == null)
            return new ArrayList<>();
        return current.getValues(property);
    }

    /**
     * Gets the list of values for the specified section and property
     *
     * @param section  A section in this configuration
     * @param property A property in the section
     * @return A list of the values associated to the specified property
     */
    public String getValue(String section, String property) {
        if (section == null)
            return global.getValue(property);
        Section current = sections.get(section);
        if (current == null)
            return null;
        return current.getValue(property);
    }

    /**
     * Adds the specified property-value pair in the specified section
     *
     * @param section  A section in this configuration
     * @param property A property in the section
     * @param value    The value to associate
     */
    public void addValue(String section, String property, String value) {
        getSection(section).addValue(property, value);
    }

    /**
     * Exports this configuration to the specified file
     *
     * @param file    The file to export to
     * @param charset The charset ot use
     * @throws IOException when writing fails
     */
    public void save(String file, Charset charset) throws IOException {
        FileOutputStream stream = new FileOutputStream(file);
        try (OutputStreamWriter writer = new OutputStreamWriter(stream, charset)) {
            global.save(writer);
            boolean before = !global.isEmpty();
            for (Section section : sections.values()) {
                if (before)
                    writer.write(System.lineSeparator());
                section.save(writer);
                before = (before || !section.isEmpty());
            }
        }
    }

    /**
     * Imports the configuration from the specified file
     *
     * @param file    The file to import from
     * @param charset The charset to use
     * @throws IOException when reading fails
     */
    public void load(String file, Charset charset) throws IOException {
        try (FileInputStream stream = new FileInputStream(file)) {
            load(stream, charset);
        }
    }

    /**
     * Imports the configuration from the specified file
     *
     * @param file The file to import from
     * @throws IOException when reading fails
     */
    public void load(String file) throws IOException {
        load(file, org.xowl.utils.Files.detectEncoding(file));
    }

    /**
     * Imports the configuration from the specified stream
     *
     * @param stream  The stream to read from
     * @param charset The charset to use
     * @throws IOException when reading fails
     */
    public void load(InputStream stream, Charset charset) throws IOException {
        String content = org.xowl.utils.Files.read(stream, charset);
        String[] lines = content.split("\r\n");
        Section current = global;
        for (String line : lines) {
            if (line.startsWith("#") || line.startsWith(";"))
                continue;
            if (line.startsWith("[") && line.endsWith("]")) {
                String name = line.substring(1, line.length() - 1);
                current = new Section(name);
                sections.put(name, current);
            } else if (line.contains("=")) {
                String[] parts = line.split("=");
                if (parts.length == 2) {
                    current.addValue(parts[0].trim(), replaceEscapees(parts[1].trim()));
                } else if (parts.length > 2) {
                    java.lang.StringBuilder buffer = new java.lang.StringBuilder(parts[1]);
                    for (int i = 2; i != parts.length; i++) {
                        buffer.append("=");
                        buffer.append(parts[i]);
                    }
                    current.addValue(parts[0].trim(), replaceEscapees(buffer.toString().trim()));
                }
            }
        }
    }

    /**
     * Replaces the escape sequences by their value in the specified string
     *
     * @param value A string
     * @return The same string with the escape sequences replaced
     */
    private String replaceEscapees(String value) {
        /*  Sequence
            \\          \ (a single backslash, escaping the escape character)
            \0          Null character
            \a          Bell/Alert/Audible
            \b          Backspace, Bell character for some applications
            \t          Tab character
            \r          Carriage return
            \n          Newline
        */
        String[] parts = value.split("\\\\");
        if (parts.length == 0 || parts.length == 1)
            return value;
        StringBuilder builder = new StringBuilder(parts[0]);
        for (int i = 1; i != parts.length; i++) {
            String part = parts[i];
            if (part.isEmpty()) {
                // replaces "\\" to "\"
                builder.append("\\");
                i++;
            } else {
                char first = part.charAt(0);
                switch (first) {
                    case '0':
                        builder.append("\u0000");
                        break;
                    case 'a':
                        builder.append("\u0007");
                        break;
                    case 'b':
                        builder.append("\b");
                        break;
                    case 't':
                        builder.append("\t");
                        break;
                    case 'r':
                        builder.append("\r");
                        break;
                    case 'n':
                        builder.append("\n");
                        break;
                    default:
                        break;
                }
                builder.append(part.substring(1));
            }
        }
        return builder.toString();
    }
}
