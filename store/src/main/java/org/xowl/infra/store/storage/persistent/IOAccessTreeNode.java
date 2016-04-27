/*******************************************************************************
 * Copyright (c) 2016 Laurent Wouters
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

package org.xowl.infra.store.storage.persistent;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Represents a controlled access to an IO backend that can be checked for exclusive use
 *
 * @author Laurent Wouters
 */
abstract class IOAccessTreeNode extends IOAccess {
    /**
     * The initial size of the access buffer for this node
     */
    private static final int BUFFER_SIZE_INIT = 4;

    /**
     * State when the access element is free to use
     */
    private static final int STATE_FREE = 0;
    /**
     * State when the access element is in use
     */
    private static final int STATE_USED = 1;
    /**
     * State when the access element is frozen by a tree operation
     */
    private static final int STATE_FROZEN = 2;

    /**
     * The current state of this node
     */
    private final AtomicInteger state;
    /**
     * The parent node
     */
    private IOAccessTreeNode parent;
    /**
     * The left child for accesses entirely on the left
     */
    private IOAccessTreeNode left;
    /**
     * The right child for accesses entirely on the right
     */
    private IOAccessTreeNode right;
    /**
     * The overlapping accesses
     */
    private IOAccessTreeNode[] overlaps;
    /**
     * The number of overlapping accesses
     */
    private int overlapsCount;

    /**
     * Initializes this element
     */
    protected IOAccessTreeNode() {
        this.state = new AtomicInteger(STATE_FREE);
        this.overlaps = new IOAccessTreeNode[BUFFER_SIZE_INIT];
    }

    /**
     * Clean the data relative to the interval tree
     */
    private void clean() {
        state.set(STATE_FREE);
        parent = null;
        left = null;
        right = null;
        for (int i = 0; i != overlapsCount; i++)
            overlaps[i] = null;
        overlapsCount = 0;
    }

    /**
     * Inserts a new element in the interval tree
     *
     * @param root    The root element
     * @param element The element to insert
     */
    public static void insert(AtomicReference<IOAccessTreeNode> root, IOAccessTreeNode element) {
        // freeze the element to insert
        element.state.set(STATE_FROZEN);
        // insert
        while (true) {
            IOAccessTreeNode rootValue = root.get();
            // is there a root?
            if (rootValue == null) {
                // no, try to set the element as the root
                if (!root.compareAndSet(null, element))
                    continue;
                element.state.set(STATE_USED);
                return;
            }
            // try to insert in the tree
            if (insertFrom(rootValue, element))
                return;
        }
    }

    /**
     * Tries to insert an element in the interval tree
     * This method may fail due to concurrent operations in the tree.
     *
     * @param root    The root to insert from
     * @param element The element to insert
     * @return Whether the operation succeeded
     */
    private static boolean insertFrom(IOAccessTreeNode root, IOAccessTreeNode element) {
        IOAccessTreeNode parent = root;
        while (true) {
            if (parent == null || parent.state.get() != STATE_USED)
                // the current parent is most likely frozen, fail here
                return false;
            if (element.location + element.length <= parent.location) {
                // the new element is on the left
                if (parent.left != null) {
                    // there is a left child, go left
                    parent = parent.left;
                    continue;
                }
                // the left child is free, try to insert
                if (!parent.state.compareAndSet(STATE_USED, STATE_FROZEN))
                    // the state of the parent child changed in the meantime, fail here
                    return false;
                parent.left = element;
                element.parent = parent;
                parent.state.set(STATE_USED);
                element.state.set(STATE_USED);
                return true;
            }
            if (element.location >= parent.location + parent.length) {
                // the new element is on the right
                if (parent.right != null) {
                    // there is a right child, go right
                    parent = parent.right;
                    continue;
                }
                // the right child is free, try to insert
                if (!parent.state.compareAndSet(STATE_USED, STATE_FROZEN))
                    // the state of the parent child changed in the meantime, fail here
                    return false;
                parent.right = element;
                element.parent = parent;
                parent.state.set(STATE_USED);
                element.state.set(STATE_USED);
                return true;
            }
            // this is an overlap with the parent
            if (parent.writable || element.writable)
                // either the parent or the element require exclusivity
                return false;
            // this is non-exclusive, register the overlap
            if (!parent.state.compareAndSet(STATE_USED, STATE_FROZEN))
                // the state of the parent child changed in the meantime, fail here
                return false;
            if (parent.overlapsCount >= parent.overlaps.length)
                parent.overlaps = Arrays.copyOf(parent.overlaps, parent.overlaps.length + BUFFER_SIZE_INIT);
            parent.overlaps[parent.overlapsCount] = element;
            parent.overlapsCount++;
            parent.state.set(STATE_USED);
            element.state.set(STATE_USED);
            return true;
        }
    }

    /**
     * Removes an element from the interval tree
     *
     * @param root    The root element
     * @param element The element to remove
     */
    public static void remove(AtomicReference<IOAccessTreeNode> root, IOAccessTreeNode element) {

        element.clean();
    }
}
