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

import org.xowl.infra.jsonrpc.JsonRpcRequest;
import org.xowl.infra.jsonrpc.JsonRpcResponse;
import org.xowl.infra.jsonrpc.JsonRpcResponseError;
import org.xowl.infra.utils.concurrent.SafeRunnable;
import org.xowl.infra.utils.logging.Logging;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Implements a LSP server that is backed by a thread for processing its requests
 *
 * @author Laurent Wouters
 */
public class LspServerAsync extends LspServer implements AutoCloseable {
    /**
     * The counter of async servers
     */
    private static AtomicInteger COUNTER = new AtomicInteger(1);
    /**
     * The polling timeout in ms
     */
    private static final int POLL_TIMEOUT = 500;

    /**
     * The inner LSP server to be made concurrent
     */
    private final LspServer inner;
    /**
     * The queue of requests
     */
    private final ConcurrentLinkedQueue<Object> requests;
    /**
     * Whether the thread must exit
     */
    private final AtomicBoolean mustExit;
    /**
     * The thread for the server
     */
    private final Thread thread;

    /**
     * Initializes this server
     *
     * @param inner The inner server
     */
    public LspServerAsync(LspServer inner) {
        this.inner = inner;
        this.requests = new ConcurrentLinkedQueue<>();
        this.mustExit = new AtomicBoolean(false);
        this.thread = new Thread(new SafeRunnable() {
            @Override
            public void doRun() {
                threadMain();
            }
        }, LspServerAsync.class.getCanonicalName() + "." + COUNTER.getAndIncrement());
        this.thread.start();
    }

    /**
     * Main function for the thread
     */
    private void threadMain() {
        while (!mustExit.get()) {
            Object object = requests.poll();
            if (object == null) {
                try {
                    Thread.sleep(POLL_TIMEOUT);
                    continue;
                } catch (InterruptedException exception) {
                    return;
                }
            }

            if (object instanceof JsonRpcRequest) {
                JsonRpcRequest request = (JsonRpcRequest) object;
                JsonRpcResponse response = inner.handle(request);
                if (response == null)
                    continue;
                onResponse(request, response);
            } else {
                List<JsonRpcRequest> requests = (List<JsonRpcRequest>) object;
                List<JsonRpcResponse> responses = new ArrayList<>(requests.size());
                for (JsonRpcRequest request : requests) {
                    if (request == null)
                        responses.add(JsonRpcResponseError.newInvalidRequest(null));
                    else {
                        JsonRpcResponse response = inner.handle(request);
                        if (response != null)
                            responses.add(response);
                    }
                }
                onResponse(requests, responses);
            }
        }
    }

    @Override
    public void close() throws Exception {
        if (thread.isAlive() && !mustExit.get()) {
            mustExit.set(true);
            try {
                thread.join(POLL_TIMEOUT * 2);
            } catch (InterruptedException exception) {
                Logging.get().error(exception);
            }
        }
    }

    @Override
    public JsonRpcResponse handle(JsonRpcRequest request) {
        requests.add(request);
        return null;
    }

    @Override
    public List<JsonRpcResponse> handle(List<JsonRpcRequest> requests) {
        this.requests.add(requests);
        return Collections.emptyList();
    }

    /**
     * When a response becomes available for a request
     *
     * @param request  The original request
     * @param response The corresponding response
     */
    protected void onResponse(JsonRpcRequest request, JsonRpcResponse response) {
        // do nothing
    }

    /**
     * When a response becomes available for a request
     *
     * @param requests  The batch of original requests
     * @param responses The corresponding responses
     */
    protected void onResponse(List<JsonRpcRequest> requests, List<JsonRpcResponse> responses) {
        // do nothing
    }
}
