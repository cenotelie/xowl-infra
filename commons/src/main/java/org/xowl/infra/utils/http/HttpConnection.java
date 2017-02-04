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

package org.xowl.infra.utils.http;

import org.xowl.infra.utils.Base64;
import org.xowl.infra.utils.Files;
import org.xowl.infra.utils.logging.Logging;

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
import java.util.*;

/**
 * Represents a basic HTTP connection
 *
 * @author Laurent Wouters
 */
public class HttpConnection implements Closeable {
    /**
     * Represents a trust manager that accepts all certificates
     */
    private static final TrustManager TRUST_MANAGER_ACCEPT_ALL = new X509TrustManager() {
        @Override
        public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    };

    /**
     * Represents a hostname verifier that accepts all hosts
     */
    private static final HostnameVerifier HOSTNAME_VERIFIER_ACCEPT_ALL = new HostnameVerifier() {
        @Override
        public boolean verify(String s, SSLSession sslSession) {
            return true;
        }
    };

    /**
     * Represents a cookie for a connection
     */
    private final class Cookie {
        /**
         * The name of the cookie
         */
        public final String name;
        /**
         * The associated value
         */
        public final String value;
        /**
         * The properties associated to the cookie
         */
        public final Collection<String> properties;

        /**
         * Initializes this cookie
         *
         * @param content The original content
         */
        public Cookie(String content) {
            String data = content.trim();
            int indexEqual = content.indexOf('=');
            int indexSemicolon = content.indexOf(';');
            this.name = content.substring(0, indexEqual).trim();
            this.value = content.substring(indexEqual, indexSemicolon < 0 ? content.length() : indexSemicolon).trim();
            this.properties = new ArrayList<>();
            if (indexSemicolon > 0) {
                data = data.substring(indexSemicolon + 1).trim();
                while (!data.isEmpty()) {
                    indexSemicolon = data.indexOf(';');
                    String value = data.substring(0, indexSemicolon < 0 ? data.length() : indexSemicolon).trim();
                    properties.add(value);
                    if (indexSemicolon > 0) {
                        data = data.substring(indexSemicolon + 1).trim();
                    }
                }
            }
        }
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
     * The cookies for this connection
     */
    private final Map<String, Cookie> cookies;
    /**
     * Login/Password for the endpoint, if any, used for an HTTP Basic authentication
     */
    private final String authToken;

    /**
     * Initializes this connection
     *
     * @param endpoint URI of the endpoint (base target URI)
     */
    public HttpConnection(String endpoint) {
        this(endpoint, null, null);
    }

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
            sc = SSLContext.getInstance("TLSv1.2");
            sc.init(null, new TrustManager[]{TRUST_MANAGER_ACCEPT_ALL}, new SecureRandom());
        } catch (NoSuchAlgorithmException | KeyManagementException exception) {
            Logging.getDefault().error(exception);
        }
        this.sslContext = sc;
        this.hostnameVerifier = HOSTNAME_VERIFIER_ACCEPT_ALL;
        this.endpoint = endpoint;
        this.cookies = new HashMap<>();
        if (login != null && password != null) {
            String buffer = (login + ":" + password);
            this.authToken = Base64.encodeBase64(buffer);
        } else {
            this.authToken = null;
        }
    }

    @Override
    public void close() {
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
        return request(uriComplement, method, body.getBytes(Files.CHARSET), contentType, false, accept);
    }

    /**
     * Sends an HTTP request to the endpoint, completed with an URI complement
     *
     * @param uriComplement The URI complement to append to the original endpoint URI, if any
     * @param method        The HTTP method to use, if any
     * @param accept        The MIME type to accept for the response, if any
     * @return The response, or null if the request failed before reaching the server
     */
    public HttpResponse request(String uriComplement, String method, String accept) {
        return request(uriComplement, method, null, null, false, accept);
    }

    /**
     * Sends an HTTP request to the endpoint, completed with an URI complement
     *
     * @param uriComplement The URI complement to append to the original endpoint URI, if any
     * @param method        The HTTP method to use, if any
     * @param body          The request body, if any
     * @param contentType   The request body content type, if any
     * @param compressed    Whether the body is compressed with gzip
     * @param accept        The MIME type to accept for the response, if any
     * @return The response, or null if the request failed before reaching the server
     */
    public HttpResponse request(String uriComplement, String method, byte[] body, String contentType, boolean compressed, String accept) {
        URL url;
        try {
            url = new URL((endpoint != null ? endpoint : "") + (uriComplement != null ? uriComplement : ""));
        } catch (MalformedURLException exception) {
            Logging.getDefault().error(exception);
            return null;
        }

        HttpURLConnection connection;
        try {
            connection = (HttpURLConnection) url.openConnection();
        } catch (IOException exception) {
            Logging.getDefault().error(exception);
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
            Logging.getDefault().error(exception);
            return null;
        }
        if (accept != null)
            connection.setRequestProperty("Accept", accept);
        if (authToken != null)
            connection.setRequestProperty("Authorization", "Basic " + authToken);
        if (!cookies.isEmpty()) {
            StringBuilder builder = new StringBuilder();
            boolean first = true;
            for (Cookie cookie : cookies.values()) {
                if (!first)
                    builder.append("; ");
                first = false;
                builder.append(cookie.name);
                builder.append("=");
                builder.append(cookie.value);
            }
            connection.setRequestProperty(HttpConstants.HEADER_COOKIE, builder.toString());
        }
        connection.setUseCaches(false);
        connection.setDoOutput(true);
        if (body != null) {
            if (contentType != null)
                connection.setRequestProperty("Content-Type", contentType);
            if (compressed)
                connection.setRequestProperty("Content-Encoding", "gzip");
            try (OutputStream stream = connection.getOutputStream()) {
                stream.write(body);
            } catch (IOException exception) {
                Logging.getDefault().error(exception);
                return null;
            }
        }

        int code;
        try {
            code = connection.getResponseCode();
        } catch (IOException exception) {
            Logging.getDefault().error(exception);
            connection.disconnect();
            return null;
        }

        List<String> values = connection.getHeaderFields().get(HttpConstants.HEADER_SET_COOKIE);
        if (values != null) {
            for (String value : values) {
                Cookie cookie = new Cookie(value);
                cookies.put(cookie.name, cookie);
            }
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
                Logging.getDefault().error(exception);
            }
        }
        connection.disconnect();
        return new HttpResponse(code, responseContentType, responseBody);
    }
}
