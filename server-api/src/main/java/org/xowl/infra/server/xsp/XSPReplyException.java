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

package org.xowl.infra.server.xsp;

import org.xowl.infra.utils.TextUtils;

/**
 * Implements a reply to a xOWL server protocol request when the request failed due to internal exception
 *
 * @author Laurent Wouters
 */
public class XSPReplyException implements XSPReply {
    /**
     * The standard message for an exception
     */
    public static final String MESSAGE = "Internal server error, see log for more details.";

    /**
     * The thrown exception
     */
    private final Throwable throwable;

    /**
     * Initializes this reply
     *
     * @param throwable The thrown exception
     */
    public XSPReplyException(Throwable throwable) {
        this.throwable = throwable;
    }

    /**
     * Gets the thrown exception
     *
     * @return The thrown exception
     */
    public Throwable getThrowable() {
        return throwable;
    }

    @Override
    public boolean isSuccess() {
        return false;
    }

    @Override
    public String getMessage() {
        return throwable != null ? throwable.getMessage() : MESSAGE;
    }

    @Override
    public String serializedString() {
        return "ERROR: " + getMessage();
    }

    @Override
    public String serializedJSON() {
        return "{\"type\": \"" +
                TextUtils.escapeStringJSON(XSPReply.class.getCanonicalName()) +
                "\", \"kind\": \"" +
                TextUtils.escapeStringJSON(XSPReplyException.class.getSimpleName()) +
                "\", \"isSuccess\": false," +
                "\"message\": \"" + TextUtils.escapeStringJSON(getMessage()) + "\"}";
    }
}
