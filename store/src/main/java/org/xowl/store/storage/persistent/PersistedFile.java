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
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Represents a persisted binary file
 *
 * @author Laurent Wouters
 */
class PersistedFile {
    /**
     * The default size of a block (8kb)
     */
    public static final int DEFAULT_BLOCK_SIZE = 8192;
    /**
     * The maximum number of loaded blocks
     */
    private static final int MAX_LOADED_BLOCKS = 256;

    /**
     * The backing file
     */
    private final RandomAccessFile file;
    /**
     * The size of a block
     */
    private final int blockSize;
    /**
     * The upper index mask
     */
    private final long upperMask;
    /**
     * The lower mask
     */
    private final long lowerMask;
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
    private final ByteBuffer[] blockBuffers;
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
     * The current size of this data file
     */
    private long size;
    /**
     * The current time (for hitting blocks)
     */
    private long time;

    /**
     * Gets the size of a block in this file
     *
     * @return The size of a block in this file
     */
    public int getBlockSize() {
        return blockSize;
    }

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
     */
    public long getSize() {
        return size;
    }

    /**
     * Gets the remaining amount of data in the current block
     *
     * @return The remaining amount of data
     */
    public long getRemainingInBlock() {
        return blockSize - (index & lowerMask);
    }

    /**
     * Initializes this data file
     *
     * @param file The file location
     * @throws IOException When the backing file cannot be accessed
     */
    public PersistedFile(File file) throws IOException {
        this(file, DEFAULT_BLOCK_SIZE, 0);
    }

    /**
     * Initializes this data file
     *
     * @param file      The file location
     * @param blockSize The block size to use
     * @throws IOException When the backing file cannot be accessed
     */
    public PersistedFile(File file, int blockSize) throws IOException {
        this(file, blockSize, 0);
    }

    /**
     * Initializes this data file
     *
     * @param file       The file location
     * @param keyRadical The key radical for this file
     * @throws IOException When the backing file cannot be accessed
     */
    public PersistedFile(File file, long keyRadical) throws IOException {
        this(file, DEFAULT_BLOCK_SIZE, keyRadical);
    }

    /**
     * Initializes this data file
     *
     * @param file       The file location
     * @param blockSize  The block size to use
     * @param keyRadical The key radical for this file
     * @throws IOException When the backing file cannot be accessed
     */
    public PersistedFile(File file, int blockSize, long keyRadical) throws IOException {
        this.file = new RandomAccessFile(file, "rw");
        this.blockSize = blockSize;
        this.lowerMask = blockSize - 1;
        this.upperMask = ~lowerMask;
        this.keyRadical = keyRadical;
        this.buffer = ByteBuffer.allocate(8);
        this.blockBuffers = new ByteBuffer[MAX_LOADED_BLOCKS];
        this.blockLocations = new long[MAX_LOADED_BLOCKS];
        this.blockLastHits = new long[MAX_LOADED_BLOCKS];
        this.blockIsDirty = new boolean[MAX_LOADED_BLOCKS];
        this.blockPages = new PersistedFilePage[MAX_LOADED_BLOCKS];
        this.blockCount = 0;
        this.size = file.length();
        this.currentBlock = -1;
        this.index = 0;
        this.size = 0;
        this.time = 0;
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
        long targetLocation = index * blockSize;
        if (targetLocation < size) {
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
        return getPage((int) ((location & upperMask) / blockSize));
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
        return getPage((int) ((key - keyRadical) >> 16));
    }

    /**
     * Positions the index of this file
     *
     * @param index The new index
     */
    public void seek(long index) {
        if (index < 0)
            throw new IndexOutOfBoundsException("The index must be within the file");
        if (this.index == index)
            // do nothing
            return;
        this.index = index;
        // look for a corresponding block
        for (int i = 0; i != blockCount; i++) {
            long location = blockLocations[i];
            if (index >= location && index < location + blockSize) {
                blockBuffers[i].position((int) (index & lowerMask));
                currentBlock = i;
                return;
            }
        }
        currentBlock = -1;
    }

    /**
     * Positions the index of this file onto the next block
     */
    public void seekNextBlock() {
        seek(index & upperMask + blockSize);
    }

    /**
     * Reads a single byte at the current index
     *
     * @return The byte
     * @throws IOException When an IO operation failed
     */
    public byte readByte() throws IOException {
        if (index >= size)
            throw new IndexOutOfBoundsException("Cannot read the specified amount of data at this index");
        prepareIOAt();
        byte value = blockBuffers[currentBlock].get((int) (index & lowerMask));
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
        if (this.index + length > size)
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
        if (this.index + length > size)
            throw new IndexOutOfBoundsException("Cannot read the specified amount of data at this index");
        if (index < 0 || index + length > buffer.length)
            throw new IndexOutOfBoundsException("Out of bounds index and length for the specified buffer");
        int remainingInBlock = blockSize - (int) (this.index & lowerMask);
        int remainingLength = length;
        int targetIndex = index;
        while (remainingLength > remainingInBlock) {
            prepareIOAt();
            blockBuffers[currentBlock].get(buffer, targetIndex, remainingInBlock);
            remainingLength -= remainingInBlock;
            targetIndex += remainingInBlock;
            this.index += remainingInBlock;
            remainingInBlock = blockSize;
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
        if (index + 2 > size)
            throw new IndexOutOfBoundsException("Cannot read the specified amount of data at this index");
        prepareIOAt();
        if ((int) (this.index & lowerMask) + 2 <= blockSize) {
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
        if (index + 4 > size)
            throw new IndexOutOfBoundsException("Cannot read the specified amount of data at this index");
        prepareIOAt();
        if ((int) (this.index & lowerMask) + 4 <= blockSize) {
            // within the same block
            int value = blockBuffers[currentBlock].getInt();
            index += 4;
            return value;
        }
        buffer.position(0);
        for (int i = 0; i != 4; i++) {
            buffer.put(blockBuffers[currentBlock].get());
            index++;
            if (i < 3 && (index & lowerMask) == 0) {
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
        if (index + 8 > size)
            throw new IndexOutOfBoundsException("Cannot read the specified amount of data at this index");
        prepareIOAt();
        if ((int) (this.index & lowerMask) + 8 <= blockSize) {
            // within the same block
            long value = blockBuffers[currentBlock].getLong();
            index += 8;
            return value;
        }
        buffer.position(0);
        for (int i = 0; i != 8; i++) {
            buffer.put(blockBuffers[currentBlock].get());
            index++;
            if (i < 7 && (index & lowerMask) == 0) {
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
        if (index + 4 > size)
            throw new IndexOutOfBoundsException("Cannot read the specified amount of data at this index");
        prepareIOAt();
        if ((int) (this.index & lowerMask) + 4 <= blockSize) {
            // within the same block
            float value = blockBuffers[currentBlock].getFloat();
            index += 4;
            return value;
        }
        buffer.position(0);
        for (int i = 0; i != 4; i++) {
            buffer.put(blockBuffers[currentBlock].get());
            index++;
            if (i < 3 && (index & lowerMask) == 0) {
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
        if (index + 8 > size)
            throw new IndexOutOfBoundsException("Cannot read the specified amount of data at this index");
        prepareIOAt();
        if ((int) (this.index & lowerMask) + 8 <= blockSize) {
            // within the same block
            double value = blockBuffers[currentBlock].getDouble();
            index += 8;
            return value;
        }
        buffer.position(0);
        for (int i = 0; i != 8; i++) {
            buffer.put(blockBuffers[currentBlock].get());
            index++;
            if (i < 7 && (index & lowerMask) == 0) {
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
        if (index + length > size)
            throw new IndexOutOfBoundsException("Cannot read the specified amount of data at this index");
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            int remainingInBlock = blockSize - (int) (this.index & lowerMask);
            int remainingLength = length;
            while (remainingLength > remainingInBlock) {
                prepareIOAt();
                md.update(blockBuffers[currentBlock].array(), blockBuffers[currentBlock].position(), remainingInBlock);
                remainingLength -= remainingInBlock;
                this.index += remainingInBlock;
                remainingInBlock = blockSize;
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
        blockBuffers[currentBlock].put((int) (index & lowerMask), value);
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
        int remainingInBlock = blockSize - (int) (this.index & lowerMask);
        int remainingLength = length;
        int targetIndex = index;
        while (remainingLength > remainingInBlock) {
            prepareIOAt();
            blockBuffers[currentBlock].put(buffer, targetIndex, remainingInBlock);
            blockIsDirty[currentBlock] = true;
            remainingLength -= remainingInBlock;
            targetIndex += remainingInBlock;
            this.index += remainingInBlock;
            remainingInBlock = blockSize;
        }
        prepareIOAt();
        blockBuffers[currentBlock].put(buffer, targetIndex, remainingInBlock);
        blockIsDirty[currentBlock] = true;
        this.index += remainingInBlock;
    }

    /**
     * Writes a single char at the current index
     *
     * @param value The char to write
     * @throws IOException When an IO operation failed
     */
    public void writeChar(char value) throws IOException {
        prepareIOAt();
        if ((int) (this.index & lowerMask) + 2 <= blockSize) {
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
        if ((int) (this.index & lowerMask) + 4 <= blockSize) {
            // within the same block
            blockBuffers[currentBlock].putInt(value);
            index += 4;
        } else {
            buffer.putInt(0, value);
            for (int i = 0; i != 4; i++) {
                blockBuffers[currentBlock].put(buffer.get(i));
                index++;
                if (i < 3 && (index & lowerMask) == 0) {
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
        if ((int) (this.index & lowerMask) + 8 <= blockSize) {
            // within the same block
            blockBuffers[currentBlock].putLong(value);
            index += 8;
        } else {
            buffer.putLong(0, value);
            for (int i = 0; i != 8; i++) {
                blockBuffers[currentBlock].put(buffer.get(i));
                index++;
                if (i < 7 && (index & lowerMask) == 0) {
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
        if ((int) (this.index & lowerMask) + 4 <= blockSize) {
            // within the same block
            blockBuffers[currentBlock].putFloat(value);
            index += 4;
        } else {
            buffer.putFloat(0, value);
            for (int i = 0; i != 4; i++) {
                blockBuffers[currentBlock].put(buffer.get(i));
                index++;
                if (i < 3 && (index & lowerMask) == 0) {
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
        if ((int) (this.index & lowerMask) + 8 <= blockSize) {
            // within the same block
            blockBuffers[currentBlock].putDouble(value);
            index += 8;
        } else {
            buffer.putDouble(0, value);
            for (int i = 0; i != 8; i++) {
                blockBuffers[currentBlock].put(buffer.get(i));
                index++;
                if (i < 7 && (index & lowerMask) == 0) {
                    prepareIOAt();
                    blockIsDirty[currentBlock] = true;
                }
            }
        }
    }

    /**
     * Moves the content at the current index with the specified length of the specified offset
     * This method only supports move operation within the same block.
     *
     * @param length The number of bytes to move
     * @param offset The offset number of bytes
     * @throws IOException When an IO operation failed
     */
    public void moveInBlock(int length, int offset) throws IOException {
        int contentStart = (int) (index & lowerMask);
        if (contentStart + length > blockSize)
            throw new IndexOutOfBoundsException("The content to move in not within the block");
        if (contentStart + offset < 0 || contentStart + length + offset > blockSize)
            throw new IndexOutOfBoundsException("The move operation spills off the block");
        prepareIOAt();
        blockIsDirty[currentBlock] = true;
        System.arraycopy(blockBuffers[currentBlock].array(), contentStart, blockBuffers[currentBlock].array(), contentStart + offset, length);
    }

    /**
     * Commits any outstanding changes
     *
     * @throws IOException When the changes could not be committed on disk
     */
    public void commit() throws IOException {
        for (int i = 0; i != blockCount; i++) {
            if (blockIsDirty[i]) {
                if (blockPages[i] != null)
                    blockPages[i].onCommit();
                file.seek(blockLocations[i]);
                file.write(blockBuffers[i].array());
            }
        }
        file.getChannel().force(true);
        for (int i = 0; i != blockCount; i++) {
            blockIsDirty[i] = false;
        }
    }

    /**
     * Prepares the blocks for IO operations at the current index
     */
    private void prepareIOAt() throws IOException {
        time++;
        if (currentBlock >= 0 && index >= blockLocations[currentBlock] && index < blockLocations[currentBlock] + blockSize) {
            // this is the current block
            blockBuffers[currentBlock].position((int) (index & lowerMask));
            blockLastHits[currentBlock] = time;
            return;
        }
        // is the block loaded
        long targetLocation = index & upperMask;
        for (int i = 0; i != blockCount; i++) {
            if (blockLocations[i] == targetLocation) {
                currentBlock = i;
                blockBuffers[currentBlock].position((int) (index & lowerMask));
                blockLastHits[currentBlock] = time;
                return;
            }
        }
        currentBlock = blockCount;
        // we need to load a block
        if (currentBlock == MAX_LOADED_BLOCKS)
            currentBlock = makeSlotAvailable();
        loadBlock(targetLocation);
        blockCount++;
        blockBuffers[currentBlock].position((int) (index & lowerMask));
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
        if (blockBuffers[currentBlock] == null)
            blockBuffers[currentBlock] = ByteBuffer.allocate(blockSize);
        blockBuffers[currentBlock].position(0);
        blockLocations[currentBlock] = location;
        blockLastHits[currentBlock] = time;
        blockIsDirty[currentBlock] = false;
        blockPages[currentBlock] = null;
        if (location < size) {
            file.seek(location);
            int totalRead = 0;
            while (totalRead < blockSize) {
                int read = file.read(blockBuffers[currentBlock].array(), totalRead, blockSize - totalRead);
                if (read == -1)
                    return;
                totalRead += read;
            }
        }
    }
}
