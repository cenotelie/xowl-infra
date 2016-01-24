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

package org.xowl.store.writers;

import org.xowl.store.RDFUtils;
import org.xowl.store.rdf.*;
import org.xowl.store.storage.UnsupportedNodeType;
import org.xowl.infra.utils.collections.Couple;
import org.xowl.infra.utils.logging.Logger;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Represents a serializer of RDF data in the RDF/XML format
 *
 * @author Laurent Wouters
 */
public class RDFXMLSerializer extends StructuredSerializer {
    /**
     * The serializer
     */
    private final XMLSerializer serializer;

    /**
     * Initializes this serializer
     *
     * @param writer The writer to use
     */
    public RDFXMLSerializer(Writer writer) {
        this.serializer = new XMLSerializer(writer, true);
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
    private void serialize() throws IOException, UnsupportedNodeType {
        serializer.onPreambule("utf-8");
        serializer.onElementOpenBegin("rdf:RDF");
        for (Map.Entry<String, String> entry : namespaces.entrySet()) {
            serializer.onAttribute("xmlns:" + entry.getValue(), entry.getKey());
        }
        serializer.onElementOpenEnd();
        for (Map.Entry<GraphNode, Map<SubjectNode, List<Couple<Property, Object>>>> entry : content.entrySet()) {
            serializeGraph(entry.getKey(), entry.getValue());
        }
        serializer.onElementClose("rdf:RDF");
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
            SubjectNode subject = entry.getKey();
            serializer.onElementOpenBegin("rdf:Description");
            if (subject.getNodeType() == Node.TYPE_IRI) {
                serializer.onAttribute("rdf:about", ((IRINode) subject).getIRIValue());
            } else {
                serializer.onAttribute("rdf:nodeID", "n" + getBlankID((BlankNode) subject));
            }
            serializeProperties(entry.getValue());
            serializer.onElementClose("rdf:Description");
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
            bufferProperties.add(property);
            serializeProperty(property, properties.get(i).y);
            for (int j = i + 1; j != properties.size(); j++) {
                Couple<Property, Object> data = properties.get(j);
                if (RDFUtils.same(data.x, property)) {
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
        String propertyName = getShortName(((IRINode) property).getIRIValue());
        serializer.onElementOpenBegin(propertyName);
        if (value instanceof List) {
            serializer.onAttribute("rdf:parseType", "Collection");
            serializer.onElementOpenEnd();
            List<Node> list = (List<Node>) value;
            for (Node element : list)
                serializeNode(element);
            serializer.onElementClose(propertyName);
        } else {
            switch (((Node) value).getNodeType()) {
                case Node.TYPE_IRI:
                    serializer.onAttribute("rdf:about", ((IRINode) value).getIRIValue());
                    serializer.onElementOpenEndAndClose();
                    break;
                case Node.TYPE_BLANK:
                    serializer.onAttribute("rdf:nodeID", "n" + getBlankID((BlankNode) value));
                    serializer.onElementOpenEndAndClose();
                    break;
                case Node.TYPE_LITERAL:
                    String lexicalValue = ((LiteralNode) value).getLexicalValue();
                    String datatype = ((LiteralNode) value).getDatatype();
                    String language = ((LiteralNode) value).getLangTag();
                    if (language != null) {
                        serializer.onAttribute("xml:lang", language);
                    } else if (datatype != null) {
                        serializer.onAttribute("rdf:datatype", datatype);
                    }
                    serializer.onElementOpenEnd();
                    serializer.onContent(lexicalValue);
                    serializer.onElementClose(propertyName);
                    break;
                default:
                    throw new UnsupportedNodeType((Node) value, "RDF serialization only support IRI, Blank and Literal nodes");
            }
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
            case Node.TYPE_IRI:
                serializer.onElementOpenBegin("rdf:Description");
                serializer.onAttribute("rdf:about", ((IRINode) node).getIRIValue());
                serializer.onElementOpenEndAndClose();
                break;
            case Node.TYPE_BLANK:
                serializer.onElementOpenBegin("rdf:Description");
                serializer.onAttribute("rdf:nodeID", "n" + getBlankID((BlankNode) node));
                serializer.onElementOpenEndAndClose();
                break;
            default:
                throw new UnsupportedNodeType(node, "RDF/XML serialization only support IRI and Blank nodes as list members");
        }
    }
}
