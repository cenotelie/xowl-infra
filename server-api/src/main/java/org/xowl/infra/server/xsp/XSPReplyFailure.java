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

package org.xowl.infra.server.xsp;

import org.xowl.infra.store.IOUtils;

/**
 * Implements a reply to a xOWL server protocol request when the request failed
 *
 * @author Laurent Wouters
 */
public class XSPReplyFailure implements XSPReply {
    /**
     * The singleton instance
     */
    private static XSPReplyFailure INSTANCE = null;

    /**
     * Gets the default instance
     *
     * @return The default instance
     */
    public synchronized static XSPReplyFailure instance() {
        if (INSTANCE == null)
            return new XSPReplyFailure("FAILED");
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
    public XSPReplyFailure(String message) {
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
        return "{ \"isSuccess\": false, \"message\": \"" + (message == null ? "" : IOUtils.escapeStringJSON(message)) + "\" }";
    }
}