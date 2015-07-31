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

import org.xowl.store.Vocabulary;
import org.xowl.store.rdf.*;
import org.xowl.utils.Logger;
import org.xowl.utils.collections.Couple;

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
    private final Writer writer;

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
        for (Map.Entry<String, String> entry : namespaces.entrySet()) {
            writer.write("@prefix ");
            writer.write(entry.getValue());
            writer.write(": <");
            writer.write(entry.getKey());
            writer.write("> .");
            writer.write(System.lineSeparator());
        }
        for (Map.Entry<SubjectNode, List<Quad>> entry : data.entrySet()) {
            writer.write(System.lineSeparator());
            serializeTopLevel(entry.getKey(), entry.getValue());
        }
    }

    /**
     * Serializes a top-level entity
     *
     * @param subject The subject
     * @param quads   All the quads for its property
     */
    private void serializeTopLevel(SubjectNode subject, List<Quad> quads) throws IOException, UnsupportedNodeType {
        if (subject.getNodeType() == IRINode.TYPE) {
            writer.write("<");
            writer.write(((IRINode) subject).getIRIValue());
            writer.write(">");
        } else {
            writer.write("_:n");
            writer.write(getBlankID((BlankNode) subject));
        }
        writer.write(System.lineSeparator());

        boolean first = true;
        for (int i = 0; i != quads.size(); i++) {
            Property property = quads.get(i).getProperty();
            if (bufferProperties.contains(property))
                continue;
            bufferProperties.add(property);
            for (int j = i; j != quads.size(); j++) {
                Quad quad = quads.get(j);
                if (quad.getProperty() == property) {
                    if (!first) {
                        writer.write(" ;");
                        writer.write(System.lineSeparator());
                    }
                    writer.write("    ");
                    first = false;
                    serializeProperty(quad);
                }
            }
        }
        bufferProperties.clear();
        writer.write(" .");
        writer.write(System.lineSeparator());
    }

    /**
     * Serializes a property from a node
     *
     * @param quad The quad representing the property
     */
    private void serializeProperty(Quad quad) throws IOException, UnsupportedNodeType {
        String property = ((IRINode) quad.getProperty()).getIRIValue();
        if (Vocabulary.rdfType.equals(property))
            writer.write("a");
        else {
            Couple<String, String> compact = getCompactIRI(quad.getProperty(), property);
            writer.write(compact.x + ":" + compact.y);
        }
        writer.write(" ");

        switch (quad.getObject().getNodeType()) {
            case IRINode.TYPE:
                writer.write("<");
                writer.write(((IRINode) quad.getObject()).getIRIValue());
                writer.write(">");
                break;
            case BlankNode.TYPE:
                writer.write("_:n");
                writer.write(getBlankID((BlankNode) quad.getObject()));
                break;
            case LiteralNode.TYPE:
                String lexicalValue = ((LiteralNode) quad.getObject()).getLexicalValue();
                String datatype = ((LiteralNode) quad.getObject()).getDatatype();
                String language = ((LiteralNode) quad.getObject()).getLangTag();
                writer.write("\"");
                writer.write(lexicalValue);
                writer.write("\"");
                if (language != null) {
                    writer.write("@");
                    writer.write(language);
                } else if (datatype != null) {
                    writer.write("^^");
                    Couple<String, String> compact = getCompactIRI(quad.getObject(), datatype);
                    writer.write(compact.x + ":" + compact.y);
                }
                break;
            default:
                throw new UnsupportedNodeType(quad.getObject(), "RDF serialization only support IRI, Blank and Literal nodes");
        }
    }
}
