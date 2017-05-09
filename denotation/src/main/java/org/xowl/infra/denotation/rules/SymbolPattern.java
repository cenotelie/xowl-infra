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

import org.xowl.infra.denotation.artifact.SymbolRelation;
import org.xowl.infra.utils.collections.Couple;

import java.util.Collection;

/**
 * Represents a pattern of concrete symbol
 *
 * @author Laurent Wouters
 */
public class SymbolPattern {
    /**
     * The identifier for this pattern
     */
    private final String identifier;
    /**
     * The constraint on the symbol's properties
     */
    private Expression properties;
    /**
     * The constraint on the symbol's relations
     */
    private Collection<Couple<SymbolRelation, SymbolPattern>> relations;
    /**
     * The identifier of the variable for the bound domain element, if any
     */
    private String domain;

    /**
     * Initializes this pattern
     *
     * @param identifier The identifier for this pattern
     */
    public SymbolPattern(String identifier) {
        this.identifier = identifier;
        this.properties = null;
        this.relations = null;
        this.domain = null;
    }
}
