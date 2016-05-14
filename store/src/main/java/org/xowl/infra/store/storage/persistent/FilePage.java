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

/**
 * Represents a block of contiguous data in a file that can be interpreted as a page of data
 * Page general layout:
 * - header
 * - entry array (fill down from just after the header)
 * - ... free space
 * - data content (fill up from the bottom of the page)
 * <p/>
 * Header layout:
 * - Number of entries (2 bytes)
 * - Max entry size for this page (2 bytes)
 * - Offset to start of free space (2 bytes)
 * - Offset to start of data content (2 bytes)
 * <p/>
 * Entry layout:
 * - offset (2 bytes)
 * - length (2 bytes)
 *
 * @author Laurent Wouters
 */
class FilePage extends FileBlockTS {
    /**
     * The size of the page header in bytes
     * char: Number of entries (2 bytes)
     * char: Max entry size for this page
     * char: Offset to start of free space (2 bytes)
     * char: Offset to start of data content (2 bytes)
     */
    public static final int PAGE_HEADER_SIZE = 2 + 2 + 2 + 2;
    /**
     * The size of an entry in the entry table of a page (in bytes)
     * char: offset (2 bytes)
     * char: length (2 bytes)
     */
    public static final int PAGE_ENTRY_INDEX_SIZE = 2 + 2;
    /**
     * The maximum size of the payload of an entry in a page
     */
    public static final int MAX_ENTRY_SIZE = BLOCK_SIZE - PAGE_HEADER_SIZE - PAGE_ENTRY_INDEX_SIZE;
}
