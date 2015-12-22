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
import org.xowl.store.loaders.JSONLDLoader;
import org.xowl.store.sparql.Result;
import org.xowl.store.storage.NodeManager;
import org.xowl.store.storage.cache.CachedNodes;
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
import java.util.ArrayList;
import java.util.Collection;

/**
 * Represents a connection to a remote database
 *
 * @author Laurent Wouters
 */
public abstract class Connection implements Closeable {
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
                            // TODO: check certificate
                        }

                        @Override
                        public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
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
     * Sends a SPARQL command to the remote host
     *
     * @param command The SPARQL command
     * @return The result
     */
    public abstract Result sparql(String command);

    /**
     * Sends an XSP command to the remote host
     *
     * @param command The XSP command
     * @return The result
     */
    public abstract XSPReply xsp(String command);

    /**
     * Parses a XSP result serialized in JSON
     *
     * @param content The content
     * @return The result
     */
    protected XSPReply parseXSPResponseJSON(String content) {
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
            dispatchLogger.error("Failed to parse the response");
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
            return new XSPReplyFailure(builder.toString());
        }

        Object obj = loadXSPObject(parseResult.getRoot());
        if (obj == null)
            return new XSPReplyFailure("Unexpected JSON format");
        return new XSPReplyResult<>(obj);
    }

    /**
     * Loads an XSP object from the specified AST root
     *
     * @param root The AST root node for an object
     * @return The object, or null if it is not recognized
     */
    protected Object loadXSPObject(ASTNode root) {
        if ("array".equals(root.getSymbol().getName()))
            return null;

        String value = root.getValue();
        if (value != null)
            return value;

        ASTNode nodeType = null;
        ASTNode nodeResults = null;
        ASTNode nodeName = null;
        for (ASTNode memberNode : root.getChildren()) {
            String memberName = IOUtils.unescape(memberNode.getChildren().get(0).getValue());
            memberName = memberName.substring(1, memberName.length() - 1);
            ASTNode memberValue = memberNode.getChildren().get(1);
            switch (memberName) {
                case "type":
                    nodeType = memberValue;
                    break;
                case "results":
                    nodeResults = memberValue;
                    break;
                case "name":
                    nodeName = memberValue;
                    break;
            }
        }

        if (nodeType == null)
            return null;
        String type = IOUtils.unescape(nodeType.getValue());
        type = type.substring(1, type.length() - 1);

        switch (type) {
            case "Collection":
                return loadXSPObjectCollection(nodeResults);
            case "org.xowl.server.db.User":
            case "org.xowl.server.db.Database":
                if (nodeName == null)
                    return null;
                String name = IOUtils.unescape(nodeName.getValue());
                return name.substring(1, name.length() - 1);
            case "org.xowl.server.db.UserPrivileges":
            case "org.xowl.server.db.DatabasePrivileges":
                // TODO: implement this
                return null;
            case "org.xowl.store.rdf.RuleExplanation":
                // TODO: implement this
                return null;
            case "org.xowl.store.rete.MatchStatus":
                // TODO: implement this
                return null;
        }
        return null;
    }

    /**
     * Loads an XSP result as a collection
     *
     * @param nodeResults The AST node for the collection
     * @return The collection
     */
    protected Object loadXSPObjectCollection(ASTNode nodeResults) {
        if (nodeResults == null)
            return null;
        Collection<Object> result = new ArrayList<>(nodeResults.getChildren().size());
        for (ASTNode child : nodeResults.getChildren()) {
            Object obj = loadXSPObject(child);
            if (obj != null)
                result.add(obj);
        }
        return result;
    }
}
