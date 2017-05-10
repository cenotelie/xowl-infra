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

import org.xowl.infra.store.rdf.GraphNode;
import org.xowl.infra.store.rdf.Quad;
import org.xowl.infra.store.rdf.SubjectNode;
import org.xowl.infra.store.storage.NodeManager;

/**
 * A property template for a seme when the value is another seme template
 *
 * @author Laurent Wouters
 */
public class SemeTemplatePropertyTemplate extends SemeTemplateProperty {
    /**
     * The referenced seme template
     */
    private final SemeTemplate reference;

    /**
     * Initializes this property
     *
     * @param propertyIri The property's IRI
     * @param reference   The referenced seme template
     */
    public SemeTemplatePropertyTemplate(String propertyIri, SemeTemplate reference) {
        super(propertyIri);
        this.reference = reference;
    }

    /**
     * Gets the referenced seme template
     *
     * @return The referenced seme template
     */
    public SemeTemplate getReference() {
        return reference;
    }

    @Override
    public void buildRdfProperty(GraphNode graphSigns, GraphNode graphSemes, GraphNode graphMeta, NodeManager nodes, SubjectNode parent, DenotationRuleContext context) {
        context.getRdfRule().addConsequentPositive(new Quad(graphSemes,
                parent,
                nodes.getIRINode(propertyIri),
                reference.getSubject(nodes, context)
        ));
    }
}
