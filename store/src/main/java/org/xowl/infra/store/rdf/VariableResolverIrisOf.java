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

/**
 * A variable resolver that resolves variable with:
 * - Variable nodes used as graphs will be resolved as IRIs in the same way as the standard resolver
 * - Other variable nodes will be resolved as IRIs in the specified target graph
 *
 * @author Laurent Wouters
 */
public class VariableResolverIrisOf extends VariableResolver {
    /**
     * The target graph to use
     */
    private final GraphNode target;

    /**
     * Initializes this resolver
     *
     * @param target The target graph to use
     */
    public VariableResolverIrisOf(GraphNode target) {
        this.target = target;
    }

    @Override
    public Node resolve(VariableNode variable, RDFPatternSolution solution, NodeManager nodes, boolean isGraph) {
        if (isGraph)
            return nodes.getIRINode((GraphNode) null);
        return nodes.getIRINode(target);
    }
}
