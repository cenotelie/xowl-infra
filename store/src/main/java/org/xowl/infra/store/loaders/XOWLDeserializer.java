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
import org.xowl.infra.store.execution.EvaluableExpression;

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
        OpaqueExpression opaqueExpression = ActionsFactory.newOpaqueExpression();
        opaqueExpression.setValue(loadForm(node.getChildren().get(2)));
        axiom.setDefinition(opaqueExpression);
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
    protected EvaluableExpression loadForm(ASTNode node) {
        return loadForm(serializeClojure(node));
    }

    /**
     * Loads the opaque behavior from an AST node
     *
     * @param source The source for the expression
     * @return The opaque behavior
     */
    protected abstract EvaluableExpression loadForm(String source);

    /**
     * Re-serializes the specified AST node into a string for the Clojure reader
     *
     * @param node An AST node
     * @return The serialized Clojure source
     */
    private static String serializeClojure(ASTNode node) {
        StringBuilder builder = new StringBuilder();
        serializeClojure(builder, node);
        return builder.toString();
    }

    /**
     * Re-serializes the specified AST node into a string for the Clojure reader
     *
     * @param builder The string builder for the result
     * @param node    An AST node
     */
    private static void serializeClojure(StringBuilder builder, ASTNode node) {
        switch (node.getSymbol().getID()) {
            case XOWLLexer.ID.CLJ_SYMBOL:
            case XOWLLexer.ID.CLJ_KEYWORD:
            case XOWLLexer.ID.LITERAL_STRING:
            case XOWLLexer.ID.LITERAL_CHAR:
            case XOWLLexer.ID.LITERAL_NIL:
            case XOWLLexer.ID.LITERAL_TRUE:
            case XOWLLexer.ID.LITERAL_FALSE:
            case XOWLLexer.ID.LITERAL_INTEGER:
            case XOWLLexer.ID.LITERAL_FLOAT:
            case XOWLLexer.ID.LITERAL_RATIO:
            case XOWLLexer.ID.LITERAL_ARGUMENT:
                builder.append(node.getValue());
                break;
            case XOWLParser.ID.list:
                builder.append("( ");
                for (ASTNode child : node.getChildren())
                    serializeClojure(builder, child);
                builder.append(")");
                break;
            case XOWLParser.ID.vector:
                builder.append("[ ");
                for (ASTNode child : node.getChildren())
                    serializeClojure(builder, child);
                builder.append("]");
                break;
            case XOWLParser.ID.map:
                builder.append("{ ");
                for (ASTNode couple : node.getChildren()) {
                    serializeClojure(builder, couple.getChildren().get(0));
                    serializeClojure(builder, couple.getChildren().get(1));
                }
                builder.append("}");
                break;
            case XOWLParser.ID.set:
                builder.append("#{ ");
                for (ASTNode child : node.getChildren())
                    serializeClojure(builder, child);
                builder.append("}");
                break;
            case XOWLParser.ID.constructor:
                builder.append("#");
                serializeClojure(builder, node.getChildren().get(0));
                serializeClojure(builder, node.getChildren().get(1));
                break;
            case XOWLParser.ID.quote:
                builder.append("'");
                serializeClojure(builder, node.getChildren().get(0));
                break;
            case XOWLParser.ID.deref:
                builder.append("@");
                serializeClojure(builder, node.getChildren().get(0));
                break;
            case XOWLParser.ID.metadata:
                builder.append("^");
                serializeClojure(builder, node.getChildren().get(0));
                serializeClojure(builder, node.getChildren().get(1));
                break;
            case XOWLParser.ID.regexp:
                builder.append("#");
                builder.append(node.getChildren().get(0));
                break;
            case XOWLParser.ID.var_quote:
                builder.append("#'");
                serializeClojure(builder, node.getChildren().get(0));
                break;
            case XOWLParser.ID.anon_function:
                builder.append("#");
                serializeClojure(builder, node.getChildren().get(0));
                break;
            case XOWLParser.ID.ignore:
                builder.append("#_");
                serializeClojure(builder, node.getChildren().get(0));
                break;
            case XOWLParser.ID.syntax_quote:
                builder.append("`");
                serializeClojure(builder, node.getChildren().get(0));
                break;
            case XOWLParser.ID.unquote:
                builder.append("~");
                serializeClojure(builder, node.getChildren().get(0));
                break;
            case XOWLParser.ID.unquote_splicing:
                builder.append("~@");
                serializeClojure(builder, node.getChildren().get(0));
                break;
            case XOWLParser.ID.conditional:
                builder.append("#?");
                serializeClojure(builder, node.getChildren().get(0));
                break;
            default:
                throw new Error("Unsupported construct: " + node.getSymbol().getName());
        }
        builder.append(" ");
    }
}
