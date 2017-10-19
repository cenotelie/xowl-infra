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

package org.xowl.infra.store.rdf;

import fr.cenotelie.commons.utils.Serializable;
import fr.cenotelie.commons.utils.TextUtils;
import fr.cenotelie.commons.utils.collections.Couple;
import fr.cenotelie.commons.utils.logging.Logging;
import org.xowl.infra.store.RDFUtils;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.*;

/**
 * Represents the data of a RDF rule execution
 *
 * @author Laurent Wouters
 */
public abstract class RDFRuleExecution implements Serializable {
    /**
     * The original rule
     */
    private final RDFRule rule;
    /**
     * The mapping of special nodes in the consequents
     */
    private final Map<Node, Node> specials;

    /**
     * Initializes this data
     *
     * @param rule The original rule
     */
    public RDFRuleExecution(RDFRule rule) {
        this.rule = rule;
        this.specials = new HashMap<>();
    }

    /**
     * Gets the original rule
     *
     * @return The original rule
     */
    public RDFRule getRule() {
        return rule;
    }

    /**
     * Gets the value bound to the specified variable in this token
     *
     * @param variable A variable
     * @return The value bound to the variable, or null if none is
     */
    public abstract Node getBinding(VariableNode variable);

    /**
     * Gets all the variable bindings
     *
     * @return The variable bindings
     */
    public abstract Iterator<Couple<VariableNode, Node>> getBindings();

    /**
     * Gets all the bindings for an evaluator
     *
     * @return The bindings for an evaluator
     */
    public Map<String, Object> getEvaluatorBindings() {
        Map<String, Object> bindings = new HashMap<>();
        Iterator<Couple<VariableNode, Node>> iterator = getBindings();
        while (iterator.hasNext()) {
            Couple<VariableNode, Node> entry = iterator.next();
            if (!bindings.containsKey(entry.x.getName()))
                bindings.put(entry.x.getName(), RDFUtils.getNative(entry.y));
        }
        for (Map.Entry<Node, Node> entry : specials.entrySet()) {
            if (entry.getKey().getNodeType() == Node.TYPE_VARIABLE) {
                bindings.put(((VariableNode) entry.getValue()).getName(), RDFUtils.getNative(entry.getValue()));
            }
        }
        return bindings;
    }

    /**
     * Gets the node associated to the specified special node
     *
     * @param node A special node
     * @return The associated node
     */
    public Node getSpecial(Node node) {
        return specials.get(node);
    }

    /**
     * Binds a special node
     *
     * @param special The special node
     * @param value   The associated value
     */
    public void bindSpecial(Node special, Node value) {
        specials.put(special, value);
    }

    @Override
    public String serializedString() {
        return serializedJSON();
    }

    @Override
    public String serializedJSON() {
        Writer writer = new StringWriter();
        try {
            writer.append("{\"bindings\": {");
            boolean first = true;
            Collection<String> names = new ArrayList<>();
            Iterator<Couple<VariableNode, Node>> iterator = getBindings();
            while (iterator.hasNext()) {
                Couple<VariableNode, Node> entry = iterator.next();
                if (!names.contains(entry.x.getName())) {
                    names.add(entry.x.getName());
                    names.add(entry.x.getName());
                    if (!first)
                        writer.append(", ");
                    first = false;
                    writer.append("\"");
                    writer.append(TextUtils.escapeStringJSON(entry.x.getName()));
                    writer.append("\":");
                    RDFUtils.serializeJSON(writer, entry.y);
                }
            }
            writer.append("}}");
            return writer.toString();
        } catch (IOException exception) {
            Logging.get().error(exception);
            return null;
        }
    }
}
