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
    public void testSimpleRule() throws Exception {
        try (RepositoryRDF repositoryRdf = new RepositoryRDF()) {
            String rule = "rule xowl:test-rule { ?x rdf:type xowl:y . } => { ?x rdf:type xowl:z . }";
            repositoryRdf.runAsTransaction((repository, transaction) -> {
                IRINode x = transaction.getDataset().getIRINode("http://xowl.org/infra/store/rules/xowl#x");
                IRINode y = transaction.getDataset().getIRINode("http://xowl.org/infra/store/rules/xowl#y");
                IRINode z = transaction.getDataset().getIRINode("http://xowl.org/infra/store/rules/xowl#z");
                Quad q1 = new Quad(transaction.getDataset().getIRINode(IRIs.GRAPH_DEFAULT),
                        x, transaction.getDataset().getIRINode(Vocabulary.rdfType), y);
                Quad q2 = new Quad(transaction.getDataset().getIRINode(IRIs.GRAPH_INFERENCE),
                        x, transaction.getDataset().getIRINode(Vocabulary.rdfType), z);

                transaction.getDataset().add(q1);
                repository.getRDFRuleEngine().add(loadXRDFRule(repository, rule));
                repository.getRDFRuleEngine().flush();
                Iterator<? extends Quad> iterator = transaction.getDataset().getAll(x, null, null);
                List<Quad> content = new ArrayList<>();
                while (iterator.hasNext())
                    content.add(iterator.next());
                W3CTestSuite.matchesQuads(new ArrayList<>(Arrays.asList(q1, q2)), content);
                return null;
            }, true);
        }
    }

    @Test
    public void testSelectRule() throws Exception {
        try (RepositoryRDF repositoryRdf = new RepositoryRDF()) {
            String rule = "rule xowl:test-rule { SELECT * WHERE {?x a xowl:y} } => { ?x rdf:type xowl:z . }";
            repositoryRdf.runAsTransaction((repository, transaction) -> {
                IRINode x = transaction.getDataset().getIRINode("http://xowl.org/infra/store/rules/xowl#x");
                IRINode y = transaction.getDataset().getIRINode("http://xowl.org/infra/store/rules/xowl#y");
                IRINode z = transaction.getDataset().getIRINode("http://xowl.org/infra/store/rules/xowl#z");
                Quad q1 = new Quad(transaction.getDataset().getIRINode(IRIs.GRAPH_DEFAULT),
                        x, transaction.getDataset().getIRINode(Vocabulary.rdfType), y);
                Quad q2 = new Quad(transaction.getDataset().getIRINode(IRIs.GRAPH_INFERENCE),
                        x, transaction.getDataset().getIRINode(Vocabulary.rdfType), z);

                transaction.getDataset().add(q1);
                repository.getRDFRuleEngine().add(loadXRDFRule(repository, rule));
                repository.getRDFRuleEngine().flush();
                Iterator<? extends Quad> iterator = transaction.getDataset().getAll(x, null, null);
                List<Quad> content = new ArrayList<>();
                while (iterator.hasNext())
                    content.add(iterator.next());
                W3CTestSuite.matchesQuads(new ArrayList<>(Arrays.asList(q1, q2)), content);
                return null;
            }, true);
        }
    }

    @Test
    public void testRuleChaining() throws Exception {
        try (RepositoryRDF repositoryRdf = new RepositoryRDF()) {
            String rule1 = "rule xowl:test-rule1 { ?x rdf:type xowl:y . } => { ?x rdf:type xowl:z . }";
            String rule2 = "rule xowl:test-rule2 { ?x rdf:type xowl:z . } => { ?x rdf:type xowl:x . }";
            repositoryRdf.runAsTransaction((repository, transaction) -> {
                IRINode x = transaction.getDataset().getIRINode("http://xowl.org/infra/store/rules/xowl#x");
                IRINode y = transaction.getDataset().getIRINode("http://xowl.org/infra/store/rules/xowl#y");
                IRINode z = transaction.getDataset().getIRINode("http://xowl.org/infra/store/rules/xowl#z");
                Quad q1 = new Quad(transaction.getDataset().getIRINode(IRIs.GRAPH_DEFAULT),
                        x, transaction.getDataset().getIRINode(Vocabulary.rdfType), y);
                Quad q2 = new Quad(transaction.getDataset().getIRINode(IRIs.GRAPH_INFERENCE),
                        x, transaction.getDataset().getIRINode(Vocabulary.rdfType), z);
                Quad q3 = new Quad(transaction.getDataset().getIRINode(IRIs.GRAPH_INFERENCE),
                        x, transaction.getDataset().getIRINode(Vocabulary.rdfType), x);

                transaction.getDataset().add(q1);
                repository.getRDFRuleEngine().add(loadXRDFRule(repository, rule1));
                repository.getRDFRuleEngine().add(loadXRDFRule(repository, rule2));
                repository.getRDFRuleEngine().flush();
                Iterator<? extends Quad> iterator = transaction.getDataset().getAll(x, null, null);
                List<Quad> content = new ArrayList<>();
                while (iterator.hasNext())
                    content.add(iterator.next());
                W3CTestSuite.matchesQuads(new ArrayList<>(Arrays.asList(q1, q2, q3)), content);
                return null;
            }, true);
        }
    }

    @Test
    public void testRuleLoop() throws Exception {
        try (RepositoryRDF repositoryRdf = new RepositoryRDF()) {
            String rule1 = "rule xowl:test-rule1 { ?x rdf:type xowl:y . } => { ?x rdf:type xowl:z . }";
            String rule2 = "rule xowl:test-rule2 { ?x rdf:type xowl:z . } => { ?x rdf:type xowl:y . }";
            repositoryRdf.runAsTransaction((repository, transaction) -> {
                IRINode x = transaction.getDataset().getIRINode("http://xowl.org/infra/store/rules/xowl#x");
                IRINode y = transaction.getDataset().getIRINode("http://xowl.org/infra/store/rules/xowl#y");
                IRINode z = transaction.getDataset().getIRINode("http://xowl.org/infra/store/rules/xowl#z");
                Quad q1 = new Quad(transaction.getDataset().getIRINode(IRIs.GRAPH_DEFAULT),
                        x, transaction.getDataset().getIRINode(Vocabulary.rdfType), y);
                Quad q2 = new Quad(transaction.getDataset().getIRINode(IRIs.GRAPH_INFERENCE),
                        x, transaction.getDataset().getIRINode(Vocabulary.rdfType), z);
                Quad q3 = new Quad(transaction.getDataset().getIRINode(IRIs.GRAPH_INFERENCE),
                        x, transaction.getDataset().getIRINode(Vocabulary.rdfType), y);

                transaction.getDataset().add(q1);
                repository.getRDFRuleEngine().add(loadXRDFRule(repository, rule1));
                repository.getRDFRuleEngine().add(loadXRDFRule(repository, rule2));
                repository.getRDFRuleEngine().flush();
                Iterator<? extends Quad> iterator = transaction.getDataset().getAll(x, null, null);
                List<Quad> content = new ArrayList<>();
                while (iterator.hasNext())
                    content.add(iterator.next());
                W3CTestSuite.matchesQuads(new ArrayList<>(Arrays.asList(q1, q2, q3)), content);
                return null;
            }, true);
        }
    }

    @Test
    public void testSimpleRetract() throws Exception {
        try (RepositoryRDF repositoryRdf = new RepositoryRDF()) {
            String rule = "rule xowl:test-rule { ?x rdf:type xowl:y . } => { ?x rdf:type xowl:z . }";
            repositoryRdf.runAsTransaction((repository, transaction) -> {
                IRINode x = transaction.getDataset().getIRINode("http://xowl.org/infra/store/rules/xowl#x");
                IRINode y = transaction.getDataset().getIRINode("http://xowl.org/infra/store/rules/xowl#y");
                IRINode z = transaction.getDataset().getIRINode("http://xowl.org/infra/store/rules/xowl#z");
                Quad q1 = new Quad(transaction.getDataset().getIRINode(IRIs.GRAPH_DEFAULT),
                        x, transaction.getDataset().getIRINode(Vocabulary.rdfType), y);
                Quad q2 = new Quad(transaction.getDataset().getIRINode(IRIs.GRAPH_INFERENCE),
                        x, transaction.getDataset().getIRINode(Vocabulary.rdfType), z);

                transaction.getDataset().add(q1);
                repository.getRDFRuleEngine().add(loadXRDFRule(repository, rule));
                repository.getRDFRuleEngine().flush();
                Iterator<? extends Quad> iterator = transaction.getDataset().getAll(x, null, null);
                List<Quad> content = new ArrayList<>();
                while (iterator.hasNext())
                    content.add(iterator.next());
                W3CTestSuite.matchesQuads(new ArrayList<>(Arrays.asList(q1, q2)), content);
                transaction.getDataset().remove(q1);
                iterator = transaction.getDataset().getAll(x, null, null);
                Assert.assertFalse("Should not have quads", iterator.hasNext());
                return null;
            }, true);
        }
    }

    @Test
    public void testSelectRetract() throws Exception {
        try (RepositoryRDF repositoryRdf = new RepositoryRDF()) {
            String rule = "rule xowl:test-rule { SELECT * WHERE {?x a xowl:y} } => { ?x rdf:type xowl:z . }";
            repositoryRdf.runAsTransaction((repository, transaction) -> {
                IRINode x = transaction.getDataset().getIRINode("http://xowl.org/infra/store/rules/xowl#x");
                IRINode y = transaction.getDataset().getIRINode("http://xowl.org/infra/store/rules/xowl#y");
                IRINode z = transaction.getDataset().getIRINode("http://xowl.org/infra/store/rules/xowl#z");
                Quad q1 = new Quad(transaction.getDataset().getIRINode(IRIs.GRAPH_DEFAULT),
                        x, transaction.getDataset().getIRINode(Vocabulary.rdfType), y);
                Quad q2 = new Quad(transaction.getDataset().getIRINode(IRIs.GRAPH_INFERENCE),
                        x, transaction.getDataset().getIRINode(Vocabulary.rdfType), z);

                transaction.getDataset().add(q1);
                repository.getRDFRuleEngine().add(loadXRDFRule(repository, rule));
                repository.getRDFRuleEngine().flush();
                Iterator<? extends Quad> iterator = transaction.getDataset().getAll(x, null, null);
                List<Quad> content = new ArrayList<>();
                while (iterator.hasNext())
                    content.add(iterator.next());
                W3CTestSuite.matchesQuads(new ArrayList<>(Arrays.asList(q1, q2)), content);
                transaction.getDataset().remove(q1);
                iterator = transaction.getDataset().getAll(x, null, null);
                Assert.assertFalse("Should not have quads", iterator.hasNext());
                return null;
            }, true);
        }
    }

    @Test
    public void testRetractChaining() throws Exception {
        try (RepositoryRDF repositoryRdf = new RepositoryRDF()) {
            String rule1 = "rule xowl:test-rule1 { ?x rdf:type xowl:y . } => { ?x rdf:type xowl:z . }";
            String rule2 = "rule xowl:test-rule2 { ?x rdf:type xowl:z . } => { ?x rdf:type xowl:x . }";
            repositoryRdf.runAsTransaction((repository, transaction) -> {
                IRINode x = transaction.getDataset().getIRINode("http://xowl.org/infra/store/rules/xowl#x");
                IRINode y = transaction.getDataset().getIRINode("http://xowl.org/infra/store/rules/xowl#y");
                IRINode z = transaction.getDataset().getIRINode("http://xowl.org/infra/store/rules/xowl#z");
                Quad q1 = new Quad(transaction.getDataset().getIRINode(IRIs.GRAPH_DEFAULT),
                        x, transaction.getDataset().getIRINode(Vocabulary.rdfType), y);
                Quad q2 = new Quad(transaction.getDataset().getIRINode(IRIs.GRAPH_INFERENCE),
                        x, transaction.getDataset().getIRINode(Vocabulary.rdfType), z);
                Quad q3 = new Quad(transaction.getDataset().getIRINode(IRIs.GRAPH_INFERENCE),
                        x, transaction.getDataset().getIRINode(Vocabulary.rdfType), x);

                transaction.getDataset().add(q1);
                repository.getRDFRuleEngine().add(loadXRDFRule(repository, rule1));
                repository.getRDFRuleEngine().add(loadXRDFRule(repository, rule2));
                repository.getRDFRuleEngine().flush();
                Iterator<? extends Quad> iterator = transaction.getDataset().getAll(x, null, null);
                List<Quad> content = new ArrayList<>();
                while (iterator.hasNext())
                    content.add(iterator.next());
                W3CTestSuite.matchesQuads(new ArrayList<>(Arrays.asList(q1, q2, q3)), content);
                transaction.getDataset().remove(q1);
                iterator = transaction.getDataset().getAll(x, null, null);
                Assert.assertFalse("Should not have quads", iterator.hasNext());
                return null;
            }, true);
        }
    }

    @Test
    public void testRetractLoop() throws Exception {
        try (RepositoryRDF repositoryRdf = new RepositoryRDF()) {
            String rule1 = "rule xowl:test-rule1 { ?x rdf:type xowl:y . } => { ?x rdf:type xowl:z . }";
            String rule2 = "rule xowl:test-rule2 { ?x rdf:type xowl:z . } => { ?x rdf:type xowl:y . }";
            repositoryRdf.runAsTransaction((repository, transaction) -> {
                IRINode x = transaction.getDataset().getIRINode("http://xowl.org/infra/store/rules/xowl#x");
                IRINode y = transaction.getDataset().getIRINode("http://xowl.org/infra/store/rules/xowl#y");
                IRINode z = transaction.getDataset().getIRINode("http://xowl.org/infra/store/rules/xowl#z");
                Quad q1 = new Quad(transaction.getDataset().getIRINode(IRIs.GRAPH_DEFAULT),
                        x, transaction.getDataset().getIRINode(Vocabulary.rdfType), y);
                Quad q2 = new Quad(transaction.getDataset().getIRINode(IRIs.GRAPH_INFERENCE),
                        x, transaction.getDataset().getIRINode(Vocabulary.rdfType), z);
                Quad q3 = new Quad(transaction.getDataset().getIRINode(IRIs.GRAPH_INFERENCE),
                        x, transaction.getDataset().getIRINode(Vocabulary.rdfType), y);

                transaction.getDataset().add(q1);
                repository.getRDFRuleEngine().add(loadXRDFRule(repository, rule1));
                repository.getRDFRuleEngine().add(loadXRDFRule(repository, rule2));
                repository.getRDFRuleEngine().flush();
                Iterator<? extends Quad> iterator = transaction.getDataset().getAll(x, null, null);
                List<Quad> content = new ArrayList<>();
                while (iterator.hasNext())
                    content.add(iterator.next());
                W3CTestSuite.matchesQuads(new ArrayList<>(Arrays.asList(q1, q2, q3)), content);
                transaction.getDataset().remove(q1);
                iterator = transaction.getDataset().getAll(x, null, null);
                Assert.assertFalse("Should not have quads", iterator.hasNext());
                return null;
            }, true);
        }
    }

    @Test
    public void testSelectAggregator() throws Exception {
        try (RepositoryRDF repositoryRdf = new RepositoryRDF()) {
            String rule = "rule xowl:test-rule { SELECT (COUNT(?v) AS ?c) WHERE {xowl:x a ?v} } => { xowl:x xowl:value ?c . }";
            repositoryRdf.runAsTransaction((repository, transaction) -> {
                IRINode x = transaction.getDataset().getIRINode("http://xowl.org/infra/store/rules/xowl#x");
                IRINode y1 = transaction.getDataset().getIRINode("http://xowl.org/infra/store/rules/xowl#y1");
                IRINode y2 = transaction.getDataset().getIRINode("http://xowl.org/infra/store/rules/xowl#y2");
                IRINode y3 = transaction.getDataset().getIRINode("http://xowl.org/infra/store/rules/xowl#y3");
                IRINode value = transaction.getDataset().getIRINode("http://xowl.org/infra/store/rules/xowl#value");
                Quad qy1 = new Quad(transaction.getDataset().getIRINode(IRIs.GRAPH_DEFAULT),
                        x, transaction.getDataset().getIRINode(Vocabulary.rdfType), y1);
                Quad qy2 = new Quad(transaction.getDataset().getIRINode(IRIs.GRAPH_DEFAULT),
                        x, transaction.getDataset().getIRINode(Vocabulary.rdfType), y2);
                Quad qy3 = new Quad(transaction.getDataset().getIRINode(IRIs.GRAPH_DEFAULT),
                        x, transaction.getDataset().getIRINode(Vocabulary.rdfType), y3);

                transaction.getDataset().add(qy1);
                transaction.getDataset().add(qy2);
                repository.getRDFRuleEngine().add(loadXRDFRule(repository, rule));
                repository.getRDFRuleEngine().flush();

                List<Quad> content = new ArrayList<>();
                Iterator<? extends Quad> iterator = transaction.getDataset().getAll(x, value, null);
                while (iterator.hasNext())
                    content.add(iterator.next());
                Assert.assertEquals("Unexpected inferences", 1, content.size());
                Assert.assertTrue("Unexpected inferences", RDFUtils.same(content.get(0).getObject(), transaction.getDataset().getLiteralNode("2", Vocabulary.xsdInteger, null)));

                transaction.getDataset().add(qy3);
                repository.getRDFRuleEngine().flush();

                content.clear();
                iterator = transaction.getDataset().getAll(x, value, null);
                while (iterator.hasNext())
                    content.add(iterator.next());
                Assert.assertEquals("Unexpected inferences", 1, content.size());
                Assert.assertTrue("Unexpected inferences", RDFUtils.same(content.get(0).getObject(), transaction.getDataset().getLiteralNode("3", Vocabulary.xsdInteger, null)));
                return null;
            }, true);
        }
    }
}
