/*******************************************************************************
 * Copyright (c) 2017 Association Cénotélie (cenotelie.fr)
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

package org.xowl.infra.server.remote;

import fr.cenotelie.commons.utils.api.ApiFactory;
import fr.cenotelie.hime.redist.ASTNode;
import org.xowl.infra.server.api.*;
import org.xowl.infra.server.base.BaseDatabasePrivileges;
import org.xowl.infra.server.base.BaseRule;
import org.xowl.infra.server.base.BaseUserPrivileges;

/**
 * Implements the API remote objects factory
 *
 * @author Laurent Wouters
 */
public class RemoteFactory implements ApiFactory {
    /**
     * The parent server
     */
    private final RemoteServer server;

    /**
     * Initializes this factory
     *
     * @param server The parent server
     */
    public RemoteFactory(RemoteServer server) {
        this.server = server;
    }

    @Override
    public Object newObject(String type, ASTNode definition) {
        if (XOWLDatabase.class.getCanonicalName().equals(type)) {
            return new RemoteDatabase(server, definition);
        } else if (XOWLDatabasePrivileges.class.getCanonicalName().equals(type)) {
            return new BaseDatabasePrivileges(definition);
        } else if (XOWLRule.class.getCanonicalName().equals(type)) {
            return new BaseRule(definition);
        } else if (XOWLUser.class.getCanonicalName().equals(type)) {
            return new RemoteUser(server, definition);
        } else if (XOWLUserPrivileges.class.getCanonicalName().equals(type)) {
            return new BaseUserPrivileges(definition);
        } else if (XOWLDatabaseConfiguration.class.getCanonicalName().equals(type)) {
            return new XOWLDatabaseConfiguration(definition);
        }
        return null;
    }
}
