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

package org.xowl.infra.store.sparql;

import org.xowl.infra.store.RDFUtils;
import org.xowl.infra.store.Repository;
import org.xowl.infra.store.Vocabulary;
import org.xowl.infra.store.rdf.IRINode;
import org.xowl.infra.store.rdf.LiteralNode;
import org.xowl.infra.store.rdf.Node;
import org.xowl.infra.store.rdf.RDFPatternSolution;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * Represents the call to an external function in an expression
 *
 * @author Laurent Wouters
 */
public class ExpressionFunctionCall implements Expression {
    /**
     * The function's IRI
     */
    private final String iri;
    /**
     * The arguments to use
     */
    private final List<Expression> arguments;
    /**
     * Whether the DISTINCT keyword is applied
     */
    private final boolean isDistinct;
    /**
     * The separator marker
     */
    private final String separator;

    /**
     * Initializes this expression
     *
     * @param iri        The function's IRI
     * @param arguments  The arguments to use
     * @param isDistinct Whether the DISTINCT keyword is applied
     * @param separator  The separator marker
     */
    public ExpressionFunctionCall(String iri, List<Expression> arguments, boolean isDistinct, String separator) {
        this.iri = iri;
        this.arguments = new ArrayList<>(arguments);
        this.isDistinct = isDistinct;
        this.separator = separator;
    }

    @Override
    public Object eval(Repository repository, RDFPatternSolution bindings) throws EvalException {
        if (iri.equalsIgnoreCase("IF")) {
            if (arguments.size() < 3)
                throw new EvalException("IF requires 3 arguments");
            boolean test = ExpressionOperator.bool(ExpressionOperator.primitive(arguments.get(0).eval(repository, bindings)));
            return arguments.get(test ? 1 : 2).eval(repository, bindings);
        }
        if (iri.equalsIgnoreCase("COALESCE")) {
            for (Expression exp : arguments) {
                try {
                    return exp.eval(repository, bindings);
                } catch (EvalException ex) {
                    // ignore as per SPARQL specification
                }
            }
            throw new EvalException("No adequate argument provided to COALESCE");
        }
        if (iri.equalsIgnoreCase("sameTerm")) {
            if (arguments.size() < 2)
                throw new EvalException("sameTerm requires 2 arguments");
            Object v1 = arguments.get(0).eval(repository, bindings);
            Object v2 = arguments.get(1).eval(repository, bindings);
            if (!(v1 instanceof Node) || !(v2 instanceof Node))
                throw new EvalException("Type error (RDF nodes required)");
            RDFUtils.same((Node) v1, (Node) v2);
        }
        if (iri.equalsIgnoreCase("isIRI") || iri.equalsIgnoreCase("isURI")) {
            if (arguments.size() < 1)
                throw new EvalException("isIRI requires 1 argument");
            Object v1 = arguments.get(0).eval(repository, bindings);
            if (!(v1 instanceof Node))
                throw new EvalException("Type error (RDF node required)");
            return ((Node) v1).getNodeType() == Node.TYPE_IRI;
        }
        if (iri.equalsIgnoreCase("isBlank")) {
            if (arguments.size() < 1)
                throw new EvalException("isBlank requires 1 argument");
            Object v1 = arguments.get(0).eval(repository, bindings);
            if (!(v1 instanceof Node))
                throw new EvalException("Type error (RDF node required)");
            return ((Node) v1).getNodeType() == Node.TYPE_BLANK;
        }
        if (iri.equalsIgnoreCase("isLiteral")) {
            if (arguments.size() < 1)
                throw new EvalException("isLiteral requires 1 argument");
            Object v1 = arguments.get(0).eval(repository, bindings);
            if (!(v1 instanceof Node))
                throw new EvalException("Type error (RDF node required)");
            return ((Node) v1).getNodeType() == Node.TYPE_LITERAL;
        }
        if (iri.equalsIgnoreCase("isNumeric")) {
            if (arguments.size() < 1)
                throw new EvalException("isNumeric requires 1 argument");
            Object v1 = arguments.get(0).eval(repository, bindings);
            if (!(v1 instanceof LiteralNode))
                throw new EvalException("Type error (RDF node required)");
            v1 = ExpressionOperator.primitive(v1);
            return (ExpressionOperator.isNumInteger(v1) || ExpressionOperator.isNumDecimal(v1));
        }
        if (iri.equalsIgnoreCase("str")) {
            if (arguments.size() < 1)
                throw new EvalException("str requires 1 argument");
            Object v1 = arguments.get(0).eval(repository, bindings);
            if (v1 instanceof IRINode)
                return ((IRINode) v1).getIRIValue();
            if (v1 instanceof LiteralNode)
                return ((LiteralNode) v1).getLexicalValue();
            throw new EvalException("Type error (RDF Literal or IRI node required)");
        }
        if (iri.equalsIgnoreCase("lang")) {
            if (arguments.size() < 1)
                throw new EvalException("lang requires 1 argument");
            Object v1 = arguments.get(0).eval(repository, bindings);
            if (!(v1 instanceof LiteralNode))
                throw new EvalException("Type error (RDF Literal node required)");
            return ((LiteralNode) v1).getLangTag();
        }
        if (iri.equalsIgnoreCase("datatype")) {
            if (arguments.size() < 1)
                throw new EvalException("datatype requires 1 argument");
            Object v1 = arguments.get(0).eval(repository, bindings);
            if (!(v1 instanceof LiteralNode))
                throw new EvalException("Type error (RDF Literal node required)");
            return repository.getStore().getIRINode(((LiteralNode) v1).getDatatype());
        }
        if (iri.equalsIgnoreCase("iri") || iri.equalsIgnoreCase("uri")) {
            if (arguments.size() < 1)
                throw new EvalException("iri requires 1 argument");
            Object v1 = arguments.get(0).eval(repository, bindings);
            if (v1 instanceof String)
                return repository.getStore().getIRINode(v1.toString());
            if (v1 instanceof IRINode)
                return v1;
            if (v1 instanceof LiteralNode)
                return repository.getStore().getIRINode(((LiteralNode) v1).getLexicalValue());
            throw new EvalException("Type error (String required)");
        }
        if (iri.equalsIgnoreCase("BNODE")) {
            //TODO: implement the version with 1 argument
            return repository.getStore().getBlankNode();
        }
        if (iri.equalsIgnoreCase("STRDT")) {
            if (arguments.size() < 1)
                throw new EvalException("STRDT requires 2 arguments");
            Object v1 = ExpressionOperator.primitive(arguments.get(0).eval(repository, bindings));
            Object v2 = ExpressionOperator.primitive(arguments.get(1).eval(repository, bindings));
            return repository.getStore().getLiteralNode(v1 == null ? "" : v1.toString(), v2 == null ? Vocabulary.xsdString : v2.toString(), null);
        }
        if (iri.equalsIgnoreCase("STRLANG")) {
            if (arguments.size() < 1)
                throw new EvalException("STRLANG requires 2 arguments");
            Object v1 = ExpressionOperator.primitive(arguments.get(0).eval(repository, bindings));
            Object v2 = ExpressionOperator.primitive(arguments.get(1).eval(repository, bindings));
            return repository.getStore().getLiteralNode(v1 == null ? "" : v1.toString(), null, v2 == null ? null : v2.toString());
        }
        if (iri.equalsIgnoreCase("UUID")) {
            String value = "urn:uuid:" + UUID.randomUUID().toString();
            return repository.getStore().getIRINode(value);
        }
        if (iri.equalsIgnoreCase("STRUUID")) {
            return UUID.randomUUID().toString();
        }
        if (iri.equalsIgnoreCase("STRLEN")) {
            if (arguments.size() < 1)
                throw new EvalException("STRLEN requires 1 argument");
            Object v1 = ExpressionOperator.primitive(arguments.get(0).eval(repository, bindings));
            if (!(v1 instanceof String))
                throw new EvalException("Type error (String required)");
            return v1.toString().length();
        }
        if (iri.equalsIgnoreCase("SUBSTR")) {
            if (arguments.size() < 1)
                throw new EvalException("SUBSTR requires 2 argument");
            Object v1 = ExpressionOperator.primitive(arguments.get(0).eval(repository, bindings));
            Object v2 = ExpressionOperator.primitive(arguments.get(1).eval(repository, bindings));
            Object v3 = arguments.size() >= 3 ? ExpressionOperator.primitive(arguments.get(2).eval(repository, bindings)) : null;
            if (!(v1 instanceof String))
                throw new EvalException("Type error (String required)");
            if (!ExpressionOperator.isNumInteger(v2))
                throw new EvalException("Type error (Integer required)");
            if (v3 != null && !ExpressionOperator.isNumInteger(v3))
                throw new EvalException("Type error (Integer required)");
            return v3 != null ? v1.toString().substring((int) ExpressionOperator.integer(v2), (int) ExpressionOperator.integer(v3)) : v1.toString().substring((int) ExpressionOperator.integer(v2));
        }
        if (iri.equalsIgnoreCase("UCASE")) {
            if (arguments.size() < 1)
                throw new EvalException("UCASE requires 1 argument");
            Object v1 = ExpressionOperator.primitive(arguments.get(0).eval(repository, bindings));
            if (!(v1 instanceof String))
                throw new EvalException("Type error (String required)");
            return v1.toString().toUpperCase();
        }
        if (iri.equalsIgnoreCase("LCASE")) {
            if (arguments.size() < 1)
                throw new EvalException("LCASE requires 1 argument");
            Object v1 = ExpressionOperator.primitive(arguments.get(0).eval(repository, bindings));
            if (!(v1 instanceof String))
                throw new EvalException("Type error (String required)");
            return v1.toString().toLowerCase();
        }
        throw new EvalException("Unknown function " + iri);
    }

    @Override
    public Object eval(Repository repository, Solutions solutions) throws EvalException {
        /*
          'COUNT' '(' 'DISTINCT'? ( '*' | Expression ) ')'
| 'SUM' '(' 'DISTINCT'? Expression ')'
| 'MIN' '(' 'DISTINCT'? Expression ')'
| 'MAX' '(' 'DISTINCT'? Expression ')'
| 'AVG' '(' 'DISTINCT'? Expression ')'
| 'SAMPLE' '(' 'DISTINCT'? Expression ')'
| 'GROUP_CONCAT' '(' 'DISTINCT'? Expression ( ';' 'SEPARATOR' '=' String )? ')'
         */


        /*if (iri.equalsIgnoreCase("COUNT")) {
            if (arguments.isEmpty()) {
                if (isDistinct)
                    return getDistincts(solutions).size();
                return solutions.size();
            } else {
                List<Object> values = new ArrayList<>(solutions.size());
                for (QuerySolution solution : solutions)
                    values.add(arguments.get(0).eval(repository, solution));
                if (isDistinct)
                    values = getDistincts(values);
                return values.size();
            }
        }
        if (iri.equalsIgnoreCase("SUM")) {
            List<Object> values = new ArrayList<>(solutions.size());
            for (QuerySolution solution : solutions)
                values.add(arguments.get(0).eval(repository, solution));
            if (isDistinct)
                values = getDistincts(values);
            Object accumulator = 0d;
            for (Object value : values)
                accumulator = ExpressionOperator.plus(accumulator, value);

        }
        if (iri.equalsIgnoreCase("MIN")) {

        }
        if (iri.equalsIgnoreCase("MAX")) {

        }
        if (iri.equalsIgnoreCase("AVG")) {

        }
        if (iri.equalsIgnoreCase("SAMPLE")) {

        }
        if (iri.equalsIgnoreCase("GROUP_CONCAT")) {

        }

        // not an aggregate function, applies the function an all solution set
        List<Object> results = new ArrayList<>(solutions.size());
        for (QuerySolution solution : solutions)
            results.add(eval(repository, solution));
        return results;*/
        return null;
    }

    /**
     * Gets the distinct values
     *
     * @param originals The original values
     * @return The distinct values
     */
    private List<Object> getDistincts(List<Object> originals) {
        List<Object> result = new ArrayList<>();
        for (Object original : originals)
            if (!result.contains(original))
                result.add(original);
        return result;
    }

    /**
     * Gets the distinct solutions
     *
     * @param solutions The original solutions
     * @return The distinct ones
     */
    private Collection<RDFPatternSolution> getDistincts(Collection<RDFPatternSolution> solutions) {
        Collection<RDFPatternSolution> result = new ArrayList<>();
        for (RDFPatternSolution solution : solutions)
            if (!result.contains(solution))
                result.add(solution);
        return result;
    }
}
