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

import org.xowl.infra.store.Repository;

/**
 * Base class for the NTriple loader tests
 *
 * @author Laurent Wouters
 */
public abstract class BaseNTripleTest extends W3CTestSuite {
    /**
     * Base URI for the NTriple documents
     */
    protected static final String BASE_LOCATION = "http://www.w3.org/2013/NTripleTests/";

    /**
     * Tests that the specified NTriples resource is loaded without errors
     *
     * @param resource A NTriples resource
     */
    protected void testNTriplesPositiveSyntax(String resource) {
        testPositiveSyntax(Repository.SCHEME_RESOURCE + "/ntriples/" + resource, BASE_LOCATION + resource);
    }

    /**
     * Tests that the specified NTriples resource is not loaded without errors
     *
     * @param resource A NTriples resource
     */
    protected void testNTriplesNegativeSyntax(String resource) {
        testNegativeSyntax(Repository.SCHEME_RESOURCE + "/ntriples/" + resource, BASE_LOCATION + resource);
    }
}
