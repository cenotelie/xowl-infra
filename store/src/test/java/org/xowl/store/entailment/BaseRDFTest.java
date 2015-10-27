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

package org.xowl.store.entailment;

import org.junit.Assert;
import org.xowl.store.EntailmentRegime;
import org.xowl.store.Repository;
import org.xowl.store.TestLogger;
import org.xowl.store.Vocabulary;
import org.xowl.store.loaders.W3CTestSuite;
import org.xowl.store.rdf.Quad;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Base test suite for the RDF entailment regimes
 *
 * @author Laurent Wouters
 */
public class BaseRDFTest {
    /**
     * Positive test of entailment
     *
     * @param input  The input data
     * @param regime The entailment regime
     * @param result The expected result, or null if inconsistency shall be detected
     */
    protected void testPositiveEntailment(String input, EntailmentRegime regime, String result) {
        TestLogger logger = new TestLogger();
        Repository repository = new Repository();
        repository.getIRIMapper().addRegexpMap("http://www.w3.org/2013/rdf-mt-tests/(.*)", "resource:///rdf-mt/\\1");
        repository.activateEntailmentRules(logger, regime);
        Assert.assertFalse("Failed to activate the entailment regime", logger.isOnError());
        repository.load(logger, input);
        Assert.assertFalse("Failed to load the input", logger.isOnError());

        if (result == null) {
            // expect the detection of an inconsistency
            Iterator<Quad> iterator = repository.getStore().getAll(
                    null,
                    repository.getStore().getIRINode(Vocabulary.xowlStatus),
                    repository.getStore().getIRINode(Vocabulary.xowlInconsistent));
            Assert.assertTrue("Failed to detect the inconsistency", iterator.hasNext());
        } else {
            Iterator<Quad> iterator = repository.getStore().getAll();
            List<Quad> tested = new ArrayList<>();
            while (iterator.hasNext())
                tested.add(iterator.next());
            List<Quad> expected = load(result);
            for (Quad q : expected) {
                Assert.assertTrue("Failed to entail " + q, repository.getStore().count(null, q.getSubject(), q.getProperty(), q.getObject()) > 0);
            }
            W3CTestSuite.matchesQuads(expected, tested);
        }
    }

    /**
     * Negative test of entailment
     *
     * @param input  The input data
     * @param regime The entailment regime
     * @param result The expected result, or null if inconsistency shall be detected
     */
    protected void testNegativeEntailment(String input, EntailmentRegime regime, String result) {
        TestLogger logger = new TestLogger();
        Repository repository = new Repository();
        repository.getIRIMapper().addRegexpMap("http://www.w3.org/2013/rdf-mt-tests/(.*)", "resource:///rdf-mt/\\1");
        repository.activateEntailmentRules(logger, regime);
        Assert.assertFalse("Failed to activate the entailment regime", logger.isOnError());
        repository.load(logger, input);
        Assert.assertFalse("Failed to load the input", logger.isOnError());

        if (result == null) {
            // expect the detection of an inconsistency
            Iterator<Quad> iterator = repository.getStore().getAll(
                    null,
                    repository.getStore().getIRINode(Vocabulary.xowlStatus),
                    repository.getStore().getIRINode(Vocabulary.xowlInconsistent));
            Assert.assertFalse("Incorrectly reported inconsistency", iterator.hasNext());
        } else {
            Iterator<Quad> iterator = repository.getStore().getAll();
            List<Quad> tested = new ArrayList<>();
            while (iterator.hasNext())
                tested.add(iterator.next());
            List<Quad> expected = load(result);
            for (Quad q : expected) {
                Assert.assertFalse("Incorrectly entailed " + q, repository.getStore().count(null, q.getSubject(), q.getProperty(), q.getObject()) > 0);
            }
            W3CTestSuite.matchesQuads(expected, tested);
        }
    }

    /**
     * Loads all the quads contained in a resource
     *
     * @param resource The resource to load
     * @return The contained quads
     */
    private List<Quad> load(String resource) {
        TestLogger logger = new TestLogger();
        Repository repository = new Repository();
        repository.getIRIMapper().addRegexpMap("http://www.w3.org/2013/rdf-mt-tests/(.*)", "resource:///rdf-mt/\\1");
        Assert.assertFalse("Failed to activate the entailment regime", logger.isOnError());
        repository.load(logger, resource);
        Assert.assertFalse("Failed to load the resource", logger.isOnError());
        Iterator<Quad> iterator = repository.getStore().getAll();
        List<Quad> result = new ArrayList<>();
        while (iterator.hasNext())
            result.add(iterator.next());
        return result;
    }
}