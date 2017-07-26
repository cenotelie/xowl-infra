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

package org.xowl.infra.jsonrpc;

/**
 * Represents the context of a Json-Rpc request or batch of requests
 *
 * @author Laurent Wouters
 */
public interface JsonRpcContext {
    /**
     * Gets whether this context applies to a batch of requests
     *
     * @return Whether this context applies to a batch of requests
     */
    boolean isBatch();

    /**
     * Gets whether this context is empty
     * The context is empty when all addressed requests are notifications
     *
     * @return Whether this context is empty
     */
    boolean isEmpty();

    /**
     * Gets the method for the specified request
     *
     * @param requestId The identifier of a request
     * @return The method invoked by the request
     */
    String getMethodFor(String requestId);
}
