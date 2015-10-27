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
import java.nio.charset.Charset;
import java.util.Arrays;

/**
 * Entity that manages the physical storage of strings in organized buckets.
 * How the strings are affected to buckets is outside the scope of this entity.
 * The strings are stored in an individual file.
 * The layout of the file is as follow:
 * <p/>
 * - First block:
 * - int32: Magic identifier for the store
 * - int32: Layout version
 * - int32: number of open blocks (blocks that contain data but are not full)
 * - int32: index of the next block to open (in number of block)
 * - array of block entries:
 * - int32: index of the block
 * - int32: remaining free space
 * - Second blocks and others contains string entries of the form:
 * - String entry:
 * - int64: index of the next entry, if any
 * - int32: serialized size in bytes
 * - The UTF-8 encoded string bytes
 *
 * @author Laurent Wouters
 */
class StringStoreBackend {
    /**
     * Magic identifier of the type of store
     */
    private static final int MAGIC_ID = 0x0000FF00;
    /**
     * The layout version
     */
    private static final int LAYOUT_VERSION = 1;
    /**
     * The suffix of the file backing the store
     */
    private static final String FILE_SUFFIX = "_data.bin";
    /**
     * The number of remaining bytes below which a block is considered full
     */
    private static final int THRESHOLD_BLOCK_FULL = 24;

    /**
     * The file persisting this store
     */
    private final PersistedFile file;
    /**
     * The charset to use for reading and writing the strings
     */
    private final Charset charset;

    /**
     * Initializes this backend
     *
     * @param directory The parent directory containing the backing file
     * @param prefix    The prefix for the file name of this store
     * @throws IOException      When the backing file cannot be accessed
     * @throws StorageException When the storage is in a bad state
     */
    public StringStoreBackend(File directory, String prefix) throws IOException, StorageException {
        file = new PersistedFile(new File(directory, prefix + FILE_SUFFIX));
        charset = Charset.forName("UTF-8");
        if (file.getSize() > 16) {
            file.seek(0);
            int temp = file.readInt();
            if (temp != MAGIC_ID)
                throw new StorageException("Unsupported backing file (" + Integer.toHexString(temp) + "), expected " + Integer.toHexString(MAGIC_ID));
            temp = file.readInt();
            if (temp != LAYOUT_VERSION)
                throw new StorageException("Unsupported layout version (" + Integer.toHexString(temp) + "), expected " + Integer.toHexString(LAYOUT_VERSION));
        } else {
            // initialize the file
            file.seek(0);
            file.writeInt(MAGIC_ID);
            file.writeInt(LAYOUT_VERSION);
            file.writeInt(0);
            file.writeInt(1);
        }
    }

    /**
     * Reads the string at the specified index
     *
     * @param key The key to the string
     * @return The string
     * @throws IOException When an IO operation failed
     */
    public String read(long key) throws IOException {
        file.seek(key + 8);
        int size = file.readInt();
        byte[] buffer = file.readBytes(size);
        return new String(buffer, charset);
    }

    /**
     * Retrieves the key for the specified string in a bucket
     *
     * @param bucket The key to the bucket for this string
     * @param data   The string to get the key for
     * @return The key for the string, or -1 if it is not in this store
     * @throws IOException When an IO operation failed
     */
    public long getKey(long bucket, String data) throws IOException {
        byte[] buffer = charset.encode(data).array();
        long candidate = bucket;
        while (candidate != -1) {
            file.seek(candidate);
            long next = file.readLong();
            int size = file.readInt();
            if (size == buffer.length) {
                if (Arrays.equals(buffer, file.readBytes(buffer.length)))
                    // the string is already there, return its key
                    return candidate;
            }
            candidate = next;
        }
        return -1;
    }

    /**
     * Stores the specified string in this backend
     *
     * @param bucket The key to the bucket for this string, or -1 if it must be created
     * @param data   The string to store
     * @return The key to the stored string
     * @throws IOException When an IO operation failed
     */
    public long add(long bucket, String data) throws IOException {
        byte[] buffer = charset.encode(data).array();
        long previous = -1;
        long candidate = bucket;
        while (candidate != -1) {
            file.seek(candidate);
            long next = file.readLong();
            int size = file.readInt();
            if (size == buffer.length) {
                if (Arrays.equals(buffer, file.readBytes(buffer.length)))
                    // the string is already there, return its key
                    return candidate;
            }
            previous = candidate;
            candidate = next;
        }
        long result = write(buffer);
        if (previous != -1) {
            file.seek(previous);
            file.writeLong(result);
        }
        return result;
    }

    /**
     * Writes the specified data in this store
     *
     * @param buffer the buffer of data
     * @return The index for retrieving the data
     * @throws IOException When an IO operation failed
     */
    private long write(byte[] buffer) throws IOException {
        int entrySize = buffer.length + 8 + 4;
        file.seek(8);
        int openBlockCount = file.readInt();
        int nextFreeBlock = file.readInt();
        for (int i = 0; i != openBlockCount; i++) {
            int blockIndex = file.readInt();
            int blockRemaining = file.readInt();
            if (blockRemaining >= entrySize) {
                long index = blockIndex * PersistedFile.BLOCK_SIZE + PersistedFile.BLOCK_SIZE - blockRemaining;
                writeTo(buffer, index);
                blockRemaining -= entrySize;
                if (blockRemaining >= THRESHOLD_BLOCK_FULL) {
                    file.seek(i * 8 + 16 + 4);
                    file.writeInt(blockRemaining);
                } else {
                    // the block is full
                    for (int j = i + 1; j != openBlockCount; j++) {
                        file.seek(j * 8 + 16);
                        blockIndex = file.readInt();
                        blockRemaining = file.readInt();
                        file.seek((j - 1) * 8 + 16);
                        file.writeInt(blockIndex);
                        file.writeInt(blockRemaining);
                    }
                    openBlockCount--;
                    file.seek(8);
                    file.writeInt(openBlockCount);
                }
                return index;
            }
        }
        // no available block
        return writeNewBlock(buffer, entrySize, openBlockCount, nextFreeBlock);
    }

    /**
     * Writes a new entry onto a new block
     *
     * @param buffer         The buffer of data to write
     * @param entrySize      The size of the entry to write
     * @param openBlockCount The number of open blocks
     * @param nextFreeBlock  the index of the next free block
     * @return The index of the stored data
     * @throws IOException When an IO operation failed
     */
    private long writeNewBlock(byte[] buffer, int entrySize, int openBlockCount, int nextFreeBlock) throws IOException {
        long index = nextFreeBlock * PersistedFile.BLOCK_SIZE;
        writeTo(buffer, index);
        nextFreeBlock++;
        while (entrySize < PersistedFile.BLOCK_SIZE) {
            nextFreeBlock++;
            entrySize -= PersistedFile.BLOCK_SIZE;
        }
        if (entrySize >= THRESHOLD_BLOCK_FULL) {
            file.seek(openBlockCount * 8 + 16);
            file.writeInt(nextFreeBlock - 1);
            file.writeInt(PersistedFile.BLOCK_SIZE - entrySize);
            openBlockCount++;
        }
        file.seek(8);
        file.writeInt(openBlockCount);
        file.writeInt(nextFreeBlock);
        return index;
    }

    /**
     * Writes the buffer to the specified index
     *
     * @param buffer The buffer to write
     * @param index  The index to write to
     * @throws IOException When an IO operation failed
     */
    private void writeTo(byte[] buffer, long index) throws IOException {
        file.seek(index);
        file.writeLong(-1);
        file.writeInt(buffer.length);
        file.writeBytes(buffer);
    }
}
