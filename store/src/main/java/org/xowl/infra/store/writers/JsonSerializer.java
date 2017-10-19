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

import fr.cenotelie.commons.utils.collections.Couple;
import fr.cenotelie.commons.utils.logging.Logger;
import org.xowl.infra.store.RDFUtils;
import org.xowl.infra.store.rdf.*;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Implements a JSON serializer for xOWL data
 *
 * @author Laurent Wouters
 */
public class JsonSerializer extends StructuredSerializer {
    /**
     * The writer to use
     */
    private final Writer writer;

    /**
     * Initializes this serializer
     *
     * @param writer The writer to use
     */
    public JsonSerializer(Writer writer) {
        this.writer = writer;
    }

    /**
     * Serializes the specified quads
     *
     * @param logger The logger to use
     * @param quads  The quads to serialize
     */
    public void serialize(Logger logger, Iterator<Quad> quads) {
        while (quads.hasNext()) {
            enqueue(quads.next());
        }
        buildRdfLists();
        try {
            serialize();
        } catch (IOException exception) {
            logger.error(exception);
        }
    }

    /**
     * Serializes the data
     */
    private void serialize() throws IOException {
        writer.write("[");
        boolean first = true;
        for (Map.Entry<GraphNode, Map<SubjectNode, List<Couple<Property, Object>>>> entry : content.entrySet()) {
            if (!first)
                writer.write(", ");
            first = false;
            serializeGraph(entry.getKey(), entry.getValue());
        }
        writer.write("]");
    }

    /**
     * Serializes a graph
     *
     * @param graph   The graph
     * @param content The content to serialize
     * @throws IOException When an IO error occurs
     */
    private void serializeGraph(GraphNode graph, Map<SubjectNode, List<Couple<Property, Object>>> content) throws IOException {
        writer.write("{\"graph\": ");
        RDFUtils.serializeJSON(writer, graph);
        writer.write(", \"entities\": [");
        serializeGraphContent(content);
        writer.write("]}");
    }

    /**
     * Serializes the content of a graph
     *
     * @param content The content to serialize
     * @throws IOException When an IO error occurs
     */
    private void serializeGraphContent(Map<SubjectNode, List<Couple<Property, Object>>> content) throws IOException {
        boolean first = true;
        for (Map.Entry<SubjectNode, List<Couple<Property, Object>>> entry : content.entrySet()) {
            if (!first)
                writer.write(", ");
            first = false;
            writer.write("{\"subject\": ");
            RDFUtils.serializeJSON(writer, entry.getKey());
            writer.write(", \"properties\": [");
            serializeProperties(entry.getValue());
            writer.write("]}");
        }
    }

    /**
     * Serializes the properties of a node
     *
     * @param properties The RDF properties
     * @throws IOException When an IO error occurs
     */
    private void serializeProperties(List<Couple<Property, Object>> properties) throws IOException {
        for (int i = 0; i != properties.size(); i++) {
            Property property = properties.get(i).x;
            if (bufferProperties.contains(property))
                continue;
            if (i != 0)
                writer.write(", ");
            bufferProperties.add(property);
            writer.write("{\"property\": ");
            RDFUtils.serializeJSON(writer, property);
            writer.write(", \"values\": [");
            serializePropertyValue(properties.get(i).y);
            for (int j = i + 1; j != properties.size(); j++) {
                Couple<Property, Object> data = properties.get(j);
                if (RDFUtils.same(data.x, property)) {
                    writer.write(", ");
                    serializePropertyValue(data.y);
                }
            }
            writer.write("]}");
        }
        bufferProperties.clear();
    }

    /**
     * Serializes a property value from a node
     *
     * @param value The value for the property
     * @throws IOException When an IO error occurs
     */
    private void serializePropertyValue(Object value) throws IOException {
        if (value instanceof List) {
            List<Node> list = (List<Node>) value;
            writer.write("[");
            for (int i = 0; i != list.size(); i++) {
                if (i != 0)
                    writer.write(", ");
                RDFUtils.serializeJSON(writer, list.get(i));
            }
            writer.write("]");
        } else {
            RDFUtils.serializeJSON(writer, (Node) value);
        }
    }
}
