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
import org.xowl.store.sparql.Result;

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
 * Manages a connection to a remote xOWL server
 *
 * @author Laurent Wouters
 */
public class Connection {
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
     * The database to connect to
     */
    private final String database;
    /**
     * Login/Password for the endpoint
     */
    private final String authToken;

    /**
     * Initializes this connection
     *
     * @param endpoint URI of the endpoint
     * @param database The database to connect to
     * @param login    Login for the endpoint
     * @param password Password for the endpoint
     */
    public Connection(String endpoint, String database, String login, String password) {
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
            exception.printStackTrace();
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
        this.database = database;
        byte[] buffer = (login + ":" + password).getBytes(Charset.forName("UTF-8"));
        this.authToken = Base64.encode(buffer);
    }

    /**
     * Posts a SPARQL update to the remote endpoint
     *
     * @param request The SPARQL update
     * @param accept  The MIME type to accept for the response
     * @return The returned value
     */
    public Result sparqlUpdate(String request, String accept) {
        return sparql(request, "application/sparql-update", accept);
    }

    /**
     * Posts a SPARQL query to the remote endpoint
     *
     * @param request The SPARQL query
     * @param accept  The MIME type to accept for the response
     * @return The returned value
     */
    public Result sparqlQuery(String request, String accept) {
        return sparql(request, "application/sparql-query", accept);
    }

    /**
     * Posts a SPARQL request to the remote endpoint
     *
     * @param sparql The SPARQL request
     * @param type   The SPARQL request type
     * @param accept The MIME type to accept for the response
     * @return The returned value
     */
    private Result sparql(String sparql, String type, String accept) {
        Response response = request(sparql, type, accept);
        return null;
    }

    /**
     * Shutdowns the remote server
     *
     * @return Whether the request succeeded
     */
    public boolean adminServerShutdown() {
        Response response = request(null, "application/x-xowl-admin-shutdown", null);
        return response != null && response.code == HttpsURLConnection.HTTP_OK;
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
            exception.printStackTrace();
            return null;
        }

        HttpURLConnection connection;
        try {
            connection = (HttpURLConnection) url.openConnection();
        } catch (IOException exception) {
            exception.printStackTrace();
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
            exception.printStackTrace();
            return null;
        }
        connection.setRequestProperty("Authorization", "Basic " + authToken);
        connection.setRequestProperty("X-XOWL-Database", database);
        connection.setRequestProperty("Content-Type", contentType);
        if (accept != null)
            connection.setRequestProperty("Accept", accept);
        connection.setUseCaches(false);
        connection.setDoOutput(true);
        if (body != null) {
            try (OutputStream stream = connection.getOutputStream()) {
                stream.write(body.getBytes());
            } catch (IOException exception) {
                exception.printStackTrace();
                return null;
            }
        }

        Response response = new Response();
        try {
            response.code = connection.getResponseCode();
        } catch (IOException exception) {
            exception.printStackTrace();
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
                exception.printStackTrace();
            }
        }
        connection.disconnect();
        return response;
    }
}
