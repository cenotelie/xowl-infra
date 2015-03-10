/**********************************************************************
 * Copyright (c) 2015 Laurent Wouters
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
import org.junit.Test;
import org.xowl.store.loaders.*;
import org.xowl.store.rdf.Changeset;
import org.xowl.store.rdf.Quad;
import org.xowl.store.rdf.RDFStore;
import org.xowl.store.rdf.UnsupportedNodeType;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Tests for the serialization of RDF store
 *
 * @author Laurent Wouters
 */
public class SerializationTest {
    /**
     * Loads the specified resource into a new store
     *
     * @param physicalResource The physical path to the resource
     * @param uri              The resource's URI
     */
    protected RDFStore load(String physicalResource, String uri) {
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

        RDFLoaderResult input = loader.loadRDF(logger, reader, uri);
        Assert.assertFalse("Failed to parse resource " + physicalResource, logger.isOnError());
        Assert.assertNotNull("Failed to loadRDF resource " + physicalResource, input);

        try {
            reader.close();
        } catch (IOException ex) {
            Assert.fail("Failed to close the resource " + physicalResource);
        }

        try {
            store.insert(new Changeset(input.getQuads(), new ArrayList<Quad>()));
        } catch (UnsupportedNodeType ex) {
            Assert.fail("Unable to loadOWL the resource " + physicalResource);
        }
        return store;
    }

    /**
     * Loads a RDF store from the specified resources
     *
     * @param resources The physical path to the resources
     */
    protected RDFStore load(String resources) {
        RDFStore store = null;
        try {
            store = new RDFStore(resources);
        } catch (IOException ex) {
            // do not handle
        }
        return store;
    }

    /**
     * Gets all quads in the store
     *
     * @param store A store
     * @return The contained quads
     */
    protected List<Quad> getAll(RDFStore store) {
        List<Quad> result = new ArrayList<>();
        Iterator<Quad> iterator = store.getAll();
        while (iterator.hasNext()) {
            result.add(iterator.next());
        }
        return result;
    }

    /**
     * Tests the round trip serialization and deserialization of a RDF graph
     */
    @Test
    public void roundTripTest() {
        RDFStore store1 = load("/tests/testOntology.rdf", "http://www.w3.org/2007/OWL/testOntology");
        List<Quad> originals = getAll(store1);
        try {
            store1.save("target/test-classes/serialization");
        } catch (IOException e) {
            Assert.fail("Failed to serialize the resource");
        }

        RDFStore store2 = load("target/test-classes/serialization");
        List<Quad> reloaded = getAll(store2);
        W3CTestSuite.matches(originals, reloaded);
    }
}
