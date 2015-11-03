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

import java.util.ArrayList;
import java.util.List;

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
     * The backend is currently committing the outstanding changes
     */
    public static final int STATE_COMMITTING = 1;
    /**
     * The backend is closing, pending transactions are being terminated and new one will be refused
     */
    public static final int STATE_CLOSING = 2;
    /**
     * The backend is closed, no transaction can be performed
     */
    public static final int STATE_CLOSED = 3;
    /**
     * The backend is in error, probably due to an IO error
     */
    public static final int STATE_ERROR = 4;

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
         * Initializes this transaction
         *
         * @param parent The parent IO element
         */
        public Transaction(IOBackend parent) {
            this.parent = parent;
        }

        /**
         * Setups this transaction's data for IO
         *
         * @param backend  The backend that will support the IO operations
         * @param location The location in the backend
         * @param length   The length in the backend
         * @param writable Whether the transaction allows writing to the backend
         */
        public void setupIO(IOElement backend, long location, long length, boolean writable) {
            this.backend = backend;
            this.location = location;
            this.length = length;
            this.writable = writable;
        }

        @Override
        public void close() {
            parent.transactionEnd(this);
            this.backend = null;
            this.location = 0;
            this.length = 0;
            this.writable = false;
        }
    }

    /**
     * The pool of free transaction objects
     */
    private final Transaction[] poolTransactions;
    /**
     * The transactions currently being executed
     */
    private final List<Transaction> transactions;
    /**
     * The current state of the backend
     */
    protected int state;

    /**
     * Initializes this backend
     */
    protected IOBackend() {
        this.poolTransactions = new Transaction[MAX_TRANSACTION_COUNT];
        this.transactions = new ArrayList<>(MAX_TRANSACTION_COUNT);
        this.state = STATE_READY;
    }

    /**
     * Gets the current state of the backend
     *
     * @return The current state of the backend
     */
    public int getState() {
        return state;
    }

    /**
     * Gets a free transaction object
     *
     * @return A free transaction object
     */
    private Transaction newTransaction() {
        synchronized (poolTransactions) {
            for (int i = 0; i != poolTransactions.length; i++) {
                if (poolTransactions[i] != null) {
                    Transaction result = poolTransactions[i];
                    poolTransactions[i] = null;
                    return result;
                }
            }
        }
        return new Transaction(this);
    }

    /**
     * Returns a transaction object to the pool
     *
     * @param transaction The transaction object
     */
    private void returnTransaction(Transaction transaction) {
        synchronized (poolTransactions) {
            for (int i = 0; i != poolTransactions.length; i++) {
                if (poolTransactions[i] == null) {
                    poolTransactions[i] = transaction;
                    return;
                }
            }
        }
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
        if (state != STATE_READY)
            throw new IllegalStateException("The store is not in a ready state");
        Transaction transaction = newTransaction();
        transaction.setupIO(backend, location, length, writable);
        // register the transaction
        transactions.add(transaction);
        // go ahead
        transaction.reset();
        return transaction;
    }

    /**
     * Ends a transaction
     *
     * @param transaction The transaction to end
     */
    private void transactionEnd(Transaction transaction) {
        // unregister the transaction
        transactions.remove(transaction);
        returnTransaction(transaction);
    }

    @Override
    public void close() throws Exception {
        state = STATE_CLOSED;
    }
}
