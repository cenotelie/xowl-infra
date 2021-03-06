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

import fr.cenotelie.commons.utils.logging.Logger;
import fr.cenotelie.commons.utils.logging.SinkLogger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.xowl.infra.store.IRIMapper;
import org.xowl.infra.store.RDFUtils;
import org.xowl.infra.store.Repository;
import org.xowl.infra.store.rdf.*;
import org.xowl.infra.store.storage.BaseStore;
import org.xowl.infra.store.storage.StoreFactory;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a base class for all W3C test suites
 *
 * @author Laurent Wouters
 */
public abstract class W3CTestSuite {
    /**
     * The logger
     */
    protected SinkLogger logger;
    /**
     * The store to use
     */
    protected BaseStore store;
    /**
     * The IRI mapper
     */
    protected IRIMapper mapper;

    @Before
    public void setup() throws IOException {
        logger = new SinkLogger();
        store = StoreFactory.create().make();
        mapper = IRIMapper.getDefault();
    }

    @After
    public void cleanup() {
        store = StoreFactory.create().make();
        logger.reset();
    }

    /**
     * Gets the loader for the specified resource
     *
     * @param resource A resource
     * @return The appropriate loader
     */
    protected Loader getLoader(String resource) {
        String syntax = Repository.getSyntax(resource);
        if (syntax == null)
            return null;
        switch (syntax) {
            case Repository.SYNTAX_NTRIPLES:
                return new NTriplesLoader(store);
            case Repository.SYNTAX_NQUADS:
                return new NQuadsLoader(store);
            case Repository.SYNTAX_TURTLE:
                return new TurtleLoader(store);
            case Repository.SYNTAX_RDFXML:
                return new RDFXMLLoader(store);
            case Repository.SYNTAX_JSON_LD:
                return new JsonLdLoader(store) {
                    @Override
                    protected Reader getReaderFor(Logger logger, String iri) {
                        String resource = mapper.get(iri);
                        if (resource == null) {
                            logger.error("Cannot identify the location of " + iri);
                            return null;
                        }
                        return getResourceReader(resource);
                    }
                };
            case Repository.SYNTAX_JSON:
                return new JsonLoader();
            case Repository.SYNTAX_TRIG:
                return new TriGLoader(store);
            case Repository.SYNTAX_XRDF:
                return new xRDFLoader();
        }
        return null;
    }

    /**
     * Gets a reader for a test resource
     *
     * @param resource A test resource
     * @return The reader
     */
    protected Reader getResourceReader(String resource) {
        try {
            return Repository.getReaderFor(resource);
        } catch (IOException exception) {
            Assert.fail(exception.getMessage());
            return null;
        }
    }

    /**
     * Tests the evaluation of a resource
     *
     * @param expectedResource Path to the expected resource
     * @param expectedURI      Expected resource's URI
     * @param testedResource   Path to the tested resource
     * @param testedURI        Tested resource's URI
     */
    protected void testEval(String expectedResource, String expectedURI, String testedResource, String testedURI) {
        List<Quad> expectedQuads = new ArrayList<>();
        List<Quad> testedQuads = new ArrayList<>();

        try (Reader reader = getResourceReader(expectedResource)) {
            Assert.assertNotNull("Failed to get a reader for the expected resource", reader);
            Loader loader = getLoader(expectedResource);
            Assert.assertNotNull("Failed to get a reader for the tested resource", loader);
            RDFLoaderResult expected = loader.loadRDF(logger, reader, expectedURI, expectedURI);
            Assert.assertFalse("Failed to parse expected resource " + expectedResource, logger.isOnError());
            Assert.assertNotNull("Failed to load expected resource " + expectedResource, expected);
            expectedQuads.addAll(expected.getQuads());
        } catch (IOException exception) {
            Assert.fail("Error while accessing resource " + expectedResource);
        }

        try (Reader reader = getResourceReader(testedResource)) {
            Assert.assertNotNull("Failed to get a reader for the expected resource", reader);
            Loader loader = getLoader(testedResource);
            Assert.assertNotNull("Failed to get a reader for the tested resource", loader);
            RDFLoaderResult tested = loader.loadRDF(logger, reader, testedURI, testedURI);
            Assert.assertFalse("Failed to parse tested resource " + testedResource, logger.isOnError());
            Assert.assertNotNull("Failed to load tested resource " + testedResource, tested);
            testedQuads.addAll(tested.getQuads());
        } catch (IOException exception) {
            Assert.fail("Error while accessing resource " + testedResource);
        }

        // rewrite the graph in the expected quads
        List<Quad> temp = new ArrayList<>();
        GraphNode target = store.getIRINode(testedURI);
        for (Quad quad : expectedQuads) {
            if (quad.getGraph().getNodeType() == Node.TYPE_IRI && ((IRINode) quad.getGraph()).getIRIValue().equals(expectedURI)) {
                temp.add(new Quad(target, quad.getSubject(), quad.getProperty(), quad.getObject()));
            } else {
                temp.add(quad);
            }
        }
        expectedQuads = temp;
        matchesQuads(expectedQuads, testedQuads);
    }

    /**
     * Tests that the specified resource is correctly loaded
     *
     * @param testedResource Path to the tested resource
     * @param testedURI      Tested resource's URI
     */
    protected void testPositiveSyntax(String testedResource, String testedURI) {
        try (Reader reader = getResourceReader(testedResource)) {
            Assert.assertNotNull("Failed to get a reader for the expected resource", reader);
            Loader loader = getLoader(testedResource);
            Assert.assertNotNull("Failed to get a reader for the tested resource", loader);
            RDFLoaderResult tested = loader.loadRDF(logger, reader, testedURI, testedURI);
            Assert.assertFalse("Failed to parse tested resource " + testedResource, logger.isOnError());
            Assert.assertNotNull("Failed to load tested resource " + testedResource, tested);
        } catch (IOException exception) {
            Assert.fail("Error while accessing resource " + testedResource);
        }
    }

    /**
     * Tests that the specified resource is not correctly loaded
     *
     * @param testedResource The physical path to the resource
     * @param testedURI      The resource's URI
     */
    protected void testNegativeSyntax(String testedResource, String testedURI) {
        try (Reader reader = getResourceReader(testedResource)) {

            Assert.assertNotNull("Failed to get a reader for the expected resource", reader);
            Loader loader = getLoader(testedResource);
            Assert.assertNotNull("Failed to get a reader for the tested resource", loader);
            RDFLoaderResult tested = loader.loadRDF(logger, reader, testedURI, testedURI);
            Assert.assertTrue("Failed to report error on bad input " + testedResource, logger.isOnError());
            Assert.assertNull("Mistakenly reported success of loading " + testedResource, tested);
        } catch (IOException exception) {
            Assert.fail("Error while accessing resource " + testedResource);
        }
    }

    /**
     * Eliminates the duplicate quads from a list
     *
     * @param quads A list of quads
     */
    private static void removeDuplicates(List<Quad> quads) {
        if (quads.isEmpty())
            return;
        for (int i = 0; i != quads.size() - 1; i++) {
            for (int j = i + 1; j != quads.size(); j++) {
                if (quads.get(i).equals(quads.get(j))) {
                    quads.remove(i);
                    i--;
                    break;
                }
            }
        }
    }

    /**
     * Tests whether two sets of quads describe the same dataset
     *
     * @param expected The expected set of quads
     * @param tested   The tested set of quads
     */
    public static void matchesQuads(List<Quad> expected, List<Quad> tested) {
        // eliminate duplicate quads
        removeDuplicates(expected);
        removeDuplicates(tested);

        Map<BlankNode, BlankNode> blanks = new HashMap<>();
        for (int i = 0; i != expected.size(); i++) {
            Quad quad = expected.get(i);
            if (quad.getSubject().getNodeType() != Node.TYPE_BLANK) {
                // ignore blank nodes at this time
                boolean found = false;
                for (Quad potential : tested) {
                    if (sameQuad(quad, potential, blanks)) {
                        found = true;
                        tested.remove(potential);
                        break;
                    }
                }
                if (found) {
                    expected.remove(i);
                    i--;
                } else {
                    Assert.fail("Expected triple not produced: " + quad.toString());
                }
            }
        }

        int size = expected.size() + 1;
        while (size != expected.size()) {
            // while no more modifications
            size = expected.size();
            for (int i = 0; i != expected.size(); i++) {
                Quad quad = expected.get(i);
                boolean found = false;
                for (Quad potential : tested) {
                    if (sameQuad(quad, potential, blanks)) {
                        found = true;
                        tested.remove(potential);
                        break;
                    }
                }
                if (found) {
                    expected.remove(i);
                    i--;
                } else {
                    Assert.fail("Expected triple not produced: " + quad.toString());
                }
            }
        }

        if (expected.size() != 0) {
            Assert.fail("Failed to match all triples");
        }

        for (Quad quad : tested) {
            // fail on supplementary triples
            Assert.fail("Unexpected triple produced: " + quad.toString());
        }
    }

    /**
     * Determines whether the specified quads are equivalent, using the given blank node mapping
     *
     * @param quad1  A quad
     * @param quad2  Another quad
     * @param blanks A map of blank nodes
     * @return <code>true</code> if the two quads are equivalent
     */
    public static boolean sameQuad(Quad quad1, Quad quad2, Map<BlankNode, BlankNode> blanks) {
        GraphNode graph = quad1.getGraph();
        SubjectNode subject = quad1.getSubject();
        Property property = quad1.getProperty();
        Node object = quad1.getObject();
        if (graph.getNodeType() == Node.TYPE_BLANK)
            graph = blanks.get(graph);
        if (subject.getNodeType() == Node.TYPE_BLANK)
            subject = blanks.get(subject);
        if (object.getNodeType() == Node.TYPE_BLANK)
            object = blanks.get(object);
        if (!RDFUtils.same(property, quad2.getProperty()))
            return false;
        if (graph != null && !RDFUtils.same(graph, quad2.getGraph()))
            return false;
        if (subject != null && !RDFUtils.same(subject, quad2.getSubject()))
            return false;
        if (object != null && !RDFUtils.same(object, quad2.getObject()))
            return false;
        if (graph == null && quad2.getGraph().getNodeType() != Node.TYPE_BLANK)
            return false;
        if (subject == null && quad2.getSubject().getNodeType() != Node.TYPE_BLANK)
            return false;
        if (object == null && quad2.getObject().getNodeType() != Node.TYPE_BLANK)
            return false;
        if (graph == null)
            blanks.put((BlankNode) quad1.getGraph(), (BlankNode) quad2.getGraph());
        if (subject == null)
            blanks.put((BlankNode) quad1.getSubject(), (BlankNode) quad2.getSubject());
        if (object == null)
            blanks.put((BlankNode) quad1.getObject(), (BlankNode) quad2.getObject());
        return true;
    }
}
