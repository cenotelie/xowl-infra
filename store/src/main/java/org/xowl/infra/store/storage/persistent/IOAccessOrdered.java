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

package org.xowl.infra.store.storage.persistent;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Represents a controlled access to an IO backend that can be checked for exclusive use
 *
 * @author Laurent Wouters
 */
class IOAccessOrdered extends IOAccess {
    /**
     * The parent manager
     */
    private final IOAccessManager manager;
    /**
     * The identifier of this access
     */
    protected final int identifier;
    /**
     * The marked reference to the next access
     * - int: the potential mark
     * - int: the identifier of the successor of this element
     * If the reference is marked this node (the one owning the reference) is logically deleted
     */
    protected final AtomicLong next;
    /**
     * The successor of this element in its current pool
     */
    protected int poolNext;

    /**
     * Initializes this element
     *
     * @param manager    The parent manager
     * @param identifier The identifier of this access
     */
    protected IOAccessOrdered(IOAccessManager manager, int identifier) {
        this.manager = manager;
        this.identifier = identifier;
        this.next = new AtomicLong(0x00000000FFFFFFFFL);
        this.poolNext = -1;
    }

    @Override
    public void close() {
        manager.onAccessEnd(this);
    }
}
