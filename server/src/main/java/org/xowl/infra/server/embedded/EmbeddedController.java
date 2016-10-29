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

package org.xowl.infra.server.embedded;

import org.xowl.infra.server.impl.*;
import org.xowl.infra.store.ProxyObject;
import org.xowl.infra.utils.logging.Logger;

/**
 * Represents a controller for an embedded database server
 *
 * @author Laurent Wouters
 */
class EmbeddedController extends ServerController {
    /**
     * Initializes this controller
     *
     * @param logger        The logger for this controller
     * @param configuration The current configuration
     * @throws Exception When the location cannot be accessed
     */
    public EmbeddedController(Logger logger, ServerConfiguration configuration) throws Exception {
        super(logger, configuration);
    }

    /**
     * Gets the user for the specified login
     *
     * @param login The login of a user
     * @return The user, or null if there is none for this login
     */
    protected UserImpl getPrincipal(String login) {
        return doGetUser(login);
    }

    @Override
    protected DatabaseImpl newDB(DatabaseController controller, ProxyObject proxy) {
        return new EmbeddedDatabase(logger, controller, proxy);
    }

    @Override
    protected DatabaseImpl newDB(DatabaseController controller, ProxyObject proxy, String name) {
        return new EmbeddedDatabase(logger, controller, proxy, name);
    }
}
