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

import java.io.IOException;

/**
 * Represents a hash map that is persisted within a file
 *
 * @author Laurent Wouters
 */
class PersistedHashMap {
    /**
     * The default initial size
     */
    private static final int DEFAULT_TABLE_SIZE = 1024;
    /**
     * The default load factor
     */
    private static final float DEFAULT_LOAD_FACTOR = 0.75f;

    /**
     * The number of 8-bytes entries that can fit into a single block
     */
    private static final int ENTRY_PER_BLOCK = 1 << (PersistedFile.BLOCK_INDEX_LENGTH - 3);
    /**
     * The maximum size of the hash table, i.e. the maximum number of buckets
     */
    private static final int MAX_TABLE_SIZE = ENTRY_PER_BLOCK * ENTRY_PER_BLOCK;
    /**
     * Mask for the lower part of a table index
     */
    private static final int TABLE_INDEX_LOWER_MASK = 1 - ENTRY_PER_BLOCK;
    /**
     * The number of bits to shift a table index to the right to get the upper part
     */
    private static final int TABLE_INDEX_UPPER_SHIFT = PersistedFile.BLOCK_INDEX_LENGTH - 3;

    /**
     * The backing file
     */
    private final PersistedFile file;
    /**
     * The location of the map's header block in the file
     */
    private final long locationHeader;
    /**
     * The total number of entries in this map
     */
    private int totalEntryCount;
    /**
     * The size of the bucket table (a power of two)
     */
    private int tableSize;
    /**
     * The threshold to reach before a resize
     */
    private int threshold;
    /**
     * The map's load factor
     */
    private float loadFactor;
    /**
     * The location of the block containing the bucket table header
     */
    private long locationTableHeader;
    /**
     * The location of the next entry
     */
    private long locationNextEntry;

    /**
     * Initializes a new hash map
     *
     * @param file The backing file
     * @throws IOException When an IO operation failed
     */
    public PersistedHashMap(PersistedFile file) throws IOException {
        this(file, DEFAULT_TABLE_SIZE, DEFAULT_LOAD_FACTOR);
    }

    /**
     * Initializes a new hash map
     *
     * @param file       The backing file
     * @param tableSize  The size of the bucket table (a power of two)
     * @param loadFactor The map's load factor
     * @throws IOException When an IO operation failed
     */
    public PersistedHashMap(PersistedFile file, int tableSize, float loadFactor) throws IOException {
        if (tableSize > MAX_TABLE_SIZE)
            tableSize = MAX_TABLE_SIZE;
        file.seekNextBlock();
        this.file = file;
        this.locationHeader = file.getIndex();
        this.totalEntryCount = 0;
        this.tableSize = tableSize;
        this.threshold = (int) (tableSize * loadFactor);
        this.loadFactor = loadFactor;
        this.locationTableHeader = -1;
        this.locationNextEntry = -1;
        file.writeInt(totalEntryCount);
        file.writeInt(tableSize);
        file.writeInt(threshold);
        file.writeFloat(loadFactor);
        file.writeLong(locationTableHeader);
        file.writeLong(locationNextEntry);
    }

    /**
     * Initializes an existing hash map
     *
     * @param file           The backing file
     * @param locationHeader The location of the header block
     * @throws IOException When an IO operation failed
     */
    public PersistedHashMap(PersistedFile file, long locationHeader) throws IOException {
        this.file = file;
        this.locationHeader = locationHeader;
        file.seek(locationHeader);
        this.totalEntryCount = file.readInt();
        this.tableSize = file.readInt();
        this.threshold = file.readInt();
        this.loadFactor = file.readFloat();
        this.locationTableHeader = file.readLong();
        this.locationNextEntry = file.readLong();
    }

    /**
     * Gets the location of the table entry for the specified key
     *
     * @param key     A key into the map
     * @param resolve Whether to create missing path elements to the bucket
     * @return The location of the table entry, or -1 if there is none
     * @throws IOException When an IO operation failed
     */
    private long getEntryLocation(Persistable key, boolean resolve) throws IOException {
        if (locationTableHeader == -1) {
            if (!resolve)
                return -1;
            locationTableHeader = file.seekNextBlock().getIndex();
            file.seek(locationHeader + 16).writeLong(locationTableHeader);
        }
        // hash code for the key
        int hash = hash(key.hashCode());
        // index in the hash table
        int tableIndex = (hash & (1 - tableSize));
        // the index in the table header block
        int upperIndex = tableIndex >>> TABLE_INDEX_UPPER_SHIFT;
        // the location in the file of the upperIndex
        long target = locationTableHeader + (upperIndex << 3);
        if (!file.seek(target).canRead(8)) {
            if (!resolve)
                return -1;
            // the location of the appropriate table block
            long location = file.seekNextBlock().getIndex();
            file.seek(target).writeLong(location);
            target = location;
        } else {
            // the location of the appropriate table block
            target = file.readLong();
        }
        // the location of the entry in the table
        target += (tableIndex & TABLE_INDEX_LOWER_MASK) << 3;
        if (!file.seek(target).canRead(8))
            file.writeLong(-1);
        return target;
    }

    /**
     * Puts a new mapping into this map
     *
     * @param key   A key
     * @param value A value
     * @return The location of the value
     * @throws IOException When an IO operation failed
     */
    public long put(Persistable key, Persistable value) throws IOException {
        int hash = hash(key.hashCode());
        long location = getEntryLocation(key, false);
        long target = file.seek(location).readLong();
        while (target != -1) {
            int eKeySize = file.seek(target).readInt();
            int eValueSize = file.readInt();
            int eHash = file.readInt();
            if (eHash == hash && key.isPersistedIn(file)) {
                // this is a key match
                file.seek(target + eKeySize + 12);
                if (value.isPersistedIn(file))
                    // the entry already exists
                    return target + eKeySize + 12;
            }
            // not matching the hash and key
            // got to the next entry
            location = target + eKeySize + eValueSize + 12;
            target = file.seek(location).readLong();
        }
        // this is a new entry
        // total size of the new entry
        int total = key.persistedLength() + value.persistedLength() + 20;
        // determine the target
        if (locationNextEntry == -1)
            locationNextEntry = file.seekNextBlock().getIndex();
        int remaining = file.seek(locationNextEntry).getRemainingInBlock();
        if (remaining >= total) {
            target = locationNextEntry;
        } else {
            target = file.seekNextBlock().getIndex();
        }
        // write the pointer to the entry
        file.seek(location).writeLong(target);
        // write the entry
        file.seek(target).writeInt(key.persistedLength());
        file.writeInt(value.persistedLength());
        file.writeInt(hash);
        key.persist(file);
        value.persist(file);
        // write back the
        locationNextEntry = file.getIndex();
        file.seek(locationHeader + 16 + 8).writeLong(locationNextEntry);
        return target + key.persistedLength() + 12;
    }

    /**
     * Gets the location of the first found value for the specified key
     *
     * @param key A key
     * @return The location of the first value, or -1 if there is none
     * @throws IOException When an IO operation failed
     */
    public long get(Persistable key) throws IOException {
        long location = getEntryLocation(key, false);
        // the location of the first entry in the bucket
        location = file.seek(location).readLong();
        location = find(file, location, key, hash(key.hashCode()));
        if (location == -1)
            return -1;
        return location + key.persistedLength() + 12;
    }

    /**
     * Gets an iterator over the locations of all the values for the specified key
     *
     * @param key A key
     * @return The iterator
     * @throws IOException When an IO operation failed
     */
    public EntryIterator getAll(Persistable key) throws IOException {
        long location = getEntryLocation(key, false);
        // the location of the first entry in the bucket
        location = file.seek(location).readLong();
        return new EntryIterator(file, key, location);
    }

    /**
     * Hashes the specified value
     *
     * @param value A value
     * @return The associated hash
     */
    private static int hash(int value) {
        return value;
    }

    /**
     * Finds the first matching entry in a bucket
     *
     * @param file     The backing file
     * @param location The current location
     * @param key      The key to look for
     * @param hash     The hash code to look for
     * @return The location of the entry, or -1 if there is none
     * @throws IOException When an IO operation failed
     */
    private static long find(PersistedFile file, long location, Persistable key, int hash) throws IOException {
        while (location != -1) {
            int eKeySize = file.seek(location).readInt();
            int eValueSize = file.readInt();
            int eHash = file.readInt();
            if (eHash == hash && key.isPersistedIn(file)) {
                // this is matching
                return location;
            }
            // not matching the hash and key
            // got to the next entry
            location = file.seek(location + eKeySize + eValueSize + 12).readLong();
        }
        return location;
    }

    /**
     * An iterator over values for a key
     */
    public static class EntryIterator {
        /**
         * The backing file
         */
        private final PersistedFile file;
        /**
         * The key to look for
         */
        private final Persistable key;
        /**
         * The hash code to look for
         */
        private final int hash;
        /**
         * The location of the current entry
         */
        private long location;

        /**
         * Initializes this iterator
         *
         * @param file     The backing file
         * @param key      The key to look for
         * @param location The location of the current entry
         * @throws IOException When an IO operation failed
         */
        EntryIterator(PersistedFile file, Persistable key, long location) throws IOException {
            this.file = file;
            this.key = key;
            this.hash = hash(key.hashCode());
            this.location = find(file, location, key, hash);
        }

        /**
         * Gets whether a next value is available
         *
         * @return true if a next value is available
         */
        public boolean hasNext() {
            return (location != -1);
        }

        /**
         * Gets the location of the next value
         *
         * @return The location of the next value
         * @throws IOException When an IO operation failed
         */
        public long next() throws IOException {
            long result = location + key.persistedLength() + 12;
            lookupNext();
            return result;
        }

        /**
         * Looks up the next matching entry
         *
         * @throws IOException When an IO operation failed
         */
        private void lookupNext() throws IOException {
            int eKeySize = file.seek(location).readInt();
            int eValueSize = file.readInt();
            location = file.seek(location + eKeySize + eValueSize + 12).readLong();
            location = find(file, location, key, hash);
        }
    }
}
