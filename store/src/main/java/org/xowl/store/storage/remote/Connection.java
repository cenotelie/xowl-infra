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

import org.xowl.hime.redist.ASTNode;
import org.xowl.hime.redist.ParseError;
import org.xowl.hime.redist.ParseResult;
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

import javax.net.ssl.*;
import java.io.Closeable;
import java.io.Reader;
import java.io.StringReader;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;

/**
 * Represents a connection to a remote database
 *
 * @author Laurent Wouters
 */
abstract class Connection implements Closeable {
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
     * The SSL context for HTTPS connections
     */
    protected final SSLContext sslContext;
    /**
     * The host name verifier for HTTPS connections
     */
    protected final HostnameVerifier hostnameVerifier;

    /**
     * Initializes this connection
     */
    public Connection() {
        SSLContext sc = null;
        try {
            sc = SSLContext.getInstance("SSL");
            sc.init(null, new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                            System.out.println();
                            // TODO: check certificate
                        }

                        @Override
                        public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                            System.out.println();
                            // TODO: check certificate
                        }

                        @Override
                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[0];
                        }
                    }
            }, new SecureRandom());
        } catch (NoSuchAlgorithmException | KeyManagementException exception) {
            Logger.DEFAULT.error(exception);
        }
        sslContext = sc;
        hostnameVerifier = new HostnameVerifier() {
            @Override
            public boolean verify(String s, SSLSession sslSession) {
                // TODO: check host name
                return true;
            }
        };
    }

    /**
     * Sends a SPARQL query command to the remote host
     *
     * @param command The SPARQL command
     * @return The result
     */
    public abstract Result sparqlQuery(String command);

    /**
     * Sends a SPARQL update command to the remote host
     *
     * @param command The SPARQL command
     * @return The result
     */
    public abstract Result sparqlUpdate(String command);


    /**
     * Parses a SPARQL response as solutions
     *
     * @param content The content to parse
     * @return The parsed SPARQL result
     */
    public static Result parseResponseSolutions(String content) {
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
    }

    /**
     * Parses a SPARQL response as quads
     *
     * @param content The content to parse
     * @return The parsed SPARQL result
     */
    public static Result parseResponseQuads(String content) {
        BufferedLogger bufferedLogger = new BufferedLogger();
        DispatchLogger dispatchLogger = new DispatchLogger(Logger.DEFAULT, bufferedLogger);
        NQuadsLoader loader = new NQuadsLoader(new CachedNodes());
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

    /**
     * Parses a SPARQL response to an update command
     *
     * @param content The content to parse
     * @return The parsed SPARQL result
     */
    public static Result parseResponseUpdate(String content) {
        if (content == null)
            return ResultSuccess.INSTANCE;
        if ("OK".equalsIgnoreCase(content))
            return ResultSuccess.INSTANCE;
        if ("true".equalsIgnoreCase(content))
            return new ResultYesNo(true);
        if ("false".equalsIgnoreCase(content))
            return new ResultYesNo(false);
        return ResultSuccess.INSTANCE;
    }
}
