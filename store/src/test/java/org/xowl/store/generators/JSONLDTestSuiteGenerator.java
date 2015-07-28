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

package org.xowl.store.generators;

import org.junit.Assert;
import org.xowl.store.BaseJSONLDTest;
import org.xowl.store.ProxyObject;
import org.xowl.store.Repository;
import org.xowl.store.TestLogger;

import java.io.IOException;

/**
 * The generator of the test suite for the JSON-LD syntax
 *
 * @author Laurent Wouters
 */
public class JSONLDTestSuiteGenerator {
    public void generateToRdfTests() {
        TestLogger logger = new TestLogger();
        Repository repository = null;
        try {
            repository = new Repository();
        } catch (IOException e) {
            Assert.fail("Failed to initialize the repository");
        }
        repository.getIRIMapper().addRegexpMap(BaseJSONLDTest.NAMESPACE + "(.*)", "resource://" + BaseJSONLDTest.PHYSICAL + "\\1");
        repository.load(logger, BaseJSONLDTest.NAMESPACE + "tests/toRdf-manifest.jsonld");
        repository.load(logger, BaseJSONLDTest.NAMESPACE + "tests/normalize-manifest.jsonld");

        ProxyObject classRDFTestCase = repository.resolveProxy("http://json-ld.org/test-suite/vocab#ToRDFTest");
        for (ProxyObject test : classRDFTestCase.getInstances()) {
            String name = test.getIRIString().substring((BaseJSONLDTest.NAMESPACE + "tests/toRdf-manifest.jsonld").length() + 1);
            String input = test.getObjectValue("http://www.w3.org/2001/sw/DataAccess/tests/test-manifest#action").getIRIString();
            String expect = test.getObjectValue("http://www.w3.org/2001/sw/DataAccess/tests/test-manifest#result").getIRIString();
            System.out.println("@Test public void test_toRdf_" + name + "() { toRdfTest(\"" + expect + "\", \"" + input + "\"); }");
        }
    }
}
