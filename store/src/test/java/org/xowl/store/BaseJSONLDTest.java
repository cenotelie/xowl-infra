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
 *     Stephen Creff - stephen.creff@gmail.com
 ******************************************************************************/

package org.xowl.store;

import org.junit.Assert;
import org.xowl.hime.redist.ParseResult;
import org.xowl.lang.owl2.Ontology;
import org.xowl.store.loaders.Loader;
import org.xowl.store.loaders.RDFLoaderResult;
import org.xowl.store.rdf.GraphNode;
import org.xowl.store.rdf.IRINode;
import org.xowl.store.rdf.Quad;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Base class for the JSON-LD loader tests
 *
 * @author Laurent Wouters
 * Modified to add fromRDF tests
 * @author Stephen Creff
 */
public abstract class BaseJSONLDTest extends W3CTestSuite {
    /**
     * Path to the physical resources of the tests
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


    /**
     * Performs a JSON-LD from RDF test
     *
     * @param expectedURI The URI of the expected NQuads result
     * @param testedURI   The URI of the tested JSON-LD document
     */
    protected void fromRdfTest(String expectedURI, String testedURI) {
        mapper.addRegexpMap(NAMESPACE + "(.*)", PHYSICAL + "\\1");
        testEval(mapper.get(expectedURI), expectedURI, mapper.get(testedURI), testedURI);
    }

    /**
     * Performs a generation of JSON-LD from RDF
     *
     * @param testedURI   The URI of the tested NQuads document
     * @param generatedURI The URI of the generated JSON-LD resulting document
     *
     */
    private void generateJSONLDFromRdfFile(String testedURI, String generatedURI){
        Repository repository = new Repository();

        repository.getIRIMapper().addRegexpMap(BaseJSONLDTest.NAMESPACE + "(.*)", "file://" + "src/test/resources" + BaseJSONLDTest.PHYSICAL + "\\1");
        //Load in quads
        repository.load(logger, testedURI);
        Assert.assertFalse("Failed to load the ontology(ies)", logger.isOnError());
        //serialize in jsonld
        repository.exportAll(logger, generatedURI);
    }

    /**
     * Tests the evaluation of a resource
     *
     * @param expectedResource Path to the expected resource
     * @param expectedURI      Expected resource's URI
     * @param testedResource   Path to the tested resource
     * @param testedURI        Tested resource's URI
     */
    @Override
    protected void testEval(String expectedResource, String expectedURI, String testedResource, String testedURI) {
        /**
         * @authors Stephen Creff
         * @date 8 sept 2015
         * Making a difference between fromRDF and toRDF testEval for JSON-LD resources
         */
        if (AbstractRepository.getSyntax(testedResource).equals(AbstractRepository.SYNTAX_NQUADS)){ //fromRDF
            String generatedURI = testedURI.replace(AbstractRepository.EXT_NQUADS,"_generatedFromRDF" + AbstractRepository.EXT_JSON_LD);
            String generatedResource = testedResource.replace(AbstractRepository.EXT_NQUADS, "_generatedFromRDF" + AbstractRepository.EXT_JSON_LD);
            mapper.addSimpleMap(generatedURI, generatedResource);
            //Load RDF file and serialize it in jsonld
            generateJSONLDFromRdfFile(testedURI, generatedURI);
            //Reload the generated file and compare it (quad comparison only) to a reference jsonld loaded one
            /*super.testEval(expectedResource, expectedURI, generatedResource, generatedURI);*/
            //Reload the generated file and compare it (quad comparison only) to the quads
            super.testEval(testedResource, testedURI, generatedResource, generatedURI);
            /* super.testEval(expectedResource, expectedURI, testedResource, testedURI); */
        }else //toRDF
        {
            /*
            //Temp tests of the serialisation on the toRDF files
            String generatedURI = expectedURI.replace(AbstractRepository.EXT_NQUADS,"_generatedFromRDF" + AbstractRepository.EXT_JSON_LD);
            String generatedResource = expectedResource.replace(AbstractRepository.EXT_NQUADS, "_generatedFromRDF" + AbstractRepository.EXT_JSON_LD);
            mapper.addSimpleMap(generatedURI, generatedResource);
            generateJSONLDFromRdfFile(expectedURI, generatedURI);
            super.testEval(expectedResource, expectedURI, generatedResource, generatedURI);
            */
            super.testEval(expectedResource, expectedURI, testedResource, testedURI);
        }

    }

}
