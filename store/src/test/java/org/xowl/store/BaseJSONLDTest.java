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

/**
 * Base class for the JSON-LD loader tests
 *
 * @author Laurent Wouters
 */
public abstract class BaseJSONLDTest extends W3CTestSuite {
    /**
     * Path to the phsyical resources of the tests
     */
    public static final String PHYSICAL = "/json-ld/";
    /**
     * Base URI of the test resources
     */
    public static final String NAMESPACE = "http://json-ld.org/test-suite/";

    /**
     * Performs a JSON-LD to RDF test
     *
     * @param expectedURI The URI of the expected NQuads result
     * @param testedURI   The URI of the tested JSON-LD document
     */
    protected void toRdfTest(String expectedURI, String testedURI) {
        mapper.addRegexpMap(NAMESPACE + "(.*)", PHYSICAL + "\\1");
        testEval(mapper.get(expectedURI), expectedURI, mapper.get(testedURI), testedURI);
    }

    /**
     * Performs a normalize JSON-LD to RDF test
     *
     * @param expectedURI The URI of the expected NQuads result
     * @param testedURI   The URI of the tested JSON-LD document
     */
    protected void normalizeTest(String expectedURI, String testedURI) {
        toRdfTest(expectedURI, testedURI);
    }
}
