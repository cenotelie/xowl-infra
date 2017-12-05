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

import org.xowl.infra.store.rdf.ChangeListener;

/**
 * Implements a storage system for RDF quads that acts as a simple proxy for a common dataset
 *
 * @author Laurent Wouters
 */
class StoreImplSimpleProxy extends StoreImpl {
    /**
     * The base dataset
     */
    private final DatasetImpl dataset;

    /**
     * Initializes this proxy
     *
     * @param dataset The base dataset
     */
    public StoreImplSimpleProxy(DatasetImpl dataset) {
        this.dataset = dataset;
    }

    @Override
    protected StoreTransaction createNewTransaction(boolean writable, boolean autocommit) {
        return null;
    }

    @Override
    public void addListener(ChangeListener listener) {
        dataset.addListener(listener);
    }

    @Override
    public void removeListener(ChangeListener listener) {
        dataset.removeListener(listener);
    }

    @Override
    public void close() throws Exception {
        onClose();
        dataset.close();
    }
}
