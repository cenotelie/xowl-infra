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

import org.xowl.store.rdf.*;
import org.xowl.store.storage.UnsupportedNodeType;
import org.xowl.utils.logging.Logger;
import org.xowl.utils.collections.Couple;

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
        try {
            while (quads.hasNext()) {
                enqueue(quads.next());
            }
        } catch (UnsupportedNodeType exception) {
            logger.error(exception);
        }
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
        for (Map.Entry<SubjectNode, List<Quad>> entry : data.entrySet()) {
            serializeTopLevel(entry.getKey(), entry.getValue());
        }
        serializer.onElementClose("rdf:RDF");
    }

    /**
     * Serializes a top-level entity
     *
     * @param subject The subject
     * @param quads   All the quads for its property
     */
    private void serializeTopLevel(SubjectNode subject, List<Quad> quads) throws IOException, UnsupportedNodeType {
        serializer.onElementOpenBegin("rdf:Description");
        if (subject.getNodeType() == Node.TYPE_IRI) {
            serializer.onAttribute("rdf:about", ((IRINode) subject).getIRIValue());
        } else {
            serializer.onAttribute("rdf:nodeID", "n" + getBlankID((BlankNode) subject));
        }
        for (int i = 0; i != quads.size(); i++) {
            Property property = quads.get(i).getProperty();
            if (bufferProperties.contains(property))
                continue;
            bufferProperties.add(property);
            for (int j = i; j != quads.size(); j++) {
                Quad quad = quads.get(j);
                if (quad.getProperty() == property)
                    serializeProperty(quad);
            }
        }
        bufferProperties.clear();
        serializer.onElementClose("rdf:Description");
    }

    /**
     * Serializes a property from a node
     *
     * @param quad The quad representing the property
     */
    private void serializeProperty(Quad quad) throws IOException, UnsupportedNodeType {
        Couple<String, String> property = getCompactIRI(quad.getProperty(), ((IRINode) quad.getProperty()).getIRIValue());
        serializer.onElementOpenBegin(property.x + ":" + property.y);
        switch (quad.getObject().getNodeType()) {
            case Node.TYPE_IRI:
                serializer.onAttribute("rdf:about", ((IRINode) quad.getObject()).getIRIValue());
                serializer.onElementOpenEndAndClose();
                break;
            case Node.TYPE_BLANK:
                serializer.onAttribute("rdf:nodeID", "n" + getBlankID((BlankNode) quad.getObject()));
                serializer.onElementOpenEndAndClose();
                break;
            case Node.TYPE_LITERAL:
                String lexicalValue = ((LiteralNode) quad.getObject()).getLexicalValue();
                String datatype = ((LiteralNode) quad.getObject()).getDatatype();
                String language = ((LiteralNode) quad.getObject()).getLangTag();
                if (language != null) {
                    serializer.onAttribute("xml:lang", language);
                } else if (datatype != null) {
                    serializer.onAttribute("rdf:datatype", datatype);
                }
                serializer.onElementOpenEnd();
                serializer.onContent(lexicalValue);
                serializer.onElementClose(property.x + ":" + property.y);
                break;
            default:
                throw new UnsupportedNodeType(quad.getObject(), "RDF serialization only support IRI, Blank and Literal nodes");
        }
    }
}
