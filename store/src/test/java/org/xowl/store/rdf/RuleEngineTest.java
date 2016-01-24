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

package org.xowl.store.rdf;

import org.junit.Assert;
import org.junit.Test;
import org.xowl.store.IRIs;
import org.xowl.store.Repository;
import org.xowl.store.Vocabulary;
import org.xowl.store.loaders.W3CTestSuite;
import org.xowl.store.loaders.RDFLoaderResult;
import org.xowl.store.loaders.RDFTLoader;
import org.xowl.store.storage.UnsupportedNodeType;
import org.xowl.infra.utils.logging.SinkLogger;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Tests for the RDF rule engine
 *
 * @author Laurent Wouters
 */
public class RuleEngineTest {
    /**
     * The default prefixes for loading RDFT rules
     */
    private static final String DEFAULT_PREFIXES = "@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>. " +
            "@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>. " +
            "@prefix xsd: <http://www.w3.org/2001/XMLSchema#>. " +
            "@prefix owl: <http://www.w3.org/2002/07/owl#>. " +
            "@prefix xowl: <http://xowl.org/store/rules/xowl#>. ";


    /**
     * Loads a RDFT rule from a string
     *
     * @param repository The current repository
     * @param input      The input to load from
     * @return The loaded rule
     */
    protected Rule loadRDFTRule(Repository repository, String input) {
        SinkLogger logger = new SinkLogger();
        RDFTLoader loader = new RDFTLoader(repository.getStore());
        RDFLoaderResult result = loader.loadRDF(logger, new StringReader(DEFAULT_PREFIXES + input), IRIs.GRAPH_DEFAULT, IRIs.GRAPH_DEFAULT);
        Assert.assertFalse("Failed to load the rule", logger.isOnError());
        Assert.assertNotNull("No result", result);
        Assert.assertEquals("Expected exactly 1 rule", 1, result.getRules().size());
        return result.getRules().get(0);
    }

    @Test
    public void testSimpleInference() {
        Repository repository = new Repository();
        String rule = "rule xowl:test-rule { ?x rdf:type xowl:y } => { ?x rdf:type xowl:z }";
        IRINode x = repository.getStore().getIRINode("http://xowl.org/store/rules/xowl#x");
        IRINode y = repository.getStore().getIRINode("http://xowl.org/store/rules/xowl#y");
        IRINode z = repository.getStore().getIRINode("http://xowl.org/store/rules/xowl#z");
        Quad q1 = new Quad(repository.getStore().getIRINode(IRIs.GRAPH_DEFAULT),
                x, repository.getStore().getIRINode(Vocabulary.rdfType), y);
        Quad q2 = new Quad(repository.getStore().getIRINode(IRIs.GRAPH_INFERENCE),
                x, repository.getStore().getIRINode(Vocabulary.rdfType), z);
        try {
            repository.getStore().add(q1);
            repository.getRDFRuleEngine().add(loadRDFTRule(repository, rule));
            repository.getRDFRuleEngine().flush();
            Iterator<Quad> iterator = repository.getStore().getAll(x, null, null);
            List<Quad> content = new ArrayList<>();
            while (iterator.hasNext())
                content.add(iterator.next());
            W3CTestSuite.matchesQuads(new ArrayList<>(Arrays.asList(q1, q2)), content);
        } catch (UnsupportedNodeType exception) {
            Assert.fail(exception.getMessage());
        }
    }

    @Test
    public void testRuleChaining() {
        Repository repository = new Repository();
        String rule1 = "rule xowl:test-rule { ?x rdf:type xowl:y } => { ?x rdf:type xowl:z }";
        String rule2 = "rule xowl:test-rule { ?x rdf:type xowl:z } => { ?x rdf:type xowl:x }";
        IRINode x = repository.getStore().getIRINode("http://xowl.org/store/rules/xowl#x");
        IRINode y = repository.getStore().getIRINode("http://xowl.org/store/rules/xowl#y");
        IRINode z = repository.getStore().getIRINode("http://xowl.org/store/rules/xowl#z");
        Quad q1 = new Quad(repository.getStore().getIRINode(IRIs.GRAPH_DEFAULT),
                x, repository.getStore().getIRINode(Vocabulary.rdfType), y);
        Quad q2 = new Quad(repository.getStore().getIRINode(IRIs.GRAPH_INFERENCE),
                x, repository.getStore().getIRINode(Vocabulary.rdfType), z);
        Quad q3 = new Quad(repository.getStore().getIRINode(IRIs.GRAPH_INFERENCE),
                x, repository.getStore().getIRINode(Vocabulary.rdfType), x);
        try {
            repository.getStore().add(q1);
            repository.getRDFRuleEngine().add(loadRDFTRule(repository, rule1));
            repository.getRDFRuleEngine().add(loadRDFTRule(repository, rule2));
            repository.getRDFRuleEngine().flush();
            Iterator<Quad> iterator = repository.getStore().getAll(x, null, null);
            List<Quad> content = new ArrayList<>();
            while (iterator.hasNext())
                content.add(iterator.next());
            W3CTestSuite.matchesQuads(new ArrayList<>(Arrays.asList(q1, q2, q3)), content);
        } catch (UnsupportedNodeType exception) {
            Assert.fail(exception.getMessage());
        }
    }

    @Test
    public void testRuleLoop() {
        Repository repository = new Repository();
        String rule1 = "rule xowl:test-rule { ?x rdf:type xowl:y } => { ?x rdf:type xowl:z }";
        String rule2 = "rule xowl:test-rule { ?x rdf:type xowl:z } => { ?x rdf:type xowl:y }";
        IRINode x = repository.getStore().getIRINode("http://xowl.org/store/rules/xowl#x");
        IRINode y = repository.getStore().getIRINode("http://xowl.org/store/rules/xowl#y");
        IRINode z = repository.getStore().getIRINode("http://xowl.org/store/rules/xowl#z");
        Quad q1 = new Quad(repository.getStore().getIRINode(IRIs.GRAPH_DEFAULT),
                x, repository.getStore().getIRINode(Vocabulary.rdfType), y);
        Quad q2 = new Quad(repository.getStore().getIRINode(IRIs.GRAPH_INFERENCE),
                x, repository.getStore().getIRINode(Vocabulary.rdfType), z);
        Quad q3 = new Quad(repository.getStore().getIRINode(IRIs.GRAPH_INFERENCE),
                x, repository.getStore().getIRINode(Vocabulary.rdfType), y);
        try {
            repository.getStore().add(q1);
            repository.getRDFRuleEngine().add(loadRDFTRule(repository, rule1));
            repository.getRDFRuleEngine().add(loadRDFTRule(repository, rule2));
            repository.getRDFRuleEngine().flush();
            Iterator<Quad> iterator = repository.getStore().getAll(x, null, null);
            List<Quad> content = new ArrayList<>();
            while (iterator.hasNext())
                content.add(iterator.next());
            W3CTestSuite.matchesQuads(new ArrayList<>(Arrays.asList(q1, q2, q3)), content);
        } catch (UnsupportedNodeType exception) {
            Assert.fail(exception.getMessage());
        }
    }

    @Test
    public void testSimpleRetract() {
        Repository repository = new Repository();
        String rule = "rule xowl:test-rule { ?x rdf:type xowl:y } => { ?x rdf:type xowl:z }";
        IRINode x = repository.getStore().getIRINode("http://xowl.org/store/rules/xowl#x");
        IRINode y = repository.getStore().getIRINode("http://xowl.org/store/rules/xowl#y");
        IRINode z = repository.getStore().getIRINode("http://xowl.org/store/rules/xowl#z");
        Quad q1 = new Quad(repository.getStore().getIRINode(IRIs.GRAPH_DEFAULT),
                x, repository.getStore().getIRINode(Vocabulary.rdfType), y);
        Quad q2 = new Quad(repository.getStore().getIRINode(IRIs.GRAPH_INFERENCE),
                x, repository.getStore().getIRINode(Vocabulary.rdfType), z);
        try {
            repository.getStore().add(q1);
            repository.getRDFRuleEngine().add(loadRDFTRule(repository, rule));
            repository.getRDFRuleEngine().flush();
            Iterator<Quad> iterator = repository.getStore().getAll(x, null, null);
            List<Quad> content = new ArrayList<>();
            while (iterator.hasNext())
                content.add(iterator.next());
            W3CTestSuite.matchesQuads(new ArrayList<>(Arrays.asList(q1, q2)), content);
            repository.getStore().remove(q1);
            iterator = repository.getStore().getAll(x, null, null);
            Assert.assertFalse("Should not have quads", iterator.hasNext());
        } catch (UnsupportedNodeType exception) {
            Assert.fail(exception.getMessage());
        }
    }

    @Test
    public void testRetractChaining() {
        Repository repository = new Repository();
        String rule1 = "rule xowl:test-rule { ?x rdf:type xowl:y } => { ?x rdf:type xowl:z }";
        String rule2 = "rule xowl:test-rule { ?x rdf:type xowl:z } => { ?x rdf:type xowl:x }";
        IRINode x = repository.getStore().getIRINode("http://xowl.org/store/rules/xowl#x");
        IRINode y = repository.getStore().getIRINode("http://xowl.org/store/rules/xowl#y");
        IRINode z = repository.getStore().getIRINode("http://xowl.org/store/rules/xowl#z");
        Quad q1 = new Quad(repository.getStore().getIRINode(IRIs.GRAPH_DEFAULT),
                x, repository.getStore().getIRINode(Vocabulary.rdfType), y);
        Quad q2 = new Quad(repository.getStore().getIRINode(IRIs.GRAPH_INFERENCE),
                x, repository.getStore().getIRINode(Vocabulary.rdfType), z);
        Quad q3 = new Quad(repository.getStore().getIRINode(IRIs.GRAPH_INFERENCE),
                x, repository.getStore().getIRINode(Vocabulary.rdfType), x);
        try {
            repository.getStore().add(q1);
            repository.getRDFRuleEngine().add(loadRDFTRule(repository, rule1));
            repository.getRDFRuleEngine().add(loadRDFTRule(repository, rule2));
            repository.getRDFRuleEngine().flush();
            Iterator<Quad> iterator = repository.getStore().getAll(x, null, null);
            List<Quad> content = new ArrayList<>();
            while (iterator.hasNext())
                content.add(iterator.next());
            W3CTestSuite.matchesQuads(new ArrayList<>(Arrays.asList(q1, q2, q3)), content);
            repository.getStore().remove(q1);
            iterator = repository.getStore().getAll(x, null, null);
            Assert.assertFalse("Should not have quads", iterator.hasNext());
        } catch (UnsupportedNodeType exception) {
            Assert.fail(exception.getMessage());
        }
    }

    @Test
    public void testRetractLoop() {
        Repository repository = new Repository();
        String rule1 = "rule xowl:test-rule { ?x rdf:type xowl:y } => { ?x rdf:type xowl:z }";
        String rule2 = "rule xowl:test-rule { ?x rdf:type xowl:z } => { ?x rdf:type xowl:y }";
        IRINode x = repository.getStore().getIRINode("http://xowl.org/store/rules/xowl#x");
        IRINode y = repository.getStore().getIRINode("http://xowl.org/store/rules/xowl#y");
        IRINode z = repository.getStore().getIRINode("http://xowl.org/store/rules/xowl#z");
        Quad q1 = new Quad(repository.getStore().getIRINode(IRIs.GRAPH_DEFAULT),
                x, repository.getStore().getIRINode(Vocabulary.rdfType), y);
        Quad q2 = new Quad(repository.getStore().getIRINode(IRIs.GRAPH_INFERENCE),
                x, repository.getStore().getIRINode(Vocabulary.rdfType), z);
        Quad q3 = new Quad(repository.getStore().getIRINode(IRIs.GRAPH_INFERENCE),
                x, repository.getStore().getIRINode(Vocabulary.rdfType), y);
        try {
            repository.getStore().add(q1);
            repository.getRDFRuleEngine().add(loadRDFTRule(repository, rule1));
            repository.getRDFRuleEngine().add(loadRDFTRule(repository, rule2));
            repository.getRDFRuleEngine().flush();
            Iterator<Quad> iterator = repository.getStore().getAll(x, null, null);
            List<Quad> content = new ArrayList<>();
            while (iterator.hasNext())
                content.add(iterator.next());
            W3CTestSuite.matchesQuads(new ArrayList<>(Arrays.asList(q1, q2, q3)), content);
            repository.getStore().remove(q1);
            iterator = repository.getStore().getAll(x, null, null);
            Assert.assertFalse("Should not have quads", iterator.hasNext());
        } catch (UnsupportedNodeType exception) {
            Assert.fail(exception.getMessage());
        }
    }
}
