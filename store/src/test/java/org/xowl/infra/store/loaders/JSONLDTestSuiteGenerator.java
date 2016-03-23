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

package org.xowl.infra.store.loaders;

import org.xowl.infra.store.ProxyObject;
import org.xowl.infra.store.Repository;
import org.xowl.infra.store.TestLogger;

/**
 * The generator of the test suite for the JSON-LD syntax
 *
 * @author Laurent Wouters
 * @author Stephen Creff
 */
public class JSONLDTestSuiteGenerator {

    public void generateToRdfTests() {
        TestLogger logger = new TestLogger();
        Repository repository = new Repository();
        repository.getIRIMapper().addRegexpMap(BaseJSONLDTest.NAMESPACE + "(.*)", "resource://" + BaseJSONLDTest.PHYSICAL + "\\1");
        repository.load(logger, BaseJSONLDTest.NAMESPACE + "tests/toRdf-manifest.jsonld");
        //repository.load(logger, BaseJSONLDTest.NAMESPACE + "tests/normalize-manifest.jsonld");

        ProxyObject classRDFTestCase = repository.resolveProxy("http://json-ld.org/test-suite/vocab#ToRDFTest");
        for (ProxyObject test : classRDFTestCase.getInstances()) {
            String name = test.getIRIString().substring((BaseJSONLDTest.NAMESPACE + "tests/toRdf-manifest.jsonld").length() + 1);
            String input = test.getObjectValue("http://www.w3.org/2001/sw/DataAccess/tests/test-manifest#action").getIRIString();
            String expect = test.getObjectValue("http://www.w3.org/2001/sw/DataAccess/tests/test-manifest#result").getIRIString();
            System.out.println("@Test public void test_toRdf_" + name + "() { toRdfTest(\"" + expect + "\", \"" + input + "\"); }");
        }
    }

    public void generateFromRdfTests() {
        TestLogger logger = new TestLogger();
        Repository repository  = new Repository();

        repository.getIRIMapper().addRegexpMap(BaseJSONLDTest.NAMESPACE + "(.*)", "resource://" + BaseJSONLDTest.PHYSICAL + "\\1");
        repository.load(logger, BaseJSONLDTest.NAMESPACE + "tests/fromRdf-manifest.jsonld");

        ProxyObject classRDFTestCase = repository.resolveProxy("http://json-ld.org/test-suite/vocab#FromRDFTest");
        for (ProxyObject test : classRDFTestCase.getInstances()) {
            String name = test.getIRIString().substring((BaseJSONLDTest.NAMESPACE + "tests/fromRdf-manifest.jsonld").length() + 1);
            String input = test.getObjectValue("http://www.w3.org/2001/sw/DataAccess/tests/test-manifest#action").getIRIString();
            String expect = test.getObjectValue("http://www.w3.org/2001/sw/DataAccess/tests/test-manifest#result").getIRIString();
            System.out.println("@Test public void test_fromRdf_" + name + "() { fromRdfTest(\"" + expect + "\", \"" + input + "\"); }");
        }
    }
}
