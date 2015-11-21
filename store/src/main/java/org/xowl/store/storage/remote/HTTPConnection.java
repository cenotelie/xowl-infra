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

package org.xowl.store.storage.remote;

import org.apache.xerces.impl.dv.util.Base64;
import org.xowl.hime.redist.ASTNode;
import org.xowl.hime.redist.ParseError;
import org.xowl.hime.redist.ParseResult;
import org.xowl.store.AbstractRepository;
import org.xowl.store.IOUtils;
import org.xowl.store.IRIs;
import org.xowl.store.loaders.JSONLDLoader;
import org.xowl.store.loaders.NQuadsLoader;
import org.xowl.store.loaders.RDFLoaderResult;
import org.xowl.store.rdf.Node;
import org.xowl.store.rdf.QuerySolution;
import org.xowl.store.rdf.VariableNode;
import org.xowl.store.sparql.*;
import org.xowl.store.storage.NodeManager;
import org.xowl.store.storage.cache.CachedNodes;
import org.xowl.utils.collections.Couple;
import org.xowl.utils.logging.BufferedLogger;
import org.xowl.utils.logging.DispatchLogger;
import org.xowl.utils.logging.Logger;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.*;

/**
 * Manages a connection to a standard remote SPARQL endpoint
 *
 * @author Laurent Wouters
 */
public class HTTPConnection extends Connection {
    /**
     * The MIME content-type for a SPARQL query
     */
    private static final String MIME_SPARQL_QUERY = "application/sparql-query";
    /**
     * The MIME content type for a SPARQL update
     */
    private static final String MIME_SPARQL_UPDATE = "application/sparql-update";

    /**
     * A response to a request
     */
    private static class Response {
        /**
         * The HTTP response code
         */
        public int code;
        /**
         * The response body, if any
         */
        public String body;
        /**
         * The content type for the response body, if any
         */
        public String type;
    }

    /**
     * An array of SPARQL solutions
     */
    private static class ArraySolutions implements Solutions {
        /**
         * The content
         */
        private final List<QuerySolution> content;

        /**
         * Initializes the solutions
         */
        public ArraySolutions() {
            content = new ArrayList<>();
        }

        /**
         * Adds a new solution to this set
         *
         * @param solution The new solution
         */
        public void add(QuerySolution solution) {
            content.add(solution);
        }

        @Override
        public int size() {
            return content.size();
        }

        @Override
        public Iterator<QuerySolution> iterator() {
            return content.iterator();
        }
    }

    /**
     * URI of the endpoint
     */
    private final String endpoint;
    /**
     * Login/Password for the endpoint, if any, used for an HTTP Basic authentication
     */
    private final String authToken;

    /**
     * Initializes this connection
     *
     * @param endpoint URI of the endpoint
     * @param login    Login for the endpoint, if any, used for an HTTP Basic authentication
     * @param password Password for the endpoint, if any, used for an HTTP Basic authentication
     */
    public HTTPConnection(String endpoint, String login, String password) {
        this.endpoint = endpoint;
        if (login != null && password != null) {
            byte[] buffer = (login + ":" + password).getBytes(Charset.forName("UTF-8"));
            this.authToken = Base64.encode(buffer);
        } else {
            this.authToken = null;
        }
    }

    @Override
    public Result sparqlQuery(String command) {
        Response response = request(command, MIME_SPARQL_QUERY, AbstractRepository.SYNTAX_NQUADS + "; " + Result.SYNTAX_JSON);
        if (response == null)
            return new ResultFailure("connection failed");
        if (response.code != HttpURLConnection.HTTP_OK)
            return new ResultFailure(response.body != null ? response.body : "failure");
        if (Result.SYNTAX_JSON.equals(response.type)) {
            // solution set
            NodeManager nodeManager = new CachedNodes();
            JSONLDLoader loader = new JSONLDLoader(nodeManager) {
                @Override
                protected Reader getReaderFor(Logger logger, String iri) {
                    return null;
                }
            };
            BufferedLogger bufferedLogger = new BufferedLogger();
            DispatchLogger dispatchLogger = new DispatchLogger(Logger.DEFAULT, bufferedLogger);
            ParseResult parseResult = loader.parse(dispatchLogger, new StringReader(response.body));
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
            Map<String, VariableNode> variables = new HashMap<>();
            // nodeRoot is the object
            // root[0] is the 'head' member
            // root[0][1] is the value of the 'vars' member, which is an array
            // we iterate over the values in this array
            for (ASTNode nodeVariable : nodeRoot.getChildren().get(0).getChildren().get(1).getChildren()) {
                String name = nodeVariable.getValue();
                variables.put(name, new VariableNode(name));
            }

            ArraySolutions solutions = new ArraySolutions();
            // nodeRoot is the object
            // root[1] is the 'results' member
            // root[1][1] is the value of the 'results' member, which is an array
            // we iterate over the values in this array
            for (ASTNode nodeSolution : nodeRoot.getChildren().get(1).getChildren().get(1).getChildren()) {
                List<Couple<VariableNode, Node>> bindings = new ArrayList<>();
                for (ASTNode nodeBinding : nodeSolution.getChildren()) {
                    VariableNode variable = variables.get(nodeBinding.getChildren().get(0).getValue());
                    Node value = IOUtils.deserializeJSON(nodeManager, nodeBinding.getChildren().get(1));
                    bindings.add(new Couple<>(variable, value));
                }
                solutions.add(new QuerySolution(bindings));
            }
            return new ResultSolutions(solutions);
        } else if (AbstractRepository.SYNTAX_NQUADS.equals(response.type)) {
            // quads
            BufferedLogger bufferedLogger = new BufferedLogger();
            DispatchLogger dispatchLogger = new DispatchLogger(Logger.DEFAULT, bufferedLogger);
            NQuadsLoader loader = new NQuadsLoader(new CachedNodes());
            RDFLoaderResult loaderResult = loader.loadRDF(dispatchLogger, new StringReader(response.body), null, IRIs.GRAPH_DEFAULT);
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
        } else {
            // unknown
            return new ResultFailure("Unknown content type " + response.type);
        }
    }

    @Override
    public Result sparqlUpdate(String command) {
        Response response = request(command, MIME_SPARQL_UPDATE, Result.SYNTAX_CSV);
        if (response == null)
            return new ResultFailure("connection failed");
        if (response.code != HttpURLConnection.HTTP_OK)
            return new ResultFailure(response.body != null ? response.body : "failure");
        if (response.body == null)
            return ResultSuccess.INSTANCE;
        if ("OK".equalsIgnoreCase(response.body))
            return ResultSuccess.INSTANCE;
        if ("true".equalsIgnoreCase(response.body))
            return new ResultYesNo(true);
        if ("false".equalsIgnoreCase(response.body))
            return new ResultYesNo(false);
        return ResultSuccess.INSTANCE;
    }

    @Override
    public void close() throws IOException {
        // nothing to do, HTTP connections are one-shot
    }

    /**
     * Sends an HTTP request to the endpoint
     *
     * @param body        The request body
     * @param contentType The request body content type
     * @param accept      The MIME type to accept for the response
     * @return The response, or null if the request failed before reaching the server
     */
    private Response request(String body, String contentType, String accept) {
        URL url;
        try {
            url = new URL(endpoint);
        } catch (MalformedURLException exception) {
            Logger.DEFAULT.error(exception);
            return null;
        }

        HttpURLConnection connection;
        try {
            connection = (HttpURLConnection) url.openConnection();
        } catch (IOException exception) {
            Logger.DEFAULT.error(exception);
            return null;
        }
        if (connection instanceof HttpsURLConnection) {
            // for SSL connections we should do this
            ((HttpsURLConnection) connection).setHostnameVerifier(hostnameVerifier);
            ((HttpsURLConnection) connection).setSSLSocketFactory(sslContext.getSocketFactory());
        }
        try {
            connection.setRequestMethod("POST");
        } catch (ProtocolException exception) {
            Logger.DEFAULT.error(exception);
            return null;
        }
        connection.setRequestProperty("Content-Type", contentType);
        connection.setRequestProperty("Accept", accept);
        if (authToken != null)
            connection.setRequestProperty("Authorization", "Basic " + authToken);
        connection.setUseCaches(false);
        connection.setDoOutput(true);
        if (body != null) {
            try (OutputStream stream = connection.getOutputStream()) {
                stream.write(body.getBytes());
            } catch (IOException exception) {
                Logger.DEFAULT.error(exception);
                return null;
            }
        }

        Response response = new Response();
        try {
            response.code = connection.getResponseCode();
        } catch (IOException exception) {
            Logger.DEFAULT.error(exception);
            connection.disconnect();
            return null;
        }
        response.type = connection.getContentType();
        if (connection.getContentLengthLong() > 0) {
            try (InputStream is = connection.getInputStream()) {
                BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                StringBuilder builder = new StringBuilder();
                char[] buffer = new char[1024];
                int read = rd.read(buffer);
                while (read > 0) {
                    builder.append(buffer, 0, read);
                    read = rd.read(buffer);
                }
                rd.close();
                response.body = builder.toString();
            } catch (IOException exception) {
                Logger.DEFAULT.error(exception);
            }
        }
        connection.disconnect();
        return response;
    }
}
