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

package org.xowl.infra.lsp.xowlserver;

import org.xowl.infra.lsp.runners.LspRunner;
import org.xowl.infra.lsp.runners.LspRunnerNetwork;
import org.xowl.infra.lsp.server.LspServer;
import org.xowl.infra.lsp.structures.TextDocumentSyncKind;

/**
 * The main program for this language server
 *
 * @author Laurent Wouters
 */
public class Program {
    /**
     * The default port for the server
     */
    private static final int PORT_DEFAULT = 8000;

    /**
     * The main entry point
     *
     * @param args The arguments
     */
    public static void main(String[] args) {
        int port = PORT_DEFAULT;
        if (args != null && args.length >= 1) {
            try {
                int value = Integer.parseInt(args[0]);
                if (value > 0)
                    port = value;
            } catch (NumberFormatException exception) {
                // do nothing, ignore the argument
            }
        }

        LspServer server = new LspServer(new MyServerHandler());
        server.getServerCapabilities().addCapability("textDocumentSync.openClose");
        server.getServerCapabilities().addCapability("textDocumentSync.willSave");
        server.getServerCapabilities().addCapability("textDocumentSync.willSaveWaitUntil");
        server.getServerCapabilities().addCapability("textDocumentSync.save.includeText");
        server.getServerCapabilities().addOption("textDocumentSync.change", TextDocumentSyncKind.INCREMENTAL);
        server.getServerCapabilities().addCapability("referencesProvider");
        server.getServerCapabilities().addCapability("documentSymbolProvider");
        server.getServerCapabilities().addCapability("workspaceSymbolProvider");
        server.getServerCapabilities().addCapability("definitionProvider");
        LspRunner runner = new LspRunnerNetwork(server, port);
        runner.run();
    }
}
