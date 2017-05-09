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

import java.util.Collection;
import java.util.Collections;

/**
 * Implements the "represents" denotation rule that specifically associates a single symbol to a single individual in the domain
 *
 * @author Laurent Wouters
 */
public class DenotationRuleRepresents implements DenotationRule {
    /**
     * The RDF node for the symbol
     */
    private final Node symbol;
    /**
     * The RDf node for the individual
     */
    private final Node individual;

    /**
     * Initializes this rule
     *
     * @param symbol     The RDF node for the symbol
     * @param individual The RDf node for the individual
     */
    public DenotationRuleRepresents(Node symbol, Node individual) {
        this.symbol = symbol;
        this.individual = individual;
    }

    @Override
    public boolean isReusable() {
        return false;
    }

    @Override
    public Collection<Node> getMatchedSymbols(EvalContext context) {
        return Collections.singletonList(symbol);
    }

    @Override
    public Collection<Node> getDomainConcepts() {
        return Collections.singletonList(individual);
    }

    @Override
    public RDFRule getTransformationRule() {
        return null;
    }

    @Override
    public String serializedString() {
        return symbol.toString();
    }

    @Override
    public String serializedJSON() {
        return null;
    }
}
