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

import org.xowl.infra.store.rdf.SubjectNode;
import org.xowl.infra.store.storage.NodeManager;

/**
 * Represents a previously matched seme (ontological entity) as a consequent of a denotation rule
 *
 * @author Laurent Wouters
 */
public class SemeMatched extends SemeConsequent {
    /**
     * The seme's identifier
     */
    private final String identifier;

    /**
     * Initializes this consequent
     *
     * @param identifier The seme's identifier
     */
    public SemeMatched(String identifier) {
        this.identifier = identifier;
    }

    /**
     * Gets the seme's identifier
     *
     * @return The seme's identifier
     */
    public String getIdentifier() {
        return identifier;
    }

    @Override
    protected SubjectNode getSubject(NodeManager nodes, DenotationRuleContext context) {
        return context.getVariable(identifier);
    }
}
