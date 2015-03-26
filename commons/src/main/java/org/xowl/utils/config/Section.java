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

import java.io.IOException;
import java.io.Writer;
import java.util.*;

/**
 * Represents a section in a configuration file
 *
 * @author Laurent Wouters
 */
public class Section {
    /**
     * The section's name
     */
    private final String name;
    /**
     * The properties in this section
     */
    private final Map<String, List<String>> properties;

    /**
     * Initializes this section
     *
     * @param name The section's name
     */
    public Section(String name) {
        this.name = name;
        this.properties = new HashMap<>();
    }

    /**
     * Gets the section's name
     *
     * @return The section's name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets whether this section is empty
     *
     * @return true if this section is empty
     */
    public boolean isEmpty() {
        return properties.isEmpty();
    }

    /**
     * Gets the set of properties defined in this section
     *
     * @return The set of properties defined in this section
     */
    public Set<String> getProperties() {
        return properties.keySet();
    }

    /**
     * Gets the values for the specified property
     *
     * @param property A property in this section
     * @return The values for the specified property
     */
    public List<String> getValues(String property) {
        if (!properties.containsKey(property))
            return new ArrayList<>();
        return properties.get(property);
    }

    /**
     * Gets the first value for the specified property
     *
     * @param property A property in this section
     * @return The first value for the specified property, or null if there is none
     */
    public String getValue(String property) {
        if (!properties.containsKey(property))
            return null;
        List<String> values = properties.get(property);
        if (values.isEmpty())
            return null;
        return values.get(0);
    }

    /**
     * Adds the specified property - value pair to this section
     *
     * @param property A property
     * @param value    A value to associate to the property
     */
    public void addValue(String property, String value) {
        if (!properties.containsKey(property))
            properties.put(property, new ArrayList<String>());
        properties.get(property).add(value);
    }

    /**
     * Exports this section to the specified writer
     *
     * @param writer A writer
     * @throws IOException when writing fails
     */
    public void save(Writer writer) throws IOException {
        List<String> keys = new ArrayList<>(properties.keySet());
        Collections.sort(keys);
        if (name != null) {
            writer.write("[");
            writer.write(name);
            writer.write("]");
            writer.write(System.lineSeparator());
        }
        for (String option : keys) {
            for (String value : properties.get(option)) {
                writer.write(option + " = " + value + System.lineSeparator());
            }
        }
    }
}
