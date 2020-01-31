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

import fr.cenotelie.commons.utils.http.URIUtils;
import org.xowl.infra.store.storage.NodeManager;

/**
 * A variable resolver that resolves variable with:
 * - Variable nodes used as graphs will be resolved as IRIs in the same way as the standard resolver
 * - Other variable nodes will be resolved as IRIs using the specified template
 *
 * @author Laurent Wouters
 */
public class VariableResolverIriTemplate extends VariableResolver {
    /**
     * The parts of the IRI template
     * Parts are either strings or variable nodes
     */
    private final Object[] templateParts;

    /**
     * Initializes this resolver
     *
     * @param templateParts The parts of the IRI template
     *                      Parts are either strings or variable nodes
     */
    public VariableResolverIriTemplate(Object[] templateParts) {
        this.templateParts = templateParts;
    }

    @Override
    public Node resolve(VariableNode variable, RDFPatternSolution solution, NodeManager nodes, boolean isGraph) {
        if (isGraph)
            return nodes.getIRINode((GraphNode) null);
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i != templateParts.length; i++) {
            if (templateParts[i] instanceof VariableNode) {
                Node node = solution.get((VariableNode) templateParts[i]);
                if (node != null) {
                    switch (node.getNodeType()) {
                        case Node.TYPE_IRI:
                            builder.append(((IRINode) node).getIRIValue());
                            break;
                        case Node.TYPE_LITERAL: {
                            String value = ((LiteralNode) node).getLexicalValue();
                            if (!URIUtils.allLegalCharacters(value))
                                value = URIUtils.encodeComponent(value);
                            builder.append(value);
                            break;
                        }
                    }
                }
            } else {
                String value = templateParts[i].toString();
                if (!URIUtils.allLegalCharacters(value))
                    value = URIUtils.encodeComponent(value);
                builder.append(value);
            }
        }
        return nodes.getIRINode(builder.toString());
    }
}
