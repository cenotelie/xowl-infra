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
package org.xowl.infra.store.loaders;

import fr.cenotelie.commons.utils.IOUtils;
import fr.cenotelie.commons.utils.logging.SinkLogger;
import org.junit.Assert;
import org.xowl.infra.store.Vocabulary;
import org.xowl.infra.store.rdf.*;
import org.xowl.infra.store.storage.QuadStore;
import org.xowl.infra.store.storage.QuadStoreFactory;
import org.xowl.infra.store.storage.UnsupportedNodeType;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Iterator;

/**
 * Represents a generator of tests based on a W3C manifest
 *
 * @author Laurent Wouters
 */
public class W3CTestSuiteGenerator {
    /**
     * Base URI for the manifests
     */
    protected static final String BASE_LOCATION = "http://www.w3.org/2013/Tests/";
    /**
     * The Manifest namespace
     */
    private static final String MF = "http://www.w3.org/2001/sw/DataAccess/tests/test-manifest#";
    /**
     * The RDF Tests namespace
     */
    private static final String RDFT = "http://www.w3.org/ns/rdftest#";

    /**
     * Generates all the tests
     */
    public void generate() {
        System.out.println("==== NTriples ====");
        generate("/org/w3c/ntriples/manifest.ttl");
        System.out.println("==== NQuads ====");
        generate("/org/w3c/nquads/manifest.ttl");
        System.out.println("==== Turtle ====");
        generate("/org/w3c/turtle/manifest.ttl");
        System.out.println("==== RDF/XML ====");
        generate("/org/w3c/rdfxml/manifest.ttl");
        System.out.println("==== TriG ====");
        generate("/org/w3c/trig/manifest.ttl");
    }

    /**
     * Generates the tests for the specified manifest
     *
     * @param manifest The path to a manifest
     */
    public void generate(String manifest) {
        QuadStore store = QuadStoreFactory.create().make();
        SinkLogger logger = new SinkLogger();

        InputStream stream = W3CTestSuite.class.getResourceAsStream(manifest);
        Reader reader = reader = new InputStreamReader(stream, IOUtils.CHARSET);
        Loader loader = new TurtleLoader(store);
        RDFLoaderResult input = loader.loadRDF(logger, reader, BASE_LOCATION, BASE_LOCATION);
        try {
            reader.close();
        } catch (IOException ex) {
            Assert.fail("Failed to close the resource " + manifest);
        }

        try {
            for (Quad quad : input.getQuads())
                store.add(quad);
        } catch (UnsupportedNodeType ex) {
            Assert.fail("Unsupported quad");
        }

        try {
            Iterator<Quad> triples = store.getAll(null, store.getIRINode(MF + "entries"), null);
            Iterator<Node> tests = new ListIterator(store, triples.next().getObject());
            while (tests.hasNext()) {
                SubjectNode test = (SubjectNode) tests.next();
                String type = ((IRINode) getValue(store, test, Vocabulary.rdfType)).getIRIValue();
                type = type.substring(RDFT.length());
                if (type.endsWith("PositiveSyntax")) {
                    String syntax = type.substring(4, type.length() - 14);
                    generateTestPositiveSyntax(store, BASE_LOCATION, test, syntax);
                } else if (type.endsWith("NegativeSyntax")) {
                    String syntax = type.substring(4, type.length() - 14);
                    generateTestNegativeSyntax(store, BASE_LOCATION, test, syntax);
                } else if (type.endsWith("NegativeEval")) {
                    String syntax = type.substring(4, type.length() - 12);
                    generateTestNegativeSyntax(store, BASE_LOCATION, test, syntax);
                } else if (type.endsWith("Eval")) {
                    String syntax = type.substring(4, type.length() - 4);
                    generateTestEval(store, BASE_LOCATION, test, syntax);
                }
            }
        } catch (UnsupportedNodeType exception) {
            Assert.fail(exception.getMessage());
        }
    }

    /**
     * Generates an evaluation test
     *
     * @param graph       The current RDF graph
     * @param resourceURI The UTI of the manifest resource
     * @param entry       The entry node
     * @param syntax      The syntax to be tested
     */
    private void generateTestEval(QuadStore graph, String resourceURI, SubjectNode entry, String syntax) {
        String name = ((LiteralNode) getValue(graph, entry, MF + "name")).getLexicalValue();
        name = name.replace("-", "_");

        String action = ((IRINode) getValue(graph, entry, MF + "action")).getIRIValue();
        action = action.substring(resourceURI.length());

        String result = ((IRINode) getValue(graph, entry, MF + "result")).getIRIValue();
        result = result.substring(resourceURI.length());

        System.out.println("@Test public void test_" + name + "() { test" + syntax + "Eval(\"" + action + "\", \"" + result + "\"); }");
    }

    /**
     * Generates an positive syntax test
     *
     * @param graph       The current RDF graph
     * @param resourceURI The UTI of the manifest resource
     * @param entry       The entry node
     * @param syntax      The syntax to be tested
     */
    private void generateTestPositiveSyntax(QuadStore graph, String resourceURI, SubjectNode entry, String syntax) {
        String name = ((LiteralNode) getValue(graph, entry, MF + "name")).getLexicalValue();
        name = name.replace("-", "_");

        String action = ((IRINode) getValue(graph, entry, MF + "action")).getIRIValue();
        action = action.substring(resourceURI.length());

        System.out.println("@Test public void test_" + name + "() { test" + syntax + "PositiveSyntax(\"" + action + "\"); }");
    }

    /**
     * Generates an negative syntax test
     *
     * @param graph       The current RDF graph
     * @param resourceURI The UTI of the manifest resource
     * @param entry       The entry node
     * @param syntax      The syntax to be tested
     */
    private void generateTestNegativeSyntax(QuadStore graph, String resourceURI, SubjectNode entry, String syntax) {
        String name = ((LiteralNode) getValue(graph, entry, MF + "name")).getLexicalValue();
        name = name.replace("-", "_");

        String action = ((IRINode) getValue(graph, entry, MF + "action")).getIRIValue();
        action = action.substring(resourceURI.length());

        System.out.println("@Test public void test_" + name + "() { test" + syntax + "NegativeSyntax(\"" + action + "\"); }");
    }

    /**
     * Gets the value of the specified property for the given subject node
     *
     * @param graph    The parent graph
     * @param node     A subject node
     * @param property A property
     * @return The corresponding value
     */
    private Node getValue(QuadStore graph, SubjectNode node, String property) {
        try {
            Iterator<Quad> entries = graph.getAll(node, graph.getIRINode(property), null);
            if (!entries.hasNext())
                return null;
            return entries.next().getObject();
        } catch (UnsupportedNodeType exception) {
            exception.printStackTrace();
            return null;
        }
    }
}
