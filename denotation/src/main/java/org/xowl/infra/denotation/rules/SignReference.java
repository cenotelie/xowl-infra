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

package org.xowl.infra.denotation.rules;

import org.xowl.infra.denotation.Denotation;
import org.xowl.infra.store.rdf.GraphNode;
import org.xowl.infra.store.rdf.Quad;
import org.xowl.infra.store.storage.NodeManager;

/**
 * Represents a reference to a specific sign as an antecedent to a denotation rule
 *
 * @author Laurent Wouters
 */
public class SignReference implements DenotationRuleAntecedent {
    /**
     * The identifier of the referenced sign
     */
    private final String signId;

    /**
     * Initializes this reference
     *
     * @param signId The identifier of the referenced sign
     */
    public SignReference(String signId) {
        this.signId = signId;
    }

    /**
     * Gets the identifier of the referenced sign
     *
     * @return The identifier of the referenced sign
     */
    public String getSignId() {
        return signId;
    }

    @Override
    public void buildRdf(GraphNode graphSigns, GraphNode graphSemes, GraphNode graphMeta, NodeManager nodes, DenotationRuleContext context) {
        context.getRdfRule().addConsequentPositive(new Quad(graphMeta,
                nodes.getIRINode(signId),
                nodes.getIRINode(Denotation.META_MATCHED_BY),
                nodes.getIRINode(context.getRdfRule().getIRI())
        ));
    }
}
