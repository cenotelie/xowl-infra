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

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Represents a controlled access to an IO backend that can be checked for exclusive use
 *
 * @author Laurent Wouters
 */
abstract class IOAccessOrdered extends IOAccess {
    /**
     * The special value of the next index when no next value
     */
    private static final int INDEX_NULL = -1;
    /**
     * The special value of the next index when representing a hazard
     */
    private static final int INDEX_HAZARD = -2;

    /**
     * The value representing a null stamped reference
     */
    private static final long REF_NULL = 0x00000000FFFFFFFFL;

    /**
     * The index of this access in its pool
     */
    private final int index;
    /**
     * The next access element, a stamped reference composed of:
     * - int: index of the next access in the pool
     * - int: the reference's stamp
     */
    private final AtomicLong next;
    /**
     * The next free element in the pool
     */
    private final AtomicReference<IOAccessOrdered> poolNext;

    /**
     * Initializes this element
     *
     * @param index The index of this access in its pool
     */
    protected IOAccessOrdered(int index) {
        this.index = index;
        this.next = new AtomicLong(REF_NULL);
        this.poolNext = new AtomicReference<>(null);
    }

    /**
     * Inserts a new element into a pool
     *
     * @param poolFirst The reference to the next free element
     * @param element   The element to insert in the pool
     */
    public static void poolInsert(AtomicReference<IOAccessOrdered> poolFirst, IOAccessOrdered element) {
        while (true) {
            // get the current first free element
            IOAccessOrdered previous = poolFirst.get();
            // set it as the next for the current element to insert
            element.poolNext.set(previous);
            // atomically replace the old head by the current element to insert
            if (poolFirst.compareAndSet(previous, element))
                return;
        }
    }

    /**
     * Gets the next free element from a pool (or null if there is none)
     *
     * @param poolFirst The reference to the next free element
     * @return The next free element from the pool (or null if there is none)
     */
    public static IOAccessOrdered poolGet(AtomicReference<IOAccessOrdered> poolFirst) {
        while (true) {
            // get the current first free element
            IOAccessOrdered result = poolFirst.get();
            if (result == null)
                // there is none, return null
                return null;
            // get the second free element
            IOAccessOrdered next = result.poolNext.get();
            if (poolFirst.compareAndSet(result, next))
                return result;
        }
    }

    /**
     * Inserts a new element in the interval list
     *
     * @param pool    The pool of accesses
     * @param root    The root element
     * @param element The element to insert
     * @return The number of tries it took
     */
    public static int insert(IOAccessOrdered[] pool, AtomicReference<IOAccessOrdered> root, IOAccessOrdered element) {
        int tries = 1;
        while (true) {
            if (insertFrom(pool, root, element))
                return tries;
            tries++;
        }
    }

    /**
     * Tries to insert an element in the interval list
     * This method may fail due to concurrent operations in the list.
     *
     * @param pool    The pool of accesses
     * @param root    The root to insert from
     * @param element The element to insert
     * @return Whether the operation succeeded
     */
    private static boolean insertFrom(IOAccessOrdered[] pool, AtomicReference<IOAccessOrdered> root, IOAccessOrdered element) {
        IOAccessOrdered current = root.get();
        long currentNextRef;
        IOAccessOrdered insertAfter = null;
        long insertAfterNextRef = REF_NULL;
        IOAccessOrdered insertBefore = current;

        while (current != null) {
            // get the next access
            currentNextRef = current.next.get();
            int currentNextIndex = getReferenceIndex(currentNextRef);
            if (currentNextIndex == INDEX_HAZARD)
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
            if (currentNextIndex == INDEX_NULL) {
                // no next access element
                if (insertAfter == null) {
                    // there is no next element and the insertAfter has still not be set (will be inserted as last)
                    insertAfter = current;
                    insertAfterNextRef = currentNextRef;
                    insertBefore = null;
                }
                current = null;
            } else {
                // there is a next
                IOAccessOrdered next = pool[currentNextIndex];
                if (element.location < next.location) {
                    // the element to insert must be before the next one
                    insertAfter = current;
                    insertAfterNextRef = currentNextRef;
                    insertBefore = next;
                }
                current = next;
            }
        }

        // do the insert
        // setup the element to insert
        element.next.set(getUpdatedStampedReference(element.next.get(), insertBefore));
        // no element to insert after?
        if (insertAfter == null)
            // insert as root
            return root.compareAndSet(current, element);
        // insert after the selected element
        return insertAfter.next.compareAndSet(insertAfterNextRef, getUpdatedStampedReference(insertAfterNextRef, element));
    }

    /**
     * Removes an element from the interval list
     *
     * @param pool    The pool of accesses
     * @param root    The root element
     * @param element The element to remove
     * @return The number of tries it took
     */
    public static int remove(IOAccessOrdered[] pool, AtomicReference<IOAccessOrdered> root, IOAccessOrdered element) {
        int tries = 1;
        while (true) {
            if (removeFrom(pool, root, element))
                return tries;
            tries++;
        }
    }

    /**
     * Tries to removes an element from the interval list
     * This method may fail due to concurrent operations in the list.
     *
     * @param pool    The pool of accesses
     * @param root    The root element
     * @param element The element to remove
     * @return Whether the operation succeeded
     */
    private static boolean removeFrom(IOAccessOrdered[] pool, AtomicReference<IOAccessOrdered> root, IOAccessOrdered element) {
        IOAccessOrdered current = root.get();

        // is the element to remove the root?
        if (current == element) {
            long nextRef = element.next.get();
            int nextIndex = getReferenceIndex(nextRef);
            // mark the element for delete
            long hazard = getUpdatedStampedReference(nextRef, INDEX_HAZARD);
            if (!element.next.compareAndSet(nextRef, hazard))
                // concurrent insertion after this element
                return false;
            // replace the root
            IOAccessOrdered next = nextIndex == INDEX_NULL ? null : pool[nextIndex];
            if (root.compareAndSet(element, next))
                // success!
                return true;
            // failed to replace, current insertion at the root
            // un-mark the node and retry
            element.next.compareAndSet(hazard, nextRef);
            return false;
        }

        // not the first one, go down the list
        while (current != null) {
            long nextRef = current.next.get();
            int nextIndex = getReferenceIndex(nextRef);
            if (nextIndex == INDEX_HAZARD)
                // this is a concurrent delete ...
                return false;
            IOAccessOrdered next = nextIndex == INDEX_NULL ? null : pool[nextIndex];
            if (next == element) {
                // the next element is the one to be deleted
                long elementNextRef = element.next.get();
                int elementNextIndex = getReferenceIndex(elementNextRef);
                IOAccessOrdered elementNext = elementNextIndex == INDEX_NULL ? null : pool[elementNextIndex];
                // mark the element for delete
                long hazard = getUpdatedStampedReference(elementNextRef, INDEX_HAZARD);
                if (!element.next.compareAndSet(elementNextRef, hazard))
                    // concurrent insertion after this element
                    return false;
                // attempt to delete
                if (current.next.compareAndSet(nextRef, getUpdatedStampedReference(nextRef, elementNext)))
                    // success!
                    return true;
                // failed to replace, current insertion at the root
                // un-mark the node and retry
                element.next.compareAndSet(hazard, elementNextRef);
                return false;
            }
            current = next;
        }
        throw new Error("WTF!!");
    }

    /**
     * Gets the updated stamped reference
     *
     * @param previous The previous value of the stamped reference
     * @param newValue The new value for the referenced access
     * @return The updated stamped reference value
     */
    private static long getUpdatedStampedReference(long previous, IOAccessOrdered newValue) {
        return getUpdatedStampedReference(previous, newValue != null ? newValue.index : INDEX_NULL);
    }

    /**
     * Gets the updated stamped reference
     *
     * @param previous The previous value of the stamped reference
     * @param index    The updated index for the reference
     * @return The updated stamped reference val
     */
    private static long getUpdatedStampedReference(long previous, int index) {
        long stamp = (previous >>> 32);
        stamp++;
        if (stamp > Integer.MAX_VALUE)
            stamp = 0;
        return (stamp << 32) | (0xFFFFFFFFL & (index));
    }

    /**
     * Gets the index in the pool of the next access for the specified stamped reference
     *
     * @param reference A stamped reference
     * @return The index of the next access
     */
    private static int getReferenceIndex(long reference) {
        return (int) (reference & 0xFFFFFFFFL);
    }
}
