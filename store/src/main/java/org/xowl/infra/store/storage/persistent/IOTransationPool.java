/*******************************************************************************
 * Copyright (c) 2016 Laurent Wouters
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

package org.xowl.infra.store.storage.persistent;

import org.xowl.infra.utils.logging.Logger;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Represents a pool of reusable transactions
 *
 * @author Laurent Wouters
 */
class IOTransationPool {
    /**
     * The maximum number of transactions
     */
    private static final int MAX_TRANSACTION_COUNT = 16;

    /**
     * The pool of free transaction objects
     */
    private final AtomicReference<IOAccess>[] poolTransactions;

    /**
     * Initializes this pool
     */
    public IOTransationPool() {
        this.poolTransactions = new AtomicReference[MAX_TRANSACTION_COUNT];
        for (int i = 0; i != MAX_TRANSACTION_COUNT; i++)
            this.poolTransactions[i] = new AtomicReference<>(null);
    }

    /**
     * Begins a transaction for the specified information
     *
     * @param backend  The backend IO element
     * @param location The location of the span for this transaction within the backend
     * @param length   The length of the allowed span
     * @param writable Whether the transaction allows writing
     * @return The new transaction, or null if it cannot be prepared
     */
    public IOAccess begin(IOElement backend, long location, long length, boolean writable) {
        IOAccess transaction = resolveTransaction();
        transaction.setup(backend, location, length, writable);
        if (writable) {
            try {
                backend.lock();
            } catch (StorageException exception) {
                Logger.DEFAULT.error(exception);
                returnTransaction(transaction);
                return null;
            }
        }
        return transaction;
    }

    /**
     * Resolves a free transaction object
     *
     * @return A free transaction object
     */
    private IOAccess resolveTransaction() {
        for (int i = 0; i != poolTransactions.length; i++) {
            IOAccess candidate = poolTransactions[i].get();
            if (candidate != null && poolTransactions[i].compareAndSet(candidate, null))
                return candidate;
        }
        return new IOAccess() {
            @Override
            public void close() {
                onClose();
                returnTransaction(this);
            }
        };
    }

    /**
     * Returns a transaction object to the pool
     *
     * @param transaction The transaction object
     */
    private void returnTransaction(IOAccess transaction) {
        for (int i = 0; i != poolTransactions.length; i++) {
            IOAccess candidate = poolTransactions[i].get();
            if (candidate == null && poolTransactions[i].compareAndSet(null, transaction))
                return;
        }
    }
}