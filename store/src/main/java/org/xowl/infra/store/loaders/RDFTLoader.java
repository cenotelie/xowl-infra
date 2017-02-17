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

import org.xowl.hime.redist.ASTNode;
import org.xowl.hime.redist.ParseError;
import org.xowl.hime.redist.ParseResult;
import org.xowl.hime.redist.TextContext;
import org.xowl.infra.store.IRIs;
import org.xowl.infra.store.rdf.*;
import org.xowl.infra.store.sparql.GraphPattern;
import org.xowl.infra.store.storage.NodeManager;
import org.xowl.infra.utils.IOUtils;
import org.xowl.infra.utils.logging.Logger;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Represents a loader of RDFT syntax
 *
 * @author Laurent Wouters
 */
public class RDFTLoader implements Loader {
    /**
     * The RDF store used to create nodes
     */
    private final NodeManager nodes;
    /**
     * The inner SPARQL loader
     */
    private final SPARQLLoader sparql;

    /**
     * Initializes this loader
     *
     * @param store The RDF store used to create nodes
     */
    public RDFTLoader(NodeManager store) {
        this.nodes = store;
        this.sparql = new SPARQLLoader(store);
    }

    @Override
    public ParseResult parse(Logger logger, Reader reader) {
        ParseResult result;
        try {
            String content = IOUtils.read(reader);
            RDFTLexer lexer = new RDFTLexer(content);
            RDFTParser parser = new RDFTParser(lexer);
            parser.setModeRecoverErrors(false);
            result = parser.parse();
        } catch (IOException ex) {
            logger.error(ex);
            return null;
        }
        for (ParseError error : result.getErrors()) {
            logger.error(error);
            TextContext context = result.getInput().getContext(error.getPosition(), error.getLength());
            logger.error(context.getContent());
            logger.error(context.getPointer());
        }
        return result;
    }

    @Override
    public RDFLoaderResult loadRDF(Logger logger, Reader reader, String resourceIRI, String graphIRI) {
        ParseResult parseResult = parse(logger, reader);
        if (parseResult == null || !parseResult.isSuccess() || parseResult.getErrors().size() > 0)
            return null;
        try {
            return loadDocument(parseResult.getRoot());
        } catch (LoaderException exception) {
            logger.error(exception);
            logger.error("@" + exception.getOrigin().getPosition());
            TextContext context = exception.getOrigin().getContext();
            logger.error(context.getContent());
            logger.error(context.getPointer());
            return null;
        } catch (IllegalArgumentException exception) {
            logger.error(exception);
            return null;
        }
    }

    @Override
    public OWLLoaderResult loadOWL(Logger logger, Reader reader, String uri) {
        throw new UnsupportedOperationException();
    }

    /**
     * Loads the document represented by the specified AST node
     *
     * @param node An AST node
     * @return The loaded elements
     */
    private RDFLoaderResult loadDocument(ASTNode node) throws LoaderException {
        RDFLoaderResult result = new RDFLoaderResult();
        sparql.loadPrologue(node.getChildren().get(0));
        for (ASTNode nodeRule : node.getChildren().get(1).getChildren()) {
            if (nodeRule.getSymbol().getID() == RDFTParser.ID.rule_simple)
                result.getRules().add(loadRuleSimple(nodeRule));
            else if (nodeRule.getSymbol().getID() == RDFTParser.ID.rule_sparql)
                result.getRules().add(loadRuleSPARQL(nodeRule));
        }
        return result;
    }

    /**
     * Loads a simple rule
     *
     * @param node The AST node
     * @return The loaded rule
     */
    private RDFRule loadRuleSimple(ASTNode node) throws LoaderException {
        // initial context setup
        SPARQLContext context = new SPARQLContext(nodes, true);
        GraphNode graph = (GraphNode) context.resolveVariable("__graph");
        // load basic info
        boolean isDistinct = (node.getChildren().get(0).getChildren().size() > 0);
        String iri = sparql.getNodeIRI(node.getChildren().get(1)).getIRIValue();
        RDFRuleSimple result = new RDFRuleSimple(iri, isDistinct);
        // load the antecedents
        Collection<Quad> positives = new ArrayList<>();
        Collection<Collection<Quad>> negatives = new ArrayList<>();
        sparql.loadQuadsForTarget(context, node.getChildren().get(2), graph, positives, negatives);
        for (Quad quad : positives)
            result.addAntecedentPositive(quad);
        for (Collection<Quad> conjunction : negatives)
            result.addAntecedentNegatives(conjunction);
        positives.clear();
        negatives.clear();
        // load the consequents
        graph = nodes.getIRINode(IRIs.GRAPH_INFERENCE);
        sparql.loadQuadsForTarget(context, node.getChildren().get(3), graph, positives, negatives);
        for (Quad quad : positives)
            result.addConsequentPositive(quad);
        for (Collection<Quad> conjunction : negatives)
            for (Quad quad : conjunction)
                result.addConsequentNegative(quad);
        return result;
    }

    /**
     * Loads a rule that is base on a SPARQL query
     *
     * @param node The AST node
     * @return The loaded rule
     */
    private RDFRule loadRuleSPARQL(ASTNode node) throws LoaderException {
        // initial context setup
        SPARQLContext context = new SPARQLContext(nodes, true);
        GraphNode graph = (GraphNode) context.resolveVariable("__graph");
        // load basic info
        String iri = sparql.getNodeIRI(node.getChildren().get(1)).getIRIValue();
        // load the antecedents
        GraphPattern pattern = sparql.loadGraphPatternSubSelect(context, graph, node.getChildren().get(2));
        RDFRuleSelect result = new RDFRuleSelect(iri, pattern);
        // load the consequents
        Collection<Quad> positives = new ArrayList<>();
        Collection<Collection<Quad>> negatives = new ArrayList<>();
        graph = nodes.getIRINode(IRIs.GRAPH_INFERENCE);
        sparql.loadQuadsForTarget(context, node.getChildren().get(3), graph, positives, negatives);
        for (Quad quad : positives)
            result.addConsequentPositive(quad);
        for (Collection<Quad> conjunction : negatives)
            for (Quad quad : conjunction)
                result.addConsequentNegative(quad);
        return result;
    }
}
