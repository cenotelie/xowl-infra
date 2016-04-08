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
 * - int32: Index of the block
 * - int32: Remaining free space
 *
 * @author Laurent Wouters
 */
class FileStoreFile {
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
     * int: Index of the block
     * int: Remaining free space
     */
    private static final int FILE_PREAMBULE_ENTRY_SIZE = 4 + 4;
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
     * Initializes this data file
     *
     * @param file The file location
     * @throws IOException      When the backing file cannot be accessed
     * @throws StorageException When the initialization failed
     */
    public FileStoreFile(File file) throws IOException, StorageException {
        this(file, false);
    }

    /**
     * Initializes this data file
     *
     * @param file       The file location
     * @param isReadonly Whether this store is in readonly mode
     * @throws IOException      When the backing file cannot be accessed
     * @throws StorageException When the initialization failed
     */
    public FileStoreFile(File file, boolean isReadonly) throws IOException, StorageException {
        this.isReadonly = isReadonly;
        this.channel = newChannel(file, isReadonly);
        this.transations = new IOTransationPool();
        this.blocks = new FileStoreFileBlock[FILE_MAX_LOADED_BLOCKS];
        for (int i = 0; i != FILE_MAX_LOADED_BLOCKS; i++)
            this.blocks[i] = new FileStoreFileBlock();
        this.blockCount = new AtomicInteger(0);
        this.size = new AtomicLong(channel.size());
        this.time = new AtomicLong(Long.MIN_VALUE + 1);
        if (size.get() == 0 && !isReadonly)
            initialize();
    }

    /**
     * Initializes this file with the default preambule
     *
     * @throws StorageException When an IO error occurred
     */
    private void initialize() throws StorageException {
        try (IOTransaction transaction = accessRaw(0, FILE_PREAMBULE_HEADER_SIZE, true)) {
            transaction.writeInt(FILE_MAGIC_ID);
            transaction.writeInt(FILE_LAYOUT_VERSION);
            transaction.writeInt(0);
            transaction.writeInt(1);
        }
        commit();
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
     * @throws IOException When the backing file cannot be accessed
     */
    private static FileChannel newChannel(File file, boolean isReadonly) throws IOException {
        if (file.exists() && !file.canWrite())
            isReadonly = true;
        return isReadonly
                ? FileChannel.open(file.toPath(), StandardOpenOption.READ)
                : FileChannel.open(file.toPath(), StandardOpenOption.CREATE, StandardOpenOption.READ, StandardOpenOption.WRITE);
    }

    /**
     * Commits any outstanding changes
     *
     * @return Whether the operation fully succeeded
     */
    public boolean commit() {
        boolean success = true;
        for (int i = 0; i != blockCount.get(); i++) {
            if (blocks[i].getLocation() >= 0) {
                success &= blocks[i].commit(channel, time.get());
                tick();
            }
        }
        try {
            channel.force(true);
        } catch (IOException exception) {
            success = false;
        }
        return success;
    }

    /**
     * Rollback outstanding changes
     *
     * @return Whether the operation fully succeeded
     */
    public boolean rollback() {
        boolean success = true;
        for (int i = 0; i != blockCount.get(); i++) {
            if (blocks[i].getLocation() >= 0) {
                success &= blocks[i].rollback(channel, time.get());
                tick();
            }
        }
        return success;
    }

    /**
     * Closes the file.
     * Any outstanding changes will be lost
     *
     * @throws IOException When an IO operation failed
     */
    public void close() throws IOException {
        channel.close();
        for (int i = 0; i != FILE_MAX_LOADED_BLOCKS; i++)
            blocks[i].close();
    }

    /**
     * Tries to allocate an entry of the specified size in this file
     *
     * @param entrySize The size of the entry
     * @return The key to the entry, or -1 if it cannot be allocated
     * @throws StorageException When an IO operation failed
     */
    public int allocateEntry(int entrySize) throws StorageException {
        try (IOTransaction transaction = accessRaw(0, FileStoreFileBlock.BLOCK_SIZE, true)) {
            transaction.seek(8);
            int openBlockCount = header.readInt();
            int nextFreeBlock = header.readInt();
            for (int i = 0; i != openBlockCount; i++) {
                int blockIndex = header.readInt();
                int blockRemaining = header.readInt();
                if (blockIndex == -1)
                    // this is a free block slot
                    continue;
                if (blockRemaining >= entrySize + FileStoreFilePage.ENTRY_OVERHEAD) {
                    // the entry could fit in the page
                    FileStoreFilePage page = file.getPage(blockIndex);
                    if (!page.canStore(entrySize))
                        continue;
                    long key = getFullKey(fileIndex, page.registerEntry(entrySize));
                    blockRemaining -= entrySize;
                    blockRemaining -= FileStoreFilePage.ENTRY_OVERHEAD;
                    if (blockRemaining >= BLOCK_FULL_THRESHOLD) {
                        header.seek(FILE_PREAMBLE_HEADER_SIZE + i * FILE_PREAMBULE_ENTRY_SIZE + 4);
                        header.writeInt(blockRemaining);
                    } else {
                        header.seek(FILE_PREAMBLE_HEADER_SIZE + i * FILE_PREAMBULE_ENTRY_SIZE);
                        header.writeInt(-1);
                        header.writeInt(0);
                    }
                    return key;
                }
            }

            // cannot fit in an open block
            if (nextFreeBlock >= FILE_MAX_BLOCKS)
                return PersistedNode.KEY_NOT_PRESENT;
            FileStoreFilePage page = file.getPage(nextFreeBlock);
            page.setReuseEmptyEntries();
            nextFreeBlock++;
            int remaining = FileStoreFilePage.MAX_ENTRY_SIZE - entrySize - FileStoreFilePage.ENTRY_OVERHEAD;
            if (remaining >= BLOCK_FULL_THRESHOLD) {
                header.seek(FILE_PREAMBLE_HEADER_SIZE);
                boolean found = false;
                for (int i = 0; i != openBlockCount; i++) {
                    int blockIndex = header.readInt();
                    header.readInt();
                    if (blockIndex == -1) {
                        header.seek(FILE_PREAMBLE_HEADER_SIZE + i * FILE_PREAMBULE_ENTRY_SIZE);
                        header.writeInt(nextFreeBlock - 1);
                        header.writeInt(remaining);
                        found = true;
                        break;
                    }
                }
                if (!found && openBlockCount < FILE_MAX_OPEN_BLOCKS) {
                    header.seek(FILE_PREAMBLE_HEADER_SIZE + openBlockCount * FILE_PREAMBULE_ENTRY_SIZE);
                    header.writeInt(nextFreeBlock - 1);
                    header.writeInt(remaining);
                    openBlockCount++;
                }
            }
            header.seek(8);
            header.writeInt(openBlockCount);
            header.writeInt(nextFreeBlock);
            return getFullKey(fileIndex, page.registerEntry(entrySize));
        } catch (IOException exception) {
            // an IOException cannot happen
            return PersistedNode.KEY_NOT_PRESENT;
        }
    }

    /**
     * Clears an entry from a file
     *
     * @param file The backend file containing the entry
     * @param key  The key of the entry to remove
     * @throws StorageException When an IO operation failed
     */
    private void clearEntry(FileStoreFile file, int key) throws StorageException {
        try (IOElement header = transaction(file, 0, FileStoreFile.BLOCK_SIZE, true)) {
            FileStoreFilePage page = file.getPageFor(key);
            int length = page.removeEntry(key);
            header.seek(8);
            int openBlockCount = header.readInt();
            int nextFreeBlock = header.readInt();
            for (int i = 0; i != openBlockCount; i++) {
                int blockIndex = header.readInt();
                int blockRemaining = header.readInt();
                if (blockIndex == page.getIndex()) {
                    // already open
                    blockRemaining += length + FileStoreFilePage.ENTRY_OVERHEAD;
                    header.seek(FILE_PREAMBLE_HEADER_SIZE + i * FILE_PREAMBULE_ENTRY_SIZE + 4);
                    header.writeInt(blockRemaining);
                    return;
                }
            }
            // the block was not open
            if (openBlockCount < FILE_MAX_OPEN_BLOCKS) {
                int remaining = page.getFreeSpace();
                if (remaining >= BLOCK_FULL_THRESHOLD) {
                    header.seek(FILE_PREAMBLE_HEADER_SIZE + openBlockCount * FILE_PREAMBULE_ENTRY_SIZE);
                    header.writeInt(page.getIndex());
                    header.writeInt(remaining);
                    openBlockCount++;
                    header.seek(8);
                    header.writeInt(openBlockCount);
                    header.writeInt(nextFreeBlock);
                }
            }
        } catch (IOException exception) {
            // an IOException cannot happen
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
    public IOTransaction accessEntry(int key, boolean writable) throws StorageException {
        if (key <= 0)
            throw new StorageException("Invalid key");
        FileStoreFileBlock block = getBlockFor(keyBlockLocation(key));
        long offset;
        long length;
        try (IOTransaction transaction = transations.begin(block, 0, FileStoreFileBlock.PAGE_HEADER_SIZE, false)) {
            if (transaction == null) {
                block.releaseShared();
                return null;
            }
            int entryIndex = keyEntryIndex(key);
            transaction.seek(FileStoreFileBlock.PAGE_HEADER_SIZE + entryIndex * FileStoreFileBlock.PAGE_ENTRY_INDEX_SIZE);
            offset = transaction.readChar();
            length = transaction.readChar();
        }
        if (offset == 0 || length == 0) {
            block.releaseShared();
            throw new StorageException("The entry for the specified key has been removed");
        }
        IOTransaction transaction = transations.begin(block, offset, length, !isReadonly && writable);
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
    public IOTransaction accessRaw(long index, long length, boolean writable) throws StorageException {
        long targetLocation = index & INDEX_MASK_UPPER;
        if (index - targetLocation + length > FileStoreFileBlock.BLOCK_SIZE)
            throw new StorageException("IO transaction cannot cross block boundaries");
        FileStoreFileBlock block = getBlockFor(index);
        IOTransaction transaction = transations.begin(block, index - targetLocation, length, !isReadonly && writable);
        if (transaction == null)
            block.releaseShared();
        return transaction;
    }

    /**
     * Acquires the block for the specified index in this file
     *
     * @param index The requested index in this file
     * @return The corresponding block
     */
    private FileStoreFileBlock getBlockFor(long index) {
        // is the block loaded
        long targetLocation = index & INDEX_MASK_UPPER;
        for (int i = 0; i != blockCount.get(); i++) {
            if (blocks[i].getLocation() == targetLocation && blocks[i].useShared(targetLocation, time.get())) {
                tick();
                return blocks[i];
            }
        }
        // we need to load the block
        for (int i = blockCount.get(); i != FILE_MAX_LOADED_BLOCKS; i++) {
            if (blocks[i].getLocation() == -1 && blocks[i].reserve(targetLocation, channel, size.get(), time.get())) {
                tick();
                extendSizeTo(Math.max(size.get(), targetLocation + FileStoreFileBlock.BLOCK_SIZE));
                if (blocks[i].useShared(targetLocation, time.get())) {
                    tick();
                    return blocks[i];
                }
            }
        }
        // no free block, look for a clean block to reclaim
        while (true) {
            int minIndex = -1;
            long minTime = Long.MAX_VALUE;
            for (int i = 0; i != FILE_MAX_LOADED_BLOCKS; i++) {
                long t = blocks[i].getLastHit();
                if (t < minTime) {
                    minIndex = i;
                    minTime = t;
                }
            }
            if (minIndex >= 0
                    && blocks[minIndex].getLastHit() == minTime
                    && blocks[minIndex].reclaim(channel, targetLocation, time.get())) {
                if (blocks[minIndex].reserve(targetLocation, channel, size.get(), time.get())) {
                    tick();
                    extendSizeTo(Math.max(size.get(), targetLocation + FileStoreFileBlock.BLOCK_SIZE));
                    if (blocks[minIndex].useShared(targetLocation, time.get())) {
                        tick();
                        return blocks[minIndex];
                    }
                }
            }
        }
    }

    /**
     * Gets the location of the block referred to by the specified file-local key
     *
     * @param key The file-local key to an entry
     * @return The location (index within this file) of the corresponding block
     */
    private static long keyBlockLocation(int key) {
        return (key >> 16) * FileStoreFileBlock.BLOCK_SIZE;
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
}
