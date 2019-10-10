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

import org.xowl.infra.store.storage.cache.CachedDataset;
import org.xowl.infra.store.storage.persistent.PersistedDataset;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.UUID;

/**
 * Represents a factory of stores with different capabilities
 *
 * @author Laurent Wouters
 */
public class StoreFactory {
    /**
     * Creates a new in-memory store
     *
     * @return The new in-memory store
     */
    public static Store newInMemory() {
        return new StoreImpl(new CachedDataset());
    }

    /**
     * Creates a new persisted store
     *
     * @param location   The location for the store
     * @param isReadonly Whether the store is read-only
     * @return The new store
     * @throws IOException when an error occurred while accessing the storage
     */
    public static Store newPersisted(File location, boolean isReadonly) throws IOException {
        if (location == null)
            location = Files.createTempDirectory(UUID.randomUUID().toString()).toFile();
        return new StoreImpl(new DatasetReasonable(new PersistedDataset(location, isReadonly)));
    }
}
