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

import org.xowl.infra.store.IOUtils;
import org.xowl.infra.store.rdf.*;
import org.xowl.infra.store.storage.UnsupportedNodeType;
import org.xowl.infra.utils.logging.Logger;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.Iterator;

/**
 * Represents a serializer of basic triples and quads
 *
 * @author Laurent Wouters
 */
public abstract class NXSerializer implements RDFSerializer {
    /**
     * Initial size of the buffer for the blank node map
     */
    private static final int BLANKS_MAP_INIT_SIZE = 256;

    /**
     * The writer to use
     */
    protected final Writer writer;
    /**
     * Buffer for renaming blank nodes
     */
    private long[] blanks;
    /**
     * Index of the next blank node slot
     */
    private int nextBlank;

    /**
     * Initializes this serializer
     *
     * @param writer The writer to use
     */
    public NXSerializer(Writer writer) {
        this.writer = writer;
        this.blanks = new long[BLANKS_MAP_INIT_SIZE];
        this.nextBlank = 0;
    }

    /**
     * Serializes the specified quads
     *
     * @param logger The logger to use
     * @param quads  The quads to serialize
     */
    public final void serialize(Logger logger, Iterator<Quad> quads) {
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
    protected abstract void serialize(Quad quad) throws IOException, UnsupportedNodeType;

    /**
     * Serialized the specified node
     *
     * @param node The node to serialize
     * @throws IOException         When an IO error occurs
     * @throws UnsupportedNodeType When the specified node is not supported
     */
    protected final void serialize(Node node) throws IOException, UnsupportedNodeType {
        switch (node.getNodeType()) {
            case Node.TYPE_IRI: {
                writer.write("<");
                writer.write(IOUtils.escapeAbsoluteURIW3C(((IRINode) node).getIRIValue()));
                writer.write(">");
                break;
            }
            case Node.TYPE_BLANK: {
                writer.write("_:");
                writer.write(Integer.toString(rename((BlankNode) node)));
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
                    writer.write(IOUtils.escapeAbsoluteURIW3C(datatype));
                    writer.write(">");
                }
                break;
            }
            default:
                throw new UnsupportedNodeType(node, "Unsupported node type. Supported types are IRI nodes, blank nodes and literal nodes");
        }
    }

    /**
     * Renames the specified blank node
     *
     * @param node A blank node
     * @return The renamed identifier for the node
     */
    private int rename(BlankNode node) {
        long id = node.getBlankID();
        for (int i = 0; i != nextBlank; i++) {
            if (blanks[i] == id)
                return i;
        }
        if (nextBlank == blanks.length)
            blanks = Arrays.copyOf(blanks, blanks.length + BLANKS_MAP_INIT_SIZE);
        blanks[nextBlank] = id;
        int result = nextBlank;
        nextBlank++;
        return result;
    }
}
