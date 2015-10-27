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
import java.util.ArrayList;
import java.util.List;

/**
 * A store of binary data backed by files
 * <p>
 * <p>
 * Each data file is composed of blocks (or pages)
 * - First block:
 * - int32: Magic identifier for the store
 * - int32: Layout version
 * - int32: number of open blocks (blocks that contain data but are not full)
 * - int32: index of the next block to open (in number of block)
 * - array of block entries:
 * - int32: index of the block
 * - int32: remaining free space
 *
 * @author Laurent Wouters
 */
class FileStore extends IOBackend {
    /**
     * Magic identifier of the type of store
     */
    private static final int MAGIC_ID = 0x0000FF00;
    /**
     * The layout version
     */
    private static final int LAYOUT_VERSION = 1;
    /**
     * The number of remaining bytes below which a block is considered full
     */
    private static final int THRESHOLD_BLOCK_FULL = 24;
    /**
     * The maximum number of open blocks in a file
     */
    private static final int MAX_OPEN_BLOCKS = (FileStoreFile.BLOCK_SIZE - 16) / 8;
    /**
     * The maximum number of blocks per file
     */
    private static final int MAX_BLOCKS_PER_FILE = 1 << 16;

    /**
     * The directory containing the backing files
     */
    private final File directory;
    /**
     * The common name of the files backing this store
     */
    private final String name;
    /**
     * The files backing this store
     */
    private final List<FileStoreFile> files;

    /**
     * Initializes this store
     *
     * @param directory The directory containing the backing files
     * @param name      The common name of the files backing this store
     */
    public FileStore(File directory, String name) throws IOException, StorageException {
        this.directory = directory;
        this.name = name;
        this.files = new ArrayList<>();
        int index = 0;
        File candidate = new File(directory, getNameFor(name, index));
        while (candidate.exists()) {
            FileStoreFile child = new FileStoreFile(candidate, getRadicalFor(index));
            child.seek(0);
            int temp = child.readInt();
            if (temp != MAGIC_ID)
                throw new StorageException("Unsupported backing file (" + Integer.toHexString(temp) + "), expected " + Integer.toHexString(MAGIC_ID));
            temp = child.readInt();
            if (temp != LAYOUT_VERSION)
                throw new StorageException("Unsupported layout version (" + Integer.toHexString(temp) + "), expected " + Integer.toHexString(LAYOUT_VERSION));
            files.add(child);
            index++;
            candidate = new File(directory, getNameFor(name, index));
        }
        if (files.isEmpty()) {
            // initializes
            FileStoreFile first = new FileStoreFile(candidate, getRadicalFor(0));
            initializeFile(first);
            files.add(first);
        }
    }

    @Override
    public void close() throws IOException {
        finalizeAllTransactions();
        for (FileStoreFile child : files)
            child.close();
    }

    /**
     * Gets an access to the entry for the specified key
     *
     * @param key The key to an entry
     * @return The IO element that can be used for reading and writing
     * @throws IOException      When an IO operation failed
     * @throws StorageException When the page version does not match the expected one
     */
    public IOElement access(long key) throws IOException, StorageException {
        return access(key, FLAG_READ | FLAG_WRITE);
    }

    /**
     * Gets a reading access to the entry for the specified key
     *
     * @param key The key to an entry
     * @return The IO element that can be used for reading
     * @throws IOException      When an IO operation failed
     * @throws StorageException When the page version does not match the expected one
     */
    public IOElement read(long key) throws IOException, StorageException {
        return access(key, FLAG_READ);
    }

    /**
     * Gets an access to the entry for the specified key
     *
     * @param key The key to an entry
     * @return The IO element that can be used for reading and writing
     * @throws IOException      When an IO operation failed
     * @throws StorageException When the page version does not match the expected one
     */
    protected IOElement access(long key, int flags) throws IOException, StorageException {
        int index = getFileIndexFor(key);
        FileStoreFile file = files.get(index);
        FileStorePage page = file.getPageFor(key);
        int length = page.positionFor(key);
        return transaction(file, file.getIndex(), length, flags);
    }

    /**
     * Adds a new entry of the specified size
     *
     * @param entrySize The size of the entry to write
     * @return The key for retrieving the data
     * @throws IOException      When an IO operation failed
     * @throws StorageException When the page version does not match the expected one
     */
    public long add(int entrySize) throws IOException, StorageException {
        if (entrySize > FileStorePage.MAX_ENTRY_SIZE)
            throw new StorageException("The entry is too large for this store");
        FileStoreFile file = files.get(files.size() - 1);
        long result = provision(file, entrySize);
        if (result == -1) {
            file = new FileStoreFile(new File(directory, getNameFor(name, files.size())), getRadicalFor(files.size()));
            files.add(file);
            result = provision(file, entrySize);
        }
        return result;
    }

    /**
     * Removes the entry for the specified key
     *
     * @param key The key of the entry to remove
     * @throws IOException      When an IO operation failed
     * @throws StorageException When the page version does not match the expected one
     */
    public void remove(long key) throws IOException, StorageException {
        int index = getFileIndexFor(key);
        FileStoreFile file = files.get(index);
        clear(file, key);
    }

    /**
     * Provisions an entry of the specified size in a file
     *
     * @param file      The backend file to write to
     * @param entrySize The size of the entry to write
     * @return The key for retrieving the data
     * @throws IOException      When an IO operation failed
     * @throws StorageException When the page version does not match the expected one
     */
    private static long provision(FileStoreFile file, int entrySize) throws IOException, StorageException {
        file.seek(8);
        int openBlockCount = file.readInt();
        int nextFreeBlock = file.readInt();
        for (int i = 0; i != openBlockCount; i++) {
            int blockIndex = file.readInt();
            int blockRemaining = file.readInt();
            if (blockRemaining >= entrySize + FileStorePage.ENTRY_OVERHEAD) {
                FileStorePage page = file.getPage(blockIndex);
                if (!page.canStore(entrySize))
                    continue;
                long key = page.registerEntry(entrySize);
                blockRemaining -= entrySize;
                blockRemaining -= FileStorePage.ENTRY_OVERHEAD;
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
                return key;
            }
        }
        {
            if (nextFreeBlock >= MAX_BLOCKS_PER_FILE)
                return -1;
            FileStorePage page = file.getPage(nextFreeBlock);
            page.setReuseEmptyEntries();
            nextFreeBlock++;
            int remaining = FileStorePage.MAX_ENTRY_SIZE - entrySize - FileStorePage.ENTRY_OVERHEAD;
            if (remaining >= THRESHOLD_BLOCK_FULL) {
                file.seek(openBlockCount * 8 + 16);
                file.writeInt(nextFreeBlock - 1);
                file.writeInt(remaining);
                openBlockCount++;
            }
            file.seek(8);
            file.writeInt(openBlockCount);
            file.writeInt(nextFreeBlock);
            return page.registerEntry(entrySize);
        }
    }

    /**
     * Clears an entry from a file
     *
     * @param file The backend file containing the entry
     * @param key  The key of the entry to remove
     * @return The key for retrieving the data
     * @throws IOException      When an IO operation failed
     * @throws StorageException When the page version does not match the expected one
     */
    private static void clear(FileStoreFile file, long key) throws IOException, StorageException {
        FileStorePage page = file.getPageFor(key);
        int length = page.removeEntry(key);
        file.seek(8);
        int openBlockCount = file.readInt();
        int nextFreeBlock = file.readInt();
        for (int i = 0; i != openBlockCount; i++) {
            int blockIndex = file.readInt();
            int blockRemaining = file.readInt();
            if (blockIndex * FileStoreFile.BLOCK_SIZE == page.getLocation()) {
                // already open
                blockRemaining -= length + FileStorePage.ENTRY_OVERHEAD;
                file.seek(i * 8 + 16 + 4);
                file.writeInt(blockRemaining);
                return;
            }
        }
    }

    /**
     * Gets the name of the i-th file
     *
     * @param radical The name radical
     * @param index   The index
     * @return The name of the file
     */
    private static String getNameFor(String radical, int index) {
        String suffix = Integer.toString(index);
        while (suffix.length() < 3)
            suffix = "0" + suffix;
        return radical + suffix;
    }

    /**
     * Gets the key radical for entries in the i-th file
     *
     * @param index The index
     * @return The key radical
     */
    private static long getRadicalFor(int index) {
        return ((long) index) << 32;
    }

    /**
     * Gets the file index for the specified key
     *
     * @param key A key to an entry
     * @return The index of the corresponding file
     */
    private static int getFileIndexFor(long key) {
        return (int) (key >>> 32);
    }

    /**
     * Initializes a backing file
     *
     * @param file The file to initialize
     * @throws IOException When an IO error occurred
     */
    private static void initializeFile(FileStoreFile file) throws IOException {
        file.seek(0);
        file.writeInt(MAGIC_ID);
        file.writeInt(LAYOUT_VERSION);
        file.writeInt(0);
        file.writeInt(1);
    }
}
