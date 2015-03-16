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
import org.xowl.store.rdf.BlankNode;
import org.xowl.store.rdf.Quad;

import java.io.IOException;
import java.util.*;

/**
 * Base class for entailment tests for the built-in reasoning rules
 *
 * @author Laurent Wouters
 */
public class OWLEntailmentTest {
    /**
     * Test of positive entailment
     *
     * @param premiseResource    The resource for the premise
     * @param conclusionResource The resource for the conclusion
     */
    protected void testPositiveEntailment(String premiseResource, String conclusionResource) {
        TestLogger logger = new TestLogger();
        Repository repository = null;
        try {
            repository = new Repository();
        } catch (IOException e) {
            Assert.fail("Failed to initialize the repository");
        }
        repository.getIRIMapper().addSimpleMap("http://xowl.org/store/tests/entailment/premise", "resource:///entailment/" + premiseResource);
        repository.getIRIMapper().addSimpleMap("http://xowl.org/store/tests/entailment/conclusion", "resource:///entailment/" + conclusionResource);

        // load the conclusion ontology at get all the quads in it
        Ontology ontologyConclusion = repository.load(logger, "http://xowl.org/store/tests/entailment/conclusion");
        List<Quad> conclusion = new ArrayList<>();
        Iterator<Quad> iterator = repository.getBackend().getAll(repository.getGraph(ontologyConclusion));
        while (iterator.hasNext()) {
            conclusion.add(iterator.next());
        }

        // activate the default reasoning rules
        repository.addEntailmentRulesForOWL2_RDFBasedSemantics(logger);
        // load the premise ontology and the default ontologies
        Ontology ontologyPremise = repository.load(logger, "http://xowl.org/store/tests/entailment/premise");
        List<Quad> premise = new ArrayList<>();
        iterator = repository.getBackend().getAll(repository.getGraph(ontologyPremise));
        while (iterator.hasNext()) {
            premise.add(iterator.next());
        }
        iterator = repository.getBackend().getAll(repository.getGraph(repository.resolveOntology("http://www.w3.org/2002/07/owl")));
        while (iterator.hasNext()) {
            premise.add(iterator.next());
        }

        Map<BlankNode, BlankNode> blanks = new HashMap<>();
        for (Quad quad : conclusion) {
            boolean found = false;
            for (Quad candidate : premise) {
                if (W3CTestSuite.sameTriple(quad, candidate, blanks)) {
                    found = true;
                    break;
                }
            }
            if (!found)
                Assert.fail("Expected triple not produced: " + quad.toString());
        }

        Assert.assertFalse("Some error occurred", logger.isOnError());
    }
}
