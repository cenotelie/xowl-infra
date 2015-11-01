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

/**
 * Represents the public API of a data store (a dataset and a node manager)
 *
 * @author Laurent Wouters
 */
public interface BaseStore extends Dataset, NodeManager, AutoCloseable {
    /**
     * Commits the outstanding changes to this store
     *
     * @return Whether the operation succeeded
     */
    boolean commit();

    /**
     * Rollback the outstanding changes to this store
     *
     * @return Whether the operation succeeded
     */
    boolean rollback();
}
