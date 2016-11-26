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

import org.xowl.hime.redist.ASTNode;
import org.xowl.infra.server.api.XOWLUser;
import org.xowl.infra.server.xsp.XSPReply;
import org.xowl.infra.server.xsp.XSPReplyUnsupported;
import org.xowl.infra.utils.TextUtils;

/**
 * Base implementation of a user
 *
 * @author Laurent Wouters
 */
public class BaseUser implements XOWLUser {
    /**
     * The user's name
     */
    protected final String name;

    /**
     * Initializes this user
     *
     * @param name The user's name
     */
    public BaseUser(String name) {
        this.name = name;
    }

    /**
     * Initializes this user
     *
     * @param root The user's definition
     */
    public BaseUser(ASTNode root) {
        String value = null;
        for (ASTNode child : root.getChildren()) {
            ASTNode nodeMemberName = child.getChildren().get(0);
            String name = TextUtils.unescape(nodeMemberName.getValue());
            name = name.substring(1, name.length() - 1);
            if (name.equals("name")) {
                ASTNode nodeValue = child.getChildren().get(1);
                value = TextUtils.unescape(nodeValue.getValue());
                value = value.substring(1, value.length() - 1);
            }
        }
        this.name = value;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public XSPReply updatePassword(String password) {
        return XSPReplyUnsupported.instance();
    }

    @Override
    public XSPReply getPrivileges() {
        return XSPReplyUnsupported.instance();
    }

    @Override
    public String serializedString() {
        return getName();
    }

    @Override
    public String serializedJSON() {
        return "{\"type\": \"" + TextUtils.escapeStringJSON(XOWLUser.class.getCanonicalName()) + "\", \"name\": \"" + TextUtils.escapeStringJSON(getName()) + "\"}";
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof XOWLUser && name.equals(((XOWLUser) obj).getName());
    }
}
