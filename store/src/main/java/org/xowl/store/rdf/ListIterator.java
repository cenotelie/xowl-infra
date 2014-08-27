/**********************************************************************
 * Copyright (c) 2014 Laurent Wouters and others
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
package org.xowl.store.rdf;

import java.util.Iterator;

/**
 * Represents an iterator over the elements of an RDF list
 *
 * @author Laurent Wouters
 */
public abstract class ListIterator implements Iterator<Node> {
    private int keyFirst;
    private int keyRest;
    private int keyNil;
    private Node proxy;
    private Node nextValue;

    /**
     * Initializes this iterator
     *
     * @param keyFirst The key to the rdf:first IRI
     * @param keyRest  The key to the rdf:rest IRI
     * @param keyNil   The key to the rdf:nil IRI
     * @param head     The list's head node
     */
    public ListIterator(int keyFirst, int keyRest, int keyNil, Node head) {
        this.keyFirst = keyFirst;
        this.keyRest = keyRest;
        this.keyNil = keyNil;
        this.proxy = head;
        findNext();
    }

    /**
     * Finds the next value
     */
    private void findNext() {
        if (proxy == null) {
            nextValue = null;
            return;
        }
        if (proxy instanceof IRINodeImpl) {
            int key = ((IRINodeImpl) proxy).getKey();
            if (key == keyNil) {
                proxy = null;
                nextValue = null;
                return;
            }
        }
        nextValue = getValue((SubjectNode) proxy, keyFirst);
        proxy = getValue((SubjectNode) proxy, keyRest);
    }

    /**
     * Gets the value of the specified node for the specified property
     *
     * @param node     A subject node
     * @param property The key to the property's IRI
     * @return The associated value
     */
    private Node getValue(SubjectNode node, int property) {
        EdgeBucket bucket = getBucketOf(node);
        if (bucket == null)
            return null;
        for (Edge edge : bucket) {
            Property potential = edge.getProperty();
            if (potential instanceof IRINodeImpl) {
                int potentialKey = ((IRINodeImpl) potential).getKey();
                if (property == potentialKey) {
                    Iterator<EdgeTarget> targets = edge.iterator();
                    return targets.next().getTarget();
                }
            }
        }
        return null;
    }

    /**
     * Gets the edge bucket associated to the specified node
     *
     * @param node A subject node
     * @return The associated edge bucket
     */
    protected abstract EdgeBucket getBucketOf(SubjectNode node);

    @Override
    public boolean hasNext() {
        return (nextValue != null);
    }

    @Override
    public Node next() {
        Node result = nextValue;
        findNext();
        return result;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
