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

package org.xowl.store;

import org.junit.Assert;
import org.xowl.hime.redist.ParseResult;
import org.xowl.lang.owl2.Ontology;
import org.xowl.store.loaders.Loader;
import org.xowl.store.loaders.NTriplesLoader;
import org.xowl.store.loaders.RDFXMLLoader;
import org.xowl.store.loaders.TurtleLoader;
import org.xowl.store.rdf.RDFGraph;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * Represents a test suite from the W3C
 *
 * @author Laurent Wouters
 */
public abstract class W3CTestSuite {

    /**
     * The RDF graph for the parsing tests
     */
    protected RDFGraph graph;

    /**
     * Logger for the tests
     */
    protected TestLogger logger;

    /**
     * Initializes this test suite
     *
     * @throws IOException
     */
    protected W3CTestSuite() throws IOException {
        graph = new RDFGraph();
        logger = new TestLogger();
    }

    /**
     * Tests the parsing of a resource
     *
     * @param resource     A resource
     * @param shallSucceed Whether the loading is expected to succeed
     */
    protected void test_parsing(String resource, boolean shallSucceed) {
        System.out.println("Parsing test: " + resource);
        logger.reset();
        Loader loader = null;
        if (resource.endsWith(".nt")) {
            loader = new NTriplesLoader(graph);
        } else if (resource.endsWith(".ttl")) {
            loader = new TurtleLoader(graph);
        } else if (resource.endsWith(".xml")) {
            loader = new RDFXMLLoader(graph);
        }
        Assert.assertNotNull("No loader found for resource " + resource, loader);

        InputStream stream = W3CTestSuite.class.getResourceAsStream(resource);
        Reader reader = new InputStreamReader(stream);

        ParseResult result = loader.parse(logger, reader);
        if (shallSucceed) {
            Assert.assertTrue("Failed to parse resource " + resource, result.isSuccess());
            Assert.assertEquals("Failed to parse resource " + resource, 0, result.getErrors().size());
        } else {
            Assert.assertNotEquals("No error reported while parsing " + resource, 0, result.getErrors().size());
        }

        try {
            reader.close();
        } catch (IOException ex) {
            Assert.fail("Failed to close the resource " + resource);
        }
    }

    /**
     * Tests the loading of a resource
     *
     * @param resource     A resource
     * @param shallSucceed Whether the loading is expected to succeed
     */
    protected void test_loading(String resource, boolean shallSucceed) {
        logger.reset();
        Loader loader = null;
        if (resource.endsWith(".nt")) {
            loader = new NTriplesLoader(graph);
        } else if (resource.endsWith(".ttl")) {
            loader = new TurtleLoader(graph);
        } else if (resource.endsWith(".xml")) {
            loader = new RDFXMLLoader(graph);
        }
        Assert.assertNotNull("No loader found for resource " + resource, loader);

        InputStream stream = W3CTestSuite.class.getResourceAsStream(resource);
        Reader reader = new InputStreamReader(stream);

        Ontology result = loader.load(logger, reader);
        if (shallSucceed) {
            Assert.assertFalse("Failed to parse resource " + resource, logger.isOnError());
            Assert.assertNotNull("Failed to load resource " + resource, result);
        } else {
            Assert.assertTrue("No error reported while parsing " + resource, logger.isOnError());
            Assert.assertNull("Mistakenly reported success of loading " + resource, result);
        }

        try {
            reader.close();
        } catch (IOException ex) {
            Assert.fail("Failed to close the resource " + resource);
        }
    }
}
