/**********************************************************************
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
 **********************************************************************/
package org.xowl.store.writers;

import org.xowl.store.rdf.*;
import org.xowl.utils.Logger;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;

/**
 * Represents a serializer of RDF data in the N-Triples format
 *
 * @author Laurent Wouters
 */
public class NTripleSerializer implements RDFSerializer {
    /**
     * Serializes the specified quads
     *
     * @param logger The logger to use
     * @param writer The writer to write to
     * @param quads  The quads to serialize
     */
    public void serialize(Logger logger, Writer writer, Iterator<Quad> quads) {
        try {
            while (quads.hasNext()) {
                serialize(writer, quads.next());
            }
        } catch (IOException | UnsupportedNodeType ex) {
            logger.error(ex);
        }
    }

    /**
     * Serialized the specified quad
     *
     * @param writer The writer to use
     * @param quad   The quad to serialize
     * @throws IOException         When an IO error occurs
     * @throws UnsupportedNodeType When a node is not supported
     */
    private void serialize(Writer writer, Quad quad) throws IOException, UnsupportedNodeType {
        serialize(writer, quad.getSubject());
        writer.write(" ");
        serialize(writer, quad.getProperty());
        writer.write(" ");
        serialize(writer, quad.getObject());
        writer.write(" .");
        writer.write(System.lineSeparator());
    }

    /**
     * Serialized the specified node
     *
     * @param writer The writer to use
     * @param node   The node to serialize
     * @throws IOException         When an IO error occurs
     * @throws UnsupportedNodeType When the specified node is not supported
     */
    private void serialize(Writer writer, Node node) throws IOException, UnsupportedNodeType {
        switch (node.getNodeType()) {
            case IRINode.TYPE: {
                writer.write("<");
                writer.write(((IRINode) node).getIRIValue());
                writer.write(">");
                break;
            }
            case BlankNode.TYPE: {
                writer.write("_:");
                writer.write(((BlankNode) node).getBlankID());
                break;
            }
            case LiteralNode.TYPE: {
                LiteralNode literalNode = (LiteralNode) node;
                writer.write("\"");
                writer.write(literalNode.getLexicalValue());
                writer.write("\"");
                String datatype = literalNode.getDatatype();
                String langTag = literalNode.getLangTag();
                if (langTag != null) {
                    writer.write("@");
                    writer.write(langTag);
                } else if (datatype != null) {
                    writer.write("^^<");
                    writer.write(datatype);
                    writer.write(">");
                }
                break;
            }
            default:
                throw new UnsupportedNodeType(node, "Unsupported node type. Supported types are IRI nodes, blank nodes and literal nodes");
        }
    }
}