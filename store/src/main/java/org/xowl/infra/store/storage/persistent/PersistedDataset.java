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

package org.xowl.infra.store.storage.persistent;

import fr.cenotelie.commons.storage.Transaction;
import fr.cenotelie.commons.storage.TransactionalStorage;
import fr.cenotelie.commons.storage.files.RawFileBuffered;
import fr.cenotelie.commons.storage.files.RawFileFactory;
import fr.cenotelie.commons.storage.files.RawFileSplit;
import fr.cenotelie.commons.storage.stores.ObjectStore;
import fr.cenotelie.commons.storage.stores.ObjectStoreTransactional;
import fr.cenotelie.commons.storage.wal.WriteAheadLog;
import org.xowl.infra.store.storage.DatasetImpl;
import org.xowl.infra.store.storage.DatasetNodesImpl;
import org.xowl.infra.store.storage.DatasetQuadsImpl;

import java.io.File;
import java.io.IOException;

/**
 * Base implementation of a quad storage system that is persisted, presumably in a file on disk
 *
 * @author Laurent Wouters
 */
public class PersistedDataset extends DatasetImpl {
    /**
     * The storage system
     */
    private final TransactionalStorage storage;
    /**
     * The store for the nodes
     */
    private final PersistedDatasetNodes persistedNodes;
    /**
     * The store for the quads
     */
    private final PersistedDatasetQuads persistedDataset;

    /**
     * Initializes this store
     */
    /**
     * Initializes this store
     *
     * @param directory  The parent directory containing the backing files
     * @param isReadonly Whether this store is in readonly mode
     * @throws IOException When the backing files cannot be accessed
     */
    public PersistedDataset(File directory, boolean isReadonly) throws IOException {
        this.storage = new WriteAheadLog(
                new RawFileSplit(directory, "xowl-", ".dat", RawFileFactory.DEFAULT, !isReadonly, 1 << 30),
                new RawFileBuffered(new File(directory, "xowl-log.dat"), !isReadonly)
        );
        boolean doInit = storage.getSize() == 0;
        ObjectStore objectStore = new ObjectStoreTransactional(storage);
        try (Transaction transaction = storage.newTransaction(doInit)) {
            this.persistedNodes = new PersistedDatasetNodes(objectStore, doInit);
            this.persistedDataset = new PersistedDatasetQuads(persistedNodes, objectStore, doInit);
            if (doInit)
                transaction.commit();
        }
    }

    @Override
    public Transaction newTransaction(boolean writable, boolean autocommit) {
        return storage.newTransaction(writable, autocommit);
    }

    @Override
    protected DatasetNodesImpl getNodes() {
        return persistedNodes;
    }

    @Override
    protected DatasetQuadsImpl getQuads() {
        return persistedDataset;
    }

    @Override
    public void close() throws IOException {
        storage.close();
    }
}
