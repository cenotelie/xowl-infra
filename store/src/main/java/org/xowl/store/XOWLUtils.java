/**********************************************************************
 * Copyright (c) 2014 Laurent Wouters
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

package org.xowl.store;

import org.xowl.lang.actions.*;
import org.xowl.lang.interop.JavaObjectExpression;
import org.xowl.lang.owl2.*;
import org.xowl.lang.runtime.Entity;
import org.xowl.lang.runtime.JavaObject;
import org.xowl.lang.runtime.Value;
import org.xowl.utils.collections.Adapter;
import org.xowl.utils.collections.AdaptingIterator;

import java.util.*;

/**
 * Utility class for the manipulation of xOWL language elements
 *
 * @author Laurent Wouters
 */
public class XOWLUtils {

    /**
     * Determines whether the given expression is a query variable
     *
     * @param expression An expression
     * @return <code>true</code> if the expression is a query variable
     */
    public static boolean isQueryVar(Expression expression) {
        return (expression instanceof QueryVariable);
    }

    /**
     * Determines whether the specified expression is dynamic
     *
     * @param expression An expression
     * @return <code>true</code> if the expression is dynamic
     */
    public static boolean isDynamicExpression(Expression expression) {
        if (isQueryVar(expression)) return false;
        if (expression instanceof Invoke) return true;
        if (expression instanceof Execute) return true;
        if (expression instanceof Query) return true;
        if (expression instanceof CodeVariable) return true;
        if (expression instanceof ArrayElement) return true;
        if (expression instanceof NewEntity) return true;
        if (expression instanceof EntityForIRI) return true;
        if (expression instanceof NewIndividual) return true;
        if ((expression instanceof LiteralExpression) && !(expression instanceof Literal)) return true;
        if (expression instanceof ExecutableExpression) return true;
        if (expression instanceof ArrayExpression) return true;
        if (expression instanceof JavaObjectExpression) return true;
        return false;
    }

    /**
     * Determines whether the specified expression is static (not dynamic)
     *
     * @param expression An expression
     * @return <code>true</code> if the expression is static (not dynamic)
     */
    public static boolean isStaticExpression(Expression expression) {
        if (isQueryVar(expression)) return false;
        return !isDynamicExpression(expression);
    }

    /**
     * Gets an iterator over all the expressions within the specified expression of a sequence
     *
     * @param expression The expression of a sequence
     * @return An iterator over the contained expressions
     */
    public static Iterator<ClassExpression> getAll(ClassSequenceExpression expression) {
        List<ClassElement> elements = new ArrayList<>(((ClassSequence) expression).getAllClassElements());
        sortElements(elements);
        return new AdaptingIterator<>(elements.iterator(), new Adapter<ClassExpression>() {
            @Override
            public <X> ClassExpression adapt(X element) {
                return ((ClassElement) element).getClasse();
            }
        });
    }

    /**
     * Gets an iterator over all the expressions within the specified expression of a sequence
     *
     * @param expression The expression of a sequence
     * @return An iterator over the contained expressions
     */
    public static Iterator<Datarange> getAll(DatarangeSequenceExpression expression) {
        List<DatarangeElement> elements = new ArrayList<>(((DatarangeSequence) expression).getAllDatarangeElements());
        sortElements(elements);
        return new AdaptingIterator<>(elements.iterator(), new Adapter<Datarange>() {
            @Override
            public <X> Datarange adapt(X element) {
                return ((DatarangeElement) element).getDatarange();
            }
        });
    }

    /**
     * Gets an iterator over all the expressions within the specified expression of a sequence
     *
     * @param expression The expression of a sequence
     * @return An iterator over the contained expressions
     */
    public static Iterator<ObjectPropertyExpression> getAll(ObjectPropertySequenceExpression expression) {
        List<ObjectPropertyElement> elements = new ArrayList<>(((ObjectPropertySequence) expression).getAllObjectPropertyElements());
        sortElements(elements);
        return new AdaptingIterator<>(elements.iterator(), new Adapter<ObjectPropertyExpression>() {
            @Override
            public <X> ObjectPropertyExpression adapt(X element) {
                return ((ObjectPropertyElement) element).getObjectProperty();
            }
        });
    }

    /**
     * Gets an iterator over all the expressions within the specified expression of a sequence
     *
     * @param expression The expression of a sequence
     * @return An iterator over the contained expressions
     */
    public static Iterator<DataPropertyExpression> getAll(DataPropertySequenceExpression expression) {
        List<DataPropertyElement> elements = new ArrayList<>(((DataPropertySequence) expression).getAllDataPropertyElements());
        sortElements(elements);
        return new AdaptingIterator<>(elements.iterator(), new Adapter<DataPropertyExpression>() {
            @Override
            public <X> DataPropertyExpression adapt(X element) {
                return ((DataPropertyElement) element).getDataProperty();
            }
        });
    }

    /**
     * Gets an iterator over all the expressions within the specified expression of a sequence
     *
     * @param expression The expression of a sequence
     * @return An iterator over the contained expressions
     */
    public static Iterator<IndividualExpression> getAll(IndividualSequenceExpression expression) {
        List<IndividualElement> elements = new ArrayList<>(((IndividualSequence) expression).getAllIndividualElements());
        sortElements(elements);
        return new AdaptingIterator<>(elements.iterator(), new Adapter<IndividualExpression>() {
            @Override
            public <X> IndividualExpression adapt(X element) {
                return ((IndividualElement) element).getIndividual();
            }
        });
    }

    /**
     * Gets an iterator over all the expressions within the specified expression of a sequence
     *
     * @param expression The expression of a sequence
     * @return An iterator over the contained expressions
     */
    public static Iterator<LiteralExpression> getAll(LiteralSequenceExpression expression) {
        List<LiteralElement> elements = new ArrayList<>(((LiteralSequence) expression).getAllLiteralElements());
        sortElements(elements);
        return new AdaptingIterator<>(elements.iterator(), new Adapter<LiteralExpression>() {
            @Override
            public <X> LiteralExpression adapt(X element) {
                return ((LiteralElement) element).getLiteral();
            }
        });
    }

    /**
     * Sorts the specified list of sequence element by their indices
     *
     * @param elements A list of sequence elements
     */
    private static void sortElements(List<? extends SequenceElement> elements) {
        Collections.sort(elements, new Comparator<SequenceElement>() {
            @Override
            public int compare(SequenceElement left, SequenceElement right) {
                return left.getIndex().compareTo(right.getIndex());
            }
        });
    }

    /**
     * Gets the literal expression of the specified runtime literal
     *
     * @param literal A runtime literal
     * @return The equivalent expression
     */
    public static org.xowl.lang.owl2.Literal getExpression(org.xowl.lang.runtime.Literal literal) {
        org.xowl.lang.owl2.Literal result = new org.xowl.lang.owl2.Literal();
        result.setLexicalValue(literal.getLexicalValue());
        result.setMemberOf(literal.getMemberOf().getInterpretationOf().getHasIRI());
        return result;
    }

    /**
     * Builds the string representation of the specified runtime value
     *
     * @param value A runtime value
     * @return The string representation
     */
    public static String toString(Value value) {
        if (value == null) return null;
        if (value instanceof Entity) {
            return ((Entity) value).getHasIRI().getHasValue();
        } else if (value instanceof org.xowl.lang.runtime.Literal) {
            org.xowl.lang.runtime.Literal lit = (org.xowl.lang.runtime.Literal) value;
            String datatype = lit.getMemberOf().getInterpretationOf().getHasIRI().getHasValue();
            return ("\"" + lit.getLexicalValue() + "\"^^" + datatype);
        } else if (value instanceof org.xowl.lang.runtime.Array) {
            org.xowl.lang.runtime.Array array = (org.xowl.lang.runtime.Array) value;
            StringBuilder builder = new StringBuilder("[");
            boolean first = true;
            for (Value elem : toList(array)) {
                if (!first)
                    builder.append(", ");
                builder.append(toString(elem));
                first = false;
            }
            builder.append("]");
            return builder.toString();
        } else if (value instanceof JavaObject) {
            return ((JavaObject) value).getObject().toString();
        }
        return value.toString();
    }

    /**
     * Determines whether the specified runtime value is true
     *
     * @param value A runtime value
     * @return <code>true</code> if the runtime value is a literal representing the boolean true
     */
    public static boolean isTrue(Value value) {
        org.xowl.lang.runtime.Literal lit = (org.xowl.lang.runtime.Literal) value;
        if (lit.getMemberOf() == null) {
            return (lit.getLexicalValue() != null && !lit.getLexicalValue().isEmpty());
        } else {
            String iri = lit.getMemberOf().getInterpretationOf().getHasIRI().getHasValue();
            if (Vocabulary.xsdBoolean.equals(iri))
                return "true".equalsIgnoreCase(lit.getLexicalValue());
            else if (Vocabulary.xsdInt.equals(iri) || Vocabulary.xsdInteger.equals(iri))
                return (!"0".equals(lit.getLexicalValue()));
            return false;
        }
    }

    /**
     * Translates the specified xOWL array into a list of its elements
     *
     * @param expression A xOWL array
     * @return The list of the array's elements
     */
    public static List<Value> toList(org.xowl.lang.runtime.Array expression) {
        List<Value> results = new ArrayList<>();
        List<org.xowl.lang.runtime.Element> elements = new ArrayList<>(expression.getAllElements());
        Collections.sort(elements, new Comparator<org.xowl.lang.runtime.Element>() {
            @Override
            public int compare(org.xowl.lang.runtime.Element left, org.xowl.lang.runtime.Element right) {
                return left.getIndex().compareTo(right.getIndex());
            }
        });
        for (org.xowl.lang.runtime.Element elem : elements)
            results.add(elem.getValue());
        return results;
    }
}