/*******************************************************************************
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
 ******************************************************************************/

package org.xowl.store;

import org.junit.Assert;
import org.xowl.store.loaders.SPARQLLoader;
import org.xowl.store.sparql.Command;
import org.xowl.store.storage.BaseStore;
import org.xowl.store.storage.InMemoryStore;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Base class for the SPARQL tests
 *
 * @author Laurent Wouters
 */
public abstract class BaseSPARQLTest {
    /**
     * Tests the correct loading of the specified SPARQL resource
     *
     * @param resource The resource to load
     */
    protected void testPositiveSyntax(String resource) {
        TestLogger logger = new TestLogger();
        BaseStore store = new InMemoryStore();
        IRIMapper mapper = new IRIMapper();
        mapper.addRegexpMap("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/(.*)", "/sparql/\\1");
        SPARQLLoader loader = new SPARQLLoader(store);
        try (InputStream stream = BaseSPARQLTest.class.getResourceAsStream(mapper.get(resource))) {
            InputStreamReader reader = new InputStreamReader(stream, "UTF-8");
            List<Command> commands = loader.load(logger, reader);
            Assert.assertFalse("Errors while loading", logger.isOnError());
            Assert.assertNotNull("Errors while loading", commands);
            Assert.assertFalse("No command loaded", commands.isEmpty());
        } catch (IOException exception) {
            Assert.fail(exception.getMessage());
        }
    }

    /**
     * Tests the reporting of errors while loading the specified SPARQL resource
     *
     * @param resource The resource to load
     */
    protected void testNegativeSyntax(String resource) {
        TestLogger logger = new TestLogger();
        BaseStore store = new InMemoryStore();
        IRIMapper mapper = new IRIMapper();
        mapper.addRegexpMap("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/(.*)", "/sparql/\\1");
        SPARQLLoader loader = new SPARQLLoader(store);
        try (InputStream stream = BaseSPARQLTest.class.getResourceAsStream(mapper.get(resource))) {
            InputStreamReader reader = new InputStreamReader(stream, "UTF-8");
            List<Command> commands = loader.load(logger, reader);
            Assert.assertTrue("Failed to report error while loading", logger.isOnError());
            Assert.assertNull("Failed to return null on incorrect loading", commands);
        } catch (IOException exception) {
            Assert.fail(exception.getMessage());
        }
    }
}
