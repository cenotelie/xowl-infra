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

/**
 * Implements a reply to a xOWL server protocol request when the user is not authorized to perform the request
 *
 * @author Laurent Wouters
 */
public class ProtocolReplyUnauthorized implements ProtocolReply {
    /**
     * The singleton instance
     */
    private static ProtocolReplyUnauthorized INSTANCE = null;

    /**
     * Gets the singleton instance
     *
     * @return The singleton instance
     */
    public synchronized static ProtocolReplyUnauthorized instance() {
        if (INSTANCE == null)
            return new ProtocolReplyUnauthorized();
        return INSTANCE;
    }

    /**
     * Initializes this instance
     */
    private ProtocolReplyUnauthorized() {

    }

    @Override
    public boolean isSuccess() {
        return false;
    }

    @Override
    public String getMessage() {
        return "UNAUTHORIZED";
    }
}