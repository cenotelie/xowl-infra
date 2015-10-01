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
import java.nio.charset.Charset;
import java.util.Arrays;

/**
 * Implements a persistable string into a file.
 * Instances of this class can be reused by changing the backing value.
 * This avoids the need to instantiate a new object each time a persistable string is required.
 *
 * @author Laurent Wouters
 */
class PersistableFastString implements Persistable {
    /**
     * The charset for persisting IRIS
     */
    public static final Charset CHARSET = Charset.forName("UTF-8");

    /**
     * The value to serialize
     */
    private byte[] value;

    /**
     * Sets the value of this string
     *
     * @param value The string value
     */
    public void set(String value) {
        this.value = value.getBytes(CHARSET);
    }

    @Override
    public int persistedLength() {
        return value.length + 4;
    }

    @Override
    public void persist(PersistedFile file) throws IOException {
        file.writeInt(value.length);
        file.writeBytes(value);
    }

    @Override
    public boolean isPersistedIn(PersistedFile file) throws IOException {
        return file.readInt() == value.length && Arrays.equals(value, file.readBytes(value.length));
    }
}
