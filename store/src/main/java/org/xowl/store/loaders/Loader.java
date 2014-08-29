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

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xowl.hime.redist.ParseResult;
import org.xowl.lang.owl2.IRI;
import org.xowl.lang.owl2.Ontology;
import org.xowl.utils.Logger;

import java.io.Reader;
import java.net.URI;
import java.util.Iterator;

/**
 * Represents a loader for a store
 *
 * @author Laurent Wouters
 */
public abstract class Loader {
    /**
     * Default URIs for the loaded ontologies
     */
    protected static final String DEFAULT_GRAPH_URIS = "http://xowl.org/store/rdfgraphs/";
    /**
     * Strings containing the escaped glyphs
     */
    protected static final String escapedGlyhps = "\\'\"_~.!$&()*+,;=/?#@%-";

    /**
     * Creates a new ontology based on the default URI prefix
     *
     * @return The new ontology
     */
    protected static Ontology createNewOntology() {
        java.util.Random rand = new java.util.Random();
        String value = DEFAULT_GRAPH_URIS + Integer.toHexString(rand.nextInt());
        IRI iri = new IRI();
        iri.setHasValue(value);
        Ontology ontology = new Ontology();
        ontology.setHasIRI(iri);
        return ontology;
    }

    /**
     * Gets an iterator over the Element children of the specified XML node
     *
     * @param node A XML node
     * @return An iterator over the Element children
     */
    protected static Iterator<Element> getXMLChildren(Node node) {
        final NodeList list = node.getChildNodes();
        return new Iterator<Element>() {
            int index = getNext(0);

            private int getNext(int start) {
                for (int i = start; i != list.getLength(); i++)
                    if (list.item(i).getNodeType() == Node.ELEMENT_NODE)
                        return i;
                return list.getLength();
            }

            @Override
            public boolean hasNext() {
                return (index != list.getLength());
            }

            @Override
            public Element next() {
                Element result = (Element) list.item(index);
                index = getNext(index + 1);
                return result;
            }
        };
    }

    /**
     * Translates the specified string into a new one by replacing the escape sequences by their value
     *
     * @param content A string that can contain escape sequences
     * @return The translated string with the escape sequences replaced by their value
     */
    protected String unescape(String content) {
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
                } else if (escapedGlyhps.contains(Character.toString(n))) {
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
    protected String normalizeIRI(String resource, String base, String iri) {
        iri = unescape(iri);
        URI uriBase = URI.create(base != null ? base : resource);
        URI uri = uriBase.resolve(iri);
        uri = uri.normalize();
        return uri.toString();
    }

    /**
     * Parses the specified input
     *
     * @param logger The logger to use
     * @param reader The input to parse
     * @return The result of the parsing operation
     */
    public abstract ParseResult parse(Logger logger, Reader reader);

    /**
     * Loads data into the store
     *
     * @param logger The logger to use
     * @param reader The resource's reader
     * @param uri    The resource's URI
     * @return The ontology containing the loaded data
     */
    public abstract Ontology load(Logger logger, Reader reader, String uri);
}
