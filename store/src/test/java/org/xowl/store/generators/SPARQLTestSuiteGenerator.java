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
import org.junit.Test;
import org.xowl.store.ProxyObject;
import org.xowl.store.Repository;
import org.xowl.store.TestLogger;

import java.io.IOException;

/**
 * The generator of the test suite for SPARQL
 *
 * @author Laurent Wouters
 */
public class SPARQLTestSuiteGenerator {

    /**
     * Generates the SPARQL test suite
     */
    @Test
    public void generate() {
        TestLogger logger = new TestLogger();
        Repository repository = null;
        try {
            repository = new Repository();
        } catch (IOException e) {
            Assert.fail("Failed to initialize the repository");
        }

        repository.getIRIMapper().addRegexpMap("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/(.*)", "resource:///sparql/\\1");
        repository.load(logger, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-fed/manifest.ttl");
        repository.load(logger, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/manifest.ttl");
        repository.load(logger, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-update-1/manifest.ttl");
        repository.load(logger, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-update-2/manifest.ttl");

        ProxyObject classTestCase = repository.resolveProxy("http://www.w3.org/2001/sw/DataAccess/tests/test-manifest#PositiveSyntaxTest11");
        for (ProxyObject test : classTestCase.getInstances())
            generateCodePositiveSyntax(test);
        classTestCase = repository.resolveProxy("http://www.w3.org/2001/sw/DataAccess/tests/test-manifest#PositiveSyntaxTest");
        for (ProxyObject test : classTestCase.getInstances())
            generateCodePositiveSyntax(test);
        classTestCase = repository.resolveProxy("http://www.w3.org/2001/sw/DataAccess/tests/test-manifest#NegativeSyntaxTest11");
        for (ProxyObject test : classTestCase.getInstances())
            generateCodeNegativeSyntax(test);
        classTestCase = repository.resolveProxy("http://www.w3.org/2001/sw/DataAccess/tests/test-manifest#NegativeSyntaxTest");
        for (ProxyObject test : classTestCase.getInstances())
            generateCodeNegativeSyntax(test);
    }

    /**
     * Generates the code for the specified positive syntax test
     *
     * @param test A test specification
     */
    private void generateCodePositiveSyntax(ProxyObject test) {
        String name = getName((String) test.getDataValue("http://www.w3.org/2001/sw/DataAccess/tests/test-manifest#name"));
        String resource = test.getObjectValue("http://www.w3.org/2001/sw/DataAccess/tests/test-manifest#action").getIRIString();
        System.out.println("@Test public void testPositiveSyntax_" + name + "() { testPositiveSyntax(\"" + resource + "\"); }");
    }

    /**
     * Generates the code for the specified negative syntax test
     *
     * @param test A test specification
     */
    private void generateCodeNegativeSyntax(ProxyObject test) {
        String name = getName((String) test.getDataValue("http://www.w3.org/2001/sw/DataAccess/tests/test-manifest#name"));
        String resource = test.getObjectValue("http://www.w3.org/2001/sw/DataAccess/tests/test-manifest#action").getIRIString();
        System.out.println("@Test public void testNegativeSyntax_" + name + "() { testNegativeSyntax(\"" + resource + "\"); }");
    }

    /**
     * Gets the sanitized name for the specified original name
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
