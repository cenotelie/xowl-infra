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

package org.xowl.store.rdf;

/**
 * Utility APIs for RDF
 */
public class Utils {
    /**
     * Determines whether two RDF nodes are equivalent
     * @param node1 A first node
     * @param node2 A second node
     * @return true of the two nodes are equivalent
     */
    public static boolean same(Node node1, Node node2) {
        return (node1 == node2 || (node1 != null  && node2 != null
                && node1.getNodeType() == node2.getNodeType()
                && node1.equals(node2)));
    }
}
