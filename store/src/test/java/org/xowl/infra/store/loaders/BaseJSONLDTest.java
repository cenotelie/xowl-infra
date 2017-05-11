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

import org.junit.Assert;
import org.xowl.infra.store.Repository;
import org.xowl.infra.store.RepositoryRDF;
import org.xowl.infra.store.ResourceAccess;

import java.io.File;
import java.io.IOException;

/**
 * Base class for the JSON-LD loader tests
 *
 * @author Laurent Wouters
 *         Modified to add fromRDF tests
 * @author Stephen Creff
 */
public abstract class BaseJSONLDTest extends W3CTestSuite {
    /**
     * Path to the physical resources of the tests
     */
    public static final String PHYSICAL = "/org/json-ld/";
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
        mapper.addRegexpMap(NAMESPACE + "(.*)", ResourceAccess.SCHEME_RESOURCE + PHYSICAL + "\\1");
        testEval(mapper.get(expectedURI), expectedURI, mapper.get(testedURI), testedURI);
    }

    /**
     * Performs a JSON-LD from RDF test
     * The strategy for this test is to:
     * - load the tested NQuad
     * - export all its context as JSON-LD
     * - Reload both expected JSON-LD and the generated one as quads
     * - Compare the quads are matching
     * The test confirms that the information in the generated JSON-LD document is the same as the expected one.
     * The test does not check that the output syntax is exactly the same as the expected one.
     *
     * @param expectedURI The URI of the expected NQuads result
     * @param testedURI   The URI of the tested JSON-LD document
     */
    protected void fromRdfTest(String expectedURI, String testedURI) {
        // load RDF file and serialize it in jsonld
        String generatedURI = testedURI.replace(Repository.SYNTAX_NQUADS_EXTENSION, "_generatedFromRDF" + Repository.SYNTAX_JSON_LD_EXTENSION);
        File generated = generateJSONLDFromRdfFile(testedURI, generatedURI);
        if (generated == null) {
            // cannot happen due to the assertion failure, but get rid of the null warning
            return;
        }
        mapper.addRegexpMap(NAMESPACE + "(.*)", ResourceAccess.SCHEME_RESOURCE + PHYSICAL + "\\1");
        mapper.addSimpleMap(generatedURI, ResourceAccess.SCHEME_FILE + generated.getAbsolutePath());
        testEval(mapper.get(expectedURI), expectedURI, mapper.get(generatedURI), testedURI);
    }

    /**
     * Performs a generation of JSON-LD from RDF
     *
     * @param testedURI    The URI of the tested NQuads document
     * @param generatedURI The URI of the generated JSON-LD resulting document
     * @return The file that has been generated
     */
    private File generateJSONLDFromRdfFile(String testedURI, String generatedURI) {
        // the temporary generated file
        File file;
        try {
            file = File.createTempFile("tempXOWLTest", Repository.SYNTAX_JSON_LD_EXTENSION);
        } catch (IOException exception) {
            Assert.fail(exception.getMessage());
            return null;
        }

        // loads the tested file
        RepositoryRDF repository = new RepositoryRDF();
        repository.getIRIMapper().addRegexpMap(NAMESPACE + "(.*)", ResourceAccess.SCHEME_RESOURCE + PHYSICAL + "\\1");
        repository.getIRIMapper().addSimpleMap(generatedURI, ResourceAccess.SCHEME_FILE + file.getAbsolutePath());
        try {
            repository.load(logger, testedURI);
        } catch (Exception exception) {
            logger.error(exception);
        }
        Assert.assertFalse("Failed to load the ontology(ies)", logger.isOnError());

        // export the test file to a JSON-LD temporary file
        try {
            repository.exportAll(logger, generatedURI);
        } catch (Exception exception) {
            logger.error(exception);
        }
        Assert.assertFalse("Failed to generated the target", logger.isOnError());
        return file;
    }
}
