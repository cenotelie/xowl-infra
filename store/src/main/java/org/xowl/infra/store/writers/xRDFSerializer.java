/*******************************************************************************
 * Copyright (c) 2017 Association Cénotélie (cenotelie.fr)
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

import org.xowl.infra.store.rdf.DynamicNode;
import org.xowl.infra.store.rdf.Node;
import org.xowl.infra.store.rdf.VariableNode;
import org.xowl.infra.store.storage.UnsupportedNodeType;

import java.io.IOException;
import java.io.Writer;

/**
 * Implements a serializer for the xRDF syntax
 *
 * @author Laurent Wouters
 */
public class xRDFSerializer extends TriGSerializer {
    /**
     * Initializes this serializer
     *
     * @param writer The writer to use
     */
    public xRDFSerializer(Writer writer) {
        super(writer);
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
            case Node.TYPE_VARIABLE: {
                writer.write("?");
                writer.write(((VariableNode) node).getName());
                break;
            }
            case Node.TYPE_DYNAMIC: {
                writer.write("$ ");
                writer.write(((DynamicNode) node).getEvaluable().getSource());
                break;
            }
            default: {
                super.serializeNode(node);
                break;
            }
        }
    }
}
