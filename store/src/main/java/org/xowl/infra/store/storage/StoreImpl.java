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

import fr.cenotelie.commons.storage.NoTransactionException;

import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.WeakHashMap;

/**
 * Basic implementation for a storage system of RDF quads
 *
 * @author Laurent Wouters
 */
abstract class StoreImpl implements Store {
    /**
     * The currently running transactions
     */
    protected volatile StoreTransaction[] transactions;
    /**
     * The currently running transactions by thread
     */
    protected final WeakHashMap<Thread, StoreTransaction> transactionsByThread;
    /**
     * The number of running transactions
     */
    protected volatile int transactionsCount;

    /**
     * Initializes this store
     */
    protected StoreImpl() {
        this.transactions = new StoreTransaction[8];
        this.transactionsByThread = new WeakHashMap<>();
        this.transactionsCount = 0;
    }

    @Override
    public StoreTransaction newTransaction(boolean writable, boolean autocommit) {
        StoreTransaction transaction = createNewTransaction(writable, autocommit);
        synchronized (transactionsByThread) {
            transactionsByThread.put(Thread.currentThread(), transaction);
            if (transactionsCount >= transactions.length) {
                transactions = Arrays.copyOf(transactions, transactions.length * 2);
                transactions[transactionsCount++] = transaction;
                return transaction;
            }
            for (int i = 0; i != transactions.length; i++) {
                if (transactions[i] == null) {
                    transactions[i] = transaction;
                    transactionsCount++;
                    return transaction;
                }
            }
            throw new ConcurrentModificationException();
        }
    }

    /**
     * Creates a new transaction
     *
     * @param writable   Whether the transaction shall support writing
     * @param autocommit Whether this transaction should commit when being closed
     * @return The new transaction
     */
    protected abstract StoreTransaction createNewTransaction(boolean writable, boolean autocommit);

    @Override
    public StoreTransaction getTransaction() throws NoTransactionException {
        synchronized (transactionsByThread) {
            StoreTransaction transaction = transactionsByThread.get(Thread.currentThread());
            if (transaction == null)
                throw new NoTransactionException();
            return transaction;
        }
    }

    /**
     * When the store is closing
     */
    protected void onClose() {
        // TODO: wait for the transactions here
    }
}
