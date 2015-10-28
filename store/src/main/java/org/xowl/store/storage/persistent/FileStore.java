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
 * <p/>
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
    private static final int MAGIC_ID = 0x784F574C;
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
     * The size of the preamble in the header
     */
    private static final int HEADER_PREAMBLE_SIZE = 16;
    /**
     * the size of an open block entry in the header
     */
    private static final int HEADER_OPEN_BLOCK_ENTRY_SIZE = 8;


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
            initializeFile(file);
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
    private long provision(FileStoreFile file, int entrySize) throws IOException, StorageException {
        try (IOElement header = transaction(file, 0, FileStoreFile.BLOCK_SIZE, IOBackend.FLAG_READ | IOBackend.FLAG_WRITE)) {
            header.seek(8);
            int openBlockCount = header.readInt();
            int nextFreeBlock = header.readInt();
            for (int i = 0; i != openBlockCount; i++) {
                int blockIndex = header.readInt();
                int blockRemaining = header.readInt();
                if (blockIndex == -1)
                    // this is a free block slot
                    continue;
                if (blockRemaining >= entrySize + FileStorePage.ENTRY_OVERHEAD) {
                    // the entry could fit in the page
                    FileStorePage page = file.getPage(blockIndex);
                    if (!page.canStore(entrySize))
                        continue;
                    long key = page.registerEntry(entrySize);
                    blockRemaining -= entrySize;
                    blockRemaining -= FileStorePage.ENTRY_OVERHEAD;
                    if (blockRemaining >= THRESHOLD_BLOCK_FULL) {
                        header.seek(HEADER_PREAMBLE_SIZE + i * HEADER_OPEN_BLOCK_ENTRY_SIZE + 4);
                        header.writeInt(blockRemaining);
                    } else {
                        header.seek(HEADER_PREAMBLE_SIZE + i * HEADER_OPEN_BLOCK_ENTRY_SIZE);
                        header.writeInt(-1);
                        header.writeInt(0);
                    }
                    return key;
                }
            }

            // cannot fit in an open block
            if (nextFreeBlock >= MAX_BLOCKS_PER_FILE)
                return -1;
            FileStorePage page = file.getPage(nextFreeBlock);
            page.setReuseEmptyEntries();
            nextFreeBlock++;
            int remaining = FileStorePage.MAX_ENTRY_SIZE - entrySize - FileStorePage.ENTRY_OVERHEAD;
            if (remaining >= THRESHOLD_BLOCK_FULL) {
                header.seek(HEADER_PREAMBLE_SIZE);
                boolean found = false;
                for (int i = 0; i != openBlockCount; i++) {
                    int blockIndex = header.readInt();
                    header.readInt();
                    if (blockIndex == -1) {
                        header.seek(HEADER_PREAMBLE_SIZE + i * HEADER_OPEN_BLOCK_ENTRY_SIZE);
                        header.writeInt(nextFreeBlock - 1);
                        header.writeInt(remaining);
                        found = true;
                        break;
                    }
                }
                if (!found && openBlockCount < MAX_OPEN_BLOCKS) {
                    header.seek(HEADER_PREAMBLE_SIZE + openBlockCount * HEADER_OPEN_BLOCK_ENTRY_SIZE);
                    header.writeInt(nextFreeBlock - 1);
                    header.writeInt(remaining);
                    openBlockCount++;
                }
            }
            header.seek(8);
            header.writeInt(openBlockCount);
            header.writeInt(nextFreeBlock);
            return page.registerEntry(entrySize);
        }
    }

    /**
     * Clears an entry from a file
     *
     * @param file The backend file containing the entry
     * @param key  The key of the entry to remove
     * @throws IOException      When an IO operation failed
     * @throws StorageException When the page version does not match the expected one
     */
    private void clear(FileStoreFile file, long key) throws IOException, StorageException {
        try (IOElement header = transaction(file, 0, FileStoreFile.BLOCK_SIZE, IOBackend.FLAG_READ | IOBackend.FLAG_WRITE)) {
            FileStorePage page = file.getPageFor(key);
            int length = page.removeEntry(key);
            header.seek(8);
            int openBlockCount = header.readInt();
            int nextFreeBlock = header.readInt();
            for (int i = 0; i != openBlockCount; i++) {
                int blockIndex = header.readInt();
                int blockRemaining = header.readInt();
                if (blockIndex == page.getIndex()) {
                    // already open
                    blockRemaining += length + FileStorePage.ENTRY_OVERHEAD;
                    header.seek(HEADER_PREAMBLE_SIZE + i * HEADER_OPEN_BLOCK_ENTRY_SIZE + 4);
                    header.writeInt(blockRemaining);
                    return;
                }
            }
            // the block was not open
            if (openBlockCount < MAX_OPEN_BLOCKS) {
                int remaining = page.getFreeSpace();
                if (remaining >= THRESHOLD_BLOCK_FULL) {
                    header.seek(HEADER_PREAMBLE_SIZE + openBlockCount * HEADER_OPEN_BLOCK_ENTRY_SIZE);
                    header.writeInt(page.getIndex());
                    header.writeInt(remaining);
                    openBlockCount++;
                    header.seek(8);
                    header.writeInt(openBlockCount);
                    header.writeInt(nextFreeBlock);
                }
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
