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
 *
 * @author Laurent Wouters
 */
class BackendStringStore extends FileStore {
    /**
     * The suffix of the file backing the store
     */
    private static final String FILE_SUFFIX = "_data.bin";

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
    public BackendStringStore(File directory, String prefix) throws IOException, StorageException {
        super(directory, prefix + FILE_SUFFIX);
        charset = Charset.forName("UTF-8");
    }

    /**
     * Reads the string at the specified index
     *
     * @param key The key to the string
     * @return The string
     * @throws IOException      When an IO operation failed
     * @throws StorageException When the page version does not match the expected one
     */
    public String retrieve(long key) throws IOException, StorageException {
        try (IOElement element = read(key)) {
            element.readLong();
            int length = element.readInt();
            byte[] data = element.readBytes(length);
            return new String(data, charset);
        }
    }

    /**
     * Retrieves the key for the specified string in a bucket
     *
     * @param bucket The key to the bucket for this string
     * @param data   The string to get the key for
     * @return The key for the string, or KEY_NOT_PRESENT if it is not in this store
     * @throws IOException      When an IO operation failed
     * @throws StorageException When the page version does not match the expected one
     */
    public long getKey(long bucket, String data) throws IOException, StorageException {
        byte[] buffer = charset.encode(data).array();
        long candidate = bucket;
        while (candidate != PersistedNode.KEY_NOT_PRESENT) {
            try (IOElement entry = read(candidate)) {
                long next = entry.readLong();
                int size = entry.readInt();
                if (size == buffer.length) {
                    if (Arrays.equals(buffer, entry.readBytes(buffer.length)))
                        // the string is already there, return its key
                        return candidate;
                }
                candidate = next;
            }
        }
        return PersistedNode.KEY_NOT_PRESENT;
    }

    /**
     * Stores the specified string in this backend
     *
     * @param bucket The key to the bucket for this string, or KEY_NOT_PRESENT if it must be created
     * @param data   The string to store
     * @return The key to the stored string
     * @throws IOException      When an IO operation failed
     * @throws StorageException When the page version does not match the expected one
     */
    public long add(long bucket, String data) throws IOException, StorageException {
        byte[] buffer = charset.encode(data).array();
        long previous = PersistedNode.KEY_NOT_PRESENT;
        long candidate = bucket;
        while (candidate != PersistedNode.KEY_NOT_PRESENT) {
            try (IOElement entry = read(candidate)) {
                long next = entry.readLong();
                int size = entry.readInt();
                if (size == buffer.length) {
                    if (Arrays.equals(buffer, entry.readBytes(buffer.length)))
                        // the string is already there, return its key
                        return candidate;
                }
                previous = candidate;
                candidate = next;
            }
        }
        long result = add(buffer.length + 12);
        try (IOElement previousEntry = access(previous)) {
            previousEntry.writeLong(result);
        }
        try (IOElement entry = access(result)) {
            entry.writeLong(PersistedNode.KEY_NOT_PRESENT);
            entry.writeInt(buffer.length);
            entry.writeBytes(buffer);
        }
        return result;
    }
}
