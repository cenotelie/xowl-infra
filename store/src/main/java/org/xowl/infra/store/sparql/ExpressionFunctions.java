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

package org.xowl.infra.store.sparql;

import org.xowl.infra.store.RDFUtils;
import org.xowl.infra.store.Vocabulary;
import org.xowl.infra.store.execution.EvaluationException;
import org.xowl.infra.store.execution.EvaluationUtils;
import org.xowl.infra.store.rdf.*;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Dictionary of the known SPARQL functions
 *
 * @author Laurent Wouters
 */
class ExpressionFunctions {
    /**
     * The known functions
     */
    private static final Map<String, ExpressionFunction> FUNCTIONS = new ConcurrentHashMap<>();

    /**
     * Gets the function for the specified name
     *
     * @param name The name of the function to look for
     * @return The function, or null if it is not registered
     */
    public static ExpressionFunction get(String name) {
        return FUNCTIONS.get(name);
    }

    /**
     * Registers a new function
     *
     * @param function The function
     */
    public static void register(ExpressionFunction function) {
        FUNCTIONS.put(function.getName(), function);
    }

    static {
        register(new ExpressionFunction("STR", 1, 1) {
            @Override
            protected Object doEval(EvalContext context, RDFPatternSolution bindings, List<Expression> arguments) throws EvaluationException {
                Object v1 = arguments.get(0).eval(context, bindings);
                if (!(v1 instanceof Node))
                    return v1.toString();
                switch (((Node) v1).getNodeType()) {
                    case Node.TYPE_IRI:
                        return ((IRINode) v1).getIRIValue();
                    case Node.TYPE_LITERAL:
                        return ((LiteralNode) v1).getLexicalValue();
                    default:
                        return v1.toString();
                }
            }
        });
        register(new ExpressionFunction("LANG", 1, 1) {
            @Override
            protected Object doEval(EvalContext context, RDFPatternSolution bindings, List<Expression> arguments) throws EvaluationException {
                Object v1 = arguments.get(0).eval(context, bindings);
                if (!(v1 instanceof LiteralNode))
                    throw new EvaluationException("Type error (Literal node required)");
                return ((LiteralNode) v1).getLangTag();
            }
        });
        register(new ExpressionFunction("LANGMATCHES", 2, 2) {
            @Override
            protected Object doEval(EvalContext context, RDFPatternSolution bindings, List<Expression> arguments) throws EvaluationException {
                throw new EvaluationException("Not implemented");
            }
        });
        register(new ExpressionFunction("DATATYPE", 1, 1) {
            @Override
            protected Object doEval(EvalContext context, RDFPatternSolution bindings, List<Expression> arguments) throws EvaluationException {
                Object v1 = arguments.get(0).eval(context, bindings);
                if (!(v1 instanceof LiteralNode))
                    throw new EvaluationException("Type error (Literal node required)");
                String datatype = ((LiteralNode) v1).getDatatype();
                return context.getNodes().getIRINode(datatype);
            }
        });
        register(new ExpressionFunction("BOUND", 1, 1) {
            @Override
            protected Object doEval(EvalContext context, RDFPatternSolution bindings, List<Expression> arguments) throws EvaluationException {
                Expression exp1 = arguments.get(0);
                if (!(exp1 instanceof ExpressionRDF))
                    throw new EvaluationException("Type error (Variable node required)");
                Node node = ((ExpressionRDF) exp1).getNode();
                if (node.getNodeType() != Node.TYPE_VARIABLE)
                    throw new EvaluationException("Type error (Variable node required)");
                Node value = bindings.get((VariableNode) node);
                return (value != null);
            }
        });
        register(new ExpressionFunction("IRI", 1, 1) {
            @Override
            protected Object doEval(EvalContext context, RDFPatternSolution bindings, List<Expression> arguments) throws EvaluationException {
                Object v1 = arguments.get(0).eval(context, bindings);
                String value = v1.toString();
                if (v1 instanceof Node) {
                    switch (((Node) v1).getNodeType()) {
                        case Node.TYPE_IRI:
                            value = ((IRINode) v1).getIRIValue();
                            break;
                        case Node.TYPE_LITERAL:
                            value = ((LiteralNode) v1).getLexicalValue();
                            break;
                    }
                }
                return context.getNodes().getIRINode(value);
            }
        });
        register(new ExpressionFunction("URI", 1, 1) {
            @Override
            protected Object doEval(EvalContext context, RDFPatternSolution bindings, List<Expression> arguments) throws EvaluationException {
                Object v1 = arguments.get(0).eval(context, bindings);
                String value = v1.toString();
                if (v1 instanceof Node) {
                    switch (((Node) v1).getNodeType()) {
                        case Node.TYPE_IRI:
                            value = ((IRINode) v1).getIRIValue();
                            break;
                        case Node.TYPE_LITERAL:
                            value = ((LiteralNode) v1).getLexicalValue();
                            break;
                    }
                }
                return context.getNodes().getIRINode(value);
            }
        });
        register(new ExpressionFunction("BNODE", 0, 1) {
            @Override
            protected Object doEval(EvalContext context, RDFPatternSolution bindings, List<Expression> arguments) throws EvaluationException {
                if (arguments.size() == 0)
                    return context.getNodes().getBlankNode();
                throw new EvaluationException("Not implemented");
            }
        });
        register(new ExpressionFunction("RAND", 0, 0) {
            @Override
            protected Object doEval(EvalContext context, RDFPatternSolution bindings, List<Expression> arguments) throws EvaluationException {
                return (new Random()).nextDouble();
            }
        });
        register(new ExpressionFunction("ABS", 1, 1) {
            @Override
            protected Object doEval(EvalContext context, RDFPatternSolution bindings, List<Expression> arguments) throws EvaluationException {
                Object v1 = EvaluationUtils.primitive(arguments.get(0).eval(context, bindings));
                if (EvaluationUtils.isNumInteger(v1))
                    return Math.abs((long) v1);
                if (EvaluationUtils.isNumDecimal(v1))
                    return Math.abs((double) v1);
                throw new EvaluationException("Type error (numeric required)");
            }
        });
        register(new ExpressionFunction("CEIL", 1, 1) {
            @Override
            protected Object doEval(EvalContext context, RDFPatternSolution bindings, List<Expression> arguments) throws EvaluationException {
                Object v1 = EvaluationUtils.primitive(arguments.get(0).eval(context, bindings));
                if (EvaluationUtils.isNumInteger(v1) || EvaluationUtils.isNumDecimal(v1))
                    return Math.ceil((double) v1);
                throw new EvaluationException("Type error (numeric required)");
            }
        });
        register(new ExpressionFunction("FLOOR", 1, 1) {
            @Override
            protected Object doEval(EvalContext context, RDFPatternSolution bindings, List<Expression> arguments) throws EvaluationException {
                Object v1 = EvaluationUtils.primitive(arguments.get(0).eval(context, bindings));
                if (EvaluationUtils.isNumInteger(v1) || EvaluationUtils.isNumDecimal(v1))
                    return Math.floor((double) v1);
                throw new EvaluationException("Type error (numeric required)");
            }
        });
        register(new ExpressionFunction("ROUND", 1, 1) {
            @Override
            protected Object doEval(EvalContext context, RDFPatternSolution bindings, List<Expression> arguments) throws EvaluationException {
                Object v1 = EvaluationUtils.primitive(arguments.get(0).eval(context, bindings));
                if (EvaluationUtils.isNumInteger(v1) || EvaluationUtils.isNumDecimal(v1))
                    return Math.round((double) v1);
                throw new EvaluationException("Type error (numeric required)");
            }
        });
        register(new ExpressionFunction("CONCAT", 0, Integer.MAX_VALUE) {
            @Override
            protected Object doEval(EvalContext context, RDFPatternSolution bindings, List<Expression> arguments) throws EvaluationException {
                throw new EvaluationException("Not implemented");
            }
        });
        register(new ExpressionFunction("SUBSTR", 2, 3) {
            @Override
            protected Object doEval(EvalContext context, RDFPatternSolution bindings, List<Expression> arguments) throws EvaluationException {
                throw new EvaluationException("Not implemented");
            }
        });
        register(new ExpressionFunction("STRLEN", 1, 1) {
            @Override
            protected Object doEval(EvalContext context, RDFPatternSolution bindings, List<Expression> arguments) throws EvaluationException {
                Object v1 = EvaluationUtils.primitive(arguments.get(0).eval(context, bindings));
                if (!(v1 instanceof String))
                    throw new EvaluationException("Type error (String required)");
                return v1.toString().length();
            }
        });
        register(new ExpressionFunction("REPLACE", 3, 4) {
            @Override
            protected Object doEval(EvalContext context, RDFPatternSolution bindings, List<Expression> arguments) throws EvaluationException {
                throw new EvaluationException("Not implemented");
            }
        });
        register(new ExpressionFunction("UCASE", 1, 1) {
            @Override
            protected Object doEval(EvalContext context, RDFPatternSolution bindings, List<Expression> arguments) throws EvaluationException {
                throw new EvaluationException("Not implemented");
            }
        });
        register(new ExpressionFunction("LCASE", 1, 1) {
            @Override
            protected Object doEval(EvalContext context, RDFPatternSolution bindings, List<Expression> arguments) throws EvaluationException {
                throw new EvaluationException("Not implemented");
            }
        });
        register(new ExpressionFunction("ENCODE_FOR_URI", 1, 1) {
            @Override
            protected Object doEval(EvalContext context, RDFPatternSolution bindings, List<Expression> arguments) throws EvaluationException {
                throw new EvaluationException("Not implemented");
            }
        });
        register(new ExpressionFunction("CONTAINS", 2, 2) {
            @Override
            protected Object doEval(EvalContext context, RDFPatternSolution bindings, List<Expression> arguments) throws EvaluationException {
                throw new EvaluationException("Not implemented");
            }
        });
        register(new ExpressionFunction("STRSTARTS", 2, 2) {
            @Override
            protected Object doEval(EvalContext context, RDFPatternSolution bindings, List<Expression> arguments) throws EvaluationException {
                throw new EvaluationException("Not implemented");
            }
        });
        register(new ExpressionFunction("STRENDS", 2, 2) {
            @Override
            protected Object doEval(EvalContext context, RDFPatternSolution bindings, List<Expression> arguments) throws EvaluationException {
                throw new EvaluationException("Not implemented");
            }
        });
        register(new ExpressionFunction("STRBEFORE", 2, 2) {
            @Override
            protected Object doEval(EvalContext context, RDFPatternSolution bindings, List<Expression> arguments) throws EvaluationException {
                throw new EvaluationException("Not implemented");
            }
        });
        register(new ExpressionFunction("STRAFTER", 2, 2) {
            @Override
            protected Object doEval(EvalContext context, RDFPatternSolution bindings, List<Expression> arguments) throws EvaluationException {
                throw new EvaluationException("Not implemented");
            }
        });
        register(new ExpressionFunction("YEAR", 1, 1) {
            @Override
            protected Object doEval(EvalContext context, RDFPatternSolution bindings, List<Expression> arguments) throws EvaluationException {
                throw new EvaluationException("Not implemented");
            }
        });
        register(new ExpressionFunction("MONTH", 1, 1) {
            @Override
            protected Object doEval(EvalContext context, RDFPatternSolution bindings, List<Expression> arguments) throws EvaluationException {
                throw new EvaluationException("Not implemented");
            }
        });
        register(new ExpressionFunction("DAY", 1, 1) {
            @Override
            protected Object doEval(EvalContext context, RDFPatternSolution bindings, List<Expression> arguments) throws EvaluationException {
                throw new EvaluationException("Not implemented");
            }
        });
        register(new ExpressionFunction("HOURS", 1, 1) {
            @Override
            protected Object doEval(EvalContext context, RDFPatternSolution bindings, List<Expression> arguments) throws EvaluationException {
                throw new EvaluationException("Not implemented");
            }
        });
        register(new ExpressionFunction("MINUTES", 1, 1) {
            @Override
            protected Object doEval(EvalContext context, RDFPatternSolution bindings, List<Expression> arguments) throws EvaluationException {
                throw new EvaluationException("Not implemented");
            }
        });
        register(new ExpressionFunction("SECONDS", 1, 1) {
            @Override
            protected Object doEval(EvalContext context, RDFPatternSolution bindings, List<Expression> arguments) throws EvaluationException {
                throw new EvaluationException("Not implemented");
            }
        });
        register(new ExpressionFunction("TIMEZONE", 1, 1) {
            @Override
            protected Object doEval(EvalContext context, RDFPatternSolution bindings, List<Expression> arguments) throws EvaluationException {
                throw new EvaluationException("Not implemented");
            }
        });
        register(new ExpressionFunction("TZ", 1, 1) {
            @Override
            protected Object doEval(EvalContext context, RDFPatternSolution bindings, List<Expression> arguments) throws EvaluationException {
                throw new EvaluationException("Not implemented");
            }
        });
        register(new ExpressionFunction("NOW", 0, 0) {
            @Override
            protected Object doEval(EvalContext context, RDFPatternSolution bindings, List<Expression> arguments) throws EvaluationException {
                throw new EvaluationException("Not implemented");
            }
        });
        register(new ExpressionFunction("UUID", 0, 0) {
            @Override
            protected Object doEval(EvalContext context, RDFPatternSolution bindings, List<Expression> arguments) throws EvaluationException {
                String value = "urn:uuid:" + UUID.randomUUID().toString();
                return context.getNodes().getIRINode(value);
            }
        });
        register(new ExpressionFunction("STRUUID", 0, 0) {
            @Override
            protected Object doEval(EvalContext context, RDFPatternSolution bindings, List<Expression> arguments) throws EvaluationException {
                return UUID.randomUUID().toString();
            }
        });
        register(new ExpressionFunction("MD5", 1, 1) {
            @Override
            protected Object doEval(EvalContext context, RDFPatternSolution bindings, List<Expression> arguments) throws EvaluationException {
                throw new EvaluationException("Not implemented");
            }
        });
        register(new ExpressionFunction("SHA1", 1, 1) {
            @Override
            protected Object doEval(EvalContext context, RDFPatternSolution bindings, List<Expression> arguments) throws EvaluationException {
                throw new EvaluationException("Not implemented");
            }
        });
        register(new ExpressionFunction("SHA256", 1, 1) {
            @Override
            protected Object doEval(EvalContext context, RDFPatternSolution bindings, List<Expression> arguments) throws EvaluationException {
                throw new EvaluationException("Not implemented");
            }
        });
        register(new ExpressionFunction("SHA384", 1, 1) {
            @Override
            protected Object doEval(EvalContext context, RDFPatternSolution bindings, List<Expression> arguments) throws EvaluationException {
                throw new EvaluationException("Not implemented");
            }
        });
        register(new ExpressionFunction("SHA512", 1, 1) {
            @Override
            protected Object doEval(EvalContext context, RDFPatternSolution bindings, List<Expression> arguments) throws EvaluationException {
                throw new EvaluationException("Not implemented");
            }
        });
        register(new ExpressionFunction("COALESCE", 0, Integer.MAX_VALUE) {
            @Override
            protected Object doEval(EvalContext context, RDFPatternSolution bindings, List<Expression> arguments) throws EvaluationException {
                for (Expression exp : arguments) {
                    try {
                        return exp.eval(context, bindings);
                    } catch (EvaluationException ex) {
                        // ignore as per SPARQL specification
                    }
                }
                throw new EvaluationException("No adequate argument provided to COALESCE");
            }
        });
        register(new ExpressionFunction("IF", 3, 3) {
            @Override
            protected Object doEval(EvalContext context, RDFPatternSolution bindings, List<Expression> arguments) throws EvaluationException {
                boolean test = EvaluationUtils.bool(EvaluationUtils.primitive(arguments.get(0).eval(context, bindings)));
                return arguments.get(test ? 1 : 2).eval(context, bindings);
            }
        });
        register(new ExpressionFunction("STRLANG", 2, 2) {
            @Override
            protected Object doEval(EvalContext context, RDFPatternSolution bindings, List<Expression> arguments) throws EvaluationException {
                Object v1 = arguments.get(0).eval(context, bindings);
                Object v2 = arguments.get(1).eval(context, bindings);
                String s1 = v1 != null ? v1.toString() : "";
                if (v1 instanceof Node) {
                    switch (((Node) v1).getNodeType()) {
                        case Node.TYPE_IRI:
                            s1 = ((IRINode) v1).getIRIValue();
                            break;
                        case Node.TYPE_LITERAL:
                            s1 = ((LiteralNode) v1).getLexicalValue();
                            break;
                    }
                }
                String s2 = v2 != null ? v2.toString() : "";
                if (v2 instanceof Node) {
                    switch (((Node) v2).getNodeType()) {
                        case Node.TYPE_LITERAL:
                            s2 = ((LiteralNode) v2).getLexicalValue();
                            break;
                    }
                }
                return context.getNodes().getLiteralNode(s1, null, s2.isEmpty() ? null : s2);
            }
        });
        register(new ExpressionFunction("STRDT", 2, 2) {
            @Override
            protected Object doEval(EvalContext context, RDFPatternSolution bindings, List<Expression> arguments) throws EvaluationException {
                Object v1 = arguments.get(0).eval(context, bindings);
                Object v2 = arguments.get(1).eval(context, bindings);
                String s1 = v1 != null ? v1.toString() : "";
                if (v1 instanceof Node) {
                    switch (((Node) v1).getNodeType()) {
                        case Node.TYPE_IRI:
                            s1 = ((IRINode) v1).getIRIValue();
                            break;
                        case Node.TYPE_LITERAL:
                            s1 = ((LiteralNode) v1).getLexicalValue();
                            break;
                    }
                }
                String s2 = v2 != null ? v2.toString() : "";
                if (v2 instanceof Node) {
                    switch (((Node) v2).getNodeType()) {
                        case Node.TYPE_IRI:
                            s2 = ((IRINode) v2).getIRIValue();
                            break;
                        case Node.TYPE_LITERAL:
                            s2 = ((LiteralNode) v2).getLexicalValue();
                            break;
                    }
                }
                return context.getNodes().getLiteralNode(s1, s2.isEmpty() ? Vocabulary.xsdString : s2, null);
            }
        });
        register(new ExpressionFunction("sameTerm", 2, 2) {
            @Override
            protected Object doEval(EvalContext context, RDFPatternSolution bindings, List<Expression> arguments) throws EvaluationException {
                Object v1 = arguments.get(0).eval(context, bindings);
                Object v2 = arguments.get(1).eval(context, bindings);
                if (!(v1 instanceof Node) || !(v2 instanceof Node))
                    throw new EvaluationException("Type error (RDF nodes required)");
                return RDFUtils.same((Node) v1, (Node) v2);
            }
        });
        register(new ExpressionFunction("isIRI", 1, 1) {
            @Override
            protected Object doEval(EvalContext context, RDFPatternSolution bindings, List<Expression> arguments) throws EvaluationException {
                Object v1 = arguments.get(0).eval(context, bindings);
                if (!(v1 instanceof Node))
                    throw new EvaluationException("Type error (RDF node required)");
                return ((Node) v1).getNodeType() == Node.TYPE_IRI;
            }
        });
        register(new ExpressionFunction("isURI", 1, 1) {
            @Override
            protected Object doEval(EvalContext context, RDFPatternSolution bindings, List<Expression> arguments) throws EvaluationException {
                Object v1 = arguments.get(0).eval(context, bindings);
                if (!(v1 instanceof Node))
                    throw new EvaluationException("Type error (RDF node required)");
                return ((Node) v1).getNodeType() == Node.TYPE_IRI;
            }
        });
        register(new ExpressionFunction("isBLANK", 1, 1) {
            @Override
            protected Object doEval(EvalContext context, RDFPatternSolution bindings, List<Expression> arguments) throws EvaluationException {
                Object v1 = arguments.get(0).eval(context, bindings);
                if (!(v1 instanceof Node))
                    throw new EvaluationException("Type error (RDF node required)");
                return ((Node) v1).getNodeType() == Node.TYPE_BLANK;
            }
        });
        register(new ExpressionFunction("isLITERAL", 1, 1) {
            @Override
            protected Object doEval(EvalContext context, RDFPatternSolution bindings, List<Expression> arguments) throws EvaluationException {
                Object v1 = arguments.get(0).eval(context, bindings);
                if (!(v1 instanceof Node))
                    throw new EvaluationException("Type error (RDF node required)");
                return ((Node) v1).getNodeType() == Node.TYPE_LITERAL;
            }
        });
        register(new ExpressionFunction("isNUMERIC", 1, 1) {
            @Override
            protected Object doEval(EvalContext context, RDFPatternSolution bindings, List<Expression> arguments) throws EvaluationException {
                Object v1 = arguments.get(0).eval(context, bindings);
                if (v1 instanceof LiteralNode)
                    v1 = EvaluationUtils.primitive(v1);
                return (EvaluationUtils.isNumInteger(v1) || EvaluationUtils.isNumDecimal(v1));
            }
        });
        register(new ExpressionFunction("REGEX", 2, 3) {
            @Override
            protected Object doEval(EvalContext context, RDFPatternSolution bindings, List<Expression> arguments) throws EvaluationException {
                throw new EvaluationException("Not implemented");
            }
        });
    }
}
