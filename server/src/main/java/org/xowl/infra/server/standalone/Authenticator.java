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

package org.xowl.infra.server.standalone;

import com.sun.net.httpserver.BasicAuthenticator;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpPrincipal;
import org.xowl.infra.server.impl.ServerController;
import org.xowl.infra.server.xsp.XSPReply;
import org.xowl.infra.utils.Base64;

import java.net.HttpURLConnection;
import java.net.InetAddress;

/**
 * HTTPS authenticator
 *
 * @author Laurent Wouters
 */
class Authenticator extends BasicAuthenticator {
    /**
     * The current controller
     */
    private final ServerController controller;

    /**
     * Initializes this authenticator
     *
     * @param controller The current controller
     * @param realm      The security realm
     */
    public Authenticator(ServerController controller, String realm) {
        super(realm);
        this.controller = controller;
    }

    @Override
    public Result authenticate(HttpExchange httpExchange) {
        InetAddress client = httpExchange.getRemoteAddress().getAddress();
        Headers requestHeaders = httpExchange.getRequestHeaders();
        String headerAuth = requestHeaders.getFirst("Authorization");
        if (headerAuth != null) {
            int index = headerAuth.indexOf(32);
            if (index != -1 && headerAuth.substring(0, index).equals("Basic")) {
                String authToken = Base64.decodeBase64(headerAuth.substring(index + 1));
                int indexColon = authToken.indexOf(58);
                if (indexColon != -1) {
                    String login = authToken.substring(0, indexColon);
                    String password = authToken.substring(indexColon + 1);
                    if (checkCredentials(client, login, password))
                        return new Success(new HttpPrincipal(login, this.realm));
                }
            }
        }
        Headers responseHeaders = httpExchange.getResponseHeaders();
        responseHeaders.set("WWW-Authenticate", "Basic realm=\"" + this.realm + "\"");
        return new Failure(HttpURLConnection.HTTP_UNAUTHORIZED);
    }

    /**
     * Checks the credential of a client
     *
     * @param client   The client
     * @param login    The login
     * @param password The password
     * @return Whether the authentication succeeded
     */
    public boolean checkCredentials(InetAddress client, String login, String password) {
        XSPReply reply = controller.login(client, login, password);
        return reply != null && reply.isSuccess();
    }

    @Override
    public boolean checkCredentials(String login, String password) {
        return false;
    }
}
