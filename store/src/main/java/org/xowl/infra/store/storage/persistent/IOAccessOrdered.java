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

import java.util.concurrent.atomic.AtomicBoolean;
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
     * Whether an operation is touching this node
     */
    private final AtomicBoolean isBusy;

    /**
     * Initializes this element
     */
    protected IOAccessOrdered() {
        this.next = new AtomicReference<>(null);
        this.isBusy = new AtomicBoolean(false);
    }

    /**
     * Inserts a new element in the interval list
     *
     * @param root    The root element
     * @param element The element to insert
     */
    public static void insert(AtomicReference<IOAccessOrdered> root, IOAccessOrdered element) {
        element.isBusy.set(true);
        while (true) {
            if (insertFrom(root, element))
                break;
        }
        element.isBusy.set(false);
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
        IOAccessOrdered insertBefore = root.get();
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
                // or there is no next element and the insertAfter has still not be set (will be inserted as last)
                insertAfter = current;
                insertBefore = next;
            }
            current = next;
        }
        // do the insert
        element.next.set(insertBefore);
        if (insertAfter == null)
            return insertRoot(root, element, insertBefore);
        return insertAfter(insertAfter, element, insertBefore);
    }

    /**
     * Tries to insert the element as the root
     *
     * @param root     The root to insert from
     * @param toInsert The element to insert
     * @param before   The element before which to insert
     * @return Whether the operation succeeded
     */
    private static boolean insertRoot(AtomicReference<IOAccessOrdered> root, IOAccessOrdered toInsert, IOAccessOrdered before) {
        if (before != null) {
            if (!before.isBusy.compareAndSet(false, true))
                return false;
        }
        boolean success = root.compareAndSet(before, toInsert);
        if (before != null)
            before.isBusy.set(false);
        return success;
    }

    /**
     * Tries to insert the element after the current one
     *
     * @param current  The current element in the list to insert after
     * @param toInsert The element to insert
     * @param before   The element before which to insert
     * @return Whether the operation succeeded
     */
    private static boolean insertAfter(IOAccessOrdered current, IOAccessOrdered toInsert, IOAccessOrdered before) {
        if (!current.isBusy.compareAndSet(false, true))
            return false;
        if (before != null) {
            if (!before.isBusy.compareAndSet(false, true))
                return false;
        }
        boolean success = current.next.compareAndSet(before, toInsert);
        if (before != null)
            before.isBusy.set(false);
        current.isBusy.set(false);
        return success;
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
        element.isBusy.set(false);
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
        if (root.get() == element)
            return removeRoot(root, element);
        // not the first one, go down the list
        IOAccessOrdered current = root.get();
        while (current != null) {
            IOAccessOrdered next = current.next.get();
            if (next == element)
                return removeFrom(current, next);
            current = next;
        }
        throw new Error("WTF");
    }

    /**
     * Tries to remove the root element
     *
     * @param root    The root element
     * @param element The element to remove
     * @return Whether the operation succeeded
     */
    private static boolean removeRoot(AtomicReference<IOAccessOrdered> root, IOAccessOrdered element) {
        if (!element.isBusy.compareAndSet(false, true))
            return false;
        IOAccessOrdered next = element.next.get();
        if (next != null) {
            if (!next.isBusy.compareAndSet(false, true)) {
                element.isBusy.set(false);
                return false;
            }
        }
        boolean success = root.compareAndSet(element, next);
        if (next != null)
            next.isBusy.set(false);
        element.isBusy.set(false);
        return success;
    }

    /**
     * Tries to remove an element from the interval list
     *
     * @param previous The element before the one to delete
     * @param toDelete The element to delete
     * @return Whether the operation succeeded
     */
    private static boolean removeFrom(IOAccessOrdered previous, IOAccessOrdered toDelete) {
        if (!toDelete.isBusy.compareAndSet(false, true))
            return false;
        if (!previous.isBusy.compareAndSet(false, true)) {
            toDelete.isBusy.set(false);
            return false;
        }
        IOAccessOrdered next = toDelete.next.get();
        if (next != null) {
            if (!next.isBusy.compareAndSet(false, true)) {
                previous.isBusy.set(false);
                toDelete.isBusy.set(false);
                return false;
            }
        }
        boolean success = previous.next.compareAndSet(toDelete, next);
        if (next != null)
            next.isBusy.set(false);
        previous.isBusy.set(false);
        toDelete.isBusy.set(false);
        return success;
    }
}
