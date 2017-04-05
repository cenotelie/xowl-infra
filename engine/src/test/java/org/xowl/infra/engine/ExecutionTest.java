/*******************************************************************************
 * Copyright (c) 2016 Association Cénotélie (cenotelie.fr)
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
 ******************************************************************************/

package org.xowl.infra.engine;

import org.junit.Assert;
import org.junit.Test;
import org.xowl.infra.lang.owl2.IRI;
import org.xowl.infra.lang.owl2.Owl2Factory;
import org.xowl.infra.store.ProxyObject;
import org.xowl.infra.store.Repository;
import org.xowl.infra.store.RepositoryRDF;
import org.xowl.infra.store.loaders.SPARQLLoader;
import org.xowl.infra.store.rdf.LiteralNode;
import org.xowl.infra.store.rdf.Node;
import org.xowl.infra.store.rdf.RDFPatternSolution;
import org.xowl.infra.store.sparql.Command;
import org.xowl.infra.store.sparql.Result;
import org.xowl.infra.store.sparql.ResultSolutions;
import org.xowl.infra.store.sparql.Solutions;
import org.xowl.infra.utils.IOUtils;
import org.xowl.infra.utils.logging.BufferedLogger;
import org.xowl.infra.utils.logging.SinkLogger;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;

/**
 * Testing the execution of xOWL ontologies with Clojure
 *
 * @author Laurent Wouters
 */
public class ExecutionTest {
    @Test
    public void testSimpleExecution() {
        SinkLogger logger = new SinkLogger();
        RepositoryRDF repository = new RepositoryRDF();
        repository.getIRIMapper().addSimpleMap("http://xowl.org/infra/engine/tests", Repository.SCHEME_RESOURCE + "/org/xowl/infra/engine/testSimpleExecution.xowl");
        try {
            repository.load(logger, "http://xowl.org/infra/engine/tests");
        } catch (Exception exception) {
            logger.error(exception);
        }
        Assert.assertFalse("Failed to load the xOWL ontology", logger.isOnError());
        Object result = repository.getEvaluator().execute("http://xowl.org/infra/engine/tests#sayHello");
        Assert.assertFalse("Failed to execute the function", logger.isOnError());
        Assert.assertEquals("Hello World", result);
    }

    @Test
    public void testCallClojure() throws IOException {
        try (InputStream stream = ExecutionTest.class.getResourceAsStream("/org/xowl/infra/engine/testCallClojure.clj")) {
            ClojureManager.loadClojure(stream);
        }
        SinkLogger logger = new SinkLogger();
        RepositoryRDF repository = new RepositoryRDF();
        repository.getIRIMapper().addSimpleMap("http://xowl.org/infra/engine/tests", Repository.SCHEME_RESOURCE + "/org/xowl/infra/engine/testCallClojure.xowl");
        try {
            repository.load(logger, "http://xowl.org/infra/engine/tests");
        } catch (Exception exception) {
            logger.error(exception);
        }
        Assert.assertFalse("Failed to load the xOWL ontology", logger.isOnError());
        Object result = repository.getEvaluator().execute("http://xowl.org/infra/engine/tests#sayHello");
        Assert.assertFalse("Failed to execute the function", logger.isOnError());
        Assert.assertEquals("Hello World", result);
    }

    @Test
    public void testCallOtherXOWL() {
        SinkLogger logger = new SinkLogger();
        RepositoryRDF repository = new RepositoryRDF();
        repository.getIRIMapper().addSimpleMap("http://xowl.org/infra/engine/tests", Repository.SCHEME_RESOURCE + "/org/xowl/infra/engine/testCallOtherXOWL.xowl");
        try {
            repository.load(logger, "http://xowl.org/infra/engine/tests");
        } catch (Exception exception) {
            logger.error(exception);
        }
        Assert.assertFalse("Failed to load the xOWL ontology", logger.isOnError());
        Object result = repository.getEvaluator().execute("http://xowl.org/infra/engine/tests#sayHello");
        Assert.assertFalse("Failed to execute the function", logger.isOnError());
        Assert.assertEquals("Hello World", result);
    }

    @Test
    public void testDynExpInRuleConsequent() {
        SinkLogger logger = new SinkLogger();
        RepositoryRDF repository = new RepositoryRDF();
        repository.getIRIMapper().addSimpleMap("http://xowl.org/infra/engine/tests", Repository.SCHEME_RESOURCE + "/org/xowl/infra/engine/testDynExpInRuleConsequent.xowl");
        try {
            repository.load(logger, "http://xowl.org/infra/engine/tests");
        } catch (Exception exception) {
            logger.error(exception);
        }
        Assert.assertFalse("Failed to load the xOWL ontology", logger.isOnError());
        repository.getOWLRuleEngine().flush();
        ProxyObject peter = repository.getProxy("http://xowl.org/infra/engine/tests#peter");
        Object result = peter.getDataValue("http://xowl.org/infra/engine/tests#result");
        Assert.assertEquals(27L, result);
    }

    @Test
    public void testCallInSPARQL() throws IOException {
        SinkLogger logger = new SinkLogger();
        RepositoryRDF repository = new RepositoryRDF();
        repository.getIRIMapper().addSimpleMap("http://xowl.org/infra/engine/tests", Repository.SCHEME_RESOURCE + "/org/xowl/infra/engine/testCallInSPARQL.xowl");
        try {
            repository.load(logger, "http://xowl.org/infra/engine/tests");
        } catch (Exception exception) {
            logger.error(exception);
        }
        Assert.assertFalse("Failed to load the xOWL ontology", logger.isOnError());

        BufferedLogger bufferedLogger = new BufferedLogger();
        SPARQLLoader loader = new SPARQLLoader(repository.getStore(), Collections.<String>emptyList(), Collections.<String>emptyList());
        Command command;
        try (InputStream stream = ExecutionTest.class.getResourceAsStream("/org/xowl/infra/engine/testCallInSPARQL.rq")) {
            command = loader.load(bufferedLogger, new InputStreamReader(stream, IOUtils.CHARSET));
        }
        if (command == null) {
            // ill-formed request
            Assert.fail(bufferedLogger.getErrorsAsString());
        }
        Result result = command.execute(repository);
        Solutions solutions = ((ResultSolutions) result).getSolutions();
        Assert.assertEquals(1, solutions.size());
        RDFPatternSolution solution = solutions.iterator().next();
        Node value = solution.get("v");
        Assert.assertTrue(value != null && value instanceof LiteralNode);
        Assert.assertEquals(29, Integer.parseInt(((LiteralNode) value).getLexicalValue()));
    }

    @Test
    public void testStateMachine() throws IOException {
        SinkLogger logger = new SinkLogger();
        RepositoryRDF repository = new RepositoryRDF();
        repository.getIRIMapper().addSimpleMap("http://xowl.org/infra/engine/tests", Repository.SCHEME_RESOURCE + "/org/xowl/infra/engine/testStateMachine.xowl");
        try {
            repository.load(logger, "http://xowl.org/infra/engine/tests");
        } catch (Exception exception) {
            logger.error(exception);
        }
        Assert.assertFalse("Failed to load the xOWL ontology", logger.isOnError());

        Object result = repository.getEvaluator().execute("http://xowl.org/infra/engine/tests#simulate",
                getIRI("http://xowl.org/infra/engine/tests#s0"),
                new IRI[]{
                        getIRI("http://xowl.org/infra/engine/tests#a"),
                        getIRI("http://xowl.org/infra/engine/tests#b")
                });
        Assert.assertNotNull(result);
        Assert.assertTrue(result instanceof IRI);
        Assert.assertEquals("http://xowl.org/infra/engine/tests#s2", ((IRI) result).getHasValue());
    }

    /**
     * Gets the IRI object for the specified entity
     *
     * @param entity The entity
     * @return The IRI
     */
    private IRI getIRI(String entity) {
        IRI iri = Owl2Factory.newIRI();
        iri.setHasValue(entity);
        return iri;
    }
}
