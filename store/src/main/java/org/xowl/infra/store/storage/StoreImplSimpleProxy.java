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
 * Implements a storage system for RDF quads that acts as a simple proxy for a common dataset
 *
 * @author Laurent Wouters
 */
class StoreImplSimpleProxy extends StoreImpl {
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
         * Initializes this transaction
         *
         * @param parent     The parent store
         * @param updates       The updates known to this transaction
         * @param writable   Whether this transaction allows writing
         * @param autocommit Whether this transaction should commit when being closed
         */
        public MyTransaction(StoreImpl parent, DatasetQuadsDiff[] updates, boolean writable, boolean autocommit) {
            super(writable, autocommit);
            this.parent = parent;
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

        @Override
        protected void onClose() {
            parent.onTransactionEnd(this);
        }
    }

    /**
     * The base dataset
     */
    private final DatasetImpl dataset;
    /**
     * The updates of previously committed transactions
     */
    private volatile DatasetQuadsDiff[] updates;
    /**
     * The number of updates
     */
    private volatile int updatesCount;

    /**
     * Initializes this proxy
     *
     * @param dataset The base dataset
     */
    public StoreImplSimpleProxy(DatasetImpl dataset) {
        this.dataset = dataset;
    }

    @Override
    protected StoreTransaction createNewTransaction(boolean writable, boolean autocommit) {
        return null;
    }

    @Override
    public void addListener(ChangeListener listener) {
        dataset.addListener(listener);
    }

    @Override
    public void removeListener(ChangeListener listener) {
        dataset.removeListener(listener);
    }

    @Override
    protected void onClose() throws Exception {
        dataset.close();
    }
}
