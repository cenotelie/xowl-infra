/*******************************************************************************
 * Copyright (c) 2015 Laurent Wouters
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
 *
 * Contributors:
 *     Laurent Wouters - lwouters@xowl.org
 ******************************************************************************/

package org.xowl.store.storage.persistent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The base implementation of an IO backend
 *
 * @author Laurent Wouters
 */
class IOBackend implements AutoCloseable {
    /**
     * The backend is ready
     */
    public static final int STATE_READY = 0;
    /**
     * The backend is closing, pending transactions are being terminated and new one will be refused
     */
    public static final int STATE_CLOSING = 1;
    /**
     * The backend is closed, no transaction can be performed
     */
    public static final int STATE_CLOSED = 2;

    /**
     * The maximum number of ms to wait for finalizing a transaction
     */
    private static final int WAIT_TIMEOUT = 1000;
    /**
     * The maximum number of transactions
     */
    private static final int MAX_TRANSACTION_COUNT = 16;

    /**
     * Represents a transaction within this store
     */
    protected static class Transaction extends IOProxy {
        /**
         * The parent IO element
         */
        protected final IOBackend parent;
        /**
         * The thread executing the transaction
         */
        protected Thread thread;

        /**
         * Gets the thread executing this transaction
         *
         * @return The thread executing this transaction
         */
        public Thread getThread() {
            return thread;
        }

        /**
         * Initializes this transaction
         *
         * @param parent The parent IO element
         */
        public Transaction(IOBackend parent) {
            this.parent = parent;
        }

        /**
         * Setups this transaction
         *
         * @param backend  The backend
         * @param location The location in the backend
         * @param length   The length in the backend
         * @param writable Whether the transaction allows writing to the backend
         */
        public void setup(IOElement backend, long location, long length, boolean writable) {
            this.thread = Thread.currentThread();
            this.backend = backend;
            this.location = location;
            this.length = length;
            this.writable = writable;
        }

        /**
         * Gets whether this transaction is in conflict with the specified one
         *
         * @param transaction A transaction
         * @return true of there is a conflict, false otherwise
         */
        public boolean isInConflict(Transaction transaction) {
            if (transaction.backend != this.backend
                    || transaction.location >= this.location + this.length
                    || transaction.location + transaction.length <= this.location)
                return false;
            return (transaction.writable || writable);
        }

        @Override
        public void close() throws IOException {
            if (thread == null)
                return;
            if (thread != Thread.currentThread()) {
                try {
                    thread.join(WAIT_TIMEOUT);
                } catch (InterruptedException exception) {
                    // do nothing
                }
                if (thread.isAlive()) {
                    // force interrupt
                    thread.interrupt();
                }
            }
            parent.transactionEnd(this);
            this.thread = null;
            this.backend = null;
            this.location = 0;
            this.length = 0;
            this.writable = false;
        }
    }

    /**
     * The pool of free transaction objects
     */
    private final Transaction[] pool;
    /**
     * The transactions currently being executed
     */
    private final List<Transaction> transactions;
    /**
     * The current state of the backend
     */
    private AtomicInteger state;

    /**
     * Initializes this backend
     */
    protected IOBackend() {
        this.pool = new Transaction[MAX_TRANSACTION_COUNT];
        this.transactions = new ArrayList<>(MAX_TRANSACTION_COUNT);
        this.state = new AtomicInteger(STATE_READY);
    }

    /**
     * Gets the current state of the backend
     *
     * @return The current state of the backend
     */
    public int getState() {
        return state.get();
    }

    /**
     * Begins a transaction
     *
     * @param backend  The backend element for the IO
     * @param location The location in the backend
     * @param length   The length in the backend
     * @param writable Whether the transaction allows writing to the backend
     * @return The transaction object
     */
    protected Transaction transaction(IOElement backend, long location, long length, boolean writable) {
        if (state.get() != STATE_READY)
            return null;
        Transaction transaction = null;
        synchronized (pool) {
            for (int i = 0; i != pool.length; i++) {
                if (pool[i] != null) {
                    transaction = pool[i];
                    pool[i] = null;
                    break;
                }
            }
        }
        if (transaction == null)
            transaction = new Transaction(this);
        transaction.setup(backend, location, length, writable);
        synchronized (transactions) {
            transactions.add(transaction);
        }
        transaction.reset();
        return transaction;
    }

    /**
     * Ends a transaction
     *
     * @param transaction The transaction to end
     */
    private void transactionEnd(Transaction transaction) {
        synchronized (transactions) {
            transactions.remove(transaction);
        }
        synchronized (pool) {
            for (int i = 0; i != pool.length; i++) {
                if (pool[i] == null) {
                    pool[i] = transaction;
                    break;
                }
            }
        }
    }

    /**
     * Waits for all transactions to
     */
    protected void finalizeAllTransactions() {
        if (state.compareAndSet(STATE_READY, STATE_CLOSING))
            return;
        synchronized (transactions) {
            for (Transaction transaction : transactions) {
                try {
                    transaction.close();
                } catch (IOException exception) {
                    // do nothing
                }
            }
            transactions.clear();
        }
        state.compareAndSet(STATE_CLOSING, STATE_CLOSED);
    }

    @Override
    public void close() throws Exception {
        finalizeAllTransactions();
    }
}
