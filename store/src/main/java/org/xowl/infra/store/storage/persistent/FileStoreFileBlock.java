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
import java.nio.channels.FileLock;
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
     * The block is locked by a writing operation
     */
    public static final int BLOCK_STATE_LOCKED = 3;
    /**
     * The block is currently used by a read operation
     */
    public static final int BLOCK_STATE_READING = 4;

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
    public static final char FLAG_REUSE_EMPTY_ENTRIES = 0x0001;
    /**
     * The maximum size of the payload of an entry in a page
     */
    public static final int MAX_ENTRY_SIZE = BLOCK_SIZE - PAGE_HEADER_SIZE - PAGE_ENTRY_INDEX_SIZE;
    /**
     * The number of bytes required in addition to an entry's payload
     */
    public static final int ENTRY_OVERHEAD = PAGE_ENTRY_INDEX_SIZE;

    /**
     * The state of this block
     */
    private final AtomicInteger state;
    /**
     * The associated buffer
     */
    private final ByteBuffer buffer;
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
        this.buffer = ByteBuffer.allocate(BLOCK_SIZE);
        this.location = -1;
        this.lastHit = Long.MIN_VALUE;
        this.isDirty = false;
    }

    /**
     * Tries to reserve this block
     *
     * @param location The location for the block
     * @param channel  The originating file channel
     * @param fileSize The current size of the file
     * @param time     The current time
     * @return The reservation status
     */
    public boolean reserve(long location, FileChannel channel, long fileSize, long time) {
        int current = state.get();
        if (current == BLOCK_STATE_FREE && state.compareAndSet(BLOCK_STATE_FREE, BLOCK_STATE_RESERVED)) {
            // the block is free and reserved
            this.location = location;
            this.lastHit = time;
            this.isDirty = false;
            if (this.location < fileSize) {
                try (FileLock lock = channel.lock()) {
                    channel.position(location);
                    channel.read(buffer);
                } catch (IOException exception) {
                    zeroes();
                }
            } else {
                zeroes();
            }
            state.compareAndSet(BLOCK_STATE_RESERVED, BLOCK_STATE_READY);
            return true;
        }
        while (true) {
            if (this.location != location)
                return false;
            current = state.get();
            if (current >= BLOCK_STATE_READY)
                return true;
            if (current < BLOCK_STATE_RESERVED)
                return false;
        }
    }

    /**
     * Commits any outstanding changes to the backing file
     *
     * @param channel The originating file channel
     * @param time    The current time
     * @return Whether the operation succeeded
     */
    public boolean commit(FileChannel channel, long time) {
        if (!isDirty)
            return true;
        if (!onWriteBegin(time))
            return false;
        boolean success = true;
        try (FileLock lock = channel.lock()) {
            channel.position(location);
            channel.write(buffer);
            isDirty = false;
        } catch (IOException exception) {
            success = false;
        }
        onWriteEnd();
        return success;
    }

    /**
     * Rollbacks any outstanding changes
     *
     * @param channel The originating file channel
     * @param time    The current time
     * @return Whether the operation succeeded
     */
    public boolean rollback(FileChannel channel, long time) {
        if (!isDirty)
            return true;
        if (!onWriteBegin(time))
            return false;
        boolean success = true;
        try (FileLock lock = channel.lock()) {
            channel.position(location);
            channel.read(buffer);
            isDirty = false;
        } catch (IOException exception) {
            success = false;
        }
        onWriteEnd();
        return success;
    }

    /**
     * Reclaims this block
     * Commits any outstanding changes to the backing file
     *
     * @return Whether the block was reclaimed
     */
    public boolean reclaim(FileChannel channel, long time) {
        int current = state.get();
        while (true) {
            // if the current block is not usable, exit
            if (current < BLOCK_STATE_READY)
                return false;
            // block is dirty, try to lock, commit and reclaim in one go
            if (isDirty) {
                if (!onWriteBegin(time))
                    return false;
                try (FileLock lock = channel.lock()) {
                    channel.position(location);
                    channel.write(buffer);
                    channel.force(false);
                    isDirty = false;
                } catch (IOException exception) {
                    onWriteEnd();
                    return false;
                }
                location = -1;
                return state.compareAndSet(BLOCK_STATE_LOCKED, BLOCK_STATE_FREE);
            }
            // if the state is ready is is successfully locked for reclamation
            if (current == BLOCK_STATE_READY && state.compareAndSet(BLOCK_STATE_READY, BLOCK_STATE_FREE)) {
                location = -1;
                return true;
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
    public long getSize() {
        return BLOCK_SIZE;
    }

    @Override
    public boolean canRead(long index, int length) {
        return (index + length <= BLOCK_SIZE);
    }

    @Override
    public boolean onReadBegin(long time) {
        while (true) {
            int current = state.get();
            // if the current block is not usable, exit
            if (current < BLOCK_STATE_READY)
                return false;
            // if the block is being locked for writing, retry
            if (current == BLOCK_STATE_LOCKED)
                continue;
            // if the block is ready but not used, try to set it as reading
            if (current == BLOCK_STATE_READY && state.compareAndSet(BLOCK_STATE_READY, BLOCK_STATE_READING)) {
                lastHit = time;
                return true;
            }
            // if the block is being read, try to increment
            if (current >= BLOCK_STATE_READING && state.compareAndSet(current, current + 1)) {
                lastHit = time;
                return true;
            }
        }
    }

    @Override
    public void onReadEnd() {
        while (true) {
            int current = state.get();
            // if this is the only read operation, back to the ready state
            if (current == BLOCK_STATE_READING && state.compareAndSet(BLOCK_STATE_READING, BLOCK_STATE_READY))
                return;
            // there is more than one reading operation, decrement
            if (current > BLOCK_STATE_READING && state.compareAndSet(current, current - 1))
                return;
        }
    }

    @Override
    public boolean onWriteBegin(long time) {
        while (true) {
            int current = state.get();
            // if the current block is not usable, exit
            if (current < BLOCK_STATE_READY)
                return false;
            // if the block is being locked for writing, retry
            if (current == BLOCK_STATE_LOCKED)
                continue;
            // if the block is ready but not used, try to set it as reading
            if (current == BLOCK_STATE_READY && state.compareAndSet(BLOCK_STATE_READY, BLOCK_STATE_LOCKED)) {
                lastHit = time;
                return true;
            }
        }
    }

    @Override
    public void onWriteEnd() {
        state.compareAndSet(BLOCK_STATE_LOCKED, BLOCK_STATE_READY);
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
    public void close() throws IOException {
        location = -1;
        isDirty = false;
    }
}
