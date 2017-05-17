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

package org.xowl.infra.store.rdf;

import org.xowl.infra.store.storage.NodeManager;

import java.util.HashMap;
import java.util.Map;

/**
 * A composite variable resolver that enables different resolvers to be used for different variables
 *
 * @author Laurent Wouters
 */
public class VariableResolverComposite extends VariableResolver {
    /**
     * The resolvers composing this one
     */
    private final Map<VariableNode, VariableResolver> parts;
    /**
     * The fallback resolver
     */
    private final VariableResolver fallback;

    /**
     * Initializes this resolver
     */
    public VariableResolverComposite() {
        this(VariableResolveStandard.INSTANCE);
    }

    /**
     * Initializes this resolver
     *
     * @param fallback The fallback resolver
     */
    public VariableResolverComposite(VariableResolver fallback) {
        this.parts = new HashMap<>();
        this.fallback = fallback;
    }

    /**
     * Adds a partial resolver
     *
     * @param variable The variable
     * @param resolver The associated resolver
     */
    public void addResolver(VariableNode variable, VariableResolver resolver) {
        parts.put(variable, resolver);
    }

    @Override
    public Node resolve(VariableNode variable, RDFRuleExecution execution, NodeManager nodes, boolean isGraph) {
        VariableResolver resolver = parts.get(variable);
        if (resolver == null)
            resolver = fallback;
        return resolver.resolve(variable, execution, nodes, isGraph);
    }
}
