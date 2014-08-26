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
package org.xowl.store;

import org.junit.Test;
import org.xowl.lang.owl2.Ontology;
import org.xowl.store.loaders.Loader;
import org.xowl.store.loaders.TurtleLoader;
import org.xowl.store.rdf.*;
import org.xowl.store.voc.RDF;

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
public class Generator {
    /**
     * The Manifest namespace
     */
    private static final String mf = "http://www.w3.org/2001/sw/DataAccess/tests/test-manifest#";
    /**
     * The RDF Tests namespace
     */
    private static final String rdft = "http://www.w3.org/ns/rdftest#";

    /**
     * The RDF graph to use
     */
    private RDFGraph graph;
    /**
     * The logger to use
     */
    private TestLogger logger;

    /**
     * Initializes this generator
     */
    public Generator() {
        try {
            graph = new RDFGraph();
        } catch (IOException ex) {
            // do not handle
        }
        logger = new TestLogger();
    }

    /**
     * Generates all the tests
     */
    public void generate() {
        try {
            generate("/turtle/manifest.ttl");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Generates the tests for the specified manifest
     *
     * @param manifest The path to a manifest
     * @throws IOException When the manifest cannot be accessed
     */
    public void generate(String manifest) throws IOException {
        logger.reset();

        Loader loader = new TurtleLoader(graph);
        InputStream stream = Generator.class.getResourceAsStream(manifest);
        Reader reader = new InputStreamReader(stream);
        Ontology ontology = loader.load(logger, reader);
        reader.close();

        try {
            Iterator<Triple> entries = graph.getAll(null, graph.getNodeIRI(mf + "entries"), null);
            while (entries.hasNext()) {
                Triple entry = entries.next();

                SubjectNode test = (SubjectNode) entry.getObject();
                String type = ((IRINode) getValue(test, RDF.rdfType)).getIRIValue();
                type = type.substring(rdft.length());
                if ("TestTurtleEval".equals(type))
                    generateTestTurtleEval(ontology, test);
                else if ("TestTurtlePositiveSyntax".equals(type))
                    generateTestTurtlePositiveSyntax(ontology, test);
                else if ("TestTurtleNegativeSyntax".equals(type))
                    generateTestTurtleNegativeSyntax(ontology, test);
            }
        } catch (UnsupportedNodeType ex) {
            // cannot happen
        }
    }

    /**
     * Generates a Turtle evaluation test
     *
     * @param ontology The parent ontology
     * @param entry    The test entry
     */
    private void generateTestTurtleEval(Ontology ontology, SubjectNode entry) {
        String name = ((LiteralNode) getValue(entry, mf + "name")).getLexicalValue();
        name = name.replace("-", "_");

        String action = ((IRINode) getValue(entry, mf + "action")).getIRIValue();
        action = action.substring(ontology.getHasIRI().getHasValue().length());

        String result = ((IRINode) getValue(entry, mf + "result")).getIRIValue();
        result = result.substring(ontology.getHasIRI().getHasValue().length());

        System.out.println("@Test public void test_" + name + "() { testTurtleEval(\"" + action + "\", \"" + result + "\"); }");
    }

    /**
     * Generates a positive syntax test
     *
     * @param ontology The parent ontology
     * @param entry    The test entry
     */
    private void generateTestTurtlePositiveSyntax(Ontology ontology, SubjectNode entry) {
        String name = ((LiteralNode) getValue(entry, mf + "name")).getLexicalValue();
        name = name.replace("-", "_");

        String action = ((IRINode) getValue(entry, mf + "action")).getIRIValue();
        action = action.substring(ontology.getHasIRI().getHasValue().length());

        System.out.println("@Test public void test_" + name + "() { testTurtlePositiveSyntax(\"" + action + "\"); }");
    }

    /**
     * Generates a negative syntax test
     *
     * @param ontology The parent ontology
     * @param entry    The test entry
     */
    private void generateTestTurtleNegativeSyntax(Ontology ontology, SubjectNode entry) {
        String name = ((LiteralNode) getValue(entry, mf + "name")).getLexicalValue();
        name = name.replace("-", "_");

        String action = ((IRINode) getValue(entry, mf + "action")).getIRIValue();
        action = action.substring(ontology.getHasIRI().getHasValue().length());

        System.out.println("@Test public void test_" + name + "() { testTurtleNegativeSyntax(\"" + action + "\"); }");
    }

    /**
     * Gets the value of the specified property for the given subject node
     *
     * @param node     A subject node
     * @param property A property
     * @return The corresponding value
     */
    private Node getValue(SubjectNode node, String property) {
        try {
            Iterator<Triple> entries = graph.getAll(node, graph.getNodeIRI(property), null);
            if (!entries.hasNext())
                return null;
            return entries.next().getObject();
        } catch (UnsupportedNodeType ex) {
            // cannot happen
        }
        return null;
    }
}
