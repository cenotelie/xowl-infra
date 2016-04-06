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
 * Base API for an IO transaction on an IOElement
 * The transaction defines a span within the backend that can be accessed.
 * Only operations within this span are allowed.
 * The beginning of the span is a mapped to the 0 index of the transaction element.
 *
 * @author Laurent Wouters
 */
class IOTransaction implements Closeable {
    /**
     * The backing IO element
     */
    protected IOElement backend;
    /**
     * The location in the backend
     */
    protected long location;
    /**
     * The length of the proxy in the backend
     */
    protected long length;
    /**
     * Whether the transaction allows writing
     */
    protected boolean writable;
    /**
     * The current index in the backend
     */
    protected long index;

    /**
     * Gets whether the specified number of bytes at the current index are within the allowed bounds
     *
     * @param length A number of bytes
     * @return true if the bytes are within the bounds
     */
    protected boolean withinBounds(int length) {
        return (index >= location && index + length <= location + length);
    }

    /**
     * Setups this transaction before using it
     *
     * @param backend  The backend IO element
     * @param location The location of the span for this transaction within the backend
     * @param length   The length of the allowed span
     * @param writable Whether the transaction allows writing
     * @param time     The current time
     * @return Whether the operation succeeded
     */
    public boolean setup(IOElement backend, long location, long length, boolean writable, long time) {
        this.backend = backend;
        this.location = location;
        this.length = length;
        this.writable = writable;
        this.index = location;
        return writable ? backend.onWriteBegin(time) : backend.onReadBegin(time);
    }

    /**
     * Positions the index of this element
     *
     * @param index The new index
     * @return The element
     */
    public IOTransaction seek(long index) {
        this.index = location + index;
        return this;
    }

    /**
     * Resets the index in this element to its initial position
     *
     * @return The element
     */
    public IOTransaction reset() {
        this.index = location;
        return this;
    }

    /**
     * Gets whether the specified amount of bytes can be read at the current index
     *
     * @param length The number of bytes to read
     * @return true if this is legal to read
     */
    public boolean canRead(int length) {
        return (withinBounds(length) && backend.canRead(index, length));
    }

    /**
     * Reads a single byte at the current index
     *
     * @return The byte
     * @throws StorageException When an IO operation failed
     */
    public byte readByte() throws StorageException {
        if (!canRead(1))
            throw new IndexOutOfBoundsException("Cannot read the specified amount of data at this index");
        return backend.readByte(index);
    }

    /**
     * Reads a specified number of bytes a the current index
     *
     * @param length The number of bytes to read
     * @return The bytes
     * @throws StorageException When an IO operation failed
     */
    public byte[] readBytes(int length) throws StorageException {
        if (!canRead(length))
            throw new IndexOutOfBoundsException("Cannot read the specified amount of data at this index");
        return backend.readBytes(index, length);
    }

    /**
     * Reads a specified number of bytes a the current index
     *
     * @param buffer The buffer to fill
     * @param start  The index in the buffer to start filling at
     * @param length The number of bytes to read
     * @throws StorageException When an IO operation failed
     */
    public void readBytes(byte[] buffer, int start, int length) throws StorageException {
        if (!canRead(length))
            throw new IndexOutOfBoundsException("Cannot read the specified amount of data at this index");
        backend.readBytes(index, buffer, start, length);
    }

    /**
     * Reads a single char at the current index
     *
     * @return The char
     * @throws StorageException When an IO operation failed
     */
    public char readChar() throws StorageException {
        if (!canRead(2))
            throw new IndexOutOfBoundsException("Cannot read the specified amount of data at this index");
        return backend.readChar(index);
    }

    /**
     * Reads a single int at the current index
     *
     * @return The int
     * @throws StorageException When an IO operation failed
     */
    public int readInt() throws StorageException {
        if (!canRead(4))
            throw new IndexOutOfBoundsException("Cannot read the specified amount of data at this index");
        return backend.readInt(index);
    }

    /**
     * Reads a single long at the current index
     *
     * @return The long
     * @throws StorageException When an IO operation failed
     */
    public long readLong() throws StorageException {
        if (!canRead(8))
            throw new IndexOutOfBoundsException("Cannot read the specified amount of data at this index");
        return backend.readLong(index);
    }

    /**
     * Reads a single float at the current index
     *
     * @return The float
     * @throws StorageException When an IO operation failed
     */
    public float readFloat() throws StorageException {
        if (!canRead(4))
            throw new IndexOutOfBoundsException("Cannot read the specified amount of data at this index");
        return backend.readFloat(index);
    }

    /**
     * Reads a single double at the current index
     *
     * @return The double
     * @throws StorageException When an IO operation failed
     */
    public double readDouble() throws StorageException {
        if (!canRead(8))
            throw new IndexOutOfBoundsException("Cannot read the specified amount of data at this index");
        return backend.readDouble(index);
    }

    /**
     * Writes a single byte at the current index
     *
     * @param value The byte to write
     * @throws StorageException When an IO operation failed
     */
    public void writeByte(byte value) throws StorageException {
        if (!writable || !withinBounds(1))
            throw new IndexOutOfBoundsException("Cannot write the specified amount of data at this index");
        backend.writeByte(index, value);
    }

    /**
     * Writes bytes at the current index
     *
     * @param value The bytes to write
     * @throws StorageException When an IO operation failed
     */
    public void writeBytes(byte[] value) throws StorageException {
        if (!writable || !withinBounds(value.length))
            throw new IndexOutOfBoundsException("Cannot write the specified amount of data at this index");
        backend.writeBytes(index, value);
    }

    /**
     * Writes bytes at the current index
     *
     * @param buffer The buffer with the bytes to write
     * @param start  The index in the buffer to start writing from
     * @param length The number of bytes to write
     * @throws StorageException When an IO operation failed
     */
    public void writeBytes(byte[] buffer, int start, int length) throws StorageException {
        if (!writable || !withinBounds(length))
            throw new IndexOutOfBoundsException("Cannot write the specified amount of data at this index");
        backend.writeBytes(index, buffer, start, length);
    }

    /**
     * Writes a single char at the current index
     *
     * @param value The char to write
     * @throws StorageException When an IO operation failed
     */
    public void writeChar(char value) throws StorageException {
        if (!writable || !withinBounds(2))
            throw new IndexOutOfBoundsException("Cannot write the specified amount of data at this index");
        backend.writeChar(index, value);
    }

    /**
     * Writes a single int at the current index
     *
     * @param value The int to write
     * @throws StorageException When an IO operation failed
     */
    public void writeInt(int value) throws StorageException {
        if (!writable || !withinBounds(4))
            throw new IndexOutOfBoundsException("Cannot write the specified amount of data at this index");
        backend.writeInt(index, value);
    }

    /**
     * Writes a single long at the current index
     *
     * @param value The long to write
     * @throws StorageException When an IO operation failed
     */
    public void writeLong(long value) throws StorageException {
        if (!writable || !withinBounds(8))
            throw new IndexOutOfBoundsException("Cannot write the specified amount of data at this index");
        backend.writeLong(index, value);
    }

    /**
     * Writes a single float at the current index
     *
     * @param value The float to write
     * @throws StorageException When an IO operation failed
     */
    public void writeFloat(float value) throws StorageException {
        if (!writable || !withinBounds(4))
            throw new IndexOutOfBoundsException("Cannot write the specified amount of data at this index");
        backend.writeFloat(index, value);
    }

    /**
     * Writes a single double at the current index
     *
     * @param value The double to write
     * @throws StorageException When an IO operation failed
     */
    public void writeDouble(double value) throws StorageException {
        if (!writable || !withinBounds(8))
            throw new IndexOutOfBoundsException("Cannot write the specified amount of data at this index");
        backend.writeDouble(index, value);
    }

    @Override
    public void close() {
        if (writable)
            backend.onWriteEnd();
        else
            backend.onReadEnd();
    }
}
