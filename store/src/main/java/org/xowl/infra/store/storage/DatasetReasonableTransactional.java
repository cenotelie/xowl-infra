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

import fr.cenotelie.commons.storage.Transaction;

/**
 * Represents a dataset of RDF quads composed of
 * - a backend dataset of ground quads
 * - a volatile dataset for quads coming from reasoning facilities
 *
 * @author Laurent Wouters
 */
public class DatasetReasonableTransactional extends DatasetReasonable implements DatasetTransactional {
    /**
     * The ground dataset
     */
    private final DatasetTransactional ground;

    /**
     * Initializes this store
     *
     * @param ground The ground dataset
     */
    public DatasetReasonableTransactional(DatasetTransactional ground) {
        super(ground);
        this.ground = ground;
    }

    @Override
    public Transaction newTransaction(boolean writable, boolean autocommit) {
        return ground.newTransaction(writable, autocommit);
    }
}
