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

package org.xowl.infra.lsp.client;

import org.xowl.infra.lsp.LspEndpoint;
import org.xowl.infra.lsp.LspEndpointBaseConnected;
import org.xowl.infra.lsp.LspHandler;

/**
 * Base interface for a LSP client
 *
 * @author Laurent Wouters
 */
public class LspClient extends LspEndpointBaseConnected {
    /**
     * Initializes this endpoint
     *
     * @param handler The handler for the requests coming to this endpoint
     */
    public LspClient(LspHandler handler) {
        super(handler, new LspClientResponseDeserializer());
    }

    /**
     * Initializes this endpoint
     *
     * @param handler The handler for the requests coming to this endpoint
     * @param remote  The remote endpoint to connect to
     */
    public LspClient(LspHandler handler, LspEndpoint remote) {
        super(handler, new LspClientResponseDeserializer(), remote);
    }
}
