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
 * Base class for the RDF/XML loader tests
 *
 * @author Laurent Wouters
 */
public abstract class BaseRDFXMLTest extends W3CTestSuite {
    /**
     * Base URI for the RDF/XML documents
     */
    protected static final String BASE_LOCATION = "http://www.w3.org/2013/RDFXMLTests/";

    /**
     * Tests that the specified RDF/XML resource is loaded and evaluated as the specified NTriple resource
     *
     * @param rdfResource     A RDF/XML resource
     * @param triplesResource A NTriple resource
     */
    protected void testXMLEval(String rdfResource, String triplesResource) {
        testEval("/rdfxml/" + triplesResource, BASE_LOCATION + triplesResource, "/rdfxml/" + rdfResource, BASE_LOCATION + rdfResource);
    }

    /**
     * Tests that the specified RDF/XML resource is loaded without errors
     *
     * @param resource A RDF/XML resource
     */
    protected void testXMLPositiveSyntax(String resource) {
        testPositiveSyntax("/rdfxml/" + resource, BASE_LOCATION + resource);
    }

    /**
     * Tests that the specified RDF/XML resource is not loaded without errors
     *
     * @param resource A RDF/XML resource
     */
    protected void testXMLNegativeSyntax(String resource) {
        testNegativeSyntax("/rdfxml/" + resource, BASE_LOCATION + resource);
    }
}
