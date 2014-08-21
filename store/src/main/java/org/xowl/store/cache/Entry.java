/**********************************************************************
 * Copyright (c) 2014 Laurent Wouters
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
 **********************************************************************/

package org.xowl.store.cache;

/**
 * Represents an entry in a store
 *
 * @author Laurent Wouters
 */
class Entry {
    /**
     * Offset in the store of the value associated to this key
     */
    private int offset;
    /**
     * Size of the value associated to this key
     */
    private int size;

    /**
     * Initializes this entry
     *
     * @param offset Offset in the store of the value associated to this entry
     * @param size   Size of the value associated to this entry
     */
    public Entry(int offset, int size) {
        this.offset = offset;
        this.size = size;
    }

    /**
     * Gets the starting offset of this entry
     *
     * @return The starting offset of this entry
     */
    public int getOffset() {
        return offset;
    }

    /**
     * Gets the size of this entry
     *
     * @return The size of this entry
     */
    public int getSize() {
        return size;
    }

    @Override
    public int hashCode() {
        return offset;
    }

    @Override
    public boolean equals(Object o) {
        return ((o instanceof Entry) && (this == o));
    }
}
