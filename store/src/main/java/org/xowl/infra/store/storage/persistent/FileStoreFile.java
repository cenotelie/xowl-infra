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

import java.io.File;

/**
 * Represents a persisted binary file used for storing objects
 * A file is composed of blocks (or pages)
 * The first block has a special structure with the following layout
 * - int: Magic identifier for the store
 * - int: Layout version
 * - int: Start offset to free space
 * - int: Number of pools of reusable objects
 * - Array pool heads:
 * - int: Size of the objects in this pool
 * - int: Index of the first reusable object in the pool
 * <p>
 * An object stored in this file has the layout:
 * - header: char: object size
 * - content: the object
 * <p>
 * This structure is thread-safe and uses a lock-free synchronization scheme.
 * When IO operations consists of reading, writing and removing entries in pages, this structure ensures the consistency of the book-keeping data.
 *
 * @author Laurent Wouters
 */
class FileStoreFile extends FileBackend {
    /**
     * Magic identifier of the type of store
     */
    private static final int FILE_MAGIC_ID = 0x784F574C;
    /**
     * The layout version
     */
    private static final int FILE_LAYOUT_VERSION = 1;
    /**
     * The size of the header in the preamble block
     * int: Magic identifier for the store
     * int: Layout version
     * int: Start offset to free space
     * int: Number of pools of reusable objects
     */
    private static final int FILE_PREAMBLE_HEADER_SIZE = 4 + 4 + 4 + 4;
    /**
     * The size of an open block entry in the preamble
     * char: Index of the block
     * char: Remaining free space
     */
    private static final int FILE_PREAMBLE_ENTRY_SIZE = 4 + 4;
    /**
     * The maximum size of a file
     */
    private static final int FILE_MAX_SIZE = 1 << (16 + FileBlock.BLOCK_INDEX_LENGTH);
    /**
     * The maximum number of pools in this store
     */
    private static final int FILE_MAX_POOLS = (FileBlock.BLOCK_SIZE - FILE_PREAMBLE_HEADER_SIZE) / FILE_PREAMBLE_ENTRY_SIZE;
    /**
     * Size of the header for each object stored in a file
     */
    private static final int FILE_OBJECT_HEADER_SIZE = 2;
    /**
     * Minimum size of objects in this store
     */
    private static final int FILE_OBJECT_MIN_SIZE = 8 - FILE_OBJECT_HEADER_SIZE;
    /**
     * Maximum size of objects in this store
     */
    private static final int FILE_OBJECT_MAX_SIZE = FileBlock.BLOCK_SIZE - FILE_OBJECT_HEADER_SIZE;

    /**
     * Initializes this data file
     *
     * @param file       The file location
     * @param isReadonly Whether this store is in readonly mode
     * @param noInit     Whether to skip the file initialization
     * @throws StorageException When the initialization failed
     */
    public FileStoreFile(File file, boolean isReadonly, boolean noInit) throws StorageException {
        super(file, isReadonly);
        if (!noInit)
            initialize(isReadonly, file.getAbsolutePath());
    }

    /**
     * Initializes this file
     *
     * @param isReadonly Whether this store is in readonly mode
     * @param fileName   The name of the represented file
     * @throws StorageException When an IO error occurred
     */
    private void initialize(boolean isReadonly, String fileName) throws StorageException {
        if (getSize() == 0) {
            if (isReadonly)
                throw new StorageException("FileBackend is empty but open as read-only: " + fileName);
            // the file is empty and not read-only
            try (IOAccess access = access(0, FILE_PREAMBLE_HEADER_SIZE, true)) {
                access.writeInt(FILE_MAGIC_ID);
                access.writeInt(FILE_LAYOUT_VERSION);
                access.writeInt(FileBlock.BLOCK_SIZE);
                access.writeInt((char) 0);
            }
            flush();
        } else {
            // file is not empty, verify the header
            try (IOAccess access = access(0, FILE_PREAMBLE_HEADER_SIZE, false)) {
                int magic = access.readInt();
                if (magic != FILE_MAGIC_ID)
                    throw new StorageException("Invalid file header, expected 0x" + Integer.toHexString(FILE_MAGIC_ID) + ", got 0x" + Integer.toHexString(magic) + ": " + fileName);
                int layout = access.readInt();
                if (layout != FILE_LAYOUT_VERSION)
                    throw new StorageException("Invalid file layout, expected 0x" + Integer.toHexString(FILE_LAYOUT_VERSION) + ", got 0x" + Integer.toHexString(layout) + ": " + fileName);
            }
        }
    }

    /**
     * Accesses an object in this file through an access element
     * An access must be within the boundaries of a block.
     *
     * @param index    The index within this file of the reserved area for the access
     * @param writable Whether the access shall allow writing
     * @throws StorageException
     */
    public IOAccess access(long index, boolean writable) throws StorageException {
        try (FileBlockTS block = getBlockFor(index)) {
            long length = block.readChar(index - 2);
            return access(index, length, writable, block);
        }
    }

    /**
     * Tries to allocate an object of the specified size in this store
     * This method tries to reuse empty space to reduce fragmentation.
     * Objects allocated with this method can be freed later.
     *
     * @param size The size of the object
     * @return The key to the object, or KEY_NULL if it cannot be allocated in this file
     * @throws StorageException When an IO operation fails
     */
    public long allocate(int size) throws StorageException {
        int toAllocate = size < FILE_OBJECT_MIN_SIZE ? FILE_OBJECT_MIN_SIZE : size;
        if (size > FILE_OBJECT_MAX_SIZE)
            throw new StorageException("Cannot allocate an object of this size: requested " + size + ", max is " + FILE_OBJECT_MAX_SIZE);
        try (FileBlockTS preamble = getBlockFor(0)) {
            // get the number of pools
            int poolCount;
            try (IOAccess access = access(12, 4, false, preamble)) {
                poolCount = access.readInt();
            }
            // look into the pools
            if (poolCount > 0) {
                try (IOAccess access = access(FILE_PREAMBLE_HEADER_SIZE, poolCount * FILE_PREAMBLE_ENTRY_SIZE, false, preamble)) {
                    for (int i = 0; i != poolCount; i++) {
                        int poolSize = access.readInt();
                        access.readInt();
                        if (poolSize == toAllocate) {
                            // the pool size fits, try to reuse ...
                            long result = allocateReuse(preamble, i);
                            if (result != FileStore.KEY_NULL)
                                // success => return the result
                                return result;
                            // failed, stop looking into pools
                            break;
                        }
                    }
                }
            }
            // fall back to direct allocation from the free space
            return doAllocateDirect(preamble, toAllocate);
        }
    }

    /**
     * Tries to allocate an object by reusing the an empty entry in this store
     *
     * @param preamble  The preamble block
     * @param poolIndex The index of the pool to use
     * @return The key to the object, or KEY_NULL if it cannot be allocated in this file
     * @throws StorageException When an IO operation fails
     */
    private long allocateReuse(FileBlockTS preamble, int poolIndex) throws StorageException {
        try (IOAccess access = access(FILE_PREAMBLE_HEADER_SIZE + poolIndex * FILE_PREAMBLE_ENTRY_SIZE, 8, true, preamble)) {
            int size = access.readInt();
            int target = access.readInt();
            if (target == 0)
                return FileStore.KEY_NULL;
            int next;
            try (IOAccess accessTarget = access(target, 4, true)) {
                next = accessTarget.readInt();
                access.reset().writeChar((char) size);
            }
            access.seek(4).writeInt(next);
            return target + 2;
        }
    }

    /**
     * Tries to allocate an object of the specified size in this store
     * This method directly allocate the object without looking up for reusable space.
     * Objects allocated with this method cannot be freed later.
     *
     * @param size The size of the object
     * @return The key to the object, or KEY_NULL if it cannot be allocated in this file
     * @throws StorageException When an IO operation fails
     */
    public long allocateDirect(int size) throws StorageException {
        int toAllocate = size < FILE_OBJECT_MIN_SIZE ? FILE_OBJECT_MIN_SIZE : size;
        if (size > FILE_OBJECT_MAX_SIZE)
            throw new StorageException("Cannot allocate an object of this size: requested " + size + ", max is " + FILE_OBJECT_MAX_SIZE);
        try (FileBlockTS preamble = getBlockFor(0)) {
            return doAllocateDirect(preamble, toAllocate);
        }
    }

    /**
     * Allocates at the end
     *
     * @param size The size of the object
     * @return The key to the object, or KEY_NULL if it cannot be allocated in this file
     * @throws StorageException When an IO operation fails
     */
    private long doAllocateDirect(FileBlockTS preamble, int size) throws StorageException {
        int target;
        try (IOAccess access = access(8, 4, true, preamble)) {
            int freeSpace = access.readInt();
            target = freeSpace;
            freeSpace += size + FILE_OBJECT_HEADER_SIZE;
            if ((freeSpace & INDEX_MASK_UPPER) != (target & INDEX_MASK_UPPER)) {
                // not the same block, the object would be split between blocks
                // go to the next block entirely
                target = (int) (freeSpace & INDEX_MASK_UPPER);
                freeSpace = target + size + FILE_OBJECT_HEADER_SIZE;
            }
            if (freeSpace > FILE_MAX_SIZE)
                // not enough space in this file
                return FileStore.KEY_NULL;
            access.reset().writeInt(freeSpace);
        }
        try (IOAccess access = access(target, 2, true)) {
            access.writeChar((char) size);
        }
        return target;
    }

    /**
     * Frees the object at the specified index
     *
     * @param index The index of an object in this store
     * @throws StorageException When an IO operation fails
     */
    public void free(long index) throws StorageException {

    }
}
