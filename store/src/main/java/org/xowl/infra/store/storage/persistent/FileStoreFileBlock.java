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

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Represents a block in a file
 * A block may be interpreted as a page of entries, in which case the block has the following layout:
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
class FileStoreFileBlock implements IOElement {
    /**
     * The number of bits to use in order to represent an index within a block
     */
    public static final int BLOCK_INDEX_LENGTH = 13;
    /**
     * The size of a block in bytes
     */
    public static final int BLOCK_SIZE = 1 << BLOCK_INDEX_LENGTH;
    /**
     * The mask for the index within a block
     */
    public static final long INDEX_MASK_LOWER = BLOCK_SIZE - 1;

    /**
     * The block is free, i.e. not assigned to any location
     */
    public static final int BLOCK_STATE_FREE = 0;
    /**
     * The block is reserved, i.e. is going to contain data but is not ready yet
     */
    public static final int BLOCK_STATE_RESERVED = 1;
    /**
     * The block exists and is ready for IO
     */
    public static final int BLOCK_STATE_READY = 2;
    /**
     * The block is used in an exclusive IO operation
     */
    public static final int BLOCK_STATE_EXCLUSIVE_USE = 3;
    /**
     * The block is used by one or more users in a shared manner
     */
    public static final int BLOCK_STATE_SHARED_USE = 4;

    /**
     * The version of the page layout to use
     */
    public static final char PAGE_LAYOUT_VERSION = 1;
    /**
     * The size of the page header in bytes
     * char: Layout version (2 bytes)
     * char: Flags (2 bytes)
     * char: Number of entries (2 bytes)
     * char: Offset to start of free space (2 bytes)
     * char: Offset to start of data content (2 bytes)
     */
    public static final int PAGE_HEADER_SIZE = 2 + 2 + 2 + 2 + 2;
    /**
     * The size of an entry in the entry table of a page (in bytes)
     * char: offset (2 bytes)
     * char: length (2 bytes)
     */
    public static final int PAGE_ENTRY_INDEX_SIZE = 2 + 2;
    /**
     * Flag whether the page shall reuse the space of removed entries
     */
    public static final char PAGE_FLAG_REUSE_EMPTY_ENTRIES = 0x0001;
    /**
     * The maximum size of the payload of an entry in a page
     */
    public static final int MAX_ENTRY_SIZE = BLOCK_SIZE - PAGE_HEADER_SIZE - PAGE_ENTRY_INDEX_SIZE;

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
                return "SHARED_USE(" + state + ")";
        }
    }

    /**
     * The state of this block
     */
    private final AtomicInteger state;
    /**
     * The associated buffer
     */
    private ByteBuffer buffer;
    /**
     * The location of this block in the parent file
     */
    private volatile long location;
    /**
     * The timestamp for the last time this block was hit
     */
    private volatile long lastHit;
    /**
     * Whether this block is dirty
     */
    private volatile boolean isDirty;

    /**
     * Gets the location of this block in the parent file
     *
     * @return The location of this block in the parent file
     */
    public long getLocation() {
        return location;
    }

    /**
     * Gets the timestamp for the last time this block was hit
     *
     * @return The timestamp for the last time this block was hit
     */
    public long getLastHit() {
        return lastHit;
    }

    /**
     * Initializes this structure
     */
    public FileStoreFileBlock() {
        this.state = new AtomicInteger(BLOCK_STATE_FREE);
        this.buffer = null;
        this.location = -1;
        this.lastHit = Long.MIN_VALUE;
        this.isDirty = false;
    }

    /**
     * Touches this block
     *
     * @param time The current time
     */
    private void touch(long time) {
        lastHit = Math.max(lastHit, time);
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
                int total = 0;
                buffer.position(0);
                while (total < BLOCK_SIZE) {
                    int read = channel.read(buffer, location + total);
                    if (read == -1)
                        throw new IOException("Unexpected end of stream");
                    total += read;
                }
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
        if (!useShared(location, time))
            // reclaimed for another location
            return false;
        serializeBlock(channel);
        this.location = -1;
        return state.compareAndSet(BLOCK_STATE_EXCLUSIVE_USE, BLOCK_STATE_FREE);
    }

    /**
     * Flushes any outstanding changes to the backend file
     *
     * @param channel The originating file channel
     * @param time    The current time
     * @throws StorageException When an IO error occurs
     */
    public void flush(FileChannel channel, long time) throws StorageException {
        if (!useShared(location, time))
            // reclaimed for another location
            return;
        serializeBlock(channel);
        releaseExclusive();
        releaseShared();
    }

    /**
     * Serializes this block to the underlying file channel
     *
     * @param channel The originating file channel
     * @throws StorageException When an IO error occurs
     */
    private void serializeBlock(FileChannel channel) throws StorageException {
        useExclusive();
        if (isDirty) {
            try {
                buffer.position(0);
                int total = 0;
                buffer.position(0);
                while (total < BLOCK_SIZE) {
                    int written = channel.write(buffer, location + total);
                    total += written;
                }
                isDirty = false;
            } catch (IOException exception) {
                releaseExclusive();
                releaseShared();
                throw new StorageException(exception, "Failed to write block at 0x" + Long.toHexString(location));
            }
        }
    }

    /**
     * Arrays of empty data used for zeroing the content of a buffer
     */
    private static final byte[] ZEROES = new byte[256];

    /**
     * Zeroes the content of the buffer
     */
    private void zeroes() {
        if (buffer.hasArray()) {
            Arrays.fill(buffer.array(), (byte) 0);
            return;
        }
        buffer.position(0);
        for (int i = 0; i != BLOCK_SIZE; i += ZEROES.length) {
            buffer.put(ZEROES);
        }
    }

    @Override
    public void lock() throws StorageException {
        useExclusive();
    }

    @Override
    public void release() throws StorageException {
        while (true) {
            int current = state.get();
            if (current <= BLOCK_STATE_READY)
                throw new StorageException("Bad block state: " + stateName(current) + ", expected SHARED_USE or EXCLUSIVE_USE");
            else if (current == BLOCK_STATE_EXCLUSIVE_USE && state.compareAndSet(BLOCK_STATE_EXCLUSIVE_USE, BLOCK_STATE_READY))
                return;
            else if (current == BLOCK_STATE_SHARED_USE && state.compareAndSet(BLOCK_STATE_SHARED_USE, BLOCK_STATE_READY))
                return;
            else if (current > BLOCK_STATE_SHARED_USE && state.compareAndSet(current, current - 1))
                return;
        }
    }

    @Override
    public long getSize() {
        return BLOCK_SIZE;
    }

    @Override
    public byte readByte(long index) throws StorageException {
        return buffer.get((int) (index & INDEX_MASK_LOWER));
    }

    @Override
    public byte[] readBytes(long index, int length) throws StorageException {
        byte[] result = new byte[length];
        readBytes(index, result, 0, length);
        return result;
    }

    @Override
    public synchronized void readBytes(long index, byte[] buffer, int start, int length) throws StorageException {
        this.buffer.position((int) (index & INDEX_MASK_LOWER));
        this.buffer.get(buffer, start, length);
    }

    @Override
    public char readChar(long index) throws StorageException {
        return buffer.getChar((int) (index & INDEX_MASK_LOWER));
    }

    @Override
    public int readInt(long index) throws StorageException {
        return buffer.getInt((int) (index & INDEX_MASK_LOWER));
    }

    @Override
    public long readLong(long index) throws StorageException {
        return buffer.getLong((int) (index & INDEX_MASK_LOWER));
    }

    @Override
    public float readFloat(long index) throws StorageException {
        return buffer.getFloat((int) (index & INDEX_MASK_LOWER));
    }

    @Override
    public double readDouble(long index) throws StorageException {
        return buffer.getDouble((int) (index & INDEX_MASK_LOWER));
    }

    @Override
    public void writeByte(long index, byte value) throws StorageException {
        buffer.put((int) (index & INDEX_MASK_LOWER), value);
        isDirty = true;
    }

    @Override
    public void writeBytes(long index, byte[] value) throws StorageException {
        writeBytes(index, value, 0, value.length);
    }

    @Override
    public void writeBytes(long index, byte[] buffer, int start, int length) throws StorageException {
        this.buffer.position((int) (index & INDEX_MASK_LOWER));
        this.buffer.put(buffer, start, length);
        isDirty = true;
    }

    @Override
    public void writeChar(long index, char value) throws StorageException {
        buffer.putChar((int) (index & INDEX_MASK_LOWER), value);
        isDirty = true;
    }

    @Override
    public void writeInt(long index, int value) throws StorageException {
        buffer.putInt((int) (index & INDEX_MASK_LOWER), value);
        isDirty = true;
    }

    @Override
    public void writeLong(long index, long value) throws StorageException {
        buffer.putLong((int) (index & INDEX_MASK_LOWER), value);
        isDirty = true;
    }

    @Override
    public void writeFloat(long index, float value) throws StorageException {
        buffer.putFloat((int) (index & INDEX_MASK_LOWER), value);
        isDirty = true;
    }

    @Override
    public void writeDouble(long index, double value) throws StorageException {
        buffer.putDouble((int) (index & INDEX_MASK_LOWER), value);
        isDirty = true;
    }

    @Override
    public void close() {
        location = -1;
        isDirty = false;
    }
}
