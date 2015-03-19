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
package org.xowl.store;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents a mapper of IRIs to physical resources
 *
 * @author Laurent Wouters
 */
public class IRIMapper {
    /**
     * Represents an entry in this mapper
     */
    private static class Entry {
        /**
         * The pattern to match
         */
        private Pattern pattern;
        /**
         * The corresponding physical template
         */
        private String physical;

        /**
         * Initializes this entry
         *
         * @param pattern  The pattern to match
         * @param physical The corresponding physical template
         */
        public Entry(Pattern pattern, String physical) {
            this.pattern = pattern;
            this.physical = physical;

        }

        /**
         * Determines whether the specified iri matches this entry
         *
         * @param iri An iri
         * @return true if this entry matches the iri
         */
        public boolean matches(String iri) {
            return pattern.matcher(iri).matches();
        }

        /**
         * Gets the physical pattern for the specified iri
         *
         * @param iri An iri
         * @return The corresponding physical location
         */
        public String getLocationFor(String iri) {
            Matcher matcher = pattern.matcher(iri);
            if (!matcher.matches())
                return null;
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i != physical.length(); i++) {
                char c = physical.charAt(i);
                if (c == '\\' && i != physical.length() - 1) {
                    i++;
                    c = physical.charAt(i);
                    if (c == '\\') {
                        builder.append(c);
                    } else {
                        int index = Integer.parseInt(Character.toString(c));
                        builder.append(matcher.group(index));
                    }
                } else {
                    builder.append(c);
                }
            }
            return builder.toString();
        }
    }

    /**
     * The entries in this mapper
     */
    private List<Entry> entries;

    /**
     * Initializes this mapper
     */
    public IRIMapper() {
        entries = new ArrayList<>();
    }

    /**
     * Adds the specified simple map
     *
     * @param iri      The iri prefix to match
     * @param location The physical pattern's root
     */
    public void addSimpleMap(String iri, String location) {
        Pattern pattern = Pattern.compile(iri);
        entries.add(new Entry(pattern, location));
    }

    /**
     * Adds the specified map matched by a prefix
     *
     * @param prefix   The iri prefix to match
     * @param location The physical pattern's root
     */
    public void addPrefixMap(String prefix, String location) {
        Pattern pattern = Pattern.compile(prefix + "(.*)");
        entries.add(new Entry(pattern, location + "\1"));
    }

    /**
     * Adds the specified map matched using a regular expression
     *
     * @param pattern  The regular expression to be matched by an iri
     * @param location The template for the physical location
     */
    public void addRegexpMap(String pattern, String location) {
        entries.add(new Entry(Pattern.compile(pattern), location));
    }

    /**
     * Gets the physical pattern for the specified iri
     *
     * @param iri An iri
     * @return The corresponding physical pattern
     */
    public String get(String iri) {
        for (Entry entry : entries) {
            if (entry.matches(iri))
                return entry.getLocationFor(iri);
        }
        return null;
    }


    /**
     * Returns a IRI mapper with the default mappings
     *
     * @return A default IRI mapper
     */
    public static IRIMapper getDefault() {
        IRIMapper mapper = new IRIMapper();
        // map the owl2, rdf and rdfs ontologies to the embarked one
        mapper.addSimpleMap("http://www.w3.org/2002/07/owl", "resource:///org/xowl/store/base/owl2.ttl");
        mapper.addSimpleMap("http://www.w3.org/1999/02/22-rdf-syntax-ns", "resource:///org/xowl/store/base/rdf.ttl");
        mapper.addSimpleMap("http://www.w3.org/2000/01/rdf-schema", "resource:///org/xowl/store/base/rdfs.ttl");
        // map the xOWL abstract syntax
        mapper.addRegexpMap("http://xowl.org/lang/(.*)", "resource:///org/xowl/lang/defs/\\1.fs");
        // map the OWL2 RL reasoning rules
        mapper.addRegexpMap("http://xowl.org/store/rules/(.*)", "resource:///org/xowl/store/rules/\\1.rdft");
        return mapper;
    }
}
