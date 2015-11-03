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

/**
 * An entity that acts as a proxy to a backing element that can be read from and written to.
 * This entity can enforce boundaries on the underlying element.
 *
 * @author Laurent Wouters
 */
abstract class IOProxy implements IOElement {
    /**
     * The backing IO element
     */
    protected IOElement backend;
    /**
     * The location in the backend of the proxy
     */
    protected long location;
    /**
     * The length of the proxy in the backend
     */
    protected long length;
    /**
     * Whether the proxy allows writing
     */
    protected boolean writable;

    /**
     * Gets whether the specified number of bytes at the current index are within the proxy bounds
     *
     * @param length A number of bytes
     * @return true if the bytes are within the bounds
     */
    private boolean withinBounds(int length) {
        return (backend.getIndex() >= location && backend.getIndex() + length <= location + this.length);
    }

    @Override
    public long getIndex() {
        return backend.getIndex() - location;
    }

    @Override
    public long getSize() {
        return length;
    }

    @Override
    public IOElement seek(long index) {
        return backend.seek(location + index);
    }

    @Override
    public IOElement reset() {
        return backend.seek(location);
    }

    @Override
    public boolean canRead(int length) {
        return (withinBounds(length) && backend.canRead(length));
    }

    @Override
    public byte readByte() throws StorageException {
        if (!canRead(1))
            throw new IndexOutOfBoundsException("Cannot read the specified amount of data at this index");
        return backend.readByte();
    }

    @Override
    public byte[] readBytes(int length) throws StorageException {
        if (!canRead(length))
            throw new IndexOutOfBoundsException("Cannot read the specified amount of data at this index");
        return backend.readBytes(length);
    }

    @Override
    public void readBytes(byte[] buffer, int index, int length) throws StorageException {
        if (!canRead(length))
            throw new IndexOutOfBoundsException("Cannot read the specified amount of data at this index");
        backend.readBytes(buffer, index, length);
    }

    @Override
    public char readChar() throws StorageException {
        if (!canRead(2))
            throw new IndexOutOfBoundsException("Cannot read the specified amount of data at this index");
        return backend.readChar();
    }

    @Override
    public int readInt() throws StorageException {
        if (!canRead(4))
            throw new IndexOutOfBoundsException("Cannot read the specified amount of data at this index");
        return backend.readInt();
    }

    @Override
    public long readLong() throws StorageException {
        if (!canRead(8))
            throw new IndexOutOfBoundsException("Cannot read the specified amount of data at this index");
        return backend.readLong();
    }

    @Override
    public float readFloat() throws StorageException {
        if (!canRead(4))
            throw new IndexOutOfBoundsException("Cannot read the specified amount of data at this index");
        return backend.readFloat();
    }

    @Override
    public double readDouble() throws StorageException {
        if (!canRead(8))
            throw new IndexOutOfBoundsException("Cannot read the specified amount of data at this index");
        return backend.readDouble();
    }

    @Override
    public void writeByte(byte value) throws StorageException {
        if (!writable || !withinBounds(1))
            throw new IndexOutOfBoundsException("Cannot write the specified amount of data at this index");
        backend.writeByte(value);
    }

    @Override
    public void writeBytes(byte[] value) throws StorageException {
        if (!writable || !withinBounds(value.length))
            throw new IndexOutOfBoundsException("Cannot write the specified amount of data at this index");
        backend.writeBytes(value);
    }

    @Override
    public void writeBytes(byte[] buffer, int index, int length) throws StorageException {
        if (!writable || !withinBounds(length))
            throw new IndexOutOfBoundsException("Cannot write the specified amount of data at this index");
        backend.writeBytes(buffer, index, length);
    }

    @Override
    public void writeChar(char value) throws StorageException {
        if (!writable || !withinBounds(2))
            throw new IndexOutOfBoundsException("Cannot write the specified amount of data at this index");
        backend.writeChar(value);
    }

    @Override
    public void writeInt(int value) throws StorageException {
        if (!writable || !withinBounds(4))
            throw new IndexOutOfBoundsException("Cannot write the specified amount of data at this index");
        backend.writeInt(value);
    }

    @Override
    public void writeLong(long value) throws StorageException {
        if (!writable || !withinBounds(8))
            throw new IndexOutOfBoundsException("Cannot write the specified amount of data at this index");
        backend.writeLong(value);
    }

    @Override
    public void writeFloat(float value) throws StorageException {
        if (!writable || !withinBounds(4))
            throw new IndexOutOfBoundsException("Cannot write the specified amount of data at this index");
        backend.writeFloat(value);
    }

    @Override
    public void writeDouble(double value) throws StorageException {
        if (!writable || !withinBounds(8))
            throw new IndexOutOfBoundsException("Cannot write the specified amount of data at this index");
        backend.writeDouble(value);
    }
}
