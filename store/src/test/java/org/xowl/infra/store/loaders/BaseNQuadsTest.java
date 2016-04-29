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
package org.xowl.infra.store.loaders;

/**
 * Base class for the NQuads loader tests
 *
 * @author Laurent Wouters
 */
public abstract class BaseNQuadsTest extends W3CTestSuite {
    /**
     * Base URI for the NQuads documents
     */
    protected static final String BASE_LOCATION = "http://www.w3.org/2013/NQuadsTests/";

    /**
     * Tests that the specified NQuads resource is loaded without errors
     *
     * @param resource A NQuads resource
     */
    protected void testNQuadsPositiveSyntax(String resource) {
        testPositiveSyntax("/nquads/" + resource, BASE_LOCATION + resource);
    }

    /**
     * Tests that the specified NQuads resource is not loaded without errors
     *
     * @param resource A NQuads resource
     */
    protected void testNQuadsNegativeSyntax(String resource) {
        testNegativeSyntax("/nquads/" + resource, BASE_LOCATION + resource);
    }
}
