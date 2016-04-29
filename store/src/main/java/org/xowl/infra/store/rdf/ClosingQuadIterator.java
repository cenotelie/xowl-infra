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

package org.xowl.infra.store.rdf;

import org.xowl.infra.store.storage.Dataset;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

/**
 * Represents an iterator over a set of quads that closes over blank nodes in the graph
 *
 * @author Laurent Wouters
 */
public class ClosingQuadIterator implements Iterator<Quad> {
    /**
     * The parent store
     */
    private Dataset store;
    /**
     * The content iterators
     */
    private Stack<Iterator<Quad>> content;
    /**
     * The already explored blank nodes
     */
    private List<BlankNode> explored;

    /**
     * Initializes this closing iterator
     *
     * @param store    The parent store
     * @param original The iterator over the original quads
     */
    public ClosingQuadIterator(Dataset store, Iterator<Quad> original) {
        this.store = store;
        this.content = new Stack<>();
        this.content.push(original);
        this.explored = new ArrayList<>();
    }

    @Override
    public boolean hasNext() {
        while (!content.isEmpty()) {
            if (content.peek().hasNext())
                return true;
            content.pop();
        }
        return false;
    }

    @Override
    public Quad next() {
        Quad result = content.peek().next();
        if (result.getObject().getNodeType() == Node.TYPE_BLANK) {
            BlankNode blank = (BlankNode) result.getObject();
            if (!explored.contains(blank)) {
                // push the iterator for this blank node
                content.push(store.getAll(null, blank, null, null));
                explored.add(blank);
            }
        }
        return result;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
