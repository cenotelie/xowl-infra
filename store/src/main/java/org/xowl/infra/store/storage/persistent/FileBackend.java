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
 * Represents a single file for storage
 *
 * @author Laurent Wouters
 */
class FileBackend implements IOBackend, Closeable {
    /**
     * The maximum number of loaded blocks
     */
    private static final int FILE_MAX_LOADED_BLOCKS = 1024;
    /**
     * The mask for the index of a block
     */
    protected static final int INDEX_MASK_UPPER = ~FileBlock.INDEX_MASK_LOWER;

    /**
     * The backend is ready for IO
     */
    private static final int STATE_READY = 0;
    /**
     * The backend is flushing
     */
    private static final int STATE_FLUSHING = 1;
    /**
     * The backend is reclaiming some block
     */
    private static final int STATE_RECLAIMING = 2;
    /**
     * The file is now closed
     */
    private static final int STATE_CLOSED = 3;

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
    private final FileBlockTS[] blocks;
    /**
     * The number of currently loaded blocks
     */
    private final AtomicInteger blockCount;
    /**
     * The total size of this file
     */
    private final AtomicInteger size;
    /**
     * The current time
     */
    private final AtomicLong time;
    /**
     * The state of this file backend
     */
    private final AtomicInteger state;

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
    public FileBackend(File file, boolean isReadonly) throws StorageException {
        this.fileName = file.getAbsolutePath();
        this.isReadonly = isReadonly;
        this.channel = newChannel(file, isReadonly);
        this.accessManager = new IOAccessManager(this);
        this.blocks = new FileBlockTS[FILE_MAX_LOADED_BLOCKS];
        for (int i = 0; i != FILE_MAX_LOADED_BLOCKS; i++)
            this.blocks[i] = new FileBlockTS();
        this.blockCount = new AtomicInteger(0);
        this.size = new AtomicInteger(initSize());
        this.time = new AtomicLong(Long.MIN_VALUE + 1);
        this.state = new AtomicInteger(STATE_READY);
    }

    /**
     * Gets the current size of the file channel
     *
     * @return The current size
     * @throws StorageException When an IO error occurred
     */
    private int initSize() throws StorageException {
        try {
            return (int) channel.size();
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
    private int extendSizeTo(int newSize) {
        while (true) {
            int current = size.get();
            int target = Math.max(current, newSize);
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
        while (true) {
            int s = state.get();
            if (s == STATE_CLOSED)
                throw new StorageException("The file is closed");
            if (!state.compareAndSet(STATE_READY, STATE_FLUSHING))
                continue;
            for (int i = 0; i != blockCount.get(); i++) {
                blocks[i].flush(channel);
            }
            try {
                channel.force(true);
            } catch (IOException exception) {
                throw new StorageException(exception, "Failed to write back to " + fileName);
            } finally {
                state.set(STATE_READY);
            }
            return;
        }
    }

    /**
     * Accesses the content of this file through an access element
     * An access must be within the boundaries of a block.
     *
     * @param index    The index within this file of the reserved area for the access
     * @param length   The length of the reserved area for the access
     * @param writable Whether the access shall allow writing
     * @return The access element
     * @throws StorageException When the requested access cannot be fulfilled
     */
    public IOAccess access(int index, int length, boolean writable) throws StorageException {
        return accessManager.get(index, length, !isReadonly && writable);
    }

    /**
     * Accesses a specific block of this file through an access element
     *
     * @param index    The index within this file of the reserved area for the access
     * @param length   The length of the reserved area for the access
     * @param writable Whether the access shall allow writing
     * @param block    The block that will backs the access
     * @return The access element
     * @throws StorageException When the requested access cannot be fulfilled
     */
    protected IOAccess access(int index, int length, boolean writable, FileBlockTS block) throws StorageException {
        IOAccess access = accessManager.get(index, length, !isReadonly && writable, block);
        block.use(block.location, tick());
        return access;
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
    protected FileBlockTS getBlockFor(int index) throws StorageException {
        int targetLocation = index & INDEX_MASK_UPPER;
        if (blockCount.get() < FILE_MAX_LOADED_BLOCKS)
            return getBlockWhenNotFull(targetLocation);
        else
            return getBlockWhenFull(targetLocation);
    }

    /**
     * Acquires the block for the specified index in this file when the pool of blocks is not full yet
     *
     * @param targetLocation The location of the requested block in this file
     * @return The corresponding block
     * @throws StorageException When an IO error occurs
     */
    private FileBlockTS getBlockWhenNotFull(int targetLocation) throws StorageException {
        // try to allocate one of the free block
        int count = blockCount.get();
        while (count < FILE_MAX_LOADED_BLOCKS) {
            // look for the block
            for (int i = 0; i != count; i++) {
                // is this the block we are looking for?
                if (blocks[i].getLocation() == targetLocation && blocks[i].use(targetLocation, tick())) {
                    // yes and we locked it
                    return blocks[i];
                }
            }
            // get the last block
            FileBlockTS target = blocks[count];
            // try to reserve it
            switch (target.reserve(targetLocation, channel, size.get(), tick())) {
                case FileBlockTS.RESERVE_RESULT_READY:
                    // same block and location, but another thread ...
                    if (target.use(targetLocation, tick()))
                        return target;
                    break;
                case FileBlockTS.RESERVE_RESULT_OK:
                    // reserved by this thread for the location
                    // update the file data
                    blockCount.incrementAndGet();
                    extendSizeTo(Math.max(size.get(), targetLocation + FileBlock.BLOCK_SIZE));
                    if (target.use(targetLocation, tick()))
                        return target;
                    break;
            }
            // retry with the next block
            count = blockCount.get();
        }
        // now the pool if full ... fallback
        return getBlockWhenNotFound(targetLocation);
    }

    /**
     * Acquires the block for the specified index in this file when the pool of blocks is full
     * This method attempts a full scan of the allocated block to find the corresponding one.
     * If this fails, is falls back to the reclaiming an existing one.
     *
     * @param targetLocation The location of the requested block in this file
     * @return The corresponding block
     * @throws StorageException When an IO error occurs
     */
    private FileBlockTS getBlockWhenFull(int targetLocation) throws StorageException {
        for (int i = 0; i != FILE_MAX_LOADED_BLOCKS; i++) {
            // is this the block we are looking for?
            if (blocks[i].getLocation() == targetLocation && blocks[i].use(targetLocation, tick())) {
                // yes and we locked it
                return blocks[i];
            }
        }
        return getBlockWhenNotFound(targetLocation);
    }

    /**
     * Acquires the block for the specified index in this file when previous attempts to find a corresponding block failed
     * This method will reclaim the oldest block when an already provisioned block for the location is not found.
     *
     * @param targetLocation The location of the requested block in this file
     * @return The corresponding block
     * @throws StorageException When an IO error occurs
     */
    private FileBlockTS getBlockWhenNotFound(int targetLocation) throws StorageException {
        while (true) {
            int s = state.get();
            if (s == STATE_CLOSED)
                throw new StorageException("The file is closed");
            if (state.compareAndSet(STATE_READY, STATE_RECLAIMING))
                break;
        }

        // lookup and reclaim the oldest block
        while (true) {
            // keep track of the oldest block
            int oldestIndex = -1;
            long oldestTime = Long.MAX_VALUE;
            long oldestLocation = -1;
            for (int i = 0; i != FILE_MAX_LOADED_BLOCKS; i++) {
                // is this the block we are looking for?
                if (blocks[i].getLocation() == targetLocation && blocks[i].use(targetLocation, tick())) {
                    // yes and we locked it
                    state.set(STATE_READY);
                    return blocks[i];
                }
                // is this the oldest block?
                long t = blocks[i].getLastHit();
                if (t < oldestTime) {
                    oldestIndex = i;
                    oldestTime = t;
                    oldestLocation = blocks[i].getLocation();
                }
            }
            // we did not find the block, try to reclaim the oldest one
            FileBlockTS target = blocks[oldestIndex];
            if (target.getLastHit() == oldestTime // the time did not change
                    && target.getLocation() == oldestLocation // still the same location
                    && target.reclaim(targetLocation, channel, size.get(), tick()) // try to reclaim
                    ) {
                // update the file data
                extendSizeTo(Math.max(size.get(), targetLocation + FileBlock.BLOCK_SIZE));
                if (target.use(targetLocation, tick())) {
                    // we got the block
                    state.set(STATE_READY);
                    return target;
                }
            }
        }
    }

    @Override
    public IOElement onAccessRequested(IOAccess access) throws StorageException {
        return getBlockFor(access.getLocation());
    }

    @Override
    public void onAccessTerminated(IOAccess access, IOElement element) throws StorageException {
        ((FileBlockTS) element).release();
    }

    @Override
    public void close() throws IOException {
        while (true) {
            int s = state.get();
            if (s == STATE_CLOSED)
                throw new IOException("The file is already closed");
            if (state.compareAndSet(STATE_READY, STATE_CLOSED))
                break;
        }
        channel.close();
    }
}
