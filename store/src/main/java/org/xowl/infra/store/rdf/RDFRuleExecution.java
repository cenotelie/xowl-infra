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

import org.xowl.infra.store.IOUtils;
import org.xowl.infra.store.Serializable;
import org.xowl.infra.store.rete.Token;
import org.xowl.infra.utils.collections.Couple;
import org.xowl.infra.utils.logging.Logger;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents the data of a RDF rule execution
 *
 * @author Laurent Wouters
 */
public class RDFRuleExecution implements Serializable {
    /**
     * The original rule
     */
    public final RDFRule rule;
    /**
     * The tokens that triggered the rule
     * The tokens are to be in the same order as the pattern parts in the rule
     */
    public final Token[] tokens;
    /**
     * The mapping of special nodes in the consequents
     */
    public final Map<Node, Node> specials;

    /**
     * Initializes this data
     *
     * @param rule   The original rule
     * @param tokens The tokens that triggered the rule
     */
    public RDFRuleExecution(RDFRule rule, Token[] tokens) {
        this.rule = rule;
        this.tokens = tokens;
        this.specials = new HashMap<>();
    }

    /**
     * Gets the value bound to the specified variable in this token
     *
     * @param variable A variable
     * @return The value bound to the variable, or null if none is
     */
    public Node getBinding(VariableNode variable) {
        for (int i = 0; i != tokens.length; i++) {
            Node result = tokens[i].getBinding(variable);
            if (result != null)
                return result;
        }
        return null;
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
            for (int i = 0; i != tokens.length; i++) {
                if (tokens[i] != null) {
                    for (Couple<VariableNode, Node> binding : tokens[i].getBindings()) {
                        if (!first)
                            writer.append(", ");
                        first = false;
                        writer.append("\"");
                        writer.append(IOUtils.escapeStringJSON(binding.x.getName()));
                        writer.append("\":");
                        IOUtils.serializeJSON(writer, binding.y);
                    }
                }
            }
            writer.append("}}");
            return writer.toString();
        } catch (IOException exception) {
            Logger.DEFAULT.error(exception);
            return null;
        }
    }
}
