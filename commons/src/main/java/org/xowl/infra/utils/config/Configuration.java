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
import org.xowl.infra.utils.TextUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;

/**
 * Represents a configuration with values associated to properties
 * This structure is NOT thread safe
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
     * Gets all the named sections in this configuration
     *
     * @return The named sections
     */
    public Collection<Section> getSections() {
        return new ArrayList<>(sections.values());
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
    public List<String> getAll(String property) {
        return global.getAll(property);
    }

    /**
     * Gets the first value for the specified property in the global section
     *
     * @param property A property in the global section
     * @return The first value associated to the specified property
     */
    public String get(String property) {
        return global.get(property);
    }

    /**
     * Gets the list of values for the specified section and property
     *
     * @param section  A section in this configuration
     * @param property A property in the section
     * @return A list of the values associated to the specified property
     */
    public List<String> getAll(String section, String property) {
        if (section == null)
            return global.getAll(property);
        Section current = sections.get(section);
        if (current == null)
            return Collections.emptyList();
        return current.getAll(property);
    }

    /**
     * Gets the list of values for the specified section and property
     *
     * @param section  A section in this configuration
     * @param property A property in the section
     * @return A list of the values associated to the specified property
     */
    public String get(String section, String property) {
        if (section == null)
            return global.get(property);
        Section current = sections.get(section);
        if (current == null)
            return null;
        return current.get(property);
    }

    /**
     * Gets whether the specified property and associated value are present in a section
     *
     * @param section  A section
     * @param property A property
     * @param value    An associated value
     * @return Whether the property has the associated value
     */
    public boolean hasValue(String section, String property, String value) {
        if (section == null)
            return global.hasValue(property, value);
        Section current = sections.get(section);
        return current != null && current.hasValue(property, value);
    }

    /**
     * Adds the specified property-value pair in the specified section
     *
     * @param property A property in the section
     * @param value    The value to associate
     */
    public void add(String property, String value) {
        global.add(property, value);
    }

    /**
     * Adds the specified property-value pair in the specified section
     *
     * @param section  A section in this configuration
     * @param property A property in the section
     * @param value    The value to associate
     */
    public void add(String section, String property, String value) {
        getSection(section).add(property, value);
    }

    /**
     * Removes the specified property - value pair from global section
     *
     * @param property A property
     * @param value    The associated value to remove
     */
    public void remove(String property, String value) {
        global.remove(property, value);
    }

    /**
     * Removes the specified property - value pair from the specified section
     *
     * @param section  Name of the section in the config file
     * @param property A property
     * @param value    The associated value to remove
     */
    public void remove(String section, String property, String value) {
        getSection(section).remove(property, value);
    }

    /**
     * Sets the property, removing all previous values, if any
     *
     * @param property A property
     * @param value    The new value
     */
    public void set(String property, String value) {
        global.set(property, value);
    }

    /**
     * Sets the property, removing all previous values, if any
     *
     * @param section  A section in this configuration
     * @param property A property
     * @param value    The new value
     */
    public void set(String section, String property, String value) {
        getSection(section).set(property, value);
    }

    /**
     * Clears any value for the property
     *
     * @param property The property to clear
     */
    public void clear(String property) {
        global.clear(property);
    }

    /**
     * Clears any value for the property
     *
     * @param section  A section in this configuration
     * @param property The property to clear
     */
    public void clear(String section, String property) {
        getSection(section).clear(property);
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
                    writer.write(Files.LINE_SEPARATOR);
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
     * Imports the configuration from the specified stream
     *
     * @param stream  The stream to read from
     * @param charset The charset to use
     * @throws IOException when reading fails
     */
    public void load(InputStream stream, Charset charset) throws IOException {
        String content = org.xowl.infra.utils.Files.read(stream, charset);
        String[] lines = content.split("(\r\n?)|(\r?\n)");
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
                    current.add(parts[0].trim(), TextUtils.unescape(parts[1].trim()));
                } else if (parts.length > 2) {
                    java.lang.StringBuilder buffer = new java.lang.StringBuilder(parts[1]);
                    for (int i = 2; i != parts.length; i++) {
                        buffer.append("=");
                        buffer.append(parts[i]);
                    }
                    current.add(parts[0].trim(), TextUtils.unescape(buffer.toString().trim()));
                }
            }
        }
    }
}
