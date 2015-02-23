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

package org.xowl.store.rdf;

import org.xowl.store.cache.StringStore;
import org.xowl.utils.data.Attribute;
import org.xowl.utils.data.Dataset;

/**
 * Represents a node associated to an IRI in a RDF graph
 *
 * @author Laurent Wouters
 */
class IRINodeImpl extends IRINode {
    /**
     * The identifier key for the serialization of the key attribute
     */
    private static final String SERIALIZATION_KEY = "key";

    /**
     * The string store storing the IRI value
     */
    private StringStore store;
    /**
     * The key in the store
     */
    private int key;

    /**
     * Initializes this node
     *
     * @param store The string store storing the IRI value
     * @param key   The key in the store
     */
    public IRINodeImpl(StringStore store, int key) {
        this.store = store;
        this.key = key;
    }

    /**
     * Initializes this node from a dataset
     *
     * @param store The string store storing the IRI value
     * @param data  The node of serialized data
     */
    public IRINodeImpl(StringStore store, org.xowl.utils.data.Node data) {
        this.store = store;
        this.key = (int) data.attribute(SERIALIZATION_KEY).getValue();
    }

    /**
     * Gets the key to the IRI in the string store
     *
     * @return The key to the IRI
     */
    int getKey() {
        return key;
    }

    @Override
    public String getIRIValue() {
        return store.retrieve(key);
    }

    @Override
    public org.xowl.utils.data.Node serialize(Dataset dataset) {
        org.xowl.utils.data.Node result = new org.xowl.utils.data.Node(dataset, SERIALIZATION_NAME);
        Attribute attributeType = new Attribute(dataset, SERIALIZATION_TYPE);
        attributeType.setValue(TYPE);
        result.getAttributes().add(attributeType);
        Attribute attributeKey = new Attribute(dataset, SERIALIZATION_KEY);
        attributeKey.setValue(key);
        result.getAttributes().add(attributeKey);
        return result;
    }

    @Override
    public int hashCode() {
        return key;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof IRINodeImpl) {
            IRINodeImpl node = (IRINodeImpl) obj;
            return (this.key == node.key);
        }
        return false;
    }
}
