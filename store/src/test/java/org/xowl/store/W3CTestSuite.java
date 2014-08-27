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

import org.junit.Assert;
import org.xowl.lang.owl2.Ontology;
import org.xowl.store.loaders.Loader;
import org.xowl.store.loaders.NTriplesLoader;
import org.xowl.store.loaders.RDFXMLLoader;
import org.xowl.store.loaders.TurtleLoader;
import org.xowl.store.rdf.*;

import java.io.*;
import java.util.*;

/**
 * Represents a base class for all W3C test suites
 *
 * @author Laurent Wouters
 */
public abstract class W3CTestSuite {

    /**
     * Tests the evaluation of a resource
     *
     * @param expectedResource Path to the expected resource
     * @param expectedURI      Expected resource's URI
     * @param testedResource   Path to the tested resource
     * @param testedURI        Tested resource's URI
     */
    protected void testEval(String expectedResource, String expectedURI, String testedResource, String testedURI) {
        RDFGraph graph = null;
        try {
            graph = new RDFGraph();
        } catch (IOException ex) {
            // do not handle
        }
        TestLogger logger = new TestLogger();

        InputStream stream = W3CTestSuite.class.getResourceAsStream(expectedResource);
        Reader reader = null;
        try {
            reader = new InputStreamReader(stream, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            Assert.fail(ex.toString());
        }
        Loader loader = null;
        if (expectedResource.endsWith(".nt"))
            loader = new NTriplesLoader(graph);
        else if (expectedResource.endsWith(".ttl"))
            loader = new TurtleLoader(graph);
        else if (expectedResource.endsWith(".rdf"))
            loader = new RDFXMLLoader(graph);
        if (loader == null)
            Assert.fail("Failed to recognize resource " + expectedResource);
        Ontology expectedOntology = loader.load(logger, reader, expectedURI);
        Assert.assertFalse("Failed to parse resource " + expectedResource, logger.isOnError());
        Assert.assertNotNull("Failed to load resource " + expectedResource, expectedOntology);
        try {
            reader.close();
        } catch (IOException ex) {
            Assert.fail("Failed to close the resource " + expectedResource);
        }

        stream = W3CTestSuite.class.getResourceAsStream(testedResource);
        try {
            reader = new InputStreamReader(stream, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            Assert.fail(ex.toString());
        }
        loader = null;
        if (testedResource.endsWith(".nt"))
            loader = new NTriplesLoader(graph);
        else if (testedResource.endsWith(".ttl"))
            loader = new TurtleLoader(graph);
        else if (testedResource.endsWith(".rdf"))
            loader = new RDFXMLLoader(graph);
        if (loader == null)
            Assert.fail("Failed to recognize resource " + testedResource);
        Ontology testedOntology = loader.load(logger, reader, testedURI);
        Assert.assertFalse("Failed to parse resource " + testedResource, logger.isOnError());
        Assert.assertNotNull("Failed to load resource " + testedResource, testedOntology);
        try {
            reader.close();
        } catch (IOException ex) {
            Assert.fail("Failed to close the resource " + testedResource);
        }

        matches(graph, expectedOntology, testedOntology);
    }

    /**
     * Tests that the specified resource is correctly loaded
     *
     * @param physicalResource The physical path to the resource
     * @param uri              The resource's URI
     */
    protected void testPositiveSyntax(String physicalResource, String uri) {
        RDFGraph graph = null;
        try {
            graph = new RDFGraph();
        } catch (IOException ex) {
            // do not handle
        }
        TestLogger logger = new TestLogger();
        Loader loader = null;
        if (physicalResource.endsWith(".nt"))
            loader = new NTriplesLoader(graph);
        else if (physicalResource.endsWith(".ttl"))
            loader = new TurtleLoader(graph);
        else if (physicalResource.endsWith(".rdf"))
            loader = new RDFXMLLoader(graph);
        if (loader == null)
            Assert.fail("Failed to recognize resource " + physicalResource);

        InputStream stream = W3CTestSuite.class.getResourceAsStream(physicalResource);
        Reader reader = null;
        try {
            reader = new InputStreamReader(stream, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            Assert.fail(ex.toString());
        }

        Ontology result = loader.load(logger, reader, uri);
        Assert.assertFalse("Failed to parse resource " + physicalResource, logger.isOnError());
        Assert.assertNotNull("Failed to load resource " + physicalResource, result);

        try {
            reader.close();
        } catch (IOException ex) {
            Assert.fail("Failed to close the resource " + physicalResource);
        }
    }

    /**
     * Tests that the specified resource is not correctly loaded
     *
     * @param physicalResource The physical path to the resource
     * @param uri              The resource's URI
     */
    protected void testNegativeSyntax(String physicalResource, String uri) {
        RDFGraph graph = null;
        try {
            graph = new RDFGraph();
        } catch (IOException ex) {
            // do not handle
        }
        TestLogger logger = new TestLogger();
        Loader loader = null;
        if (physicalResource.endsWith(".nt"))
            loader = new NTriplesLoader(graph);
        else if (physicalResource.endsWith(".ttl"))
            loader = new TurtleLoader(graph);
        else if (physicalResource.endsWith(".rdf"))
            loader = new RDFXMLLoader(graph);
        if (loader == null)
            Assert.fail("Failed to recognize resource " + physicalResource);

        InputStream stream = W3CTestSuite.class.getResourceAsStream(physicalResource);
        Reader reader = null;
        try {
            reader = new InputStreamReader(stream, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            Assert.fail(ex.toString());
        }

        Ontology result = loader.load(logger, reader, uri);
        Assert.assertNull("Mistakenly reported success of loading " + physicalResource, result);

        try {
            reader.close();
        } catch (IOException ex) {
            Assert.fail("Failed to close the resource " + physicalResource);
        }
    }

    /**
     * Determines whether two ontologies in a RDF graph contains the same triples
     *
     * @param graph    A RDF graph
     * @param expected The ontology containing the expected triples
     * @param tested   The ontology containing the tested triples
     */
    protected void matches(RDFGraph graph, Ontology expected, Ontology tested) {
        List<Triple> triplesExpected = new ArrayList<>();
        Iterator<Triple> iterator = graph.getAll(expected);
        while (iterator.hasNext()) {
            triplesExpected.add(iterator.next());
        }

        List<Triple> triplesTested = new ArrayList<>();
        iterator = graph.getAll(tested);
        while (iterator.hasNext()) {
            triplesTested.add(iterator.next());
        }

        matches(triplesExpected, triplesTested);
    }

    /**
     * Tests whether two sets of triples describe the same graph
     *
     * @param expected The expected set of triples
     * @param tested   The tested set of triples
     */
    protected void matches(List<Triple> expected, List<Triple> tested) {
        Map<BlankNode, BlankNode> blanks = new HashMap<>();
        for (int i = 0; i != expected.size(); i++) {
            Triple triple = expected.get(i);
            if (triple.getSubject().getNodeType() != BlankNode.TYPE) {
                // ignore blank nodes at this time
                boolean found = false;
                for (Triple potential : tested) {
                    if (sameTriple(triple, potential, blanks)) {
                        found = true;
                        tested.remove(potential);
                        break;
                    }
                }
                if (found) {
                    expected.remove(i);
                    i--;
                } else {
                    Assert.fail("Expected triple not produced: " + triple.toString());
                }
            }
        }

        int size = expected.size() + 1;
        while (size != expected.size()) {
            // while no more modifications
            size = expected.size();
            for (int i = 0; i != expected.size(); i++) {
                Triple triple = expected.get(i);
                boolean found = false;
                for (Triple potential : tested) {
                    if (sameTriple(triple, potential, blanks)) {
                        found = true;
                        tested.remove(potential);
                        break;
                    }
                }
                if (found) {
                    expected.remove(i);
                    i--;
                } else {
                    Assert.fail("Expected triple not produced: " + triple.toString());
                }
            }
        }

        if (expected.size() != 0) {
            Assert.fail("Failed to match all triples");
        }

        for (Triple triple : tested) {
            // fail on supplementary triples
            Assert.fail("Unexpected triple produced: " + triple.toString());
        }
    }

    /**
     * Determines whether the specified triples are equivalent, using the given blank nde mapping
     *
     * @param triple1 A triple
     * @param triple2 Another triple
     * @param blanks  A map of blank nodes
     * @return <code>true</code> if the two triples are equivalent
     */
    protected boolean sameTriple(Triple triple1, Triple triple2, Map<BlankNode, BlankNode> blanks) {
        SubjectNode subject = triple1.getSubject();
        Property property = triple1.getProperty();
        Node object = triple1.getObject();
        if (subject.getNodeType() == BlankNode.TYPE) {
            subject = blanks.get(subject);
        }
        if (object.getNodeType() == BlankNode.TYPE) {
            object = blanks.get(object);
        }
        if (!property.equals(triple2.getProperty()))
            return false;
        if (subject != null && !subject.equals(triple2.getSubject()))
            return false;
        if (object != null && !object.equals(triple2.getObject()))
            return false;
        if (subject == null && triple2.getSubject().getNodeType() != BlankNode.TYPE)
            return false;
        if (object == null && triple2.getObject().getNodeType() != BlankNode.TYPE)
            return false;
        if (subject == null)
            blanks.put((BlankNode) triple1.getSubject(), (BlankNode) triple2.getSubject());
        if (object == null)
            blanks.put((BlankNode) triple1.getObject(), (BlankNode) triple2.getObject());
        return true;
    }
}
