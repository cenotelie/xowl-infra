/*******************************************************************************
 * Copyright (c) 2015 Laurent Wouters
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
 *
 * Contributors:
 *     Laurent Wouters - lwouters@xowl.org
 ******************************************************************************/

package org.xowl.store.storage;

import org.xowl.store.storage.persistent.StorageException;

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
     * Creates a new store that is held in memory
     *
     * @return The store
     */
    public static BaseStore newInMemoryStore() {
        return new InMemoryStore();
    }

    /**
     * Creates a new store backed by disk files in a temporary folder
     *
     * @return The store
     */
    public static BaseStore newFileStore() {
        try {
            File directory = Files.createTempDirectory(UUID.randomUUID().toString()).toFile();
            return new OnDiskStore(directory, false);
        } catch (IOException | StorageException exception) {
            return null;
        }
    }

    /**
     * Creates a new store backed by disk files
     *
     * @param directory The directory containing the data files
     * @return The store
     */
    public static BaseStore newFileStore(File directory) {
        try {
            return new OnDiskStore(directory, false);
        } catch (IOException | StorageException exception) {
            return null;
        }
    }

    /**
     * Creates a new store backed by disk files in read-only mode
     *
     * @param directory The directory containing the data files
     * @return The store
     */
    public static BaseStore newReadOnlyFileStore(File directory) {
        try {
            return new OnDiskStore(directory, true);
        } catch (IOException | StorageException exception) {
            return null;
        }
    }
}
