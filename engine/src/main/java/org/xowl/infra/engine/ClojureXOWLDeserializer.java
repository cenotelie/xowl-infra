/*******************************************************************************
 * Copyright (c) 2016 Association Cénotélie (cenotelie.fr)
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

package org.xowl.infra.engine;

import org.xowl.hime.redist.ASTNode;
import org.xowl.infra.lang.actions.ActionsFactory;
import org.xowl.infra.lang.actions.FunctionDefinitionAxiom;
import org.xowl.infra.lang.actions.FunctionExpression;
import org.xowl.infra.lang.owl2.Axiom;
import org.xowl.infra.lang.owl2.IRI;
import org.xowl.infra.store.Evaluator;
import org.xowl.infra.store.loaders.XOWLDeserializer;

/**
 * The specialized xOWL deserializer that handles Clojure code
 *
 * @author Laurent Wouters
 */
public class ClojureXOWLDeserializer extends XOWLDeserializer {
    /**
     * Initializes this de-serializer
     *
     * @param evaluator The evaluator to use
     */
    public ClojureXOWLDeserializer(Evaluator evaluator) {
        super(evaluator);
    }

    @Override
    protected Object loadForm(ASTNode node) {
        return ((ClojureEvaluator) evaluator).loadExpression(node);
    }

    /**
     * Loads a FunctionDefinition axiom from an AST node
     *
     * @param node The AST node
     * @return The axiom
     */
    protected Axiom loadAxiomFunctionDefinition(ASTNode node) {
        FunctionExpression functionExpression = loadExpFunction(node.getChildren().get(1));
        String iri = (functionExpression instanceof IRI) ? (((IRI) functionExpression).getHasValue()) : null;
        ClojureFunction definition = ((ClojureEvaluator) evaluator).loadFunction(iri, node.getChildren().get(2));
        FunctionDefinitionAxiom axiom = ActionsFactory.newFunctionDefinitionAxiom();
        loadAxiomBase(node, axiom);
        axiom.setFunction(functionExpression);
        axiom.setDefinition(definition);
        return axiom;
    }
}
