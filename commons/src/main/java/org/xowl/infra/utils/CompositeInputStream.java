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

package org.xowl.infra.utils;

import java.io.IOException;
import java.io.InputStream;


/**
 * Represents a input stream composed of a header and another input stream
 *
 * @author Laurent Wouters
 */
class CompositeInputStream extends InputStream {
    /**
     * The header before the encapsulated input stream
     */
    private final int[] buffer;
    /**
     * The encapsulated input stream
     */
    private final InputStream stream;
    /**
     * The current index in the buffer
     */
    private int index;

    /**
     * Initializes this composite stream
     *
     * @param buffer The header before the encapsulated input stream
     * @param stream The encapsulated input stream
     */
    public CompositeInputStream(int[] buffer, InputStream stream) {
        this.buffer = buffer;
        this.stream = stream;
    }

    /**
     * Read a single byte from this stream
     *
     * @return The byte's value
     * @throws IOException
     */
    @Override
    public int read() throws IOException {
        if (index >= buffer.length) return stream.read();
        int value = buffer[index];
        index++;
        if (value != 0) return value;
        return stream.read();
    }
}
