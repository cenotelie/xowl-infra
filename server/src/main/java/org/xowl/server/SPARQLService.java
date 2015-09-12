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
import org.xowl.store.Repository;
import org.xowl.store.loaders.SPARQLLoader;
import org.xowl.store.sparql.Command;
import org.xowl.store.sparql.Result;
import org.xowl.store.sparql.ResultQuads;
import org.xowl.store.sparql.ResultSuccess;
import org.xowl.utils.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.StringReader;
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
                response.setStatus(400);
            } else {
                String contentType = negotiateType(getContentTypes(request));
                String[] defaults = request.getParameterValues("default-graph-uri");
                String[] named = request.getParameterValues("named-graph-uri");
                response.setHeader("Content-Type", contentType);
                executeRequest(query, Arrays.asList(defaults), Arrays.asList(named), contentType, response);
            }
        } else {
            // expected a query parameter
            // ill-formed request
            response.setStatus(400);
        }
    }

    @Override
    public void onPost(HttpServletRequest request, HttpServletResponse response) {
        // common response configuration
        response.setCharacterEncoding("UTF-8");

        List<String> contentTypes = getContentTypes(request);
        if (contentTypes.contains(TYPE_SPARQL_QUERY) || contentTypes.contains(TYPE_SPARQL_UPDATE)) {
            String[] defaults = request.getParameterValues("default-graph-uri");
            String[] named = request.getParameterValues("named-graph-uri");
            String contentType = negotiateType(contentTypes);
            response.setHeader("Content-Type", contentType);
            String query = getMessageBody(request);
            executeRequest(query, Arrays.asList(defaults), Arrays.asList(named), contentType, response);
        } else if (contentTypes.contains(TYPE_URL_ENCODED)) {
            String contentType = negotiateType(contentTypes);
            response.setHeader("Content-Type", contentType);
            String content = getMessageBody(request);
            // TODO: decode and implement this
            response.setStatus(501);
        } else {
            // incorrect content types
            response.setStatus(400);
        }
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
    private void executeRequest(String request, Collection<String> defaultIRIs, Collection<String> namedIRIs, String contentType, HttpServletResponse response) {
        SPARQLLoader loader = new SPARQLLoader(repository.getStore(), defaultIRIs, namedIRIs);
        List<Command> commands = loader.load(logger, new StringReader(request));
        if (commands == null) {
            // ill-formed request
            response.setStatus(400);
            return;
        }
        Result result = ResultSuccess.INSTANCE;
        for (Command command : commands) {
            Result temp = command.execute(repository);
            if (temp.isFailure()) {
                result = temp;
                break;
            }
        }
        response.setStatus(result.isFailure() ? 500 : 200);
        try {
            result.print(response.getWriter(), coerceContentType(result, contentType));
        } catch (IOException exception) {
            logger.error(exception);
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
}
