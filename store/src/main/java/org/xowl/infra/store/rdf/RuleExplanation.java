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

package org.xowl.infra.store.rdf;

import org.xowl.infra.store.IOUtils;
import org.xowl.infra.store.Serializable;
import org.xowl.infra.utils.collections.Couple;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents an explanation of how a quad has been produced by the rule engine
 *
 * @author Laurent Wouters
 */
public class RuleExplanation implements Serializable {
    /**
     * Represents a node in the explanation
     */
    public static class ENode {
        /**
         * The unique identifier of this node
         */
        private final int id;
        /**
         * The quad represented by this node
         */
        public final Quad quad;
        /**
         * The antecedents of this quad, if any
         */
        public final List<Couple<String, List<ENode>>> antecedents;

        /**
         * Initializes this node
         *
         * @param id   The unique identifier of this node
         * @param quad The represented quad
         */
        private ENode(int id, Quad quad) {
            this.id = id;
            this.quad = quad;
            this.antecedents = new ArrayList<>();
        }

        /**
         * Serializes this explanation node in the JSON syntax
         *
         * @param writer The writer to write to
         */
        private void printJSON(Writer writer) throws IOException {
            writer.write("{ \"quadSubject\": ");
            IOUtils.serializeJSON(writer, quad.getSubject());
            writer.write(", \"quadProperty\": ");
            IOUtils.serializeJSON(writer, quad.getProperty());
            writer.write(", \"quadObject\": ");
            IOUtils.serializeJSON(writer, quad.getObject());
            writer.write(", \"quadGraph\": ");
            IOUtils.serializeJSON(writer, quad.getGraph());
            writer.write(", \"antecedents\": [");
            for (int i = 0; i != antecedents.size(); i++) {
                Couple<String, List<ENode>> antecedent = antecedents.get(i);
                if (i != 0)
                    writer.write(", ");
                writer.write("{ \"rule\": \"");
                writer.write(IOUtils.escapeStringJSON(antecedent.x));
                writer.write("\", \"targets\": [");
                for (int j = 0; j != antecedent.y.size(); j++) {
                    if (j != 0)
                        writer.write(", ");
                    writer.write(Integer.toString(antecedent.y.get(j).id));
                }
                writer.write("] }");
            }
            writer.write("] }");
        }
    }

    /**
     * The nodes in this explanation
     */
    private final List<ENode> nodes;

    /**
     * Initializes this explanation
     *
     * @param quad The root quad to explain
     */
    public RuleExplanation(Quad quad) {
        this.nodes = new ArrayList<>();
        this.nodes.add(new ENode(0, quad));
    }

    /**
     * Gets the root explanation node
     *
     * @return The root node
     */
    public ENode getRoot() {
        return nodes.get(0);
    }

    /**
     * Resolves the node that explain the specified quad
     *
     * @param quad The quad to explain
     * @return The representing node
     */
    public ENode resolve(Quad quad) {
        for (ENode node : nodes) {
            if (quad.equals(node.quad))
                return node;
        }
        ENode n = new ENode(nodes.size(), quad);
        nodes.add(n);
        return n;
    }

    @Override
    public String serializedString() {
        return serializedJSON();
    }

    @Override
    public String serializedJSON() {
        StringWriter writer = new StringWriter();
        writer.write("{ \"type\": \"");
        writer.write(IOUtils.escapeStringJSON(RuleExplanation.class.getCanonicalName()));
        writer.write("\", \"root\": 0, \"nodes\": [");
        for (int i = 0; i != nodes.size(); i++) {
            if (i != 0)
                writer.write(", ");
            try {
                nodes.get(i).printJSON(writer);
            } catch (IOException exception) {
                // cannot happen
            }
        }
        writer.write("] }");
        return writer.toString();
    }
}
