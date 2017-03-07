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

import org.xowl.infra.utils.logging.Logging;
import org.xowl.infra.utils.metrics.Metric;
import org.xowl.infra.utils.metrics.MetricComposite;
import org.xowl.infra.utils.metrics.MetricSnapshot;
import org.xowl.infra.utils.metrics.MetricSnapshotComposite;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A store of objects persisted in files
 * This structure is thread-safe and uses lock-free synchronization mechanisms.
 *
 * @author Laurent Wouters
 */
class FileStore implements AutoCloseable {
    /**
     * The null entry key, denotes the absence of value for a key
     */
    public static final long KEY_NULL = 0xFFFFFFFFFFFFFFFFL;

    /**
     * The store is ready for use
     */
    private static final int STATE_READY = 0;
    /**
     * An operation that touches the files is ongoing
     */
    private static final int STATE_TOUCHING_FILES = 3;

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
     * The state of this store
     */
    private final AtomicInteger state;
    /**
     * The composite metric for this store
     */
    private final MetricComposite metricStore;

    /**
     * Initializes this store
     *
     * @param directory  The directory containing the backing files
     * @param name       The common name of the files backing this store
     * @param isReadonly Whether this store is in readonly mode
     * @throws StorageException When the storage is unsupported
     */
    public FileStore(File directory, String name, boolean isReadonly) throws StorageException {
        this.directory = directory;
        this.name = name;
        this.files = new ArrayList<>();
        this.isReadonly = isReadonly;
        int index = 0;
        File candidate = new File(directory, getNameFor(name, index));
        while (candidate.exists()) {
            FileStoreFile child = new FileStoreFile(candidate, isReadonly, false);
            files.add(child);
            index++;
            candidate = new File(directory, getNameFor(name, index));
        }
        if (files.isEmpty() && !isReadonly) {
            // initializes
            FileStoreFile first = new FileStoreFile(candidate, false, false);
            files.add(first);
        }
        this.state = new AtomicInteger(STATE_READY);
        String fileName = (new File(directory, name + FILE_SUFFIX)).getAbsolutePath();
        this.metricStore = new MetricComposite(FileBackend.class.getCanonicalName() + "[" + fileName + "]",
                "File Store " + fileName,
                1000000000);
        for (FileStoreFile file : files) {
            metricStore.addPart(file.getMetric());
        }
    }

    /**
     * Gets whether this store is empty
     *
     * @return Whether this store is empty
     */
    public boolean isEmpty() {
        return files.size() == 1 && files.get(0).getSize() <= FileBlock.BLOCK_SIZE;
    }

    /**
     * Gets the composite metric for this store
     *
     * @return The metric for this store
     */
    public Metric getMetric() {
        return metricStore;
    }

    /**
     * Gets a snapshot of the metrics for this store
     *
     * @param timestamp The timestamp to use
     * @return The snapshot
     */
    public MetricSnapshot getMetricSnapshot(long timestamp) {
        MetricSnapshotComposite snapshot = new MetricSnapshotComposite(timestamp);
        for (FileStoreFile file : files) {
            snapshot.addPart(file.getMetric(), file.getMetricSnapshot(timestamp));
        }
        return snapshot;
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
        for (int i = 0; i != files.size(); i++) {
            try {
                files.get(i).flush();
            } catch (StorageException exception) {
                Logging.get().error(exception);
                success = false;
            }
        }
        return success;
    }

    @Override
    public void close() {
        for (int i = 0; i != files.size(); i++) {
            try {
                files.get(i).close();
            } catch (IOException exception) {
                Logging.get().error(exception);
            }
        }
    }

    /**
     * Removes all data from this store
     */
    public void clear() {
        if (isReadonly)
            return;
        while (true) {
            if (state.compareAndSet(STATE_READY, STATE_TOUCHING_FILES))
                break;
        }
        for (int i = 0; i != files.size(); i++) {
            try {
                files.get(i).close();
            } catch (IOException exception) {
                Logging.get().error(exception);
            }
            File target = new File(directory, getNameFor(name, i));
            if (!target.delete()) {
                Logging.get().error("Failed to delete file " + target.getAbsolutePath());
            }
        }
        files.clear();
        metricStore.clearParts();
        try {
            FileStoreFile first = new FileStoreFile(new File(directory, getNameFor(name, 0)), false, false);
            files.add(first);
        } catch (StorageException exception) {
            Logging.get().error(exception);
        }

        state.set(STATE_READY);
    }

    /**
     * Gets a read and write access to the object for the specified key
     *
     * @param key The key to an entry
     * @return The IO access that can be used for reading and writing
     * @throws StorageException When an IO operation failed
     */
    public IOAccess accessW(long key) throws StorageException {
        return access(key, true);
    }

    /**
     * Gets a reading access to the object for the specified key
     *
     * @param key The key to an entry
     * @return The IO access that can be used for reading
     * @throws StorageException When an IO operation failed
     */
    public IOAccess accessR(long key) throws StorageException {
        return access(key, false);
    }

    /**
     * Gets an access to the object for the specified key
     *
     * @param key      The key to an object
     * @param writable Whether the transaction allows writing to the backend
     * @return The IO access that can be used for reading and writing
     * @throws StorageException When an IO operation failed
     */
    private IOAccess access(long key, boolean writable) throws StorageException {
        if (key == KEY_NULL)
            throw new StorageException("Invalid key (null key)");
        return files.get(getFileIndexFor(key)).access(getShortKey(key), writable);
    }

    /**
     * Allocate space for a new object
     *
     * @param size The size of the object
     * @return The key to the allocated space
     * @throws StorageException When an IO operation failed
     */
    public long allocate(int size) throws StorageException {
        if (isReadonly)
            throw new StorageException("The store is read only");
        while (true) {
            // try to allocate from the last file
            int count = files.size();
            long result = files.get(count - 1).allocate(size);
            if (result != KEY_NULL)
                return result;
            // failed to allocate from the last file, try to allocate a new file
            while (true) {
                if (state.compareAndSet(STATE_READY, STATE_TOUCHING_FILES))
                    break;
            }
            if (count != files.size()) {
                // the files changed
                state.set(STATE_READY);
                continue;
            }
            // allocate a new file
            try {
                FileStoreFile file = new FileStoreFile(new File(directory, getNameFor(name, files.size())), false, false);
                files.add(file);
                metricStore.addPart(file.getMetric());
                state.set(STATE_READY);
            } catch (StorageException exception) {
                state.set(STATE_READY);
                throw exception;
            }
            // retry
        }
    }

    /**
     * Allocate space for a new object
     * This method directly allocate the object without looking up for reusable space.
     *
     * @param size The size of the object
     * @return The key to the allocated space
     * @throws StorageException When an IO operation failed
     */
    public long allocateDirect(int size) throws StorageException {
        if (isReadonly)
            throw new StorageException("The store is read only");
        while (true) {
            // try to allocate from the last file
            int count = files.size();
            long result = files.get(count - 1).allocateDirect(size);
            if (result != KEY_NULL)
                return result;
            // failed to allocate from the last file, try to allocate a new file
            while (true) {
                if (state.compareAndSet(STATE_READY, STATE_TOUCHING_FILES))
                    break;
            }
            if (count != files.size()) {
                // the files changed
                state.set(STATE_READY);
                continue;
            }
            // allocate a new file
            try {
                FileStoreFile file = new FileStoreFile(new File(directory, getNameFor(name, files.size())), false, false);
                files.add(file);
                metricStore.addPart(file.getMetric());
                state.set(STATE_READY);
            } catch (StorageException exception) {
                state.set(STATE_READY);
                throw exception;
            }
            // retry
        }
    }

    /**
     * Frees the object for the specified key
     *
     * @param key The key of the entry to free
     * @throws StorageException When an IO operation failed
     */
    public void free(long key) throws StorageException {
        if (isReadonly)
            throw new StorageException("The store is read only");
        if (key == KEY_NULL)
            throw new StorageException("Invalid key (null key)");
        files.get(getFileIndexFor(key)).free(getShortKey(key));
    }

    /**
     * Frees the object for the specified key
     *
     * @param key    The key of the entry to free
     * @param length The expected length of the object to free
     * @throws StorageException When an IO operation failed
     */
    public void free(long key, int length) throws StorageException {
        if (isReadonly)
            throw new StorageException("The store is read only");
        if (key == KEY_NULL)
            throw new StorageException("Invalid key (null key)");
        files.get(getFileIndexFor(key)).free(getShortKey(key), length);
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
