/*******************************************************************************
 * Copyright (c) 2016 Association Cénotélie (cenotelie.fr)
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
 ******************************************************************************/

package org.xowl.infra.store.http;

import org.apache.xerces.impl.dv.util.Base64;
import org.xowl.infra.utils.Files;
import org.xowl.infra.utils.logging.Logger;

import javax.net.ssl.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * Represents a basic HTTP connection
 *
 * @author Laurent Wouters
 */
public class HttpConnection implements Closeable {
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
     * @param endpoint URI of the endpoint (base target URI)
     * @param login    Login for the endpoint, if any, used for an HTTP Basic authentication
     * @param password Password for the endpoint, if any, used for an HTTP Basic authentication
     */
    public HttpConnection(String endpoint, String login, String password) {
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
            byte[] buffer = (login + ":" + password).getBytes(Files.CHARSET);
            this.authToken = Base64.encode(buffer);
        } else {
            this.authToken = null;
        }
    }

    @Override
    public void close() throws IOException {
        // nothing to do, HTTP connections are one-shot
    }

    /**
     * Sends an HTTP request to the endpoint, completed with an URI complement
     *
     * @param uriComplement The URI complement to append to the original endpoint URI, if any
     * @param method        The HTTP method to use, if any
     * @param body          The request body, if any
     * @param contentType   The request body content type, if any
     * @param accept        The MIME type to accept for the response, if any
     * @return The response, or null if the request failed before reaching the server
     */
    public HttpResponse request(String uriComplement, String method, String body, String contentType, String accept) {
        URL url;
        try {
            url = new URL((endpoint != null ? endpoint : "") + (uriComplement != null ? uriComplement : ""));
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
            connection.setRequestMethod(method == null || method.isEmpty() ? "GET" : method);
        } catch (ProtocolException exception) {
            Logger.DEFAULT.error(exception);
            return null;
        }
        if (contentType != null)
            connection.setRequestProperty("Content-Type", contentType);
        if (accept != null)
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

        int code;
        try {
            code = connection.getResponseCode();
        } catch (IOException exception) {
            Logger.DEFAULT.error(exception);
            connection.disconnect();
            return null;
        }
        String responseContentType = connection.getContentType();
        String responseBody = null;
        if (connection.getContentLengthLong() == -1 || connection.getContentLengthLong() > 0) {
            // if the content length is unknown or if there is content
            // for codes 4xx and 5xx, use the error stream
            // otherwise use the input stream
            try (InputStream is = ((code >= 400 && code < 600) ? connection.getErrorStream() : connection.getInputStream())) {
                BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                StringBuilder builder = new StringBuilder();
                char[] buffer = new char[1024];
                int read = rd.read(buffer);
                while (read > 0) {
                    builder.append(buffer, 0, read);
                    read = rd.read(buffer);
                }
                rd.close();
                responseBody = builder.toString();
            } catch (IOException exception) {
                Logger.DEFAULT.error(exception);
            }
        }
        connection.disconnect();
        return new HttpResponse(code, responseContentType, responseBody);
    }
}
