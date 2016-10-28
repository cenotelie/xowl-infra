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
package org.xowl.infra.store.entailment;

import org.junit.Assert;
import org.xowl.infra.lang.owl2.Ontology;
import org.xowl.infra.store.EntailmentRegime;
import org.xowl.infra.store.Repository;
import org.xowl.infra.store.rdf.*;
import org.xowl.infra.store.storage.UnsupportedNodeType;
import org.xowl.infra.utils.logging.SinkLogger;

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
        SinkLogger logger = new SinkLogger();

        // load the conclusion ontology at get all the quads in it
        Repository repository = new Repository();
        // add mapping for imported remote ontologies
        repository.getIRIMapper().addRegexpMap("http://www.w3.org/2002/03owlt/imports/(.*)", "resource:///imports/\\1.rdf");
        repository.getIRIMapper().addSimpleMap("http://xowl.org/store/tests/entailment/conclusion", "resource:///entailment/" + conclusionResource);
        Ontology ontologyConclusion = repository.load(logger, "http://xowl.org/store/tests/entailment/conclusion");
        List<Quad> conclusion = new ArrayList<>();
        try {
            Iterator<Quad> iterator = repository.getStore().getAll(repository.getGraph(ontologyConclusion));
            while (iterator.hasNext()) {
                conclusion.add(iterator.next());
            }
            repository.getStore().remove(repository.getGraph(ontologyConclusion), null, null, null);
        } catch (UnsupportedNodeType ex) {
            // cannot happen
        }

        // load the premise ontology and the default ontologies
        repository.getIRIMapper().addSimpleMap("http://xowl.org/store/tests/entailment/premise", "resource:///entailment/" + premiseResource);
        repository.setEntailmentRegime(logger, EntailmentRegime.OWL2_RDF);
        repository.load(logger, "http://xowl.org/store/tests/entailment/premise");
        Assert.assertFalse("Some error occurred", logger.isOnError());

        // query the premise for a matching conclusion, modulo the blank nodes
        RDFQuery query = new RDFQuery();
        Map<BlankNode, VariableNode> variables = new HashMap<>();
        for (Quad quad : conclusion) {
            SubjectNode nodeSubject = quad.getSubject();
            Property nodeProperty = quad.getProperty();
            Node nodeObject = quad.getObject();
            if (nodeSubject.getNodeType() == Node.TYPE_BLANK) {
                VariableNode variableNode = variables.get(nodeSubject);
                if (variableNode == null) {
                    variableNode = new VariableNode(UUID.randomUUID().toString());
                    variables.put((BlankNode) nodeSubject, variableNode);
                }
                nodeSubject = variableNode;
            }
            if (nodeObject.getNodeType() == Node.TYPE_BLANK) {
                VariableNode variableNode = variables.get(nodeObject);
                if (variableNode == null) {
                    variableNode = new VariableNode(UUID.randomUUID().toString());
                    variables.put((BlankNode) nodeObject, variableNode);
                }
                nodeObject = variableNode;
            }
            query.getPositives().add(new Quad(null, nodeSubject, nodeProperty, nodeObject));
        }

        Collection<RDFPatternSolution> solutions = repository.getRDFQueryEngine().execute(query);
        Assert.assertFalse("Entailment failed", solutions.isEmpty());
    }

    /**
     * Test of negative entailment
     *
     * @param premiseResource    The resource for the premise
     * @param conclusionResource The resource for the conclusion
     */
    protected void testNegativeEntailment(String premiseResource, String conclusionResource) {
        SinkLogger logger = new SinkLogger();

        // load the conclusion ontology at get all the quads in it
        Repository repository = new Repository();
        // add mapping for imported remote ontologies
        repository.getIRIMapper().addRegexpMap("http://www.w3.org/2002/03owlt/imports/(.*)", "resource:///imports/\\1.rdf");
        repository.getIRIMapper().addSimpleMap("http://xowl.org/store/tests/entailment/conclusion", "resource:///entailment/" + conclusionResource);
        Ontology ontologyConclusion = repository.load(logger, "http://xowl.org/store/tests/entailment/conclusion");
        List<Quad> conclusion = new ArrayList<>();
        try {
            Iterator<Quad> iterator = repository.getStore().getAll(repository.getGraph(ontologyConclusion));
            while (iterator.hasNext()) {
                conclusion.add(iterator.next());
            }
            repository.getStore().remove(repository.getGraph(ontologyConclusion), null, null, null);
        } catch (UnsupportedNodeType ex) {
            // cannot happen
        }

        // load the premise ontology and the default ontologies
        repository.getIRIMapper().addSimpleMap("http://xowl.org/store/tests/entailment/premise", "resource:///entailment/" + premiseResource);
        repository.setEntailmentRegime(logger, EntailmentRegime.OWL2_RDF);
        repository.load(logger, "http://xowl.org/store/tests/entailment/premise");
        Assert.assertFalse("Some error occurred", logger.isOnError());

        // query the premise for a matching conclusion, modulo the blank nodes
        RDFQuery query = new RDFQuery();
        Map<BlankNode, VariableNode> variables = new HashMap<>();
        for (Quad quad : conclusion) {
            SubjectNode nodeSubject = quad.getSubject();
            Property nodeProperty = quad.getProperty();
            Node nodeObject = quad.getObject();
            if (nodeSubject.getNodeType() == Node.TYPE_BLANK) {
                VariableNode variableNode = variables.get(nodeSubject);
                if (variableNode == null) {
                    variableNode = new VariableNode(UUID.randomUUID().toString());
                    variables.put((BlankNode) nodeSubject, variableNode);
                }
                nodeSubject = variableNode;
            }
            if (nodeObject.getNodeType() == Node.TYPE_BLANK) {
                VariableNode variableNode = variables.get(nodeObject);
                if (variableNode == null) {
                    variableNode = new VariableNode(UUID.randomUUID().toString());
                    variables.put((BlankNode) nodeObject, variableNode);
                }
                nodeObject = variableNode;
            }
            query.getPositives().add(new Quad(null, nodeSubject, nodeProperty, nodeObject));
        }

        Collection<RDFPatternSolution> solutions = repository.getRDFQueryEngine().execute(query);
        Assert.assertTrue("Erroneous entailment", solutions.isEmpty());
    }

    /**
     * Test of consistency
     *
     * @param premiseResource The resource for the premise
     */
    protected void testConsistency(String premiseResource) {
        SinkLogger logger = new SinkLogger();
        Repository repository = new Repository();
        // add mapping for imported remote ontologies
        repository.getIRIMapper().addRegexpMap("http://www.w3.org/2002/03owlt/imports/(.*)", "resource:///imports/\\1.rdf");
        repository.getIRIMapper().addSimpleMap("http://xowl.org/store/tests/entailment/premise", "resource:///entailment/" + premiseResource);

        // activate the default reasoning rules
        repository.setEntailmentRegime(logger, EntailmentRegime.OWL2_RDF);
        // load the premise ontology and the default ontologies
        Ontology ontologyPremise = repository.load(logger, "http://xowl.org/store/tests/entailment/premise");
        try {
            Iterator<Quad> iterator = repository.getStore().getAll(null, null, repository.getStore().getIRINode("http://xowl.org/store/rules/xowl#status"), repository.getStore().getIRINode("http://xowl.org/store/rules/xowl#inconsistent"));
            if (iterator.hasNext()) {
                StringBuilder builder = new StringBuilder("Spurious inconsistencies:");
                while (iterator.hasNext()) {
                    Quad quad = iterator.next();
                    builder.append(" ");
                    builder.append(quad.toString());
                }
                Assert.fail(builder.toString());
            }
        } catch (UnsupportedNodeType exception) {
            Assert.fail(exception.getMessage());
        }
    }

    /**
     * Test of inconsistency
     *
     * @param premiseResource The resource for the premise
     */
    protected void testInconsistency(String premiseResource) {
        SinkLogger logger = new SinkLogger();
        Repository repository = new Repository();
        repository.getIRIMapper().addSimpleMap("http://xowl.org/store/tests/entailment/premise", "resource:///entailment/" + premiseResource);

        // activate the default reasoning rules
        repository.setEntailmentRegime(logger, EntailmentRegime.OWL2_RDF);
        // load the premise ontology and the default ontologies
        Ontology ontologyPremise = repository.load(logger, "http://xowl.org/store/tests/entailment/premise");
        try {
            Iterator<Quad> iterator = repository.getStore().getAll(null, null, repository.getStore().getIRINode("http://xowl.org/store/rules/xowl#status"), repository.getStore().getIRINode("http://xowl.org/store/rules/xowl#inconsistent"));
            Assert.assertTrue("Failed to detect inconsistency", iterator.hasNext());
        } catch (UnsupportedNodeType exception) {
            Assert.fail(exception.getMessage());
        }
    }
}
