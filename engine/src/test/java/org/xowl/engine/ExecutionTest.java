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

package org.xowl.engine;

import org.junit.Assert;
import org.junit.Test;
import org.xowl.store.AbstractRepository;
import org.xowl.store.Repository;

import java.io.IOException;

/**
 * Testing the execution of xOWL ontologies with Clojure
 *
 * @author Laurent Wouters
 */
public class ExecutionTest {
    @Test
    public void testExecutionHello() {
        try {
            TestLogger logger = new TestLogger();
            Repository repository = new Repository();
            repository.getIRIMapper().addSimpleMap("http://xowl.org/engine/tests/Sample", AbstractRepository.SCHEME_RESOURCE + "/org/xowl/engine/Sample.xowl");
            repository.load(logger, "http://xowl.org/engine/tests/Sample");
            Assert.assertFalse("Failed to load the xOWL ontology", logger.isOnError());
            Engine engine = new Engine(repository);
            Object result = engine.execute("http://xowl.org/engine/tests/Sample#hello");
            Assert.assertFalse("Failed to execute the function", logger.isOnError());
            Assert.assertEquals("Hello World", result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testExecutionInnerCall() {
        try {
            TestLogger logger = new TestLogger();
            Repository repository = new Repository();
            repository.getIRIMapper().addSimpleMap("http://xowl.org/engine/tests/Sample", AbstractRepository.SCHEME_RESOURCE + "/org/xowl/engine/Sample.xowl");
            repository.load(logger, "http://xowl.org/engine/tests/Sample");
            Assert.assertFalse("Failed to load the xOWL ontology", logger.isOnError());
            Engine engine = new Engine(repository);
            Object result = engine.execute("http://xowl.org/engine/tests/Sample#total", 2);
            Assert.assertFalse("Failed to execute the function", logger.isOnError());
            Assert.assertEquals(6l, result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
