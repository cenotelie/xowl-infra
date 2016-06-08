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

package org.xowl.infra.server.base;

/**
 * Constants for the server schema
 *
 * @author Laurent Wouters
 */
public interface Schema {
    /**
     * The User concept in the administration database
     */
    String ADMIN_USER = "http://xowl.org/server/admin#User";
    /**
     * The Database concept in the administration database
     */
    String ADMIN_DATABASE = "http://xowl.org/server/admin#Database";
    /**
     * The Name concept in the administration database
     */
    String ADMIN_NAME = "http://xowl.org/server/admin#name";
    /**
     * The Location concept in the administration database
     */
    String ADMIN_LOCATION = "http://xowl.org/server/admin#location";
    /**
     * The Password concept in the administration database
     */
    String ADMIN_PASSWORD = "http://xowl.org/server/admin#password";
    /**
     * The AdminOf concept in the administration database
     */
    String ADMIN_ADMINOF = "http://xowl.org/server/admin#adminOf";
    /**
     * The CanRead concept in the administration database
     */
    String ADMIN_CANREAD = "http://xowl.org/server/admin#canRead";
    /**
     * The CanWrite concept in the administration database
     */
    String ADMIN_CANWRITE = "http://xowl.org/server/admin#canWrite";
    /**
     * The User graph in the administration database
     */
    String ADMIN_GRAPH_USERS = "http://xowl.org/server/users#";
    /**
     * The Database graph in the administration database
     */
    String ADMIN_GRAPH_DBS = "http://xowl.org/server/db#";
}
