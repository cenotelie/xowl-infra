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

package org.xowl.store.storage.cache;

import org.xowl.store.rdf.LiteralNode;

import java.lang.ref.WeakReference;
import java.util.Arrays;

/**
 * Represents a bucket of literals with the same lexical value in a RDF graph
 *
 * @author Laurent Wouters
 */
class LiteralBucket {
    /**
     * Initial size of a bucket
     */
    private static final int INIT_SIZE = 3;

    /**
     * The existing literal nodes
     * Do not store strong references to the literals so that they can be gc-ed if not used
     */
    private WeakReference<LiteralNode>[] nodes;
    /**
     * The number of literals in this bucket
     */
    private int size;

    /**
     * Gets the number of literals in this bucket
     * @return The number of literals in this bucket
     */
    public int getSize() {
        return size;
    }

    /**
     * Initializes this bucket
     */
    public LiteralBucket() {
        this.nodes = new WeakReference[INIT_SIZE];
        this.size = 0;
    }

    /**
     * Gets the literal with the specified type and language tag
     *
     * @param lexical The original lexical value
     * @param datatype The datatype to match
     * @param langTag  The language tag to match
     * @return The matching literal node
     */
    public LiteralNode get(String lexical, String datatype, String langTag) {
        LiteralNode result = null;
        int insert = 0;
        for (int i = 0; i != size; i++) {
            LiteralNode candidate = nodes[i].get();
            if (candidate != null) {
                nodes[insert] = nodes[i];
                insert++;
                if (result == null) {
                    if (langTag != null) {
                        if (langTag.equals(candidate.getLangTag()))
                            result = candidate;
                    } else if (datatype != null) {
                        if (datatype.equals(candidate.getDatatype()))
                            result = candidate;
                    }
                }
            }
        }
        size = insert;
        if (result != null)
            return result;
        if (size == nodes.length)
            nodes = Arrays.copyOf(nodes, nodes.length + INIT_SIZE);
        result = new CachedLiteralNode(size == 0 ? lexical : nodes[0].get().getLexicalValue(), datatype, langTag);
        nodes[size] = new WeakReference<>(result);
        size++;
        return result;
    }

    /**
     * Cleanups dead entries in this bucket
     */
    public void cleanup() {
        int insert = 0;
        for (int i = 0; i != size; i++) {
            LiteralNode candidate = nodes[i].get();
            if (candidate != null) {
                nodes[insert] = nodes[i];
                insert++;
            }
        }
        size = insert;
    }
}
