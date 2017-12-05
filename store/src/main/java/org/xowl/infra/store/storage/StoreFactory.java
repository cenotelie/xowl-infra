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
import org.xowl.infra.store.storage.persistent.PersistedDatasetTransactional;

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
     * Creates a new store
     *
     * @return The configuration element for the store
     */
    public static StoreFactory create() {
        return new StoreFactory();
    }

    /**
     * The primary type of storage
     */
    private StoreType primaryStorage;
    /**
     * The location of the on-disk storage, if necessary
     */
    private File location;
    /**
     * Whether the store is read-only
     */
    private boolean isReadonly;
    /**
     * Whether the store shall support reasoning
     * When reasoning is explicitly supported, the volatile inferred quads will never be committed to the primary storage
     */
    private boolean supportReasoning;

    /**
     * Initializes this configuration element
     */
    private StoreFactory() {
        primaryStorage = StoreType.InMemory;
    }

    /**
     * Selects an in-memory storage system
     *
     * @return This configuration element
     */
    public StoreFactory inMemory() {
        primaryStorage = StoreType.InMemory;
        return this;
    }

    /**
     * Selects a persisted storage system
     *
     * @param location The target folder location
     * @return This configuration element
     */
    public StoreFactory persisted(File location) {
        primaryStorage = StoreType.Persisted;
        this.location = location;
        return this;
    }

    /**
     * Makes the storage system read-only
     * This only makes sense with persisted storage systems.
     *
     * @return This configuration element
     */
    public StoreFactory readonly() {
        isReadonly = true;
        return this;
    }

    /**
     * Activates the support of reasoning
     * When reasoning is explicitly supported, the volatile inferred quads will never be committed to the primary storage
     *
     * @return This configuration element
     */
    public StoreFactory withReasoning() {
        supportReasoning = true;
        return this;
    }

    /**
     * Makes the store
     *
     * @return The store
     * @throws IOException when failed to access the specified location
     */
    public Store make() throws IOException {
        Store primary = null;
        switch (primaryStorage) {
            case InMemory: {
                if (supportReasoning)
                    return new StoreImplSimpleProxy(new DatasetReasonable(new CachedDataset()));
                return new StoreImplSimpleProxy(new CachedDataset());
            }
            case Persisted: {
                if (location == null)
                    location = Files.createTempDirectory(UUID.randomUUID().toString()).toFile();
                if (supportReasoning)
                    return new StoreImplTransactional(new DatasetReasonableTransactional(new PersistedDatasetTransactional(location, isReadonly)));
                return new StoreImplTransactional(new PersistedDatasetTransactional(location, isReadonly));
            }
            default:
                return null;
        }
    }
}
