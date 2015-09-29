/*******************************************************************************
 * Copyright (c) 2015 Laurent Wouters
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

package org.xowl.store.storage.persistent;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Represents a persisted binary file
 *
 * @author Laurent Wouters
 */
class PersistedFile {
    /**
     * The number of bits to use in order to represent an index within a block
     */
    public static final int BLOCK_INDEX_LENGTH = 13;
    /**
     * The size of a block in bytes
     */
    public static final int BLOCK_SIZE = 1 << BLOCK_INDEX_LENGTH;
    /**
     * The upper index mask
     */
    private static final long INDEX_MASK_LOWER = BLOCK_SIZE - 1;
    /**
     * The lower index mask
     */
    private static final long INDEX_MASK_UPPER = ~INDEX_MASK_LOWER;

    /**
     * The maximum number of loaded blocks
     */
    private static final int MAX_LOADED_BLOCKS = 256;

    /**
     * The file channel
     */
    private final FileChannel channel;
    /**
     * The key radical for this file
     */
    private final long keyRadical;
    /**
     * A buffer for reading and writing
     */
    private final ByteBuffer buffer;
    /**
     * The block buffers
     */
    private final MappedByteBuffer[] blockBuffers;
    /**
     * The location of the respective blocks
     */
    private final long[] blockLocations;
    /**
     * The timestamp the respective blocks have been hit
     */
    private final long[] blockLastHits;
    /**
     * Whether the respective block is dirty
     */
    private final boolean[] blockIsDirty;
    /**
     * The page accesses for the respective blocks
     */
    private final PersistedFilePage[] blockPages;
    /**
     * The number of currently loaded blocks
     */
    private int blockCount;
    /**
     * The index of the current block
     */
    private int currentBlock;
    /**
     * The current index in this file
     */
    private long index;
    /**
     * The current time (for hitting blocks)
     */
    private long time;

    /**
     * Gets the current index in this file
     *
     * @return The current index in this file
     */
    public long getIndex() {
        return index;
    }

    /**
     * Gets the size of this file
     *
     * @return The size of this file
     * @throws IOException When an IO operation failed
     */
    public long getSize() throws IOException {
        return channel.size();
    }

    /**
     * Gets the remaining amount of data in the current block
     *
     * @return The remaining amount of data
     */
    public int getRemainingInBlock() {
        return BLOCK_SIZE - (int) (index & INDEX_MASK_LOWER);
    }

    /**
     * Initializes this data file
     *
     * @param file The file location
     * @throws IOException When the backing file cannot be accessed
     */
    public PersistedFile(File file) throws IOException {
        this(file, 0);
    }

    /**
     * Initializes this data file
     *
     * @param file       The file location
     * @param keyRadical The key radical for this file
     * @throws IOException When the backing file cannot be accessed
     */
    public PersistedFile(File file, long keyRadical) throws IOException {
        this.channel = FileChannel.open(file.toPath(), StandardOpenOption.CREATE, StandardOpenOption.READ, StandardOpenOption.WRITE);
        this.keyRadical = keyRadical;
        this.buffer = ByteBuffer.allocate(8);
        this.blockBuffers = new MappedByteBuffer[MAX_LOADED_BLOCKS];
        this.blockLocations = new long[MAX_LOADED_BLOCKS];
        this.blockLastHits = new long[MAX_LOADED_BLOCKS];
        this.blockIsDirty = new boolean[MAX_LOADED_BLOCKS];
        this.blockPages = new PersistedFilePage[MAX_LOADED_BLOCKS];
        this.blockCount = 0;
        this.currentBlock = -1;
        this.index = 0;
        this.time = 0;
    }

    /**
     * Commits any outstanding changes
     *
     * @return The persistent file
     * @throws IOException When an IO operation failed
     */
    public PersistedFile commit() throws IOException {
        for (int i = 0; i != blockCount; i++) {
            if (blockIsDirty[i]) {
                if (blockPages[i] != null)
                    blockPages[i].onCommit();
                blockBuffers[i].force();
            }
        }
        channel.force(true);
        for (int i = 0; i != blockCount; i++) {
            blockIsDirty[i] = false;
        }
        return this;
    }

    /**
     * Flushes any outstanding changes and closes this file
     *
     * @throws IOException When an IO operation failed
     */
    public void close() throws IOException {
        commit();
        channel.close();
    }

    /**
     * Gets the page that contains the current index
     *
     * @return The page that contains the current index
     * @throws IOException      When an IO operation failed
     * @throws StorageException When the page version does not match the expected one
     */
    public PersistedFilePage getPage() throws IOException, StorageException {
        return getPageAt(index);
    }

    /**
     * Gets the n-th page in this file
     *
     * @param index The index of a page
     * @return The n-th page
     * @throws IOException      When an IO operation failed
     * @throws StorageException When the page version does not match the expected one
     */
    public PersistedFilePage getPage(int index) throws IOException, StorageException {
        long originalIndex = this.index;
        long targetLocation = index * BLOCK_SIZE;
        if (targetLocation < getSize()) {
            // is the block loaded
            for (int i = 0; i != blockCount; i++) {
                if (blockLocations[i] == targetLocation) {
                    if (blockPages[i] == null)
                        blockPages[i] = new PersistedFilePage(this, targetLocation, keyRadical + (index << 16));
                    seek(originalIndex);
                    return blockPages[i];
                }
            }
            // creating the page data will force the block to be loaded
        } else {
            // the page does not exist in the backend
            // force the block to be allocated
            seek(targetLocation);
            prepareIOAt();
        }
        PersistedFilePage result = new PersistedFilePage(this, targetLocation, keyRadical + (index << 16));
        if (blockLocations[currentBlock] != targetLocation)
            throw new StorageException("Failed to allocate the page");
        blockPages[currentBlock] = result;
        seek(originalIndex);
        return result;
    }

    /**
     * Gets the page that contains the specified location
     *
     * @param location A location
     * @return The page that contains the specified location
     * @throws IOException      When an IO operation failed
     * @throws StorageException When the page version does not match the expected one
     */
    public PersistedFilePage getPageAt(long location) throws IOException, StorageException {
        return getPage((int) ((location & INDEX_MASK_UPPER) / BLOCK_SIZE));
    }

    /**
     * Gets the page that contains the specified key
     *
     * @param key A key
     * @return The page that contains the specified key
     * @throws IOException      When an IO operation failed
     * @throws StorageException When the page version does not match the expected one
     */
    public PersistedFilePage getPageFor(long key) throws IOException, StorageException {
        return getPage((int) ((key - keyRadical) >>> 16));
    }

    /**
     * Positions the index of this file
     *
     * @param index The new index
     * @return The persistent file
     */
    public PersistedFile seek(long index) {
        if (index < 0)
            throw new IndexOutOfBoundsException("The index must be within the file");
        if (this.index == index)
            // do nothing
            return this;
        this.index = index;
        // look for a corresponding block
        for (int i = 0; i != blockCount; i++) {
            long location = blockLocations[i];
            if (index >= location && index < location + BLOCK_SIZE) {
                blockBuffers[i].position((int) (index & INDEX_MASK_LOWER));
                currentBlock = i;
                return this;
            }
        }
        currentBlock = -1;
        return this;
    }

    /**
     * Positions the index of this file onto the next free block
     *
     * @return The persistent file
     */
    public PersistedFile seekNextBlock() throws IOException {
        long size = getSize();
        if ((size & INDEX_MASK_UPPER) == size)
            // already at the beginning of the next free block
            return this;
        return seek((size & INDEX_MASK_UPPER) + BLOCK_SIZE);
    }

    /**
     * Gets whether the specified amount of bytes can be read at the current index
     *
     * @param length The number of bytes to read
     * @return true if this is legal to read
     * @throws IOException When an IO operation failed
     */
    public boolean canRead(int length) throws IOException {
        return (index + length > getSize());
    }

    /**
     * Reads a single byte at the current index
     *
     * @return The byte
     * @throws IOException When an IO operation failed
     */
    public byte readByte() throws IOException {
        if (!canRead(1))
            throw new IndexOutOfBoundsException("Cannot read the specified amount of data at this index");
        prepareIOAt();
        byte value = blockBuffers[currentBlock].get((int) (index & INDEX_MASK_LOWER));
        index++;
        return value;
    }

    /**
     * Reads a specified number of bytes a the current index
     *
     * @param length The number of bytes to read
     * @return The bytes
     * @throws IOException When an IO operation failed
     */
    public byte[] readBytes(int length) throws IOException {
        if (!canRead(length))
            throw new IndexOutOfBoundsException("Cannot read the specified amount of data at this index");
        byte[] result = new byte[length];
        readBytes(result, 0, length);
        return result;
    }

    /**
     * Reads a specified number of bytes a the current index
     *
     * @param buffer The buffer to fill
     * @param index  The index in the buffer to start filling at
     * @param length The number of bytes to read
     * @throws IOException When an IO operation failed
     */
    public void readBytes(byte[] buffer, int index, int length) throws IOException {
        if (!canRead(1))
            throw new IndexOutOfBoundsException("Cannot read the specified amount of data at this index");
        if (index < 0 || index + length > buffer.length)
            throw new IndexOutOfBoundsException("Out of bounds index and length for the specified buffer");
        int remainingInBlock = BLOCK_SIZE - (int) (this.index & INDEX_MASK_LOWER);
        int remainingLength = length;
        int targetIndex = index;
        while (remainingLength > remainingInBlock) {
            prepareIOAt();
            blockBuffers[currentBlock].get(buffer, targetIndex, remainingInBlock);
            remainingLength -= remainingInBlock;
            targetIndex += remainingInBlock;
            this.index += remainingInBlock;
            remainingInBlock = BLOCK_SIZE;
        }
        prepareIOAt();
        blockBuffers[currentBlock].get(buffer, targetIndex, remainingLength);
        this.index += remainingLength;
    }

    /**
     * Reads a single char at the current index
     *
     * @return The char
     * @throws IOException When an IO operation failed
     */
    public char readChar() throws IOException {
        if (!canRead(2))
            throw new IndexOutOfBoundsException("Cannot read the specified amount of data at this index");
        prepareIOAt();
        if ((int) (this.index & INDEX_MASK_LOWER) + 2 <= BLOCK_SIZE) {
            // within the same block
            char value = blockBuffers[currentBlock].getChar();
            index += 2;
            return value;
        }
        buffer.position(0);
        buffer.put(blockBuffers[currentBlock].get());
        index++;
        // next block
        prepareIOAt();
        buffer.put(blockBuffers[currentBlock].get());
        index++;
        return buffer.getChar(0);
    }

    /**
     * Reads a single int at the current index
     *
     * @return The int
     * @throws IOException When an IO operation failed
     */
    public int readInt() throws IOException {
        if (!canRead(4))
            throw new IndexOutOfBoundsException("Cannot read the specified amount of data at this index");
        prepareIOAt();
        if ((int) (this.index & INDEX_MASK_LOWER) + 4 <= BLOCK_SIZE) {
            // within the same block
            int value = blockBuffers[currentBlock].getInt();
            index += 4;
            return value;
        }
        buffer.position(0);
        for (int i = 0; i != 4; i++) {
            buffer.put(blockBuffers[currentBlock].get());
            index++;
            if (i < 3 && (index & INDEX_MASK_LOWER) == 0) {
                prepareIOAt();
            }
        }
        return buffer.getInt(0);
    }

    /**
     * Reads a single long at the current index
     *
     * @return The long
     * @throws IOException When an IO operation failed
     */
    public long readLong() throws IOException {
        if (!canRead(8))
            throw new IndexOutOfBoundsException("Cannot read the specified amount of data at this index");
        prepareIOAt();
        if ((int) (this.index & INDEX_MASK_LOWER) + 8 <= BLOCK_SIZE) {
            // within the same block
            long value = blockBuffers[currentBlock].getLong();
            index += 8;
            return value;
        }
        buffer.position(0);
        for (int i = 0; i != 8; i++) {
            buffer.put(blockBuffers[currentBlock].get());
            index++;
            if (i < 7 && (index & INDEX_MASK_LOWER) == 0) {
                prepareIOAt();
            }
        }
        return buffer.getLong(0);
    }

    /**
     * Reads a single float at the current index
     *
     * @return The float
     * @throws IOException When an IO operation failed
     */
    public float readFloat() throws IOException {
        if (!canRead(4))
            throw new IndexOutOfBoundsException("Cannot read the specified amount of data at this index");
        prepareIOAt();
        if ((int) (this.index & INDEX_MASK_LOWER) + 4 <= BLOCK_SIZE) {
            // within the same block
            float value = blockBuffers[currentBlock].getFloat();
            index += 4;
            return value;
        }
        buffer.position(0);
        for (int i = 0; i != 4; i++) {
            buffer.put(blockBuffers[currentBlock].get());
            index++;
            if (i < 3 && (index & INDEX_MASK_LOWER) == 0) {
                prepareIOAt();
            }
        }
        return buffer.getFloat(0);
    }

    /**
     * Reads a single double at the current index
     *
     * @return The double
     * @throws IOException When an IO operation failed
     */
    public double readDouble() throws IOException {
        if (!canRead(8))
            throw new IndexOutOfBoundsException("Cannot read the specified amount of data at this index");
        prepareIOAt();
        if ((int) (this.index & INDEX_MASK_LOWER) + 8 <= BLOCK_SIZE) {
            // within the same block
            double value = blockBuffers[currentBlock].getDouble();
            index += 8;
            return value;
        }
        buffer.position(0);
        for (int i = 0; i != 8; i++) {
            buffer.put(blockBuffers[currentBlock].get());
            index++;
            if (i < 7 && (index & INDEX_MASK_LOWER) == 0) {
                prepareIOAt();
            }
        }
        return buffer.getDouble(0);
    }

    /**
     * Computes the SHA-1 value of the content at the current index and the specified length
     *
     * @param length The number of bytes
     * @return The SHA-1 hash value
     * @throws IOException When an IO operation failed
     */
    public byte[] digestSHA1(int length) throws IOException {
        if (!canRead(length))
            throw new IndexOutOfBoundsException("Cannot read the specified amount of data at this index");
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            int remainingInBlock = BLOCK_SIZE - (int) (this.index & INDEX_MASK_LOWER);
            int remainingLength = length;
            while (remainingLength > remainingInBlock) {
                prepareIOAt();
                md.update(blockBuffers[currentBlock].array(), blockBuffers[currentBlock].position(), remainingInBlock);
                remainingLength -= remainingInBlock;
                this.index += remainingInBlock;
                remainingInBlock = BLOCK_SIZE;
            }
            prepareIOAt();
            md.update(blockBuffers[currentBlock].array(), blockBuffers[currentBlock].position(), remainingLength);
            this.index += remainingLength;
            return md.digest();
        } catch (NoSuchAlgorithmException exception) {
            // cannot happen
            return null;
        }
    }

    /**
     * Writes a single byte at the current index
     *
     * @param value The byte to write
     * @throws IOException When an IO operation failed
     */
    public void writeByte(byte value) throws IOException {
        prepareIOAt();
        blockBuffers[currentBlock].put((int) (index & INDEX_MASK_LOWER), value);
        blockIsDirty[currentBlock] = true;
        index++;
    }

    /**
     * Writes bytes at the current index
     *
     * @param value The bytes to write
     * @throws IOException When an IO operation failed
     */
    public void writeBytes(byte[] value) throws IOException {
        writeBytes(value, 0, value.length);
    }

    /**
     * Writes bytes at the current index
     *
     * @param buffer The buffer with the bytes to write
     * @param index  The index in the buffer to start writing from
     * @param length The number of bytes to write
     * @throws IOException When an IO operation failed
     */
    public void writeBytes(byte[] buffer, int index, int length) throws IOException {
        if (index < 0 || index + length > buffer.length)
            throw new IndexOutOfBoundsException("Out of bounds index and length for the specified buffer");
        int remainingInBlock = BLOCK_SIZE - (int) (this.index & INDEX_MASK_LOWER);
        int remainingLength = length;
        int targetIndex = index;
        while (remainingLength > remainingInBlock) {
            prepareIOAt();
            blockBuffers[currentBlock].put(buffer, targetIndex, remainingInBlock);
            blockIsDirty[currentBlock] = true;
            remainingLength -= remainingInBlock;
            targetIndex += remainingInBlock;
            this.index += remainingInBlock;
            remainingInBlock = BLOCK_SIZE;
        }
        prepareIOAt();
        blockBuffers[currentBlock].put(buffer, targetIndex, remainingLength);
        blockIsDirty[currentBlock] = true;
        this.index += remainingLength;
    }

    /**
     * Writes a single char at the current index
     *
     * @param value The char to write
     * @throws IOException When an IO operation failed
     */
    public void writeChar(char value) throws IOException {
        prepareIOAt();
        if ((int) (this.index & INDEX_MASK_LOWER) + 2 <= BLOCK_SIZE) {
            // within the same block
            blockBuffers[currentBlock].putChar(value);
            blockIsDirty[currentBlock] = true;
            index += 2;
        } else {
            buffer.putChar(0, value);
            blockBuffers[currentBlock].put(buffer.get(0));
            blockIsDirty[currentBlock] = true;
            index++;
            // next block
            prepareIOAt();
            blockBuffers[currentBlock].put(buffer.get(1));
            blockIsDirty[currentBlock] = true;
            index++;
        }
    }

    /**
     * Writes a single int at the current index
     *
     * @param value The int to write
     * @throws IOException When an IO operation failed
     */
    public void writeInt(int value) throws IOException {
        prepareIOAt();
        blockIsDirty[currentBlock] = true;
        if ((int) (this.index & INDEX_MASK_LOWER) + 4 <= BLOCK_SIZE) {
            // within the same block
            blockBuffers[currentBlock].putInt(value);
            index += 4;
        } else {
            buffer.putInt(0, value);
            for (int i = 0; i != 4; i++) {
                blockBuffers[currentBlock].put(buffer.get(i));
                index++;
                if (i < 3 && (index & INDEX_MASK_LOWER) == 0) {
                    prepareIOAt();
                    blockIsDirty[currentBlock] = true;
                }
            }
        }
    }

    /**
     * Writes a single long at the current index
     *
     * @param value The long to write
     * @throws IOException When an IO operation failed
     */
    public void writeLong(long value) throws IOException {
        prepareIOAt();
        blockIsDirty[currentBlock] = true;
        if ((int) (this.index & INDEX_MASK_LOWER) + 8 <= BLOCK_SIZE) {
            // within the same block
            blockBuffers[currentBlock].putLong(value);
            index += 8;
        } else {
            buffer.putLong(0, value);
            for (int i = 0; i != 8; i++) {
                blockBuffers[currentBlock].put(buffer.get(i));
                index++;
                if (i < 7 && (index & INDEX_MASK_LOWER) == 0) {
                    prepareIOAt();
                    blockIsDirty[currentBlock] = true;
                }
            }
        }
    }

    /**
     * Writes a single float at the current index
     *
     * @param value The float to write
     * @throws IOException When an IO operation failed
     */
    public void writeFloat(float value) throws IOException {
        prepareIOAt();
        blockIsDirty[currentBlock] = true;
        if ((int) (this.index & INDEX_MASK_LOWER) + 4 <= BLOCK_SIZE) {
            // within the same block
            blockBuffers[currentBlock].putFloat(value);
            index += 4;
        } else {
            buffer.putFloat(0, value);
            for (int i = 0; i != 4; i++) {
                blockBuffers[currentBlock].put(buffer.get(i));
                index++;
                if (i < 3 && (index & INDEX_MASK_LOWER) == 0) {
                    prepareIOAt();
                    blockIsDirty[currentBlock] = true;
                }
            }
        }
    }

    /**
     * Writes a single double at the current index
     *
     * @param value The double to write
     * @throws IOException When an IO operation failed
     */
    public void writeDouble(double value) throws IOException {
        prepareIOAt();
        blockIsDirty[currentBlock] = true;
        if ((int) (this.index & INDEX_MASK_LOWER) + 8 <= BLOCK_SIZE) {
            // within the same block
            blockBuffers[currentBlock].putDouble(value);
            index += 8;
        } else {
            buffer.putDouble(0, value);
            for (int i = 0; i != 8; i++) {
                blockBuffers[currentBlock].put(buffer.get(i));
                index++;
                if (i < 7 && (index & INDEX_MASK_LOWER) == 0) {
                    prepareIOAt();
                    blockIsDirty[currentBlock] = true;
                }
            }
        }
    }

    /**
     * Prepares the blocks for IO operations at the current index
     */
    private void prepareIOAt() throws IOException {
        time++;
        if (currentBlock >= 0 && index >= blockLocations[currentBlock] && index < blockLocations[currentBlock] + BLOCK_SIZE) {
            // this is the current block
            blockBuffers[currentBlock].position((int) (index & INDEX_MASK_LOWER));
            blockLastHits[currentBlock] = time;
            return;
        }
        // is the block loaded
        long targetLocation = index & INDEX_MASK_UPPER;
        for (int i = 0; i != blockCount; i++) {
            if (blockLocations[i] == targetLocation) {
                currentBlock = i;
                blockBuffers[currentBlock].position((int) (index & INDEX_MASK_LOWER));
                blockLastHits[currentBlock] = time;
                return;
            }
        }
        currentBlock = blockCount;
        // we need to load a block
        if (currentBlock == MAX_LOADED_BLOCKS) {
            currentBlock = makeSlotAvailable();
        } else {
            blockCount++;
        }
        loadBlock(targetLocation);
        blockBuffers[currentBlock].position((int) (index & INDEX_MASK_LOWER));
    }

    /**
     * Makes a slot available for loading a block
     *
     * @return The block index to use
     */
    private int makeSlotAvailable() throws IOException {
        // look for a clean block with the lowest hit count
        int minCleanIndex = -1;
        long minCleanTime = Long.MAX_VALUE;
        int minDirtyIndex = -1;
        long minDirtyTime = Long.MAX_VALUE;
        for (int i = MAX_LOADED_BLOCKS - 1; i != -1; i--) {
            if (!blockIsDirty[i]) {
                if (blockLastHits[i] < minCleanTime) {
                    minCleanIndex = i;
                    minCleanTime = blockLastHits[i];
                }
            }
            if (blockLastHits[i] < minDirtyTime) {
                minDirtyIndex = i;
                minDirtyTime = blockLastHits[i];
            }
        }
        if (minCleanIndex == -1) {
            // all blocks are dirty ... commit
            commit();
            minCleanIndex = minDirtyIndex;
        }
        return minCleanIndex;
    }

    /**
     * Loads the block at the specified location
     *
     * @param location The location of the block to load
     */
    private void loadBlock(long location) throws IOException {
        blockBuffers[currentBlock] = channel.map(FileChannel.MapMode.READ_WRITE, location, BLOCK_SIZE);
        blockBuffers[currentBlock].position(0);
        blockLocations[currentBlock] = location;
        blockLastHits[currentBlock] = time;
        blockIsDirty[currentBlock] = false;
        blockPages[currentBlock] = null;
        if (location < getSize())
            blockBuffers[currentBlock].load();
    }
}
