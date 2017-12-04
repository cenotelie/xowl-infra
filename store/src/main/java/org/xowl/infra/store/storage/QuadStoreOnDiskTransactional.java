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
import fr.cenotelie.commons.storage.TransactionalStorage;
import fr.cenotelie.commons.storage.files.RawFile;
import fr.cenotelie.commons.storage.files.RawFileBuffered;
import fr.cenotelie.commons.storage.files.RawFileFactory;
import fr.cenotelie.commons.storage.files.RawFileSplit;
import fr.cenotelie.commons.storage.stores.ObjectStore;
import fr.cenotelie.commons.storage.stores.ObjectStoreTransactional;
import fr.cenotelie.commons.storage.wal.WriteAheadLog;
import org.xowl.infra.store.rdf.*;
import org.xowl.infra.store.storage.cache.CachedNodes;
import org.xowl.infra.store.storage.persistent.PersistedDataset;
import org.xowl.infra.store.storage.persistent.PersistedNodes;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

/**
 * Implementation of a persisted data store that use a transactional storage system.
 *
 * @author Laurent Wouters
 */
class QuadStoreOnDiskTransactional extends QuadStoreTransactional {
    /**
     * Base store for storage
     */
    private static class StoreBase extends QuadStoreOnDisk {
        /**
         * The storage system
         */
        private final TransactionalStorage storage;

        /**
         * Initializes this store
         *
         * @param directory  The parent directory containing the backing files
         * @param isReadonly Whether this store is in readonly mode
         * @throws IOException When the backing files cannot be accessed
         */
        public StoreBase(File directory, boolean isReadonly) throws IOException {
            this.storage = new WriteAheadLog(
                    new RawFileSplit(directory, "xowl-", ".dat", new RawFileFactory() {
                        @Override
                        public RawFile newStorage(File file, boolean writable) throws IOException {
                            return new RawFileBuffered(file, writable);
                        }
                    }, !isReadonly, 1 << 30),
                    new RawFileBuffered(new File(directory, "xowl-log.dat"), !isReadonly)
            );
            boolean doInit = storage.getSize() == 0;
            ObjectStore objectStore = new ObjectStoreTransactional(storage);
            try (Transaction transaction = storage.newTransaction(doInit)) {
                this.persistedNodes = new PersistedNodes(objectStore, doInit);
                this.persistedDataset = new PersistedDataset(persistedNodes, objectStore, doInit);
                if (doInit)
                    transaction.commit();
            }
            this.cacheNodes = new CachedNodes();
        }

        @Override
        public void close() throws IOException {
            storage.close();
        }
    }

    /**
     * Interface store for transactions
     */
    private static class StoreInterface extends QuadStoreOnDisk {
        /**
         * The cache to use for the dataset
         */
        private final DatasetChaching cache;
        /**
         * The diff-ing dataset for the transaction
         */
        private final DatasetDiff diff;

        /**
         * Initializes this interface
         *
         * @param toCopy The interface to copy
         */
        public StoreInterface(QuadStoreOnDisk toCopy) {
            super(toCopy);
            this.cache = new DatasetChaching(toCopy.persistedDataset);
            this.diff = new DatasetDiff(this.cache);
        }

        @Override
        public long getMultiplicity(Quad quad) throws UnsupportedNodeType {
            return diff.getMultiplicity(quad);
        }

        @Override
        public long getMultiplicity(GraphNode graph, SubjectNode subject, Property property, Node object) throws UnsupportedNodeType {
            return diff.getMultiplicity(graph, subject, property, object);
        }

        @Override
        public Iterator<? extends Quad> getAll() {
            return diff.getAll();
        }

        @Override
        public Iterator<? extends Quad> getAll(GraphNode graph) throws UnsupportedNodeType {
            return diff.getAll(graph);
        }

        @Override
        public Iterator<? extends Quad> getAll(SubjectNode subject, Property property, Node object) throws UnsupportedNodeType {
            return diff.getAll(subject, property, object);
        }

        @Override
        public Iterator<? extends Quad> getAll(GraphNode graph, SubjectNode subject, Property property, Node object) throws UnsupportedNodeType {
            return diff.getAll(graph, subject, property, object);
        }

        @Override
        public Collection<GraphNode> getGraphs() {
            return diff.getGraphs();
        }

        @Override
        public long count() {
            return diff.count();
        }

        @Override
        public long count(GraphNode graph) throws UnsupportedNodeType {
            return diff.count(graph);
        }

        @Override
        public long count(SubjectNode subject, Property property, Node object) throws UnsupportedNodeType {
            return diff.count(subject, property, object);
        }

        @Override
        public long count(GraphNode graph, SubjectNode subject, Property property, Node object) throws UnsupportedNodeType {
            return diff.count(graph, subject, property, object);
        }


        @Override
        public void insert(Changeset changeset) throws UnsupportedNodeType {
            diff.insert(changeset);
        }

        @Override
        public void add(Quad quad) throws UnsupportedNodeType {
            diff.add(quad);
        }

        @Override
        public void add(GraphNode graph, SubjectNode subject, Property property, Node value) throws UnsupportedNodeType {
            diff.add(graph, subject, property, value);
        }

        @Override
        public void remove(Quad quad) throws UnsupportedNodeType {
            diff.remove(quad);
        }

        @Override
        public void remove(GraphNode graph, SubjectNode subject, Property property, Node value) throws UnsupportedNodeType {
            diff.remove(graph, subject, property, value);
        }

        @Override
        public void clear() {
            diff.clear();
        }

        @Override
        public void clear(GraphNode graph) throws UnsupportedNodeType {
            diff.clear(graph);
        }

        @Override
        public void copy(GraphNode origin, GraphNode target, boolean overwrite) throws UnsupportedNodeType {
            diff.copy(origin, target, overwrite);
        }

        @Override
        public void move(GraphNode origin, GraphNode target) throws UnsupportedNodeType {
            diff.move(origin, target);
        }

        @Override
        public void close() throws Exception {
            throw new UnsupportedOperationException();
        }
    }

    /**
     * Implementation of a transaction for this store
     */
    private static class StoreTransaction extends QuadStoreTransaction {
        /**
         * The interface store for this transaction
         */
        private final StoreInterface storeInterface;
        /**
         * The transaction from the backing storage system
         */
        private final Transaction transaction;

        /**
         * Initializes this transaction
         *
         * @param storeBase  The base store
         * @param writable   Whether this transaction allows writing
         * @param autocommit Whether this transaction should commit when being closed
         */
        public StoreTransaction(StoreBase storeBase, boolean writable, boolean autocommit) {
            super(writable, autocommit);
            this.storeInterface = new StoreInterface(storeBase);
            this.transaction = storeBase.storage.newTransaction(writable, autocommit);
        }

        @Override
        public QuadStore getStore() {
            return storeInterface;
        }

        @Override
        protected void doCommit() throws ConcurrentWriteException {
            storeInterface.diff.commit();
            transaction.commit();
        }

        @Override
        protected void doAbort() {
            storeInterface.diff.rollback();
            transaction.abort();
        }
    }

    /**
     * The base store
     */
    private final StoreBase base;

    /**
     * Initializes this store
     *
     * @param directory  The parent directory containing the backing files
     * @param isReadonly Whether this store is in readonly mode
     * @throws IOException When the backing files cannot be accessed
     */
    public QuadStoreOnDiskTransactional(File directory, boolean isReadonly) throws IOException {
        this.base = new StoreBase(directory, isReadonly);
    }

    @Override
    protected QuadStoreTransaction createNewTransaction(boolean writable, boolean autocommit) {
        return new StoreTransaction(base, writable, autocommit);
    }

    @Override
    public void close() throws IOException {
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