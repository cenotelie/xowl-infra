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

package org.xowl.store.rete;

import org.xowl.store.rdf.RDFNode;

import java.util.*;

/**
 * Represents the simple hash joining strategy
 *
 * @param <LEFT>  The type of the left elements
 * @param <RIGHT> The type fo the right elements
 * @author Laurent Wouters
 */
public abstract class SimpleHashJoin<LEFT, RIGHT> extends JoinStrategy implements Iterator<Couple> {
    /**
     * Hash map of the left elements
     */
    private Map<RDFNode, Map<RDFNode, Map<RDFNode, Collection<LEFT>>>> mapLefts;
    /**
     * Innermost iterator of left elements
     */
    private Iterator<LEFT> leftIterator;
    /**
     * Iterator of right elements
     */
    private Iterator<RIGHT> rightIterator;
    /**
     * Current right element
     */
    private RIGHT currentRight;
    /**
     * Next matching couple
     */
    private Couple nextCouple = null;

    /**
     * Initializes this strategy
     *
     * @param t1 The first test
     * @param t2 The second test
     * @param t3 The third test
     */
    public SimpleHashJoin(BetaJoinNodeTest t1, BetaJoinNodeTest t2, BetaJoinNodeTest t3) {
        super(t1, t2, t3);
        mapLefts = new HashMap<>();
    }

    /**
     * Creates the couple corresponding to the specified elements
     *
     * @param t1 The left element
     * @param t2 The right element
     * @return The corresponding couple
     */
    protected abstract Couple createCouple(LEFT t1, RIGHT t2);

    /**
     * Gets the value for the specified left element
     *
     * @param left A left element
     * @param test A test
     * @return The value corresponding to the element
     */
    protected abstract RDFNode getValueForLeft(LEFT left, BetaJoinNodeTest test);

    /**
     * Gets the value for the specified right element
     *
     * @param right A right element
     * @param test  A test
     * @return The value corresponding to the element
     */
    protected abstract RDFNode getValueForRight(RIGHT right, BetaJoinNodeTest test);

    /**
     * Creates a generic iterator over the joined elelements
     *
     * @param leftElements  A collection of left elements
     * @param rightElements A collection of right elements
     * @return An iterator over the join results
     */
    protected Iterator<Couple> joinGenerics(Collection<LEFT> leftElements, Collection<RIGHT> rightElements) {
        for (LEFT token : leftElements)
            insertLeft(token);
        rightIterator = rightElements.iterator();
        nextCouple = findNext();
        return this;
    }

    @Override
    public boolean hasNext() {
        return (nextCouple != null);
    }

    @Override
    public Couple next() {
        Couple result = nextCouple;
        nextCouple = findNext();
        return result;
    }

    @Override
    public void remove() {
    }

    /**
     * Computes the next result of the join
     *
     * @return The next result
     */
    private Couple findNext() {
        while (leftIterator == null || !leftIterator.hasNext()) {
            if (!rightIterator.hasNext())
                return null;
            currentRight = rightIterator.next();
            leftIterator = getMatchingCollection(currentRight);
        }
        return createCouple(leftIterator.next(), currentRight);
    }

    /**
     * Gets the iterator over the left elements matching the specified right element
     *
     * @param current A right element
     * @return The matching left elements
     */
    private Iterator<LEFT> getMatchingCollection(RIGHT current) {
        RDFNode n1 = (test1 != null) ? getValueForRight(current, test1) : null;
        RDFNode n2 = (test2 != null) ? getValueForRight(current, test2) : null;
        RDFNode n3 = (test3 != null) ? getValueForRight(current, test3) : null;
        Map<RDFNode, Map<RDFNode, Collection<LEFT>>> sub1 = mapLefts.get(n1);
        if (sub1 == null)
            return null;
        Map<RDFNode, Collection<LEFT>> sub2 = sub1.get(n2);
        if (sub2 == null)
            return null;
        Collection<LEFT> sub3 = sub2.get(n3);
        if (sub3 == null)
            return null;
        return sub3.iterator();
    }

    /**
     * Inserts the specified left element in the map
     *
     * @param left A left element
     */
    private void insertLeft(LEFT left) {
        RDFNode v1 = (test1 != null ? getValueForLeft(left, test1) : null);
        RDFNode v2 = (test2 != null ? getValueForLeft(left, test2) : null);
        RDFNode v3 = (test3 != null ? getValueForLeft(left, test3) : null);
        Collection<LEFT> collec = resolveLefts(v1, v2, v3);
        collec.add(left);
    }

    /**
     * Resolves the collection of left elements for the specified nodes
     *
     * @param v1 subject node to match
     * @param v2 property node to match
     * @param v3 object node to match
     * @return The corresponding collection of left elements
     */
    private Collection<LEFT> resolveLefts(RDFNode v1, RDFNode v2, RDFNode v3) {
        Map<RDFNode, Map<RDFNode, Collection<LEFT>>> sub1 = mapLefts.get(v1);
        if (sub1 == null) {
            sub1 = new HashMap<>();
            Map<RDFNode, Collection<LEFT>> sub2 = new HashMap<>();
            Collection<LEFT> sub3 = new ArrayList<>();
            mapLefts.put(v1, sub1);
            sub1.put(v2, sub2);
            sub2.put(v3, sub3);
            return sub3;
        }
        Map<RDFNode, Collection<LEFT>> sub2 = sub1.get(v2);
        if (sub2 == null) {
            sub2 = new HashMap<>();
            Collection<LEFT> sub3 = new ArrayList<>();
            sub1.put(v2, sub2);
            sub2.put(v3, sub3);
            return sub3;
        }
        Collection<LEFT> sub3 = sub2.get(v3);
        if (sub3 == null) {
            sub3 = new ArrayList<>();
            sub2.put(v3, sub3);
        }
        return sub3;
    }
}