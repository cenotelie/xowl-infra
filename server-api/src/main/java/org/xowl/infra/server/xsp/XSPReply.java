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

import org.xowl.infra.store.Serializable;

/**
 * Represents a reply to a request in the xOWL server protocol
 *
 * @author Laurent Wouters
 */
public interface XSPReply extends Serializable {
    /**
     * The MIME content type for an XSP command
     */
    String MIME_XSP_COMMAND = "application/x-xowl-xsp";

    /**
     * Gets whether the reply is a success
     *
     * @return Whether the reply is a success
     */
    boolean isSuccess();

    /**
     * Gets the message, if any
     *
     * @return The message, if any
     */
    String getMessage();
}
