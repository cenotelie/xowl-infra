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

import com.sun.net.httpserver.HttpExchange;
import org.xowl.server.db.Controller;
import org.xowl.server.db.Database;
import org.xowl.server.db.User;
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
import java.io.StringWriter;
import java.util.Collection;

/**
 * Handler for xOWL extensions
 *
 * @author Laurent Wouters
 */
class XOWLHandler extends HandlerPart {
    /**
     * The content type for the explanation of how a quad has been inferred
     */
    public static final String TYPE_RULE_EXPLANATION = "application/x-xowl-rule-explanation";
    /**
     * The content type for listing the active reasoning rules
     */
    public static final String TYPE_RULE_LIST = "application/x-xowl-rule-list";
    /**
     * The content type for the matching status of a reasoning rule
     */
    public static final String TYPE_RULE_STATUS = "application/x-xowl-rule-status";
    /**
     * The content type for adding a new rule
     */
    public static final String TYPE_RULE_ADD = "application/x-xowl-rule-add";
    /**
     * The content type for removing an active rule
     */
    public static final String TYPE_RULE_REMOVE = "application/x-xowl-rule-remove";

    /**
     * Initializes this handler
     *
     * @param controller The top controller
     */
    public XOWLHandler(Controller controller) {
        super(controller);
    }

    @Override
    public void handle(HttpExchange httpExchange, String method, String contentType, String body, User user, Database database) {
        if (controller.isAdminOf(user, database) || controller.isServerAdmin(user)) {
            switch (contentType) {
                case TYPE_RULE_EXPLANATION:
                    explainQuad(httpExchange, database, body);
                    break;
                case TYPE_RULE_LIST:
                    ruleList(httpExchange, database);
                    break;
                case TYPE_RULE_STATUS:
                    ruleStatus(httpExchange, database, body);
                    break;
                case TYPE_RULE_ADD:
                    ruleAdd(httpExchange, database, body);
                    break;
                case TYPE_RULE_REMOVE:
                    ruleRemove(httpExchange, database, body);
                    break;
            }
        } else {
            response(httpExchange, Utils.HTTP_CODE_UNAUTHORIZED, null);
        }
    }

    /**
     * Retrieve the explanation about a quad
     *
     * @param httpExchange The HTTP exchange
     * @param database     The current database
     * @param quad         The serialized quad
     */
    private void explainQuad(HttpExchange httpExchange, Database database, String quad) {
        BufferedLogger bufferedLogger = new BufferedLogger();
        DispatchLogger dispatchLogger = new DispatchLogger(database.getLogger(), bufferedLogger);
        NQuadsLoader loader = new NQuadsLoader(database.getRepository().getStore());
        RDFLoaderResult result = loader.loadRDF(dispatchLogger, new StringReader(quad), IRIs.GRAPH_DEFAULT, IRIs.GRAPH_DEFAULT);
        if (result == null || result.getQuads().isEmpty()) {
            dispatchLogger.error("Failed to parse and load the request");
            response(httpExchange, Utils.HTTP_CODE_INTERNAL_ERROR, Utils.getLog(bufferedLogger));
            return;
        }
        Quad first = result.getQuads().get(0);
        RuleExplanation explanation = database.getRepository().getRDFRuleEngine().explain(first);
        StringWriter writer = new StringWriter();
        try {
            explanation.printJSON(writer);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Lists the active rules
     *
     * @param httpExchange The HTTP exchange
     * @param database     The current database
     */
    private void ruleList(HttpExchange httpExchange, Database database) {
        Collection<Rule> rules = database.getRepository().getRDFRuleEngine().getRules();
        StringBuilder builder = new StringBuilder();
        builder.append("{\"rules\": [");
        boolean first = true;
        for (Rule rule : rules) {
            if (!first)
                builder.append(", ");
            first = false;
            builder.append("\"");
            builder.append(IOUtils.escapeStringJSON(rule.getIRI()));
            builder.append("\"");
        }
        builder.append("]}");
        httpExchange.getResponseHeaders().add("Content-Type", Result.SYNTAX_JSON);
        response(httpExchange, Utils.HTTP_CODE_OK, builder.toString());
    }

    /**
     * Retrieve the matching status of a rule
     *
     * @param httpExchange The HTTP exchange
     * @param database     The current database
     * @param rule         The IRI of a rule
     */
    private void ruleStatus(HttpExchange httpExchange, Database database, String rule) {
        MatchStatus status = database.getRepository().getRDFRuleEngine().getMatchStatus(rule);
        StringWriter writer = new StringWriter();
        try {
            status.printJSON(writer);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        httpExchange.getResponseHeaders().add("Content-Type", Result.SYNTAX_JSON);
        response(httpExchange, Utils.HTTP_CODE_OK, writer.toString());
    }

    /**
     * Adds a new rule
     *
     * @param httpExchange The HTTP exchange
     * @param database     The current database
     * @param rule         The rule in the RDFT syntax
     */
    private void ruleAdd(HttpExchange httpExchange, Database database, String rule) {
        BufferedLogger bufferedLogger = new BufferedLogger();
        DispatchLogger dispatchLogger = new DispatchLogger(database.getLogger(), bufferedLogger);
        RDFTLoader loader = new RDFTLoader(database.getRepository().getStore());
        RDFLoaderResult result = loader.loadRDF(dispatchLogger, new StringReader(rule), IRIs.GRAPH_DEFAULT, IRIs.GRAPH_DEFAULT);
        if (result == null || result.getRules().isEmpty()) {
            dispatchLogger.error("Failed to parse and load the request");
            response(httpExchange, Utils.HTTP_CODE_INTERNAL_ERROR, Utils.getLog(bufferedLogger));
            return;
        }
        for (Rule rdfRule : result.getRules())
            database.getRepository().getRDFRuleEngine().add(rdfRule);
        database.getRepository().getRDFRuleEngine().flush();
        response(httpExchange, Utils.HTTP_CODE_OK, null);
    }

    /**
     * Removes the rule with the specified IRI
     *
     * @param httpExchange The HTTP exchange
     * @param database     The current database
     * @param rule         The IRI of a rule
     */
    private void ruleRemove(HttpExchange httpExchange, Database database, String rule) {
        database.getRepository().getRDFRuleEngine().remove(rule);
        response(httpExchange, Utils.HTTP_CODE_OK, null);
    }
}
