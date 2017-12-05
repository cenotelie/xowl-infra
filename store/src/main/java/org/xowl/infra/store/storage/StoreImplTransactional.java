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
import org.xowl.infra.store.rdf.ChangeListener;
import org.xowl.infra.store.rdf.Dataset;

/**
 * Implements a storage system for RDF quads that relies on the support of transactions in the underlying dataset
 *
 * @author Laurent Wouters
 */
class StoreImplTransactional extends StoreImpl {
    /**
     * Implementation of a transaction for this store
     */
    private static class MyTransaction extends StoreTransaction {
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
         * @param base       The base dataset
         * @param writable   Whether this transaction allows writing
         * @param autocommit Whether this transaction should commit when being closed
         */
        public MyTransaction(DatasetTransactional base, boolean writable, boolean autocommit) {
            super(writable, autocommit);
            this.dataset = new DatasetForTransaction((DatasetImpl) base, true);
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
    }

    /**
     * The base dataset
     */
    private final DatasetTransactional base;

    /**
     * Initializes this store
     *
     * @param base The base dataset
     */
    public StoreImplTransactional(DatasetTransactional base) {
        this.base = base;
    }

    @Override
    protected MyTransaction createNewTransaction(boolean writable, boolean autocommit) {
        return new MyTransaction(base, writable, autocommit);
    }

    @Override
    public void close() throws Exception {
        base.close();
    }

    @Override
    public void addListener(ChangeListener listener) {
        base.addListener(listener);
    }

    @Override
    public void removeListener(ChangeListener listener) {
        base.removeListener(listener);
    }
}