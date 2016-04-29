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

import java.util.concurrent.atomic.AtomicReference;

/**
 * Represents a controlled access to an IO backend that can be checked for exclusive use
 *
 * @author Laurent Wouters
 */
abstract class IOAccessOrdered extends IOAccess {
    /**
     * The next access
     */
    private final AtomicReference<IOAccessOrdered> next;

    /**
     * Initializes this element
     */
    protected IOAccessOrdered() {
        this.next = new AtomicReference<>(null);
    }

    /**
     * Inserts a new element in the interval list
     *
     * @param root    The root element
     * @param element The element to insert
     */
    public static void insert(AtomicReference<IOAccessOrdered> root, IOAccessOrdered element) {
        while (true) {
            if (insertFrom(root, element))
                break;
        }
    }

    /**
     * Tries to insert an element in the interval list
     * This method may fail due to concurrent operations in the list.
     *
     * @param root    The root to insert from
     * @param element The element to insert
     * @return Whether the operation succeeded
     */
    private static boolean insertFrom(AtomicReference<IOAccessOrdered> root, IOAccessOrdered element) {
        IOAccessOrdered current = root.get();
        IOAccessOrdered insertAfter = null;
        IOAccessOrdered insertPredecessor = root.get();
        while (current != null) {
            if (element.location + element.length <= current.location)
                // the element to insert is completely before the current element
                break;
            if ((current.writable || element.writable) && !current.disjoints(element)) {
                // there is an overlap between the current element and the one to insert
                // and either are exclusive
                return false;
            }
            // get the next access
            IOAccessOrdered next = current.next.get();
            if ((next != null && element.location < next.location) || (next == null && insertAfter == null)) {
                // either the element to insert must be before the next one
                // or there is no ext element and the insertAfter has still not be set (will be inserted as last)
                insertAfter = current;
                insertPredecessor = next;
            }
            current = next;
        }
        // do the insert
        element.next.set(insertPredecessor);
        if (insertAfter == null)
            // replace the root
            return root.compareAndSet(insertPredecessor, element);
        // else, replace the next of the insertAfter
        return insertAfter.next.compareAndSet(insertPredecessor, element);
    }

    /**
     * Removes an element from the interval list
     *
     * @param root    The root element
     * @param element The element to remove
     */
    public static void remove(AtomicReference<IOAccessOrdered> root, IOAccessOrdered element) {
        while (true) {
            if (removeFrom(root, element))
                break;
        }
        // cleanup
        element.next.set(null);
    }

    /**
     * Tries to removes an element from the interval list
     * This method may fail due to concurrent operations in the list.
     *
     * @param root    The root element
     * @param element The element to remove
     */
    private static boolean removeFrom(AtomicReference<IOAccessOrdered> root, IOAccessOrdered element) {
        if (root.get() == element)
            // the first element must be removed
            return root.compareAndSet(element, element.next.get());
        IOAccessOrdered current = root.get();
        while (current != null) {
            IOAccessOrdered next = current.next.get();
            if (next == element)
                return current.next.compareAndSet(element, element.next.get());
        }
        return false;
    }
}
