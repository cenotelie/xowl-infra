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

package org.xowl.store;

import org.xowl.lang.owl2.*;
import org.xowl.lang.runtime.Entity;
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
}
