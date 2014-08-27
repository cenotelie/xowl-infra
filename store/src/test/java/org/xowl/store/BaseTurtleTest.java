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
import org.xowl.store.loaders.TurtleLoader;
import org.xowl.store.rdf.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.*;

/**
 * Base class for the Turtle loader tests
 *
 * @author Laurent Wouters
 */
public class BaseTurtleTest {
    /**
     * Base URI for the Turtle documents
     */
    protected static final String BASE_LOCATION = "http://www.w3.org/2013/TurtleTests/";

    /**
     * Tests that the specified Turtle resource is loaded and evaluated as the specified NTriple resource
     *
     * @param turtleResource  A Turtle resource
     * @param triplesResource A NTriple resource
     */
    protected void testTurtleEval(String turtleResource, String triplesResource) {
        RDFGraph graph = null;
        try {
            graph = new RDFGraph();
        } catch (IOException ex) {
            // do not handle
        }
        TestLogger logger = new TestLogger();

        InputStream stream = BaseTurtleTest.class.getResourceAsStream("/turtle/" + turtleResource);
        Reader reader = new InputStreamReader(stream);
        Loader loader = new TurtleLoader(graph);
        Ontology ontologyTurtle = loader.load(logger, reader, BASE_LOCATION + turtleResource);
        Assert.assertFalse("Failed to parse resource " + turtleResource, logger.isOnError());
        Assert.assertNotNull("Failed to load resource " + turtleResource, ontologyTurtle);
        try {
            reader.close();
        } catch (IOException ex) {
            Assert.fail("Failed to close the resource " + turtleResource);
        }

        stream = BaseTurtleTest.class.getResourceAsStream("/turtle/" + triplesResource);
        reader = new InputStreamReader(stream);
        loader = new NTriplesLoader(graph);
        Ontology ontologyNTriple = loader.load(logger, reader, BASE_LOCATION + triplesResource);
        Assert.assertFalse("Failed to parse resource " + triplesResource, logger.isOnError());
        Assert.assertNotNull("Failed to load resource " + triplesResource, ontologyNTriple);
        try {
            reader.close();
        } catch (IOException ex) {
            Assert.fail("Failed to close the resource " + triplesResource);
        }

        List<Triple> inTurtle = new ArrayList<>();
        Iterator<Triple> iterator = graph.getAll(ontologyTurtle);
        while (iterator.hasNext()) {
            inTurtle.add(iterator.next());
        }

        List<Triple> inNTriples = new ArrayList<>();
        iterator = graph.getAll(ontologyNTriple);
        while (iterator.hasNext()) {
            inNTriples.add(iterator.next());
        }

        matches(inNTriples, inTurtle);
    }

    private void matches(List<Triple> expected, List<Triple> tested) {
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

    private boolean sameTriple(Triple triple1, Triple triple2, Map<BlankNode, BlankNode> blanks) {
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

    /**
     * Tests that the specified Turtle resource is loaded without errors
     *
     * @param resource A Turtle resource
     */
    protected void testTurtlePositiveSyntax(String resource) {
        RDFGraph graph = null;
        try {
            graph = new RDFGraph();
        } catch (IOException ex) {
            // do not handle
        }
        TestLogger logger = new TestLogger();
        Loader loader = new TurtleLoader(graph);

        InputStream stream = BaseTurtleTest.class.getResourceAsStream("/turtle/" + resource);
        Reader reader = new InputStreamReader(stream);

        Ontology result = loader.load(logger, reader, BASE_LOCATION + resource);
        Assert.assertFalse("Failed to parse resource " + resource, logger.isOnError());
        Assert.assertNotNull("Failed to load resource " + resource, result);

        try {
            reader.close();
        } catch (IOException ex) {
            Assert.fail("Failed to close the resource " + resource);
        }
    }

    /**
     * Tests that the specified Turtle resource is loaded without errors
     *
     * @param resource A Turtle resource
     */
    protected void testTurtleNegativeSyntax(String resource) {
        RDFGraph graph = null;
        try {
            graph = new RDFGraph();
        } catch (IOException ex) {
            // do not handle
        }
        TestLogger logger = new TestLogger();
        Loader loader = new TurtleLoader(graph);

        InputStream stream = BaseTurtleTest.class.getResourceAsStream("/turtle/" + resource);
        Reader reader = new InputStreamReader(stream);

        Ontology result = loader.load(logger, reader, BASE_LOCATION + resource);
        Assert.assertTrue("No error reported while parsing " + resource, logger.isOnError());
        Assert.assertNull("Mistakenly reported success of loading " + resource, result);

        try {
            reader.close();
        } catch (IOException ex) {
            Assert.fail("Failed to close the resource " + resource);
        }
    }
}
