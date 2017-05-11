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

import org.xowl.infra.store.Vocabulary;
import org.xowl.infra.store.rdf.GraphNode;
import org.xowl.infra.store.rdf.Quad;
import org.xowl.infra.store.rdf.SubjectNode;
import org.xowl.infra.store.rdf.VariableNode;
import org.xowl.infra.store.storage.NodeManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a template of seme (ontological entity) as a consequent to a denotation rule
 *
 * @author Laurent Wouters
 */
public class SemeTemplate extends SemeConsequent {
    /**
     * The template's identifier
     */
    private final String identifier;
    /**
     * The IRI of the seme's type
     */
    private final String typeIri;

    /**
     * Initializes this consequent
     *
     * @param identifier The template's identifier
     * @param typeIri    The IRI of the seme's type
     */
    public SemeTemplate(String identifier, String typeIri) {
        this.identifier = identifier;
        this.typeIri = typeIri;
    }

    /**
     * Gets the template's identifier
     *
     * @return The template's identifier
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * Gets the IRI of the seme's type
     *
     * @return The IRI of the seme's type
     */
    public String getTypeIri() {
        return typeIri;
    }



    @Override
    public void buildRdf(GraphNode graphSigns, GraphNode graphSemes, GraphNode graphMeta, NodeManager nodes, DenotationRuleContext context) {
        VariableNode variable = context.getVariable(identifier);

        context.getRdfRule().addConsequentPositive(new Quad(graphSemes,
                variable,
                nodes.getIRINode(Vocabulary.rdfType),
                nodes.getIRINode(typeIri)
        ));

        // properties
        if (properties != null) {
            for (SemeTemplateProperty property : properties)
                property.buildRdfProperty(graphSigns, graphSemes, graphMeta, nodes, variable, context);
        }

        buildRdfBindings(graphSigns, graphSemes, graphMeta, nodes, variable, context);
    }

    @Override
    protected SubjectNode getSubject(NodeManager nodes, DenotationRuleContext context) {
        return context.getVariable(identifier);
    }
}
