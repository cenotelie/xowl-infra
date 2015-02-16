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

import java.util.Arrays;

/**
 * Represents a bucket of entries with the same hash
 *
 * @author Laurent Wouters
 */
class HashBucket {
    /**
     * Initial size of the bucket
     */
    private static final int INIT_SIZE = 4;
    /**
     * Increment for the bucket expansion
     */
    private static final int SIZE_INCREMENT = 4;

    /**
     * The common hash
     */
    private int hash;
    /**
     * Indices of the entries in this bucket
     */
    private int[] content;
    /**
     * The size of this bucket
     */
    private int size;

    /**
     * Initializes this bucket
     *
     * @param hash The common hash for entries in this bucket
     */
    public HashBucket(int hash) {
        this.hash = hash;
        this.content = new int[INIT_SIZE];
        this.size = 0;
    }

    /**
     * Gets the common hash for entries in this bucket
     *
     * @return The common hash
     */
    public int getHash() {
        return hash;
    }

    /**
     * Gets the size of this bucket
     *
     * @return The size of this bucket
     */
    public int getSize() {
        return size;
    }

    /**
     * Gets the index in the store of the entry at the given index in this bucket
     *
     * @param index The index in this bucket
     * @return The index in the store of the entry
     */
    public int getEntry(int index) {
        return content[index];
    }

    /**
     * Adds an entry to this bucket
     *
     * @param index The index in the store of the entry to add
     */
    public void add(int index) {
        if (size == content.length)
            content = Arrays.copyOf(content, content.length + SIZE_INCREMENT);
        content[size] = index;
        size++;
    }
}
