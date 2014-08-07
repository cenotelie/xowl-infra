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
import java.util.HashMap;
import java.util.Map;

/**
 * Represents an efficient storage of Strings used in an ontology
 *
 * @author Laurent Wouters
 */
public class StringStore {
    /**
     * The buffer's size
     */
    private static final int bufferSize = 2048;
    /**
     * The backing file
     */
    private RandomAccessFile file;
    /**
     * The charset to use
     */
    private Charset charset;
    /**
     * The storage's size
     */
    private int size;
    /**
     * The index to this store associating hashes of the values to buckets of values
     */
    private Map<Integer, Key> index;
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
        this.file = new RandomAccessFile(file.getAbsolutePath(), "rw");
        this.charset = Charset.forName("UTF-8");
        this.index = new HashMap<>();
        this.inner = new byte[bufferSize];
    }

    /**
     * Stores the specified value in this store
     *
     * @param value The value to store
     * @return The key used to retrieve the value
     */
    public Key store(String value) {
        Key bucket = index.get(value.hashCode());
        if (bucket != null) {
            Key result = find(bucket, value);
            if (result != null)
                return result;
            result = dump(value);
            insert(bucket, result);
            return result;
        } else {
            bucket = dump(value);
            index.put(value.hashCode(), bucket);
            return bucket;
        }
    }

    /**
     * Inserts the specified key in the bucket
     *
     * @param bucket A bucket
     * @param key    The key to insert
     */
    private void insert(Key bucket, Key key) {
        while (bucket.next != null)
            bucket = bucket.next;
        bucket.next = key;
    }

    /**
     * Retrieve the key for the specified value in the given bucket
     *
     * @param bucket A bucket
     * @param value  The value to search for
     * @return The key for the value, or null if it is not in the store
     */
    private Key find(Key bucket, String value) {
        byte[] bytes = value.getBytes(charset);
        try {
            while (bucket != null) {
                if (bucket.size == bytes.length) {
                    file.seek(bucket.offset);
                    read(bucket.size);
                    if (matches(bytes, inner))
                        return bucket;
                }
                bucket = bucket.next;
            }
            return null;
        } catch (java.io.IOException ex) {
            return null;
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

    /**
     * Writes the specified value in this store
     *
     * @param value A value
     * @return The key to retrieve the value
     */
    private Key dump(String value) {
        try {
            byte[] bytes = value.getBytes(charset);
            Key key = new Key(size, bytes.length);
            file.seek(size);
            file.write(bytes);
            size += bytes.length;
            return key;
        } catch (IOException ex) {
            return null;
        }
    }

    /**
     * Gets the value associated to the specified key
     *
     * @param key A key
     * @return The value associated to the key
     */
    public String retrieve(Key key) {
        try {
            file.seek(key.size);
            read(key.size);
            return new String(inner, 0, key.size, charset);
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
            toRead -= file.read(inner, length - toRead, toRead);
        }
    }

    /**
     * Represents a key to a string in a StringStore
     */
    public static class Key implements org.xowl.store.rdf.Key {
        /**
         * Offset in the store of the value associated to this key
         */
        private int offset;
        /**
         * Size of the value associated to this key
         */
        private int size;
        /**
         * The next key
         */
        private Key next;

        /**
         * Initializes this key
         *
         * @param offset Offset in the store of the value associated to this key
         * @param size   Size of the value associated to this key
         */
        private Key(int offset, int size) {
            this.offset = offset;
            this.size = size;
        }

        @Override
        public int hashCode() {
            return offset;
        }

        @Override
        public boolean equals(Object o) {
            return ((o instanceof Key) && (this == o));
        }
    }
}
