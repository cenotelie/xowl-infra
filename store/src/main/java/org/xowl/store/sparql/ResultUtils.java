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

package org.xowl.store.sparql;

import org.xowl.hime.redist.ASTNode;
import org.xowl.hime.redist.ParseError;
import org.xowl.hime.redist.ParseResult;
import org.xowl.store.AbstractRepository;
import org.xowl.store.IOUtils;
import org.xowl.store.IRIs;
import org.xowl.store.loaders.*;
import org.xowl.store.rdf.Node;
import org.xowl.store.rdf.QuerySolution;
import org.xowl.store.rdf.VariableNode;
import org.xowl.store.storage.NodeManager;
import org.xowl.store.storage.cache.CachedNodes;
import org.xowl.utils.collections.Couple;
import org.xowl.utils.logging.BufferedLogger;
import org.xowl.utils.logging.DispatchLogger;
import org.xowl.utils.logging.Logger;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utilities for SPARQL results
 *
 * @author Laurent Wouters
 */
public class ResultUtils {
    /**
     * Parses a SPARQL response to a command
     *
     * @param content     The content to parse
     * @param contentType The content type hint, or null if there is none
     * @return The parsed SPARQL result
     */
    public static Result parseResponse(String content, String contentType) {
        if (content == null)
            // fail on empty result
            return new ResultFailure("No response");
        if ("OK".equalsIgnoreCase(content))
            return ResultSuccess.INSTANCE;
        if ("true".equalsIgnoreCase(content))
            return new ResultYesNo(true);
        if ("false".equalsIgnoreCase(content))
            return new ResultYesNo(false);
        if (contentType != null) {
            switch (contentType) {
                case AbstractRepository.SYNTAX_NQUADS:
                    return parseResponseQuads(content, new NQuadsLoader(new CachedNodes()));
                case AbstractRepository.SYNTAX_NTRIPLES:
                    return parseResponseQuads(content, new NTriplesLoader(new CachedNodes()));
                case AbstractRepository.SYNTAX_TURTLE:
                    return parseResponseQuads(content, new TurtleLoader(new CachedNodes()));
                case AbstractRepository.SYNTAX_TRIG:
                    return parseResponseQuads(content, new TriGLoader(new CachedNodes()));
                case AbstractRepository.SYNTAX_RDFXML:
                    return parseResponseQuads(content, new RDFXMLLoader(new CachedNodes()));
                case AbstractRepository.SYNTAX_JSON_LD:
                    return parseResponseQuads(content, new JSONLDLoader(new CachedNodes()) {
                        @Override
                        protected Reader getReaderFor(Logger logger, String iri) {
                            return null;
                        }
                    });
                case Result.SYNTAX_JSON:
                case "application/json":
                    return parseResponseJSON(content);
                case Result.SYNTAX_CSV:
                case Result.SYNTAX_TSV:
                case Result.SYNTAX_XML:
                    // TODO: handle this
                    return new ResultFailure("Cannot handle content " + contentType);
                default:
                    return new ResultFailure("Cannot handle content " + contentType);
            }
        } else {
            content = content.trim();
            if (content.startsWith("{")) {
                // this is probably some JSON
                return parseResponseJSON(content);
            } else {
                // here assume n-quads
                return parseResponseQuads(content, new NQuadsLoader(new CachedNodes()));
            }
        }
    }

    /**
     * Parses a SPARQL result serialized in JSON
     *
     * @param content The content
     * @return The result
     */
    private static Result parseResponseJSON(String content) {
        NodeManager nodeManager = new CachedNodes();
        JSONLDLoader loader = new JSONLDLoader(nodeManager) {
            @Override
            protected Reader getReaderFor(Logger logger, String iri) {
                return null;
            }
        };
        BufferedLogger bufferedLogger = new BufferedLogger();
        DispatchLogger dispatchLogger = new DispatchLogger(Logger.DEFAULT, bufferedLogger);
        ParseResult parseResult = loader.parse(dispatchLogger, new StringReader(content));
        if (parseResult == null || !parseResult.isSuccess()) {
            dispatchLogger.error("Failed to parse and load the solutions:");
            if (parseResult != null) {
                for (ParseError error : parseResult.getErrors()) {
                    dispatchLogger.error(error);
                }
            }
            StringBuilder builder = new StringBuilder();
            for (Object error : bufferedLogger.getErrorMessages()) {
                builder.append(error.toString());
                builder.append("\n");
            }
            return new ResultFailure(builder.toString());
        }

        ASTNode nodeRoot = parseResult.getRoot();
        if ("array".equals(nodeRoot.getSymbol().getName()))
            return new ResultFailure("Unexpected JSON format");

        ASTNode nodeHead = null;
        ASTNode nodeResults = null;
        ASTNode nodeBoolean = null;
        ASTNode nodeError = null;
        for (ASTNode memberNode : nodeRoot.getChildren()) {
            String memberName = IOUtils.unescape(memberNode.getChildren().get(0).getValue());
            memberName = memberName.substring(1, memberName.length() - 1);
            ASTNode memberValue = memberNode.getChildren().get(1);
            switch (memberName) {
                case "head":
                    nodeHead = memberValue;
                    break;
                case "results":
                    nodeResults = memberValue;
                    break;
                case "boolean":
                    nodeBoolean = memberValue;
                    break;
                case "error":
                    nodeError = memberValue;
                    break;
            }
        }

        if (nodeResults != null) {
            // this is solution set
            if (nodeHead == null)
                return new ResultFailure("Unexpected JSON format");
            ASTNode nodeVars = null;
            for (ASTNode memberNode : nodeHead.getChildren()) {
                String memberName = IOUtils.unescape(memberNode.getChildren().get(0).getValue());
                memberName = memberName.substring(1, memberName.length() - 1);
                ASTNode memberValue = memberNode.getChildren().get(1);
                if ("vars".equals(memberName)) {
                    nodeVars = memberValue;
                    break;
                }
            }
            if (nodeVars == null)
                return new ResultFailure("Unexpected JSON format");
            Map<String, VariableNode> variables = new HashMap<>();
            for (ASTNode nodeVariable : nodeVars.getChildren()) {
                String name = IOUtils.unescape(nodeVariable.getValue());
                name = name.substring(1, name.length() - 1);
                variables.put(name, new VariableNode(name));
            }
            ASTNode nodeBindings = null;
            for (ASTNode memberNode : nodeResults.getChildren()) {
                String memberName = IOUtils.unescape(memberNode.getChildren().get(0).getValue());
                memberName = memberName.substring(1, memberName.length() - 1);
                ASTNode memberValue = memberNode.getChildren().get(1);
                if ("vars".equals(memberName)) {
                    nodeBindings = memberValue;
                    break;
                }
            }
            if (nodeBindings == null)
                return new ResultFailure("Unexpected JSON format");
            SolutionsArray solutions = new SolutionsArray();
            for (ASTNode nodeSolution : nodeBindings.getChildren()) {
                List<Couple<VariableNode, Node>> bindings = new ArrayList<>();
                for (ASTNode nodeBinding : nodeSolution.getChildren()) {
                    VariableNode variable = variables.get(nodeBinding.getChildren().get(0).getValue());
                    Node value = IOUtils.deserializeJSON(nodeManager, nodeBinding.getChildren().get(1));
                    bindings.add(new Couple<>(variable, value));
                }
                solutions.add(new QuerySolution(bindings));
            }
            return new ResultSolutions(solutions);
        } else if (nodeError != null) {
            // this is an error
            String error = IOUtils.unescape(nodeError.getValue());
            error = error.substring(1, error.length() - 1);
            return new ResultFailure(error);
        } else if (nodeBoolean != null) {
            // this is a boolean value
            String value = IOUtils.unescape(nodeBoolean.getValue());
            value = value.substring(1, value.length() - 1);
            return new ResultYesNo(value.equalsIgnoreCase("true"));
        } else {
            return new ResultFailure("Unexpected JSON format");
        }
    }

    /**
     * Parses a SPARQL result as quads in a syntax
     *
     * @param content The data to parse
     * @param loader  The loader to use
     * @return The result
     */
    private static Result parseResponseQuads(String content, Loader loader) {
        BufferedLogger bufferedLogger = new BufferedLogger();
        DispatchLogger dispatchLogger = new DispatchLogger(Logger.DEFAULT, bufferedLogger);
        RDFLoaderResult loaderResult = loader.loadRDF(dispatchLogger, new StringReader(content), null, IRIs.GRAPH_DEFAULT);
        if (loaderResult == null) {
            dispatchLogger.error("Failed to parse and load the quads");
            StringBuilder builder = new StringBuilder();
            for (Object error : bufferedLogger.getErrorMessages()) {
                builder.append(error.toString());
                builder.append("\n");
            }
            return new ResultFailure(builder.toString());
        }
        return new ResultQuads(loaderResult.getQuads());
    }
}