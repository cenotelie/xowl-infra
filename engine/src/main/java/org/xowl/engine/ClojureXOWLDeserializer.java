/*******************************************************************************
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
 ******************************************************************************/

package org.xowl.engine;

import clojure.java.api.Clojure;
import org.xowl.hime.redist.ASTNode;
import org.xowl.lang.actions.FunctionDefinitionAxiom;
import org.xowl.lang.actions.FunctionExpression;
import org.xowl.lang.owl2.Axiom;
import org.xowl.lang.owl2.IRI;
import org.xowl.store.loaders.XOWLDeserializer;
import org.xowl.store.loaders.XOWLLexer;
import org.xowl.store.loaders.XOWLParser;

/**
 * The specialized xOWL deserializer that handles Clojure code
 *
 * @author Laurent Wouters
 */
public class ClojureXOWLDeserializer extends XOWLDeserializer {
    /**
     * The counter for anonymous functions
     */
    private static int COUNTER = 0;

    /**
     * Generates a new Clojure name for an anonymous function
     *
     * @return A new Clojure name
     */
    private static String newCljName() {
        String name = "_xowl_" + COUNTER;
        COUNTER++;
        return name;
    }

    @Override
    protected Object loadForm(ASTNode node) {
        StringBuilder builder = new StringBuilder();
        serializeClojure(builder, node);
        return Clojure.read(builder.toString());
    }

    /**
     * Loads a FunctionDefinition axiom from an AST node
     *
     * @param node The AST node
     * @return The axiom
     */
    protected Axiom loadAxiomFunctionDefinition(ASTNode node) {
        FunctionExpression functionExpression = loadExpFunction(node.getChildren().get(1));
        String name;
        if (functionExpression instanceof IRI) {
            String iri = ((IRI) functionExpression).getHasValue();
            String[] parts = iri.split("#");
            name = parts[parts.length - 1];
        } else {
            name = newCljName();
        }
        StringBuilder builder = new StringBuilder();
        serializeClojure(builder, node.getChildren().get(2));
        ClojureFunction definition = Engine.register(name, builder.toString());

        FunctionDefinitionAxiom axiom = new FunctionDefinitionAxiom();
        loadAxiomBase(node, axiom);
        axiom.setFunction(functionExpression);
        axiom.setDefinition(definition);
        return axiom;
    }

    /**
     * Re-serializes the specified AST node into a string for the Clojure reader
     *
     * @param builder The string builder for the result
     * @param node    An AST node
     */
    private void serializeClojure(StringBuilder builder, ASTNode node) {
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
