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

package org.xowl.server.xsp;

import org.xowl.infra.store.IOUtils;

/**
 * Implements a simple successful reply to a xOWL server request
 *
 * @author Laurent Wouters
 */
public class XSPReplySuccess implements XSPReply {
    /**
     * The singleton instance
     */
    private static XSPReplySuccess INSTANCE = null;

    /**
     * Gets the default instance
     *
     * @return The default instance
     */
    public synchronized static XSPReplySuccess instance() {
        if (INSTANCE == null)
            return new XSPReplySuccess("OK");
        return INSTANCE;
    }

    /**
     * The associated message
     */
    private final String message;

    /**
     * Initializes this success
     *
     * @param message The associated message
     */
    public XSPReplySuccess(String message) {
        this.message = message;
    }

    @Override
    public boolean isSuccess() {
        return true;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public String serializedString() {
        return message == null ? "OK" : ("OK: " + message);
    }

    @Override
    public String serializedJSON() {
        return "{ \"isSuccess\": true, \"message\": \"" + (message == null ? "" : IOUtils.escapeStringJSON(message)) + "\" }";
    }
}
