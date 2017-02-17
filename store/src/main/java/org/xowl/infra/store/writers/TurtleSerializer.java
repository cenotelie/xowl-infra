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

import org.xowl.infra.store.RDFUtils;
import org.xowl.infra.store.Vocabulary;
import org.xowl.infra.store.rdf.*;
import org.xowl.infra.store.storage.UnsupportedNodeType;
import org.xowl.infra.utils.IOUtils;
import org.xowl.infra.utils.TextUtils;
import org.xowl.infra.utils.collections.Couple;
import org.xowl.infra.utils.logging.Logger;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Represents a serializer of RDF data in the Turtle format
 *
 * @author Laurent Wouters
 */
public class TurtleSerializer extends StructuredSerializer {
    /**
     * The writer to use
     */
    protected final Writer writer;

    /**
     * Initializes this serializer
     *
     * @param writer The writer to use
     */
    public TurtleSerializer(Writer writer) {
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
        } catch (IOException | UnsupportedNodeType exception) {
            logger.error(exception);
        }
    }

    /**
     * Serializes the data
     */
    protected void serialize() throws IOException, UnsupportedNodeType {
        serializeNamespaces();
        writer.write(IOUtils.LINE_SEPARATOR);

        for (Map.Entry<GraphNode, Map<SubjectNode, List<Couple<Property, Object>>>> entry : content.entrySet()) {
            serializeGraph(entry.getKey(), entry.getValue());
        }
    }

    /**
     * Serializes the namespaces data
     *
     * @throws IOException When an IO error occurs
     */
    protected void serializeNamespaces() throws IOException {
        for (Map.Entry<String, String> entry : namespaces.entrySet()) {
            writer.write("@prefix ");
            writer.write(entry.getValue());
            writer.write(": <");
            writer.write(TextUtils.escapeAbsoluteURIW3C(entry.getKey()));
            writer.write("> .");
            writer.write(IOUtils.LINE_SEPARATOR);
        }
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
        serializeGraphContent(content);
    }

    /**
     * Serializes the content of a graph
     *
     * @param content The content to serialize
     * @throws IOException         When an IO error occurs
     * @throws UnsupportedNodeType When the specified node is not supported
     */
    protected void serializeGraphContent(Map<SubjectNode, List<Couple<Property, Object>>> content) throws IOException, UnsupportedNodeType {
        for (Map.Entry<SubjectNode, List<Couple<Property, Object>>> entry : content.entrySet()) {
            serializeNode(entry.getKey());
            writer.write(" ");
            serializeProperties(entry.getValue());
            writer.write(" .");
            writer.write(IOUtils.LINE_SEPARATOR);
        }
    }

    /**
     * Serializes the properties of a node
     *
     * @param properties The RDF properties
     * @throws IOException         When an IO error occurs
     * @throws UnsupportedNodeType When the specified node is not supported
     */
    protected void serializeProperties(List<Couple<Property, Object>> properties) throws IOException, UnsupportedNodeType {
        for (int i = 0; i != properties.size(); i++) {
            Property property = properties.get(i).x;
            if (bufferProperties.contains(property))
                continue;
            if (i != 0)
                writer.write(" ; ");
            bufferProperties.add(property);
            serializeProperty(property, properties.get(i).y);
            for (int j = i + 1; j != properties.size(); j++) {
                Couple<Property, Object> data = properties.get(j);
                if (RDFUtils.same(data.x, property)) {
                    writer.write(" , ");
                    serializeProperty(property, data.y);
                }
            }
        }
        bufferProperties.clear();
    }

    /**
     * Serializes a property from a node
     *
     * @param property The RDF property
     * @param value    The value for the property
     * @throws IOException         When an IO error occurs
     * @throws UnsupportedNodeType When the specified node is not supported
     */
    protected void serializeProperty(Property property, Object value) throws IOException, UnsupportedNodeType {
        String iri = ((IRINode) property).getIRIValue();
        if (Vocabulary.rdfType.equals(iri)) {
            writer.write("a");
        } else {
            String compact = getShortName(iri);
            if (compact == null) {
                writer.write("<");
                writer.write(TextUtils.escapeAbsoluteURIW3C(iri));
                writer.write(">");
            } else {
                writer.write(compact);
            }
        }
        writer.write(" ");

        if (value instanceof List) {
            List<Node> list = (List<Node>) value;
            writer.write("(");
            for (int i = 0; i != list.size(); i++) {
                if (i != 0)
                    writer.write(" ");
                serializeNode(list.get(i));
            }
            writer.write(")");
        } else {
            serializeNode((Node) value);
        }
    }

    /**
     * Serialized the specified node
     *
     * @param node The node to serialize
     * @throws IOException         When an IO error occurs
     * @throws UnsupportedNodeType When the specified node is not supported
     */
    protected void serializeNode(Node node) throws IOException, UnsupportedNodeType {
        switch (node.getNodeType()) {
            case Node.TYPE_IRI: {
                String compact = getShortName(((IRINode) node).getIRIValue());
                if (compact == null) {
                    writer.write("<");
                    writer.write(TextUtils.escapeAbsoluteURIW3C(((IRINode) node).getIRIValue()));
                    writer.write(">");
                } else {
                    writer.write(compact);
                }
                break;
            }
            case Node.TYPE_BLANK: {
                writer.write("_:");
                writer.write(Integer.toString(getBlankID((BlankNode) node)));
                break;
            }
            case Node.TYPE_LITERAL: {
                LiteralNode literalNode = (LiteralNode) node;
                writer.write("\"");
                writer.write(TextUtils.escapeStringW3C(literalNode.getLexicalValue()));
                writer.write("\"");
                String datatype = literalNode.getDatatype();
                String langTag = literalNode.getLangTag();
                if (langTag != null) {
                    writer.write("@");
                    writer.write(langTag);
                } else if (datatype != null) {
                    writer.write("^^");
                    String compact = getShortName(datatype);
                    if (compact == null) {
                        writer.write("<");
                        writer.write(TextUtils.escapeAbsoluteURIW3C(datatype));
                        writer.write(">");
                    } else {
                        writer.write(compact);
                    }
                }
                break;
            }
            default:
                throw new UnsupportedNodeType(node, "Unsupported node type. Supported types are IRI nodes, blank nodes and literal nodes");
        }
    }
}
