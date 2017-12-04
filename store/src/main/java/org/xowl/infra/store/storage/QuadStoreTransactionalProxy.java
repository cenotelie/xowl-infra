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
import org.xowl.infra.store.execution.ExecutionManager;

import java.util.WeakHashMap;

/**
 * Proxies an existing quad store into a transactional quad store
 *
 * @author Laurent Wouters
 */
public class QuadStoreTransactionalProxy extends QuadStoreTransactional {
    /**
     * The original store to be protected by this interface
     */
    private final QuadStore base;
    /**
     * The currently running transactions
     */
    private volatile QuadStoreTransaction[] transactions;
    /**
     * The currently running transactions by thread
     */
    private final WeakHashMap<Thread, QuadStoreTransaction> transactionsByThread;
    /**
     * The number of running transactions
     */
    private volatile int transactionsCount;
    /**
     * The index of transaction data currently in the log
     */
    private volatile DatasetDiff[] index;
    /**
     * The number of transaction data in the index
     */
    private volatile int indexLength;


    @Override
    public QuadStoreTransaction newTransaction(boolean writable, boolean autocommit) {
        return null;
    }

    @Override
    public QuadStoreTransaction getTransaction() throws NoTransactionException {
        return null;
    }

    @Override
    public void setExecutionManager(ExecutionManager executionManager) {

    }

    @Override
    public void close() throws Exception {

    }
}
