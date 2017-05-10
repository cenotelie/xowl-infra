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

import org.xowl.infra.utils.collections.Couple;

import java.util.Collection;

/**
 * Represents a template for a domain element
 *
 * @author Laurent Wouters
 */
public class DomainTemplate {
    /**
     * The identifier for this template
     */
    private final String identifier;
    /**
     * The IRI of the instantiated domain concept
     */
    private final String conceptIri;
    /**
     * The properties for this element
     */
    private Collection<Couple<String, Expression>> properties;
    /**
     * The symbol pattern to bind to this template
     */
    private Collection<SymbolPattern> bound;

    /**
     * Initializes this template
     *
     * @param identifier The identifier for this template
     * @param conceptIri The IRI of the instantiated domain concept
     */
    public DomainTemplate(String identifier, String conceptIri) {
        this.identifier = identifier;
        this.conceptIri = conceptIri;
    }
}