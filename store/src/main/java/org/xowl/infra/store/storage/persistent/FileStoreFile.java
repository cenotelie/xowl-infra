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
     * int: Number of open blocks (blocks that contain data but are not full)
     * int: Index of the next block to open (in number of block)
     */
    private static final int FILE_PREAMBULE_HEADER_SIZE = 4 + 4 + 4 + 4;
    /**
     * The size of an open block entry in the preambule
     * char: Index of the block
     * char: Remaining free space
     */
    private static final int FILE_PREAMBULE_ENTRY_SIZE = 2 + 2;
    /**
     * The number of remaining bytes below which a block is considered full
     */
    private static final int BLOCK_FULL_THRESHOLD = 24;
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
     * The pool of transactions for this file
     */
    private final IOTransationPool transations;
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
        this.transations = new IOTransationPool();
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
            try (IOAccess transaction = accessRaw(0, FILE_PREAMBULE_HEADER_SIZE, true)) {
                transaction.writeInt(FILE_MAGIC_ID);
                transaction.writeInt(FILE_LAYOUT_VERSION);
                transaction.writeInt(0);
                transaction.writeInt(1);
            }
            flush();
        } else {
            // file is not empty, verify the header
            try (IOAccess transaction = accessRaw(0, FILE_PREAMBULE_HEADER_SIZE, false)) {
                int magic = transaction.readInt();
                if (magic != FILE_MAGIC_ID)
                    throw new StorageException("Invalid file header, expected 0x" + Integer.toHexString(FILE_MAGIC_ID) + ", got 0x" + Integer.toHexString(magic) + ": " + fileName);
                int layout = transaction.readInt();
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

        try (IOAccess transaction = accessRaw(0, FileStoreFileBlock.BLOCK_SIZE, true)) {
            if (transaction == null)
                return -1;
            transaction.seek(8);
            int openBlockCount = transaction.readInt();
            int nextFreeBlock = transaction.readInt();
            int inspected = 0;
            for (int i = 0; i != FILE_MAX_OPEN_BLOCKS; i++) {
                if (inspected >= openBlockCount)
                    break;
                char blockIndex = transaction.readChar();
                char blockRemaining = transaction.readChar();
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
                    if (blockRemaining >= BLOCK_FULL_THRESHOLD) {
                        transaction.seek(FILE_PREAMBULE_HEADER_SIZE + i * FILE_PREAMBULE_ENTRY_SIZE + 2);
                        transaction.writeChar(blockRemaining);
                    } else {
                        transaction.seek(FILE_PREAMBULE_HEADER_SIZE + i * FILE_PREAMBULE_ENTRY_SIZE);
                        transaction.writeChar('\0');
                        transaction.writeChar('\0');
                        transaction.seek(8).writeInt(openBlockCount - 1);
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
            pageMarkOpen(transaction, nextFreeBlock, remaining);
            transaction.seek(8 + 4);
            transaction.writeInt(nextFreeBlock + 1);
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
        try (IOAccess transaction = accessRaw(0, FILE_PREAMBULE_HEADER_SIZE, true)) {
            if (transaction == null)
                return;
            pageMarkOpen(transaction, keyPageIndex(key), remaining);
        }
    }

    /**
     * Marks a page as open
     *
     * @param transaction The current transaction
     * @param pageIndex   The page to mark as open
     * @param remaining   The remaining space in the page
     * @throws StorageException When an IO operation failed
     */
    private void pageMarkOpen(IOAccess transaction, int pageIndex, int remaining) throws StorageException {
        int openBlockCount = transaction.seek(8).readInt();
        transaction.readInt();
        int inspected = 0;
        for (int i = 0; i != FILE_MAX_OPEN_BLOCKS; i++) {
            if (inspected >= openBlockCount)
                break;
            char blockIndex = transaction.readChar();
            if (blockIndex > 0)
                inspected++;
            if (blockIndex == pageIndex) {
                // already open
                transaction.writeChar((char) remaining);
                return;
            }
            transaction.readChar();
        }
        // the block was not open
        if (openBlockCount < FILE_MAX_OPEN_BLOCKS) {
            if (remaining >= BLOCK_FULL_THRESHOLD) {
                transaction.seek(FILE_PREAMBULE_HEADER_SIZE + openBlockCount * FILE_PREAMBULE_ENTRY_SIZE);
                transaction.writeChar((char) pageIndex);
                transaction.writeChar((char) remaining);
                transaction.seek(8).writeInt(openBlockCount + 1);
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
        try (IOAccess transaction = accessRaw(pageIndex << FileStoreFileBlock.BLOCK_INDEX_LENGTH, FileStoreFileBlock.BLOCK_SIZE, true)) {
            if (transaction == null)
                return false;
            transaction.writeChar(FileStoreFileBlock.PAGE_LAYOUT_VERSION);
            transaction.writeChar(FileStoreFileBlock.PAGE_FLAG_REUSE_EMPTY_ENTRIES);
            transaction.writeChar('\0');
            transaction.writeChar((char) FileStoreFileBlock.PAGE_HEADER_SIZE);
            transaction.writeChar((char) FileStoreFileBlock.BLOCK_SIZE);
            return true;
        }
    }


    /**
     * Tries to store an entry of the specified size in the given page
     *
     * @param pageIndex The index of the page
     * @param length    The length of the entry to register
     * @return The key to be used to retrieve the data, or -1 if the entry cannot be allocated
     * @throws StorageException When an IO operation failed
     */
    private int pageTryStoreEntry(int pageIndex, int length) throws StorageException {
        try (IOAccess transaction = accessRaw(pageIndex << FileStoreFileBlock.BLOCK_INDEX_LENGTH, FileStoreFileBlock.BLOCK_SIZE, true)) {
            if (transaction == null)
                return -1;
            char entryCount = transaction.seek(4).readChar();
            char startFreeSpace = transaction.readChar();
            char startData = transaction.readChar();
            if (entryCount > (startFreeSpace - FileStoreFileBlock.PAGE_HEADER_SIZE) >>> 2) {
                // there is at least one empty entry
                char entryIndex = 0;
                char dataOffset = (char) FileStoreFileBlock.BLOCK_SIZE;
                transaction.seek(FileStoreFileBlock.PAGE_HEADER_SIZE);
                while (entryIndex * FileStoreFileBlock.PAGE_ENTRY_INDEX_SIZE + FileStoreFileBlock.PAGE_HEADER_SIZE < startFreeSpace) {
                    char eOffset = transaction.readChar();
                    char eLength = transaction.readChar();
                    if (eOffset == 0 && eLength >= length) {
                        // reuse this entry
                        transaction.seek(entryIndex * FileStoreFileBlock.PAGE_ENTRY_INDEX_SIZE + FileStoreFileBlock.PAGE_HEADER_SIZE);
                        transaction.writeChar((char) (dataOffset - eLength));
                        entryCount++;
                        transaction.seek(4).writeChar(entryCount);
                        return keyLocal(pageIndex, entryIndex);
                    }
                    dataOffset -= eLength;
                }
            }
            if (startData - startFreeSpace - length - FileStoreFileBlock.PAGE_ENTRY_INDEX_SIZE < 0)
                // cannot store the entry
                return -1;
            // write a new entry
            transaction.seek(startFreeSpace);
            transaction.writeChar((char) (startData - length));
            transaction.writeChar((char) length);
            int key = keyLocal(pageIndex, entryCount);
            transaction.seek(4).writeChar((char) (entryCount + 1));
            transaction.writeChar((char) (startFreeSpace + FileStoreFileBlock.PAGE_ENTRY_INDEX_SIZE));
            transaction.writeChar((char) (startData - length));
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
        try (IOAccess transaction = accessRaw(pageLocation, FileStoreFileBlock.BLOCK_SIZE, true)) {
            if (transaction == null)
                return -1;
            char entryCount = transaction.seek(4).readChar();
            char startFreeSpace = transaction.readChar();
            char startData = transaction.readChar();
            if (entryIndex >= (startFreeSpace - FileStoreFileBlock.PAGE_HEADER_SIZE) >>> 2)
                throw new StorageException("The entry for the specified key is not in this page");
            transaction.seek(FileStoreFileBlock.PAGE_HEADER_SIZE + entryIndex * FileStoreFileBlock.PAGE_ENTRY_INDEX_SIZE);
            char offset = transaction.readChar();
            char length = transaction.readChar();
            if (offset == 0 || length == 0)
                throw new StorageException("The entry for the specified key has been removed");
            if (offset < startData) {
                // not the last entry in this page
                // simply marks this entry as empty by erasing the offset
                transaction.seek(FileStoreFileBlock.PAGE_HEADER_SIZE + entryIndex * FileStoreFileBlock.PAGE_ENTRY_INDEX_SIZE);
                transaction.writeChar('\0');
            } else {
                // here this is the last entry in this page
                do {
                    startFreeSpace -= FileStoreFileBlock.PAGE_ENTRY_INDEX_SIZE;
                    startData += length;
                    entryCount--;
                    // go to the previous entry and get its info
                    entryIndex--;
                    transaction.seek(FileStoreFileBlock.PAGE_HEADER_SIZE + entryIndex * FileStoreFileBlock.PAGE_ENTRY_INDEX_SIZE);
                    offset = transaction.readChar();
                    length = transaction.readChar();
                } while (offset == 0 && entryIndex >= 0);
                transaction.seek(4).writeChar(entryCount);
                transaction.writeChar(startFreeSpace);
                transaction.writeChar(startData);
            }
            // compute the remaining free space
            int remainingSpace = 0;
            transaction.seek(FileStoreFileBlock.PAGE_HEADER_SIZE);
            for (int i = 0; i != entryCount; i++) {
                char eOffset = transaction.readChar();
                char eLength = transaction.readChar();
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
     * @param writable Whether the transaction allows writing to the backend
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
        try (IOAccess transaction = transations.begin(block, 0, FileStoreFileBlock.PAGE_HEADER_SIZE, false)) {
            if (transaction == null) {
                block.releaseShared();
                return null;
            }
            transaction.seek(FileStoreFileBlock.PAGE_HEADER_SIZE + entryIndex * FileStoreFileBlock.PAGE_ENTRY_INDEX_SIZE);
            offset = transaction.readChar();
            length = transaction.readChar();
        }
        if (offset == 0 || length == 0) {
            block.releaseShared();
            throw new StorageException("The entry for the specified key has been removed");
        }
        IOAccess transaction = transations.begin(block, offset, length, !isReadonly && writable);
        if (transaction == null)
            block.releaseShared();
        return transaction;
    }

    /**
     * Access the content of this file through a transaction
     * A transaction must be within the boundaries of a block.
     *
     * @param index    The index within this file of the reserved area for the transaction
     * @param length   The length of the reserved area for the transaction
     * @param writable Whether the transaction shall allow writing
     * @return The transaction
     * @throws StorageException When the requested transaction cannot be fulfilled
     */
    public IOAccess accessRaw(long index, long length, boolean writable) throws StorageException {
        long targetLocation = index & INDEX_MASK_UPPER;
        if (index - targetLocation + length > FileStoreFileBlock.BLOCK_SIZE)
            throw new StorageException("IO transaction cannot cross block boundaries");
        FileStoreFileBlock block = getBlockFor(index);
        IOAccess transaction = transations.begin(block, index - targetLocation, length, !isReadonly && writable);
        if (transaction == null)
            block.releaseShared();
        return transaction;
    }

    /**
     * Acquires the block for the specified index in this file
     * This method ensures that:
     * 1) Only one block object can be assigned to a location in the file
     * 2) When a block object is returned, it corresponds to the requested location
     * 3) ... and will continue to do so until the transaction finishes using it
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
        return (key >> 16);
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
