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

package org.xowl.infra.server.api;

import org.xowl.infra.server.xsp.XSPReply;

/**
 * The base API for a xOWL Server
 *
 * @author Laurent Wouters
 */
public interface XOWLServer {
    /**
     * Login a user
     *
     * @param login    The user to log in
     * @param password The user password
     * @return The protocol reply, or null if the client is banned
     */
    XSPReply login(String login, String password);

    /**
     * Requests the shutdown of the server
     *
     * @return The protocol reply
     */
    XSPReply serverShutdown();

    /**
     * Requests the restart of the server
     *
     * @return The protocol reply
     */
    XSPReply serverRestart();

    /**
     * Gets the user for the specified login
     *
     * @param login The user's login
     * @return The protocol reply
     */
    XSPReply getUser(String login);

    /**
     * Gets the users on this server
     *
     * @return The protocol reply
     */
    XSPReply getUsers();

    /**
     * Creates a new user for this server
     *
     * @param login    The login
     * @param password The password
     * @return The protocol reply
     */
    XSPReply createUser(String login, String password);

    /**
     * Deletes a user from this server
     *
     * @param toDelete The user to delete
     * @return The protocol reply
     */
    XSPReply deleteUser(XOWLUser toDelete);

    /**
     * Changes the password of the requesting user
     *
     * @param password The new password
     * @return The protocol reply
     */
    XSPReply changePassword(String password);

    /**
     * Resets the password of a target user
     *
     * @param target   The user for which the password should be changed
     * @param password The new password
     * @return The protocol reply
     */
    XSPReply resetPassword(XOWLUser target, String password);

    /**
     * Gets the privileges assigned to a user
     *
     * @param user The target user
     * @return The protocol reply
     */
    XSPReply getPrivileges(XOWLUser user);

    /**
     * Grants server administrative privilege to a target user
     *
     * @param target The target user
     * @return The protocol reply
     */
    XSPReply grantServerAdmin(XOWLUser target);

    /**
     * Revokes server administrative privilege to a target user
     *
     * @param target The target user
     * @return The protocol reply
     */
    XSPReply revokeServerAdmin(XOWLUser target);

    /**
     * Grants a privilege to a user on a database
     *
     * @param user      The target user
     * @param database  The database
     * @param privilege The privilege to grant
     * @return The protocol reply
     */
    XSPReply grantDB(XOWLUser user, XOWLDatabase database, int privilege);

    /**
     * Revokes a privilege from a user on a database
     *
     * @param user      The target user
     * @param database  The database
     * @param privilege The privilege to revoke
     * @return The protocol reply
     */
    XSPReply revokeDB(XOWLUser user, XOWLDatabase database, int privilege);

    /**
     * Gets the database for the specified name
     *
     * @param name The name of a database
     * @return The protocol reply
     */
    XSPReply getDatabase(String name);

    /**
     * Gets the databases on this server
     *
     * @return The protocol reply
     */
    XSPReply getDatabases();

    /**
     * Creates a new database
     *
     * @param name The name of the database
     * @return The protocol reply
     */
    XSPReply createDatabase(String name);

    /**
     * Drops a database
     *
     * @param database The database to drop
     * @return The protocol reply
     */
    XSPReply dropDatabase(XOWLDatabase database);

    /**
     * Gets the privileges assigned to users on a database
     *
     * @param database The target database
     * @return The protocol reply
     */
    XSPReply getPrivileges(XOWLDatabase database);
}
