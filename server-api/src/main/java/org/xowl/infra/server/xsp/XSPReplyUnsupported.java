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

/**
 * Implements a reply to a xOWL server protocol request when the requested operation is not supported
 *
 * @author Laurent Wouters
 */
public class XSPReplyUnsupported implements XSPReply {
    /**
     * The singleton instance
     */
    private static XSPReplyUnsupported INSTANCE = null;

    /**
     * Gets the singleton instance
     *
     * @return The singleton instance
     */
    public synchronized static XSPReplyUnsupported instance() {
        if (INSTANCE == null)
            return new XSPReplyUnsupported();
        return INSTANCE;
    }

    /**
     * Initializes this reply
     */
    private XSPReplyUnsupported() {
    }

    @Override
    public boolean isSuccess() {
        return false;
    }

    @Override
    public String getMessage() {
        return "UNSUPPORTED";
    }

    @Override
    public String serializedString() {
        return "UNSUPPORTED";
    }

    @Override
    public String serializedJSON() {
        return "{ \"isSuccess\": false, \"message\": \"UNSUPPORTED\", \"cause\": \"UNSUPPORTED\" }";
    }
}
