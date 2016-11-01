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

package org.xowl.infra.store.loaders;

import org.xowl.hime.redist.ASTNode;
import org.xowl.infra.lang.actions.ActionsFactory;
import org.xowl.infra.lang.actions.FunctionDefinitionAxiom;
import org.xowl.infra.lang.actions.FunctionExpression;
import org.xowl.infra.lang.actions.OpaqueExpression;
import org.xowl.infra.lang.owl2.Axiom;
import org.xowl.infra.lang.owl2.EntityExpression;
import org.xowl.infra.lang.owl2.LiteralExpression;
import org.xowl.infra.lang.rules.Assertion;
import org.xowl.infra.lang.rules.Rule;
import org.xowl.infra.lang.rules.RulesFactory;

/**
 * Implements the deserialization of xOWL ontologies
 *
 * @author Laurent Wouters
 */
public abstract class XOWLDeserializer extends FunctionalOWL2Deserializer {
    /**
     * The current lexical context
     */
    protected LexicalContext context;

    @Override
    protected void loadElement(ASTNode node) {
        if (node.getSymbol().getID() == XOWLParser.ID.rule) {
            cache.addRule(loadRule(node));
        } else {
            super.loadElement(node);
        }
    }

    @Override
    protected Axiom loadAxiom(ASTNode node) {
        if (node.getSymbol().getID() == XOWLParser.ID.axiomFunctionDefinition) {
            return loadAxiomFunctionDefinition(node);
        } else {
            return super.loadAxiom(node);
        }
    }

    /**
     * Loads a FunctionDefinition axiom from an AST node
     *
     * @param node The AST node
     * @return The axiom
     */
    protected Axiom loadAxiomFunctionDefinition(ASTNode node) {
        FunctionDefinitionAxiom axiom = ActionsFactory.newFunctionDefinitionAxiom();
        loadAxiomBase(node, axiom);
        axiom.setFunction(loadExpFunction(node.getChildren().get(1)));
        axiom.setDefinition(loadForm(node.getChildren().get(2)));
        return axiom;
    }

    /**
     * Loads a rule from an AST node
     *
     * @param node The AST node
     * @return The rule
     */
    protected Rule loadRule(ASTNode node) {
        context = new LexicalContext();
        Rule rule = RulesFactory.newRule();
        rule.setHasIRI(loadEntity(node.getChildren().get(0)));
        for (ASTNode child : node.getChildren().get(1).getChildren())
            rule.addAntecedents(loadAssertion(child));
        for (ASTNode child : node.getChildren().get(2).getChildren())
            rule.addConsequents(loadAssertion(child));
        for (ASTNode child : node.getChildren().get(3).getChildren()) {
            OpaqueExpression expression = ActionsFactory.newOpaqueExpression();
            expression.setValue(loadForm(child));
            rule.setGuard(expression);
        }
        return rule;
    }

    /**
     * Loads a rule assertion element from an AST node
     *
     * @param node The AST node
     * @return The rule assertion element
     */
    protected Assertion loadAssertion(ASTNode node) {
        Assertion assertion = RulesFactory.newAssertion();
        assertion.setIsPositive(true);
        assertion.setIsMeta(false);
        for (ASTNode child : node.getChildren()) {
            if (child.getSymbol().getID() == XOWLLexer.ID.RULE_ELEMENT_META)
                assertion.setIsMeta(true);
            else if (child.getSymbol().getID() == XOWLLexer.ID.RULE_ELEMENT_NOT)
                assertion.setIsPositive(false);
            else
                assertion.addAxioms(loadAxiom(child));
        }
        return assertion;
    }

    @Override
    protected LiteralExpression loadExpLiteral(ASTNode node) {
        switch (node.getSymbol().getID()) {
            case XOWLParser.ID.literalInteger:
                return loadExpIntegerLiteral(node.getChildren().get(0));
            case XOWLLexer.ID.XOWL_QVAR:
                return context.resolveQVar(node.getValue().substring(1));
            case XOWLLexer.ID.XOWL_OPAQUE_EXP:
                OpaqueExpression expression = ActionsFactory.newOpaqueExpression();
                expression.setValue(loadForm(node.getChildren().get(0)));
                return expression;
            default:
                return super.loadExpLiteral(node);
        }
    }

    @Override
    protected EntityExpression loadExpEntity(ASTNode node) {
        switch (node.getSymbol().getID()) {
            case XOWLLexer.ID.XOWL_QVAR:
                return context.resolveQVar(node.getValue().substring(1));
            case XOWLLexer.ID.XOWL_OPAQUE_EXP:
                OpaqueExpression expression = ActionsFactory.newOpaqueExpression();
                expression.setValue(loadForm(node.getChildren().get(0)));
                return expression;
            default:
                return super.loadExpEntity(node);
        }
    }

    /**
     * Loads a function expression from an AST node
     *
     * @param node The AST node
     * @return The function expression
     */
    protected FunctionExpression loadExpFunction(ASTNode node) {
        return loadExpEntity(node);
    }

    /**
     * Loads the opaque behavior from an AST node
     *
     * @param node The AST node
     * @return The opaque behavior
     */
    protected abstract Object loadForm(ASTNode node);
}
