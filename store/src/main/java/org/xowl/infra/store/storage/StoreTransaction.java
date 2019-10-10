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
import fr.cenotelie.commons.storage.Transaction;
import org.xowl.infra.store.rdf.Dataset;

/**
 * Represents a transaction for an interaction with a store of RDF quads
 *
 * @author Laurent Wouters
 */
public abstract class StoreTransaction extends Transaction {
    /**
     * Initializes this transaction
     *
     * @param writable   Whether this transaction allows writing
     * @param autocommit Whether this transaction should commit when being closed
     */
    public StoreTransaction(boolean writable, boolean autocommit) {
        super(writable, autocommit);
    }

    /**
     * Gets the dataset to use for this transaction
     *
     * @return The dataset to use for this transaction
     */
    public abstract Dataset getDataset();

    @Override
    protected Access newAccess(long index, int length, boolean writable) {
        // do not allow direct access to the storage
        throw new UnsupportedOperationException();
    }
}