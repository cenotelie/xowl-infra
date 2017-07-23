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

package org.xowl.infra.utils.api;

import org.xowl.infra.utils.TextUtils;

/**
 * Implements a reply to a request when a network error occurred (for any reason)
 *
 * @author Laurent Wouters
 */
public class ReplyNetworkError implements Reply {
    /**
     * The singleton instance
     */
    private static ReplyNetworkError INSTANCE = null;

    /**
     * Gets the singleton instance
     *
     * @return The singleton instance
     */
    public synchronized static ReplyNetworkError instance() {
        if (INSTANCE == null)
            INSTANCE = new ReplyNetworkError("Connection failure");
        return INSTANCE;
    }

    /**
     * The message associated to the failure
     */
    private final String message;

    /**
     * Initializes this reply
     *
     * @param message The message associated to the failure
     */
    public ReplyNetworkError(String message) {
        this.message = message;
    }

    @Override
    public boolean isSuccess() {
        return false;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public String serializedString() {
        return "ERROR: " + message;
    }

    @Override
    public String serializedJSON() {
        return "{\"type\": \"" +
                TextUtils.escapeStringJSON(Reply.class.getCanonicalName()) +
                "\", \"kind\": \"" +
                TextUtils.escapeStringJSON(ReplyNetworkError.class.getSimpleName()) +
                "\", \"isSuccess\": false," +
                "\"message\": \"" + TextUtils.escapeStringJSON(message != null ? message : "Connection failure") + "\"}";
    }
}
