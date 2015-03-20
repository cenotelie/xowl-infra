/**********************************************************************
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
 **********************************************************************/
package org.xowl.store;

import org.junit.Assert;
import org.xowl.lang.owl2.Ontology;
import org.xowl.store.rdf.*;

import java.io.IOException;
import java.util.*;

/**
 * Base class for entailment tests for the built-in reasoning rules
 *
 * @author Laurent Wouters
 */
public class BaseOWLReasoningTest {
    /**
     * Test of positive entailment
     *
     * @param premiseResource    The resource for the premise
     * @param conclusionResource The resource for the conclusion
     */
    protected void testPositiveEntailment(String premiseResource, String conclusionResource) {
        TestLogger logger = new TestLogger();

        // load the conclusion ontology at get all the quads in it
        Repository repository = null;
        try {
            repository = new Repository();
        } catch (IOException e) {
            Assert.fail("Failed to initialize the repository");
        }
        repository.getIRIMapper().addSimpleMap("http://xowl.org/store/tests/entailment/conclusion", "resource:///entailment/" + conclusionResource);
        Ontology ontologyConclusion = repository.load(logger, "http://xowl.org/store/tests/entailment/conclusion");
        List<Quad> conclusion = new ArrayList<>();
        Iterator<Quad> iterator = repository.getBackend().getAll(repository.getGraph(ontologyConclusion));
        while (iterator.hasNext()) {
            conclusion.add(iterator.next());
        }

        // load the premise ontology and the default ontologies
        try {
            repository = new Repository();
        } catch (IOException e) {
            Assert.fail("Failed to initialize the repository");
        }
        repository.getIRIMapper().addSimpleMap("http://xowl.org/store/tests/entailment/premise", "resource:///entailment/" + premiseResource);
        repository.addEntailmentRulesForOWL2_RDFBasedSemantics(logger);
        Ontology ontologyPremise = repository.load(logger, "http://xowl.org/store/tests/entailment/premise");
        Assert.assertFalse("Some error occurred", logger.isOnError());

        // query the premise for a matching conclusion, modulo the blank nodes
        Query query = new Query();
        Map<BlankNode, VariableNode> variables = new HashMap<>();
        for (Quad quad : conclusion) {
            GraphNode nodeGraph = repository.getGraph(ontologyPremise);
            SubjectNode nodeSubject = quad.getSubject();
            Property nodeProperty = quad.getProperty();
            Node nodeObject = quad.getObject();
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
        Assert.assertFalse("Entailment failed", solutions.isEmpty());
    }

    /**
     * Test of negative entailment
     *
     * @param premiseResource    The resource for the premise
     * @param conclusionResource The resource for the conclusion
     */
    protected void testNegativeEntailment(String premiseResource, String conclusionResource) {
        TestLogger logger = new TestLogger();

        // load the conclusion ontology at get all the quads in it
        Repository repository = null;
        try {
            repository = new Repository();
        } catch (IOException e) {
            Assert.fail("Failed to initialize the repository");
        }
        repository.getIRIMapper().addSimpleMap("http://xowl.org/store/tests/entailment/conclusion", "resource:///entailment/" + conclusionResource);
        Ontology ontologyConclusion = repository.load(logger, "http://xowl.org/store/tests/entailment/conclusion");
        List<Quad> conclusion = new ArrayList<>();
        Iterator<Quad> iterator = repository.getBackend().getAll(repository.getGraph(ontologyConclusion));
        while (iterator.hasNext()) {
            conclusion.add(iterator.next());
        }

        // load the premise ontology and the default ontologies
        try {
            repository = new Repository();
        } catch (IOException e) {
            Assert.fail("Failed to initialize the repository");
        }
        repository.getIRIMapper().addSimpleMap("http://xowl.org/store/tests/entailment/premise", "resource:///entailment/" + premiseResource);
        repository.addEntailmentRulesForOWL2_RDFBasedSemantics(logger);
        Ontology ontologyPremise = repository.load(logger, "http://xowl.org/store/tests/entailment/premise");
        Assert.assertFalse("Some error occurred", logger.isOnError());

        // query the premise for a matching conclusion, modulo the blank nodes
        Query query = new Query();
        Map<BlankNode, VariableNode> variables = new HashMap<>();
        for (Quad quad : conclusion) {
            GraphNode nodeGraph = repository.getGraph(ontologyPremise);
            SubjectNode nodeSubject = quad.getSubject();
            Property nodeProperty = quad.getProperty();
            Node nodeObject = quad.getObject();
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
        Assert.assertTrue("Erroneous entailment", solutions.isEmpty());
    }

    /**
     * Test of consistency
     *
     * @param premiseResource The resource for the premise
     */
    protected void testConsistency(String premiseResource) {
        TestLogger logger = new TestLogger();
        Repository repository = null;
        try {
            repository = new Repository();
        } catch (IOException e) {
            Assert.fail("Failed to initialize the repository");
        }
        repository.getIRIMapper().addSimpleMap("http://xowl.org/store/tests/entailment/premise", "resource:///entailment/" + premiseResource);

        // activate the default reasoning rules
        repository.addEntailmentRulesForOWL2_RDFBasedSemantics(logger);
        // load the premise ontology and the default ontologies
        Ontology ontologyPremise = repository.load(logger, "http://xowl.org/store/tests/entailment/premise");
        Iterator<Quad> iterator = repository.getBackend().getAll(repository.getGraph(ontologyPremise), null, repository.getBackend().getNodeIRI("http://xowl.org/store/rules/xowl#status"), repository.getBackend().getNodeIRI("http://xowl.org/store/rules/xowl#inconsistent"));
        if (iterator.hasNext()) {
            StringBuilder builder = new StringBuilder("Spurious inconsistencies:");
            while (iterator.hasNext()) {
                Quad quad = iterator.next();
                builder.append(" ");
                builder.append(quad.toString());
            }
            Assert.fail(builder.toString());
        }
    }

    /**
     * Test of inconsistency
     *
     * @param premiseResource The resource for the premise
     */
    protected void testInconsistency(String premiseResource) {
        TestLogger logger = new TestLogger();
        Repository repository = null;
        try {
            repository = new Repository();
        } catch (IOException e) {
            Assert.fail("Failed to initialize the repository");
        }
        repository.getIRIMapper().addSimpleMap("http://xowl.org/store/tests/entailment/premise", "resource:///entailment/" + premiseResource);

        // activate the default reasoning rules
        repository.addEntailmentRulesForOWL2_RDFBasedSemantics(logger);
        // load the premise ontology and the default ontologies
        Ontology ontologyPremise = repository.load(logger, "http://xowl.org/store/tests/entailment/premise");
        Iterator<Quad> iterator = repository.getBackend().getAll(repository.getGraph(ontologyPremise), null, repository.getBackend().getNodeIRI("http://xowl.org/store/rules/xowl#status"), repository.getBackend().getNodeIRI("http://xowl.org/store/rules/xowl#inconsistent"));
        Assert.assertTrue("Failed to detect inconsistency", iterator.hasNext());
    }
}
