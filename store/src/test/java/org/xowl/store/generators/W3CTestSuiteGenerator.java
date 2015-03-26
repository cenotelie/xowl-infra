/**********************************************************************
 * Copyright (c) 2014 Laurent Wouters and others
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
package org.xowl.store.generators;

import org.junit.Assert;
import org.xowl.store.TestLogger;
import org.xowl.store.Vocabulary;
import org.xowl.store.W3CTestSuite;
import org.xowl.store.loaders.Loader;
import org.xowl.store.loaders.RDFLoaderResult;
import org.xowl.store.loaders.TurtleLoader;
import org.xowl.store.rdf.*;

import java.io.*;
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
        generate("/ntriples/manifest.ttl");
        System.out.println("==== NQuads ====");
        generate("/nquads/manifest.ttl");
        System.out.println("==== Turtle ====");
        generate("/turtle/manifest.ttl");
        System.out.println("==== RDF/XML ====");
        generate("/rdfxml/manifest.ttl");
    }

    /**
     * Generates the tests for the specified manifest
     *
     * @param manifest The path to a manifest
     */
    public void generate(String manifest) {
        RDFStore store = null;
        try {
            store = new RDFStore();
        } catch (IOException ex) {
            // do not handle
        }
        TestLogger logger = new TestLogger();

        InputStream stream = W3CTestSuite.class.getResourceAsStream(manifest);
        Reader reader = null;
        try {
            reader = new InputStreamReader(stream, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            Assert.fail(ex.toString());
        }
        Loader loader = new TurtleLoader(store);
        RDFLoaderResult input = loader.loadRDF(logger, reader, BASE_LOCATION);
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

        Iterator<Quad> triples = store.getAll(null, store.getNodeIRI(MF + "entries"), null);
        Iterator<Node> tests = store.getList((SubjectNode) triples.next().getObject());
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
    }

    /**
     * Generates an evaluation test
     *
     * @param graph       The current RDF graph
     * @param resourceURI The UTI of the manifest resource
     * @param entry       The entry node
     * @param syntax      The syntax to be tested
     */
    private void generateTestEval(RDFStore graph, String resourceURI, SubjectNode entry, String syntax) {
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
    private void generateTestPositiveSyntax(RDFStore graph, String resourceURI, SubjectNode entry, String syntax) {
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
    private void generateTestNegativeSyntax(RDFStore graph, String resourceURI, SubjectNode entry, String syntax) {
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
    private Node getValue(RDFStore graph, SubjectNode node, String property) {
        Iterator<Quad> entries = graph.getAll(node, graph.getNodeIRI(property), null);
        if (!entries.hasNext())
            return null;
        return entries.next().getObject();
    }
}
