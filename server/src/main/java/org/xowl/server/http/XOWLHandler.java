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

package org.xowl.server.http;

import org.xowl.store.IOUtils;
import org.xowl.store.IRIs;
import org.xowl.store.loaders.NQuadsLoader;
import org.xowl.store.loaders.RDFLoaderResult;
import org.xowl.store.loaders.RDFTLoader;
import org.xowl.store.rdf.Quad;
import org.xowl.store.rdf.Rule;
import org.xowl.store.rdf.RuleExplanation;
import org.xowl.store.rete.MatchStatus;
import org.xowl.store.sparql.Result;
import org.xowl.utils.BufferedLogger;
import org.xowl.utils.DispatchLogger;

import java.io.IOException;
import java.io.StringReader;
import java.io.Writer;
import java.util.Collection;

/**
 * Handler for xOWL extensions
 *
 * @author Laurent Wouters
 */
class XOWLHandler {
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
     * Retrieve the explanation about a quad
     *
     * @param quad     The serialized quad
     * @param response The response to write to
     */
    private void explainQuad(String quad, HttpServletResponse response) {
        BufferedLogger bufferedLogger = new BufferedLogger();
        DispatchLogger dispatchLogger = new DispatchLogger(logger, bufferedLogger);
        NQuadsLoader loader = new NQuadsLoader(repository.getStore());
        RDFLoaderResult result = loader.loadRDF(dispatchLogger, new StringReader(quad), IRIs.GRAPH_DEFAULT, IRIs.GRAPH_DEFAULT);
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
        RDFLoaderResult result = loader.loadRDF(dispatchLogger, new StringReader(rule), IRIs.GRAPH_DEFAULT, IRIs.GRAPH_DEFAULT);
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
}
