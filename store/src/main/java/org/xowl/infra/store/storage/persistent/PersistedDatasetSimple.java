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

import fr.cenotelie.commons.storage.Storage;
import fr.cenotelie.commons.storage.files.RawFileBuffered;
import fr.cenotelie.commons.storage.stores.ObjectStore;
import fr.cenotelie.commons.storage.stores.ObjectStoreSimple;
import org.xowl.infra.store.storage.cache.CachedDatasetNodes;

import java.io.File;
import java.io.IOException;

/**
 * Simple implementation of a quad storage system that is persisted, presumably in a file on disk.
 *
 * @author Laurent Wouters
 */
public class PersistedDatasetSimple extends PersistedDataset {
    /**
     * The backing storage system
     */
    private final Storage storage;

    /**
     * Initializes this store
     *
     * @param file       The backing file
     * @param isReadonly Whether this store is in readonly mode
     * @throws IOException When the backing files cannot be accessed
     */
    public PersistedDatasetSimple(File file, boolean isReadonly) throws IOException {
        this.storage = new RawFileBuffered(file, !isReadonly);
        boolean doInit = storage.getSize() == 0;
        ObjectStore objectStore = new ObjectStoreSimple(storage);
        this.persistedNodes = new PersistedDatasetNodes(objectStore, doInit);
        this.persistedDataset = new PersistedDatasetQuads(persistedNodes, objectStore, doInit);
        this.cacheNodes = new CachedDatasetNodes();
    }

    @Override
    public void close() throws IOException {
        storage.close();
    }
}
