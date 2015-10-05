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

package org.xowl.server;

import org.xowl.store.AbstractRepository;
import org.xowl.store.IOUtils;
import org.xowl.store.Repository;
import org.xowl.store.loaders.NQuadsLoader;
import org.xowl.store.loaders.RDFLoaderResult;
import org.xowl.store.loaders.RDFTLoader;
import org.xowl.store.loaders.SPARQLLoader;
import org.xowl.store.rdf.Quad;
import org.xowl.store.rdf.Rule;
import org.xowl.store.rdf.RuleExplanation;
import org.xowl.store.rete.MatchStatus;
import org.xowl.store.sparql.Command;
import org.xowl.store.sparql.Result;
import org.xowl.store.sparql.ResultFailure;
import org.xowl.store.sparql.ResultQuads;
import org.xowl.store.storage.NodeManager;
import org.xowl.utils.BufferedLogger;
import org.xowl.utils.DispatchLogger;
import org.xowl.utils.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.StringReader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Implements the SPARQL protocol
 * (See <a href="http://www.w3.org/TR/2013/REC-sparql11-protocol-20130321/">SPARQL Protocol</a>)
 * This service does NOT handle any security concern.
 *
 * @author Laurent Wouters
 */
public class SPARQLService extends Service {
    /**
     * The content type for a URL encoded message body
     */
    private static final String TYPE_URL_ENCODED = "application/x-www-form-urlencoded";
    /**
     * The content type for a SPARQL query in a message body
     */
    private static final String TYPE_SPARQL_QUERY = "application/sparql-query";
    /**
     * The content type for a SPARQL update in a message body
     */
    private static final String TYPE_SPARQL_UPDATE = "application/sparql-update";
    /**
     * The content type for the explanation of how a quad has been inferred
     */
    private static final String TYPE_RULE_EXPLANATION = "application/x-xowl-explanation";
    /**
     * The content type for listing the active reasoning rules
     */
    private static final String TYPE_RULE_LIST = "application/x-xowl-list-rules";
    /**
     * The content type for the matching status of a reasoning rule
     */
    private static final String TYPE_RULE_STATUS = "application/x-xowl-rule-status";
    /**
     * The content type for adding a new rule
     */
    private static final String TYPE_RULE_ADD = "application/x-xowl-rule-add";
    /**
     * The content type for removing an active rule
     */
    private static final String TYPE_RULE_REMOVE = "application/x-xowl-rule-remove";

    /**
     * The logger
     */
    private final Logger logger;
    /**
     * The served repository
     */
    private final Repository repository;

    /**
     * Initializes this service
     *
     * @param logger     The logger
     * @param repository The served repository
     */
    public SPARQLService(Logger logger, Repository repository) {
        this.logger = logger;
        this.repository = repository;
    }

    @Override
    public void onGet(HttpServletRequest request, HttpServletResponse response) {
        // common response configuration
        response.setCharacterEncoding("UTF-8");

        String query = request.getParameter("query");
        if (query != null) {
            if (request.getContentLength() > 0) {
                // should be empty
                // ill-formed request
                logger.error("Ill-formed request, content is not empty");
                response.setStatus(400);
            } else {
                String contentType = negotiateType(getContentTypes(request));
                String[] defaults = request.getParameterValues("default-graph-uri");
                String[] named = request.getParameterValues("named-graph-uri");
                enableCORS(response);
                executeSPARQL(query, defaults == null ? new ArrayList<String>() : Arrays.asList(defaults), named == null ? new ArrayList<String>() : Arrays.asList(named), contentType, response);
            }
        } else {
            // ill-formed request
            logger.error("Ill-formed request, expected a query parameter");
            response.setStatus(400);
        }
    }

    @Override
    public void onPost(HttpServletRequest request, HttpServletResponse response) {
        // common response configuration
        response.setCharacterEncoding("UTF-8");

        List<String> contentTypes = getContentTypes(request);
        String requestType = request.getContentType();
        int index = requestType.indexOf(";");
        if (index != -1)
            requestType = requestType.substring(0, index);
        requestType = requestType.trim();
        switch (requestType) {
            case TYPE_SPARQL_QUERY:
            case TYPE_SPARQL_UPDATE: {
                String[] defaults = request.getParameterValues("default-graph-uri");
                String[] named = request.getParameterValues("named-graph-uri");
                String contentType = negotiateType(contentTypes);
                enableCORS(response);
                String query = getMessageBody(request);
                executeSPARQL(query, defaults == null ? new ArrayList<String>() : Arrays.asList(defaults), named == null ? new ArrayList<String>() : Arrays.asList(named), contentType, response);
                break;
            }
            case TYPE_URL_ENCODED: {
                // TODO: decode and implement this
                response.setStatus(501);
                break;
            }
            case TYPE_RULE_EXPLANATION: {
                String quad = getMessageBody(request);
                enableCORS(response);
                explainQuad(quad, response);
                break;
            }
            case TYPE_RULE_LIST: {
                enableCORS(response);
                ruleList(response);
                break;
            }
            case TYPE_RULE_STATUS: {
                String rule = getMessageBody(request);
                enableCORS(response);
                ruleStatus(rule, response);
                break;
            }
            case TYPE_RULE_ADD: {
                String rule = getMessageBody(request);
                enableCORS(response);
                ruleAdd(rule, response);
                break;
            }
            case TYPE_RULE_REMOVE: {
                String rule = getMessageBody(request);
                enableCORS(response);
                ruleRemove(rule, response);
                break;
            }
            default:
                // incorrect content types
                logger.error("Incorrect content type for the request: " + requestType);
                response.setStatus(400);
                break;
        }
    }

    /**
     * Retrieve the explanation about a quad
     *
     * @param quad     The serialized quad
     * @param response The response to write to
     */
    private void explainQuad(String quad, HttpServletResponse response) {
        BufferedLogger bufferedLogger = new BufferedLogger();
        DispatchLogger dispatchLogger = new DispatchLogger(logger, bufferedLogger);
        NQuadsLoader loader = new NQuadsLoader(repository.getStore());
        RDFLoaderResult result = loader.loadRDF(dispatchLogger, new StringReader(quad), NodeManager.DEFAULT_GRAPH, NodeManager.DEFAULT_GRAPH);
        if (result == null || result.getQuads().isEmpty()) {
            response.setStatus(500);
            dispatchLogger.error("Failed to parse and load the request");
            outputLog(bufferedLogger, response);
            return;
        }
        Quad first = result.getQuads().get(0);
        RuleExplanation explanation = repository.getRDFRuleEngine().explain(first);
        response.setStatus(200);
        response.setHeader("Content-Type", Result.SYNTAX_JSON);
        try {
            explanation.printJSON(response.getWriter());
        } catch (IOException exception) {
            logger.error(exception);
        }
    }

    /**
     * Lists the active rules
     *
     * @param response The response to write to
     */
    private void ruleList(HttpServletResponse response) {
        Collection<Rule> rules = repository.getRDFRuleEngine().getRules();
        response.setStatus(200);
        response.setHeader("Content-Type", Result.SYNTAX_JSON);
        try (Writer writer = response.getWriter()) {
            writer.write("{\"rules\": [");
            boolean first = true;
            for (Rule rule : rules) {
                if (!first)
                    writer.write(", ");
                first = false;
                writer.write("\"");
                writer.write(IOUtils.escapeStringJSON(rule.getIRI()));
                writer.write("\"");
            }
            writer.write("]}");
        } catch (IOException exception) {
            logger.error(exception);
        }
    }

    /**
     * Retrieve the matching status of a rule
     *
     * @param rule     The IRI of a rule
     * @param response The response to write to
     */
    private void ruleStatus(String rule, HttpServletResponse response) {
        MatchStatus status = repository.getRDFRuleEngine().getMatchStatus(rule);
        response.setStatus(200);
        response.setHeader("Content-Type", Result.SYNTAX_JSON);
        try {
            status.printJSON(response.getWriter());
        } catch (IOException exception) {
            logger.error(exception);
        }
    }

    /**
     * Adds a new rule
     *
     * @param rule     The rule in the RDFT syntax
     * @param response The response to write to
     */
    private void ruleAdd(String rule, HttpServletResponse response) {
        BufferedLogger bufferedLogger = new BufferedLogger();
        DispatchLogger dispatchLogger = new DispatchLogger(logger, bufferedLogger);
        RDFTLoader loader = new RDFTLoader(repository.getStore());
        RDFLoaderResult result = loader.loadRDF(dispatchLogger, new StringReader(rule), NodeManager.DEFAULT_GRAPH, NodeManager.DEFAULT_GRAPH);
        if (result == null || result.getRules().isEmpty()) {
            response.setStatus(500);
            dispatchLogger.error("Failed to parse and load the rule(s)");
            outputLog(bufferedLogger, response);
            return;
        }
        for (Rule rdfRule : result.getRules())
            repository.getRDFRuleEngine().add(rdfRule);
        repository.getRDFRuleEngine().flush();
        response.setStatus(200);
    }

    /**
     * Removes the rule with the specified IRI
     *
     * @param rule     The IRI of a rule
     * @param response The response to write to
     */
    private void ruleRemove(String rule, HttpServletResponse response) {
        repository.getRDFRuleEngine().remove(rule);
        response.setStatus(200);
    }

    /**
     * Executes a SPARQL request
     *
     * @param request     The request
     * @param defaultIRIs The context's default IRIs
     * @param namedIRIs   The context's named IRIs
     * @param contentType The negotiated content type for the response
     * @param response    The response to write to
     */
    private void executeSPARQL(String request, Collection<String> defaultIRIs, Collection<String> namedIRIs, String contentType, HttpServletResponse response) {
        BufferedLogger bufferedLogger = new BufferedLogger();
        DispatchLogger dispatchLogger = new DispatchLogger(logger, bufferedLogger);
        SPARQLLoader loader = new SPARQLLoader(repository.getStore(), defaultIRIs, namedIRIs);
        List<Command> commands = loader.load(dispatchLogger, new StringReader(request));
        if (commands == null) {
            // ill-formed request
            dispatchLogger.error("Failed to parse and load the request");
            outputLog(bufferedLogger, response);
            response.setStatus(400);
            return;
        }
        Result result = ResultFailure.INSTANCE;
        for (Command command : commands) {
            result = command.execute(repository);
            if (result.isFailure()) {
                break;
            }
        }
        if (result.isFailure()) {
            response.setStatus(500);
            bufferedLogger.error(((ResultFailure) result).getMessage());
            outputLog(bufferedLogger, response);
        } else {
            response.setStatus(200);
            response.setHeader("Content-Type", coerceContentType(result, contentType));
            try {
                result.print(response.getWriter(), coerceContentType(result, contentType));
            } catch (IOException exception) {
                logger.error(exception);
            }
        }
    }

    /**
     * Coerce the content type of a SPARQL response depending on the result type
     *
     * @param result The SPARQL result
     * @param type   The negotiated content type
     * @return The coerced content type
     */
    private String coerceContentType(Result result, String type) {
        if (result instanceof ResultQuads) {
            switch (type) {
                case AbstractRepository.SYNTAX_NTRIPLES:
                case AbstractRepository.SYNTAX_NQUADS:
                case AbstractRepository.SYNTAX_TURTLE:
                case AbstractRepository.SYNTAX_RDFXML:
                case AbstractRepository.SYNTAX_JSON_LD:
                    return type;
                default:
                    return AbstractRepository.SYNTAX_NQUADS;
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
     * Outputs the logger's error in the response
     *
     * @param logger   The logger
     * @param response The response
     */
    private void outputLog(BufferedLogger logger, HttpServletResponse response) {
        try (Writer writer = response.getWriter()) {
            for (Object error : logger.getErrorMessages()) {
                writer.write(error.toString());
                writer.write("\n");
            }
            writer.flush();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}
