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
import org.xowl.infra.store.rdf.ChangeListener;

/**
 * Represents the public interface of a storage system for RDF quads
 *
 * @author Laurent Wouters
 */
public interface Store extends AutoCloseable {
    /**
     * Starts a new transaction
     * The transaction must be ended by a call to the transaction's close method.
     * The transaction will NOT automatically commit when closed, the commit method should be called before closing.
     *
     * @param writable Whether the transaction shall support writing
     * @return The new transaction
     */
    default StoreTransaction newTransaction(boolean writable) {
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
    StoreTransaction newTransaction(boolean writable, boolean autocommit);

    /**
     * Gets the currently running transactions for the current thread
     *
     * @return The current transaction
     * @throws NoTransactionException when the current thread does not use a transaction
     */
    StoreTransaction getTransaction() throws NoTransactionException;

    /**
     * Adds the specified listener to this store
     *
     * @param listener A listener
     */
    void addListener(ChangeListener listener);

    /**
     * Removes the specified listener from this store
     *
     * @param listener A listener
     */
    void removeListener(ChangeListener listener);
}
