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

package org.xowl.store.rete;

import org.xowl.store.rdf.Node;

import java.util.*;

/**
 * Represents the double hash joining strategy
 *
 * @param <LEFT>  The type of the left elements to join
 * @param <RIGHT> The type of the right elements to join
 * @author Laurent Wouters
 */
abstract class GraceHashJoin<LEFT, RIGHT> extends JoinStrategy implements Iterator<Couple> {
    /**
     * Map of all the left elements
     */
    private final Map<Node, Map<Node, Map<Node, Collection<LEFT>>>> mapLeftElements;
    /**
     * Map of all the right elements
     */
    private final Map<Node, Map<Node, Map<Node, Collection<RIGHT>>>> mapRightElements;
    /**
     * Outer iterator over the left elements
     */
    private Iterator<Map.Entry<Node, Map<Node, Map<Node, Collection<LEFT>>>>> outerIterator;
    /**
     * Middle iterator over the left elements
     */
    private Iterator<Map.Entry<Node, Map<Node, Collection<LEFT>>>> middleIterator;
    /**
     * Inner iterator over the left elements
     */
    private Iterator<Map.Entry<Node, Collection<LEFT>>> innerIterator;
    /**
     * Innermost iterator over the left elements
     */
    private Iterator<LEFT> leftIterator;
    /**
     * Current outer element
     */
    private Map.Entry<Node, Map<Node, Map<Node, Collection<LEFT>>>> currentOuter;
    /**
     * Current middle element
     */
    private Map.Entry<Node, Map<Node, Collection<LEFT>>> currentMiddle;
    /**
     * Current inner element
     */
    private Map.Entry<Node, Collection<LEFT>> currentInner;
    /**
     * Current left element
     */
    private LEFT currentLeft;
    /**
     * Current right elements
     */
    private Collection<RIGHT> currentRightElements;
    /**
     * Iterator over the matching right elements
     */
    private Iterator<RIGHT> rightIterator;
    /**
     * Next matching couple
     */
    private Couple nextCouple = null;

    /**
     * Initializes this strategy
     *
     * @param test1 The first test
     * @param test2 The second test
     * @param test3 The third test
     * @param test4 The fourth test
     */
    public GraceHashJoin(JoinTest test1, JoinTest test2, JoinTest test3, JoinTest test4) {
        super(test1, test2, test3, test4);
        mapLeftElements = new HashMap<>();
        mapRightElements = new HashMap<>();
    }

    /**
     * Creates the couple corresponding to the specified elements
     *
     * @param left  The left element
     * @param right The right element
     * @return The corresponding couple
     */
    protected abstract Couple createCouple(LEFT left, RIGHT right);

    /**
     * Gets the value for the specified left element
     *
     * @param left A left element
     * @param test A test
     * @return The value corresponding to the element
     */
    protected abstract Node getValueForLeft(LEFT left, JoinTest test);

    /**
     * Gets the value for the specified right element
     *
     * @param right A right element
     * @param test  A test
     * @return The value corresponding to the element
     */
    protected abstract Node getValueForRight(RIGHT right, JoinTest test);

    /**
     * Creates a generic iterator over the joined elelements
     *
     * @param leftElements  A collection of left elements
     * @param rightElements A collection of right elements
     * @return An iterator over the join results
     */
    protected Iterator<Couple> joinGenerics(Collection<LEFT> leftElements, Collection<RIGHT> rightElements) {
        for (LEFT left : leftElements)
            insertLeft(left);
        for (RIGHT right : rightElements)
            insertRight(right);
        outerIterator = mapLeftElements.entrySet().iterator();
        currentOuter = outerIterator.next();
        middleIterator = currentOuter.getValue().entrySet().iterator();
        currentMiddle = middleIterator.next();
        innerIterator = currentMiddle.getValue().entrySet().iterator();
        currentInner = innerIterator.next();
        currentRightElements = retrieveRights();
        if (currentRightElements == null)
            findNextKeyMatch();
        if (currentRightElements != null) {
            if (rightIterator == null)
                rightIterator = currentRightElements.iterator();
            leftIterator = currentInner.getValue().iterator();
            currentLeft = leftIterator.next();
            nextCouple = createCouple(currentLeft, rightIterator.next());
        }
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
        while (rightIterator == null || !rightIterator.hasNext()) {
            while (leftIterator == null || !leftIterator.hasNext()) {
                findNextKeyMatch();
                if (currentRightElements == null)
                    return null;
                leftIterator = currentInner.getValue().iterator();
            }
            currentLeft = leftIterator.next();
            rightIterator = currentRightElements.iterator();
        }
        return createCouple(currentLeft, rightIterator.next());
    }

    /**
     * Finds the next matching keys
     */
    private void findNextKeyMatch() {
        currentRightElements = null;
        while (currentRightElements == null) {
            if (!innerIterator.hasNext()) {
                if (!middleIterator.hasNext()) {
                    if (!outerIterator.hasNext()) {
                        return;
                    } else {
                        currentOuter = outerIterator.next();
                        middleIterator = currentOuter.getValue().entrySet().iterator();
                    }
                } else {
                    currentMiddle = middleIterator.next();
                    innerIterator = currentMiddle.getValue().entrySet().iterator();
                }
            } else {
                currentInner = innerIterator.next();
                currentRightElements = retrieveRights();
            }
        }
        rightIterator = currentRightElements.iterator();
    }

    /**
     * Retrieve the matching right elements
     *
     * @return The matching right elements
     */
    private Collection<RIGHT> retrieveRights() {
        Map<Node, Map<Node, Collection<RIGHT>>> sub1 = mapRightElements.get(currentOuter.getKey());
        if (sub1 == null)
            return null;
        Map<Node, Collection<RIGHT>> sub2 = sub1.get(currentMiddle.getKey());
        if (sub2 == null)
            return null;
        return sub2.get(currentInner.getKey());
    }

    /**
     * Inserts the specified left element in the map
     *
     * @param left A left element
     */
    private void insertLeft(LEFT left) {
        Node v1 = (test1 != null && test1.useInIndex()) ? getValueForLeft(left, test1) : null;
        Node v2 = (test2 != null && test2.useInIndex()) ? getValueForLeft(left, test2) : null;
        Node v3 = (test3 != null && test3.useInIndex()) ? getValueForLeft(left, test3) : null;
        Collection<LEFT> collec = resolveLefts(v1, v2, v3);
        collec.add(left);
    }

    /**
     * Inserts the specified right element in the map
     *
     * @param right A right element
     */
    private void insertRight(RIGHT right) {
        Node v1 = (test1 != null && test1.useInIndex()) ? getValueForRight(right, test1) : null;
        Node v2 = (test2 != null && test2.useInIndex()) ? getValueForRight(right, test2) : null;
        Node v3 = (test3 != null && test3.useInIndex()) ? getValueForRight(right, test3) : null;
        Collection<RIGHT> collec = resolveRights(v1, v2, v3);
        collec.add(right);
    }

    /**
     * Resolves the collection of left elements for the specified nodes
     *
     * @param v1 subject node to match
     * @param v2 property node to match
     * @param v3 object node to match
     * @return The corresponding collection of left elements
     */
    private Collection<LEFT> resolveLefts(Node v1, Node v2, Node v3) {
        Map<Node, Map<Node, Collection<LEFT>>> sub1 = mapLeftElements.get(v1);
        if (sub1 == null) {
            sub1 = new HashMap<>();
            Map<Node, Collection<LEFT>> sub2 = new HashMap<>();
            Collection<LEFT> sub3 = new ArrayList<>();
            mapLeftElements.put(v1, sub1);
            sub1.put(v2, sub2);
            sub2.put(v3, sub3);
            return sub3;
        }
        Map<Node, Collection<LEFT>> sub2 = sub1.get(v2);
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

    /**
     * Resolves the collection of right elements for the specified nodes
     *
     * @param v1 subject node to match
     * @param v2 property node to match
     * @param v3 object node to match
     * @return The corresponding collection of right elements
     */
    private Collection<RIGHT> resolveRights(Node v1, Node v2, Node v3) {
        Map<Node, Map<Node, Collection<RIGHT>>> sub1 = mapRightElements.get(v1);
        if (sub1 == null) {
            sub1 = new HashMap<>();
            Map<Node, Collection<RIGHT>> sub2 = new HashMap<>();
            Collection<RIGHT> sub3 = new ArrayList<>();
            mapRightElements.put(v1, sub1);
            sub1.put(v2, sub2);
            sub2.put(v3, sub3);
            return sub3;
        }
        Map<Node, Collection<RIGHT>> sub2 = sub1.get(v2);
        if (sub2 == null) {
            sub2 = new HashMap<>();
            Collection<RIGHT> sub3 = new ArrayList<>();
            sub1.put(v2, sub2);
            sub2.put(v3, sub3);
            return sub3;
        }
        Collection<RIGHT> sub3 = sub2.get(v3);
        if (sub3 == null) {
            sub3 = new ArrayList<>();
            sub2.put(v3, sub3);
        }
        return sub3;
    }
}
