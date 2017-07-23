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

import fr.cenotelie.hime.redist.ASTNode;
import org.xowl.infra.server.api.XOWLUser;
import org.xowl.infra.utils.TextUtils;
import org.xowl.infra.utils.api.Reply;
import org.xowl.infra.utils.api.ReplyUnsupported;

/**
 * Base implementation of a user
 *
 * @author Laurent Wouters
 */
public class BaseUser implements XOWLUser {
    /**
     * The user's name
     */
    protected final String identifier;

    /**
     * Initializes this user
     *
     * @param identifier The user's identifier
     */
    public BaseUser(String identifier) {
        this.identifier = identifier;
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
        this.identifier = value;
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public String getName() {
        return identifier;
    }

    @Override
    public Reply updatePassword(String password) {
        return ReplyUnsupported.instance();
    }

    @Override
    public Reply getPrivileges() {
        return ReplyUnsupported.instance();
    }

    @Override
    public String serializedString() {
        return getIdentifier();
    }

    @Override
    public String serializedJSON() {
        return "{\"type\": \"" +
                TextUtils.escapeStringJSON(XOWLUser.class.getCanonicalName()) +
                "\", \"identifier\": \"" +
                TextUtils.escapeStringJSON(getIdentifier()) +
                "\", \"name\": \"" +
                TextUtils.escapeStringJSON(getName()) +
                "\"}";
    }

    @Override
    public int hashCode() {
        return identifier.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof XOWLUser && identifier.equals(((XOWLUser) obj).getIdentifier());
    }
}
