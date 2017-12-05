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

/**
 * Implements a dataset of RDF quads that can be used within a transaction
 *
 * @author Laurent Wouters
 */
class DatasetForTransaction extends DatasetImpl {
    /**
     * The base dataset protected by the transaction
     */
    private final DatasetImpl base;
    /**
     * The diff-ing dataset for the transaction
     */
    private final DatasetQuadsDiff diff;

    /**
     * Initializes this dataset
     *
     * @param base     The base dataset protected by the transaction
     * @param useCache Whether to use a cache when interacting with the base dataset
     */
    public DatasetForTransaction(DatasetImpl base, boolean useCache) {
        this.base = base;
        this.diff = new DatasetQuadsDiff(useCache ? new DatasetQuadsCaching(base.getQuads()) : base.getQuads());
    }

    @Override
    protected DatasetNodesImpl getNodes() {
        return base.getNodes();
    }

    @Override
    protected DatasetQuadsImpl getQuads() {
        return diff;
    }

    @Override
    public void close() {
        // this method should not be called
        throw new UnsupportedOperationException();
    }

    /**
     * Commits the changes made by the transaction to the base dataset
     */
    public void commit() {
        diff.commit();
    }

    /**
     * Reverts any change made by the transaction
     */
    public void rollback() {
        diff.rollback();
    }
}
