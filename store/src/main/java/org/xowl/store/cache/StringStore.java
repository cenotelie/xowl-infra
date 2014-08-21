/**********************************************************************
 * Copyright (c) 2014 Laurent Wouters
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
 **********************************************************************/

package org.xowl.store.cache;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;
import java.util.Arrays;

/**
 * Represents an efficient storage of Strings used in an ontology
 *
 * @author Laurent Wouters
 */
public class StringStore {
    /**
     * The buffer's size
     */
    private static final int BUFFER_SIZE = 2048;
    /**
     * The initial size of the buffer storing the entries
     */
    private static final int INIT_ENTRY_COUNT = 1024;
    /**
     * The initial size of the buffer storing the bucket data
     */
    private static final int INIT_BUCKET_COUNT = 200;

    /**
     * The backing data file
     */
    private RandomAccessFile fileData;
    /**
     * The charset to use
     */
    private Charset charset;
    /**
     * The storage's size
     */
    private int size;
    /**
     * List of entries in this store
     */
    private Entry[] entries;
    /**
     * The number of entries
     */
    private int entryCount;
    /**
     * Hash of the buckets
     */
    private HashBucket[] buckets;
    /**
     * The number of buckets
     */
    private int bucketCount;
    /**
     * A buffer
     */
    private byte[] inner;

    /**
     * Initializes this store
     *
     * @throws java.io.IOException when the store cannot allocate a temporary file
     */
    public StringStore() throws IOException {
        File file = File.createTempFile("store", null);
        this.fileData = new RandomAccessFile(file.getAbsolutePath(), "rw");
        this.charset = Charset.forName("UTF-8");
        this.entries = new Entry[INIT_ENTRY_COUNT];
        this.entryCount = 0;
        this.buckets = new HashBucket[INIT_BUCKET_COUNT];
        this.bucketCount = 0;
        this.inner = new byte[BUFFER_SIZE];
    }

    /**
     * Gets the value for the specified entry
     *
     * @param entry The entry to retrieve
     * @return The value associated to the key
     */
    public String retrieve(int entry) {
        Entry e = entries[entry];
        try {
            fileData.seek(e.getOffset());
            read(e.getSize());
            return new String(inner, 0, e.getSize(), charset);
        } catch (IOException ex) {
            return null;
        }
    }

    /**
     * Reads the specified number of bytes from the store and stores it in the buffer
     *
     * @param length The number of bytes to read
     * @throws IOException when reading fails
     */
    private void read(int length) throws IOException {
        // grows the buffer to the required length
        if (length > inner.length)
            inner = new byte[length];
        // read
        int toRead = length;
        while (toRead > 0) {
            toRead -= fileData.read(inner, length - toRead, toRead);
        }
    }

    /**
     * Stores the specified value in this store
     *
     * @param value The value to store
     * @return The key used to retrieve the value
     */
    public int store(String value) {
        int hash = value.hashCode();
        byte[] bytes = value.getBytes(charset);
        for (int i = 0; i != bucketCount; i++) {
            if (buckets[i].getHash() == hash) {
                return storeInBucket(buckets[i], bytes);
            }
        }
        // this is a new hash => new bucket
        if (bucketCount == buckets.length)
            buckets = Arrays.copyOf(buckets, buckets.length + INIT_BUCKET_COUNT);
        int index = dump(bytes);
        buckets[bucketCount] = new HashBucket(hash);
        buckets[bucketCount].add(index);
        bucketCount++;
        return index;
    }

    /**
     * Stores the specified value in the specified bucket
     *
     * @param bucket The bucket to store in
     * @param bytes  The bytes to store
     * @return The index of the entry
     */
    private int storeInBucket(HashBucket bucket, byte[] bytes) {
        for (int i = 0; i != bucket.getSize(); i++) {
            int index = bucket.getEntry(i);
            Entry entry = entries[bucket.getEntry(i)];
            if (entry.getSize() == bytes.length) {
                try {
                    fileData.seek(entry.getOffset());
                    read(entry.getSize());
                } catch (IOException ex) {
                    continue;
                }
                if (matches(bytes, inner))
                    return index;
            }
        }
        // not in this bucket yet ...
        int index = dump(bytes);
        bucket.add(index);
        return index;
    }

    /**
     * Writes the specified bytes in this store
     *
     * @param bytes The bytes to store
     * @return The key to retrieve the value
     */
    private int dump(byte[] bytes) {
        try {
            Entry entry = new Entry(size, bytes.length);
            fileData.seek(size);
            fileData.write(bytes);
            size += bytes.length;
            if (entryCount == entries.length)
                entries = Arrays.copyOf(entries, entries.length + INIT_ENTRY_COUNT);
            entries[entryCount] = entry;
            entryCount++;
            return entryCount - 1;
        } catch (IOException ex) {
            return -1;
        }
    }

    /**
     * Determines whether two byte arrays are equals
     *
     * @param left  The left byte array
     * @param right The right byte array
     * @return true if the byte arrays are equals
     */
    private boolean matches(byte[] left, byte[] right) {
        for (int i = 0; i != left.length; i++)
            if (left[i] != right[i])
                return false;
        return true;
    }
}
