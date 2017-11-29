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
import fr.cenotelie.commons.storage.Transaction;

/**
 * Implements a transaction for the on-disk store
 *
 * @author Laurent Wouters
 */
class QuadStoreOnDiskTransaction extends QuadTransaction {
    /**
     * The inner transaction
     */
    private final Transaction inner;

    /**
     * Initializes this transaction
     *
     * @param transaction The inner transaction
     */
    public QuadStoreOnDiskTransaction(Transaction transaction) {
        super(transaction.isWritable(), transaction.isAutocommit());
        this.inner = transaction;
    }

    /**
     * Gets the current state of this transaction
     *
     * @return The current state of this transaction
     */
    public int getState() {
        return inner.getState();
    }

    /**
     * Gets whether this transaction is an orphan because the thread that created it is no longer alive and the transaction is still running
     *
     * @return Whether this transaction is orphan
     */
    public boolean isOrphan() {
        return inner.isOrphan();
    }

    /**
     * Commits this transaction to the parent log
     *
     * @throws ConcurrentWriteException when a concurrent transaction already committed conflicting changes to the log
     */
    public void commit() throws ConcurrentWriteException {
        inner.commit();
    }

    @Override
    protected void doCommit() throws ConcurrentWriteException {
        // should not be called ...
        throw new UnsupportedOperationException();
    }

    /**
     * Aborts this transaction
     */
    public void abort() {
        inner.abort();
    }

    @Override
    public void close() throws ConcurrentWriteException {
        inner.close();
    }
}
