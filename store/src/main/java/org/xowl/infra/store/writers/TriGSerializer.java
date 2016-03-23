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

package org.xowl.infra.store.writers;

import org.xowl.infra.store.rdf.GraphNode;
import org.xowl.infra.store.rdf.Property;
import org.xowl.infra.store.rdf.SubjectNode;
import org.xowl.infra.store.storage.UnsupportedNodeType;
import org.xowl.infra.utils.collections.Couple;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Map;

/**
 * Represents a serializer of RDF data in the TriG format
 *
 * @author Laurent Wouters
 */
public class TriGSerializer extends TurtleSerializer {
    /**
     * Initializes this serializer
     *
     * @param writer The writer to use
     */
    public TriGSerializer(Writer writer) {
        super(writer);
    }

    /**
     * Serializes a graph
     *
     * @param graph   The graph
     * @param content The content to serialize
     * @throws IOException         When an IO error occurs
     * @throws UnsupportedNodeType When the specified node is not supported
     */
    protected void serializeGraph(GraphNode graph, Map<SubjectNode, List<Couple<Property, Object>>> content) throws IOException, UnsupportedNodeType {
        writer.write("GRAPH ");
        serializeNode(graph);
        writer.write(" {");
        serializeGraphContent(content);
        writer.write("}");
    }
}
