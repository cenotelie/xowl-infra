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
package org.xowl.infra.store.writers;

import org.xowl.infra.store.rdf.Quad;
import org.xowl.infra.store.storage.UnsupportedNodeType;
import org.xowl.infra.utils.IOUtils;

import java.io.IOException;
import java.io.Writer;

/**
 * Represents a serializer of RDF data in the N-Triples format
 *
 * @author Laurent Wouters
 */
public class NTripleSerializer extends NXSerializer {
    /**
     * Initializes this serializer
     *
     * @param writer The writer to use
     */
    public NTripleSerializer(Writer writer) {
        super(writer);
    }

    /**
     * Serialized the specified quad
     *
     * @param quad The quad to serialize
     * @throws IOException         When an IO error occurs
     * @throws UnsupportedNodeType When a node is not supported
     */
    protected void serialize(Quad quad) throws IOException, UnsupportedNodeType {
        serialize(quad.getSubject());
        writer.write(" ");
        serialize(quad.getProperty());
        writer.write(" ");
        serialize(quad.getObject());
        writer.write(" .");
        writer.write(IOUtils.LINE_SEPARATOR);
    }
}
