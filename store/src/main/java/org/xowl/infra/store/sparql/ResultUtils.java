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

package org.xowl.infra.store.sparql;

import org.xowl.hime.redist.ASTNode;
import org.xowl.infra.store.IRIs;
import org.xowl.infra.store.RDFUtils;
import org.xowl.infra.store.Repository;
import org.xowl.infra.store.RepositoryRDF;
import org.xowl.infra.store.loaders.*;
import org.xowl.infra.store.rdf.Node;
import org.xowl.infra.store.rdf.Quad;
import org.xowl.infra.store.rdf.RDFPatternSolution;
import org.xowl.infra.store.rdf.VariableNode;
import org.xowl.infra.store.storage.cache.CachedNodes;
import org.xowl.infra.utils.TextUtils;
import org.xowl.infra.utils.collections.Couple;
import org.xowl.infra.utils.http.HttpConstants;
import org.xowl.infra.utils.logging.BufferedLogger;
import org.xowl.infra.utils.logging.DispatchLogger;
import org.xowl.infra.utils.logging.Logger;
import org.xowl.infra.utils.logging.Logging;

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
     * Coerce the content type of a SPARQL response depending on the result type
     *
     * @param result The SPARQL result
     * @param type   The negotiated content type
     * @return The coerced content type
     */
    public static String coerceContentType(Result result, String type) {
        if (result instanceof ResultQuads) {
            switch (type) {
                case Repository.SYNTAX_NTRIPLES:
                case Repository.SYNTAX_NQUADS:
                case Repository.SYNTAX_TURTLE:
                case Repository.SYNTAX_TRIG:
                case Repository.SYNTAX_RDFXML:
                case Repository.SYNTAX_JSON_LD:
                case Repository.SYNTAX_XRDF:
                case HttpConstants.MIME_JSON:
                    return type;
                default:
                    return Repository.SYNTAX_NQUADS;
            }
        } else {
            switch (type) {
                case Result.SYNTAX_CSV:
                case Result.SYNTAX_TSV:
                case Result.SYNTAX_XML:
                case Result.SYNTAX_JSON:
                    return type;
                default:
                    return Result.SYNTAX_JSON;
            }
        }
    }

    /**
     * Parses a SPARQL response to a command
     *
     * @param content     The content to parse
     * @param contentType The content type hint, or null if there is none
     * @return The parsed SPARQL result
     */
    public static Result parseResponse(String content, String contentType) {
        if (content == null) {
            // this is probably an empty content
            if (contentType == null)
                // no content type, cannot determine what type of response, interpret as absence of repsonse
                return new ResultFailure("No response");
            switch (contentType) {
                // empty quads
                case Repository.SYNTAX_NQUADS:
                case Repository.SYNTAX_NTRIPLES:
                case Repository.SYNTAX_TURTLE:
                case Repository.SYNTAX_TRIG:
                case Repository.SYNTAX_RDFXML:
                case Repository.SYNTAX_JSON_LD:
                case Repository.SYNTAX_XRDF:
                    return new ResultQuads(new ArrayList<Quad>(0));
                // empty solutions
                case Result.SYNTAX_JSON:
                case HttpConstants.MIME_JSON:
                case Result.SYNTAX_CSV:
                case Result.SYNTAX_TSV:
                case Result.SYNTAX_XML:
                    return new ResultSolutions(new SolutionsMultiset());
                default:
                    return new ResultFailure("Cannot handle content " + contentType);
            }
        }
        if ("OK".equalsIgnoreCase(content))
            return ResultSuccess.INSTANCE;
        if ("true".equalsIgnoreCase(content))
            return new ResultYesNo(true);
        if ("false".equalsIgnoreCase(content))
            return new ResultYesNo(false);
        if (contentType != null) {
            switch (contentType) {
                case Repository.SYNTAX_NQUADS:
                    return parseResponseQuads(content, new NQuadsLoader());
                case Repository.SYNTAX_NTRIPLES:
                    return parseResponseQuads(content, new NTriplesLoader());
                case Repository.SYNTAX_TURTLE:
                    return parseResponseQuads(content, new TurtleLoader());
                case Repository.SYNTAX_TRIG:
                    return parseResponseQuads(content, new TriGLoader());
                case Repository.SYNTAX_RDFXML:
                    return parseResponseQuads(content, new RDFXMLLoader());
                case Repository.SYNTAX_JSON_LD:
                    return parseResponseQuads(content, new JSONLDLoader() {
                        @Override
                        protected Reader getReaderFor(Logger logger, String iri) {
                            return null;
                        }
                    });
                case Repository.SYNTAX_XRDF:
                    return parseResponseQuads(content, new xRDFLoader());
                case Result.SYNTAX_JSON:
                case HttpConstants.MIME_JSON:
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
        Repository repository = null;
        BufferedLogger bufferedLogger = new BufferedLogger();
        ASTNode nodeRoot = JSONLDLoader.parseJSON(bufferedLogger, content);
        if (nodeRoot == null)
            return new ResultFailure(bufferedLogger.getErrorsAsString());
        if ("array".equals(nodeRoot.getSymbol().getName()))
            return new ResultFailure("Unexpected JSON format");

        ASTNode nodeHead = null;
        ASTNode nodeResults = null;
        ASTNode nodeBoolean = null;
        ASTNode nodeError = null;
        for (ASTNode memberNode : nodeRoot.getChildren()) {
            String memberName = TextUtils.unescape(memberNode.getChildren().get(0).getValue());
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
                String memberName = TextUtils.unescape(memberNode.getChildren().get(0).getValue());
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
                String name = TextUtils.unescape(nodeVariable.getValue());
                name = name.substring(1, name.length() - 1);
                variables.put(name, new VariableNode(name));
            }
            ASTNode nodeBindings = null;
            for (ASTNode memberNode : nodeResults.getChildren()) {
                String memberName = TextUtils.unescape(memberNode.getChildren().get(0).getValue());
                memberName = memberName.substring(1, memberName.length() - 1);
                ASTNode memberValue = memberNode.getChildren().get(1);
                if ("bindings".equals(memberName)) {
                    nodeBindings = memberValue;
                    break;
                }
            }
            if (nodeBindings == null)
                return new ResultFailure("Unexpected JSON format");
            SolutionsMultiset solutions = new SolutionsMultiset();
            for (ASTNode nodeSolution : nodeBindings.getChildren()) {
                List<Couple<VariableNode, Node>> bindings = new ArrayList<>();
                for (ASTNode nodeBinding : nodeSolution.getChildren()) {
                    String varName = nodeBinding.getChildren().get(0).getValue();
                    VariableNode variable = variables.get(varName.substring(1, varName.length() - 1));
                    if (repository == null)
                        repository = new RepositoryRDF();
                    Node value = RDFUtils.deserializeJSON(repository, nodeBinding.getChildren().get(1));
                    bindings.add(new Couple<>(variable, value));
                }
                solutions.add(new RDFPatternSolution(bindings));
            }
            return new ResultSolutions(solutions);
        } else if (nodeError != null) {
            // this is an error
            String error = TextUtils.unescape(nodeError.getValue());
            error = error.substring(1, error.length() - 1);
            return new ResultFailure(error);
        } else if (nodeBoolean != null) {
            // this is a boolean value
            String value = TextUtils.unescape(nodeBoolean.getValue());
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
        DispatchLogger dispatchLogger = new DispatchLogger(Logging.get(), bufferedLogger);
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
