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

import org.junit.Assert;
import org.xowl.lang.owl2.Ontology;
import org.xowl.store.rdf.*;

import java.io.IOException;
import java.util.*;

/**
 * Base class for the JSON-LD loader tests
 *
 * @author Laurent Wouters
 */
public class BaseJSONLDTest {
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
        TestLogger logger = new TestLogger();

        // load the conclusion ontology at get all the quads in it
        Repository repository = null;
        try {
            repository = new Repository();
        } catch (IOException e) {
            Assert.fail("Failed to initialize the repository");
        }
        // add mapping for imported remote ontologies
        repository.getIRIMapper().addRegexpMap(NAMESPACE + "(.*)", "resource://" + PHYSICAL + "\\1");
        Ontology expectedOntology = repository.load(logger, expectedURI);
        List<Quad> expectedQuads = new ArrayList<>();
        Iterator<Quad> iterator = repository.getStore().getAll();
        while (iterator.hasNext())
            expectedQuads.add(iterator.next());
        repository.getStore().removeAll();

        // load the premise ontology and the default ontologies
        Ontology testedOntology = repository.load(logger, testedURI);
        List<Quad> testedQuads = new ArrayList<>();
        iterator = repository.getStore().getAll();
        while (iterator.hasNext())
            testedQuads.add(iterator.next());
        Assert.assertFalse("Some error occurred", logger.isOnError());

        if (expectedQuads.isEmpty() && testedQuads.isEmpty())
            // assert success here
            return;
        // query the premise for a matching conclusion, modulo the blank nodes
        Query query = new Query();
        Map<BlankNode, VariableNode> variables = new HashMap<>();
        for (Quad quad : expectedQuads) {
            GraphNode nodeGraph = quad.getGraph();
            SubjectNode nodeSubject = quad.getSubject();
            Property nodeProperty = quad.getProperty();
            Node nodeObject = quad.getObject();
            if (nodeGraph.getNodeType() == IRINode.TYPE && nodeGraph == repository.getGraph(expectedOntology))
                nodeGraph = repository.getGraph(testedOntology);
            if (nodeSubject.getNodeType() == BlankNode.TYPE) {
                VariableNode variableNode = variables.get(nodeSubject);
                if (variableNode == null) {
                    variableNode = new VariableNode(UUID.randomUUID().toString());
                    variables.put((BlankNode) nodeSubject, variableNode);
                }
                nodeSubject = variableNode;
            }
            if (nodeObject.getNodeType() == BlankNode.TYPE) {
                VariableNode variableNode = variables.get(nodeObject);
                if (variableNode == null) {
                    variableNode = new VariableNode(UUID.randomUUID().toString());
                    variables.put((BlankNode) nodeObject, variableNode);
                }
                nodeObject = variableNode;
            }
            query.getPositives().add(new Quad(nodeGraph, nodeSubject, nodeProperty, nodeObject));
        }

        Collection<QuerySolution> solutions = repository.getQueryEngine().getBackend().execute(query);
        Assert.assertFalse("Mismatch between the expected and tested content", solutions.isEmpty());
        Assert.assertEquals("Expected and tested content have different sizes", expectedQuads.size(), testedQuads.size());

    }
}
