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
import org.xowl.infra.store.rdf.SubjectNode;
import org.xowl.infra.store.storage.NodeManager;

/**
 * Represents a template for a seme's property
 *
 * @author Laurent Wouters
 */
public abstract class SemeTemplateProperty {
    /**
     * The property's IRI
     */
    protected final String propertyIri;

    /**
     * Initializes this property
     *
     * @param propertyIri The property's IRI
     */
    public SemeTemplateProperty(String propertyIri) {
        this.propertyIri = propertyIri;
    }

    /**
     * Gets the property's IRI
     *
     * @return The property's IRI
     */
    public String getPropertyIri() {
        return propertyIri;
    }

    /**
     * Builds the RDF rule for this property
     *
     * @param graphSigns The graph for the signs
     * @param graphSemes The graph for the semes
     * @param graphMeta  The graph for the metadata
     * @param nodes      The node manager to use
     * @param parent     The node representing this consequent
     * @param context    The current context
     */
    public abstract void buildRdfProperty(GraphNode graphSigns, GraphNode graphSemes, GraphNode graphMeta, NodeManager nodes, SubjectNode parent, DenotationRuleContext context);
}
