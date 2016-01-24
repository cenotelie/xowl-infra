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

package org.xowl.infra.store.rete;

/**
 * Represents a buffer of alpha memories
 *
 * @author Laurent Wouters
 */
class AlphaMemoryBuffer {
    /**
     * The content;
     */
    private final AlphaMemory[] content;
    /**
     * The number of contained elements
     */
    private int size;

    /**
     * Initializes the buffer
     */
    public AlphaMemoryBuffer() {
        this.content = new AlphaMemory[16];
    }

    /**
     * Gets the number of memories in this buffer
     *
     * @return The number of memories in this buffer
     */
    public int size() {
        return size;
    }

    /**
     * Adds the specified memory to this buffer
     *
     * @param memory A memory
     */
    public void add(AlphaMemory memory) {
        content[size++] = memory;
    }

    /**
     * Gets the i-th memory in this buffer
     *
     * @param index An index
     * @return The i-th memory in this buffer
     */
    public AlphaMemory get(int index) {
        return content[index];
    }

    /**
     * Clears the content of this buffer
     */
    public void clear() {
        size = 0;
    }
}
