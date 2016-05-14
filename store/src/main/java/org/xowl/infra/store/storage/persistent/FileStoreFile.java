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

import java.io.File;

/**
 * Represents a persisted binary file
 * A file is composed of blocks (or pages)
 * The first block has a special structure with the following layout
 * - int32: Magic identifier for the store
 * - int32: Layout version
 * - int32: Number of open blocks (blocks that contain data but are not full)
 * - int32: Index of the next block to open (in number of block)
 * - array of block entries:
 * - char: Index of the block
 * - char: Remaining free space
 * <p>
 * This structure is thread-safe and uses a lock-free synchronization scheme.
 * When IO operations consists of reading, writing and removing entries in pages, this structure ensures the consistency of the book-keeping data.
 *
 * @author Laurent Wouters
 */
class FileStoreFile extends FileBackend {
    /**
     * Magic identifier of the type of store
     */
    private static final int FILE_MAGIC_ID = 0x784F574C;
    /**
     * The layout version
     */
    private static final int FILE_LAYOUT_VERSION = 1;
    /**
     * The size of the header in the preambule block
     * int: Magic identifier for the store
     * int: Layout version
     * char: Number of open blocks (blocks that contain data but are not full)
     * char: Index of the next block to open (in number of block)
     */
    private static final int FILE_PREAMBULE_HEADER_SIZE = 4 + 4 + 2 + 2;
    /**
     * The size of an open block entry in the preambule
     * char: Index of the block
     * char: Remaining free space
     */
    private static final int FILE_PREAMBULE_ENTRY_SIZE = 2 + 2;
    /**
     * The number of remaining bytes in a block below which it is considered as full
     */
    private static final int BLOCK_FULL_THRESHOLD_SIZE = 100;
    /**
     * The maximum number of open blocks in a file
     */
    private static final int FILE_MAX_OPEN_BLOCKS = (FileBlock.BLOCK_SIZE - FILE_PREAMBULE_HEADER_SIZE) / FILE_PREAMBULE_ENTRY_SIZE;
    /**
     * The maximum number of blocks per file
     */
    private static final int FILE_MAX_BLOCKS = 1 << 16;
    /**
     * The maximum number of loaded blocks
     */
    private static final int FILE_MAX_LOADED_BLOCKS = 256;
    /**
     * The mask for the index of a block
     */
    private static final long INDEX_MASK_UPPER = ~FileBlock.INDEX_MASK_LOWER;


    /**
     * Initializes this data file
     *
     * @param file       The file location
     * @param isReadonly Whether this store is in readonly mode
     * @param noInit     Whether to skip the file initialization
     * @throws StorageException When the initialization failed
     */
    public FileStoreFile(File file, boolean isReadonly, boolean noInit) throws StorageException {
        super(file, isReadonly);
        if (!noInit)
            initialize(isReadonly, file.getAbsolutePath());
    }

    /**
     * Initializes this file
     *
     * @param isReadonly Whether this store is in readonly mode
     * @param fileName   The name of the represented file
     * @throws StorageException When an IO error occurred
     */
    private void initialize(boolean isReadonly, String fileName) throws StorageException {
        if (getSize() == 0) {
            if (isReadonly)
                throw new StorageException("FileBackend is empty but open as read-only: " + fileName);
            // the file is empty and not read-only
            try (IOAccess access = accessRaw(0, FILE_PREAMBULE_HEADER_SIZE, true)) {
                access.writeInt(FILE_MAGIC_ID);
                access.writeInt(FILE_LAYOUT_VERSION);
                access.writeChar((char) 0);
                access.writeChar((char) 1);
            }
            flush();
        } else {
            // file is not empty, verify the header
            try (IOAccess access = accessRaw(0, FILE_PREAMBULE_HEADER_SIZE, false)) {
                int magic = access.readInt();
                if (magic != FILE_MAGIC_ID)
                    throw new StorageException("Invalid file header, expected 0x" + Integer.toHexString(FILE_MAGIC_ID) + ", got 0x" + Integer.toHexString(magic) + ": " + fileName);
                int layout = access.readInt();
                if (layout != FILE_LAYOUT_VERSION)
                    throw new StorageException("Invalid file layout, expected 0x" + Integer.toHexString(FILE_LAYOUT_VERSION) + ", got 0x" + Integer.toHexString(layout) + ": " + fileName);
            }
        }
    }

    /**
     * Tries to allocate an entry of the specified size in this file
     *
     * @param entrySize The size of the entry
     * @return The key to the entry, or -1 if it cannot be allocated
     * @throws StorageException When an IO operation failed
     */
    public int allocateEntry(int entrySize) throws StorageException {
        if (entrySize > FileStoreFileBlock.MAX_ENTRY_SIZE)
            throw new StorageException("Entry is too big (" + entrySize + "), max is " + FileStoreFileBlock.MAX_ENTRY_SIZE);

        try (IOAccess access = accessRaw(0, FileStoreFileBlock.BLOCK_SIZE, true)) {
            access.seek(8);
            int openBlockCount = access.readChar();
            int nextFreeBlock = access.readChar();
            int inspected = 0;
            for (int i = 0; i != FILE_MAX_OPEN_BLOCKS; i++) {
                if (inspected >= openBlockCount)
                    break;
                char blockIndex = access.readChar();
                char blockRemaining = access.readChar();
                if (blockIndex == 0)
                    // this is a free block slot
                    continue;
                inspected++;
                if (blockRemaining >= entrySize + FileStoreFileBlock.PAGE_ENTRY_INDEX_SIZE) {
                    // the entry could fit in the page
                    int key = pageTryStoreEntry(blockIndex, entrySize);
                    if (key == -1)
                        continue;
                    blockRemaining -= entrySize;
                    blockRemaining -= FileStoreFileBlock.PAGE_ENTRY_INDEX_SIZE;
                    if (blockRemaining >= BLOCK_FULL_THRESHOLD_SIZE) {
                        access.seek(FILE_PREAMBULE_HEADER_SIZE + i * FILE_PREAMBULE_ENTRY_SIZE + 2);
                        access.writeChar(blockRemaining);
                    } else {
                        access.seek(FILE_PREAMBULE_HEADER_SIZE + i * FILE_PREAMBULE_ENTRY_SIZE);
                        access.writeChar('\0');
                        access.writeChar('\0');
                        access.seek(8).writeChar((char) (openBlockCount - 1));
                    }
                    return key;
                }
            }

            // cannot fit in an open block
            if (nextFreeBlock >= FILE_MAX_BLOCKS)
                return -1;
            if (!pageInitialize(nextFreeBlock))
                return -1;
            int key = pageTryStoreEntry(nextFreeBlock, entrySize);
            if (key == -1)
                return -1;
            int remaining = FileStoreFileBlock.MAX_ENTRY_SIZE - entrySize - FileStoreFileBlock.PAGE_ENTRY_INDEX_SIZE;
            pageMarkOpen(access, nextFreeBlock, remaining);
            access.seek(8 + 2).writeChar((char) (nextFreeBlock + 1));
            return key;
        }
    }

    /**
     * Clears an entry from this file
     *
     * @param key The file-local key to the entry to remove
     * @throws StorageException When an IO operation failed
     */
    public void removeEntry(int key) throws StorageException {
        long blockLocation = keyPageLocation(key);
        int entryIndex = keyEntryIndex(key);
        if (blockLocation >= size.get() // the block is not allocated
                || blockLocation <= 0 // cannot have entries in the first block
                || entryIndex < 0) // invalid entry index
            throw new StorageException("Invalid key (0x" + Integer.toHexString(key) + ")");

        int remaining = pageRemoveEntry(blockLocation, entryIndex);
        if (remaining == -1)
            return;
        try (IOAccess access = accessRaw(0, FILE_PREAMBULE_HEADER_SIZE, true)) {
            pageMarkOpen(access, keyPageIndex(key), remaining);
        }
    }

    /**
     * Marks a page as open
     *
     * @param access    The current access
     * @param pageIndex The page to mark as open
     * @param remaining The remaining space in the page
     * @throws StorageException When an IO operation failed
     */
    private void pageMarkOpen(IOAccess access, int pageIndex, int remaining) throws StorageException {
        int openBlockCount = access.seek(8).readChar();
        access.readChar();
        int inspected = 0;
        for (int i = 0; i != FILE_MAX_OPEN_BLOCKS; i++) {
            if (inspected >= openBlockCount)
                break;
            char blockIndex = access.readChar();
            if (blockIndex > 0)
                inspected++;
            if (blockIndex == pageIndex) {
                // already open
                access.writeChar((char) remaining);
                return;
            }
            access.readChar();
        }
        // the block was not open
        if (openBlockCount < FILE_MAX_OPEN_BLOCKS) {
            if (remaining >= BLOCK_FULL_THRESHOLD_SIZE) {
                access.seek(FILE_PREAMBULE_HEADER_SIZE + openBlockCount * FILE_PREAMBULE_ENTRY_SIZE);
                access.writeChar((char) pageIndex);
                access.writeChar((char) remaining);
                access.seek(8).writeChar((char) (openBlockCount + 1));
            }
        }
    }

    /**
     * Initializes a page
     *
     * @param pageIndex The index of the page to initialize
     * @return Whether the operation succeeded
     * @throws StorageException When an IO operation failed
     */
    private boolean pageInitialize(int pageIndex) throws StorageException {
        try (IOAccess access = accessRaw(pageIndex << FileStoreFileBlock.BLOCK_INDEX_LENGTH, FileStoreFileBlock.PAGE_HEADER_SIZE, true)) {
            access.writeChar('\0');
            access.writeChar((char) FileStoreFileBlock.MAX_ENTRY_SIZE);
            access.writeChar((char) FileStoreFileBlock.PAGE_HEADER_SIZE);
            access.writeChar((char) FileStoreFileBlock.BLOCK_SIZE);
            return true;
        }
    }

    /**
     * Tries to store an entry of the specified size in the given page
     *
     * @param pageIndex The index of the page
     * @param length    The length of the entry to register
     * @return The bit field composed of: uint16 the remaining max size, and uint16 the entry index in the block; or -1 if the operation fails
     * The key to be used to retrieve the data, or -1 if the entry cannot be allocated
     * @throws StorageException When an IO operation failed
     */
    private int pageTryStoreEntry(int pageIndex, int length) throws StorageException {
        long blockLocation = pageIndex << FileStoreFileBlock.BLOCK_INDEX_LENGTH;
        try (FileStoreFileBlock block = getBlockFor(blockLocation)) {
            while (true) {
                // get block data
                char entryCount;
                char maxSize;
                char startFreeSpace;
                char startData;
                try (IOAccess access = accessRaw(blockLocation, FileStoreFileBlock.PAGE_HEADER_SIZE, false, block)) {
                    // gather the block data
                    entryCount = access.readChar();
                    maxSize = access.readChar();
                    startFreeSpace = access.readChar();
                    startData = access.readChar();
                }
                // can store?
                if (maxSize < length)
                    // cannot store in this block anymore
                    return -1;
                // look for an empty entry
                if (entryCount > 0) {
                    int selectedIndex = -1;
                    try (IOAccess access = accessRaw(blockLocation + FileStoreFileBlock.PAGE_HEADER_SIZE, entryCount * FileStoreFileBlock.PAGE_ENTRY_INDEX_SIZE, false, block)) {
                        access.setBackward(true);
                        access.seek(entryCount * FileStoreFileBlock.PAGE_ENTRY_INDEX_SIZE - 2);
                        int currentOffset = FileStoreFileBlock.BLOCK_SIZE;
                        for (int i = entryCount - 1; i != -1; i++) {
                            char entryLength = access.readChar();
                            char entryOffset = access.readChar();
                            currentOffset -= entryLength;
                            if (entryOffset == 0 && entryLength) {
                                int result = pageTryReuseEntry(block, i, currentOffset, length);
                                if (result != -1)
                                    return result;
                            }
                        }
                    }
                }
                // store in a new entry
                if (startData - startFreeSpace < length + FileStoreFileBlock.PAGE_ENTRY_INDEX_SIZE)
                    // not enough space for new entry
                    continue;
                int result = pageTryAllocateFromFreeSpace(block, length);
                if (result != -1)
                    return result;
            }
        }
    }

    private int pageTryReuseEntry(FileStoreFileBlock block, int entryIndex, int offset, int length) throws StorageException {
        try (IOAccess access = accessRaw(block.getLocation() + FileStoreFileBlock.PAGE_HEADER_SIZE + entryIndex * FileStoreFileBlock.PAGE_ENTRY_INDEX_SIZE, 4, true, block)) {
            char entryOffset = access.readChar();
            char entryLength = access.readChar();
            if (entryOffset != 0 || entryLength < length)
                // already reused ...
                return -1;
            access.reset();
            access.writeChar((char) offset);
            access.writeChar();
        }
    }

    private int pageTryAllocateFromFreeSpace(FileStoreFileBlock block, int length) throws StorageException {

    }

    private int pageRecomputeFreeSpace(FileStoreFileBlock block) throws StorageException {

    }

    /**
     * Removes an entry for a page
     *
     * @param pageLocation The location of the page
     * @param entryIndex   The index within the page of the entry to remove
     * @return The remaining free space in the page
     * @throws StorageException When an IO operation failed
     */
    private int pageRemoveEntry(long pageLocation, int entryIndex) throws StorageException {
        try (IOAccess access = accessRaw(pageLocation, FileStoreFileBlock.BLOCK_SIZE, true)) {
            char entryCount = access.seek(4).readChar();
            char startFreeSpace = access.readChar();
            char startData = access.readChar();
            if (entryIndex >= (startFreeSpace - FileStoreFileBlock.PAGE_HEADER_SIZE) >>> 2)
                throw new StorageException("The entry for the specified key is not in this page");
            access.seek(FileStoreFileBlock.PAGE_HEADER_SIZE + entryIndex * FileStoreFileBlock.PAGE_ENTRY_INDEX_SIZE);
            char offset = access.readChar();
            char length = access.readChar();
            if (offset == 0 || length == 0)
                throw new StorageException("The entry for the specified key has been removed");
            if (offset < startData) {
                // not the last entry in this page
                // simply marks this entry as empty by erasing the offset
                access.seek(FileStoreFileBlock.PAGE_HEADER_SIZE + entryIndex * FileStoreFileBlock.PAGE_ENTRY_INDEX_SIZE);
                access.writeChar('\0');
            } else {
                // here this is the last entry in this page
                do {
                    startFreeSpace -= FileStoreFileBlock.PAGE_ENTRY_INDEX_SIZE;
                    startData += length;
                    entryCount--;
                    // go to the previous entry and get its info
                    entryIndex--;
                    access.seek(FileStoreFileBlock.PAGE_HEADER_SIZE + entryIndex * FileStoreFileBlock.PAGE_ENTRY_INDEX_SIZE);
                    offset = access.readChar();
                    length = access.readChar();
                } while (offset == 0 && entryIndex >= 0);
                access.seek(4).writeChar(entryCount);
                access.writeChar(startFreeSpace);
                access.writeChar(startData);
            }
            // compute the remaining free space
            int remainingSpace = 0;
            access.seek(FileStoreFileBlock.PAGE_HEADER_SIZE);
            for (int i = 0; i != entryCount; i++) {
                char eOffset = access.readChar();
                char eLength = access.readChar();
                if (eOffset == 0)
                    remainingSpace += eLength;
            }
            remainingSpace += startData - startFreeSpace;
            return remainingSpace;
        }
    }

    /**
     * Gets an access to the entry for the specified key
     *
     * @param key      The file-local key to an entry
     * @param writable Whether the access allows writing to the backend
     * @return The IO element that can be used for reading and writing
     * @throws StorageException When an IO operation failed
     */
    public IOAccess accessEntry(int key, boolean writable) throws StorageException {
        long blockLocation = keyPageLocation(key);
        int entryIndex = keyEntryIndex(key);
        if (blockLocation >= size.get() // the block is not allocated
                || blockLocation <= 0 // cannot have entries in the first block
                || entryIndex < 0) // invalid entry index
            throw new StorageException("Invalid key (0x" + Integer.toHexString(key) + ")");

        FileStoreFileBlock block = getBlockFor(blockLocation);
        long offset;
        long length;
        try (IOAccess access = accessManager.get(block, FileStoreFileBlock.PAGE_HEADER_SIZE + entryIndex * FileStoreFileBlock.PAGE_ENTRY_INDEX_SIZE, 4, false)) {
            if (access == null) {
                block.releaseShared();
                return null;
            }
            offset = access.readChar();
            length = access.readChar();
            block.useShared(blockLocation, tick());
        }
        if (offset == 0 || length == 0) {
            block.releaseShared();
            throw new StorageException("The entry for the specified key has been removed");
        }
        return accessManager.get(block, offset, length, !isReadonly && writable);
    }

    /**
     * Gets the location of the page referred to by the specified file-local key
     *
     * @param key The file-local key to an entry
     * @return The location (index within this file) of the corresponding page
     */
    private static long keyPageLocation(int key) {
        return ((long) keyPageIndex(key)) << FileBlock.BLOCK_INDEX_LENGTH;
    }

    /**
     * Gets the index of the page that stores the entry referred to by the specified file-local key
     *
     * @param key The file-local key to an entry
     * @return The index of the page that stores the entry
     */
    private static int keyPageIndex(int key) {
        return (key >>> 16);
    }

    /**
     * Gets the index of the entry referred to by the specified file-local key
     *
     * @param key The file-local key to an entry
     * @return The index of the entry within the associated block
     */
    private static int keyEntryIndex(int key) {
        return (key & 0xFFFF);
    }

    /**
     * Gets the file-local key to an entry
     *
     * @param pageIndex  The index of the page that stores the entry
     * @param entryIndex The index of the entry within the block
     * @return The file-local key
     */
    private static int keyLocal(int pageIndex, int entryIndex) {
        return (pageIndex << 16) | entryIndex;
    }
}
