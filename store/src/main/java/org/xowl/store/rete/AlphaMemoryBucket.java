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
package org.xowl.store.rete;

import org.xowl.store.rdf.AbstractStore;
import org.xowl.store.rdf.Node;
import org.xowl.store.rdf.Quad;
import org.xowl.store.rdf.VariableNode;

import java.util.Arrays;

/**
 * Represents a bucket of alpha memories
 *
 * @author Laurent Wouters
 */
abstract class AlphaMemoryBucket implements AlphaMemoryBucketElement {
    /**
     * The initial size of the buffer within this bucket
     */
    private static final int INIT_SIZE = 5;

    /**
     * The buffer of recognized nodes
     */
    private Node[] nodes;
    /**
     * The buffer of matching elements
     */
    private AlphaMemoryBucketElement[] subs;
    /**
     * The catch-all sub-element
     */
    private AlphaMemoryBucketElement catchAll;
    /**
     * The current number of registered memories in the buffer
     */
    private int size;

    /**
     * Initializes this bucket
     */
    AlphaMemoryBucket() {
        this.nodes = new Node[INIT_SIZE];
        this.subs = new AlphaMemoryBucketElement[INIT_SIZE];
    }

    /**
     * Gets the relevant node from the specified data
     *
     * @param quad A quad
     * @return The relevant node
     */
    protected abstract Node getNode(Quad quad);

    /**
     * Create a sub-bucket
     *
     * @return A new sub-bucket
     */
    protected abstract AlphaMemoryBucketElement createSub();

    @Override
    public void matchMemories(AlphaMemoryBuffer buffer, Quad quad) {
        Node node = getNode(quad);
        if (catchAll != null) {
            catchAll.matchMemories(buffer, quad);
        }
        for (int i = 0; i != nodes.length; i++) {
            if (nodes[i] == node) {
                subs[i].matchMemories(buffer, quad);
                return;
            }
        }
    }

    @Override
    public AlphaMemory resolveMemory(Quad pattern, AbstractStore store) {
        Node node = getNode(pattern);
        if (node == null || node.getNodeType() == VariableNode.TYPE) {
            if (catchAll == null)
                catchAll = createSub();
            return catchAll.resolveMemory(pattern, store);
        }

        for (int i = 0; i != nodes.length; i++) {
            if (nodes[i] == node) {
                return subs[i].resolveMemory(pattern, store);
            }
        }

        if (size == nodes.length) {
            nodes = Arrays.copyOf(nodes, nodes.length + INIT_SIZE);
            subs = Arrays.copyOf(subs, subs.length + INIT_SIZE);
            AlphaMemoryBucketElement sub = createSub();
            nodes[size] = node;
            subs[size] = sub;
            size++;
            return sub.resolveMemory(pattern, store);
        }

        for (int i = 0; i != nodes.length; i++) {
            if (nodes[i] == null) {
                nodes[i] = node;
                subs[i] = createSub();
                size++;
                return subs[i].resolveMemory(pattern, store);
            }
        }

        // cannot happen
        return null;
    }

    /**
     * Clears the content of this bucket
     */
    public void clear() {
        this.nodes = new Node[INIT_SIZE];
        this.subs = new AlphaMemoryBucketElement[INIT_SIZE];
        this.catchAll = null;
        this.size = 0;
    }
}
