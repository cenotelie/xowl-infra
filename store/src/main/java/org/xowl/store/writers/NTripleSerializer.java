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

import org.xowl.store.IOUtils;
import org.xowl.store.rdf.*;
import org.xowl.store.storage.UnsupportedNodeType;
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
     * The writer to use
     */
    private final Writer writer;

    /**
     * Initializes this serializer
     *
     * @param writer The writer to use
     */
    public NTripleSerializer(Writer writer) {
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
                serialize(quads.next());
            }
        } catch (IOException | UnsupportedNodeType ex) {
            logger.error(ex);
        }
    }

    /**
     * Serialized the specified quad
     *
     * @param quad The quad to serialize
     * @throws IOException         When an IO error occurs
     * @throws UnsupportedNodeType When a node is not supported
     */
    private void serialize(Quad quad) throws IOException, UnsupportedNodeType {
        serialize(quad.getSubject());
        writer.write(" ");
        serialize(quad.getProperty());
        writer.write(" ");
        serialize(quad.getObject());
        writer.write(" .");
        writer.write(System.lineSeparator());
    }

    /**
     * Serialized the specified node
     *
     * @param node The node to serialize
     * @throws IOException         When an IO error occurs
     * @throws UnsupportedNodeType When the specified node is not supported
     */
    private void serialize(Node node) throws IOException, UnsupportedNodeType {
        switch (node.getNodeType()) {
            case Node.TYPE_IRI: {
                writer.write("<");
                writer.write(IOUtils.escapeURI(((IRINode) node).getIRIValue()));
                writer.write(">");
                break;
            }
            case Node.TYPE_BLANK: {
                writer.write("_:");
                writer.write(Long.toString(((BlankNode) node).getBlankID()));
                break;
            }
            case Node.TYPE_LITERAL: {
                LiteralNode literalNode = (LiteralNode) node;
                writer.write("\"");
                writer.write(IOUtils.escapeStringW3C(literalNode.getLexicalValue()));
                writer.write("\"");
                String datatype = literalNode.getDatatype();
                String langTag = literalNode.getLangTag();
                if (langTag != null) {
                    writer.write("@");
                    writer.write(langTag);
                } else if (datatype != null) {
                    writer.write("^^<");
                    writer.write(IOUtils.escapeURI(datatype));
                    writer.write(">");
                }
                break;
            }
            default:
                throw new UnsupportedNodeType(node, "Unsupported node type. Supported types are IRI nodes, blank nodes and literal nodes");
        }
    }
}
