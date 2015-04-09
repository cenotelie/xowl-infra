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
import java.util.Collections;
import java.util.Comparator;
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
     * The default priority of simple (exact matches)
     */
    public static final int PRIORITY_SIMPLE_MATCH = 100;
    /**
     * The default priority of prefix matches
     */
    public static final int PRIORITY_PREFIX_MATCH = 50;
    /**
     * The default priority of regular expression matches
     */
    public static final int PRIORITY_REGEXP_MATCH = 20;
    /**
     * The default priority of identity HTTP matches (defer to downloading the resource)
     */
    public static final int PRIORITY_HTTP_MATCH = 0;

    /**
     * Represents an entry in this mapper
     */
    private static abstract class BaseEntry {
        /**
         * The priority of this entry
         */
        protected final int priority;
        /**
         * The corresponding physical template
         */
        protected final String physical;

        /**
         * Initializes this entry
         *
         * @param priority The priority of this entry
         * @param physical The corresponding physical template
         */
        protected BaseEntry(int priority, String physical) {
            this.priority = priority;
            this.physical = physical;
        }

        /**
         * Gets the priority of this matcher, the greater the number the more priority it has
         *
         * @return The priority of this matcher, as a positive integer
         */
        public final int getPriority() {
            return priority;
        }

        /**
         * Determines whether the specified iri matches this entry
         *
         * @param iri An iri
         * @return true if this entry matches the iri
         */
        public abstract boolean matches(String iri);

        /**
         * Gets the physical pattern for the specified iri
         *
         * @param iri An iri
         * @return The corresponding physical location
         */
        public abstract String getLocationFor(String iri);
    }

    /**
     * Represents a simple entry in this mapper matching a single IRI for a single resource
     */
    private static class SimpleEntry extends BaseEntry {
        /**
         * The iri to match
         */
        protected final String iri;

        /**
         * Initializes this entry
         *
         * @param iri      The iri to match
         * @param physical The corresponding physical template
         */
        public SimpleEntry(String iri, String physical) {
            super(PRIORITY_SIMPLE_MATCH, physical);
            this.iri = iri;
        }

        /**
         * Initializes this entry
         *
         * @param priority The priority of this entry
         * @param iri      The iri to match
         * @param physical The corresponding physical template
         */
        public SimpleEntry(int priority, String iri, String physical) {
            super(priority, physical);
            this.iri = iri;
        }

        @Override
        public boolean matches(String iri) {
            return this.iri.equals(iri);
        }

        @Override
        public String getLocationFor(String iri) {
            return physical;
        }
    }

    /**
     * Represents an entry in this mapper matching a prefix IRI for a set of resources
     */
    private static class PrefixEntry extends BaseEntry {
        /**
         * The iri prefix to match
         */
        protected final String prefix;

        /**
         * Initializes this entry
         *
         * @param prefix   The iri to match
         * @param physical The corresponding physical template
         */
        public PrefixEntry(String prefix, String physical) {
            super(PRIORITY_PREFIX_MATCH, physical);
            this.prefix = prefix;
        }

        /**
         * Initializes this entry
         *
         * @param priority The priority of this entry
         * @param prefix   The iri to match
         * @param physical The corresponding physical template
         */
        public PrefixEntry(int priority, String prefix, String physical) {
            super(priority, physical);
            this.prefix = prefix;
        }

        @Override
        public boolean matches(String iri) {
            return iri.startsWith(prefix);
        }

        @Override
        public String getLocationFor(String iri) {
            return physical + iri.substring(physical.length());
        }
    }

    /**
     * Represents an entry in this mapper matching IRIs with a regular expression
     */
    private static class RegExpEntry extends BaseEntry {
        /**
         * The pattern to match
         */
        protected final Pattern pattern;

        /**
         * Initializes this entry
         *
         * @param pattern  The pattern to match
         * @param physical The corresponding physical template
         */
        public RegExpEntry(String pattern, String physical) {
            super(PRIORITY_REGEXP_MATCH, physical);
            this.pattern = Pattern.compile(pattern);
        }

        /**
         * Initializes this entry
         *
         * @param priority The priority of this entry
         * @param pattern  The pattern to match
         * @param physical The corresponding physical template
         */
        public RegExpEntry(int priority, String pattern, String physical) {
            super(priority, physical);
            this.pattern = Pattern.compile(pattern);
        }

        @Override
        public boolean matches(String iri) {
            return pattern.matcher(iri).matches();
        }

        @Override
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
     * Represents a HTTP matching entry mapping to identity IRIs
     */
    private static class HTTPEntry extends BaseEntry {
        /**
         * Initializes this entry
         */
        public HTTPEntry() {
            super(PRIORITY_HTTP_MATCH, null);
        }

        /**
         * Initializes this entry
         *
         * @param priority The priority of this entry
         */
        public HTTPEntry(int priority) {
            super(priority, null);
        }

        @Override
        public boolean matches(String iri) {
            return iri.startsWith("http://");
        }

        @Override
        public String getLocationFor(String iri) {
            return iri;
        }
    }

    /**
     * The entries in this mapper
     */
    private final List<BaseEntry> entries;

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
        entries.add(new SimpleEntry(iri, location));
    }

    /**
     * Adds the specified simple map
     *
     * @param priority The priority of this mapping
     * @param iri      The iri prefix to match
     * @param location The physical pattern's root
     */
    public void addSimpleMap(int priority, String iri, String location) {
        entries.add(new SimpleEntry(priority, iri, location));
    }

    /**
     * Adds the specified map matched by a prefix
     *
     * @param prefix   The iri prefix to match
     * @param location The physical pattern's root
     */
    public void addPrefixMap(String prefix, String location) {
        entries.add(new PrefixEntry(prefix, location));
    }

    /**
     * Adds the specified map matched by a prefix
     *
     * @param priority The priority of this mapping
     * @param prefix   The iri prefix to match
     * @param location The physical pattern's root
     */
    public void addPrefixMap(int priority, String prefix, String location) {
        entries.add(new PrefixEntry(priority, prefix, location));
    }

    /**
     * Adds the specified map matched using a regular expression
     *
     * @param pattern  The regular expression to be matched by an iri
     * @param location The template for the physical location
     */
    public void addRegexpMap(String pattern, String location) {
        entries.add(new RegExpEntry(pattern, location));
    }

    /**
     * Adds the specified map matched using a regular expression
     *
     * @param priority The priority of this mapping
     * @param pattern  The regular expression to be matched by an iri
     * @param location The template for the physical location
     */
    public void addRegexpMap(int priority, String pattern, String location) {
        entries.add(new RegExpEntry(priority, pattern, location));
    }

    /**
     * Adds a mapping of HTTP URIs to themselves
     */
    public void addHTTPMap() {
        entries.add(new HTTPEntry());
    }

    /**
     * Adds a mapping of HTTP URIs to themselves
     *
     * @param priority The priority of this mapping
     */
    public void addHTTPMap(int priority) {
        entries.add(new HTTPEntry(priority));
    }

    /**
     * Gets the physical pattern for the specified iri
     *
     * @param iri An iri
     * @return The corresponding physical pattern
     */
    public String get(String iri) {
        List<BaseEntry> matches = new ArrayList<>(entries.size());
        for (BaseEntry entry : entries) {
            if (entry.matches(iri)) {
                matches.add(entry);
            }
        }
        if (matches.isEmpty())
            return null;
        Collections.sort(matches, new Comparator<BaseEntry>() {
            @Override
            public int compare(BaseEntry e1, BaseEntry e2) {
                return e2.getPriority() - e1.getPriority();
            }
        });
        return matches.get(0).getLocationFor(iri);
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
        // map HTTP protocol to itself
        mapper.addHTTPMap();
        return mapper;
    }
}
