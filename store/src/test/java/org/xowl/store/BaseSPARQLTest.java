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
import org.xowl.store.rdf.Quad;
import org.xowl.store.sparql.Command;
import org.xowl.store.sparql.Result;
import org.xowl.store.storage.BaseStore;
import org.xowl.store.storage.InMemoryStore;
import org.xowl.store.storage.NodeManager;
import org.xowl.utils.Logger;
import org.xowl.utils.collections.Couple;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
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

    /**
     * Tests the correct execution of an update request
     *
     * @param resource The resource containing the request
     * @param inputs   The data before the request's execution
     * @param outputs  The data after the request's execution
     */
    protected void testUpdateEvaluation(String resource, Couple<String, String>[] inputs, Couple<String, String>[] outputs) {
        TestLogger logger = new TestLogger();
        Repository before = prepare(logger, inputs);
        Assert.assertFalse("Failed to prepare the repository", logger.isOnError());
        Repository after = prepare(logger, outputs);
        Assert.assertFalse("Failed to prepare the repository", logger.isOnError());
        String request = before.getIRIMapper().get(resource);
        request = request.substring(AbstractRepository.SCHEME_RESOURCE.length());
        try (InputStream stream = BaseSPARQLTest.class.getResourceAsStream(request)) {
            InputStreamReader reader = new InputStreamReader(stream, "UTF-8");
            char[] buffer = new char[1024];
            StringBuilder builder = new StringBuilder();
            int read = reader.read(buffer);
            while (read > 0) {
                builder.append(buffer, 0, read);
                read = reader.read(buffer);
            }
            request = builder.toString();
        } catch (IOException exception) {
            Assert.fail(exception.getMessage());
        }
        Result result = before.execute(logger, request);
        Assert.assertFalse("Error while executing the request", logger.isOnError());
        W3CTestSuite.matchesQuads(getQuads(after), getQuads(before));
    }

    /**
     * Gets all the quads in the specified repository
     *
     * @param repository The repository
     * @return The extracted quads
     */
    private List<Quad> getQuads(Repository repository) {
        List<Quad> quads = new ArrayList<>();
        Iterator<Quad> iterator = repository.getStore().getAll();
        while (iterator.hasNext())
            quads.add(iterator.next());
        return quads;
    }

    /**
     * Prepares a repository with the specified inputs in it
     *
     * @param logger The logger to use
     * @param inputs The inputs
     * @return The repository
     */
    private Repository prepare(Logger logger, Couple<String, String>[] inputs) {
        Repository repository = null;
        try {
            repository = new Repository();
            repository.getIRIMapper().addRegexpMap("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/(.*)", "resource:///sparql/\\1");
        } catch (IOException exception) {
            Assert.fail("Failed to initialize the repository");
        }
        for (Couple<String, String> input : inputs) {
            repository.load(logger, input.x, input.y == null ? NodeManager.DEFAULT_GRAPH : input.y, true);
        }
        return repository;
    }
}
