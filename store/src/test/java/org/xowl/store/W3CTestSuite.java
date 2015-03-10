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
import org.xowl.store.loaders.*;
import org.xowl.store.rdf.*;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        RDFStore store = null;
        try {
            store = new RDFStore();
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
            loader = new NTriplesLoader(store);
        else if (expectedResource.endsWith(".nq"))
            loader = new NQuadsLoader(store);
        else if (expectedResource.endsWith(".ttl"))
            loader = new TurtleLoader(store);
        else if (expectedResource.endsWith(".rdf"))
            loader = new RDFXMLLoader(store);
        if (loader == null)
            Assert.fail("Failed to recognize resource " + expectedResource);
        RDFLoaderResult expected = loader.loadRDF(logger, reader, expectedURI);
        Assert.assertFalse("Failed to parse resource " + expectedResource, logger.isOnError());
        Assert.assertNotNull("Failed to loadOWL resource " + expectedResource, expected);
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
            loader = new NTriplesLoader(store);
        else if (expectedResource.endsWith(".nq"))
            loader = new NQuadsLoader(store);
        else if (testedResource.endsWith(".ttl"))
            loader = new TurtleLoader(store);
        else if (testedResource.endsWith(".rdf"))
            loader = new RDFXMLLoader(store);
        if (loader == null)
            Assert.fail("Failed to recognize resource " + testedResource);
        RDFLoaderResult tested = loader.loadRDF(logger, reader, testedURI);
        Assert.assertFalse("Failed to parse resource " + testedResource, logger.isOnError());
        Assert.assertNotNull("Failed to loadRDF resource " + testedResource, tested);
        try {
            reader.close();
        } catch (IOException ex) {
            Assert.fail("Failed to close the resource " + testedResource);
        }

        matches(expected.getQuads(), tested.getQuads());
    }

    /**
     * Tests that the specified resource is correctly loaded
     *
     * @param physicalResource The physical path to the resource
     * @param uri              The resource's URI
     */
    protected void testPositiveSyntax(String physicalResource, String uri) {
        RDFStore store = null;
        try {
            store = new RDFStore();
        } catch (IOException ex) {
            // do not handle
        }
        TestLogger logger = new TestLogger();
        Loader loader = null;
        if (physicalResource.endsWith(".nt"))
            loader = new NTriplesLoader(store);
        else if (physicalResource.endsWith(".nq"))
            loader = new NQuadsLoader(store);
        else if (physicalResource.endsWith(".ttl"))
            loader = new TurtleLoader(store);
        else if (physicalResource.endsWith(".rdf"))
            loader = new RDFXMLLoader(store);
        if (loader == null)
            Assert.fail("Failed to recognize resource " + physicalResource);

        InputStream stream = W3CTestSuite.class.getResourceAsStream(physicalResource);
        Reader reader = null;
        try {
            reader = new InputStreamReader(stream, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            Assert.fail(ex.toString());
        }

        RDFLoaderResult result = loader.loadRDF(logger, reader, uri);
        Assert.assertFalse("Failed to parse resource " + physicalResource, logger.isOnError());
        Assert.assertNotNull("Failed to loadRDF resource " + physicalResource, result);

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
        RDFStore store = null;
        try {
            store = new RDFStore();
        } catch (IOException ex) {
            // do not handle
        }
        TestLogger logger = new TestLogger();
        Loader loader = null;
        if (physicalResource.endsWith(".nt"))
            loader = new NTriplesLoader(store);
        else if (physicalResource.endsWith(".nq"))
            loader = new NQuadsLoader(store);
        else if (physicalResource.endsWith(".ttl"))
            loader = new TurtleLoader(store);
        else if (physicalResource.endsWith(".rdf"))
            loader = new RDFXMLLoader(store);
        if (loader == null)
            Assert.fail("Failed to recognize resource " + physicalResource);

        InputStream stream = W3CTestSuite.class.getResourceAsStream(physicalResource);
        Reader reader = null;
        try {
            reader = new InputStreamReader(stream, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            Assert.fail(ex.toString());
        }

        RDFLoaderResult result = loader.loadRDF(logger, reader, uri);
        Assert.assertNull("Mistakenly reported success of loading " + physicalResource, result);

        try {
            reader.close();
        } catch (IOException ex) {
            Assert.fail("Failed to close the resource " + physicalResource);
        }
    }

    /**
     * Tests whether two sets of quads describe the same graph
     *
     * @param expected The expected set of triples
     * @param tested   The tested set of triples
     */
    public static void matches(List<Quad> expected, List<Quad> tested) {
        Map<BlankNode, BlankNode> blanks = new HashMap<>();
        for (int i = 0; i != expected.size(); i++) {
            Quad quad = expected.get(i);
            if (quad.getSubject().getNodeType() != BlankNode.TYPE) {
                // ignore blank nodes at this time
                boolean found = false;
                for (Quad potential : tested) {
                    if (sameTriple(quad, potential, blanks)) {
                        found = true;
                        tested.remove(potential);
                        break;
                    }
                }
                if (found) {
                    expected.remove(i);
                    i--;
                } else {
                    Assert.fail("Expected triple not produced: " + quad.toString());
                }
            }
        }

        int size = expected.size() + 1;
        while (size != expected.size()) {
            // while no more modifications
            size = expected.size();
            for (int i = 0; i != expected.size(); i++) {
                Quad quad = expected.get(i);
                boolean found = false;
                for (Quad potential : tested) {
                    if (sameTriple(quad, potential, blanks)) {
                        found = true;
                        tested.remove(potential);
                        break;
                    }
                }
                if (found) {
                    expected.remove(i);
                    i--;
                } else {
                    Assert.fail("Expected triple not produced: " + quad.toString());
                }
            }
        }

        if (expected.size() != 0) {
            Assert.fail("Failed to match all triples");
        }

        for (Quad quad : tested) {
            // fail on supplementary triples
            Assert.fail("Unexpected triple produced: " + quad.toString());
        }
    }

    /**
     * Determines whether the specified triples are equivalent, using the given blank nde mapping
     *
     * @param quad1  A triple
     * @param quad2  Another triple
     * @param blanks A map of blank nodes
     * @return <code>true</code> if the two triples are equivalent
     */
    public static boolean sameTriple(Quad quad1, Quad quad2, Map<BlankNode, BlankNode> blanks) {
        SubjectNode subject = quad1.getSubject();
        Property property = quad1.getProperty();
        Node object = quad1.getObject();
        if (subject.getNodeType() == BlankNode.TYPE) {
            subject = blanks.get(subject);
        }
        if (object.getNodeType() == BlankNode.TYPE) {
            object = blanks.get(object);
        }
        if (!property.equals(quad2.getProperty()))
            return false;
        if (subject != null && !subject.equals(quad2.getSubject()))
            return false;
        if (object != null && !object.equals(quad2.getObject()))
            return false;
        if (subject == null && quad2.getSubject().getNodeType() != BlankNode.TYPE)
            return false;
        if (object == null && quad2.getObject().getNodeType() != BlankNode.TYPE)
            return false;
        if (subject == null)
            blanks.put((BlankNode) quad1.getSubject(), (BlankNode) quad2.getSubject());
        if (object == null)
            blanks.put((BlankNode) quad1.getObject(), (BlankNode) quad2.getObject());
        return true;
    }
}
