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
import org.xowl.infra.utils.logging.BufferedLogger;
import org.xowl.infra.utils.logging.SinkLogger;

import java.io.StringReader;
import java.util.Collections;

/**
 * Testing the execution of xOWL ontologies with Clojure
 *
 * @author Laurent Wouters
 */
public class ExecutionTest {
    @Test
    public void testExecutionHello() {
        SinkLogger logger = new SinkLogger();
        ClojureEvaluator evaluator = new ClojureEvaluator();
        RepositoryRDF repository = new RepositoryRDF(evaluator);
        repository.getIRIMapper().addSimpleMap("http://xowl.org/infra/engine/tests/Sample", Repository.SCHEME_RESOURCE + "/org/xowl/infra/engine/Sample.xowl");
        try {
            repository.load(logger, "http://xowl.org/infra/engine/tests/Sample");
        } catch (Exception exception) {
            logger.error(exception);
        }
        Assert.assertFalse("Failed to load the xOWL ontology", logger.isOnError());
        Object result = evaluator.execute("http://xowl.org/infra/engine/tests/Sample#hello");
        Assert.assertFalse("Failed to execute the function", logger.isOnError());
        Assert.assertEquals("Hello World", result);
    }

    @Test
    public void testExecutionInnerCall() {
        SinkLogger logger = new SinkLogger();
        ClojureEvaluator evaluator = new ClojureEvaluator();
        RepositoryRDF repository = new RepositoryRDF(evaluator);
        repository.getIRIMapper().addSimpleMap("http://xowl.org/infra/engine/tests/Sample", Repository.SCHEME_RESOURCE + "/org/xowl/infra/engine/Sample.xowl");
        try {
            repository.load(logger, "http://xowl.org/infra/engine/tests/Sample");
        } catch (Exception exception) {
            logger.error(exception);
        }
        Assert.assertFalse("Failed to load the xOWL ontology", logger.isOnError());
        Object result = evaluator.execute("http://xowl.org/infra/engine/tests/Sample#total", 2);
        Assert.assertFalse("Failed to execute the function", logger.isOnError());
        Assert.assertEquals(6l, result);
    }

    @Test
    public void testExecutionInnerRule() {
        SinkLogger logger = new SinkLogger();
        ClojureEvaluator evaluator = new ClojureEvaluator();
        RepositoryRDF repository = new RepositoryRDF(evaluator);
        repository.getIRIMapper().addSimpleMap("http://xowl.org/infra/engine/tests/Sample", Repository.SCHEME_RESOURCE + "/org/xowl/infra/engine/Sample.xowl");
        try {
            repository.load(logger, "http://xowl.org/infra/engine/tests/Sample");
        } catch (Exception exception) {
            logger.error(exception);
        }
        Assert.assertFalse("Failed to load the xOWL ontology", logger.isOnError());
        repository.getOWLRuleEngine().flush();
    }

    @Test
    public void testExecutionFromSPARQL() {
        SinkLogger logger = new SinkLogger();
        ClojureEvaluator evaluator = new ClojureEvaluator();
        RepositoryRDF repository = new RepositoryRDF(evaluator);
        repository.getIRIMapper().addSimpleMap("http://xowl.org/infra/engine/tests/Sample", Repository.SCHEME_RESOURCE + "/org/xowl/infra/engine/Sample.xowl");
        try {
            repository.load(logger, "http://xowl.org/infra/engine/tests/Sample");
        } catch (Exception exception) {
            logger.error(exception);
        }
        Assert.assertFalse("Failed to load the xOWL ontology", logger.isOnError());

        BufferedLogger bufferedLogger = new BufferedLogger();
        SPARQLLoader loader = new SPARQLLoader(repository.getStore(), Collections.<String>emptyList(), Collections.<String>emptyList());
        Command command = loader.load(bufferedLogger, new StringReader("PREFIX : <http://xowl.org/infra/engine/tests/Sample#> " +
                "SELECT (:total (?x) AS ?v) " +
                "WHERE { GRAPH ?g { :peter :age ?x } }"));
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
}
