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

package org.xowl.server.http;

import com.sun.net.httpserver.HttpExchange;
import org.xowl.server.db.Controller;
import org.xowl.server.db.Database;
import org.xowl.server.db.User;

/**
 * Handler for administration requests
 *
 * @author Laurent Wouters
 */
class AdminHandler extends HandlerPart {
    /**
     * The content type for requesting the server shutdown
     */
    public static final String TYPE_ADMIN_SHUTDOWN = "application/x-xowl-admin-shutdown";
    /**
     * The content type for requesting the server startup
     */
    public static final String TYPE_ADMIN_RESTART = "application/x-xowl-admin-restart";
    /**
     * The content type for the creation of a user
     */
    public static final String TYPE_ADMIN_CREATE_USER = "application/x-xowl-admin-create-user";
    /**
     * The content type for the creation of a database
     */
    public static final String TYPE_ADMIN_CREATE_DATABASE = "application/x-xowl-admin-create-database";

    /**
     * Initializes this handler
     *
     * @param controller The top controller
     */
    public AdminHandler(Controller controller) {
        super(controller);
    }

    @Override
    public void handle(HttpExchange httpExchange, String method, String contentType, String body, User user, Database database) {
        switch (contentType) {
            case TYPE_ADMIN_SHUTDOWN: {
                if (controller.isServerAdmin(user)) {
                    controller.onRequestShutdown();
                    response(httpExchange, Utils.HTTP_CODE_OK, null);
                } else {
                    response(httpExchange, Utils.HTTP_CODE_UNAUTHORIZED, null);
                }
                break;
            }
            case TYPE_ADMIN_RESTART: {
                if (controller.isServerAdmin(user)) {
                    controller.onRequestShutdown();
                    response(httpExchange, Utils.HTTP_CODE_OK, null);
                } else {
                    response(httpExchange, Utils.HTTP_CODE_UNAUTHORIZED, null);
                }
                break;
            }
            case TYPE_ADMIN_CREATE_USER:
                if (controller.isServerAdmin(user)) {
                    createUser(httpExchange, body);
                } else {
                    response(httpExchange, Utils.HTTP_CODE_UNAUTHORIZED, null);
                }
                break;
            case TYPE_ADMIN_CREATE_DATABASE:
                if (controller.isServerAdmin(user)) {
                    createDatabase(httpExchange, body);
                } else {
                    response(httpExchange, Utils.HTTP_CODE_UNAUTHORIZED, null);
                }
                break;
        }
    }

    /**
     * Creates a new user
     *
     * @param httpExchange The HTTP exchange
     * @param body         The request body
     */
    private void createUser(HttpExchange httpExchange, String body) {
        String[] parts = body.split("(\r\n?)|(\r?\n)");
        if (parts.length != 2) {
            response(httpExchange, Utils.HTTP_CODE_PROTOCOL_ERROR, "Failed to parse the request");
            return;
        }
        boolean success = controller.newUser(parts[0], parts[1]);
        if (success)
            response(httpExchange, Utils.HTTP_CODE_OK, null);
        else
            response(httpExchange, Utils.HTTP_CODE_INTERNAL_ERROR, "Failed to create the new user");
    }

    /**
     * Creates a new database
     *
     * @param httpExchange The HTTP exchange
     * @param body         The request body
     */
    private void createDatabase(HttpExchange httpExchange, String body) {
        Database database = controller.newDatabase(body);
        if (database != null)
            response(httpExchange, Utils.HTTP_CODE_OK, null);
        else
            response(httpExchange, Utils.HTTP_CODE_INTERNAL_ERROR, "Failed to create the new user");
    }
}
