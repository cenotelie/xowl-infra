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

import java.io.Closeable;

/**
 * The base API for a xOWL Server
 *
 * @author Laurent Wouters
 */
public interface XOWLServer extends Closeable {
    /**
     * Gets whether a user is logged-in
     *
     * @return Whether a user is logged-in
     */
    boolean isLoggedIn();

    /**
     * Gets the currently logged-in user, if any
     *
     * @return The currently logged-in user, if any
     */
    XOWLUser getLoggedInUser();

    /**
     * Login a user
     *
     * @param login    The user to log in
     * @param password The user password
     * @return The protocol reply, or null if the client is banned
     */
    XSPReply login(String login, String password);

    /**
     * Logout the current user
     *
     * @return The protocol reply
     */
    XSPReply logout();

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
     * Grants server administrative privilege to a target user
     *
     * @param target The target user
     * @return The protocol reply
     */
    XSPReply serverGrantAdmin(XOWLUser target);

    /**
     * Grants server administrative privilege to a target user
     *
     * @param target The target user
     * @return The protocol reply
     */
    XSPReply serverGrantAdmin(String target);

    /**
     * Revokes server administrative privilege to a target user
     *
     * @param target The target user
     * @return The protocol reply
     */
    XSPReply serverRevokeAdmin(XOWLUser target);

    /**
     * Revokes server administrative privilege to a target user
     *
     * @param target The target user
     * @return The protocol reply
     */
    XSPReply serverRevokeAdmin(String target);

    /**
     * Gets the databases on this server
     *
     * @return The protocol reply
     */
    XSPReply getDatabases();

    /**
     * Gets the database for the specified identifier
     *
     * @param identifier The identifier of a database
     * @return The protocol reply
     */
    XSPReply getDatabase(String identifier);

    /**
     * Creates a new database
     *
     * @param identifier The identifier of the database
     * @return The protocol reply
     */
    XSPReply createDatabase(String identifier);

    /**
     * Drops a database
     *
     * @param database The database to drop
     * @return The protocol reply
     */
    XSPReply dropDatabase(XOWLDatabase database);

    /**
     * Drops a database
     *
     * @param database The database to drop
     * @return The protocol reply
     */
    XSPReply dropDatabase(String database);

    /**
     * Gets the users on this server
     *
     * @return The protocol reply
     */
    XSPReply getUsers();

    /**
     * Gets the user for the specified login
     *
     * @param login The user's login
     * @return The protocol reply
     */
    XSPReply getUser(String login);

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
     * Deletes a user from this server
     *
     * @param toDelete The user to delete
     * @return The protocol reply
     */
    XSPReply deleteUser(String toDelete);
}
