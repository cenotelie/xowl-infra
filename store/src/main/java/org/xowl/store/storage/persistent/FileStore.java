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
 *
 *
 * Each data file is composed of blocks (or pages)
 * - First block:
 * - int32: Magic identifier for the store
 * - int32: Layout version
 * - int32: number of open blocks (blocks that contain data but are not full)
 * - int32: index of the next block to open (in number of block)
 * - array of block entries:
 * - int32: index of the block
 * - int32: remaining free space
 * @author Laurent Wouters
 */
class FileStore implements AutoCloseable {
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
     * @param directory The directory containing the backing files
     * @param name The common name of the files backing this store
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

    /**
     * Gets the name of the i-th file
     * @param radical The name radical
     * @param index The index
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
     * @param index The index
     * @return The key radical
     */
    private static long getRadicalFor(int index) {
        return ((long)index) << 32;
    }

    /**
     * Initializes a backing file
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

    @Override
    public void close() throws IOException {
        for (FileStoreFile child : files)
            child.close();
    }
}
