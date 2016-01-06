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
 * This class is NOT thread safe.
 * <p/>
 * Each data file is composed of blocks (or pages)
 * - First block:
 * - int32: Magic identifier for the store
 * - int32: Layout version
 * - int32: Number of open blocks (blocks that contain data but are not full)
 * - int32: Index of the next block to open (in number of block)
 * - array of block entries:
 * - int32: Index of the block
 * - int32: Remaining free space
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
     * int: Magic identifier for the store
     * int: Layout version
     * int: Number of open blocks (blocks that contain data but are not full)
     * int: Index of the next block to open (in number of block)
     */
    private static final int HEADER_PREAMBLE_SIZE = 4 + 4 + 4 + 4;
    /**
     * The size of an open block entry in the header
     * int: Index of the block
     * int: Remaining free space
     */
    private static final int HEADER_OPEN_BLOCK_ENTRY_SIZE = 4 + 4;


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
     * Whether this store is in readonly mode
     */
    private final boolean isReadonly;

    /**
     * Gets whether this store is in readonly mode
     *
     * @return Whether this store is in readonly mode
     */
    public boolean isReadonly() {
        return isReadonly;
    }

    /**
     * Initializes this store
     *
     * @param directory  The directory containing the backing files
     * @param name       The common name of the files backing this store
     * @param isReadonly Whether this store is in readonly mode
     * @throws IOException      When the backing file cannot be accessed
     * @throws StorageException When the storage is unsupported
     */
    public FileStore(File directory, String name, boolean isReadonly) throws IOException, StorageException {
        this.directory = directory;
        this.name = name;
        this.files = new ArrayList<>();
        this.isReadonly = isReadonly;
        int index = 0;
        File candidate = new File(directory, getNameFor(name, index));
        while (candidate.exists()) {
            FileStoreFile child = new FileStoreFile(candidate, isReadonly);
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
        if (files.isEmpty() && !isReadonly) {
            // initializes
            FileStoreFile first = new FileStoreFile(candidate, false);
            initializeFile(first);
            files.add(first);
        }
    }

    /**
     * Commits the outstanding data
     *
     * @return Whether the operation succeeded
     */
    public boolean commit() {
        state = STATE_COMMITTING;
        boolean success = true;
        for (FileStoreFile child : files) {
            success &= child.commit();
        }
        state = (success ? STATE_READY : STATE_ERROR);
        return success;
    }

    /**
     * Rollback outstanding changes
     *
     * @return Whether the operation fully succeeded
     */
    public boolean rollback() {
        state = STATE_COMMITTING;
        boolean success = true;
        for (FileStoreFile child : files) {
            success &= child.rollback();
        }
        state = (success ? STATE_READY : STATE_ERROR);
        return success;
    }

    @Override
    public void close() throws IOException {
        state = STATE_CLOSING;
        for (FileStoreFile child : files) {
            try {
                child.close();
            } catch (IOException exception) {
                // do nothing
            }
        }
        state = STATE_CLOSED;
    }

    /**
     * Removes all data from this store
     */
    public void clear() {
        if (isReadonly)
            return;
        state = STATE_COMMITTING;
        for (int i = 0; i != files.size(); i++) {
            try {
                files.get(i).close();
            } catch (IOException exception) {
                // do nothing
            }
            File target = new File(directory, getNameFor(name, i));
            target.delete();
        }
        files.clear();
        try {
            FileStoreFile first = new FileStoreFile(new File(directory, getNameFor(name, 0)), false);
            initializeFile(first);
            files.add(first);
        } catch (StorageException | IOException exception) {
            // do nothing
        }
        state = STATE_READY;
    }

    /**
     * Gets an access to the entry for the specified key
     *
     * @param key The key to an entry
     * @return The IO element that can be used for reading and writing
     * @throws StorageException When an IO operation failed
     */
    public IOElement access(long key) throws StorageException {
        return access(key, true);
    }

    /**
     * Gets a reading access to the entry for the specified key
     *
     * @param key The key to an entry
     * @return The IO element that can be used for reading
     * @throws StorageException When an IO operation failed
     */
    public IOElement read(long key) throws StorageException {
        return access(key, false);
    }

    /**
     * Gets an access to the entry for the specified key
     *
     * @param key      The key to an entry
     * @param writable Whether the transaction allows writing to the backend
     * @return The IO element that can be used for reading and writing
     * @throws StorageException When an IO operation failed
     */
    protected IOElement access(long key, boolean writable) throws StorageException {
        if (key <= 0)
            throw new StorageException("Invalid key");
        int index = getFileIndexFor(key);
        int sk = getShortKey(key);
        FileStoreFile file = files.get(index);
        FileStorePage page = file.getPageFor(sk);
        int length = page.positionFor(sk);
        return transaction(file, file.getIndex(), length, !isReadonly && writable);
    }

    /**
     * Adds a new entry of the specified size
     *
     * @param entrySize The size of the entry to write
     * @return The key for retrieving the data
     * @throws IOException      When a new file cannot be allocated
     * @throws StorageException When an IO operation failed
     */
    public long add(int entrySize) throws IOException, StorageException {
        if (isReadonly)
            throw new StorageException("The store is read only");
        if (entrySize > FileStorePage.MAX_ENTRY_SIZE)
            throw new StorageException("The entry is too large for this store");
        FileStoreFile file = files.get(files.size() - 1);
        long result = provision(files.size() - 1, file, entrySize);
        if (result == -1) {
            file = new FileStoreFile(new File(directory, getNameFor(name, files.size())), false);
            initializeFile(file);
            files.add(file);
            result = provision(files.size() - 1, file, entrySize);
        }
        return result;
    }

    /**
     * Removes the entry for the specified key
     *
     * @param key The key of the entry to remove
     * @throws StorageException When an IO operation failed
     */
    public void remove(long key) throws StorageException {
        if (isReadonly)
            throw new StorageException("The store is read only");
        int index = getFileIndexFor(key);
        FileStoreFile file = files.get(index);
        clear(file, getShortKey(key));
    }

    /**
     * Provisions an entry of the specified size in a file
     *
     * @param fileIndex The index of the backend file to write to
     * @param file      The backend file to write to
     * @param entrySize The size of the entry to write
     * @return The key for retrieving the data
     * @throws StorageException When an IO operation failed
     */
    private long provision(int fileIndex, FileStoreFile file, int entrySize) throws StorageException {
        try (IOElement header = transaction(file, 0, FileStoreFile.BLOCK_SIZE, true)) {
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
                    long key = getFullKey(fileIndex, page.registerEntry(entrySize));
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
                return PersistedNode.KEY_NOT_PRESENT;
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
            return getFullKey(fileIndex, page.registerEntry(entrySize));
        } catch (IOException exception) {
            // an IOException cannot happen
            return PersistedNode.KEY_NOT_PRESENT;
        }
    }

    /**
     * Clears an entry from a file
     *
     * @param file The backend file containing the entry
     * @param key  The key of the entry to remove
     * @throws StorageException When an IO operation failed
     */
    private void clear(FileStoreFile file, int key) throws StorageException {
        try (IOElement header = transaction(file, 0, FileStoreFile.BLOCK_SIZE, true)) {
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
        } catch (IOException exception) {
            // an IOException cannot happen
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
     * Gets the file index for the specified key
     *
     * @param key A key to an entry
     * @return The index of the corresponding file
     */
    private static int getFileIndexFor(long key) {
        return (int) (key >>> 32);
    }

    /**
     * Gets the key radical for the specified key
     *
     * @param key A key
     * @return The radical
     */
    public static int getKeyRadical(long key) {
        return getFileIndexFor(key);
    }

    /**
     * Gets the short key for the specified one
     *
     * @param key A key
     * @return The short key
     */
    public static int getShortKey(long key) {
        return (int) (key & 0xFFFFFFFFL);
    }

    /**
     * Gets the full key a radical and a short key
     *
     * @param radical  The radical
     * @param shortKey The short key
     * @return The full key
     */
    public static long getFullKey(int radical, int shortKey) {
        return (((long) radical << 32) | (long) shortKey);
    }

    /**
     * Initializes a backing file
     *
     * @param file The file to initialize
     * @throws StorageException When an IO error occurred
     */
    private static void initializeFile(FileStoreFile file) throws StorageException {
        file.seek(0);
        file.writeInt(MAGIC_ID);
        file.writeInt(LAYOUT_VERSION);
        file.writeInt(0);
        file.writeInt(1);
        if (!file.commit())
            throw new StorageException("Failed to initialize the file");
    }
}
