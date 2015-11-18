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
import java.util.Arrays;

/**
 * Represents a persisted binary file
 * Due to the use of an internal state for maintaining the current index, this class is NOT thread safe.
 *
 * @author Laurent Wouters
 */
class FileStoreFile implements IOElement {
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
    private static final long INDEX_MASK_LOWER = BLOCK_SIZE - 1;
    /**
     * The mask for the index of a block
     */
    private static final long INDEX_MASK_UPPER = ~INDEX_MASK_LOWER;

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
    private final FileStorePage[] blockPages;
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
     * The total size of this file
     */
    private long size;
    /**
     * The current time (for hitting blocks)
     */
    private long time;

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
        this.blockBuffers = new MappedByteBuffer[MAX_LOADED_BLOCKS];
        this.blockLocations = new long[MAX_LOADED_BLOCKS];
        this.blockLastHits = new long[MAX_LOADED_BLOCKS];
        this.blockIsDirty = new boolean[MAX_LOADED_BLOCKS];
        this.blockPages = new FileStorePage[MAX_LOADED_BLOCKS];
        this.blockCount = 0;
        this.currentBlock = -1;
        this.index = 0;
        this.size = channel.size();
        this.time = 0;
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

    @Override
    public long getIndex() {
        return index;
    }

    @Override
    public long getSize() {
        return size;
    }

    @Override
    public FileStoreFile seek(long index) {
        if (index < 0)
            throw new IndexOutOfBoundsException("The index must be positive");
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

    @Override
    public FileStoreFile reset() {
        return seek(0);
    }

    @Override
    public boolean canRead(int length) {
        return (index + length <= getSize());
    }

    @Override
    public byte readByte() throws StorageException {
        if (!canRead(1))
            throw new StorageException("Cannot read the specified amount of data at this index");
        if (!prepareIOAt())
            throw new StorageException("Failed to access the data at index 0x" + Long.toHexString(index));
        byte value = blockBuffers[currentBlock].get((int) (index & INDEX_MASK_LOWER));
        index++;
        return value;
    }

    @Override
    public byte[] readBytes(int length) throws StorageException {
        if (!canRead(length))
            throw new IndexOutOfBoundsException("Cannot read the specified amount of data at this index");
        byte[] result = new byte[length];
        readBytes(result, 0, length);
        return result;
    }

    @Override
    public void readBytes(byte[] buffer, int index, int length) throws StorageException {
        if (!canRead(1))
            throw new IndexOutOfBoundsException("Cannot read the specified amount of data at this index");
        if (index < 0 || index + length > buffer.length)
            throw new IndexOutOfBoundsException("Out of bounds index and length for the specified buffer");
        if (length > BLOCK_SIZE - (int) (this.index & INDEX_MASK_LOWER))
            throw new StorageException("IO operation crosses block boundaries");
        if (!prepareIOAt())
            throw new StorageException("Failed to access the data at index 0x" + Long.toHexString(index));
        blockBuffers[currentBlock].get(buffer, index, length);
        this.index += length;
    }

    @Override
    public char readChar() throws StorageException {
        if (!canRead(2))
            throw new IndexOutOfBoundsException("Cannot read the specified amount of data at this index");
        if ((int) (this.index & INDEX_MASK_LOWER) + 2 > BLOCK_SIZE)
            throw new StorageException("IO operation crosses block boundaries");
        if (!prepareIOAt())
            throw new StorageException("Failed to access the data at index 0x" + Long.toHexString(index));
        char value = blockBuffers[currentBlock].getChar();
        index += 2;
        return value;
    }

    @Override
    public int readInt() throws StorageException {
        if (!canRead(4))
            throw new IndexOutOfBoundsException("Cannot read the specified amount of data at this index");
        if ((int) (this.index & INDEX_MASK_LOWER) + 4 > BLOCK_SIZE)
            throw new StorageException("IO operation crosses block boundaries");
        if (!prepareIOAt())
            throw new StorageException("Failed to access the data at index 0x" + Long.toHexString(index));
        int value = blockBuffers[currentBlock].getInt();
        index += 4;
        return value;
    }

    @Override
    public long readLong() throws StorageException {
        if (!canRead(8))
            throw new IndexOutOfBoundsException("Cannot read the specified amount of data at this index");
        if ((int) (this.index & INDEX_MASK_LOWER) + 8 > BLOCK_SIZE)
            throw new StorageException("IO operation crosses block boundaries");
        if (!prepareIOAt())
            throw new StorageException("Failed to access the data at index 0x" + Long.toHexString(index));
        long value = blockBuffers[currentBlock].getLong();
        index += 8;
        return value;
    }

    @Override
    public float readFloat() throws StorageException {
        if (!canRead(4))
            throw new IndexOutOfBoundsException("Cannot read the specified amount of data at this index");
        if ((int) (this.index & INDEX_MASK_LOWER) + 4 > BLOCK_SIZE)
            throw new StorageException("IO operation crosses block boundaries");
        if (!prepareIOAt())
            throw new StorageException("Failed to access the data at index 0x" + Long.toHexString(index));
        float value = blockBuffers[currentBlock].getFloat();
        index += 4;
        return value;
    }

    @Override
    public double readDouble() throws StorageException {
        if (!canRead(8))
            throw new IndexOutOfBoundsException("Cannot read the specified amount of data at this index");
        if ((int) (this.index & INDEX_MASK_LOWER) + 8 > BLOCK_SIZE)
            throw new StorageException("IO operation crosses block boundaries");
        if (!prepareIOAt())
            throw new StorageException("Failed to access the data at index 0x" + Long.toHexString(index));
        double value = blockBuffers[currentBlock].getDouble();
        index += 8;
        return value;
    }

    @Override
    public void writeByte(byte value) throws StorageException {
        if (isReadonly)
            throw new StorageException("File is in readonly mode");
        if (!prepareIOAt())
            throw new StorageException("Failed to access the data at index 0x" + Long.toHexString(index));
        blockBuffers[currentBlock].put((int) (index & INDEX_MASK_LOWER), value);
        blockIsDirty[currentBlock] = true;
        index++;
    }

    @Override
    public void writeBytes(byte[] value) throws StorageException {
        writeBytes(value, 0, value.length);
    }

    @Override
    public void writeBytes(byte[] buffer, int index, int length) throws StorageException {
        if (index < 0 || index + length > buffer.length)
            throw new IndexOutOfBoundsException("Out of bounds index and length for the specified buffer");
        if (isReadonly)
            throw new StorageException("File is in readonly mode");
        if (length > BLOCK_SIZE - (int) (this.index & INDEX_MASK_LOWER))
            throw new StorageException("IO operation crosses block boundaries");
        if (!prepareIOAt())
            throw new StorageException("Failed to access the data at index 0x" + Long.toHexString(index));
        blockBuffers[currentBlock].put(buffer, index, length);
        blockIsDirty[currentBlock] = true;
        this.index += length;
    }

    @Override
    public void writeChar(char value) throws StorageException {
        if (isReadonly)
            throw new StorageException("File is in readonly mode");
        if ((int) (this.index & INDEX_MASK_LOWER) + 2 > BLOCK_SIZE)
            throw new StorageException("IO operation crosses block boundaries");
        if (!prepareIOAt())
            throw new StorageException("Failed to access the data at index 0x" + Long.toHexString(index));
        blockBuffers[currentBlock].putChar(value);
        blockIsDirty[currentBlock] = true;
        index += 2;
    }

    @Override
    public void writeInt(int value) throws StorageException {
        if (isReadonly)
            throw new StorageException("File is in readonly mode");
        if ((int) (this.index & INDEX_MASK_LOWER) + 4 > BLOCK_SIZE)
            throw new StorageException("IO operation crosses block boundaries");
        if (!prepareIOAt())
            throw new StorageException("Failed to access the data at index 0x" + Long.toHexString(index));
        blockIsDirty[currentBlock] = true;
        blockBuffers[currentBlock].putInt(value);
        index += 4;
    }

    @Override
    public void writeLong(long value) throws StorageException {
        if (isReadonly)
            throw new StorageException("File is in readonly mode");
        if ((int) (this.index & INDEX_MASK_LOWER) + 8 > BLOCK_SIZE)
            throw new StorageException("IO operation crosses block boundaries");
        if (!prepareIOAt())
            throw new StorageException("Failed to access the data at index 0x" + Long.toHexString(index));
        blockIsDirty[currentBlock] = true;
        blockBuffers[currentBlock].putLong(value);
        index += 8;
    }

    @Override
    public void writeFloat(float value) throws StorageException {
        if (isReadonly)
            throw new StorageException("File is in readonly mode");
        if ((int) (this.index & INDEX_MASK_LOWER) + 4 > BLOCK_SIZE)
            throw new StorageException("IO operation crosses block boundaries");
        if (!prepareIOAt())
            throw new StorageException("Failed to access the data at index 0x" + Long.toHexString(index));
        blockIsDirty[currentBlock] = true;
        blockBuffers[currentBlock].putFloat(value);
        index += 4;
    }

    @Override
    public void writeDouble(double value) throws StorageException {
        if (isReadonly)
            throw new StorageException("File is in readonly mode");
        if ((int) (this.index & INDEX_MASK_LOWER) + 8 > BLOCK_SIZE)
            throw new StorageException("IO operation crosses block boundaries");
        if (!prepareIOAt())
            throw new StorageException("Failed to access the data at index 0x" + Long.toHexString(index));
        blockIsDirty[currentBlock] = true;
        blockBuffers[currentBlock].putDouble(value);
        index += 8;
    }

    /**
     * Gets the page that contains the current index
     *
     * @return The page that contains the current index
     * @throws StorageException When the page version does not match the expected one
     */
    public FileStorePage getPage() throws StorageException {
        return getPageAt(index);
    }

    /**
     * Gets the n-th page in this file
     *
     * @param index The index of a page
     * @return The n-th page
     * @throws StorageException When the page version does not match the expected one
     */
    public FileStorePage getPage(int index) throws StorageException {
        long originalIndex = this.index;
        long targetLocation = index * BLOCK_SIZE;
        if (targetLocation < getSize()) {
            // is the block loaded
            for (int i = 0; i != blockCount; i++) {
                if (blockLocations[i] == targetLocation) {
                    if (blockPages[i] == null)
                        blockPages[i] = new FileStorePage(this, targetLocation, index << 16);
                    seek(originalIndex);
                    return blockPages[i];
                }
            }
            // creating the page data will force the block to be loaded
        } else {
            // the page does not exist in the backend
            // force the block to be allocated
            seek(targetLocation);
            if (!prepareIOAt())
                throw new StorageException("Failed to access the data at index 0x" + Long.toHexString(index));
        }
        FileStorePage result = new FileStorePage(this, targetLocation, index << 16);
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
     * @throws StorageException When the page version does not match the expected one
     */
    public FileStorePage getPageAt(long location) throws StorageException {
        return getPage((int) ((location & INDEX_MASK_UPPER) / BLOCK_SIZE));
    }

    /**
     * Gets the page that contains the specified key
     *
     * @param key A key
     * @return The page that contains the specified key
     * @throws StorageException When the page version does not match the expected one
     */
    public FileStorePage getPageFor(int key) throws StorageException {
        return getPage(key >>> 16);
    }

    /**
     * Positions the index of this file onto the next free block
     *
     * @return The persistent file
     */
    public FileStoreFile seekNextBlock() {
        long size = getSize();
        if ((size & INDEX_MASK_UPPER) == size)
            // the size is a multiple of BLOCK_SIZE
            return seek(size);
        return seek((index & INDEX_MASK_UPPER) + BLOCK_SIZE);
    }

    /**
     * Prepares the blocks for IO operations at the current index
     *
     * @return Whether the operation succeeded
     */
    private boolean prepareIOAt() {
        time++;
        if (currentBlock >= 0 && index >= blockLocations[currentBlock] && index < blockLocations[currentBlock] + BLOCK_SIZE) {
            // this is the current block
            blockBuffers[currentBlock].position((int) (index & INDEX_MASK_LOWER));
            blockLastHits[currentBlock] = time;
            return true;
        }
        // is the block loaded
        long targetLocation = index & INDEX_MASK_UPPER;
        for (int i = 0; i != blockCount; i++) {
            if (blockLocations[i] == targetLocation) {
                currentBlock = i;
                blockBuffers[currentBlock].position((int) (index & INDEX_MASK_LOWER));
                blockLastHits[currentBlock] = time;
                return true;
            }
        }
        currentBlock = blockCount;
        // we need to load a block
        if (currentBlock == MAX_LOADED_BLOCKS) {
            currentBlock = makeSlotAvailable();
            if (currentBlock == -1)
                return false;
        } else {
            blockCount++;
        }
        if (loadBlock(targetLocation)) {
            blockBuffers[currentBlock].position((int) (index & INDEX_MASK_LOWER));
            return true;
        }
        return false;
    }

    /**
     * Makes a slot available for loading a block
     *
     * @return The block index to use, or -1 if no block can be made available
     */
    private int makeSlotAvailable() {
        // look for a clean block with the lowest hit count
        int minCleanIndex = -1;
        long minCleanTime = Long.MAX_VALUE;
        for (int i = MAX_LOADED_BLOCKS - 1; i != -1; i--) {
            if (!blockIsDirty[i]) {
                if (blockLastHits[i] < minCleanTime) {
                    minCleanIndex = i;
                    minCleanTime = blockLastHits[i];
                }
            }
        }
        return minCleanIndex;
    }

    /**
     * Loads the block at the specified location
     *
     * @param location The location of the block to load
     * @return Whether the operation succeeded
     */
    private boolean loadBlock(long location) {
        if (blockBuffers[currentBlock] == null)
            blockBuffers[currentBlock] = ByteBuffer.allocateDirect(BLOCK_SIZE);
        blockBuffers[currentBlock].position(0);
        blockLocations[currentBlock] = location;
        blockLastHits[currentBlock] = time;
        blockIsDirty[currentBlock] = false;
        blockPages[currentBlock] = null;
        size = Math.max(size, location + BLOCK_SIZE);
        if (location < getSize()) {
            try {
                channel.position(location);
                channel.read(blockBuffers[currentBlock]);
                return true;
            } catch (IOException exception) {
                // zeroes the buffer
                zeroes(blockBuffers[currentBlock]);
                return false;
            }
        } else {
            // zeroes the buffer
            zeroes(blockBuffers[currentBlock]);
            return true;
        }
    }

    /**
     * Arrays of empty data used for zeroing the content of a buffer
     */
    private static final byte[] ZEROES = new byte[256];

    /**
     * Zeroes the content of the buffer
     *
     * @param buffer The buffer to clear
     */
    private static void zeroes(ByteBuffer buffer) {
        if (buffer.hasArray()) {
            Arrays.fill(buffer.array(), (byte) 0);
            return;
        }
        buffer.position(0);
        for (int i = 0; i != BLOCK_SIZE; i += ZEROES.length) {
            buffer.put(ZEROES);
        }
    }
}
