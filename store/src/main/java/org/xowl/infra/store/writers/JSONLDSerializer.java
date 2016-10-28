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
import org.xowl.infra.utils.TextUtils;
import org.xowl.infra.utils.collections.Couple;
import org.xowl.infra.utils.logging.Logger;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Represents a serializer of RDF data in the JSON-LD format
 *
 * @author Stephen Creff
 * @author Laurent Wouters
 */
public class JSONLDSerializer extends StructuredSerializer {
    /**
     * The writer to use
     */
    private final Writer writer;

    /**
     * Initializes this serializer
     *
     * @param writer The writer to use
     */
    public JSONLDSerializer(Writer writer) {
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
     * Serializes the namespaces data
     *
     * @throws IOException When an IO error occurs
     */
    protected void serializeNamespaces() throws IOException {
        writer.write("\"@context\": {");
        boolean first = true;
        for (Map.Entry<String, String> entry : namespaces.entrySet()) {
            if (!first)
                writer.write(", ");
            first = false;
            writer.write("\"");
            writer.write(TextUtils.escapeStringJSON(entry.getValue()));
            writer.write("\": \"");
            writer.write(TextUtils.escapeStringJSON(entry.getKey()));
            writer.write("\"");
        }
        writer.write("}");
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
        writer.write("{");
        serializeNamespaces();
        writer.write(", \"@id\": \"");
        if (graph.getNodeType() == Node.TYPE_IRI) {
            String iri = ((IRINode) graph).getIRIValue();
            String shortName = getShortName(iri);
            writer.write(TextUtils.escapeStringJSON(shortName != null ? shortName : iri));
        } else {
            int id = getBlankID((BlankNode) graph);
            writer.write("_:" + Integer.toString(id));
        }
        writer.write("\", \"@graph\": [");
        serializeGraphContent(content);
        writer.write("]}");
    }

    /**
     * Serializes the content of a graph
     *
     * @param content The content to serialize
     * @throws IOException         When an IO error occurs
     * @throws UnsupportedNodeType When the specified node is not supported
     */
    protected void serializeGraphContent(Map<SubjectNode, List<Couple<Property, Object>>> content) throws IOException, UnsupportedNodeType {
        boolean first = true;
        for (Map.Entry<SubjectNode, List<Couple<Property, Object>>> entry : content.entrySet()) {
            if (!first)
                writer.write(", ");
            first = false;
            writer.write("{");
            writer.write("\"@id\": \"");
            if (entry.getKey().getNodeType() == Node.TYPE_IRI) {
                String iri = ((IRINode) entry.getKey()).getIRIValue();
                String shortName = getShortName(iri);
                writer.write(TextUtils.escapeStringJSON(shortName != null ? shortName : iri));
            } else {
                int id = getBlankID((BlankNode) entry.getKey());
                writer.write("_:" + Integer.toString(id));
            }
            writer.write("\"");
            serializeProperties(entry.getValue());
            writer.write("}");
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
            writer.write(", ");
            bufferProperties.add(property);
            serializeProperty(property, properties.get(i).y);
            for (int j = i + 1; j != properties.size(); j++) {
                Couple<Property, Object> data = properties.get(j);
                if (RDFUtils.same(data.x, property)) {
                    writer.write(", ");
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
            writer.write("\"@type\": ");
        } else {
            writer.write("\"");
            String shortName = getShortName(iri);
            writer.write(TextUtils.escapeStringJSON(shortName != null ? shortName : iri));
            writer.write("\": ");
        }

        if (value instanceof List) {
            List<Node> list = (List<Node>) value;
            writer.write("{\"@list\": [");
            for (int i = 0; i != list.size(); i++) {
                if (i != 0)
                    writer.write(", ");
                serializeNode(list.get(i));
            }
            writer.write("]}");
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
                String iri = ((IRINode) node).getIRIValue();
                String shortName = getShortName(iri);
                writer.write("{\"@id\": \"");
                writer.write(TextUtils.escapeStringJSON(shortName != null ? shortName : iri));
                writer.write("\"}");
                break;
            }
            case Node.TYPE_BLANK: {
                writer.write("{\"@id\": \"_:");
                writer.write(Integer.toString(getBlankID((BlankNode) node)));
                writer.write("\"}");
                break;
            }
            case Node.TYPE_LITERAL: {
                LiteralNode literalNode = (LiteralNode) node;
                String lexical = literalNode.getLexicalValue();
                String datatype = literalNode.getDatatype();
                String langTag = literalNode.getLangTag();
                if (langTag != null) {
                    writer.write("{\"@value\": \"");
                    writer.write(TextUtils.escapeStringJSON(lexical));
                    writer.write("\", \"@language\": \"");
                    writer.write(TextUtils.escapeStringJSON(langTag));
                    writer.write("\"}");
                } else if (datatype != null && !Vocabulary.xsdString.equals(datatype)) {
                    String compact = getShortName(datatype);
                    writer.write("{\"@value\": \"");
                    writer.write(TextUtils.escapeStringJSON(lexical));
                    writer.write("\", \"@type\": \"");
                    writer.write(TextUtils.escapeStringJSON(compact != null ? compact : datatype));
                    writer.write("\"}");
                } else {
                    writer.write("\"");
                    writer.write(TextUtils.escapeStringJSON(lexical));
                    writer.write("\"");
                }
                break;
            }
            default:
                throw new UnsupportedNodeType(node, "Unsupported node type. Supported types are IRI nodes, blank nodes and literal nodes");
        }
    }
}
