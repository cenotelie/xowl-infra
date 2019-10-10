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

package org.xowl.infra.store.storage;

import fr.cenotelie.commons.storage.ConcurrentWriteException;
import fr.cenotelie.commons.storage.NoTransactionException;
import fr.cenotelie.commons.storage.Transaction;
import org.xowl.infra.store.execution.ExecutionManager;
import org.xowl.infra.store.rdf.ChangeListener;
import org.xowl.infra.store.rdf.Dataset;

import java.util.Arrays;
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Basic implementation for a storage system of RDF quads
 *
 * @author Laurent Wouters
 */
class StoreImpl implements Store {
    /**
     * The storage system is now closed
     */
    private static final int STATE_CLOSED = -1;
    /**
     * The storage system is ready for IO
     */
    private static final int STATE_READY = 0;
    /**
     * State flag for locking the log due to its closing
     * In this state, new transactions cannot be created.
     * Ongoing transactions must terminate.
     */
    private static final int STATE_FLAG_CLOSING_LOCK = 1;
    /**
     * State flag for locking access to the transactions register
     */
    private static final int STATE_FLAG_TRANSACTIONS_LOCK = 2;

    /**
     * Implementation of a transaction for this store
     */
    private static class MyTransaction extends StoreTransaction {
        /**
         * The parent store
         */
        private final StoreImpl parent;
        /**
         * The interface store for this transaction
         */
        private final DatasetForTransaction dataset;
        /**
         * The transaction from the backing storage system
         */
        private final Transaction transaction;

        /**
         * Initializes this transaction
         *
         * @param parent     The parent store
         * @param base       The base dataset
         * @param writable   Whether this transaction allows writing
         * @param autocommit Whether this transaction should commit when being closed
         */
        public MyTransaction(StoreImpl parent, DatasetImpl base, boolean writable, boolean autocommit) {
            super(writable, autocommit);
            this.parent = parent;
            this.dataset = new DatasetForTransaction(base, true);
            this.transaction = base.newTransaction(writable, autocommit);
        }

        @Override
        public Dataset getDataset() {
            return dataset;
        }

        @Override
        protected void doCommit() throws ConcurrentWriteException {
            dataset.commit();
            transaction.commit();
        }

        @Override
        protected void doAbort() {
            dataset.rollback();
            transaction.abort();
        }

        @Override
        protected void onClose() {
            parent.onTransactionEnd(this);
        }
    }

    /**
     * The current state of the log
     */
    private final AtomicInteger state;
    /**
     * The currently running transactions
     */
    private volatile StoreTransaction[] transactions;
    /**
     * The currently running transactions by thread
     */
    private final WeakHashMap<Thread, StoreTransaction> transactionsByThread;
    /**
     * The number of running transactions
     */
    private volatile int transactionsCount;
    /**
     * The base dataset
     */
    private final DatasetImpl base;

    /**
     * Initializes this store
     *
     * @param base The base dataset
     */
    public StoreImpl(DatasetImpl base) {
        this.transactions = new StoreTransaction[8];
        this.transactionsByThread = new WeakHashMap<>();
        this.transactionsCount = 0;
        this.state = new AtomicInteger(STATE_READY);
        this.base = base;
    }

    @Override
    public void setExecutionManager(ExecutionManager executionManager) {
        base.setExecutionManager(executionManager);
    }

    @Override
    public void addListener(ChangeListener listener) {
        base.addListener(listener);
    }

    @Override
    public void removeListener(ChangeListener listener) {
        base.removeListener(listener);
    }

    /**
     * Releases the lock on a resource in this log
     *
     * @param flag The flag used for locking the resource
     */
    private void stateRelease(int flag) {
        while (true) {
            int s = state.get();
            int target = s & (~flag);
            if (state.compareAndSet(s, target))
                break;
        }
    }

    @Override
    public StoreTransaction newTransaction(boolean writable, boolean autocommit) {
        while (true) {
            int s = state.get();
            if (s == STATE_CLOSED)
                // already closed ...
                throw new IllegalStateException();
            if ((s & STATE_FLAG_CLOSING_LOCK) == STATE_FLAG_CLOSING_LOCK)
                // flag is already used, someone is already closing this log ...
                throw new IllegalStateException();
            if ((s & STATE_FLAG_TRANSACTIONS_LOCK) == STATE_FLAG_TRANSACTIONS_LOCK)
                // flag is already used by another thread
                continue;
            if (state.compareAndSet(s, s | STATE_FLAG_TRANSACTIONS_LOCK))
                break;
        }
        try {
            StoreTransaction transaction = createNewTransaction(writable, autocommit);
            // register this transaction
            if (transactionsCount >= transactions.length) {
                //noinspection NonAtomicOperationOnVolatileField
                transactions = Arrays.copyOf(transactions, transactions.length * 2);
                transactions[transactionsCount] = transaction;
            } else {
                for (int i = 0; i != transactions.length; i++) {
                    if (transactions[i] == null) {
                        transactions[i] = transaction;
                        break;
                    }
                }
            }
            //noinspection NonAtomicOperationOnVolatileField
            transactionsCount++;
            transactionsByThread.put(Thread.currentThread(), transaction);
            return transaction;
        } finally {
            stateRelease(STATE_FLAG_TRANSACTIONS_LOCK);
        }
    }

    /**
     * Creates a new transaction
     *
     * @param writable   Whether the transaction shall support writing
     * @param autocommit Whether this transaction should commit when being closed
     * @return The new transaction
     */
    private StoreTransaction createNewTransaction(boolean writable, boolean autocommit) {
        return new MyTransaction(this, base, writable, autocommit);
    }

    @Override
    public StoreTransaction getTransaction() throws NoTransactionException {
        StoreTransaction transaction = transactionsByThread.get(Thread.currentThread());
        if (transaction == null)
            throw new NoTransactionException();
        return transaction;
    }

    /**
     * When the transaction ended
     * Unregisters this transaction
     *
     * @param transaction The transaction that ended
     */
    public void onTransactionEnd(StoreTransaction transaction) {
        while (true) {
            int s = state.get();
            if (s == STATE_CLOSED)
                // already closed ...
                throw new IllegalStateException();
            if ((s & STATE_FLAG_TRANSACTIONS_LOCK) == STATE_FLAG_TRANSACTIONS_LOCK)
                // flag is already used by another thread
                continue;
            if (state.compareAndSet(s, s | STATE_FLAG_TRANSACTIONS_LOCK))
                break;
        }
        try {
            for (int i = 0; i != transactions.length; i++) {
                if (transactions[i] == transaction) {
                    transactions[i] = null;
                    //noinspection NonAtomicOperationOnVolatileField
                    transactionsCount--;
                    transactionsByThread.remove(transaction.getThread());
                    break;
                }
            }
        } finally {
            stateRelease(STATE_FLAG_TRANSACTIONS_LOCK);
        }
    }

    /**
     * Kills the orphaned transactions
     */
    private void cleanupKillOrphans() {
        // cleanup dead transactions
        while (true) {
            int s = state.get();
            if (s == STATE_CLOSED)
                // already closed ...
                throw new IllegalStateException();
            if ((s & STATE_FLAG_TRANSACTIONS_LOCK) == STATE_FLAG_TRANSACTIONS_LOCK)
                // flag is already used by another thread
                continue;
            if (state.compareAndSet(s, s | STATE_FLAG_TRANSACTIONS_LOCK))
                break;
        }
        StoreTransaction[] toKill = null;
        int toKillCount = 0;
        try {
            for (int i = 0; i != transactions.length; i++) {
                if (transactions[i] != null && transactions[i].isOrphan()) {
                    if (toKill == null)
                        toKill = new StoreTransaction[4];
                    if (toKillCount == toKill.length)
                        toKill = Arrays.copyOf(toKill, toKill.length * 2);
                    toKill[toKillCount++] = transactions[i];
                }
            }
        } finally {
            stateRelease(STATE_FLAG_TRANSACTIONS_LOCK);
        }
        // kill the orphaned transactions
        for (int i = 0; i != toKillCount; i++) {
            transactions[i].abort();
            try {
                transactions[i].close();
            } catch (ConcurrentWriteException exception) {
                // cannot happen because we aborted the transaction before
            }
        }
    }

    @Override
    public void close() throws Exception {
        while (true) {
            int s = state.get();
            if (s == STATE_CLOSED)
                // already closed ...
                throw new IllegalStateException();
            if ((s & STATE_FLAG_CLOSING_LOCK) == STATE_FLAG_CLOSING_LOCK)
                // flag is already used, someone is already closing this log ...
                throw new IllegalStateException();
            if (state.compareAndSet(s, s | STATE_FLAG_CLOSING_LOCK))
                break;
        }
        try {
            cleanupKillOrphans();
            onClose();
        } finally {
            state.set(STATE_CLOSED);
        }
    }

    /**
     * When the store is closing
     *
     * @throws Exception when an error occurred
     */
    private void onClose() throws Exception {
        base.close();
    }
}
