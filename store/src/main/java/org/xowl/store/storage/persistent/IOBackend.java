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
import java.util.concurrent.locks.ReentrantLock;

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
     * The backend is finalizing current transactions, new one will wait
     */
    public static final int STATE_FINALIZING = 1;
    /**
     * The backend is closing, pending transactions are being terminated and new one will be refused
     */
    public static final int STATE_CLOSING = 2;
    /**
     * The backend is closed, no transaction can be performed
     */
    public static final int STATE_CLOSED = 3;

    /**
     * The maximum number of ms to wait for finalizing a transaction
     */
    private static final int WAIT_TIMEOUT = 1000;
    /**
     * The maximum number of transactions
     */
    private static final int MAX_TRANSACTION_COUNT = 16;
    /**
     * The maximum number of locks
     */
    private static final int MAX_LOCK_COUNT = 32;

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
         * The lock for the overlapping preceding block, if any
         */
        protected ReentrantLock lockBefore;
        /**
         * The lock for this block
         */
        protected ReentrantLock lockBlock;
        /**
         * The lock for the overlapping following block, if any
         */
        protected ReentrantLock lockAfter;

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
            this.lockBefore = null;
            this.lockBlock = null;
            this.lockAfter = null;
            this.backend = null;
            this.location = 0;
            this.length = 0;
            this.writable = false;
        }
    }

    /**
     * A global lock for this backend
     */
    protected final ReentrantLock globalLock;
    /**
     * The pool of free transaction objects
     */
    private final Transaction[] poolTransactions;
    /**
     * The transactions currently being executed
     */
    private final List<Transaction> transactions;
    /**
     * The pool of free lock objects
     */
    private final ReentrantLock[] poolLocks;
    /**
     * The current state of the backend
     */
    protected AtomicInteger state;

    /**
     * Initializes this backend
     */
    protected IOBackend() {
        this.globalLock = new ReentrantLock();
        this.poolTransactions = new Transaction[MAX_TRANSACTION_COUNT];
        this.transactions = new ArrayList<>(MAX_TRANSACTION_COUNT);
        this.poolLocks = new ReentrantLock[MAX_LOCK_COUNT];
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
     * Gets a free lock object
     *
     * @return A free lock object
     */
    private ReentrantLock newLock() {
        synchronized (poolLocks) {
            for (int i = 0; i != poolLocks.length; i++) {
                if (poolLocks[i] != null) {
                    ReentrantLock result = poolLocks[i];
                    poolLocks[i] = null;
                    return result;
                }
            }
        }
        return new ReentrantLock();
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
     * Returns a lock object to the pool
     *
     * @param lock The lock object
     */
    private void returnLock(ReentrantLock lock) {
        synchronized (poolLocks) {
            for (int i = 0; i != poolLocks.length; i++) {
                if (poolLocks[i] == null) {
                    poolLocks[i] = lock;
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
        int s = state.get();
        if (s == STATE_FINALIZING) {
            // wait for the end of the commit process
            globalLock.lock();
            globalLock.unlock();
        } else if (s >= STATE_CLOSING) {
            throw new IllegalStateException("The store is not in a ready state");
        }
        Transaction transaction = newTransaction();
        transaction.setupIO(backend, location, length, writable);
        transaction.thread = Thread.currentThread();
        transaction.lockBefore = null;
        transaction.lockBlock = newLock();
        transaction.lockAfter = null;
        synchronized (transactions) {
            for (Transaction executing : transactions) {
                if (executing.isInConflict(transaction)) {
                    if (executing.location <= transaction.location) {
                        transaction.lockBefore = executing.lockBlock;
                        if (transaction.lockAfter != null)
                            break;
                    } else {
                        transaction.lockAfter = executing.lockBlock;
                        if (transaction.lockBefore != null)
                            break;
                    }
                }
            }
        }
        // obtain the locks
        transaction.lockBlock.lock();
        if (transaction.lockBefore != null)
            transaction.lockBefore.lock();
        if (transaction.lockAfter != null)
            transaction.lockAfter.lock();
        // register the transaction
        synchronized (transactions) {
            transactions.add(transaction);
        }
        // go ahead
        transaction.reset();
        return transaction;
    }

    /**
     * Frees the specified lock
     *
     * @param lock A lock
     */
    private void freeLock(ReentrantLock lock) {
        if (lock == null)
            return;
        boolean queued = lock.hasQueuedThreads();
        lock.unlock();
        if (!queued)
            returnLock(lock);
    }

    /**
     * Ends a transaction
     *
     * @param transaction The transaction to end
     */
    private void transactionEnd(Transaction transaction) {
        // unregister the transaction
        synchronized (transactions) {
            transactions.remove(transaction);
        }
        // free all the locks
        freeLock(transaction.lockAfter);
        freeLock(transaction.lockBefore);
        freeLock(transaction.lockBlock);
        returnTransaction(transaction);
    }

    /**
     * Waits for all transactions to finish
     */
    protected void finalizeAllTransactions() {
        List<Transaction> currentTransactions;
        synchronized (transactions) {
            currentTransactions = new ArrayList<>(transactions);
        }
        for (Transaction transaction : currentTransactions) {
            try {
                transaction.close();
            } catch (IOException exception) {
                // do nothing
            }
        }
    }

    @Override
    public void close() throws Exception {
        globalLock.lock();
        if (!state.compareAndSet(STATE_READY, STATE_CLOSING)) {
            globalLock.unlock();
            throw new IllegalStateException("The store is not in a ready state");
        }
        finalizeAllTransactions();
        state.compareAndSet(STATE_CLOSING, STATE_CLOSED);
        globalLock.unlock();
    }
}
