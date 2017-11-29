/*******************************************************************************
 * Copyright (c) 2016 Association Cénotélie (cenotelie.fr)
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

/**
 * Represents the public API of a store of RDF quads
 *
 * @author Laurent Wouters
 */
public abstract class QuadStore implements Dataset, NodeManager, AutoCloseable {
    /**
     * Sets the execution manager to use
     *
     * @param executionManager The execution manager to use
     */
    public void setExecutionManager(ExecutionManager executionManager) {
    }

    /**
     * Starts a new transaction
     * The transaction must be ended by a call to the transaction's close method.
     * The transaction will NOT automatically commit when closed, the commit method should be called before closing.
     *
     * @param writable Whether the transaction shall support writing
     * @return The new transaction
     */
    public QuadTransaction newTransaction(boolean writable) {
        return newTransaction(writable, false);
    }

    /**
     * Starts a new transaction
     * The transaction must be ended by a call to the transaction's close method.
     *
     * @param writable   Whether the transaction shall support writing
     * @param autocommit Whether this transaction should commit when being closed
     * @return The new transaction
     */
    public abstract QuadTransaction newTransaction(boolean writable, boolean autocommit);

    /**
     * Gets the currently running transactions for the current thread
     *
     * @return The current transaction
     * @throws NoTransactionException when the current thread does not use a transaction
     */
    public abstract QuadTransaction getTransaction() throws NoTransactionException;

    @Override
    public void close() throws Exception {
        // do nothing
    }
}
