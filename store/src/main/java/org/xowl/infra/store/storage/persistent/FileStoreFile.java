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

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

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
class FileStoreFile implements Closeable {
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
    private static final int FILE_MAX_OPEN_BLOCKS = (FileStoreFileBlock.BLOCK_SIZE - FILE_PREAMBULE_HEADER_SIZE) / FILE_PREAMBULE_ENTRY_SIZE;
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
    private static final long INDEX_MASK_UPPER = ~FileStoreFileBlock.INDEX_MASK_LOWER;

    /**
     * The file name
     */
    private final String fileName;
    /**
     * Whether the file is in readonly mode
     */
    private final boolean isReadonly;
    /**
     * The file channel
     */
    private final FileChannel channel;
    /**
     * The access manager for this file
     */
    private final IOAccessManager accessManager;
    /**
     * The loaded blocks in this file
     */
    private final FileStoreFileBlock[] blocks;
    /**
     * The number of currently loaded blocks
     */
    private final AtomicInteger blockCount;
    /**
     * The total size of this file
     */
    private final AtomicLong size;
    /**
     * The current time
     */
    private final AtomicLong time;

    /**
     * Gets the size of this file
     *
     * @return The size of this file
     */
    public long getSize() {
        return size.get();
    }

    /**
     * Initializes this data file
     *
     * @param file       The file location
     * @param isReadonly Whether this store is in readonly mode
     * @throws StorageException When the initialization failed
     */
    public FileStoreFile(File file, boolean isReadonly) throws StorageException {
        this(file, isReadonly, false);
    }

    /**
     * Initializes this data file
     *
     * @param file       The file location
     * @param isReadonly Whether this store is in readonly mode
     * @param noInit     Whether to skip the file initialization
     * @throws StorageException When the initialization failed
     */
    FileStoreFile(File file, boolean isReadonly, boolean noInit) throws StorageException {
        this.fileName = file.getAbsolutePath();
        this.isReadonly = isReadonly;
        this.channel = newChannel(file, isReadonly);
        this.accessManager = new IOAccessManager();
        this.blocks = new FileStoreFileBlock[FILE_MAX_LOADED_BLOCKS];
        for (int i = 0; i != FILE_MAX_LOADED_BLOCKS; i++)
            this.blocks[i] = new FileStoreFileBlock();
        this.blockCount = new AtomicInteger(0);
        this.size = new AtomicLong(initSize());
        this.time = new AtomicLong(Long.MIN_VALUE + 1);
        if (!noInit)
            initialize();
    }

    /**
     * Initializes this file
     *
     * @throws StorageException When an IO error occurred
     */
    private void initialize() throws StorageException {
        if (size.get() == 0) {
            if (isReadonly)
                throw new StorageException("File is empty but open as read-only: " + fileName);
            // the file is empty and not read-only
            try (IOAccess access = accessRaw(0, FILE_PREAMBULE_HEADER_SIZE, true)) {
                access.writeInt(FILE_MAGIC_ID);
                access.writeInt(FILE_LAYOUT_VERSION);
                access.writeChar((char)0);
                access.writeChar((char)1);
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
     * Gets the current size of the file channel
     *
     * @return The current size
     * @throws StorageException When an IO error occurred
     */
    private long initSize() throws StorageException {
        try {
            return channel.size();
        } catch (IOException exception) {
            throw new StorageException(exception, "Failed to access file " + fileName);
        }
    }

    /**
     * Extends the size of this file to the specified one
     *
     * @param newSize The new size
     * @return The final size
     */
    private long extendSizeTo(long newSize) {
        while (true) {
            long current = size.get();
            long target = Math.max(current, newSize);
            if (size.compareAndSet(current, target))
                return target;
        }
    }

    /**
     * Ticks the time of this file
     *
     * @return The new time
     */
    private long tick() {
        return time.incrementAndGet();
    }

    /**
     * Get the file channel for this file
     *
     * @param file       The file location
     * @param isReadonly Whether this store is in readonly mode
     * @return The file channel
     * @throws StorageException When the backing file cannot be accessed
     */
    private static FileChannel newChannel(File file, boolean isReadonly) throws StorageException {
        try {
            if (file.exists() && !file.canWrite())
                isReadonly = true;
            return isReadonly
                    ? FileChannel.open(file.toPath(), StandardOpenOption.READ)
                    : FileChannel.open(file.toPath(), StandardOpenOption.CREATE, StandardOpenOption.READ, StandardOpenOption.WRITE);
        } catch (IOException exception) {
            throw new StorageException(exception, "Failed to create a channel to " + file.getAbsolutePath());
        }
    }

    /**
     * Flushes any outstanding changes to the backend file
     *
     * @throws StorageException When an IO operation failed
     */
    public void flush() throws StorageException {
        for (int i = 0; i != blockCount.get(); i++) {
            blocks[i].flush(channel, tick());
        }
        try {
            channel.force(true);
        } catch (IOException exception) {
            throw new StorageException(exception, "Failed to write back to " + fileName);
        }
    }

    @Override
    public void close() throws IOException {
        channel.close();
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
                        access.seek(8).writeChar((char)(openBlockCount - 1));
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
            access.seek(8 + 2).writeChar((char)(nextFreeBlock + 1));
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
                access.seek(8).writeChar((char)(openBlockCount + 1));
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
            access.writeChar((char)FileStoreFileBlock.MAX_ENTRY_SIZE);
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
        FileStoreFileBlock block = getBlockFor(pageIndex << FileStoreFileBlock.BLOCK_INDEX_LENGTH);


        block.releaseShared();


        try (IOAccess access = accessRaw(pageIndex << FileStoreFileBlock.BLOCK_INDEX_LENGTH, FileStoreFileBlock.PAGE_HEADER_SIZE, true)) {
            // gather the block data
            char entryCount = access.seek(4).readChar();
            char maxSize = access.readChar();
            char startFreeSpace = access.readChar();
            char startData = access.readChar();

            if (maxSize < length)
                return -1;



        }

        try (IOAccess access = accessRaw(pageIndex << FileStoreFileBlock.BLOCK_INDEX_LENGTH, FileStoreFileBlock.BLOCK_SIZE, true)) {
            char entryCount = access.seek(4).readChar();
            char startFreeSpace = access.readChar();
            char startData = access.readChar();
            if (entryCount > (startFreeSpace - FileStoreFileBlock.PAGE_HEADER_SIZE) >>> 2) {
                // there is at least one empty entry
                char entryIndex = 0;
                char dataOffset = (char) FileStoreFileBlock.BLOCK_SIZE;
                access.seek(FileStoreFileBlock.PAGE_HEADER_SIZE);
                while (entryIndex * FileStoreFileBlock.PAGE_ENTRY_INDEX_SIZE + FileStoreFileBlock.PAGE_HEADER_SIZE < startFreeSpace) {
                    char eOffset = access.readChar();
                    char eLength = access.readChar();
                    if (eOffset == 0 && eLength >= length) {
                        // reuse this entry
                        access.seek(entryIndex * FileStoreFileBlock.PAGE_ENTRY_INDEX_SIZE + FileStoreFileBlock.PAGE_HEADER_SIZE);
                        access.writeChar((char) (dataOffset - eLength));
                        entryCount++;
                        access.seek(4).writeChar(entryCount);
                        return keyLocal(pageIndex, entryIndex);
                    }
                    dataOffset -= eLength;
                }
            }
            if (startData - startFreeSpace - length - FileStoreFileBlock.PAGE_ENTRY_INDEX_SIZE < 0)
                // cannot store the entry
                return -1;
            // write a new entry
            access.seek(startFreeSpace);
            access.writeChar((char) (startData - length));
            access.writeChar((char) length);
            int key = keyLocal(pageIndex, entryCount);
            access.seek(4).writeChar((char) (entryCount + 1));
            access.writeChar((char) (startFreeSpace + FileStoreFileBlock.PAGE_ENTRY_INDEX_SIZE));
            access.writeChar((char) (startData - length));
            return key;
        }
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
     * Accesses the content of this file through an access element
     * An access must be within the boundaries of a block.
     *
     * @param index    The index within this file of the reserved area for the access
     * @param length   The length of the reserved area for the access
     * @param writable Whether the access shall allow writing
     * @return The access
     * @throws StorageException When the requested access cannot be fulfilled
     */
    public IOAccess accessRaw(long index, long length, boolean writable) throws StorageException {
        long targetLocation = index & INDEX_MASK_UPPER;
        if (index - targetLocation + length > FileStoreFileBlock.BLOCK_SIZE)
            throw new StorageException("IO access cannot cross block boundaries");
        FileStoreFileBlock block = getBlockFor(index);
        return accessManager.get(block, index, length, !isReadonly && writable);
    }

    /**
     * Acquires the block for the specified index in this file
     * This method ensures that:
     * 1) Only one block object can be assigned to a location in the file
     * 2) When a block object is returned, it corresponds to the requested location
     * 3) ... and will continue to do so until the access finishes using it
     *
     * @param index The requested index in this file
     * @return The corresponding block
     * @throws StorageException When an IO error occurs
     */
    private FileStoreFileBlock getBlockFor(long index) throws StorageException {
        long targetLocation = index & INDEX_MASK_UPPER;
        if (blockCount.get() < FILE_MAX_LOADED_BLOCKS)
            return getBlockForNotFull(targetLocation);
        else
            return getBlockForPoolFull(targetLocation);
    }

    /**
     * Acquires the block for the specified index in this file when the pool of blocks is not full yet
     *
     * @param targetLocation The location of the requested block in this file
     * @return The corresponding block
     * @throws StorageException When an IO error occurs
     */
    private FileStoreFileBlock getBlockForNotFull(long targetLocation) throws StorageException {
        // look for the block
        for (int i = 0; i != blockCount.get(); i++) {
            // is this the block we are looking for?
            if (blocks[i].getLocation() == targetLocation && blocks[i].useShared(targetLocation, tick())) {
                // yes and we locked it
                return blocks[i];
            }
        }
        // try to allocate one of the free block
        int count = blockCount.get();
        while (count < FILE_MAX_LOADED_BLOCKS) {
            // get the last block
            FileStoreFileBlock target = blocks[count];
            // try to reserve it
            if (target.reserve(targetLocation, channel, size.get(), tick())) {
                // this is the block
                // update the file data
                blockCount.incrementAndGet();
                extendSizeTo(Math.max(size.get(), targetLocation + FileStoreFileBlock.BLOCK_SIZE));
                if (target.useShared(targetLocation, tick())) {
                    // we got the block
                    return target;
                }
            }
            // retry with the next block
            count = blockCount.get();
        }
        // now the pool if full ... fallback
        return getBlockForPoolFull(targetLocation);
    }

    /**
     * Acquires the block for the specified index in this file when the pool of blocks is full
     *
     * @param targetLocation The location of the requested block in this file
     * @return The corresponding block
     * @throws StorageException When an IO error occurs
     */
    private FileStoreFileBlock getBlockForPoolFull(long targetLocation) throws StorageException {
        while (true) {
            // keep track of the oldest block
            int oldestIndex = -1;
            long oldestTime = Long.MAX_VALUE;
            long oldestLocation = -1;
            for (int i = 0; i != FILE_MAX_LOADED_BLOCKS; i++) {
                // is this the block we are looking for?
                if (blocks[i].getLocation() == targetLocation && blocks[i].useShared(targetLocation, tick())) {
                    // yes and we locked it
                    return blocks[i];
                }
                // is this the oldest block
                long t = blocks[i].getLastHit();
                if (t < oldestTime) {
                    oldestIndex = i;
                    oldestTime = t;
                    oldestLocation = blocks[i].getLocation();
                }
            }
            // we did not find the block, try to reclaim the block
            FileStoreFileBlock target = blocks[oldestIndex];
            if (target.getLastHit() == oldestTime // the time did not change
                    && target.getLocation() == oldestLocation // still the same location
                    && target.reclaim(channel, oldestLocation, tick()) // try to reclaim
                    ) {
                // the block was successfully reclaimed, reserve it
                if (target.reserve(targetLocation, channel, size.get(), tick())) {
                    // update the file data
                    extendSizeTo(Math.max(size.get(), targetLocation + FileStoreFileBlock.BLOCK_SIZE));
                    if (target.useShared(targetLocation, tick())) {
                        // we got the block
                        return target;
                    }
                }
            }
        }
    }

    /**
     * Gets the location of the page referred to by the specified file-local key
     *
     * @param key The file-local key to an entry
     * @return The location (index within this file) of the corresponding page
     */
    private static long keyPageLocation(int key) {
        return ((long) keyPageIndex(key)) << FileStoreFileBlock.BLOCK_INDEX_LENGTH;
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
