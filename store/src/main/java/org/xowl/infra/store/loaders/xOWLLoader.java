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
import org.xowl.hime.redist.ParseError;
import org.xowl.hime.redist.ParseResult;
import org.xowl.hime.redist.TextContext;
import org.xowl.hime.redist.parsers.InitializationException;
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
import org.xowl.infra.store.execution.ExecutionManager;
import org.xowl.infra.utils.IOUtils;
import org.xowl.infra.utils.logging.Logger;

import java.io.IOException;
import java.io.Reader;

/**
 * Loader of xOWL ontologies serialized
 *
 * @author Laurent Wouters
 */
public class xOWLLoader extends FunctionalOWL2Loader {
    /**
     * The current execution manager
     */
    private final ExecutionManager executionManager;
    /**
     * The current lexical context
     */
    private LexicalContext context;

    /**
     * Initializes this loader
     *
     * @param executionManager The current execution manager
     */
    public xOWLLoader(ExecutionManager executionManager) {
        this.executionManager = executionManager;
    }

    @Override
    public ParseResult parse(Logger logger, Reader reader) {
        ParseResult result;
        try {
            String content = IOUtils.read(reader);
            xOWLLexer lexer = new xOWLLexer(content);
            xOWLParser parser = new xOWLParser(lexer);
            parser.setModeRecoverErrors(false);
            result = parser.parse();
        } catch (IOException | InitializationException exception) {
            logger.error(exception);
            return null;
        }
        for (ParseError error : result.getErrors()) {
            logger.error(error);
            TextContext context = result.getInput().getContext(error.getPosition(), error.getLength());
            logger.error(context.getContent());
            logger.error(context.getPointer());
        }
        return result;
    }

    @Override
    protected void loadElement(ASTNode node) {
        if (node.getSymbol().getID() == xOWLParser.ID.rule) {
            cache.addRule(loadRule(node));
        } else {
            super.loadElement(node);
        }
    }

    @Override
    protected Axiom loadAxiom(ASTNode node) {
        if (node.getSymbol().getID() == xOWLParser.ID.axiomFunctionDefinition) {
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
            if (child.getSymbol().getID() == xOWLLexer.ID.RULE_ELEMENT_META)
                assertion.setIsMeta(true);
            else if (child.getSymbol().getID() == xOWLLexer.ID.RULE_ELEMENT_NOT)
                assertion.setIsPositive(false);
            else
                assertion.addAxioms(loadAxiom(child));
        }
        return assertion;
    }

    @Override
    protected LiteralExpression loadExpLiteral(ASTNode node) {
        switch (node.getSymbol().getID()) {
            case xOWLParser.ID.literalInteger:
                return loadExpIntegerLiteral(node.getChildren().get(0));
            case xOWLLexer.ID.XOWL_QVAR:
                return context.resolveQVar(node.getValue().substring(1));
            case xOWLLexer.ID.XOWL_OPAQUE_EXP:
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
            case xOWLLexer.ID.XOWL_QVAR:
                return context.resolveQVar(node.getValue().substring(1));
            case xOWLLexer.ID.XOWL_OPAQUE_EXP:
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
        return executionManager.loadExpression(serializeClojure(node));
    }

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
            case xOWLLexer.ID.CLJ_SYMBOL:
            case xOWLLexer.ID.CLJ_KEYWORD:
            case xOWLLexer.ID.LITERAL_STRING:
            case xOWLLexer.ID.LITERAL_CHAR:
            case xOWLLexer.ID.LITERAL_NIL:
            case xOWLLexer.ID.LITERAL_TRUE:
            case xOWLLexer.ID.LITERAL_FALSE:
            case xOWLLexer.ID.LITERAL_INTEGER:
            case xOWLLexer.ID.LITERAL_FLOAT:
            case xOWLLexer.ID.LITERAL_RATIO:
            case xOWLLexer.ID.LITERAL_ARGUMENT:
                builder.append(node.getValue());
                break;
            case xOWLParser.ID.list:
                builder.append("( ");
                for (ASTNode child : node.getChildren())
                    serializeClojure(builder, child);
                builder.append(")");
                break;
            case xOWLParser.ID.vector:
                builder.append("[ ");
                for (ASTNode child : node.getChildren())
                    serializeClojure(builder, child);
                builder.append("]");
                break;
            case xOWLParser.ID.map:
                builder.append("{ ");
                for (ASTNode couple : node.getChildren()) {
                    serializeClojure(builder, couple.getChildren().get(0));
                    serializeClojure(builder, couple.getChildren().get(1));
                }
                builder.append("}");
                break;
            case xOWLParser.ID.set:
                builder.append("#{ ");
                for (ASTNode child : node.getChildren())
                    serializeClojure(builder, child);
                builder.append("}");
                break;
            case xOWLParser.ID.constructor:
                builder.append("#");
                serializeClojure(builder, node.getChildren().get(0));
                serializeClojure(builder, node.getChildren().get(1));
                break;
            case xOWLParser.ID.quote:
                builder.append("'");
                serializeClojure(builder, node.getChildren().get(0));
                break;
            case xOWLParser.ID.deref:
                builder.append("@");
                serializeClojure(builder, node.getChildren().get(0));
                break;
            case xOWLParser.ID.metadata:
                builder.append("^");
                serializeClojure(builder, node.getChildren().get(0));
                serializeClojure(builder, node.getChildren().get(1));
                break;
            case xOWLParser.ID.regexp:
                builder.append("#");
                builder.append(node.getChildren().get(0));
                break;
            case xOWLParser.ID.var_quote:
                builder.append("#'");
                serializeClojure(builder, node.getChildren().get(0));
                break;
            case xOWLParser.ID.anon_function:
                builder.append("#");
                serializeClojure(builder, node.getChildren().get(0));
                break;
            case xOWLParser.ID.ignore:
                builder.append("#_");
                serializeClojure(builder, node.getChildren().get(0));
                break;
            case xOWLParser.ID.syntax_quote:
                builder.append("`");
                serializeClojure(builder, node.getChildren().get(0));
                break;
            case xOWLParser.ID.unquote:
                builder.append("~");
                serializeClojure(builder, node.getChildren().get(0));
                break;
            case xOWLParser.ID.unquote_splicing:
                builder.append("~@");
                serializeClojure(builder, node.getChildren().get(0));
                break;
            case xOWLParser.ID.conditional:
                builder.append("#?");
                serializeClojure(builder, node.getChildren().get(0));
                break;
            default:
                throw new Error("Unsupported construct: " + node.getSymbol().getName());
        }
        builder.append(" ");
    }
}
