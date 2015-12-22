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
import org.xowl.store.storage.remote.Connection;
import org.xowl.store.storage.remote.HTTPConnection;
import org.xowl.utils.logging.Logger;

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
     * The primary type of storage
     */
    public enum StorageType {
        /**
         * An in-memory storage
         */
        InMemory,
        /**
         * An on-disk storage
         */
        OnDisk,
        /**
         * A remote storage
         */
        Remote,
    }

    /**
     * The configuration of a store
     */
    public static class Config {
        /**
         * The primary type of storage
         */
        private StorageType primaryStorage;
        /**
         * The location of the on-disk storage, if necessary
         */
        private File location;
        /**
         * The location for an HTTP remote host
         */
        private String httpEndpoint;
        /**
         * The login for a remote host
         */
        private String remoteLogin;
        /**
         * The password for a remote host
         */
        private String remotePassword;
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
         * Whether the store shall support multi-threading
         */
        private boolean supportMultiThreading;
        /**
         * Whether the store shall support transactions
         */
        private boolean supportTransactions;
        /**
         * Whether the store shall support version control
         */
        private boolean supportVersioning;

        /**
         * Initializes this configuration element
         */
        public Config() {
            primaryStorage = StorageType.InMemory;
        }

        /**
         * Selects an in-memory primary storage for the store
         *
         * @return This configuration element
         */
        public Config inMemory() {
            primaryStorage = StorageType.InMemory;
            return this;
        }

        /**
         * Selects an on-disk primary storage for the store
         *
         * @param location The target folder location
         * @return This configuration element
         */
        public Config onDisk(File location) {
            primaryStorage = StorageType.OnDisk;
            this.location = location;
            return this;
        }

        /**
         * Selects a remote storage accessed through a standard SPARQL endpoint
         *
         * @param endpoint The location of the HTTP remote host
         * @param login    The login for the remote host, if any
         * @param password The password for the remote host, if any
         * @return This configuration element
         */
        public Config remoteHTTP(String endpoint, String login, String password) {
            primaryStorage = StorageType.Remote;
            httpEndpoint = endpoint;
            remoteLogin = login;
            remotePassword = password;
            return this;
        }

        /**
         * Makes the store read-only
         * This only makes sense with on-disk storage.
         *
         * @return This configuration element
         */
        public Config readonly() {
            isReadonly = true;
            return this;
        }

        /**
         * Activates the support of reasoning
         * When reasoning is explicitly supported, the volatile inferred quads will never be committed to the primary storage
         *
         * @return This configuration element
         */
        public Config withReasoning() {
            supportReasoning = true;
            return this;
        }

        /**
         * Activates the support of multi-threading
         * This protects the primary storage against concurrent accesses
         *
         * @return This configuration element
         */
        public Config withMultithreading() {
            supportMultiThreading = true;
            return this;
        }

        /**
         * Activates the support of transactions
         *
         * @return This configuration element
         */
        public Config withTransactions() {
            supportTransactions = true;
            return this;
        }

        /**
         * Activates the support of version control on the store
         *
         * @return This configuration element
         */
        public Config withVersioning() {
            supportVersioning = true;
            return this;
        }

        /**
         * Makes the store
         *
         * @return The store
         */
        public BaseStore make() {
            BaseStore primary = null;
            switch (primaryStorage) {
                case InMemory:
                    primary = new InMemoryStore();
                    break;
                case OnDisk: {
                    try {
                        if (location == null)
                            location = Files.createTempDirectory(UUID.randomUUID().toString()).toFile();
                        primary = new OnDiskStore(location, isReadonly);
                    } catch (IOException | StorageException exception) {
                        Logger.DEFAULT.error(exception);
                        return null;
                    }
                    break;
                }
                case Remote: {
                    Connection connection = null;
                    if (httpEndpoint != null)
                        connection = new HTTPConnection(httpEndpoint, remoteLogin, remotePassword);
                    primary = new RemoteStore(connection, isReadonly);
                    break;
                }
            }
            BaseStore result = primary;
            if (supportReasoning)
                result = new BaseReasonableStore(result);
            if (supportMultiThreading)
                result = new BaseMTSafeStore(result);
            return result;
        }
    }

    /**
     * Creates a new store
     *
     * @return The configuration element for the store
     */
    public static Config create() {
        return new Config();
    }
}
