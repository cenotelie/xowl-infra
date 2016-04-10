/*******************************************************************************
 * Copyright (c) 2016 Laurent Wouters
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

package org.xowl.infra.store.storage.persistent;

import java.io.Closeable;

/**
 * API for elements that can be read from and written to
 *
 * @author Laurent Wouters
 */
interface IOElement extends Closeable {
    /**
     * Attempts to lock this element for an exclusive use
     *
     * @throws StorageException When the block is in a bad state
     */
    void lock() throws StorageException;

    /**
     * Attempts to release this element when an exclusive use has finished
     *
     * @throws StorageException When the block is in a bad state
     */
    void release() throws StorageException;

    /**
     * Gets the size of this element
     *
     * @return The size of this element, or -1 if the operation failed
     */
    long getSize();

    /**
     * Reads a single byte at the current index
     *
     * @param index The index within this element for this operation
     * @return The byte
     * @throws StorageException When an IO operation failed
     */
    byte readByte(long index) throws StorageException;

    /**
     * Reads a specified number of bytes a the current index
     *
     * @param index  The index within this element for this operation
     * @param length The number of bytes to read
     * @return The bytes
     * @throws StorageException When an IO operation failed
     */
    byte[] readBytes(long index, int length) throws StorageException;

    /**
     * Reads a specified number of bytes a the current index
     *
     * @param index  The index within this element for this operation
     * @param buffer The buffer to fill
     * @param start  The index in the buffer to start filling at
     * @param length The number of bytes to read
     * @throws StorageException When an IO operation failed
     */
    void readBytes(long index, byte[] buffer, int start, int length) throws StorageException;

    /**
     * Reads a single char at the current index
     *
     * @param index The index within this element for this operation
     * @return The char
     * @throws StorageException When an IO operation failed
     */
    char readChar(long index) throws StorageException;

    /**
     * Reads a single int at the current index
     *
     * @param index The index within this element for this operation
     * @return The int
     * @throws StorageException When an IO operation failed
     */
    int readInt(long index) throws StorageException;

    /**
     * Reads a single long at the current index
     *
     * @param index The index within this element for this operation
     * @return The long
     * @throws StorageException When an IO operation failed
     */
    long readLong(long index) throws StorageException;

    /**
     * Reads a single float at the current index
     *
     * @param index The index within this element for this operation
     * @return The float
     * @throws StorageException When an IO operation failed
     */
    float readFloat(long index) throws StorageException;

    /**
     * Reads a single double at the current index
     *
     * @param index The index within this element for this operation
     * @return The double
     * @throws StorageException When an IO operation failed
     */
    double readDouble(long index) throws StorageException;

    /**
     * Writes a single byte at the current index
     *
     * @param index The index within this element for this operation
     * @param value The byte to write
     * @throws StorageException When an IO operation failed
     */
    void writeByte(long index, byte value) throws StorageException;

    /**
     * Writes bytes at the current index
     *
     * @param index The index within this element for this operation
     * @param value The bytes to write
     * @throws StorageException When an IO operation failed
     */
    void writeBytes(long index, byte[] value) throws StorageException;

    /**
     * Writes bytes at the current index
     *
     * @param index  The index within this element for this operation
     * @param buffer The buffer with the bytes to write
     * @param start  The index in the buffer to start writing from
     * @param length The number of bytes to write
     * @throws StorageException When an IO operation failed
     */
    void writeBytes(long index, byte[] buffer, int start, int length) throws StorageException;

    /**
     * Writes a single char at the current index
     *
     * @param index The index within this element for this operation
     * @param value The char to write
     * @throws StorageException When an IO operation failed
     */
    void writeChar(long index, char value) throws StorageException;

    /**
     * Writes a single int at the current index
     *
     * @param index The index within this element for this operation
     * @param value The int to write
     * @throws StorageException When an IO operation failed
     */
    void writeInt(long index, int value) throws StorageException;

    /**
     * Writes a single long at the current index
     *
     * @param index The index within this element for this operation
     * @param value The long to write
     * @throws StorageException When an IO operation failed
     */
    void writeLong(long index, long value) throws StorageException;

    /**
     * Writes a single float at the current index
     *
     * @param index The index within this element for this operation
     * @param value The float to write
     * @throws StorageException When an IO operation failed
     */
    void writeFloat(long index, float value) throws StorageException;

    /**
     * Writes a single double at the current index
     *
     * @param index The index within this element for this operation
     * @param value The double to write
     * @throws StorageException When an IO operation failed
     */
    void writeDouble(long index, double value) throws StorageException;
}
