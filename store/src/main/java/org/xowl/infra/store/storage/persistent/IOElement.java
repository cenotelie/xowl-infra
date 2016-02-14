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

package org.xowl.infra.store.storage.persistent;

import java.io.Closeable;

/**
 * API for elements that can be read form and written to
 *
 * @author Laurent Wouters
 */
interface IOElement extends Closeable {
    /**
     * Gets the current index in this element
     *
     * @return The current index in this element
     */
    long getIndex();

    /**
     * Gets the size of this element
     *
     * @return The size of this element, or -1 if the operation failed
     */
    long getSize();

    /**
     * Positions the index of this element
     *
     * @param index The new index
     * @return The element
     */
    IOElement seek(long index);

    /**
     * Resets the index in this element to its initial position
     *
     * @return The element
     */
    IOElement reset();

    /**
     * Gets whether the specified amount of bytes can be read at the current index
     *
     * @param length The number of bytes to read
     * @return true if this is legal to read
     */
    boolean canRead(int length);

    /**
     * Reads a single byte at the current index
     *
     * @return The byte
     * @throws StorageException When an IO operation failed
     */
    byte readByte() throws StorageException;

    /**
     * Reads a specified number of bytes a the current index
     *
     * @param length The number of bytes to read
     * @return The bytes
     * @throws StorageException When an IO operation failed
     */
    byte[] readBytes(int length) throws StorageException;

    /**
     * Reads a specified number of bytes a the current index
     *
     * @param buffer The buffer to fill
     * @param index  The index in the buffer to start filling at
     * @param length The number of bytes to read
     * @throws StorageException When an IO operation failed
     */
    void readBytes(byte[] buffer, int index, int length) throws StorageException;

    /**
     * Reads a single char at the current index
     *
     * @return The char
     * @throws StorageException When an IO operation failed
     */
    char readChar() throws StorageException;

    /**
     * Reads a single int at the current index
     *
     * @return The int
     * @throws StorageException When an IO operation failed
     */
    int readInt() throws StorageException;

    /**
     * Reads a single long at the current index
     *
     * @return The long
     * @throws StorageException When an IO operation failed
     */
    long readLong() throws StorageException;

    /**
     * Reads a single float at the current index
     *
     * @return The float
     * @throws StorageException When an IO operation failed
     */
    float readFloat() throws StorageException;

    /**
     * Reads a single double at the current index
     *
     * @return The double
     * @throws StorageException When an IO operation failed
     */
    double readDouble() throws StorageException;

    /**
     * Writes a single byte at the current index
     *
     * @param value The byte to write
     * @throws StorageException When an IO operation failed
     */
    void writeByte(byte value) throws StorageException;

    /**
     * Writes bytes at the current index
     *
     * @param value The bytes to write
     * @throws StorageException When an IO operation failed
     */
    void writeBytes(byte[] value) throws StorageException;

    /**
     * Writes bytes at the current index
     *
     * @param buffer The buffer with the bytes to write
     * @param index  The index in the buffer to start writing from
     * @param length The number of bytes to write
     * @throws StorageException When an IO operation failed
     */
    void writeBytes(byte[] buffer, int index, int length) throws StorageException;

    /**
     * Writes a single char at the current index
     *
     * @param value The char to write
     * @throws StorageException When an IO operation failed
     */
    void writeChar(char value) throws StorageException;

    /**
     * Writes a single int at the current index
     *
     * @param value The int to write
     * @throws StorageException When an IO operation failed
     */
    void writeInt(int value) throws StorageException;

    /**
     * Writes a single long at the current index
     *
     * @param value The long to write
     * @throws StorageException When an IO operation failed
     */
    void writeLong(long value) throws StorageException;

    /**
     * Writes a single float at the current index
     *
     * @param value The float to write
     * @throws StorageException When an IO operation failed
     */
    void writeFloat(float value) throws StorageException;

    /**
     * Writes a single double at the current index
     *
     * @param value The double to write
     * @throws StorageException When an IO operation failed
     */
    void writeDouble(double value) throws StorageException;
}