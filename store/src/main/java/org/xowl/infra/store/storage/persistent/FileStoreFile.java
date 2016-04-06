/*******************************************************************************
 * Copyright (c) 2015 Laurent Wouters
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Lesser General
 * Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 * <p>
 * Contributors:
 * Laurent Wouters - lwouters@xowl.org
 ******************************************************************************/

package org.xowl.infra.store.storage.persistent;

import java.io.File;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;

/**
 * Represents a persisted binary file
 *
 * @author Laurent Wouters
 */
class FileStoreFile {
    /**
     * The mask for the index of a block
     */
    private static final long INDEX_MASK_UPPER = ~FileStoreFileBlock.INDEX_MASK_LOWER;
    /**
     * The maximum number of loaded blocks
     */
    private static final int MAX_LOADED_BLOCKS = 256;

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
    private volatile int blockCount;
    /**
     * The total size of this file
     */
    private volatile long size;
    /**
     * The current time
     */
    private volatile long time;

    /**
     * Initializes this data file
     *
     * @param file The file location
     * @throws IOException When the backing file cannot be accessed
     */
    public FileStoreFile(File file) throws IOException {
        this(file, false);
    }

    /**
     * Initializes this data file
     *
     * @param file       The file location
     * @param isReadonly Whether this store is in readonly mode
     * @throws IOException When the backing file cannot be accessed
     */
    public FileStoreFile(File file, boolean isReadonly) throws IOException {
        this.isReadonly = isReadonly;
        this.channel = newChannel(file, isReadonly);
        this.transations = new IOTransationPool();
        this.blocks = new FileStoreFileBlock[MAX_LOADED_BLOCKS];
        for (int i = 0; i != MAX_LOADED_BLOCKS; i++)
            this.blocks[i] = new FileStoreFileBlock();
        this.blockCount = 0;
        this.size = channel.size();
        this.time = Long.MIN_VALUE;
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
        for (int i = 0; i != blockCount; i++) {
            if (blockIsDirty[i]) {
                boolean successBlock = true;
                if (blockPages[i] != null) {
                    successBlock = blockPages[i].onCommit();
                }
                try {
                    blockBuffers[i].position(0);
                    channel.position(blockLocations[i]);
                    channel.write(blockBuffers[i]);
                } catch (IOException exception) {
                    successBlock = false;
                }
                if (successBlock)
                    blockIsDirty[i] = false;
                success &= successBlock;
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
        for (int i = 0; i != blockCount; i++) {
            if (blockIsDirty[i]) {
                boolean successBlock = true;
                // reload the block
                if (blockLocations[i] < getSize()) {
                    try {
                        blockBuffers[i].position(0);
                        channel.position(blockLocations[i]);
                        channel.read(blockBuffers[i]);
                    } catch (IOException exception) {
                        successBlock = false;
                    }
                }
                success &= successBlock;
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
        for (int i = 0; i != MAX_LOADED_BLOCKS; i++) {
            blockBuffers[i] = null;
            blockLocations[i] = -1;
            blockLastHits[i] = 0;
            blockIsDirty[i] = false;
            blockPages[i] = null;
        }
        channel.close();
    }

    /**
     * Gets the page that contains the specified location
     *
     * @param location A location
     * @return The page that contains the specified location
     * @throws StorageException When the page version does not match the expected one
     */
    public FileStoreFilePage getPageAt(long location) throws StorageException {
        return getPage((int) ((location & INDEX_MASK_UPPER) / BLOCK_SIZE));
    }

    /**
     * Gets the page that contains the specified key
     *
     * @param key A key
     * @return The page that contains the specified key
     * @throws StorageException When the page version does not match the expected one
     */
    public FileStoreFilePage getPageFor(int key) throws StorageException {
        return getPage(key >> 16);
    }

    /**
     * Gets an access to the entry for the specified key
     *
     * @param key The file-local key to an entry
     * @return The IO element that can be used for reading and writing
     * @throws StorageException When an IO operation failed
     */
    public IOTransaction access(int key) throws StorageException {
        return access(key, true);
    }

    /**
     * Gets a reading access to the entry for the specified key
     *
     * @param key The file-local key to an entry
     * @return The IO element that can be used for reading
     * @throws StorageException When an IO operation failed
     */
    public IOTransaction read(int key) throws StorageException {
        return access(key, false);
    }

    /**
     * Gets an access to the entry for the specified key
     *
     * @param key      The file-local key to an entry
     * @param writable Whether the transaction allows writing to the backend
     * @return The IO element that can be used for reading and writing
     * @throws StorageException When an IO operation failed
     */
    protected IOTransaction access(int key, boolean writable) throws StorageException {

    }

    /**
     * Acquires the block for the specified index in this file
     *
     * @param index The requested index in this file
     * @return The index of the corresponding block
     */
    private FileStoreFileBlock getBlockFor(long index) {
        // is the block loaded
        long targetLocation = index & INDEX_MASK_UPPER;
        for (int i = 0; i != blockCount; i++) {
            if (blocks[i].getLocation() == targetLocation) {
                return blocks[i];
            }
        }
        // we need to load the block
        for (int i = blockCount; i != MAX_LOADED_BLOCKS; i++) {
            if (blocks[i].getLocation() == -1 && blocks[i].reserve(targetLocation, channel, size, time)) {
                time++;
                size = Math.max(size, targetLocation + FileStoreFileBlock.BLOCK_SIZE);
                return blocks[i];
            }
        }
        // no free block, look for a clean block to reclaim
        while (true) {
            int minIndex = -1;
            long minTime = Long.MAX_VALUE;
            for (int i = 0; i != MAX_LOADED_BLOCKS; i++) {
                long t = blocks[i].getLastHit();
                if (t < minTime) {
                    minIndex = i;
                    minTime = t;
                }
            }
            if (minIndex >= 0
                    && blocks[minIndex].getLastHit() == minTime
                    && blocks[minIndex].reclaim()) {
                if (blocks[minIndex].reserve(targetLocation, channel, size, time)) {
                    time++;
                    size = Math.max(size, targetLocation + FileStoreFileBlock.BLOCK_SIZE);
                    return blocks[minIndex];
                }
            }
        }
    }
}
