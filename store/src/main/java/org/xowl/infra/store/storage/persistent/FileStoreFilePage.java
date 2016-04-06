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

/**
 * Represents a page of data in a persisted binary store
 * <p/>
 * Page general layout:
 * - header
 * - entry array (fill down from just after the header)
 * - ... free space
 * - data content (fill up from the bottom of the page)
 * <p/>
 * Header layout:
 * - Layout version (2 bytes)
 * - Flags (2 bytes)
 * - Number of entries (2 bytes)
 * - Offset to start of free space (2 bytes)
 * - Offset to start of data content (2 bytes)
 * <p/>
 * Entry layout:
 * - offset (2 bytes)
 * - length (2 bytes)
 *
 * @author Laurent Wouters
 */
class FileStoreFilePage {
    /**
     * The version of the page layout to use
     */
    private static final char PAGE_LAYOUT_VERSION = 1;
    /**
     * The size of the page header in bytes
     * char: Layout version (2 bytes)
     * char: Flags (2 bytes)
     * char: Number of entries (2 bytes)
     * char: Offset to start of free space (2 bytes)
     * char: Offset to start of data content (2 bytes)
     */
    private static final int PAGE_HEADER_SIZE = 2 + 2 + 2 + 2 + 2;
    /**
     * The size of an entry in the entry table of a page (in bytes)
     * char: offset (2 bytes)
     * char: length (2 bytes)
     */
    private static final int PAGE_ENTRY_INDEX_SIZE = 2 + 2;

    /**
     * Flag whether the page shall reuse the space of removed entries
     */
    private static final char FLAG_REUSE_EMPTY_ENTRIES = 0x0001;

    /**
     * The maximum size of the payload of an entry in a page
     */
    public static final int MAX_ENTRY_SIZE = BLOCK_SIZE - PAGE_HEADER_SIZE - PAGE_ENTRY_INDEX_SIZE;
    /**
     * The number of bytes required in addition to an entry's payload
     */
    public static final int ENTRY_OVERHEAD = PAGE_ENTRY_INDEX_SIZE;

    /**
     * The current flags
     */
    private char flags;
    /**
     * The number of entries in this page
     */
    private char entryCount;
    /**
     * The offset to the start of the free space
     */
    private char startFreeSpace;
    /**
     * The offset to the start of the data content
     */
    private char startData;

    @Override
    protected void onLoaded() throws StorageException {
        char version = readChar(0);
        if (version == 0) {
            // this is a new page
            flags = FLAG_REUSE_EMPTY_ENTRIES;
            startFreeSpace = PAGE_HEADER_SIZE;
            startData = (char) BLOCK_SIZE;
        } else if (version != PAGE_LAYOUT_VERSION) {
            throw new StorageException("Invalid page layout version " + Integer.toHexString(version) + ", expected " + Integer.toHexString(PAGE_LAYOUT_VERSION));
        } else {
            flags = readChar(2);
            entryCount = readChar(4);
            startFreeSpace = readChar(6);
            startData = readChar(8);
        }
    }

    /**
     * Gets the amount of free space for entry payloads in this page
     *
     * @param pool The transaction pool to use
     * @param time The current time
     * @return The amount of free space
     * @throws StorageException When an IO operation failed
     */
    public int getFreeSpace(IOTransationPool pool, long time) throws StorageException {
        int result = 0;
        if ((flags & FLAG_REUSE_EMPTY_ENTRIES) == FLAG_REUSE_EMPTY_ENTRIES && entryCount > (startFreeSpace - PAGE_HEADER_SIZE) >>> 2) {
            // we can reuse empty entries and there are at least one
            try (IOTransaction transaction = pool.begin(this, 0, BLOCK_SIZE, false, time)) {
                if (transaction != null) {
                    char entryIndex = 0;
                    transaction.seek(PAGE_HEADER_SIZE);
                    while (entryIndex * PAGE_ENTRY_INDEX_SIZE + PAGE_HEADER_SIZE < startFreeSpace) {
                        char eOffset = transaction.readChar();
                        char eLength = transaction.readChar();
                        if (eOffset == 0) {
                            result += eLength;
                        }
                    }
                    return result;
                }
            }
        }
        result += startData - startFreeSpace - PAGE_ENTRY_INDEX_SIZE;
        return result;
    }

    /**
     * Gets whether this page can store a content of the specified size
     *
     * @param pool The transaction pool to use
     * @param time The current time
     * @param length The length of the content to store
     * @return true if the content can be stored
     * @throws StorageException When an IO operation failed
     */
    public boolean canStore(IOTransationPool pool, long time, int length) throws StorageException {
        if (length > MAX_ENTRY_SIZE)
            return false;
        if ((flags & FLAG_REUSE_EMPTY_ENTRIES) == FLAG_REUSE_EMPTY_ENTRIES && entryCount > (startFreeSpace - PAGE_HEADER_SIZE) >>> 2) {
            // we can reuse empty entries and there are at least one
            try (IOTransaction transaction = pool.begin(this, 0, BLOCK_SIZE, false, time)) {
                if (transaction != null) {
                    char entryIndex = 0;
                    transaction.seek(PAGE_HEADER_SIZE);
                    while (entryIndex * PAGE_ENTRY_INDEX_SIZE + PAGE_HEADER_SIZE < startFreeSpace) {
                        char eOffset = transaction.readChar();
                        char eLength = transaction.readChar();
                        if (eOffset == 0 && eLength >= length) {
                            // reuse this entry
                            return true;
                        }
                    }
                }
            }
            // no suitable empty entry
        }
        return (startData - startFreeSpace - length - PAGE_ENTRY_INDEX_SIZE >= 0);
    }

    /**
     * Register an entry of the specified length and places the backend index at the appropriate position for writing
     *
     * @param length The length of the entry to register
     * @return The key to be used to retrieve the data
     * @throws StorageException When an IO operation failed
     */
    public int registerEntry(int length) throws StorageException {
        if ((flags & FLAG_REUSE_EMPTY_ENTRIES) == FLAG_REUSE_EMPTY_ENTRIES && entryCount > (startFreeSpace - PAGE_HEADER_SIZE) >>> 2) {
            // we can reuse empty entries and there are at least one
            char entryIndex = 0;
            char dataOffset = (char) FileStoreFile.BLOCK_SIZE;
            backend.seek(location + PAGE_HEADER_SIZE);
            while (entryIndex * PAGE_ENTRY_INDEX_SIZE + PAGE_HEADER_SIZE < startFreeSpace) {
                char eOffset = backend.readChar();
                char eLength = backend.readChar();
                if (eOffset == 0 && eLength >= length) {
                    // reuse this entry
                    return overwriteEmptyEntry(entryIndex, (char) (dataOffset - eLength));
                }
                dataOffset -= eLength;
            }
            // no suitable empty entry
        }
        if (startData - startFreeSpace - length - PAGE_ENTRY_INDEX_SIZE < 0)
            throw new StorageException("Cannot store an entry of the specified size");
        return writeNewEntry(length);
    }

    /**
     * Overwrites an empty entry
     *
     * @param entryIndex The entry's index
     * @param dataOffset The data offset to overwrite with
     * @return The key to be used to retrieve the data
     * @throws StorageException When an IO operation failed
     */
    private int overwriteEmptyEntry(int entryIndex, char dataOffset) throws StorageException {
        // compute the entry data
        int key = keyRadical + entryIndex;
        // write the entry
        backend.seek(location + entryIndex * PAGE_ENTRY_INDEX_SIZE);
        backend.writeChar(dataOffset);
        // update the header data
        entryCount++;
        // position on to the data location
        backend.seek(location + dataOffset);
        // return the key
        return key;
    }

    /**
     * Writes a new entry for the specified length
     *
     * @param length The length of the entry to register
     * @return The key to be used to retrieve the data
     * @throws StorageException When an IO operation failed
     */
    private int writeNewEntry(int length) throws StorageException {
        // compute the entry data
        int key = keyRadical + (int) entryCount;
        long dataLocation = location + startData - length;
        // write the entry
        backend.seek(location + startFreeSpace);
        backend.writeChar((char) (startData - length));
        backend.writeChar((char) length);
        // update the header data
        entryCount++;
        startFreeSpace += PAGE_ENTRY_INDEX_SIZE;
        startData -= length;
        // position on to the data location
        backend.seek(dataLocation);
        // return the key
        return key;
    }

    /**
     * Removes the entry identified by the specified key
     *
     * @param key The key to an entry
     * @return The length of the removed entry
     * @throws StorageException When an IO operation failed
     */
    public int removeEntry(int key) throws StorageException {
        int entryIndex = key - keyRadical;
        if (entryIndex < 0 || entryIndex >= (startFreeSpace - PAGE_HEADER_SIZE) >>> 2)
            throw new StorageException("The entry for the specified key is not in this page");
        backend.seek(location + entryIndex * PAGE_ENTRY_INDEX_SIZE + PAGE_HEADER_SIZE);
        char offset = backend.readChar();
        char length = backend.readChar();
        if (offset == 0)
            throw new StorageException("The entry for the specified key has already been removed");
        if (offset == startData) {
            // this is the last entry in this page
            startFreeSpace -= PAGE_ENTRY_INDEX_SIZE;
            startData += length;
            entryCount--;
            // go to the previous entry en get its info
            entryIndex--;
            backend.seek(location + entryIndex * PAGE_ENTRY_INDEX_SIZE + PAGE_HEADER_SIZE);
            offset = backend.readChar();
            length = backend.readChar();
            // while the entry is empty (and this is an actual entry)
            while (offset == 0 && entryIndex >= 0) {
                // remove it completely because this is the last one
                startFreeSpace -= PAGE_ENTRY_INDEX_SIZE;
                startData += length;
                entryCount--;
                // go to the previous entry
                entryIndex--;
                // is is a valid entry?
                if (entryIndex < 0)
                    break;
                // get its info
                backend.seek(location + entryIndex * PAGE_ENTRY_INDEX_SIZE + PAGE_HEADER_SIZE);
                offset = backend.readChar();
                length = backend.readChar();
            }
        } else {
            // simply marks this entry as empty by erasing the offset
            backend.seek(location + entryIndex * PAGE_ENTRY_INDEX_SIZE + PAGE_HEADER_SIZE);
            backend.writeChar('\0');
        }
        return length;
    }

    /**
     * Positions the backend for reading the entry identified by the specified key
     *
     * @param key The key identifying the data
     * @return The length of the entry
     * @throws StorageException When an IO operation failed
     */
    public int positionFor(int key) throws StorageException {
        int entryIndex = key - keyRadical;
        if (entryIndex < 0 || entryIndex >= (startFreeSpace - PAGE_HEADER_SIZE) >>> 2)
            throw new StorageException("The entry for the specified key is not in this page");
        backend.seek(location + entryIndex * PAGE_ENTRY_INDEX_SIZE + PAGE_HEADER_SIZE);
        char offset = backend.readChar();
        char length = backend.readChar();
        if (offset == 0)
            throw new StorageException("The entry for the specified key has already been removed");
        backend.seek(location + offset);
        return length;
    }

    /**
     * When this page is going to be committed
     * Writes the page's header to the backend
     *
     * @return true if the cached data was successfully written back to the file
     */
    public boolean onCommit() {
        try {
            backend.seek(location);
            backend.writeChar(PAGE_LAYOUT_VERSION);
            backend.writeChar(flags);
            backend.writeChar(entryCount);
            backend.writeChar(startFreeSpace);
            backend.writeChar(startData);
            return true;
        } catch (StorageException exception) {
            return false;
        }
    }
}
