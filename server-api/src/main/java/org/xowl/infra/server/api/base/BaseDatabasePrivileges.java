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

package org.xowl.infra.server.api.base;

import org.xowl.hime.redist.ASTNode;
import org.xowl.infra.server.api.XOWLDatabasePrivileges;
import org.xowl.infra.server.api.XOWLPrivilege;
import org.xowl.infra.server.api.XOWLUser;
import org.xowl.infra.store.IOUtils;
import org.xowl.infra.utils.Files;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * Base implementation of database privileges
 *
 * @author Laurent Wouters
 */
public class BaseDatabasePrivileges implements XOWLDatabasePrivileges {
    /**
     * The users
     */
    private XOWLUser[] users;
    /**
     * The associated privileges
     */
    private int[] privileges;

    /**
     * Initializes this structure
     */
    public BaseDatabasePrivileges() {
        this.users = new XOWLUser[4];
        this.privileges = new int[4];
    }

    /**
     * Initializes this user
     *
     * @param definition The definition
     */
    public BaseDatabasePrivileges(ASTNode definition) {
        this.users = new XOWLUser[4];
        this.privileges = new int[4];
        for (ASTNode member : definition.getChildren()) {
            String name = IOUtils.unescape(member.getChildren().get(0).getValue());
            name = name.substring(1, name.length() - 1);
            if (name.equals("accesses")) {
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
            String userName = null;
            boolean canAdmin = false;
            boolean canWrite = false;
            boolean canRead = false;
            for (ASTNode member : access.getChildren()) {
                String name = IOUtils.unescape(member.getChildren().get(0).getValue());
                name = name.substring(1, name.length() - 1);
                switch (name) {
                    case "user":
                        userName = IOUtils.unescape(member.getChildren().get(1).getValue());
                        userName = userName.substring(1, userName.length() - 1);
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
            if (userName != null) {
                int privilege = (canAdmin ? XOWLPrivilege.ADMIN : 0) | (canWrite ? XOWLPrivilege.WRITE : 0) | (canRead ? XOWLPrivilege.READ : 0);
                add(new BaseUser(userName), privilege);
            }
        }
    }

    /**
     * Adds a privilege
     *
     * @param user      The user
     * @param privilege The privilege
     */
    public void add(XOWLUser user, int privilege) {
        for (int i = 0; i != users.length; i++) {
            if (users[i] == null) {
                users[i] = user;
                privileges[i] = privilege;
                return;
            } else if (users[i].getName().equals(user.getName())) {
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

    @Override
    public Collection<XOWLUser> getUsers() {
        Collection<XOWLUser> result = new ArrayList<>(users.length);
        for (int i = 0; i != users.length; i++) {
            if (users[i] == null)
                break;
            result.add(users[i]);
        }
        return result;
    }

    @Override
    public int getPrivilegeFor(XOWLUser user) {
        for (int i = 0; i != users.length; i++) {
            if (users[i] == null)
                return XOWLPrivilege.NONE;
            else if (users[i].getName().equals(user.getName()))
                return privileges[i];
        }
        return XOWLPrivilege.NONE;
    }

    @Override
    public String serializedString() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i != users.length; i++) {
            if (users[i] == null)
                break;
            if (i != 0)
                builder.append(Files.LINE_SEPARATOR);
            boolean canAdmin = (privileges[i] & XOWLPrivilege.ADMIN) == XOWLPrivilege.ADMIN;
            boolean canWrite = (privileges[i] & XOWLPrivilege.WRITE) == XOWLPrivilege.WRITE;
            boolean canRead = (privileges[i] & XOWLPrivilege.READ) == XOWLPrivilege.READ;
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
        StringBuilder builder = new StringBuilder("{\"type\": \"");
        builder.append(IOUtils.escapeStringJSON(XOWLDatabasePrivileges.class.getCanonicalName()));
        builder.append("\", \"accesses\": [");
        for (int i = 0; i != users.length; i++) {
            if (users[i] == null)
                break;
            if (i != 0)
                builder.append(", ");
            boolean canAdmin = (privileges[i] & XOWLPrivilege.ADMIN) == XOWLPrivilege.ADMIN;
            boolean canWrite = (privileges[i] & XOWLPrivilege.WRITE) == XOWLPrivilege.WRITE;
            boolean canRead = (privileges[i] & XOWLPrivilege.READ) == XOWLPrivilege.READ;
            builder.append("{ \"user\": \"");
            builder.append(users[i].getName());
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
