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

import org.xowl.infra.lsp.Lsp;
import org.xowl.infra.utils.logging.Logging;

import java.util.concurrent.CountDownLatch;

/**
 * Implements an object for running a LSP server of the current Java process
 *
 * @author Laurent Wouters
 */
public class LspServerRunner {
    /**
     * Exit code on a nominal shutdown
     */
    private static final int EXIT_CODE_NORMAL = 0;
    /**
     * Exit code when an improper shutdown
     */
    private static final int EXIT_CODE_ERROR = 1;

    /**
     * The LSP server to run
     */
    private final LspServer server;
    /**
     * The signalling object for the main thread
     */
    private final CountDownLatch signal;
    /**
     * Whether the server should stop
     */
    private volatile boolean shouldStop;
    /**
     * Whether the server has shutdown properly
     */
    private volatile boolean hasShutdown;

    /**
     * Initializes this runner
     *
     * @param server The LSP server to run
     */
    public LspServerRunner(LspServer server) {
        this.server = server;
        this.signal = new CountDownLatch(1);
        this.shouldStop = false;
        this.hasShutdown = false;
    }

    /**
     * Runs this program
     */
    public void run() {
        server.registerListener(new LspServerListener() {
            @Override
            public void onInitialize() {
                // do nothing
            }

            @Override
            public void onShutdown() {
                hasShutdown = true;
            }

            @Override
            public void onExit() {
                shouldStop = true;
                signal.countDown();
                onClose();
            }
        });
        // register hook for shutdown events
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                shouldStop = true;
                signal.countDown();
                onClose();
            }
        }, LspServerRunner.class.getCanonicalName() + ".shutdown"));
        Lsp.serveByStdStreams(server);

        while (!shouldStop) {
            try {
                signal.await();
            } catch (InterruptedException exception) {
                break;
            }
        }

        onClose();

        if (hasShutdown)
            System.exit(EXIT_CODE_NORMAL);
        else
            System.exit(EXIT_CODE_ERROR);
    }

    /**
     * When this application is closing
     */
    private synchronized void onClose() {
        try {
            server.close();
        } catch (Exception exception) {
            Logging.get().error(exception);
        }
    }
}
