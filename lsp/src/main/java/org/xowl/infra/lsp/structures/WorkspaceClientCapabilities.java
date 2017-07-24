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
import org.xowl.infra.utils.Serializable;
import org.xowl.infra.utils.TextUtils;

/**
 * WorkspaceClientCapabilities define capabilities the editor / tool provides on the workspace
 *
 * @author Laurent Wouters
 */
public class WorkspaceClientCapabilities implements Serializable {
    /**
     * The client supports applying batch edits to the workspace by supporting the request 'workspace/applyEdit'
     */
    private final boolean applyEdit;
    /**
     * Capabilities specific to `WorkspaceEdit`s
     * The client supports versioned document changes in `WorkspaceEdit`s
     */
    private final boolean workspaceEditDocumentChanges;
    /**
     * Capabilities specific to the `workspace/didChangeConfiguration` notification.
     * Did change configuration notification supports dynamic registration.
     */
    private final boolean didChangeConfigurationDynamicRegistration;
    /**
     * Capabilities specific to the `workspace/didChangeWatchedFiles` notification.
     * Did change watched files notification supports dynamic registration.
     */
    private final boolean didChangeWatchedFilesDynamicRegistration;
    /**
     * Capabilities specific to the `workspace/symbol` request.
     * Symbol request supports dynamic registration.
     */
    private final boolean symbolDynamicRegistration;
    /**
     * Capabilities specific to the `workspace/executeCommand` request.
     * Execute command supports dynamic registration.
     */
    private final boolean executeCommandDynamicRegistration;

    /**
     * Gets whether the client supports applying batch edits to the workspace by supporting the request 'workspace/applyEdit'
     *
     * @return The client supports applying batch edits to the workspace by supporting the request 'workspace/applyEdit'
     */
    public boolean supportsApplyEdit() {
        return applyEdit;
    }

    /**
     * Capabilities specific to `WorkspaceEdit`s
     * Gets whether the client supports versioned document changes in `WorkspaceEdit`s
     *
     * @return The client supports versioned document changes in `WorkspaceEdit`s
     */
    public boolean supportsWorkspaceEditDocumentChanges() {
        return workspaceEditDocumentChanges;
    }

    /**
     * Capabilities specific to the `workspace/didChangeConfiguration` notification.
     * Gets whether the client supports dynamic registration for the Did change configuration notification
     *
     * @return Did change configuration notification supports dynamic registration.
     */
    public boolean supportsDidChangeConfigurationDynamicRegistration() {
        return didChangeConfigurationDynamicRegistration;
    }

    /**
     * Capabilities specific to the `workspace/didChangeWatchedFiles` notification.
     * Gets whether the client supports dynamic registration for the Did change watched files notification
     *
     * @return Did change watched files notification supports dynamic registration.
     */
    public boolean supportsDidChangeWatchedFilesDynamicRegistration() {
        return didChangeWatchedFilesDynamicRegistration;
    }

    /**
     * Capabilities specific to the `workspace/symbol` request.
     * Gets whether the client supports dynamic registration for Symbol request
     *
     * @return Symbol request supports dynamic registration.
     */
    public boolean supportsSymbolDynamicRegistration() {
        return symbolDynamicRegistration;
    }

    /**
     * Capabilities specific to the `workspace/executeCommand` request.
     * Gets whether the client supports dynamic registration for the Execute command
     *
     * @return Execute command supports dynamic registration.
     */
    public boolean supportsExecuteCommandDynamicRegistration() {
        return executeCommandDynamicRegistration;
    }

    /**
     * Initializes this structure
     *
     * @param applyEdit                                 The client supports applying batch edits to the workspace by supporting the request 'workspace/applyEdit'
     * @param workspaceEditDocumentChanges              Capabilities specific to `WorkspaceEdit`s
     *                                                  The client supports versioned document changes in `WorkspaceEdit`s
     * @param didChangeConfigurationDynamicRegistration Capabilities specific to the `workspace/didChangeConfiguration` notification.
     *                                                  Did change configuration notification supports dynamic registration.
     * @param didChangeWatchedFilesDynamicRegistration  Capabilities specific to the `workspace/didChangeWatchedFiles` notification.
     *                                                  Did change watched files notification supports dynamic registration.
     * @param symbolDynamicRegistration                 Capabilities specific to the `workspace/symbol` request.
     *                                                  Symbol request supports dynamic registration.
     * @param executeCommandDynamicRegistration         Capabilities specific to the `workspace/executeCommand` request.
     *                                                  Execute command supports dynamic registration.
     */
    public WorkspaceClientCapabilities(
            boolean applyEdit,
            boolean workspaceEditDocumentChanges,
            boolean didChangeConfigurationDynamicRegistration,
            boolean didChangeWatchedFilesDynamicRegistration,
            boolean symbolDynamicRegistration,
            boolean executeCommandDynamicRegistration) {
        this.applyEdit = applyEdit;
        this.workspaceEditDocumentChanges = workspaceEditDocumentChanges;
        this.didChangeConfigurationDynamicRegistration = didChangeConfigurationDynamicRegistration;
        this.didChangeWatchedFilesDynamicRegistration = didChangeWatchedFilesDynamicRegistration;
        this.symbolDynamicRegistration = symbolDynamicRegistration;
        this.executeCommandDynamicRegistration = executeCommandDynamicRegistration;
    }

    /**
     * Initializes this structure
     *
     * @param definition The serialized definition
     */
    public WorkspaceClientCapabilities(ASTNode definition) {
        boolean applyEdit = false;
        boolean workspaceEditDocumentChanges = false;
        boolean didChangeConfigurationDynamicRegistration = false;
        boolean didChangeWatchedFilesDynamicRegistration = false;
        boolean symbolDynamicRegistration = false;
        boolean executeCommandDynamicRegistration = false;
        for (ASTNode child : definition.getChildren()) {
            ASTNode nodeMemberName = child.getChildren().get(0);
            String name = TextUtils.unescape(nodeMemberName.getValue());
            name = name.substring(1, name.length() - 1);
            ASTNode nodeValue = child.getChildren().get(1);
            switch (name) {
                case "applyEdit": {
                    applyEdit = Boolean.parseBoolean(nodeValue.getValue());
                    break;
                }
                case "workspaceEdit": {
                    for (ASTNode sub : definition.getChildren()) {
                        nodeMemberName = sub.getChildren().get(0);
                        name = TextUtils.unescape(nodeMemberName.getValue());
                        name = name.substring(1, name.length() - 1);
                        nodeValue = sub.getChildren().get(1);
                        switch (name) {
                            case "documentChanges": {
                                workspaceEditDocumentChanges = Boolean.parseBoolean(nodeValue.getValue());
                                break;
                            }
                        }
                    }
                    break;
                }
                case "didChangeConfiguration": {
                    for (ASTNode sub : definition.getChildren()) {
                        nodeMemberName = sub.getChildren().get(0);
                        name = TextUtils.unescape(nodeMemberName.getValue());
                        name = name.substring(1, name.length() - 1);
                        nodeValue = sub.getChildren().get(1);
                        switch (name) {
                            case "dynamicRegistration": {
                                didChangeConfigurationDynamicRegistration = Boolean.parseBoolean(nodeValue.getValue());
                                break;
                            }
                        }
                    }
                    break;
                }
                case "didChangeWatchedFiles": {
                    for (ASTNode sub : definition.getChildren()) {
                        nodeMemberName = sub.getChildren().get(0);
                        name = TextUtils.unescape(nodeMemberName.getValue());
                        name = name.substring(1, name.length() - 1);
                        nodeValue = sub.getChildren().get(1);
                        switch (name) {
                            case "dynamicRegistration": {
                                didChangeWatchedFilesDynamicRegistration = Boolean.parseBoolean(nodeValue.getValue());
                                break;
                            }
                        }
                    }
                    break;
                }
                case "symbol": {
                    for (ASTNode sub : definition.getChildren()) {
                        nodeMemberName = sub.getChildren().get(0);
                        name = TextUtils.unescape(nodeMemberName.getValue());
                        name = name.substring(1, name.length() - 1);
                        nodeValue = sub.getChildren().get(1);
                        switch (name) {
                            case "dynamicRegistration": {
                                symbolDynamicRegistration = Boolean.parseBoolean(nodeValue.getValue());
                                break;
                            }
                        }
                    }
                    break;
                }
                case "executeCommand": {
                    for (ASTNode sub : definition.getChildren()) {
                        nodeMemberName = sub.getChildren().get(0);
                        name = TextUtils.unescape(nodeMemberName.getValue());
                        name = name.substring(1, name.length() - 1);
                        nodeValue = sub.getChildren().get(1);
                        switch (name) {
                            case "dynamicRegistration": {
                                executeCommandDynamicRegistration = Boolean.parseBoolean(nodeValue.getValue());
                                break;
                            }
                        }
                    }
                    break;
                }
            }
        }
        this.applyEdit = applyEdit;
        this.workspaceEditDocumentChanges = workspaceEditDocumentChanges;
        this.didChangeConfigurationDynamicRegistration = didChangeConfigurationDynamicRegistration;
        this.didChangeWatchedFilesDynamicRegistration = didChangeWatchedFilesDynamicRegistration;
        this.symbolDynamicRegistration = symbolDynamicRegistration;
        this.executeCommandDynamicRegistration = executeCommandDynamicRegistration;
    }

    @Override
    public String serializedString() {
        return serializedJSON();
    }

    @Override
    public String serializedJSON() {
        return "{\"applyEdit\": " +
                Boolean.toString(applyEdit) +
                ", \"workspaceEdit\": {\"documentChanges\": " +
                Boolean.toString(workspaceEditDocumentChanges) +
                "}, \"didChangeConfiguration\": {\"dynamicRegistration\": " +
                Boolean.toString(didChangeConfigurationDynamicRegistration) +
                "}, \"didChangeWatchedFiles\": {\"dynamicRegistration\": " +
                Boolean.toString(didChangeWatchedFilesDynamicRegistration) +
                "}, \"symbol\": {\"dynamicRegistration\": " +
                Boolean.toString(symbolDynamicRegistration) +
                "}, \"executeCommand\": {\"dynamicRegistration\": " +
                Boolean.toString(executeCommandDynamicRegistration) +
                "}}";
    }
}
