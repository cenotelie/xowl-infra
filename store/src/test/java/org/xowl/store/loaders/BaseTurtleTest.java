/**********************************************************************
 * Copyright (c) 2014 Laurent Wouters and others
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
 **********************************************************************/
package org.xowl.store.loaders;

/**
 * Base class for the Turtle loader tests
 *
 * @author Laurent Wouters
 */
public abstract class BaseTurtleTest extends W3CTestSuite {
    /**
     * Base URI for the Turtle documents
     */
    protected static final String BASE_LOCATION = "http://www.w3.org/2013/TurtleTests/";

    /**
     * Tests that the specified Turtle resource is loaded and evaluated as the specified NTriple resource
     *
     * @param turtleResource  A Turtle resource
     * @param triplesResource A NTriple resource
     */
    protected void testTurtleEval(String turtleResource, String triplesResource) {
        testEval("/turtle/" + triplesResource, BASE_LOCATION + triplesResource, "/turtle/" + turtleResource, BASE_LOCATION + turtleResource);
    }

    /**
     * Tests that the specified Turtle resource is loaded without errors
     *
     * @param resource A Turtle resource
     */
    protected void testTurtlePositiveSyntax(String resource) {
        testPositiveSyntax("/turtle/" + resource, BASE_LOCATION + resource);
    }

    /**
     * Tests that the specified Turtle resource is loaded without errors
     *
     * @param resource A Turtle resource
     */
    protected void testTurtleNegativeSyntax(String resource) {
        testNegativeSyntax("/turtle/" + resource, BASE_LOCATION + resource);
    }
}
