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

package org.xowl.store.storage.persistent;

/**
 * An entity that acts as a proxy to a backing element that can be read from and written to.
 * This entity can enforce boundaries on the underlying element.
 *
 * @author Laurent Wouters
 */
abstract class IOProxy implements IOElement {
    /**
     * The backing IO element
     */
    protected final IOElement backend;

    /**
     * Initializes this proxy
     *
     * @param backend The backing IO element
     */
    protected IOProxy(IOElement backend) {
        this.backend = backend;
    }

    /**
     * Resets the index to the beginning
     */
    public abstract void reset();

    /**
     * Positions the index to the specified position
     *
     * @param index The position for the index
     * @return The proxy
     */
    public abstract IOProxy seek(long index);

    /**
     * Gets the number of accessible bytes
     *
     * @return The number of accessible bytes
     */
    public abstract long length();
}
