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

import org.xowl.infra.utils.logging.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A store of binary data backed by files
 *
 * @author Laurent Wouters
 */
class FileStore {
    /**
     * The null entry key, denotes the absence of value for a key
     */
    public static final long KEY_NULL = 0xFFFFFFFFFFFFFFFFL;

    /**
     * The suffix of store files
     */
    private static final String FILE_SUFFIX = ".xowl";

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
            files.add(child);
            index++;
            candidate = new File(directory, getNameFor(name, index));
        }
        if (files.isEmpty() && !isReadonly) {
            // initializes
            FileStoreFile first = new FileStoreFile(candidate, false);
            files.add(first);
        }
    }

    /**
     * Gets whether this store is empty
     *
     * @return Whether this store is empty
     */
    public boolean isEmpty() {
        return files.size() == 1 && files.get(0).getSize() <= FileStoreFileBlock.BLOCK_SIZE;
    }

    /**
     * Flushes any outstanding changes to the backing files
     *
     * @return Whether the operation succeeded
     */
    public boolean flush() {
        if (isReadonly)
            return true;
        boolean success = true;
        synchronized (files) {
            for (FileStoreFile child : files) {
                try {
                    child.flush();
                } catch (StorageException exception) {
                    Logger.DEFAULT.error(exception);
                    success = false;
                }
            }
        }
        return success;
    }

    /**
     * Closes this store
     */
    public void close() {
        synchronized (files) {
            for (FileStoreFile child : files) {
                try {
                    child.close();
                } catch (IOException exception) {
                    Logger.DEFAULT.error(exception);
                }
            }
        }
    }

    /**
     * Removes all data from this store
     */
    public void clear() {
        if (isReadonly)
            return;
        synchronized (files) {
            for (int i = 0; i != files.size(); i++) {
                try {
                    files.get(i).close();
                } catch (IOException exception) {
                    Logger.DEFAULT.error(exception);
                }
                File target = new File(directory, getNameFor(name, i));
                if (!target.delete()) {
                    Logger.DEFAULT.error("Failed to delete file " + target.getAbsolutePath());
                }
            }
            files.clear();
            try {
                FileStoreFile first = new FileStoreFile(new File(directory, getNameFor(name, 0)), false);
                files.add(first);
            } catch (StorageException exception) {
                Logger.DEFAULT.error(exception);
            }
        }
    }

    /**
     * Gets an access to the entry for the specified key
     *
     * @param key The key to an entry
     * @return The IO element that can be used for reading and writing
     * @throws StorageException When an IO operation failed
     */
    public IOAccess access(long key) throws StorageException {
        return access(key, true);
    }

    /**
     * Gets a reading access to the entry for the specified key
     *
     * @param key The key to an entry
     * @return The IO element that can be used for reading
     * @throws StorageException When an IO operation failed
     */
    public IOAccess read(long key) throws StorageException {
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
    protected IOAccess access(long key, boolean writable) throws StorageException {
        if (key == KEY_NULL)
            throw new StorageException("Invalid key (null key)");
        return files.get(getFileIndexFor(key)).accessEntry(getShortKey(key), writable);
    }

    /**
     * Adds a new entry of the specified size
     *
     * @param entrySize The size of the entry to write
     * @return The key for retrieving the data
     * @throws StorageException When an IO operation failed
     */
    public long add(int entrySize) throws StorageException {
        if (isReadonly)
            throw new StorageException("The store is read only");
        if (entrySize > FileStoreFileBlock.MAX_ENTRY_SIZE)
            throw new StorageException("The entry is too large for this store");
        FileStoreFile file = files.get(files.size() - 1);
        int result = file.allocateEntry(entrySize);
        if (result == -1) {
            synchronized (files) {
                file = new FileStoreFile(new File(directory, getNameFor(name, files.size())), false);
                files.add(file);
                result = file.allocateEntry(entrySize);
            }
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
        if (key == KEY_NULL)
            throw new StorageException("Invalid key (null key)");
        files.get(getFileIndexFor(key)).removeEntry(getShortKey(key));
    }

    /**
     * Gets the name of the i-th file
     *
     * @param radical The name radical
     * @param index   The index
     * @return The name of the file
     */
    private static String getNameFor(String radical, int index) {
        String num = Integer.toString(index);
        while (num.length() < 3)
            num = "0" + num;
        return radical + num + FILE_SUFFIX;
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
}
