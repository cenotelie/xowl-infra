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
     * The RDF nodes
     */
    private RDFLiteralNode[] nodes;
    /**
     * The key to the type values
     */
    private int[] types;
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
        this.nodes = new RDFLiteralNode[INIT_SIZE];
        this.types = new int[INIT_SIZE];
        this.tags = new int[INIT_SIZE];
        this.size = 0;
    }

    /**
     * Gets the literal with the specified type and language tag already exists in this bucket
     *
     * @param type The key to the type
     * @param tag  The key to the language tag
     * @return The matching literal exists in this bucket, or <c>null</c> if none is found
     */
    public RDFLiteralNode get(int type, int tag) {
        for (int i = 0; i != size; i++) {
            if (types[i] == type && tags[i] == tag)
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
    public void add(int type, int tag, RDFLiteralNode node) {
        if (size == nodes.length) {
            nodes = Arrays.copyOf(nodes, nodes.length + INIT_SIZE);
            types = Arrays.copyOf(types, types.length + INIT_SIZE);
            tags = Arrays.copyOf(tags, tags.length + INIT_SIZE);
        }
        types[size] = type;
        tags[size] = tag;
        nodes[size] = node;
        size++;
    }
}
