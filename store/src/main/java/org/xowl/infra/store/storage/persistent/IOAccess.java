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

/**
 * Base API for a controlled access to an IO backend element
 * The access defines a span within the backend element that can be accessed.
 * Only operations within this span are allowed.
 * The beginning of the span is a mapped to the 0 index of this access element.
 * The access element keeps track of its current index within the span and will automatically update it upon reading and writing.
 *
 * @author Laurent Wouters
 */
abstract class IOAccess implements AutoCloseable {
    /**
     * The backing IO element
     */
    protected IOElement element;
    /**
     * The location in the backend
     */
    protected int location;
    /**
     * The length of the proxy in the backend
     */
    protected int length;
    /**
     * Whether the access allows writing
     */
    protected boolean writable;
    /**
     * The current index in the backend
     */
    private int index;

    /**
     * Setups this access before using it
     *
     * @param location The location of the span for this access within the backend
     * @param length   The length of the allowed span
     * @param writable Whether the access allows writing
     */
    protected void setupIOData(int location, int length, boolean writable) {
        this.location = location;
        this.length = length;
        this.writable = writable;
        this.index = location;
    }

    /**
     * Setups this access before using it
     *
     * @param backend The backend IO element
     */
    protected void setupIOData(IOElement backend) {
        this.element = backend;
    }

    /**
     * Gets the location of this access in the backend
     *
     * @return The location of this access in the backend
     */
    public int getLocation() {
        return location;
    }

    /**
     * Gets the current index of this access
     * The index is local to this access, meaning that 0 represents the start of the access window in the associated backend.
     *
     * @return The current access index
     */
    public int getIndex() {
        return (index - location);
    }

    /**
     * Gets the length of this access window in the associated backend
     *
     * @return The length of this access window
     */
    public int getLength() {
        return length;
    }

    /**
     * Gets whether this access is disjoint from the specified one
     *
     * @param access An access
     * @return Whether the two access are disjoint
     */
    protected boolean disjoints(IOAccess access) {
        return (this.location + this.length <= access.location) // this is completely before parameter
                || (access.location + access.length <= this.location); // parameter is completely before this
    }

    /**
     * Positions the index of this access
     * The index is local to this access, meaning that 0 represents the start of the access window in the associated backend.
     *
     * @param index The new access index
     * @return This access
     */
    public IOAccess seek(int index) {
        this.index = location + index;
        return this;
    }

    /**
     * Resets the index of this access to its initial position
     * The index is local to this access, meaning that 0 represents the start of the access window in the associated backend.
     *
     * @return This access
     */
    public IOAccess reset() {
        this.index = location;
        return this;
    }

    /**
     * Moves the index of this access
     * The index is local to this access, meaning that 0 represents the start of the access window in the associated backend.
     *
     * @param offset The offset to move from
     * @return This access
     */
    public IOAccess skip(int offset) {
        this.index += offset;
        return this;
    }

    /**
     * Gets whether the specified number of bytes at the current index are within the allowed bounds
     *
     * @param length A number of bytes
     * @return true if the bytes are within the bounds
     */
    private boolean withinBounds(int length) {
        return (index >= location && index + length <= location + this.length);
    }

    /**
     * Reads a single byte at the current index
     *
     * @return The byte
     */
    public byte readByte() {
        if (!withinBounds(1))
            throw new IndexOutOfBoundsException("Cannot read the specified amount of data at this index");
        byte value = element.readByte(index);
        index++;
        return value;
    }

    /**
     * Reads a specified number of bytes a the current index
     *
     * @param length The number of bytes to read
     * @return The bytes
     */
    public byte[] readBytes(int length) {
        if (!withinBounds(length))
            throw new IndexOutOfBoundsException("Cannot read the specified amount of data at this index");
        byte[] value = element.readBytes(index, length);
        index += length;
        return value;
    }

    /**
     * Reads a specified number of bytes a the current index
     *
     * @param buffer The buffer to fill
     * @param start  The index in the buffer to start filling at
     * @param length The number of bytes to read
     */
    public void readBytes(byte[] buffer, int start, int length) {
        if (!withinBounds(length))
            throw new IndexOutOfBoundsException("Cannot read the specified amount of data at this index");
        element.readBytes(index, buffer, start, length);
        index += length;
    }

    /**
     * Reads a single char at the current index
     *
     * @return The char
     */
    public char readChar() {
        if (!withinBounds(2))
            throw new IndexOutOfBoundsException("Cannot read the specified amount of data at this index");
        char value = element.readChar(index);
        index += 2;
        return value;
    }

    /**
     * Reads a single int at the current index
     *
     * @return The int
     */
    public int readInt() {
        if (!withinBounds(4))
            throw new IndexOutOfBoundsException("Cannot read the specified amount of data at this index");
        int value = element.readInt(index);
        index += 4;
        return value;
    }

    /**
     * Reads a single long at the current index
     *
     * @return The long
     */
    public long readLong() {
        if (!withinBounds(8))
            throw new IndexOutOfBoundsException("Cannot read the specified amount of data at this index");
        long value = element.readLong(index);
        index += 8;
        return value;
    }

    /**
     * Reads a single float at the current index
     *
     * @return The float
     */
    public float readFloat() {
        if (!withinBounds(4))
            throw new IndexOutOfBoundsException("Cannot read the specified amount of data at this index");
        float value = element.readFloat(index);
        index += 4;
        return value;
    }

    /**
     * Reads a single double at the current index
     *
     * @return The double
     */
    public double readDouble() {
        if (!withinBounds(8))
            throw new IndexOutOfBoundsException("Cannot read the specified amount of data at this index");
        double value = element.readDouble(index);
        index += 8;
        return value;
    }

    /**
     * Writes a single byte at the current index
     *
     * @param value The byte to write
     */
    public void writeByte(byte value) {
        if (!writable || !withinBounds(1))
            throw new IndexOutOfBoundsException("Cannot write the specified amount of data at this index");
        element.writeByte(index, value);
        index++;
    }

    /**
     * Writes bytes at the current index
     *
     * @param value The bytes to write
     */
    public void writeBytes(byte[] value) {
        if (!writable || !withinBounds(value.length))
            throw new IndexOutOfBoundsException("Cannot write the specified amount of data at this index");
        element.writeBytes(index, value);
        index += value.length;
    }

    /**
     * Writes bytes at the current index
     *
     * @param buffer The buffer with the bytes to write
     * @param start  The index in the buffer to start writing from
     * @param length The number of bytes to write
     */
    public void writeBytes(byte[] buffer, int start, int length) {
        if (!writable || !withinBounds(length))
            throw new IndexOutOfBoundsException("Cannot write the specified amount of data at this index");
        element.writeBytes(index, buffer, start, length);
        index += length;
    }

    /**
     * Writes a single char at the current index
     *
     * @param value The char to write
     */
    public void writeChar(char value) {
        if (!writable || !withinBounds(2))
            throw new IndexOutOfBoundsException("Cannot write the specified amount of data at this index");
        element.writeChar(index, value);
        index += 2;
    }

    /**
     * Writes a single int at the current index
     *
     * @param value The int to write
     */
    public void writeInt(int value) {
        if (!writable || !withinBounds(4))
            throw new IndexOutOfBoundsException("Cannot write the specified amount of data at this index");
        element.writeInt(index, value);
        index += 4;
    }

    /**
     * Writes a single long at the current index
     *
     * @param value The long to write
     */
    public void writeLong(long value) {
        if (!writable || !withinBounds(8))
            throw new IndexOutOfBoundsException("Cannot write the specified amount of data at this index");
        element.writeLong(index, value);
        index += 8;
    }

    /**
     * Writes a single float at the current index
     *
     * @param value The float to write
     */
    public void writeFloat(float value) {
        if (!writable || !withinBounds(4))
            throw new IndexOutOfBoundsException("Cannot write the specified amount of data at this index");
        element.writeFloat(index, value);
        index += 4;
    }

    /**
     * Writes a single double at the current index
     *
     * @param value The double to write
     */
    public void writeDouble(double value) {
        if (!writable || !withinBounds(8))
            throw new IndexOutOfBoundsException("Cannot write the specified amount of data at this index");
        element.writeDouble(index, value);
        index += 8;
    }

    @Override
    public void close() {
    }

    @Override
    public String toString() {
        return (writable ? "W" : "R") + "[0x" + Integer.toHexString(location) + ", 0x" + Integer.toHexString(location + length) + ")";
    }
}
