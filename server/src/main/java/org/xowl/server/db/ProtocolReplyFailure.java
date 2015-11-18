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
 * Implements a reply to a xOWL server protocol request when the request failed
 *
 * @author Laurent Wouters
 */
public class ProtocolReplyFailure implements ProtocolReply {
    /**
     * The singleton instance
     */
    private static ProtocolReplyFailure INSTANCE = null;

    /**
     * Gets the default instance
     *
     * @return The default instance
     */
    public synchronized static ProtocolReplyFailure instance() {
        if (INSTANCE == null)
            return new ProtocolReplyFailure("FAILED");
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
    public ProtocolReplyFailure(String message) {
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
}
