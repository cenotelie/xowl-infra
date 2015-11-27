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

package org.xowl.server.db;

import org.xowl.store.Serializable;
import org.xowl.utils.Files;
import org.xowl.utils.collections.SparseIterator;

import java.util.Arrays;
import java.util.Iterator;

/**
 * Represents the privileges for on a database
 *
 * @author Laurent Wouters
 */
public class DatabasePrivileges implements Serializable {
    /**
     * Initial length of the buffers
     */
    private static final int INIT_LENGTH = 8;

    /**
     * The users for the privileges
     */
    private User[] users;
    /**
     * The associated privileges
     */
    private int[] privileges;

    /**
     * Initializes this structure
     */
    public DatabasePrivileges() {
        this.users = new User[INIT_LENGTH];
        this.privileges = new int[INIT_LENGTH];
    }

    /**
     * Adds a privilege
     *
     * @param user      The user
     * @param privilege The privilege
     */
    public void add(User user, int privilege) {
        for (int i = 0; i != users.length; i++) {
            if (users[i] == null) {
                users[i] = user;
                privileges[i] = privilege;
                return;
            } else if (users[i] == user) {
                privileges[i] |= privilege;
                return;
            }
        }
        int index = users.length;
        users = Arrays.copyOf(users, users.length * 2);
        privileges = Arrays.copyOf(privileges, privileges.length * 2);
        users[index] = user;
        privileges[index] = privilege;
    }

    /**
     * Gets the privileges for a user
     *
     * @param user The user
     * @return The privileges
     */
    public int getFor(User user) {
        for (int i = 0; i != users.length; i++) {
            if (users[i] == user)
                return privileges[i];
            if (users[i] == null)
                return 0;
        }
        return 0;
    }

    /**
     * Gets an iterator over the users
     *
     * @return An iterator over the users
     */
    public Iterator<User> getUsers() {
        return new SparseIterator<>(users);
    }

    @Override
    public String serializedString() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i != users.length; i++) {
            if (users[i] == null)
                break;
            if (i != 0)
                builder.append(Files.LINE_SEPARATOR);
            boolean canAdmin = (privileges[i] & Schema.PRIVILEGE_ADMIN) == Schema.PRIVILEGE_ADMIN;
            boolean canWrite = (privileges[i] & Schema.PRIVILEGE_WRITE) == Schema.PRIVILEGE_WRITE;
            boolean canRead = (privileges[i] & Schema.PRIVILEGE_READ) == Schema.PRIVILEGE_READ;
            builder.append(users[i].getName());
            builder.append(":");
            if (canAdmin)
                builder.append(" ADMIN");
            if (canWrite)
                builder.append(" WRITE");
            if (canRead)
                builder.append(" READ");
        }
        return builder.toString();
    }

    @Override
    public String serializedJSON() {
        StringBuilder builder = new StringBuilder("{ \"results\": [");
        for (int i = 0; i != users.length; i++) {
            if (users[i] == null)
                break;
            if (i != 0)
                builder.append(", ");
            builder.append("{ \"user\": \"");
            builder.append(users[i].getName());
            builder.append("\", \"isAdmin\": ");
            builder.append((privileges[i] & Schema.PRIVILEGE_ADMIN) == Schema.PRIVILEGE_ADMIN);
            builder.append(", \"canWrite\": ");
            builder.append((privileges[i] & Schema.PRIVILEGE_WRITE) == Schema.PRIVILEGE_WRITE);
            builder.append(", \"canRead\": ");
            builder.append((privileges[i] & Schema.PRIVILEGE_READ) == Schema.PRIVILEGE_READ);
            builder.append("}");
        }
        builder.append("]}");
        return builder.toString();
    }
}
