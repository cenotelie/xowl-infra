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

import org.xowl.store.sparql.Result;
import org.xowl.utils.logging.Logger;

import javax.net.ssl.*;
import java.io.Closeable;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * Represents a connection to a remote database
 *
 * @author Laurent Wouters
 */
abstract class Connection implements Closeable {
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
}
