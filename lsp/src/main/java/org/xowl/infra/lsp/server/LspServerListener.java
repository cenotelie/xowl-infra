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

package org.xowl.infra.lsp.server;

/**
 * A listener for server events
 *
 * @author Laurent Wouters
 */
public interface LspServerListener {
    /**
     * When the server has received and treated a request for initialization
     */
    void onInitialize();

    /**
     * When the server has received and treated a request for shutdown
     */
    void onShutdown();

    /**
     * When the server has received and treated a request for exit
     */
    void onExit();
}
