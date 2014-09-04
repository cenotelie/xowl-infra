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

package org.xowl.store.loaders;

import java.net.URI;

/**
 * Utilities for the loaders
 *
 * @author Laurent Zouters
 */
public class Utils {
    /**
     * Default URIs for the loaded ontologies
     */
    private static final String DEFAULT_GRAPH_URIS = "http://xowl.org/store/rdfgraphs/";

    /**
     * Creates a new ontology based on the default URI prefix
     *
     * @return The new ontology
     */
    public static String createNewOntology() {
        java.util.Random rand = new java.util.Random();
        return DEFAULT_GRAPH_URIS + Integer.toHexString(rand.nextInt());
    }


    /**
     * Strings containing the escaped glyphs
     */
    private static final String ESCAPED_GLYHPS = "\\'\"_~.!$&()*+,;=/?#@%-";

    /**
     * Translates the specified string into a new one by replacing the escape sequences by their value
     *
     * @param content A string that can contain escape sequences
     * @return The translated string with the escape sequences replaced by their value
     */
    public static String unescape(String content) {
        char[] buffer = new char[content.length()];
        int next = 0;
        for (int i = 0; i != content.length(); i++) {
            char c = content.charAt(i);
            if (c != '\\') {
                buffer[next++] = c;
            } else {
                char n = content.charAt(i + 1);
                if (n == 't') {
                    buffer[next++] = '\t';
                    i++;
                } else if (n == 'b') {
                    buffer[next++] = '\b';
                    i++;
                } else if (n == 'n') {
                    buffer[next++] = '\n';
                    i++;
                } else if (n == 'r') {
                    buffer[next++] = '\r';
                    i++;
                } else if (n == 'f') {
                    buffer[next++] = '\f';
                    i++;
                } else if (n == 'u') {
                    int codepoint = Integer.parseInt(content.substring(i + 2, i + 6), 16);
                    String str = new String(new int[]{codepoint}, 0, 1);
                    for (int j = 0; j != str.length(); j++)
                        buffer[next++] = str.charAt(j);
                    i += 5;
                } else if (n == 'U') {
                    int codepoint = Integer.parseInt(content.substring(i + 2, i + 10), 16);
                    String str = new String(new int[]{codepoint}, 0, 1);
                    for (int j = 0; j != str.length(); j++)
                        buffer[next++] = str.charAt(j);
                    i += 9;
                } else if (ESCAPED_GLYHPS.contains(Character.toString(n))) {
                    buffer[next++] = n;
                    i++;
                }
            }
        }
        return new String(buffer, 0, next);
    }

    /**
     * Resolves and normalizes the specified IRI
     *
     * @param resource The URI of the parent enclosing document
     * @param base     The current base URI, or <code>null</code> if none is defined
     * @param iri      The IRI to  resolve and normalize
     * @return The resolved and normalized IRI
     */
    public static String normalizeIRI(String resource, String base, String iri) {
        if (iri == null || iri.isEmpty())
            return (base != null ? base : resource);
        iri = unescape(iri);
        URI uriBase = URI.create(base != null ? base : resource);
        URI uri = uriBase.resolve(iri);
        uri = uri.normalize();
        return uri.toString();
    }
}
