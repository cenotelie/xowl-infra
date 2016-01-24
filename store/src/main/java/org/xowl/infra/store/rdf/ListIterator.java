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
package org.xowl.infra.store.rdf;

import org.xowl.infra.store.Vocabulary;
import org.xowl.infra.store.storage.BaseStore;
import org.xowl.infra.store.storage.Dataset;

import java.util.Iterator;

/**
 * Represents an iterator over the elements of an RDF list
 *
 * @author Laurent Wouters
 */
public class ListIterator implements Iterator<Node> {
    /**
     * The dataset that contains the data
     */
    private final Dataset dataset;
    /**
     * The rdf:first node
     */
    private final IRINode keyFirst;
    /**
     * The rdf:rest node
     */
    private final IRINode keyRest;
    /**
     * The rdf:nil node
     */
    private final IRINode keyNil;
    /**
     * The current proxy node
     */
    private Node proxy;
    /**
     * The next value to return
     */
    private Node nextValue;

    /**
     * Initializes this iterator
     *
     * @param store The node that contains the data
     * @param head  The list's head node
     */
    public ListIterator(BaseStore store, Node head) {
        this.dataset = store;
        this.keyFirst = store.getIRINode(Vocabulary.rdfFirst);
        this.keyRest = store.getIRINode(Vocabulary.rdfRest);
        this.keyNil = store.getIRINode(Vocabulary.rdfNil);
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
        if (proxy == keyNil) {
            proxy = null;
            nextValue = null;
            return;
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
    private Node getValue(SubjectNode node, Property property) {
        Iterator<Quad> iterator = dataset.getAll(node, property, null);
        return iterator.hasNext() ? iterator.next().getObject() : null;
    }

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
