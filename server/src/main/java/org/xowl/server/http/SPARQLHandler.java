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
import org.xowl.store.AbstractRepository;
import org.xowl.store.loaders.SPARQLLoader;
import org.xowl.store.sparql.Command;
import org.xowl.store.sparql.Result;
import org.xowl.store.sparql.ResultFailure;
import org.xowl.store.sparql.ResultQuads;
import org.xowl.utils.BufferedLogger;
import org.xowl.utils.DispatchLogger;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Implements the SPARQL protocol
 * (See <a href="http://www.w3.org/TR/2013/REC-sparql11-protocol-20130321/">SPARQL Protocol</a>)
 * This service does NOT handle any security concern.
 *
 * @author Laurent Wouters
 */
class SPARQLHandler extends HandlerPart {
    /**
     * The content type for a URL encoded message body
     */
    public static final String TYPE_URL_ENCODED = "application/x-www-form-urlencoded";
    /**
     * The content type for a SPARQL query in a message body
     */
    public static final String TYPE_SPARQL_QUERY = "application/sparql-query";
    /**
     * The content type for a SPARQL update in a message body
     */
    public static final String TYPE_SPARQL_UPDATE = "application/sparql-update";

    /**
     * Initializes this handler
     *
     * @param controller The top controller
     */
    public SPARQLHandler(Controller controller) {
        super(controller);
    }

    @Override
    public void handle(HttpExchange httpExchange, String method, String contentType, String body, User user, Database database) {
        switch (method) {
            case "GET":
                onGet(httpExchange, contentType, body, user, database);
                break;
            case "POST":
                onPost(httpExchange, contentType, body, user, database);
                break;
            default:
                response(httpExchange, Utils.HTTP_CODE_INTERNAL_ERROR, "Cannot handle this request");
                break;
        }
    }

    /**
     * Handles a GET request on a SPARQL endpoint
     *
     * @param httpExchange The HTTP exchange
     * @param contentType  The content type for the request body
     * @param body         The request body
     * @param user         The current user
     * @param database     The current database
     */
    private void onGet(HttpExchange httpExchange, String contentType, String body, User user, Database database) {
        Map<String, List<String>> params = Utils.getRequestParameters(httpExchange.getRequestURI());
        List<String> vQuery = params.get("query");
        String query = vQuery == null ? null : vQuery.get(0);
        if (query != null) {
            if (body != null && !body.isEmpty()) {
                // should be empty
                // ill-formed request
                response(httpExchange, Utils.HTTP_CODE_PROTOCOL_ERROR, "Ill-formed request, content is not empty");
            } else {
                List<String> acceptTypes = Utils.getAcceptTypes(httpExchange.getRequestHeaders());
                List<String> defaults = params.get("default-graph-uri");
                List<String> named = params.get("named-graph-uri");
                String resultType = Utils.negotiateType(acceptTypes);
                executeSPARQL(
                        httpExchange,
                        query,
                        defaults == null ? new ArrayList<String>() : defaults,
                        named == null ? new ArrayList<String>() : named,
                        resultType, database);
            }
        } else {
            // ill-formed request
            response(httpExchange, Utils.HTTP_CODE_PROTOCOL_ERROR, "Ill-formed request, expected a query parameter");
        }
    }

    /**
     * Handles a POST request on a SPARQL endpoint
     *
     * @param httpExchange The HTTP exchange
     * @param contentType  The content type for the request body
     * @param body         The request body
     * @param user         The current user
     * @param database     The current database
     */
    private void onPost(HttpExchange httpExchange, String contentType, String body, User user, Database database) {
        Map<String, List<String>> params = Utils.getRequestParameters(httpExchange.getRequestURI());
        List<String> acceptTypes = Utils.getAcceptTypes(httpExchange.getRequestHeaders());
        switch (contentType) {
            case TYPE_SPARQL_QUERY:
            case TYPE_SPARQL_UPDATE: {
                List<String> defaults = params.get("default-graph-uri");
                List<String> named = params.get("named-graph-uri");
                String resultType = Utils.negotiateType(acceptTypes);
                executeSPARQL(
                        httpExchange,
                        body,
                        defaults == null ? new ArrayList<String>() : defaults,
                        named == null ? new ArrayList<String>() : named,
                        resultType, database);
                break;
            }
            case TYPE_URL_ENCODED: {
                // TODO: decode and implement this
                response(httpExchange, Utils.HTTP_CODE_INTERNAL_ERROR, "Not implemented");
                break;
            }
        }
    }

    /**
     * Executes a SPARQL request
     *
     * @param httpExchange The response to write to
     * @param body         The request body
     * @param defaultIRIs  The context's default IRIs
     * @param namedIRIs    The context's named IRIs
     * @param contentType  The negotiated content type for the response
     * @param database     The active database
     */
    private void executeSPARQL(HttpExchange httpExchange, String body, Collection<String> defaultIRIs, Collection<String> namedIRIs, String contentType, Database database) {
        BufferedLogger bufferedLogger = new BufferedLogger();
        DispatchLogger dispatchLogger = new DispatchLogger(database.getLogger(), bufferedLogger);
        SPARQLLoader loader = new SPARQLLoader(database.getRepository().getStore(), defaultIRIs, namedIRIs);
        List<Command> commands = loader.load(dispatchLogger, new StringReader(body));
        if (commands == null) {
            // ill-formed request
            dispatchLogger.error("Failed to parse and load the request");
            response(httpExchange, Utils.HTTP_CODE_PROTOCOL_ERROR, getLog(bufferedLogger));
            return;
        }
        Result result = ResultFailure.INSTANCE;
        for (Command command : commands) {
            result = command.execute(database.getRepository());
            if (result.isFailure()) {
                break;
            }
        }
        if (result.isFailure()) {
            response(httpExchange, Utils.HTTP_CODE_INTERNAL_ERROR, getLog(bufferedLogger));
        } else {
            StringWriter writer = new StringWriter();
            try {
                result.print(writer, coerceContentType(result, contentType));
            } catch (IOException exception) {
                exception.printStackTrace();
            }
            httpExchange.getResponseHeaders().add("Content-Type", coerceContentType(result, contentType));
            response(httpExchange, Utils.HTTP_CODE_OK, writer.toString());
        }
    }

    /**
     * Coerce the content type of a SPARQL response depending on the result type
     *
     * @param result The SPARQL result
     * @param type   The negotiated content type
     * @return The coerced content type
     */
    private static String coerceContentType(Result result, String type) {
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
     * Gets the content of the log
     *
     * @param logger The logger
     * @return The content of the log
     */
    private static String getLog(BufferedLogger logger) {
        StringBuilder builder = new StringBuilder();
        for (Object error : logger.getErrorMessages()) {
            builder.append(error.toString());
            builder.append("\n");
        }
        return builder.toString();
    }
}
