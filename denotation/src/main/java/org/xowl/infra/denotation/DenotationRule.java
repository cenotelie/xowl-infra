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

package org.xowl.infra.denotation;

import org.xowl.infra.store.rdf.Node;
import org.xowl.infra.store.rdf.RDFRule;
import org.xowl.infra.store.sparql.EvalContext;
import org.xowl.infra.utils.Serializable;

import java.util.Collection;

/**
 * Represents a denotation rule, i.e. a rule that associates symbols in a user's artifact to elements of domain knowledge
 *
 * @author Laurent Wouters
 */
public interface DenotationRule extends Serializable {
    /**
     * Gets whether this rule can be reused in another denotation
     *
     * @return Whether this rule can be reused in another denotation
     */
    boolean isReusable();

    /**
     * Gets the collection of symbols matched by this rule
     *
     * @param context The current context for the rule
     * @return The matched symbols
     */
    Collection<Node> getMatchedSymbols(EvalContext context);

    /**
     * Gets the associated domain concepts
     *
     * @return The associated domain concepts
     */
    Collection<Node> getDomainConcepts();

    /**
     * Get the transformation rule that can generate an ontological instance of the domain schema
     * for the symbols of a user's artifact
     *
     * @return The transformation rule
     */
    RDFRule getTransformationRule();
}
