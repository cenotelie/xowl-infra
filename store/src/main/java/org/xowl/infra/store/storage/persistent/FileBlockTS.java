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

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Represents a block of contiguous data in a file with thread-safe accesses
 * <p>
 * A block is designed to be thread-safe when it is accessed through a transaction.
 * Multiple threads can read from the block at the same time, but only one thread can lock and write to it at any given time.
 * Writing to the block prevents other threads from reading.
 * This structure assumes:
 * 1) A high number of read compared to a low number of write
 * 2) The quick release of the block when a write has exclusive use
 * This structure uses lock-free mechanisms for thread safety and uses a state-machine to track its use:
 * <p>
 * +--------------+------------------------------------------------------------+
 * | State        | Description                                                |
 * +--------------+------------------------------------------------------------+
 * | Free         | The block is not assigned to a location in the file        |
 * | Reserved     | The block is reserved for a location and being initialized |
 * | Ready        | The block is ready for read and write operations           |
 * | ExclusiveUse | The block is reserved for an exclusive operation (write)   |
 * | SharedUse(n) | The block is used in a non-exclusive manner (read)         |
 * +--------------+------------------------------------------------------------+
 * <p>
 * +------------------+------------------------------------+
 * | Operation        | Transition from state to state     |
 * +------------------+------------------------------------+
 * | reserve:         | Free --&gt; Reserved --&gt; Ready  |
 * | useShared:       | Ready        --&gt; SharedUse(0)   |
 * | useShared:       | SharedUse(n) --&gt; SharedUse(n+1) |
 * | releaseShared:   | SharedUse(n) --&gt; SharedUse(n-1) |
 * | releaseShared:   | SharedUse(0) --&gt; Ready          |
 * | useExclusive:    | SharedUse(0) --&gt; ExclusiveUse   |
 * | releaseExclusive:| ExclusiveUse --&gt; SharedUse(0)   |
 * | reclaim:         | Ready --&gt; Free                  |
 * +------------------+------------------------------------+
 *
 * @author Laurent Wouters
 */
class FileBlockTS extends FileBlock {
    /**
     * The block is free, i.e. not assigned to any location
     */
    private static final int BLOCK_STATE_FREE = 0;
    /**
     * The block is reserved, i.e. is going to contain data but is not ready yet
     */
    private static final int BLOCK_STATE_RESERVED = 1;
    /**
     * The block exists and is ready for IO
     */
    private static final int BLOCK_STATE_READY = 2;
    /**
     * The block is used in an exclusive IO operation
     */
    private static final int BLOCK_STATE_EXCLUSIVE_USE = 3;
    /**
     * The block is used by one or more users in a shared manner
     */
    private static final int BLOCK_STATE_SHARED_USE = 4;

    /**
     * Gets the name of the block state
     *
     * @param state The block state
     * @return The associated name
     */
    private static String stateName(int state) {
        if (state < 0)
            return "INVALID(" + state + ")";
        switch (state) {
            case BLOCK_STATE_FREE:
                return "READY";
            case BLOCK_STATE_RESERVED:
                return "RESERVED";
            case BLOCK_STATE_READY:
                return "READY";
            case BLOCK_STATE_EXCLUSIVE_USE:
                return "EXCLUSIVE_USE";
            case BLOCK_STATE_SHARED_USE:
                return "SHARED_USE(0)";
            default:
                return "SHARED_USE(" + (state - BLOCK_STATE_SHARED_USE) + ")";
        }
    }

    /**
     * The state of this block
     */
    private final AtomicInteger state;

    /**
     * Initializes this structure
     */
    public FileBlockTS() {
        this.state = new AtomicInteger(BLOCK_STATE_FREE);
    }

    /**
     * Tries to reserve this block
     *
     * @param location The location for the block
     * @param channel  The originating file channel
     * @param fileSize The current size of the file
     * @param time     The current time
     * @return The reservation status
     * @throws StorageException When an IO error occurs
     */
    public boolean reserve(long location, FileChannel channel, long fileSize, long time) throws StorageException {
        while (true) {
            if (this.location != -1 && this.location != location)
                // the block was reserved for another location in the meantime
                return false;
            int current = state.get();
            if (current >= BLOCK_STATE_READY) {
                // the block was made ready by another thread
                touch(time);
                return true;
            }
            if (current == BLOCK_STATE_FREE && reserveFromFree(location, channel, fileSize, time))
                // the block was free and the reservation went ok
                return true;
        }
    }

    /**
     * Tries to reserve this block for location assuming it is free
     *
     * @param location The location for the block
     * @param channel  The originating file channel
     * @param fileSize The current size of the file
     * @param time     The current time
     * @return Whether the operation was successful
     * @throws StorageException When an IO error occurs
     */
    private boolean reserveFromFree(long location, FileChannel channel, long fileSize, long time) throws StorageException {
        if (!state.compareAndSet(BLOCK_STATE_FREE, BLOCK_STATE_RESERVED))
            return false;
        // the block was free and is now reserved
        this.location = location;
        if (this.buffer == null)
            this.buffer = ByteBuffer.allocate(BLOCK_SIZE);
        this.isDirty = false;
        if (this.location < fileSize) {
            try {
                load(channel);
            } catch (IOException exception) {
                state.compareAndSet(BLOCK_STATE_RESERVED, BLOCK_STATE_FREE);
                throw new StorageException(exception, "Failed to read block at 0x" + Long.toHexString(location));
            }
        } else {
            zeroes();
        }
        touch(time);
        state.compareAndSet(BLOCK_STATE_RESERVED, BLOCK_STATE_READY);
        return true;
    }

    /**
     * Attempt to get a shared use of this block for the specified expected location
     *
     * @param location The expected location for this block
     * @param time     The current time
     * @return true if the shared use is granted, false if the effective location of this block was not the expected one
     * @throws StorageException When the block is in a bad state
     */
    public boolean useShared(long location, long time) throws StorageException {
        while (true) {
            int current = state.get();
            if (current < BLOCK_STATE_READY)
                // the block may have been reclaimed in the meantime
                return false;
            if (current == BLOCK_STATE_READY) {
                if (state.compareAndSet(BLOCK_STATE_READY, BLOCK_STATE_SHARED_USE)) {
                    if (this.location == location) {
                        touch(time);
                        return true;
                    }
                    // this is the wrong location, release the block and fail
                    releaseShared();
                    return false;
                }
            } else if (current >= BLOCK_STATE_SHARED_USE) {
                if (state.compareAndSet(current, current + 1)) {
                    if (this.location == location) {
                        touch(time);
                        return true;
                    }
                    // this is the wrong location, release the block and fail
                    releaseShared();
                    return false;
                }
            }
        }
    }

    /**
     * Releases a share use of this block
     *
     * @throws StorageException When the block is in a bad state
     */
    public void releaseShared() throws StorageException {
        while (true) {
            int current = state.get();
            if (current <= BLOCK_STATE_EXCLUSIVE_USE)
                throw new StorageException("Bad block state: " + stateName(current) + ", expected SHARED_USE");
            else if (current == BLOCK_STATE_SHARED_USE && state.compareAndSet(BLOCK_STATE_SHARED_USE, BLOCK_STATE_READY))
                return;
            else if (current > BLOCK_STATE_SHARED_USE && state.compareAndSet(current, current - 1))
                return;
        }
    }

    /**
     * Attempt to get an exclusive use of this block
     *
     * @throws StorageException When the block is in a bad state
     */
    public void useExclusive() throws StorageException {
        while (true) {
            int current = state.get();
            if (current <= BLOCK_STATE_READY)
                throw new StorageException("Bad block state: " + stateName(current) + ", expected SHARED_USE");
            if (current == BLOCK_STATE_SHARED_USE && state.compareAndSet(BLOCK_STATE_SHARED_USE, BLOCK_STATE_EXCLUSIVE_USE))
                return;
        }
    }

    /**
     * Releases the exclusive use of this block
     *
     * @throws StorageException When the block is in a bad state
     */
    public void releaseExclusive() throws StorageException {
        int current = state.get();
        if (!state.compareAndSet(BLOCK_STATE_EXCLUSIVE_USE, BLOCK_STATE_SHARED_USE))
            throw new StorageException("Bad block state: " + stateName(current) + ", expected EXCLUSIVE_USE");
    }

    /**
     * Reclaims this block
     * Commits any outstanding changes to the backing file
     *
     * @param channel  The originating file channel
     * @param location The expected location for this block
     * @param time     The current time
     * @return Whether the block was reclaimed
     * @throws StorageException When an IO error occurs
     */
    public boolean reclaim(FileChannel channel, long location, long time) throws StorageException {
        if (doFlush(channel, location, time)) {
            this.location = -1;
            return state.compareAndSet(BLOCK_STATE_EXCLUSIVE_USE, BLOCK_STATE_FREE);
        }
        return false;
    }

    /**
     * Flushes any outstanding changes to the backend file
     *
     * @param channel The originating file channel
     * @param time    The current time
     * @throws StorageException When an IO error occurs
     */
    public void flush(FileChannel channel, long time) throws StorageException {
        if (doFlush(channel, location, time)) {
            releaseExclusive();
            releaseShared();
        }
    }

    /**
     * Gets an exclusive use of this block, flushes any outstanding changes to the backend file
     * This method does not release the exclusive use
     *
     * @param channel  The originating file channel
     * @param location The expected location for this block
     * @param time     The current time
     * @return Whether the operation succeeded
     * @throws StorageException When an IO error occurs
     */
    private boolean doFlush(FileChannel channel, long location, long time) throws StorageException {
        if (!useShared(location, time))
            // reclaimed for another location
            return false;
        try {
            useExclusive();
            serialize(channel);
            return true;
        } catch (IOException exception) {
            releaseExclusive();
            releaseShared();
            throw new StorageException(exception, "Failed to write block at 0x" + Long.toHexString(location));
        }
    }

    @Override
    public void close() throws StorageException {
        releaseShared();
    }
}
