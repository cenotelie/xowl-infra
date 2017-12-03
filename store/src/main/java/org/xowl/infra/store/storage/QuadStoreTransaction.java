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

import fr.cenotelie.commons.storage.Access;
import fr.cenotelie.commons.storage.ConcurrentWriteException;
import fr.cenotelie.commons.storage.Transaction;

/**
 * Represents a transaction for an interaction with a store of quads
 *
 * @author Laurent Wouters
 */
public class QuadStoreTransaction extends Transaction {
    /**
     * The interface to use for the store
     */
    private final QuadStore store;

    /**
     * Gets the interface to use for the store
     *
     * @return The interface to use for the store
     */
    public QuadStore getStore() {
        return store;
    }

    /**
     * Initializes this transaction
     *
     * @param writable   Whether this transaction allows writing
     * @param autocommit Whether this transaction should commit when being closed
     */
    public QuadStoreTransaction(QuadStore store, boolean writable, boolean autocommit) {
        super(writable, autocommit);
    }

    @Override
    protected void doCommit() throws ConcurrentWriteException {

    }

    @Override
    protected Access newAccess(long index, int length, boolean writable) {
        // do not allow direct access to the storage
        throw new UnsupportedOperationException();
    }
}