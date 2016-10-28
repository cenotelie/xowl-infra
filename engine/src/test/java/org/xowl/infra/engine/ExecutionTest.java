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
import org.xowl.infra.store.AbstractRepository;
import org.xowl.infra.store.Repository;
import org.xowl.infra.utils.logging.SinkLogger;

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
        Repository repository = new Repository(evaluator);
        repository.getIRIMapper().addSimpleMap("http://xowl.org/engine/tests/Sample", AbstractRepository.SCHEME_RESOURCE + "/org/xowl/infra/engine/Sample.xowl");
        repository.load(logger, "http://xowl.org/engine/tests/Sample");
        Assert.assertFalse("Failed to load the xOWL ontology", logger.isOnError());
        Object result = evaluator.execute(repository.getProxy("http://xowl.org/engine/tests/Sample#hello"));
        Assert.assertFalse("Failed to execute the function", logger.isOnError());
        Assert.assertEquals("Hello World", result);
    }

    @Test
    public void testExecutionInnerCall() {
        SinkLogger logger = new SinkLogger();
        ClojureEvaluator evaluator = new ClojureEvaluator();
        Repository repository = new Repository(evaluator);
        repository.getIRIMapper().addSimpleMap("http://xowl.org/engine/tests/Sample", AbstractRepository.SCHEME_RESOURCE + "/org/xowl/infra/engine/Sample.xowl");
        repository.load(logger, "http://xowl.org/engine/tests/Sample");
        Assert.assertFalse("Failed to load the xOWL ontology", logger.isOnError());
        Object result = evaluator.execute(repository.getProxy("http://xowl.org/engine/tests/Sample#total"), 2);
        Assert.assertFalse("Failed to execute the function", logger.isOnError());
        Assert.assertEquals(6l, result);
    }

    @Test
    public void testExecutionInnerRule() {
        SinkLogger logger = new SinkLogger();
        Repository repository = new Repository(new ClojureEvaluator());
        repository.getIRIMapper().addSimpleMap("http://xowl.org/engine/tests/Sample", AbstractRepository.SCHEME_RESOURCE + "/org/xowl/infra/engine/Sample.xowl");
        repository.load(logger, "http://xowl.org/engine/tests/Sample");
        Assert.assertFalse("Failed to load the xOWL ontology", logger.isOnError());
        repository.getOWLRuleEngine().flush();
    }
}
