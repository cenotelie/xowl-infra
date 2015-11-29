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
 * Represents the privileges for a user
 *
 * @author Laurent Wouters
 */
public class UserPrivileges implements Serializable {
    /**
     * Initial length of the buffers
     */
    private static final int INIT_LENGTH = 8;

    /**
     * The databases for the privileges
     */
    private Database[] databases;
    /**
     * The associated privileges
     */
    private int[] privileges;
    /**
     * Whether the user is a server admin
     */
    private boolean isServerAdmin;

    /**
     * Initializes this structure
     *
     * @param isServerAdmin Whether the user is a server admin
     */
    public UserPrivileges(boolean isServerAdmin) {
        this.databases = new Database[INIT_LENGTH];
        this.privileges = new int[INIT_LENGTH];
        this.isServerAdmin = isServerAdmin;
    }

    /**
     * Adds a privilege
     *
     * @param database  The database
     * @param privilege The privilege
     */
    public void add(Database database, int privilege) {
        for (int i = 0; i != databases.length; i++) {
            if (databases[i] == null) {
                databases[i] = database;
                privileges[i] = privilege;
                return;
            } else if (databases[i] == database) {
                privileges[i] |= privilege;
                return;
            }
        }
        int index = databases.length;
        databases = Arrays.copyOf(databases, databases.length * 2);
        privileges = Arrays.copyOf(privileges, privileges.length * 2);
        databases[index] = database;
        privileges[index] = privilege;
    }

    /**
     * Gets the privileges on a database
     *
     * @param database The database
     * @return The privileges
     */
    public int getFor(Database database) {
        for (int i = 0; i != databases.length; i++) {
            if (databases[i] == database)
                return privileges[i];
            if (databases[i] == null)
                return 0;
        }
        return 0;
    }

    /**
     * Gets an iterator over the databases
     *
     * @return An iterator over the databases
     */
    public Iterator<Database> getDatabases() {
        return new SparseIterator<>(databases);
    }

    @Override
    public String serializedString() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i != databases.length; i++) {
            if (databases[i] == null)
                break;
            if (i != 0)
                builder.append(Files.LINE_SEPARATOR);
            boolean canAdmin = (privileges[i] & Schema.PRIVILEGE_ADMIN) == Schema.PRIVILEGE_ADMIN;
            boolean canWrite = (privileges[i] & Schema.PRIVILEGE_WRITE) == Schema.PRIVILEGE_WRITE;
            boolean canRead = (privileges[i] & Schema.PRIVILEGE_READ) == Schema.PRIVILEGE_READ;
            builder.append(databases[i].getName());
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
        StringBuilder builder = new StringBuilder("{ \"isServerAdmin\": ");
        builder.append(isServerAdmin);
        builder.append(", \"accesses\": [");
        for (int i = 0; i != databases.length; i++) {
            if (databases[i] == null)
                break;
            if (i != 0)
                builder.append(", ");
            builder.append("{ \"database\": \"");
            builder.append(databases[i].getName());
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
