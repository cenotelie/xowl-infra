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

import org.xowl.infra.store.rdf.BlankNode;
import org.xowl.infra.store.rdf.IRINode;
import org.xowl.infra.store.storage.DatasetImpl;
import org.xowl.infra.store.storage.DatasetNodesImpl;
import org.xowl.infra.store.storage.DatasetQuadsImpl;
import org.xowl.infra.store.storage.cache.CachedDatasetNodes;

/**
 * Base implementation of a quad storage system that is persisted, presumably in a file on disk
 *
 * @author Laurent Wouters
 */
public abstract class PersistedDataset extends DatasetImpl {
    /**
     * The store for the nodes
     */
    protected PersistedDatasetNodes persistedNodes;
    /**
     * The store for the quads
     */
    protected PersistedDatasetQuads persistedDataset;
    /**
     * The node manager for the cache
     */
    protected CachedDatasetNodes cacheNodes;

    /**
     * Initializes this store
     */
    protected PersistedDataset() {
    }

    /**
     * Initializes this store
     *
     * @param toCopy The store to copy
     */
    protected PersistedDataset(PersistedDataset toCopy) {
        this.persistedNodes = toCopy.persistedNodes;
        this.persistedDataset = toCopy.persistedDataset;
        this.cacheNodes = toCopy.cacheNodes;
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
    public IRINode getExistingIRINode(String iri) {
        return persistedNodes.getExistingIRINode(iri);
    }

    @Override
    public BlankNode getBlankNode() {
        return persistedNodes.getBlankNode();
    }
}
