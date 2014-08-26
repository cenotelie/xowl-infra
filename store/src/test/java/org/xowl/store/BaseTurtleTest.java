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
import org.xowl.store.loaders.TurtleLoader;
import org.xowl.store.rdf.RDFGraph;
import org.xowl.store.rdf.Triple;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Base class for the Turtle loader tests
 *
 * @author Laurent Wouters
 */
public class BaseTurtleTest {

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
        Loader loader = new TurtleLoader(graph);

        InputStream stream = BaseTurtleTest.class.getResourceAsStream("/turtle/" + turtleResource);
        Reader reader = new InputStreamReader(stream);
        Ontology ontologyTurtle = loader.load(logger, reader);
        Assert.assertFalse("Failed to parse resource " + turtleResource, logger.isOnError());
        Assert.assertNotNull("Failed to load resource " + turtleResource, ontologyTurtle);
        try {
            reader.close();
        } catch (IOException ex) {
            Assert.fail("Failed to close the resource " + turtleResource);
        }

        stream = BaseTurtleTest.class.getResourceAsStream("/turtle/" + triplesResource);
        reader = new InputStreamReader(stream);
        Ontology ontologyNTriple = loader.load(logger, reader);
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

        for (Triple triple : inNTriples) {
            boolean found = false;
            for (Triple potential : inTurtle) {
                if (sameTriple(potential, triple)) {
                    found = true;
                    inTurtle.remove(potential);
                    break;
                }
            }
            if (!found)
                Assert.fail("Expected triple not produced: " + triple.toString());
        }

        for (Triple triple : inTurtle) {
            Assert.fail("Unexpected triple produced: " + triple.toString());
        }
    }

    private boolean sameTriple(Triple triple1, Triple triple2) {
        return (triple1.getSubject().equals(triple2.getSubject())
                && triple1.getProperty().equals(triple2.getProperty())
                && triple1.getObject().equals(triple2.getObject()));
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

        Ontology result = loader.load(logger, reader);
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

        Ontology result = loader.load(logger, reader);
        Assert.assertTrue("No error reported while parsing " + resource, logger.isOnError());
        Assert.assertNull("Mistakenly reported success of loading " + resource, result);

        try {
            reader.close();
        } catch (IOException ex) {
            Assert.fail("Failed to close the resource " + resource);
        }
    }
}
