/*******************************************************************************
 * Copyright (c) 2016 Association Cénotélie (cenotelie.fr)
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
     * The hazard pointer for the elements
     */
    private static final IOAccessOrdered HAZARD = new IOAccessOrdered() {};

    /**
     * The next access element
     */
    private final AtomicReference<IOAccessOrdered> next;

    /**
     * Initializes this element
     */
    protected IOAccessOrdered() {
        this.next = new AtomicReference<>(HAZARD);
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
        IOAccessOrdered insertBefore = current;

        while (current != null) {
            // get the next access
            IOAccessOrdered next = current.next.get();
            if (next == HAZARD)
                // the current element is marked, fail here to retry
                return false;
            // shall we insert before the current element?
            if (element.location + element.length <= current.location)
                // yes, the element to insert is completely before the current element
                break;
            // not completely before the current element
            // is there an exclusivity overlap?
            if ((current.writable || element.writable) && !current.disjoints(element)) {
                // there is an overlap between the current element and the one to insert
                // and either one are exclusive
                return false;
            }
            // shall we insert before the next element?
            if ((next != null && element.location < next.location) || (next == null && insertAfter == null)) {
                // either the element to insert must be before the next one
                // or there is no next element and the insertAfter has still not be set (will be inserted as last)
                insertAfter = current;
                insertBefore = next;
            }
            current = next;
        }

        // do the insert
        // setup the element to insert
        element.next.set(insertBefore);
        // no element to insert after?
        if (insertAfter == null)
            // insert as root
            return root.compareAndSet(current, element);
        // insert after the selected element
        return insertAfter.next.compareAndSet(insertBefore, element);
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
    }

    /**
     * Tries to removes an element from the interval list
     * This method may fail due to concurrent operations in the list.
     *
     * @param root    The root element
     * @param element The element to remove
     * @return Whether the operation succeeded
     */
    private static boolean removeFrom(AtomicReference<IOAccessOrdered> root, IOAccessOrdered element) {
        IOAccessOrdered current = root.get();

        // is the element to remove the root?
        if (current == element) {
            IOAccessOrdered next = element.next.get();
            // mark the element for delete
            if (!element.next.compareAndSet(next, HAZARD))
                // concurrent insertion after this element
                return false;
            // replace the root
            if (root.compareAndSet(element, next))
                // success!
                return true;
            // failed to replace, current insertion at the root
            // un-mark the node and retry
            element.next.compareAndSet(HAZARD, next);
            return false;
        }

        // not the first one, go down the list
        while (current != null) {
            IOAccessOrdered next = current.next.get();
            if (next == HAZARD)
                // this is a concurrent delete ...
                return false;
            if (next == element) {
                // the next element is the one to be deleted
                IOAccessOrdered elementNext = element.next.get();
                // mark the element for delete
                if (!element.next.compareAndSet(elementNext, HAZARD))
                    // concurrent insertion after this element
                    return false;
                // attempt to delete
                if (current.next.compareAndSet(element, elementNext))
                    // success!
                    return true;
                // failed to replace, current insertion at the root
                // un-mark the node and retry
                element.next.compareAndSet(HAZARD, elementNext);
                return false;
            }
            current = next;
        }
        throw new Error("WTF!!");
    }
}
