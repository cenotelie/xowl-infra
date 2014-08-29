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

package org.xowl.store.loaders;

import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Represents a set of XML attributes
 *
 * @author Laurent Wouters
 */
class XMLAttributes implements Iterable<Node> {
    /**
     * The current list of attributes
     */
    private List<Node> content;

    /**
     * Initializes this set
     *
     * @param element The parent XML Element node to get the attributes from
     */
    public XMLAttributes(Node element) {
        content = new ArrayList<>();
        for (int i = 0; i != element.getAttributes().getLength(); i++)
            content.add(element.getAttributes().item(i));
    }

    /**
     * Gets the attributes with the specified name and removes it
     *
     * @param name A attribute's name
     * @return The corresponding attribute, or <code>null</code> if none is found
     */
    public Node pop(String name) {
        for (int i = 0; i != content.size(); i++) {
            Node attribute = content.get(i);
            if (attribute.getNodeName().equals(name)) {
                content.remove(i);
                return attribute;
            }
        }
        return null;
    }

    /**
     * Gets an iterator over all the attributes with the specified prefix (and removes them when they are accessed)
     *
     * @param prefix The prefix to look for
     * @return An iterator over the attributes
     */
    public Iterator<Node> popAll(final String prefix) {
        return new Iterator<Node>() {
            private int index = getNext(0);

            private int getNext(int start) {
                for (int i = start; i != content.size(); i++)
                    if (content.get(i).getNodeName().startsWith(prefix))
                        return index;
                return content.size();
            }

            @Override
            public boolean hasNext() {
                return (index != content.size());
            }

            @Override
            public Node next() {
                Node result = content.get(index);
                content.remove(index);
                index = getNext(index);
                return result;
            }
        };
    }

    @Override
    public Iterator<Node> iterator() {
        return content.iterator();
    }

    /**
     * Gets the number of attributes currently in this collection
     *
     * @return The number of attributes currently in this collection
     */
    public int count() {
        return content.size();
    }
}
