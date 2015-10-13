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

package org.xowl.store.loaders;

/**
 * Base class for the TriG loader tests
 *
 * @author Laurent Wouters
 */
public abstract class BaseTriGTest extends W3CTestSuite {
    /**
     * Base URI for the Turtle documents
     */
    protected static final String BASE_LOCATION = "http://www.w3.org/2013/TrigTests/";

    /**
     * Tests that the specified TriG resource is loaded and evaluated as the specified NQuad resource
     *
     * @param trigResource  A TriG resource
     * @param quadResource A NTriple resource
     */
    protected void testTrigEval(String trigResource, String quadResource) {
        testEval("/trig/" + quadResource, BASE_LOCATION + quadResource, "/trig/" + trigResource, BASE_LOCATION + trigResource);
    }

    /**
     * Tests that the specified TriG resource is loaded without errors
     *
     * @param resource A TriG resource
     */
    protected void testTrigPositiveSyntax(String resource) {
        testPositiveSyntax("/trig/" + resource, BASE_LOCATION + resource);
    }

    /**
     * Tests that the specified TriG resource is loaded without errors
     *
     * @param resource A TriG resource
     */
    protected void testTrigNegativeSyntax(String resource) {
        testNegativeSyntax("/trig/" + resource, BASE_LOCATION + resource);
    }
}
