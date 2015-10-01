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

import org.xowl.store.rdf.IRINode;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * Implementation of a persisted IRI node
 *
 * @author Laurent Wouters
 */
class PersistedIRINode extends IRINode {
    /**
     * The charset for persisting IRIS
     */
    public static final Charset CHARSET = Charset.forName("UTF-8");

    /**
     * The file that persists the node
     */
    private final PersistedFile file;
    /**
     * The location of the IRI value into the file
     */
    private final long location;
    /**
     * The cached IRI value, if any
     */
    private String value;

    /**
     * Initializes this node
     *
     * @param file     The file that persists the node
     * @param location The location of the IRI value into the file
     */
    public PersistedIRINode(PersistedFile file, long location) {
        this.file = file;
        this.location = location;
    }

    @Override
    public String getIRIValue() {
        if (value == null) {
            try {
                int length = file.seek(location).readInt();
                byte[] content = file.readBytes(length);
                value = new String(content, CHARSET);
            } catch (IOException exception) {
                value = "#error#";
            }
        }
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof PersistedIRINode) {
            PersistedIRINode node = (PersistedIRINode) o;
            if (node.file == this.file)
                return node.location == this.location;
            return getIRIValue().equals(node.getIRIValue());
        }
        return (o instanceof IRINode) && (getIRIValue().equals(((IRINode) o).getIRIValue()));
    }
}
