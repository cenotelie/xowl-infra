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

package org.xowl.infra.server.api.base;

import org.xowl.hime.redist.ASTNode;
import org.xowl.infra.server.api.XOWLDatabase;
import org.xowl.infra.server.api.XOWLPrivilege;
import org.xowl.infra.server.api.XOWLUserPrivileges;
import org.xowl.infra.store.IOUtils;
import org.xowl.infra.utils.Files;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * Base implementations of user privileges
 *
 * @author Laurent Wouters
 */
public class BaseUserPrivileges implements XOWLUserPrivileges {
    /**
     * The databases
     */
    private XOWLDatabase[] databases;
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
    public BaseUserPrivileges(boolean isServerAdmin) {
        this.databases = new XOWLDatabase[4];
        this.privileges = new int[4];
        this.isServerAdmin = isServerAdmin;
    }

    /**
     * Initializes this user
     *
     * @param definition The definition
     */
    public BaseUserPrivileges(ASTNode definition) {
        this.databases = new XOWLDatabase[4];
        this.privileges = new int[4];
        for (ASTNode member : definition.getChildren()) {
            String name = IOUtils.unescape(member.getChildren().get(0).getValue());
            name = name.substring(1, name.length() - 1);
            if (name.equals("isServerAdmin")) {
                String value = IOUtils.unescape(member.getChildren().get(1).getValue());
                isServerAdmin = value.equalsIgnoreCase("true");
            } else if (name.equals("accesses")) {
                loadAccesses(member.getChildren().get(1));
            }
        }
    }

    /**
     * Loads the accesses from the AST node
     *
     * @param definition The definition node
     */
    private void loadAccesses(ASTNode definition) {
        for (ASTNode access : definition.getChildren()) {
            String dbName = null;
            boolean canAdmin = false;
            boolean canWrite = false;
            boolean canRead = false;
            for (ASTNode member : access.getChildren()) {
                String name = IOUtils.unescape(member.getChildren().get(0).getValue());
                name = name.substring(1, name.length() - 1);
                switch (name) {
                    case "database":
                        dbName = IOUtils.unescape(member.getChildren().get(1).getValue());
                        dbName = dbName.substring(1, dbName.length() - 1);
                        break;
                    case "isAdmin": {
                        String value = IOUtils.unescape(member.getChildren().get(1).getValue());
                        canAdmin = value.equalsIgnoreCase("true");
                        break;
                    }
                    case "canWrite": {
                        String value = IOUtils.unescape(member.getChildren().get(1).getValue());
                        canWrite = value.equalsIgnoreCase("true");
                        break;
                    }
                    case "canRead": {
                        String value = IOUtils.unescape(member.getChildren().get(1).getValue());
                        canRead = value.equalsIgnoreCase("true");
                        break;
                    }
                }
            }
            if (dbName != null) {
                int privilege = (canAdmin ? XOWLPrivilege.ADMIN : 0) | (canWrite ? XOWLPrivilege.WRITE : 0) | (canRead ? XOWLPrivilege.READ : 0);
                add(new BaseDatabase(dbName), privilege);
            }
        }
    }

    /**
     * Adds a privilege
     *
     * @param database  The database
     * @param privilege The privilege
     */
    public void add(XOWLDatabase database, int privilege) {
        for (int i = 0; i != databases.length; i++) {
            if (databases[i] == null) {
                databases[i] = database;
                privileges[i] = privilege;
                return;
            } else if (databases[i].getName().equals(database.getName())) {
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

    @Override
    public Collection<XOWLDatabase> getDatabases() {
        Collection<XOWLDatabase> result = new ArrayList<>(databases.length);
        for (int i = 0; i != databases.length; i++) {
            if (databases[i] == null)
                break;
            result.add(databases[i]);
        }
        return result;
    }

    @Override
    public int getPrivilegeFor(XOWLDatabase database) {
        for (int i = 0; i != databases.length; i++) {
            if (databases[i] == null)
                return XOWLPrivilege.NONE;
            else if (databases[i].getName().equals(database.getName()))
                return privileges[i];
        }
        return XOWLPrivilege.NONE;
    }

    @Override
    public boolean isServerAdmin() {
        return isServerAdmin;
    }

    @Override
    public String serializedString() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i != databases.length; i++) {
            if (databases[i] == null)
                break;
            if (i != 0)
                builder.append(Files.LINE_SEPARATOR);
            boolean canAdmin = (privileges[i] & XOWLPrivilege.ADMIN) == XOWLPrivilege.ADMIN;
            boolean canWrite = (privileges[i] & XOWLPrivilege.WRITE) == XOWLPrivilege.WRITE;
            boolean canRead = (privileges[i] & XOWLPrivilege.READ) == XOWLPrivilege.READ;
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
        StringBuilder builder = new StringBuilder("{\"type\": \"");
        builder.append(IOUtils.escapeStringJSON(XOWLUserPrivileges.class.getCanonicalName()));
        builder.append("\", \"isServerAdmin\": ");
        builder.append(isServerAdmin);
        builder.append(", \"accesses\": [");
        for (int i = 0; i != databases.length; i++) {
            if (databases[i] == null)
                break;
            if (i != 0)
                builder.append(", ");
            boolean canAdmin = (privileges[i] & XOWLPrivilege.ADMIN) == XOWLPrivilege.ADMIN;
            boolean canWrite = (privileges[i] & XOWLPrivilege.WRITE) == XOWLPrivilege.WRITE;
            boolean canRead = (privileges[i] & XOWLPrivilege.READ) == XOWLPrivilege.READ;
            builder.append("{ \"database\": \"");
            builder.append(databases[i].getName());
            builder.append("\", \"isAdmin\": ");
            builder.append(Boolean.toString(canAdmin));
            builder.append(", \"canWrite\": ");
            builder.append(Boolean.toString(canWrite));
            builder.append(", \"canRead\": ");
            builder.append(Boolean.toString(canRead));
            builder.append("}");
        }
        builder.append("]}");
        return builder.toString();
    }
}
