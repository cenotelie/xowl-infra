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
package org.xowl.engine;

import org.junit.Assert;
import org.junit.Test;
import org.xowl.engine.loaders.FunctionalOWL2Loader;
import org.xowl.engine.loaders.Loader;
import org.xowl.engine.loaders.LoaderResult;
import org.xowl.engine.owl.TranslationException;
import org.xowl.engine.owl.XOWLStore;
import org.xowl.store.rdf.UnsupportedNodeType;

import java.io.*;

/**
 * Tests for the functional loaders
 *
 * @author Laurent Wouters
 */
public class FunctionalLoaderTest {

    /**
     * Tests that the specified resource is correctly loaded
     *
     * @param physicalResource The physical path to the resource
     * @param uri              The resource's URI
     */
    protected void testLoading(String physicalResource, String uri) {
        TestLogger logger = new TestLogger();
        Loader loader = new FunctionalOWL2Loader();
        InputStream stream = FunctionalLoaderTest.class.getResourceAsStream(physicalResource);
        Reader reader = null;
        try {
            reader = new InputStreamReader(stream, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            Assert.fail(ex.toString());
        }

        LoaderResult result = loader.load(logger, reader, uri);
        Assert.assertFalse("Failed to parse resource " + physicalResource, logger.isOnError());
        Assert.assertNotNull("Failed to loadQuads resource " + physicalResource, result);

        try {
            reader.close();
        } catch (IOException ex) {
            Assert.fail("Failed to close the resource " + physicalResource);
        }

        XOWLStore store = null;
        try {
            store = new XOWLStore();
            store.add(result);
        } catch (IOException ex) {
            // do not handle
        } catch (TranslationException | UnsupportedNodeType ex) {
            Assert.fail("Failed to translate " + physicalResource);
        }
    }

    @Test
    public void testLoadingOfDefinitionOWL2() {
        testLoading("/org/xowl/lang/defs/OWL2.owl", "http://xowl.org/lang/defs/OWL2.owl");
    }
}
