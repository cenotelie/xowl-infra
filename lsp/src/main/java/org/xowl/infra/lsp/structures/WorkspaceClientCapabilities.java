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

package org.xowl.infra.lsp.structures;

import fr.cenotelie.hime.redist.ASTNode;
import org.xowl.infra.utils.json.JsonDeserializer;

/**
 * WorkspaceClientCapabilities define capabilities the editor / tool provides on the workspace
 *
 * @author Laurent Wouters
 */
public class WorkspaceClientCapabilities extends Capabilities {
    /**
     * The client supports applying batch edits to the workspace by supporting the request 'workspace/applyEdit'
     */
    public static final String applyEdit = "applyEdit";
    /**
     * Capabilities specific to `WorkspaceEdit`s
     * The client supports versioned document changes in `WorkspaceEdit`s
     */
    public static final String workspaceEdit_documentChanges = "workspaceEdit.documentChanges";
    /**
     * Capabilities specific to the `workspace/didChangeConfiguration` notification.
     * Did change configuration notification supports dynamic registration.
     */
    public static final String didChangeConfiguration_dynamicRegistration = "didChangeConfiguration.dynamicRegistration";
    /**
     * Capabilities specific to the `workspace/didChangeWatchedFiles` notification.
     * Did change watched files notification supports dynamic registration.
     */
    public static final String didChangeWatchedFiles_dynamicRegistration = "didChangeWatchedFiles.dynamicRegistration";
    /**
     * Capabilities specific to the `workspace/symbol` request.
     * Symbol request supports dynamic registration.
     */
    public static final String symbol_dynamicRegistration = "symbol.dynamicRegistration";
    /**
     * Capabilities specific to the `workspace/executeCommand` request.
     * Execute command supports dynamic registration.
     */
    public static final String executeCommand_dynamicRegistration = "executeCommand.dynamicRegistration";

    /**
     * Initializes this structure
     */
    public WorkspaceClientCapabilities() {
        super();
    }

    /**
     * Initializes this structure
     *
     * @param definition   The serialized definition
     * @param deserializer The deserializer to use
     */
    public WorkspaceClientCapabilities(ASTNode definition, JsonDeserializer deserializer) {
        super(definition, deserializer);
    }
}
