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

package org.xowl.infra.store.rdf;

import fr.cenotelie.commons.utils.logging.SinkLogger;
import org.junit.Assert;
import org.junit.Test;
import org.xowl.infra.store.IRIs;
import org.xowl.infra.store.RDFUtils;
import org.xowl.infra.store.RepositoryRDF;
import org.xowl.infra.store.Vocabulary;
import org.xowl.infra.store.loaders.RDFLoaderResult;
import org.xowl.infra.store.loaders.W3CTestSuite;
import org.xowl.infra.store.loaders.xRDFLoader;
import org.xowl.infra.store.storage.UnsupportedNodeType;

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
public class RDFRuleEngineTest {
    /**
     * The default prefixes for loading xRDF rules
     */
    private static final String DEFAULT_PREFIXES = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
            "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
            "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> " +
            "PREFIX owl: <http://www.w3.org/2002/07/owl#> " +
            "PREFIX xowl: <http://xowl.org/infra/store/rules/xowl#> ";


    /**
     * Loads a xRDF rule from a string
     *
     * @param repository The current repository
     * @param input      The input to load from
     * @return The loaded rule
     */
    protected RDFRule loadXRDFRule(RepositoryRDF repository, String input) {
        SinkLogger logger = new SinkLogger();
        xRDFLoader loader = new xRDFLoader(repository);
        RDFLoaderResult result = loader.loadRDF(logger, new StringReader(DEFAULT_PREFIXES + input), IRIs.GRAPH_DEFAULT, IRIs.GRAPH_DEFAULT);
        Assert.assertFalse("Failed to load the rule", logger.isOnError());
        Assert.assertNotNull("No result", result);
        Assert.assertEquals("Expected exactly 1 rule", 1, result.getRules().size());
        return result.getRules().get(0);
    }

    @Test
    public void testSimpleRule() {
        RepositoryRDF repository = new RepositoryRDF();
        String rule = "rule xowl:test-rule { ?x rdf:type xowl:y . } => { ?x rdf:type xowl:z . }";
        IRINode x = repository.getStore().getIRINode("http://xowl.org/infra/store/rules/xowl#x");
        IRINode y = repository.getStore().getIRINode("http://xowl.org/infra/store/rules/xowl#y");
        IRINode z = repository.getStore().getIRINode("http://xowl.org/infra/store/rules/xowl#z");
        Quad q1 = new Quad(repository.getStore().getIRINode(IRIs.GRAPH_DEFAULT),
                x, repository.getStore().getIRINode(Vocabulary.rdfType), y);
        Quad q2 = new Quad(repository.getStore().getIRINode(IRIs.GRAPH_INFERENCE),
                x, repository.getStore().getIRINode(Vocabulary.rdfType), z);
        try {
            repository.getStore().add(q1);
            repository.getRDFRuleEngine().add(loadXRDFRule(repository, rule));
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
    public void testSelectRule() {
        RepositoryRDF repository = new RepositoryRDF();
        String rule = "rule xowl:test-rule { SELECT * WHERE {?x a xowl:y} } => { ?x rdf:type xowl:z . }";
        IRINode x = repository.getStore().getIRINode("http://xowl.org/infra/store/rules/xowl#x");
        IRINode y = repository.getStore().getIRINode("http://xowl.org/infra/store/rules/xowl#y");
        IRINode z = repository.getStore().getIRINode("http://xowl.org/infra/store/rules/xowl#z");
        Quad q1 = new Quad(repository.getStore().getIRINode(IRIs.GRAPH_DEFAULT),
                x, repository.getStore().getIRINode(Vocabulary.rdfType), y);
        Quad q2 = new Quad(repository.getStore().getIRINode(IRIs.GRAPH_INFERENCE),
                x, repository.getStore().getIRINode(Vocabulary.rdfType), z);
        try {
            repository.getStore().add(q1);
            repository.getRDFRuleEngine().add(loadXRDFRule(repository, rule));
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
        RepositoryRDF repository = new RepositoryRDF();
        String rule1 = "rule xowl:test-rule1 { ?x rdf:type xowl:y . } => { ?x rdf:type xowl:z . }";
        String rule2 = "rule xowl:test-rule2 { ?x rdf:type xowl:z . } => { ?x rdf:type xowl:x . }";
        IRINode x = repository.getStore().getIRINode("http://xowl.org/infra/store/rules/xowl#x");
        IRINode y = repository.getStore().getIRINode("http://xowl.org/infra/store/rules/xowl#y");
        IRINode z = repository.getStore().getIRINode("http://xowl.org/infra/store/rules/xowl#z");
        Quad q1 = new Quad(repository.getStore().getIRINode(IRIs.GRAPH_DEFAULT),
                x, repository.getStore().getIRINode(Vocabulary.rdfType), y);
        Quad q2 = new Quad(repository.getStore().getIRINode(IRIs.GRAPH_INFERENCE),
                x, repository.getStore().getIRINode(Vocabulary.rdfType), z);
        Quad q3 = new Quad(repository.getStore().getIRINode(IRIs.GRAPH_INFERENCE),
                x, repository.getStore().getIRINode(Vocabulary.rdfType), x);
        try {
            repository.getStore().add(q1);
            repository.getRDFRuleEngine().add(loadXRDFRule(repository, rule1));
            repository.getRDFRuleEngine().add(loadXRDFRule(repository, rule2));
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
        RepositoryRDF repository = new RepositoryRDF();
        String rule1 = "rule xowl:test-rule1 { ?x rdf:type xowl:y . } => { ?x rdf:type xowl:z . }";
        String rule2 = "rule xowl:test-rule2 { ?x rdf:type xowl:z . } => { ?x rdf:type xowl:y . }";
        IRINode x = repository.getStore().getIRINode("http://xowl.org/infra/store/rules/xowl#x");
        IRINode y = repository.getStore().getIRINode("http://xowl.org/infra/store/rules/xowl#y");
        IRINode z = repository.getStore().getIRINode("http://xowl.org/infra/store/rules/xowl#z");
        Quad q1 = new Quad(repository.getStore().getIRINode(IRIs.GRAPH_DEFAULT),
                x, repository.getStore().getIRINode(Vocabulary.rdfType), y);
        Quad q2 = new Quad(repository.getStore().getIRINode(IRIs.GRAPH_INFERENCE),
                x, repository.getStore().getIRINode(Vocabulary.rdfType), z);
        Quad q3 = new Quad(repository.getStore().getIRINode(IRIs.GRAPH_INFERENCE),
                x, repository.getStore().getIRINode(Vocabulary.rdfType), y);
        try {
            repository.getStore().add(q1);
            repository.getRDFRuleEngine().add(loadXRDFRule(repository, rule1));
            repository.getRDFRuleEngine().add(loadXRDFRule(repository, rule2));
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
        RepositoryRDF repository = new RepositoryRDF();
        String rule = "rule xowl:test-rule { ?x rdf:type xowl:y . } => { ?x rdf:type xowl:z . }";
        IRINode x = repository.getStore().getIRINode("http://xowl.org/infra/store/rules/xowl#x");
        IRINode y = repository.getStore().getIRINode("http://xowl.org/infra/store/rules/xowl#y");
        IRINode z = repository.getStore().getIRINode("http://xowl.org/infra/store/rules/xowl#z");
        Quad q1 = new Quad(repository.getStore().getIRINode(IRIs.GRAPH_DEFAULT),
                x, repository.getStore().getIRINode(Vocabulary.rdfType), y);
        Quad q2 = new Quad(repository.getStore().getIRINode(IRIs.GRAPH_INFERENCE),
                x, repository.getStore().getIRINode(Vocabulary.rdfType), z);
        try {
            repository.getStore().add(q1);
            repository.getRDFRuleEngine().add(loadXRDFRule(repository, rule));
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
    public void testSelectRetract() {
        RepositoryRDF repository = new RepositoryRDF();
        String rule = "rule xowl:test-rule { SELECT * WHERE {?x a xowl:y} } => { ?x rdf:type xowl:z . }";
        IRINode x = repository.getStore().getIRINode("http://xowl.org/infra/store/rules/xowl#x");
        IRINode y = repository.getStore().getIRINode("http://xowl.org/infra/store/rules/xowl#y");
        IRINode z = repository.getStore().getIRINode("http://xowl.org/infra/store/rules/xowl#z");
        Quad q1 = new Quad(repository.getStore().getIRINode(IRIs.GRAPH_DEFAULT),
                x, repository.getStore().getIRINode(Vocabulary.rdfType), y);
        Quad q2 = new Quad(repository.getStore().getIRINode(IRIs.GRAPH_INFERENCE),
                x, repository.getStore().getIRINode(Vocabulary.rdfType), z);
        try {
            repository.getStore().add(q1);
            repository.getRDFRuleEngine().add(loadXRDFRule(repository, rule));
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
        RepositoryRDF repository = new RepositoryRDF();
        String rule1 = "rule xowl:test-rule1 { ?x rdf:type xowl:y . } => { ?x rdf:type xowl:z . }";
        String rule2 = "rule xowl:test-rule2 { ?x rdf:type xowl:z . } => { ?x rdf:type xowl:x . }";
        IRINode x = repository.getStore().getIRINode("http://xowl.org/infra/store/rules/xowl#x");
        IRINode y = repository.getStore().getIRINode("http://xowl.org/infra/store/rules/xowl#y");
        IRINode z = repository.getStore().getIRINode("http://xowl.org/infra/store/rules/xowl#z");
        Quad q1 = new Quad(repository.getStore().getIRINode(IRIs.GRAPH_DEFAULT),
                x, repository.getStore().getIRINode(Vocabulary.rdfType), y);
        Quad q2 = new Quad(repository.getStore().getIRINode(IRIs.GRAPH_INFERENCE),
                x, repository.getStore().getIRINode(Vocabulary.rdfType), z);
        Quad q3 = new Quad(repository.getStore().getIRINode(IRIs.GRAPH_INFERENCE),
                x, repository.getStore().getIRINode(Vocabulary.rdfType), x);
        try {
            repository.getStore().add(q1);
            repository.getRDFRuleEngine().add(loadXRDFRule(repository, rule1));
            repository.getRDFRuleEngine().add(loadXRDFRule(repository, rule2));
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
        RepositoryRDF repository = new RepositoryRDF();
        String rule1 = "rule xowl:test-rule1 { ?x rdf:type xowl:y . } => { ?x rdf:type xowl:z . }";
        String rule2 = "rule xowl:test-rule2 { ?x rdf:type xowl:z . } => { ?x rdf:type xowl:y . }";
        IRINode x = repository.getStore().getIRINode("http://xowl.org/infra/store/rules/xowl#x");
        IRINode y = repository.getStore().getIRINode("http://xowl.org/infra/store/rules/xowl#y");
        IRINode z = repository.getStore().getIRINode("http://xowl.org/infra/store/rules/xowl#z");
        Quad q1 = new Quad(repository.getStore().getIRINode(IRIs.GRAPH_DEFAULT),
                x, repository.getStore().getIRINode(Vocabulary.rdfType), y);
        Quad q2 = new Quad(repository.getStore().getIRINode(IRIs.GRAPH_INFERENCE),
                x, repository.getStore().getIRINode(Vocabulary.rdfType), z);
        Quad q3 = new Quad(repository.getStore().getIRINode(IRIs.GRAPH_INFERENCE),
                x, repository.getStore().getIRINode(Vocabulary.rdfType), y);
        try {
            repository.getStore().add(q1);
            repository.getRDFRuleEngine().add(loadXRDFRule(repository, rule1));
            repository.getRDFRuleEngine().add(loadXRDFRule(repository, rule2));
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
    public void testSelectAggregator() {
        RepositoryRDF repository = new RepositoryRDF();
        String rule = "rule xowl:test-rule { SELECT (COUNT(?v) AS ?c) WHERE {xowl:x a ?v} } => { xowl:x xowl:value ?c . }";
        IRINode x = repository.getStore().getIRINode("http://xowl.org/infra/store/rules/xowl#x");
        IRINode y1 = repository.getStore().getIRINode("http://xowl.org/infra/store/rules/xowl#y1");
        IRINode y2 = repository.getStore().getIRINode("http://xowl.org/infra/store/rules/xowl#y2");
        IRINode y3 = repository.getStore().getIRINode("http://xowl.org/infra/store/rules/xowl#y3");
        IRINode value = repository.getStore().getIRINode("http://xowl.org/infra/store/rules/xowl#value");
        Quad qy1 = new Quad(repository.getStore().getIRINode(IRIs.GRAPH_DEFAULT),
                x, repository.getStore().getIRINode(Vocabulary.rdfType), y1);
        Quad qy2 = new Quad(repository.getStore().getIRINode(IRIs.GRAPH_DEFAULT),
                x, repository.getStore().getIRINode(Vocabulary.rdfType), y2);
        Quad qy3 = new Quad(repository.getStore().getIRINode(IRIs.GRAPH_DEFAULT),
                x, repository.getStore().getIRINode(Vocabulary.rdfType), y3);
        try {
            repository.getStore().add(qy1);
            repository.getStore().add(qy2);
            repository.getRDFRuleEngine().add(loadXRDFRule(repository, rule));
            repository.getRDFRuleEngine().flush();

            List<Quad> content = new ArrayList<>();
            Iterator<Quad> iterator = repository.getStore().getAll(x, value, null);
            while (iterator.hasNext())
                content.add(iterator.next());
            Assert.assertEquals("Unexpected inferences", 1, content.size());
            Assert.assertTrue("Unexpected inferences", RDFUtils.same(content.get(0).getObject(), repository.getStore().getLiteralNode("2", Vocabulary.xsdInteger, null)));

            repository.getStore().add(qy3);
            repository.getRDFRuleEngine().flush();

            content.clear();
            iterator = repository.getStore().getAll(x, value, null);
            while (iterator.hasNext())
                content.add(iterator.next());
            Assert.assertEquals("Unexpected inferences", 1, content.size());
            Assert.assertTrue("Unexpected inferences", RDFUtils.same(content.get(0).getObject(), repository.getStore().getLiteralNode("3", Vocabulary.xsdInteger, null)));
        } catch (UnsupportedNodeType exception) {
            Assert.fail(exception.getMessage());
        }
    }
}
