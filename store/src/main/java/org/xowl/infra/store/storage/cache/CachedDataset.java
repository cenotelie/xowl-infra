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

package org.xowl.infra.store.storage.cache;

import fr.cenotelie.commons.storage.Access;
import fr.cenotelie.commons.storage.Transaction;
import org.xowl.infra.store.storage.DatasetImpl;
import org.xowl.infra.store.storage.DatasetNodesImpl;
import org.xowl.infra.store.storage.DatasetQuadsImpl;

/**
 * Concrete implementation of a basic in-memory dataset of RDF quads
 * This implementation delegates all its behavior to caching stores.
 * This quad storage system is NOT transactional.
 *
 * @author Laurent Wouters
 */
public class CachedDataset extends DatasetImpl {
    /**
     * The store for the nodes
     */
    private final CachedDatasetNodes nodes;
    /**
     * The store for the quads
     */
    private final CachedDatasetQuads quads;

    /**
     * Initializes this store
     */
    public CachedDataset() {
        nodes = new CachedDatasetNodes();
        quads = new CachedDatasetQuads();
    }

    @Override
    protected DatasetNodesImpl getNodes() {
        return nodes;
    }

    @Override
    protected DatasetQuadsImpl getQuads() {
        return quads;
    }

    @Override
    protected Transaction newTransaction(boolean writable, boolean autocommit) {
        return new Transaction(writable, autocommit) {
            @Override
            protected void doCommit() {
                // do nothing here
            }

            @Override
            protected Access newAccess(long index, int length, boolean writable) {
                // should not be called
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override
    public void close() {
        // do nothing
    }
}
