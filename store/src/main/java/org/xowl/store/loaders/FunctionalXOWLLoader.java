/**********************************************************************
 * Copyright (c) 2015 Laurent Wouters
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
 *
 * Contributors:
 *     Laurent Wouters - lwouters@xowl.org
 **********************************************************************/
package org.xowl.store.loaders;

import org.xowl.hime.redist.ASTNode;
import org.xowl.hime.redist.Context;
import org.xowl.hime.redist.ParseError;
import org.xowl.hime.redist.ParseResult;
import org.xowl.hime.redist.parsers.InitializationException;
import org.xowl.lang.owl2.EntityExpression;
import org.xowl.lang.owl2.LiteralExpression;
import org.xowl.lang.rules.Assertion;
import org.xowl.lang.rules.Rule;
import org.xowl.utils.Files;
import org.xowl.utils.Logger;

import java.io.IOException;
import java.io.Reader;

/**
 * Loader of xOWL ontologies serialized in the Functional syntax
 *
 * @author Laurent Wouters
 */
public class FunctionalXOWLLoader extends FunctionalOWL2Loader {
    /**
     * The current lexical context
     */
    protected LexicalContext context;

    @Override
    public ParseResult parse(Logger logger, Reader reader) {
        ParseResult result;
        try {
            String content = Files.read(reader);
            FunctionalXOWLLexer lexer = new FunctionalXOWLLexer(content);
            FunctionalXOWLParser parser = new FunctionalXOWLParser(lexer);
            parser.setRecover(false);
            result = parser.parse();
        } catch (IOException ex) {
            logger.error(ex);
            return null;
        } catch (InitializationException ex) {
            logger.error(ex);
            return null;
        }
        for (ParseError error : result.getErrors()) {
            logger.error(error);
            Context context = result.getInput().getContext(error.getPosition());
            logger.error(context.getContent());
            logger.error(context.getPointer());
        }
        return result;
    }

    @Override
    protected void loadElement(ASTNode node) {
        if (node.getSymbol().getID() == FunctionalXOWLParser.ID.rule) {
            cache.addRule(loadRule(node));
        } else {
            super.loadElement(node);
        }
    }

    /**
     * Loads a rule from an AST node
     *
     * @param node The AST node
     * @return The rule
     */
    protected Rule loadRule(ASTNode node) {
        context = new LexicalContext();
        Rule rule = new Rule();
        rule.setHasIRI(loadEntity(node.getChildren().get(0)));
        for (ASTNode child : node.getChildren().get(1).getChildren())
            rule.addAntecedents(loadAssertion(child));
        for (ASTNode child : node.getChildren().get(2).getChildren())
            rule.addConsequents(loadAssertion(child));
        return rule;
    }

    /**
     * Loads a rule assertion element from an AST node
     *
     * @param node The AST node
     * @return The rule assertion element
     */
    protected Assertion loadAssertion(ASTNode node) {
        Assertion assertion = new Assertion();
        assertion.setIsPositive(true);
        for (ASTNode child : node.getChildren()) {
            if (child.getSymbol().getID() == FunctionalXOWLLexer.ID.RULE_ELEMENT_META)
                assertion.setIsMeta(true);
            else if (child.getSymbol().getID() == FunctionalXOWLLexer.ID.RULE_ELEMENT_NOT)
                assertion.setIsPositive(false);
            else
                assertion.addAxioms(loadAxiom(child));
        }
        return assertion;
    }


    @Override
    protected LiteralExpression loadExpLiteral(ASTNode node) {
        switch (node.getSymbol().getID()) {
            case FunctionalXOWLParser.ID.literalInteger:
                return loadExpIntegerLiteral(node.getChildren().get(0));
            case FunctionalXOWLLexer.ID.QVAR:
                return context.resolveQVar(node.getSymbol().getValue());
            default:
                return super.loadExpLiteral(node);
        }
    }

    @Override
    protected EntityExpression loadExpEntity(ASTNode node) {
        switch (node.getSymbol().getID()) {
            case FunctionalXOWLLexer.ID.QVAR:
                return context.resolveQVar(node.getSymbol().getValue());
            default:
                return super.loadExpEntity(node);
        }
    }
}
