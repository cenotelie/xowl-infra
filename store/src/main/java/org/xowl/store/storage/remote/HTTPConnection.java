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
import org.xowl.store.AbstractRepository;
import org.xowl.store.IOUtils;
import org.xowl.store.sparql.Command;
import org.xowl.store.sparql.Result;
import org.xowl.store.sparql.ResultFailure;
import org.xowl.store.sparql.ResultUtils;
import org.xowl.store.xsp.*;
import org.xowl.utils.logging.Logger;

import javax.net.ssl.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * Manages a connection to a remote endpoint
 *
 * @author Laurent Wouters
 */
public class HTTPConnection implements Connection {
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
     * The SSL context for HTTPS connections
     */
    private final SSLContext sslContext;
    /**
     * The host name verifier for HTTPS connections
     */
    private final HostnameVerifier hostnameVerifier;
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
        this.endpoint = endpoint;
        if (login != null && password != null) {
            byte[] buffer = (login + ":" + password).getBytes(Charset.forName("UTF-8"));
            this.authToken = Base64.encode(buffer);
        } else {
            this.authToken = null;
        }
    }

    @Override
    public Result sparql(String command) {
        Response response = request(command, Command.MIME_SPARQL_QUERY, AbstractRepository.SYNTAX_NQUADS + "; " + Result.SYNTAX_JSON);
        if (response == null)
            return new ResultFailure("connection failed");
        if (response.code != HttpURLConnection.HTTP_OK)
            return new ResultFailure(response.body != null ? response.body : "failure (HTTP " + response.code + ")");
        return ResultUtils.parseResponse(response.body, response.type);
    }

    @Override
    public XSPReply xsp(String command) {
        Response response = request(command, XSPReply.MIME_XSP_COMMAND, Result.SYNTAX_JSON);
        if (response == null)
            return new XSPReplyNetworkError("connection failed");
        if (response.code == HttpURLConnection.HTTP_UNAUTHORIZED)
            return XSPReplyUnauthenticated.instance();
        if (response.code == HttpURLConnection.HTTP_FORBIDDEN)
            return XSPReplyUnauthorized.instance();
        if (response.code == HttpURLConnection.HTTP_INTERNAL_ERROR)
            return new XSPReplyFailure(response.body);
        if (response.code != HttpURLConnection.HTTP_OK)
            return new XSPReplyFailure(response.body != null ? response.body : "failure (HTTP " + response.code + ")");
        // the result is OK from hereon
        if (response.body == null)
            return XSPReplySuccess.instance();
        if (response.type == null || IOUtils.MIME_TEXT_PLAIN.equals(response.type))
            // no response type or plain text
            return new XSPReplyResult<>(response.body);
        if (IOUtils.MIME_JSON.equals(response.type))
            // pure JSON response
            return XSPReplyUtils.parseJSONResult(response.body);
        // assume SPARQL reply
        Result sparqlResult = ResultUtils.parseResponse(response.body, response.type);
        return new XSPReplyResult<>(sparqlResult);
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
