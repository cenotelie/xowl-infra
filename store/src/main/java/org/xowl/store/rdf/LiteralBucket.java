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
import org.xowl.utils.data.Dataset;

import java.util.Arrays;

/**
 * Represents a bucket of literals with the same lexical value in a RDF graph
 *
 * @author Laurent Wouters
 */
public class LiteralBucket {
    /**
     * Initial size of a bucket
     */
    private static final int INIT_SIZE = 3;
    /**
     * The identifier key for the serialization of this element
     */
    public static final String SERIALIZATION_NAME = "Bucket";

    /**
     * The RDF nodes
     */
    private LiteralNode[] nodes;
    /**
     * The key to the type values
     */
    private int[] datatypes;
    /**
     * The key to the language tags
     */
    private int[] tags;
    /**
     * The number of literals in this bucket
     */
    private int size;

    /**
     * Initializes this bucket
     */
    public LiteralBucket() {
        this.nodes = new LiteralNode[INIT_SIZE];
        this.datatypes = new int[INIT_SIZE];
        this.tags = new int[INIT_SIZE];
        this.size = 0;
    }

    /**
     * Initializes this bucket from a dataset
     *
     * @param store The string store storing the IRI value
     * @param data  The node of serialized data
     */
    public LiteralBucket(StringStore store, org.xowl.utils.data.Node data) {
        this.size = data.getChildren().size();
        int init = Math.max(size, INIT_SIZE);
        this.nodes = new LiteralNode[init];
        this.datatypes = new int[init];
        this.tags = new int[init];
        for (int i = 0; i != size; i++) {
            org.xowl.utils.data.Node child = data.getChildren().get(i);
            nodes[i] = new LiteralNodeImpl(store, child);
            datatypes[i] = (int) child.attribute(LiteralNodeImpl.SERIALIZATION_DATATYPE).getValue();
            tags[i] = (int) child.attribute(LiteralNodeImpl.SERIALIZATION_TAG).getValue();
        }
    }

    /**
     * Gets the literal with the specified type and language tag already exists in this bucket
     *
     * @param type The key to the type
     * @param tag  The key to the language tag
     * @return The matching literal exists in this bucket, or <code>null</code> if none is found
     */
    public LiteralNode get(int type, int tag) {
        for (int i = 0; i != size; i++) {
            if (datatypes[i] == type && tags[i] == tag)
                return nodes[i];
        }
        return null;
    }

    /**
     * Adds the specified literal to this bucket
     *
     * @param type The key to the type
     * @param tag  The key to the language tag
     * @param node The corresponding literal node
     */
    public void add(int type, int tag, LiteralNode node) {
        if (size == nodes.length) {
            nodes = Arrays.copyOf(nodes, nodes.length + INIT_SIZE);
            datatypes = Arrays.copyOf(datatypes, datatypes.length + INIT_SIZE);
            tags = Arrays.copyOf(tags, tags.length + INIT_SIZE);
        }
        datatypes[size] = type;
        tags[size] = tag;
        nodes[size] = node;
        size++;
    }

    /**
     * Serializes this bucket
     *
     * @param dataset The dataset to serialize to
     * @return The serialized data
     */
    public org.xowl.utils.data.Node serialize(Dataset dataset) {
        org.xowl.utils.data.Node result = new org.xowl.utils.data.Node(dataset, SERIALIZATION_NAME);
        for (int i = 0; i != size; i++) {
            result.getChildren().add(nodes[i].serialize(dataset));
        }
        return result;
    }
}
