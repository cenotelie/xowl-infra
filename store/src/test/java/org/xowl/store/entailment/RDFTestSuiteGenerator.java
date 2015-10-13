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

package org.xowl.store.entailment;

import org.xowl.store.ProxyObject;
import org.xowl.store.Repository;
import org.xowl.store.TestLogger;
import org.xowl.store.Vocabulary;

import java.util.Collection;

/**
 * Generator for the RDF test suite
 *
 * @author Laurent Wouters
 */
public class RDFTestSuiteGenerator {

    /**
     * Generates the RDF test suite
     */
    public void generate() {
        TestLogger logger = new TestLogger();
        Repository repository = new Repository();
        repository.getIRIMapper().addRegexpMap("http://www.w3.org/2013/rdf-mt-tests/(.*)", "resource:///rdf-mt/\\1");
        repository.load(logger, "http://www.w3.org/2013/rdf-mt-tests/manifest.ttl");

        ProxyObject manifest = repository.resolveProxy("http://www.w3.org/2013/rdf-mt-tests/manifest.ttl");
        ProxyObject list = manifest.getObjectValue("http://www.w3.org/2001/sw/DataAccess/tests/test-manifest#entries");
        while (!Vocabulary.rdfNil.equals(list.getIRIString())) {
            generateCode(list.getObjectValue(Vocabulary.rdfFirst));
            list = list.getObjectValue(Vocabulary.rdfRest);
        }
    }

    /**
     * Generates the code for the specified test
     *
     * @param test A test specification
     */
    private void generateCode(ProxyObject test) {
        String id = (String) test.getDataValue("http://www.w3.org/2001/sw/DataAccess/tests/test-manifest#name");
        String name = getName(id);
        String input = test.getObjectValue("http://www.w3.org/2001/sw/DataAccess/tests/test-manifest#action").getIRIString();
        Object result = test.getDataValue("http://www.w3.org/2001/sw/DataAccess/tests/test-manifest#result");
        if (result == null)
            result = test.getObjectValue("http://www.w3.org/2001/sw/DataAccess/tests/test-manifest#result").getIRIString();
        String entailment = (String) test.getDataValue("http://www.w3.org/2001/sw/DataAccess/tests/test-manifest#entailmentRegime");

        String target = null;
        Collection<ProxyObject> types = test.getClassifiers();
        for (ProxyObject type : types) {
            switch (type.getIRIString()) {
                case "http://www.w3.org/2001/sw/DataAccess/tests/test-manifest#PositiveEntailmentTest":
                    target = "testPositiveEntailment";
                    break;
                case "http://www.w3.org/2001/sw/DataAccess/tests/test-manifest#NegativeEntailmentTest":
                    target = "testNegativeEntailment";
                    break;
            }
        }

        System.out.print("@Test public void test_");
        System.out.print(name);
        System.out.print("() { ");
        System.out.print(target);
        System.out.print("(\"");
        System.out.print(input);
        System.out.print("\", EntailmentRegime.");
        System.out.print(entailment);
        System.out.print(", ");
        if (result instanceof Boolean) {
            System.out.print("null");
        } else {
            System.out.print("\"");
            System.out.print(result);
            System.out.print("\"");
        }
        System.out.println("); }");
    }

    /**
     * Gets the sanitized name for the specified origina name
     *
     * @param original The original name
     * @return The sanitized name
     */
    private String getName(String original) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i != original.length(); i++) {
            char c = original.charAt(i);
            if (c >= 'a' && c <= 'z') {
                builder.append(c);
            } else if (c >= 'A' && c <= 'Z') {
                builder.append(c);
            } else if (c >= '0' && c <= '9') {
                builder.append(c);
            } else {
                builder.append('_');
            }
        }
        return builder.toString();
    }
}
