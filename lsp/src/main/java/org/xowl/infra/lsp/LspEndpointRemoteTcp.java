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

package org.xowl.infra.lsp;

import org.xowl.infra.utils.api.Reply;
import org.xowl.infra.utils.api.ReplyUnsupported;
import org.xowl.infra.utils.json.JsonDeserializer;

/**
 * Represents a remote LSP endpoint, connected through a TCP connection
 *
 * @author Laurent Wouters
 */
public class LspEndpointRemoteTcp extends LspEndpointBase {
    /**
     * Initializes this endpoint
     *
     * @param listener     The listener for requests coming from the remote endpoint
     * @param deserializer The de-serializer to use
     */
    public LspEndpointRemoteTcp(LspEndpointListener listener, JsonDeserializer deserializer) {
        super(listener, deserializer);
    }

    @Override
    public Reply send(String message) {
        return ReplyUnsupported.instance();
    }
}
