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

import org.xowl.utils.data.Attribute;
import org.xowl.utils.data.Dataset;
import org.xowl.utils.data.Node;

/**
 * Represents an entry in a store
 *
 * @author Laurent Wouters
 */
class Entry {
    /**
     * The identifier key for the serialization of this element
     */
    private static final String SERIALIZATION_KEY = "Entry";
    /**
     * The identifier key for the serialization of the offset attribute
     */
    private static final String SERIALIZATION_OFFSET = "offset";
    /**
     * The identifier key for the serialization of the size attribute
     */
    private static final String SERIALIZATION_SIZE = "size";

    /**
     * Offset in the store of the value associated to this key
     */
    private final int offset;
    /**
     * Size of the value associated to this key
     */
    private final int size;

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
     * Loads this entry from the specified serialized data node
     *
     * @param node A data node
     */
    public Entry(Node node) {
        this.offset = (int) node.attribute(SERIALIZATION_OFFSET).getValue();
        this.size = (int) node.attribute(SERIALIZATION_SIZE).getValue();
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

    /**
     * Gets the serialization of this entry
     *
     * @param dataset The dataset to serialize in
     * @return The node containing the serailized data
     */
    public Node serializes(Dataset dataset) {
        Node node = new Node(dataset, SERIALIZATION_KEY);
        Attribute attributeOffset = new Attribute(dataset, SERIALIZATION_OFFSET);
        Attribute attributeSize = new Attribute(dataset, SERIALIZATION_SIZE);
        attributeOffset.setValue(offset);
        attributeSize.setValue(size);
        node.getAttributes().add(attributeOffset);
        node.getAttributes().add(attributeSize);
        return node;
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
